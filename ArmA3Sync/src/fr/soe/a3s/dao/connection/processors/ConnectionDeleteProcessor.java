package fr.soe.a3s.dao.connection.processors;

import java.io.IOException;
import java.util.List;

import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.connection.RemoteFile;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class ConnectionDeleteProcessor extends AbstractConnectionProcessor {

	private final Repository repository;

	public ConnectionDeleteProcessor(AbstractConnexionDAO abstractConnexionDAO,
			List<SyncTreeNodeDTO> filesToDelete,
			boolean isCompressedPboFilesOnly, boolean withzsync,
			Repository repository) {
		super(abstractConnexionDAO, filesToDelete, isCompressedPboFilesOnly,
				withzsync);
		this.repository = repository;
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
				boolean found = abstractConnexionDAO.fileExists(repository,
						remoteFile);
				if (found) {
					abstractConnexionDAO.deleteFile(remoteFile, repository
							.getProtocol().getRemotePath());
				}
				count++;
				abstractConnexionDAO.setCount(count);
				abstractConnexionDAO.updateObserverCount();
			}
		}
	}
}
