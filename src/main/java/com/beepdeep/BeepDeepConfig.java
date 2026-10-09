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

	// ===================== Raid - Enter =====================

	@ConfigSection(
		name = "Raid - Enter",
		description = "Sounds played when you enter the Tombs of Amascut.",
		position = 21,
		closedByDefault = true
	)
	String raidEnterSection = "raidEnterSection";

	@ConfigItem(keyName = "raidEnterEnabled", name = "Enabled", description = "Play a sound for this event.", section = raidEnterSection, position = 0)
	default boolean raidEnterEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "raidEnterSound1", name = "Sound 1", description = SOUND_DESC, section = raidEnterSection, position = 1)
	default String raidEnterSound1()
	{
		return "2192";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "raidEnterVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = raidEnterSection, position = 2)
	default int raidEnterVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "raidEnterSound2", name = "Sound 2", description = SOUND_DESC, section = raidEnterSection, position = 3)
	default String raidEnterSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "raidEnterVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = raidEnterSection, position = 4)
	default int raidEnterVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "raidEnterSound3", name = "Sound 3", description = SOUND_DESC, section = raidEnterSection, position = 5)
	default String raidEnterSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "raidEnterVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = raidEnterSection, position = 6)
	default int raidEnterVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "raidEnterSound4", name = "Sound 4", description = SOUND_DESC, section = raidEnterSection, position = 7)
	default String raidEnterSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "raidEnterVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = raidEnterSection, position = 8)
	default int raidEnterVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "raidEnterSound5", name = "Sound 5", description = SOUND_DESC, section = raidEnterSection, position = 9)
	default String raidEnterSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "raidEnterVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = raidEnterSection, position = 10)
	default int raidEnterVolume5()
	{
		return 50;
	}

	// ===================== Raid - Leave =====================

	@ConfigSection(
		name = "Raid - Leave",
		description = "Sounds played when you actually exit the raid, including teleporting out.",
		position = 22,
		closedByDefault = true
	)
	String raidLeaveSection = "raidLeaveSection";

	@ConfigItem(keyName = "raidLeaveEnabled", name = "Enabled", description = "Play a sound for this event.", section = raidLeaveSection, position = 0)
	default boolean raidLeaveEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "raidLeaveSound1", name = "Sound 1", description = SOUND_DESC, section = raidLeaveSection, position = 1)
	default String raidLeaveSound1()
	{
		return "2192";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "raidLeaveVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = raidLeaveSection, position = 2)
	default int raidLeaveVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "raidLeaveSound2", name = "Sound 2", description = SOUND_DESC, section = raidLeaveSection, position = 3)
	default String raidLeaveSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "raidLeaveVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = raidLeaveSection, position = 4)
	default int raidLeaveVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "raidLeaveSound3", name = "Sound 3", description = SOUND_DESC, section = raidLeaveSection, position = 5)
	default String raidLeaveSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "raidLeaveVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = raidLeaveSection, position = 6)
	default int raidLeaveVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "raidLeaveSound4", name = "Sound 4", description = SOUND_DESC, section = raidLeaveSection, position = 7)
	default String raidLeaveSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "raidLeaveVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = raidLeaveSection, position = 8)
	default int raidLeaveVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "raidLeaveSound5", name = "Sound 5", description = SOUND_DESC, section = raidLeaveSection, position = 9)
	default String raidLeaveSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "raidLeaveVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = raidLeaveSection, position = 10)
	default int raidLeaveVolume5()
	{
		return 50;
	}

	// ===================== Raid - Failed or Wiped Challenge Room =====================

	@ConfigSection(
		name = "Raid - Failed or Wiped Challenge Room",
		description = "Sounds played when your party fails a puzzle or boss room challenge.",
		position = 23,
		closedByDefault = true
	)
	String roomFailSection = "roomFailSection";

	@ConfigItem(keyName = "roomFailEnabled", name = "Enabled", description = "Play a sound for this event.", section = roomFailSection, position = 0)
	default boolean roomFailEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "roomFailSound1", name = "Sound 1", description = SOUND_DESC, section = roomFailSection, position = 1)
	default String roomFailSound1()
	{
		return "3892";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "roomFailVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = roomFailSection, position = 2)
	default int roomFailVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "roomFailSound2", name = "Sound 2", description = SOUND_DESC, section = roomFailSection, position = 3)
	default String roomFailSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "roomFailVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = roomFailSection, position = 4)
	default int roomFailVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "roomFailSound3", name = "Sound 3", description = SOUND_DESC, section = roomFailSection, position = 5)
	default String roomFailSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "roomFailVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = roomFailSection, position = 6)
	default int roomFailVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "roomFailSound4", name = "Sound 4", description = SOUND_DESC, section = roomFailSection, position = 7)
	default String roomFailSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "roomFailVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = roomFailSection, position = 8)
	default int roomFailVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "roomFailSound5", name = "Sound 5", description = SOUND_DESC, section = roomFailSection, position = 9)
	default String roomFailSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "roomFailVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = roomFailSection, position = 10)
	default int roomFailVolume5()
	{
		return 50;
	}

	// ===================== Raid - Failed or Abandoned Raid =====================

	@ConfigSection(
		name = "Raid - Failed or Abandoned Raid",
		description = "Sounds played when you fail to survive or abandon the raid.",
		position = 24,
		closedByDefault = true
	)
	String raidFailSection = "raidFailSection";

	@ConfigItem(keyName = "raidFailEnabled", name = "Enabled", description = "Play a sound for this event.", section = raidFailSection, position = 0)
	default boolean raidFailEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "raidFailSound1", name = "Sound 1", description = SOUND_DESC, section = raidFailSection, position = 1)
	default String raidFailSound1()
	{
		return "3892";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "raidFailVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = raidFailSection, position = 2)
	default int raidFailVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "raidFailSound2", name = "Sound 2", description = SOUND_DESC, section = raidFailSection, position = 3)
	default String raidFailSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "raidFailVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = raidFailSection, position = 4)
	default int raidFailVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "raidFailSound3", name = "Sound 3", description = SOUND_DESC, section = raidFailSection, position = 5)
	default String raidFailSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "raidFailVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = raidFailSection, position = 6)
	default int raidFailVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "raidFailSound4", name = "Sound 4", description = SOUND_DESC, section = raidFailSection, position = 7)
	default String raidFailSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "raidFailVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = raidFailSection, position = 8)
	default int raidFailVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "raidFailSound5", name = "Sound 5", description = SOUND_DESC, section = raidFailSection, position = 9)
	default String raidFailSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "raidFailVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = raidFailSection, position = 10)
	default int raidFailVolume5()
	{
		return 50;
	}

	// ===================== General =====================

	@ConfigSection(
		name = "General",
		description = "General Beep Deep options.",
		position = 0
	)
	String generalSection = "generalSection";

	@ConfigSection(
		name = "Test configured sounds",
		description = "Preview one configured sound at its slot and master volume.",
		position = 25
	)
	String soundTestSection = "soundTestSection";

	@ConfigItem(
		keyName = "soundTestEvent",
		name = "Event",
		description = "Select the event whose sound you want to test, including disabled events.",
		section = soundTestSection,
		position = 0
	)
	default ToaEvent soundTestEvent()
	{
		return ToaEvent.RAID_ENTER;
	}

	@ConfigItem(
		keyName = "soundTestSlot",
		name = "Sound slot",
		description = "Select the exact sound slot to play.",
		section = soundTestSection,
		position = 1
	)
	default SoundTestSlot soundTestSlot()
	{
		return SoundTestSlot.SOUND_1;
	}

	@ConfigItem(
		keyName = "playTestSound",
		name = "Play sound",
		description = "Click to play the selected sound using its configured volume and master volume. "
			+ "This checkbox resets to unchecked.",
		section = soundTestSection,
		position = 2
	)
	default boolean playTestSound()
	{
		return false;
	}

	@ConfigItem(
		keyName = "openSoundSharingDialog",
		name = "Open sharing dialog",
		description = "Click to open a dialog with Copy and Import buttons. This checkbox resets to unchecked.",
		section = generalSection,
		position = 0
	)
	default boolean openSoundSharingDialog()
	{
		return false;
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(
		keyName = "masterVolume",
		name = "Master volume",
		description = "Overall volume for all plugin sounds. Each sound's volume is scaled by this percentage. 0 mutes all sounds.",
		section = generalSection,
		position = 2
	)
	default int masterVolume()
	{
		return 50;
	}

	@ConfigItem(
		keyName = "enableRemoteUrls",
		name = "Allow remote URLs",
		description = "Allow sounds to be loaded from http(s) URLs. Downloaded sounds are cached on disk. " + REMOTE_WARNING,
		section = generalSection,
		position = 1
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
		description = "Sounds played when you leave the Crondis puzzle room for Zebak's boss room.",
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

	// ===================== Path of Crondis: Palm Damage =====================

	// Retain the original crocodile attack keys so saved sounds and volumes still apply.
	@ConfigSection(
		name = "Path of Crondis - Palm Damage",
		description = "Sounds played when a crocodile damages the Palm of Resourcefulness in the Crondis puzzle room.",
		position = 3,
		closedByDefault = true
	)
	String crocAttackSection = "crocAttackSection";

	@ConfigItem(keyName = "crocAttackEnabled", name = "Enabled", description = "Play a sound for this event.", section = crocAttackSection, position = 0)
	default boolean crocAttackEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "crocAttackSound1", name = "Sound 1", description = SOUND_DESC, section = crocAttackSection, position = 1)
	default String crocAttackSound1()
	{
		return "2192";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crocAttackVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = crocAttackSection, position = 2)
	default int crocAttackVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "crocAttackSound2", name = "Sound 2", description = SOUND_DESC, section = crocAttackSection, position = 3)
	default String crocAttackSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crocAttackVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = crocAttackSection, position = 4)
	default int crocAttackVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "crocAttackSound3", name = "Sound 3", description = SOUND_DESC, section = crocAttackSection, position = 5)
	default String crocAttackSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crocAttackVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = crocAttackSection, position = 6)
	default int crocAttackVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "crocAttackSound4", name = "Sound 4", description = SOUND_DESC, section = crocAttackSection, position = 7)
	default String crocAttackSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crocAttackVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = crocAttackSection, position = 8)
	default int crocAttackVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "crocAttackSound5", name = "Sound 5", description = SOUND_DESC, section = crocAttackSection, position = 9)
	default String crocAttackSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crocAttackVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = crocAttackSection, position = 10)
	default int crocAttackVolume5()
	{
		return 50;
	}

	// ===================== Path of Crondis: Water Collection Reminder =====================

	@ConfigSection(
		name = "Path of Crondis - Water Collection Reminder",
		description = "Sounds played when you try to take water without a container or from an empty waterfall in the Crondis puzzle room.",
		position = 4,
		closedByDefault = true
	)
	String crondisNoContainerSection = "crondisNoContainerSection";

	@ConfigItem(keyName = "crondisNoContainerEnabled", name = "Enabled", description = "Play a sound when taking water fails because you have no container or the waterfall is empty.", section = crondisNoContainerSection, position = 0)
	default boolean crondisNoContainerEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "crondisNoContainerSound1", name = "Sound 1", description = SOUND_DESC, section = crondisNoContainerSection, position = 1)
	default String crondisNoContainerSound1()
	{
		return "2192";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crondisNoContainerVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = crondisNoContainerSection, position = 2)
	default int crondisNoContainerVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "crondisNoContainerSound2", name = "Sound 2", description = SOUND_DESC, section = crondisNoContainerSection, position = 3)
	default String crondisNoContainerSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crondisNoContainerVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = crondisNoContainerSection, position = 4)
	default int crondisNoContainerVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "crondisNoContainerSound3", name = "Sound 3", description = SOUND_DESC, section = crondisNoContainerSection, position = 5)
	default String crondisNoContainerSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crondisNoContainerVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = crondisNoContainerSection, position = 6)
	default int crondisNoContainerVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "crondisNoContainerSound4", name = "Sound 4", description = SOUND_DESC, section = crondisNoContainerSection, position = 7)
	default String crondisNoContainerSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crondisNoContainerVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = crondisNoContainerSection, position = 8)
	default int crondisNoContainerVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "crondisNoContainerSound5", name = "Sound 5", description = SOUND_DESC, section = crondisNoContainerSection, position = 9)
	default String crondisNoContainerSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crondisNoContainerVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = crondisNoContainerSection, position = 10)
	default int crondisNoContainerVolume5()
	{
		return 50;
	}

	// ===================== Path of Apmeken: Enter =====================

	@ConfigSection(
		name = "Path of Apmeken - Enter",
		description = "Sounds played when you enter the Path of Apmeken room.",
		position = 5,
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
		description = "Sounds played when you leave the Apmeken puzzle room for Ba-Ba's boss room.",
		position = 6,
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
		position = 8,
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
		position = 9,
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
		description = "Sounds played when you leave the Scabaras puzzle room for Kephri's boss room.",
		position = 10,
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

	// ===================== Path of Scabaras: Obelisks Rockfall =====================

	@ConfigSection(
		name = "Path of Scabaras - Obelisks Rockfall",
		description = "Sounds played when you get hit by rockfall during the obelisks puzzle.",
		position = 11,
		closedByDefault = true
	)
	String scabarasRockfallSection = "scabarasRockfallSection";

	@ConfigItem(keyName = "scabarasRockfallEnabled", name = "Enabled", description = "Play a sound for this event.", section = scabarasRockfallSection, position = 0)
	default boolean scabarasRockfallEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "scabarasRockfallSound1", name = "Sound 1", description = SOUND_DESC, section = scabarasRockfallSection, position = 1)
	default String scabarasRockfallSound1()
	{
		return "2192";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasRockfallVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = scabarasRockfallSection, position = 2)
	default int scabarasRockfallVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasRockfallSound2", name = "Sound 2", description = SOUND_DESC, section = scabarasRockfallSection, position = 3)
	default String scabarasRockfallSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasRockfallVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = scabarasRockfallSection, position = 4)
	default int scabarasRockfallVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasRockfallSound3", name = "Sound 3", description = SOUND_DESC, section = scabarasRockfallSection, position = 5)
	default String scabarasRockfallSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasRockfallVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = scabarasRockfallSection, position = 6)
	default int scabarasRockfallVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasRockfallSound4", name = "Sound 4", description = SOUND_DESC, section = scabarasRockfallSection, position = 7)
	default String scabarasRockfallSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasRockfallVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = scabarasRockfallSection, position = 8)
	default int scabarasRockfallVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasRockfallSound5", name = "Sound 5", description = SOUND_DESC, section = scabarasRockfallSection, position = 9)
	default String scabarasRockfallSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasRockfallVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = scabarasRockfallSection, position = 10)
	default int scabarasRockfallVolume5()
	{
		return 50;
	}

	// ===================== Path of Scabaras: Sequence Failure =====================

	@ConfigSection(
		name = "Path of Scabaras - Sequence Failure",
		description = "Sounds played when you fail the sequence pressure plate puzzle (Wiki puzzle #1).",
		position = 12,
		closedByDefault = true
	)
	String scabarasSequenceFailSection = "scabarasSequenceFailSection";

	@ConfigItem(keyName = "scabarasSequenceFailEnabled", name = "Enabled", description = "Play a sound for this event.", section = scabarasSequenceFailSection, position = 0)
	default boolean scabarasSequenceFailEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "scabarasSequenceFailSound1", name = "Sound 1", description = SOUND_DESC, section = scabarasSequenceFailSection, position = 1)
	default String scabarasSequenceFailSound1()
	{
		return "3892";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasSequenceFailVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = scabarasSequenceFailSection, position = 2)
	default int scabarasSequenceFailVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasSequenceFailSound2", name = "Sound 2", description = SOUND_DESC, section = scabarasSequenceFailSection, position = 3)
	default String scabarasSequenceFailSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasSequenceFailVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = scabarasSequenceFailSection, position = 4)
	default int scabarasSequenceFailVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasSequenceFailSound3", name = "Sound 3", description = SOUND_DESC, section = scabarasSequenceFailSection, position = 5)
	default String scabarasSequenceFailSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasSequenceFailVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = scabarasSequenceFailSection, position = 6)
	default int scabarasSequenceFailVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasSequenceFailSound4", name = "Sound 4", description = SOUND_DESC, section = scabarasSequenceFailSection, position = 7)
	default String scabarasSequenceFailSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasSequenceFailVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = scabarasSequenceFailSection, position = 8)
	default int scabarasSequenceFailVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasSequenceFailSound5", name = "Sound 5", description = SOUND_DESC, section = scabarasSequenceFailSection, position = 9)
	default String scabarasSequenceFailSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasSequenceFailVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = scabarasSequenceFailSection, position = 10)
	default int scabarasSequenceFailVolume5()
	{
		return 50;
	}

	// ===================== Path of Scabaras: Number Puzzle Failure =====================

	@ConfigSection(
		name = "Path of Scabaras - Number Puzzle Failure",
		description = "Sounds played when an incorrect number puzzle solution deals damage (Wiki puzzle #4).",
		position = 13,
		closedByDefault = true
	)
	String scabarasNumberFailSection = "scabarasNumberFailSection";

	@ConfigItem(keyName = "scabarasNumberFailEnabled", name = "Enabled", description = "Play a sound for this event.", section = scabarasNumberFailSection, position = 0)
	default boolean scabarasNumberFailEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "scabarasNumberFailSound1", name = "Sound 1", description = SOUND_DESC, section = scabarasNumberFailSection, position = 1)
	default String scabarasNumberFailSound1()
	{
		return "3892";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasNumberFailVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = scabarasNumberFailSection, position = 2)
	default int scabarasNumberFailVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasNumberFailSound2", name = "Sound 2", description = SOUND_DESC, section = scabarasNumberFailSection, position = 3)
	default String scabarasNumberFailSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasNumberFailVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = scabarasNumberFailSection, position = 4)
	default int scabarasNumberFailVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasNumberFailSound3", name = "Sound 3", description = SOUND_DESC, section = scabarasNumberFailSection, position = 5)
	default String scabarasNumberFailSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasNumberFailVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = scabarasNumberFailSection, position = 6)
	default int scabarasNumberFailVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasNumberFailSound4", name = "Sound 4", description = SOUND_DESC, section = scabarasNumberFailSection, position = 7)
	default String scabarasNumberFailSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasNumberFailVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = scabarasNumberFailSection, position = 8)
	default int scabarasNumberFailVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "scabarasNumberFailSound5", name = "Sound 5", description = SOUND_DESC, section = scabarasNumberFailSection, position = 9)
	default String scabarasNumberFailSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "scabarasNumberFailVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = scabarasNumberFailSection, position = 10)
	default int scabarasNumberFailVolume5()
	{
		return 50;
	}

	// ===================== Path of Het: Enter =====================

	@ConfigSection(
		name = "Path of Het - Enter",
		description = "Sounds played when you enter the Path of Het room.",
		position = 14,
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
		description = "Sounds played when you leave the Het puzzle room for Akkha's boss room.",
		position = 15,
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

	// ===================== Path of Het: Orb/Light Damage =====================

	@ConfigSection(
		name = "Path of Het - Orb/Light Damage",
		description = "Sounds played when any player takes damage from light or dark orbs in the Het puzzle room.",
		position = 16,
		closedByDefault = true
	)
	String hetOrbDamageSection = "hetOrbDamageSection";

	@ConfigItem(keyName = "hetOrbDamageEnabled", name = "Enabled", description = "Play a sound for this event.", section = hetOrbDamageSection, position = 0)
	default boolean hetOrbDamageEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "hetOrbDamageSound1", name = "Sound 1", description = SOUND_DESC, section = hetOrbDamageSection, position = 1)
	default String hetOrbDamageSound1()
	{
		return "2192";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetOrbDamageVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = hetOrbDamageSection, position = 2)
	default int hetOrbDamageVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetOrbDamageSound2", name = "Sound 2", description = SOUND_DESC, section = hetOrbDamageSection, position = 3)
	default String hetOrbDamageSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetOrbDamageVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = hetOrbDamageSection, position = 4)
	default int hetOrbDamageVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetOrbDamageSound3", name = "Sound 3", description = SOUND_DESC, section = hetOrbDamageSection, position = 5)
	default String hetOrbDamageSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetOrbDamageVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = hetOrbDamageSection, position = 6)
	default int hetOrbDamageVolume3()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetOrbDamageSound4", name = "Sound 4", description = SOUND_DESC, section = hetOrbDamageSection, position = 7)
	default String hetOrbDamageSound4()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetOrbDamageVolume4", name = "Volume 4", description = "Playback volume for sound 4.", section = hetOrbDamageSection, position = 8)
	default int hetOrbDamageVolume4()
	{
		return 50;
	}

	@ConfigItem(keyName = "hetOrbDamageSound5", name = "Sound 5", description = SOUND_DESC, section = hetOrbDamageSection, position = 9)
	default String hetOrbDamageSound5()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "hetOrbDamageVolume5", name = "Volume 5", description = "Playback volume for sound 5.", section = hetOrbDamageSection, position = 10)
	default int hetOrbDamageVolume5()
	{
		return 50;
	}

	// ===================== Path of Het: Unstable Orb Hit =====================

	@ConfigSection(
		name = "Path of Het - Unstable Orb Hit",
		description = "Sounds played when any player is hit by an Unstable Orb in Akkha's arena.",
		position = 18,
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
		position = 17,
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
		position = 7,
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
		position = 19,
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
		position = 20,
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

	// Keep the original key so previously pasted drafts remain available in the dialog.
	@ConfigItem(
		keyName = "soundConfigurationCode",
		name = "Configuration code draft",
		description = "The last configuration code entered in the sharing dialog.",
		hidden = true
	)
	default String soundConfigurationCode()
	{
		return "";
	}
}
