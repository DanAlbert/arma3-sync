package fr.soe.a3s.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fr.soe.a3s.constant.DefaultProfileName;
import fr.soe.a3s.dao.ConfigurationDAO;
import fr.soe.a3s.dao.ProfileDAO;
import fr.soe.a3s.domain.Profile;
import fr.soe.a3s.domain.TreeDirectory;
import fr.soe.a3s.domain.TreeLeaf;
import fr.soe.a3s.domain.TreeNode;
import fr.soe.a3s.domain.configration.LauncherOptions;
import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.TreeLeafDTO;
import fr.soe.a3s.dto.TreeNodeDTO;
import fr.soe.a3s.dto.configuration.LauncherOptionsDTO;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.ProfileException;
import fr.soe.a3s.exception.WritingException;

public class ProfileService extends ObjectDTOtransformer {

	private static final ConfigurationDAO configurationDAO = new ConfigurationDAO();
	private static final ProfileDAO profileDAO = new ProfileDAO();

	public void readAll() throws LoadingException {

		List<String> profilesFailedToLoad = profileDAO.readProfiles();
		if (!profilesFailedToLoad.isEmpty()) {
			String message = "Failded to load profile:";
			for (String name : profilesFailedToLoad) {
				message = message + "\n" + " - " + name;
			}
			throw new LoadingException(message);
		}
	}

	public void writeAll() throws WritingException {
		profileDAO.writeProfiles();
	}

