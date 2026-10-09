package com.beepdeep;

import net.runelite.api.gameval.AnimationID;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BananaSlipTest
{
	@Test
	public void playerSlipUsesGenericFallRatherThanBananaAnimation()
	{
		assertTrue(LeaderEvents.isBananaSlip(15188, AnimationID.ROYAL_HUMAN_SLIP_FALL));
		assertFalse(LeaderEvents.isBananaSlip(15188, AnimationID.TOA_BABA_BANANA_FALL));
		assertFalse(LeaderEvents.isBananaSlip(15188, -1));
	}

	@Test
	public void genericFallsOutsideBabaDoNotTriggerBananaSound()
	{
		assertFalse(LeaderEvents.isBananaSlip(-1, AnimationID.ROYAL_HUMAN_SLIP_FALL));
		assertFalse(LeaderEvents.isBananaSlip(15186, AnimationID.ROYAL_HUMAN_SLIP_FALL));
	}
}
