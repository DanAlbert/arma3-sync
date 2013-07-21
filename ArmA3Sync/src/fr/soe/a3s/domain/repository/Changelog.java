package fr.soe.a3s.domain.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Changelog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 749128678979864247L;
	private int revision;
	private Date buildDate;
	private List<String> newAddons = new ArrayList<String>();
	private List<String> updatedAddons = new ArrayList<String>();
	private List<String> deletedAddons = new ArrayList<String>();
	private List<String> addons = new ArrayList<String>();

	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	public Date getBuildDate() {
		return buildDate;
	}

	public void setBuildDate(Date buildDate) {
		this.buildDate = buildDate;
	}

	public List<String> getNewAddons() {
		return newAddons;
	}

	public List<String> getUpdatedAddons() {
		return updatedAddons;
	}

	public List<String> getDeletedAddons() {
		return deletedAddons;
	}

	public List<String> getAddons() {
		return addons;
	}

}
