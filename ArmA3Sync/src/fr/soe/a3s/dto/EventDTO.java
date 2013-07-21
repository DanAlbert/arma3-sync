package fr.soe.a3s.dto;

import java.util.ArrayList;
import java.util.List;

public class EventDTO {

	private String name;
	private List<String> addonNames = new ArrayList<String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getAddonNames() {
		return addonNames;
	}

}
