package com.beepdeep;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Beep Deep",
	description = "Plays some beeps when you're deep within a raid.",
	tags = {"toa", "tombs", "amascut", "sound", "audio", "raid", "raids", "fun"}
)
public class BeepDeepPlugin extends Plugin
{
	private static final int CRONDIS_PUZZLE_REGION = 15698;
    private static final int TOA_VAULT_REGION = 14672;
	private static final int VARBIT_ID_SARCOPHAGUS = 14373;

	private static final int TICKS_TO_WAIT_FOR_HET_COMPLETE = 25;
	private static final String HET_SEAL_STRUCK = "The statue has been struck! The seal weakens!";
	private static final String HET_CHALLENGE_COMPLETE = "Challenge complete: Path of Het";

	private static final String APMEKEN_FAIL_DEBRIS = "Damaged roof supports cause some debris to fall on you!";
	private static final String APMEKEN_FAIL_FUMES = "The fumes filling the room suddenly ignite!";
	private static final String APMEKEN_FAIL_CORRUPTION = "Your group is overwhelmed by Amascut's corruption!";

	@Inject
	private Client client;

	@Inject
	private BeepDeepConfig config;

	@Inject
	private SoundManager soundManager;

	private int currentRegion = -1;
	private boolean inCrondisPuzzle = false;
	private int hetSealWaitingTick = -1;

	@Override
	protected void startUp()
	{
		soundManager.startUp();
		resetState();
	}

	@Override
	protected void shutDown()
	{
		soundManager.shutDown();
		resetState();
	}

	private void resetState()
	{
		currentRegion = -1;
		inCrondisPuzzle = false;
		hetSealWaitingTick = -1;
	}

	@Provides
	BeepDeepConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BeepDeepConfig.class);
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
		// Check Het seal completion deadline
		if (hetSealWaitingTick > 0)
		{
			hetSealWaitingTick--;
			if (hetSealWaitingTick == 0)
			{
				soundManager.trigger(ToaEvent.HET_ONE_PHASE_FAIL);
			}
		}

		Player local = client.getLocalPlayer();
		if (local == null)
		{
			return;
		}

		LocalPoint lp = local.getLocalLocation();
		if (lp == null)
		{
			return;
		}

		WorldPoint wp = WorldPoint.fromLocalInstance(client, lp);
		int region = wp == null ? -1 : wp.getRegionID();
		if (region == currentRegion)
		{
			return;
		}

		int previous = currentRegion;
		currentRegion = region;
		onRegionChanged(previous, region);
	}

    // Enter or leave Crondis challenge room
	private void onRegionChanged(int previous, int region)
	{
		boolean nowInCrondis = region == CRONDIS_PUZZLE_REGION;
		if (nowInCrondis && !inCrondisPuzzle)
		{
			soundManager.trigger(ToaEvent.CRONDIS_ENTER);
		}
		else if (!nowInCrondis && inCrondisPuzzle)
		{
			soundManager.trigger(ToaEvent.CRONDIS_LEAVE);
		}
		inCrondisPuzzle = nowInCrondis;

		// Reset Het seal wait if leaving raid
		if (notInInstance())
		{
			hetSealWaitingTick = -1;
		}

        // Vault loot room event
        if (region == TOA_VAULT_REGION) {
            int varbitValue = client.getVarbitValue(VARBIT_ID_SARCOPHAGUS);
            soundManager.trigger(determineVaultLootEvent(varbitValue));
        }
	}

    private ToaEvent determineVaultLootEvent(int varbitValue)
    {
        return (varbitValue & 1) != 0 ? ToaEvent.VAULT_RARE_LOOT : ToaEvent.VAULT_NO_RARE_LOOT;
    }

	public void onChatMessage(ChatMessage event)
	{
		if (notInInstance())
		{
			return;
		}

		String message = event.getMessage();

		// Het seal struck: start countdown to failure if not completed
		if (message.contains(HET_SEAL_STRUCK))
		{
			hetSealWaitingTick = TICKS_TO_WAIT_FOR_HET_COMPLETE;
		}

		// Het challenge completed: cancel failure detection
		if (message.contains(HET_CHALLENGE_COMPLETE))
		{
			hetSealWaitingTick = -1;
		}

		// Apmeken failures
		if (message.contains(APMEKEN_FAIL_DEBRIS)
			|| message.contains(APMEKEN_FAIL_FUMES)
			|| message.contains(APMEKEN_FAIL_CORRUPTION))
		{
			soundManager.trigger(ToaEvent.APMEKEN_FAIL);
		}
	}

	private boolean notInInstance()
	{
		WorldView worldView = client.getTopLevelWorldView();
		return worldView == null || !worldView.isInstance();
	}
}
