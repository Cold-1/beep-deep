package com.beepdeep;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.runelite.api.Client;
import net.runelite.api.Preferences;
import net.runelite.client.audio.AudioPlayer;
import net.runelite.client.callback.ClientThread;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SoundManagerPreviewTest
{
	private final Client client = mock(Client.class);
	private final AudioPlayer audio = mock(AudioPlayer.class);
	private final SoundFileResolver resolver = mock(SoundFileResolver.class);
	private final BeepDeepConfig config = mock(BeepDeepConfig.class);
	private SoundManager manager;

	@Before
	public void setUp()
	{
		when(client.getPreferences()).thenReturn(mock(Preferences.class));
		when(config.masterVolume()).thenReturn(50);
		ClientThread clientThread = mock(ClientThread.class);
		doAnswer(invocation ->
		{
			((Runnable) invocation.getArgument(0)).run();
			return null;
		}).when(clientThread).invoke(any(Runnable.class));
		manager = new SoundManager(client, clientThread, null, audio, resolver, config);
		manager.startUp();
		clearInvocations(resolver);
	}

	@After
	public void tearDown()
	{
		manager.shutDown();
	}

	@Test
	public void previewsEveryExactSlotIncludingDisabledEventsAtConfiguredVolume()
	{
		List<String> errors = new ArrayList<>();
		for (ToaEvent event : ToaEvent.values())
		{
			for (SoundTestSlot selected : SoundTestSlot.values())
			{
				int id = 2000 + event.ordinal() * 5 + selected.getIndex();
				ToaEvent.SoundSlot binding = event.getSlots().get(selected.getIndex());
				when(binding.source(config)).thenReturn(" \"" + id + "\" ");
				when(binding.volume(config)).thenReturn(20 * (selected.getIndex() + 1));
				manager.preview(event, selected, errors::add);
				verify(client).playSoundEffect(id, SoundManager.effectVolumeFromPercent(10 * (selected.getIndex() + 1)));
			}
		}
		assertTrue(errors.isEmpty());
		verifyNoInteractions(audio, resolver);
	}

	@Test
	public void localPreviewResolvesAndPlaysOnWorkerWithCombinedVolume() throws Exception
	{
		when(config.hetEnterSound5()).thenReturn(" \"beep-deep/my sound.wav\" ");
		when(config.hetEnterVolume5()).thenReturn(80);
		File file = new File("my sound.wav");
		Thread caller = Thread.currentThread();
		when(resolver.localFile("beep-deep/my sound.wav")).thenAnswer(invocation ->
		{
			assertNotSame(caller, Thread.currentThread());
			return file;
		});
		CompletableFuture<Float> played = new CompletableFuture<>();
		doAnswer(invocation ->
		{
			assertNotSame(caller, Thread.currentThread());
			played.complete(invocation.getArgument(1));
			return null;
		}).when(audio).play(eq(file), anyFloat());
		manager.preview(ToaEvent.HET_ENTER, SoundTestSlot.SOUND_5, error -> played.completeExceptionally(new AssertionError(error)));
		assertEquals(SoundManager.gainForVolume(40), played.get(2, TimeUnit.SECONDS), 0.001f);
		verifyNoInteractions(client);
	}

	@Test
	public void reportsMissingAndUnsupportedFilesAndCanRetry() throws Exception
	{
		when(config.hetEnterSound5()).thenReturn("missing.wav");
		when(config.hetEnterVolume5()).thenReturn(100);
		when(resolver.localFile("missing.wav")).thenThrow(new NoSuchFileException("missing.wav"));
		String missing = failure();
		assertTrue(missing.contains("Path of Het - Enter / Sound 5"));
		assertTrue(missing.contains("Sound file is missing: missing.wav"));

		File file = new File("unsupported.mp3");
		when(config.hetEnterSound5()).thenReturn(file.getName());
		when(resolver.localFile(file.getName())).thenReturn(file);
		doThrow(new UnsupportedAudioFileException()).when(audio).play(eq(file), anyFloat());
		String unsupported = failure();
		assertTrue(unsupported.contains("Unsupported sound file"));
		assertTrue(unsupported.contains("MP3 and OGG are not supported"));

		CompletableFuture<Void> retried = new CompletableFuture<>();
		doAnswer(invocation ->
		{
			retried.complete(null);
			return null;
		}).when(audio).play(eq(file), anyFloat());
		manager.preview(ToaEvent.HET_ENTER, SoundTestSlot.SOUND_5, error -> retried.completeExceptionally(new AssertionError(error)));
		retried.get(2, TimeUnit.SECONDS);
	}

	@Test
	public void emptyMutedAndRemoteOptOutExplainWhyNoSoundPlays() throws Exception
	{
		assertTrue(failure().contains("slot is empty"));
		when(config.hetEnterSound5()).thenReturn("2192");
		assertTrue(failure().contains("muted"));
		when(config.hetEnterVolume5()).thenReturn(100);
		when(config.masterVolume()).thenReturn(0);
		assertTrue(failure().contains("muted"));
		when(config.masterVolume()).thenReturn(50);
		when(config.hetEnterSound5()).thenReturn("https://example.com/sound.wav");
		assertTrue(failure().contains("Remote URLs are disabled"));
		verifyNoInteractions(audio, resolver, client);
	}

	@Test
	public void failedRemoteDownloadReportsErrorAndNextPreviewRetries() throws Exception
	{
		String url = "https://example.com/sound.wav";
		when(config.hetEnterSound5()).thenReturn(url);
		when(config.hetEnterVolume5()).thenReturn(100);
		when(config.enableRemoteUrls()).thenReturn(true);
		File file = new File("downloaded.wav");
		when(resolver.download(eq(url), any())).thenReturn(CompletableFuture.completedFuture(null))
			.thenReturn(CompletableFuture.completedFuture(file));
		assertTrue(failure().contains("Could not download the sound"));
		CompletableFuture<Void> played = new CompletableFuture<>();
		doAnswer(invocation ->
		{
			played.complete(null);
			return null;
		}).when(audio).play(eq(file), anyFloat());
		manager.preview(ToaEvent.HET_ENTER, SoundTestSlot.SOUND_5, error -> played.completeExceptionally(new AssertionError(error)));
		played.get(2, TimeUnit.SECONDS);
		verify(resolver, times(2)).download(eq(url), any());
	}

	@Test
	public void stoppedManagerIgnoresPreviews()
	{
		manager.shutDown();
		manager.preview(ToaEvent.HET_ENTER, SoundTestSlot.SOUND_5, error -> fail(error));
		verifyNoInteractions(audio, client);
	}

	private String failure() throws Exception
	{
		CompletableFuture<String> error = new CompletableFuture<>();
		manager.preview(ToaEvent.HET_ENTER, SoundTestSlot.SOUND_5, error::complete);
		return error.get(2, TimeUnit.SECONDS);
	}
}
