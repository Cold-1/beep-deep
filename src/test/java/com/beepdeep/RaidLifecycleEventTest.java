package com.beepdeep;

import net.runelite.api.ChatMessageType;
import net.runelite.api.GameState;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class RaidLifecycleEventTest
{
	private PluginEventFixture f;

	@Before
	public void setUp()
	{
		f = new PluginEventFixture();
		f.region(14160);
	}

	@Test
	public void entryAcceptsEveryDifficultyAndFormattingBeforeInstanceLoads()
	{
		when(f.world.isInstance()).thenReturn(false);
		for (String difficulty : new String[]{"Entry Mode", "Normal Mode", "Expert Mode"})
		{
			chat(ChatMessageType.GAMEMESSAGE,
				"You enter the Tombs of Amascut (<col=ff0000>" + difficulty + "</col>)...");
		}
		chat(ChatMessageType.SPAM, "You enter the Tombs of Amascut (Normal Mode)...");
		verify(f.sounds, times(4)).trigger(ToaEvent.RAID_ENTER);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void raidLocationAloneDoesNotPlayEntry()
	{
		f.ticks(3);
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void roomFailureIgnoresAllTrailingTextInPuzzleAndBossRooms()
	{
		for (int region : new int[]{15698, 15700, 15186, 15188, 14162, 14164, 14674, 14676, 15184, 15696})
		{
			f.region(region);
			chat(ChatMessageType.GAMEMESSAGE, "<col=ff0000>Your party failed to complete the challenge.</col>");
			chat(ChatMessageType.SPAM, "Your party failed to complete the challenge. You have 2 attempts remaining.");
		}
		verify(f.sounds, times(20)).trigger(ToaEvent.ROOM_FAIL);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void failureAndAbandonmentWorkAfterLeavingInstanceAndDoNotSignalExit()
	{
		when(f.world.isInstance()).thenReturn(false);
		chat(ChatMessageType.GAMEMESSAGE, "<col=ff0000>You failed to survive the Tombs of Amascut.</col>");
		chat(ChatMessageType.SPAM, "You abandon the raid and leave the Tombs of Amascut.");
		verify(f.sounds, times(2)).trigger(ToaEvent.RAID_FAIL);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void playerChatAndIncompleteOrUnrelatedGameMessagesAreSilent()
	{
		String[] messages = {
			"You enter the Tombs of Amascut (Normal Mode)...",
			"Your party failed to complete the challenge.",
			"You failed to survive the Tombs of Amascut.",
			"You abandon the raid and leave the Tombs of Amascut."
		};
		for (String message : messages)
		{
			chat(ChatMessageType.PUBLICCHAT, message);
			chat(ChatMessageType.PRIVATECHAT, message);
			chat(ChatMessageType.FRIENDSCHAT, message);
		}
		chat(ChatMessageType.GAMEMESSAGE, "You enter the Tombs of Amascut...");
		chat(ChatMessageType.GAMEMESSAGE, "Your party failed to complete the challenge");
		chat(ChatMessageType.GAMEMESSAGE, "Challenge complete: Path of Het");
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void allRaidRoomsAndLoadingStayInsideUntilActualExit()
	{
		for (int region : new int[]{14160, 15698, 15700, 15186, 15188, 14162, 14164, 14674, 14676, 15184, 15696, 14672})
		{
			f.region(region);
			f.ticks(1);
			verify(f.sounds, never()).trigger(ToaEvent.RAID_LEAVE);
		}
		clearInvocations(f.sounds);
		when(f.client.getGameState()).thenReturn(GameState.LOADING);
		when(f.world.isInstance()).thenReturn(false);
		f.ticks(3);
		verifyNoInteractions(f.sounds);
		when(f.client.getGameState()).thenReturn(GameState.LOGGED_IN);
		f.ticks(3);
		verify(f.sounds).trigger(ToaEvent.RAID_LEAVE);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void leavingForLobbyOrAnotherInstancePlaysOnceAndCanRepeatNextRaid()
	{
		for (int destination : new int[]{13454, 12345})
		{
			f.region(14160);
			f.ticks(1);
			f.region(destination);
			f.ticks(3);
		}
		verify(f.sounds, times(2)).trigger(ToaEvent.RAID_LEAVE);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void failureMessageWaitsForLocationChangeBeforeExitSound()
	{
		f.ticks(1);
		chat(ChatMessageType.GAMEMESSAGE, "You abandon the raid and leave the Tombs of Amascut.");
		f.ticks(3);
		verify(f.sounds).trigger(ToaEvent.RAID_FAIL);
		verifyNoMoreInteractions(f.sounds);
		f.region(13454);
		f.ticks(3);
		verify(f.sounds).trigger(ToaEvent.RAID_LEAVE);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void missingPlayerLocationOrWorldDoesNotSignalExit()
	{
		f.ticks(1);
		when(f.client.getLocalPlayer()).thenReturn(null);
		f.ticks(1);
		when(f.client.getLocalPlayer()).thenReturn(f.local);
		LocalPoint location = f.local.getLocalLocation();
		when(f.local.getLocalLocation()).thenReturn(null);
		f.ticks(1);
		when(f.local.getLocalLocation()).thenReturn(location);
		when(f.client.getTopLevelWorldView()).thenReturn(null);
		f.ticks(1);
		verifyNoInteractions(f.sounds);
		when(f.client.getTopLevelWorldView()).thenReturn(f.world);
		when(f.world.isInstance()).thenReturn(false);
		f.ticks(1);
		verify(f.sounds).trigger(ToaEvent.RAID_LEAVE);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void logoutHopDisconnectAndRestartClearExitTracking()
	{
		for (GameState state : new GameState[]{GameState.LOGIN_SCREEN, GameState.HOPPING, GameState.CONNECTION_LOST})
		{
			when(f.world.isInstance()).thenReturn(true);
			f.ticks(1);
			GameStateChanged event = new GameStateChanged();
			event.setGameState(state);
			f.plugin.onGameStateChanged(event);
			when(f.world.isInstance()).thenReturn(false);
			f.ticks(1);
		}
		when(f.world.isInstance()).thenReturn(true);
		f.ticks(1);
		f.plugin.shutDown();
		f.plugin.startUp();
		clearInvocations(f.sounds);
		when(f.world.isInstance()).thenReturn(false);
		f.ticks(1);
		verifyNoInteractions(f.sounds);
	}

	private void chat(ChatMessageType type, String message)
	{
		ChatMessage event = new ChatMessage();
		event.setType(type);
		event.setMessage(message);
		f.plugin.onChatMessage(event);
	}
}
