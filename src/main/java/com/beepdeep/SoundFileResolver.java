package com.beepdeep;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;
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
 * Resolves a configured sound source (a local file path or an http(s) URL) into
 * a playable {@link File}. Remote sounds are downloaded once via OkHttp and
 * cached under {@code .runelite/beep-deep/cache}.
 *
 * <p>All disk and network access happens off the client thread: cache lookups
 * are performed on the {@link SoundManager} audio executor and downloads run on
 * the OkHttp dispatcher.
 */
@Singleton
@Slf4j
class SoundFileResolver
{
	/** Hard cap on a single downloaded sound to avoid filling the disk. */
	private static final long MAX_DOWNLOAD_BYTES = 25L * 1024 * 1024;

	private static final File CACHE_DIR =
		new File(RuneLite.RUNELITE_DIR, "beep-deep" + File.separator + "cache");

	private final OkHttpClient httpClient;

	@Inject
	SoundFileResolver(OkHttpClient httpClient)
	{
		this.httpClient = httpClient;
	}

	boolean isRemote(String source)
	{
		String s = source.toLowerCase();
		return s.startsWith("http://") || s.startsWith("https://");
	}

	/**
	 * Returns the cached file for a URL if it has already been downloaded, or
	 * {@code null} if it still needs to be fetched. Performs a disk stat, so it
	 * must be called off the client thread.
	 */
	File cachedFile(String url)
	{
		File file = cacheFileFor(url);
		return file.isFile() && file.length() > 0 ? file : null;
	}

	/**
	 * Downloads a remote sound and invokes {@code onReady} with the cached file
	 * once it is available. The download runs on the OkHttp dispatcher; the
	 * callback is invoked on that same OkHttp thread.
	 */
	void download(String url, Consumer<File> onReady)
	{
		HttpUrl parsed = HttpUrl.parse(url);
		if (parsed == null)
		{
			log.warn("Beep Deep: invalid sound URL {}", url);
			return;
		}

		Request request = new Request.Builder().url(parsed).build();
		httpClient.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				log.warn("Beep Deep: failed to download sound {}: {}", url, e.getMessage());
			}

			@Override
			public void onResponse(Call call, Response response)
			{
				try (Response res = response)
				{
					if (!res.isSuccessful())
					{
						log.warn("Beep Deep: failed to download sound {}: HTTP {}", url, res.code());
						return;
					}

					ResponseBody body = res.body();
					if (body == null)
					{
						log.warn("Beep Deep: empty response body for sound {}", url);
						return;
					}

					File target = cacheFileFor(url);
					if (writeToCache(body.byteStream(), target))
					{
						onReady.accept(target);
					}
				}
			}
		});
	}

	private boolean writeToCache(InputStream in, File target)
	{
		File dir = target.getParentFile();
		if (dir != null && !dir.isDirectory() && !dir.mkdirs())
		{
			log.warn("Beep Deep: could not create cache directory {}", dir);
			return false;
		}

		File tmp = new File(target.getParentFile(), target.getName() + ".tmp");
		long total = 0;
		try (OutputStream out = new FileOutputStream(tmp))
		{
			byte[] buffer = new byte[8192];
			int read;
			while ((read = in.read(buffer)) != -1)
			{
				total += read;
				if (total > MAX_DOWNLOAD_BYTES)
				{
					log.warn("Beep Deep: sound download exceeded {} bytes, aborting {}", MAX_DOWNLOAD_BYTES, target.getName());
					out.close();
					//noinspection ResultOfMethodCallIgnored
					tmp.delete();
					return false;
				}
				out.write(buffer, 0, read);
			}
		}
		catch (IOException e)
		{
			log.warn("Beep Deep: error writing sound cache for {}: {}", target.getName(), e.getMessage());
			//noinspection ResultOfMethodCallIgnored
			tmp.delete();
			return false;
		}

		//noinspection ResultOfMethodCallIgnored
		target.delete();
		if (!tmp.renameTo(target))
		{
			log.warn("Beep Deep: could not finalize sound cache file {}", target.getName());
			//noinspection ResultOfMethodCallIgnored
			tmp.delete();
			return false;
		}
		return true;
	}

	private File cacheFileFor(String url)
	{
		return new File(CACHE_DIR, hash(url) + extensionOf(url));
	}

	private static String extensionOf(String url)
	{
		String path = url;
		int query = path.indexOf('?');
		if (query >= 0)
		{
			path = path.substring(0, query);
		}
		int slash = path.lastIndexOf('/');
		int dot = path.lastIndexOf('.');
		if (dot > slash && dot < path.length() - 1)
		{
			String ext = path.substring(dot);
			if (ext.length() <= 6)
			{
				return ext;
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
			return Integer.toHexString(url.hashCode());
		}
	}
}
