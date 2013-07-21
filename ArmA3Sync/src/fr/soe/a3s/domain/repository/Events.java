package fr.soe.a3s.domain.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Events implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5141643688299352462L;
	public List<Event> list  = new ArrayList<Event>();

	public List<Event> getList() {
		return list;
	}

}
