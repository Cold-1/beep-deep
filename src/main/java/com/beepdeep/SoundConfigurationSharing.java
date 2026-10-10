package com.beepdeep;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;

@Singleton
class SoundConfigurationSharing
{
	private static final String PATH_WARNING =
		"Contains absolute file paths. Those files may not exist on another client; replace them with shared URLs or install the files and update the paths.";
	private final BeepDeepConfig config;
	private final ConfigManager configManager;
	private final AtomicInteger generation = new AtomicInteger();
	private final AtomicInteger remoteRequest = new AtomicInteger();
	private final ThreadLocal<Boolean> changingRemotePermission = ThreadLocal.withInitial(() -> false);
	private volatile boolean active;
	// Dialog state is accessed only on the EDT.
	private JDialog dialog;
	private DialogContent content;
	private int dialogSession;

	@Inject
	SoundConfigurationSharing(BeepDeepConfig config, ConfigManager configManager)
	{
		this.config = config;
		this.configManager = configManager;
	}

	void startUp()
	{
		generation.incrementAndGet();
		configManager.setConfiguration(BeepDeepConfig.GROUP, "openSoundSharingDialog", false);
		active = true;
	}

	void shutDown()
	{
		active = false;
		int stoppedSession = generation.incrementAndGet();
		SwingUtilities.invokeLater(() ->
		{
			if (dialog != null && dialogSession < stoppedSession)
			{
				dialog.dispose();
				dialog = null;
				content = null;
			}
		});
	}

	void onConfigChanged(ConfigChanged event)
	{
		if (!active || !BeepDeepConfig.GROUP.equals(event.getGroup())
			|| Objects.equals(event.getNewValue(), event.getOldValue()))
		{
			return;
		}

		int session = generation.get();
		if ("enableRemoteUrls".equals(event.getKey()))
		{
			if (changingRemotePermission.get())
			{
				return;
			}
			int request = remoteRequest.incrementAndGet();
			if (!"true".equals(event.getNewValue()))
			{
				return;
			}
			JCheckBox checkbox = configCheckbox("Allow remote URLs");
			// Keep network access disabled while the user considers the warning.
			setRemotePermission(event, false);
			SwingUtilities.invokeLater(() ->
			{
				if (!isActive(session) || remoteRequest.get() != request)
				{
					return;
				}
				if (checkbox != null)
				{
					checkbox.setSelected(false);
				}
				boolean allowed = confirm(BeepDeepConfig.REMOTE_WARNING
					+ "\n\nAllow sounds to be downloaded from remote URLs?", "Allow remote URLs", true);
				if (isActive(session) && remoteRequest.get() == request && allowed)
				{
					setRemotePermission(event, true);
					if (checkbox != null)
					{
						checkbox.setSelected(true);
					}
				}
			});
			return;
		}
		if (!"openSoundSharingDialog".equals(event.getKey()) || !"true".equals(event.getNewValue()))
		{
			return;
		}

		JCheckBox checkbox = configCheckbox("Open sharing dialog");
		configManager.setConfiguration(BeepDeepConfig.GROUP, event.getProfile(), "openSoundSharingDialog", false);
		SwingUtilities.invokeLater(() ->
		{
			if (checkbox != null)
			{
				checkbox.setSelected(false);
			}
			if (isActive(session))
			{
				openDialog(session);
			}
		});
	}

	private void setRemotePermission(ConfigChanged event, boolean allowed)
	{
		changingRemotePermission.set(true);
		try
		{
			configManager.setConfiguration(BeepDeepConfig.GROUP, event.getProfile(), "enableRemoteUrls", allowed);
		}
		finally
		{
			changingRemotePermission.remove();
		}
	}

	JCheckBox configCheckbox(String name)
	{
		if (!SwingUtilities.isEventDispatchThread())
		{
			return null;
		}
		// ConfigPanel doesn't refresh existing checkbox widgets after ConfigChanged.
		// Only adjust the initiating control, identified by its adjacent config label.
		AWTEvent current = EventQueue.getCurrentEvent();
		Component source = current != null && current.getSource() instanceof Component
			? (Component) current.getSource() : null;
		return labeledCheckbox(source, name);
	}

