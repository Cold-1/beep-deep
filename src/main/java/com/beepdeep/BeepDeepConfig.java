package com.beepdeep;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup(BeepDeepConfig.GROUP)
public interface BeepDeepConfig extends Config
{
	String GROUP = "beepdeep";

	String REMOTE_WARNING =
		"This feature submits your IP address to a 3rd-party server not controlled or verified by RuneLite developers";

	String SOUND_DESC =
		"Sound effect ID (numeric), absolute file path, relative path inside .runelite, or URL (only used when remote URLs are enabled). "
			+ "For files: WAV, AU, AIFF. Leave blank to disable this slot.";

	// ===================== General =====================

	@ConfigSection(
		name = "General",
		description = "General Beep Deep options.",
		position = 0
	)
	String generalSection = "generalSection";

	@ConfigItem(
		keyName = "enableRemoteUrls",
		name = "Allow remote URLs",
		description = "Allow sounds to be loaded from http(s) URLs. Downloaded sounds are cached on disk.",
		section = generalSection,
		position = 0,
		warning = REMOTE_WARNING
	)
	default boolean enableRemoteUrls()
	{
		return false;
	}

	// ===================== Path of Crondis: Enter =====================

	@ConfigSection(
		name = "Path of Crondis - Enter",
		description = "Sounds played when you enter the Path of Crondis room.",
		position = 1,
		closedByDefault = true
	)
	String crondisEnterSection = "crondisEnterSection";

