package fr.soe.a3s.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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

	private final FtpDAO ftpDAO = new FtpDAO();
	private static final RepositoryDAO repositoryDAO = new RepositoryDAO();
	private static final ConfigurationDAO configurationDAO = new ConfigurationDAO();

	@Override
	public AutoConfigDTO importAutoConfig(String url) throws FtpException,
			WritingException, ConnectException {

		AutoConfig autoConfig = ftpDAO.downloadAutoConfig(url);
		disconnect();
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

		String remotePath = ftpDAO.connectToRepository(repository.getName(),
				repository.getProtocole());

		SyncTreeDirectory syncTreeDirectory = ftpDAO.downloadSync(
				repositoryName, remotePath);
		repository.setSync(syncTreeDirectory);// null if not found
		ServerInfo serverInfo = ftpDAO.downloadSeverInfo(repositoryName,
				remotePath);
		repository.setServerInfo(serverInfo);// null if not found
		if (serverInfo != null) {
			repository.getHiddenFolderPath().addAll(
					serverInfo.getHiddenFolderPaths());
		}
		Changelogs changelogs = ftpDAO.downloadChangelogs(repositoryName,
				remotePath);
		repository.setChangelogs(changelogs);// null if not found
		Events events = ftpDAO.downloadEvent(repositoryName, remotePath);
		repository.setEvents(events);
		AutoConfig autoConfig = ftpDAO.downloadAutoconfig(repositoryName,
				remotePath);
		repository.setAutoConfig(autoConfig);
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

		String remotePath = ftpDAO.connectToRepository(repository.getName(),
				repository.getProtocole());

		SyncTreeDirectory syncTreeDirectory = ftpDAO.downloadSync(
				repositoryName, remotePath);
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

		String remotePath = ftpDAO.connectToRepository(repository.getName(),
				repository.getProtocole());

		ServerInfo serverInfo = ftpDAO.downloadSeverInfo(repositoryName,
				remotePath);
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

		String remotePath = ftpDAO.connectToRepository(repository.getName(),
				repository.getProtocole());

		Changelogs changelogs = ftpDAO.downloadChangelogs(repositoryName,
				remotePath);
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

		String remotePath = ftpDAO.connectToRepository(repository.getName(),
				repository.getRepositoryUploadProtocole());

		boolean response = false;
		try {
			response = ftpDAO.uploadEvents(repository.getEvents(), remotePath);
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
			List<SyncTreeNodeDTO> listFiles, boolean resume)
			throws RepositoryException, FtpException, WritingException,
			ConnectException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		assert (repository.getSync() != null);
		assert (repository.getServerInfo() != null);

		String rootRemotePath = ftpDAO.connectToRepository(
				repository.getName(), repository.getProtocole());
		String rootDestinationPath = repository.getDefaultDownloadLocation();
		try {
			for (SyncTreeNodeDTO node : listFiles) {
				String destinationPath = null;
				String remotePath = rootRemotePath;
				String path = determinePath(node);
				if (node.getDestinationPath() != null) {
					destinationPath = node.getDestinationPath();
					remotePath = remotePath + "/" + path;
				} else {
					destinationPath = rootDestinationPath + "/" + path;
					remotePath = remotePath + "/" + path;
				}

				if (ftpDAO.isCanceled()) {
					break;
				}

				boolean found = ftpDAO.downloadFile(remotePath,
						destinationPath, node, resume);

				resume = false;

				if (!found) {
					throw new FileNotFoundException(
							"File not found on repository : " + node.getName());
				}
			}
		} catch (Exception e) {
			if (!ftpDAO.isCanceled()) {
				e.printStackTrace();
				throw new WritingException(e.getMessage());
			}
		} finally {
			disconnect();
		}
	}

	@Override
	public void determineCompletion(String repositoryName,
			SyncTreeDirectoryDTO parent) throws RepositoryException,
			HttpException, WritingException {

		for (SyncTreeNodeDTO node : parent.getList()) {
			if (node.isLeaf()) {
				SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
				leaf.setComplete(0);
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
			String updateVersionName = ftpDAO.downloadXMLupdateFile(devMode);
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

		String remotePath = ftpDAO.connectToRepository(repository.getName(),
				repository.getRepositoryUploadProtocole());

		try {
			return ftpDAO.fileExists(remotePath, node);
		} catch (IOException e) {
			if (!ftpDAO.isCanceled()) {
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

		String remotePath = ftpDAO.connectToRepository(repository.getName(),
				repository.getRepositoryUploadProtocole());

		SyncTreeDirectory syncTreeDirectory = ftpDAO.downloadSync(
				repositoryName, remotePath);
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

		String remotePath = ftpDAO.connectToRepository(repository.getName(),
				repository.getRepositoryUploadProtocole());

		try {
			for (SyncTreeNodeDTO node : filesToDelete) {
				if (ftpDAO.isCanceled()) {
					return;
				}
				String parentPath = remotePath + "/"
						+ node.getParent().getRelativePath();
				ftpDAO.deleteFile(node.getName(), node.isLeaf(), parentPath);
			}

			for (SyncTreeNodeDTO node : filesToUpload) {
				if (ftpDAO.isCanceled()) {
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

				ftpDAO.uploadFile(sourceFile, remotePath, node, resume);

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
					ftpDAO.uploadSingleFile(zsyncFile, remotePath, node, resume);
				}
			}

			ftpDAO.uploadSync(repository.getLocalSync(), remotePath);
			ftpDAO.uploadServerInfo(repository.getLocalServerInfo(), remotePath);
			ftpDAO.uploadChangelogs(repository.getLocalChangelogs(), remotePath);
			ftpDAO.uploadAutoconfig(repository.getLocalAutoConfig(), remotePath);

		} catch (IOException e) {
			if (!ftpDAO.isCanceled()) {
				e.printStackTrace();
				throw new FtpException(e.getMessage());
			}
		} finally {
			ftpDAO.disconnect();
		}
	}

	@Override
	public void cancel(boolean resumable) {
		ftpDAO.cancel(resumable);
	}

	@Override
	public void disconnect() {
		ftpDAO.disconnect();
	}

	@Override
	public FtpDAO getConnexionDAO() {
		return ftpDAO;
	}
}
