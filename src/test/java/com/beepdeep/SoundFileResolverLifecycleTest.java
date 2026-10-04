package com.beepdeep;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Timeout;
import org.junit.Test;

import static org.junit.Assert.*;

public class SoundFileResolverLifecycleTest
{
	@Test
	public void duplicateDownloadsShareRequestAndShutdownCancelsIt()
	{
		RecordingClient http = new RecordingClient();
		SoundFileResolver resolver = new SoundFileResolver(http);
		resolver.startUp();
		CompletableFuture<File> result = resolver.download("https://example.com/sound.wav", () -> true);
		assertSame(result, resolver.download("https://example.com/sound.wav", () -> true));
		assertEquals(1, http.calls.size());
		resolver.shutDown();
		assertTrue(http.calls.get(0).isCanceled());
		assertTrue(result.isCancelled());
		assertNull(resolver.download("https://example.com/sound.wav", () -> true).join());
	}

	@Test
	public void failedDownloadCanBeRetried()
	{
		RecordingClient http = new RecordingClient();
		SoundFileResolver resolver = new SoundFileResolver(http);
		resolver.startUp();
		try
		{
			CompletableFuture<File> result = resolver.download("https://example.com/sound.wav", () -> true);
			http.calls.get(0).fail();
			assertNull(result.join());
			assertNotSame(result, resolver.download("https://example.com/sound.wav", () -> true));
			assertEquals(2, http.calls.size());
		}
		finally
		{
			resolver.shutDown();
		}
	}

	@Test
	public void staleFailureDoesNotRemoveDownloadFromNewSession()
	{
		RecordingClient http = new RecordingClient();
		SoundFileResolver resolver = new SoundFileResolver(http);
		resolver.startUp();
		resolver.download("https://example.com/sound.wav", () -> true);
		resolver.shutDown();
		resolver.startUp();
		try
		{
			CompletableFuture<File> current = resolver.download("https://example.com/sound.wav", () -> true);
			http.calls.get(0).fail();
			assertSame(current, resolver.download("https://example.com/sound.wav", () -> true));
			assertEquals(2, http.calls.size());
		}
		finally
		{
			resolver.shutDown();
		}
	}

	@Test
	public void inactiveSessionAndInvalidUrlDoNotStartRequests()
	{
		RecordingClient http = new RecordingClient();
		SoundFileResolver resolver = new SoundFileResolver(http);
		resolver.startUp();
		try
		{
			assertNull(resolver.download("https://example.com/sound.wav", () -> false).join());
			assertNull(resolver.download("https://", () -> true).join());
			assertTrue(http.calls.isEmpty());
		}
		finally
		{
			resolver.shutDown();
		}
	}

	private static final class RecordingClient extends OkHttpClient
	{
		private final List<PendingCall> calls = new ArrayList<>();

		@Override
		public Call newCall(Request request)
		{
			PendingCall call = new PendingCall(request);
			calls.add(call);
			return call;
		}
	}

	private static final class PendingCall implements Call
	{
		private final Request request;
		private Callback callback;
		private boolean canceled;

		private PendingCall(Request request)
		{
			this.request = request;
		}

		void fail()
		{
			callback.onFailure(this, new IOException("Simulated download failure"));
		}

		@Override
		public Request request()
		{
			return request;
		}

		@Override
		public Response execute()
		{
			throw new UnsupportedOperationException("Tests require asynchronous requests");
		}

		@Override
		public void enqueue(Callback callback)
		{
			this.callback = callback;
		}

		@Override
		public void cancel()
		{
			canceled = true;
		}

		@Override
		public boolean isExecuted()
		{
			return callback != null;
		}

		@Override
		public boolean isCanceled()
		{
			return canceled;
		}

		@Override
		public Timeout timeout()
		{
			return new Timeout();
		}

		@Override
		public Call clone()
		{
			return new PendingCall(request);
		}
	}
}