	@ConfigItem(keyName = "crondisEnterEnabled", name = "Enabled", description = "Play a sound for this event.", section = crondisEnterSection, position = 0)
	default boolean crondisEnterEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "crondisEnterSound1", name = "Sound 1", description = SOUND_DESC, section = crondisEnterSection, position = 1)
	default String crondisEnterSound1()
	{
		return "2192";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crondisEnterVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = crondisEnterSection, position = 2)
	default int crondisEnterVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "crondisEnterSound2", name = "Sound 2", description = SOUND_DESC, section = crondisEnterSection, position = 3)
	default String crondisEnterSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crondisEnterVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = crondisEnterSection, position = 4)
	default int crondisEnterVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "crondisEnterSound3", name = "Sound 3", description = SOUND_DESC, section = crondisEnterSection, position = 5)
	default String crondisEnterSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crondisEnterVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = crondisEnterSection, position = 6)
	default int crondisEnterVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "crondisEnterSound4", name = "Sound 4", description = SOUND_DESC, section = crondisEnterSection, position = 7)
	default String crondisEnterSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crondisEnterVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = crondisEnterSection, position = 8)
	default int crondisEnterVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "crondisEnterSound5", name = "Sound 5", description = SOUND_DESC, section = crondisEnterSection, position = 9)
	default String crondisEnterSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crondisEnterVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = crondisEnterSection, position = 10)
	default int crondisEnterVolume5()
	{
		return 50;
	}

	// ===================== Path of Crondis: Leave =====================

	@ConfigSection(
		name = "Path of Crondis - Leave",
		description = "Sounds played when you leave the Path of Crondis room.",
		position = 2,
		closedByDefault = true
	)
	String crondisLeaveSection = "crondisLeaveSection";

	@ConfigItem(keyName = "crondisLeaveEnabled", name = "Enabled", description = "Play a sound for this event.", section = crondisLeaveSection, position = 0)
	default boolean crondisLeaveEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "crondisLeaveSound1", name = "Sound 1", description = SOUND_DESC, section = crondisLeaveSection, position = 1)
	default String crondisLeaveSound1()
	{
		return "2192";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crondisLeaveVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = crondisLeaveSection, position = 2)
	default int crondisLeaveVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "crondisLeaveSound2", name = "Sound 2", description = SOUND_DESC, section = crondisLeaveSection, position = 3)
	default String crondisLeaveSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crondisLeaveVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = crondisLeaveSection, position = 4)
	default int crondisLeaveVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "crondisLeaveSound3", name = "Sound 3", description = SOUND_DESC, section = crondisLeaveSection, position = 5)
	default String crondisLeaveSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crondisLeaveVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = crondisLeaveSection, position = 6)
	default int crondisLeaveVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "crondisLeaveSound4", name = "Sound 4", description = SOUND_DESC, section = crondisLeaveSection, position = 7)
	default String crondisLeaveSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crondisLeaveVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = crondisLeaveSection, position = 8)
	default int crondisLeaveVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "crondisLeaveSound5", name = "Sound 5", description = SOUND_DESC, section = crondisLeaveSection, position = 9)
	default String crondisLeaveSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crondisLeaveVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = crondisLeaveSection, position = 10)
	default int crondisLeaveVolume5()
	{
		return 50;
	}

	// ===================== Path of Apmeken: Enter =====================

	@ConfigSection(
		name = "Path of Apmeken - Enter",
		description = "Sounds played when you enter the Path of Apmeken room.",
		position = 3,
		closedByDefault = true
	)
	String apmekenEnterSection = "apmekenEnterSection";

	@ConfigItem(keyName = "apmekenEnterEnabled", name = "Enabled", description = "Play a sound for this event.", section = apmekenEnterSection, position = 0)
	default boolean apmekenEnterEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "apmekenEnterSound1", name = "Sound 1", description = SOUND_DESC, section = apmekenEnterSection, position = 1)
	default String apmekenEnterSound1()
	{
		return "2192";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "apmekenEnterVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = apmekenEnterSection, position = 2)
	default int apmekenEnterVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "apmekenEnterSound2", name = "Sound 2", description = SOUND_DESC, section = apmekenEnterSection, position = 3)
	default String apmekenEnterSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "apmekenEnterVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = apmekenEnterSection, position = 4)
	default int apmekenEnterVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "apmekenEnterSound3", name = "Sound 3", description = SOUND_DESC, section = apmekenEnterSection, position = 5)
	default String apmekenEnterSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "apmekenEnterVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = apmekenEnterSection, position = 6)
	default int apmekenEnterVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "apmekenEnterSound4", name = "Sound 4", description = SOUND_DESC, section = apmekenEnterSection, position = 7)
	default String apmekenEnterSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "apmekenEnterVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = apmekenEnterSection, position = 8)
	default int apmekenEnterVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "apmekenEnterSound5", name = "Sound 5", description = SOUND_DESC, section = apmekenEnterSection, position = 9)
	default String apmekenEnterSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "apmekenEnterVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = apmekenEnterSection, position = 10)
	default int apmekenEnterVolume5()
	{
		return 50;
	}

	// ===================== Path of Apmeken: Leave =====================

	@ConfigSection(
		name = "Path of Apmeken - Leave",
		description = "Sounds played when you leave the Path of Apmeken room.",
		position = 4,
		closedByDefault = true
	)
	String apmekenLeaveSection = "apmekenLeaveSection";

	@ConfigItem(keyName = "apmekenLeaveEnabled", name = "Enabled", description = "Play a sound for this event.", section = apmekenLeaveSection, position = 0)
	default boolean apmekenLeaveEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "apmekenLeaveSound1", name = "Sound 1", description = SOUND_DESC, section = apmekenLeaveSection, position = 1)
	default String apmekenLeaveSound1()
	{
		return "2192";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "apmekenLeaveVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = apmekenLeaveSection, position = 2)
	default int apmekenLeaveVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "apmekenLeaveSound2", name = "Sound 2", description = SOUND_DESC, section = apmekenLeaveSection, position = 3)
	default String apmekenLeaveSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "apmekenLeaveVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = apmekenLeaveSection, position = 4)
	default int apmekenLeaveVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "apmekenLeaveSound3", name = "Sound 3", description = SOUND_DESC, section = apmekenLeaveSection, position = 5)
	default String apmekenLeaveSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "apmekenLeaveVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = apmekenLeaveSection, position = 6)
	default int apmekenLeaveVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "apmekenLeaveSound4", name = "Sound 4", description = SOUND_DESC, section = apmekenLeaveSection, position = 7)
	default String apmekenLeaveSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "apmekenLeaveVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = apmekenLeaveSection, position = 8)
	default int apmekenLeaveVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "apmekenLeaveSound5", name = "Sound 5", description = SOUND_DESC, section = apmekenLeaveSection, position = 9)
	default String apmekenLeaveSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "apmekenLeaveVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = apmekenLeaveSection, position = 10)
	default int apmekenLeaveVolume5()
	{
		return 50;
	}

	// ===================== Path of Apmeken: Banana Slip =====================

	@ConfigSection(
		name = "Path of Apmeken - Banana Slip",
		description = "Sounds played when any player slips on a banana peel in Ba-Ba's boss room.",
		position = 6,
		closedByDefault = true
	)
	String babaBananaSlipSection = "babaBananaSlipSection";

	@ConfigItem(keyName = "babaBananaSlipEnabled", name = "Enabled", description = "Play a sound for this event.", section = babaBananaSlipSection, position = 0)
	default boolean babaBananaSlipEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "babaBananaSlipSound1", name = "Sound 1", description = SOUND_DESC, section = babaBananaSlipSection, position = 1)
	default String babaBananaSlipSound1()
	{
		return "3892";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "babaBananaSlipVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = babaBananaSlipSection, position = 2)
	default int babaBananaSlipVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "babaBananaSlipSound2", name = "Sound 2", description = SOUND_DESC, section = babaBananaSlipSection, position = 3)
	default String babaBananaSlipSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "babaBananaSlipVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = babaBananaSlipSection, position = 4)
	default int babaBananaSlipVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "babaBananaSlipSound3", name = "Sound 3", description = SOUND_DESC, section = babaBananaSlipSection, position = 5)
	default String babaBananaSlipSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "babaBananaSlipVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = babaBananaSlipSection, position = 6)
	default int babaBananaSlipVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "babaBananaSlipSound4", name = "Sound 4", description = SOUND_DESC, section = babaBananaSlipSection, position = 7)
	default String babaBananaSlipSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "babaBananaSlipVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = babaBananaSlipSection, position = 8)
	default int babaBananaSlipVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "babaBananaSlipSound5", name = "Sound 5", description = SOUND_DESC, section = babaBananaSlipSection, position = 9)
	default String babaBananaSlipSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "babaBananaSlipVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = babaBananaSlipSection, position = 10)
	default int babaBananaSlipVolume5()
	{
		return 50;
	}

	// ===================== Path of Scabaras: Enter =====================

	@ConfigSection(
		name = "Path of Scabaras - Enter",
		description = "Sounds played when you enter the Path of Scabaras room.",
		position = 7,
		closedByDefault = true
	)
	String scabarasEnterSection = "scabarasEnterSection";

	@ConfigItem(keyName = "scabarasEnterEnabled", name = "Enabled", description = "Play a sound for this event.", section = scabarasEnterSection, position = 0)
	default boolean scabarasEnterEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "scabarasEnterSound1", name = "Sound 1", description = SOUND_DESC, section = scabarasEnterSection, position = 1)
	default String scabarasEnterSound1()
	{
		return "2192";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasEnterVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = scabarasEnterSection, position = 2)
	default int scabarasEnterVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasEnterSound2", name = "Sound 2", description = SOUND_DESC, section = scabarasEnterSection, position = 3)
	default String scabarasEnterSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasEnterVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = scabarasEnterSection, position = 4)
	default int scabarasEnterVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasEnterSound3", name = "Sound 3", description = SOUND_DESC, section = scabarasEnterSection, position = 5)
	default String scabarasEnterSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasEnterVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = scabarasEnterSection, position = 6)
	default int scabarasEnterVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasEnterSound4", name = "Sound 4", description = SOUND_DESC, section = scabarasEnterSection, position = 7)
	default String scabarasEnterSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasEnterVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = scabarasEnterSection, position = 8)
	default int scabarasEnterVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasEnterSound5", name = "Sound 5", description = SOUND_DESC, section = scabarasEnterSection, position = 9)
	default String scabarasEnterSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasEnterVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = scabarasEnterSection, position = 10)
	default int scabarasEnterVolume5()
	{
		return 50;
	}

	// ===================== Path of Scabaras: Leave =====================

	@ConfigSection(
		name = "Path of Scabaras - Leave",
		description = "Sounds played when you leave the Path of Scabaras room.",
		position = 8,
		closedByDefault = true
	)
	String scabarasLeaveSection = "scabarasLeaveSection";

	@ConfigItem(keyName = "scabarasLeaveEnabled", name = "Enabled", description = "Play a sound for this event.", section = scabarasLeaveSection, position = 0)
	default boolean scabarasLeaveEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "scabarasLeaveSound1", name = "Sound 1", description = SOUND_DESC, section = scabarasLeaveSection, position = 1)
	default String scabarasLeaveSound1()
	{
		return "2192";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasLeaveVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = scabarasLeaveSection, position = 2)
	default int scabarasLeaveVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasLeaveSound2", name = "Sound 2", description = SOUND_DESC, section = scabarasLeaveSection, position = 3)
	default String scabarasLeaveSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasLeaveVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = scabarasLeaveSection, position = 4)
	default int scabarasLeaveVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasLeaveSound3", name = "Sound 3", description = SOUND_DESC, section = scabarasLeaveSection, position = 5)
	default String scabarasLeaveSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasLeaveVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = scabarasLeaveSection, position = 6)
	default int scabarasLeaveVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasLeaveSound4", name = "Sound 4", description = SOUND_DESC, section = scabarasLeaveSection, position = 7)
	default String scabarasLeaveSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasLeaveVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = scabarasLeaveSection, position = 8)
	default int scabarasLeaveVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasLeaveSound5", name = "Sound 5", description = SOUND_DESC, section = scabarasLeaveSection, position = 9)
	default String scabarasLeaveSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasLeaveVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = scabarasLeaveSection, position = 10)
	default int scabarasLeaveVolume5()
	{
		return 50;
	}

	// ===================== Path of Het: Enter =====================

	@ConfigSection(
		name = "Path of Het - Enter",
		description = "Sounds played when you enter the Path of Het room.",
		position = 9,
		closedByDefault = true
	)
	String hetEnterSection = "hetEnterSection";

	@ConfigItem(keyName = "hetEnterEnabled", name = "Enabled", description = "Play a sound for this event.", section = hetEnterSection, position = 0)
	default boolean hetEnterEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "hetEnterSound1", name = "Sound 1", description = SOUND_DESC, section = hetEnterSection, position = 1)
	default String hetEnterSound1()
	{
		return "2192";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetEnterVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = hetEnterSection, position = 2)
	default int hetEnterVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetEnterSound2", name = "Sound 2", description = SOUND_DESC, section = hetEnterSection, position = 3)
	default String hetEnterSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetEnterVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = hetEnterSection, position = 4)
	default int hetEnterVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetEnterSound3", name = "Sound 3", description = SOUND_DESC, section = hetEnterSection, position = 5)
	default String hetEnterSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetEnterVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = hetEnterSection, position = 6)
	default int hetEnterVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetEnterSound4", name = "Sound 4", description = SOUND_DESC, section = hetEnterSection, position = 7)
	default String hetEnterSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetEnterVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = hetEnterSection, position = 8)
	default int hetEnterVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetEnterSound5", name = "Sound 5", description = SOUND_DESC, section = hetEnterSection, position = 9)
	default String hetEnterSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetEnterVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = hetEnterSection, position = 10)
	default int hetEnterVolume5()
	{
		return 50;
	}

	// ===================== Path of Het: Leave =====================

	@ConfigSection(
		name = "Path of Het - Leave",
		description = "Sounds played when you leave the Path of Het room.",
		position = 10,
		closedByDefault = true
	)
	String hetLeaveSection = "hetLeaveSection";

	@ConfigItem(keyName = "hetLeaveEnabled", name = "Enabled", description = "Play a sound for this event.", section = hetLeaveSection, position = 0)
	default boolean hetLeaveEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "hetLeaveSound1", name = "Sound 1", description = SOUND_DESC, section = hetLeaveSection, position = 1)
	default String hetLeaveSound1()
	{
		return "2192";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetLeaveVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = hetLeaveSection, position = 2)
	default int hetLeaveVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetLeaveSound2", name = "Sound 2", description = SOUND_DESC, section = hetLeaveSection, position = 3)
	default String hetLeaveSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetLeaveVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = hetLeaveSection, position = 4)
	default int hetLeaveVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetLeaveSound3", name = "Sound 3", description = SOUND_DESC, section = hetLeaveSection, position = 5)
	default String hetLeaveSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetLeaveVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = hetLeaveSection, position = 6)
	default int hetLeaveVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetLeaveSound4", name = "Sound 4", description = SOUND_DESC, section = hetLeaveSection, position = 7)
	default String hetLeaveSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetLeaveVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = hetLeaveSection, position = 8)
	default int hetLeaveVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetLeaveSound5", name = "Sound 5", description = SOUND_DESC, section = hetLeaveSection, position = 9)
	default String hetLeaveSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetLeaveVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = hetLeaveSection, position = 10)
	default int hetLeaveVolume5()
	{
		return 50;
	}

	// ===================== Path of Het: Unstable Orb Hit =====================

	@ConfigSection(
		name = "Path of Het - Unstable Orb Hit",
		description = "Sounds played when any player is hit by an Unstable Orb in Akkha's arena.",
		position = 12,
		closedByDefault = true
	)
	String hetUnstableOrbHitSection = "hetUnstableOrbHitSection";

	@ConfigItem(keyName = "hetUnstableOrbHitEnabled", name = "Enabled", description = "Play a sound for this event.", section = hetUnstableOrbHitSection, position = 0)
	default boolean hetUnstableOrbHitEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "hetUnstableOrbHitSound1", name = "Sound 1", description = SOUND_DESC, section = hetUnstableOrbHitSection, position = 1)
	default String hetUnstableOrbHitSound1()
	{
		return "2192";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetUnstableOrbHitVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = hetUnstableOrbHitSection, position = 2)
	default int hetUnstableOrbHitVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetUnstableOrbHitSound2", name = "Sound 2", description = SOUND_DESC, section = hetUnstableOrbHitSection, position = 3)
	default String hetUnstableOrbHitSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetUnstableOrbHitVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = hetUnstableOrbHitSection, position = 4)
	default int hetUnstableOrbHitVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetUnstableOrbHitSound3", name = "Sound 3", description = SOUND_DESC, section = hetUnstableOrbHitSection, position = 5)
	default String hetUnstableOrbHitSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetUnstableOrbHitVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = hetUnstableOrbHitSection, position = 6)
	default int hetUnstableOrbHitVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetUnstableOrbHitSound4", name = "Sound 4", description = SOUND_DESC, section = hetUnstableOrbHitSection, position = 7)
	default String hetUnstableOrbHitSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetUnstableOrbHitVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = hetUnstableOrbHitSection, position = 8)
	default int hetUnstableOrbHitVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetUnstableOrbHitSound5", name = "Sound 5", description = SOUND_DESC, section = hetUnstableOrbHitSection, position = 9)
	default String hetUnstableOrbHitSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetUnstableOrbHitVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = hetUnstableOrbHitSection, position = 10)
	default int hetUnstableOrbHitVolume5()
	{
		return 50;
	}

	// ===================== Path of Het: Seal Not One-Phased =====================

	@ConfigSection(
		name = "Path of Het - Seal Not One-Phased",
		description = "Sounds played when the Path of Het seal is not killed in one phase.",
		position = 11,
		closedByDefault = true
	)
	String hetOnePhaseFailSection = "hetOnePhaseFailSection";

	@ConfigItem(keyName = "hetOnePhaseFailEnabled", name = "Enabled", description = "Play a sound for this event.", section = hetOnePhaseFailSection, position = 0)
	default boolean hetOnePhaseFailEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "hetOnePhaseFailSound1", name = "Sound 1", description = SOUND_DESC, section = hetOnePhaseFailSection, position = 1)
	default String hetOnePhaseFailSound1()
	{
		return "3892";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetOnePhaseFailVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = hetOnePhaseFailSection, position = 2)
	default int hetOnePhaseFailVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetOnePhaseFailSound2", name = "Sound 2", description = SOUND_DESC, section = hetOnePhaseFailSection, position = 3)
	default String hetOnePhaseFailSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetOnePhaseFailVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = hetOnePhaseFailSection, position = 4)
	default int hetOnePhaseFailVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetOnePhaseFailSound3", name = "Sound 3", description = SOUND_DESC, section = hetOnePhaseFailSection, position = 5)
	default String hetOnePhaseFailSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetOnePhaseFailVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = hetOnePhaseFailSection, position = 6)
	default int hetOnePhaseFailVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetOnePhaseFailSound4", name = "Sound 4", description = SOUND_DESC, section = hetOnePhaseFailSection, position = 7)
	default String hetOnePhaseFailSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetOnePhaseFailVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = hetOnePhaseFailSection, position = 8)
	default int hetOnePhaseFailVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetOnePhaseFailSound5", name = "Sound 5", description = SOUND_DESC, section = hetOnePhaseFailSection, position = 9)
	default String hetOnePhaseFailSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetOnePhaseFailVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = hetOnePhaseFailSection, position = 10)
	default int hetOnePhaseFailVolume5()
	{
		return 50;
	}

	// ===================== Path of Apmeken: Issue Not Fixed =====================

	@ConfigSection(
		name = "Path of Apmeken - Issue Not Fixed",
		description = "Sounds played when a Path of Apmeken issue is not fixed in time.",
		position = 5,
		closedByDefault = true
	)
	String apmekenFailSection = "apmekenFailSection";

	@ConfigItem(keyName = "apmekenFailEnabled", name = "Enabled", description = "Play a sound for this event.", section = apmekenFailSection, position = 0)
	default boolean apmekenFailEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "apmekenFailSound1", name = "Sound 1", description = SOUND_DESC, section = apmekenFailSection, position = 1)
	default String apmekenFailSound1()
	{
		return "3892";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "apmekenFailVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = apmekenFailSection, position = 2)
	default int apmekenFailVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "apmekenFailSound2", name = "Sound 2", description = SOUND_DESC, section = apmekenFailSection, position = 3)
	default String apmekenFailSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "apmekenFailVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = apmekenFailSection, position = 4)
	default int apmekenFailVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "apmekenFailSound3", name = "Sound 3", description = SOUND_DESC, section = apmekenFailSection, position = 5)
	default String apmekenFailSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "apmekenFailVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = apmekenFailSection, position = 6)
	default int apmekenFailVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "apmekenFailSound4", name = "Sound 4", description = SOUND_DESC, section = apmekenFailSection, position = 7)
	default String apmekenFailSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "apmekenFailVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = apmekenFailSection, position = 8)
	default int apmekenFailVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "apmekenFailSound5", name = "Sound 5", description = SOUND_DESC, section = apmekenFailSection, position = 9)
	default String apmekenFailSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "apmekenFailVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = apmekenFailSection, position = 10)
	default int apmekenFailVolume5()
	{
		return 50;
	}

	// ===================== Vault: Loot Room - No Rare Loot =====================

	@ConfigSection(
		name = "Vault - No Rare Loot",
		description = "Sounds played when the ToA vault opens without rare loot.",
		position = 13,
		closedByDefault = true
	)
	String vaultNoRareLootSection = "vaultNoRareLootSection";

	@ConfigItem(keyName = "vaultNoRareLootEnabled", name = "Enabled", description = "Play a sound for this event.", section = vaultNoRareLootSection, position = 0)
	default boolean vaultNoRareLootEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "vaultNoRareLootSound1", name = "Sound 1", description = SOUND_DESC, section = vaultNoRareLootSection, position = 1)
	default String vaultNoRareLootSound1()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "vaultNoRareLootVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = vaultNoRareLootSection, position = 2)
	default int vaultNoRareLootVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "vaultNoRareLootSound2", name = "Sound 2", description = SOUND_DESC, section = vaultNoRareLootSection, position = 3)
	default String vaultNoRareLootSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "vaultNoRareLootVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = vaultNoRareLootSection, position = 4)
	default int vaultNoRareLootVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "vaultNoRareLootSound3", name = "Sound 3", description = SOUND_DESC, section = vaultNoRareLootSection, position = 5)
	default String vaultNoRareLootSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "vaultNoRareLootVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = vaultNoRareLootSection, position = 6)
	default int vaultNoRareLootVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "vaultNoRareLootSound4", name = "Sound 4", description = SOUND_DESC, section = vaultNoRareLootSection, position = 7)
	default String vaultNoRareLootSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "vaultNoRareLootVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = vaultNoRareLootSection, position = 8)
	default int vaultNoRareLootVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "vaultNoRareLootSound5", name = "Sound 5", description = SOUND_DESC, section = vaultNoRareLootSection, position = 9)
	default String vaultNoRareLootSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "vaultNoRareLootVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = vaultNoRareLootSection, position = 10)
	default int vaultNoRareLootVolume5()
	{
		return 50;
	}

	// ===================== Vault: Loot Room - Rare Loot =====================

	@ConfigSection(
		name = "Vault - Rare Loot",
		description = "Sounds played when the ToA vault opens with rare loot.",
		position = 14,
		closedByDefault = true
	)
	String vaultRareLootSection = "vaultRareLootSection";

	@ConfigItem(keyName = "vaultRareLootEnabled", name = "Enabled", description = "Play a sound for this event.", section = vaultRareLootSection, position = 0)
	default boolean vaultRareLootEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "vaultRareLootSound1", name = "Sound 1", description = SOUND_DESC, section = vaultRareLootSection, position = 1)
	default String vaultRareLootSound1()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "vaultRareLootVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = vaultRareLootSection, position = 2)
	default int vaultRareLootVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "vaultRareLootSound2", name = "Sound 2", description = SOUND_DESC, section = vaultRareLootSection, position = 3)
	default String vaultRareLootSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "vaultRareLootVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = vaultRareLootSection, position = 4)
	default int vaultRareLootVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "vaultRareLootSound3", name = "Sound 3", description = SOUND_DESC, section = vaultRareLootSection, position = 5)
	default String vaultRareLootSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "vaultRareLootVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = vaultRareLootSection, position = 6)
	default int vaultRareLootVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "vaultRareLootSound4", name = "Sound 4", description = SOUND_DESC, section = vaultRareLootSection, position = 7)
	default String vaultRareLootSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "vaultRareLootVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = vaultRareLootSection, position = 8)
	default int vaultRareLootVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "vaultRareLootSound5", name = "Sound 5", description = SOUND_DESC, section = vaultRareLootSection, position = 9)
	default String vaultRareLootSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "vaultRareLootVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = vaultRareLootSection, position = 10)
	default int vaultRareLootVolume5()
	{
		return 50;
	}
}
