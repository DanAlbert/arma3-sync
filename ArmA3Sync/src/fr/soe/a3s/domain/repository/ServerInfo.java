package fr.soe.a3s.domain.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ServerInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7697232677958952953L;
	private int revision;
	private Date buildDate;
	private long numberOfFiles;
	private long totalFilesSize;
	/** Server data: Hide extra local folder content */
	private Set<String> hiddenFolderPaths = new HashSet<String>();
	/** Number of client connections */
	private int numberOfConnections = 1;
	/** Perform partial file transfer */
	private boolean noPartialFileTransfer = false;
	/** Repository content have changed since last Build */
	public boolean repositoryContentUpdated = false;
	/** Repository contents only compressed pbo files */
	public boolean compressedPboFilesOnly = false;

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

	public Set<String> getHiddenFolderPaths() {
		if (this.hiddenFolderPaths == null) {
			this.hiddenFolderPaths = new HashSet<String>();
		}
		return hiddenFolderPaths;
	}

	public int getNumberOfConnections() {
		return numberOfConnections;
	}

	public void setNumberOfConnections(int numberOfConnections) {
		this.numberOfConnections = numberOfConnections;
	}

	public boolean isRepositoryContentUpdated() {
		return repositoryContentUpdated;
	}

	public void setRepositoryContentUpdated(boolean repositoryContentUpdated) {
		this.repositoryContentUpdated = repositoryContentUpdated;
	}

	public boolean isNoPartialFileTransfer() {
		return noPartialFileTransfer;
	}

	public void setNoPartialFileTransfer(boolean performPartialFileTransfer) {
		this.noPartialFileTransfer = performPartialFileTransfer;
	}

	public boolean isCompressedPboFilesOnly() {
		return compressedPboFilesOnly;
	}

	public void setCompressedPboFilesOnly(boolean compressedPboFilesOnly) {
		this.compressedPboFilesOnly = compressedPboFilesOnly;
	}
}
