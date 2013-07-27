package fr.soe.a3s.domain.configration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fr.soe.a3s.constant.DefaultProfileName;
import fr.soe.a3s.constant.GameVersions;
import fr.soe.a3s.domain.Preferences;

public class Configuration implements Serializable {

	private static final long serialVersionUID = -8310476744472497506L;
	private String profileName = DefaultProfileName.DEFAULT.getDescription();
	private String gameVersion = GameVersions.ARMA3.getDescription();
	private String serverName;
	private boolean viewModeTree = true;
	private Set<String> addonSearchDirectoryPaths = new TreeSet<String>();
	private LauncherOptions launcherOptions = new LauncherOptions();
	private List<FavoriteServer> favoriteServers = new ArrayList<FavoriteServer>();
	private List<ExternalApplication> externalApplications = new ArrayList<ExternalApplication>();
	private AcreOptions acreOptions = new AcreOptions();
	private AiAOptions aiaOptions = new AiAOptions();
	
	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getGameVersion() {
		return gameVersion;
	}

	public void setGameVersion(String gameVersion) {
		this.gameVersion = gameVersion;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public Set<String> getAddonSearchDirectoryPaths() {
		return addonSearchDirectoryPaths;
	}

	public LauncherOptions getLauncherOptions() {
		return launcherOptions;
	}

	public List<FavoriteServer> getFavoriteServers() {
		return favoriteServers;
	}

	public List<ExternalApplication> getExternalApplications() {
		return externalApplications;
	}

	public void setAddonSearchDirectoryPaths(
			Set<String> addonSearchDirectoryPaths) {
		this.addonSearchDirectoryPaths = addonSearchDirectoryPaths;
	}

	public void setFavoriteServers(List<FavoriteServer> favoriteServers) {
		this.favoriteServers = favoriteServers;
	}

	public void setExternalApplications(
			List<ExternalApplication> externalApplications) {
		this.externalApplications = externalApplications;
	}

	public boolean isViewModeTree() {
		return viewModeTree;
	}

	public void setViewModeTree(boolean viewModeTree) {
		this.viewModeTree = viewModeTree;
	}

	public AiAOptions getAiaOptions() {
		if (aiaOptions == null) {
			aiaOptions = new AiAOptions();
		}
		return aiaOptions;
	}

	public AcreOptions getAcreOptions() {
		if (acreOptions==null){
			acreOptions = new AcreOptions();
		}
		return acreOptions;
	}

}
