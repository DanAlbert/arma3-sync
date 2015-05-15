package fr.soe.a3s.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

import fr.soe.a3s.controller.ObserverFileDownload;
import fr.soe.a3s.dao.AbstractConnexionDAO;
import fr.soe.a3s.dao.ConfigurationDAO;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.FtpDAO;
import fr.soe.a3s.dao.RepositoryDAO;
import fr.soe.a3s.domain.Http;
import fr.soe.a3s.domain.configration.FavoriteServer;
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
import fr.soe.a3s.exception.HttpException;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.main.Version;

public class FtpService extends AbstractConnexionService implements
		DataAccessConstants {

	private final List<FtpDAO> ftpDAOPool = new ArrayList<FtpDAO>();
	private final Stack<SyncTreeNodeDTO> downloadFilesStack = new Stack<SyncTreeNodeDTO>();
	private final List<Exception> errors = new ArrayList<Exception>();
	private int semaphore = 1;
	boolean end = false;
	private static final RepositoryDAO repositoryDAO = new RepositoryDAO();
	private static final ConfigurationDAO configurationDAO = new ConfigurationDAO();

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

	@Override
	public AutoConfigDTO importAutoConfig(String autoconfigURL)
			throws FtpException, WritingException, ConnectException {

		AutoConfig autoConfig = ftpDAOPool.get(0).importAutoConfig(
				autoconfigURL);
		if (autoConfig != null) {
			List<FavoriteServer> list1 = autoConfig.getFavoriteServers();
			List<FavoriteServer> list2 = configurationDAO.getConfiguration()
					.getFavoriteServers();
			List<FavoriteServer> newList = new ArrayList<FavoriteServer>();
			newList.addAll(list1);
			for (FavoriteServer favoriteServer2 : list2) {
				boolean contains = false;
				for (FavoriteServer favoriteServer : newList) {
					if (favoriteServer.getName().equals(
							favoriteServer2.getName())) {
						contains = true;
						break;
					}
				}
				if (!contains) {
					newList.add(favoriteServer2);
				}
			}

			configurationDAO.getConfiguration().getFavoriteServers().clear();
			configurationDAO.getConfiguration().getFavoriteServers()
					.addAll(newList);
			return transformAutoConfig2DTO(autoConfig);
		} else {
			return null;
		}
	}

	@Override
	public void checkRepository(String repositoryName) throws FtpException,
			RepositoryException, WritingException, ConnectException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		ftpDAOPool.get(0).connectToRepository(repository.getName(),
				repository.getProtocole());

		// Sync
		SyncTreeDirectory syncTreeDirectory = ftpDAOPool.get(0).downloadSync(
				repositoryName, repository.getProtocole());
		repository.setSync(syncTreeDirectory);// null if not found

		// Serverinfo
		ServerInfo serverInfo = ftpDAOPool.get(0).downloadSeverInfo(
				repositoryName, repository.getProtocole());
		repository.setServerInfo(serverInfo);// null if not found

		if (serverInfo != null) {
			repository.getHiddenFolderPath().addAll(
					serverInfo.getHiddenFolderPaths());
		}

		// Cnangelogs
		Changelogs changelogs = ftpDAOPool.get(0).downloadChangelogs(
				repositoryName, repository.getProtocole());
		repository.setChangelogs(changelogs);// null if not found

		// Events
		Events events = ftpDAOPool.get(0).downloadEvent(repositoryName,
				repository.getProtocole());
		repository.setEvents(events);

		// Auto config
		AutoConfig autoConfig = ftpDAOPool.get(0).downloadAutoconfig(
				repositoryName, repository.getProtocole());
		repository.setAutoConfig(autoConfig);// null if not found

		if (autoConfig != null) {
			List<FavoriteServer> favoriteServers = autoConfig
					.getFavoriteServers();
			for (FavoriteServer favoriteServer : favoriteServers) {
				boolean contains = false;
				for (FavoriteServer server : configurationDAO
						.getConfiguration().getFavoriteServers()) {
					if (favoriteServer.getName().equals(server.getName())) {
						contains = true;
						break;
					}
				}
				if (!contains) {
					configurationDAO.getConfiguration().getFavoriteServers()
							.add(favoriteServer);
				}
			}
		}

		disconnect();
	}

	@Override
	public void getSync(String repositoryName) throws RepositoryException,
			FtpException, WritingException, ConnectException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		ftpDAOPool.get(0).connectToRepository(repository.getName(),
				repository.getProtocole());

		SyncTreeDirectory syncTreeDirectory = ftpDAOPool.get(0).downloadSync(
				repositoryName, repository.getProtocole());
		repository.setSync(syncTreeDirectory);

		disconnect();
	}

	@Override
	public void getServerInfo(String repositoryName)
			throws RepositoryException, ConnectException, FtpException,
			WritingException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		ftpDAOPool.get(0).connectToRepository(repository.getName(),
				repository.getProtocole());

		ServerInfo serverInfo = ftpDAOPool.get(0).downloadSeverInfo(
				repositoryName, repository.getProtocole());
		repository.setServerInfo(serverInfo);

		disconnect();
	}

	@Override
	public void getChangelogs(String repositoryName) throws ConnectException,
			FtpException, RepositoryException, WritingException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		ftpDAOPool.get(0).connectToRepository(repository.getName(),
				repository.getProtocole());

		Changelogs changelogs = ftpDAOPool.get(0).downloadChangelogs(
				repositoryName, repository.getProtocole());
		repository.setChangelogs(changelogs);

		disconnect();
	}

	@Override
	public boolean upLoadEvents(String repositoryName)
			throws RepositoryException, FtpException, ConnectException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		ftpDAOPool.get(0).connectToRepository(repository.getName(),
				repository.getRepositoryUploadProtocole());

		boolean response = false;
		try {
			response = ftpDAOPool.get(0).uploadEvents(repository.getEvents(),
					repository.getProtocole());
		} catch (Exception e) {
			e.printStackTrace();
			throw new FtpException(e.getMessage());
		} finally {
			disconnect();
		}
		return response;
	}

	@Override
	public void downloadAddons(String repositoryName,
			List<SyncTreeNodeDTO> listFiles) throws Exception {

		final Repository repository = repositoryDAO.getMap()
				.get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		assert (repository.getSync() != null);
		assert (repository.getServerInfo() != null);

		final String rootDestinationPath = repository
				.getDefaultDownloadLocation();

		downloadFilesStack.addAll(listFiles);
		this.semaphore = 1;
		this.end = false;

		for (final FtpDAO ftpDAO : ftpDAOPool) {
			ftpDAO.addObserverFileDownload(new ObserverFileDownload() {
				@Override
				public void proceed() {
					if (!ftpDAO.isCanceled()) {
						final SyncTreeNodeDTO node = popDownloadFilesStack();
						if (node != null) {
							Thread t = new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										if (aquireSemaphore()) {
											ftpDAO.setAcquiredSemaphore(true);
										}

										ftpDAO.setActiveConnection(true);
										ftpDAO.updateObserverActiveConnection();

										boolean found = downloadAddon(ftpDAO,
												node, rootDestinationPath,
												repository);

										if (!found) {
											String message = "File not found on repository: "
													+ node.getRelativePath();
											addError(new FileNotFoundException(
													message));
										}
									} catch (Exception e) {
										if (!ftpDAO.isCanceled()) {
											addError(e);
										}
									} finally {
										if (ftpDAO.isAcquiredSmaphore()) {
											releaseSemaphore();
											ftpDAO.setAcquiredSemaphore(false);
										}
										ftpDAO.setActiveConnection(false);
										ftpDAO.updateObserverActiveConnection();
										ftpDAO.updateFileDownloadObserver();
									}
								}
							});
							t.start();
						} else {// no more file to download
							if (ftpDAO.isAcquiredSmaphore()) {
								releaseSemaphore();
								ftpDAO.setAcquiredSemaphore(false);
							}
							if (!end) {
								end = true;
								for (FtpDAO ftpDAO : ftpDAOPool) {
									if (ftpDAO.isActiveConnection()) {
										end = false;
										break;
									}
								}
								if (end) {
									if (errors.isEmpty()) {
										ftpDAO.updateObserverEnd();
									} else {
										ftpDAO.updateObserverError(errors);
									}
								} else {
									for (FtpDAO ftpDAO : ftpDAOPool) {
										if (ftpDAO.isActiveConnection()
												&& aquireSemaphore()) {
											ftpDAO.setAcquiredSemaphore(true);
											break;
										}
									}
								}
							}
						}
					}
				}
			});
		}

		for (FtpDAO ftpDAO : ftpDAOPool) {
			if (!downloadFilesStack.isEmpty()) {// nb files < nb connections
				try {
					ftpDAO.connectToRepository(repository.getName(),
							repository.getProtocole());
					ftpDAO.updateFileDownloadObserver();
				} catch (FtpException | ConnectException e) {
					boolean isDowloading = false;
					ftpDAO.setActiveConnection(false);
					for (FtpDAO fDAO : ftpDAOPool) {
						if (fDAO.isActiveConnection()) {
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

	private boolean downloadAddon(final FtpDAO ftpDAO,
			final SyncTreeNodeDTO node, final String rootDestinationPath,
			final Repository repository) throws Exception {

		String destinationPath = null;
		String remotePath = repository.getProtocole().getRemotePath();
		String path = determinePath(node);
		if (node.getDestinationPath() != null) {
			destinationPath = node.getDestinationPath();
			remotePath = remotePath + "/" + path;
		} else {
			destinationPath = rootDestinationPath + "/" + path;
			remotePath = remotePath + "/" + path;
		}
		boolean found = ftpDAO.downloadFile(remotePath, destinationPath, node);
		return found;
	}

	private synchronized void addError(Exception e) {
		errors.add(e);
	}

	private synchronized SyncTreeNodeDTO popDownloadFilesStack() {

		if (downloadFilesStack.isEmpty()) {
			return null;
		} else {
			return downloadFilesStack.pop();
		}
	}

	private synchronized boolean aquireSemaphore() {

		if (this.semaphore == 1) {
			this.semaphore = 0;
			return true;
		} else {
			return false;
		}
	}

	private synchronized void releaseSemaphore() {
		semaphore = 1;
	}

	@Override
	public void determineCompletion(String repositoryName,
			SyncTreeDirectoryDTO parent) throws RepositoryException,
			HttpException, WritingException {

		for (SyncTreeNodeDTO node : parent.getList()) {
			if (node.isLeaf()) {
				SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
				if (leaf.isUpdated()) {
					leaf.setComplete(0);
				} else {
					leaf.setComplete(100);
				}
			} else {
				SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) node;
				determineCompletion(repositoryName, directory);
			}
		}
	}

	private String determinePath(SyncTreeNodeDTO syncTreeNodeDTO) {

		assert (syncTreeNodeDTO.getParent() != null);
		String path = "";
		while (syncTreeNodeDTO.getParent().getName() != "racine") {
			path = syncTreeNodeDTO.getParent().getName() + "/" + path;
			syncTreeNodeDTO = syncTreeNodeDTO.getParent();
		}
		return path;
	}

	public String checkForUpdate(boolean devMode) throws FtpException {

		String response = null;
		try {
			String updateVersionName = ftpDAOPool.get(0).downloadXMLupdateFile(
					devMode);
			disconnect();
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
			throw new FtpException("Failed to connect to updates repository.");
		} finally {
			disconnect();
		}
		return response;
	}

	@Override
	public boolean remoteFileExists(String repositoryName, SyncTreeNodeDTO node)
			throws RepositoryException, ConnectException, FtpException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		ftpDAOPool.get(0).connectToRepository(repository.getName(),
				repository.getRepositoryUploadProtocole());
		String remotePath = repository.getRepositoryUploadProtocole()
				.getRemotePath();

		try {
			return ftpDAOPool.get(0).fileExists(remotePath, node);
		} catch (IOException e) {
			if (!ftpDAOPool.get(0).isCanceled()) {
				e.printStackTrace();
				throw new FtpException(e.getMessage());
			}
		} finally {
			disconnect();
		}
		return false;
	}

	// public List<String> fileExists(String repositoryName,
	// Collection<SyncTreeNodeDTO> nodes) throws RepositoryException,
	// ConnectException, FtpException {
	//
	// List<String> list = new ArrayList<String>();
	//
	// Repository repository = repositoryDAO.getMap().get(repositoryName);
	// if (repository == null) {
	// throw new RepositoryException("Repository " + repositoryName
	// + " not found!");
	// }
	//
	// String baseRemotePath = ftpDAO.connectToRepository(repository);
	//
	// try {
	// for (SyncTreeNodeDTO node : nodes) {
	// String remotePath = baseRemotePath + "/"
	// + node.getParent().getRelativePath();
	// boolean found = ftpDAO.fileExists(node.getName(),
	// node.isLeaf(), remotePath);
	// if (found) {
	// list.add(node.getRelativePath());
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new FtpException(e.getMessage());
	// }
	// return list;
	// }

	// public List<FTPFile> getFiles(String repositoryName)
	// throws RepositoryException, ConnectException, FtpException {
	//
	// List<FTPFile> list = new ArrayList<FTPFile>();
	//
	// Repository repository = repositoryDAO.getMap().get(repositoryName);
	// if (repository == null) {
	// throw new RepositoryException("Repository " + repositoryName
	// + " not found!");
	// }
	//
	// String remotePath = ftpDAO.connectToRepository(repository);
	//
	// try {
	// list.addAll(ftpDAO.getFiles(remotePath));
	// } catch (IOException e) {
	// e.printStackTrace();
	// throw new FtpException(e.getMessage());
	// }
	// return list;
	// }

	@Override
	public void getSyncWithRepositoryUploadProtocole(String repositoryName)
			throws RepositoryException, WritingException, ConnectException,
			FtpException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		ftpDAOPool.get(0).connectToRepository(repository.getName(),
				repository.getRepositoryUploadProtocole());

		SyncTreeDirectory syncTreeDirectory = ftpDAOPool.get(0).downloadSync(
				repositoryName, repository.getProtocole());
		repository.setSync(syncTreeDirectory);

		disconnect();
	}

	@Override
	public void uploadRepository(String repositoryName,
			List<SyncTreeNodeDTO> filesToUpload,
			List<SyncTreeNodeDTO> filesToDelete, boolean resume)
			throws RepositoryException, ConnectException, FtpException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		ftpDAOPool.get(0).connectToRepository(repository.getName(),
				repository.getRepositoryUploadProtocole());
		String remotePath = repository.getRepositoryUploadProtocole()
				.getRemotePath();

		try {
			for (SyncTreeNodeDTO node : filesToDelete) {
				if (ftpDAOPool.get(0).isCanceled()) {
					return;
				}
				String parentPath = repository.getProtocole() + "/"
						+ node.getParent().getRelativePath();
				ftpDAOPool.get(0).deleteFile(node.getName(), node.isLeaf(),
						parentPath);
			}

			for (SyncTreeNodeDTO node : filesToUpload) {
				if (ftpDAOPool.get(0).isCanceled()) {
					return;
				}

				String sourceFilePath = repository.getPath() + "/"
						+ node.getRelativePath();
				File sourceFile = new File(sourceFilePath);
				if (!sourceFile.exists()) {
					throw new RepositoryException(
							"Can't find file on local repository "
									+ sourceFile.getAbsolutePath());
				}

				ftpDAOPool.get(0).uploadFile(sourceFile, remotePath, node,
						resume);

				resume = false;

				// Add ZSync file
				if (repository.getProtocole() instanceof Http && node.isLeaf()) {
					String zsyncFilePath = repository.getPath() + "/"
							+ node.getRelativePath() + ZSYNC_EXTENSION;
					File zsyncFile = new File(zsyncFilePath);
					if (!zsyncFile.exists()) {
						throw new RepositoryException(
								"Can't find file on local repository "
										+ zsyncFile.getAbsolutePath());
					}
					ftpDAOPool.get(0).uploadSingleFile(zsyncFile, remotePath,
							node, resume);
				}
			}

			ftpDAOPool.get(0).uploadSync(repository.getLocalSync(), remotePath);
			ftpDAOPool.get(0).uploadServerInfo(repository.getLocalServerInfo(),
					remotePath);
			ftpDAOPool.get(0).uploadChangelogs(repository.getLocalChangelogs(),
					remotePath);
			ftpDAOPool.get(0).uploadAutoconfig(repository.getLocalAutoConfig(),
					remotePath);
			ftpDAOPool.get(0).uploadEvents(repository.getLocalEvents(),
					remotePath);

		} catch (IOException e) {
			if (!ftpDAOPool.get(0).isCanceled()) {
				e.printStackTrace();
				throw new FtpException(e.getMessage());
			}
		} finally {
			ftpDAOPool.get(0).disconnect();
		}
	}

	@Override
	public void cancel(boolean resumable) {
		for (FtpDAO ftpDAO : ftpDAOPool) {
			ftpDAO.cancel(resumable);
		}
	}

	@Override
	public void disconnect() {
		for (FtpDAO ftpDAO : ftpDAOPool) {
			ftpDAO.disconnect();
		}
	}

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
}
