package com.beepdeep;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;

/** Sound-slot-count-prefixed, compressed clipboard format using only Java's standard library. */
final class SoundConfigurationCodec
{
	static final int SOUND_SLOT_COUNT = Arrays.stream(ToaEvent.values())
		.mapToInt(event -> event.getSlots().size()).sum();
	static final String PREFIX = "BD" + SOUND_SLOT_COUNT + ":";
	static final int MAX_CODE_LENGTH = 1_000_000;
	private static final int MAX_PAYLOAD_LENGTH = 2_000_000;
	private static final int MAX_SOURCE_LENGTH = 4096;

	private SoundConfigurationCodec()
	{
	}

	static Export encode(BeepDeepConfig config)
	{
		boolean absolutePaths = false;
		try
		{
			ByteArrayOutputStream compressed = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(new DeflaterOutputStream(compressed)))
			{
				out.writeByte(ToaEvent.values().length);
				for (ToaEvent event : ToaEvent.values())
				{
					// Use stable configuration keys, never enum ordinals.
					out.writeUTF(event.getConfigPrefix());
					out.writeByte(event.isEnabled(config) ? 1 : 0);
					out.writeByte(event.getSlots().size());
					for (ToaEvent.SoundSlot slot : event.getSlots())
					{
						String source = slot.source(config);
						if (source == null)
						{
							source = "";
						}
						validateSource(source);
						absolutePaths |= isAbsolutePath(source);
						out.writeUTF(source);
						out.writeByte(slot.volume(config));
					}
				}
			}
			String code = PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(compressed.toByteArray());
			if (code.length() > MAX_CODE_LENGTH)
			{
				throw new IllegalArgumentException("This sound configuration is too large to share.");
			}
			return new Export(code, absolutePaths);
		}
		catch (IOException e)
		{
			throw new IllegalArgumentException("Could not encode the sound configuration.", e);
		}
	}

	static Imported decode(String text)
	{
		if (text == null || text.trim().isEmpty())
		{
			throw new IllegalArgumentException("Paste a Beep Deep configuration code first.");
		}
		if (text.length() > MAX_CODE_LENGTH)
		{
			throw new IllegalArgumentException("The pasted configuration is too large.");
		}
		String code = text.replaceAll("\\s", "");
		if (!code.startsWith(PREFIX))
		{
			int separator = code.indexOf(':');
			if (code.startsWith("BD") && separator > 2
				&& code.substring(2, separator).matches("[0-9]+"))
			{
				throw new IllegalArgumentException("The code uses a different number of sound slots. This plugin expects "
					+ SOUND_SLOT_COUNT + " slots. Use a compatible plugin version.");
			}
			throw new IllegalArgumentException("Expected a Beep Deep code starting with " + PREFIX + ".");
		}
		try
		{
			byte[] payload = inflate(decodeBase64(code.substring(PREFIX.length())));
			Map<String, ToaEvent> events = new LinkedHashMap<>();
			for (ToaEvent event : ToaEvent.values())
			{
				events.put(event.getConfigPrefix(), event);
			}
			Map<String, Object> settings = new LinkedHashMap<>();
			boolean absolutePaths = false;
			try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(payload)))
			{
				int count = in.readUnsignedByte();
				if (count != events.size())
				{
					throw new IllegalArgumentException("The code is incomplete or uses a different set of events. Use matching plugin versions.");
				}
				for (int i = 0; i < count; i++)
				{
					String key = in.readUTF();
					ToaEvent event = events.remove(key);
					if (event == null)
					{
						throw new IllegalArgumentException("The code contains an unknown or duplicate event assignment.");
					}
					int enabled = in.readUnsignedByte();
					if (enabled > 1)
					{
						throw new IllegalArgumentException("The code contains an invalid event toggle.");
					}
					settings.put(key + "Enabled", enabled == 1);
					if (in.readUnsignedByte() != event.getSlots().size())
					{
						throw new IllegalArgumentException("The code contains an incomplete sound slot assignment.");
					}
					for (int slot = 1; slot <= event.getSlots().size(); slot++)
					{
						String source = in.readUTF();
						validateSource(source);
						absolutePaths |= isAbsolutePath(source);
						settings.put(key + "Sound" + slot, source);
						settings.put(key + "Volume" + slot, readVolume(in));
					}
				}
				if (in.read() != -1)
				{
					throw new IllegalArgumentException("The code contains unexpected extra configuration data.");
				}
			}
			return new Imported(settings, absolutePaths);
		}
		catch (IOException | DataFormatException e)
		{
			throw new IllegalArgumentException("The code is invalid or incomplete. Copy the entire exported code and try again.", e);
		}
	}

	private static byte[] decodeBase64(String encoded)
	{
		try
		{
			return Base64.getUrlDecoder().decode(encoded);
		}
		catch (IllegalArgumentException e)
		{
			throw new IllegalArgumentException("The code is invalid or incomplete. Copy the entire exported code and try again.", e);
		}
	}

	private static byte[] inflate(byte[] compressed) throws DataFormatException
	{
		Inflater inflater = new Inflater();
		try
		{
			inflater.setInput(compressed);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			while (!inflater.finished())
			{
				int size = inflater.inflate(buffer);
				if (out.size() + size > MAX_PAYLOAD_LENGTH)
				{
					throw new IllegalArgumentException("The pasted configuration expands beyond the size limit.");
				}
				out.write(buffer, 0, size);
				if (size == 0 && !inflater.finished())
				{
					throw new DataFormatException("Incomplete compressed configuration");
				}
			}
			if (inflater.getRemaining() != 0)
			{
				throw new DataFormatException("Extra compressed data");
			}
			return out.toByteArray();
		}
		finally
		{
			inflater.end();
		}
	}

	private static int readVolume(DataInputStream in) throws IOException
	{
		int volume = in.readUnsignedByte();
		if (volume > 100)
		{
			throw new IllegalArgumentException("The code contains a volume outside 0-100%.");
		}
		return volume;
	}

	private static void validateSource(String source)
	{
		if (source.length() > MAX_SOURCE_LENGTH)
		{
			throw new IllegalArgumentException("A sound link or path exceeds the 4096-character limit.");
		}
		for (int i = 0; i < source.length(); i++)
		{
			if (Character.isISOControl(source.charAt(i)))
			{
				throw new IllegalArgumentException("A sound link or path contains an invalid control character.");
			}
		}
	}

	static boolean isAbsolutePath(String source)
	{
		String path = source.trim();
		// Recognize Unix, drive-letter and UNC paths regardless of the importing OS.
		return path.startsWith("/") || path.startsWith("\\")
			|| path.matches("(?i)^[a-z]:[\\\\/].*");
	}

	static final class Export
	{
		final String code;
		final boolean absolutePaths;

		private Export(String code, boolean absolutePaths)
		{
			this.code = code;
			this.absolutePaths = absolutePaths;
		}
	}

	static final class Imported
	{
		final Map<String, Object> settings;
		final boolean absolutePaths;

		private Imported(Map<String, Object> settings, boolean absolutePaths)
		{
			this.settings = Collections.unmodifiableMap(settings);
			this.absolutePaths = absolutePaths;
		}
	}
}
