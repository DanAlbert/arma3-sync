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
import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.TreeLeafDTO;
import fr.soe.a3s.dto.TreeNodeDTO;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.ProfileException;
import fr.soe.a3s.exception.WritingException;

public class ProfileService {

	private static final ConfigurationDAO configurationDAO = new ConfigurationDAO();
	private static final ProfileDAO profileDAO = new ProfileDAO();

	public void readAll() throws LoadingException {
		profileDAO.readProfiles();
	}

	public void writeAll() throws WritingException {
		profileDAO.writeProfiles();
	}

	public void setAdditionalParameters(String additionalParameters)
			throws ProfileException {
		String profileName = configurationDAO.getConfiguration()
				.getProfileName();
		Profile profile = (Profile) profileDAO.getMap().get(profileName);
		if (profile != null) {
			profile.setAdditionalParameters(additionalParameters);
		} else {
			throw new ProfileException("Profile " + profileName + " not found!");
		}
	}

	public String getAdditionalParameters() throws ProfileException {
		String profileName = configurationDAO.getConfiguration()
				.getProfileName();
		Profile profile = (Profile) profileDAO.getMap().get(profileName);
		if (profile != null) {
			return profile.getAdditionalParameters();
		} else {
			throw new ProfileException("Profile " + profileName + " not found!");
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
		Profile profile = (Profile) profileDAO.getMap().get(profileName);
		Profile duplicateProfile = new Profile(duplicateProfileName);
		TreeDirectory treeDirectory = profile.getTree();
		TreeDirectory duplicateTreeDirectory = duplicateProfile.getTree();
		duplicateTree(treeDirectory, duplicateTreeDirectory);
		profileDAO.getMap().put(duplicateProfile.getName(), duplicateProfile);
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

	public void renameProfile(String initProfileName, String newProfileName)
			throws ProfileException {
		Profile profile = (Profile) profileDAO.getMap().get(initProfileName);
		if (profile == null) {
			throw new ProfileException("Profile with name " + initProfileName
					+ " does not exists.");
		}
		profile.setName(newProfileName);
		profileDAO.getMap().remove(initProfileName);
		profileDAO.getMap().put(profile.getName(), profile);
	}

	public void removeProfile(String profileName) throws ProfileException {
		Profile profile = (Profile) profileDAO.getMap().get(profileName);
		if (profile == null) {
			throw new ProfileException("Profile with name " + profileName
					+ " does not exists.");
		}
		profileDAO.getMap().remove(profileName);
	}

	public TreeDirectoryDTO getAddonGroupsTree() {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();
		if (profileName == null) {
			configurationDAO.getConfiguration().setProfileName(
					DefaultProfileName.DEFAULT.getDescription());
		}
		Profile profile = (Profile) profileDAO.getMap().get(profileName);
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
		Profile profile = (Profile) profileDAO.getMap().get(profileName);
		if (profile != null) {
			TreeDirectory treeDirectory = profile.getTree();
			treeDirectory.getList().clear();
			transform2TreeDirectory(treeDirectoryDTO, treeDirectory);
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

		Profile profile = (Profile) profileDAO.getMap().get(profileName);
		if (profile != null) {
			List<String> list = profile.getAddonNamesByPriority();
			TreeDirectory treeDirectory = profile.getTree();
			Set<String> extractedAddonNames = new TreeSet<String>();
			getAddonsByName(treeDirectory, extractedAddonNames);
			if (list.isEmpty()){
				list.addAll(extractedAddonNames);
				return list;
			}else {
				Iterator iter = extractedAddonNames.iterator();
				while (iter.hasNext()){
					String name = (String) iter.next();
					if (!list.contains(name)){
						list.add(name);
					}
				}
				List<String> addonNamesToRemove = new ArrayList<String>();
				for (String stg:list){
					if (!extractedAddonNames.contains(stg)){
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

	public void upPriority(int index) {

		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = (Profile) profileDAO.getMap().get(profileName);
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
	
	public void downPriority(int index){
		
		String profileName = configurationDAO.getConfiguration()
				.getProfileName();

		Profile profile = (Profile) profileDAO.getMap().get(profileName);
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
		treeLeafDTO.setOptional(treeLeaf.isOptional());
		return treeLeafDTO;
	}

	private void transform2TreeDirectory(TreeDirectoryDTO treeDirectoryDTO,
			TreeDirectory treeDirectory) {

		List<TreeNodeDTO> list = treeDirectoryDTO.getList();

		for (TreeNodeDTO treeNodeDTO : list) {
			if (treeNodeDTO.isLeaf()) {
				TreeLeafDTO treeLeafDTO = (TreeLeafDTO) treeNodeDTO;
				TreeLeaf treeLeaf = transform2TreeLeaf(treeLeafDTO);
				treeLeaf.setParent(treeDirectory);
				treeDirectory.addTreeNode(treeLeaf);
			} else {
				TreeDirectoryDTO treeDirectoryDTO2 = (TreeDirectoryDTO) treeNodeDTO;
				TreeDirectory treedDirectory2 = new TreeDirectory(
						treeDirectoryDTO2.getName(), treeDirectory);
				treedDirectory2.setSelected(treeDirectoryDTO2.isSelected());
				treeDirectory.addTreeNode(treedDirectory2);
				transform2TreeDirectory(treeDirectoryDTO2, treedDirectory2);
			}
		}
	}

	private TreeLeaf transform2TreeLeaf(TreeLeafDTO treeLeafDTO) {

		TreeLeaf treeLeaf = new TreeLeaf();
		treeLeaf.setName(treeLeafDTO.getName());
		treeLeaf.setSelected(treeLeafDTO.isSelected());
		treeLeaf.setOptional(treeLeafDTO.isOptional());
		return treeLeaf;
	}

}
