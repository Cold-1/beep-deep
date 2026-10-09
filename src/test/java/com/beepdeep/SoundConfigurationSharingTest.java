package com.beepdeep;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import javax.swing.SwingUtilities;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SoundConfigurationSharingTest
{
	@Test
	public void preservesEveryEventSlotToggleAndVolume()
	{
		BeepDeepConfig config = mock(BeepDeepConfig.class);
		when(config.masterVolume()).thenReturn(37);
		for (ToaEvent event : ToaEvent.values())
		{
			when(event.isEnabled(config)).thenReturn(event.ordinal() % 2 == 0);
			for (int slot = 0; slot < event.getSlots().size(); slot++)
			{
				ToaEvent.SoundSlot binding = event.getSlots().get(slot);
				// Include Unicode, URL query delimiters, whitespace and deliberately empty slots.
				when(binding.source(config)).thenReturn(slot == 2 ? ""
					: "beep-deep/" + event.name() + "/音 " + slot + "?a=1&b=2.wav");
				when(binding.volume(config)).thenReturn(slot * 25);
			}
		}
		SoundConfigurationCodec.Export exported = SoundConfigurationCodec.encode(config);
		SoundConfigurationCodec.Imported imported = SoundConfigurationCodec.decode(exported.code);
		assertFalse(imported.settings.containsKey("masterVolume"));
		assertEquals(ToaEvent.values().length * 11, imported.settings.size());
		assertFalse(imported.absolutePaths);
		for (ToaEvent event : ToaEvent.values())
		{
			String prefix = event.getConfigPrefix();
			assertEquals(event.isEnabled(config), imported.settings.get(prefix + "Enabled"));
			for (int slot = 0; slot < 5; slot++)
			{
				assertEquals(event.getSlots().get(slot).source(config),
					imported.settings.get(prefix + "Sound" + (slot + 1)));
				assertEquals(event.getSlots().get(slot).volume(config),
					imported.settings.get(prefix + "Volume" + (slot + 1)));
			}
		}
	}

	@Test
	public void exportDoesNotIncludeMasterVolume()
	{
		BeepDeepConfig quiet = new BeepDeepConfig()
		{
			@Override
			public int masterVolume()
			{
				return 0;
			}
		};
		BeepDeepConfig loud = new BeepDeepConfig()
		{
			@Override
			public int masterVolume()
			{
				return 100;
			}
		};
		String code = SoundConfigurationCodec.encode(quiet).code;
		assertTrue(code.startsWith(SoundConfigurationCodec.PREFIX));
		assertEquals(code, SoundConfigurationCodec.encode(loud).code);
		assertFalse(SoundConfigurationCodec.decode(code).settings.containsKey("masterVolume"));
	}

	@Test
	public void countPrefixMatchesAllSlotsAndRejectsIncompatibleCountsBeforeWriting()
	{
		BeepDeepConfig config = new BeepDeepConfig() {};
		int slots = Arrays.stream(ToaEvent.values()).mapToInt(event -> event.getSlots().size()).sum();
		String code = SoundConfigurationCodec.encode(config).code;
		assertEquals(120, slots);
		assertTrue(code.startsWith("BD" + slots + ":"));
		String encoded = code.substring(SoundConfigurationCodec.PREFIX.length());
		ConfigManager manager = mock(ConfigManager.class);
		SoundConfigurationSharing sharing = new SoundConfigurationSharing(config, manager);
		for (String count : new String[]{"0", "1", "2", "119", "125", "99999999999999999999"})
		{
			try
			{
				sharing.importCode("BD" + count + ":" + encoded, imported -> true);
				fail("Accepted incompatible sound-slot count: " + count);
			}
			catch (IllegalArgumentException e)
			{
				assertTrue(e.getMessage().contains("different number of sound slots"));
				assertTrue(e.getMessage().contains("120"));
			}
		}
		verifyNoInteractions(manager);
	}

	@Test
	public void successfulImportClosesSharingDialogThenShowsRefreshNotice() throws Exception
	{
		ConfigManager manager = mock(ConfigManager.class);
		SoundConfigurationSharing sharing = spy(new SoundConfigurationSharing(new BeepDeepConfig() {}, manager));
		JDialog dialog = mock(JDialog.class);
		when(dialog.isDisplayable()).thenReturn(true);
		doReturn(dialog).when(sharing).createDialog();
		doNothing().when(sharing).showRefreshNotice();
		sharing.startUp();
		clearInvocations(manager);
		String code = SoundConfigurationCodec.encode(new BeepDeepConfig() {}).code;
		SwingUtilities.invokeAndWait(() ->
		{
			sharing.openDialog(1);
			sharing.importConfiguration("invalid code", 1);
			verifyNoInteractions(manager);
			verify(dialog, never()).dispose();
			verify(sharing, never()).showRefreshNotice();
			doReturn(false).when(sharing).confirm(anyString(), anyString(), anyBoolean());
			sharing.importConfiguration(code, 1);
			verifyNoInteractions(manager);
			verify(dialog, never()).dispose();
			verify(sharing, never()).showRefreshNotice();
			doReturn(true).when(sharing).confirm(anyString(), anyString(), anyBoolean());
			sharing.importConfiguration(code, 1);
		});
		SoundConfigurationCodec.decode(code).settings.forEach((key, value) ->
			verify(manager).setConfiguration(BeepDeepConfig.GROUP, key, value));
		verifyNoMoreInteractions(manager);
		org.mockito.InOrder order = inOrder(dialog, sharing);
		order.verify(dialog).dispose();
		order.verify(sharing).showRefreshNotice();
	}

	@Test
	public void keepsTypicalSetupWithinOneDiscordMessage()
	{
		BeepDeepConfig defaults = new BeepDeepConfig() {};
		assertTrue(SoundConfigurationCodec.encode(defaults).code.length() < 2000);
		BeepDeepConfig urls = mock(BeepDeepConfig.class);
		for (ToaEvent event : ToaEvent.values())
		{
			for (ToaEvent.SoundSlot slot : event.getSlots())
			{
				when(slot.source(urls)).thenReturn("https://example.com/sounds/" + event.getConfigPrefix() + ".wav");
			}
		}
		assertTrue(SoundConfigurationCodec.encode(urls).code.length() < 2000);
	}

	@Test
	public void recognizesAbsolutePathsOnAllPlatforms()
	{
		for (String source : new String[]{"C:\\sounds\\beep.wav", "d:/sounds/beep.wav",
			"\\\\server\\share\\beep.wav", "/home/player/beep.wav", "\\sounds\\beep.wav"})
		{
			assertTrue(source, SoundConfigurationCodec.isAbsolutePath(source));
		}
		for (String source : new String[]{"2192", "beep-deep/sounds/beep.wav", "https://example.com/beep.wav", ""})
		{
			assertFalse(source, SoundConfigurationCodec.isAbsolutePath(source));
		}
		BeepDeepConfig config = new BeepDeepConfig()
		{
			@Override
			public String raidEnterSound5()
			{
				return "C:\\sounds\\音.wav";
			}
		};
		SoundConfigurationCodec.Export exported = SoundConfigurationCodec.encode(config);
		assertTrue(exported.absolutePaths);
		assertTrue(SoundConfigurationCodec.decode(exported.code).absolutePaths);
	}

	@Test
	public void acceptsWhitespaceFromMessageWrapping()
	{
		String code = SoundConfigurationCodec.encode(new BeepDeepConfig() {}).code;
		String wrapped = " \n" + code.substring(0, 20) + "\n\t" + code.substring(20) + "\n";
		assertEquals(SoundConfigurationCodec.decode(code).settings, SoundConfigurationCodec.decode(wrapped).settings);
	}

	@Test
	public void rejectsMalformedAndIncompleteCodesWithoutWritingSettings() throws Exception
	{
		String code = SoundConfigurationCodec.encode(new BeepDeepConfig() {}).code;
		byte[] payload = payload(code);
		int toggleIndex = 3 + "raidEnter".length();
		byte[] invalidToggle = payload.clone();
		invalidToggle[toggleIndex] = 2;
		byte[] invalidSlots = payload.clone();
		invalidSlots[toggleIndex + 1] = 4;
		byte[] unknownEvent = payload.clone();
		unknownEvent[3] = 'X';
		byte[] invalidEventCount = payload.clone();
		invalidEventCount[0] = 101;
		byte[] missingEvents = payload.clone();
		missingEvents[0]--;
		byte[] invalidSlotVolume = payload.clone();
		// Two bytes for the first source length followed by the default sound ID.
		invalidSlotVolume[toggleIndex + 2 + 2 + "2192".length()] = 101;
		byte[] duplicateEvent = payload.clone();
		byte[] leaveKey = "raidLeave".getBytes(java.nio.charset.StandardCharsets.UTF_8);
		for (int i = 3; i < duplicateEvent.length - leaveKey.length; i++)
		{
			if (Arrays.equals(leaveKey, Arrays.copyOfRange(duplicateEvent, i, i + leaveKey.length)))
			{
				System.arraycopy("raidEnter".getBytes(java.nio.charset.StandardCharsets.UTF_8), 0,
					duplicateEvent, i, leaveKey.length);
				break;
			}
		}
		byte[] compressed = Base64.getUrlDecoder().decode(code.substring(SoundConfigurationCodec.PREFIX.length()));
		byte[] corrupted = compressed.clone();
		corrupted[corrupted.length - 1] ^= 1;
		String[] invalid = {null, "", "hello", "BD3:abc", SoundConfigurationCodec.PREFIX + "***", SoundConfigurationCodec.PREFIX + "A",
			code.substring(0, code.length() - 10), SoundConfigurationCodec.PREFIX + "a".repeat(1_000_000),
			compress(invalidToggle), compress(invalidSlots), compress(unknownEvent), compress(invalidEventCount),
			compress(missingEvents), compress(invalidSlotVolume), compress(duplicateEvent),
			compress(Arrays.copyOf(payload, payload.length - 1)),
			compress(Arrays.copyOf(payload, payload.length + 1)),
			SoundConfigurationCodec.PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(corrupted),
			SoundConfigurationCodec.PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(Arrays.copyOf(compressed, compressed.length + 1)),
			compress(new byte[2_000_001])};
		ConfigManager manager = mock(ConfigManager.class);
		SoundConfigurationSharing sharing = new SoundConfigurationSharing(
			new BeepDeepConfig() {}, manager);
		for (String input : invalid)
		{
			try
			{
				sharing.importCode(input, imported -> true);
				fail("Accepted invalid code: " + (input == null ? "null" : input.substring(0, Math.min(20, input.length()))));
			}
			catch (IllegalArgumentException e)
			{
				assertNotNull(e.getMessage());
				assertFalse(e.getMessage().isEmpty());
			}
		}
		verifyNoInteractions(manager);
	}

	@Test
	public void importWritesOnlySoundSettingsAndKeepsRemotePermission()
	{
		BeepDeepConfig config = new BeepDeepConfig() {};
		ConfigManager manager = mock(ConfigManager.class);
		SoundConfigurationSharing sharing = new SoundConfigurationSharing(config, manager);
		String code = SoundConfigurationCodec.encode(config).code;
		assertTrue(sharing.importCode(code, imported -> true));
		SoundConfigurationCodec.decode(code).settings.forEach((key, value) ->
			verify(manager).setConfiguration(BeepDeepConfig.GROUP, key, value));
		verifyNoMoreInteractions(manager);
		reset(manager);
		assertFalse(sharing.importCode(code, imported -> false));
		verifyNoInteractions(manager);
	}

	@Test
	public void configLauncherRepeatsAndOtherSettingsDoNotOpenDialog() throws Exception
	{
		ConfigManager manager = mock(ConfigManager.class);
		SoundConfigurationSharing sharing = spy(new SoundConfigurationSharing(
			new BeepDeepConfig() {}, manager));
		JCheckBox checkbox = new JCheckBox();
		doReturn(checkbox).when(sharing).configCheckbox("Open sharing dialog");
		doNothing().when(sharing).openDialog(anyInt());
		sharing.startUp();
		verify(manager).setConfiguration(BeepDeepConfig.GROUP, "openSoundSharingDialog", false);
		clearInvocations(manager);
		checkbox.setSelected(true);
		sharing.onConfigChanged(changed(BeepDeepConfig.GROUP, "openSoundSharingDialog", "false", "true"));
		sharing.onConfigChanged(changed(BeepDeepConfig.GROUP, "openSoundSharingDialog", "true", "false"));
		sharing.onConfigChanged(changed(BeepDeepConfig.GROUP, "openSoundSharingDialog", "false", "true"));
		sharing.onConfigChanged(changed(BeepDeepConfig.GROUP, "soundConfigurationCode", "", "BD120:example"));
		sharing.onConfigChanged(changed(BeepDeepConfig.GROUP, "copySoundConfiguration", "false", "true"));
		sharing.onConfigChanged(changed(BeepDeepConfig.GROUP, "importSoundConfiguration", "false", "true"));
		sharing.onConfigChanged(changed("other-plugin", "openSoundSharingDialog", "false", "true"));
		sharing.onConfigChanged(changed(BeepDeepConfig.GROUP, "openSoundSharingDialog", null, "false"));
		sharing.onConfigChanged(changed(BeepDeepConfig.GROUP, "openSoundSharingDialog", "false", "false"));
		SwingUtilities.invokeAndWait(() -> {});
		verify(sharing, times(2)).openDialog(anyInt());
		assertFalse(checkbox.isSelected());
		verify(manager, times(2)).setConfiguration(BeepDeepConfig.GROUP, null, "openSoundSharingDialog", false);
		verifyNoMoreInteractions(manager);
	}

	@Test
	public void remoteUrlsWarnOnlyOnEnableAndCancelKeepsThemDisabled() throws Exception
	{
		ConfigManager manager = mock(ConfigManager.class);
		SoundConfigurationSharing sharing = spy(new SoundConfigurationSharing(new BeepDeepConfig() {}, manager));
		JCheckBox checkbox = new JCheckBox();
		doReturn(checkbox).when(sharing).configCheckbox("Allow remote URLs");
		doReturn(false).when(sharing).confirm(anyString(), anyString(), anyBoolean());
		// Simulate ConfigManager's synchronous event delivery, including our own writes.
		doAnswer(invocation ->
		{
			boolean allowed = invocation.getArgument(3);
			sharing.onConfigChanged(changed(BeepDeepConfig.GROUP, "enableRemoteUrls",
				Boolean.toString(!allowed), Boolean.toString(allowed)));
			return null;
		}).when(manager).setConfiguration(eq(BeepDeepConfig.GROUP), isNull(), eq("enableRemoteUrls"), any(Boolean.class));
		sharing.startUp();
		clearInvocations(manager);
		sharing.onConfigChanged(changed(BeepDeepConfig.GROUP, "enableRemoteUrls", "true", "false"));
		SwingUtilities.invokeAndWait(() -> {});
		verify(sharing, never()).confirm(anyString(), anyString(), anyBoolean());
		verifyNoInteractions(manager);

		SwingUtilities.invokeAndWait(() ->
		{
			checkbox.setSelected(true);
			sharing.onConfigChanged(changed(BeepDeepConfig.GROUP, "enableRemoteUrls", "false", "true"));
		});
		SwingUtilities.invokeAndWait(() -> {});
		assertFalse(checkbox.isSelected());
		verify(sharing).confirm(contains(BeepDeepConfig.REMOTE_WARNING), eq("Allow remote URLs"), eq(true));
		verify(manager).setConfiguration(BeepDeepConfig.GROUP, null, "enableRemoteUrls", false);
		verify(manager, never()).setConfiguration(BeepDeepConfig.GROUP, null, "enableRemoteUrls", true);
		verifyNoMoreInteractions(manager);
	}

	@Test
	public void acceptingRemoteWarningEnablesPermissionAndVisibleCheckbox() throws Exception
	{
		ConfigManager manager = mock(ConfigManager.class);
		SoundConfigurationSharing sharing = spy(new SoundConfigurationSharing(new BeepDeepConfig() {}, manager));
		JCheckBox checkbox = new JCheckBox();
		doReturn(checkbox).when(sharing).configCheckbox("Allow remote URLs");
		doAnswer(invocation ->
		{
			verify(manager).setConfiguration(BeepDeepConfig.GROUP, null, "enableRemoteUrls", false);
			assertFalse(checkbox.isSelected());
			return true;
		}).when(sharing).confirm(anyString(), anyString(), anyBoolean());
		sharing.startUp();
		clearInvocations(manager);
		SwingUtilities.invokeAndWait(() ->
		{
			checkbox.setSelected(true);
			sharing.onConfigChanged(changed(BeepDeepConfig.GROUP, "enableRemoteUrls", "false", "true"));
		});
		SwingUtilities.invokeAndWait(() -> {});
		assertTrue(checkbox.isSelected());
		verify(manager).setConfiguration(BeepDeepConfig.GROUP, null, "enableRemoteUrls", true);
		verify(sharing, times(1)).confirm(anyString(), anyString(), anyBoolean());
	}

	@Test
	public void shutdownOrDisableDiscardsPendingRemoteWarning() throws Exception
	{
		for (boolean shutdown : new boolean[]{false, true})
		{
			SoundConfigurationSharing sharing = spy(new SoundConfigurationSharing(
				new BeepDeepConfig() {}, mock(ConfigManager.class)));
			doReturn(true).when(sharing).confirm(anyString(), anyString(), anyBoolean());
			sharing.startUp();
			SwingUtilities.invokeAndWait(() ->
			{
				sharing.onConfigChanged(changed(BeepDeepConfig.GROUP, "enableRemoteUrls", "false", "true"));
				if (shutdown)
				{
					sharing.shutDown();
				}
				else
				{
					sharing.onConfigChanged(changed(BeepDeepConfig.GROUP, "enableRemoteUrls", "true", "false"));
				}
			});
			SwingUtilities.invokeAndWait(() -> {});
			verify(sharing, never()).confirm(anyString(), anyString(), anyBoolean());
		}
	}

	@Test
	public void checkboxLookupRequiresMatchingConfigLabel()
	{
		JPanel row = new JPanel();
		JCheckBox checkbox = new JCheckBox();
		row.add(new JLabel("Open sharing dialog"));
		row.add(checkbox);
		assertSame(checkbox, SoundConfigurationSharing.labeledCheckbox(checkbox, "Open sharing dialog"));
		assertNull(SoundConfigurationSharing.labeledCheckbox(checkbox, "Allow remote URLs"));
		assertNull(SoundConfigurationSharing.labeledCheckbox(new JButton(), "Open sharing dialog"));
	}

	@Test
	public void dialogButtonsCopyAndImportCurrentTextWithoutToggleState() throws Exception
	{
		SoundConfigurationSharing sharing = spy(new SoundConfigurationSharing(
			new BeepDeepConfig() {}, mock(ConfigManager.class)));
		doNothing().when(sharing).copyConfiguration(anyInt());
		doNothing().when(sharing).importConfiguration(anyString(), anyInt());
		sharing.startUp();
		SwingUtilities.invokeAndWait(() ->
		{
			JPanel content = sharing.createDialogContent(1);
			JTextArea code = (JTextArea) ((JScrollPane) ((BorderLayout) content.getLayout())
				.getLayoutComponent(BorderLayout.CENTER)).getViewport().getView();
			JPanel footer = (JPanel) ((BorderLayout) content.getLayout()).getLayoutComponent(BorderLayout.SOUTH);
			JPanel actions = (JPanel) ((BorderLayout) footer.getLayout()).getLayoutComponent(BorderLayout.NORTH);
			JButton copy = (JButton) actions.getComponent(0);
			JButton importButton = (JButton) actions.getComponent(1);
			assertEquals("Copy configuration", copy.getText());
			assertEquals("Import configuration", importButton.getText());
			code.setText("BD120:example");
			verify(sharing, never()).importConfiguration(anyString(), anyInt());
			copy.doClick(0);
			copy.doClick(0);
			importButton.doClick(0);
			code.setText("BD120:replacement");
			importButton.doClick(0);
		});
		verify(sharing, times(2)).copyConfiguration(1);
		verify(sharing).importConfiguration("BD120:example", 1);
		verify(sharing).importConfiguration("BD120:replacement", 1);
	}

	@Test
	public void discardsQueuedActionsAfterShutdownAndRestart() throws Exception
	{
		SoundConfigurationSharing sharing = spy(new SoundConfigurationSharing(
			new BeepDeepConfig() {}, mock(ConfigManager.class)));
		doNothing().when(sharing).openDialog(anyInt());
		sharing.startUp();
		SwingUtilities.invokeAndWait(() ->
		{
			sharing.onConfigChanged(changed(BeepDeepConfig.GROUP, "openSoundSharingDialog", "false", "true"));
			sharing.shutDown();
			sharing.onConfigChanged(changed(BeepDeepConfig.GROUP, "openSoundSharingDialog", "true", "false"));
			sharing.startUp();
		});
		SwingUtilities.invokeAndWait(() -> {});
		verify(sharing, never()).openDialog(anyInt());
	}

	private static ConfigChanged changed(String group, String key, String oldValue, String newValue)
	{
		ConfigChanged event = new ConfigChanged();
		event.setGroup(group);
		event.setKey(key);
		event.setOldValue(oldValue);
		event.setNewValue(newValue);
		return event;
	}

	@Test
	public void rejectsInvalidSourcesOnExport()
	{
		for (String source : new String[]{"a".repeat(4097), "bad\npath.wav"})
		{
			BeepDeepConfig config = new BeepDeepConfig()
			{
				@Override
				public String raidEnterSound1()
				{
					return source;
				}
			};
			try
			{
				SoundConfigurationCodec.encode(config);
				fail("Invalid source was exported");
			}
			catch (IllegalArgumentException e)
			{
				assertTrue(e.getMessage().startsWith("A sound link or path"));
			}
		}
	}

	private static byte[] payload(String code) throws Exception
	{
		try (InflaterInputStream in = new InflaterInputStream(new ByteArrayInputStream(
			Base64.getUrlDecoder().decode(code.substring(SoundConfigurationCodec.PREFIX.length())))))
		{
			return in.readAllBytes();
		}
	}

	private static String compress(byte[] payload) throws Exception
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (DeflaterOutputStream deflater = new DeflaterOutputStream(out))
		{
			deflater.write(payload);
		}
		return SoundConfigurationCodec.PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(out.toByteArray());
	}
}
