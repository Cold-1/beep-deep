package com.beepdeep;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class PuzzleRoomTransitionTest
{
	@Parameterized.Parameters(name = "{1}")
	public static Collection<Object[]> rooms()
	{
		return Arrays.asList(new Object[][]{
			{15698, ToaEvent.CRONDIS_ENTER, ToaEvent.CRONDIS_LEAVE, 15700},
			{15186, ToaEvent.APMEKEN_ENTER, ToaEvent.APMEKEN_LEAVE, 15188},
			{14162, ToaEvent.SCABARAS_ENTER, ToaEvent.SCABARAS_LEAVE, 14164},
			{14674, ToaEvent.HET_ENTER, ToaEvent.HET_LEAVE, 14676}
		});
	}

	private final int region;
	private final ToaEvent enter;
	private final ToaEvent leave;
	private final int bossRegion;

	public PuzzleRoomTransitionTest(int region, ToaEvent enter, ToaEvent leave, int bossRegion)
	{
		this.region = region;
		this.enter = enter;
		this.leave = leave;
		this.bossRegion = bossRegion;
	}

	@Test
	public void walkingWithinRoomIsSilentAndReentryPlaysAgain()
	{
		PluginEventFixture f = new PluginEventFixture();
		f.region(region);
		f.ticks(3);
		verify(f.sounds).trigger(enter);
		verifyNoMoreInteractions(f.sounds);
		f.region(bossRegion);
		f.ticks(2);
		verify(f.sounds).trigger(leave);
		verifyNoMoreInteractions(f.sounds);
		f.region(region);
		f.ticks(2);
		verify(f.sounds, times(2)).trigger(enter);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void teleportOutPlaysOnlyRaidLeave()
	{
		PluginEventFixture f = new PluginEventFixture();
		f.region(region);
		f.ticks(1);
		clearInvocations(f.sounds);
		when(f.world.isInstance()).thenReturn(false);
		f.ticks(3);
		verify(f.sounds).trigger(ToaEvent.RAID_LEAVE);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void returningToNexusOrAnotherBossDoesNotPlayPuzzleLeave()
	{
		for (int destination : new int[]{14160, bossRegion == 15188 ? 15700 : 15188})
		{
			PluginEventFixture f = new PluginEventFixture();
			f.region(region);
			f.ticks(1);
			clearInvocations(f.sounds);
			f.region(destination);
			f.ticks(3);
			verifyNoInteractions(f.sounds);
		}
	}
}
