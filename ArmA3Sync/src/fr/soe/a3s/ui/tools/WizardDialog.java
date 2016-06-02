package fr.soe.a3s.ui.tools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.soe.a3s.service.AddonService;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.service.ProfileService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ImagePanel;
import fr.soe.a3s.ui.UIConstants;

public abstract class WizardDialog extends JDialog implements UIConstants {

	protected Facade facade;
	protected String title;
	protected String description;
	protected Image image;
	/* ArmA 3 */
	protected JTextField textFieldArmA3InstallationDirectory;
	protected JButton buttonSelectArmA3InstallationDirectory;
	/* TS3 */
	protected JTextField textFieldTS3InstallationDirectory;
	protected JButton buttonSelectTS3InstallationDirectory;
	/* Addon Plugin */
	protected JTextField textFieldPluginInstallationDirectory;
	protected JButton buttonSelectPluginInstallationDirectory;
	/* Addon userconfig */
	protected JTextField textFieldUserconfigInstallationDirectory;
	protected JButton buttonSelectUserconfigInstallationDirectory;
	/* System specs view */
	protected JLabel labelOS;
	protected JLabel labelOSValue;
	protected JLabel labelTS3;
	protected JLabel labelTS3Value;
	protected JLabel labelPlugin;
	protected JLabel labelPluginValue;
	protected ImagePanel imagePanel;
	protected JLabel labelDescription;
	protected JButton buttonFist, buttonSecond;
	/* Services */
	protected final ConfigurationService configurationService = new ConfigurationService();
	protected final AddonService addonService = new AddonService();
	protected final ProfileService profileService = new ProfileService();

	public WizardDialog(Facade facade, String title, String description,
			Image image) {
		super(facade.getMainPanel(), title, true);

		this.facade = facade;
		this.title = title;
		this.description = description;
		this.image = image;
		setIconImage(ICON);

		this.setLayout(new BorderLayout());
		{
			JPanel topPanel = new JPanel();
			topPanel.setLayout(new BorderLayout());
			if (image != null) {
				{
					imagePanel = new ImagePanel();
					imagePanel.setBackground(new java.awt.Color(255, 255, 255));
					imagePanel.setImage(image);
					topPanel.add(imagePanel);
					imagePanel.repaint();
					topPanel.add(imagePanel, BorderLayout.WEST);
				}
			}
			{
				Box hBox = Box.createHorizontalBox();
				hBox.add(Box.createHorizontalStrut(10));
				labelDescription = new JLabel();
				labelDescription.setText(description);
				hBox.add(labelDescription);
				topPanel.add(hBox, BorderLayout.CENTER);
			}
			topPanel.setBackground(new java.awt.Color(255, 255, 255));
			this.add(topPanel, BorderLayout.NORTH);
		}
		{
			JPanel controlPanel = new JPanel();
			buttonFist = new JButton();
			buttonSecond = new JButton();
			FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
			controlPanel.setLayout(flowLayout);
			controlPanel.add(buttonFist);
			controlPanel.add(buttonSecond);
			this.add(controlPanel, BorderLayout.SOUTH);
		}

		buttonFist.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonFistPerformed();
			}
		});
		buttonSecond.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonSecondPerformed();
			}
		});
		// Add Listeners
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				menuExitPerformed();
			}
		});
	}

	public abstract void buttonFistPerformed();

	public abstract void buttonSecondPerformed();

	private void menuExitPerformed() {
		this.dispose();
	}

	protected void determineOS() {

		/* OS */
		String osName = System.getProperty("os.name");
		boolean is64bit = false;
		if (System.getProperty("os.name").contains("Windows")) {
			is64bit = (System.getenv("ProgramFiles(x86)") != null);
		} else {
			is64bit = (System.getProperty("os.arch").indexOf("64") != -1);
		}
		if (is64bit) {
			labelOSValue.setText(osName + " - 64 bit");
		} else {
			labelOSValue.setText(osName + " - 32 bit");
		}
	}

	protected void determineTS3() {

		/* TS3 version */
		boolean is64bit = false;
		boolean is32bit = false;
		if (textFieldTS3InstallationDirectory.getText().isEmpty()) {
			labelTS3Value.setText("Unknown");
		} else {
			String ts3Version = configurationService
					.getTS3version(textFieldTS3InstallationDirectory.getText());
			if (ts3Version == null) {
				ts3Version = "Unknown";
			}
			is64bit = configurationService
					.isTS364bit(textFieldTS3InstallationDirectory.getText());

			is32bit = configurationService
					.isTS332bit(textFieldTS3InstallationDirectory.getText());

			if (is64bit) {
				labelTS3Value.setText(ts3Version + " - 64 bit");
			} else if (is32bit) {
				labelTS3Value.setText(ts3Version + " - 32 bit");
			} else {
				labelTS3Value.setText("Unknown");
			}
		}
	}
}
