package fr.soe.a3s.service.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.connection.HttpDAO;
import fr.soe.a3s.dao.connection.processors.ConnectionCheckProcessor;
import fr.soe.a3s.dao.connection.processors.ConnectionDownloadProcessor;
import fr.soe.a3s.dao.repository.RepositoryDAO;
import fr.soe.a3s.dao.zip.UnZipFlowProcessor;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.Changelogs;
import fr.soe.a3s.domain.repository.Events;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.dto.AutoConfigDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.exception.repository.RepositoryNotFoundException;
import fr.soe.a3s.exception.repository.ServerInfoNotFoundException;
import fr.soe.a3s.exception.repository.SyncFileNotFoundException;

public class HttpService extends AbstractConnexionService implements
		ConnexionService, DataAccessConstants {

	private final List<HttpDAO> httpDAOPool = new ArrayList<HttpDAO>();
	private final UnZipFlowProcessor unZipFlowProcessor = new UnZipFlowProcessor();
	private static final RepositoryDAO repositoryDAO = new RepositoryDAO();

	/* Initialize Service */

	public HttpService(int nbConnections) {
		assert (nbConnections != 0);
		for (int i = 0; i < nbConnections; i++) {
			HttpDAO httpDAO = new HttpDAO();
			httpDAOPool.add(httpDAO);
		}
	}

	public HttpService() {
		HttpDAO httpDAO = new HttpDAO();
		httpDAOPool.add(httpDAO);
	}

	/* Get A3S Files */

	@Override
	public void getSync(String repositoryName) throws RepositoryException,
			IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		SyncTreeDirectory syncTreeDirectory = null;
		try {
			syncTreeDirectory = httpDAOPool.get(0).downloadSync(repositoryName,
					repository.getProtocol());
		} finally {
			repository.setSync(syncTreeDirectory);
			httpDAOPool.get(0).disconnect();
		}
	}

	@Override
	public void getServerInfo(String repositoryName)
			throws RepositoryException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		ServerInfo serverInfo = null;
		try {
			serverInfo = httpDAOPool.get(0).downloadSeverInfo(
					repository.getName(), repository.getProtocol());
		} finally {
			repository.setServerInfo(serverInfo);
			httpDAOPool.get(0).disconnect();
		}
	}

	@Override
	public void getChangelogs(String repositoryName)
			throws RepositoryException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		Changelogs changelogs = null;
		try {
			changelogs = httpDAOPool.get(0).downloadChangelogs(
					repository.getName(), repository.getProtocol());
		} finally {
			repository.setChangelogs(changelogs);
			httpDAOPool.get(0).disconnect();
		}
	}

	@Override
	public void getAutoconfig(String repositoryName)
			throws RepositoryException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		AutoConfig autoConfig = null;
		try {
			autoConfig = httpDAOPool.get(0).downloadAutoconfig(
					repository.getName(), repository.getProtocol());
		} finally {
			repository.setAutoConfig(autoConfig);
			httpDAOPool.get(0).disconnect();
		}
	}

	@Override
	public void getEvents(String repositoryName) throws RepositoryException,
			IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		Events events = null;
		try {
			events = httpDAOPool.get(0).downloadEvents(repository.getName(),
					repository.getProtocol());
		} finally {
			repository.setEvents(events);
			httpDAOPool.get(0).disconnect();
		}
	}

	/* Check Repository */

	@Override
	public void checkRepository(String repositoryName)
			throws RepositoryException, IOException {

		System.out.println("Checking repository: " + repositoryName);

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		/* Sync */
		if (!httpDAOPool.get(0).isCanceled()) {
			getSync(repositoryName);
		}

		/* Serverinfo */
		if (!httpDAOPool.get(0).isCanceled()) {
			getServerInfo(repositoryName);
			if (repository.getServerInfo() != null) {
				repository.getHiddenFolderPath().addAll(
						repository.getServerInfo().getHiddenFolderPaths());
				if (repository.getNumberOfClientConnections() == 0) {
					repository.setNumberOfClientConnections(repository
							.getServerInfo().getNumberOfConnections());
				}
			}
		}
		/* Changelogs */
		if (!httpDAOPool.get(0).isCanceled()) {
			getChangelogs(repositoryName);
		}
		/* Events */
		if (!httpDAOPool.get(0).isCanceled()) {
			getEvents(repositoryName);
		}
		/* Autoconfig */
		if (!httpDAOPool.get(0).isCanceled()) {
			getAutoconfig(repositoryName);
			if (repository.getAutoConfig() != null) {
				updateFavoriteServersFromAutoconfig(repository.getAutoConfig());
			}
		}
	}

	/* Import autoconfig */

	@Override
	public AutoConfigDTO importAutoConfig(AbstractProtocole protocol)
			throws IOException {

		AutoConfigDTO autoConfigDTO = null;
		try {
			AutoConfig autoConfig = httpDAOPool.get(0).importAutoConfig(
					protocol);
			if (autoConfig != null) {
				updateFavoriteServersFromAutoconfig(autoConfig);
				autoConfigDTO = transformAutoConfig2DTO(autoConfig);
			}
		} finally {
			httpDAOPool.get(0).disconnect();
		}
		return autoConfigDTO;
	}

	/* Determine file completion */

	@Override
	public String determineFilesCompletion(String repositoryName,
			SyncTreeDirectoryDTO parent) throws RepositoryException,
			IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		assert (repository.getSync() != null);
		assert (repository.getServerInfo() != null);

		if (repository.getSync() == null) {
			throw new SyncFileNotFoundException(repositoryName);
		}
		if (repository.getServerInfo() == null) {
			throw new ServerInfoNotFoundException(repositoryName);
		}

		boolean isCompressedPboFilesOnly = repository.getServerInfo()
				.isCompressedPboFilesOnly();
		boolean noPartialFileTransfer = repository.getServerInfo()
				.isNoPartialFileTransfer();

		List<SyncTreeLeafDTO> list = parent.getDeepSearchLeafsList();

		httpDAOPool.get(0).setTotalCount(list.size());

		// Check if server supports partial file transfer
		String header = null;
		if (!noPartialFileTransfer) {
			header = httpDAOPool.get(0).checkPartialFileTransfer(
					repository.getName(), repository.getProtocol());
		}

		if (header != null) {// Partial file transfer is not supported
			noPartialFileTransfer = true;
		}

		for (SyncTreeLeafDTO leaf : list) {
			if (httpDAOPool.get(0).isCanceled()) {
				break;
			} else {
				if (leaf.isUpdated()) {
					if (isCompressedPboFilesOnly) {
						leaf.setComplete(0);
					} else if (noPartialFileTransfer) {
						leaf.setComplete(0);
					} else {
						final String rootDestinationPath = repository
								.getDefaultDownloadLocation();

						String destinationPath = null;
						String remotePath = repository.getProtocol()
								.getRemotePath();
						String path = leaf.getParentRelativePath();
						if (leaf.getDestinationPath() != null) {
							destinationPath = leaf.getDestinationPath();
							if (!path.isEmpty()) {
								remotePath = remotePath + "/" + path;
							}
						} else {
							destinationPath = rootDestinationPath;
							if (!path.isEmpty()) {
								destinationPath = rootDestinationPath + "/"
										+ path;
								remotePath = remotePath + "/" + path;
							}
						}

						leaf.setDestinationPath(destinationPath);
						leaf.setRemotePath(remotePath);

						double complete = httpDAOPool.get(0).getFileCompletion(
								leaf.getRemotePath(),
								leaf.getDestinationPath(), leaf,
								repository.getProtocol());
						leaf.setComplete(complete);
					}
				} else {
					leaf.setComplete(100);
				}
			}
		}

		return header;
	}

	/* Download Files */

	@Override
	public void synchronize(String repositoryName,
			List<SyncTreeNodeDTO> filesToDownload) throws RepositoryException,
			IOException {

		final Repository repository = repositoryDAO.getMap()
				.get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		List<AbstractConnexionDAO> connectionDAOs = new ArrayList<AbstractConnexionDAO>();
		for (HttpDAO httpDAO : httpDAOPool) {
			connectionDAOs.add(httpDAO);
		}

		ConnectionDownloadProcessor downloadProcessor = new ConnectionDownloadProcessor();
		downloadProcessor.init(filesToDownload, connectionDAOs, repository,
				unZipFlowProcessor);
		downloadProcessor.run();
	}

	/* Check Repository synchronization */

	@Override
	public List<Exception> checkRepositoryContent(String repositoryName)
			throws RepositoryException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		assert (repository.getSync() != null);
		assert (repository.getServerInfo() != null);

		SyncTreeDirectory parent = repository.getSync();

		SyncTreeDirectoryDTO parentDTO = new SyncTreeDirectoryDTO();
		parentDTO.setName("racine");
		parentDTO.setParent(null);
		transformSyncTreeDirectory2DTO(parent, parentDTO);

		List<SyncTreeNodeDTO> filesToCheck = parentDTO.getDeepSearchNodeList();
		boolean isCompressedPboFilesOnly = repository.getServerInfo()
				.isCompressedPboFilesOnly();
		boolean withzsync = true;

		try {
			ConnectionCheckProcessor checkProcessor = new ConnectionCheckProcessor(
					httpDAOPool.get(0), filesToCheck, isCompressedPboFilesOnly,
					withzsync, repositoryName, repository.getProtocol());
			checkProcessor.run();
			return checkProcessor.getErrors();
		} finally {
			httpDAOPool.get(0).disconnect();
		}
	}

	/* Upload Repository */

	@Override
	public void getSyncWithUploadProtocole(String repositoryName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void uploadRepository(String repositoryName,
			List<SyncTreeNodeDTO> filesToCheck,
			List<SyncTreeNodeDTO> filesToUpload,
			List<SyncTreeNodeDTO> filesToDelete, int lastIndexFileUploaded) {
		throw new UnsupportedOperationException();
	}

	/* Upload Events */

	@Override
	public boolean upLoadEvents(String repositoryName)
			throws RepositoryException, IOException {
		throw new UnsupportedOperationException();
	}

	/* Cancel */

	@Override
	public void cancel() {
		unZipFlowProcessor.cancel();
		for (HttpDAO httpDAO : httpDAOPool) {
			httpDAO.cancel();
			httpDAO.disconnect();
		}
	}

	/* Getters and Setters */

	@Override
	public HttpDAO getConnexionDAO() {
		return httpDAOPool.get(0);
	}

	@Override
	public List<AbstractConnexionDAO> getConnexionDAOs() {
		List<AbstractConnexionDAO> list = new ArrayList<>();
		for (HttpDAO httpDAO : httpDAOPool) {
			list.add(httpDAO);
		}
		return list;
	}

	@Override
	public int getNumberConnections() {
		return httpDAOPool.size();
	}

	@Override
	public void setMaximumClientDownloadSpeed(double value) {
		for (HttpDAO httpDAO : httpDAOPool) {
			httpDAO.setMaximumClientDownloadSpeed(value);
		}
	}

	@Override
	public UnZipFlowProcessor getUnZipFlowProcessor() {
		return this.unZipFlowProcessor;
	}
}
