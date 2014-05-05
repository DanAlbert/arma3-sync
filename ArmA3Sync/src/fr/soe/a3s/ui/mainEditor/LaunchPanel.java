package fr.soe.a3s.ui.mainEditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import fr.soe.a3s.constant.GameVersions;
import fr.soe.a3s.constant.MinimizationType;
import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.dto.configuration.FavoriteServerDTO;
import fr.soe.a3s.exception.LaunchException;
import fr.soe.a3s.exception.RepositoryException;
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

	private final Facade facade;
	private JLabel gameVersionLabel, joinServerLabel;
	private JComboBox gameVersionComboBox, joinServerComboBox;
	private JButton startButton;
	/* Data */
	private Map<String, Object> map = new TreeMap<String, Object>();
	/* Services */
	private final ConfigurationService configurationService = new ConfigurationService();
	private final CommonService commonService = new CommonService();
	private final LaunchService launchService = new LaunchService();
	private final RepositoryService repositoryService = new RepositoryService();;

	public LaunchPanel(Facade facade) {

		this.facade = facade;
		facade.setLaunchPanel(this);

		this.setLayout(new BorderLayout());
		{
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
			joinServerLabel = new JLabel("Join Server");
			panel.add(joinServerLabel);
			this.add(panel, BorderLayout.WEST);
		}
		{
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			joinServerComboBox = new JComboBox();
			joinServerComboBox.setFocusable(false);
			panel.add(joinServerComboBox, BorderLayout.CENTER);
			this.add(panel, BorderLayout.CENTER);
			JPanel northPanel = new JPanel();
			northPanel.setPreferredSize(new Dimension(100, 5));
			panel.add(northPanel, BorderLayout.NORTH);
			JPanel southPanel = new JPanel();
			southPanel.setPreferredSize(new Dimension(100, 5));
			panel.add(southPanel, BorderLayout.SOUTH);
		}
		{
			JPanel rightPanel = new JPanel();
			rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
			this.add(rightPanel, BorderLayout.EAST);
			{
				{
					gameVersionLabel = new JLabel("Game Version");
					rightPanel.add(gameVersionLabel);
					gameVersionComboBox = new JComboBox();
					ComboBoxModel gameVersionModel = new DefaultComboBoxModel(
							new String[] { GameVersions.ARMA3.getDescription(),
									GameVersions.ARMA3_AIA.getDescription() });
					gameVersionComboBox.setModel(gameVersionModel);
					gameVersionComboBox.setFocusable(false);
					rightPanel.add(gameVersionComboBox);
					gameVersionComboBox
							.setPreferredSize(new java.awt.Dimension(100, 27));
				}
				{
					startButton = new JButton("Start Game");
					startButton.setFont(new Font("Tohama", Font.BOLD, 11));
					rightPanel.add(startButton);
					startButton
							.setPreferredSize(new java.awt.Dimension(110, 27));
				}
			}
		}

		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						startButtonPerformed();
					}
				});
			}
		});
		joinServerComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				serverSelectionPerformed();
			}
		});
		gameVersionComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gameVersionSelectionPerformed();
			}
		});
	}

	public void init() {

		String serverName = configurationService.getServerName();
		String defaultModset = configurationService.getDefaultModset();
		String gameVersion = configurationService.getGameVersion();

		List<FavoriteServerDTO> favoriteServersDTO = configurationService
				.getFavoriteServers();
		ComboBoxModel joinServerModel = new DefaultComboBoxModel(
				new String[] { "" });
		this.joinServerComboBox.setModel(joinServerModel);
		for (int i = 0; i < favoriteServersDTO.size(); i++) {
			String stg = favoriteServersDTO.get(i).getName();
			if (!(favoriteServersDTO.get(i).getModsetName() == null)
					&& !("".equals(favoriteServersDTO.get(i).getModsetName()
							.trim()))) {
				stg = stg + " - " + favoriteServersDTO.get(i).getModsetName();
			}
			this.joinServerComboBox.addItem(stg);
		}

		if (serverName != null) {
			if (defaultModset != null) {
				this.joinServerComboBox.setSelectedItem(serverName + " - "
						+ defaultModset);
			} else {
				this.joinServerComboBox.setSelectedItem(serverName);
			}
		}

		if (gameVersion != null) {
			this.gameVersionComboBox.setSelectedItem(gameVersion);
		}

		List<RepositoryDTO> repositoryDTOs = repositoryService
				.getRepositories();

		map = new TreeMap<String, Object>();
		for (RepositoryDTO repositoryDTO : repositoryDTOs) {
			map.put(repositoryDTO.getName(), repositoryDTO);
			try {
				List<EventDTO> list2 = repositoryService
						.getEvents(repositoryDTO.getName());
				if (list2 != null) {
					for (EventDTO eventDTO : list2) {
						map.put(eventDTO.getName(), eventDTO);
					}
				}
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
	}

	private void updatejoinServerComboBox() {

		List<FavoriteServerDTO> favoriteServersDTO = configurationService
				.getFavoriteServers();
		ComboBoxModel joinServerModel = new DefaultComboBoxModel(
				new String[] { "" });
		this.joinServerComboBox.setModel(joinServerModel);
		for (int i = 0; i < favoriteServersDTO.size(); i++) {
			this.joinServerComboBox
					.addItem(favoriteServersDTO.get(i).getName());
		}
	}

	private void serverSelectionPerformed() {

		String selection = (String) this.joinServerComboBox.getSelectedItem();
		if (selection.isEmpty()) {
			configurationService.saveServerName(null);
			configurationService.setDefautlModset(null);
		} else {
			int index = selection.indexOf("-");
			if (index == -1) {
				String serverName = selection;
				configurationService.saveServerName(serverName);
			} else {
				String serverName = selection.substring(0, index - 1).trim();
				String modsetName = selection.substring(index + 1).trim();
				configurationService.saveServerName(serverName);
				configurationService.setDefautlModset(modsetName);
				Object objectDTO = map.get(modsetName);// null if not found
				if (objectDTO instanceof RepositoryDTO) {
					List<String> list = new ArrayList<String>();
					list.add(((RepositoryDTO) objectDTO).getName());
					facade.getAddonsPanel().createGroupFromRepository(list);
				} else if (objectDTO instanceof EventDTO) {
					List<EventDTO> eventDTOs = new ArrayList<EventDTO>();
					eventDTOs.add((EventDTO) objectDTO);
					facade.getAddonsPanel().createGroupFromEvents(eventDTOs);
				}
				facade.getAddonsPanel().selectModset(modsetName);
			}
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
			// Update join server addons selection
			serverSelectionPerformed();

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
		} else if (MinimizationType.CLOSE.equals(minimize)) {
			facade.getMainPanel().menuExitPerformed();
		}
	}
}
