package fr.soe.a3s.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import fr.soe.a3s.dao.AddonDAO;
import fr.soe.a3s.dao.ConfigurationDAO;
import fr.soe.a3s.domain.Addon;
import fr.soe.a3s.domain.configration.AiAOptions;
import fr.soe.a3s.domain.configration.ExternalApplication;
import fr.soe.a3s.domain.configration.FavoriteServer;
import fr.soe.a3s.domain.configration.LauncherOptions;
import fr.soe.a3s.dto.configuration.AiAOptionsDTO;
import fr.soe.a3s.dto.configuration.ExternalApplicationDTO;
import fr.soe.a3s.dto.configuration.FavoriteServerDTO;
import fr.soe.a3s.dto.configuration.LauncherOptionsDTO;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.WritingException;

public class ConfigurationService extends ObjectDTOtransformer {

	private static final ConfigurationDAO configurationDAO = new ConfigurationDAO();
	private static final AddonDAO addonDAO = new AddonDAO();

	/* Read/Write configuration */
	public void read() throws LoadingException {
		configurationDAO.read();
	}

	public void write() throws WritingException {
		configurationDAO.write();
	}

	/* Addon options panel */
	public Set<String> getAddonSearchDirectoryPaths() {
		return configurationDAO.getConfiguration()
				.getAddonSearchDirectoryPaths();
	}

	public void removeSearchDirectoryPath(String path) {
		Set<String> set = configurationDAO.getConfiguration()
				.getAddonSearchDirectoryPaths();
		set.remove(path);
	}

	/* Launch panel */
	public String getProfileName() {
		return configurationDAO.getConfiguration().getProfileName();
	}

	public void setProfileName(String profileName) {
		configurationDAO.getConfiguration().setProfileName(profileName);
	}

	public String getServerName() {
		return configurationDAO.getConfiguration().getServerName();
	}

	public void saveServerName(String serverName) {
		configurationDAO.getConfiguration().setServerName(serverName);
	}

	public String getGameVersion() {
		return configurationDAO.getConfiguration().getGameVersion();
	}

	public void setGameVersion(String gameVersion) {
		configurationDAO.getConfiguration().setGameVersion(gameVersion);
	}

	/* Addons panel */
	public boolean isViewModeTree() {
		return configurationDAO.getConfiguration().isViewModeTree();
	}

	public void setViewModeTree(boolean value) {
		configurationDAO.getConfiguration().setViewModeTree(value);
	}

	public void setViewMode(boolean viewModeTree) {
		configurationDAO.getConfiguration().setViewModeTree(viewModeTree);
	}

	/* online panel */
	public List<FavoriteServerDTO> getFavoriteServers() {

		List<FavoriteServer> favoriteServers = configurationDAO
				.getConfiguration().getFavoriteServers();
		List<FavoriteServerDTO> favoriteServerDTOs = new ArrayList<FavoriteServerDTO>();
		for (FavoriteServer favoriteServer : favoriteServers) {
			FavoriteServerDTO f = transformFavoriteServers2DTO(favoriteServer);
			favoriteServerDTOs.add(f);
		}
		return favoriteServerDTOs;
	}

	public void setFavoriteServers(List<FavoriteServerDTO> favoriteServerDTOs) {

		configurationDAO.getConfiguration().getFavoriteServers().clear();
		List<FavoriteServer> favoriteServers = new ArrayList<FavoriteServer>();
		for (FavoriteServerDTO favoriteServerDTO : favoriteServerDTOs) {
			FavoriteServer favoriteServer = transformDTO2FavoriteServer(favoriteServerDTO);
			favoriteServers.add(favoriteServer);
		}
		Collections.sort(favoriteServers);
		for (FavoriteServer favoriteServer : favoriteServers) {
			configurationDAO.getConfiguration().getFavoriteServers()
					.add(favoriteServer);
		}
	}

	/* Externals Applications panel */
	public List<ExternalApplicationDTO> getExternalApplications() {

		List<ExternalApplication> externalApplications = configurationDAO
				.getConfiguration().getExternalApplications();
		List<ExternalApplicationDTO> externalApplicationDTOs = new ArrayList<ExternalApplicationDTO>();
		for (ExternalApplication externalApplication : externalApplications) {
			ExternalApplicationDTO ex = transformExternalApplication2DTO(externalApplication);
			externalApplicationDTOs.add(ex);
		}
		return externalApplicationDTOs;
	}

