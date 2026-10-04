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
			{15698, ToaEvent.CRONDIS_ENTER, ToaEvent.CRONDIS_LEAVE},
			{15186, ToaEvent.APMEKEN_ENTER, ToaEvent.APMEKEN_LEAVE},
			{14162, ToaEvent.SCABARAS_ENTER, ToaEvent.SCABARAS_LEAVE},
			{14674, ToaEvent.HET_ENTER, ToaEvent.HET_LEAVE}
		});
	}

	private final int region;
	private final ToaEvent enter;
	private final ToaEvent leave;

	public PuzzleRoomTransitionTest(int region, ToaEvent enter, ToaEvent leave)
	{
		this.region = region;
		this.enter = enter;
		this.leave = leave;
	}

	@Test
	public void walkingWithinRoomIsSilentAndReentryPlaysAgain()
	{
		PluginEventFixture f = new PluginEventFixture();
		f.region(region);
		f.ticks(3);
		verify(f.sounds).trigger(enter);
		verifyNoMoreInteractions(f.sounds);
		f.region(15188); // Ba-Ba is not a puzzle room.
		f.ticks(2);
		verify(f.sounds).trigger(leave);
		verifyNoMoreInteractions(f.sounds);
		f.region(region);
		f.ticks(2);
		verify(f.sounds, times(2)).trigger(enter);
		verifyNoMoreInteractions(f.sounds);
	}
}
