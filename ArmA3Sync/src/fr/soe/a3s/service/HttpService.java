package fr.soe.a3s.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import fr.soe.a3s.controller.ObserverProceed;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.connection.HttpDAO;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.Changelogs;
import fr.soe.a3s.domain.repository.Events;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.domain.repository.SyncTreeLeaf;
import fr.soe.a3s.dto.AutoConfigDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.exception.repository.RepositoryNotFoundException;
import fr.soe.a3s.exception.repository.ServerInfoNotFoundException;
import fr.soe.a3s.exception.repository.SyncFileNotFoundException;

public class HttpService extends AbstractConnexionService implements
		DataAccessConstants {

	private final List<HttpDAO> httpDAOPool = new ArrayList<HttpDAO>();

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
			httpDAOPool.get(0).connectToRepository(repositoryName,
					repository.getProtocol(), SYNC_FILE_PATH);
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
			httpDAOPool.get(0).connectToRepository(repositoryName,
					repository.getProtocol(), SERVERINFO_FILE_PATH);
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
			httpDAOPool.get(0).connectToRepository(repositoryName,
					repository.getProtocol(), CHANGELOGS_FILE_PATH);
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
			httpDAOPool.get(0).connectToRepository(repositoryName,
					repository.getProtocol(), AUTOCONFIG_FILE_PATH);
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
			httpDAOPool.get(0).connectToRepository(repositoryName,
					repository.getProtocol(), EVENTS_FILE_PATH);
			events = httpDAOPool.get(0).downloadEvents(repository.getName(),
					repository.getProtocol());
		} finally {
			repository.setEvents(events);
			httpDAOPool.get(0).disconnect();
		}
	}

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
	public String determineCompletion(String repositoryName,
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

		String rootDestinationPath = repository.getDefaultDownloadLocation();
		String remotePath = repository.getProtocol().getRemotePath();

		List<SyncTreeLeafDTO> list = new ArrayList<SyncTreeLeafDTO>();

		determineFileForComputingCompletion(remotePath, rootDestinationPath,
				parent, list);

		httpDAOPool.get(0).setTotalCount(list.size());

		boolean isCompressedPboFilesOnly = repository.getServerInfo()
				.isCompressedPboFilesOnly();
		boolean noPartialFileTransfer = repository.getServerInfo()
				.isNoPartialFileTransfer();

		String header = null;
		if (!noPartialFileTransfer) {
			header = httpDAOPool.get(0).checkPartialFileTransfer(
					repository.getName(), repository.getProtocol());
		}

		if (header != null) {
			noPartialFileTransfer = true;
		}

		for (SyncTreeLeafDTO leaf : list) {
			if (httpDAOPool.get(0).isCanceled()) {
				break;
			} else {
				if (isCompressedPboFilesOnly) {
					leaf.setComplete(0);
				} else if (noPartialFileTransfer) {
					leaf.setComplete(0);
				} else {
					double complete = httpDAOPool.get(0).getFileCompletion(
							leaf.getRemotePath(), leaf.getDestinationPath(),
							leaf, repository.getProtocol());
					leaf.setComplete(complete);
				}
			}
		}

		return header;
	}

	private void determineFileForComputingCompletion(String rootRemotePath,
			String rootDestinationPath, SyncTreeDirectoryDTO parent,
			List<SyncTreeLeafDTO> list) {

		for (SyncTreeNodeDTO node : parent.getList()) {
			if (node.isLeaf()) {
				SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
				if (leaf.isUpdated()) {
					String destinationPath = null;
					String remotePath = rootRemotePath;
					String path = determinePath(node);
					if (node.getDestinationPath() != null) {
						destinationPath = node.getDestinationPath();
						if (!path.isEmpty()) {
							remotePath = remotePath + "/" + path;
						}
					} else {
						destinationPath = rootDestinationPath;
						if (!path.isEmpty()) {
							destinationPath = rootDestinationPath + "/" + path;
							remotePath = remotePath + "/" + path;
						}
					}
					leaf.setDestinationPath(destinationPath);
					leaf.setRemotePath(remotePath);

					File file = new File(destinationPath + "/" + node.getName());
					if (file.exists()) {
						list.add(leaf);
					} else {
						leaf.setComplete(0);
					}
				} else {
					leaf.setComplete(100);
				}

			} else {
				SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) node;
				determineFileForComputingCompletion(rootRemotePath,
						rootDestinationPath, directory, list);
			}
		}
	}

	@Override
	public void downloadAddons(String repositoryName,
			List<SyncTreeNodeDTO> listFiles) throws RepositoryException,
			IOException {

		final Repository repository = repositoryDAO.getMap()
				.get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		assert (repository.getSync() != null);
		assert (repository.getServerInfo() != null);

		final String rootDestinationPath = repository
				.getDefaultDownloadLocation();

		this.downloadFilesStack = new Stack<SyncTreeNodeDTO>();
		this.downloadFilesStack.addAll(listFiles);
		this.downloadErrors = new ArrayList<Exception>();
		this.downloadTimeouterrors = new ArrayList<Exception>();
		this.semaphore = 1;
		this.unZipFlowProcessor.init();

		for (final HttpDAO httpDAO : httpDAOPool) {
			httpDAO.addObserverProceed(new ObserverProceed() {
				@Override
				public void proceed() {
					if (!httpDAO.isCanceled()) {
						final SyncTreeNodeDTO node = popDownloadFilesStack();
						if (node != null) {
							Thread t = new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										if (aquireSemaphore()) {
											httpDAO.setAcquiredSemaphore(true);
										}

										httpDAO.setActiveConnection(true);
										httpDAO.updateObserverDownloadActiveConnections();

										File downloadedFile = downloadAddon(
												httpDAO, node,
												rootDestinationPath, repository);

										if (downloadedFile != null) {
											if (downloadedFile.isFile()) {
												if (downloadedFile
														.getName()
														.toLowerCase()
														.contains(
																DataAccessConstants.PBO_ZIP_EXTENSION)) {
													unZipFlowProcessor
															.unZipAsynchronously(downloadedFile);
												}
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
										if (!httpDAO.isCanceled()) {
											if (e instanceof SocketTimeoutException) {
												addTimeoutError(e);
												addError(e);
											} else if (e instanceof IOException) {
												// reset count
												downloadTimeouterrors.clear();
												addError(e);
											}
										}
									} finally {
										if (httpDAO.isAcquiredSemaphore()) {
											releaseSemaphore();
											httpDAO.setAcquiredSemaphore(false);
										}
										httpDAO.setActiveConnection(false);
										httpDAO.updateObserverDownloadActiveConnections();

										if (downloadTimeouterrors.size() > httpDAOPool
												.size()) {
											httpDAO.updateObserverDownloadTooManyTimeoutErrors(
													httpDAOPool.size(),
													downloadTimeouterrors);
										} else if (downloadErrors.size() > 10) {
											httpDAO.updateObserverDownloadTooManyErrors(
													10, downloadErrors);
										} else {
											httpDAO.updateObserverProceed();
										}
									}
								}
							});
							t.start();
						} else {// no more file to download

							// Check if there is no more active connections
							boolean downloadFinished = true;
							for (HttpDAO httpDAO : httpDAOPool) {
								if (httpDAO.isActiveConnection()) {
									downloadFinished = false;
									break;
								}
							}

							// download is finished
							if (downloadFinished) {
								// display uncompressing progress
								if (unZipFlowProcessor
										.uncompressionIsFinished()) {
									downloadErrors.addAll(unZipFlowProcessor
											.getErrors());
									if (downloadErrors.isEmpty()) {
										httpDAO.updateObserverDownloadEnd();
									} else {
										httpDAO.updateObserverDownloadEndWithErrors(downloadErrors);
									}
								} else {
									if (!unZipFlowProcessor.isStarted()) {
										unZipFlowProcessor
												.start(downloadErrors);
									}
								}
							} else {
								// Give semaphore to the other DAOs
								for (HttpDAO httpDAO : httpDAOPool) {
									if (httpDAO.isActiveConnection()
											&& aquireSemaphore()) {
										httpDAO.setAcquiredSemaphore(true);
										break;
									}
								}
							}
						}
					}
				}
			});
		}

		for (HttpDAO httpDAO : httpDAOPool) {
			if (!downloadFilesStack.isEmpty()) {// nb files < nb connections
				try {
					// Test connection to the Repository
					httpDAO.connectToRepository(repositoryName,
							repository.getProtocol(), SYNC_FILE_PATH);
					httpDAO.disconnect();
					httpDAO.updateObserverProceed();
				} catch (IOException e) {
					boolean isDowloading = false;
					httpDAO.setActiveConnection(false);
					for (HttpDAO hDAO : httpDAOPool) {
						if (hDAO.isActiveConnection()) {
							isDowloading = true;
							break;
						}
					}
					if (!isDowloading) {
						throw e;
					}
				}
			}
		}
	}

	private File downloadAddon(final HttpDAO httpDAO,
			final SyncTreeNodeDTO node, final String rootDestinationPath,
			final Repository repository) throws IOException {

		String destinationPath = null;
		String remotePath = repository.getProtocol().getRemotePath();
		String path = determinePath(node);
		if (node.getDestinationPath() != null) {
			destinationPath = node.getDestinationPath();
			if (!path.isEmpty()) {
				remotePath = remotePath + "/" + path;
			}
		} else {
			destinationPath = rootDestinationPath;
			if (!path.isEmpty()) {
				destinationPath = rootDestinationPath + "/" + path;
				remotePath = remotePath + "/" + path;
			}
		}

		return httpDAO.downloadFile(repository.getName(),
				repository.getProtocol(), remotePath, destinationPath, node);
	}

	@Override
	public boolean upLoadEvents(String repositoryName)
			throws RepositoryException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		boolean response = httpDAOPool.get(0).uploadEvents(
				repository.getEvents(), repository.getName(),
				repository.getRepositoryUploadProtocole());
		return response;
	}

	@Override
	public void getSyncWithRepositoryUploadProtocole(String repositoryName)
			throws RepositoryException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		SyncTreeDirectory syncTreeDirectory = httpDAOPool.get(0)
				.downloadSync(repository.getName(),
						repository.getRepositoryUploadProtocole());
		repository.setSync(syncTreeDirectory);// null if not found
	}

	@Override
	public void uploadRepository(String repositoryName,
			List<SyncTreeNodeDTO> allLocalFiles,
			List<SyncTreeNodeDTO> filesToUpload,
			List<SyncTreeNodeDTO> filesToDelete) {
		// unimplemented
	}

	@Override
	public List<Exception> checkRepositoryContent(String repositoryName)
			throws RepositoryException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		SyncTreeDirectory sync = repository.getSync();
		if (sync == null) {
			throw new SyncFileNotFoundException(repositoryName);
		}

		ServerInfo serverInfo = repository.getServerInfo();
		if (serverInfo == null) {
			throw new ServerInfoNotFoundException(repositoryName);
		}

		/* Get Files */
		this.checkRepositoryFilesList = new ArrayList<SyncTreeLeaf>();
		getFiles(sync);

		/* Errors */
		List<Exception> errorsCheckRepository = new ArrayList<Exception>();

		httpDAOPool.get(0).setTotalCount(this.checkRepositoryFilesList.size());
		httpDAOPool.get(0).setCount(0);

		int count = 0;
		for (SyncTreeLeaf leaf : this.checkRepositoryFilesList) {
			if (httpDAOPool.get(0).isCanceled()) {
				break;
			} else {
				String relativePath = determinePath(leaf);
				List<String> listFilesName = new ArrayList<String>();
				if (leaf.isCompressed()) {
					if (serverInfo.isCompressedPboFilesOnly()) {
						String fileName = leaf.getName() + ZIP_EXTENSION;// *.pbo.zip
						listFilesName.add(fileName);
					} else {
						String fileName = leaf.getName();
						listFilesName.add(fileName);
						fileName = leaf.getName() + ZSYNC_EXTENSION;
						listFilesName.add(fileName);
						fileName = leaf.getName() + ZIP_EXTENSION;
						listFilesName.add(fileName);
					}
				} else {
					String fileName = leaf.getName();
					listFilesName.add(fileName);
					fileName = leaf.getName() + ZSYNC_EXTENSION;
					listFilesName.add(fileName);
				}

				for (String fileName : listFilesName) {
					boolean found = fileExists(repository.getName(),
							relativePath, fileName, repository.getProtocol());
					if (!found) {
						errorsCheckRepository.add(new FileNotFoundException(
								"File not found on repository: " + relativePath
										+ "/" + fileName));
						httpDAOPool.get(0).updateObserverCheckCountError(
								errorsCheckRepository.size());
					}
				}
				count++;
				httpDAOPool.get(0).setCount(count);
			}
			httpDAOPool.get(0).updateObserverCheckProgress();
		}
		return errorsCheckRepository;
	}

	private boolean fileExists(String repositoryName, String relativePath,
			String fileName, AbstractProtocole protocole) throws IOException {

		return httpDAOPool.get(0).fileExists(repositoryName, relativePath,
				fileName, protocole);
	}

	/* Cancel */

	@Override
	public void cancel() {
		for (HttpDAO httpDAO : httpDAOPool) {
			httpDAO.cancel();
		}
		for (HttpDAO httpDAO : httpDAOPool) {
			httpDAO.disconnect();
		}
	}

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
}
