package fr.soe.a3s.domain.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Event  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7456226002765813117L;
	private String name;
	private String description;
	private List<String> addonNames = new ArrayList<String>();

	public Event(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getAddonNames() {
		return addonNames;
	}

}
