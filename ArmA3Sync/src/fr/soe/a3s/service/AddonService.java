package fr.soe.a3s.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fr.soe.a3s.constant.GameSystemFolders;
import fr.soe.a3s.dao.AddonDAO;
import fr.soe.a3s.dao.ConfigurationDAO;
import fr.soe.a3s.domain.Addon;
import fr.soe.a3s.domain.TreeDirectory;
import fr.soe.a3s.domain.TreeLeaf;
import fr.soe.a3s.domain.TreeNode;
import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.TreeLeafDTO;
import fr.soe.a3s.dto.TreeNodeDTO;

public class AddonService {

	private static final ConfigurationDAO configurationDAO = new ConfigurationDAO();
	private static final AddonDAO addonDAO = new AddonDAO();
	private static List<String> excludedFolderList = new ArrayList<String>();
	static {
		GameSystemFolders[] tab1 = GameSystemFolders.values();
		for (int i = 0; i < tab1.length; i++) {
			excludedFolderList.add(tab1[i].toString().toLowerCase());
		}
	}
	private static TreeDirectory availableAddonsTreeInstance = null;

	private TreeDirectory getAvailableAddonsTreeInstance() {

		if (availableAddonsTreeInstance == null) {

			Set<String> addonSearchDirectoryPaths = configurationDAO
					.getConfiguration().getAddonSearchDirectoryPaths();
			List<String> list = new ArrayList<String>();
			Iterator iter = addonSearchDirectoryPaths.iterator();
			while (iter.hasNext()) {
				list.add((String) iter.next());
			}

			addonDAO.getMap().clear();
			TreeDirectory racine = new TreeDirectory("racine1", null);

			// get addons inside all locations
			for (String path : list) {
				File file = new File(path);
				if (file.exists() && file.isDirectory()) {
					File[] subfiles = file.listFiles();
					for (File f : subfiles) {
						if (f.isDirectory()) {
							if (!excludedFolderList.contains(f.getName()
									.toLowerCase())) {
								generateTree(f, racine);
							}
						}
					}
				}
			}

			// keep marked directory, change terminal directory to leaf
			TreeDirectory racineCleaned = new TreeDirectory("racine1", null);

			for (TreeNode directory : racine.getList()) {
				TreeDirectory d = (TreeDirectory) directory;
				cleanTree(d, racineCleaned);
			}
			availableAddonsTreeInstance = racineCleaned;
		}
		return availableAddonsTreeInstance;
	}

	public TreeDirectoryDTO getAvailableAddonsTree() {

		TreeDirectory treeDirectory = getAvailableAddonsTreeInstance();
		TreeDirectoryDTO treeDirectoryDTO = new TreeDirectoryDTO();
		treeDirectoryDTO.setName("racine1");
		treeDirectoryDTO.setParent(null);
		transformTreeDirectory2DTO(treeDirectory, treeDirectoryDTO);

		boolean isTreeMode = configurationDAO.getConfiguration()
				.isViewModeTree();

		if (!isTreeMode) {
			TreeDirectoryDTO newTreeDirectoryDTO = new TreeDirectoryDTO();
			newTreeDirectoryDTO.setName("racine1");
			newTreeDirectoryDTO.setParent(null);
			generateTreeList(treeDirectoryDTO, newTreeDirectoryDTO);
			return newTreeDirectoryDTO;
		} else {
			return treeDirectoryDTO;
		}
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
					if (!leafNames.contains(treeNodeDTO.getName())){
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

		if (file.isDirectory()) {

			/* Check if directory already exists in the Tree */
			TreeDirectory treeDirectory = null;
			for (TreeNode n : node.getList()) {
				if (n.getName().equals(file.getName()) && !n.isLeaf()) {
					treeDirectory = (TreeDirectory) n;
				}
			}

			if (treeDirectory == null) {
				treeDirectory = new TreeDirectory(file.getName(), node);
				node.addTreeNode(treeDirectory);
			}

			File[] subfiles = file.listFiles();
			boolean contains = false;
			if (subfiles == null) {
				return;
			}
			for (File f : subfiles) {
				if (f.getName().toLowerCase().contains("addons")) {
					contains = true;
					break;
				}
			}
			if (contains) {
				String addonName = treeDirectory.getName();
				Addon addon = new Addon(addonName, file.getParentFile()
						.getAbsolutePath());
				addonDAO.getMap().put(addonName.toLowerCase(), addon);
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

	public void resetAvailableAddonTree() {
		availableAddonsTreeInstance = null;
	}

	public String getACREinstallationFolder() {

		Addon addon = addonDAO.getMap().get("@acre");
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

	/* Business methods */

	public void transformTreeDirectory2DTO(TreeDirectory treeDirectory,
			TreeDirectoryDTO treeDirectoryDTO) {

		List<TreeNode> list = treeDirectory.getList();

		for (TreeNode treeNode : list) {
			if (treeNode.isLeaf()) {
				TreeLeaf treeLeaf = (TreeLeaf) treeNode;
				TreeLeafDTO treeLeafDTO = transformTreeLeaf2DTO(treeLeaf);
				treeLeafDTO.setParent(treeDirectoryDTO);
				treeDirectoryDTO.addTreeNode(treeLeafDTO);
			} else {
				TreeDirectory treeDirectory2 = (TreeDirectory) treeNode;
				TreeDirectoryDTO treedDirectoryDTO2 = new TreeDirectoryDTO();
				treedDirectoryDTO2.setName(treeDirectory2.getName());
				treedDirectoryDTO2.setSelected(treeDirectory2.isSelected());
				treedDirectoryDTO2.setParent(treeDirectoryDTO);
				treeDirectoryDTO.addTreeNode(treedDirectoryDTO2);
				transformTreeDirectory2DTO(treeDirectory2, treedDirectoryDTO2);
			}
		}
	}

	private TreeLeafDTO transformTreeLeaf2DTO(TreeLeaf treeLeaf) {

		TreeLeafDTO treeLeafDTO = new TreeLeafDTO();
		treeLeafDTO.setName(treeLeaf.getName());
		treeLeafDTO.setSelected(treeLeaf.isSelected());
		return treeLeafDTO;
	}


}
