package fr.soe.a3s.dao;

import java.util.HashMap;
import java.util.Map;

import fr.soe.a3s.domain.Addon;

public class AddonDAO {

	/* Key = addon name to lower case */
	private static final Map<String, Addon> mapAddons = new HashMap<String, Addon>();

	public Map<String, Addon> getMap() {
		return mapAddons;
	}
}
