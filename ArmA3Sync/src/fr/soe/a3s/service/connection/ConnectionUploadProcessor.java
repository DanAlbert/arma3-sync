package fr.soe.a3s.service.connection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.connection.RemoteFile;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.Http;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class ConnectionUploadProcessor extends AbstractConnectionProcessor {

	private final Repository repository;
	private List<RemoteFile> missingRemoteFiles = null;
	private final int lastIndexFileUploaded;
	private AbstractProtocole protocol = null;

	public ConnectionUploadProcessor(AbstractConnexionDAO abstractConnexionDAO,
			List<SyncTreeNodeDTO> filesToUpload,
			List<RemoteFile> missingRemoteFiles, int lastIndexFileUploaded,
			Repository repository) {
		super(abstractConnexionDAO, filesToUpload, repository
				.isUploadCompressedPboFilesOnly(),
				(repository.getProtocol() instanceof Http));
		this.repository = repository;
		this.missingRemoteFiles = missingRemoteFiles;
		this.lastIndexFileUploaded = lastIndexFileUploaded;
		this.protocol = repository.getUploadProtocole();
	}

	public void run() throws IOException {

		extract();

		// Append missing remotefiles
		remoteFiles.addAll(missingRemoteFiles);

		// Resume
		int maxIndex = remoteFiles.size() - 1;
		List<RemoteFile> list = new ArrayList<RemoteFile>();
		if (lastIndexFileUploaded < maxIndex) {
			for (int i = lastIndexFileUploaded; i <= maxIndex; i++) {
				list.add(remoteFiles.get(i));
			}
			remoteFiles.clear();
			remoteFiles.addAll(list);
		}

		// Determine total files size
		long totalFilesSize = 0;
		for (RemoteFile remoteFile : remoteFiles) {
			String relativePath = remoteFile.getParentDirectoryRelativePath();
			String fileName = remoteFile.getFilename();
			boolean isFile = !remoteFile.isDirectory();
			if (isFile) {
				File file = new File(repository.getPath() + "/" + relativePath
						+ "/" + fileName);
				if (!file.exists()) {
					throw new FileNotFoundException("File not found: "
							+ file.getAbsolutePath());
				} else {
					totalFilesSize = totalFilesSize + file.length();
				}
			}
		}

		abstractConnexionDAO.updateObserverUploadTotalSize(totalFilesSize);

		// Upload files
		for (RemoteFile remoteFile : remoteFiles) {
			if (abstractConnexionDAO.isCanceled()) {
				break;
			} else {
				File file = new File(repository.getPath() + "/"
						+ remoteFile.getRelativeFilePath());
				abstractConnexionDAO.uploadFile(protocol, file, remoteFile);
			}
		}

		// Set serverInfo with upload options
		repository.getLocalServerInfo().setCompressedPboFilesOnly(
				repository.isUploadCompressedPboFilesOnly());

		// Upload repository metadata
		if (!abstractConnexionDAO.isCanceled()) {

			abstractConnexionDAO.uploadA3SObject(repository.getLocalSync(),
					repository.getUploadProtocole(),
					DataAccessConstants.SYNC_FILE_NAME, repository.getName());

			abstractConnexionDAO.uploadA3SObject(
					repository.getLocalServerInfo(),
					repository.getUploadProtocole(),
					DataAccessConstants.SERVERINFO_FILE_NAME,
					repository.getName());

			abstractConnexionDAO.uploadA3SObject(
					repository.getLocalChangelogs(),
					repository.getUploadProtocole(),
					DataAccessConstants.CHANGELOGS_FILE_NAME,
					repository.getName());

			abstractConnexionDAO.uploadA3SObject(
					repository.getLocalAutoConfig(),
					repository.getUploadProtocole(),
					DataAccessConstants.AUTOCONFIG_FILE_NAME,
					repository.getName());

			if (repository.getLocalEvents() != null) {
				abstractConnexionDAO.uploadA3SObject(
						repository.getLocalEvents(),
						repository.getUploadProtocole(),
						DataAccessConstants.EVENTS_FILE_NAME,
						repository.getName());
			}
		}
	}
}
