package fr.soe.a3s.dao.connection;

import java.io.File;
import java.io.IOException;

import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class BitTorrentDAO extends AbstractConnexionDAO {

	@Override
	public boolean uploadFile(RemoteFile remoteFile, String repositoryPath,
			String remotePath) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean fileExists(AbstractProtocole protocole, RemoteFile remoteFile)
			throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteFile(RemoteFile remoteFile, String repositoryRemotePath)
			throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public File downloadFile(Repository repository, String remotePath,
			String destinationPath, SyncTreeNodeDTO node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void disconnect() {
		throw new UnsupportedOperationException();
	}
}
