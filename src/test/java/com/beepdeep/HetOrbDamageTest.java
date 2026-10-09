package com.beepdeep;

import net.runelite.api.Actor;
import net.runelite.api.GameState;
import net.runelite.api.Hitsplat;
import net.runelite.api.HitsplatID;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.HitsplatApplied;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class HetOrbDamageTest
{
	private PluginEventFixture f;

	@Before
	public void setUp()
	{
		f = new PluginEventFixture();
		f.region(14674);
	}

	@Test
	public void eachPositiveHitAlertsImmediatelyWithoutOrbOrPuzzleTracking()
	{
		hitsplat(f.local, HitsplatID.DAMAGE_ME, 1);
		hitsplat(f.local, HitsplatID.DAMAGE_ME, 10);
		hitsplat(f.local, HitsplatID.DAMAGE_MAX_ME, 20);
		verify(f.sounds, times(3)).trigger(ToaEvent.HET_ORB_DAMAGE);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void zeroDamageBlocksHealingAndOtherHitsplatTypesStaySilent()
	{
		hitsplat(f.local, HitsplatID.DAMAGE_ME, 0);
		hitsplat(f.local, HitsplatID.DAMAGE_MAX_ME, 0);
		hitsplat(f.local, HitsplatID.BLOCK_ME, 0);
		hitsplat(f.local, HitsplatID.HEAL, 5);
		hitsplat(f.local, HitsplatID.POISON, 5);
		hitsplat(f.local, HitsplatID.DAMAGE_OTHER, 0);
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void teammateDamageAlertsImmediately()
	{
		Player teammate = teammate();
		hitsplat(teammate, HitsplatID.DAMAGE_OTHER, 10);
		hitsplat(teammate, HitsplatID.DAMAGE_ME, 10);
		hitsplat(teammate, HitsplatID.DAMAGE_MAX_ME, 20);
		verify(f.sounds, times(3)).trigger(ToaEvent.HET_ORB_DAMAGE);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void npcAndTeammateNonDamageHitsStaySilent()
	{
		Player teammate = teammate();
		hitsplat(teammate, HitsplatID.DAMAGE_OTHER, 0);
		hitsplat(teammate, HitsplatID.BLOCK_OTHER, 0);
		hitsplat(teammate, HitsplatID.HEAL, 10);
		hitsplat(teammate, HitsplatID.POISON, 5);
		hitsplat(mock(NPC.class), HitsplatID.DAMAGE_ME, 10);
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void teammateInDifferentWorldOrMissingCoordinatesStaysSilent()
	{
		Player teammate = teammate();
		when(teammate.getWorldView()).thenReturn(mock(WorldView.class));
		hitsplat(teammate, HitsplatID.DAMAGE_OTHER, 10);
		when(teammate.getWorldView()).thenReturn(f.world);
		when(teammate.getLocalLocation()).thenReturn(null);
		hitsplat(teammate, HitsplatID.DAMAGE_OTHER, 10);
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void teammateOutsideHetStaysSilentWhileLocalIsInHet()
	{
		int hetChunk = ((14674 >> 8) * 8) << 14 | ((14674 & 255) * 8) << 3;
		int akkhaChunk = ((14676 >> 8) * 8) << 14 | ((14676 & 255) * 8) << 3;
		when(f.world.getInstanceTemplateChunks()).thenReturn(new int[][][]{{{hetChunk}, {akkhaChunk}}});
		Player teammate = teammate();
		LocalPoint location = LocalPoint.fromScene(9, 1, f.world);
		when(teammate.getLocalLocation()).thenReturn(location);
		hitsplat(teammate, HitsplatID.DAMAGE_OTHER, 10);
		verifyNoInteractions(f.sounds);
		hitsplat(f.local, HitsplatID.DAMAGE_ME, 10);
		verify(f.sounds).trigger(ToaEvent.HET_ORB_DAMAGE);
	}

	@Test
	public void currentCoordinatesAllowEntryHitsAndRejectExitHitsBeforeNextTick()
	{
		f.region(14676);
		f.ticks(1);
		f.region(14674);
		hitsplat(f.local, HitsplatID.DAMAGE_ME, 10);
		verify(f.sounds).trigger(ToaEvent.HET_ORB_DAMAGE);
		f.ticks(1);
		clearInvocations(f.sounds);
		f.region(14676);
		hitsplat(f.local, HitsplatID.DAMAGE_ME, 10);
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void otherPuzzleAndBossRoomsDoNotTriggerHetAlert()
	{
		for (int region : new int[]{15698, 15186, 14162, 14676, 15188, 14672})
		{
			f.region(region);
			hitsplat(f.local, HitsplatID.DAMAGE_ME, 10);
		}
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void loggedOutLoadingAndMissingPlayerStaySilent()
	{
		for (GameState state : new GameState[]{GameState.LOGIN_SCREEN, GameState.LOADING,
			GameState.HOPPING, GameState.CONNECTION_LOST})
		{
			when(f.client.getGameState()).thenReturn(state);
			hitsplat(f.local, HitsplatID.DAMAGE_ME, 10);
		}
		when(f.client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(f.client.getLocalPlayer()).thenReturn(null);
		hitsplat(f.local, HitsplatID.DAMAGE_ME, 10);
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void nonInstanceOrMissingWorldAndCoordinatesStaySilent()
	{
		when(f.world.isInstance()).thenReturn(false);
		hitsplat(f.local, HitsplatID.DAMAGE_ME, 10);
		when(f.world.isInstance()).thenReturn(true);
		when(f.client.getTopLevelWorldView()).thenReturn(null);
		hitsplat(f.local, HitsplatID.DAMAGE_ME, 10);
		when(f.client.getTopLevelWorldView()).thenReturn(f.world);
		when(f.local.getLocalLocation()).thenReturn(null);
		hitsplat(f.local, HitsplatID.DAMAGE_ME, 10);
		verifyNoInteractions(f.sounds);
	}

	private Player teammate()
	{
		Player teammate = mock(Player.class);
		when(teammate.getWorldView()).thenReturn(f.world);
		LocalPoint location = f.local.getLocalLocation();
		when(teammate.getLocalLocation()).thenReturn(location);
		return teammate;
	}

	private void hitsplat(Actor actor, int type, int amount)
	{
		Hitsplat hit = mock(Hitsplat.class);
		when(hit.getHitsplatType()).thenReturn(type);
		when(hit.getAmount()).thenReturn(amount);
		HitsplatApplied event = new HitsplatApplied();
		event.setActor(actor);
		event.setHitsplat(hit);
		f.plugin.onHitsplatApplied(event);
	}
}
