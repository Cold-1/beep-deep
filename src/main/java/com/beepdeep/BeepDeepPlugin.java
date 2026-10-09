package com.beepdeep;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.ActorSpotAnim;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.gameval.AnimationID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.SpotanimID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.party.WSClient;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "Beep Deep",
	description = "Plays some beeps when you're deep within a raid.",
	tags = {"toa", "tombs", "amascut", "sound", "audio", "raid", "raids", "fun"}
)
public class BeepDeepPlugin extends Plugin
{
	private static final int CRONDIS_PUZZLE_REGION = 15698;
	private static final int APMEKEN_PUZZLE_REGION = 15186;
	private static final int BABA_REGION = 15188;
	private static final int SCABARAS_PUZZLE_REGION = 14162;
	private static final int HET_PUZZLE_REGION = 14674;
	private static final int AKKHA_REGION = 14676;
	private static final int TOA_VAULT_REGION = 14672;

	private static final int TICKS_TO_WAIT_FOR_HET_COMPLETE = 25;
	private static final String HET_SEAL_STRUCK = "The statue has been struck! The seal weakens!";
	private static final String HET_CHALLENGE_COMPLETE = "Challenge complete: Path of Het";

	private static final String APMEKEN_FAIL_DEBRIS = "Damaged roof supports cause some debris to fall on you!";
	private static final String APMEKEN_FAIL_FUMES = "The fumes filling the room suddenly ignite!";
	private static final String APMEKEN_FAIL_CORRUPTION = "Your group is overwhelmed by Amascut's corruption!";

	private static final int SCABARAS_ROCKFALL_DURATION = 5;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private WSClient wsClient;

	@Inject
	private SoundManager soundManager;

	@Inject
	private BeepDeepConfig config;

	private int currentRegion = -1;
	private final Map<Integer, Integer> orbImpactCycles = new HashMap<>();
	private int hetSealWaitingTick = -1;
	private int scabarasRockfallTick = -1;
	private final List<GraphicsObject> scabarasRocks = new ArrayList<>();

	@Override
	protected void startUp()
	{
		soundManager.startUp();
		wsClient.registerMessage(BeepDeepPartyMessage.class);
		resetState();
	}

	@Override
	protected void shutDown()
	{
		soundManager.shutDown();
		wsClient.unregisterMessage(BeepDeepPartyMessage.class);
		resetState();
	}

	private void resetState()
	{
		currentRegion = -1;
		orbImpactCycles.clear();
		hetSealWaitingTick = -1;
	}

