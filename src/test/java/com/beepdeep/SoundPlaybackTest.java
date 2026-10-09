package com.beepdeep;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.runelite.api.Client;
import net.runelite.client.audio.AudioPlayer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SoundPlaybackTest
{
	private Client client;
	private AudioPlayer audio;
	private SoundFileResolver resolver;
	private BeepDeepConfig config;
	private SoundManager manager;
	private CompletableFuture<Void> drained;
	private final AtomicBoolean remoteEnabled = new AtomicBoolean();

	@Before
	public void setUp() throws Exception
	{
		client = mock(Client.class);
		audio = mock(AudioPlayer.class);
		resolver = mock(SoundFileResolver.class);
		config = mock(BeepDeepConfig.class);
		when(config.crondisEnterEnabled()).thenReturn(true);
		when(config.crondisEnterVolume1()).thenReturn(50);
		when(config.enableRemoteUrls()).thenAnswer(invocation -> remoteEnabled.get());
		File sentinel = new File("sentinel.wav");
		when(config.crondisLeaveEnabled()).thenReturn(true);
		when(config.crondisLeaveSound1()).thenReturn("sentinel.wav");
		when(config.crondisLeaveVolume1()).thenReturn(100);
		when(resolver.localFile("sentinel.wav")).thenReturn(sentinel);
		drained = playbackCompletion(sentinel, Thread.currentThread());
		manager = new SoundManager(client, audio, resolver, config);
		manager.startUp();
		clearInvocations(resolver);
	}

	@After
	public void tearDown()
	{
		manager.shutDown();
	}

	@Test
	public void hetOrbDamageUsesConfiguredSoundAndHonorsToggleAndVolume()
	{
		when(config.hetOrbDamageSound1()).thenReturn("1234");
		when(config.hetOrbDamageVolume1()).thenReturn(75);
		manager.trigger(ToaEvent.HET_ORB_DAMAGE);
		verifyNoInteractions(client, audio, resolver);
		when(config.hetOrbDamageEnabled()).thenReturn(true);
		manager.trigger(ToaEvent.HET_ORB_DAMAGE);
		verify(client).playSoundEffect(1234, SoundManager.effectVolumeFromPercent(75));
		when(config.hetOrbDamageVolume1()).thenReturn(0);
		manager.trigger(ToaEvent.HET_ORB_DAMAGE);
		verifyNoMoreInteractions(client);
		verifyNoInteractions(audio, resolver);
	}

	@Test
	public void quotedSoundEffectUsesClientVolumeWithoutFileIo()
	{
		when(config.crondisEnterSound1()).thenReturn("  \"2192\"  ");
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		verify(client).playSoundEffect(2192, 64);
		verifyNoMoreInteractions(client);
		verifyNoInteractions(audio, resolver);
	}

	@Test
	public void disabledEmptyAndMutedEventsDoNotPlay()
	{
		when(config.crondisEnterSound1()).thenReturn("2192");
		when(config.crondisEnterEnabled()).thenReturn(false);
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		when(config.crondisEnterEnabled()).thenReturn(true);
		when(config.crondisEnterVolume1()).thenReturn(-5);
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		when(config.crondisEnterVolume1()).thenReturn(50);
		when(config.crondisEnterSound1()).thenReturn(" \" \" ");
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		verifyNoInteractions(client, audio, resolver);
	}

	@Test
	public void onlyFilledLaterSlotIsSelectedWithItsOwnVolume()
	{
		when(config.crondisEnterSound1()).thenReturn(" ");
		when(config.crondisEnterSound5()).thenReturn("2192");
		when(config.crondisEnterVolume5()).thenReturn(200);
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		verify(client).playSoundEffect(2192, 127);
		verifyNoMoreInteractions(client);
		verifyNoInteractions(audio, resolver);
	}

	@Test
	public void multipleFilledSlotsPlayExactlyOneConfiguredSoundPerTrigger()
	{
		when(config.crondisEnterSound1()).thenReturn("2192");
		when(config.crondisEnterSound2()).thenReturn("2193");
		when(config.crondisEnterVolume2()).thenReturn(100);
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		assertEquals(1, mockingDetails(client).getInvocations().size());
		Object[] arguments = mockingDetails(client).getInvocations().iterator().next().getArguments();
		int id = (Integer) arguments[0];
		assertTrue(id == 2192 || id == 2193);
		assertEquals(id == 2192 ? 64 : 127, arguments[1]);
		verifyNoInteractions(audio, resolver);
	}

	@Test
	public void clientPlaybackFailureDoesNotBreakLaterEvents()
	{
		when(config.crondisEnterSound1()).thenReturn("2192");
		doThrow(new IllegalStateException("Client unavailable")).doNothing()
			.when(client).playSoundEffect(2192, 64);
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		verify(client, times(2)).playSoundEffect(2192, 64);
	}

	@Test
	public void localFileResolutionAndPlaybackRunOnBackgroundThread() throws Exception
	{
		when(config.crondisEnterSound1()).thenReturn(" \"beep-deep/sounds/my sound.wav\" ");
		File file = new File("my sound.wav");
		Thread caller = Thread.currentThread();
		when(resolver.localFile("beep-deep/sounds/my sound.wav")).thenAnswer(invocation ->
		{
			assertNotSame(caller, Thread.currentThread());
			return file;
		});
		CompletableFuture<Void> played = playbackCompletion(file, caller);
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		played.get(2, TimeUnit.SECONDS);
		verify(audio).play(eq(file), floatThat(gain -> Math.abs(gain + 6.0206f) < 0.001f));
		verify(resolver).localFile("beep-deep/sounds/my sound.wav");
		verifyNoMoreInteractions(audio, resolver);
		verifyNoInteractions(client);
	}

	@Test
	public void badLocalFileAndAudioFailureDoNotPoisonWorker() throws Exception
	{
		when(config.crondisEnterSound1()).thenReturn("broken.wav");
		File file = new File("valid.wav");
		when(resolver.localFile("broken.wav")).thenThrow(new IOException("Missing file"))
			.thenReturn(file);
		CompletableFuture<Void> recovered = new CompletableFuture<>();
		doThrow(new IOException("Invalid audio")).doAnswer(invocation ->
		{
			recovered.complete(null);
			return null;
		}).when(audio).play(eq(file), anyFloat());
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		recovered.get(2, TimeUnit.SECONDS);
		verify(resolver, times(3)).localFile("broken.wav");
		verify(audio, times(2)).play(eq(file), anyFloat());
	}

	@Test
	public void cachedRemoteSoundPlaysWithoutDownloading() throws Exception
	{
		String url = "https://example.com/cached.wav";
		when(config.crondisEnterSound1()).thenReturn(url);
		remoteEnabled.set(true);
		File file = new File("cached.wav");
		when(resolver.cachedFile(url)).thenReturn(file);
		CompletableFuture<Void> played = playbackCompletion(file, Thread.currentThread());
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		played.get(2, TimeUnit.SECONDS);
		verify(resolver).cachedFile(url);
		verifyNoMoreInteractions(resolver);
	}

	@Test
	public void remoteOptOutSkipsEvenCachedSounds() throws Exception
	{
		when(config.crondisEnterSound1()).thenReturn("https://example.com/cached.wav");
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		drainWorker();
		verify(resolver, never()).cachedFile(anyString());
		verify(resolver, never()).download(anyString(), any());
		verifyNoInteractions(client);
	}

	@Test
	public void disablingRemoteUrlsDuringDownloadPreventsPlayback() throws Exception
	{
		when(config.crondisEnterSound1()).thenReturn("https://example.com/sound.wav");
		remoteEnabled.set(true);
		CompletableFuture<Void> callbackRegistered = new CompletableFuture<>();
		CompletableFuture<File> download = new CompletableFuture<File>()
		{
			@Override
			public CompletableFuture<Void> thenAccept(Consumer<? super File> action)
			{
				CompletableFuture<Void> result = super.thenAccept(action);
				callbackRegistered.complete(null);
				return result;
			}
		};
		CompletableFuture<BooleanSupplier> requested = new CompletableFuture<>();
		when(resolver.download(anyString(), any())).thenAnswer(invocation ->
		{
			requested.complete(invocation.getArgument(1));
			return download;
		});
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		BooleanSupplier active = requested.get(2, TimeUnit.SECONDS);
		callbackRegistered.get(2, TimeUnit.SECONDS);
		assertTrue(active.getAsBoolean());
		remoteEnabled.set(false);
		assertFalse(active.getAsBoolean());
		download.complete(new File("downloaded.wav"));
		drainWorker();
		verify(audio, never()).play(eq(new File("downloaded.wav")), anyFloat());
	}

	@Test
	public void failedDownloadDoesNotPlayAndNextTriggerRetries() throws Exception
	{
		when(config.crondisEnterSound1()).thenReturn("https://example.com/sound.wav");
		remoteEnabled.set(true);
		File file = new File("retry.wav");
		when(resolver.download(anyString(), any())).thenReturn(CompletableFuture.completedFuture(null))
			.thenReturn(CompletableFuture.completedFuture(file));
		CompletableFuture<Void> played = playbackCompletion(file, Thread.currentThread());
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		played.get(2, TimeUnit.SECONDS);
		verify(resolver, times(2)).download(anyString(), any());
		verify(audio).play(eq(file), anyFloat());
		verifyNoMoreInteractions(audio);
	}

	private CompletableFuture<Void> playbackCompletion(File file, Thread caller) throws Exception
	{
		CompletableFuture<Void> result = new CompletableFuture<>();
		doAnswer(invocation ->
		{
			assertNotSame(caller, Thread.currentThread());
			result.complete(null);
			return null;
		}).when(audio).play(eq(file), anyFloat());
		return result;
	}

	/** A later local playback is a barrier for work queued on the single audio worker. */
	private void drainWorker() throws Exception
	{
		File sentinel = new File("sentinel.wav");
		manager.trigger(ToaEvent.CRONDIS_LEAVE);
		drained.get(2, TimeUnit.SECONDS);
		verify(audio).play(eq(sentinel), anyFloat());
		verifyNoMoreInteractions(audio);
	}
}
