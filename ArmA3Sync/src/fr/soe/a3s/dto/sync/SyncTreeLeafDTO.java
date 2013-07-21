package fr.soe.a3s.dto.sync;


public class SyncTreeLeafDTO implements SyncTreeNodeDTO {

	private String name;
	private SyncTreeDirectoryDTO parent;
	private boolean updated;
	private boolean selected;
	private long size;
	private String destinationPath;
	private boolean deleted;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
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
		return true;
	}

	@Override
	public boolean isUpdated() {
		return updated;
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

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String toString() {
		return this.name;
	}

	@Override
	public int compareTo(Object other) {
		String name = ((SyncTreeLeafDTO) other).toString();
		int result = 1;
		if (name.compareToIgnoreCase(toString()) > 0)
			result = -1;
		else if (name.compareToIgnoreCase(toString()) == 0)
			result = 0;
		return result;
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

	@Override
	public void setDeleted(boolean value) {
		this.deleted = value;
	}

	@Override
	public boolean isDeleted() {
		return deleted;
	}

}
