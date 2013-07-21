package fr.soe.a3s.domain.repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class Sync implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6921574658374719514L;
	private Map<String,String> mapSync = new HashMap<String,String>();

	public Map<String, String> getMapSync() {
		return mapSync;
	}
}
