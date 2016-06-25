package fr.soe.a3s.ui.main;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.Box;
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
import fr.soe.a3s.constant.RepositoryStatus;
import fr.soe.a3s.controller.ObserverEnd;
import fr.soe.a3s.controller.ObserverError;
import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.dto.configuration.FavoriteServerDTO;
import fr.soe.a3s.exception.LaunchException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.CommonService;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.service.LaunchService;
import fr.soe.a3s.service.PreferencesService;
import fr.soe.a3s.service.ProfileService;
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
	private final LaunchService launchService = new LaunchService();
	private final ConfigurationService configurationService = new ConfigurationService();
	private final CommonService commonService = new CommonService();
	private final RepositoryService repositoryService = new RepositoryService();
	private final ProfileService profileService = new ProfileService();

	public LaunchPanel(Facade facade) {

		this.facade = facade;
		facade.setLaunchPanel(this);

		this.setLayout(new BorderLayout());
		{
			JPanel northPanel = new JPanel();
			this.add(northPanel, BorderLayout.NORTH);
			JPanel southPanel = new JPanel();
			this.add(southPanel, BorderLayout.SOUTH);
		}
		{
			Box hBox = Box.createHorizontalBox();
			this.add(hBox, BorderLayout.WEST);
			{
				hBox.add(Box.createHorizontalStrut(10));
				joinServerLabel = new JLabel("Join Server");
				hBox.add(joinServerLabel);
				hBox.add(Box.createHorizontalStrut(10));
			}
		}
		{
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			joinServerComboBox = new JComboBox();
			joinServerComboBox.setFocusable(false);
			panel.add(joinServerComboBox);
			this.add(panel, BorderLayout.CENTER);
		}

		{
			JPanel rightPanel = new JPanel();
			rightPanel.setLayout(new BorderLayout());
			this.add(rightPanel, BorderLayout.EAST);
			{
				Box hBox = Box.createHorizontalBox();
				rightPanel.add(hBox);
				{
					hBox.add(Box.createHorizontalStrut(10));
					gameVersionLabel = new JLabel("Game Version");
					hBox.add(gameVersionLabel);
					hBox.add(Box.createHorizontalStrut(10));
				}
				{
					gameVersionComboBox = new JComboBox();
					ComboBoxModel gameVersionModel = new DefaultComboBoxModel(
							new String[] { GameVersions.ARMA3.getDescription(),
									GameVersions.ARMA3_AIA.getDescription() });
					gameVersionComboBox.setModel(gameVersionModel);
					gameVersionComboBox.setFocusable(false);
					hBox.add(gameVersionComboBox);
					hBox.add(Box.createHorizontalStrut(10));
				}
				{
					JPanel panel = new JPanel();
					panel.setLayout(new BorderLayout());
					startButton = new JButton("Start Game");
					Font boldFont = startButton.getFont().deriveFont(Font.BOLD);
					startButton.setFont(boldFont);
					panel.add(startButton, BorderLayout.CENTER);
					hBox.add(panel);
					hBox.add(Box.createHorizontalStrut(10));
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

		joinServerComboBox.setEnabled(false);

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
					&& !("".equals(favoriteServersDTO.get(i).getModsetName()))) {
				stg = stg + " - " + favoriteServersDTO.get(i).getModsetName();
			}
			this.joinServerComboBox.addItem(stg);
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

		joinServerComboBox.setEnabled(true);
	}

	private void serverSelectionPerformed() {

		String selection = (String) this.joinServerComboBox.getSelectedItem();
		int selectedIndex = this.joinServerComboBox.getSelectedIndex();
		if (selection == null || "".equals(selection)) {
			configurationService.setServerName(null);
			configurationService.setDefautlModset(null);
		} else {
			List<FavoriteServerDTO> favoriteServersDTO = configurationService
					.getFavoriteServers();
			if (selectedIndex != -1
					&& selectedIndex <= favoriteServersDTO.size()
					&& !favoriteServersDTO.isEmpty()) {
				FavoriteServerDTO favoriteServerDTO = favoriteServersDTO
						.get(selectedIndex - 1);
				String serverName = favoriteServerDTO.getName();
				String modsetName = favoriteServerDTO.getModsetName();
				configurationService.setServerName(serverName);
				configurationService.setDefautlModset(modsetName);
				if (modsetName != null) {
					Object objectDTO = map.get(modsetName);// null if not found
					if (objectDTO instanceof RepositoryDTO) {
						List<String> list = new ArrayList<String>();
						String name = ((RepositoryDTO) objectDTO).getName();
						list.add(name);
						facade.getAddonsPanel().createGroupFromRepository(list);
					} else if (objectDTO instanceof EventDTO) {
						List<EventDTO> eventDTOs = new ArrayList<EventDTO>();
						EventDTO eventDTO = (EventDTO) objectDTO;
						eventDTOs.add(eventDTO);
						facade.getAddonsPanel()
								.createGroupFromEvents(eventDTOs);
					}
					facade.getAddonsPanel().selectModset(modsetName);
				}
			} else {
				String serverName = selection;
				configurationService.setServerName(serverName);
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

		/* Blocking messages */
		try {
			// Update join server addons selection
			serverSelectionPerformed();

			// Check selected addons
			facade.getAddonsPanel().updateAvailableAddons();
			facade.getAddonsPanel().updateAddonGroups();
			String message = launchService.checkSelectedAddons();
			if (message != null) {
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

		/* Warning messages */
		// Check if addons are not being downloaded
		if (repositoryService.isDownloading()) {
			String message = "Addons are currently being updated." + "\n"
					+ "Proceed with launch anyway?";
			int res = JOptionPane.showConfirmDialog(facade.getMainPanel(),
					message, "ArmA 3 Start Game", JOptionPane.YES_NO_OPTION);
			if (res == 1) {
				return;
			}
		}

		// Check if selected modset is updated
		String modsetName = configurationService.getDefaultModset();
		if (modsetName != null) {
			String name = null;
			Object objectDTO = map.get(modsetName);// null if not found
			if (objectDTO instanceof RepositoryDTO) {
				name = ((RepositoryDTO) objectDTO).getName();

			} else if (objectDTO instanceof EventDTO) {
				EventDTO eventDTO = (EventDTO) objectDTO;
				name = eventDTO.getRepositoryName();
			}
			if (name != null) {
				RepositoryStatus status = RepositoryStatus.INDETERMINATED;
				try {
					status = repositoryService.getRepositoryStatus(name);
				} catch (Exception e) {
				}
				if (status.equals(RepositoryStatus.UPDATED)) {
					String message = "Repository: " + name
							+ " have been updated." + "\n"
							+ "Proceed with launch anyway?";
					int res = JOptionPane.showConfirmDialog(
							facade.getMainPanel(), message,
							"ArmA 3 Start Game", JOptionPane.YES_NO_OPTION);
					if (res == 1) {
						return;
					}
				}
			}
		}

		launchService.getLauncherDAO().addObserverError(new ObserverError() {
			@Override
			public void error(List<Exception> errors) {
				JOptionPane.showMessageDialog(facade.getMainPanel(), errors
						.get(0).getMessage(), "ArmA 3 Start Game",
						JOptionPane.ERROR_MESSAGE);
			}
		});

		launchService.getLauncherDAO().addObserverEnd(new ObserverEnd() {
			@Override
			public void end() {
				PreferencesService preferencesService = new PreferencesService();
				MinimizationType minimize = preferencesService.getPreferences()
						.getLaunchPanelGameLaunch();
				if (MinimizationType.TASK_BAR.equals(minimize)) {
					facade.getMainPanel().setToTaskBar();
				} else if (MinimizationType.TRAY.equals(minimize)) {
					facade.getMainPanel().setVisible(false);
					facade.getMainPanel().setToTray();
				} else if (MinimizationType.CLOSE.equals(minimize)) {
					if (!profileService.isAutoRestart()) {
						facade.getMainPanel().menuExitPerformed();
					}
				}
			}
		});

		// Run External Applications
		launchService.launchExternalApplications();

		// Run ArmA 3
		launchService.launchArmA3();
	}
}
