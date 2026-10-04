package com.beepdeep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class SoundManagerTest
{
	@Test
	public void effectVolumeClampsToClientRange()
	{
		assertEquals(0, SoundManager.effectVolumeFromPercent(-1));
		assertEquals(0, SoundManager.effectVolumeFromPercent(0));
		assertEquals(64, SoundManager.effectVolumeFromPercent(50));
		assertEquals(127, SoundManager.effectVolumeFromPercent(100));
		assertEquals(127, SoundManager.effectVolumeFromPercent(200));
	}

	@Test
	public void quotedAndBlankSourcesAreNormalized()
	{
		assertEquals("", SoundManager.normalizeSource(null));
		assertEquals("", SoundManager.normalizeSource("  \" \"  "));
		assertEquals("2192", SoundManager.normalizeSource(" \"2192\" "));
		assertEquals("beep-deep/sounds/my sound.wav", SoundManager.normalizeSource(" \"beep-deep/sounds/my sound.wav\" "));
	}

	@Test
	public void invalidOrOverflowingIdsAreNotParsed()
	{
		assertEquals(Integer.valueOf(2192), SoundManager.tryParseSoundId(" 2192 "));
		assertNull(SoundManager.tryParseSoundId(null));
		assertNull(SoundManager.tryParseSoundId("-1"));
		assertNull(SoundManager.tryParseSoundId("2147483648"));
		assertNull(SoundManager.tryParseSoundId("sound.wav"));
	}

	@Test
	public void typedConfigUsesDefaultsAndExplicitOverrides()
	{
		BeepDeepConfig defaults = new BeepDeepConfig() {};
		assertEquals("2192", ToaEvent.CRONDIS_ENTER.getSlots().get(0).source(defaults));
		assertEquals(50, ToaEvent.CRONDIS_ENTER.getSlots().get(0).volume(defaults));
		BeepDeepConfig customized = new BeepDeepConfig()
		{
			@Override
			public boolean crondisEnterEnabled()
			{
				return false;
			}

			@Override
			public int crondisEnterVolume1()
			{
				return 150;
			}
		};
		assertTrue(!ToaEvent.CRONDIS_ENTER.isEnabled(customized));
		assertEquals(100, ToaEvent.CRONDIS_ENTER.getSlots().get(0).volume(customized));
		assertTrue(ToaEvent.CRONDIS_LEAVE.isEnabled(customized));
	}

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
