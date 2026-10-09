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
	CRONDIS_ENTER(BeepDeepConfig::crondisEnterEnabled,
		slot(BeepDeepConfig::crondisEnterSound1, BeepDeepConfig::crondisEnterVolume1),
		slot(BeepDeepConfig::crondisEnterSound2, BeepDeepConfig::crondisEnterVolume2),
		slot(BeepDeepConfig::crondisEnterSound3, BeepDeepConfig::crondisEnterVolume3),
		slot(BeepDeepConfig::crondisEnterSound4, BeepDeepConfig::crondisEnterVolume4),
		slot(BeepDeepConfig::crondisEnterSound5, BeepDeepConfig::crondisEnterVolume5)),
	CRONDIS_LEAVE(BeepDeepConfig::crondisLeaveEnabled,
		slot(BeepDeepConfig::crondisLeaveSound1, BeepDeepConfig::crondisLeaveVolume1),
		slot(BeepDeepConfig::crondisLeaveSound2, BeepDeepConfig::crondisLeaveVolume2),
		slot(BeepDeepConfig::crondisLeaveSound3, BeepDeepConfig::crondisLeaveVolume3),
		slot(BeepDeepConfig::crondisLeaveSound4, BeepDeepConfig::crondisLeaveVolume4),
		slot(BeepDeepConfig::crondisLeaveSound5, BeepDeepConfig::crondisLeaveVolume5)),
	APMEKEN_ENTER(BeepDeepConfig::apmekenEnterEnabled,
		slot(BeepDeepConfig::apmekenEnterSound1, BeepDeepConfig::apmekenEnterVolume1),
		slot(BeepDeepConfig::apmekenEnterSound2, BeepDeepConfig::apmekenEnterVolume2),
		slot(BeepDeepConfig::apmekenEnterSound3, BeepDeepConfig::apmekenEnterVolume3),
		slot(BeepDeepConfig::apmekenEnterSound4, BeepDeepConfig::apmekenEnterVolume4),
		slot(BeepDeepConfig::apmekenEnterSound5, BeepDeepConfig::apmekenEnterVolume5)),
	APMEKEN_LEAVE(BeepDeepConfig::apmekenLeaveEnabled,
		slot(BeepDeepConfig::apmekenLeaveSound1, BeepDeepConfig::apmekenLeaveVolume1),
		slot(BeepDeepConfig::apmekenLeaveSound2, BeepDeepConfig::apmekenLeaveVolume2),
		slot(BeepDeepConfig::apmekenLeaveSound3, BeepDeepConfig::apmekenLeaveVolume3),
		slot(BeepDeepConfig::apmekenLeaveSound4, BeepDeepConfig::apmekenLeaveVolume4),
		slot(BeepDeepConfig::apmekenLeaveSound5, BeepDeepConfig::apmekenLeaveVolume5)),
	SCABARAS_ENTER(BeepDeepConfig::scabarasEnterEnabled,
		slot(BeepDeepConfig::scabarasEnterSound1, BeepDeepConfig::scabarasEnterVolume1),
		slot(BeepDeepConfig::scabarasEnterSound2, BeepDeepConfig::scabarasEnterVolume2),
		slot(BeepDeepConfig::scabarasEnterSound3, BeepDeepConfig::scabarasEnterVolume3),
		slot(BeepDeepConfig::scabarasEnterSound4, BeepDeepConfig::scabarasEnterVolume4),
		slot(BeepDeepConfig::scabarasEnterSound5, BeepDeepConfig::scabarasEnterVolume5)),
	SCABARAS_LEAVE(BeepDeepConfig::scabarasLeaveEnabled,
		slot(BeepDeepConfig::scabarasLeaveSound1, BeepDeepConfig::scabarasLeaveVolume1),
		slot(BeepDeepConfig::scabarasLeaveSound2, BeepDeepConfig::scabarasLeaveVolume2),
		slot(BeepDeepConfig::scabarasLeaveSound3, BeepDeepConfig::scabarasLeaveVolume3),
		slot(BeepDeepConfig::scabarasLeaveSound4, BeepDeepConfig::scabarasLeaveVolume4),
		slot(BeepDeepConfig::scabarasLeaveSound5, BeepDeepConfig::scabarasLeaveVolume5)),
	SCABARAS_ROCKFALL(BeepDeepConfig::scabarasRockfallEnabled,
		slot(BeepDeepConfig::scabarasRockfallSound1, BeepDeepConfig::scabarasRockfallVolume1),
		slot(BeepDeepConfig::scabarasRockfallSound2, BeepDeepConfig::scabarasRockfallVolume2),
		slot(BeepDeepConfig::scabarasRockfallSound3, BeepDeepConfig::scabarasRockfallVolume3),
		slot(BeepDeepConfig::scabarasRockfallSound4, BeepDeepConfig::scabarasRockfallVolume4),
		slot(BeepDeepConfig::scabarasRockfallSound5, BeepDeepConfig::scabarasRockfallVolume5)),
	HET_ENTER(BeepDeepConfig::hetEnterEnabled,
		slot(BeepDeepConfig::hetEnterSound1, BeepDeepConfig::hetEnterVolume1),
		slot(BeepDeepConfig::hetEnterSound2, BeepDeepConfig::hetEnterVolume2),
		slot(BeepDeepConfig::hetEnterSound3, BeepDeepConfig::hetEnterVolume3),
		slot(BeepDeepConfig::hetEnterSound4, BeepDeepConfig::hetEnterVolume4),
		slot(BeepDeepConfig::hetEnterSound5, BeepDeepConfig::hetEnterVolume5)),
	HET_LEAVE(BeepDeepConfig::hetLeaveEnabled,
		slot(BeepDeepConfig::hetLeaveSound1, BeepDeepConfig::hetLeaveVolume1),
		slot(BeepDeepConfig::hetLeaveSound2, BeepDeepConfig::hetLeaveVolume2),
		slot(BeepDeepConfig::hetLeaveSound3, BeepDeepConfig::hetLeaveVolume3),
		slot(BeepDeepConfig::hetLeaveSound4, BeepDeepConfig::hetLeaveVolume4),
		slot(BeepDeepConfig::hetLeaveSound5, BeepDeepConfig::hetLeaveVolume5)),
	HET_UNSTABLE_ORB_HIT(BeepDeepConfig::hetUnstableOrbHitEnabled,
		slot(BeepDeepConfig::hetUnstableOrbHitSound1, BeepDeepConfig::hetUnstableOrbHitVolume1),
		slot(BeepDeepConfig::hetUnstableOrbHitSound2, BeepDeepConfig::hetUnstableOrbHitVolume2),
		slot(BeepDeepConfig::hetUnstableOrbHitSound3, BeepDeepConfig::hetUnstableOrbHitVolume3),
		slot(BeepDeepConfig::hetUnstableOrbHitSound4, BeepDeepConfig::hetUnstableOrbHitVolume4),
		slot(BeepDeepConfig::hetUnstableOrbHitSound5, BeepDeepConfig::hetUnstableOrbHitVolume5)),
	HET_ONE_PHASE_FAIL(BeepDeepConfig::hetOnePhaseFailEnabled,
		slot(BeepDeepConfig::hetOnePhaseFailSound1, BeepDeepConfig::hetOnePhaseFailVolume1),
		slot(BeepDeepConfig::hetOnePhaseFailSound2, BeepDeepConfig::hetOnePhaseFailVolume2),
		slot(BeepDeepConfig::hetOnePhaseFailSound3, BeepDeepConfig::hetOnePhaseFailVolume3),
		slot(BeepDeepConfig::hetOnePhaseFailSound4, BeepDeepConfig::hetOnePhaseFailVolume4),
		slot(BeepDeepConfig::hetOnePhaseFailSound5, BeepDeepConfig::hetOnePhaseFailVolume5)),
	VAULT_NO_RARE_LOOT(BeepDeepConfig::vaultNoRareLootEnabled,
		slot(BeepDeepConfig::vaultNoRareLootSound1, BeepDeepConfig::vaultNoRareLootVolume1),
		slot(BeepDeepConfig::vaultNoRareLootSound2, BeepDeepConfig::vaultNoRareLootVolume2),
		slot(BeepDeepConfig::vaultNoRareLootSound3, BeepDeepConfig::vaultNoRareLootVolume3),
		slot(BeepDeepConfig::vaultNoRareLootSound4, BeepDeepConfig::vaultNoRareLootVolume4),
		slot(BeepDeepConfig::vaultNoRareLootSound5, BeepDeepConfig::vaultNoRareLootVolume5)),
	VAULT_RARE_LOOT(BeepDeepConfig::vaultRareLootEnabled,
		slot(BeepDeepConfig::vaultRareLootSound1, BeepDeepConfig::vaultRareLootVolume1),
		slot(BeepDeepConfig::vaultRareLootSound2, BeepDeepConfig::vaultRareLootVolume2),
		slot(BeepDeepConfig::vaultRareLootSound3, BeepDeepConfig::vaultRareLootVolume3),
		slot(BeepDeepConfig::vaultRareLootSound4, BeepDeepConfig::vaultRareLootVolume4),
		slot(BeepDeepConfig::vaultRareLootSound5, BeepDeepConfig::vaultRareLootVolume5)),
	BABA_BANANA_SLIP(BeepDeepConfig::babaBananaSlipEnabled,
		slot(BeepDeepConfig::babaBananaSlipSound1, BeepDeepConfig::babaBananaSlipVolume1),
		slot(BeepDeepConfig::babaBananaSlipSound2, BeepDeepConfig::babaBananaSlipVolume2),
		slot(BeepDeepConfig::babaBananaSlipSound3, BeepDeepConfig::babaBananaSlipVolume3),
		slot(BeepDeepConfig::babaBananaSlipSound4, BeepDeepConfig::babaBananaSlipVolume4),
		slot(BeepDeepConfig::babaBananaSlipSound5, BeepDeepConfig::babaBananaSlipVolume5)),
	APMEKEN_FAIL(BeepDeepConfig::apmekenFailEnabled,
		slot(BeepDeepConfig::apmekenFailSound1, BeepDeepConfig::apmekenFailVolume1),
		slot(BeepDeepConfig::apmekenFailSound2, BeepDeepConfig::apmekenFailVolume2),
		slot(BeepDeepConfig::apmekenFailSound3, BeepDeepConfig::apmekenFailVolume3),
		slot(BeepDeepConfig::apmekenFailSound4, BeepDeepConfig::apmekenFailVolume4),
		slot(BeepDeepConfig::apmekenFailSound5, BeepDeepConfig::apmekenFailVolume5));

	private final Predicate<BeepDeepConfig> enabled;
	private final List<SoundSlot> slots;

	ToaEvent(Predicate<BeepDeepConfig> enabled, SoundSlot... slots)
	{
		this.enabled = enabled;
		for (int i = 0; i < slots.length; i++)
		{
			slots[i].index = i;
		}
		this.slots = List.of(slots);
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
		private int index;
		private final Function<BeepDeepConfig, String> source;
		private final ToIntFunction<BeepDeepConfig> volume;

		private SoundSlot(Function<BeepDeepConfig, String> source, ToIntFunction<BeepDeepConfig> volume)
		{
			this.source = source;
			this.volume = volume;
		}

		int index()
		{
			return index;
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
