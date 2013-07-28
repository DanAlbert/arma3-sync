package fr.soe.a3s.domain;

import java.io.Serializable;



public class TreeLeaf implements TreeNode,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8681722025933532378L;
	private String name;
	private boolean selected = false;
	private boolean optional = false;
	private TreeDirectory parent;
	
	public TreeLeaf(String name, TreeDirectory parent){
		this.name = name;
		this.parent = parent;
	}
	
	public TreeLeaf() {
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean value) {
		this.selected = value;
	}
	
	@Override
	public String toString(){
		return name;
	}

	@Override
	public int compareTo(Object other) {
		String name =  ((TreeLeaf) other).getName();
		int result = 1;
		if (name.compareToIgnoreCase(getName())> 0)
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
		return parent;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
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
