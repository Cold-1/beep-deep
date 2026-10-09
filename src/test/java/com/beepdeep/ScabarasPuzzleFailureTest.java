package com.beepdeep;

import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GraphicsObject;
import net.runelite.api.GroundObject;
import net.runelite.api.Hitsplat;
import net.runelite.api.HitsplatID;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.SpotanimID;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ScabarasPuzzleFailureTest
{
	private PluginEventFixture f;
	private Tile[][] tiles;
	private int tick;

	@Before
	public void setUp()
	{
		f = new PluginEventFixture();
		f.region(14162);
		tiles = new Tile[8][8];
		Scene scene = mock(Scene.class);
		when(f.world.getScene()).thenReturn(scene);
		when(scene.getTiles()).thenReturn(new Tile[][][]{tiles});
		for (int x = 1; x <= 5; x++)
		{
			plate(x, 1, ObjectID.TOA_SCABARAS_SIMONSAYS_TILE_UP);
		}
		plate(2, 2, ObjectID.TOA_SCABARAS_SIMONSAYS_TILE_UP);
		advance();
		clearInvocations(f.sounds);
	}

	@Test
	public void firstWrongSequenceStepNeedsDamageButNoGraphicOrPriorProgress()
	{
		demonstrate();
		move(2, 2);
		damage(1);
		damage(4);
		advance();
		verify(f.sounds).trigger(ToaEvent.SCABARAS_SEQUENCE_FAIL);
		// Scarabs hitting a player left standing on the failed tile must not repeat it.
		damage(3);
		advance();
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void loggedFailureAfterVacatingFirstCorrectPlateAlertsInBothEventOrders()
	{
		for (boolean damageFirst : new boolean[]{true, false})
		{
			f.plugin.shutDown();
			f.plugin.startUp();
			advance();
			clearInvocations(f.sounds);
			demonstrate();
			GameObject first = enter(1, 1);
			advance();
			despawn(first); // Leaving a correct plate does not reset the entered sequence.
			advance();
			hitsplat(f.local, HitsplatID.BLOCK_ME, 0);
			advance();
			move(3, 1); // The third displayed plate is incorrect as the second input.
			if (damageFirst)
			{
				damage(4);
			}
			GameObject wrong = spawn(3, 1, ObjectID.TOA_SCABARAS_SIMONSAYS_TILE_DOWN_PLAYER);
			if (!damageFirst)
			{
				damage(4);
			}
			advance();
			verify(f.sounds).trigger(ToaEvent.SCABARAS_SEQUENCE_FAIL);
			hitsplat(f.local, HitsplatID.BLOCK_ME, 0);
			despawn(wrong);
			damage(3); // A later Scarab hit while standing here must not repeat the alert.
			advance();
			verifyNoMoreInteractions(f.sounds);
		}
	}

	@Test
	public void vacatingCorrectSequencePlatesAndTakingScarabDamageNeverResetsProgress()
	{
		demonstrate();
		for (int x = 1; x <= 5; x++)
		{
			GameObject correct = enter(x, 1);
			damage(5);
			advance();
			despawn(correct);
			move(6, 6);
			advance();
		}
		chat(ChatMessageType.GAMEMESSAGE, "Puzzle 2 has been completed!");
		move(2, 2);
		damage(5);
		advance();
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void teammateWrongLitPlateAfterCorrectInputAlertsAndNextAttemptCanFailAgain()
	{
		Player teammate = teammate("Teammate", 1, 1);
		demonstrate();
		move(6, 6);
		for (int attempt = 0; attempt < 2; attempt++)
		{
			move(teammate, 1, 1);
			GameObject correct = spawn(1, 1, ObjectID.TOA_SCABARAS_SIMONSAYS_TILE_DOWN_PLAYER);
			advance();
			despawn(correct);
			move(teammate, 6, 6);
			advance();
			move(teammate, 3, 1);
			GameObject wrong = spawn(3, 1, ObjectID.TOA_SCABARAS_SIMONSAYS_TILE_DOWN_PLAYER);
			hitsplat(teammate, HitsplatID.DAMAGE_OTHER, 4);
			advance();
			despawn(wrong);
			advance();
		}
		verify(f.sounds, times(2)).trigger(ToaEvent.SCABARAS_SEQUENCE_FAIL);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void failureResetAndRandomDamageWorkInBothEventOrders()
	{
		for (boolean damageFirst : new boolean[]{true, false})
		{
			f.plugin.shutDown();
			f.plugin.startUp();
			advance();
			clearInvocations(f.sounds);
			demonstrate();
			GameObject first = enter(1, 1);
			advance();
			move(2, 2);
			if (damageFirst)
			{
				damage(2);
				despawn(first);
			}
			else
			{
				despawn(first);
				damage(19);
			}
			advance();
			verify(f.sounds).trigger(ToaEvent.SCABARAS_SEQUENCE_FAIL);
			verifyNoMoreInteractions(f.sounds);
		}
	}

	@Test
	public void scarabDamageDuringDisplayAndCorrectSequenceInputIsSilent()
	{
		damage(5); // Idle on an unlit sequence plate before the button is pushed.
		advance();
		demonstrate();
		for (int x = 1; x <= 5; x++)
		{
			enter(x, 1);
			damage(6);
			advance();
		}
		chat(ChatMessageType.GAMEMESSAGE, "<col=00ff00>Puzzle 3 has been completed!</col>");
		damage(4);
		advance();
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void successfulSequenceResetWithScarabDamageIsSilent()
	{
		demonstrate();
		List<GameObject> entries = new ArrayList<>();
		for (int x = 1; x <= 5; x++)
		{
			entries.add(enter(x, 1));
		}
		for (GameObject object : entries)
		{
			despawn(object);
		}
		damage(3);
		chat(ChatMessageType.SPAM, "Puzzle 1 has been completed!");
		advance();
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void numberSelectionIsSilentUntilIncorrectTotalDealsRandomDamage()
	{
		numberTarget(24);
		numberPlate(1, 2, ObjectID.TOA_SCABARAS_TOTALTILES_TILE9, ObjectID.TOA_SCABARAS_FX12);
		damage(6);
		advance();
		numberPlate(2, 2, ObjectID.TOA_SCABARAS_TOTALTILES_TILE9, ObjectID.TOA_SCABARAS_FX12);
		damage(2);
		advance();
		verifyNoInteractions(f.sounds);
		numberPlate(3, 2, ObjectID.TOA_SCABARAS_TOTALTILES_TILE9, ObjectID.TOA_SCABARAS_FX12);
		advance(); // Exceeding the target alone must not play a sound.
		verifyNoInteractions(f.sounds);
		damage(1);
		damage(17);
		advance();
		verify(f.sounds).trigger(ToaEvent.SCABARAS_NUMBER_FAIL);
		damage(4);
		advance();
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void scarabHitWhileIdleOnWrongSequenceTileCannotInventFailure()
	{
		move(2, 2);
		demonstrate();
		advance();
		advance();
		damage(5);
		advance();
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void numberFailureCanAlertAgainAfterResetAndNewAttempt()
	{
		numberTarget(24);
		for (int attempt = 0; attempt < 2; attempt++)
		{
			List<GameObject> selected = selectThreeNumberPlates();
			for (GameObject object : selected)
			{
				despawn(object);
			}
			damage(attempt + 1);
			advance();
		}
		verify(f.sounds, times(2)).trigger(ToaEvent.SCABARAS_NUMBER_FAIL);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void enablingMidRoomSeedsExistingNumberSelectionsOnlyOnce()
	{
		numberTarget(24);
		selectThreeNumberPlates();
		f.plugin.shutDown();
		f.plugin.startUp();
		advance();
		clearInvocations(f.sounds);
		numberTarget(24);
		damage(3);
		advance();
		verify(f.sounds).trigger(ToaEvent.SCABARAS_NUMBER_FAIL);
	}

	@Test
	public void numberResetAndDamageWorkInBothEventOrdersWithoutObservedTarget()
	{
		for (boolean damageFirst : new boolean[]{true, false})
		{
			f.plugin.shutDown();
			f.plugin.startUp();
			advance();
			clearInvocations(f.sounds);
			List<GameObject> selected = selectThreeNumberPlates();
			advance();
			if (damageFirst)
			{
				damage(2);
			}
			for (GameObject object : selected)
			{
				despawn(object);
			}
			if (!damageFirst)
			{
				damage(14);
			}
			advance();
			verify(f.sounds).trigger(ToaEvent.SCABARAS_NUMBER_FAIL);
			verifyNoMoreInteractions(f.sounds);
		}
	}

	@Test
	public void successfulNumberResetAndScarabDamageAreSilent()
	{
		numberTarget(24);
		List<GameObject> selected = new ArrayList<>();
		selected.add(numberPlate(1, 2, ObjectID.TOA_SCABARAS_TOTALTILES_TILE7, ObjectID.TOA_SCABARAS_FX03));
		selected.add(numberPlate(2, 2, ObjectID.TOA_SCABARAS_TOTALTILES_TILE8, ObjectID.TOA_SCABARAS_FX11));
		selected.add(numberPlate(3, 2, ObjectID.TOA_SCABARAS_TOTALTILES_TILE9, ObjectID.TOA_SCABARAS_FX12));
		for (GameObject object : selected)
		{
			despawn(object);
		}
		damage(3);
		advance();
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void numberCompletionMessageWithRandomOrdinalCancelsPendingFailure()
	{
		selectThreeNumberPlates();
		damage(4);
		chat(ChatMessageType.GAMEMESSAGE, "Puzzle 2 has been completed!");
		advance();
		damage(5);
		advance();
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void togglingOneNumberPlateOffWhileScarabHitsIsSilent()
	{
		GameObject selected = numberPlate(1, 2, ObjectID.TOA_SCABARAS_TOTALTILES_TILE9, ObjectID.TOA_SCABARAS_FX12);
		advance();
		despawn(selected);
		damage(4);
		advance();
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void poisonHealingZeroHitsAndUntrackedActorsCannotTriggerFailures()
	{
		numberTarget(24);
		selectThreeNumberPlates();
		hitsplat(f.local, HitsplatID.POISON, 6);
		hitsplat(f.local, HitsplatID.HEAL, 15);
		hitsplat(f.local, HitsplatID.DAMAGE_ME, 0);
		hitsplat(mock(Player.class), HitsplatID.DAMAGE_ME, 9);
		hitsplat(mock(NPC.class), HitsplatID.DAMAGE_OTHER, 9);
		advance();
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void teammateWrongSequenceStepAlertsWhileLocalPlayerIsElsewhere()
	{
		Player teammate = teammate("Teammate", 1, 1);
		demonstrate();
		move(6, 6);
		move(teammate, 2, 2);
		hitsplat(teammate, HitsplatID.DAMAGE_OTHER, 13);
		advance();
		verify(f.sounds).trigger(ToaEvent.SCABARAS_SEQUENCE_FAIL);
		hitsplat(teammate, HitsplatID.DAMAGE_OTHER, 4);
		advance();
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void simultaneousPlayerDamageKeepsSeparateMovementHistoryAndOneAlert()
	{
		Player teammate = teammate("Teammate", 1, 1);
		demonstrate();
		move(teammate, 2, 2);
		hitsplat(teammate, HitsplatID.DAMAGE_OTHER, 3);
		damage(4); // The local player remains on the next correct plate.
		advance();
		verify(f.sounds).trigger(ToaEvent.SCABARAS_SEQUENCE_FAIL);
		damage(2);
		hitsplat(teammate, HitsplatID.DAMAGE_OTHER, 5);
		advance();
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void localMovementCannotMakeIdleTeammateScarabDamageASequenceFailure()
	{
		Player teammate = teammate("Teammate", 2, 2);
		demonstrate();
		advance();
		advance();
		move(4, 1);
		hitsplat(teammate, HitsplatID.DAMAGE_OTHER, 5);
		advance();
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void teammateNumberResetAlertsWithoutLocalTargetOrDamage()
	{
		List<GameObject> selected = selectThreeNumberPlates();
		Player teammate = teammate("Teammate", 3, 2);
		move(6, 6);
		advance();
		hitsplat(teammate, HitsplatID.DAMAGE_OTHER, 11);
		for (GameObject object : selected)
		{
			despawn(object);
		}
		advance();
		verify(f.sounds).trigger(ToaEvent.SCABARAS_NUMBER_FAIL);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void multipleDamagedPlayersProduceOneNumberAlert()
	{
		numberTarget(24);
		selectThreeNumberPlates();
		Player teammate = teammate("Teammate", 3, 2);
		damage(8);
		hitsplat(teammate, HitsplatID.DAMAGE_OTHER, 2);
		advance();
		verify(f.sounds).trigger(ToaEvent.SCABARAS_NUMBER_FAIL);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void despawningPlayerCancelsTheirQueuedFailure()
	{
		Player teammate = teammate("Teammate", 1, 1);
		demonstrate();
		move(teammate, 2, 2);
		hitsplat(teammate, HitsplatID.DAMAGE_OTHER, 6);
		f.players.remove(teammate);
		f.plugin.onPlayerDespawned(new PlayerDespawned(teammate));
		advance();
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void teammateCompletionDoesNotDependOnObserversBoard()
	{
		demonstrate();
		List<GameObject> entries = new ArrayList<>();
		for (int x = 1; x <= 5; x++)
		{
			entries.add(enter(x, 1));
		}
		Player teammate = teammate("Teammate", 5, 1);
		move(6, 6);
		for (GameObject object : entries)
		{
			despawn(object);
		}
		hitsplat(teammate, HitsplatID.DAMAGE_OTHER, 6);
		chat(ChatMessageType.GAMEMESSAGE, "Puzzle 4 has been completed!");
		advance();
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void teammateSequenceFailurePlaysSoundWithoutAddingChatMessages()
	{
		demonstrate();
		Player teammate = teammate("Teammate", 1, 1);
		advance();
		move(teammate, 2, 2);
		hitsplat(teammate, HitsplatID.DAMAGE_OTHER, 7);
		advance();
		verify(f.sounds).trigger(ToaEvent.SCABARAS_SEQUENCE_FAIL);
		verify(f.client, never()).addChatMessage(any(ChatMessageType.class), anyString(), anyString(), anyString());
	}

	@Test
	public void numberFailurePlaysSoundWithoutAddingChatMessages()
	{
		numberTarget(24);
		selectThreeNumberPlates();
		damage(5);
		advance();
		verify(f.sounds).trigger(ToaEvent.SCABARAS_NUMBER_FAIL);
		verify(f.client, never()).addChatMessage(any(ChatMessageType.class), anyString(), anyString(), anyString());
	}

	@Test
	public void wrongRoomNonInstanceAndLoadingDamageAreIgnored()
	{
		demonstrate();
		move(2, 2);
		f.region(14674);
		damage(4);
		f.region(14162);
		when(f.world.isInstance()).thenReturn(false);
		damage(4);
		when(f.world.isInstance()).thenReturn(true);
		when(f.client.getGameState()).thenReturn(GameState.LOADING);
		damage(4);
		when(f.client.getGameState()).thenReturn(GameState.LOGGED_IN);
		advance();
		// Het damage has its own alert, but must not count as a Scabaras failure.
		verify(f.sounds).trigger(ToaEvent.HET_ORB_DAMAGE);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void lightsMatchingAndObeliskGraphicsCannotTriggerPuzzleFailure()
	{
		plate(1, 2, ObjectID.TOA_SCABARAS_LIGHTSOUT_TILE_OFF);
		move(1, 2);
		damage(20);
		plate(1, 2, ObjectID.TOA_SCABARAS_MEMORYGAME_TILE1);
		spawn(1, 2, ObjectID.TOA_SCABARAS_FX05);
		damage(4);
		GraphicsObject graphic = mock(GraphicsObject.class);
		when(graphic.getId()).thenReturn(SpotanimID.GA_BEAST_ROCK_FALL);
		f.plugin.onGraphicsObjectCreated(new GraphicsObjectCreated(graphic));
		advance();
		verifyNoInteractions(f.sounds);
	}

	@Test
	public void leavingRoomCancelsPendingFailureBeforeProcessingIt()
	{
		demonstrate();
		move(2, 2);
		damage(4);
		f.region(14674);
		advance();
		verify(f.sounds).trigger(ToaEvent.HET_ENTER);
		verifyNoMoreInteractions(f.sounds);
	}

	@Test
	public void sessionResetCancelsPendingFailure()
	{
		demonstrate();
		move(2, 2);
		damage(4);
		GameStateChanged event = new GameStateChanged();
		event.setGameState(GameState.LOGIN_SCREEN);
		f.plugin.onGameStateChanged(event);
		advance();
		verify(f.sounds).trigger(ToaEvent.SCABARAS_ENTER);
		verifyNoMoreInteractions(f.sounds);
	}

	private void demonstrate()
	{
		for (int x = 1; x <= 5; x++)
		{
			GameObject display = spawn(x, 1, ObjectID.TOA_SCABARAS_SIMONSAYS_TILE_DOWN);
			damage(3);
			advance();
			despawn(display);
			advance();
		}
	}

	private List<GameObject> selectThreeNumberPlates()
	{
		List<GameObject> selected = new ArrayList<>();
		for (int x = 1; x <= 3; x++)
		{
			selected.add(numberPlate(x, 2, ObjectID.TOA_SCABARAS_TOTALTILES_TILE9, ObjectID.TOA_SCABARAS_FX12));
		}
		return selected;
	}

	private GameObject enter(int x, int y)
	{
		move(x, y);
		return spawn(x, y, ObjectID.TOA_SCABARAS_SIMONSAYS_TILE_DOWN_PLAYER);
	}

	private GameObject numberPlate(int x, int y, int ground, int light)
	{
		plate(x, y, ground);
		move(x, y);
		return spawn(x, y, light);
	}

	private void numberTarget(int target)
	{
		chat(ChatMessageType.GAMEMESSAGE, "The number @red@" + target + " has been hastily chipped into the stone.");
	}

	private void plate(int x, int y, int id)
	{
		Tile tile = mock(Tile.class);
		GroundObject ground = mock(GroundObject.class);
		when(ground.getId()).thenReturn(id);
		when(tile.getGroundObject()).thenReturn(ground);
		tiles[x][y] = tile;
	}

	private GameObject spawn(int x, int y, int id)
	{
		GameObject object = mock(GameObject.class);
		when(object.getId()).thenReturn(id);
		LocalPoint location = LocalPoint.fromScene(x, y, f.world);
		when(object.getLocalLocation()).thenReturn(location);
		when(tiles[x][y].getGameObjects()).thenReturn(new GameObject[]{object});
		GameObjectSpawned event = new GameObjectSpawned();
		event.setTile(tiles[x][y]);
		event.setGameObject(object);
		f.plugin.onGameObjectSpawned(event);
		return object;
	}

	private void despawn(GameObject object)
	{
		LocalPoint point = object.getLocalLocation();
		when(tiles[point.getSceneX()][point.getSceneY()].getGameObjects()).thenReturn(new GameObject[0]);
		GameObjectDespawned event = new GameObjectDespawned();
		event.setTile(tiles[point.getSceneX()][point.getSceneY()]);
		event.setGameObject(object);
		f.plugin.onGameObjectDespawned(event);
	}

	private void move(int x, int y)
	{
		move(f.local, x, y);
	}

	private void move(Player player, int x, int y)
	{
		LocalPoint location = LocalPoint.fromScene(x, y, f.world);
		when(player.getLocalLocation()).thenReturn(location);
	}

	private Player teammate(String name, int x, int y)
	{
		Player player = mock(Player.class);
		when(player.getName()).thenReturn(name);
		when(player.getWorldView()).thenReturn(f.world);
		move(player, x, y);
		f.players.add(player);
		return player;
	}

	private void damage(int amount)
	{
		hitsplat(f.local, HitsplatID.DAMAGE_ME, amount);
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

	private void chat(ChatMessageType type, String message)
	{
		ChatMessage event = new ChatMessage();
		event.setType(type);
		event.setMessage(message);
		f.plugin.onChatMessage(event);
	}

	private void advance()
	{
		when(f.client.getTickCount()).thenReturn(++tick);
		f.ticks(1);
	}
}
