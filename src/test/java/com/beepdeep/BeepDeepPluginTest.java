package com.beepdeep;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BeepDeepPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BeepDeepPlugin.class);
		RuneLite.main(args);
	}
}
