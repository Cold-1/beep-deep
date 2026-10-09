package com.beepdeep;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/**
 * Typed bindings between raid events and their configuration.
 * Method references keep the config defaults and slot mapping checked by the compiler.
 */
enum ToaEvent
{
	RAID_ENTER("raidEnter", BeepDeepConfig::raidEnterEnabled,
		slot(BeepDeepConfig::raidEnterSound1, BeepDeepConfig::raidEnterVolume1),
		slot(BeepDeepConfig::raidEnterSound2, BeepDeepConfig::raidEnterVolume2),
		slot(BeepDeepConfig::raidEnterSound3, BeepDeepConfig::raidEnterVolume3),
		slot(BeepDeepConfig::raidEnterSound4, BeepDeepConfig::raidEnterVolume4),
		slot(BeepDeepConfig::raidEnterSound5, BeepDeepConfig::raidEnterVolume5)),
	RAID_LEAVE("raidLeave", BeepDeepConfig::raidLeaveEnabled,
		slot(BeepDeepConfig::raidLeaveSound1, BeepDeepConfig::raidLeaveVolume1),
		slot(BeepDeepConfig::raidLeaveSound2, BeepDeepConfig::raidLeaveVolume2),
		slot(BeepDeepConfig::raidLeaveSound3, BeepDeepConfig::raidLeaveVolume3),
		slot(BeepDeepConfig::raidLeaveSound4, BeepDeepConfig::raidLeaveVolume4),
		slot(BeepDeepConfig::raidLeaveSound5, BeepDeepConfig::raidLeaveVolume5)),
	ROOM_FAIL("roomFail", BeepDeepConfig::roomFailEnabled,
		slot(BeepDeepConfig::roomFailSound1, BeepDeepConfig::roomFailVolume1),
		slot(BeepDeepConfig::roomFailSound2, BeepDeepConfig::roomFailVolume2),
		slot(BeepDeepConfig::roomFailSound3, BeepDeepConfig::roomFailVolume3),
		slot(BeepDeepConfig::roomFailSound4, BeepDeepConfig::roomFailVolume4),
		slot(BeepDeepConfig::roomFailSound5, BeepDeepConfig::roomFailVolume5)),
	RAID_FAIL("raidFail", BeepDeepConfig::raidFailEnabled,
		slot(BeepDeepConfig::raidFailSound1, BeepDeepConfig::raidFailVolume1),
		slot(BeepDeepConfig::raidFailSound2, BeepDeepConfig::raidFailVolume2),
		slot(BeepDeepConfig::raidFailSound3, BeepDeepConfig::raidFailVolume3),
		slot(BeepDeepConfig::raidFailSound4, BeepDeepConfig::raidFailVolume4),
		slot(BeepDeepConfig::raidFailSound5, BeepDeepConfig::raidFailVolume5)),
	CRONDIS_ENTER("crondisEnter", BeepDeepConfig::crondisEnterEnabled,
		slot(BeepDeepConfig::crondisEnterSound1, BeepDeepConfig::crondisEnterVolume1),
		slot(BeepDeepConfig::crondisEnterSound2, BeepDeepConfig::crondisEnterVolume2),
		slot(BeepDeepConfig::crondisEnterSound3, BeepDeepConfig::crondisEnterVolume3),
		slot(BeepDeepConfig::crondisEnterSound4, BeepDeepConfig::crondisEnterVolume4),
		slot(BeepDeepConfig::crondisEnterSound5, BeepDeepConfig::crondisEnterVolume5)),
	CRONDIS_LEAVE("crondisLeave", BeepDeepConfig::crondisLeaveEnabled,
		slot(BeepDeepConfig::crondisLeaveSound1, BeepDeepConfig::crondisLeaveVolume1),
		slot(BeepDeepConfig::crondisLeaveSound2, BeepDeepConfig::crondisLeaveVolume2),
		slot(BeepDeepConfig::crondisLeaveSound3, BeepDeepConfig::crondisLeaveVolume3),
		slot(BeepDeepConfig::crondisLeaveSound4, BeepDeepConfig::crondisLeaveVolume4),
		slot(BeepDeepConfig::crondisLeaveSound5, BeepDeepConfig::crondisLeaveVolume5)),
	CRONDIS_PALM_DAMAGE("crocAttack", BeepDeepConfig::crocAttackEnabled,
		slot(BeepDeepConfig::crocAttackSound1, BeepDeepConfig::crocAttackVolume1),
		slot(BeepDeepConfig::crocAttackSound2, BeepDeepConfig::crocAttackVolume2),
		slot(BeepDeepConfig::crocAttackSound3, BeepDeepConfig::crocAttackVolume3),
		slot(BeepDeepConfig::crocAttackSound4, BeepDeepConfig::crocAttackVolume4),
		slot(BeepDeepConfig::crocAttackSound5, BeepDeepConfig::crocAttackVolume5)),
	CRONDIS_NO_CONTAINER("crondisNoContainer", BeepDeepConfig::crondisNoContainerEnabled,
		slot(BeepDeepConfig::crondisNoContainerSound1, BeepDeepConfig::crondisNoContainerVolume1),
		slot(BeepDeepConfig::crondisNoContainerSound2, BeepDeepConfig::crondisNoContainerVolume2),
		slot(BeepDeepConfig::crondisNoContainerSound3, BeepDeepConfig::crondisNoContainerVolume3),
		slot(BeepDeepConfig::crondisNoContainerSound4, BeepDeepConfig::crondisNoContainerVolume4),
		slot(BeepDeepConfig::crondisNoContainerSound5, BeepDeepConfig::crondisNoContainerVolume5)),
	APMEKEN_ENTER("apmekenEnter", BeepDeepConfig::apmekenEnterEnabled,
		slot(BeepDeepConfig::apmekenEnterSound1, BeepDeepConfig::apmekenEnterVolume1),
		slot(BeepDeepConfig::apmekenEnterSound2, BeepDeepConfig::apmekenEnterVolume2),
		slot(BeepDeepConfig::apmekenEnterSound3, BeepDeepConfig::apmekenEnterVolume3),
		slot(BeepDeepConfig::apmekenEnterSound4, BeepDeepConfig::apmekenEnterVolume4),
		slot(BeepDeepConfig::apmekenEnterSound5, BeepDeepConfig::apmekenEnterVolume5)),
	APMEKEN_LEAVE("apmekenLeave", BeepDeepConfig::apmekenLeaveEnabled,
		slot(BeepDeepConfig::apmekenLeaveSound1, BeepDeepConfig::apmekenLeaveVolume1),
		slot(BeepDeepConfig::apmekenLeaveSound2, BeepDeepConfig::apmekenLeaveVolume2),
		slot(BeepDeepConfig::apmekenLeaveSound3, BeepDeepConfig::apmekenLeaveVolume3),
		slot(BeepDeepConfig::apmekenLeaveSound4, BeepDeepConfig::apmekenLeaveVolume4),
		slot(BeepDeepConfig::apmekenLeaveSound5, BeepDeepConfig::apmekenLeaveVolume5)),
	SCABARAS_ENTER("scabarasEnter", BeepDeepConfig::scabarasEnterEnabled,
		slot(BeepDeepConfig::scabarasEnterSound1, BeepDeepConfig::scabarasEnterVolume1),
		slot(BeepDeepConfig::scabarasEnterSound2, BeepDeepConfig::scabarasEnterVolume2),
		slot(BeepDeepConfig::scabarasEnterSound3, BeepDeepConfig::scabarasEnterVolume3),
		slot(BeepDeepConfig::scabarasEnterSound4, BeepDeepConfig::scabarasEnterVolume4),
		slot(BeepDeepConfig::scabarasEnterSound5, BeepDeepConfig::scabarasEnterVolume5)),
	SCABARAS_LEAVE("scabarasLeave", BeepDeepConfig::scabarasLeaveEnabled,
		slot(BeepDeepConfig::scabarasLeaveSound1, BeepDeepConfig::scabarasLeaveVolume1),
		slot(BeepDeepConfig::scabarasLeaveSound2, BeepDeepConfig::scabarasLeaveVolume2),
		slot(BeepDeepConfig::scabarasLeaveSound3, BeepDeepConfig::scabarasLeaveVolume3),
		slot(BeepDeepConfig::scabarasLeaveSound4, BeepDeepConfig::scabarasLeaveVolume4),
		slot(BeepDeepConfig::scabarasLeaveSound5, BeepDeepConfig::scabarasLeaveVolume5)),
	SCABARAS_ROCKFALL("scabarasRockfall", BeepDeepConfig::scabarasRockfallEnabled,
		slot(BeepDeepConfig::scabarasRockfallSound1, BeepDeepConfig::scabarasRockfallVolume1),
		slot(BeepDeepConfig::scabarasRockfallSound2, BeepDeepConfig::scabarasRockfallVolume2),
		slot(BeepDeepConfig::scabarasRockfallSound3, BeepDeepConfig::scabarasRockfallVolume3),
		slot(BeepDeepConfig::scabarasRockfallSound4, BeepDeepConfig::scabarasRockfallVolume4),
		slot(BeepDeepConfig::scabarasRockfallSound5, BeepDeepConfig::scabarasRockfallVolume5)),
	SCABARAS_SEQUENCE_FAIL("scabarasSequenceFail", BeepDeepConfig::scabarasSequenceFailEnabled,
		slot(BeepDeepConfig::scabarasSequenceFailSound1, BeepDeepConfig::scabarasSequenceFailVolume1),
		slot(BeepDeepConfig::scabarasSequenceFailSound2, BeepDeepConfig::scabarasSequenceFailVolume2),
		slot(BeepDeepConfig::scabarasSequenceFailSound3, BeepDeepConfig::scabarasSequenceFailVolume3),
		slot(BeepDeepConfig::scabarasSequenceFailSound4, BeepDeepConfig::scabarasSequenceFailVolume4),
		slot(BeepDeepConfig::scabarasSequenceFailSound5, BeepDeepConfig::scabarasSequenceFailVolume5)),
	SCABARAS_NUMBER_FAIL("scabarasNumberFail", BeepDeepConfig::scabarasNumberFailEnabled,
		slot(BeepDeepConfig::scabarasNumberFailSound1, BeepDeepConfig::scabarasNumberFailVolume1),
		slot(BeepDeepConfig::scabarasNumberFailSound2, BeepDeepConfig::scabarasNumberFailVolume2),
		slot(BeepDeepConfig::scabarasNumberFailSound3, BeepDeepConfig::scabarasNumberFailVolume3),
		slot(BeepDeepConfig::scabarasNumberFailSound4, BeepDeepConfig::scabarasNumberFailVolume4),
		slot(BeepDeepConfig::scabarasNumberFailSound5, BeepDeepConfig::scabarasNumberFailVolume5)),
	HET_ENTER("hetEnter", BeepDeepConfig::hetEnterEnabled,
		slot(BeepDeepConfig::hetEnterSound1, BeepDeepConfig::hetEnterVolume1),
		slot(BeepDeepConfig::hetEnterSound2, BeepDeepConfig::hetEnterVolume2),
		slot(BeepDeepConfig::hetEnterSound3, BeepDeepConfig::hetEnterVolume3),
		slot(BeepDeepConfig::hetEnterSound4, BeepDeepConfig::hetEnterVolume4),
		slot(BeepDeepConfig::hetEnterSound5, BeepDeepConfig::hetEnterVolume5)),
	HET_LEAVE("hetLeave", BeepDeepConfig::hetLeaveEnabled,
		slot(BeepDeepConfig::hetLeaveSound1, BeepDeepConfig::hetLeaveVolume1),
		slot(BeepDeepConfig::hetLeaveSound2, BeepDeepConfig::hetLeaveVolume2),
		slot(BeepDeepConfig::hetLeaveSound3, BeepDeepConfig::hetLeaveVolume3),
		slot(BeepDeepConfig::hetLeaveSound4, BeepDeepConfig::hetLeaveVolume4),
		slot(BeepDeepConfig::hetLeaveSound5, BeepDeepConfig::hetLeaveVolume5)),
	HET_ORB_DAMAGE("hetOrbDamage", BeepDeepConfig::hetOrbDamageEnabled,
		slot(BeepDeepConfig::hetOrbDamageSound1, BeepDeepConfig::hetOrbDamageVolume1),
		slot(BeepDeepConfig::hetOrbDamageSound2, BeepDeepConfig::hetOrbDamageVolume2),
		slot(BeepDeepConfig::hetOrbDamageSound3, BeepDeepConfig::hetOrbDamageVolume3),
		slot(BeepDeepConfig::hetOrbDamageSound4, BeepDeepConfig::hetOrbDamageVolume4),
		slot(BeepDeepConfig::hetOrbDamageSound5, BeepDeepConfig::hetOrbDamageVolume5)),
	HET_UNSTABLE_ORB_HIT("hetUnstableOrbHit", BeepDeepConfig::hetUnstableOrbHitEnabled,
		slot(BeepDeepConfig::hetUnstableOrbHitSound1, BeepDeepConfig::hetUnstableOrbHitVolume1),
		slot(BeepDeepConfig::hetUnstableOrbHitSound2, BeepDeepConfig::hetUnstableOrbHitVolume2),
		slot(BeepDeepConfig::hetUnstableOrbHitSound3, BeepDeepConfig::hetUnstableOrbHitVolume3),
		slot(BeepDeepConfig::hetUnstableOrbHitSound4, BeepDeepConfig::hetUnstableOrbHitVolume4),
		slot(BeepDeepConfig::hetUnstableOrbHitSound5, BeepDeepConfig::hetUnstableOrbHitVolume5)),
	HET_ONE_PHASE_FAIL("hetOnePhaseFail", BeepDeepConfig::hetOnePhaseFailEnabled,
		slot(BeepDeepConfig::hetOnePhaseFailSound1, BeepDeepConfig::hetOnePhaseFailVolume1),
		slot(BeepDeepConfig::hetOnePhaseFailSound2, BeepDeepConfig::hetOnePhaseFailVolume2),
		slot(BeepDeepConfig::hetOnePhaseFailSound3, BeepDeepConfig::hetOnePhaseFailVolume3),
		slot(BeepDeepConfig::hetOnePhaseFailSound4, BeepDeepConfig::hetOnePhaseFailVolume4),
		slot(BeepDeepConfig::hetOnePhaseFailSound5, BeepDeepConfig::hetOnePhaseFailVolume5)),
	VAULT_NO_RARE_LOOT("vaultNoRareLoot", BeepDeepConfig::vaultNoRareLootEnabled,
		slot(BeepDeepConfig::vaultNoRareLootSound1, BeepDeepConfig::vaultNoRareLootVolume1),
		slot(BeepDeepConfig::vaultNoRareLootSound2, BeepDeepConfig::vaultNoRareLootVolume2),
		slot(BeepDeepConfig::vaultNoRareLootSound3, BeepDeepConfig::vaultNoRareLootVolume3),
		slot(BeepDeepConfig::vaultNoRareLootSound4, BeepDeepConfig::vaultNoRareLootVolume4),
		slot(BeepDeepConfig::vaultNoRareLootSound5, BeepDeepConfig::vaultNoRareLootVolume5)),
	VAULT_RARE_LOOT("vaultRareLoot", BeepDeepConfig::vaultRareLootEnabled,
		slot(BeepDeepConfig::vaultRareLootSound1, BeepDeepConfig::vaultRareLootVolume1),
		slot(BeepDeepConfig::vaultRareLootSound2, BeepDeepConfig::vaultRareLootVolume2),
		slot(BeepDeepConfig::vaultRareLootSound3, BeepDeepConfig::vaultRareLootVolume3),
		slot(BeepDeepConfig::vaultRareLootSound4, BeepDeepConfig::vaultRareLootVolume4),
		slot(BeepDeepConfig::vaultRareLootSound5, BeepDeepConfig::vaultRareLootVolume5)),
	BABA_BANANA_SLIP("babaBananaSlip", BeepDeepConfig::babaBananaSlipEnabled,
		slot(BeepDeepConfig::babaBananaSlipSound1, BeepDeepConfig::babaBananaSlipVolume1),
		slot(BeepDeepConfig::babaBananaSlipSound2, BeepDeepConfig::babaBananaSlipVolume2),
		slot(BeepDeepConfig::babaBananaSlipSound3, BeepDeepConfig::babaBananaSlipVolume3),
		slot(BeepDeepConfig::babaBananaSlipSound4, BeepDeepConfig::babaBananaSlipVolume4),
		slot(BeepDeepConfig::babaBananaSlipSound5, BeepDeepConfig::babaBananaSlipVolume5)),
	APMEKEN_FAIL("apmekenFail", BeepDeepConfig::apmekenFailEnabled,
		slot(BeepDeepConfig::apmekenFailSound1, BeepDeepConfig::apmekenFailVolume1),
		slot(BeepDeepConfig::apmekenFailSound2, BeepDeepConfig::apmekenFailVolume2),
		slot(BeepDeepConfig::apmekenFailSound3, BeepDeepConfig::apmekenFailVolume3),
		slot(BeepDeepConfig::apmekenFailSound4, BeepDeepConfig::apmekenFailVolume4),
		slot(BeepDeepConfig::apmekenFailSound5, BeepDeepConfig::apmekenFailVolume5));

