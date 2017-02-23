package fr.soe.a3s.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

		String key = name.toLowerCase();
		if (!mapAddons.containsKey(key)) {
			return false;
		} else {
			if (name.contains("*")) {
				return true;
			} else {
				key = name.toLowerCase() + "*";
				if (mapAddons.containsKey(key)) {
					return true;
				}
			}
		}
		return false;
	}

	public List<String> getDuplicates(String name) {

		List<String> list = new ArrayList<String>();
		for (Iterator<String> iter = mapAddons.keySet().iterator(); iter
				.hasNext();) {
			String key = iter.next();
			if (key.equals(name.toLowerCase())) {
				list.add(key);
			} else if (key.contains(name.toLowerCase() + "*")) {
				list.add(key);
			}
		}
		return list;
	}
}
