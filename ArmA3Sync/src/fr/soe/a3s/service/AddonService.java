package fr.soe.a3s.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import fr.soe.a3s.constant.GameSystemFolders;
import fr.soe.a3s.constant.ModsetType;
import fr.soe.a3s.dao.AddonDAO;
import fr.soe.a3s.dao.ConfigurationDAO;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.ProfileDAO;
import fr.soe.a3s.dao.repository.RepositoryDAO;
import fr.soe.a3s.domain.Addon;
import fr.soe.a3s.domain.Profile;
import fr.soe.a3s.domain.TreeDirectory;
import fr.soe.a3s.domain.TreeLeaf;
import fr.soe.a3s.domain.TreeNode;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.TreeLeafDTO;
import fr.soe.a3s.dto.TreeNodeDTO;

public class AddonService extends ObjectDTOtransformer implements
		DataAccessConstants {

	private static final ConfigurationDAO configurationDAO = new ConfigurationDAO();
	private static final ProfileDAO profileDAO = new ProfileDAO();
	private static final AddonDAO addonDAO = new AddonDAO();
	private static final RepositoryDAO repositoryDAO = new RepositoryDAO();
	private final List<String> excludedFilePathList = new ArrayList<String>();

	private TreeDirectory getAvailableAddonsTreeInstance() {

		List<String> list = new ArrayList<String>();

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();
		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			Iterator iter = profile.getAddonSearchDirectories().iterator();
			while (iter.hasNext()) {
				list.add((String) iter.next());
			}
		}

		List<String> newList = new ArrayList<String>();

		for (int i = 0; i < list.size(); i++) {
			String ipath = list.get(i);
			String ipathForCompare = ipath;

			if (!new File(ipath).exists()) {
				continue;
			}

			String osName = System.getProperty("os.name");
			if (osName.toLowerCase().contains("windows")) {
				ipathForCompare = ipath.toLowerCase();
			}
			String pathToKeep = ipath;

			File iparentFile = new File(ipath).getParentFile();

			if (iparentFile == null) {
				continue;
			} else if (!iparentFile.exists()) {
				continue;
			}

			for (int j = 0; j < list.size(); j++) {
				String jpath = list.get(j);

				if (!new File(jpath).exists()) {
					continue;
				}

				String jpathForCompare = jpath;
				if (osName.toLowerCase().contains("windows")) {
					jpathForCompare = jpath.toLowerCase();
				}

				File jparentFile = new File(jpath).getParentFile();

				if (jparentFile == null) {
					continue;
				} else if (!jparentFile.exists()) {
					continue;
				}

				if (!iparentFile.getAbsolutePath().equals(
						jparentFile.getAbsolutePath())) {
					if (ipathForCompare.contains(jpathForCompare)
							|| jpathForCompare.contains(ipathForCompare)) {
						if (jpath.length() < pathToKeep.length()) {
							pathToKeep = jpath;
						}
					}
				}
			}

			if (!newList.contains(pathToKeep)) {
				newList.add(pathToKeep);
			}
		}

		File arma3Directory = null;
		String arma3ExePath = profile.getLauncherOptions().getArma3ExePath();
		if (arma3ExePath != null) {
			if (!arma3ExePath.isEmpty()) {
				arma3Directory = new File(arma3ExePath).getParentFile();
			}
		}

		this.excludedFilePathList.clear();
		if (arma3Directory != null) {
			GameSystemFolders[] tab = GameSystemFolders.values();
			for (int i = 0; i < tab.length; i++) {
				File excludedFile = new File(arma3Directory + "/"
						+ tab[i].toString());
				this.excludedFilePathList.add(excludedFile.getAbsolutePath());
			}
		}

		addonDAO.getMap().clear();
		TreeDirectory racine = new TreeDirectory("racine1", null);

		for (String path : newList) {
			File file = new File(path);
			if (file.exists()) {
				TreeDirectory treeDirectory = new TreeDirectory(file.getName(),
						racine);
				racine.addTreeNode(treeDirectory);

				for (Iterator<String> iter = repositoryDAO.getMap().keySet()
						.iterator(); iter.hasNext();) {
					Repository repository = repositoryDAO.getMap().get(
							iter.next());
					if (file.getAbsolutePath().equals(
							repository.getDefaultDownloadLocation())) {
						treeDirectory.setModsetRepositoryName(repository
								.getName());
						treeDirectory.setModsetType(ModsetType.REPOSITORY);
						break;
					}
				}

				File[] subfiles = file.listFiles();
				if (subfiles != null) {
					for (File f : subfiles) {
						generateTree(f, treeDirectory);
					}
				}
			}
		}

		// keep marked directory, change terminal directory to leaf
		TreeDirectory availableAddonsTree = new TreeDirectory("racine1", null);

		if (racine.getList().size() == 1) {
			TreeDirectory parent = (TreeDirectory) racine.getList().get(0);
			for (TreeNode directory : parent.getList()) {
				TreeDirectory d = (TreeDirectory) directory;
				cleanTree(d, availableAddonsTree);
			}
		} else {
			for (TreeNode directory : racine.getList()) {
				TreeDirectory d = (TreeDirectory) directory;
				cleanTree(d, availableAddonsTree);
			}
		}

		return availableAddonsTree;
	}

	public boolean hasDuplicate(String name) {
		return addonDAO.hasDuplicate(name);
	}

	public TreeDirectoryDTO getAvailableAddonsTree() {

		TreeDirectory treeDirectory = getAvailableAddonsTreeInstance();
		TreeDirectoryDTO treeDirectoryDTO = new TreeDirectoryDTO();
		treeDirectoryDTO.setName("racine1");
		treeDirectoryDTO.setParent(null);
		transformTreeDirectory2DTO(treeDirectory, treeDirectoryDTO);
		return treeDirectoryDTO;
	}

	public TreeDirectoryDTO getAvailableAddonsList() {

		TreeDirectory treeDirectory = getAvailableAddonsTreeInstance();
		TreeDirectoryDTO treeDirectoryDTO = new TreeDirectoryDTO();
		treeDirectoryDTO.setName("racine1");
		treeDirectoryDTO.setParent(null);
		transformTreeDirectory2DTO(treeDirectory, treeDirectoryDTO);

		TreeDirectoryDTO newTreeDirectoryDTO = new TreeDirectoryDTO();
		newTreeDirectoryDTO.setName("racine1");
		newTreeDirectoryDTO.setParent(null);
		generateTreeList(treeDirectoryDTO, newTreeDirectoryDTO);
		return newTreeDirectoryDTO;
	}

	private void generateTreeList(TreeDirectoryDTO treeDirectoryDTO,
			TreeDirectoryDTO newTreeDirectoryDTO) {

		for (TreeNodeDTO treeNodeDTO : treeDirectoryDTO.getList()) {
			if (treeNodeDTO.isLeaf()) {
				if (newTreeDirectoryDTO.getList().isEmpty()) {
					newTreeDirectoryDTO.addTreeNode(treeNodeDTO);
				} else {
					List<String> leafNames = new ArrayList<String>();
					for (TreeNodeDTO n : newTreeDirectoryDTO.getList()) {
						leafNames.add(n.getName());
					}
					if (!leafNames.contains(treeNodeDTO.getName())) {
						newTreeDirectoryDTO.addTreeNode(treeNodeDTO);
					}
				}
			} else {
				generateTreeList((TreeDirectoryDTO) treeNodeDTO,
						newTreeDirectoryDTO);
			}
		}
	}

	private void generateTree(File file, TreeDirectory node) {

		if (file.isDirectory()
				&& (!excludedFilePathList.contains(file.getAbsolutePath()))) {

			TreeDirectory treeDirectory = new TreeDirectory(file.getName(),
					node);
			node.addTreeNode(treeDirectory);

			for (Iterator<String> iter = repositoryDAO.getMap().keySet()
					.iterator(); iter.hasNext();) {
				Repository repository = repositoryDAO.getMap().get(iter.next());
				if (file.getAbsolutePath().equals(
						repository.getDefaultDownloadLocation())) {
					treeDirectory.setModsetRepositoryName(repository.getName());
					treeDirectory.setModsetType(ModsetType.REPOSITORY);
					break;
				}
			}

			File[] subfiles = file.listFiles();
			boolean contains = false;
			if (subfiles == null) {
				return;
			}
			for (File f : subfiles) {
				if (f.getName().toLowerCase().equals("addons")) {
					File[] subfiles2 = f.listFiles();
					if (subfiles != null) {
						for (File f2 : subfiles2) {
							if (f2.getName().contains(PBO_EXTENSION)) {
								contains = true;
								break;
							}
						}
					}
				}
			}
			if (contains) {// it is an addon
				String name = treeDirectory.getName();
				Addon addon = new Addon(name, file.getParentFile()
						.getAbsolutePath());

				// Determine the symbolic key
				String key = addonDAO.determineNewAddonKey(name);
				addonDAO.getMap().put(key.toLowerCase(), addon);

				// Set directory name with addon key
				treeDirectory.setName(key);

				// Mark up every directories to true
				markRecursively(treeDirectory);

			} else if (!contains && subfiles.length != 0) {
				for (File f : subfiles) {
					generateTree(f, treeDirectory);
				}
			}
		}
	}

	private void markRecursively(TreeDirectory treeDirectory) {

		treeDirectory.setMarked(true);
		TreeDirectory parent = treeDirectory.getParent();
		if (parent != null) {
			markRecursively(parent);
		}
	}

	private void cleanTree(TreeDirectory directory,
			TreeDirectory directoryCleaned) {

		if (directory.isMarked() && directory.getList().size() != 0) {
			TreeDirectory newDirectory = new TreeDirectory(directory.getName(),
					directoryCleaned);
			directoryCleaned.addTreeNode(newDirectory);
			newDirectory.setModsetRepositoryName(directory
					.getModsetRepositoryName());
			newDirectory.setModsetType(directory.getModsetType());
			for (TreeNode n : directory.getList()) {
				TreeDirectory d = (TreeDirectory) n;
				cleanTree(d, newDirectory);
			}
		} else if (directory.isMarked() && directory.getList().size() == 0) {
			TreeLeaf newTreelLeaf = new TreeLeaf(directory.getName(),
					directoryCleaned);
			directoryCleaned.addTreeNode(newTreelLeaf);
		}
	}

	public List<String> getAddonsByPriorityList() {

		TreeDirectoryDTO availableAddonsList = getAvailableAddonsList();
		List<String> availableAddonsByName = new ArrayList<String>();
		for (TreeNodeDTO node : availableAddonsList.getList()) {
			availableAddonsByName.add(node.getName());
		}
		Collections.sort(availableAddonsByName, new SortIgnoreCase());

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();
		Profile profile = profileDAO.getMap().get(profileName);
		if (profile != null) {
			List<String> addonNamesByPriority = profile
					.getAddonNamesByPriority();
			if (availableAddonsByName.isEmpty()) {
				addonNamesByPriority.clear();
			} else {
				Iterator iter = availableAddonsByName.iterator();
				while (iter.hasNext()) {
					String name = (String) iter.next();
					if (!addonNamesByPriority.contains(name)) {
						addonNamesByPriority.add(name);
					}
				}
				List<String> addonNamesToRemove = new ArrayList<String>();
				for (String stg : addonNamesByPriority) {
					if (!availableAddonsByName.contains(stg)) {
						addonNamesToRemove.add(stg);
					}
				}
				addonNamesByPriority.removeAll(addonNamesToRemove);
			}
			return addonNamesByPriority;
		}
		return null;
	}

	private class SortIgnoreCase implements Comparator<Object> {
		@Override
		public int compare(Object o1, Object o2) {
			String s1 = (String) o1;
			String s2 = (String) o2;
			return s1.toLowerCase().compareTo(s2.toLowerCase());
		}
	}

	public void resolveDuplicates(TreeDirectoryDTO racine) {

		for (TreeNodeDTO node : racine.getList()) {
			TreeDirectoryDTO directory = (TreeDirectoryDTO) node;
			if (directory.getModsetType() != null) {
				if (directory.getModsetType().equals(ModsetType.REPOSITORY)
						| directory.getModsetType().equals(ModsetType.EVENT)) {
					String repositoryName = directory.getModsetRepositoryName();
					if (repositoryDAO.getMap().containsKey(repositoryName)) {
						Repository repository = repositoryDAO.getMap().get(
								repositoryName);
						String defaultDownloadLocation = repository
								.getDefaultDownloadLocation();
						resolveDuplicates(directory, defaultDownloadLocation);
					}
				}
			}
		}
	}

	private void resolveDuplicates(TreeDirectoryDTO directory,
			String repositoryPath) {

		List<TreeNodeDTO> list = directory.getList();

		for (TreeNodeDTO node : list) {
			if (node.isLeaf()) {
				TreeLeafDTO leaf = (TreeLeafDTO) node;
				boolean duplicated = addonDAO.hasDuplicate(leaf.getName());
				if (duplicated) {
					List<String> duplicateKeys = addonDAO.getDuplicates(leaf
							.getName());
					for (String key : duplicateKeys) {
						Addon addon = addonDAO.getMap().get(key);
						if (addon.getPath().contains(repositoryPath)) {
							leaf.setName(key);
						}
					}
				}
			} else {
				TreeDirectoryDTO d = (TreeDirectoryDTO) node;
				resolveDuplicates(d, repositoryPath);
			}
		}
	}

	public String getACRE2installationFolder() {

		Addon addon = addonDAO.getMap().get("@acre2");
		if (addon == null) {
			return null;
		} else {
			return addon.getPath();
		}
	}

	public String getTFARinstallationFolder() {

		Addon addon = addonDAO.getMap().get("@task_force_radio");
		if (addon == null) {
			return null;
		} else {
			return addon.getPath();
		}
	}
}
