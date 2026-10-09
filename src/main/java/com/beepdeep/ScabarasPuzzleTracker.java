package com.beepdeep;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.Player;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.gameval.ObjectID;

/** Correlates puzzle state changes with damage; damage alone is not a failure. */
final class ScabarasPuzzleTracker
{
	private static final int SEQUENCE_LENGTH = 5;
	private static final Pattern NUMBER_TARGET = Pattern.compile(
		"The number (?:@[^@]*@)*(\\d+) has been hastily chipped into the stone\\.");
	private static final Pattern COMPLETION = Pattern.compile("Puzzle \\d+ has been completed!");

	private final List<LocalPoint> sequence = new ArrayList<>(SEQUENCE_LENGTH);
	private final Set<LocalPoint> displaying = new HashSet<>();
	private final Map<LocalPoint, GameObject> pressedSequenceTiles = new HashMap<>();
	private final Map<LocalPoint, SequenceStep> sequenceSteps = new HashMap<>();
	private final Map<LocalPoint, GameObject> numberObjects = new HashMap<>();
	private final Map<LocalPoint, Integer> numberValues = new HashMap<>();
	private boolean sequenceReady;
	private boolean sequenceComplete;
	private int lastDisplayTick = -1;
	private int sequenceProgress;
	private final Map<Player, PlayerState> playerStates = new HashMap<>();
	private final Set<Player> numberDamagedPlayers = new HashSet<>();
	private int targetNumber;
	private int numberPeakTotal;
	private int numberResetTick = -1;
	private int numberResetTotal;
	private int numberRemovalTick = -1;
	private int numberRemovedCount;
	private int numberResetCount;
	private boolean numberComplete;
	private boolean numberFailureLatched;

	void reset()
	{
		restartSequence();
		numberObjects.clear();
		numberValues.clear();
		targetNumber = 0;
		numberPeakTotal = 0;
		numberResetTick = -1;
		numberResetTotal = 0;
		numberRemovalTick = -1;
		numberRemovedCount = 0;
		numberResetCount = 0;
		numberDamagedPlayers.clear();
		numberComplete = false;
		numberFailureLatched = false;
		playerStates.clear();
	}

	// Seed active plates once after a scene load or enabling the plugin mid-room.
	void snapshot(Tile[][] tiles)
	{
		for (Tile[] column : tiles)
		{
			if (column == null)
			{
				continue;
			}
			for (Tile tile : column)
			{
				if (tile == null || tile.getGameObjects() == null)
				{
					continue;
				}
				for (GameObject object : tile.getGameObjects())
				{
					if (object != null && (object.getId() == ObjectID.TOA_SCABARAS_SIMONSAYS_TILE_DOWN_PLAYER
						|| numberValue(tile) > 0))
					{
						spawned(tile, object, -1);
					}
				}
			}
		}
	}

	void restartSequence()
	{
		sequence.clear();
		displaying.clear();
		pressedSequenceTiles.clear();
		sequenceSteps.clear();
		sequenceReady = false;
		sequenceComplete = false;
		lastDisplayTick = -1;
		sequenceProgress = 0;
		for (PlayerState state : playerStates.values())
		{
			state.sequenceDamage = null;
			state.lastSequenceFailure = null;
			state.lastSequenceStepTick = -1;
		}
	}

	void spawned(Tile tile, GameObject object, int tick)
	{
		LocalPoint point = object.getLocalLocation();
		if (object.getId() == ObjectID.TOA_SCABARAS_SIMONSAYS_TILE_DOWN)
		{
			// Completion lights multiple tiles together; demonstration lights one per tick.
			if (lastDisplayTick == tick)
			{
				sequenceComplete = true;
				sequenceReady = false;
				return;
			}
			if (sequenceReady || sequenceComplete || sequence.size() == SEQUENCE_LENGTH)
			{
				restartSequence();
			}
			sequence.add(point);
			displaying.add(point);
			sequenceReady = false;
			lastDisplayTick = tick;
		}
		else if (object.getId() == ObjectID.TOA_SCABARAS_SIMONSAYS_TILE_DOWN_PLAYER)
		{
			// Both correct and incorrect input lights this object. It goes dark when
			// vacated, so active lights cannot represent accepted sequence progress.
			if (pressedSequenceTiles.put(point, object) != object && tick >= 0
				&& sequenceReady && !sequenceComplete && sequenceProgress < sequence.size())
			{
				LocalPoint expected = sequence.get(sequenceProgress);
				boolean correct = expected.equals(point);
				sequenceSteps.put(point, new SequenceStep(tick, correct));
				sequenceProgress = correct ? sequenceProgress + 1 : 0;
				if (correct)
				{
					for (PlayerState state : playerStates.values())
					{
						state.lastSequenceFailure = null;
					}
				}
			}
		}
		else if (isNumberLight(object.getId()) && numberValue(tile) > 0)
		{
			if (numberObjects.get(point) != object)
			{
				numberFailureLatched = false;
			}
			numberObjects.put(point, object);
			numberValues.put(point, numberValue(tile));
			numberPeakTotal = Math.max(numberPeakTotal, numberTotal());
		}
	}

