package fr.soe.a3s.service;

import java.io.File;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.dao.AddonDAO;
import fr.soe.a3s.dao.ConfigurationDAO;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.AbstractProtocoleFactory;
import fr.soe.a3s.domain.Addon;
import fr.soe.a3s.domain.configration.AiAOptions;
import fr.soe.a3s.domain.configration.ExternalApplication;
import fr.soe.a3s.domain.configration.FavoriteServer;
import fr.soe.a3s.domain.configration.LauncherOptions;
import fr.soe.a3s.domain.configration.Proxy;
import fr.soe.a3s.dto.ProtocolDTO;
import fr.soe.a3s.dto.configuration.AiAOptionsDTO;
import fr.soe.a3s.dto.configuration.ExternalApplicationDTO;
import fr.soe.a3s.dto.configuration.FavoriteServerDTO;
import fr.soe.a3s.dto.configuration.ProxyDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.WritingException;

public class ConfigurationService extends ObjectDTOtransformer {

	private static final ConfigurationDAO configurationDAO = new ConfigurationDAO();
	private static final AddonDAO addonDAO = new AddonDAO();

	/* Read/Write configuration */
	public void read() throws LoadingException {
		System.out.println("Loading configuration...");
		configurationDAO.read();
	}

	public void write() throws WritingException {
		configurationDAO.write();
	}

	/* Launch panel */
	public String getProfileName() {
		return configurationDAO.getConfiguration().getProfileName();
	}

	public void setProfileName(String profileName) {
		configurationDAO.getConfiguration().setProfileName(profileName);
		try {
			configurationDAO.write();
		} catch (WritingException e) {
		}
	}

	public String getServerName() {
		return configurationDAO.getConfiguration().getServerName();
	}

	public void setServerName(String serverName) {
		configurationDAO.getConfiguration().setServerName(serverName);
		try {
			configurationDAO.write();
		} catch (WritingException e) {
		}
	}

	public String getDefaultModset() {
		return configurationDAO.getConfiguration().getDefaultModset();
	}

	public void setDefautlModset(String defaultModset) {
		configurationDAO.getConfiguration().setDefaultModset(defaultModset);
		try {
			configurationDAO.write();
		} catch (WritingException e) {
		}
	}

	public String getGameVersion() {
		return configurationDAO.getConfiguration().getGameVersion();
	}

	public void setGameVersion(String gameVersion) {
		configurationDAO.getConfiguration().setGameVersion(gameVersion);
		try {
			configurationDAO.write();
		} catch (WritingException e) {
		}
	}

	/* Addons panel */
	public boolean isViewModeTree() {
		return configurationDAO.getConfiguration().isViewModeTree();
	}

	public void setViewModeTree(boolean value) {
		configurationDAO.getConfiguration().setViewModeTree(value);
		try {
			configurationDAO.write();
		} catch (WritingException e) {
		}
	}

