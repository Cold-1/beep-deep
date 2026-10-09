package com.beepdeep;

public enum SoundTestSlot
{
	SOUND_1(0),
	SOUND_2(1),
	SOUND_3(2),
	SOUND_4(3),
	SOUND_5(4);

	private final int index;

	SoundTestSlot(int index)
	{
		this.index = index;
	}

	int getIndex()
	{
		return index;
	}

	@Override
	public String toString()
	{
		return "Sound " + (index + 1);
	}
}
