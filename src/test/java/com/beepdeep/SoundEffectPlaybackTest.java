package com.beepdeep;

import java.util.concurrent.atomic.AtomicInteger;
import net.runelite.api.Client;
import net.runelite.api.Preferences;
import net.runelite.client.audio.AudioPlayer;
import net.runelite.client.callback.ClientThread;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SoundEffectPlaybackTest
{
	private Client client;
	private Preferences preferences;
	private BeepDeepConfig config;
	private SoundManager manager;
	private final AtomicInteger gameVolume = new AtomicInteger(96);
	private final AtomicInteger playedVolume = new AtomicInteger(-1);

	@Before
	public void setUp()
	{
		client = mock(Client.class);
		preferences = mock(Preferences.class);
		config = mock(BeepDeepConfig.class);
		when(client.getPreferences()).thenReturn(preferences);
		when(preferences.getSoundEffectVolume()).thenAnswer(invocation -> gameVolume.get());
		doAnswer(invocation ->
		{
			gameVolume.set(invocation.getArgument(0));
			return null;
		}).when(preferences).setSoundEffectVolume(anyInt());
		// Reproduce RuneLite's API: a nonzero game volume overrides the argument.
		doAnswer(invocation ->
		{
			playedVolume.set(gameVolume.get() == 0 ? invocation.getArgument(1) : gameVolume.get());
			return null;
		}).when(client).playSoundEffect(anyInt(), anyInt());
		when(config.crondisEnterEnabled()).thenReturn(true);
		when(config.crondisEnterSound1()).thenReturn("2192");
		when(config.crondisEnterVolume1()).thenReturn(80);
		when(config.masterVolume()).thenReturn(50);
		ClientThread clientThread = mock(ClientThread.class);
		doAnswer(invocation ->
		{
			((Runnable) invocation.getArgument(0)).run();
			return null;
		}).when(clientThread).invoke(any(Runnable.class));
		manager = new SoundManager(client, clientThread, null, mock(AudioPlayer.class), mock(SoundFileResolver.class), config);
		manager.startUp();
	}

	@After
	public void tearDown()
	{
		manager.shutDown();
	}

	@Test
	public void pluginVolumesApplyWithGameSoundEnabledAndSettingIsRestored()
	{
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		assertEquals(51, playedVolume.get());
		assertEquals(96, gameVolume.get());
		when(config.masterVolume()).thenReturn(100);
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		assertEquals(102, playedVolume.get());
		assertEquals(96, gameVolume.get());
		when(config.crondisEnterVolume1()).thenReturn(20);
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		assertEquals(25, playedVolume.get());
		assertEquals(96, gameVolume.get());
		verify(preferences, times(3)).setSoundEffectVolume(0);
		verify(preferences, times(3)).setSoundEffectVolume(96);
	}

	@Test
	public void alreadyMutedGameAudioUsesPluginVolumeWithoutChangingSetting()
	{
		gameVolume.set(0);
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		assertEquals(51, playedVolume.get());
		assertEquals(0, gameVolume.get());
		verify(preferences, never()).setSoundEffectVolume(anyInt());
	}

	@Test
	public void playbackFailureStillRestoresGameVolume()
	{
		doThrow(new IllegalStateException("Playback failed")).when(client).playSoundEffect(2192, 51);
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		assertEquals(96, gameVolume.get());
		verify(preferences).setSoundEffectVolume(0);
		verify(preferences).setSoundEffectVolume(96);
	}

	@Test
	public void mutedOrSubResolutionPluginVolumeDoesNotPlayOrChangeGameSettings()
	{
		when(config.masterVolume()).thenReturn(0);
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		when(config.masterVolume()).thenReturn(100);
		when(config.crondisEnterVolume1()).thenReturn(0);
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		when(config.masterVolume()).thenReturn(1);
		when(config.crondisEnterVolume1()).thenReturn(1);
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		assertEquals(-1, playedVolume.get());
		assertEquals(96, gameVolume.get());
		verifyNoInteractions(client, preferences);
	}
}
