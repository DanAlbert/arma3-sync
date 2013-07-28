package fr.soe.a3s.domain.repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7456226002765813117L;
	private String name;
	private String description;
	//private List<String> addonNames = new ArrayList<String>();//All addons
	private Map<String,Boolean> addonNames = new HashMap<String,Boolean>();
				
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

	public Map<String, Boolean> getAddonNames() {
		return addonNames;
	}

//	public List<String> getAddonNames() {
//		return addonNames;
//	}
	
	

}
