package fr.soe.a3s.dto.configuration;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationDTO {

	private String profileName;
	private String gameVersion;
	private String serverName;
	private boolean viewModeTree;
	private List<String> addonSearchDirectoryPaths = new ArrayList<String>();
	private LauncherOptionsDTO launcherOptionsDTO = new LauncherOptionsDTO();
	private List<FavoriteServerDTO> favoriteServersDTO = new ArrayList<FavoriteServerDTO>();
	private List<ExternalApplicationDTO> externalApplicationsDTO = new ArrayList<ExternalApplicationDTO>();

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
	
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServerName() {
		return serverName;
	}

	public List<String> getAddonSearchDirectoryPaths() {
		return addonSearchDirectoryPaths;
	}

	public LauncherOptionsDTO getLauncherOptionsDTO() {
		return launcherOptionsDTO;
	}

	public List<FavoriteServerDTO> getFavoriteServersDTO() {
		return favoriteServersDTO;
	}

	public List<ExternalApplicationDTO> getExternalApplicationsDTO() {
		return externalApplicationsDTO;
	}

	public boolean isViewModeTree() {
		return viewModeTree;
	}

	public void setViewModeTree(boolean viewModeTree) {
		this.viewModeTree = viewModeTree;
	}

}
