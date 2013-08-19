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

public class ConfigurationService {

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

	public void setNoSplashScreen(boolean value) {
		configurationDAO.getConfiguration().getLauncherOptions()
				.setNoSplashScreen(value);
	}

	public void setDefaultWorld(boolean value) {
		configurationDAO.getConfiguration().getLauncherOptions()
				.setDefaultWorld(value);
	}

	public void saveLaunchOptions(LauncherOptionsDTO launcherOptionsDTO) {

		/* Launcher options */
		configurationDAO.getConfiguration().getLauncherOptions()
				.setGameProfile(launcherOptionsDTO.getGameProfile());
		configurationDAO.getConfiguration().getLauncherOptions()
				.setShowScriptErrors(launcherOptionsDTO.isShowScriptError());
		configurationDAO.getConfiguration().getLauncherOptions()
				.setWindowMode(launcherOptionsDTO.isWindowMode());
		configurationDAO.getConfiguration().getLauncherOptions()
				.setNoPause(launcherOptionsDTO.isNoPause());
		configurationDAO.getConfiguration().getLauncherOptions()
				.setRunBeta(launcherOptionsDTO.isRunBeta());

		/* Performance */
		configurationDAO
				.getConfiguration()
				.getLauncherOptions()
				.setCpuCountSelection(launcherOptionsDTO.getCpuCountSelection());
		configurationDAO.getConfiguration().getLauncherOptions()
				.setDefaultWorld(launcherOptionsDTO.isDefaultWorld());
		configurationDAO
				.getConfiguration()
				.getLauncherOptions()
				.setMaxMemorySelection(
						launcherOptionsDTO.getMaxMemorySelection());
		configurationDAO.getConfiguration().getLauncherOptions()
				.setNoSplashScreen(launcherOptionsDTO.isNoSplashScreen());

		/* Executable locations */
		configurationDAO.getConfiguration().getLauncherOptions()
				.setArma3ExePath(launcherOptionsDTO.getArma3ExePath());
		configurationDAO.getConfiguration().getLauncherOptions()
				.setSteamExePath(launcherOptionsDTO.getSteamExePath());
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
				aiaOptions.setArmaPath("");
			} else {
				aiaOptions.setArmaPath(tohPath);
			}
		}
		Addon addon = addonDAO.getMap().get("a1dummies");
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

	public void setHeight(int height) {
		configurationDAO.getConfiguration().setHeight(height);
	}

	public void setWidth(int width) {
		configurationDAO.getConfiguration().setWidth(width);
	}

	/* Business Methods */

	private ExternalApplicationDTO transformExternalApplication2DTO(
			ExternalApplication externalApplication) {

		final ExternalApplicationDTO externalApplicationDTO = new ExternalApplicationDTO();
		externalApplicationDTO.setName(externalApplication.getName());
		externalApplicationDTO.setExecutablePath(externalApplication
				.getExecutablePath());
		externalApplicationDTO.setEnable(externalApplication.isEnable());
		externalApplicationDTO.setParameters(externalApplication
				.getParameters());
		return externalApplicationDTO;
	}

	private ExternalApplication transformDTO2ExternalApplication(
			ExternalApplicationDTO externalApplicationDTO) {

		final ExternalApplication externalApplication = new ExternalApplication();
		externalApplication.setName(externalApplicationDTO.getName());
		externalApplication.setExecutablePath(externalApplicationDTO
				.getExecutablePath());
		externalApplication.setEnable(externalApplicationDTO.isEnable());
		externalApplication.setParameters(externalApplicationDTO
				.getParameters());
		return externalApplication;
	}

	private LauncherOptionsDTO transformLauncherOptions2DTO(
			LauncherOptions launcherOptions) {

		final LauncherOptionsDTO launcherOptionsDTO = new LauncherOptionsDTO();

		launcherOptionsDTO.setArma3ExePath(launcherOptions.getArma3ExePath());
		launcherOptionsDTO.setSteamExePath(launcherOptions.getSteamExePath());
		launcherOptionsDTO.setCpuCountSelection(launcherOptions
				.getCpuCountSelection());
		launcherOptionsDTO.setDefaultWorld(launcherOptions.isDefaultWorld());
		launcherOptionsDTO.setGameProfile(launcherOptions.getGameProfile());
		launcherOptionsDTO.setMaxMemorySelection(launcherOptions
				.getMaxMemorySelection());
		launcherOptionsDTO.setCpuCountSelection(launcherOptions
				.getCpuCountSelection());
		launcherOptionsDTO.setNoPause(launcherOptions.isNoPause());
		launcherOptionsDTO
				.setNoSplashScreen(launcherOptions.isNoSplashScreen());
		launcherOptionsDTO.setRunBeta(launcherOptions.isRunBeta());
		launcherOptionsDTO.setShowScriptError(launcherOptions
				.isShowScriptErrors());
		launcherOptionsDTO.setWindowMode(launcherOptions.isWindowMode());

		return launcherOptionsDTO;
	}

	private FavoriteServerDTO transformFavoriteServers2DTO(
			FavoriteServer favoriteServer) {

		final FavoriteServerDTO favoriteServerDTO = new FavoriteServerDTO();
		favoriteServerDTO.setName(favoriteServer.getName());
		favoriteServerDTO.setIpAddress(favoriteServer.getIpAddress());
		favoriteServerDTO.setPort(favoriteServer.getPort());
		favoriteServerDTO.setPassword(favoriteServer.getPassword());
		return favoriteServerDTO;
	}

	private FavoriteServer transformDTO2FavoriteServer(
			FavoriteServerDTO favoriteServerDTO) {

		final FavoriteServer favoriteServer = new FavoriteServer();
		favoriteServer.setName(favoriteServerDTO.getName());
		favoriteServer.setIpAddress(favoriteServerDTO.getIpAddress());
		favoriteServer.setPort(favoriteServerDTO.getPort());
		favoriteServer.setPassword(favoriteServerDTO.getPassword());
		return favoriteServer;
	}

	private AiAOptionsDTO transformAiAOptions2DTO(AiAOptions aiaOptions) {

		final AiAOptionsDTO aiaOptionsDTO = new AiAOptionsDTO();
		aiaOptionsDTO.setArma2Path(aiaOptions.getArma2Path());
		aiaOptionsDTO.setArma2OAPath(aiaOptions.getArma2OAPath());
		aiaOptionsDTO.setArmaPath(aiaOptions.getArmaPath());
		aiaOptionsDTO.setTohPath(aiaOptions.getTohPath());
		aiaOptionsDTO.setAllinArmaPath(aiaOptions.getAllinArmaPath());
		return aiaOptionsDTO;
	}

}
