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
		"Using sound files from URLs submits your IP address to a 3rd-party server not controlled or verified by RuneLite developers";

	String SOUND_DESC =
		"Sound effect ID (numeric), file path, or URL (only used when remote URLs are enabled). "
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

	// ===================== Path of Crondis: Crocodile Spawns =====================

	@ConfigSection(
		name = "Path of Crondis - Crocodile Spawns",
		description = "Sounds played when a crocodile spawns in the Path of Crondis.",
		position = 3,
		closedByDefault = true
	)
	String crocSpawnSection = "crocSpawnSection";

	@ConfigItem(keyName = "crocSpawnEnabled", name = "Enabled", description = "Play a sound for this event.", section = crocSpawnSection, position = 0)
	default boolean crocSpawnEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "crocSpawnSound1", name = "Sound 1", description = SOUND_DESC, section = crocSpawnSection, position = 1)
	default String crocSpawnSound1()
	{
		return "2192";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crocSpawnVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = crocSpawnSection, position = 2)
	default int crocSpawnVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "crocSpawnSound2", name = "Sound 2", description = SOUND_DESC, section = crocSpawnSection, position = 3)
	default String crocSpawnSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crocSpawnVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = crocSpawnSection, position = 4)
	default int crocSpawnVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "crocSpawnSound3", name = "Sound 3", description = SOUND_DESC, section = crocSpawnSection, position = 5)
	default String crocSpawnSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crocSpawnVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = crocSpawnSection, position = 6)
	default int crocSpawnVolume3()
	{
		return 50;
	}

	// ===================== Path of Crondis: Crocodile Defeated =====================

	@ConfigSection(
		name = "Path of Crondis - Crocodile Defeated",
		description = "Sounds played when a crocodile is defeated in the Path of Crondis.",
		position = 4,
		closedByDefault = true
	)
	String crocDeathSection = "crocDeathSection";

	@ConfigItem(keyName = "crocDeathEnabled", name = "Enabled", description = "Play a sound for this event.", section = crocDeathSection, position = 0)
	default boolean crocDeathEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "crocDeathSound1", name = "Sound 1", description = SOUND_DESC, section = crocDeathSection, position = 1)
	default String crocDeathSound1()
	{
		return "2192";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crocDeathVolume1", name = "Volume 1", description = "Playback volume for sound 1.", section = crocDeathSection, position = 2)
	default int crocDeathVolume1()
	{
		return 50;
	}

	@ConfigItem(keyName = "crocDeathSound2", name = "Sound 2", description = SOUND_DESC, section = crocDeathSection, position = 3)
	default String crocDeathSound2()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crocDeathVolume2", name = "Volume 2", description = "Playback volume for sound 2.", section = crocDeathSection, position = 4)
	default int crocDeathVolume2()
	{
		return 50;
	}

	@ConfigItem(keyName = "crocDeathSound3", name = "Sound 3", description = SOUND_DESC, section = crocDeathSection, position = 5)
	default String crocDeathSound3()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "crocDeathVolume3", name = "Volume 3", description = "Playback volume for sound 3.", section = crocDeathSection, position = 6)
	default int crocDeathVolume3()
	{
		return 50;
	}

	// ===================== Path of Crondis: Crocodile Attack =====================

	@ConfigSection(
		name = "Path of Crondis - Crocodile Attack",
		description = "Sounds played when a crocodile attacks.",
		position = 5,
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

	// ===================== Path of Het: Seal Not One-Phased =====================

	@ConfigSection(
		name = "Path of Het - Seal Not One-Phased",
		description = "Sounds played when the Path of Het seal is not killed in one phase.",
		position = 6,
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

	// ===================== Vault: Loot Room - No Rare Loot =====================

	@ConfigSection(
		name = "Vault - No Rare Loot",
		description = "Sounds played when the ToA vault opens without rare loot.",
		position = 8,
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

	// ===================== Vault: Loot Room - Rare Loot =====================

	@ConfigSection(
		name = "Vault - Rare Loot",
		description = "Sounds played when the ToA vault opens with rare loot.",
		position = 9,
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
}
