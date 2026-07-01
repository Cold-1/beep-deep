package com.beepdeep;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.audio.AudioPlayer;
import net.runelite.client.config.ConfigManager;

/**
 * Selects and plays the sounds configured for a {@link ToaEvent}.
 *
 * <p>Slots are read generically from the config so there is no per-event switch.
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
	private final ConfigManager configManager;
	private final Random random = new Random();

	private ExecutorService executor;

	@Inject
	SoundManager(Client client, AudioPlayer audioPlayer, SoundFileResolver resolver, BeepDeepConfig config, ConfigManager configManager)
	{
		this.client = client;
		this.audioPlayer = audioPlayer;
		this.resolver = resolver;
		this.config = config;
		this.configManager = configManager;
	}

	void startUp()
	{
		executor = Executors.newSingleThreadExecutor(r ->
		{
			Thread thread = new Thread(r, "beep-deep-audio");
			thread.setDaemon(true);
			return thread;
		});
	}

	void shutDown()
	{
		if (executor != null)
		{
			executor.shutdownNow();
			executor = null;
		}
	}

	/**
	 * Plays a random configured sound for the given event, if the event is
	 * enabled and has at least one non-empty slot. Safe to call from the client
	 * thread; all blocking work is offloaded to the audio executor.
	 */
	void trigger(ToaEvent event)
	{
		if (!isEnabled(event))
		{
			return;
		}

		List<Slot> slots = filledSlots(event);
		if (slots.isEmpty())
		{
			return;
		}

		Slot chosen = slots.get(random.nextInt(slots.size()));
		log.debug("Beep Deep: event {} -> playing '{}' at volume {}", event, chosen.source, chosen.volume);

		// Try to parse as sound effect ID (must run on client thread)
		Integer soundId = tryParseSoundId(chosen.source);
		if (soundId != null)
		{
			playEffect(soundId, chosen.volume);
			return;
		}

		// File/URL handling (offload to executor for blocking I/O)
		ExecutorService ex = executor;
		if (ex != null)
		{
			ex.submit(() -> resolveAndPlayFile(chosen.source, chosen.volume));
		}
	}

	private void resolveAndPlayFile(String source, int volume)
	{
		// Treat as file path or URL
		if (resolver.isRemote(source))
		{
			if (!config.enableRemoteUrls())
			{
				log.debug("Beep Deep: remote URLs are disabled, skipping {}", source);
				return;
			}

			File cached = resolver.cachedFile(source);
			if (cached != null)
			{
				playFile(cached, volume);
				return;
			}

			resolver.download(source, file ->
			{
				ExecutorService ex = executor;
				if (ex != null)
				{
					ex.submit(() -> playFile(file, volume));
				}
			});
		}
		else
		{
			playFile(new File(source), volume);
		}
	}

	private void playFile(File file, int volume)
	{
		if (volume <= 0)
		{
			return;
		}

		try
		{
			audioPlayer.play(file, gainForVolume(volume));
		}
		catch (UnsupportedAudioFileException e)
		{
			log.warn("Beep Deep: unsupported audio format for {} (only WAV/AU/AIFF are supported): {}", file, e.getMessage());
		}
		catch (IOException e)
		{
			log.warn("Beep Deep: could not read sound file {}: {}", file, e.getMessage());
		}
		catch (LineUnavailableException e)
		{
			log.warn("Beep Deep: audio line unavailable for {}: {}", file, e.getMessage());
		}
	}

	/**
	 * Plays a RuneScape sound effect by ID.
	 */
	private void playEffect(int soundId, int volumePercent)
	{
		int effectVolume = effectVolumeFromPercent(volumePercent);
		log.debug("Beep Deep: playing sound effect {} at volume {}", soundId, effectVolume);
		try
		{
			client.playSoundEffect(soundId, effectVolume);
		}
		catch (Exception e)
		{
			log.warn("Beep Deep: failed to play sound effect {}: {}", soundId, e.getMessage());
		}
	}

	/**
	 * Converts 0-100 volume to 0-127 MIDI range (0=silent, 127=max volume).
	 */
	private static int effectVolumeFromPercent(int percent)
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
	private static Integer tryParseSoundId(String source)
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

	private boolean isEnabled(ToaEvent event)
	{
		String value = configManager.getConfiguration(BeepDeepConfig.GROUP, event.enabledKey());
		// Events default to enabled (see BeepDeepConfig); only an explicit "false" disables one.
		return !"false".equals(value);
	}

	private List<Slot> filledSlots(ToaEvent event)
	{
		List<Slot> slots = new ArrayList<>();
		for (int slot = 1; slot <= ToaEvent.SLOT_COUNT; slot++)
		{
			String source = configManager.getConfiguration(BeepDeepConfig.GROUP, event.soundKey(slot));
			if (source == null || source.trim().isEmpty())
			{
				continue;
			}
			source = stripQuotes(source.trim());
			slots.add(new Slot(source, readVolume(event, slot)));
		}
		return slots;
	}

	private int readVolume(ToaEvent event, int slot)
	{
		String value = configManager.getConfiguration(BeepDeepConfig.GROUP, event.volumeKey(slot));
		if (value == null)
		{
			return 100;
		}
		try
		{
			return Math.max(0, Math.min(100, Integer.parseInt(value.trim())));
		}
		catch (NumberFormatException e)
		{
			return 100;
		}
	}

	/**
	 * Converts a linear 1-100 volume into the decibel gain expected by
	 * {@link AudioPlayer}. 100 maps to 0 dB (no attenuation).
	 */
	static float gainForVolume(int volume)
	{
		return (float) (20.0 * Math.log10(volume / 100.0));
	}

	/**
	 * Strips surrounding double quotes from a path if present.
	 * Allows users to configure paths like: C:\sounds\alert.wav or "C:\sounds\alert.wav"
	 * Numeric strings (sound IDs) are not affected.
	 */
	private static String stripQuotes(String source)
	{
		if (source.length() >= 2 && source.startsWith("\"") && source.endsWith("\""))
		{
			return source.substring(1, source.length() - 1);
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
