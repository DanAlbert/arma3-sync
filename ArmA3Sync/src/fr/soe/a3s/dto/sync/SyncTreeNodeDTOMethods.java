package fr.soe.a3s.dto.sync;

import java.util.ArrayList;
import java.util.List;

public abstract class SyncTreeNodeDTOMethods {

	private List<SyncTreeNodeDTO> nodesList = new ArrayList<SyncTreeNodeDTO>();
	private List<SyncTreeLeafDTO> leafsList = new ArrayList<SyncTreeLeafDTO>();

	protected String determinePath(SyncTreeNodeDTO syncTreeNodeDTO) {

		String path = syncTreeNodeDTO.getName();
		SyncTreeDirectoryDTO p = syncTreeNodeDTO.getParent();
		if (p == null) {
			return "";
		} else {
			while (p != null && !SyncTreeNodeDTO.RACINE.equals(p.getName())) {
				path = p.getName() + "/" + path;
				p = p.getParent();
			}
		}
		return path;
	}

	protected String determineParentRelativePath(SyncTreeNodeDTO syncTreeNodeDTO) {

		String path = "";
		if (syncTreeNodeDTO.getParent() != null) {
			while (!syncTreeNodeDTO.getParent().getName()
					.equals(SyncTreeNodeDTO.RACINE)) {
				if (path.isEmpty()) {
					path = syncTreeNodeDTO.getParent().getName();
				} else {
					path = syncTreeNodeDTO.getParent().getName() + "/" + path;
				}
				syncTreeNodeDTO = syncTreeNodeDTO.getParent();
			}
		}
		return path;
	}

	protected List<SyncTreeNodeDTO> determineDeepSearchNodesList(
			SyncTreeNodeDTO node) {
		this.nodesList.clear();
		performDeepSearchNodesList(node);
		return this.nodesList;
	}

	private void performDeepSearchNodesList(SyncTreeNodeDTO node) {

		if (!node.isLeaf()) {
			SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) node;
			if (!directory.getName().equals(SyncTreeNodeDTO.RACINE)) {
				nodesList.add(directory);
			}
			for (SyncTreeNodeDTO n : directory.getList()) {
				performDeepSearchNodesList(n);
			}
		} else {
			SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
			nodesList.add(leaf);
		}
	}

	protected List<SyncTreeLeafDTO> determineDeepSearchLeafsList(
			SyncTreeNodeDTO node) {
		this.leafsList.clear();
		performDeepSearchLeafsList(node);
		return this.leafsList;
	}

	private void performDeepSearchLeafsList(SyncTreeNodeDTO node) {

		if (!node.isLeaf()) {
			SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) node;
			for (SyncTreeNodeDTO n : directory.getList()) {
				performDeepSearchLeafsList(n);
			}
		} else {
			SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
			leafsList.add(leaf);
		}
	}
}
