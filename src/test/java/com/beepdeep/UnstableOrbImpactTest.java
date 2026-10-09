package com.beepdeep;

import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UnstableOrbImpactTest
{
	private PluginEventFixture f;

	@Before
	public void setUp()
	{
		f = new PluginEventFixture();
	}

	@Test
	public void repeatedUpdatesForOneImpactDoNotReplaySound()
	{
		LeaderEvents events = new LeaderEvents();
		assertTrue(events.recordOrbImpact(1, 100));
		assertFalse(events.recordOrbImpact(1, 100));
		assertTrue(events.recordOrbImpact(1, 101));
		assertFalse(events.recordOrbImpact(1, 101));
	}

	@Test
	public void separateOrbsHittingInTheSameCycleAreBothRecorded()
	{
		LeaderEvents events = new LeaderEvents();
		assertTrue(events.recordOrbImpact(1, 100));
		assertTrue(events.recordOrbImpact(2, 100));
		assertFalse(events.recordOrbImpact(1, 100));
		assertFalse(events.recordOrbImpact(2, 100));
	}

	@Test
	public void reconnectClearsImpactHistory()
	{
		assertTrue(f.leaderEvents.recordOrbImpact(1, 100));
		GameStateChanged event = new GameStateChanged();
		event.setGameState(GameState.CONNECTION_LOST);
		f.plugin.onGameStateChanged(event);
		f.leaderEvents.onGameStateChanged(event);
		assertTrue(f.leaderEvents.recordOrbImpact(1, 100));
	}
}
