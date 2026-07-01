package com.beepdeep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class SoundManagerTest
{
	@Test
	public void fullVolumeIsZeroGain()
	{
		assertEquals(0f, SoundManager.gainForVolume(100), 0.0001f);
	}

	@Test
	public void halfVolumeIsAboutMinusSixDecibels()
	{
		assertEquals(-6.02f, SoundManager.gainForVolume(50), 0.01f);
	}

	@Test
	public void quieterVolumesProduceNegativeGain()
	{
		assertTrue(SoundManager.gainForVolume(25) < SoundManager.gainForVolume(75));
		assertTrue(SoundManager.gainForVolume(99) < 0f);
	}
}
