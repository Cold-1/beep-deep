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
import net.runelite.client.audio.AudioPlayer;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.party.PartyService;

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
	private final ClientThread clientThread;
	private final PartyService partyService;
	private final AudioPlayer audioPlayer;
	private final SoundFileResolver resolver;
	private final BeepDeepConfig config;
	private final Random random = new Random();

	private volatile ExecutorService executor;

	@Inject
	SoundManager(Client client, ClientThread clientThread, PartyService partyService, AudioPlayer audioPlayer, SoundFileResolver resolver, BeepDeepConfig config)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.partyService = partyService;
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
		trigger(event, false);
	}

	void trigger(ToaEvent event, boolean transmit)
	{
		if (executor == null || !event.isEnabled(config))
		{
			return;
		}

		List<ToaEvent.SoundSlot> filled = filledSlots(event);
		if (filled.isEmpty())
		{
			return;
		}

		int index = filled.get(random.nextInt(filled.size())).index();
		trigger(event, index);

		if (transmit && partyService.isInParty())
		{
			BeepDeepPartyMessage message = new BeepDeepPartyMessage();
			message.setEvent(event.name());
			message.setSlotIndex(index);
			partyService.send(message);
		}
	}

	void trigger(ToaEvent event, int slotIndex)
	{
		ExecutorService session = executor;
		List<ToaEvent.SoundSlot> all = event.getSlots();
		if (session == null || !event.isEnabled(config) || slotIndex < 0 || slotIndex >= all.size())
		{
			return;
		}

		ToaEvent.SoundSlot slot = all.get(slotIndex);
		String source = normalizeSource(slot.source(config));
		int volume = slot.volume(config);
		if (source.isEmpty() || volume == 0)
		{
			return;
		}
		log.debug("Beep Deep: event {} sound {} -> playing '{}' at volume {}", event, slotIndex + 1, source, volume);

		// Try to parse as sound effect ID (must run on client thread)
		Integer soundId = tryParseSoundId(source);
		if (soundId != null)
		{
			playEffect(soundId, volume);
			return;
		}

		// File/URL handling (offload to executor for blocking I/O)
		submit(session, () -> resolveAndPlayFile(session, source, volume));
	}

	private void resolveAndPlayFile(ExecutorService session, String source, int volume)
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

	private void playFile(ExecutorService session, File file, int volume)
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
	private void playEffect(int soundId, int volumePercent)
	{
		int effectVolume = effectVolumeFromPercent(volumePercent);
		log.debug("Beep Deep: playing sound effect {} at volume {}", soundId, effectVolume);
		clientThread.invoke(() ->
		{
			try
			{
				client.playSoundEffect(soundId, effectVolume);
			}
			catch (RuntimeException e)
			{
				log.debug("Beep Deep: failed to play sound effect {}: {}", soundId, e.getMessage());
			}
		});
	}

	/**
	 * Converts 0-100 volume to the client's 0-127 sound effect range.
	 */
	static int effectVolumeFromPercent(int percent)
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

	private List<ToaEvent.SoundSlot> filledSlots(ToaEvent event)
	{
		List<ToaEvent.SoundSlot> filled = new ArrayList<>();
		for (ToaEvent.SoundSlot slot : event.getSlots())
		{
			if (!normalizeSource(slot.source(config)).isEmpty())
			{
				filled.add(slot);
			}
		}
		return filled;
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
}
