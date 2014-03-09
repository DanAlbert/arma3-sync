package fr.soe.a3s.domain.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SyncTreeDirectory implements SyncTreeNode,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2855304993780573704L;
	private String name;
	private SyncTreeDirectory parent;
	private List<SyncTreeNode> list = new ArrayList<SyncTreeNode>();
	private boolean markAsAddon = false;
	private transient String destinationPath;
	private boolean deleted = false;
	private boolean hidden = false;

	public SyncTreeDirectory(String name, SyncTreeDirectory parent) {
		super();
		this.name = name;
		this.parent = parent;
	}

	public void addTreeNode(SyncTreeNode syncTreeNode) {

		list.add(syncTreeNode);

		List<SyncTreeDirectory> directories = new ArrayList<SyncTreeDirectory>();
		List<SyncTreeLeaf> leafs = new ArrayList<SyncTreeLeaf>();
		for (SyncTreeNode t : list) {
			if (t instanceof SyncTreeDirectory) {
				directories.add((SyncTreeDirectory) t);
			} else {
				leafs.add((SyncTreeLeaf) t);
			}
		}
		Collections.sort(directories);
		Collections.sort(leafs);
		list.clear();
		list.addAll(directories);
		list.addAll(leafs);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public SyncTreeDirectory getParent() {
		return this.parent;
	}

	@Override
	public void setParent(SyncTreeDirectory parent) {
		this.parent = parent;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}
	
	public List<SyncTreeNode> getList() {
		return list;
	}

	public boolean isMarkAsAddon() {
		return markAsAddon;
	}

	public void setMarkAsAddon(boolean markAsAddon) {
		this.markAsAddon = markAsAddon;
	}
	
	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

	@Override
	public int compareTo(Object other) {
		String name = ((SyncTreeDirectory) other).getName();
		int result = 1;
		if (name.compareToIgnoreCase(getName()) > 0)
			result = -1;
		else if (name.compareToIgnoreCase(getName()) == 0)
			result = 0;
		return result;
	}

	@Override
	public void setDeleted(boolean value) {
		this.deleted = value;
	}

	@Override
	public boolean isDeleted() {
		return this.deleted;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
}
