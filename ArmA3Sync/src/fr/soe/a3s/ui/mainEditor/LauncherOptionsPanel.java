package fr.soe.a3s.ui.mainEditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import fr.soe.a3s.constant.MaxMemoryValues;
import fr.soe.a3s.dto.configuration.LauncherOptionsDTO;
import fr.soe.a3s.exception.ProfileException;
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
	private JButton buttonSelectArmAExe, buttonSelectSteamExe,
			buttonAutoRestartPreferences;
	private JComboBox comboBoxProfiles, comboBoxMaxMemory, comboBoxCpuCount,
			comboBoxExThreads, comboBoxMalloc;
	private JCheckBox checkBoxProfiles, checkBoxNoPause, checkBoxWindowMode,
			checkBoxShowScriptErrors, checkBoxMaxMemory, checkBoxCpuCount,
			checkBoxNoSplashScreen, checkBoxDefaultWorld, checkBoxNoLogs,
			checkBoxCheckSignatures, checkBoxExThreads, checkBoxEnableHT,
			checkBoxNoFilePatching, checkBoxAutoRestart, checkBoxMalloc;
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
		vertBox1.add(Box.createVerticalStrut(10));

		JPanel centerPanel = new JPanel();
		GridLayout grid1 = new GridLayout(2, 2);
		centerPanel.setLayout(grid1);
		vertBox1.add(centerPanel, BorderLayout.CENTER);
		this.add(vertBox1);

		launcherOptionsPanel = new JPanel();
		launcherOptionsPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Launcher Options"));

		performancePanel = new JPanel();
		performancePanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Performance"));

		/* Launch options */
		launcherOptionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		Box vBox = Box.createVerticalBox();
		launcherOptionsPanel.add(vBox);
		{
			checkBoxProfiles = new JCheckBox();
			checkBoxProfiles.setText("Profile:   ");
			checkBoxProfiles.setFocusable(false);
			comboBoxProfiles = new JComboBox();
			javax.swing.filechooser.FileSystemView fsv = javax.swing.filechooser.FileSystemView
					.getFileSystemView();
			File myDocuments = fsv.getDefaultDirectory();
			List<String> listProfileNames = new ArrayList<String>();
			if (myDocuments != null) {
				File[] subfiles = myDocuments.listFiles();
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
			String[] tab = new String[listProfileNames.size() + 1];
			tab[0] = "Default";
			for (int i = 0; i < listProfileNames.size(); i++) {
				tab[i + 1] = listProfileNames.get(i);
			}
			ComboBoxModel profilesModel = new DefaultComboBoxModel(tab);
			comboBoxProfiles.setModel(profilesModel);
			comboBoxProfiles.setFocusable(false);
			comboBoxProfiles.setPreferredSize(new Dimension(120, 8));
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxProfiles);
			hBox.add(comboBoxProfiles);
			vBox.add(hBox);
		}
		{
			checkBoxShowScriptErrors = new JCheckBox();
			checkBoxShowScriptErrors.setText("Show script errors");
			checkBoxShowScriptErrors.setFocusable(false);
			checkBoxShowScriptErrors.setFont(new Font("Tohama", Font.BOLD, 11));
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
			checkBoxNoFilePatching = new JCheckBox();
			checkBoxNoFilePatching.setText("No File Patching");
			checkBoxNoFilePatching.setFocusable(false);
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxNoFilePatching);
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
			checkBoxAutoRestart = new JCheckBox();
			checkBoxAutoRestart.setText("Auto-restart");
			// checkBoxAutoRestart
			// .setPreferredSize(new java.awt.Dimension(90, 23));
			checkBoxAutoRestart.setFocusable(false);
			buttonAutoRestartPreferences = new JButton();
			ImageIcon autoRestartOptionsIcon = new ImageIcon(PREFERENCES);
			buttonAutoRestartPreferences.setIcon(autoRestartOptionsIcon);
			buttonAutoRestartPreferences.setFocusable(false);
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxAutoRestart);
			// hBox.add(buttonAutoRestartPreferences);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
		}

		/* Performances */
		performancePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		vBox = Box.createVerticalBox();
		performancePanel.add(vBox);
		{
			checkBoxMaxMemory = new JCheckBox();
			checkBoxMaxMemory.setText("Max Memory:");
			checkBoxMaxMemory.setFocusable(false);
			checkBoxMaxMemory.setPreferredSize(new java.awt.Dimension(100, 23));
			comboBoxMaxMemory = new JComboBox();
			comboBoxMaxMemory.setPreferredSize(new java.awt.Dimension(70, 23));
			comboBoxMaxMemory.setFocusable(false);
			ComboBoxModel maxMemoryModel = new DefaultComboBoxModel(
					new String[] {
							"",
							Integer.toString(MaxMemoryValues.MIN.getValue()),
							Integer.toString(MaxMemoryValues.MEDIUM.getValue()),
							Integer.toString(MaxMemoryValues.MAX.getValue()) });
			comboBoxMaxMemory.setModel(maxMemoryModel);
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxMaxMemory);
			hBox.add(comboBoxMaxMemory);
			vBox.add(hBox);
		}
		{
			checkBoxCpuCount = new JCheckBox();
			checkBoxCpuCount.setText("CPU Count:");
			checkBoxCpuCount.setFocusable(false);
			checkBoxCpuCount.setPreferredSize(new java.awt.Dimension(100, 23));
			comboBoxCpuCount = new JComboBox();
			comboBoxCpuCount.setPreferredSize(new java.awt.Dimension(70, 23));
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
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxCpuCount);
			hBox.add(comboBoxCpuCount);
			vBox.add(hBox);
		}
		{
			checkBoxExThreads = new JCheckBox();
			checkBoxExThreads.setText("exThreads:");
			checkBoxExThreads.setFocusable(false);
			checkBoxExThreads.setPreferredSize(new java.awt.Dimension(100, 23));
			comboBoxExThreads = new JComboBox();
			comboBoxExThreads.setPreferredSize(new java.awt.Dimension(70, 23));
			comboBoxExThreads.setFocusable(false);
			ComboBoxModel exThreadsModel = new DefaultComboBoxModel(
					new String[] { "", "0", "1", "3", "5", "7" });
			comboBoxExThreads.setModel(exThreadsModel);
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxExThreads);
			hBox.add(comboBoxExThreads);
			vBox.add(hBox);
		}
		{
			checkBoxMalloc = new JCheckBox();
			checkBoxMalloc.setText("Malloc");
			checkBoxMalloc.setFocusable(false);
			checkBoxMalloc.setPreferredSize(new java.awt.Dimension(100, 23));
			comboBoxMalloc = new JComboBox();
			comboBoxMalloc.setFocusable(false);
			comboBoxMalloc.setPreferredSize(new java.awt.Dimension(70, 25));
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxMalloc);
			hBox.add(comboBoxMalloc);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
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

		/* Run parameters */
		runParametersTextArea = new JTextArea();
		runParametersTextArea.setFont(new Font("Tohama", Font.ITALIC, 11));
		runParametersTextArea.setLineWrap(true);
		runParametersTextArea.setFont(new Font("Tohama", Font.PLAIN, 11));
		runParametersTextArea.setEditable(false);
		runParametersTextArea.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		scrollPaneRunParameters = new JScrollPane(runParametersTextArea);
		scrollPaneRunParameters.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Run Parameters"));

		/* Additional parameters */
		additionalParametersTextArea = new JTextArea();
		additionalParametersTextArea
				.setFont(new Font("Tohama", Font.ITALIC, 11));
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
		JPanel southPanel = new JPanel();
		GridLayout grid2 = new GridLayout(0, 1);
		southPanel.setLayout(grid2);
		this.add(southPanel, BorderLayout.SOUTH);
		{
			armaPanel = new JPanel();
			armaPanel.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(),
					"ArmA III Executable Location (game/server)"));
			southPanel.add(armaPanel);
			armaPanel.setLayout(new BorderLayout());

			vBox = Box.createVerticalBox();
			{
				JPanel panel1 = new JPanel();
				panel1.setLayout(new BorderLayout());
				textFieldArmAExecutableLocation = new JTextField();
				textFieldArmAExecutableLocation.setEditable(false);
				buttonSelectArmAExe = new JButton("Select");
				panel1.add(textFieldArmAExecutableLocation, BorderLayout.CENTER);
				panel1.add(buttonSelectArmAExe, BorderLayout.EAST);
				vBox.add(panel1);
			}
			armaPanel.add(vBox);
		}

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
		checkBoxNoFilePatching.addActionListener(new ActionListener() {
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
		buttonAutoRestartPreferences.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonAutoRestartPreferencesPerformed();
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
				buttonSelectMallocDllPathPerformed();
			}
		});
		checkBoxEnableHT.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxEnableHTPerformed();
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
		checkBoxNoFilePatching.setToolTipText("Load only PBO files");
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
		checkBoxNoSplashScreen.setToolTipText("Disables splash screens");
		checkBoxDefaultWorld.setToolTipText("No world loaded at game startup");
		checkBoxNoLogs.setToolTipText("Do no write errors into RPT file");
		checkBoxAutoRestart.setToolTipText("Auto-restart game/server");
		checkBoxMalloc.setToolTipText("Sets memory allocator");
		comboBoxMalloc.setToolTipText("Sets memory allocator");
	}

	public void init() {

		/*
		 * Launcher options Automatically updates Run Parameters
		 */
		updateOptions();

		/* Additional Parameters */
		try {
			String additionalParameters = profileService
					.getAdditionalParameters();
			if (additionalParameters != null) {
				additionalParametersTextArea.setText(additionalParameters);
			}
		} catch (ProfileException e) {
			e.printStackTrace();
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
			configurationService.setGameProfile(gameProfileName);
		} else {
			checkBoxProfiles.setSelected(false);
			configurationService.setGameProfile(null);
		}
		updateRunParameters();
	}

	private void checkBoxShowScriptErrorsPerformed() {
		configurationService
				.setCheckBoxShowScriptErrors(checkBoxShowScriptErrors
						.isSelected());
		updateRunParameters();
	}

	private void checkBoxNoPausePerformed() {
		configurationService.setCheckBoxNoPause(checkBoxNoPause.isSelected());
		updateRunParameters();
	}

	private void checkBoxNoFilePatchingPerformed() {
		configurationService.setCheckBoxNoFilePatching(checkBoxNoFilePatching
				.isSelected());
		updateRunParameters();
	}

	private void checkBoxWindowModePerformed() {
		configurationService.setCheckBoxWindowMode(checkBoxWindowMode
				.isSelected());
		updateRunParameters();
	}

	private void checkBoxCheckSignaturesPerformed() {
		configurationService.setCheckBoxCheckSignatures(checkBoxCheckSignatures
				.isSelected());
		updateRunParameters();
	}

	private void checkBoxAutoRestartPerformed() {
		configurationService.setCheckBoxAutoRestart(checkBoxAutoRestart
				.isSelected());
	}

	private void buttonAutoRestartPreferencesPerformed() {
		AutoRestartPanel autoRestartPanel = new AutoRestartPanel(facade);
		autoRestartPanel.setVisible(true);

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
			configurationService.setMaxMemory(maxMemory);
		} else {
			checkBoxMaxMemory.setSelected(false);
			configurationService.setMaxMemory(null);
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
			configurationService.setCpuCount(cpuCount);
			checkBoxEnableHT.setSelected(false);
			checkBoxEnableHT.setEnabled(false);
			configurationService.setEnableHT(false);
		} else {
			checkBoxCpuCount.setSelected(false);
			configurationService.setCpuCount(null);
			checkBoxEnableHT.setEnabled(true);;
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
			configurationService.setExThreads(exThreads);
		} else {
			checkBoxExThreads.setSelected(false);
			configurationService.setExThreads(null);
		}
		updateRunParameters();
	}

	private void checkBoxMallocPerformed() {
		if (!checkBoxMalloc.isSelected()) {
			comboBoxMalloc.setSelectedIndex(0);
		}
		updateRunParameters();
	}

	private void buttonSelectMallocDllPathPerformed() {

		String mallocDll = (String) comboBoxMalloc.getSelectedItem();
		if (mallocDll == null) {
			return;
		}
		if (!mallocDll.isEmpty()) {
			checkBoxMalloc.setSelected(true);
			configurationService.setMalloc(mallocDll);
		} else {
			checkBoxMalloc.setSelected(false);
			configurationService.setMalloc(null);
		}
		updateRunParameters();
	}

	private void checkBoxEnableHTPerformed() {
		configurationService.setEnableHT(checkBoxEnableHT.isSelected());
		updateRunParameters();
	}

	private void checkBoxNoSplashScreenPerformed() {
		configurationService.setNoSplashScreen(checkBoxNoSplashScreen
				.isSelected());
		updateRunParameters();
	}

	private void checkBoxDefaultWorldPerformed() {
		configurationService.setDefaultWorld(checkBoxDefaultWorld.isSelected());
		updateRunParameters();
	}

	private void checkBoxNoLogsPerformed() {
		configurationService.setNoLogs(checkBoxNoLogs.isSelected());
		updateRunParameters();
	}

	private void buttonSelectArmAExePerformed() {

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

		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnVal = fc.showOpenDialog(LauncherOptionsPanel.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			configurationService.setArmA3ExePath(path);
			if (file.getParent() != null) {
				String parentPath = file.getParentFile().getAbsolutePath();
				Set<String> set = configurationService
						.getAddonSearchDirectoryPaths();
				Iterator iter = set.iterator();
				List<String> list = new ArrayList<String>();
				while (iter.hasNext()) {
					list.add((String) iter.next());
				}
				boolean contains = false;
				for (int i = 0; i < list.size(); i++) {
					String osName = System.getProperty("os.name");
					if (osName.contains("Windows")) {
						if (parentPath.equalsIgnoreCase(list.get(i))) {
							contains = true;
						}
					} else {
						if (parentPath.equals(list.get(i))) {
							contains = true;
						}
					}
				}
				if (!contains) {
					configurationService.getAddonSearchDirectoryPaths().add(
							parentPath);
					facade.getAddonOptionsPanel()
							.updateAddonSearchDirectories();
				}
			}
			addonService.resetAvailableAddonTree();
			facade.getAddonsPanel().updateAvailableAddons();
			facade.getAddonsPanel().updateAddonGroups();
			facade.getAddonsPanel().expandAddonGroups();
			textFieldArmAExecutableLocation.setText(path);
		} else {
			configurationService.setArmA3ExePath(null);
			textFieldArmAExecutableLocation.setText("");
		}
	}

	private void buttonSelectSteamExePerformed() {

		String steamExePath = configurationService.determineSteamExePath();
		JFileChooser fc = new JFileChooser(steamExePath);
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnVal = fc.showOpenDialog(LauncherOptionsPanel.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			configurationService.setSteamExePath(path);
			textFieldSteamExecutableLocation.setText(path);
		} else {
			configurationService.setSteamExePath(null);
			textFieldSteamExecutableLocation.setText("");
		}
	}

	/* Update Run Parameters */
	public void updateRunParameters() {

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

	/* Additional Parameters */
	public void setAdditionalParameters() {

		String additionalParameters = additionalParametersTextArea.getText()
				.trim();
		try {
			// System.out.println(additionalParameters);
			profileService.setAdditionalParameters(additionalParameters);
		} catch (ProfileException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void updateAdditionalParameters() {

		try {
			String additionalParameters = profileService
					.getAdditionalParameters();
			if (additionalParameters != null) {
				additionalParametersTextArea.setText(additionalParameters);
				additionalParametersTextArea.setCaretPosition(0);
				additionalParametersTextArea.updateUI();
			} else {
				additionalParametersTextArea.setText("");
			}
		} catch (ProfileException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void updateOptions() {

		LauncherOptionsDTO launcherOptionsDTO = configurationService
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
		checkBoxNoFilePatching.setSelected(launcherOptionsDTO
				.isNoFilePatching());
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
		checkBoxNoSplashScreen.setSelected(launcherOptionsDTO
				.isNoSplashScreen());
		checkBoxDefaultWorld.setSelected(launcherOptionsDTO.isDefaultWorld());
		checkBoxNoLogs.setSelected(launcherOptionsDTO.isNoLogs());

		/* ArmA 3 Executable Location and Malloc */
		String arma3ExePath = launcherOptionsDTO.getArma3ExePath();
		if (arma3ExePath != null) {
			File arma3ExeFile = new File(arma3ExePath);
			if (arma3ExeFile.exists()) {
				textFieldArmAExecutableLocation.setText(launcherOptionsDTO
						.getArma3ExePath());
			}
			if (arma3ExeFile.getParentFile() != null) {
				File parent = new File(arma3ExeFile.getParent());
				List<String> list = new ArrayList<String>();
				if (parent != null) {
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
				} else {
					comboBoxMalloc.setSelectedIndex(0);
				}
			}
		}
	}

	/* additionalParametersTextArea modification listener */
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		String additionalParameters = additionalParametersTextArea.getText();
		try {
			profileService.setAdditionalParameters(additionalParameters);
		} catch (ProfileException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		String additionalParameters = additionalParametersTextArea.getText();
		try {
			profileService.setAdditionalParameters(additionalParameters);
		} catch (ProfileException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		String additionalParameters = additionalParametersTextArea.getText();
		try {
			profileService.setAdditionalParameters(additionalParameters);
		} catch (ProfileException e) {
			e.printStackTrace();
		}
	}
}
