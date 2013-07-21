package fr.soe.a3s.domain.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Changelogs implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7332983755625818660L;
	List<Changelog> list = new ArrayList<Changelog>();

	public List<Changelog> getList() {
		return list;
	}

}
