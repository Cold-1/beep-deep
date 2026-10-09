package com.beepdeep;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.inject.Inject;
import net.runelite.api.ActorSpotAnim;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GraphicsObject;
import net.runelite.api.GroundObject;
import net.runelite.api.HitsplatID;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Tile;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.api.gameval.AnimationID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.SpotanimID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.PartyChanged;
import net.runelite.client.party.PartyMember;
import net.runelite.client.party.PartyService;
import net.runelite.client.party.WSClient;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@PluginDescriptor(
	name = "Beep Deep",
	description = "Plays some beeps when you're deep within a raid.",
	tags = {"toa", "tombs", "amascut", "sound", "audio", "raid", "raids", "fun"}
)
public class BeepDeepPlugin extends Plugin
{
	private static final int CRONDIS_PUZZLE_REGION = 15698;
	private static final String CRONDIS_NO_CONTAINER = "You don't have anything to fill.";
	private static final String CRONDIS_EMPTY_WATERFALL = "It's empty";
	private static final int SCABARAS_PUZZLE_REGION = 14162;
	private static final int SCABARAS_ROCKFALL_DURATION = 5;

	@Inject
	private Client client;

	@Inject
	private PartyService partyService;

	@Inject
	private WSClient wsClient;

	@Inject
	private SoundManager soundManager;

	@Inject
	private SoundConfigurationSharing soundSharing;

	@Inject
	private SoundPreview soundPreview;

	@Inject
	private BeepDeepConfig config;

	@Inject
	private LeaderEvents leaderEvents;

	private int currentRegion = -1;
	private int scabarasRockfallTick = -1;
	private final List<GraphicsObject> scabarasRocks = new ArrayList<>();

	@Override
	protected void startUp()
	{
		soundManager.startUp();
		soundSharing.startUp();
		soundPreview.startUp();
		wsClient.registerMessage(BeepDeepPartyMessage.class);
		updateLeaderEvents();
		resetState();
	}

	@Override
	protected void shutDown()
	{
		soundPreview.shutDown();
		soundSharing.shutDown();
		soundManager.shutDown();
		leaderEvents.stop();
		wsClient.unregisterMessage(BeepDeepPartyMessage.class);
		resetState();
	}

	private void resetState()
	{
		currentRegion = -1;
		leaderEvents.resetState();
	}

	private void updateLeaderEvents()
	{
		if (!config.enablePartySync() || !partyService.isInParty() || config.isPartyLeader())
		{
			leaderEvents.start();
		}
		else
		{
			leaderEvents.stop();
		}
	}

	@Provides
	BeepDeepConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BeepDeepConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(BeepDeepConfig.GROUP))
		{
			return;
		}
		switch (event.getKey())
		{
			case BeepDeepConfig.PARTY_SYNC_KEY:
			case BeepDeepConfig.PARTY_LEADER_KEY:
				updateLeaderEvents();
				break;

		}
		soundSharing.onConfigChanged(event);
		soundPreview.onConfigChanged(event);
	}

	@Subscribe
	public void onPartyChanged(PartyChanged partyChanged)
	{
		updateLeaderEvents();
	}

	@Subscribe
	public void onBeepDeepPartyMessage(BeepDeepPartyMessage message)
	{
		if (!config.enablePartySync())
		{
			return;
		}

		PartyMember self = partyService.getLocalMember();
		if (self != null && message.getMemberId() == self.getMemberId())
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
		if (local.getLocalLocation() == null || client.getTopLevelWorldView() == null)
		{
			return;
		}

		int region = regionOf(local);
		if (region == -1 && !notInInstance())
		{
			return;
		}
		currentRegion = region;

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

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated graphicsObjectCreated)
	{
		GraphicsObject graphicsObject = graphicsObjectCreated.getGraphicsObject();
		if (currentRegion == SCABARAS_PUZZLE_REGION)
		{
			if (graphicsObject.getId() == SpotanimID.GA_BEAST_ROCK_FALL)
			{
				scabarasRocks.add(graphicsObject);
				scabarasRockfallTick = client.getTickCount();
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE
			&& event.getType() != ChatMessageType.SPAM)
		{
			return;
		}

		String message = event.getMessage();
		String plainMessage = Text.removeTags(message);

		if (notInInstance())
		{
			return;
		}
		Player local = client.getLocalPlayer();
		int region = local == null ? -1 : regionOf(local);
		if (region == CRONDIS_PUZZLE_REGION && (CRONDIS_NO_CONTAINER.equals(plainMessage)
			|| CRONDIS_EMPTY_WATERFALL.equals(plainMessage)
			|| (CRONDIS_EMPTY_WATERFALL + ".").equals(plainMessage)))
		{
			soundManager.trigger(ToaEvent.CRONDIS_NO_CONTAINER);
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
