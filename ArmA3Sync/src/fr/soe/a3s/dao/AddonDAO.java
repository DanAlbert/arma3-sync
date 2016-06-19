package fr.soe.a3s.dao;

import java.util.HashMap;
import java.util.Map;

import fr.soe.a3s.domain.Addon;

public class AddonDAO {

	/* Key = Symbolic addon name */
	private static final Map<String, Addon> mapAddons = new HashMap<String, Addon>();

	public Map<String, Addon> getMap() {
		return mapAddons;
	}

	public String determineNewAddonKey(String key) {

		if (mapAddons.containsKey(key.toLowerCase())) {
			String newKey = key + "*";
			return determineNewAddonKey(newKey);
		} else {
			return key;
		}
	}

	public boolean hasDuplicate(String name) {

		if (name.contains("*")) {
			return true;
		} else {
			String key = name.toLowerCase() + "*";
			if (mapAddons.containsKey(key)) {
				return true;
			}
		}
		return false;
	}
}
