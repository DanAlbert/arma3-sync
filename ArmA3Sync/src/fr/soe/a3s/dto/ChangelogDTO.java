package fr.soe.a3s.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChangelogDTO {

	private int revision;
	private Date buildDate;
	private List<String> newAddons = new ArrayList<String>();
	private List<String> updatedAddons = new ArrayList<String>();
	private List<String> deletedAddons = new ArrayList<String>();

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

}
