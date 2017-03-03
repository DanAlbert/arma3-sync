package fr.soe.a3s.ui;

import fr.soe.a3s.ui.main.AddonOptionsPanel;
import fr.soe.a3s.ui.main.AddonsPanel;
import fr.soe.a3s.ui.main.ExternalApplicationsPanel;
import fr.soe.a3s.ui.main.InfoPanel;
import fr.soe.a3s.ui.main.LaunchPanel;
import fr.soe.a3s.ui.main.LauncherOptionsPanel;
import fr.soe.a3s.ui.main.MainPanel;
import fr.soe.a3s.ui.main.OnlinePanel;
import fr.soe.a3s.ui.main.SyncPanel;
import fr.soe.a3s.ui.main.dialogs.InfoUpdatedRepositoryDialog;

public class Facade {

	/** Dev mode */
	private boolean devMode = false;
	/** Run mode, true if started as a service */
	private boolean runMode = false;
	/** Safe mode, true use Java L&F */
	private boolean safeMode = false;
	/** Views single instance */
	private MainPanel mainPanel;
	private InfoPanel infoPanel;
	private LaunchPanel launchPanel;
	private AddonsPanel addonsPanel;
	private AddonOptionsPanel addonOptionsPanel;
	private LauncherOptionsPanel launchOptionsPanel;
	private OnlinePanel onlinePanel;
	private ExternalApplicationsPanel externalApplicationsPanel;
	private SyncPanel syncPanel;
	private InfoUpdatedRepositoryDialog infoUpdatedRepositoryPanel;

	public boolean isDevMode() {
		return devMode;
	}

	public void setDevMode(boolean value) {
		this.devMode = value;
	}

	public boolean isRunMode() {
		return runMode;
	}

	public void setRunMode(boolean runMode) {
		this.runMode = runMode;
	}
	
	public void setSafeMode(boolean safeMode) {
		this.safeMode = safeMode;
	}

	public boolean isSafeMode() {
		return this.safeMode;
	}

	public void setMainPanel(MainPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	public void setInfoPanel(InfoPanel infoPanel) {
		this.infoPanel = infoPanel;
	}

	public void setAddonsPanel(AddonsPanel addonsPanel) {
		this.addonsPanel = addonsPanel;
	}

	public void setLaunchPanel(LaunchPanel launchPanel) {
		this.launchPanel = launchPanel;
	}

	public void setAddonOptionsPanel(AddonOptionsPanel addonOptionsPanel) {
		this.addonOptionsPanel = addonOptionsPanel;
	}

	public void setLaunchOptionsPanel(LauncherOptionsPanel launchOptionsPanel) {
		this.launchOptionsPanel = launchOptionsPanel;
	}

	public void setOnlinePanel(OnlinePanel onlinePanel) {
		this.onlinePanel = onlinePanel;
	}

	public void setExternalApplicationsPanel(
			ExternalApplicationsPanel externalApplicationsPanel) {
		this.externalApplicationsPanel = externalApplicationsPanel;
	}

	public void setSyncPanel(SyncPanel syncPanel) {
		this.syncPanel = syncPanel;
	}

	public MainPanel getMainPanel() {
		return mainPanel;
	}

	public InfoPanel getInfoPanel() {
		return infoPanel;
	}

	public AddonsPanel getAddonsPanel() {
		return addonsPanel;
	}

	public LaunchPanel getLaunchPanel() {
		return launchPanel;
	}

	public AddonOptionsPanel getAddonOptionsPanel() {
		return addonOptionsPanel;
	}

	public LauncherOptionsPanel getLaunchOptionsPanel() {
		return launchOptionsPanel;
	}

	public ExternalApplicationsPanel getExternalApplicationsPanel() {
		return externalApplicationsPanel;
	}

	public OnlinePanel getOnlinePanel() {
		return onlinePanel;
	}

	public SyncPanel getSyncPanel() {
		return syncPanel;
	}

	public InfoUpdatedRepositoryDialog getInfoUpdatedRepositoryPanel() {
		return infoUpdatedRepositoryPanel;
	}

	public void setInfoUpdatedRepositoryPanel(
			InfoUpdatedRepositoryDialog infoUpdatedRepositoryPanel) {
		this.infoUpdatedRepositoryPanel = infoUpdatedRepositoryPanel;
	}
}
