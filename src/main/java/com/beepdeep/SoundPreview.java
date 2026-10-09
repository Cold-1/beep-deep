package com.beepdeep;

import java.awt.Dimension;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;

@Singleton
class SoundPreview
{
	private final BeepDeepConfig config;
	private final ConfigManager configManager;
	private final SoundManager sounds;
	private final ClientThread clientThread;
	private final SoundConfigurationSharing sharing;
	private volatile Object session;
	// Dialog state is accessed only on the EDT.
	private JDialog feedback;
	private Object feedbackSession;

	@Inject
	SoundPreview(BeepDeepConfig config, ConfigManager configManager, SoundManager sounds,
		ClientThread clientThread, SoundConfigurationSharing sharing)
	{
		this.config = config;
		this.configManager = configManager;
		this.sounds = sounds;
		this.clientThread = clientThread;
		this.sharing = sharing;
	}

	void startUp()
	{
		configManager.setConfiguration(BeepDeepConfig.GROUP, "playTestSound", false);
		session = new Object();
	}

	void shutDown()
	{
		Object stopped = session;
		session = null;
		SwingUtilities.invokeLater(() ->
		{
			if (feedback != null && feedbackSession == stopped)
			{
				feedback.dispose();
				feedback = null;
			}
		});
	}

	void onConfigChanged(ConfigChanged event)
	{
		Object requestedSession = session;
		if (requestedSession == null || !BeepDeepConfig.GROUP.equals(event.getGroup())
			|| !"playTestSound".equals(event.getKey()) || !"true".equals(event.getNewValue())
			|| Objects.equals(event.getOldValue(), event.getNewValue()))
		{
			return;
		}

		ToaEvent selectedEvent = config.soundTestEvent();
		SoundTestSlot selectedSlot = config.soundTestSlot();
		JCheckBox checkbox = sharing.configCheckbox("Play sound");
		configManager.setConfiguration(BeepDeepConfig.GROUP, event.getProfile(), "playTestSound", false);
		// ConfigPanel does not refresh an existing checkbox when its saved value changes.
		SwingUtilities.invokeLater(() ->
		{
			if (checkbox != null)
			{
				checkbox.setSelected(false);
			}
		});
		clientThread.invoke(() ->
		{
			if (session == requestedSession)
			{
				sounds.preview(selectedEvent, selectedSlot, message -> SwingUtilities.invokeLater(() ->
				{
					if (session == requestedSession)
					{
						showError(message, requestedSession);
					}
				}));
			}
		});
	}

	void showError(String message, Object requestedSession)
	{
		if (feedback != null)
		{
			feedback.dispose();
		}
		JTextArea text = new JTextArea(message);
		text.setColumns(42);
		text.setEditable(false);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		text.setOpaque(false);
		Dimension initial = text.getPreferredSize();
		text.setSize(initial.width, Short.MAX_VALUE);
		text.setPreferredSize(new Dimension(initial.width, text.getPreferredSize().height + 16));
		JOptionPane pane = new JOptionPane(text, JOptionPane.WARNING_MESSAGE);
		feedback = pane.createDialog(null, "Beep Deep sound test");
		feedbackSession = requestedSession;
		feedback.setModal(false);
		feedback.setVisible(true);
	}
}
