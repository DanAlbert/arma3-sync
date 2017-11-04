package fr.soe.a3s.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.dom4j.DocumentException;

import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.FileAccessMethods;
import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.connection.RemoteFile;
import fr.soe.a3s.dao.connection.ftp.FtpDAO;
import fr.soe.a3s.dao.connection.http.HttpDAO;
import fr.soe.a3s.dao.repository.RepositoryDAO;
import fr.soe.a3s.dao.zip.UnZipFlowProcessor;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.Ftp;
import fr.soe.a3s.domain.Http;
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
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.exception.repository.RepositoryNotFoundException;
import fr.soe.a3s.exception.repository.ServerInfoNotFoundException;
import fr.soe.a3s.exception.repository.SyncFileNotFoundException;
import fr.soe.a3s.main.Version;
import fr.soe.a3s.service.connection.ConnectionCheckProcessor;
import fr.soe.a3s.service.connection.ConnectionCompletionProcessor;
import fr.soe.a3s.service.connection.ConnectionDeleteProcessor;
import fr.soe.a3s.service.connection.ConnectionDownloadProcessor;
import fr.soe.a3s.service.connection.ConnectionUploadProcessor;

public class ConnectionService extends ObjectDTOtransformer {

	private final List<AbstractConnexionDAO> connexionDAOPool = new ArrayList<AbstractConnexionDAO>();
	private final UnZipFlowProcessor unZipFlowProcessor = new UnZipFlowProcessor();
	private static final RepositoryDAO repositoryDAO = new RepositoryDAO();

	/* Initialize Service */

	public ConnectionService(int nbConnections, AbstractProtocole protocol)
			throws CheckException {
		assert (nbConnections != 0);
		if (nbConnections == 0) {
			nbConnections = 1;
		}
		init(nbConnections, protocol);
	}

	public ConnectionService(AbstractProtocole protocol) throws CheckException {
		init(1, protocol);
	}

	private void init(int nbConnections, AbstractProtocole protocol)
			throws CheckException {

		for (int i = 0; i < nbConnections; i++) {
			if (protocol instanceof Ftp) {
				AbstractConnexionDAO ftpDAO = new FtpDAO();
				connexionDAOPool.add(ftpDAO);
			} else if (protocol instanceof Http) {
				AbstractConnexionDAO httpDAO = new HttpDAO();
				connexionDAOPool.add(httpDAO);
			} else {
				throw new CheckException("Unknown or unsupported protocol.");
			}
		}
	}

	/* Get A3S Files */

