package com.beepdeep;

import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.WorldView;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.gameval.AnimationID;
import net.runelite.api.gameval.NpcID;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class CrondisEventTest
{
	private static final int CRONDIS = 15698;
	private static final String NO_CONTAINER = "You don't have anything to fill.";
	private static final String EMPTY_WATERFALL = "It's empty";
	private PluginEventFixture f;
	private NPC crocodile;

	@Before
	public void setUp()
	{
		f = new PluginEventFixture();
		f.region(CRONDIS);
		crocodile = mock(NPC.class);
		when(crocodile.getWorldView()).thenReturn(f.world);
		when(crocodile.getId()).thenReturn(NpcID.TOA_CRONDIS_CROCODILE);
		when(crocodile.getAnimation()).thenReturn(AnimationID.CROC_ATTACK_MERGE);
	}

	@Test
	public void palmAttackPlaysEachTimeCrocodileAttacks()
	{
		animate(crocodile);
		when(crocodile.getAnimation()).thenReturn(AnimationID.CROC_READY);
		animate(crocodile);
		when(crocodile.getAnimation()).thenReturn(AnimationID.CROC_ATTACK_MERGE);
		animate(crocodile);
		verify(f.sounds, times(2)).trigger(ToaEvent.CRONDIS_PALM_DAMAGE);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void otherCrocodileInteractionsAndNpcsStaySilent()
	{
		for (int animation : new int[]{AnimationID.CROC_WALK, AnimationID.CROC_READY,
			AnimationID.CROC_PARRY, AnimationID.CROC_DEATH, AnimationID.CROC_ATTACK, -1})
		{
			when(crocodile.getAnimation()).thenReturn(animation);
			animate(crocodile);
		}
		when(crocodile.getAnimation()).thenReturn(AnimationID.CROC_ATTACK_MERGE);
		when(crocodile.getId()).thenReturn(NpcID.TOA_ZEBAK_LANDCROC);
		animate(crocodile);
		when(f.local.getAnimation()).thenReturn(AnimationID.CROC_ATTACK_MERGE);
		animate(f.local);
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void attackRequiresCrondisInstanceAndMatchingWorldView()
	{
		f.region(15696); // Zebak's boss room.
		animate(crocodile);
		f.region(CRONDIS);
		when(crocodile.getWorldView()).thenReturn(mock(WorldView.class));
		animate(crocodile);
		when(crocodile.getWorldView()).thenReturn(f.world);
		when(f.world.isInstance()).thenReturn(false);
		animate(crocodile);
		when(f.world.isInstance()).thenReturn(true);
		when(f.client.getGameState()).thenReturn(GameState.LOADING);
		animate(crocodile);
		when(f.client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(f.client.getLocalPlayer()).thenReturn(null);
		animate(crocodile);
		when(f.client.getLocalPlayer()).thenReturn(f.local);
		when(f.local.getLocalLocation()).thenReturn(null);
		animate(crocodile);
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void exactGameMessageAndTaggedSpamPlayContainerReminder()
	{
		chat(ChatMessageType.GAMEMESSAGE, NO_CONTAINER);
		chat(ChatMessageType.SPAM, "<col=ff0000>" + NO_CONTAINER + "</col>");
		verify(f.sounds, times(2)).trigger(ToaEvent.CRONDIS_NO_CONTAINER, true);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void emptyWaterfallUsesSameReminderWithOrWithoutFinalPeriod()
	{
		for (ChatMessageType type : new ChatMessageType[]{ChatMessageType.GAMEMESSAGE, ChatMessageType.SPAM})
		{
			chat(type, EMPTY_WATERFALL);
			chat(type, "<col=ff0000>" + EMPTY_WATERFALL + ".</col>");
		}
		verify(f.sounds, times(4)).trigger(ToaEvent.CRONDIS_NO_CONTAINER, true);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void playerChatAndUnrelatedMessagesStaySilent()
	{
		chat(ChatMessageType.PUBLICCHAT, NO_CONTAINER);
		chat(ChatMessageType.PRIVATECHAT, NO_CONTAINER);
		chat(ChatMessageType.PUBLICCHAT, EMPTY_WATERFALL);
		chat(ChatMessageType.PRIVATECHAT, EMPTY_WATERFALL + ".");
		chat(ChatMessageType.GAMEMESSAGE, "You fill your container with water.");
		chat(ChatMessageType.GAMEMESSAGE, "Reminder: " + NO_CONTAINER);
		chat(ChatMessageType.SPAM, NO_CONTAINER + " More text.");
		chat(ChatMessageType.GAMEMESSAGE, "Reminder: " + EMPTY_WATERFALL);
		chat(ChatMessageType.SPAM, EMPTY_WATERFALL + " More text.");
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void containerReminderRequiresCrondisInstanceAndLocalPlayer()
	{
		f.region(14674);
		chat(ChatMessageType.GAMEMESSAGE, NO_CONTAINER);
		chat(ChatMessageType.GAMEMESSAGE, EMPTY_WATERFALL);
		f.region(CRONDIS);
		when(f.world.isInstance()).thenReturn(false);
		chat(ChatMessageType.GAMEMESSAGE, NO_CONTAINER);
		chat(ChatMessageType.GAMEMESSAGE, EMPTY_WATERFALL);
		when(f.world.isInstance()).thenReturn(true);
		when(f.client.getLocalPlayer()).thenReturn(null);
		chat(ChatMessageType.GAMEMESSAGE, NO_CONTAINER);
		chat(ChatMessageType.GAMEMESSAGE, EMPTY_WATERFALL);
		when(f.client.getLocalPlayer()).thenReturn(f.local);
		when(f.local.getLocalLocation()).thenReturn(null);
		chat(ChatMessageType.GAMEMESSAGE, NO_CONTAINER);
		chat(ChatMessageType.GAMEMESSAGE, EMPTY_WATERFALL);
		when(f.client.getTopLevelWorldView()).thenReturn(null);
		chat(ChatMessageType.GAMEMESSAGE, NO_CONTAINER);
		chat(ChatMessageType.GAMEMESSAGE, EMPTY_WATERFALL);
		verifyNoInteractions(f.sounds);
	}

	private void animate(Actor actor)
	{
		AnimationChanged event = new AnimationChanged();
		event.setActor(actor);
		f.plugin.onAnimationChanged(event);
	}

	private void chat(ChatMessageType type, String message)
	{
		ChatMessage event = new ChatMessage();
		event.setType(type);
		event.setMessage(message);
		f.plugin.onChatMessage(event);
	}
}