	public void setAdditionalParameters(String additionalParameters)
			throws ProfileException {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();
		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.setAdditionalParameters(additionalParameters);
		} else {
			throw new ProfileException("Profile " + profileName + " not found!");
		}
	}

	public String getAdditionalParameters() {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			return profile.getAdditionalParameters();
		} else {
			return null;
		}
	}

	public List<String> getProfileNames() {

		List<String> list = new ArrayList<String>();
		for (Iterator<String> i = profileDAO.getMap().keySet().iterator(); i
				.hasNext();) {
			list.add(i.next());
		}
		Collections.sort(list);
		return list;
	}

	public void createProfile(String profileName) throws ProfileException {

		Profile profile = new Profile(profileName);
		if (profileDAO.getMap().containsKey(profileName)) {
			throw new ProfileException("Profile with name " + profileName
					+ " already exists.");
		}
		profileDAO.getMap().put(profile.getName(), profile);
	}

	public void duplicateProfile(String profileName, String duplicateProfileName) {

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			Profile duplicateProfile = new Profile(duplicateProfileName);
			TreeDirectory treeDirectory = profile.getTree();
			TreeDirectory duplicateTreeDirectory = duplicateProfile.getTree();
			duplicateTree(treeDirectory, duplicateTreeDirectory);
			duplicateProfile.setAddonSearchDirectories(profile
					.getAddonSearchDirectories());
			duplicateProfile.setAdditionalParameters(profile
					.getAdditionalParameters());
			duplicateLauncherOptions(profile.getLauncherOptions(),
					duplicateProfile.getLauncherOptions());
			profileDAO.getMap().put(duplicateProfile.getName(),
					duplicateProfile);
		}
	}

	public void renameProfile(String initProfileName, String newProfileName)
			throws ProfileException {

		Profile profile = profileDAO.getMap().get(initProfileName);
		if (profile == null) {
			throw new ProfileException("Profile with name " + initProfileName
					+ " does not exists.");
		}
		profile.setName(newProfileName);
		profileDAO.getMap().remove(initProfileName);
		profileDAO.getMap().put(profile.getName(), profile);
	}

	public void removeProfile(String profileName) throws ProfileException {

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile == null) {
			throw new ProfileException("Profile with name " + profileName
					+ " does not exists.");
		}
		profileDAO.getMap().remove(profileName);
	}

	private void duplicateTree(TreeDirectory treeDirectory,
			TreeDirectory duplicateTreeDirectory) {

		List<TreeNode> list = treeDirectory.getList();

		for (TreeNode treeNode : list) {
			if (treeNode.isLeaf()) {
				TreeLeaf treeLeaf = (TreeLeaf) treeNode;
				TreeLeaf duplicateTreeLeaf = duplicateTreeLeaf(treeLeaf);
				duplicateTreeLeaf.setParent(duplicateTreeDirectory);
				duplicateTreeDirectory.addTreeNode(duplicateTreeLeaf);
			} else {
				TreeDirectory treeDirectory2 = (TreeDirectory) treeNode;
				TreeDirectory duplicateTreedDirectory2 = new TreeDirectory(
						treeDirectory2.getName(), duplicateTreeDirectory);
				duplicateTreedDirectory2.setSelected(treeDirectory2
						.isSelected());
				duplicateTreeDirectory.addTreeNode(duplicateTreedDirectory2);
				duplicateTree(treeDirectory2, duplicateTreedDirectory2);
			}
		}
	}

	private TreeLeaf duplicateTreeLeaf(TreeLeaf treeLeaf) {
		TreeLeaf duplicateTreeLeaf = new TreeLeaf();
		duplicateTreeLeaf.setName(treeLeaf.getName());
		duplicateTreeLeaf.setSelected(treeLeaf.isSelected());
		return duplicateTreeLeaf;
	}

	private void duplicateLauncherOptions(LauncherOptions launcherOptions,
			LauncherOptions duplicateLauncherOptions) {

		duplicateLauncherOptions.setCpuCountSelection(launcherOptions
				.getCpuCountSelection());
		duplicateLauncherOptions.setExThreadsSelection(launcherOptions
				.getExThreadsSelection());
		duplicateLauncherOptions.setMallocSelection(launcherOptions
				.getMallocSelection());
		duplicateLauncherOptions.setDefaultWorld(launcherOptions
				.isDefaultWorld());
		duplicateLauncherOptions.setGameProfile(launcherOptions
				.getGameProfile());
		duplicateLauncherOptions.setMaxMemorySelection(launcherOptions
				.getMaxMemorySelection());
		duplicateLauncherOptions.setFilePatching(launcherOptions
				.isFilePatching());
		duplicateLauncherOptions.setNoLogs(launcherOptions.isNologs());
		duplicateLauncherOptions.setNoPause(launcherOptions.isNoPause());
		duplicateLauncherOptions.setEnableHT(launcherOptions.isEnableHT());
		duplicateLauncherOptions.setNoSplashScreen(launcherOptions.isNoPause());
		duplicateLauncherOptions.setShowScriptErrors(launcherOptions
				.isShowScriptErrors());
		duplicateLauncherOptions.setWindowMode(launcherOptions.isWindowMode());
		duplicateLauncherOptions.setCheckSignatures(launcherOptions
				.isCheckSignatures());
		duplicateLauncherOptions
				.setAutoRestart(launcherOptions.isAutoRestart());
		duplicateLauncherOptions.setArma3ExePath(launcherOptions
				.getArma3ExePath());
	}

	public LauncherOptionsDTO getLauncherOptions() {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			LauncherOptions launcherOptions = profile.getLauncherOptions();
			LauncherOptionsDTO launcherOptionsDTO = transformLauncherOptions2DTO(launcherOptions);
			return launcherOptionsDTO;
		} else {
			return null;
		}
	}

	public List<String> getAddonSearchDirectoryPaths() {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile == null) {
			return new ArrayList<String>();
		} else {
			return profile.getAddonSearchDirectories();
		}
	}

	public void addAddonSearchDirectoryPath(String path) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getAddonSearchDirectories().add(path);
		}
	}

	public void removeAddonSearchDirectoryPath(String path) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getAddonSearchDirectories().remove(path);
		}
	}

	public TreeDirectoryDTO getAddonGroupsTree() {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		if (profileName == null) {
			configurationDAO.getConfiguration().setProfileName(
					DefaultProfileName.DEFAULT.getDescription());
		}
		Profile profile = profileDAO.getMap().get(profileName);
		TreeDirectoryDTO treeDirectoryDTO = new TreeDirectoryDTO();
		treeDirectoryDTO.setName("racine2");
		treeDirectoryDTO.setParent(null);
		if (profile != null) {
			TreeDirectory treeDirectory = profile.getTree();
			transformTreeDirectory2DTO(treeDirectory, treeDirectoryDTO);
		}
		return treeDirectoryDTO;
	}

	public void setAddonGroupsTree(TreeDirectoryDTO treeDirectoryDTO) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		if (profileName == null) {
			configurationDAO.getConfiguration().setProfileName(
					DefaultProfileName.DEFAULT.getDescription());
		}
		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			TreeDirectory treeDirectory = profile.getTree();
			treeDirectory.getList().clear();
			transformDTO2TreeDirectory(treeDirectoryDTO, treeDirectory);
		}
	}

	public void merge(TreeDirectoryDTO sourceTreeDirectoryDTO,
			TreeDirectoryDTO targetTreeDirectoryDTO) {

		List<TreeDirectoryDTO> listSourceDirectory = new ArrayList<TreeDirectoryDTO>();

		for (TreeNodeDTO node : sourceTreeDirectoryDTO.getList()) {
			if (!node.isLeaf()) {
				TreeDirectoryDTO directory = (TreeDirectoryDTO) node;
				listSourceDirectory.add(directory);
			}
		}

		List<TreeDirectoryDTO> listTargetDirectory = new ArrayList<TreeDirectoryDTO>();

		for (TreeNodeDTO node : targetTreeDirectoryDTO.getList()) {
			if (!node.isLeaf()) {
				TreeDirectoryDTO directory = (TreeDirectoryDTO) node;
				listTargetDirectory.add(directory);
			}
		}

		for (TreeDirectoryDTO targetDirectory : listTargetDirectory) {
			for (TreeDirectoryDTO sourceDirectory : listSourceDirectory) {
				List<String> listLeafNames = new ArrayList<String>();
				List<String> listDirectoryNames = new ArrayList<String>();
				for (TreeNodeDTO targetNode : targetDirectory.getList()) {
					if (targetNode.isLeaf()) {
						listLeafNames.add(targetNode.getName());
					} else {
						listDirectoryNames.add(targetNode.getName());
					}
				}
				if (targetDirectory.getName().equals(sourceDirectory.getName())) {
					for (TreeNodeDTO sourceNode : sourceDirectory.getList()) {
						if (sourceNode.isLeaf()
								&& !listLeafNames
										.contains(sourceNode.getName())) {
							TreeLeafDTO treeLeafDTO = new TreeLeafDTO();
							treeLeafDTO.setName(sourceNode.getName());
							treeLeafDTO.setParent(targetDirectory);
							targetDirectory.addTreeNode(treeLeafDTO);
						} else if (!sourceNode.isLeaf()
								&& !listDirectoryNames.contains(sourceNode
										.getName())) {
							TreeDirectoryDTO treeDirectoryDTO = new TreeDirectoryDTO();
							treeDirectoryDTO.setName(sourceNode.getName());
							treeDirectoryDTO.setParent(targetDirectory);
							targetDirectory.addTreeNode(treeDirectoryDTO);
						}
					}
				}
				merge(sourceDirectory, targetDirectory);
			}
		}
		setAddonGroupsTree(targetTreeDirectoryDTO);
	}

	public List<String> getAddonNamesByPriority() {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			List<String> list = profile.getAddonNamesByPriority();
			TreeDirectory treeDirectory = profile.getTree();
			Set<String> extractedAddonNames = new TreeSet<String>();
			getAddonsByName(treeDirectory, extractedAddonNames);
			if (list.isEmpty()) {
				list.addAll(extractedAddonNames);
				return list;
			} else {
				Iterator iter = extractedAddonNames.iterator();
				while (iter.hasNext()) {
					String name = (String) iter.next();
					if (!list.contains(name)) {
						list.add(name);
					}
				}
				List<String> addonNamesToRemove = new ArrayList<String>();
				for (String stg : list) {
					if (!extractedAddonNames.contains(stg)) {
						addonNamesToRemove.add(stg);
					}
				}
				list.removeAll(addonNamesToRemove);
				return list;
			}
		}
		return null;
	}

	private void getAddonsByName(TreeNode treendNode, Set<String> set) {

		if (treendNode.isLeaf()) {
			TreeLeaf treeLeaf = (TreeLeaf) treendNode;
			set.add(treeLeaf.getName());
		} else {
			TreeDirectory treeDirectory = (TreeDirectory) treendNode;
			for (TreeNode node : treeDirectory.getList()) {
				getAddonsByName(node, set);
			}
		}
	}

	public void upAddonPriority(int index) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			List<String> list = profile.getAddonNamesByPriority();
			if (index != 0 && !(index > list.size() - 1)) {
				String name = list.get(index);
				String nextName = list.get(index - 1);
				list.set(index, nextName);
				list.set(index - 1, name);
			}
		}
	}

	public void downAddonPriority(int index) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			List<String> list = profile.getAddonNamesByPriority();
			if (!(index >= list.size() - 1)) {
				String name = list.get(index);
				String previousName = list.get(index + 1);
				list.set(index, previousName);
				list.set(index + 1, name);
			}
		}
	}

	public void upDirectoryPriority(int index) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			List<String> list = profile.getAddonSearchDirectories();
			if (index != 0 && !(index > list.size() - 1)) {
				String name = list.get(index);
				String nextName = list.get(index - 1);
				list.set(index, nextName);
				list.set(index - 1, name);
			}
		}
	}

	public void downDirectoryPriority(int index) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			List<String> list = profile.getAddonSearchDirectories();
			if (!(index >= list.size() - 1)) {
				String name = list.get(index);
				String previousName = list.get(index + 1);
				list.set(index, previousName);
				list.set(index + 1, name);
			}
		}
	}

	public void setGameProfile(String gameProfileName) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setGameProfile(gameProfileName);
		}
	}

	public void setCheckBoxShowScriptErrors(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setShowScriptErrors(selected);
		}
	}

	public void setCheckBoxNoPause(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setNoPause(selected);
		}
	}

	public void setCheckBoxFilePatching(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setFilePatching(selected);
		}
	}

	public void setCheckBoxWindowMode(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setWindowMode(selected);
		}
	}

	public void setCheckBoxCheckSignatures(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setCheckSignatures(selected);
		}
	}

	public void setCheckBoxAutoRestart(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setAutoRestart(selected);
		}
	}

	public boolean isAutoRestart() {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			return profile.getLauncherOptions().isAutoRestart();
		} else {
			return false;
		}
	}

	public void setMaxMemory(String maxMemory) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setMaxMemorySelection(maxMemory);
		}
	}

	public void setCpuCount(String cpuCount) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			if (cpuCount == null) {
				profile.getLauncherOptions().setCpuCountSelection(0);
			} else {
				profile.getLauncherOptions().setCpuCountSelection(
						Integer.parseInt(cpuCount));
			}
		}
	}

	public void setEnableHT(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setEnableHT(selected);
		}
	}

	public void setExThreads(String exThreads) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setExThreadsSelection(exThreads);
		}
	}

	public void setMalloc(String mallocDll) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setMallocSelection(mallocDll);
		}
	}

	public void setNoSplashScreen(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setNoSplashScreen(selected);
		}
	}

	public void setDefaultWorld(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setDefaultWorld(selected);
		}
	}

	public void setNoLogs(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setNoLogs(selected);
		}
	}

	public void setArmA3ExePath(String arma3ExePath) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setArma3ExePath(arma3ExePath);
		}
	}

	public String getArma3ExePath() {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			return profile.getLauncherOptions().getArma3ExePath();
		} else {
			return null;
		}
	}
}
