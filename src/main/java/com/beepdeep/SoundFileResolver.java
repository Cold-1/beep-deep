package com.beepdeep;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Resolves local files and caches remote sounds. Disk operations run on the audio
 * executor or OkHttp dispatcher, never on the client thread.
 */
@Singleton
@Slf4j
class SoundFileResolver
{
	static final long MAX_DOWNLOAD_BYTES = 25L * 1024 * 1024;
	private static final File CACHE_DIR =
		RuneLite.RUNELITE_DIR.toPath().resolve("plugin-data").resolve("beep-deep").resolve("cache").toFile();

	private final OkHttpClient httpClient;
	private final File cacheDir;
	private final Map<String, Download> downloads = new HashMap<>();
	private boolean running;

	@Inject
	SoundFileResolver(OkHttpClient httpClient)
	{
		this(httpClient, CACHE_DIR);
	}

	SoundFileResolver(OkHttpClient httpClient, File cacheDir)
	{
		this.httpClient = httpClient;
		this.cacheDir = cacheDir;
	}

	synchronized void startUp()
	{
		running = true;
	}

	synchronized void shutDown()
	{
		running = false;
		for (Download download : downloads.values())
		{
			download.call.cancel();
			download.result.cancel(false);
		}
		downloads.clear();
	}

	static boolean isRemote(String source)
	{
		String normalized = source.toLowerCase(Locale.ROOT);
		return normalized.startsWith("http://") || normalized.startsWith("https://");
	}

	/** Absolute paths may be external; relative paths resolve from .runelite. */
	File localFile(String source) throws IOException
	{
		return localFile(RuneLite.RUNELITE_DIR.toPath(), source);
	}

	static File localFile(Path root, String source) throws IOException
	{
		Path configured = Path.of(source);
		Path resolved;
		if (configured.isAbsolute())
		{
			resolved = configured.toRealPath();
		}
		else
		{
			Path resolvedRoot = root.toRealPath();
			resolved = resolvedRoot.resolve(configured).toRealPath();
			if (!resolved.startsWith(resolvedRoot))
			{
				throw new IOException("Relative sound paths must stay inside .runelite");
			}
		}
		if (!Files.isRegularFile(resolved))
		{
			throw new IOException("Sound paths must point to regular files");
		}
		return resolved.toFile();
	}

	/** Performs a disk stat; call off the client thread. */
	File cachedFile(String url)
	{
		File file = cacheFileFor(url);
		return file.isFile() && file.length() > 0 ? file : null;
	}

	/**
	 * Shares in-flight requests for the same URL. Failed downloads resolve to null,
	 * allowing the next trigger to retry. Shutdown cancels only this plugin's calls.
	 */
	synchronized CompletableFuture<File> download(String url, BooleanSupplier active)
	{
		if (!running || !active.getAsBoolean())
		{
			return CompletableFuture.completedFuture(null);
		}
		Download pending = downloads.get(url);
		if (pending != null)
		{
			return pending.result;
		}

		HttpUrl parsed = HttpUrl.parse(url);
		if (parsed == null)
		{
			log.debug("Beep Deep: invalid sound URL {}", url);
			return CompletableFuture.completedFuture(null);
		}

		Call call = httpClient.newCall(new Request.Builder().url(parsed).build());
		Download download = new Download(call);
		downloads.put(url, download);
		call.enqueue(new Callback()
		{
			@Override
			public void onFailure(Call failedCall, IOException e)
			{
				log.debug("Beep Deep: sound download failed for {}: {}", url, e.getMessage());
				finish(url, download, null);
			}

			@Override
			public void onResponse(Call responseCall, Response response)
			{
				File file = null;
				try (Response res = response)
				{
					ResponseBody body = res.body();
					if (!res.isSuccessful() || body == null || body.contentLength() > MAX_DOWNLOAD_BYTES)
					{
						log.debug("Beep Deep: rejected sound response for {} (HTTP {})", url, res.code());
						return;
					}
					File target = cacheFileFor(url);
					try (InputStream in = body.byteStream())
					{
						writeToCache(in, target.toPath(), responseCall::isCanceled);
					}
					if (!responseCall.isCanceled())
					{
						file = target;
					}
				}
				catch (IOException e)
				{
					log.debug("Beep Deep: could not cache sound {}: {}", url, e.getMessage());
				}
				finally
				{
					finish(url, download, file);
				}
			}
		});
		return download.result;
	}

	private void finish(String url, Download download, File file)
	{
		synchronized (this)
		{
			downloads.remove(url, download);
		}
		download.result.complete(file);
	}

	/**
	 * Publishes only complete, non-empty downloads. Each writer has its own temp
	 * file so cancellation and retries cannot overwrite another writer's work.
	 */
	static void writeToCache(InputStream in, Path target, BooleanSupplier canceled) throws IOException
	{
		Files.createDirectories(target.getParent());
		Path temporary = Files.createTempFile(target.getParent(), "sound-", ".tmp");
		try
		{
			long total = 0;
			try (OutputStream out = Files.newOutputStream(temporary))
			{
				byte[] buffer = new byte[8192];
				int read;
				while ((read = in.read(buffer)) != -1)
				{
					total += read;
					if (canceled.getAsBoolean() || total > MAX_DOWNLOAD_BYTES)
					{
						throw new IOException("Sound download canceled or exceeded size limit");
					}
					out.write(buffer, 0, read);
				}
			}
			if (total == 0 || canceled.getAsBoolean())
			{
				throw new IOException("Sound download empty or canceled");
			}
			try
			{
				Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
			}
			catch (AtomicMoveNotSupportedException e)
			{
				Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
			}
		}
		finally
		{
			Files.deleteIfExists(temporary);
		}
	}

	private File cacheFileFor(String url)
	{
		return new File(cacheDir, hash(url) + extensionOf(url));
	}

	static String extensionOf(String url)
	{
		HttpUrl parsed = HttpUrl.parse(url);
		if (parsed == null)
		{
			return ".wav";
		}
		String path = parsed.encodedPath();
		int dot = path.lastIndexOf('.');
		if (dot > path.lastIndexOf('/'))
		{
			String extension = path.substring(dot);
			if (extension.matches("\\.[a-zA-Z0-9]{1,5}"))
			{
				return extension;
			}
		}
		return ".wav";
	}

	private static String hash(String url)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(url.getBytes(StandardCharsets.UTF_8));
			return String.format("%064x", new BigInteger(1, digest));
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new IllegalStateException("SHA-256 is required by Java", e);
		}
	}

	private static final class Download
	{
		private final Call call;
		private final CompletableFuture<File> result = new CompletableFuture<>();

		private Download(Call call)
		{
			this.call = call;
		}
	}
}
