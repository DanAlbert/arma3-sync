package fr.soe.a3s.dao.connection.processors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.connection.RemoteFile;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class ConnectionUploadProcessor extends AbstractConnectionProcessor {

	private final String repositoryName;
	private final String repositoryPath;
	private final AbstractProtocole protocole;
	private List<RemoteFile> missingRemoteFiles = null;
	private final int lastIndexFileUploaded;

	public ConnectionUploadProcessor(AbstractConnexionDAO abstractConnexionDAO,
			List<SyncTreeNodeDTO> filesToUpload,
			boolean isCompressedPboFilesOnly, boolean withzsync,
			String repositoryName, AbstractProtocole protocole,
			String repositoryPath, List<RemoteFile> missingRemoteFiles,
			int lastIndexFileUploaded) {
		super(abstractConnexionDAO, filesToUpload, isCompressedPboFilesOnly,
				withzsync);
		this.repositoryName = repositoryName;
		this.repositoryPath = repositoryPath;
		this.protocole = protocole;
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
				File file = new File(repositoryPath + "/" + relativePath + "/"
						+ fileName);
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
				abstractConnexionDAO.updateObserverText("Uploading file: "
						+ remoteFile.getParentDirectoryRelativePath() + "/"
						+ remoteFile.getFilename());
				boolean ok = abstractConnexionDAO.uploadFile(remoteFile,
						repositoryPath, this.protocole.getRemotePath());
				if (!ok) {
					throw new IOException("Failed to upload file: "
							+ remoteFile.getParentDirectoryRelativePath() + "/"
							+ remoteFile.getFilename());
				} else {
					abstractConnexionDAO
							.updateObserverUploadTotalSizeProgress();
					abstractConnexionDAO
							.updateObserverUploadLastIndexFileUploaded();
				}
			}
		}

		// // Set upload total size
		// ftpDAOPool.get(0).updateObserverUploadTotalSize(totalFilesSize);
		//
		// // Upload file
		// ftpDAOPool.get(0).updateObserverCountWithText("Uploading files...");
		//
		// try {
		// String repositoryRemotePath = repository
		// .getRepositoryUploadProtocole().getRemotePath();
		//
		// // Reconnect
		// ftpDAOPool.get(0).connectToRepository(repository.getName(),
		// repository.getRepositoryUploadProtocole());
		//
		// for (Iterator<List<RemoteFile>> iter = mapFtpFilesToUpload.values()
		// .iterator(); iter.hasNext();) {
		// List<RemoteFile> list = iter.next();
		// for (RemoteFile remoteFile : list) {
		// if (ftpDAOPool.get(0).isCanceled()) {
		// return;
		// } else {
		// String relativePath = remoteFile
		// .getParentDirectoryRelativePath();
		// String fileName = remoteFile.getFilename();
		// ftpDAOPool.get(0).updateObserverCountWithText(
		// "Uploading file: " + relativePath + "/"
		// + fileName);
		// boolean ok = ftpDAOPool.get(0).uploadFile(remoteFile,
		// repositoryPath, repositoryRemotePath);
		// if (!ok) {
		// throw new IOException("Failed to upload file: "
		// + relativePath + "/" + fileName);
		// }
		// }
		// }
		// ftpDAOPool.get(0).updateObserverUploadLastIndexFileUploaded();
		// }
	}
}
