package fr.soe.a3s.ui;

import fr.soe.a3s.ui.mainEditor.AddonOptionsPanel;
import fr.soe.a3s.ui.mainEditor.AddonsPanel;
import fr.soe.a3s.ui.mainEditor.ExternalApplicationsEditionPanel;
import fr.soe.a3s.ui.mainEditor.ExternalApplicationsPanel;
import fr.soe.a3s.ui.mainEditor.InfoPanel;
import fr.soe.a3s.ui.mainEditor.InfoUpdatedRepositoryPanel;
import fr.soe.a3s.ui.mainEditor.LaunchPanel;
import fr.soe.a3s.ui.mainEditor.LauncherOptionsPanel;
import fr.soe.a3s.ui.mainEditor.MainPanel;
import fr.soe.a3s.ui.mainEditor.OnlinePanel;
import fr.soe.a3s.ui.mainEditor.PreferencesPanel;
import fr.soe.a3s.ui.mainEditor.SyncPanel;
import fr.soe.a3s.ui.profileEditor.ProfilePanel;
import fr.soe.a3s.ui.repositoryEditor.ConnectionPanel;
import fr.soe.a3s.ui.tools.acre2Editor.FirstPageACRE2InstallerPanel;
import fr.soe.a3s.ui.tools.acreEditor.FirstPageACREInstallerPanel;
import fr.soe.a3s.ui.tools.tfarEditor.FirstPageTFARInstallerPanel;

public class Facade {

	/** Views single instance */
	private MainPanel mainPanel;
	private InfoPanel infoPanel;
	private AddonsPanel addonsPanel;
	private LaunchPanel launchPanel;
	private AddonOptionsPanel addonOptionsPanel;
	private LauncherOptionsPanel launchOptionsPanel;
	private OnlinePanel onlinePanel;
	private ExternalApplicationsPanel externalApplicationsPanel;
	private ProfilePanel profilePanel;
	private SyncPanel syncPanel;
	private ExternalApplicationsEditionPanel externalApplicationsEditionPanel;
	private PreferencesPanel preferencesPanel;
	private ConnectionPanel connectiongPanel;
	private FirstPageACREInstallerPanel firstPageACREInstallerPanel;
	private FirstPageACRE2InstallerPanel firstPageACRE2InstallerPanel;
	private FirstPageTFARInstallerPanel firstPageTFARInstallerPanel;
	private InfoUpdatedRepositoryPanel infoUpdatedRepositoryPanel;

	/** Dev mode */
	private boolean devMode = false;

	/* Getters and setters on the Views */
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

	public void setProfilePanel(ProfilePanel profilePanel) {
		this.profilePanel = profilePanel;
	}

	public void setSyncPanel(SyncPanel syncPanel) {
		this.syncPanel = syncPanel;
	}

	public void setExternalApplicationsEditionPanel(
			ExternalApplicationsEditionPanel externalApplicationsEditionPanel) {
		this.externalApplicationsEditionPanel = externalApplicationsEditionPanel;
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

	public ProfilePanel getProfilePanel() {
		return profilePanel;
	}

	public SyncPanel getSyncPanel() {
		return syncPanel;
	}

	public ExternalApplicationsEditionPanel getExternalApplicationsEditionPanel() {
		return externalApplicationsEditionPanel;
	}

	public boolean isDevMode() {
		return devMode;
	}

	public void setDevMode(boolean value) {
		this.devMode = value;
	}

	public PreferencesPanel getPreferencesPanel() {
		return preferencesPanel;
	}

	public void setPreferencesPanel(PreferencesPanel preferencesPanel) {
		this.preferencesPanel = preferencesPanel;
	}

	public void setConnectiongPanel(ConnectionPanel connectiongPanel) {
		this.connectiongPanel = connectiongPanel;
	}

	public ConnectionPanel getConnectiongPanel() {
		return connectiongPanel;
	}

	public void setFirstPageACREInstallerPanel(
			FirstPageACREInstallerPanel firstPageInstallerPanel) {
		this.firstPageACREInstallerPanel = firstPageInstallerPanel;
	}

	public FirstPageACREInstallerPanel getFirstPageACREInstallerPanel() {
		return firstPageACREInstallerPanel;
	}

	public FirstPageACRE2InstallerPanel getFirstPageACRE2InstallerPanel() {
		return firstPageACRE2InstallerPanel;
	}

	public void setFirstPageACRE2InstallerPanel(
			FirstPageACRE2InstallerPanel firstPageACRE2InstallerPanel) {
		this.firstPageACRE2InstallerPanel = firstPageACRE2InstallerPanel;
	}

	public void setInfoUpdatedRepositoryPanel(
			InfoUpdatedRepositoryPanel infoUpdatedRepositoryPanel) {
		this.infoUpdatedRepositoryPanel = infoUpdatedRepositoryPanel;
	}

	public InfoUpdatedRepositoryPanel getInfoUpdatedRepositoryPanel() {
		return infoUpdatedRepositoryPanel;
	}

	public FirstPageTFARInstallerPanel getFirstPageTFARInstallerPanel() {
		return firstPageTFARInstallerPanel;
	}

	public void setFirstPageTFARInstallerPanel(
			FirstPageTFARInstallerPanel firstPageTFARInstallerPanel) {
		this.firstPageTFARInstallerPanel = firstPageTFARInstallerPanel;
	}
}
