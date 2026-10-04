package com.beepdeep;

import net.runelite.api.ActorSpotAnim;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameState;
import net.runelite.api.IterableHashTable;
import net.runelite.api.NPC;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.gameval.AnimationID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.SpotanimID;
import net.runelite.api.gameval.VarbitID;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

public class RaidEventHandlerTest
{
	private static final String SEAL = "The statue has been struck! The seal weakens!";
	private PluginEventFixture f;

	@Before
	public void setUp()
	{
		f = new PluginEventFixture();
	}

	@Test
	public void directPuzzleTransitionPlaysLeaveBeforeEnter()
	{
		f.region(15698);
		f.ticks(1);
		f.region(14674);
		f.ticks(1);
		InOrder order = inOrder(f.sounds);
		order.verify(f.sounds).trigger(ToaEvent.CRONDIS_ENTER);
		order.verify(f.sounds).trigger(ToaEvent.CRONDIS_LEAVE);
		order.verify(f.sounds).trigger(ToaEvent.HET_ENTER);
		order.verifyNoMoreInteractions();
	}

	@Test
	public void vaultReadsRareBitOnlyOncePerEntry()
	{
		f.region(14672);
		when(f.client.getVarbitValue(VarbitID.TOA_VAULT_SARCOPHAGUS)).thenReturn(2);
		f.ticks(3);
		verify(f.sounds).trigger(ToaEvent.VAULT_NO_RARE_LOOT);
		when(f.client.getVarbitValue(VarbitID.TOA_VAULT_SARCOPHAGUS)).thenReturn(3);
		f.ticks(1);
		verifyNoMoreInteractions(f.sounds);
		f.region(15188);
		f.ticks(1);
		f.region(14672);
		f.ticks(2);
		verify(f.sounds).trigger(ToaEvent.VAULT_RARE_LOOT);
		verify(f.client, times(2)).getVarbitValue(VarbitID.TOA_VAULT_SARCOPHAGUS);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void sealDeadlineFiresOnTwentyFifthTickAndOnlyOnce()
	{
		enterHet();
		chat(ChatMessageType.GAMEMESSAGE, "<col=ff0000>" + SEAL + "</col>");
		f.ticks(24);
		verifyNoInteractions(f.sounds);
		f.ticks(1);
		verify(f.sounds).trigger(ToaEvent.HET_ONE_PHASE_FAIL);
		f.ticks(30);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void completionAtDeadlineCancelsFailure()
	{
		enterHet();
		chat(ChatMessageType.SPAM, SEAL);
		f.ticks(24);
		chat(ChatMessageType.GAMEMESSAGE, "Challenge complete: Path of Het (1:05)");
		f.ticks(30);
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void anotherSealStrikeRestartsDeadline()
	{
		enterHet();
		chat(ChatMessageType.GAMEMESSAGE, SEAL);
		f.ticks(20);
		chat(ChatMessageType.SPAM, SEAL);
		f.ticks(24);
		verifyNoInteractions(f.sounds);
		f.ticks(1);
		verify(f.sounds).trigger(ToaEvent.HET_ONE_PHASE_FAIL);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void leavingOnDeadlineCancelsFailureBeforeProcessingTimer()
	{
		enterHet();
		chat(ChatMessageType.GAMEMESSAGE, SEAL);
		f.ticks(24);
		f.region(14676);
		f.ticks(30);
		verify(f.sounds).trigger(ToaEvent.HET_LEAVE);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void playerChatCannotStartOrCancelSealDeadline()
	{
		enterHet();
		chat(ChatMessageType.PUBLICCHAT, SEAL);
		f.ticks(25);
		verifyNoInteractions(f.sounds);
		chat(ChatMessageType.GAMEMESSAGE, SEAL);
		chat(ChatMessageType.PUBLICCHAT, "Challenge complete: Path of Het");
		f.ticks(25);
		verify(f.sounds).trigger(ToaEvent.HET_ONE_PHASE_FAIL);
	}

	@Test
	public void failureMessagesRequireApmekenInstanceAndGameChat()
	{
		String[] failures = {
			"Damaged roof supports cause some debris to fall on you!",
			"The fumes filling the room suddenly ignite!",
			"Your group is overwhelmed by Amascut's corruption!"
		};
		f.region(15186);
		for (String failure : failures)
		{
			chat(ChatMessageType.GAMEMESSAGE, "<col=ff0000>" + failure + "</col>");
			chat(ChatMessageType.SPAM, failure);
			chat(ChatMessageType.PUBLICCHAT, failure);
		}
		verify(f.sounds, times(6)).trigger(ToaEvent.APMEKEN_FAIL);
		clearInvocations(f.sounds);
		chat(ChatMessageType.GAMEMESSAGE, "Unrelated game message");
		f.region(14674);
		chat(ChatMessageType.GAMEMESSAGE, failures[0]);
		f.region(15186);
		when(f.world.isInstance()).thenReturn(false);
		chat(ChatMessageType.GAMEMESSAGE, failures[0]);
		when(f.client.getTopLevelWorldView()).thenReturn(null);
		chat(ChatMessageType.GAMEMESSAGE, failures[0]);
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void missingPlayerAndWrongRoomCannotStartSealDeadline()
	{
		f.region(15186);
		chat(ChatMessageType.GAMEMESSAGE, SEAL);
		f.region(14674);
		when(f.client.getLocalPlayer()).thenReturn(null);
		chat(ChatMessageType.GAMEMESSAGE, SEAL);
		f.ticks(30);
		when(f.client.getLocalPlayer()).thenReturn(f.local);
		f.ticks(30);
		verify(f.sounds).trigger(ToaEvent.HET_ENTER);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void ticksWithoutLoggedInPlayerDoNotAdvanceDeadline()
	{
		enterHet();
		chat(ChatMessageType.GAMEMESSAGE, SEAL);
		when(f.client.getGameState()).thenReturn(GameState.LOADING);
		f.ticks(30);
		when(f.client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(f.client.getLocalPlayer()).thenReturn(null);
		f.ticks(30);
		when(f.client.getLocalPlayer()).thenReturn(f.local);
		f.ticks(24);
		verifyNoInteractions(f.sounds);
		f.ticks(1);
		verify(f.sounds).trigger(ToaEvent.HET_ONE_PHASE_FAIL);
	}

	@Test
	public void sessionResetClearsTimerRegionAndOrbHistory()
	{
		for (GameState state : new GameState[]{GameState.LOGIN_SCREEN, GameState.HOPPING, GameState.CONNECTION_LOST})
		{
			enterHet();
			chat(ChatMessageType.GAMEMESSAGE, SEAL);
			f.plugin.recordOrbImpact(1, 100);
			GameStateChanged event = new GameStateChanged();
			event.setGameState(state);
			f.plugin.onGameStateChanged(event);
			org.junit.Assert.assertTrue(f.plugin.recordOrbImpact(1, 100));
			f.ticks(30);
			verify(f.sounds).trigger(ToaEvent.HET_ENTER);
			verifyNoMoreInteractions(f.sounds);
			clearInvocations(f.sounds);
		}
	}

	@Test
	public void loadingPreservesActiveSealDeadline()
	{
		enterHet();
		chat(ChatMessageType.GAMEMESSAGE, SEAL);
		GameStateChanged event = new GameStateChanged();
		event.setGameState(GameState.LOADING);
		f.plugin.onGameStateChanged(event);
		f.ticks(25);
		verify(f.sounds).trigger(ToaEvent.HET_ONE_PHASE_FAIL);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void restartResetsStateAndDelegatesSoundLifecycle()
	{
		enterHet();
		chat(ChatMessageType.GAMEMESSAGE, SEAL);
		f.plugin.shutDown();
		f.plugin.startUp();
		f.ticks(30);
		InOrder order = inOrder(f.sounds);
		order.verify(f.sounds).shutDown();
		order.verify(f.sounds).startUp();
		order.verify(f.sounds).trigger(ToaEvent.HET_ENTER);
		order.verifyNoMoreInteractions();
	}

	@Test
	public void slipEventsAcceptTeammatesButRejectNpcsAndNonInstances()
	{
		f.region(15188);
		when(f.local.getAnimation()).thenReturn(AnimationID.ROYAL_HUMAN_SLIP_FALL);
		AnimationChanged event = new AnimationChanged();
		event.setActor(f.local);
		f.plugin.onAnimationChanged(event);
		net.runelite.api.Player teammate = mock(net.runelite.api.Player.class);
		net.runelite.api.coords.LocalPoint location = f.local.getLocalLocation();
		when(teammate.getLocalLocation()).thenReturn(location);
		when(teammate.getAnimation()).thenReturn(AnimationID.ROYAL_HUMAN_SLIP_FALL);
		event.setActor(teammate);
		f.plugin.onAnimationChanged(event);
		verify(f.sounds, times(2)).trigger(ToaEvent.BABA_BANANA_SLIP);
		clearInvocations(f.sounds);
		event.setActor(mock(NPC.class));
		f.plugin.onAnimationChanged(event);
		event.setActor(f.local);
		f.region(15186);
		f.plugin.onAnimationChanged(event);
		f.region(15188);
		when(f.world.isInstance()).thenReturn(false);
		f.plugin.onAnimationChanged(event);
		when(f.world.isInstance()).thenReturn(true);
		when(f.local.getLocalLocation()).thenReturn(null);
		f.plugin.onAnimationChanged(event);
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void orbGraphicsDeduplicateAndDespawnAllowsNpcIndexReuse()
	{
		f.region(14676);
		NPC orb = orb(1, 100);
		graphic(orb);
		graphic(orb);
		graphic(orb(2, 100));
		graphic(orb(1, 101));
		verify(f.sounds, times(3)).trigger(ToaEvent.HET_UNSTABLE_ORB_HIT);
		f.plugin.onNpcDespawned(new NpcDespawned(orb));
		graphic(orb(1, 101));
		verify(f.sounds, times(4)).trigger(ToaEvent.HET_UNSTABLE_ORB_HIT);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void orbEventsRejectWrongActorNpcGraphicRoomAndMissingPlayer()
	{
		f.region(14676);
		GraphicChanged playerGraphic = new GraphicChanged();
		playerGraphic.setActor(f.local);
		f.plugin.onGraphicChanged(playerGraphic);
		NPC npc = orb(1, 100);
		when(npc.getId()).thenReturn(-1);
		graphic(npc);
		when(npc.getId()).thenReturn(NpcID.AKKHA_ENRAGE_ORB);
		when(f.client.getLocalPlayer()).thenReturn(null);
		graphic(npc);
		when(f.client.getLocalPlayer()).thenReturn(f.local);
		f.region(14674);
		graphic(npc);
		f.region(14676);
		when(f.world.isInstance()).thenReturn(false);
		graphic(npc);
		when(f.world.isInstance()).thenReturn(true);
		ActorSpotAnim unrelated = mock(ActorSpotAnim.class);
		when(unrelated.getId()).thenReturn(-1);
		IterableHashTable<ActorSpotAnim> animations = spotAnims(unrelated);
		when(npc.getSpotAnims()).thenReturn(animations);
		graphic(npc);
		verifyNoInteractions(f.sounds);
		graphic(orb(1, 100)); // Rejected events must not poison deduplication.
		verify(f.sounds).trigger(ToaEvent.HET_UNSTABLE_ORB_HIT);
	}

	@Test
	public void regionTransitionClearsOrbDeduplication()
	{
		f.region(14676);
		f.ticks(1);
		graphic(orb(1, 100));
		f.region(15188);
		f.ticks(1);
		f.region(14676);
		f.ticks(1);
		graphic(orb(1, 100));
		verify(f.sounds, times(2)).trigger(ToaEvent.HET_UNSTABLE_ORB_HIT);
		verifyNoMoreInteractions(f.sounds);
	}

	private void enterHet()
	{
		f.region(14674);
		f.ticks(1);
		clearInvocations(f.sounds);
	}

	private void chat(ChatMessageType type, String message)
	{
		ChatMessage event = new ChatMessage();
		event.setType(type);
		event.setMessage(message);
		f.plugin.onChatMessage(event);
	}

	private void graphic(NPC npc)
	{
		GraphicChanged event = new GraphicChanged();
		event.setActor(npc);
		f.plugin.onGraphicChanged(event);
	}

	private NPC orb(int index, int cycle)
	{
		NPC npc = mock(NPC.class);
		when(npc.getId()).thenReturn(NpcID.AKKHA_ENRAGE_ORB);
		when(npc.getIndex()).thenReturn(index);
		ActorSpotAnim impact = mock(ActorSpotAnim.class);
		when(impact.getId()).thenReturn(SpotanimID.AKKHA_ENRAGE_ORB_IMPACT);
		when(impact.getStartCycle()).thenReturn(cycle);
		ActorSpotAnim unrelated = mock(ActorSpotAnim.class);
		when(unrelated.getId()).thenReturn(-1);
		IterableHashTable<ActorSpotAnim> animations = spotAnims(unrelated, impact);
		when(npc.getSpotAnims()).thenReturn(animations);
		return npc;
	}

	@SuppressWarnings("unchecked")
	private IterableHashTable<ActorSpotAnim> spotAnims(ActorSpotAnim... animations)
	{
		IterableHashTable<ActorSpotAnim> table = mock(IterableHashTable.class);
		when(table.iterator()).thenAnswer(invocation -> Arrays.asList(animations).iterator());
		return table;
	}
}
