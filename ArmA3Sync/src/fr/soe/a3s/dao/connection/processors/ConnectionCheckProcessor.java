package fr.soe.a3s.dao.connection.processors;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.connection.FtpDAO;
import fr.soe.a3s.dao.connection.HttpDAO;
import fr.soe.a3s.dao.connection.RemoteFile;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.Http;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.domain.repository.SyncTreeLeaf;
import fr.soe.a3s.domain.repository.SyncTreeNode;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class ConnectionCheckProcessor extends AbstractConnectionProcessor {

	private String repositoryName;
	private AbstractProtocole protocole;
	private List<Exception> errors = null;
	private List<RemoteFile> missingRemoteFiles = null;

	public ConnectionCheckProcessor(AbstractConnexionDAO abstractConnexionDAO,
			List<SyncTreeNodeDTO> filesToCheck,
			boolean isCompressedPboFilesOnly, boolean withzsync,
			String repositoryName, AbstractProtocole protocole) {
		super(abstractConnexionDAO, filesToCheck, isCompressedPboFilesOnly,
				withzsync);
		this.repositoryName = repositoryName;
		this.protocole = protocole;
		this.errors = new ArrayList<Exception>();
		this.missingRemoteFiles = new ArrayList<RemoteFile>();
	}

	public void run() throws IOException {

		extract();

		abstractConnexionDAO.setTotalCount(this.remoteFiles.size());
		abstractConnexionDAO.setCount(0);
		int count = 0;

		for (RemoteFile remoteFile : remoteFiles) {
			if (abstractConnexionDAO.isCanceled()) {
				break;
			} else {
				boolean found = abstractConnexionDAO.fileExists(repositoryName,
						protocole, remoteFile);
				if (!found) {
					missingRemoteFiles.add(remoteFile);
					errors.add(new FileNotFoundException(
							"File not found on repository: "
									+ remoteFile
											.getParentDirectoryRelativePath()
									+ "/" + remoteFile.getFilename()));
					abstractConnexionDAO.updateObserverCountErrors(errors
							.size());
				}
				count++;
				abstractConnexionDAO.setCount(count);
				abstractConnexionDAO.updateObserverCount();
			}
		}
	}

	public List<RemoteFile> getMissingRemoteFiles() {
		return missingRemoteFiles;
	}

	public List<Exception> getErrors() {
		return this.errors;
	}
}
