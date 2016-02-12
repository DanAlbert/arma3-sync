package fr.soe.a3s.dao.connection.processors;

import java.util.ArrayList;
import java.util.List;

import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.connection.RemoteFile;
import fr.soe.a3s.domain.Http;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public abstract class AbstractConnectionProcessor implements DataAccessConstants {

	private List<SyncTreeNodeDTO> filesToExtract = null;
	private boolean compressedPboFilesOnly = false;
	private boolean withzsync = false;
	protected AbstractConnexionDAO abstractConnexionDAO = null;
	protected List<RemoteFile> remoteFiles = null;

	public AbstractConnectionProcessor(
			AbstractConnexionDAO abstractConnexionDAO,
			List<SyncTreeNodeDTO> filesToExtract,
			boolean isCompressedPboFilesOnly, boolean withzsync) {
		this.abstractConnexionDAO = abstractConnexionDAO;
		this.filesToExtract = filesToExtract;
		this.compressedPboFilesOnly = isCompressedPboFilesOnly;
		this.withzsync = withzsync;
		this.remoteFiles = new ArrayList<RemoteFile>();
	}

	protected void extract() {

		for (SyncTreeNodeDTO node : filesToExtract) {
			if (abstractConnexionDAO.isCanceled()) {
				break;
			} else {
				String parentDirectoryRelativePath = node.getParentRelativePath();
				if (node.isLeaf()) {
					SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
					if (leaf.isCompressed()) {
						if (compressedPboFilesOnly) {
							String fileName = leaf.getName() + ZIP_EXTENSION;// *.pbo.zip
							remoteFiles.add(new RemoteFile(fileName,
									parentDirectoryRelativePath, false));
						} else {
							String fileName = leaf.getName();// *.*
							remoteFiles.add(new RemoteFile(fileName,
									parentDirectoryRelativePath, false));
							fileName = leaf.getName() + ZIP_EXTENSION;// *.pbo.zip
							remoteFiles.add(new RemoteFile(fileName,
									parentDirectoryRelativePath, false));
							if (withzsync) {
								fileName = leaf.getName() + ZSYNC_EXTENSION;// *.pbo.zsync
								remoteFiles.add(new RemoteFile(fileName,
										parentDirectoryRelativePath, false));
							}
						}
					} else {
						String fileName = leaf.getName();// *.*
						remoteFiles.add(new RemoteFile(fileName,
								parentDirectoryRelativePath, false));
						if (withzsync) {
							fileName = leaf.getName() + ZSYNC_EXTENSION;// *.pbo.zsync
							remoteFiles.add(new RemoteFile(fileName,
									parentDirectoryRelativePath, false));
						}
					}
				} else {
					SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) node;
					String fileName = directory.getName();// *.*
					remoteFiles.add(new RemoteFile(fileName,
							parentDirectoryRelativePath, true));
				}
			}
		}
	}
}
