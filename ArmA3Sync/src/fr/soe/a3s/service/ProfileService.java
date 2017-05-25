package fr.soe.a3s.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import fr.soe.a3s.constant.DefaultProfileName;
import fr.soe.a3s.dao.ConfigurationDAO;
import fr.soe.a3s.dao.ProfileDAO;
import fr.soe.a3s.domain.Profile;
import fr.soe.a3s.domain.TreeDirectory;
import fr.soe.a3s.domain.TreeLeaf;
import fr.soe.a3s.domain.TreeNode;
import fr.soe.a3s.domain.configration.LauncherOptions;
import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.configuration.LauncherOptionsDTO;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.ProfileException;
import fr.soe.a3s.exception.WritingException;

public class ProfileService extends ObjectDTOtransformer {

	private static final ConfigurationDAO configurationDAO = new ConfigurationDAO();
	private static final ProfileDAO profileDAO = new ProfileDAO();

	public void readAll() throws LoadingException {
		System.out.println("Loading profiles...");
		profileDAO.readProfiles();
	}

	public void setAdditionalParameters(String additionalParameters) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();
		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.setAdditionalParameters(additionalParameters);
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
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

	public void createProfile(String profileName) throws ProfileException,
			WritingException {

		Profile profile = new Profile(profileName);
		if (profileDAO.getMap().containsKey(profileName)) {
			throw new ProfileException("Profile with name " + profileName
					+ " already exists.");
		}
		profileDAO.getMap().put(profile.getName(), profile);
		profileDAO.write(profile);
	}

	public void duplicateProfile(String profileName, String duplicateProfileName)
			throws WritingException {

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
			profileDAO.write(duplicateProfile);
		}
	}

	public void renameProfile(String initProfileName, String newProfileName)
			throws ProfileException, WritingException {

		Profile profile = profileDAO.getMap().get(initProfileName);
		if (profile == null) {
			throw new ProfileException("Profile with name " + initProfileName
					+ " does not exists.");
		}
		profile.setName(newProfileName);
		profileDAO.getMap().remove(initProfileName);
		profileDAO.delete(profile);
		profileDAO.getMap().put(profile.getName(), profile);
		profileDAO.write(profile);
	}

	public void removeProfile(String profileName) throws ProfileException {

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile == null) {
			throw new ProfileException("Profile with name " + profileName
					+ " does not exists.");
		}
		profileDAO.getMap().remove(profileName);
		profileDAO.delete(profile);
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
				duplicateTreedDirectory2.setModsetType(treeDirectory2
						.getModsetType());
				duplicateTreedDirectory2.setModsetRepositoryName(treeDirectory2
						.getModsetRepositoryName());
				duplicateTreeDirectory.addTreeNode(duplicateTreedDirectory2);
				duplicateTree(treeDirectory2, duplicateTreedDirectory2);
			}
		}
	}

	private TreeLeaf duplicateTreeLeaf(TreeLeaf treeLeaf) {
		TreeLeaf duplicateTreeLeaf = new TreeLeaf();
		duplicateTreeLeaf.setName(treeLeaf.getName());
		duplicateTreeLeaf.setSelected(treeLeaf.isSelected());
		duplicateTreeLeaf.setOptional(treeLeaf.isOptional());
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
		duplicateLauncherOptions.setHugePages(launcherOptions.isHugePages());
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
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
		}
	}

	public void removeAddonSearchDirectoryPath(String path) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getAddonSearchDirectories().remove(path);
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
		}
	}

	public String getLastAddedAddonSearchDirecotry() {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			return profile.getLastAddedAddonSearchDirectory();
		} else {
			return null;
		}
	}

	public void setLastAddedAddonSearchDirectory(String path) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.setLastAddedAddonSearchDirectory(path);
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
		}
	}

	public TreeDirectoryDTO getAddonGroups() {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		if (profileName == null) {
			profileName = DefaultProfileName.DEFAULT.getDescription();
			configurationDAO.getConfiguration().setProfileName(profileName);
		}
		Profile profile = profileDAO.getMap().get(profileName);
		TreeDirectoryDTO treeDirectoryDTO = new TreeDirectoryDTO();
		treeDirectoryDTO.setName("racine2");
		treeDirectoryDTO.setParent(null);
		if (profile != null) {
			TreeDirectory racine = profile.getTree();
			transformTreeDirectory2DTO(racine, treeDirectoryDTO);
		}
		return treeDirectoryDTO;
	}

	public void setAddonGroups(TreeDirectoryDTO newTree) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		if (profileName == null) {
			profileName = DefaultProfileName.DEFAULT.getDescription();
			configurationDAO.getConfiguration().setProfileName(profileName);
		}
		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			TreeDirectory tree = profile.getTree();
			tree.getList().clear();
			transformDTO2TreeDirectory(newTree, tree);
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
		}
	}

	public void resetAddonPriority() {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getAddonNamesByPriority().clear();
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
		}
	}

	public void topAddonPriority(int index) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			List<String> list = profile.getAddonNamesByPriority();
			if (index != 0 && !(index > list.size() - 1)) {
				String name = list.get(index);
				list.remove(index);
				list.add(0, name);
				try {
					profileDAO.write(profile);
				} catch (WritingException e) {
				}
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
				try {
					profileDAO.write(profile);
				} catch (WritingException e) {
				}
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
				try {
					profileDAO.write(profile);
				} catch (WritingException e) {
				}
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
				try {
					profileDAO.write(profile);
				} catch (WritingException e) {
				}
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
				try {
					profileDAO.write(profile);
				} catch (WritingException e) {
				}
			}
		}
	}

	public void setGameProfile(String gameProfileName) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setGameProfile(gameProfileName);
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
		}
	}

	public void setCheckBoxShowScriptErrors(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setShowScriptErrors(selected);
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
		}
	}

	public void setCheckBoxNoPause(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setNoPause(selected);
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
		}
	}

	public void setCheckBoxFilePatching(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setFilePatching(selected);
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
		}
	}

	public void setCheckBoxWindowMode(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setWindowMode(selected);
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
		}
	}

	public void setCheckBoxCheckSignatures(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setCheckSignatures(selected);
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
		}
	}

	public void setCheckBoxAutoRestart(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setAutoRestart(selected);
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
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
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
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
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
		}
	}

	public void setEnableHT(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setEnableHT(selected);
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
		}
	}

	public void setHugePages(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setHugePages(selected);
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
		}
	}

	public void setExThreads(String exThreads) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setExThreadsSelection(exThreads);
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
		}
	}

	public void setMalloc(String mallocDll) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setMallocSelection(mallocDll);
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
		}
	}

	public void setNoSplashScreen(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setNoSplashScreen(selected);
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
		}
	}

	public void setDefaultWorld(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setDefaultWorld(selected);
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
		}
	}

	public void setNoLogs(boolean selected) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setNoLogs(selected);
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
		}
	}

	public void setArmA3ExePath(String arma3ExePath) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.getLauncherOptions().setArma3ExePath(arma3ExePath);
			try {
				profileDAO.write(profile);
			} catch (WritingException e) {
			}
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
