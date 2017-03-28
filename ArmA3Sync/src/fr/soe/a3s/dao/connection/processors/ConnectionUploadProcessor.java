package fr.soe.a3s.dao.connection.processors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.connection.FtpDAO;
import fr.soe.a3s.dao.connection.RemoteFile;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.Http;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class ConnectionUploadProcessor extends AbstractConnectionProcessor {

	private final Repository repository;
	private final AbstractProtocole protocole;
	private List<RemoteFile> missingRemoteFiles = null;
	private final int lastIndexFileUploaded;

	public ConnectionUploadProcessor(AbstractConnexionDAO abstractConnexionDAO,
			List<SyncTreeNodeDTO> filesToUpload,
			List<RemoteFile> missingRemoteFiles, int lastIndexFileUploaded,
			Repository repository) {
		super(abstractConnexionDAO, filesToUpload, repository
				.isUploadCompressedPboFilesOnly(),
				(repository.getProtocol() instanceof Http));
		this.repository = repository;
		this.protocole = repository.getUploadProtocole();
		this.missingRemoteFiles = missingRemoteFiles;
		this.lastIndexFileUploaded = lastIndexFileUploaded;
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
				abstractConnexionDAO.uploadFile(remoteFile,
						repository.getPath(), this.protocole.getRemotePath());
			}
		}

		// Set serverInfo with upload options
		repository.getLocalServerInfo().setCompressedPboFilesOnly(
				repository.isUploadCompressedPboFilesOnly());

		if (!abstractConnexionDAO.isCanceled()) {
			((FtpDAO) abstractConnexionDAO).uploadSync(repository
					.getLocalSync(), repository.getUploadProtocole()
					.getRemotePath());
			((FtpDAO) abstractConnexionDAO).uploadServerInfo(repository
					.getLocalServerInfo(), repository.getUploadProtocole()
					.getRemotePath());
			((FtpDAO) abstractConnexionDAO).uploadChangelogs(repository
					.getLocalChangelogs(), repository.getUploadProtocole()
					.getRemotePath());
			((FtpDAO) abstractConnexionDAO).uploadAutoconfig(repository
					.getLocalAutoConfig(), repository.getUploadProtocole()
					.getRemotePath());
			if (repository.getLocalEvents() != null) {
				((FtpDAO) abstractConnexionDAO).uploadEvents(repository
						.getLocalEvents(), repository.getUploadProtocole()
						.getRemotePath());
			}
		}
	}
}
