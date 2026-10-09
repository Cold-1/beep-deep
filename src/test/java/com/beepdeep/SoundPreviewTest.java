package com.beepdeep;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SoundPreviewTest
{
	private final BeepDeepConfig config = mock(BeepDeepConfig.class);
	private final ConfigManager configManager = mock(ConfigManager.class);
	private final SoundManager sounds = mock(SoundManager.class);
	private final ClientThread clientThread = mock(ClientThread.class);
	private final SoundConfigurationSharing sharing = mock(SoundConfigurationSharing.class);
	private final List<Runnable> clientTasks = new ArrayList<>();
	private SoundPreview preview;

	@Before
	public void setUp()
	{
		when(config.soundTestEvent()).thenReturn(ToaEvent.HET_ENTER);
		when(config.soundTestSlot()).thenReturn(SoundTestSlot.SOUND_5);
		doAnswer(invocation ->
		{
			clientTasks.add(invocation.getArgument(0));
			return null;
		}).when(clientThread).invoke(any(Runnable.class));
		preview = spy(new SoundPreview(config, configManager, sounds, clientThread, sharing));
		doNothing().when(preview).showError(anyString(), any());
		preview.startUp();
		clearInvocations(configManager);
	}

	@Test
	public void repeatedClicksResetSavedAndVisibleCheckboxAndCaptureSelection() throws Exception
	{
		JCheckBox checkbox = new JCheckBox();
		when(sharing.configCheckbox("Play sound")).thenReturn(checkbox);
		for (int click = 0; click < 2; click++)
		{
			SwingUtilities.invokeAndWait(() ->
			{
				checkbox.setSelected(true);
				preview.onConfigChanged(change(BeepDeepConfig.GROUP, "playTestSound", "false", "true"));
			});
			SwingUtilities.invokeAndWait(() -> assertFalse(checkbox.isSelected()));
		}
		verify(configManager, times(2)).setConfiguration(BeepDeepConfig.GROUP, "profile", "playTestSound", false);
		verifyNoInteractions(sounds);
		when(config.soundTestEvent()).thenReturn(ToaEvent.RAID_LEAVE);
		when(config.soundTestSlot()).thenReturn(SoundTestSlot.SOUND_1);
		clientTasks.forEach(Runnable::run);
		verify(sounds, times(2)).preview(eq(ToaEvent.HET_ENTER), eq(SoundTestSlot.SOUND_5), any());
		preview.shutDown();
	}

	@Test
	public void unrelatedChangesAndUncheckedOrUnchangedValuesNeverPlay()
	{
		preview.onConfigChanged(change("another-plugin", "playTestSound", "false", "true"));
		preview.onConfigChanged(change(BeepDeepConfig.GROUP, "soundTestEvent", "RAID_ENTER", "HET_ENTER"));
		preview.onConfigChanged(change(BeepDeepConfig.GROUP, "playTestSound", "true", "false"));
		preview.onConfigChanged(change(BeepDeepConfig.GROUP, "playTestSound", "true", "true"));
		verifyNoInteractions(sounds, clientThread, configManager);
		preview.shutDown();
	}

	@Test
	public void shutdownAndRestartDiscardQueuedPlaybackAndOldErrors() throws Exception
	{
		preview.onConfigChanged(change(BeepDeepConfig.GROUP, "playTestSound", "false", "true"));
		preview.shutDown();
		preview.startUp();
		clientTasks.remove(0).run();
		verifyNoInteractions(sounds);

		List<Consumer<String>> errors = new ArrayList<>();
		doAnswer(invocation ->
		{
			errors.add(invocation.getArgument(2));
			return null;
		}).when(sounds).preview(any(), any(), any());
		preview.onConfigChanged(change(BeepDeepConfig.GROUP, "playTestSound", "false", "true"));
		clientTasks.remove(0).run();
		errors.get(0).accept("current error");
		SwingUtilities.invokeAndWait(() -> {});
		verify(preview).showError(eq("current error"), any());
		preview.shutDown();
		preview.startUp();
		errors.get(0).accept("stale error");
		SwingUtilities.invokeAndWait(() -> {});
		verify(preview, never()).showError(eq("stale error"), any());
		preview.shutDown();
		preview.onConfigChanged(change(BeepDeepConfig.GROUP, "playTestSound", "false", "true"));
		assertTrue(clientTasks.isEmpty());
	}

	@Test
	public void startupClearsPersistedPlayRequestWithoutPlaying()
	{
		preview.startUp();
		verify(configManager).setConfiguration(BeepDeepConfig.GROUP, "playTestSound", false);
		verifyNoInteractions(sounds, clientThread);
		preview.shutDown();
	}

	private static ConfigChanged change(String group, String key, String oldValue, String newValue)
	{
		ConfigChanged change = new ConfigChanged();
		change.setGroup(group);
		change.setProfile("profile");
		change.setKey(key);
		change.setOldValue(oldValue);
		change.setNewValue(newValue);
		return change;
	}
}