	public void getSync(String repositoryName) throws RepositoryException,
			IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		SyncTreeDirectory syncTreeDirectory = null;
		try {
			syncTreeDirectory = (SyncTreeDirectory) connexionDAOPool.get(0)
					.downloadA3SObject(repositoryName,
							repository.getProtocol(),
							DataAccessConstants.SYNC_FILE_NAME);
		} finally {
			repository.setSync(syncTreeDirectory);
		}
	}

	public void getServerInfo(String repositoryName)
			throws RepositoryException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		ServerInfo serverInfo = null;
		try {
			serverInfo = (ServerInfo) connexionDAOPool.get(0)
					.downloadA3SObject(repositoryName,
							repository.getProtocol(),
							DataAccessConstants.SERVERINFO_FILE_NAME);
		} finally {
			repository.setServerInfo(serverInfo);
		}
	}

	public void getChangelogs(String repositoryName)
			throws RepositoryException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		Changelogs changelogs = null;
		try {
			changelogs = (Changelogs) connexionDAOPool.get(0)
					.downloadA3SObject(repositoryName,
							repository.getProtocol(),
							DataAccessConstants.CHANGELOGS_FILE_NAME);
		} finally {
			repository.setChangelogs(changelogs);
		}
	}

	public void getAutoconfig(String repositoryName)
			throws RepositoryException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		AutoConfig autoConfig = null;
		try {
			autoConfig = (AutoConfig) connexionDAOPool.get(0)
					.downloadA3SObject(repositoryName,
							repository.getProtocol(),
							DataAccessConstants.AUTOCONFIG_FILE_NAME);
		} finally {
			repository.setAutoConfig(autoConfig);
		}
	}

	public void getEvents(String repositoryName) throws RepositoryException,
			IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		Events events = null;
		try {
			events = (Events) connexionDAOPool.get(0).downloadA3SObject(
					repositoryName, repository.getProtocol(),
					DataAccessConstants.EVENTS_FILE_NAME);
		} finally {
			repository.setEvents(events);
		}
	}

	/* Check Repository */

	public void checkRepository(String repositoryName)
			throws RepositoryException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		System.out.println("Checking repository: " + repositoryName
				+ " on url: "
				+ repository.getProtocol().getProtocolType().getPrompt()
				+ repository.getProtocol().getHostname() + ":"
				+ repository.getProtocol().getPort()
				+ repository.getProtocol().getRemotePath());

		/* Sync */
		if (!connexionDAOPool.get(0).isCanceled()) {
			getSync(repositoryName);
		}

		/* Serverinfo */
		if (!connexionDAOPool.get(0).isCanceled()) {
			getServerInfo(repositoryName);
		}
		/* Changelogs */
		if (!connexionDAOPool.get(0).isCanceled()) {
			getChangelogs(repositoryName);
		}
		/* Events */
		if (!connexionDAOPool.get(0).isCanceled()) {
			getEvents(repositoryName);
		}
		/* Autoconfig */
		if (!connexionDAOPool.get(0).isCanceled()) {
			getAutoconfig(repositoryName);
		}
	}

	/* Import autoconfig */

	public AutoConfigDTO importAutoConfig(AbstractProtocole protocol)
			throws IOException {

		System.out.println("Importing autoconfig from url: "
				+ protocol.getProtocolType().getPrompt()
				+ protocol.getHostname() + ":" + protocol.getPort()
				+ protocol.getRemotePath() + "/"
				+ DataAccessConstants.AUTOCONFIG_FILE_NAME);

		AutoConfigDTO autoConfigDTO = null;
		AutoConfig autoConfig = connexionDAOPool.get(0).importAutoConfig(
				protocol);
		if (autoConfig != null) {
			autoConfigDTO = transformAutoConfig2DTO(autoConfig);
		}
		return autoConfigDTO;
	}

	public String CheckPartialFileTransfer(String repositoryName)
			throws RepositoryNotFoundException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		String header = connexionDAOPool.get(0).checkPartialFileTransfer(
				repository);

		return header;
	}

	/* Determine file completion */

	public void determineFilesCompletion(String repositoryName,
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

		// Check if server supports partial file transfer
		String header = null;
		if (!noPartialFileTransfer) {
			header = CheckPartialFileTransfer(repositoryName);
		}

		if (header != null) {// Partial file transfer is not supported
			noPartialFileTransfer = true;
		}

		List<SyncTreeLeafDTO> filesToCheck = new ArrayList<SyncTreeLeafDTO>();

		for (SyncTreeLeafDTO leaf : list) {
			if (leaf.isUpdated()) {
				if (isCompressedPboFilesOnly) {
					leaf.setComplete(0);
				} else if (noPartialFileTransfer) {
					leaf.setComplete(0);
				}
				// else if (!leaf.getName().contains(
				// DataAccessConstants.PBO_EXTENSION)) {
				// // zsync only for pbo files
				// leaf.setComplete(0);
				// }
				else {
					filesToCheck.add(leaf);
				}
			} else {
				leaf.setComplete(100);
			}
		}

		ConnectionCompletionProcessor completionProcessor = new ConnectionCompletionProcessor(
				filesToCheck, connexionDAOPool, repository);
		completionProcessor.run();
	}

	/* Synchronize Files */

	public void synchronize(String repositoryName,
			List<SyncTreeNodeDTO> filesToDownload) throws RepositoryException {

		final Repository repository = repositoryDAO.getMap()
				.get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		System.out.println("Downloading from repository: " + repositoryName
				+ " on url: " + repository.getProtocol().getHostUrl());

		ConnectionDownloadProcessor downloadProcessor = new ConnectionDownloadProcessor(
				filesToDownload, connexionDAOPool, repository,
				unZipFlowProcessor);
		downloadProcessor.run();
	}

	/* Check Repository Synchronization */

	public List<Exception> checkRepositoryContent(String repositoryName)
			throws RepositoryException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		assert (repository.getSync() != null);
		assert (repository.getServerInfo() != null);

		System.out.println("Checking repository content: " + repositoryName
				+ " on url: "
				+ repository.getProtocol().getProtocolType().getPrompt()
				+ repository.getProtocol().getHostname() + ":"
				+ repository.getProtocol().getPort()
				+ repository.getProtocol().getRemotePath());

		SyncTreeDirectory parent = repository.getSync();

		SyncTreeDirectoryDTO parentDTO = new SyncTreeDirectoryDTO();
		parentDTO.setName("racine");
		parentDTO.setParent(null);
		transformSyncTreeDirectory2DTO(parent, parentDTO);

		List<SyncTreeNodeDTO> filesToCheck = parentDTO.getDeepSearchNodeList();
		boolean isCompressedPboFilesOnly = repository.getServerInfo()
				.isCompressedPboFilesOnly();

		ConnectionCheckProcessor checkProcessor = new ConnectionCheckProcessor(
				connexionDAOPool.get(0), filesToCheck,
				isCompressedPboFilesOnly,
				(repository.getProtocol() instanceof Http),
				repository.getProtocol());
		checkProcessor.run();
		return checkProcessor.getErrors();
	}

	/* Upload Repository */

	public void getSyncWithUploadProtocole(String repositoryName)
			throws RepositoryException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		SyncTreeDirectory syncTreeDirectory = null;
		try {
			syncTreeDirectory = (SyncTreeDirectory) connexionDAOPool.get(0)
					.downloadA3SObject(repositoryName,
							repository.getUploadProtocole(),
							DataAccessConstants.SYNC_FILE_NAME);
		} finally {
			repository.setSync(syncTreeDirectory);
		}
	}

	public void uploadRepository(String repositoryName,
			List<SyncTreeNodeDTO> filesToCheck,
			List<SyncTreeNodeDTO> filesToUpload,
			List<SyncTreeNodeDTO> filesToDelete, int lastIndexFileUploaded)
			throws RepositoryException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		System.out.println("Uploading repository: " + repositoryName
				+ " on url: "
				+ repository.getUploadProtocole().getProtocolType().getPrompt()
				+ repository.getUploadProtocole().getHostname() + ":"
				+ repository.getUploadProtocole().getPort()
				+ repository.getUploadProtocole().getRemotePath());

		/* Check remote files */
		connexionDAOPool.get(0).updateObserverText("Checking remote files...");

		ConnectionCheckProcessor checkProcessor = new ConnectionCheckProcessor(
				connexionDAOPool.get(0), filesToCheck,
				repository.isUploadCompressedPboFilesOnly(),
				(repository.getProtocol() instanceof Http),
				repository.getUploadProtocole());
		checkProcessor.run();

		List<RemoteFile> missingRemoteFiles = checkProcessor
				.getMissingRemoteFiles();

		/* Upload files */
		connexionDAOPool.get(0).updateObserverText("Uploading files...");

		ConnectionUploadProcessor uploadProcessor = new ConnectionUploadProcessor(
				connexionDAOPool.get(0), filesToUpload, missingRemoteFiles,
				lastIndexFileUploaded, repository);
		uploadProcessor.run();

		/* Delete extra remote files */
		connexionDAOPool.get(0).updateObserverText(
				"Deleting extra remote files...");

		ConnectionDeleteProcessor deleteProcessor = new ConnectionDeleteProcessor(
				connexionDAOPool.get(0), filesToDelete, false,
				(repository.getProtocol() instanceof Http),
				repository.getUploadProtocole());
		deleteProcessor.run();
	}

	public void upLoadEvents(String repositoryName) throws RepositoryException,
			IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		connexionDAOPool.get(0).uploadA3SObject(repository.getEvents(),
				repository.getUploadProtocole(),
				DataAccessConstants.EVENTS_FILE_NAME, repositoryName);
	}

	/* Check for Updates */

	public String checkForUpdates(boolean devMode, AbstractProtocole protocol)
			throws IOException, DocumentException {

		String response = null;

		String updateVersionName = connexionDAOPool.get(0)
				.downloadXMLupdateFile(devMode, protocol);

		if (updateVersionName != null) {

			System.out.println("ArmA3Sync Available update version = "
					+ updateVersionName);

			StringTokenizer stringTokenizer = new StringTokenizer(
					updateVersionName, ".");

			if (stringTokenizer.countTokens() == 3) {

				String major = stringTokenizer.nextToken();
				String minor = stringTokenizer.nextToken();
				String build = stringTokenizer.nextToken();

				int updateBuild = Integer.parseInt(build);
				int actualBuild = Version.getBuild();

				if (updateBuild > actualBuild) {
					response = updateVersionName;
				}
			}
		}

		return response;
	}

	public void unZip(String repositoryName,
			List<SyncTreeLeafDTO> downloadedFiles)
			throws RepositoryNotFoundException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		for (SyncTreeLeafDTO leaf : downloadedFiles) {
			File zipFile = new File(repository.getDefaultDownloadLocation()
					+ "/" + leaf.getRelativePath()
					+ DataAccessConstants.ZIP_EXTENSION);
			if (zipFile.exists()) {
				unZipFlowProcessor.unZipAsynchronously(zipFile);
			}
		}
	}

	public void deleteExtraLocalFiles(String repositoryName,
			List<SyncTreeNodeDTO> listFilesToDelete)
			throws RepositoryNotFoundException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		for (SyncTreeNodeDTO node : listFilesToDelete) {
			File file = new File(repository.getDefaultDownloadLocation() + "/"
					+ node.getRelativePath());
			if (file.exists()) {
				if (file.isFile()) {
					FileAccessMethods.deleteFile(file);
				} else if (file.isDirectory()) {
					FileAccessMethods.deleteDirectory(file);
				}
			}
		}
	}

	/* Getters and Setters */

	public List<AbstractConnexionDAO> getConnexionDAOs() {
		return connexionDAOPool;
	}

	public UnZipFlowProcessor getUnZipFlowProcessor() {
		return this.unZipFlowProcessor;
	}

	public void setMaximumClientDownloadSpeed(double maximumClientDownloadSpeed) {
		for (AbstractConnexionDAO connexionDAO : connexionDAOPool) {
			connexionDAO
					.setMaximumClientDownloadSpeed(maximumClientDownloadSpeed);
		}
	}

	/* Cancel */

	public void cancel() {
		unZipFlowProcessor.cancel();
		for (AbstractConnexionDAO connectionDAO : connexionDAOPool) {
			connectionDAO.cancel();
		}
	}
}
