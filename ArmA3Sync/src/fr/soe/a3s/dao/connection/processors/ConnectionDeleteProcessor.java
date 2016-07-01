package fr.soe.a3s.dao.connection.processors;

import java.io.IOException;
import java.util.List;

import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.connection.RemoteFile;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class ConnectionDeleteProcessor extends AbstractConnectionProcessor {

	private final AbstractProtocole protocol;

	public ConnectionDeleteProcessor(AbstractConnexionDAO abstractConnexionDAO,
			List<SyncTreeNodeDTO> filesToDelete,
			boolean isCompressedPboFilesOnly, boolean withzsync,
			AbstractProtocole protocol) {
		super(abstractConnexionDAO, filesToDelete, isCompressedPboFilesOnly,
				withzsync);
		this.protocol = protocol;
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
				if (found) {
					abstractConnexionDAO.deleteFile(remoteFile,
							protocol.getRemotePath());
				}
				count++;
				abstractConnexionDAO.setCount(count);
				abstractConnexionDAO.updateObserverCount();
			}
		}
	}
}
