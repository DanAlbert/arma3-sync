package fr.soe.a3s.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Profile implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4395099790449721021L;
	private String name;
	private TreeDirectory tree = new TreeDirectory("racine", null);
	private String additionalParameters;
	private List<String> addonNamesByPriority = new ArrayList<String>();
	
	public Profile(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public TreeDirectory getTree() {
		return tree;
	}

	public String getAdditionalParameters() {
		return additionalParameters;
	}

	public void setAdditionalParameters(String additionalParameters) {
		this.additionalParameters = additionalParameters;
	}

	public List<String> getAddonNamesByPriority() {
		if (addonNamesByPriority==null){
			addonNamesByPriority = new ArrayList<String>();
		}
		return addonNamesByPriority;
	}

}
