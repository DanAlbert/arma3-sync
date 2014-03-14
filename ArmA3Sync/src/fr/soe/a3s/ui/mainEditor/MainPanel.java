package fr.soe.a3s.ui.mainEditor;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import fr.soe.a3s.constant.MinimizationType;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.dto.ServerInfoDTO;
import fr.soe.a3s.dto.configuration.LauncherOptionsDTO;
import fr.soe.a3s.dto.configuration.PreferencesDTO;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.ProfileException;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.service.AbstractConnexionService;
import fr.soe.a3s.service.CommonService;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.service.ConnexionServiceFactory;
import fr.soe.a3s.service.FtpService;
import fr.soe.a3s.service.PreferencesService;
import fr.soe.a3s.service.ProfileService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.about.AboutPanel;
import fr.soe.a3s.ui.acreEditor.FirstPageACREInstallerPanel;
import fr.soe.a3s.ui.aiaEditor.AiaInstallerPanel;
import fr.soe.a3s.ui.autoConfigEditor.AutoConfigExportPanel;
import fr.soe.a3s.ui.autoConfigEditor.AutoConfigImportPanel;
import fr.soe.a3s.ui.profileEditor.ProfilePanel;
import fr.soe.a3s.ui.repositoryEditor.RepositoryPanel;
import fr.soe.a3s.ui.rptEditor.RptViewerPanel;
import fr.soe.a3s.ui.tfarEditor.FirstPageTFARInstallerPanel;

public class MainPanel extends JFrame implements UIConstants {

	private final Facade facade;
	private static final String TAB_TITLE_ADDONS = "Addons";
	private static final String TAB_TITLE_ADDON_OPTIONS = "Addon Options";
	private static final String TAB_TITLE_LAUNCH_OPTIONS = "Launcher Options";
	private static final String TAB_TITLE_ONLINE = "Online";
	private static final String TAB_TITLE_EXTENAL_APPS = "External Apps";
	private static final String TAB_TITLE_SYNC = "Repositories";
	private JMenuBar menuBar;
	private JMenu menuProfiles, menuGroups, menuHelp, menuTools,
			menuItemAutoConfig;
	private JMenuItem menuItemEdit, menuItemHelp, menuItemuUpdates,
			menuItemAbout, menuItemPreferences, menuItemACREwizard,
			menuItemRPTviewer, menuItemAiAwizard, menuItemServerTuner,
			menuItemBISforum, menuItemAutoConfigImport,
			menuItemAutoConfigExport;
	private JTabbedPane tabbedPane;
	private JPanel infoPanel, launchPanel;
	private final ConfigurationService configurationService = new ConfigurationService();
	private final ProfileService profileService = new ProfileService();
	private final CommonService commonService = new CommonService();
	private final PreferencesService preferencesService = new PreferencesService();
	private final RepositoryService repositoryService = new RepositoryService();
	private SystemTray tray;
	private TrayIcon trayIcon;
	private PopupMenu popup;
	private MenuItem launchItem, exitItem;
	private final Map<String, Integer> mapTabIndexes = new LinkedHashMap<String, Integer>();
	private final Container contenu;
	private JMenuItem menuItemAddGroup;
	private JMenuItem menuItemRenameGroup;
	private JMenuItem menuItemRemoveGroup;
	private JMenuItem menuItemTFARwizard;

	public MainPanel(Facade facade) {

		this.facade = facade;
		this.facade.setMainPanel(this);
		setTitle(APPLICATION_NAME);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(true);
		setIconImage(ICON);
		contenu = this.getContentPane();
		this.setLocationRelativeTo(null);
	}

