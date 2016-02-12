package fr.soe.a3s.service.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.connection.FtpDAO;
import fr.soe.a3s.dao.connection.RemoteFile;
import fr.soe.a3s.dao.connection.processors.ConnectionCheckProcessor;
import fr.soe.a3s.dao.connection.processors.ConnectionDeleteProcessor;
import fr.soe.a3s.dao.connection.processors.ConnectionDownloadProcessor;
import fr.soe.a3s.dao.connection.processors.ConnectionUploadProcessor;
import fr.soe.a3s.dao.repository.RepositoryDAO;
import fr.soe.a3s.dao.zip.UnZipFlowProcessor;
import fr.soe.a3s.domain.AbstractProtocole;
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
import fr.soe.a3s.exception.FtpException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.exception.repository.RepositoryNotFoundException;
import fr.soe.a3s.main.Version;

public class FtpService extends AbstractConnexionService implements
		ConnexionService, DataAccessConstants {

	private final List<FtpDAO> ftpDAOPool = new ArrayList<FtpDAO>();
	private final UnZipFlowProcessor unZipFlowProcessor = new UnZipFlowProcessor();
	private static final RepositoryDAO repositoryDAO = new RepositoryDAO();

	/* Initialize Service */

	public FtpService(int nbConnections) {
		assert (nbConnections != 0);
		for (int i = 0; i < nbConnections; i++) {
			FtpDAO ftpDAO = new FtpDAO();
			ftpDAOPool.add(ftpDAO);
		}
	}

	public FtpService() {
		FtpDAO ftpDAO = new FtpDAO();
		ftpDAOPool.add(ftpDAO);
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
			ftpDAOPool.get(0).connectToRepository(repository.getName(),
					repository.getProtocol());
			syncTreeDirectory = ftpDAOPool.get(0).downloadSync(repositoryName,
					repository.getProtocol());
		} finally {
			repository.setSync(syncTreeDirectory);
			ftpDAOPool.get(0).disconnect();
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
			ftpDAOPool.get(0).connectToRepository(repository.getName(),
					repository.getProtocol());
			serverInfo = ftpDAOPool.get(0).downloadSeverInfo(repositoryName,
					repository.getProtocol());
		} finally {
			repository.setServerInfo(serverInfo);
			ftpDAOPool.get(0).disconnect();
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
			ftpDAOPool.get(0).connectToRepository(repository.getName(),
					repository.getProtocol());
			changelogs = ftpDAOPool.get(0).downloadChangelogs(repositoryName,
					repository.getProtocol());
		} finally {
			repository.setChangelogs(changelogs);
			ftpDAOPool.get(0).disconnect();
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
			ftpDAOPool.get(0).connectToRepository(repository.getName(),
					repository.getProtocol());
			autoConfig = ftpDAOPool.get(0).downloadAutoconfig(repositoryName,
					repository.getProtocol());
		} finally {
			repository.setAutoConfig(autoConfig);
			ftpDAOPool.get(0).disconnect();
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
			ftpDAOPool.get(0).connectToRepository(repository.getName(),
					repository.getProtocol());
			events = ftpDAOPool.get(0).downloadEvents(repositoryName,
					repository.getProtocol());
		} finally {
			repository.setEvents(events);
			ftpDAOPool.get(0).disconnect();
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
		if (!ftpDAOPool.get(0).isCanceled()) {
			getSync(repositoryName);
		}

		/* Serverinfo */
		if (!ftpDAOPool.get(0).isCanceled()) {
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
		if (!ftpDAOPool.get(0).isCanceled()) {
			getChangelogs(repositoryName);
		}
		/* Events */
		if (!ftpDAOPool.get(0).isCanceled()) {
			getEvents(repositoryName);
		}
		/* Autoconfig */
		if (!ftpDAOPool.get(0).isCanceled()) {
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
			AutoConfig autoConfig = ftpDAOPool.get(0)
					.importAutoConfig(protocol);
			if (autoConfig != null) {
				updateFavoriteServersFromAutoconfig(autoConfig);
				autoConfigDTO = transformAutoConfig2DTO(autoConfig);
			}
		} finally {
			ftpDAOPool.get(0).disconnect();
		}
		return autoConfigDTO;
	}

	/* Determine file completion */

	@Override
	public String determineFilesCompletion(String repositoryName,
			SyncTreeDirectoryDTO parent) {

		List<SyncTreeLeafDTO> list = parent.getDeepSearchLeafsList();
		for (SyncTreeLeafDTO leaf : list) {
			if (leaf.isUpdated()) {
				leaf.setComplete(0);
			} else {
				leaf.setComplete(100);
			}
		}
		return null;
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
		for (FtpDAO ftpDAO : ftpDAOPool) {
			connectionDAOs.add(ftpDAO);
		}

		ConnectionDownloadProcessor downloadProcessor = new ConnectionDownloadProcessor();
		downloadProcessor.init(filesToDownload, connectionDAOs, repository,
				unZipFlowProcessor);
		downloadProcessor.run();
	}

	/* Upload Events */

	@Override
	public boolean upLoadEvents(String repositoryName)
			throws RepositoryException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}
		boolean response = false;
		try {
			ftpDAOPool.get(0).connectToRepository(repository.getName(),
					repository.getUploadProtocole());
			response = ftpDAOPool.get(0).uploadEvents(repository.getEvents(),
					repository.getUploadProtocole());
		} finally {
			ftpDAOPool.get(0).disconnect();
		}
		return response;
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
		boolean withzsync = false;

		try {
			ftpDAOPool.get(0).connectToRepository(repository.getName(),
					repository.getProtocol());
			ConnectionCheckProcessor checkProcessor = new ConnectionCheckProcessor(
					ftpDAOPool.get(0), filesToCheck, isCompressedPboFilesOnly,
					withzsync, repositoryName, repository.getProtocol());
			checkProcessor.run();
			return checkProcessor.getErrors();
		} finally {
			ftpDAOPool.get(0).disconnect();
		}
	}

	/* Upload Repository */

	@Override
	public void getSyncWithUploadProtocole(String repositoryName)
			throws RepositoryException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		SyncTreeDirectory syncTreeDirectory = null;
		try {
			ftpDAOPool.get(0).connectToRepository(repository.getName(),
					repository.getUploadProtocole());

			syncTreeDirectory = ftpDAOPool.get(0).downloadSync(repositoryName,
					repository.getUploadProtocole());
		} finally {
			repository.setSync(syncTreeDirectory);
			ftpDAOPool.get(0).disconnect();
		}
	}

	@Override
	public void uploadRepository(String repositoryName,
			List<SyncTreeNodeDTO> filesToCheck,
			List<SyncTreeNodeDTO> filesToUpload,
			List<SyncTreeNodeDTO> filesToDelete, int lastIndexFileUploaded)
			throws RepositoryException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		// ftpDAOPool.get(0).updateObserverCountWithText(
		// "Checking remote files...");
		//
		// Map<SyncTreeNodeDTO, List<RemoteFile>> mapFtpFilesToUpload = new
		// LinkedHashMap<SyncTreeNodeDTO, List<RemoteFile>>();
		//
		// ftpDAOPool.get(0).setTotalCount(allLocalFiles.size());
		// int count = 0;
		// int nbFilesToUpload = 0;
		//
		// try {
		// // Connect
		// ftpDAOPool.get(0).connectToRepository(repository.getName(),
		// repository.getRepositoryUploadProtocole());
		//
		// for (SyncTreeNodeDTO node : allLocalFiles) {
		// List<RemoteFile> ftpFilesToUpload = new ArrayList<RemoteFile>();
		// if (ftpDAOPool.get(0).isCanceled()) {
		// return;
		// } else {
		// String relativePath = node.getPath();
		// if (node.isLeaf()) {
		// SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
		// List<String> listFilesName = new ArrayList<String>();
		//
		// if (leaf.isCompressed()) {
		// if (repository.isUploadCompressedPboFilesOnly()) {
		// String fileName = leaf.getName()
		// + ZIP_EXTENSION;// *.pbo.zip
		// listFilesName.add(fileName);
		// } else {
		// String fileName = leaf.getName();// *.*/*.pbo
		// listFilesName.add(fileName);
		// fileName = leaf.getName() + ZIP_EXTENSION;// *.pbo.zip
		// listFilesName.add(fileName);
		// if (repository.getProtocol() instanceof Http) {
		// fileName = leaf.getName() + ZSYNC_EXTENSION;// *.pbo.zsync
		// listFilesName.add(fileName);
		// }
		// }
		// } else {
		// String fileName = leaf.getName();// *.*/*.pbo
		// listFilesName.add(fileName);
		// if (repository.getProtocol() instanceof Http) {
		// fileName = leaf.getName() + ZSYNC_EXTENSION;// *.pbo.zsync
		// listFilesName.add(fileName);
		// }
		// }
		// for (String fileName : listFilesName) {
		// boolean exists = remoteFileExists(
		// repository.getName(), relativePath,
		// fileName,
		// repository.getRepositoryUploadProtocole());
		// if (!exists || filesToUpload.contains(leaf)) {
		// ftpFilesToUpload.add(new RemoteFile(fileName,
		// relativePath, false));
		// }
		// }
		// } else {
		// String fileName = node.getName();
		// boolean exists = remoteFileExists(repository.getName(),
		// relativePath, fileName,
		// repository.getRepositoryUploadProtocole());
		// if (!exists || filesToUpload.contains(node)) {
		// ftpFilesToUpload.add(new RemoteFile(fileName,
		// relativePath, true));
		// }
		// }
		// }
		//
		// mapFtpFilesToUpload.put(node, ftpFilesToUpload);
		// nbFilesToUpload = nbFilesToUpload + ftpFilesToUpload.size();
		// count++;
		// ftpDAOPool.get(0).setCount(count);
		// ftpDAOPool.get(0).updateObserverCountWithText();
		// }
		// } finally {
		// // Disconnect
		// ftpDAOPool.get(0).disconnect();
		// }
		//
		// String repositoryPath = repository.getPath();
		// long totalFilesSize = 0;
		//
		// // Determine total files size
		// for (Iterator<List<RemoteFile>> iter = mapFtpFilesToUpload.values()
		// .iterator(); iter.hasNext();) {
		// List<RemoteFile> list = iter.next();
		// for (RemoteFile remoteFile : list) {
		// String relativePath = remoteFile
		// .getParentDirectoryRelativePath();
		// String fileName = remoteFile.getFilename();
		// boolean isFile = !remoteFile.isDirectory();
		// if (isFile) {
		// File file = new File(repositoryPath + "/" + relativePath
		// + "/" + fileName);
		// if (!file.exists()) {
		// throw new FileNotFoundException("File not found: "
		// + file.getAbsolutePath());
		// } else {
		// totalFilesSize = totalFilesSize + file.length();
		// }
		// }
		// }
		// }

		try {
			// Connect
			ftpDAOPool.get(0).connectToRepository(repository.getName(),
					repository.getUploadProtocole());

			/* Check remote files */
			ftpDAOPool.get(0).updateObserverText("Checking remote files...");

			ConnectionCheckProcessor checkProcessor = new ConnectionCheckProcessor(
					ftpDAOPool.get(0), filesToCheck,
					repository.isUploadCompressedPboFilesOnly(),
					(repository.getProtocol() instanceof Http), repositoryName,
					repository.getUploadProtocole());
			checkProcessor.run();

			List<RemoteFile> missingRemoteFiles = checkProcessor
					.getMissingRemoteFiles();

			/* Upload files */
			ftpDAOPool.get(0).updateObserverText("Uploading files...");

			ConnectionUploadProcessor uploadProcessor = new ConnectionUploadProcessor(
					ftpDAOPool.get(0), filesToUpload,
					repository.isUploadCompressedPboFilesOnly(),
					(repository.getProtocol() instanceof Http), repositoryName,
					repository.getUploadProtocole(), repository.getPath(),
					missingRemoteFiles, lastIndexFileUploaded);
			uploadProcessor.run();

			/* Delete extra remote files */
			ftpDAOPool.get(0).updateObserverText(
					"Deleting extra remote files...");

			ConnectionDeleteProcessor deleteProcessor = new ConnectionDeleteProcessor(
					ftpDAOPool.get(0), filesToDelete, false,
					(repository.getProtocol() instanceof Http), repositoryName,
					repository.getUploadProtocole());
			deleteProcessor.run();

			/* Upload sync files */
			ftpDAOPool.get(0).updateObserverText(
					"Uploading synchronization files...");

			// Set serverInfo with upload options
			repository.getLocalServerInfo().setCompressedPboFilesOnly(
					repository.isUploadCompressedPboFilesOnly());

			ftpDAOPool.get(0).uploadSync(repository.getLocalSync(),
					repository.getUploadProtocole().getRemotePath());
			ftpDAOPool.get(0).uploadServerInfo(repository.getLocalServerInfo(),
					repository.getUploadProtocole().getRemotePath());
			ftpDAOPool.get(0).uploadChangelogs(repository.getLocalChangelogs(),
					repository.getUploadProtocole().getRemotePath());
			ftpDAOPool.get(0).uploadAutoconfig(repository.getLocalAutoConfig(),
					repository.getUploadProtocole().getRemotePath());
			if (repository.getLocalEvents() != null) {
				ftpDAOPool.get(0).uploadEvents(repository.getLocalEvents(),
						repository.getUploadProtocole().getRemotePath());
			}
		} finally {
			ftpDAOPool.get(0).disconnect();
		}
	}

	/* Check for Updates */

	public String checkForUpdates(boolean devMode) throws FtpException {

		String response = null;
		try {
			String updateVersionName = ftpDAOPool.get(0).downloadXMLupdateFile(
					devMode);
			ftpDAOPool.get(0).disconnect();
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
			} else {
				response = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new FtpException("Failed to connect to updates repository");
		} finally {
			ftpDAOPool.get(0).disconnect();
		}
		return response;
	}

	/* Cancel */

	@Override
	public void cancel() {
		unZipFlowProcessor.cancel();
		for (FtpDAO ftpDAO : ftpDAOPool) {
			ftpDAO.cancel();
			ftpDAO.disconnect();
		}
	}

	/* Getters and Setters */

	@Override
	public AbstractConnexionDAO getConnexionDAO() {
		return ftpDAOPool.get(0);
	}

	@Override
	public List<AbstractConnexionDAO> getConnexionDAOs() {
		List<AbstractConnexionDAO> list = new ArrayList<>();
		for (FtpDAO ftpDAO : ftpDAOPool) {
			list.add(ftpDAO);
		}
		return list;
	}

	@Override
	public int getNumberConnections() {
		return ftpDAOPool.size();
	}

	@Override
	public void setMaximumClientDownloadSpeed(double value) {
		for (FtpDAO ftpDAO : ftpDAOPool) {
			ftpDAO.setMaximumClientDownloadSpeed(value);
		}
	}

	@Override
	public UnZipFlowProcessor getUnZipFlowProcessor() {
		return unZipFlowProcessor;
	}
}
