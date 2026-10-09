package com.beepdeep;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import net.runelite.client.audio.AudioPlayer;
import org.junit.Test;

import static org.junit.Assert.*;

public class SoundManagerLifecycleTest
{
	@Test
	public void callbackFromPreviousSessionCannotPlayAfterRestart() throws Exception
	{
		RecordingAudio audio = new RecordingAudio();
		PendingResolver resolver = new PendingResolver();
		SoundManager manager = new SoundManager(null, null, null, audio, resolver, remoteConfig(50));
		try
		{
			manager.startUp();
			manager.trigger(ToaEvent.CRONDIS_ENTER);
			CompletableFuture<File> oldDownload = resolver.requested.get(2, TimeUnit.SECONDS);
			manager.shutDown();
			manager.startUp();
			oldDownload.complete(new File("old.wav"));

			resolver.requested = new CompletableFuture<>();
			manager.trigger(ToaEvent.CRONDIS_ENTER);
			resolver.requested.get(2, TimeUnit.SECONDS).complete(new File("new.wav"));
			assertEquals("new.wav", audio.played.get(2, TimeUnit.SECONDS).getName());
			assertEquals(1, audio.count.get());
		}
		finally
		{
			manager.shutDown();
		}
	}

	@Test
	public void mutedSoundDoesNotStartDownload()
	{
		PendingResolver resolver = new PendingResolver();
		SoundManager manager = new SoundManager(null, null, null, new RecordingAudio(), resolver, remoteConfig(0));
		try
		{
			manager.startUp();
			manager.trigger(ToaEvent.CRONDIS_ENTER);
			assertFalse(resolver.requested.isDone());
		}
		finally
		{
			manager.shutDown();
		}
	}

	@Test
	public void stoppedManagerIgnoresTriggers()
	{
		PendingResolver resolver = new PendingResolver();
		SoundManager manager = new SoundManager(null, null, null, new RecordingAudio(), resolver, remoteConfig(50));
		manager.trigger(ToaEvent.CRONDIS_ENTER);
		assertFalse(resolver.requested.isDone());
	}

	private static BeepDeepConfig remoteConfig(int volume)
	{
		return new BeepDeepConfig()
		{
			@Override
			public boolean enableRemoteUrls()
			{
				return true;
			}

			@Override
			public String crondisEnterSound1()
			{
				return "https://example.com/sound.wav";
			}

			@Override
			public int crondisEnterVolume1()
			{
				return volume;
			}
		};
	}

	private static final class PendingResolver extends SoundFileResolver
	{
		private volatile CompletableFuture<CompletableFuture<File>> requested = new CompletableFuture<>();

		private PendingResolver()
		{
			super(null);
		}

		@Override
		File cachedFile(String url)
		{
			return null;
		}

		@Override
		synchronized CompletableFuture<File> download(String url, BooleanSupplier active)
		{
			CompletableFuture<File> result = new CompletableFuture<>();
			requested.complete(result);
			return result;
		}
	}

	private static final class RecordingAudio extends AudioPlayer
	{
		private final AtomicInteger count = new AtomicInteger();
		private final CompletableFuture<File> played = new CompletableFuture<>();

		@Override
		public void play(File file, float gain)
		{
			count.incrementAndGet();
			played.complete(file);
		}
	}
}
