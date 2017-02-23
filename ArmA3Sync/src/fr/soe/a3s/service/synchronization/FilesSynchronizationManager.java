package fr.soe.a3s.service.synchronization;

import java.util.ArrayList;
import java.util.List;

import fr.soe.a3s.constant.DownloadStatus;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class FilesSynchronizationManager {

	/**/
	private long totalDownloadFilesSize;
	private int totalNumberFilesSelected, totalNumberFilesUpdated,
			totalNumberFilesDeleted;
	/**/
	private final List<SyncTreeNodeDTO> listFilesToUpdate = new ArrayList<SyncTreeNodeDTO>();
	private final List<SyncTreeNodeDTO> listFilesToDelete = new ArrayList<SyncTreeNodeDTO>();
	private SyncTreeNodeDTO parent;
	/* Download Report */
	private long totalDiskFilesSize, totalCompressedFilesSize,
			totalUncompressedFilesSize, totalUncompleteExpectedFileSize,
			totalUncompleteDiskFileSize;
	private int totalNumberUnCompleteFiles, totalNumberCompressedFiles;
	private long averageDownloadSpeed;
	private long averageResponseTime;
	private int maxActiveconnections;
	/* TFAR & ACRE2 & Userconfig Update */
	private boolean tfarIsUpdated = false;
	private boolean acre2IsUpdated = false;
	private SyncTreeDirectoryDTO userconfigNode;

	public void update() {

		this.totalDownloadFilesSize = 0;
		this.totalNumberFilesSelected = 0;
		this.totalNumberFilesUpdated = 0;
		this.totalNumberFilesDeleted = 0;
		this.listFilesToUpdate.clear();
		this.listFilesToDelete.clear();
		if (parent != null) {
			compute(parent);
			// determineTFARandACREupdates();
			determineUserconfigUpdates();
		}
	}

	private void compute(SyncTreeNodeDTO node) {

		if (node.isLeaf()) {
			SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
			if (leaf.isUpdated()) {
				totalNumberFilesUpdated++;
			} else if (leaf.isDeleted()) {
				totalNumberFilesDeleted++;
			}
			if (leaf.isSelected()) {
				totalNumberFilesSelected++;
			}
			if (leaf.isSelected() && leaf.isUpdated()) {
				// Determine total download files size
				totalDownloadFilesSize = totalDownloadFilesSize
						+ determineDownloadSize(leaf);
				// Add to files to update list
				listFilesToUpdate.add(leaf);
			} else if (leaf.isSelected() && leaf.isDeleted()) {
				// Add to files to delete list
				listFilesToDelete.add(leaf);
			}
		} else {
			SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) node;
			if (node.isUpdated()) {
				totalNumberFilesUpdated++;
			} else if (directory.isDeleted()) {
				totalNumberFilesDeleted++;
			}
			if (directory.isSelected()) {
				totalNumberFilesSelected++;
			}
			if (directory.isSelected() && directory.isUpdated()) {
				// Add to files to update list
				listFilesToUpdate.add(directory);
			} else if (directory.isSelected() && directory.isDeleted()) {
				// Add to files to delete list
				listFilesToDelete.add(directory);
			}

			for (SyncTreeNodeDTO n : directory.getList()) {
				compute(n);
			}
		}
	}

	private long determineDownloadSize(SyncTreeLeafDTO leaf) {

		if (leaf.getComplete() == 0) {
			if (leaf.isCompressed()) {
				return leaf.getCompressedSize();
			} else {
				return leaf.getSize();
			}
		} else {
			return (long) (leaf.getSize() * (100 - leaf.getComplete()) / 100);
		}
	}

	public void report() {

		totalDiskFilesSize = 0;
		totalCompressedFilesSize = 0;
		totalUncompressedFilesSize = 0;
		totalUncompleteExpectedFileSize = 0;
		totalUncompleteDiskFileSize = 0;
		totalNumberUnCompleteFiles = 0;
		totalNumberCompressedFiles = 0;

		for (SyncTreeNodeDTO node : listFilesToUpdate) {
			if (node.isLeaf()) {
				SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
				totalDiskFilesSize = totalDiskFilesSize + leaf.getSize();
				if (leaf.getComplete() != 0) {
					totalNumberUnCompleteFiles++;
					totalUncompleteExpectedFileSize = totalUncompleteExpectedFileSize
							+ determineDownloadSize(leaf);
					totalUncompleteDiskFileSize = totalUncompleteDiskFileSize
							+ leaf.getSize();
				} else if (leaf.isCompressed()) {
					totalNumberCompressedFiles++;
					totalCompressedFilesSize = totalCompressedFilesSize
							+ leaf.getCompressedSize();
					totalUncompressedFilesSize = totalUncompressedFilesSize
							+ leaf.getSize();
				}
			}
		}
	}

	/* TFAR & ACRE2 Update */

	private void determineTFARandACREupdates() {

		for (SyncTreeNodeDTO node : listFilesToUpdate) {
			checkTFARandACREupdates(node);
		}
	}

	private void checkTFARandACREupdates(SyncTreeNodeDTO node) {

		if (node.isLeaf()) {
			SyncTreeDirectoryDTO parent = node.getParent();
			if (parent != null) {
				checkTFARandACREupdates(parent);
			}
		} else {
			SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) node;
			if (node.getName().toLowerCase().contains("task_force_radio")) {
				if (directory.isUpdated() || directory.isChanged()) {
					tfarIsUpdated = true;
				}
			} else if (node.getName().toLowerCase().contains("acre2")) {
				if (directory.isUpdated() || directory.isChanged()) {
					acre2IsUpdated = true;
				}
			} else {
				SyncTreeDirectoryDTO parent = node.getParent();
				if (parent != null) {
					checkTFARandACREupdates(parent);
				}
			}
		}
	}

	/* Userconfig Update */

	private void determineUserconfigUpdates() {

		userconfigNode = null;
		for (SyncTreeNodeDTO node : listFilesToUpdate) {
			isUserconfigParent(node);
			if (userconfigNode != null) {
				break;
			}
		}
	}

	private void isUserconfigParent(SyncTreeNodeDTO node) {

		boolean ok = false;
		if (!node.isLeaf()) {
			SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) node;
			if (directory.getName().toLowerCase().equals("userconfig")) {
				if (directory.getParent().getName()
						.equals(SyncTreeDirectoryDTO.RACINE)) {
					ok = true;
				}
			}
		}
		if (!ok) {
			SyncTreeDirectoryDTO parent = node.getParent();
			if (parent != null) {
				isUserconfigParent(parent);
			}
		} else {
			userconfigNode = (SyncTreeDirectoryDTO) node;
		}
	}

	/* Getters and Setters */

	public List<SyncTreeNodeDTO> getResumedFiles() {

		List<SyncTreeNodeDTO> resumedListFilesToUpdate = new ArrayList<SyncTreeNodeDTO>();
		resumedListFilesToUpdate.clear();
		for (SyncTreeNodeDTO node : listFilesToUpdate) {
			if (!node.getDownloadStatus().equals(DownloadStatus.DONE)) {
				resumedListFilesToUpdate.add(node);
			}
		}
		return resumedListFilesToUpdate;
	}

	public long getResumedFilesSize() {

		long resumedFilesSize = 0;
		for (SyncTreeNodeDTO node : listFilesToUpdate) {
			if (node.isLeaf()) {
				SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
				if (leaf.getDownloadStatus().equals(DownloadStatus.DONE)) {
					resumedFilesSize = resumedFilesSize
							+ determineDownloadSize(leaf);
				}
			}
		}
		return resumedFilesSize;
	}

	public long getTotalFilesSize() {
		return totalDownloadFilesSize;
	}

	public int getTotalFilesSelected() {
		return totalNumberFilesSelected;
	}

	public int getTotalFilesUpdated() {
		return totalNumberFilesUpdated;
	}

	public int getTotalFilesDeleted() {
		return totalNumberFilesDeleted;
	}

	public List<SyncTreeNodeDTO> getListFilesToUpdate() {
		return listFilesToUpdate;
	}

	public List<SyncTreeNodeDTO> getListFilesToDelete() {
		return listFilesToDelete;
	}

	public long getTotalDownloadFilesSize() {
		return totalDownloadFilesSize;
	}

	public int getTotalNumberFilesSelected() {
		return totalNumberFilesSelected;
	}

	public int getTotalNumberFilesUpdated() {
		return totalNumberFilesUpdated;
	}

	public int getTotalNumberFilesDeleted() {
		return totalNumberFilesDeleted;
	}

	public long getTotalDiskFilesSize() {
		return totalDiskFilesSize;
	}

	public long getTotalCompressedFilesSize() {
		return totalCompressedFilesSize;
	}

	public long getTotalUncompressedFilesSize() {
		return totalUncompressedFilesSize;
	}

	public long getTotalUncompleteExpectedFileSize() {
		return totalUncompleteExpectedFileSize;
	}

	public long getTotalUncompleteDiskFileSize() {
		return totalUncompleteDiskFileSize;
	}

	public int getTotalNumberUnCompleteFiles() {
		return totalNumberUnCompleteFiles;
	}

	public int getTotalNumberCompressedFiles() {
		return totalNumberCompressedFiles;
	}

	public void setParent(SyncTreeNodeDTO parent) {
		this.parent = parent;
	}

	public long getAverageDownloadSpeed() {
		return averageDownloadSpeed;
	}

	public void setAverageDownloadSpeed(long averageDownloadSpeed) {
		this.averageDownloadSpeed = averageDownloadSpeed;
	}

	public long getAverageResponseTime() {
		return averageResponseTime;
	}

	public void setAverageResponseTime(long averageResponseTime) {
		this.averageResponseTime = averageResponseTime;
	}

	public int getMaxActiveconnections() {
		return maxActiveconnections;
	}

	public void setMaxActiveconnections(int maxActiveconnections) {
		this.maxActiveconnections = maxActiveconnections;
	}

	public boolean isTfarIsUpdated() {
		return tfarIsUpdated;
	}

	public boolean isAcre2IsUpdated() {
		return acre2IsUpdated;
	}

	public boolean isUserconfigUpdated() {
		return userconfigNode != null;
	}

	public SyncTreeDirectoryDTO getUserconfigNode() {
		return userconfigNode;
	}
}