	static JCheckBox labeledCheckbox(Component component, String name)
	{
		if (!(component instanceof JCheckBox) || component.getParent() == null)
		{
			return null;
		}
		for (Component sibling : component.getParent().getComponents())
		{
			if (sibling instanceof JLabel && name.equals(((JLabel) sibling).getText()))
			{
				return (JCheckBox) component;
			}
		}
		return null;
	}

	private boolean isActive(int session)
	{
		return active && generation.get() == session;
	}

	void openDialog(int session)
	{
		if (!isActive(session))
		{
			return;
		}
		if (dialog != null && dialog.isDisplayable() && dialogSession == session)
		{
			dialog.setVisible(true);
			dialog.toFront();
			dialog.requestFocus();
			return;
		}
		if (dialog != null)
		{
			dialog.dispose();
		}
		JDialog opened = createDialog();
		dialog = opened;
		dialogSession = session;
		opened.setContentPane(createDialogContent(session));
		opened.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		opened.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent event)
			{
				saveDraft(session);
			}

			@Override
			public void windowClosed(WindowEvent event)
			{
				if (dialog == opened)
				{
					dialog = null;
					content = null;
				}
			}
		});
		opened.pack();
		opened.setMinimumSize(new Dimension(470, 300));
		opened.setLocationRelativeTo(null);
		opened.setVisible(true);
	}

	private void saveDraft(int session)
	{
		if (isActive(session) && content != null && dialogSession == session)
		{
			configManager.setConfiguration(BeepDeepConfig.GROUP, "soundConfigurationCode", content.code.getText());
		}
	}

	/** Only a fully decoded, whitelisted configuration can reach the settings writer. */
	boolean importCode(String code, Predicate<SoundConfigurationCodec.Imported> confirm)
	{
		SoundConfigurationCodec.Imported imported = SoundConfigurationCodec.decode(code);
		if (!confirm.test(imported))
		{
			return false;
		}
		imported.settings.forEach((key, value) -> configManager.setConfiguration(BeepDeepConfig.GROUP, key, value));
		return true;
	}

	void copyConfiguration(int session)
	{
		if (!isActive(session))
		{
			return;
		}
		try
		{
			SoundConfigurationCodec.Export exported = SoundConfigurationCodec.encode(config);
			if (exported.absolutePaths && !confirm(PATH_WARNING + "\n\nCopy anyway?", "Local file paths", true))
			{
				return;
			}
			if (!isActive(session) || dialog == null || !dialog.isDisplayable())
			{
				return;
			}
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(exported.code), null);
			content.code.setText(exported.code);
			content.code.selectAll();
			setStatus("Copied " + exported.code.length() + " characters."
				+ (exported.code.length() > 2000
					? "\nThis exceeds a typical Discord message. Share it as a text attachment or in multiple parts; import the complete code."
					: "\nShare this code with your party.")
				+ (exported.absolutePaths ? "\n" + PATH_WARNING : ""));
		}
		catch (IllegalArgumentException e)
		{
			setStatus("Copy failed: " + e.getMessage());
		}
		catch (IllegalStateException | HeadlessException | SecurityException e)
		{
			setStatus("Could not access the clipboard. Try again when the clipboard is available.");
		}
	}

	void importConfiguration(String pastedCode, int session)
	{
		if (!isActive(session))
		{
			return;
		}
		try
		{
			boolean applied = importCode(pastedCode, imported ->
			{
				String message = "Replace all event toggles, sound slots and slot volumes?\n"
					+ "Your master volume and remote URL permission stay unchanged."
					+ (imported.absolutePaths ? "\n\n" + PATH_WARNING : "");
				return confirm(message, "Import sound configuration", imported.absolutePaths)
					&& isActive(session) && dialog != null && dialog.isDisplayable();
			});
			if (applied)
			{
				closeDialog();
				showRefreshNotice();
			}
		}
		catch (IllegalArgumentException e)
		{
			setStatus("Import failed: " + e.getMessage() + "\nYour sound settings were not changed.");
		}
	}

	boolean confirm(String message, String title, boolean warning)
	{
		JTextArea text = new JTextArea(message);
		text.setColumns(42);
		text.setEditable(false);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		text.setOpaque(false);
		// Give wrapped text its full height before packing the option pane.
		Dimension initial = text.getPreferredSize();
		text.setSize(initial.width, Short.MAX_VALUE);
		Dimension wrapped = text.getPreferredSize();
		text.setPreferredSize(new Dimension(initial.width, wrapped.height + 16));
		JOptionPane pane = new JOptionPane(text,
			warning ? JOptionPane.WARNING_MESSAGE : JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		JDialog confirmation = pane.createDialog(dialog, title);
		try
		{
			confirmation.pack();
			confirmation.setMinimumSize(new Dimension(confirmation.getWidth(),
				Math.max(240, confirmation.getHeight() + 24)));
			confirmation.setSize(confirmation.getMinimumSize());
			confirmation.setLocationRelativeTo(dialog);
			confirmation.setVisible(true);
			return Integer.valueOf(JOptionPane.OK_OPTION).equals(pane.getValue());
		}
		finally
		{
			confirmation.dispose();
		}
	}

	JDialog createDialog()
	{
		return new JDialog((Frame) null, "Beep Deep sound sharing", false);
	}

	private void closeDialog()
	{
		if (dialog != null)
		{
			dialog.dispose();
			dialog = null;
			content = null;
		}
	}

	void showRefreshNotice()
	{
		JOptionPane.showMessageDialog(null,
			"Sound configuration imported.\n\nClose and reopen the Beep Deep configuration panel to refresh the displayed settings."
				+ "\n\nYour master volume and Allow remote URLs setting were kept.",
			"Configuration imported", JOptionPane.INFORMATION_MESSAGE);
	}

	private void setStatus(String message)
	{
		if (content != null)
		{
			content.status.setText(message);
			content.status.setCaretPosition(0);
		}
	}

	JPanel createDialogContent(int session)
	{
		content = new DialogContent(session);
		return content;
	}

	private final class DialogContent extends JPanel
	{
		private final JTextArea code = new JTextArea(6, 42);
		private final JTextArea status = new JTextArea(4, 42);

		private DialogContent(int session)
		{
			super(new BorderLayout(0, 8));
			setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
			add(new JLabel("Copy your setup, or paste a Beep Deep code and click Import."), BorderLayout.NORTH);
			code.setText(config.soundConfigurationCode());
			code.setLineWrap(true);
			code.getAccessibleContext().setAccessibleName("Sound configuration code");
			add(new JScrollPane(code), BorderLayout.CENTER);

			JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
			JButton copy = new JButton("Copy configuration");
			copy.addActionListener(event -> copyConfiguration(session));
			actions.add(copy);
			JButton importButton = new JButton("Import configuration");
			importButton.addActionListener(event ->
			{
				saveDraft(session);
				importConfiguration(code.getText(), session);
			});
			actions.add(importButton);
			JButton close = new JButton("Close");
			close.addActionListener(event ->
			{
				saveDraft(session);
				if (dialog != null && dialogSession == session)
				{
					dialog.dispose();
				}
			});
			actions.add(close);
			status.setEditable(false);
			status.setLineWrap(true);
			status.setWrapStyleWord(true);
			status.setOpaque(false);
			status.getAccessibleContext().setAccessibleName("Sharing result");
			JPanel footer = new JPanel(new BorderLayout(0, 8));
			footer.add(actions, BorderLayout.NORTH);
			footer.add(new JScrollPane(status), BorderLayout.CENTER);
			add(footer, BorderLayout.SOUTH);
		}
	}
}
