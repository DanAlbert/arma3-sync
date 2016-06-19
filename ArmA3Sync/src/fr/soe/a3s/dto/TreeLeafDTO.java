package fr.soe.a3s.dto;

public class TreeLeafDTO implements TreeNodeDTO {

	private String name;
	private boolean selected = false;
	private boolean missing = false;
	private boolean optional = false;
	private boolean duplicate = false;
	private TreeDirectoryDTO parent;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public TreeDirectoryDTO getParent() {
		return parent;
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
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setParent(TreeDirectoryDTO parent) {
		this.parent = parent;
	}

	@Override
	public void setSelected(boolean value) {
		this.selected = value;
	}

	@Override
	public int compareTo(Object other) {
		String name = ((TreeLeafDTO) other).getName();
		int result = 1;
		if (name.compareToIgnoreCase(getName()) > 0)
			result = -1;
		else if (name.compareToIgnoreCase(getName()) == 0)
			result = 0;
		return result;
	}

	@Override
	public String toString() {
		String stg = name;
		if (optional) {
			stg = stg + " " + "(optional)";
		}
		if (duplicate){
			stg = stg + " " + "(duplicate available)";
		}
		return stg;
	}

	@Override
	public boolean isMissing() {
		return missing;
	}

	@Override
	public void setMissing(boolean missing) {
		this.missing = missing;
	}

	@Override
	public void setOptional(boolean value) {
		this.optional = value;
	}

	@Override
	public boolean isOptional() {
		return optional;
	}

	public boolean isDuplicate() {
		return this.duplicate;
	}

	public void setDuplicate(boolean value) {
		this.duplicate = value;
	}
}