	public void saveExternalApps(
			List<ExternalApplicationDTO> externalApplicationDTOs) {

		configurationDAO.getConfiguration().getExternalApplications().clear();
		List<ExternalApplication> externalApplications = new ArrayList<ExternalApplication>();
		for (ExternalApplicationDTO externalApplicationDTO : externalApplicationDTOs) {
			ExternalApplication externalApplication = transformDTO2ExternalApplication(externalApplicationDTO);
			externalApplications.add(externalApplication);
		}
		Collections.sort(externalApplications);
		for (ExternalApplication externalApplication : externalApplications) {
			configurationDAO.getConfiguration().getExternalApplications()
					.add(externalApplication);
		}
	}

	/* Launcher options panel */
	public LauncherOptionsDTO getLauncherOptions() {
		LauncherOptions launcherOptions = configurationDAO.getConfiguration()
				.getLauncherOptions();
		LauncherOptionsDTO launcherOptionsDTO = transformLauncherOptions2DTO(launcherOptions);
		return launcherOptionsDTO;
	}

	public void setArmA3ExePath(String path) {
		configurationDAO.getConfiguration().getLauncherOptions()
				.setArma3ExePath(path);
	}

	public void setSteamExePath(String path) {
		configurationDAO.getConfiguration().getLauncherOptions()
				.setSteamExePath(path);
	}

	public void setGameProfile(String gameProfileName) {
		configurationDAO.getConfiguration().getLauncherOptions()
				.setGameProfile(gameProfileName);
	}

	public void setCheckBoxShowScriptErrors(boolean value) {
		configurationDAO.getConfiguration().getLauncherOptions()
				.setShowScriptErrors(value);
	}

	public void setCheckBoxNoPause(boolean value) {
		configurationDAO.getConfiguration().getLauncherOptions()
				.setNoPause(value);
	}

	public void setCheckBoxNoFilePatching(boolean value) {
		configurationDAO.getConfiguration().getLauncherOptions()
				.setNoFilePatching(value);
	}

	public void setCheckBoxWindowMode(boolean value) {
		configurationDAO.getConfiguration().getLauncherOptions()
				.setWindowMode(value);
	}

	public void setCheckBoxRun(boolean value) {
		configurationDAO.getConfiguration().getLauncherOptions()
				.setRunBeta(value);
	}

	public void setCheckBoxXPCompatibilityMode(boolean value) {
		configurationDAO.getConfiguration().getLauncherOptions()
				.setXpCompatibilityMode(value);
	}

	public void setMaxMemory(String maxMemory) {
		configurationDAO.getConfiguration().getLauncherOptions()
				.setMaxMemorySelection(maxMemory);
	}

	public void setCpuCount(String cpuCount) {
		if (cpuCount == null) {
			configurationDAO.getConfiguration().getLauncherOptions()
					.setCpuCountSelection(0);
		} else {
			configurationDAO.getConfiguration().getLauncherOptions()
					.setCpuCountSelection(Integer.parseInt(cpuCount));
		}
	}

	public void setExThreads(String exThreads) {
		configurationDAO.getConfiguration().getLauncherOptions()
				.setExThreadsSelection(exThreads);
	}

	public void setNoSplashScreen(boolean value) {
		configurationDAO.getConfiguration().getLauncherOptions()
				.setNoSplashScreen(value);
	}

	public void setDefaultWorld(boolean value) {
		configurationDAO.getConfiguration().getLauncherOptions()
				.setDefaultWorld(value);
	}

	public void setNoLogs(boolean value) {
		configurationDAO.getConfiguration().getLauncherOptions()
				.setNoLogs(value);
	}

	public String determineArmA3Path() {
		return configurationDAO.determineArmA3Path();
	}

	public String determineSteamExePath() {
		String path = configurationDAO.determineSteamPath();
		if (path == null) {
			return null;
		} else {
			String steamExePath = path + "\\" + "steam.exe";
			configurationDAO.getConfiguration().getLauncherOptions()
					.setSteamExePath(steamExePath);
			return steamExePath;
		}
	}

	public String getTS3installationFodler() {

		String ts3Path = configurationDAO.getConfiguration().getAcreOptions()
				.getTs3Path();

		if (ts3Path != null) {
			return ts3Path;
		} else {
			String path = configurationDAO.determineTS3path();
			return path;
		}
	}

	public String getRptPath() {

		String rptPath = configurationDAO.getConfiguration().getRptOptions()
				.getRptPath();

		if (rptPath != null) {
			return rptPath;
		} else {
			String path = configurationDAO.determineRptPath();
			return path;
		}
	}

