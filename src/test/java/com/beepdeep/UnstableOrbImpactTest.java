package com.beepdeep;

import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UnstableOrbImpactTest
{
	@Test
	public void repeatedUpdatesForOneImpactDoNotReplaySound()
	{
		BeepDeepPlugin plugin = new BeepDeepPlugin();
		assertTrue(plugin.recordOrbImpact(1, 100));
		assertFalse(plugin.recordOrbImpact(1, 100));
		assertTrue(plugin.recordOrbImpact(1, 101));
		assertFalse(plugin.recordOrbImpact(1, 101));
	}

	@Test
	public void separateOrbsHittingInTheSameCycleAreBothRecorded()
	{
		BeepDeepPlugin plugin = new BeepDeepPlugin();
		assertTrue(plugin.recordOrbImpact(1, 100));
		assertTrue(plugin.recordOrbImpact(2, 100));
		assertFalse(plugin.recordOrbImpact(1, 100));
		assertFalse(plugin.recordOrbImpact(2, 100));
	}

	@Test
	public void reconnectClearsImpactHistory()
	{
		BeepDeepPlugin plugin = new BeepDeepPlugin();
		assertTrue(plugin.recordOrbImpact(1, 100));
		GameStateChanged event = new GameStateChanged();
		event.setGameState(GameState.CONNECTION_LOST);
		plugin.onGameStateChanged(event);
		assertTrue(plugin.recordOrbImpact(1, 100));
	}
}