	private final String configPrefix;
	private final Predicate<BeepDeepConfig> enabled;
	private final List<SoundSlot> slots;

	ToaEvent(String configPrefix, Predicate<BeepDeepConfig> enabled, SoundSlot... slots)
	{
		this.configPrefix = configPrefix;
		this.enabled = enabled;
		this.slots = Collections.unmodifiableList(Arrays.asList(slots));
	}

	String getConfigPrefix()
	{
		return configPrefix;
	}

	boolean isEnabled(BeepDeepConfig config)
	{
		return enabled.test(config);
	}

	List<SoundSlot> getSlots()
	{
		return slots;
	}

	private static SoundSlot slot(Function<BeepDeepConfig, String> source, ToIntFunction<BeepDeepConfig> volume)
	{
		return new SoundSlot(source, volume);
	}

	static final class SoundSlot
	{
		private final Function<BeepDeepConfig, String> source;
		private final ToIntFunction<BeepDeepConfig> volume;

		private SoundSlot(Function<BeepDeepConfig, String> source, ToIntFunction<BeepDeepConfig> volume)
		{
			this.source = source;
			this.volume = volume;
		}

		String source(BeepDeepConfig config)
		{
			return source.apply(config);
		}

		int volume(BeepDeepConfig config)
		{
			return Math.max(0, Math.min(100, volume.applyAsInt(config)));
		}
	}
}
