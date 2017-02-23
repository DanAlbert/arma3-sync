package fr.soe.a3s.service.administration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class FilesUploadManager {

	private final List<SyncTreeNodeDTO> filesToUpload = new ArrayList<SyncTreeNodeDTO>();
	private final List<SyncTreeNodeDTO> filesToCheck = new ArrayList<SyncTreeNodeDTO>();
	private final List<SyncTreeNodeDTO> filesToDelete = new ArrayList<SyncTreeNodeDTO>();
	private SyncTreeDirectoryDTO localSync, remoteSync;

	public void update() {

		filesToUpload.clear();
		filesToCheck.clear();
		filesToDelete.clear();
		if (localSync != null) {
			compute();
		}
	}

	private void compute() {

		Map<String, SyncTreeNodeDTO> mapLocalSync = new HashMap<String, SyncTreeNodeDTO>();
		syncToMap(localSync, mapLocalSync);

		if (remoteSync == null) {
			for (Iterator<String> iter = mapLocalSync.keySet().iterator(); iter
					.hasNext();) {
				String path = iter.next();
				SyncTreeNodeDTO localNode = mapLocalSync.get(path);
				filesToUpload.add(localNode);
			}
		} else {
			Map<String, SyncTreeNodeDTO> mapRemoteSync = new HashMap<String, SyncTreeNodeDTO>();
			syncToMap(remoteSync, mapRemoteSync);

			for (Iterator<String> iter = mapLocalSync.keySet().iterator(); iter
					.hasNext();) {
				String path = iter.next();
				SyncTreeNodeDTO localNode = mapLocalSync.get(path);
				SyncTreeNodeDTO remoteNode = mapRemoteSync.get(path);

				boolean upload = true;
				if (remoteNode != null) {
					if (localNode.getName().equals(remoteNode.getName())) {
						if (localNode.isLeaf() && remoteNode.isLeaf()) {
							SyncTreeLeafDTO localLeaf = (SyncTreeLeafDTO) localNode;
							SyncTreeLeafDTO remoteLeaf = (SyncTreeLeafDTO) remoteNode;
							if (localLeaf.getSha1()
									.equals(remoteLeaf.getSha1())) {
								upload = false;
							}
						} else if (!localNode.isLeaf() && !remoteNode.isLeaf()) {// 2
																					// same
																					// directories
							upload = false;
						}
					}
				}

				if (upload) {
					filesToUpload.add(mapLocalSync.get(path));
				} else {
					filesToCheck.add(mapLocalSync.get(path));
				}
			}

			for (Iterator<String> iter = mapRemoteSync.keySet().iterator(); iter
					.hasNext();) {
				String path = iter.next();
				if (!mapLocalSync.containsKey(path)) {
					filesToDelete.add(mapRemoteSync.get(path));
				}
			}
		}
	}

	private void syncToMap(SyncTreeNodeDTO node,
			Map<String, SyncTreeNodeDTO> map) {

		if (!node.getName().equals(SyncTreeNodeDTO.RACINE)) {
			map.put(node.getRelativePath(), node);
		}
		if (!node.isLeaf()) {
			SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) node;
			for (SyncTreeNodeDTO n : directory.getList()) {
				syncToMap(n, map);
			}
		}
	}

	public List<SyncTreeNodeDTO> getFilesToUpload() {
		return filesToUpload;
	}

	public List<SyncTreeNodeDTO> getFilesToCheck() {
		return filesToCheck;
	}

	public List<SyncTreeNodeDTO> getFilesToDelete() {
		return filesToDelete;
	}

	public void setLocalSync(SyncTreeDirectoryDTO localSync) {
		this.localSync = localSync;
	}

	public void setRemoteSync(SyncTreeDirectoryDTO remoteSync) {
		this.remoteSync = remoteSync;
	}
}
