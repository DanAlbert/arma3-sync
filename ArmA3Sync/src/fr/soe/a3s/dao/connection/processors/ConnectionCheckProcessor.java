package fr.soe.a3s.dao.connection.processors;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.connection.RemoteFile;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class ConnectionCheckProcessor extends AbstractConnectionProcessor {

	private final AbstractProtocole protocol;
	private List<Exception> errors = null;
	private List<RemoteFile> missingRemoteFiles = null;

	public ConnectionCheckProcessor(AbstractConnexionDAO abstractConnexionDAO,
			List<SyncTreeNodeDTO> filesToCheck,
			boolean isCompressedPboFilesOnly, boolean withzsync,
			AbstractProtocole protocol) {
		super(abstractConnexionDAO, filesToCheck, isCompressedPboFilesOnly,
				withzsync);
		this.protocol = protocol;
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
				boolean found = abstractConnexionDAO.fileExists(protocol,
						remoteFile);
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
