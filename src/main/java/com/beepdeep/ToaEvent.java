package com.beepdeep;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The Tombs of Amascut events that can trigger a sound, along with the config
 * key prefix used to look up that event's slots and volumes.
 *
 * <p>The key prefixes here MUST stay in sync with the {@code keyName}s declared
 * in {@link BeepDeepConfig}; {@link SoundManager} reads the slots generically
 * via {@link net.runelite.client.config.ConfigManager} using these helpers.
 */
@Getter
@RequiredArgsConstructor
enum ToaEvent
{
	CRONDIS_ENTER("crondisEnter", "Enter Path of Crondis"),
	CRONDIS_LEAVE("crondisLeave", "Leave Path of Crondis"),
	CROC_SPAWN("crocSpawn", "Crocodile spawns"),
	CROC_DEATH("crocDeath", "Crocodile defeated"),
	CROC_ATTACK("crocAttack", "Crocodile attack"),
	HET_ONE_PHASE_FAIL("hetOnePhaseFail", "Het seal not one-phased"),
	VAULT_NO_RARE_LOOT("vaultNoRareLoot", "Vault opened with no rare loot"),
	VAULT_RARE_LOOT("vaultRareLoot", "Vault opened with rare loot"),
	APMEKEN_FAIL("apmekenFail", "Apmeken issue not fixed");

	/** Number of configurable sound slots per event. */
	static final int SLOT_COUNT = 3;

	private final String prefix;
	private final String displayName;

	String enabledKey()
	{
		return prefix + "Enabled";
	}

	String soundKey(int slot)
	{
		return prefix + "Sound" + slot;
	}

	String volumeKey(int slot)
	{
		return prefix + "Volume" + slot;
	}
}