	void despawned(GameObject object, int tick)
	{
		LocalPoint point = object.getLocalLocation();
		if (object.getId() == ObjectID.TOA_SCABARAS_SIMONSAYS_TILE_DOWN)
		{
			displaying.remove(point);
			sequenceReady = !sequenceComplete && displaying.isEmpty() && sequence.size() == SEQUENCE_LENGTH;
		}
		else if (pressedSequenceTiles.get(point) == object)
		{
			pressedSequenceTiles.remove(point);
		}
		else if (numberObjects.get(point) == object)
		{
			if (numberRemovalTick != tick)
			{
				numberRemovedCount = 0;
				numberRemovalTick = tick;
			}
			numberRemovedCount++;
			numberPeakTotal = Math.max(numberPeakTotal, numberTotal());
			numberObjects.remove(point);
			numberValues.remove(point);
			if (numberObjects.isEmpty())
			{
				numberResetTick = tick;
				numberResetTotal = numberPeakTotal;
				numberResetCount = numberRemovedCount;
			}
		}
	}

	void observe(Player player, int tick)
	{
		LocalPoint point = player.getLocalLocation();
		if (point == null)
		{
			return;
		}
		PlayerState state = playerStates.computeIfAbsent(player, ignored -> new PlayerState());
		if (sequenceReady && !point.equals(state.previousPoint))
		{
			state.lastSequenceStepTick = tick;
		}
		state.previousPoint = point;
	}

	void forget(Player player)
	{
		playerStates.remove(player);
		numberDamagedPlayers.remove(player);
	}

	void damaged(Player player, ToaEvent puzzle, int tick)
	{
		if (puzzle == ToaEvent.SCABARAS_SEQUENCE_FAIL)
		{
			observe(player, tick);
			PlayerState state = playerStates.get(player);
			if (state != null)
			{
				state.sequenceDamage = player.getLocalLocation();
			}
		}
		else if (puzzle == ToaEvent.SCABARAS_NUMBER_FAIL)
		{
			numberDamagedPlayers.add(player);
		}
	}

	void chat(String message)
	{
		Matcher target = NUMBER_TARGET.matcher(message);
		if (target.find())
		{
			targetNumber = Integer.parseInt(target.group(1));
			numberComplete = false;
		}
		if (COMPLETION.matcher(message).find())
		{
			// The random ordinal does not identify the puzzle, and the observer may be
			// solving a different board. Use shared completion state or pending damage.
			boolean sequenceFinished = sequenceComplete || sequenceProgress == SEQUENCE_LENGTH;
			if (sequenceFinished)
			{
				sequenceComplete = true;
				for (PlayerState state : playerStates.values())
				{
					state.sequenceDamage = null;
				}
			}
			if ((targetNumber > 0 && (numberTotal() == targetNumber || numberResetTotal == targetNumber))
				|| (!sequenceFinished && !numberDamagedPlayers.isEmpty()))
			{
				numberDamagedPlayers.clear();
				numberResetTotal = 0;
				numberPeakTotal = 0;
				numberComplete = true;
			}
		}
		if (message.startsWith("Your party failed to complete the challenge"))
		{
			reset();
		}
	}

