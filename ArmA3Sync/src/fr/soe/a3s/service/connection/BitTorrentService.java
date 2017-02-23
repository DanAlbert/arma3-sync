package fr.soe.a3s.service.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.connection.BitTorrentDAO;
import fr.soe.a3s.dao.connection.processors.ConnectionDownloadProcessor;
import fr.soe.a3s.dao.repository.RepositoryDAO;
import fr.soe.a3s.dao.zip.UnZipFlowProcessor;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.dto.AutoConfigDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.exception.repository.RepositoryNotFoundException;

public class BitTorrentService extends AbstractConnexionService implements
		ConnexionService, DataAccessConstants {

	private final List<BitTorrentDAO> bitTorrentDAOpool = new ArrayList<BitTorrentDAO>();
	private final UnZipFlowProcessor unZipFlowProcessor = new UnZipFlowProcessor();
	private static final RepositoryDAO repositoryDAO = new RepositoryDAO();
	private ConnexionService centralConnexionService = null;

	public BitTorrentService(int nbConnections) {
		assert (nbConnections != 0);
		for (int i = 0; i < nbConnections; i++) {
			BitTorrentDAO bitTorrentDAO = new BitTorrentDAO();
			bitTorrentDAOpool.add(bitTorrentDAO);
		}
	}

	public BitTorrentService(ConnexionService centralConnexion) {
		centralConnexionService = centralConnexion;
	}

	@Override
	public void getSync(String repositoryName) throws RepositoryException,
			IOException {
		centralConnexionService.getSync(repositoryName);
	}

	@Override
	public void getServerInfo(String repositoryName)
			throws RepositoryException, IOException {
		centralConnexionService.getServerInfo(repositoryName);
	}

	@Override
	public void getChangelogs(String repositoryName)
			throws RepositoryException, IOException {
		centralConnexionService.getChangelogs(repositoryName);
	}

	@Override
	public void getAutoconfig(String repositoryName)
			throws RepositoryException, IOException {
		centralConnexionService.getAutoconfig(repositoryName);
	}

	@Override
	public void getEvents(String repositoryName) throws RepositoryException,
			IOException {
		centralConnexionService.getEvents(repositoryName);
	}

	@Override
	public void checkRepository(String repositoryName)
			throws RepositoryException, IOException {
		centralConnexionService.checkRepository(repositoryName);
	}

	@Override
	public AutoConfigDTO importAutoConfig(AbstractProtocole protocol)
			throws IOException {
		return centralConnexionService.importAutoConfig(protocol);
	}

	@Override
	public String getServerRangeRequestResponseHeader(String repositoryName) {
		return null;
	}

	@Override
	public String determineFilesCompletion(String repositoryName,
			SyncTreeDirectoryDTO parent) throws RepositoryException,
			IOException {
		return centralConnexionService.determineFilesCompletion(repositoryName,
				parent);
	}

	@Override
	public boolean upLoadEvents(String repositoryName)
			throws RepositoryException, IOException {
		return centralConnexionService.upLoadEvents(repositoryName);
	}

	@Override
	public void getSyncWithUploadProtocole(String repositoryName)
			throws RepositoryException, IOException {
		centralConnexionService.getSyncWithUploadProtocole(repositoryName);
	}

	@Override
	public void uploadRepository(String repositoryName,
			List<SyncTreeNodeDTO> filesToCheck,
			List<SyncTreeNodeDTO> filesToUpload,
			List<SyncTreeNodeDTO> filesToDelete, int lastIndexFileUploaded)
			throws RepositoryException, IOException {
		centralConnexionService.uploadRepository(repositoryName, filesToCheck,
				filesToUpload, filesToDelete, lastIndexFileUploaded);
	}

	@Override
	public List<Exception> checkRepositoryContent(String repositoryName)
			throws RepositoryException, IOException {
		return centralConnexionService.checkRepositoryContent(repositoryName);
	}

	@Override
	public void synchronize(String repositoryName,
			List<SyncTreeNodeDTO> filesToDownload) throws RepositoryException {

		final Repository repository = repositoryDAO.getMap()
				.get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		List<AbstractConnexionDAO> connectionDAOs = new ArrayList<AbstractConnexionDAO>();
		for (BitTorrentDAO bitTorrentDAO : bitTorrentDAOpool) {
			connectionDAOs.add(bitTorrentDAO);
		}

		ConnectionDownloadProcessor downloadProcessor = new ConnectionDownloadProcessor(
				filesToDownload, connectionDAOs, repository, unZipFlowProcessor);
		downloadProcessor.run();
	}

	@Override
	public void cancel() {
		centralConnexionService.cancel();
		unZipFlowProcessor.cancel();
		for (BitTorrentDAO bitTorrentDAO : bitTorrentDAOpool) {
			bitTorrentDAO.cancel();
			bitTorrentDAO.disconnect();
		}
	}

	/* Getters and Setters */

	@Override
	public AbstractConnexionDAO getConnexionDAO() {
		return centralConnexionService.getConnexionDAO();
	}

	@Override
	public List<AbstractConnexionDAO> getConnexionDAOs() {
		List<AbstractConnexionDAO> list = new ArrayList<>();
		for (BitTorrentDAO bitTorrentDAO : bitTorrentDAOpool) {
			list.add(bitTorrentDAO);
		}
		return list;
	}

	@Override
	public void setMaximumClientDownloadSpeed(double value) {
		for (BitTorrentDAO bitTorrentDAO : bitTorrentDAOpool) {
			bitTorrentDAO.setMaximumClientDownloadSpeed(value);
		}
	}

	@Override
	public UnZipFlowProcessor getUnZipFlowProcessor() {
		return this.unZipFlowProcessor;
	}
}
