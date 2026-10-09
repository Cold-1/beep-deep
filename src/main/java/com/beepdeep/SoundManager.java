package com.beepdeep;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Preferences;
import net.runelite.api.SoundEffectVolume;
import net.runelite.client.audio.AudioPlayer;

/**
 * Selects and plays the sounds configured for a {@link ToaEvent}.
 *
 * <p>Slots use the typed config bindings in {@link ToaEvent}.
 * One non-empty slot is chosen at random per trigger. Decoding and playback run
 * on a dedicated single background thread because {@link AudioPlayer#play} buffers
 * the whole clip synchronously and must never run on the client thread.
 *
 * <p>Sound slot can be a sound effect ID (numeric), file path, or URL.
 */
@Singleton
@Slf4j
class SoundManager
{
	private final Client client;
	private final AudioPlayer audioPlayer;
	private final SoundFileResolver resolver;
	private final BeepDeepConfig config;
	private final Random random = new Random();

	private volatile ExecutorService executor;

	@Inject
	SoundManager(Client client, AudioPlayer audioPlayer, SoundFileResolver resolver, BeepDeepConfig config)
	{
		this.client = client;
		this.audioPlayer = audioPlayer;
		this.resolver = resolver;
		this.config = config;
	}

	void startUp()
	{
		shutDown();
		resolver.startUp();
		executor = Executors.newSingleThreadExecutor(r ->
		{
			Thread thread = new Thread(r, "beep-deep-audio");
			thread.setDaemon(true);
			return thread;
		});
	}

	void shutDown()
	{
		ExecutorService session = executor;
		executor = null;
		resolver.shutDown();
		if (session != null)
		{
			session.shutdownNow();
		}
	}

	/**
	 * Plays a random configured sound for the given event, if the event is
	 * enabled and has at least one non-empty slot. Safe to call from the client
	 * thread; all blocking work is offloaded to the audio executor.
	 */
	void trigger(ToaEvent event)
	{
		ExecutorService session = executor;
		if (session == null || !event.isEnabled(config))
		{
			return;
		}

		List<Slot> slots = filledSlots(event);
		if (slots.isEmpty())
		{
			return;
		}

		Slot chosen = slots.get(random.nextInt(slots.size()));
		float volume = chosen.volume * Math.max(0, Math.min(100, config.masterVolume())) / 100f;
		if (volume == 0)
		{
			return;
		}
		log.debug("Beep Deep: event {} -> playing '{}' at volume {}", event, chosen.source, volume);

		// Try to parse as sound effect ID (must run on client thread)
		Integer soundId = tryParseSoundId(chosen.source);
		if (soundId != null)
		{
			playEffect(soundId, volume);
			return;
		}

		// File/URL handling (offload to executor for blocking I/O)
		submit(session, () -> resolveAndPlayFile(session, chosen.source, volume));
	}

	private void resolveAndPlayFile(ExecutorService session, String source, float volume)
	{
		// Treat as file path or URL
		if (SoundFileResolver.isRemote(source))
		{
			if (!config.enableRemoteUrls())
			{
				log.debug("Beep Deep: remote URLs are disabled, skipping {}", source);
				return;
			}

			File cached = resolver.cachedFile(source);
			if (cached != null)
			{
				if (config.enableRemoteUrls())
				{
					playFile(session, cached, volume);
				}
				return;
			}

			resolver.download(source, () -> executor == session && config.enableRemoteUrls()).thenAccept(file ->
			{
				if (file != null)
				{
					submit(session, () ->
					{
						if (config.enableRemoteUrls())
						{
							playFile(session, file, volume);
						}
					});
				}
			});
		}
		else
		{
			try
			{
				playFile(session, resolver.localFile(source), volume);
			}
			catch (IOException | IllegalArgumentException e)
			{
				log.debug("Beep Deep: could not resolve sound file {}: {}", source, e.getMessage());
			}
		}
	}