	public void drawGUI() {

		/* Toolbar */
		menuBar = new JMenuBar();
		menuProfiles = new JMenu("Profiles");
		menuBar.add(menuProfiles);
		menuItemEdit = new JMenuItem("Edit", new ImageIcon(EDIT));
		JSeparator s = new JSeparator();
		menuProfiles.add(menuItemEdit);
		menuProfiles.add(s);

		menuGroups = new JMenu("Groups");
		menuItemAddGroup = new JMenuItem("Add");
		menuItemRenameGroup = new JMenuItem("Rename");
		menuItemRemoveGroup = new JMenuItem("Remove");
		menuGroups.add(menuItemAddGroup);
		menuGroups.add(menuItemRenameGroup);
		menuGroups.add(menuItemRemoveGroup);
		menuBar.add(menuGroups);

		menuTools = new JMenu("Tools");
		menuBar.add(menuTools);
		menuItemACREwizard = new JMenuItem("ACRE installer",
				new ImageIcon(ACRE));
		menuTools.add(menuItemACREwizard);
		menuItemTFARwizard = new JMenuItem("TFAR installer",
				new ImageIcon(TFAR));
		menuTools.add(menuItemTFARwizard);

		menuItemAiAwizard = new JMenuItem("AiA tweaker", new ImageIcon(
				TRANSMISSION));
		menuTools.add(menuItemAiAwizard);
		menuItemRPTviewer = new JMenuItem("RPT viewer", new ImageIcon(RPT));
		menuTools.add(menuItemRPTviewer);
		menuHelp = new JMenu("Help");
		menuItemHelp = new JMenuItem("Online Help", new ImageIcon(HELP));
		menuHelp.add(menuItemHelp);
		menuItemBISforum = new JMenuItem("BIS Forum", new ImageIcon(BIS));
		menuHelp.add(menuItemBISforum);
		JSeparator s1 = new JSeparator();
		menuHelp.add(s1);
		menuItemPreferences = new JMenuItem("Preferences", new ImageIcon(
				PREFERENCES));
		menuHelp.add(menuItemPreferences);
		menuItemAutoConfig = new JMenu("Auto-config");
		menuHelp.add(menuItemAutoConfig);
		menuItemAutoConfigImport = new JMenuItem("Import");
		menuItemAutoConfig.add(menuItemAutoConfigImport);
		menuItemAutoConfigExport = new JMenuItem("Export");
		menuItemAutoConfig.add(menuItemAutoConfigExport);
		menuItemuUpdates = new JMenuItem("Check for Updates", new ImageIcon(
				UPDATE));
		menuHelp.add(menuItemuUpdates);
		JSeparator s2 = new JSeparator();
		menuHelp.add(s2);
		menuItemAbout = new JMenuItem("About", new ImageIcon(ABOUT));
		menuHelp.add(menuItemAbout);
		menuBar.add(menuHelp);

		setJMenuBar(menuBar);

		/* Info panel */
		infoPanel = new InfoPanel(facade);
		contenu.add(infoPanel, BorderLayout.NORTH);

		/* Tab panel */
		tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.addTab(TAB_TITLE_ADDONS, new AddonsPanel(facade));
		tabbedPane.addTab(TAB_TITLE_ADDON_OPTIONS,
				new AddonOptionsPanel(facade));
		tabbedPane.addTab(TAB_TITLE_LAUNCH_OPTIONS, new LauncherOptionsPanel(
				facade));
		tabbedPane.addTab(TAB_TITLE_ONLINE, new OnlinePanel(facade));
		tabbedPane.addTab(TAB_TITLE_EXTENAL_APPS,
				new ExternalApplicationsPanel(facade));
		tabbedPane.addTab(TAB_TITLE_SYNC, new SyncPanel(facade));
		contenu.add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.setFocusable(false);

		JPanel sidePanel1 = new JPanel();
		contenu.add(sidePanel1, BorderLayout.EAST);
		JPanel sidePanel2 = new JPanel();
		contenu.add(sidePanel2, BorderLayout.WEST);

		/* Launch panel */
		launchPanel = new LaunchPanel(facade);
		contenu.add(launchPanel, BorderLayout.SOUTH);

		if (SystemTray.isSupported()) {
			/* Tray Icon */
			trayIcon = new TrayIcon(TRAYICON, "ArmA3Sync");
			tray = SystemTray.getSystemTray();
			popup = new PopupMenu();
			trayIcon.setPopupMenu(popup);
			launchItem = new MenuItem("ArmA3Sync");
			exitItem = new MenuItem("Exit");
			popup.add(launchItem);
			popup.addSeparator();
			popup.add(exitItem);
		} else {
			System.out.println("System Tray is not supported by your system.");
		}
		menuItemEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuItemEditPerformed();
			}
		});
		menuItemACREwizard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						menuItemACREwizardPerformed();
					}
				});
			}
		});
		menuItemTFARwizard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						menuItemTFARwizardPerformed();
					}
				});
			}
		});
		menuItemAiAwizard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						menuItemAiAwizardPerformed();
					}
				});
			}
		});
		menuItemRPTviewer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuItemRPTviewerPerformed();
			}
		});
		menuItemHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuItemHelpPerformed();
			}
		});
		menuItemBISforum.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuItemBISforumPerformed();
			}
		});
		menuItemPreferences.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menuItemPreferencesPerformed();
			}
		});
		menuItemAutoConfigImport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menuItemAutoConfigImportPerformed();
			}
		});
		menuItemAutoConfigExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menuItemAutoConfigExportPerformed();
			}
		});
		menuItemuUpdates.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuItemuUpdatesPerformed();
			}
		});
		menuItemAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuItemAboutPerformed();
			}
		});
		if (trayIcon != null) {
			trayIcon.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					trayIconPerformed();
				}
			});
		}
		if (launchItem != null) {
			launchItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					launchTrayItemPerformed();
				}
			});
		}
		if (exitItem != null) {
			exitItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					exitTrayItemPerformed();
				}
			});
		}
		menuItemAddGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menuItemAddGroupPerformed();
			}
		});
		menuItemRenameGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menuItemRenameGroupPerformed();
			}
		});
		menuItemRemoveGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menuItemRemoveGroupPerformed();
			}
		});

		// Add Listeners
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowDeiconified(WindowEvent arg0) {
				trayIconPerformed();
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
			}

			@Override
			public void windowActivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				menuExitPerformed();
			}

			@Override
			public void windowIconified(WindowEvent e) {
				menuIconifiedPerformed();
			}

			@Override
			public void windowOpened(WindowEvent e) {
			}
		});
	}

	public void init() {

		/* Load data */
		try {
			configurationService.read();
		} catch (LoadingException e1) {
			JOptionPane.showMessageDialog(this,
					"An error occured. \n Failded to load configuration.",
					"Error", JOptionPane.ERROR_MESSAGE);
		}

		try {
			profileService.readAll();
		} catch (LoadingException e2) {
			JOptionPane
					.showMessageDialog(
							this,
							"An error occured. \n Failded to load one or more profile(s).",
							"Error", JOptionPane.ERROR_MESSAGE);
		}

		try {
			preferencesService.read();
		} catch (LoadingException e) {
			JOptionPane.showMessageDialog(this,
					"An error occured. \n Failded to load preferences.",
					"Error", JOptionPane.ERROR_MESSAGE);
		}

		try {
			repositoryService.readAll();
		} catch (LoadingException e) {
			JOptionPane
					.showMessageDialog(
							this,
							"An error occured. \n Failded to load on or more repositories.",
							"Error", JOptionPane.ERROR_MESSAGE);
		}

		/* Set previous Height and Width */
		int height = configurationService.getHeight();
		int width = configurationService.getWidth();
		if (height != 0 && width != 0) {
			this.setPreferredSize(new Dimension(width, height));
		} else {
			setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		}
		setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - this.getPreferredSize()
				.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - this.getPreferredSize()
				.getHeight()) / 2);
		this.setLocation(x, y);
		this.pack();

		/* Init active views */
		this.facade.getInfoPanel().init();
		this.facade.getAddonsPanel().init();
		this.facade.getAddonOptionsPanel().init();
		this.facade.getLaunchOptionsPanel().init();
		this.facade.getOnlinePanel().init();
		this.facade.getExternalApplicationsPanel().init();
		this.facade.getSyncPanel().init();
		this.facade.getLaunchPanel().init();

		/* Init Profiles menu */
		updateProfilesMenu();

		/* Update repositories statuts */
		updateRepositoriesStatus();
	}

	/* Menu Actions */

	private void menuItemAddGroupPerformed() {
		tabbedPane.setSelectedIndex(0);
		facade.getAddonsPanel().addPerformed();
	}

	private void menuItemRenameGroupPerformed() {
		tabbedPane.setSelectedIndex(0);
		facade.getAddonsPanel().renamePormed();
	}

	private void menuItemRemoveGroupPerformed() {
		tabbedPane.setSelectedIndex(0);
		facade.getAddonsPanel().removePerformed();
	}

	private void menuItemEditPerformed() {

		facade.getAddonsPanel().saveAddonGroups();
		try {
			profileService.saveLauncherOptions(configurationService
					.getProfileName());
			profileService.saveAddonSearchDirectoryPaths(configurationService
					.getProfileName());
		} catch (ProfileException e) {
			e.printStackTrace();
		}

		ProfilePanel profilePanel = new ProfilePanel(facade);
		profilePanel.toFront();
		profilePanel.setVisible(true);
	}

	private void menuItemACREwizardPerformed() {

		FirstPageACREInstallerPanel firstPage = new FirstPageACREInstallerPanel(
				facade);
		firstPage.init();
		firstPage.setVisible(true);
	}

	private void menuItemTFARwizardPerformed() {

		FirstPageTFARInstallerPanel firstPage = new FirstPageTFARInstallerPanel(
				facade);
		firstPage.init();
		firstPage.setVisible(true);
	}

	private void menuItemAiAwizardPerformed() {

		AiaInstallerPanel aiaInstallerPanel = new AiaInstallerPanel(facade);
		aiaInstallerPanel.setVisible(true);
		aiaInstallerPanel.init();
	}

	private void menuItemRPTviewerPerformed() {

		RptViewerPanel rptViewerPanel = new RptViewerPanel(facade);
		rptViewerPanel.setVisible(true);
	}

	private void menuItemHelpPerformed() {

		CommonService commonService = new CommonService();
		String urlValue = commonService.getWiki();
		try {
			URI url = new java.net.URI(urlValue);
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(url);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
					"Can't open system web browser.", "Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void menuItemBISforumPerformed() {

		CommonService commonService = new CommonService();
		String urlValue = commonService.getBIS();
		try {
			URI url = new java.net.URI(urlValue);
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(url);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
					"Can't open system web browser.", "Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void menuItemPreferencesPerformed() {
		PreferencesPanel preferencesPanel = new PreferencesPanel(facade);
		preferencesPanel.init();
		preferencesPanel.setVisible(true);
	}

	private void menuItemAutoConfigImportPerformed() {
		AutoConfigImportPanel autoConfigImportPanel = new AutoConfigImportPanel(
				facade);
		autoConfigImportPanel.setVisible(true);
	}

	private void menuItemAutoConfigExportPerformed() {

		AutoConfigExportPanel autoConfigExportPanel = new AutoConfigExportPanel(
				facade);
		autoConfigExportPanel.init();
		autoConfigExportPanel.setVisible(true);
	}

	private void menuItemuUpdatesPerformed() {
		checkForUpdate(true);
	}

	private void menuItemAboutPerformed() {
		AboutPanel about = new AboutPanel(facade);
		about.setVisible(true);
	}

	private void menuIconifiedPerformed() {
		PreferencesDTO preferencesDTO = preferencesService.getPreferences();
		MinimizationType type = preferencesDTO.getLaunchPanelMinimized();
		if (type.equals(MinimizationType.TASK_BAR)) {
			setToTaskBar();
		} else if (type.equals(MinimizationType.TRAY)) {
			setToTray();
			this.setVisible(false);
		}
	}

	public void menuExitPerformed() {

		/* Write configuration and profiles. */
		try {
			commonService.saveAllParameters(this.getHeight(), this.getWidth());
		} catch (WritingException e) {
			JOptionPane.showMessageDialog(this,
					"An error occured.\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			dispose();
			System.exit(0);
		}
	}

	private void trayIconPerformed() {
		if (SystemTray.isSupported()) {
			tray.remove(trayIcon);
			this.setState(JFrame.NORMAL);
			this.setVisible(true);
			this.toFront();
		}
	}

	private void exitTrayItemPerformed() {
		if (SystemTray.isSupported()) {
			tray.remove(trayIcon);
			menuExitPerformed();
		}
	}

	private void launchTrayItemPerformed() {
		if (SystemTray.isSupported()) {
			this.setVisible(true);
			this.setState(JFrame.NORMAL);
			tray.remove(trayIcon);
		}
	}

	public void setToTaskBar() {
		this.setState(JFrame.ICONIFIED);
	}

	public void setToTray() {
		if (SystemTray.isSupported()) {
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.out.println("TrayIcon could not be added.");
				this.setState(JFrame.ICONIFIED);
				return;
			}
		}
	}

	/*
	 * class ProfilesMenuListener implements MenuListener {
	 * 
	 * @Override public void menuSelected(MenuEvent evt) {
	 * facade.getAddonsPanel().saveAddonGroups();
	 * facade.getLaunchOptionsPanel().setAdditionalParameters(); ProfilePanel
	 * profilePanel = new ProfilePanel(facade); profilePanel.toFront();
	 * profilePanel.setVisible(true); }
	 * 
	 * @Override public void menuDeselected(MenuEvent e) { //
	 * System.out.println("menuDeselected");
	 * 
	 * }
	 * 
	 * @Override public void menuCanceled(MenuEvent e) { //
	 * System.out.println("menuCanceled"); } }
	 */
	/**/

	public void checkForUpdate(final boolean withInfoMessage) {

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				FtpService ftpService = new FtpService();
				String availableVersion = ftpService.checkForUpdate(facade
						.isDevMode());

				if (availableVersion != null) {
					int response = JOptionPane.showConfirmDialog(
							facade.getMainPanel(),
							"A new update is available. Proceed update?",
							"Update", JOptionPane.OK_CANCEL_OPTION);

					if (response == 0) {
						try {
							commonService.saveAllParameters(getHeight(),
									getWidth());
						} catch (WritingException e) {
							e.printStackTrace();
						}
						// Proceed update
						String command = "java -jar -Djava.net.preferIPv4Stack=true ArmA3Sync-Updater.jar";
						if (facade.isDevMode()) {
							command = command + " -dev";
						}
						try {
							Runtime.getRuntime().exec(command);
							System.exit(0);
						} catch (IOException ex) {
							ex.printStackTrace();
							JOptionPane.showMessageDialog(
									facade.getMainPanel(), ex.getMessage(),
									"Update", JOptionPane.ERROR_MESSAGE);
						}
					}
				} else if (withInfoMessage) {
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							"No new update available.", "Update",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		t.start();
	}

	public void updateProfilesMenu() {

		int numberMenuItems = menuProfiles.getItemCount();

		for (int i = numberMenuItems - 1; i > 1; i--) {
			JMenuItem menuItem = menuProfiles.getItem(i);
			menuProfiles.remove(menuItem);
		}

		List<String> profileNames = profileService.getProfileNames();
		String initProfileName = configurationService.getProfileName();
		assert (initProfileName != null);
		for (int i = 0; i < profileNames.size(); i++) {
			final String profileName = profileNames.get(i);
			JCheckBoxMenuItem menuItemProfile = new JCheckBoxMenuItem(
					profileName);
			menuProfiles.add(menuItemProfile);
			if (profileName.equals(initProfileName)) {
				menuItemProfile.setSelected(true);
			}
			menuItemProfile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent evt) {
					menuItemProfilePerformed(evt);
				}
			});
		}
	}

	private void menuItemProfilePerformed(ActionEvent e) {

		// Save current profile
		try {
			facade.getAddonsPanel().saveAddonGroups();
			profileService.saveLauncherOptions(configurationService
					.getProfileName());
			profileService.saveAddonSearchDirectoryPaths(configurationService
					.getProfileName());
		} catch (ProfileException ex) {
			ex.printStackTrace();
		}

		int numberMenuItems = menuProfiles.getItemCount();

		for (int i = numberMenuItems - 1; i > 1; i--) {
			JCheckBoxMenuItem checkBoxItem = (JCheckBoxMenuItem) menuProfiles
					.getItem(i);
			checkBoxItem.setSelected(false);
		}

		JCheckBoxMenuItem menuItemProfile = (JCheckBoxMenuItem) e.getSource();
		menuItemProfile.setSelected(true);
		String profileName = menuItemProfile.getText();
		configurationService.setProfileName(profileName);
		facade.getInfoPanel().init();

		try {
			// Launcher options panel
			LauncherOptionsDTO launcherOptionsDTO = profileService
					.getLauncherOptions(profileName);
			configurationService.setLauncherOptions(launcherOptionsDTO);
			facade.getLaunchOptionsPanel().init();

			// Addon options panel
			Set<String> addonSearchDirectoryPaths = profileService
					.getAddonSearchDirectoryPaths(profileName);
			configurationService.getAddonSearchDirectoryPaths().clear();
			configurationService.getAddonSearchDirectoryPaths().addAll(
					addonSearchDirectoryPaths);
			facade.getAddonOptionsPanel().init();

			// Addon panel
			facade.getAddonsPanel().init();

		} catch (Exception e2) {
			e2.printStackTrace();
		}
		facade.getLaunchOptionsPanel().updateRunParameters();
		facade.getLaunchOptionsPanel().updateAdditionalParameters();
	}

	public void checkWellcomeDialog() {

		String path = configurationService.getLauncherOptions()
				.getArma3ExePath();
		if (path == null || "".equals(path)) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			WellcomePanel wellcomePanel = new WellcomePanel(facade);
			wellcomePanel.toFront();
			wellcomePanel.setVisible(true);
		}
	}

	public void updateRepositoriesStatus() {

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				List<RepositoryDTO> list = repositoryService.getRepositories();
				for (final RepositoryDTO repositoryDTO : list) {
					try {
						AbstractConnexionService connexion = ConnexionServiceFactory
								.getServiceFromRepository(repositoryDTO
										.getName());
						connexion.checkRepository(repositoryDTO.getName());
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
				facade.getSyncPanel().init();
				List<String> repositoryNames = new ArrayList<String>();
				for (final RepositoryDTO repositoryDTO : list) {
					try {
						ServerInfoDTO serverInfoDTO = repositoryService
								.getServerInfo(repositoryDTO.getName());
						if (serverInfoDTO != null) {
							if (repositoryDTO.getRevision() != serverInfoDTO
									.getRevision() && repositoryDTO.isNotify()) {
								repositoryNames.add(repositoryDTO.getName());
							}
						}
					} catch (RepositoryException e) {
						e.printStackTrace();
					}
				}
				if (!repositoryNames.isEmpty()) {
					String message = "The following repositories have been updated:";
					for (String rep : repositoryNames) {
						message = message + "\n" + "> " + rep;
					}
					InfoUpdatedRepositoryPanel infoUpdatedRepositoryPanel = new InfoUpdatedRepositoryPanel(
							facade);
					infoUpdatedRepositoryPanel.init(repositoryNames);
					infoUpdatedRepositoryPanel.setVisible(true);
				}
			}
		});
		t.start();
	}

	public void openRepository(final String repositoryName, String eventName,
			boolean update) {

		String title = repositoryName;

		if (!mapTabIndexes.containsKey(title)) {
			RepositoryPanel repositoryPanel = new RepositoryPanel(facade);
			if (eventName == null && !update) {
				repositoryPanel.init(repositoryName);
			} else if (eventName == null && update) {
				repositoryPanel.initUpdate(repositoryName);
			} else {
				repositoryPanel.init(repositoryName, eventName);
			}
			// tabbedPane.addTab(title, repositoryPanel);
			addClosableTab(repositoryPanel, repositoryName);
			final int index = tabbedPane.getTabCount() - 1;
			mapTabIndexes.put(title, index);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					tabbedPane.setSelectedIndex(index);
				}
			});
		} else {
			final int index = mapTabIndexes.get(title);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					tabbedPane.setSelectedIndex(index);
				}
			});
		}
	}

	public void addClosableTab(final JComponent c, final String title) {

		// Add the tab to the pane without any label
		tabbedPane.addTab(null, c);
		int pos = tabbedPane.indexOfComponent(c);

		// Create a FlowLayout that will space things 5px apart
		FlowLayout f = new FlowLayout(FlowLayout.CENTER, 5, 0);

		// Make a small JPanel with the layout and make it non-opaque
		JPanel pnlTab = new JPanel(f);
		pnlTab.setOpaque(false);

		// Add a JLabel with title and the left-side tab icon
		JLabel lblTitle = new JLabel(title);
		// lblTitle.setIcon(new ImageIcon(CLOSE_GRAY));

		// Create a JButton for the close tab button
		JButton btnClose = new JButton();
		btnClose.setOpaque(false);

		// Configure icon and rollover icon for button
		btnClose.setRolloverIcon(new ImageIcon(CLOSE_RED));
		btnClose.setRolloverEnabled(true);
		btnClose.setIcon(new ImageIcon(CLOSE_GRAY));

		// Set border null so the button doesn't make the tab too big
		btnClose.setBorder(null);

		// Make sure the button can't get focus, otherwise it looks funny
		btnClose.setFocusable(false);

		// Put the panel together
		pnlTab.add(lblTitle);
		pnlTab.add(btnClose);

		// Add a thin border to keep the image below the top edge of the tab
		// when the tab is selected
		pnlTab.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

		// Now assign the component for the tab
		tabbedPane.setTabComponentAt(pos, pnlTab);

		// Add the listener that removes the tab
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// The component parameter must be declared "final" so that it
				// can be
				// referenced in the anonymous listener class like this.
				int selectedIndex = tabbedPane.getSelectedIndex();
				boolean contains = false;
				String title = null;
				int indexMap = 0;
				for (Iterator<String> i = mapTabIndexes.keySet().iterator(); i
						.hasNext();) {
					title = i.next();
					int tabIndex = mapTabIndexes.get(title);
					if (selectedIndex == tabIndex) {
						contains = true;
						break;
					}
					indexMap++;
				}

				boolean isDownloading = repositoryService.isDownloading(title);

				if (contains && !isDownloading) {
					int count = 0;
					for (Iterator<String> i = mapTabIndexes.keySet().iterator(); i
							.hasNext();) {
						String key = i.next();
						int value = mapTabIndexes.get(key);
						if (count > indexMap) {
							mapTabIndexes.put(key, value - 1);
						}
						count++;
					}
					mapTabIndexes.remove(title);
					tabbedPane.remove(tabbedPane.getSelectedComponent());
				}
			}
		};
		btnClose.addActionListener(listener);
	}

	public void closeRepository(String repositoryName) {

		if (mapTabIndexes.containsKey(repositoryName)) {
			int index = mapTabIndexes.get(repositoryName);
			tabbedPane.removeTabAt(index);
			mapTabIndexes.remove(repositoryName);
		}
	}
}
