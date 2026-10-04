package com.beepdeep;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class SoundFileResolverTest
{
	@Rule
	public TemporaryFolder temporary = new TemporaryFolder(new File("build"));

	@Test
	public void remoteSchemeDetectionIsLocaleIndependent()
	{
		Locale previous = Locale.getDefault();
		try
		{
			Locale.setDefault(new Locale("tr", "TR"));
			assertTrue(SoundFileResolver.isRemote("HTTPS://example.com/sound.wav"));
			assertFalse(SoundFileResolver.isRemote("beep-deep/sounds/alert.wav"));
		}
		finally
		{
			Locale.setDefault(previous);
		}
	}

	@Test
	public void cacheExtensionIgnoresQueryAndFragment()
	{
		assertEquals(".aiff", SoundFileResolver.extensionOf("https://example.com/sound.aiff?token=1#clip"));
		assertEquals(".wav", SoundFileResolver.extensionOf("https://example.com/audio"));
		assertEquals(".wav", SoundFileResolver.extensionOf("https://example.com/sound.bad%20"));
	}

	@Test
	public void localFilesResolveRelativeAndAbsolutePathsInsideRoot() throws IOException
	{
		Path root = temporary.newFolder().toPath();
		Path sounds = Files.createDirectories(root.resolve("beep-deep/sounds"));
		Path sound = Files.write(sounds.resolve("sound.wav"), new byte[]{1});
		assertEquals(sound.toRealPath().toFile(), SoundFileResolver.localFile(root, "beep-deep/sounds/sound.wav"));
		assertEquals(sound.toRealPath().toFile(), SoundFileResolver.localFile(root, sound.toAbsolutePath().toString()));
	}

	@Test
	public void externalAbsolutePathsWorkWithoutAnExistingRuneLiteDirectory() throws IOException
	{
		Path root = temporary.newFolder().toPath().resolve("missing-runelite");
		Path outside = temporary.newFile("external sound.wav").toPath().toRealPath();
		assertEquals(outside.toFile(), SoundFileResolver.localFile(root, outside.toString()));
		assertThrowsIo(() -> SoundFileResolver.localFile(root, outside.getParent().toString()));
		assertThrowsIo(() -> SoundFileResolver.localFile(root, outside.resolveSibling("missing.wav").toString()));
	}

	@Test
	public void localFilesRejectDirectoryTraversal() throws IOException
	{
		Path root = temporary.newFolder().toPath();
		File outside = temporary.newFile();
		assertThrowsIo(() -> SoundFileResolver.localFile(root, "../" + outside.getName()));
		assertThrowsIo(() -> SoundFileResolver.localFile(root, "."));
	}

	@Test
	public void completeDownloadReplacesCacheAndRemovesTemporaryFile() throws IOException
	{
		Path target = temporary.newFolder().toPath().resolve("sound.wav");
		Files.write(target, new byte[]{9});
		SoundFileResolver.writeToCache(new ByteArrayInputStream(new byte[]{1, 2, 3}), target, () -> false);
		assertArrayEquals(new byte[]{1, 2, 3}, Files.readAllBytes(target));
		assertOnlyTargetRemains(target);
	}

	@Test
	public void emptyOrCanceledDownloadPreservesExistingCache() throws IOException
	{
		Path target = temporary.newFolder().toPath().resolve("sound.wav");
		Files.write(target, new byte[]{9});
		assertThrowsIo(() -> SoundFileResolver.writeToCache(new ByteArrayInputStream(new byte[0]), target, () -> false));
		assertThrowsIo(() -> SoundFileResolver.writeToCache(new ByteArrayInputStream(new byte[]{1}), target, () -> true));
		assertArrayEquals(new byte[]{9}, Files.readAllBytes(target));
		assertOnlyTargetRemains(target);
	}

	@Test
	public void failedStreamPreservesCacheAndRemovesTemporaryFile() throws IOException
	{
		Path target = temporary.newFolder().toPath().resolve("sound.wav");
		Files.write(target, new byte[]{9});
		InputStream broken = new InputStream()
		{
			@Override
			public int read() throws IOException
			{
				throw new IOException("Simulated read failure");
			}
		};
		assertThrowsIo(() -> SoundFileResolver.writeToCache(broken, target, () -> false));
		assertArrayEquals(new byte[]{9}, Files.readAllBytes(target));
		assertOnlyTargetRemains(target);
	}

	@Test
	public void oversizedStreamIsRejectedWithoutPublishing() throws IOException
	{
		Path target = temporary.newFolder().toPath().resolve("sound.wav");
		InputStream oversized = new InputStream()
		{
			private long remaining = SoundFileResolver.MAX_DOWNLOAD_BYTES + 1;

			@Override
			public int read()
			{
				return remaining-- > 0 ? 0 : -1;
			}

			@Override
			public int read(byte[] buffer, int offset, int length)
			{
				if (remaining == 0)
				{
					return -1;
				}
				int count = (int) Math.min(remaining, length);
				remaining -= count;
				return count;
			}
		};
		assertThrowsIo(() -> SoundFileResolver.writeToCache(oversized, target, () -> false));
		assertFalse(Files.exists(target));
		assertEquals(0, target.getParent().toFile().list().length);
	}

	private static void assertOnlyTargetRemains(Path target)
	{
		assertArrayEquals(new String[]{target.getFileName().toString()}, target.getParent().toFile().list());
	}

	private static void assertThrowsIo(IoAction action) throws IOException
	{
		try
		{
			action.run();
			fail("Expected IOException");
		}
		catch (IOException expected)
		{
			// Expected failure.
		}
	}

	private interface IoAction
	{
		void run() throws IOException;
	}
}