	/** Capture the originating session so old callbacks cannot play after a restart. */
	private void submit(ExecutorService session, Runnable task)
	{
		if (executor != session)
		{
			return;
		}
		try
		{
			session.execute(() ->
			{
				if (executor == session && !Thread.currentThread().isInterrupted())
				{
					task.run();
				}
			});
		}
		catch (RejectedExecutionException e)
		{
			log.debug("Beep Deep: audio session stopped before work could be queued");
		}
	}

	private void playFile(ExecutorService session, File file, float volume)
	{
		if (volume <= 0 || executor != session || Thread.currentThread().isInterrupted())
		{
			return;
		}

		try
		{
			audioPlayer.play(file, gainForVolume(volume));
		}

		catch (Exception e)
		{
			log.debug("Beep Deep: could not play sound file {}: {}", file, e.getMessage());
		}
	}

	/**
	 * Plays a RuneScape sound effect by ID.
	 */
	private void playEffect(int soundId, float volumePercent)
	{
		int effectVolume = effectVolumeFromPercent(volumePercent);
		if (effectVolume == SoundEffectVolume.MUTED)
		{
			return;
		}
		log.debug("Beep Deep: playing sound effect {} at volume {}", soundId, effectVolume);
		try
		{
			Preferences preferences = client.getPreferences();
			int gameVolume = preferences.getSoundEffectVolume();
			if (gameVolume == SoundEffectVolume.MUTED)
			{
				client.playSoundEffect(soundId, effectVolume);
				return;
			}

			// RuneLite otherwise overrides the supplied volume with the game setting.
			// The stream is created synchronously, so restore the setting immediately.
			try
			{
				preferences.setSoundEffectVolume(SoundEffectVolume.MUTED);
				client.playSoundEffect(soundId, effectVolume);
			}
			finally
			{
				preferences.setSoundEffectVolume(gameVolume);
			}
		}
		catch (RuntimeException e)
		{
			log.debug("Beep Deep: failed to play sound effect {}: {}", soundId, e.getMessage());
		}
	}

	/**
	 * Converts 0-100 volume to the client's 0-127 sound effect range.
	 */
	static int effectVolumeFromPercent(float percent)
	{
		if (percent <= 0)
		{
			return 0;
		}
		return Math.min(127, Math.round(percent * 127f / 100f));
	}

	/**
	 * Tries to parse source as a sound effect ID (numeric).
	 * Returns null if not numeric.
	 */
	static Integer tryParseSoundId(String source)
	{
		if (source == null || source.isEmpty())
		{
			return null;
		}

		String trimmed = source.trim();
		if (!trimmed.matches("^\\d+$"))
		{
			return null;
		}

		try
		{
			return Integer.parseInt(trimmed);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	private List<Slot> filledSlots(ToaEvent event)
	{
		List<Slot> slots = new ArrayList<>();
		for (ToaEvent.SoundSlot slot : event.getSlots())
		{
			String source = normalizeSource(slot.source(config));
			if (source.isEmpty())
			{
				continue;
			}
			slots.add(new Slot(source, slot.volume(config)));
		}
		return slots;
	}

	/**
	 * Converts a positive linear percentage into the decibel gain expected by
	 * {@link AudioPlayer}. 100 maps to 0 dB (no attenuation).
	 */
	static float gainForVolume(float volume)
	{
		return (float) (20.0 * Math.log10(volume / 100.0));
	}

	/**
	 * Strips surrounding double quotes from a path if present.
	 * Allows quoted file paths pasted from a file manager.
	 * Numeric strings (sound IDs) are not affected.
	 */
	static String normalizeSource(String source)
	{
		if (source == null)
		{
			return "";
		}
		source = source.trim();
		if (source.length() >= 2 && source.startsWith("\"") && source.endsWith("\""))
		{
			return source.substring(1, source.length() - 1).trim();
		}
		return source;
	}

	private static final class Slot
	{
		private final String source;
		private final int volume;

		private Slot(String source, int volume)
		{
			this.source = source;
			this.volume = volume;
		}
	}
}
