package fr.soe.a3s.domain.repository;

import java.io.Serializable;

public class FileAttributes implements Serializable{

	private String sha1;

	private long lastModified;

	public FileAttributes(String sha1, long lastModified) {
		this.sha1 = sha1;
		this.lastModified = lastModified;
	}

	public String getSha1() {
		return sha1;
	}

	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
}
