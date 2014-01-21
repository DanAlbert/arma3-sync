package fr.soe.a3s.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDTO {

	private String name;
	private String description;
	private Map<String, Boolean> addonNames = new HashMap<String, Boolean>();
	private Map<String, Boolean> userconfigFolderNames = new HashMap<String, Boolean>();

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

	public Map<String, Boolean> getAddonNames() {
		return addonNames;
	}

	public Map<String, Boolean> getUserconfigFolderNames() {
		return userconfigFolderNames;
	}
}
