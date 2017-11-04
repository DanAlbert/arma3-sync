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

	public boolean hasDuplicate(String addonKey) {

		List<String> list = getDuplicates(addonKey);
		if (list.size() > 1) {
			return true;
		} else {
			return false;
		}
	}

	public List<String> getDuplicates(String addonKey) {

		List<String> list = new ArrayList<String>();
		Addon addon = mapAddons.get(addonKey.toLowerCase());
		if (addon != null) {
			for (Iterator<String> iter = mapAddons.keySet().iterator(); iter.hasNext();) {
				String key = iter.next();
				if (mapAddons.get(key).getName().equals(addon.getName())) {
					list.add(key);
				}
			}
		}
		return list;
	}
}
