package fr.soe.a3s.ui.mainEditor;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import fr.soe.a3s.constant.GameExecutables;
import fr.soe.a3s.constant.GameVersions;
import fr.soe.a3s.constant.MinimizationType;
import fr.soe.a3s.domain.configration.Configuration;
import fr.soe.a3s.dto.configuration.FavoriteServerDTO;
import fr.soe.a3s.dto.configuration.LauncherOptionsDTO;
import fr.soe.a3s.exception.LaunchException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.service.CommonService;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.service.LaunchService;
import fr.soe.a3s.service.PreferencesService;
import fr.soe.a3s.service.RepositoryService;
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
public class LaunchPanel extends JPanel implements UIConstants {

	private Facade facade;
	private JLabel gameVersionLabel, joinServerLabel;
	private JComboBox gameVersionComboBox, joinServerComboBox;
	private JButton startButton;
	private ConfigurationService configurationService = new ConfigurationService();
	private CommonService commonService = new CommonService();
	private LaunchService launchService = new LaunchService();

	public LaunchPanel(Facade facade) {

		this.facade = facade;
		facade.setLaunchPanel(this);
		this.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

		joinServerLabel = new JLabel("Join Server");
		this.add(joinServerLabel);
		joinServerComboBox = new JComboBox();
		joinServerComboBox.setFocusable(false);
		this.add(joinServerComboBox);
		joinServerComboBox.setPreferredSize(new java.awt.Dimension(150, 27));
		gameVersionLabel = new JLabel("Game Version");
		this.add(gameVersionLabel);
		gameVersionComboBox = new JComboBox();
		ComboBoxModel gameVersionModel = new DefaultComboBoxModel(new String[] {
				GameVersions.ARMA3.getDescription(),
				GameVersions.ARMA3_AIA.getDescription() });
		gameVersionComboBox.setModel(gameVersionModel);
		gameVersionComboBox.setFocusable(false);
		this.add(gameVersionComboBox);
		gameVersionComboBox.setPreferredSize(new java.awt.Dimension(100, 27));
		startButton = new JButton("Start Game");
		startButton.setFont(new Font("Tohama", Font.BOLD, 11));
		this.add(startButton);
		startButton.setPreferredSize(new java.awt.Dimension(110, 27));

		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						startButtonPerformed();
					}
				});
			}
		});
		joinServerComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				serverSelectionPerformed();
			}
		});
		gameVersionComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gameVersionSelectionPerformed();
			}
		});
	}

	public void init() {

		String serverName = configurationService.getServerName();
		String gameVersion = configurationService.getGameVersion();
		List<FavoriteServerDTO> favoriteServersDTO = configurationService
				.getFavoriteServers();
		ComboBoxModel joinServerModel = new DefaultComboBoxModel(
				new String[] { "" });
		this.joinServerComboBox.setModel(joinServerModel);

		for (int i = 0; i < favoriteServersDTO.size(); i++) {
			this.joinServerComboBox
					.addItem(favoriteServersDTO.get(i).getName());
		}

		if (serverName != null) {
			this.joinServerComboBox.setSelectedItem(serverName);
		}

		if (gameVersion != null) {
			this.gameVersionComboBox.setSelectedItem(gameVersion);
		}
	}

	private void serverSelectionPerformed() {

		String serverName = (String) this.joinServerComboBox.getSelectedItem();
		if (serverName.isEmpty()) {
			configurationService.saveServerName(null);
		} else {
			 configurationService.saveServerName(serverName);
			// this.joinServerComboBox.setSelectedIndex(0);
			// JoinServerPanel joinServerPanel = new JoinServerPanel(facade);
			// joinServerPanel.setVisible(true);
			// configurationService.saveServerName(null);
			// return;
		}
		facade.getLaunchOptionsPanel().updateRunParameters();
	}

	private void gameVersionSelectionPerformed() {

		String gameVersion = (String) this.gameVersionComboBox
				.getSelectedItem();
		configurationService.setGameVersion(gameVersion);
		configurationService.determineAiAOptions();
		facade.getLaunchOptionsPanel().updateRunParameters();
	}

	private void startButtonPerformed() {

		/* AiA */
		configurationService.determineAiAOptions();

		try {
			// Check selected addons
			facade.getAddonsPanel().updateAvailableAddons();
			String message = launchService.checkSelectedAddons();
			if (message != null) {
				facade.getAddonsPanel().expandAddonGroups();
				throw new LaunchException(message);
			}

			// Check if addons are not being downloaded
			RepositoryService repositoryService = new RepositoryService();
			if (repositoryService.isDownloading()) {
				message = "Addons are currently being updated.";
				throw new LaunchException(message);
			}

			// Check ArmA 3 executable location
			launchService.checkArmA3ExecutableLocation();

			// Check @AllinArma location
			launchService.checkAllinArmALocation();

		} catch (LaunchException e) {
			// Failed to launch!
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Failed to launch ArmA 3.\n" + e.getMessage(),
					"ArmA 3 Start Game", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Run External Applications
		try {
			launchService.launchExternalApplications();
		} catch (Exception e) {
			// Failed to launch!
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "ArmA 3 Start Game",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Run ArmA 3
		try {
			launchService.launchArmA3();
		} catch (LaunchException e) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Failed to launch ArmA 3.", "ArmA 3 Start Game",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		PreferencesService preferencesService = new PreferencesService();
		MinimizationType minimize = preferencesService.getPreferences()
				.getLaunchPanelGameLaunch();

		if (MinimizationType.TASK_BAR.equals(minimize)) {
			facade.getMainPanel().setToTaskBar();
		} else if (MinimizationType.TRAY.equals(minimize)) {
			facade.getMainPanel().setVisible(false);
			facade.getMainPanel().setToTray();
		}
	}
}