	public void setRptPath(String rptPath) {
		configurationDAO.getConfiguration().getRptOptions().setRptPath(rptPath);
	}

	public void setTS3installationFodler(String ts3Path) {
		configurationDAO.getConfiguration().getAcreOptions()
				.setTs3Path(ts3Path);
	}

	public String getTS3version(String ts3InstallationDirectoryPath) {

		String version = configurationDAO
				.determineTS3version(ts3InstallationDirectoryPath);
		return version;
	}

	public boolean isTS364bit(String ts3InstallationDirectoryPath) {

		assert (ts3InstallationDirectoryPath != null);
		String ts3ExePath = ts3InstallationDirectoryPath + "\\"
				+ "ts3client_win64.exe";
		File file = new File(ts3ExePath);
		if (!file.exists()) {
			return false;
		} else {
			return true;
		}
	}

	public AiAOptionsDTO determineAiAOptions() {

		AiAOptions aiaOptions = configurationDAO.getConfiguration()
				.getAiaOptions();

		if (aiaOptions.getArma2Path() == null) {
			String arma2Path = configurationDAO.determineArmA2Path();
			if (arma2Path == null) {
				aiaOptions.setArma2Path("");
			} else {
				aiaOptions.setArma2Path(arma2Path);
			}
		}
		if (aiaOptions.getArma2OAPath() == null) {
			String arma2OAPath = configurationDAO.determineArmA2OAPath();
			if (arma2OAPath == null) {
				aiaOptions.setArma2OAPath("");
			} else {
				aiaOptions.setArma2OAPath(arma2OAPath);
			}
		}
		if (aiaOptions.getArmaPath() == null) {
			String armaPath = configurationDAO.determineArmAPath();
			if (armaPath == null) {
				aiaOptions.setArmaPath("");
			} else {
				aiaOptions.setArmaPath(armaPath);
			}
		}
		if (aiaOptions.getTohPath() == null) {
			String tohPath = configurationDAO.determineTOHPath();
			if (tohPath == null) {
				aiaOptions.setTohPath("");
			} else {
				aiaOptions.setTohPath(tohPath);
			}
		}
		Addon addon = addonDAO.getMap().get("posta3");
		if (addon != null) {
			aiaOptions.setAllinArmaPath(addon.getPath());
		} else {
			aiaOptions.setAllinArmaPath(null);
		}
		AiAOptionsDTO aiAOptionsDTO = transformAiAOptions2DTO(aiaOptions);
		return aiAOptionsDTO;
	}

	public void setAiAOptions(AiAOptionsDTO aiaOptionsDTO) {

		configurationDAO.getConfiguration().getAiaOptions()
				.setArma2Path(aiaOptionsDTO.getArma2Path());
		configurationDAO.getConfiguration().getAiaOptions()
				.setArma2OAPath(aiaOptionsDTO.getArma2OAPath());
		configurationDAO.getConfiguration().getAiaOptions()
				.setArmaPath(aiaOptionsDTO.getArmaPath());
		configurationDAO.getConfiguration().getAiaOptions()
				.setTohPath(aiaOptionsDTO.getTohPath());
		// Do no set AllinArmA path here!
	}

	public int getHeight() {
		return configurationDAO.getConfiguration().getHeight();
	}

	public int getWidth() {
		return configurationDAO.getConfiguration().getWidth();
	}

	public void setLauncherOptions(LauncherOptionsDTO launcherOptionsDTO) {

		setGameProfile(launcherOptionsDTO.getGameProfile());
		setCheckBoxShowScriptErrors(launcherOptionsDTO.isShowScriptError());
		setCheckBoxNoPause(launcherOptionsDTO.isNoPause());
		setCheckBoxNoFilePatching(launcherOptionsDTO.isNoFilePatching());
		setCheckBoxWindowMode(launcherOptionsDTO.isWindowMode());
		setMaxMemory(launcherOptionsDTO.getMaxMemorySelection());
		setCpuCount(Integer.toString(launcherOptionsDTO.getCpuCountSelection()));
		setExThreads(launcherOptionsDTO.getExThreadsSelection());
		setNoSplashScreen(launcherOptionsDTO.isNoSplashScreen());
		setDefaultWorld(launcherOptionsDTO.isDefaultWorld());
		setNoLogs(launcherOptionsDTO.isNoLogs());
		setArmA3ExePath(launcherOptionsDTO.getArma3ExePath());
	}
}
