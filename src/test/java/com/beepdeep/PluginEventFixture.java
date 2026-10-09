package com.beepdeep;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.IndexedObjectSet;
import net.runelite.api.Player;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.GameTick;

import net.runelite.client.party.WSClient;
import static org.mockito.Mockito.*;

/** Uses real instance-coordinate conversion, with a single unrotated template chunk. */
final class PluginEventFixture
{
	final Client client = mock(Client.class);
	final WSClient wsClient = mock(WSClient.class);
	final Player local = mock(Player.class);
	final WorldView world = mock(WorldView.class);
	final SoundManager sounds = mock(SoundManager.class);
	final List<Player> players = new ArrayList<>();
	final BeepDeepPlugin plugin = new BeepDeepPlugin();

	@SuppressWarnings("unchecked")
	PluginEventFixture()
	{
		Guice.createInjector(new AbstractModule()
		{
			@Override
			protected void configure()
			{
				bind(Client.class).toInstance(client);
				bind(WSClient.class).toInstance(wsClient);
				bind(BeepDeepConfig.class).toInstance(mock(BeepDeepConfig.class));
				bind(SoundManager.class).toInstance(sounds);
				bind(SoundConfigurationSharing.class).toInstance(mock(SoundConfigurationSharing.class));
				bind(SoundPreview.class).toInstance(mock(SoundPreview.class));
			}
		}).injectMembers(plugin);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(client.getLocalPlayer()).thenReturn(local);
		when(client.getTopLevelWorldView()).thenReturn(world);
		when(client.getWorldView(anyInt())).thenReturn(world);
		when(local.getWorldView()).thenReturn(world);
		IndexedObjectSet<Player> visible = mock(IndexedObjectSet.class);
		players.add(local);
		when(visible.iterator()).thenAnswer(invocation -> players.iterator());
		doReturn(visible).when(world).players();
		when(world.isInstance()).thenReturn(true);
		LocalPoint location = LocalPoint.fromScene(1, 1, world);
		when(local.getLocalLocation()).thenReturn(location);
	}

	void region(int region)
	{
		int chunkX = (region >> 8) * 8;
		int chunkY = (region & 255) * 8;
		when(world.getInstanceTemplateChunks()).thenReturn(new int[][][]{{{chunkX << 14 | chunkY << 3}}});
	}

	void ticks(int count)
	{
		for (int i = 0; i < count; i++)
		{
			plugin.onGameTick(new GameTick());
		}
	}
}
