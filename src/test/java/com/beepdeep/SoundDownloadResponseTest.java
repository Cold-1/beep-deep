package com.beepdeep;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SoundDownloadResponseTest
{
	private static final String URL = "https://example.com/sound.aiff?token=1";
	@Rule
	public TemporaryFolder temporary = new TemporaryFolder(new File("build"));
	private OkHttpClient http;
	private Call call;
	private SoundFileResolver resolver;
	private File cache;

	@Before
	public void setUp() throws IOException
	{
		http = mock(OkHttpClient.class);
		call = mock(Call.class);
		when(http.newCall(any())).thenReturn(call);
		cache = temporary.newFolder();
		resolver = new SoundFileResolver(http, cache);
		resolver.startUp();
	}

	@After
	public void tearDown()
	{
		resolver.shutDown();
	}

	@Test
	public void successfulResponsePublishesBytesAndCanBeFoundByUrl() throws Exception
	{
		byte[] bytes = {1, 2, 3, 4};
		CompletableFuture<File> result = resolver.download(URL, () -> true);
		ResponseBody body = ResponseBody.create(MediaType.parse("audio/aiff"), bytes);
		callback().onResponse(call, response(200, body));
		File file = result.join();
		assertNotNull(file);
		assertEquals(cache.getCanonicalFile(), file.getParentFile().getCanonicalFile());
		assertTrue(file.getName().matches("[a-f0-9]{64}\\.aiff"));
		assertArrayEquals(bytes, Files.readAllBytes(file.toPath()));
		assertEquals(file, resolver.cachedFile(URL));
		assertNull(resolver.cachedFile(URL + "2")); // Query is part of the cache key.
		assertEquals(1, cache.list().length);
		ArgumentCaptor<Request> request = ArgumentCaptor.forClass(Request.class);
		verify(http).newCall(request.capture());
		assertEquals(URL, request.getValue().url().toString());
		verify(call, never()).execute();
	}

	@Test
	public void httpErrorClosesBodyDoesNotReadItAndAllowsRetry() throws Exception
	{
		BufferedSource source = mock(BufferedSource.class);
		ResponseBody body = body(source, 0);
		CompletableFuture<File> result = resolver.download(URL, () -> true);
		callback().onResponse(call, response(404, body));
		assertNull(result.join());
		verify(source).close();
		verify(source, never()).inputStream();
		assertEquals(0, cache.list().length);
		assertNotSame(result, resolver.download(URL, () -> true));
		verify(http, times(2)).newCall(any());
	}

	@Test
	public void declaredOversizeBodyIsRejectedBeforeReading() throws Exception
	{
		BufferedSource source = mock(BufferedSource.class);
		ResponseBody body = body(source, SoundFileResolver.MAX_DOWNLOAD_BYTES + 1);
		CompletableFuture<File> result = resolver.download(URL, () -> true);
		callback().onResponse(call, response(200, body));
		assertNull(result.join());
		verify(source, never()).inputStream();
		verify(source).close();
		assertEquals(0, cache.list().length);
	}

	@Test
	public void emptyBodyDoesNotBecomeACacheHit() throws Exception
	{
		CompletableFuture<File> result = resolver.download(URL, () -> true);
		callback().onResponse(call, response(200, ResponseBody.create(null, new byte[0])));
		assertNull(result.join());
		assertNull(resolver.cachedFile(URL));
		assertEquals(0, cache.list().length);
	}

	@Test
	public void bodyReadFailureLeavesNoPartialCacheAndCanRetry() throws Exception
	{
		BufferedSource source = mock(BufferedSource.class);
		ResponseBody body = body(source, -1);
		java.io.InputStream input = mock(java.io.InputStream.class);
		when(source.inputStream()).thenReturn(input);
		when(input.read(any(byte[].class))).thenThrow(new IOException("Connection reset"));
		CompletableFuture<File> result = resolver.download(URL, () -> true);
		callback().onResponse(call, response(200, body));
		assertNull(result.join());
		verify(input).close();
		verify(source).close();
		assertEquals(0, cache.list().length);
		assertNotSame(result, resolver.download(URL, () -> true));
	}

	@Test
	public void canceledResponseCannotPublishBytes() throws Exception
	{
		CompletableFuture<File> result = resolver.download(URL, () -> true);
		when(call.isCanceled()).thenReturn(true);
		callback().onResponse(call, response(200, ResponseBody.create(null, new byte[]{1})));
		assertNull(result.join());
		assertEquals(0, cache.list().length);
	}

	private Callback callback()
	{
		ArgumentCaptor<Callback> callback = ArgumentCaptor.forClass(Callback.class);
		verify(call).enqueue(callback.capture());
		return callback.getValue();
	}

	private ResponseBody body(BufferedSource source, long length)
	{
		return new ResponseBody()
		{
			@Override
			public MediaType contentType()
			{
				return null;
			}

			@Override
			public long contentLength()
			{
				return length;
			}

			@Override
			public BufferedSource source()
			{
				return source;
			}
		};
	}

	private Response response(int code, ResponseBody body)
	{
		return new Response.Builder().request(new Request.Builder().url(URL).build())
			.protocol(Protocol.HTTP_1_1).code(code).message("Test response").body(body).build();
	}
}
