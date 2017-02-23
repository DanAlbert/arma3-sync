package fr.soe.a3s.dto;

public class TreeLeafDTO implements TreeNodeDTO {

	private String name;
	private boolean selected = false;
	private boolean missing = false;
	private boolean optional = false;
	private boolean duplicate = false;
	private boolean duplicatedSelection = false;
	private String sourceRelativePath;
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
		if (duplicate) {
			if (sourceRelativePath != null) {
				stg = stg + " " + "(from /" + sourceRelativePath + ")";
			} else {
				stg = stg + " " + "(from /" + ")";
			}
		}
		if (missing) {
			stg = stg + " " + "(missing)";
		}
		if (duplicatedSelection) {
			stg = stg + " " + "(duplicate selection)";
		}
		return stg;
	}

	public boolean isMissing() {
		return missing;
	}

	public void setMissing(boolean missing) {
		this.missing = missing;
	}

	public void setOptional(boolean value) {
		this.optional = value;
	}

	public boolean isOptional() {
		return optional;
	}

	public boolean isDuplicate() {
		return this.duplicate;
	}

	public void setDuplicate(boolean value) {
		this.duplicate = value;
	}

	public String getSourceRelativePath() {
		return sourceRelativePath;
	}

	public void setSourceRelativePath(String sourceRelativePath) {
		this.sourceRelativePath = sourceRelativePath;
	}

	public boolean isDuplicatedSelection() {
		return duplicatedSelection;
	}

	public void setDuplicatedSelection(boolean value) {
		this.duplicatedSelection = value;
	}
}
