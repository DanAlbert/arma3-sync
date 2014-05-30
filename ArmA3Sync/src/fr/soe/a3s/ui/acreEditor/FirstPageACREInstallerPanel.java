package fr.soe.a3s.ui.acreEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.service.AddonService;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.service.LaunchService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class FirstPageACREInstallerPanel extends AcreInstallerPanel implements
		UIConstants {

	private JTextField textFieldArmA3InstallationDirectory;
	private JButton buttonSelectArmA3InstallationDirectory;
	private JTextField textFieldTS3InstallationDirectory;
	private JButton buttonSelectTS3InstallationDirectory;
	private JTextField textFieldACREPluginInstallationDirectory;
	private JButton buttonSelectACREPluginInstallationDirectory;
	private JLabel labelOS;
	private JLabel labelOSValue;
	private JLabel labelTS3;
	private JLabel labelTS3Value;
	private JLabel labelACREplugin;
	private JLabel labelACREpluginValue;
	private ConfigurationService configurationService = new ConfigurationService();
	private AddonService addonService = new AddonService();
	private JTextField textFieldACREUserconfigInstallationDirectory;
	private JButton buttonSelectACREUserconfigInstallationDirectory;

	public FirstPageACREInstallerPanel(Facade facade) {
		super(facade);
		buttonFist.setText("Proceed");
		buttonSecond.setText("Cancel");
		getRootPane().setDefaultButton(buttonFist);
		this.facade.setFirstPageACREInstallerPanel(this);
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
					JPanel arma3InstallationDirectoryLabelPanel = new JPanel();
					arma3InstallationDirectoryLabelPanel
							.setLayout(new FlowLayout(FlowLayout.LEFT));
					JLabel labelArmA3installationDirectory = new JLabel(
							"ArmA 3");
					arma3InstallationDirectoryLabelPanel
							.add(labelArmA3installationDirectory);
					vBox.add(arma3InstallationDirectoryLabelPanel);
				}
				{
					JPanel arma3InstallationDirectoryPanel = new JPanel();
					arma3InstallationDirectoryPanel
							.setLayout(new BorderLayout());
					textFieldArmA3InstallationDirectory = new JTextField();
					buttonSelectArmA3InstallationDirectory = new JButton(
							"Select");
					buttonSelectArmA3InstallationDirectory
							.setPreferredSize(new Dimension(70, 25));
					textFieldArmA3InstallationDirectory.setEditable(false);
					textFieldArmA3InstallationDirectory
							.setBackground(Color.WHITE);
					arma3InstallationDirectoryPanel.add(
							textFieldArmA3InstallationDirectory,
							BorderLayout.CENTER);
					arma3InstallationDirectoryPanel.add(
							buttonSelectArmA3InstallationDirectory,
							BorderLayout.EAST);
					vBox.add(arma3InstallationDirectoryPanel);
				}
				vBox.add(Box.createVerticalStrut(5));
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
					buttonSelectTS3InstallationDirectory
							.setPreferredSize(new Dimension(70, 25));
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
							"@ACRE\\plugin");
					acrePluginInstallationDirectoryLabelPanel
							.add(acreInstallationDirectoryLabel);
					vBox.add(acrePluginInstallationDirectoryLabelPanel);
				}
				{
					JPanel acreInstallationDirectoryPanel = new JPanel();
					acreInstallationDirectoryPanel
							.setLayout(new BorderLayout());
					textFieldACREPluginInstallationDirectory = new JTextField();
					buttonSelectACREPluginInstallationDirectory = new JButton(
							"Select");
					buttonSelectACREPluginInstallationDirectory
							.setPreferredSize(new Dimension(70, 25));
					textFieldACREPluginInstallationDirectory.setEditable(false);
					textFieldACREPluginInstallationDirectory
							.setBackground(Color.WHITE);
					acreInstallationDirectoryPanel.add(
							textFieldACREPluginInstallationDirectory,
							BorderLayout.CENTER);
					acreInstallationDirectoryPanel.add(
							buttonSelectACREPluginInstallationDirectory,
							BorderLayout.EAST);
					vBox.add(acreInstallationDirectoryPanel);
				}
				vBox.add(Box.createVerticalStrut(5));
				{
					JPanel acreUserconfigInstallationDirectoryLabelPanel = new JPanel();
					acreUserconfigInstallationDirectoryLabelPanel
							.setLayout(new FlowLayout(FlowLayout.LEFT));
					JLabel acreInstallationDirectoryLabel = new JLabel(
							"@ACRE\\userconfig");
					acreUserconfigInstallationDirectoryLabelPanel
							.add(acreInstallationDirectoryLabel);
					vBox.add(acreUserconfigInstallationDirectoryLabelPanel);
				}
				{
					JPanel acreInstallationDirectoryPanel = new JPanel();
					acreInstallationDirectoryPanel
							.setLayout(new BorderLayout());
					textFieldACREUserconfigInstallationDirectory = new JTextField();
					buttonSelectACREUserconfigInstallationDirectory = new JButton(
							"Select");
					buttonSelectACREUserconfigInstallationDirectory
							.setPreferredSize(new Dimension(70, 25));
					textFieldACREUserconfigInstallationDirectory
							.setEditable(false);
					textFieldACREUserconfigInstallationDirectory
							.setBackground(Color.WHITE);
					acreInstallationDirectoryPanel.add(
							textFieldACREUserconfigInstallationDirectory,
							BorderLayout.CENTER);
					acreInstallationDirectoryPanel.add(
							buttonSelectACREUserconfigInstallationDirectory,
							BorderLayout.EAST);
					vBox.add(acreInstallationDirectoryPanel);
				}

				vBox.add(Box.createVerticalStrut(20));
			}
			{
				JPanel systemDescriptionPanel = new JPanel();
				systemDescriptionPanel.setBorder(BorderFactory
						.createTitledBorder(BorderFactory.createEtchedBorder(),
								"System specifications"));
				centerPanel.add(systemDescriptionPanel);
				systemDescriptionPanel.setLayout(null);
				systemDescriptionPanel.setPreferredSize(new java.awt.Dimension(
						480, 212));
				Box vBox = Box.createVerticalBox();
				systemDescriptionPanel.add(vBox);
				vBox.setBounds(11, 23, 146, 78);
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
					labelACREplugin = new JLabel("ACRE plugin: ");
					labelACREpluginValue = new JLabel();
					Box hBox = Box.createHorizontalBox();
					hBox.add(labelACREplugin);
					hBox.add(labelACREpluginValue);
					hBox.add(Box.createHorizontalGlue());
					vBox.add(hBox);
					vBox.add(Box.createVerticalStrut(10));
				}
				vBox.add(Box.createVerticalStrut(10));
			}
		}
		buttonSelectArmA3InstallationDirectory
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						buttonSelectArmA3InstallationDirectoryPerformed();
					}
				});
		buttonSelectTS3InstallationDirectory
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						buttonSelectTS3InstallationDirectoryPerformed();
					}
				});
		buttonSelectACREPluginInstallationDirectory
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						buttonSelectACREPluginInstallationDirectoryPerformed();
					}
				});
		buttonSelectACREUserconfigInstallationDirectory
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						buttonSelectACREUserconfigInstallationDirectoryPerformed();
					}
				});
	}

	public void init() {

		determinePaths();
		determineSystemSpecs();
	}

	private void determinePaths() {

		/* ArmA 3 directory */
		String arma3ExePath = configurationService.getLauncherOptions()
				.getArma3ExePath();

		if (arma3ExePath != null) {
			String arma3InstallationDirectoryPath = (new File(arma3ExePath))
					.getParentFile().getAbsolutePath();
			textFieldArmA3InstallationDirectory
					.setText(arma3InstallationDirectoryPath);
		}

		/* TS3 directory */
		String ts3InstallationDirectoryPath = configurationService
				.getTS3installationFodler();

		if (ts3InstallationDirectoryPath != null) {
			textFieldTS3InstallationDirectory
					.setText(ts3InstallationDirectoryPath);
		}

		/* @ACRE plugin directory */
		String acreInstallationDirectoryPath = addonService
				.getACREinstallationFolder();

		if (acreInstallationDirectoryPath != null) {
			String path = acreInstallationDirectoryPath + "/@ACRE/plugin";
			if (new File(path).exists()) {
				path = new File(path).getAbsolutePath();
				textFieldACREPluginInstallationDirectory.setText(path);
			}
		}

		/* @ACRE userconfig directory */
		if (acreInstallationDirectoryPath != null) {
			String path = acreInstallationDirectoryPath + "/@ACRE/userconfig";
			if (new File(path).exists()) {
				path = new File(path).getAbsolutePath();
				textFieldACREUserconfigInstallationDirectory.setText(path);
			}
		}
	}

	private void determineSystemSpecs() {

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

		/* TS3 version */
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
			if (is64bit) {
				labelTS3Value.setText(ts3Version + " - 64 bit");
			} else {
				labelTS3Value.setText(ts3Version + " - 32 bit");
			}
		}

		/* ACRE plugin */
		if (textFieldACREPluginInstallationDirectory.getText().isEmpty()) {
			labelACREpluginValue.setText("Unknown");
		} else if (textFieldTS3InstallationDirectory.getText().isEmpty()) {
			labelACREpluginValue.setText("Unknown");
		} else {
			is64bit = configurationService
					.isTS364bit(textFieldTS3InstallationDirectory.getText());
			if (is64bit) {
				labelACREpluginValue.setText("acre_win64.dll");
			} else {
				labelACREpluginValue.setText("acre_win32.dll");
			}
		}
	}

	private void buttonSelectArmA3InstallationDirectoryPerformed() {

		JFileChooser fc = null;
		String arma3Path = configurationService.determineArmA3Path();
		if (arma3Path == null) {
			fc = new JFileChooser();
		} else {
			File arma3Folder = new File(arma3Path);
			if (arma3Folder.exists()) {
				fc = new JFileChooser(arma3Path);
			} else {
				fc = new JFileChooser();
			}
		}

		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(FirstPageACREInstallerPanel.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			textFieldArmA3InstallationDirectory.setText(path);
		} else {
			textFieldArmA3InstallationDirectory.setText("");
		}
		determineSystemSpecs();
	}

	private void buttonSelectTS3InstallationDirectoryPerformed() {

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(FirstPageACREInstallerPanel.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			textFieldTS3InstallationDirectory.setText(path);
		} else {
			textFieldTS3InstallationDirectory.setText("");
		}
		configurationService
				.setTS3installationFodler(textFieldTS3InstallationDirectory
						.getText());
		determineSystemSpecs();
	}

	private void buttonSelectACREPluginInstallationDirectoryPerformed() {

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(FirstPageACREInstallerPanel.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			textFieldACREPluginInstallationDirectory.setText(path);
		} else {
			textFieldACREPluginInstallationDirectory.setText("");
		}
		determineSystemSpecs();
	}

	private void buttonSelectACREUserconfigInstallationDirectoryPerformed() {

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(FirstPageACREInstallerPanel.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			textFieldACREUserconfigInstallationDirectory.setText(path);
		} else {
			textFieldACREUserconfigInstallationDirectory.setText("");
		}
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

		if (textFieldArmA3InstallationDirectory.getText().isEmpty()) {
			message = "ArmA 3 installation directory is missing.";
		} else if (textFieldTS3InstallationDirectory.getText().isEmpty()) {
			message = "TS3 installation directory is missing.";
		} else if (textFieldACREPluginInstallationDirectory.getText().isEmpty()) {
			message = "ACRE plugin directory is missing.";
		} else if (textFieldACREUserconfigInstallationDirectory.getText()
				.isEmpty()) {
			message = "ACRE userconfig directory is missing.";
		} else if (labelTS3Value.equals("Unknown")) {
			message = "Can't determine between TS3 32/64 bit.";
		}

		String ts3PluginsFoler = textFieldTS3InstallationDirectory.getText()
				+ "/plugins";
		if (!new File(ts3PluginsFoler).exists()) {
			message = "TS3 installation directory is missing \\plugins folder";
		}

		String acrePlugin = textFieldACREPluginInstallationDirectory.getText()
				+ "/" + labelACREpluginValue.getText();
		if (!new File(acrePlugin).exists()) {
			if (labelACREpluginValue.getText().equals("Unknown")) {
				message = "ACRE plugin is missing.";
			} else {
				message = "ACRE plugin " + labelACREpluginValue.getText()
						+ " is missing.";
			}
		}

		LaunchService launchService = new LaunchService();
		boolean isTS3Running = launchService.isTS3Running();
		if (isTS3Running) {
			message = "TS3 is running and must be close to proceed.";
		}

		if (!message.isEmpty()) {
			JOptionPane.showMessageDialog(this, message, "ACRE installer",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		this.setVisible(false);
		SecondPageACREInstallerPanel secondPageInstallerPanel = new SecondPageACREInstallerPanel(
				facade);
		secondPageInstallerPanel.init(
				textFieldArmA3InstallationDirectory.getText(),
				textFieldTS3InstallationDirectory.getText(),
				textFieldACREUserconfigInstallationDirectory.getText(),
				acrePlugin);
		secondPageInstallerPanel.setVisible(true);
		secondPageInstallerPanel.setFocusable(true);
	}

	@Override
	public void buttonSecondPerformed() {
		this.dispose();
	}
}