	EnumSet<ToaEvent> finishTick(int tick)
	{
		EnumSet<ToaEvent> failures = EnumSet.noneOf(ToaEvent.class);
		for (PlayerState state : playerStates.values())
		{
			SequenceStep step = sequenceSteps.get(state.sequenceDamage);
			boolean recentPress = step != null && step.tick >= tick - 1;
			// A press record retains the expected tile before progress advances or
			// resets. This works whether the light or the damage arrives first.
			boolean wrongTile = recentPress ? !step.correct
				: state.sequenceDamage != null && state.lastSequenceStepTick >= tick - 1
					&& sequenceProgress < sequence.size() && !pressedSequenceTiles.containsKey(state.sequenceDamage)
					&& !sequence.get(sequenceProgress).equals(state.sequenceDamage);
			boolean failure = state.sequenceDamage != null && sequenceReady && !sequenceComplete
				&& !state.sequenceDamage.equals(state.lastSequenceFailure) && wrongTile;
			if (failure)
			{
				failures.add(ToaEvent.SCABARAS_SEQUENCE_FAIL);
				state.lastSequenceFailure = state.sequenceDamage;
			}
			state.sequenceDamage = null;
			if (state.lastSequenceFailure != null && !state.lastSequenceFailure.equals(state.previousPoint))
			{
				state.lastSequenceFailure = null;
			}
		}
		if (failures.contains(ToaEvent.SCABARAS_SEQUENCE_FAIL))
		{
			sequenceProgress = 0;
		}
		if (!numberDamagedPlayers.isEmpty())
		{
			boolean overTarget = targetNumber > 0 && numberPeakTotal > targetNumber;
			boolean incorrectReset = numberResetTick >= tick - 1 && numberResetTotal > 0 && numberResetCount > 1
				&& (targetNumber == 0 || numberResetTotal != targetNumber);
			boolean failure = !numberComplete && !numberFailureLatched && (overTarget || incorrectReset);
			if (failure)
			{
				failures.add(ToaEvent.SCABARAS_NUMBER_FAIL);
				numberResetTotal = 0;
				numberFailureLatched = true;
			}
		}
		numberDamagedPlayers.clear();
		numberPeakTotal = numberTotal();
		sequenceSteps.values().removeIf(step -> step.tick < tick - 1);
		return failures;
	}

	private static final class PlayerState
	{
		private LocalPoint previousPoint;
		private LocalPoint sequenceDamage;
		private LocalPoint lastSequenceFailure;
		private int lastSequenceStepTick = -1;
	}

	private static final class SequenceStep
	{
		private final int tick;
		private final boolean correct;

		private SequenceStep(int tick, boolean correct)
		{
			this.tick = tick;
			this.correct = correct;
		}
	}

	private int numberTotal()
	{
		return numberValues.values().stream().mapToInt(Integer::intValue).sum();
	}

	static int numberValue(Tile tile)
	{
		GroundObject ground = tile == null ? null : tile.getGroundObject();
		if (ground == null)
		{
			return 0;
		}
		switch (ground.getId())
		{
			case ObjectID.TOA_SCABARAS_TOTALTILES_TILE1: return 1;
			case ObjectID.TOA_SCABARAS_TOTALTILES_TILE2: return 2;
			case ObjectID.TOA_SCABARAS_TOTALTILES_TILE3: return 3;
			case ObjectID.TOA_SCABARAS_TOTALTILES_TILE4: return 4;
			case ObjectID.TOA_SCABARAS_TOTALTILES_TILE5: return 5;
			case ObjectID.TOA_SCABARAS_TOTALTILES_TILE6: return 6;
			case ObjectID.TOA_SCABARAS_TOTALTILES_TILE7: return 7;
			case ObjectID.TOA_SCABARAS_TOTALTILES_TILE8: return 8;
			case ObjectID.TOA_SCABARAS_TOTALTILES_TILE9: return 9;
			default: return 0;
		}
	}

	private static boolean isNumberLight(int id)
	{
		switch (id)
		{
			case ObjectID.TOA_SCABARAS_FX03:
			case ObjectID.TOA_SCABARAS_FX05:
			case ObjectID.TOA_SCABARAS_FX06:
			case ObjectID.TOA_SCABARAS_FX07:
			case ObjectID.TOA_SCABARAS_FX08:
			case ObjectID.TOA_SCABARAS_FX09:
			case ObjectID.TOA_SCABARAS_FX10:
			case ObjectID.TOA_SCABARAS_FX11:
			case ObjectID.TOA_SCABARAS_FX12:
				return true;
			default: return false;
		}
	}
}