	@Provides
	BeepDeepConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BeepDeepConfig.class);
	}

	@Subscribe
	public void onBeepDeepPartyMessage(BeepDeepPartyMessage message)
	{
		if (!config.enablePartySync())
		{
			return;
		}

		ToaEvent event;
		try
		{
			event = ToaEvent.valueOf(message.getEvent());
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			return;
		}

		int slotIndex = message.getSlotIndex();
		soundManager.trigger(event, slotIndex);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		GameState state = event.getGameState();
		if (state == GameState.LOGIN_SCREEN || state == GameState.HOPPING || state == GameState.CONNECTION_LOST)
		{
			resetState();
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		Player local = client.getLocalPlayer();
		if (local == null)
		{
			return;
		}

		int region = regionOf(local);
		if (region != currentRegion)
		{
			int previous = currentRegion;
			currentRegion = region;
			onRegionChanged(previous, region);
		}

		// Process the deadline after transitions, so leaving Het cancels it first.
		if (hetSealWaitingTick > 0 && --hetSealWaitingTick == 0)
		{
			hetSealWaitingTick = -1;
			soundManager.trigger(ToaEvent.HET_ONE_PHASE_FAIL);
		}

		if (!scabarasRocks.isEmpty())
		{
			if (client.getTickCount() >= scabarasRockfallTick + SCABARAS_ROCKFALL_DURATION)
			{
				for (GraphicsObject graphicsObject : scabarasRocks)
				{
					if (local.getWorldLocation().equals(WorldPoint.fromLocal(client, graphicsObject.getLocation())))
					{
						soundManager.trigger(ToaEvent.SCABARAS_ROCKFALL);
					}
				}
				scabarasRocks.clear();
			}
		}
	}

	// Enter or leave a path's puzzle room, once per region transition.
	private void onRegionChanged(int previous, int region)
	{
		ToaEvent leave = puzzleRoomEvent(previous, false);
		if (leave != null)
		{
			soundManager.trigger(leave);
		}
		ToaEvent enter = puzzleRoomEvent(region, true);
		if (enter != null)
		{
			soundManager.trigger(enter);
		}
		orbImpactCycles.clear();
		scabarasRocks.clear();

		if (region != HET_PUZZLE_REGION)
		{
			hetSealWaitingTick = -1;
		}

		if (region == TOA_VAULT_REGION)
		{
			int varbitValue = client.getVarbitValue(VarbitID.TOA_VAULT_SARCOPHAGUS);
			soundManager.trigger(determineVaultLootEvent(varbitValue));
		}
	}

	private static ToaEvent puzzleRoomEvent(int region, boolean entering)
	{
		switch (region)
		{
			case CRONDIS_PUZZLE_REGION:
				return entering ? ToaEvent.CRONDIS_ENTER : ToaEvent.CRONDIS_LEAVE;
			case APMEKEN_PUZZLE_REGION:
				return entering ? ToaEvent.APMEKEN_ENTER : ToaEvent.APMEKEN_LEAVE;
			case SCABARAS_PUZZLE_REGION:
				return entering ? ToaEvent.SCABARAS_ENTER : ToaEvent.SCABARAS_LEAVE;
			case HET_PUZZLE_REGION:
				return entering ? ToaEvent.HET_ENTER : ToaEvent.HET_LEAVE;
			default:
				return null;
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		// Players use the generic slip animation, which must be scoped to Ba-Ba's room.
		if (event.getActor() instanceof Player && !notInInstance()
			&& isBananaSlip(regionOf((Player) event.getActor()), event.getActor().getAnimation()))
		{
			soundManager.trigger(ToaEvent.BABA_BANANA_SLIP);
		}
	}

	static boolean isBananaSlip(int region, int animation)
	{
		return region == BABA_REGION && animation == AnimationID.ROYAL_HUMAN_SLIP_FALL;
	}

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated graphicsObjectCreated)
	{
		if (currentRegion == SCABARAS_PUZZLE_REGION)
		{
			GraphicsObject graphicsObject = graphicsObjectCreated.getGraphicsObject();
			if (graphicsObject.getId() == SpotanimID.GA_BEAST_ROCK_FALL)
			{
				scabarasRocks.add(graphicsObject);
				scabarasRockfallTick = client.getTickCount();
			}
		}
	}

	@Subscribe
	public void onGraphicChanged(GraphicChanged event)
	{
		if (!(event.getActor() instanceof NPC) || notInInstance())
		{
			return;
		}

		NPC orb = (NPC) event.getActor();
		Player local = client.getLocalPlayer();
		if (orb.getId() != NpcID.AKKHA_ENRAGE_ORB
			|| local == null || regionOf(local) != AKKHA_REGION)
		{
			return;
		}

		// The orb itself receives the impact graphic when it hits any player.
		// No player-position check is needed because every teammate's hit should play.
		for (ActorSpotAnim spotAnim : orb.getSpotAnims())
		{
			if (spotAnim.getId() == SpotanimID.AKKHA_ENRAGE_ORB_IMPACT)
			{
				if (recordOrbImpact(orb.getIndex(), spotAnim.getStartCycle()))
				{
					soundManager.trigger(ToaEvent.HET_UNSTABLE_ORB_HIT);
				}
				return;
			}
		}
	}

	boolean recordOrbImpact(int npcIndex, int cycle)
	{
		Integer previousCycle = orbImpactCycles.put(npcIndex, cycle);
		return previousCycle == null || previousCycle != cycle;
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		orbImpactCycles.remove(event.getNpc().getIndex());
	}

	private static ToaEvent determineVaultLootEvent(int varbitValue)
	{
		return (varbitValue & 1) != 0 ? ToaEvent.VAULT_RARE_LOOT : ToaEvent.VAULT_NO_RARE_LOOT;
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (notInInstance() || (event.getType() != ChatMessageType.GAMEMESSAGE
			&& event.getType() != ChatMessageType.SPAM))
		{
			return;
		}

		String message = event.getMessage();
		Player local = client.getLocalPlayer();
		int region = local == null ? -1 : regionOf(local);

		// Het seal struck: start countdown to failure if not completed
		if (region == HET_PUZZLE_REGION && message.contains(HET_SEAL_STRUCK))
		{
			hetSealWaitingTick = TICKS_TO_WAIT_FOR_HET_COMPLETE;
		}

		// Het challenge completed: cancel failure detection
		if (message.contains(HET_CHALLENGE_COMPLETE))
		{
			hetSealWaitingTick = -1;
		}

		// Apmeken failures
		if (region == APMEKEN_PUZZLE_REGION && (message.contains(APMEKEN_FAIL_DEBRIS)
			|| message.contains(APMEKEN_FAIL_FUMES)
			|| message.contains(APMEKEN_FAIL_CORRUPTION)))
		{
			soundManager.trigger(ToaEvent.APMEKEN_FAIL);
		}
	}

	private int regionOf(Player player)
	{
		LocalPoint location = player.getLocalLocation();
		if (location == null || notInInstance())
		{
			return -1;
		}
		WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, location);
		return worldPoint == null ? -1 : worldPoint.getRegionID();
	}

	private boolean notInInstance()
	{
		WorldView worldView = client.getTopLevelWorldView();
		return worldView == null || !worldView.isInstance();
	}
}
