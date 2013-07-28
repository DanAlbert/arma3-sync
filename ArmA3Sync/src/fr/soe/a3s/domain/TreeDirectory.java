package fr.soe.a3s.domain;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TreeDirectory implements TreeNode,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3917102856123611985L;
	private String name;
	private boolean selected = false;
	private TreeDirectory parent;
	private List<TreeNode> list = new ArrayList<TreeNode>();
	private boolean marked = false;
	private boolean optional = false;
	
	public TreeDirectory(String name,TreeDirectory parent){
		this.name = name;
		this.parent = parent;
	}
	
	public void addTreeNode(TreeNode treeNode){
		
		list.add(treeNode);
		
		List<TreeDirectory> directories = new ArrayList<TreeDirectory>();
		List<TreeLeaf> leafs = new ArrayList<TreeLeaf>();
		for (TreeNode t:list){
			if (t instanceof TreeDirectory){
				directories.add((TreeDirectory)t);
			}else {
				leafs.add((TreeLeaf)t);
			}
		}
		Collections.sort(directories);
		Collections.sort(leafs);
		list.clear();
		list.addAll(directories);
		list.addAll(leafs);
	}
	
	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean value) {
		this.selected = value;
	}
	
	public List<TreeNode> getList() {
		return list;
	}
	
	public String toString(){
		return name;
	}

	@Override
	public int compareTo(Object other) {
		String name =  ((TreeDirectory) other).getName();
		int result = 1;
		if (name.compareToIgnoreCase(getName()) > 0)
			result = -1;
		else if (name.compareToIgnoreCase(getName()) == 0)
			result = 0;
		return result;
	}
	
	@Override
	public void setParent(TreeDirectory parent) {
		this.parent = parent;
	}

	@Override
	public TreeDirectory getParent() {
		return this.parent;
	}
	
	public void setMarked(boolean value){
		this.marked=value;
	}

	public boolean isMarked() {
		return marked;
	}

	public void removeTreeNode(TreeNode treeNode) {
		list.remove(treeNode);
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
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
