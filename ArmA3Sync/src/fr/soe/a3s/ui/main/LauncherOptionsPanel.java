package fr.soe.a3s.ui.main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import fr.soe.a3s.constant.GameExecutables;
import fr.soe.a3s.constant.MaxMemoryValues;
import fr.soe.a3s.dto.configuration.LauncherOptionsDTO;
import fr.soe.a3s.service.AddonService;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.service.LaunchService;
import fr.soe.a3s.service.ProfileService;
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
public class LauncherOptionsPanel extends JPanel implements DocumentListener,
		UIConstants {

	private final Facade facade;
	private final JPanel launcherOptionsPanel, performancePanel;
	private JPanel armaPanel;
	private JPanel steamPanel;
	private final JTextArea runParametersTextArea,
			additionalParametersTextArea;
	private final JScrollPane scrollPaneRunParameters,
			scrollPaneAditionalParameters;
	private JTextField textFieldArmAExecutableLocation,
			textFieldSteamExecutableLocation;
	private JButton buttonSelectArmAExe, buttonSelectSteamExe;
	private JComboBox comboBoxProfiles, comboBoxMaxMemory, comboBoxCpuCount,
			comboBoxExThreads, comboBoxMalloc;
	private JCheckBox checkBoxProfiles, checkBoxNoPause, checkBoxWindowMode,
			checkBoxShowScriptErrors, checkBoxMaxMemory, checkBoxCpuCount,
			checkBoxNoSplashScreen, checkBoxDefaultWorld, checkBoxNoLogs,
			checkBoxCheckSignatures, checkBoxExThreads, checkBoxEnableHT,
			checkBoxFilePatching, checkBoxAutoRestart, checkBoxMalloc,
			checkBoxEnableBattleye, checkBoxHugePages;
	private JLabel labelMallocPath;

	/* Services */
	private final ConfigurationService configurationService = new ConfigurationService();
	private final ProfileService profileService = new ProfileService();
	private final AddonService addonService = new AddonService();
	private final LaunchService launchService = new LaunchService();

	public LauncherOptionsPanel(Facade facade) {
		this.facade = facade;
		this.facade.setLaunchOptionsPanel(this);
		this.setLayout(new BorderLayout());

		Box vertBox1 = Box.createVerticalBox();
		this.add(vertBox1);
		vertBox1.add(Box.createVerticalStrut(10));

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		vertBox1.add(mainPanel);

		JPanel centerPanel = new JPanel();
		GridLayout grid1 = new GridLayout(2, 2);
		centerPanel.setLayout(grid1);
		mainPanel.add(centerPanel, BorderLayout.CENTER);

		// vertBox1.add(centerPanel, BorderLayout.CENTER);
		//

		launcherOptionsPanel = new JPanel();
		launcherOptionsPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Launcher Options"));

		performancePanel = new JPanel();
		performancePanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Performance"));

		/* Launcher options */
		launcherOptionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		Box vBox = Box.createVerticalBox();
		launcherOptionsPanel.add(vBox);
		{
			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			vBox.add(panel);
			{
				checkBoxProfiles = new JCheckBox();
				checkBoxProfiles.setText("Profile:");
				checkBoxProfiles.setFocusable(false);
				comboBoxProfiles = new JComboBox();
				javax.swing.filechooser.FileSystemView fsv = javax.swing.filechooser.FileSystemView
						.getFileSystemView();
				File myDocuments = fsv.getDefaultDirectory();
				List<String> listProfileNames = new ArrayList<String>();
				if (myDocuments != null) {
					File[] subfiles = myDocuments.listFiles();
					if (subfiles != null) {
						for (File file : subfiles) {
							String name = file.getName().toUpperCase();
							if (name.contains("ARMA 3")
									&& name.contains("OTHER PROFILES")) {
								File[] subf = file.listFiles();
								if (subf != null) {
									for (int i = 0; i < subf.length; i++) {
										listProfileNames.add(subf[i].getName()
												.replaceAll("(%*)20", " ")
												.replaceAll("%2e", "."));
									}
								}
							}
						}
					}

				}
				String[] tab = new String[listProfileNames.size() + 1];
				tab[0] = "Default";
				for (int i = 0; i < listProfileNames.size(); i++) {
					tab[i + 1] = listProfileNames.get(i);
				}
				ComboBoxModel profilesModel = new DefaultComboBoxModel(tab);
				comboBoxProfiles.setModel(profilesModel);
				comboBoxProfiles.setFocusable(false);
			}
			{
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 0.5;
				c.weighty = 0;
				c.gridx = 0;
				c.gridy = 0;
				panel.add(checkBoxProfiles, c);
			}
			{
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 0.5;
				c.weighty = 0;
				c.gridx = 1;
				c.gridy = 0;
				panel.add(comboBoxProfiles, c);
			}
		}
		{
			checkBoxShowScriptErrors = new JCheckBox();
			checkBoxShowScriptErrors.setText("Show script errors");
			checkBoxShowScriptErrors.setFocusable(false);
			Font boldFont = checkBoxShowScriptErrors.getFont().deriveFont(
					Font.BOLD);
			checkBoxShowScriptErrors.setFont(boldFont);
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxShowScriptErrors);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
		}
		{
			checkBoxNoPause = new JCheckBox();
			checkBoxNoPause.setText("No Pause");
			checkBoxNoPause.setFocusable(false);
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxNoPause);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
		}
		{
			checkBoxWindowMode = new JCheckBox();
			checkBoxWindowMode.setText("Window Mode");
			checkBoxWindowMode.setFocusable(false);
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxWindowMode);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
		}
		{
			checkBoxFilePatching = new JCheckBox();
			checkBoxFilePatching.setText("File Patching");
			checkBoxFilePatching.setFocusable(false);
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxFilePatching);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
		}
		{
			checkBoxCheckSignatures = new JCheckBox();
			checkBoxCheckSignatures.setText("Check signatures");
			checkBoxCheckSignatures.setFocusable(false);
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxCheckSignatures);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
		}
		{
			checkBoxEnableBattleye = new JCheckBox();
			checkBoxEnableBattleye.setText("Enable Battleye");
			checkBoxEnableBattleye.setFocusable(false);
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxEnableBattleye);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
		}
		{
			checkBoxAutoRestart = new JCheckBox();
			checkBoxAutoRestart.setText("Auto-restart");
			checkBoxAutoRestart.setFocusable(false);
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxAutoRestart);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
		}

		/* Performances */
		performancePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		vBox = Box.createVerticalBox();
		performancePanel.add(vBox);
		{
			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			vBox.add(panel);
			{
				checkBoxMaxMemory = new JCheckBox();
				checkBoxMaxMemory.setText("Max Memory:");
				checkBoxMaxMemory.setFocusable(false);
				comboBoxMaxMemory = new JComboBox();
				comboBoxMaxMemory.setFocusable(false);

				ComboBoxModel maxMemoryModel = new DefaultComboBoxModel(
						new String[] {
								"",
								Integer.toString(MaxMemoryValues.MIN.getValue()),
								Integer.toString(MaxMemoryValues.MEDIUM
										.getValue()),
								Integer.toString(MaxMemoryValues.MAX.getValue()),
								Integer.toString(MaxMemoryValues.MAX64
										.getValue()) });
				comboBoxMaxMemory.setModel(maxMemoryModel);
			}
			{
				checkBoxCpuCount = new JCheckBox();
				checkBoxCpuCount.setText("CPU Count:");
				checkBoxCpuCount.setFocusable(false);
				comboBoxCpuCount = new JComboBox();
				comboBoxCpuCount.setFocusable(false);
				Runtime runtime = Runtime.getRuntime();
				int nbProcessors = runtime.availableProcessors();
				String[] tab = new String[nbProcessors + 1];
				tab[0] = "";
				for (int i = 1; i <= nbProcessors; i++) {
					tab[i] = Integer.toString(i);
				}
				ComboBoxModel cpuCountModel = new DefaultComboBoxModel(tab);
				comboBoxCpuCount.setModel(cpuCountModel);
			}
			{
				checkBoxExThreads = new JCheckBox();
				checkBoxExThreads.setText("ExThreads:");
				checkBoxExThreads.setFocusable(false);
				comboBoxExThreads = new JComboBox();
				comboBoxExThreads.setFocusable(false);
				ComboBoxModel exThreadsModel = new DefaultComboBoxModel(
						new String[] { "", "0", "1", "3", "5", "7" });
				comboBoxExThreads.setModel(exThreadsModel);
			}
			{
				checkBoxMalloc = new JCheckBox();
				checkBoxMalloc.setText("Malloc:");
				checkBoxMalloc.setFocusable(false);
				comboBoxMalloc = new JComboBox();
				comboBoxMalloc.setFocusable(false);
				ComboBoxModel mallocModel = new DefaultComboBoxModel(
						new String[] { "", "system" });
				comboBoxMalloc.setModel(mallocModel);
			}
			{
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 0.5;
				c.weighty = 0;
				c.gridx = 0;
				c.gridy = 0;
				panel.add(checkBoxMaxMemory, c);
			}
			{
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 0.5;
				c.weighty = 0;
				c.gridx = 1;
				c.gridy = 0;
				panel.add(comboBoxMaxMemory, c);
			}
			{
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 0.5;
				c.weighty = 0;
				c.gridx = 0;
				c.gridy = 1;
				panel.add(checkBoxCpuCount, c);
			}
			{
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 0.5;
				c.weighty = 0;
				c.gridx = 1;
				c.gridy = 1;
				panel.add(comboBoxCpuCount, c);
			}
			{
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 0.5;
				c.weighty = 0;
				c.gridx = 0;
				c.gridy = 2;
				panel.add(checkBoxExThreads, c);
			}
			{
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 0.5;
				c.weighty = 0;
				c.gridx = 1;
				c.gridy = 2;
				panel.add(comboBoxExThreads, c);
			}
			{
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 0.5;
				c.weighty = 0;
				c.gridx = 0;
				c.gridy = 3;
				panel.add(checkBoxMalloc, c);
			}
			{
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 0.5;
				c.weighty = 0;
				c.gridx = 1;
				c.gridy = 3;
				panel.add(comboBoxMalloc, c);
			}
		}
		{
			checkBoxEnableHT = new JCheckBox();
			checkBoxEnableHT.setText("Enable HT");
			checkBoxEnableHT.setFocusable(false);
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxEnableHT);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
		}
		{
			checkBoxHugePages = new JCheckBox();
			checkBoxHugePages.setText("Huge pages");
			checkBoxHugePages.setFocusable(false);
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxHugePages);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
		}
		{
			checkBoxNoSplashScreen = new JCheckBox();
			checkBoxNoSplashScreen.setText("No Splash Screen");
			checkBoxNoSplashScreen.setFocusable(false);
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxNoSplashScreen);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
		}
		{
			checkBoxDefaultWorld = new JCheckBox();
			checkBoxDefaultWorld.setText("Default World Empty");
			checkBoxDefaultWorld.setFocusable(false);
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxDefaultWorld);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
		}
		{
			checkBoxNoLogs = new JCheckBox();
			checkBoxNoLogs.setText("No logs");
			checkBoxNoLogs.setFocusable(false);
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxNoLogs);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
		}
		vBox.add(Box.createVerticalStrut(20));

		/* Run parameters */
		runParametersTextArea = new JTextArea();
		Font labelFont = UIManager.getFont("Label.font");
		runParametersTextArea.setFont(new Font(labelFont.getName(),
				labelFont.getStyle(), labelFont.getSize()));
		runParametersTextArea.setLineWrap(true);
		runParametersTextArea.setEditable(false);
		runParametersTextArea.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		scrollPaneRunParameters = new JScrollPane(runParametersTextArea);
		scrollPaneRunParameters.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Run Parameters"));

		/* Additional parameters */
		additionalParametersTextArea = new JTextArea();
		additionalParametersTextArea.setFont(new Font(labelFont.getName(),
				labelFont.getStyle(), labelFont.getSize()));
		additionalParametersTextArea.setLineWrap(true);
		additionalParametersTextArea.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		scrollPaneAditionalParameters = new JScrollPane(
				additionalParametersTextArea);
		scrollPaneAditionalParameters.setBorder(BorderFactory
				.createTitledBorder(BorderFactory.createEtchedBorder(),
						"Additional Parameters"));

		centerPanel.add(launcherOptionsPanel);
		centerPanel.add(scrollPaneRunParameters);
		centerPanel.add(performancePanel);
		centerPanel.add(scrollPaneAditionalParameters);

		/* Executable locations */
		vBox = Box.createVerticalBox();
		vBox.add(Box.createVerticalStrut(5));
		JPanel southPanel = new JPanel();
		southPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),
				"ArmA III Executable Location (game/server)"));
		southPanel.setLayout(new BorderLayout());
		{
			textFieldArmAExecutableLocation = new JTextField();
			textFieldArmAExecutableLocation.setEditable(false);
			buttonSelectArmAExe = new JButton("Select");
			southPanel
					.add(textFieldArmAExecutableLocation, BorderLayout.CENTER);
			southPanel.add(buttonSelectArmAExe, BorderLayout.EAST);
		}
		vBox.add(southPanel);
		vBox.add(Box.createVerticalStrut(5));
		mainPanel.add(vBox, BorderLayout.SOUTH);

		/* Align Profiles comboBox */
		checkBoxProfiles.setPreferredSize(checkBoxMaxMemory.getPreferredSize());

		checkBoxProfiles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxProfilesPerformed();
			}
		});
		comboBoxProfiles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				comboBoxProfilesPerformed();
			}
		});
		checkBoxShowScriptErrors.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxShowScriptErrorsPerformed();
			}
		});
		checkBoxNoPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxNoPausePerformed();
			}
		});
		checkBoxFilePatching.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxNoFilePatchingPerformed();
			}
		});
		checkBoxWindowMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxWindowModePerformed();
			}
		});
		checkBoxCheckSignatures.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				checkBoxCheckSignaturesPerformed();
			}
		});
		checkBoxEnableBattleye.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				checkBoxEnableBattleyePerformed();
			}
		});
		checkBoxAutoRestart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				checkBoxAutoRestartPerformed();
			}
		});
		checkBoxMaxMemory.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxMaxMemoryPerformed();
			}
		});
		comboBoxMaxMemory.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				comboBoxMaxMemoryPerformed();
			}
		});
		checkBoxCpuCount.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxCpuCountPerformed();
			}
		});
		comboBoxCpuCount.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				comboBoxCpuCountPerformed();
			}
		});
		checkBoxExThreads.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxExThreadsPerformed();
			}
		});
		comboBoxExThreads.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				comboBoxExThreadsPerformed();
			}
		});
		checkBoxMalloc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxMallocPerformed();
			}
		});
		comboBoxMalloc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				comboBoxMallocDllPathPerformed();
			}
		});
		checkBoxEnableHT.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxEnableHTPerformed();
			}
		});
		checkBoxHugePages.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxHugePagesPerformed();
			}
		});
		checkBoxNoSplashScreen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxNoSplashScreenPerformed();
			}
		});
		checkBoxDefaultWorld.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxDefaultWorldPerformed();
			}
		});
		checkBoxNoLogs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxNoLogsPerformed();
			}
		});
		buttonSelectArmAExe.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonSelectArmAExePerformed();
			}
		});
		additionalParametersTextArea.getDocument().addDocumentListener(this);
		setContextualHelp();
	}

	private void setContextualHelp() {

		comboBoxProfiles
				.setToolTipText("(optional) Select in game profile name");
		checkBoxShowScriptErrors.setToolTipText("Show in game script error");
		checkBoxNoPause
				.setToolTipText("Don't suspend the game when placed in background");
		checkBoxFilePatching
				.setToolTipText("Allow the game to load unpacked data");
		checkBoxWindowMode
				.setToolTipText("Display the game windowed instead of full screen");
		checkBoxCheckSignatures.setToolTipText("Check signatures of PBO files");
		comboBoxMaxMemory.setToolTipText("Restricts memory allocation");
		checkBoxMaxMemory.setToolTipText("Restricts memory allocation");
		comboBoxCpuCount.setToolTipText("Restricts number of cores used");
		checkBoxCpuCount.setToolTipText("Restricts number of cores used");
		comboBoxExThreads.setToolTipText("Sets number of extra threads to use");
		checkBoxExThreads.setToolTipText("Sets number of extra threads to use");
		checkBoxEnableHT.setToolTipText("Use all hyper-threaded cpu cores");
		checkBoxHugePages
				.setToolTipText("Enables huge pages with the default memory allocator");
		checkBoxNoSplashScreen.setToolTipText("Disables splash screens");
		checkBoxDefaultWorld.setToolTipText("No world loaded at game startup");
		checkBoxNoLogs.setToolTipText("Do not write errors into RPT file");
		checkBoxAutoRestart.setToolTipText("Auto-restart game/server");
		checkBoxMalloc.setToolTipText("Sets memory allocator");
		checkBoxEnableBattleye.setToolTipText("Start the game with Battleye");
	}

	public void update(int flag) {

		if (flag == OP_PROFILE_CHANGED) {

			// update Malloc comboBox Model and Battleye
			updateOptions();

			updateRunParameters();

			/* Additional Run Parameters */
			updateAdditionalRunParameters();
		}

		else if (flag == OP_ADDON_SELECTION_CHANGED || flag == OP_GROUP_CHANGED
				|| flag == OP_ADDON_PRIORITY_CHANGED
				|| flag == OP_ADDON_FILES_CHANGED) {

			updateRunParameters();
		}
	}

	private void updateOptions() {

		LauncherOptionsDTO launcherOptionsDTO = profileService
				.getLauncherOptions();

		/* Launcher options */
		if (launcherOptionsDTO.getGameProfile() != null) {
			comboBoxProfiles.setSelectedItem(launcherOptionsDTO
					.getGameProfile());
			checkBoxProfiles.setSelected(true);
		} else {
			comboBoxProfiles.setSelectedIndex(0);
		}

		checkBoxShowScriptErrors.setSelected(launcherOptionsDTO
				.isShowScriptError());
		checkBoxNoPause.setSelected(launcherOptionsDTO.isNoPause());
		checkBoxFilePatching.setSelected(launcherOptionsDTO.isFilePatching());
		checkBoxWindowMode.setSelected(launcherOptionsDTO.isWindowMode());
		checkBoxCheckSignatures.setSelected(launcherOptionsDTO
				.isCheckSignatures());

		checkBoxAutoRestart.setSelected(launcherOptionsDTO.isAutoRestart());

		/* Performance */
		if (launcherOptionsDTO.getMaxMemorySelection() != null) {
			comboBoxMaxMemory.setSelectedItem(launcherOptionsDTO
					.getMaxMemorySelection());
			checkBoxMaxMemory.setSelected(true);
		} else {
			comboBoxMaxMemory.setSelectedIndex(0);
		}

		if (launcherOptionsDTO.getCpuCountSelection() != 0) {
			comboBoxCpuCount.setSelectedItem(Integer
					.toString(launcherOptionsDTO.getCpuCountSelection()));
			checkBoxCpuCount.setSelected(true);
		} else {
			comboBoxCpuCount.setSelectedIndex(0);
		}

		if (launcherOptionsDTO.getExThreadsSelection() != null) {
			comboBoxExThreads.setSelectedItem(launcherOptionsDTO
					.getExThreadsSelection());
			checkBoxExThreads.setSelected(true);
		} else {
			comboBoxExThreads.setSelectedIndex(0);
		}

		checkBoxEnableHT.setSelected(launcherOptionsDTO.isEnableHT());
		checkBoxHugePages.setSelected(launcherOptionsDTO.isHugePages());
		checkBoxNoSplashScreen.setSelected(launcherOptionsDTO
				.isNoSplashScreen());
		checkBoxDefaultWorld.setSelected(launcherOptionsDTO.isDefaultWorld());
		checkBoxNoLogs.setSelected(launcherOptionsDTO.isNoLogs());

		/* ArmA 3 Executable Location, Malloc and Battleye */
		checkBoxMalloc.setSelected(false);
		comboBoxMalloc.setSelectedIndex(0);// default empty
		checkBoxEnableBattleye.setSelected(false);
		// Battleye setting may have change ArmA3ExePath
		String arma3ExePath = launcherOptionsDTO.getArma3ExePath();
		if (arma3ExePath != null) {
			File arma3ExeFile = new File(arma3ExePath);
			if (arma3ExeFile.exists()) {// set executable location
				textFieldArmAExecutableLocation.setText(launcherOptionsDTO
						.getArma3ExePath());
			}
			if (arma3ExeFile.getParentFile() != null) {// set Malloc
				File parent = new File(arma3ExeFile.getParent());
				List<String> list = new ArrayList<String>();
				if (parent != null) {
					// Add System Malloc
					// https://community.bistudio.com/wiki/Arma_3:_Custom_Memory_Allocator
					// Add from dll folder
					File dllFolder = new File(parent.getAbsolutePath() + "/Dll");
					File[] subfiles = dllFolder.listFiles();
					if (subfiles != null) {
						for (File file : subfiles) {
							if (file.getName().toLowerCase().contains(".dll")) {
								list.add(file.getName().replaceAll(".dll", ""));
							}
						}
					}
				}
				String[] tab = new String[list.size() + 1];
				tab[0] = "";
				for (int i = 0; i < list.size(); i++) {
					tab[i + 1] = list.get(i);
				}
				ComboBoxModel mallocModel = new DefaultComboBoxModel(tab);
				comboBoxMalloc.setModel(mallocModel);
				if (launcherOptionsDTO.getMallocSelection() != null) {
					comboBoxMalloc.setSelectedItem(launcherOptionsDTO
							.getMallocSelection());
					checkBoxMalloc.setSelected(true);
					checkBoxHugePages.setSelected(false);
					checkBoxHugePages.setEnabled(false);
				} else {
					comboBoxMalloc.setSelectedIndex(0);
				}
			}
			if (arma3ExeFile.getName().toLowerCase()
					.equals(GameExecutables.BATTLEYE.getDescription())) {
				checkBoxEnableBattleye.setSelected(true);
			}
		}
	}

	private void updateAdditionalRunParameters() {

		String additionalParameters = profileService.getAdditionalParameters();
		if (additionalParameters != null) {
			additionalParametersTextArea.setText(additionalParameters);
		} else {
			additionalParametersTextArea.setText("");
		}
	}

	/* Update Run Parameters */
	private void updateRunParameters() {

		runParametersTextArea.setText("");
		List<String> params = launchService.getRunParameters();
		if (params != null) {
			String txt = "";
			for (String stg : params) {
				txt = txt + (stg) + "\n";
			}
			runParametersTextArea.setText(txt);
			runParametersTextArea.setRows(1);
			runParametersTextArea.setCaretPosition(0);
		}
	}

	/* Components selection */
	private void checkBoxProfilesPerformed() {
		if (!checkBoxProfiles.isSelected()) {
			comboBoxProfiles.setSelectedIndex(0);
		}
		updateRunParameters();
	}

	private void comboBoxProfilesPerformed() {

		String gameProfileName = (String) comboBoxProfiles.getSelectedItem();
		if (gameProfileName == null) {
			return;
		}
		if (!gameProfileName.equals("Default")) {
			checkBoxProfiles.setSelected(true);
			profileService.setGameProfile(gameProfileName);
		} else {
			checkBoxProfiles.setSelected(false);
			profileService.setGameProfile(null);
		}
		updateRunParameters();
	}

	private void checkBoxShowScriptErrorsPerformed() {
		profileService.setCheckBoxShowScriptErrors(checkBoxShowScriptErrors
				.isSelected());
		updateRunParameters();
	}

	private void checkBoxNoPausePerformed() {
		profileService.setCheckBoxNoPause(checkBoxNoPause.isSelected());
		updateRunParameters();
	}

	private void checkBoxNoFilePatchingPerformed() {
		profileService.setCheckBoxFilePatching(checkBoxFilePatching
				.isSelected());
		updateRunParameters();
	}

	private void checkBoxWindowModePerformed() {
		profileService.setCheckBoxWindowMode(checkBoxWindowMode.isSelected());
		updateRunParameters();
	}

	private void checkBoxCheckSignaturesPerformed() {
		profileService.setCheckBoxCheckSignatures(checkBoxCheckSignatures
				.isSelected());
		updateRunParameters();
	}

	private void checkBoxEnableBattleyePerformed() {

		File arma3ExeFile = null;
		String arma3ExePath = textFieldArmAExecutableLocation.getText();
		if (arma3ExePath != null) {
			if (!arma3ExePath.isEmpty()) {
				arma3ExeFile = new File(arma3ExePath);
			}
		}

		String newArma3ExePath = null;
		if (arma3ExeFile != null) {
			File parent = arma3ExeFile.getParentFile();
			if (parent != null) {
				if (arma3ExeFile.getName().equals(
						GameExecutables.BATTLEYE.getDescription())) {
					
					boolean is64bit = false;
					if (System.getProperty("os.name").toLowerCase().contains("windows")) {
						is64bit = (System.getenv("ProgramFiles(x86)") != null);
					}
				
					if (is64bit){
						newArma3ExePath = parent.getAbsolutePath() + "/"
								+ GameExecutables.GAME_x64.getDescription();
					}
					else {
						newArma3ExePath = parent.getAbsolutePath() + "/"
								+ GameExecutables.GAME.getDescription();
					}
				} else if (arma3ExeFile.getName().equals(
						GameExecutables.GAME.getDescription())|| arma3ExeFile.getName().equals(
								GameExecutables.GAME_x64.getDescription())) {
					newArma3ExePath = parent.getAbsolutePath() + "/"
							+ GameExecutables.BATTLEYE.getDescription();
				}
			}
		}

		if (newArma3ExePath != null) {
			File newArma3Exe = new File(newArma3ExePath);
			if (newArma3Exe.exists()) {
				textFieldArmAExecutableLocation.setText(newArma3Exe
						.getAbsolutePath());
				profileService.setArmA3ExePath(newArma3Exe.getAbsolutePath());
				JOptionPane.showMessageDialog(facade.getMainPanel(),
						"ArmA 3 Executable Location have changed.",
						"Information", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	private void checkBoxAutoRestartPerformed() {
		profileService.setCheckBoxAutoRestart(checkBoxAutoRestart.isSelected());
	}

	private void checkBoxMaxMemoryPerformed() {
		if (!checkBoxMaxMemory.isSelected()) {
			comboBoxMaxMemory.setSelectedIndex(0);
		}
		updateRunParameters();
	}

	private void comboBoxMaxMemoryPerformed() {

		String maxMemory = (String) comboBoxMaxMemory.getSelectedItem();
		if (maxMemory == null) {
			return;
		}
		if (!maxMemory.isEmpty()) {
			checkBoxMaxMemory.setSelected(true);
			profileService.setMaxMemory(maxMemory);
		} else {
			checkBoxMaxMemory.setSelected(false);
			profileService.setMaxMemory(null);
		}
		updateRunParameters();
	}

	private void checkBoxCpuCountPerformed() {
		if (!checkBoxCpuCount.isSelected()) {
			comboBoxCpuCount.setSelectedIndex(0);
		}
		updateRunParameters();
	}

	private void comboBoxCpuCountPerformed() {

		String cpuCount = (String) comboBoxCpuCount.getSelectedItem();
		if (cpuCount == null) {
			return;
		}
		if (!cpuCount.isEmpty()) {
			checkBoxCpuCount.setSelected(true);
			profileService.setCpuCount(cpuCount);
			checkBoxEnableHT.setSelected(false);
			checkBoxEnableHT.setEnabled(false);
			profileService.setEnableHT(false);
		} else {
			checkBoxCpuCount.setSelected(false);
			profileService.setCpuCount(null);
			checkBoxEnableHT.setEnabled(true);
		}
		updateRunParameters();
	}

	private void checkBoxExThreadsPerformed() {
		if (!checkBoxExThreads.isSelected()) {
			comboBoxExThreads.setSelectedIndex(0);
		}
		updateRunParameters();
	}

	private void comboBoxExThreadsPerformed() {

		String exThreads = (String) comboBoxExThreads.getSelectedItem();
		if (exThreads == null) {
			return;
		}
		if (!exThreads.isEmpty()) {
			checkBoxExThreads.setSelected(true);
			profileService.setExThreads(exThreads);
		} else {
			checkBoxExThreads.setSelected(false);
			profileService.setExThreads(null);
		}
		updateRunParameters();
	}

	private void checkBoxMallocPerformed() {
		if (!checkBoxMalloc.isSelected()) {
			comboBoxMalloc.setSelectedIndex(0);
		}
		updateRunParameters();
	}

	private void comboBoxMallocDllPathPerformed() {

		String mallocDll = (String) comboBoxMalloc.getSelectedItem();
		if (mallocDll == null) {
			return;
		}
		if (!mallocDll.isEmpty()) {
			checkBoxMalloc.setSelected(true);
			profileService.setMalloc(mallocDll);
			checkBoxHugePages.setSelected(false);
			checkBoxHugePages.setEnabled(false);
			profileService.setHugePages(false);
		} else {
			checkBoxMalloc.setSelected(false);
			profileService.setMalloc(null);
			checkBoxHugePages.setEnabled(true);
		}
		updateRunParameters();
	}

	private void checkBoxEnableHTPerformed() {
		profileService.setEnableHT(checkBoxEnableHT.isSelected());
		updateRunParameters();
	}

	private void checkBoxHugePagesPerformed() {
		profileService.setHugePages(checkBoxHugePages.isSelected());
		updateRunParameters();
	}

	private void checkBoxNoSplashScreenPerformed() {
		profileService.setNoSplashScreen(checkBoxNoSplashScreen.isSelected());
		updateRunParameters();
	}

	private void checkBoxDefaultWorldPerformed() {
		profileService.setDefaultWorld(checkBoxDefaultWorld.isSelected());
		updateRunParameters();
	}

	private void checkBoxNoLogsPerformed() {
		profileService.setNoLogs(checkBoxNoLogs.isSelected());
		updateRunParameters();
	}

	private void buttonSelectArmAExePerformed() {

		String arma3Path = profileService.getArma3ExePath();

		boolean ok = true;
		if (arma3Path == null) {
			ok = false;
		} else if ("".equals(arma3Path)) {
			ok = false;
		} else if (!(new File(arma3Path)).exists()) {
			ok = false;
		}

		if (!ok) {
			arma3Path = configurationService.determineArmA3Path();
		}

		JFileChooser fc = null;
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

		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showOpenDialog(facade.getMainPanel());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			profileService.setArmA3ExePath(path);
			textFieldArmAExecutableLocation.setText(path);
			updateOptions();
		}
	}

	/* additionalParametersTextArea modification Listener */
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		String additionalParameters = additionalParametersTextArea.getText();
		profileService.setAdditionalParameters(additionalParameters);
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		String additionalParameters = additionalParametersTextArea.getText();
		profileService.setAdditionalParameters(additionalParameters);
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		String additionalParameters = additionalParametersTextArea.getText();
		profileService.setAdditionalParameters(additionalParameters);
	}
}
