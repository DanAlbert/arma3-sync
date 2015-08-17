package fr.soe.a3s.dto.sync;

import fr.soe.a3s.constant.DownloadStatus;

public class SyncTreeLeafDTO implements SyncTreeNodeDTO {

	private String name;
	private SyncTreeDirectoryDTO parent;
	private boolean updated;
	private boolean selected;
	private boolean compressed;
	private boolean deflated;
	private long size;
	private long compressedSize;
	private double complete;
	private String localSHA1;
	private String destinationPath;
	private String remotePath;
	private boolean deleted;
	private boolean optional;
	private String sha1;
	private DownloadStatus downloadStatus = DownloadStatus.PENDING;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public SyncTreeDirectoryDTO getParent() {
		return this.parent;
	}

	@Override
	public void setParent(SyncTreeDirectoryDTO parent) {
		this.parent = parent;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public boolean isUpdated() {
		return updated;
	}

	@Override
	public void setUpdated(boolean value) {
		this.updated = value;

	}

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean value) {
		this.selected = value;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public int compareTo(Object other) {
		String name = ((SyncTreeLeafDTO) other).getName();
		int result = 1;
		if (name.compareToIgnoreCase(getName()) > 0)
			result = -1;
		else if (name.compareToIgnoreCase(getName()) == 0)
			result = 0;
		return result;
	}

	@Override
	public String getDestinationPath() {
		return destinationPath;
	}

	@Override
	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	@Override
	public void setDeleted(boolean value) {
		this.deleted = value;
	}

	@Override
	public boolean isDeleted() {
		return deleted;
	}

	@Override
	public void setOptional(boolean value) {
		this.optional = value;
	}

	@Override
	public boolean isOptional() {
		return this.optional;
	}

	public String getLocalSHA1() {
		return localSHA1;
	}

	public void setLocalSHA1(String localSHA1) {
		this.localSHA1 = localSHA1;
	}

	public double getComplete() {
		return complete;
	}

	public void setComplete(double complete) {
		this.complete = complete;
	}

	public String getSha1() {
		return sha1;
	}

	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}

	@Override
	public String getRelativePath() {
		String path = this.name;
		SyncTreeDirectoryDTO p = this.parent;
		if (p == null) {
			return "";
		} else {
			while (p != null && !RACINE.equals(p.getName())) {
				path = p.getName() + "/" + path;
				p = p.getParent();
			}
		}
		return path;
	}

	@Override
	public DownloadStatus getDownloadStatus() {
		return this.downloadStatus;
	}

	@Override
	public void setDownloadStatus(DownloadStatus downloadStatus) {
		this.downloadStatus = downloadStatus;
	}

	public boolean isCompressed() {
		return compressed;
	}

	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}

	public long getCompressedSize() {
		return compressedSize;
	}

	public void setCompressedSize(long compressedSize) {
		this.compressedSize = compressedSize;
	}

	public boolean isDeflated() {
		return deflated;
	}

	public void setDeflated(boolean deflated) {
		this.deflated = deflated;
	}
}
