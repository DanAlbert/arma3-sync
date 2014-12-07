package fr.soe.a3s.ui.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.soe.a3s.service.AddonService;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

public abstract class WizardPanel extends JDialog implements UIConstants {

	protected Facade facade;
	protected String title;
	protected String description;
	protected ImageIcon imageIcon;
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
	/* Services */
	protected final ConfigurationService configurationService = new ConfigurationService();
	protected final AddonService addonService = new AddonService();

	protected JButton buttonFist, buttonSecond;

	public WizardPanel(Facade facade, String title, String description,
			ImageIcon imageIcon) {
		super(facade.getMainPanel(), title, false);

		this.facade = facade;
		this.title = title;
		this.description = description;
		this.imageIcon = imageIcon;

		this.setMinimumSize(new Dimension(DEFAULT_WIDTH, 500));
		setIconImage(ICON);
		this.setLocation(
				(int) facade.getMainPanel().getLocation().getX()
						+ facade.getMainPanel().getWidth() / 2
						- this.getWidth() / 2,
				(int) facade.getMainPanel().getLocation().getY()
						+ facade.getMainPanel().getHeight() / 2
						- this.getHeight() / 2);
		this.setLayout(new BorderLayout());
		{
			JPanel topPanel = new JPanel();
			this.add(topPanel, BorderLayout.NORTH);
			topPanel.setLayout(null);
			topPanel.setBackground(new java.awt.Color(255, 255, 255));
			topPanel.setPreferredSize(new java.awt.Dimension(484, 55));
			if (imageIcon != null) {
				{
					imagePanel = new ImagePanel();
					imagePanel.setBackground(new java.awt.Color(255, 255, 255));
					Image myNewImage = imageIcon.getImage();
					imagePanel.setImage(myNewImage);
					topPanel.add(imagePanel);
					imagePanel.setBounds(6, 6, 28, 42);
					imagePanel.repaint();
				}
			}
			{
				labelDescription = new JLabel();
				labelDescription.setText(description);
				topPanel.add(labelDescription);
				labelDescription.setBounds(40, 6, 300, 42);
			}
		}
		{
			JPanel controlPanel = new JPanel();
			buttonFist = new JButton();
			buttonFist.setPreferredSize(new Dimension(80, 25));
			buttonSecond = new JButton();
			buttonSecond.setPreferredSize(new Dimension(80, 25));
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

	protected class ImagePanel extends JPanel {

		private Image image = null;

		public void setImage(Image image) {
			this.image = image;
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g); // paint background
			if (image != null) { // there is a picture: draw it
				int height = this.getSize().height;
				int width = this.getSize().width;
				// g.drawImage(image, 0, 0, this); //use image size
				g.drawImage(image, 0, 0, width, height, this);
			}
		}
	}
}
