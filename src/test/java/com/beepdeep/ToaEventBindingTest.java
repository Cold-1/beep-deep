package com.beepdeep;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class ToaEventBindingTest
{
	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> events()
	{
		return Arrays.asList(new Object[][]{
			{ToaEvent.RAID_ENTER, (Predicate<BeepDeepConfig>) BeepDeepConfig::raidEnterEnabled,
				sources(BeepDeepConfig::raidEnterSound1, BeepDeepConfig::raidEnterSound2, BeepDeepConfig::raidEnterSound3, BeepDeepConfig::raidEnterSound4, BeepDeepConfig::raidEnterSound5),
				volumes(BeepDeepConfig::raidEnterVolume1, BeepDeepConfig::raidEnterVolume2, BeepDeepConfig::raidEnterVolume3, BeepDeepConfig::raidEnterVolume4, BeepDeepConfig::raidEnterVolume5), "2192"},
			{ToaEvent.RAID_LEAVE, (Predicate<BeepDeepConfig>) BeepDeepConfig::raidLeaveEnabled,
				sources(BeepDeepConfig::raidLeaveSound1, BeepDeepConfig::raidLeaveSound2, BeepDeepConfig::raidLeaveSound3, BeepDeepConfig::raidLeaveSound4, BeepDeepConfig::raidLeaveSound5),
				volumes(BeepDeepConfig::raidLeaveVolume1, BeepDeepConfig::raidLeaveVolume2, BeepDeepConfig::raidLeaveVolume3, BeepDeepConfig::raidLeaveVolume4, BeepDeepConfig::raidLeaveVolume5), "2192"},
			{ToaEvent.ROOM_FAIL, (Predicate<BeepDeepConfig>) BeepDeepConfig::roomFailEnabled,
				sources(BeepDeepConfig::roomFailSound1, BeepDeepConfig::roomFailSound2, BeepDeepConfig::roomFailSound3, BeepDeepConfig::roomFailSound4, BeepDeepConfig::roomFailSound5),
				volumes(BeepDeepConfig::roomFailVolume1, BeepDeepConfig::roomFailVolume2, BeepDeepConfig::roomFailVolume3, BeepDeepConfig::roomFailVolume4, BeepDeepConfig::roomFailVolume5), "3892"},
			{ToaEvent.RAID_FAIL, (Predicate<BeepDeepConfig>) BeepDeepConfig::raidFailEnabled,
				sources(BeepDeepConfig::raidFailSound1, BeepDeepConfig::raidFailSound2, BeepDeepConfig::raidFailSound3, BeepDeepConfig::raidFailSound4, BeepDeepConfig::raidFailSound5),
				volumes(BeepDeepConfig::raidFailVolume1, BeepDeepConfig::raidFailVolume2, BeepDeepConfig::raidFailVolume3, BeepDeepConfig::raidFailVolume4, BeepDeepConfig::raidFailVolume5), "3892"},
			{ToaEvent.CRONDIS_ENTER, (Predicate<BeepDeepConfig>) BeepDeepConfig::crondisEnterEnabled,
				sources(BeepDeepConfig::crondisEnterSound1, BeepDeepConfig::crondisEnterSound2, BeepDeepConfig::crondisEnterSound3, BeepDeepConfig::crondisEnterSound4, BeepDeepConfig::crondisEnterSound5),
				volumes(BeepDeepConfig::crondisEnterVolume1, BeepDeepConfig::crondisEnterVolume2, BeepDeepConfig::crondisEnterVolume3, BeepDeepConfig::crondisEnterVolume4, BeepDeepConfig::crondisEnterVolume5), "2192"},
			{ToaEvent.CRONDIS_LEAVE, (Predicate<BeepDeepConfig>) BeepDeepConfig::crondisLeaveEnabled,
				sources(BeepDeepConfig::crondisLeaveSound1, BeepDeepConfig::crondisLeaveSound2, BeepDeepConfig::crondisLeaveSound3, BeepDeepConfig::crondisLeaveSound4, BeepDeepConfig::crondisLeaveSound5),
				volumes(BeepDeepConfig::crondisLeaveVolume1, BeepDeepConfig::crondisLeaveVolume2, BeepDeepConfig::crondisLeaveVolume3, BeepDeepConfig::crondisLeaveVolume4, BeepDeepConfig::crondisLeaveVolume5), "2192"},
			{ToaEvent.CRONDIS_PALM_DAMAGE, (Predicate<BeepDeepConfig>) BeepDeepConfig::crocAttackEnabled,
				sources(BeepDeepConfig::crocAttackSound1, BeepDeepConfig::crocAttackSound2, BeepDeepConfig::crocAttackSound3, BeepDeepConfig::crocAttackSound4, BeepDeepConfig::crocAttackSound5),
				volumes(BeepDeepConfig::crocAttackVolume1, BeepDeepConfig::crocAttackVolume2, BeepDeepConfig::crocAttackVolume3, BeepDeepConfig::crocAttackVolume4, BeepDeepConfig::crocAttackVolume5), "2192"},
			{ToaEvent.CRONDIS_NO_CONTAINER, (Predicate<BeepDeepConfig>) BeepDeepConfig::crondisNoContainerEnabled,
				sources(BeepDeepConfig::crondisNoContainerSound1, BeepDeepConfig::crondisNoContainerSound2, BeepDeepConfig::crondisNoContainerSound3, BeepDeepConfig::crondisNoContainerSound4, BeepDeepConfig::crondisNoContainerSound5),
				volumes(BeepDeepConfig::crondisNoContainerVolume1, BeepDeepConfig::crondisNoContainerVolume2, BeepDeepConfig::crondisNoContainerVolume3, BeepDeepConfig::crondisNoContainerVolume4, BeepDeepConfig::crondisNoContainerVolume5), "2192"},
			{ToaEvent.APMEKEN_ENTER, (Predicate<BeepDeepConfig>) BeepDeepConfig::apmekenEnterEnabled,
				sources(BeepDeepConfig::apmekenEnterSound1, BeepDeepConfig::apmekenEnterSound2, BeepDeepConfig::apmekenEnterSound3, BeepDeepConfig::apmekenEnterSound4, BeepDeepConfig::apmekenEnterSound5),
				volumes(BeepDeepConfig::apmekenEnterVolume1, BeepDeepConfig::apmekenEnterVolume2, BeepDeepConfig::apmekenEnterVolume3, BeepDeepConfig::apmekenEnterVolume4, BeepDeepConfig::apmekenEnterVolume5), "2192"},
			{ToaEvent.APMEKEN_LEAVE, (Predicate<BeepDeepConfig>) BeepDeepConfig::apmekenLeaveEnabled,
				sources(BeepDeepConfig::apmekenLeaveSound1, BeepDeepConfig::apmekenLeaveSound2, BeepDeepConfig::apmekenLeaveSound3, BeepDeepConfig::apmekenLeaveSound4, BeepDeepConfig::apmekenLeaveSound5),
				volumes(BeepDeepConfig::apmekenLeaveVolume1, BeepDeepConfig::apmekenLeaveVolume2, BeepDeepConfig::apmekenLeaveVolume3, BeepDeepConfig::apmekenLeaveVolume4, BeepDeepConfig::apmekenLeaveVolume5), "2192"},
			{ToaEvent.SCABARAS_ENTER, (Predicate<BeepDeepConfig>) BeepDeepConfig::scabarasEnterEnabled,
				sources(BeepDeepConfig::scabarasEnterSound1, BeepDeepConfig::scabarasEnterSound2, BeepDeepConfig::scabarasEnterSound3, BeepDeepConfig::scabarasEnterSound4, BeepDeepConfig::scabarasEnterSound5),
				volumes(BeepDeepConfig::scabarasEnterVolume1, BeepDeepConfig::scabarasEnterVolume2, BeepDeepConfig::scabarasEnterVolume3, BeepDeepConfig::scabarasEnterVolume4, BeepDeepConfig::scabarasEnterVolume5), "2192"},
			{ToaEvent.SCABARAS_LEAVE, (Predicate<BeepDeepConfig>) BeepDeepConfig::scabarasLeaveEnabled,
				sources(BeepDeepConfig::scabarasLeaveSound1, BeepDeepConfig::scabarasLeaveSound2, BeepDeepConfig::scabarasLeaveSound3, BeepDeepConfig::scabarasLeaveSound4, BeepDeepConfig::scabarasLeaveSound5),
				volumes(BeepDeepConfig::scabarasLeaveVolume1, BeepDeepConfig::scabarasLeaveVolume2, BeepDeepConfig::scabarasLeaveVolume3, BeepDeepConfig::scabarasLeaveVolume4, BeepDeepConfig::scabarasLeaveVolume5), "2192"},
			{ToaEvent.SCABARAS_SEQUENCE_FAIL, (Predicate<BeepDeepConfig>) BeepDeepConfig::scabarasSequenceFailEnabled,
				sources(BeepDeepConfig::scabarasSequenceFailSound1, BeepDeepConfig::scabarasSequenceFailSound2, BeepDeepConfig::scabarasSequenceFailSound3, BeepDeepConfig::scabarasSequenceFailSound4, BeepDeepConfig::scabarasSequenceFailSound5),
				volumes(BeepDeepConfig::scabarasSequenceFailVolume1, BeepDeepConfig::scabarasSequenceFailVolume2, BeepDeepConfig::scabarasSequenceFailVolume3, BeepDeepConfig::scabarasSequenceFailVolume4, BeepDeepConfig::scabarasSequenceFailVolume5), "3892"},
			{ToaEvent.SCABARAS_NUMBER_FAIL, (Predicate<BeepDeepConfig>) BeepDeepConfig::scabarasNumberFailEnabled,
				sources(BeepDeepConfig::scabarasNumberFailSound1, BeepDeepConfig::scabarasNumberFailSound2, BeepDeepConfig::scabarasNumberFailSound3, BeepDeepConfig::scabarasNumberFailSound4, BeepDeepConfig::scabarasNumberFailSound5),
				volumes(BeepDeepConfig::scabarasNumberFailVolume1, BeepDeepConfig::scabarasNumberFailVolume2, BeepDeepConfig::scabarasNumberFailVolume3, BeepDeepConfig::scabarasNumberFailVolume4, BeepDeepConfig::scabarasNumberFailVolume5), "3892"},
			{ToaEvent.HET_ENTER, (Predicate<BeepDeepConfig>) BeepDeepConfig::hetEnterEnabled,
				sources(BeepDeepConfig::hetEnterSound1, BeepDeepConfig::hetEnterSound2, BeepDeepConfig::hetEnterSound3, BeepDeepConfig::hetEnterSound4, BeepDeepConfig::hetEnterSound5),
				volumes(BeepDeepConfig::hetEnterVolume1, BeepDeepConfig::hetEnterVolume2, BeepDeepConfig::hetEnterVolume3, BeepDeepConfig::hetEnterVolume4, BeepDeepConfig::hetEnterVolume5), "2192"},
			{ToaEvent.HET_LEAVE, (Predicate<BeepDeepConfig>) BeepDeepConfig::hetLeaveEnabled,
				sources(BeepDeepConfig::hetLeaveSound1, BeepDeepConfig::hetLeaveSound2, BeepDeepConfig::hetLeaveSound3, BeepDeepConfig::hetLeaveSound4, BeepDeepConfig::hetLeaveSound5),
				volumes(BeepDeepConfig::hetLeaveVolume1, BeepDeepConfig::hetLeaveVolume2, BeepDeepConfig::hetLeaveVolume3, BeepDeepConfig::hetLeaveVolume4, BeepDeepConfig::hetLeaveVolume5), "2192"},
			{ToaEvent.HET_ORB_DAMAGE, (Predicate<BeepDeepConfig>) BeepDeepConfig::hetOrbDamageEnabled,
				sources(BeepDeepConfig::hetOrbDamageSound1, BeepDeepConfig::hetOrbDamageSound2, BeepDeepConfig::hetOrbDamageSound3, BeepDeepConfig::hetOrbDamageSound4, BeepDeepConfig::hetOrbDamageSound5),
				volumes(BeepDeepConfig::hetOrbDamageVolume1, BeepDeepConfig::hetOrbDamageVolume2, BeepDeepConfig::hetOrbDamageVolume3, BeepDeepConfig::hetOrbDamageVolume4, BeepDeepConfig::hetOrbDamageVolume5), "2192"},
			{ToaEvent.HET_UNSTABLE_ORB_HIT, (Predicate<BeepDeepConfig>) BeepDeepConfig::hetUnstableOrbHitEnabled,
				sources(BeepDeepConfig::hetUnstableOrbHitSound1, BeepDeepConfig::hetUnstableOrbHitSound2, BeepDeepConfig::hetUnstableOrbHitSound3, BeepDeepConfig::hetUnstableOrbHitSound4, BeepDeepConfig::hetUnstableOrbHitSound5),
				volumes(BeepDeepConfig::hetUnstableOrbHitVolume1, BeepDeepConfig::hetUnstableOrbHitVolume2, BeepDeepConfig::hetUnstableOrbHitVolume3, BeepDeepConfig::hetUnstableOrbHitVolume4, BeepDeepConfig::hetUnstableOrbHitVolume5), "2192"},
			{ToaEvent.HET_ONE_PHASE_FAIL, (Predicate<BeepDeepConfig>) BeepDeepConfig::hetOnePhaseFailEnabled,
				sources(BeepDeepConfig::hetOnePhaseFailSound1, BeepDeepConfig::hetOnePhaseFailSound2, BeepDeepConfig::hetOnePhaseFailSound3, BeepDeepConfig::hetOnePhaseFailSound4, BeepDeepConfig::hetOnePhaseFailSound5),
				volumes(BeepDeepConfig::hetOnePhaseFailVolume1, BeepDeepConfig::hetOnePhaseFailVolume2, BeepDeepConfig::hetOnePhaseFailVolume3, BeepDeepConfig::hetOnePhaseFailVolume4, BeepDeepConfig::hetOnePhaseFailVolume5), "3892"},
			{ToaEvent.VAULT_NO_RARE_LOOT, (Predicate<BeepDeepConfig>) BeepDeepConfig::vaultNoRareLootEnabled,
				sources(BeepDeepConfig::vaultNoRareLootSound1, BeepDeepConfig::vaultNoRareLootSound2, BeepDeepConfig::vaultNoRareLootSound3, BeepDeepConfig::vaultNoRareLootSound4, BeepDeepConfig::vaultNoRareLootSound5),
				volumes(BeepDeepConfig::vaultNoRareLootVolume1, BeepDeepConfig::vaultNoRareLootVolume2, BeepDeepConfig::vaultNoRareLootVolume3, BeepDeepConfig::vaultNoRareLootVolume4, BeepDeepConfig::vaultNoRareLootVolume5), ""},
			{ToaEvent.VAULT_RARE_LOOT, (Predicate<BeepDeepConfig>) BeepDeepConfig::vaultRareLootEnabled,
				sources(BeepDeepConfig::vaultRareLootSound1, BeepDeepConfig::vaultRareLootSound2, BeepDeepConfig::vaultRareLootSound3, BeepDeepConfig::vaultRareLootSound4, BeepDeepConfig::vaultRareLootSound5),
				volumes(BeepDeepConfig::vaultRareLootVolume1, BeepDeepConfig::vaultRareLootVolume2, BeepDeepConfig::vaultRareLootVolume3, BeepDeepConfig::vaultRareLootVolume4, BeepDeepConfig::vaultRareLootVolume5), ""},
			{ToaEvent.BABA_BANANA_SLIP, (Predicate<BeepDeepConfig>) BeepDeepConfig::babaBananaSlipEnabled,
				sources(BeepDeepConfig::babaBananaSlipSound1, BeepDeepConfig::babaBananaSlipSound2, BeepDeepConfig::babaBananaSlipSound3, BeepDeepConfig::babaBananaSlipSound4, BeepDeepConfig::babaBananaSlipSound5),
				volumes(BeepDeepConfig::babaBananaSlipVolume1, BeepDeepConfig::babaBananaSlipVolume2, BeepDeepConfig::babaBananaSlipVolume3, BeepDeepConfig::babaBananaSlipVolume4, BeepDeepConfig::babaBananaSlipVolume5), "3892"},
			{ToaEvent.APMEKEN_FAIL, (Predicate<BeepDeepConfig>) BeepDeepConfig::apmekenFailEnabled,
				sources(BeepDeepConfig::apmekenFailSound1, BeepDeepConfig::apmekenFailSound2, BeepDeepConfig::apmekenFailSound3, BeepDeepConfig::apmekenFailSound4, BeepDeepConfig::apmekenFailSound5),
				volumes(BeepDeepConfig::apmekenFailVolume1, BeepDeepConfig::apmekenFailVolume2, BeepDeepConfig::apmekenFailVolume3, BeepDeepConfig::apmekenFailVolume4, BeepDeepConfig::apmekenFailVolume5), "3892"}
		});
	}

	private final ToaEvent event;
	private final Predicate<BeepDeepConfig> enabled;
	private final List<Function<BeepDeepConfig, String>> sources;
	private final List<ToIntFunction<BeepDeepConfig>> volumes;
	private final String defaultSound;

	public ToaEventBindingTest(ToaEvent event, Predicate<BeepDeepConfig> enabled,
		List<Function<BeepDeepConfig, String>> sources, List<ToIntFunction<BeepDeepConfig>> volumes,
		String defaultSound)
	{
		this.event = event;
		this.enabled = enabled;
		this.sources = sources;
		this.volumes = volumes;
		this.defaultSound = defaultSound;
	}

	@Test
	public void eachEventReadsItsOwnToggleAndAllFiveSoundVolumePairs()
	{
		BeepDeepConfig config = mock(BeepDeepConfig.class);
		when(enabled.test(config)).thenReturn(false);
		assertFalse(event.isEnabled(config));
		when(enabled.test(config)).thenReturn(true);
		assertTrue(event.isEnabled(config));
		int[] settings = {-10, 0, 37, 100, 150};
		int[] expected = {0, 0, 37, 100, 100};
		for (int i = 0; i < 5; i++)
		{
			when(sources.get(i).apply(config)).thenReturn("sound-" + i + ".wav");
			when(volumes.get(i).applyAsInt(config)).thenReturn(settings[i]);
		}
		assertEquals(5, event.getSlots().size());
		for (int i = 0; i < 5; i++)
		{
			assertEquals("slot " + i, "sound-" + i + ".wav", event.getSlots().get(i).source(config));
			assertEquals("slot " + i, expected[i], event.getSlots().get(i).volume(config));
		}
	}

	@Test
	public void defaultConfigurationEnablesEventWithOnlyItsPrimarySound()
	{
		BeepDeepConfig config = new BeepDeepConfig() {};
		assertFalse(config.enableRemoteUrls());
		assertTrue(event.isEnabled(config));
		assertEquals(defaultSound, event.getSlots().get(0).source(config));
		for (int i = 0; i < 5; i++)
		{
			assertEquals(50, event.getSlots().get(i).volume(config));
			if (i > 0)
			{
				assertEquals("", event.getSlots().get(i).source(config));
			}
		}
	}

	@SafeVarargs
	private static List<Function<BeepDeepConfig, String>> sources(Function<BeepDeepConfig, String>... sources)
	{
		return Arrays.asList(sources);
	}

	@SafeVarargs
	private static List<ToIntFunction<BeepDeepConfig>> volumes(ToIntFunction<BeepDeepConfig>... volumes)
	{
		return Arrays.asList(volumes);
	}
}
