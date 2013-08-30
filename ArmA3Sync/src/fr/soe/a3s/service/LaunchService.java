package fr.soe.a3s.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.soe.a3s.constant.GameVersions;
import fr.soe.a3s.dao.AddonDAO;
import fr.soe.a3s.dao.ConfigurationDAO;
import fr.soe.a3s.dao.FileAccessMethods;
import fr.soe.a3s.dao.LauncherDAO;
import fr.soe.a3s.dao.ProfileDAO;
import fr.soe.a3s.domain.Addon;
import fr.soe.a3s.domain.Profile;
import fr.soe.a3s.domain.TreeDirectory;
import fr.soe.a3s.domain.TreeLeaf;
import fr.soe.a3s.domain.TreeNode;
import fr.soe.a3s.domain.configration.AiAOptions;
import fr.soe.a3s.domain.configration.Configuration;
import fr.soe.a3s.domain.configration.ExternalApplication;
import fr.soe.a3s.domain.configration.FavoriteServer;
import fr.soe.a3s.domain.configration.LauncherOptions;
import fr.soe.a3s.exception.LaunchException;

public class LaunchService {

	private LauncherDAO launcherDAO = new LauncherDAO();
	private static final ConfigurationDAO configurationDAO = new ConfigurationDAO();
	private static final AddonDAO addonDAO = new AddonDAO();
	private static final ProfileDAO profileDAO = new ProfileDAO();
	private List<String> missingAddonNames = new ArrayList<String>();

	public void checkArmA3ExecutableLocation() throws LaunchException {

		Configuration configuration = configurationDAO.getConfiguration();
		String arma3ExePath = configuration.getLauncherOptions()
				.getArma3ExePath();

		if (arma3ExePath == null || "".equals(arma3ExePath)) {
			throw new LaunchException(
					"ArmA 3 Executable location is wrong or missing.\n Please checkout Launcher Options panel.");
		}
	}

	public void checkAllinArmALocation() throws LaunchException {

		String allInArmaPath = configurationDAO.getConfiguration()
				.getAiaOptions().getAllinArmaPath();
		String gameVersion = configurationDAO.getConfiguration()
				.getGameVersion();
		if (gameVersion.equals(GameVersions.ARMA3_AIA.getDescription())) {
			if (allInArmaPath == null || "".equals(allInArmaPath)) {
				throw new LaunchException("@AllinArma is missing.");
			}
		}
	}

	public void launchExternalApplications() throws LaunchException,
			InterruptedException {

		Configuration configuration = configurationDAO.getConfiguration();
		List<ExternalApplication> apps = configuration
				.getExternalApplications();

		List<Callable<Integer>> runnables = new ArrayList<Callable<Integer>>();

		for (ExternalApplication externalApplication : apps) {
			if (externalApplication.isEnable()) {
				String launchPath = externalApplication.getExecutablePath();
				File executableFile = new File(launchPath);
				if (executableFile.exists()) {
					String executableName = executableFile.getName();
					launcherDAO = new LauncherDAO();
					if (!launcherDAO.isApplicationRunning(executableName)) {
						String runParameters = externalApplication
								.getParameters();
						List<String> params = new ArrayList<String>();
						params.add(runParameters.trim());
						try {
							Callable<Integer> c = launcherDAO.call(launchPath,
									params);
							runnables.add(c);
						} catch (Exception e) {
							e.printStackTrace();
							throw new LaunchException("Failed to launch "
									+ launchPath);
						}
					}
				}
			}
		}
		if (apps.size() > 0) {
			ExecutorService executor = Executors
					.newFixedThreadPool(apps.size());
			for (Callable<Integer> c : runnables) {
				executor.submit(c);
				executor.shutdown();
			}
		}
	}

