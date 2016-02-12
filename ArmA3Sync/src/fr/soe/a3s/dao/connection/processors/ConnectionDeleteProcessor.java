package fr.soe.a3s.dao.connection.processors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.connection.RemoteFile;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class ConnectionDeleteProcessor extends AbstractConnectionProcessor {

	private String repositoryName;
	private AbstractProtocole protocole;

	public ConnectionDeleteProcessor(AbstractConnexionDAO abstractConnexionDAO,
			List<SyncTreeNodeDTO> filesToDelete,
			boolean isCompressedPboFilesOnly, boolean withzsync,
			String repositoryName, AbstractProtocole protocole) {
		super(abstractConnexionDAO, filesToDelete, isCompressedPboFilesOnly,
				withzsync);
		this.repositoryName = repositoryName;
		this.protocole = protocole;
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
				if (found) {
					abstractConnexionDAO.deleteFile(remoteFile,
							protocole.getRemotePath());
				}
				count++;
				abstractConnexionDAO.setCount(count);
				abstractConnexionDAO.updateObserverCount();
			}
		}
	}
}
