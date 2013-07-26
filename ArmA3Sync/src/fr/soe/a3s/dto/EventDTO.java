package fr.soe.a3s.dto;

import java.util.ArrayList;
import java.util.List;

public class EventDTO {

	private String name;
	private String description;
	private List<String> addonNames = new ArrayList<String>();

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