	public boolean isArmA3Running() {
		if (!launcherDAO.isApplicationRunning("arma3.exe")) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isSteamRunning() {
		if (!launcherDAO.isApplicationRunning("steam.exe")) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isTS3Running() {
		if (!launcherDAO.isApplicationRunning("ts3client_win32.exe")
				&& !launcherDAO.isApplicationRunning("ts3client_win64.exe")) {
			return false;
		} else {
			return true;
		}
	}

	@Deprecated
	public void launchArmA3WithSteam() throws LaunchException {

		/* Get steam.exe path */
		String steamLaunchPath = configurationDAO.getConfiguration()
				.getLauncherOptions().getSteamExePath();

		steamLaunchPath = "\"" + steamLaunchPath + "\"";

		/* Purge Steam appcache */
		File steamFolder = new File(steamLaunchPath).getParentFile();
		File appcache = new File(steamFolder + "/appcache");
		if (appcache.exists()) {
			FileAccessMethods.deleteDirectory(appcache);
		}

		/* Run Parameters */
		List<String> params = determineRunParameters();

		try {
			launcherDAO.runArmA3WithSteam(steamLaunchPath, params);
		} catch (Exception e) {
			e.printStackTrace();
			throw new LaunchException(e.getMessage());
		}
	}

	public void launchArmA3() throws LaunchException {

		/* Get steam.exe path */
		String arma3Path = configurationDAO.getConfiguration()
				.getLauncherOptions().getArma3ExePath();

		/* Run Parameters */
		List<String> params = determineRunParameters();
		launcherDAO = new LauncherDAO();

		// launcherDAO.run(arma3Path, runParameters);
		List<Callable<Integer>> runnables = new ArrayList<Callable<Integer>>();
		try {
			Callable<Integer> c = launcherDAO.call(arma3Path, params);
			runnables.add(c);
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.submit(c);
			executor.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
			throw new LaunchException("");
		}
	}

	private List<String> determineRunParameters() {

		List<String> params = new ArrayList<String>();
		params.addAll(getRunParameters());
		params.addAll(getAdditionalParameters());
		return params;
	}

	public List<String> getMissingAddons() {
		checkSelectedAddons();
		return this.missingAddonNames;
	}

	public String checkSelectedAddons() {

		this.missingAddonNames = new ArrayList<String>();
		Configuration configuration = configurationDAO.getConfiguration();

		String profileName = configuration.getProfileName();
		Profile profile = (Profile) profileDAO.getMap().get(profileName);
		TreeDirectory racine = profile.getTree();
		Set<String> addonNames = new TreeSet<String>();
		getSelectedAddonNames(racine, addonNames);
		if (addonNames.size() != 0) {
			Iterator<String> iterator = addonNames.iterator();
			while (iterator.hasNext()) {
				String addonName = iterator.next();
				Addon addon = addonDAO.getMap().get(addonName.toLowerCase());
				if (addon == null) {
					missingAddonNames.add(addonName);
				}
			}
		}

		String message = null;
		if (missingAddonNames.size() != 0) {
			StringBuffer stb = new StringBuffer();
			for (int i = 0; i < missingAddonNames.size() - 1; i++) {
				stb.append(missingAddonNames.get(i) + ";");
			}
			String name = missingAddonNames.get(missingAddonNames.size() - 1);
			stb.append(name);

			message = "The following selected addons are missing:" + "\n" + stb
					+ ".";
		}
		return message;
	}

	public List<String> getRunParameters() {

		List<String> params = new ArrayList<String>();

		Configuration configuration = configurationDAO.getConfiguration();
		String profileName = configuration.getProfileName();
		Profile profile = (Profile) profileDAO.getMap().get(profileName);

		if (profile == null) {
			return null;
		}

		List<String> listAddonNamesByPriority = profile
				.getAddonNamesByPriority();
		TreeDirectory racine = profile.getTree();
		Set<String> selectedAddonNames = new TreeSet<String>();
		getSelectedAddonNames(racine, selectedAddonNames);
		List<String> runListAddonNames = new ArrayList<String>();
		for (String name : listAddonNamesByPriority) {
			if (selectedAddonNames.contains(name)) {
				runListAddonNames.add(name);
			}
		}

		/*
		 * Get the corresponding ordered list of Addon. Duplicate object for
		 * setting the path later
		 */
		List<Addon> addons = new ArrayList<Addon>();
		for (String name : runListAddonNames) {
			Addon addon = addonDAO.getMap().get(name.toLowerCase());
			if (addon != null) {// may happen if addon is not present into
								// available addons list
				Addon clone = new Addon(addon.getName(), addon.getPath());
				addons.add(clone);
			}
		}

		/* Clean addon paths for those inside the arma 3 installation directory */
		String arma3ExePath = configuration.getLauncherOptions()
				.getArma3ExePath();

		if (arma3ExePath != null) {
			String parentArma3ExePath = new File(arma3ExePath).getParentFile()
					.getAbsolutePath().toLowerCase();
			for (Addon addon : addons) {
				String path = addon.getPath().toLowerCase();
				if (path.contains(parentArma3ExePath)
						&& !path.equals(parentArma3ExePath)) {
					path = path.substring(parentArma3ExePath.length() + 1);
					addon.setPath(path);
				} else if (path.equals(parentArma3ExePath)) {
					addon.setPath("");
				}
			}
		}

		/* Get list of addon list with same path */
		List<List<Addon>> ordererAddonListByPath = new ArrayList<List<Addon>>();
		for (int i = 0; i < addons.size(); i++) {
			if (ordererAddonListByPath.isEmpty()) {
				List<Addon> list1 = new ArrayList<Addon>();
				list1.add(addons.get(0));
				ordererAddonListByPath.add(list1);
				continue;
			}
			Addon addon = addons.get(i);
			List<Addon> list1 = ordererAddonListByPath
					.get(ordererAddonListByPath.size() - 1);
			if (list1.get(0).getPath().equals(addon.getPath())) {
				list1.add(addon);
			} else {
				List<Addon> list2 = new ArrayList<Addon>();
				list2.add(addon);
				ordererAddonListByPath.add(list2);
			}
		}

		String mods = "";
		for (int i = 0; i < ordererAddonListByPath.size(); i++) {
			List<Addon> list1 = ordererAddonListByPath.get(i);
			String path = list1.get(0).getPath();
			if (path.isEmpty()) {
				mods = mods + "-mod=";
			} else {
				mods = mods + "-mod=" + path + "\\";
			}
			for (Addon addon : list1) {
				mods = mods + addon.getName() + ";";
			}
		}
		String[] tab = mods.split("-mod=");

		for (int i = 0; i < tab.length; i++) {
			String param = tab[i].trim();
			if (!param.isEmpty()) {
				params.add("-mod=" + tab[i]);
			}
		}

		/* Build runParameters */
		/* AllinArma */
		if (configuration.getGameVersion().equals(
				GameVersions.ARMA3_AIA.getDescription())) {
			AiAOptions aiAOptions = configuration.getAiaOptions();
			String path = aiAOptions.getAllinArmaPath();

			if (path != null && arma3ExePath != null) {
				path = path.toLowerCase();
				String parentArma3ExePath = new File(arma3ExePath)
						.getParentFile().getAbsolutePath().toLowerCase();
				if (path.contains(parentArma3ExePath)
						&& !path.equals(parentArma3ExePath)) {
					path = path.substring(parentArma3ExePath.length() + 1);
				}
				/*
				 * @AllInArma\ProductDummies;%_ARMA1_PATH%\DBE1;%_ARMA1_PATH%;@
				 * AllInArma\A1Dummies;
				 * %_ARMA2_PATH%;%_ARMA2OA_PATH%;%_ARMA2OA_PATH%\Expansion ;
				 * %_TKOH_PATH%;@A1A2ObjectMerge;%_ARMA3_PATH%;@AllInArma\Core;
				 * 
				 * @AllInArma\PostA3"
				 */
				
				String allInArma = " -mod=" + path + "\\ProductDummies" + ";";
				
				if (aiAOptions.getArmaPath()!=null && !"".equals(aiAOptions.getArmaPath())){
					allInArma = allInArma + aiAOptions.getArmaPath() + "\\DBE1" + ";";
				}
				
				allInArma = allInArma + path+ "\\A1Dummies" + ";";
				
				if (aiAOptions.getArma2Path()!=null && !"".equals(aiAOptions.getArma2Path())){
					allInArma = allInArma + aiAOptions.getArma2Path() + ";";
				}
				
				if (aiAOptions.getArma2OAPath()!=null && !"".equals(aiAOptions.getArma2OAPath())){
					allInArma = allInArma + aiAOptions.getArma2OAPath() + ";" + aiAOptions.getArma2OAPath() + "\\Expansion" + ";";
				}
				
				if (aiAOptions.getTohPath()!=null && !"".equals(aiAOptions.getTohPath())){
					allInArma = allInArma + aiAOptions.getTohPath() + ";";
				}
				
				allInArma = allInArma + "@A1A2ObjectMerge;" + parentArma3ExePath
						+ ";" + path + "\\Core" + ";" + path + "\\PostA3" + ";";
				
//				String allInArma = " -mod=" + path + "\\ProductDummies" + ";"
//						+ aiAOptions.getArmaPath() + "\\DBE1" + ";" + path
//						+ "\\A1Dummies" + ";" + aiAOptions.getArma2Path() + ";"
//						+ aiAOptions.getArma2OAPath() + ";"
//						+ aiAOptions.getArma2OAPath() + "\\Expansion" + ";"
//						+ aiAOptions.getTohPath() + ";" + parentArma3ExePath
//						+ ";" + path + "\\Core" + ";" + path + "\\PostA3" + ";";
				params.add(allInArma);
			}
		}

		/* Launcher options */
		LauncherOptions launcherOptions = configuration.getLauncherOptions();
		if (launcherOptions.getGameProfile() != null) {
			params.add("-name=" + launcherOptions.getGameProfile());
		}
		if (launcherOptions.isShowScriptErrors()) {
			params.add("-showScriptErrors");
		}
		if (launcherOptions.isNoPause()) {
			params.add("-noPause");
		}
		if (launcherOptions.isWindowMode()) {
			params.add("-window");
		}
		if (launcherOptions.isXpCompatibilityMode()) {
			params.add("-winxp");
		}
		if (launcherOptions.getMaxMemorySelection() != null) {
			params.add("-maxMem=" + launcherOptions.getMaxMemorySelection());
		}
		if (launcherOptions.getCpuCountSelection() != 0) {
			params.add("-cpuCount=" + launcherOptions.getCpuCountSelection());
		}
		if (launcherOptions.isNoSplashScreen()) {
			params.add("-nosplash");
		}
		if (launcherOptions.isDefaultWorld()) {
			params.add("-world=empty");
		}
		if (launcherOptions.isNologs()){
			params.add("-nologs");
		}
		
		// Join Server
		String serverName = configuration.getServerName();
		if (serverName != null) {
			for (FavoriteServer s : configuration.getFavoriteServers()) {
				if (s.getName().equals(serverName)) {
					String ipAddress = s.getIpAddress();
					int port = s.getPort();
					String password = s.getPassword();
					params.add("-connect=" + ipAddress);
					params.add("-port=" + port);
					params.add("-password=" + password);
				}
			}
		}
		return params;
	}

	private List<String> getAdditionalParameters() {

		List<String> params = new ArrayList<String>();
		String profileName = configurationDAO.getConfiguration()
				.getProfileName();
		Profile profile = (Profile) profileDAO.getMap().get(profileName);
		if (profile != null) {
			String additionalParameters = profile.getAdditionalParameters();
			if (additionalParameters != null) {
				if (!additionalParameters.isEmpty()) {
					StringTokenizer stk = new StringTokenizer(
							additionalParameters, " -");
					int nbParameters = stk.countTokens();
					for (int i = 0; i < nbParameters; i++) {
						params.add("-" + stk.nextToken().trim());
					}
				}
			}
		}
		return params;
	}

	private void getSelectedAddonNames(TreeDirectory treeDirectory,
			Set<String> addonNames) {

		for (TreeNode treeNode : treeDirectory.getList()) {
			if (treeNode instanceof TreeLeaf) {
				TreeLeaf treeLeaf = (TreeLeaf) treeNode;
				if (treeLeaf.isSelected()) {
					addonNames.add(treeLeaf.getName());
				}
			} else {
				TreeDirectory d = (TreeDirectory) treeNode;
				getSelectedAddonNames(d, addonNames);
			}
		}
	}

	public void killSteam() {

		if (launcherDAO.isApplicationRunning("steam.exe")) {
			launcherDAO.killSteam("steam.exe");
		}
	}
}
