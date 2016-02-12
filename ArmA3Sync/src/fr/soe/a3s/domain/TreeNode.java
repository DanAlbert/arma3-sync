package fr.soe.a3s.domain;

public interface TreeNode extends java.lang.Comparable {

	public String getName();

	public void setName(String name);

	@Override
	public String toString();

	public boolean isLeaf();

	public boolean isSelected();

	public void setSelected(boolean value);

	public void setOptional(boolean value);

	public boolean isOptional();

	public TreeDirectory getParent();

	public void setParent(TreeDirectory treeDirectory);
}
