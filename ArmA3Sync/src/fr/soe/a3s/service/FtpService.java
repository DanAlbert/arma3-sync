package fr.soe.a3s.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import fr.soe.a3s.constant.Protocole;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.FtpDAO;
import fr.soe.a3s.dao.RepositoryDAO;
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
	private FTPClient ftpClient;

	@Override
	public AutoConfigDTO importAutoConfig(String url) {

		ftpClient = new FTPClient();
		String address = url.replace(Protocole.FTP.getPrompt(), "");
		int index1 = address.indexOf("/");
		String hostname = address.substring(0, index1);
		int index2 = address.lastIndexOf("/");
		String remotePath = address.substring(index1, index2);

		String port = "21";
		String login = "anonymous";
		String password = "";

		AutoConfigDTO autoConfigDTO = null;

		try {
			ftpClient.setConnectTimeout(5000);// 5 sec
			ftpClient.connect(hostname, Integer.parseInt(port));
			ftpClient.login(login, password);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();// passive mode
			int reply = ftpClient.getReplyCode();
			if (FTPReply.isPositiveCompletion(reply)) {
				System.out.println("Connection success.");
				AutoConfig autoConfig = ftpDAO.downloadAutoConfig(ftpClient,
						remotePath);
				if (autoConfig != null) {
					autoConfigDTO = transformAutoConfig2DTO(autoConfig);
				}
			} else {
				System.out.println("Connection failed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
		return autoConfigDTO;
	}

	@Override
	public void checkRepository(String repositoryName) throws FtpException,
			RepositoryException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		String remotePath = connectToRepository(repository);

		SyncTreeDirectory syncTreeDirectory = ftpDAO.downloadSync(ftpClient,
				repositoryName, remotePath);
		repository.setSync(syncTreeDirectory);// null if not found
		ServerInfo serverInfo = ftpDAO.downloadSeverInfo(ftpClient,
				repositoryName, remotePath);
		repository.setServerInfo(serverInfo);// null if not found
		Changelogs changelogs = ftpDAO.downloadChangelog(ftpClient,
				repositoryName, remotePath);
		repository.setChangelogs(changelogs);// null if not found
		Events events = ftpDAO.downloadEvent(ftpClient, repositoryName,
				remotePath);
		repository.setEvents(events);

		disconnect();
	}

	private String connectToRepository(Repository repository)
			throws FtpException {

		String url = repository.getProtocole().getUrl();
		String hostname = url;
		String remotePath = "";
		int index = url.indexOf("/");
		if (index != -1) {
			hostname = url.substring(0, index);
			remotePath = url.substring(index);
		}
		String port = repository.getProtocole().getPort();
		String login = repository.getProtocole().getLogin();
		String password = repository.getProtocole().getPassword();
		ftpClient = new FTPClient();
		ftpClient.setConnectTimeout(15000);// 15 sec
		ftpClient.setBufferSize(1048576);// 1024*1024
		boolean isLoged = false;
		int reply = 0;
		try {
			ftpClient.connect(hostname, Integer.parseInt(port));
			isLoged = ftpClient.login(login, password);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();// passive mode
			reply = ftpClient.getReplyCode();
		} catch (Exception e) {
			throw new FtpException("Failed to connect to repository "
					+ repository.getName());
		}

		if (!isLoged) {
			System.out.println("Connection to " + repository.getName()
					+ " failed.\nWrong login or password.");
			throw new FtpException("Failed to connect to repository " + "\""
					+ repository.getName() + "\"" + "." + "\n"
					+ "Wrong login or password.");
		}

		if (!FTPReply.isPositiveCompletion(reply)) {
			System.out.println("Connection to " + repository.getName()
					+ " failed.");
			throw new FtpException("Failed to connect to repository "
					+ repository.getName());
		}

		System.out.println("Connection to " + repository.getName()
				+ " success.");

		return remotePath;
	}

	// public void getFiles(String repositoryName) throws FtpException,
	// RepositoryException {
	//
	// Repository repository = repositoryDAO.getMap().get(repositoryName);
	// if (repository == null){
	// throw new RepositoryException("Repository " + repositoryName
	// + " not found!");
	// }
	// try {
	// FTPClient ftpClient = new FTPClient();
	// String remotePath = connectToRepository(ftpClient, repository);
	// FTPFile[] ftpFiles = ftpDAO.getFiles(ftpClient,remotePath);
	// repository.setFtpFiles(ftpFiles.clone());
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new FtpException("Failed to connect to repository.");
	// }
	// }

	@Override
	public void getSync(String repositoryName) throws RepositoryException,
			FtpException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		String remotePath = connectToRepository(repository);

		SyncTreeDirectory syncTreeDirectory = ftpDAO.downloadSync(ftpClient,
				repositoryName, remotePath);
		repository.setSync(syncTreeDirectory);
	}

	@Override
	public boolean upLoadEvents(String repositoryName)
			throws RepositoryException, FtpException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		boolean response = false;
		try {
			String remotePath = connectToRepository(repository);
			response = ftpDAO.uploadEvents(ftpClient, repository.getEvents(),
					remotePath);
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
			FileNotFoundException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		assert (repository.getSync() != null);
		assert (repository.getServerInfo() != null);

		String rootRemotePath = null;
		try {
			rootRemotePath = connectToRepository(repository);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FtpException("Failed to connect to repository.");
		}

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

				boolean found = ftpDAO.downloadAddon(ftpClient, remotePath,
						destinationPath, node, resume);

				resume = false;

				if (!found) {
					throw new FileNotFoundException(
							"File not found on repository : " + node.getName());
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			throw new WritingException(e.getMessage());
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

	public String checkForUpdate(boolean devMode) {

		String response = null;
		try {
			FTPClient ftpClientUpdate = new FTPClient();
			ftpClientUpdate.connect(UPDTATE_REPOSITORY_ADRESS,
					UPDTATE_REPOSITORY_PORT);
			ftpClientUpdate.login(UPDTATE_REPOSITORY_LOGIN,
					UPDTATE_REPOSITORY_PASS);
			ftpClientUpdate.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClientUpdate.enterLocalPassiveMode();// passive mode
			int reply = ftpClientUpdate.getReplyCode();
			if (FTPReply.isPositiveCompletion(reply)) {
				System.out.println("Connection updates repository Success");
			} else {
				System.out.println("Connection Failed");
				throw new FtpException(
						"Failed to connect to updates repository.");
			}

			String updateVersionName = ftpDAO.downloadXMLupdateFile(
					ftpClientUpdate, devMode);

			ftpClientUpdate.disconnect();

			if (updateVersionName != null) {
				System.out.println("Available update version = "
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
			new FtpException("Failed to connect to updates repository.");
		} finally {
			disconnect();
		}
		return response;
	}

	@Override
	public void stopDownload(boolean resumable) {
		disconnect();
		ftpDAO.setCanceled(true);
	}

	@Override
	public void disconnect() {
		if (ftpClient != null) {
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public FtpDAO getConnexionDAO() {
		return ftpDAO;
	}
}
