package fr.soe.a3s.ui.tools.acre2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.service.LaunchService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.tools.WizardDialog;

public class FirstPageACRE2InstallerDialog extends WizardDialog {

	public FirstPageACRE2InstallerDialog(Facade facade) {
		super(facade, "ACRE 2 installer wizard",
				"Install or update ACRE 2 for ArmA 3 and TS3", ACRE2_BIG);

		buttonFist.setText("Proceed");
		buttonSecond.setText("Cancel");
		getRootPane().setDefaultButton(buttonFist);
		{
			JPanel centerPanel = new JPanel();
			this.add(centerPanel, BorderLayout.CENTER);
			centerPanel.setBorder(BorderFactory
					.createEtchedBorder(BevelBorder.LOWERED));
			centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
			{
				JPanel locationsPanel = new JPanel();
				locationsPanel.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(),
						"Installation directories"));
				locationsPanel.setLayout(new BorderLayout());
				centerPanel.add(locationsPanel);
				Box vBox = Box.createVerticalBox();
				locationsPanel.add(vBox, BorderLayout.NORTH);
				{
					JPanel ts3InstallationDirectoryLabelPanel = new JPanel();
					ts3InstallationDirectoryLabelPanel
							.setLayout(new FlowLayout(FlowLayout.LEFT));
					JLabel ts3InstallationDirectoryLabel = new JLabel("TS3");
					ts3InstallationDirectoryLabelPanel
							.add(ts3InstallationDirectoryLabel);
					vBox.add(ts3InstallationDirectoryLabelPanel);
				}
				{
					JPanel ts3InstallationDirectoryPanel = new JPanel();
					ts3InstallationDirectoryPanel.setLayout(new BorderLayout());
					textFieldTS3InstallationDirectory = new JTextField();
					buttonSelectTS3InstallationDirectory = new JButton("Select");
					textFieldTS3InstallationDirectory.setEditable(false);
					textFieldTS3InstallationDirectory
							.setBackground(Color.WHITE);
					ts3InstallationDirectoryPanel.add(
							textFieldTS3InstallationDirectory,
							BorderLayout.CENTER);
					ts3InstallationDirectoryPanel.add(
							buttonSelectTS3InstallationDirectory,
							BorderLayout.EAST);
					vBox.add(ts3InstallationDirectoryPanel);
				}
				vBox.add(Box.createVerticalStrut(5));
				{
					JPanel acrePluginInstallationDirectoryLabelPanel = new JPanel();
					acrePluginInstallationDirectoryLabelPanel
							.setLayout(new FlowLayout(FlowLayout.LEFT));
					JLabel acreInstallationDirectoryLabel = new JLabel(
							"@ACRE2\\plugin");
					acrePluginInstallationDirectoryLabelPanel
							.add(acreInstallationDirectoryLabel);
					vBox.add(acrePluginInstallationDirectoryLabelPanel);
				}
				{
					JPanel acreInstallationDirectoryPanel = new JPanel();
					acreInstallationDirectoryPanel
							.setLayout(new BorderLayout());
					textFieldPluginInstallationDirectory = new JTextField();
					buttonSelectPluginInstallationDirectory = new JButton(
							"Select");
					textFieldPluginInstallationDirectory.setEditable(false);
					textFieldPluginInstallationDirectory
							.setBackground(Color.WHITE);
					acreInstallationDirectoryPanel.add(
							textFieldPluginInstallationDirectory,
							BorderLayout.CENTER);
					acreInstallationDirectoryPanel.add(
							buttonSelectPluginInstallationDirectory,
							BorderLayout.EAST);
					vBox.add(acreInstallationDirectoryPanel);
				}
				vBox.add(Box.createVerticalStrut(20));
			}
			{
				JPanel systemDescriptionPanel = new JPanel();
				systemDescriptionPanel.setLayout(new BorderLayout());
				systemDescriptionPanel.setBorder(BorderFactory
						.createTitledBorder(BorderFactory.createEtchedBorder(),
								"System specifications"));
				centerPanel.add(systemDescriptionPanel);
				Box vBox = Box.createVerticalBox();
				systemDescriptionPanel.add(vBox, BorderLayout.WEST);
				vBox.add(Box.createVerticalStrut(10));
				{
					labelOS = new JLabel("OS: ");
					labelOSValue = new JLabel();
					Box hBox = Box.createHorizontalBox();
					hBox.add(labelOS);
					hBox.add(labelOSValue);
					hBox.add(Box.createHorizontalGlue());
					vBox.add(hBox);
					vBox.add(Box.createVerticalStrut(10));
				}
				{
					labelTS3 = new JLabel("TS3: ");
					labelTS3Value = new JLabel();
					Box hBox = Box.createHorizontalBox();
					hBox.add(labelTS3);
					hBox.add(labelTS3Value);
					hBox.add(Box.createHorizontalGlue());
					vBox.add(hBox);
					vBox.add(Box.createVerticalStrut(10));
				}
				{
					labelPlugin = new JLabel("ACRE 2 plugin: ");
					labelPluginValue = new JLabel();
					Box hBox = Box.createHorizontalBox();
					hBox.add(labelPlugin);
					hBox.add(labelPluginValue);
					hBox.add(Box.createHorizontalGlue());
					vBox.add(hBox);
					vBox.add(Box.createVerticalStrut(10));
				}
				vBox.add(Box.createVerticalStrut(10));
			}
		}

		this.pack();
		this.setMinimumSize(new Dimension(
				facade.getMainPanel().getBounds().width,
				this.getBounds().height));
		this.setPreferredSize(new Dimension(
				facade.getMainPanel().getBounds().width,
				this.getBounds().height));
		this.pack();

		this.setLocationRelativeTo(facade.getMainPanel());

		buttonSelectTS3InstallationDirectory
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						buttonSelectTS3InstallationDirectoryPerformed();
					}
				});
		buttonSelectPluginInstallationDirectory
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						buttonSelectACREPluginInstallationDirectoryPerformed();
					}
				});
	}

	public void init() {

		determinePaths();
		determineSystemSpecs();
	}

	private void determinePaths() {

		/* TS3 directory */
		String ts3InstallationDirectoryPath = configurationService
				.getAcre2TS3installationFodler();

		if (ts3InstallationDirectoryPath != null) {
			if (new File(ts3InstallationDirectoryPath).exists()) {
				textFieldTS3InstallationDirectory
						.setText(ts3InstallationDirectoryPath);
			}
		}

		/* @ACRE2 plugin directory */
		String acre2PluginPath = configurationService.getAcre2PluginPath();

		if (acre2PluginPath != null) {
			if (new File(acre2PluginPath).exists()) {
				textFieldPluginInstallationDirectory.setText(acre2PluginPath);
			}
		} else {
			String acre2InstallationDirectoryPath = addonService
					.getACRE2installationFolder();
			if (acre2InstallationDirectoryPath != null) {
				String path = acre2InstallationDirectoryPath + "/@ACRE2/plugin";
				if (new File(path).exists()) {
					path = new File(path).getAbsolutePath();
					textFieldPluginInstallationDirectory.setText(path);
				}
			}
		}
	}

	private void determineSystemSpecs() {

		determineOS();

		determineTS3();

		/* Addon Plugin */
		boolean is64bit = false;
		boolean is32bit = false;
		if (textFieldPluginInstallationDirectory.getText().isEmpty()) {
			labelPluginValue.setText("Unknown");
		} else if (textFieldTS3InstallationDirectory.getText().isEmpty()) {
			labelPluginValue.setText("Unknown");
		} else {
			is64bit = configurationService
					.isTS364bit(textFieldTS3InstallationDirectory.getText());

			is32bit = configurationService
					.isTS332bit(textFieldTS3InstallationDirectory.getText());

			if (is64bit) {
				labelPluginValue.setText("acre2_win64.dll");
			} else if (is32bit) {
				labelPluginValue.setText("acre2_win32.dll");
			} else {
				labelPluginValue.setText("Unknown");
			}
		}
	}

	private void buttonSelectTS3InstallationDirectoryPerformed() {

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(FirstPageACRE2InstallerDialog.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			textFieldTS3InstallationDirectory.setText(path);
		} else {
			textFieldTS3InstallationDirectory.setText("");
		}
		configurationService
				.setAcre2TS3installationFodler(textFieldTS3InstallationDirectory
						.getText());
		determineSystemSpecs();
	}

	private void buttonSelectACREPluginInstallationDirectoryPerformed() {

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(FirstPageACRE2InstallerDialog.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			textFieldPluginInstallationDirectory.setText(path);
		} else {
			textFieldPluginInstallationDirectory.setText("");
		}
		configurationService
				.setAcre2PluginPath(textFieldPluginInstallationDirectory
						.getText());

		determineSystemSpecs();
	}

	@Override
	public void buttonFistPerformed() {

		String message = "";

		String osName = System.getProperty("os.name");
		if (!osName.contains("Windows")) {
			message = "This feature is not supported for your system";
			JOptionPane.showMessageDialog(this, message, "ACRE installer",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (textFieldTS3InstallationDirectory.getText().isEmpty()) {
			message = "TS3 installation directory is missing.";
		} else if (textFieldPluginInstallationDirectory.getText().isEmpty()) {
			message = "ACRE 2 plugin directory is missing.";
		} else if (labelTS3Value.equals("Unknown")) {
			message = "Can't determine between TS3 32/64 bit.";
		}

		String ts3PluginsFoler = textFieldTS3InstallationDirectory.getText()
				+ "/plugins";
		if (!new File(ts3PluginsFoler).exists()) {
			message = "TS3 installation directory is missing \\plugins folder";
		}

		String acre2Plugin = textFieldPluginInstallationDirectory.getText()
				+ "/" + labelPluginValue.getText();
		if (!new File(acre2Plugin).exists()) {
			if (labelPluginValue.getText().equals("Unknown")) {
				message = "ACRE 2 plugin is missing.";
			} else {
				message = "ACRE 2 plugin " + labelPluginValue.getText()
						+ " is missing.";
			}
		}

		LaunchService launchService = new LaunchService();
		boolean isTS3Running = launchService.isTS3Running();
		if (isTS3Running) {
			message = "TS3 is running and must be close to proceed.";
		}

		if (!message.isEmpty()) {
			JOptionPane.showMessageDialog(this, message, "ACRE 2 installer",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		this.setVisible(false);
		SecondPageACRE2InstallerDialog secondPageInstallerPanel = new SecondPageACRE2InstallerDialog(
				facade, this, title, description, image);
		secondPageInstallerPanel.init(
				textFieldTS3InstallationDirectory.getText(), acre2Plugin);
		secondPageInstallerPanel.setVisible(true);
		secondPageInstallerPanel.setFocusable(true);
	}

	@Override
	public void buttonSecondPerformed() {
		this.dispose();
	}
}
