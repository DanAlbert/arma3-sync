package fr.soe.a3s.domain.repository;

import java.util.ArrayList;
import java.util.List;

public abstract class SyncTreeNodeMethods {

	private final List<SyncTreeNode> nodesList = new ArrayList<SyncTreeNode>();
	private final List<SyncTreeLeaf> leafsList = new ArrayList<SyncTreeLeaf>();

	protected String determinePath(SyncTreeNode syncTreeNode) {

		String path = syncTreeNode.getName();
		SyncTreeDirectory p = syncTreeNode.getParent();
		if (p == null) {
			return "";
		} else {
			while (p != null && !SyncTreeNode.RACINE.equals(p.getName())) {
				path = p.getName() + "/" + path;
				p = p.getParent();
			}
		}
		return path;
	}

	protected List<SyncTreeNode> determineDeepSearchNodesList(SyncTreeNode node) {
		this.nodesList.clear();
		performDeepSearchNodesList(node);
		return this.nodesList;
	}

	private void performDeepSearchNodesList(SyncTreeNode node) {

		if (!node.isLeaf()) {
			SyncTreeDirectory directory = (SyncTreeDirectory) node;
			if (!directory.getName().equals(SyncTreeNode.RACINE)) {
				nodesList.add(directory);
			}
			for (SyncTreeNode n : directory.getList()) {
				performDeepSearchNodesList(n);
			}
		} else {
			SyncTreeLeaf leaf = (SyncTreeLeaf) node;
			nodesList.add(leaf);
		}
	}

	protected List<SyncTreeLeaf> determineDeepSearchLeafsList(SyncTreeNode node) {
		this.leafsList.clear();
		performDeepSearchLeafsList(node);
		return this.leafsList;
	}

	private void performDeepSearchLeafsList(SyncTreeNode node) {

		if (!node.isLeaf()) {
			SyncTreeDirectory directory = (SyncTreeDirectory) node;
			for (SyncTreeNode n : directory.getList()) {
				performDeepSearchLeafsList(n);
			}
		} else {
			SyncTreeLeaf leaf = (SyncTreeLeaf) node;
			leafsList.add(leaf);
		}
	}
}
