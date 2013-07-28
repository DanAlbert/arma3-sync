package fr.soe.a3s.dto.sync;

public interface SyncTreeNodeDTO extends java.lang.Comparable {
	
	public String getName();
	public void setName(String name);
	public String toString();
	public SyncTreeDirectoryDTO  getParent();
	public void setParent( SyncTreeDirectoryDTO parent);
	public boolean isLeaf();
	public boolean isUpdated();
	public void setUpdated(boolean value);
	public boolean isSelected();
	public void setSelected(boolean value);
	public String getDestinationPath();
	public void setDestinationPath(String destinationPath);
	public void setDeleted(boolean value);
	public boolean isDeleted();
	public void setOptional(boolean value);
	public boolean isOptional();

}
