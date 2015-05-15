package fr.soe.a3s.domain.repository;

import java.io.Serializable;

public class SyncTreeLeaf implements SyncTreeNode, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8849248143660225239L;
	private String name;
	private SyncTreeDirectory parent;
	private String sha1;
	private long size;
	private transient long complete;// file completion in %
	private transient String destinationPath;
	private transient String localSHA1;
	private boolean updated = false;
	private boolean deleted = false;

	public SyncTreeLeaf(String name, SyncTreeDirectory parent) {
		super();
		this.name = name;
		this.parent = parent;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public SyncTreeDirectory getParent() {
		return this.parent;
	}

	@Override
	public void setParent(SyncTreeDirectory parent) {
		this.parent = parent;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	public String getSha1() {
		return sha1;
	}

	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

	public void setLocalSHA1(String sha1) {
		this.localSHA1 = sha1;
	}

	public String getLocalSHA1() {
		return localSHA1;
	}

	public long getComplete() {
		return complete;
	}

	public void setComplete(long complete) {
		this.complete = complete;
	}

	@Override
	public int compareTo(Object other) {
		String name = ((SyncTreeNode) other).getName();
		int result = 1;
		if (name.compareToIgnoreCase(getName()) > 0)
			result = -1;
		else if (name.compareToIgnoreCase(getName()) == 0)
			result = 0;
		return result;
	}

	@Override
	public void setDeleted(boolean value) {
		this.deleted = value;
	}

	@Override
	public boolean isDeleted() {
		return this.deleted;
	}

	@Override
	public void setUpdated(boolean value) {
		this.updated = value;
	}

	@Override
	public boolean isUpdated() {
		return this.updated;
	}
}
