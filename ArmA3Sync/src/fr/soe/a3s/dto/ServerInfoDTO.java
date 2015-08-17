package fr.soe.a3s.dto;

import java.util.Date;

public class ServerInfoDTO {

	private int revision;
	private Date buildDate;
	private long numberOfFiles;
	private long totalFilesSize;
	

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

	public long getNumberOfFiles() {
		return numberOfFiles;
	}

	public void setNumberOfFiles(long numberOfFiles) {
		this.numberOfFiles = numberOfFiles;
	}

	public long getTotalFilesSize() {
		return totalFilesSize;
	}

	public void setTotalFilesSize(long totalFilesSize) {
		this.totalFilesSize = totalFilesSize;
	}
}
