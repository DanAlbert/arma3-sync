package fr.soe.a3s.dto.sync;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SyncTreeDirectoryDTO implements SyncTreeNodeDTO{

	private String name;
	private SyncTreeDirectoryDTO parent;
	private List<SyncTreeNodeDTO> list = new ArrayList<SyncTreeNodeDTO>();
	private boolean updated;
	private boolean selected;
	private String destinationPath;
	private boolean markAsAddon;
	private boolean deleted;
	private boolean optional;
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name=name;
	}

	@Override
	public SyncTreeDirectoryDTO getParent() {
		return this.parent;
	}

	@Override
	public void setParent(SyncTreeDirectoryDTO parent) {
		this.parent = parent;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public boolean isUpdated() {
		return this.updated;
	}

	@Override
	public void setUpdated(boolean value) {
		this.updated = value;
	}

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean value) {
		this.selected = value;
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}
	
	public String toString(){
		if (optional){
			return name + " (optional)";
		}else {
			return name;
		}
	}
	
	public boolean isMarkAsAddon() {
		return markAsAddon;
	}

	public void setMarkAsAddon(boolean markAsAddon) {
		this.markAsAddon = markAsAddon;
	}

	public void addTreeNode(SyncTreeNodeDTO syncTreeNodeDTO) {
		
		list.add(syncTreeNodeDTO);
		
		List<SyncTreeDirectoryDTO> directories = new ArrayList<SyncTreeDirectoryDTO>();
		List<SyncTreeLeafDTO> leafs = new ArrayList<SyncTreeLeafDTO>();
		for (SyncTreeNodeDTO t:list){
			if (t instanceof SyncTreeDirectoryDTO){
				directories.add((SyncTreeDirectoryDTO)t);
			}else {
				leafs.add((SyncTreeLeafDTO)t);
			}
		}
		Collections.sort(directories);
		Collections.sort(leafs);
		list.clear();
		list.addAll(directories);
		list.addAll(leafs);
	}

	@Override
	public int compareTo(Object other) {
		String name = ((SyncTreeDirectoryDTO) other).getName();
		int result = 1;
		if (name.compareToIgnoreCase(getName()) > 0)
			result = -1;
		else if (name.compareToIgnoreCase(getName()) == 0)
			result = 0;
		return result;
	}

	public List<SyncTreeNodeDTO> getList() {
		return this.list;
	}

	@Override
	public void setDeleted(boolean value) {
		this.deleted = value;
		
	}

	@Override
	public boolean isDeleted() {
		return this.deleted;
	}

	@Override
	public void setOptional(boolean value) {
		this.optional = value;
	}

	@Override
	public boolean isOptional() {
		return this.optional;
	}
	
}