	/* online panel */
	public List<FavoriteServerDTO> getFavoriteServers() {

		List<FavoriteServer> favoriteServers = configurationDAO
				.getConfiguration().getFavoriteServers();
		List<FavoriteServerDTO> favoriteServerDTOs = new ArrayList<FavoriteServerDTO>();
		for (FavoriteServer favoriteServer : favoriteServers) {
			if (favoriteServer != null) {
				FavoriteServerDTO f = transformFavoriteServers2DTO(favoriteServer);
				favoriteServerDTOs.add(f);
			}
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
		try {
			configurationDAO.write();
		} catch (WritingException e) {
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
		try {
			configurationDAO.write();
		} catch (WritingException e) {
		}
	}

	public String determineArmA3Path() {
		return configurationDAO.determineArmA3Path();
	}

	/* ACRE2 */
	public String getAcre2TS3installationFodler() {

		String ts3Path = configurationDAO.getConfiguration().getAcre2Options()
				.getTs3Path();

		if (ts3Path != null) {
			return ts3Path;
		} else {
			String path = configurationDAO.determineTS3path();
			return path;
		}
	}

	public void setAcre2TS3installationFodler(String ts3Path) {
		configurationDAO.getConfiguration().getAcre2Options()
				.setTs3Path(ts3Path);
	}

	public String getAcre2PluginPath() {
		String acre2PluginPath = configurationDAO.getConfiguration()
				.getAcre2Options().getAcre2PluginPath();
		return acre2PluginPath;
	}

	public void setAcre2PluginPath(String acre2PluginPath) {
		configurationDAO.getConfiguration().getAcre2Options()
				.setAcre2PluginPath(acre2PluginPath);
	}

	/* TFAR */
	public String getTfarTS3installationFodler() {

		String ts3Path = configurationDAO.getConfiguration().getTfarOptions()
				.getTs3Path();

		if (ts3Path != null) {
			return ts3Path;
		} else {
			String path = configurationDAO.determineTS3path();
			return path;
		}
	}

	public void setTfarTS3installationFodler(String ts3Path) {
		configurationDAO.getConfiguration().getTfarOptions()
				.setTs3Path(ts3Path);
	}

	public String getTfarPluginPath() {
		String tfarPluginPath = configurationDAO.getConfiguration()
				.getTfarOptions().getTfarPluginPath();
		return tfarPluginPath;
	}

	public void setTfarPluginPath(String tfarPluginPath) {
		configurationDAO.getConfiguration().getTfarOptions()
				.setTfarPluginPath(tfarPluginPath);
	}

	public String getTfarUserconfigPath() {
		String tfarUserconfigPath = configurationDAO.getConfiguration()
				.getTfarOptions().getTfarUserconfigPath();
		return tfarUserconfigPath;
	}

	public void setTfarUserconfigPath(String tfarUserconfigPath) {
		configurationDAO.getConfiguration().getTfarOptions()
				.setTfarUserconfigPath(tfarUserconfigPath);
	}

	/* RPT */
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

	public boolean isTS332bit(String ts3InstallationDirectoryPath) {

		assert (ts3InstallationDirectoryPath != null);
		String ts3ExePath = ts3InstallationDirectoryPath + "\\"
				+ "ts3client_win32.exe";
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

	public String getBiketyExtractSourceDirectoryPath() {
		return configurationDAO.getConfiguration().getBikeyExtractOptions()
				.getSourceDirectoryPath();
	}

	public void setBiketyExtractSourceDirectoryPath(String sourceDirectoryPath) {
		configurationDAO.getConfiguration().getBikeyExtractOptions()
				.setSourceDirectoryPath(sourceDirectoryPath);
	}

	public String getBiketyExtractTargetDirectoryPath() {
		return configurationDAO.getConfiguration().getBikeyExtractOptions()
				.getTargetDirectoryPath();
	}

	public void setBiketyExtractTargetDirectoryPath(String targetDirectoryPath) {
		configurationDAO.getConfiguration().getBikeyExtractOptions()
				.setTargetDirectoryPath(targetDirectoryPath);
	}

	public int getHeight() {
		return configurationDAO.getConfiguration().getHeight();
	}

	public void setHeight(int height) {
		configurationDAO.getConfiguration().setHeight(height);
	}

	public int getWidth() {
		return configurationDAO.getConfiguration().getWidth();
	}

	public void setWidth(int width) {
		configurationDAO.getConfiguration().setWidth(width);
	}

	public ProxyDTO getProxy() {
		Proxy proxy = configurationDAO.getConfiguration().getProxy();
		ProxyDTO proxyDTO = transformProxy2DTO(proxy);
		return proxyDTO;
	}

	public void setProxy(ProtocolDTO proxyProtocolDTO, boolean enableProxy)
			throws CheckException {

		if (proxyProtocolDTO == null) {
			configurationDAO.getConfiguration().getProxy()
					.setProxyProtocol(null);
			configurationDAO.getConfiguration().getProxy()
					.setEnableProxy(false);
		} else {
			final AbstractProtocole protocole = AbstractProtocoleFactory
					.getProtocol(proxyProtocolDTO.getUrl(),
							proxyProtocolDTO.getPort(),
							proxyProtocolDTO.getLogin(),
							proxyProtocolDTO.getPassword(),
							proxyProtocolDTO.getProtocolType());
			if (protocole == null) {
				throw new CheckException("Proxy protocol type error.");
			}

			protocole.setConnectionTimeOut("0");
			protocole.setReadTimeOut("0");

			protocole.checkData();

			configurationDAO.getConfiguration().getProxy()
					.setProxyProtocol(protocole);
			configurationDAO.getConfiguration().getProxy()
					.setEnableProxy(enableProxy);
		}
	}

	public void loadProxy() {

		System.out.println("Loading proxy configuration...");

		System.clearProperty("ftp.proxyHost");
		System.clearProperty("ftp.proxyPort");
		System.clearProperty("http.proxyHost");
		System.clearProperty("http.proxyPort");
		System.clearProperty("https.proxyHost");
		System.clearProperty("https.proxyPort");
		System.clearProperty("socksProxyHost");
		System.clearProperty("socksProxyPort");

		Proxy proxy = configurationDAO.getConfiguration().getProxy();
		final AbstractProtocole protocole = proxy.getProxyProtocol();
		boolean enableProxy = proxy.isEnableProxy();
		if (protocole != null && enableProxy) {
			ProtocolType protocolType = protocole.getProtocolType();
			if (protocolType.equals(ProtocolType.FTP)) {
				System.setProperty("ftp.proxyHost", protocole.getUrl());
				System.setProperty("ftp.proxyPort", protocole.getPort());
			} else if (protocolType.equals(ProtocolType.HTTP)) {
				System.setProperty("http.proxyHost", protocole.getUrl());
				System.setProperty("http.proxyPort", protocole.getPort());
			} else if (protocolType.equals(ProtocolType.HTTPS)) {
				System.setProperty("https.proxyHost", protocole.getUrl());
				System.setProperty("https.proxyPort", protocole.getPort());
			} else if (protocolType.equals(ProtocolType.SOCKS4)) {
				System.setProperty("socksProxyHost", protocole.getUrl());
				System.setProperty("socksProxyPort", protocole.getPort());
			} else if (protocolType.equals(ProtocolType.SOCKS5)) {
				System.setProperty("socksProxyHost", protocole.getUrl());
				System.setProperty("socksProxyPort", protocole.getPort());
			}
			if (!protocole.getLogin().equals("anonymous")) {
				Authenticator authenticator = new Authenticator() {
					@Override
					public PasswordAuthentication getPasswordAuthentication() {
						return (new PasswordAuthentication(
								protocole.getLogin(), protocole.getPassword()
										.toCharArray()));
					}
				};
				Authenticator.setDefault(authenticator);
			} else {
				Authenticator.setDefault(null);
			}

			System.out.println("Proxy loaded: "
					+ protocole.getProtocolType().getPrompt()
					+ protocole.getUrl() + ":" + protocole.getPort());

		} else {
			System.out.println("No proxy available.");
		}
	}

	/* DEPRECATED */

	public Set<String> getAddonSearchDirectoryPaths() {
		return configurationDAO.getConfiguration()
				.getAddonSearchDirectoryPaths();
	}

	public void resetAddonSearchDirectoryPaths() {
		configurationDAO.getConfiguration().resetAddonSearchDirectoryPaths();
	}

	public LauncherOptions getLauncherOptions() {
		return configurationDAO.getConfiguration().getLauncherOptions();
	}

	public void resetLauncherOptions() {
		configurationDAO.getConfiguration().resetLauncherOptions();
	}
}
