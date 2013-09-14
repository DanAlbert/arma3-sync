package fr.soe.a3s.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

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
import fr.soe.a3s.dto.FtpDTO;
import fr.soe.a3s.dto.ServerInfoDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.FtpException;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.main.Version;

public class FtpService implements DataAccessConstants {

	private final FtpDAO ftpDAO = new FtpDAO();
	private static final RepositoryDAO repositoryDAO = new RepositoryDAO();
	private FTPClient ftpClient;
	private boolean canceled = false;

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
				ftpClientUpdate.disconnect();
				throw new FtpException(
						"Failed to connect to updates repository.");
			}

			String updateVersionName = ftpDAO.downloadXMLupdateFile(
					ftpClientUpdate, devMode);

			ftpClientUpdate.disconnect();

			if (updateVersionName != null) {
				System.out.println("Update version = " + updateVersionName);

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
		}
		return response;
	}

	public void checkRepository(String repositoryName) throws FtpException,
			RepositoryException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}
		String remotePath = null;
		try {
			ftpClient = new FTPClient();
			remotePath = connectToRepository(ftpClient, repository);
		} catch (Exception e) {
			throw new FtpException("Failed to connect to repository "
					+ repository.getName());
		}

		try {
			SyncTreeDirectory syncTreeDirectory = ftpDAO.downloadSync(
					ftpClient, repositoryName, remotePath);
			repository.setSync(syncTreeDirectory);
			ServerInfo serverInfo = ftpDAO.downloadSeverInfo(ftpClient,
					repositoryName, remotePath);
			repository.setServerInfo(serverInfo);// null if not found
			Changelogs changelogs = ftpDAO.downloadChangelog(ftpClient,
					repositoryName, remotePath);
			repository.setChangelogs(changelogs);
			Events events = ftpDAO.downloadEvent(ftpClient, repositoryName,
					remotePath);
			repository.setEvents(events);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AutoConfigDTO importAutoConfig(String url) throws CheckException,
			FtpException {

		FTPClient ftpClient = new FTPClient();
		int index1 = url.indexOf("/");
		if (index1 == -1) {
			throw new CheckException("Auto-config url is invalid.");
		}
		String hostname = url.substring(0, index1);

		int index2 = url.lastIndexOf("/");
		String remotePath = url.substring(index1, index2);

		if (!url.substring(index2 + 1).equals("autoconfig")) {
			throw new CheckException("Auto-config url is invalid.");
		}

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
			} else {
				System.out.println("Connection failed.");
				ftpClient.disconnect();
				throw new FtpException("Failed to connect to repository.");
			}
			AutoConfig autoConfig = ftpDAO.downloadAutoConfig(ftpClient,
					remotePath);
			if (autoConfig != null) {
				autoConfigDTO = transformAutoConfig2DTO(autoConfig);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new FtpException("Failed to connect to repository.");
		}
		return autoConfigDTO;
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

	public void getSync(String repositoryName) throws RepositoryException,
			FtpException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}
		try {
			FTPClient ftpClient = new FTPClient();
			String remotePath = connectToRepository(ftpClient, repository);
			SyncTreeDirectory syncTreeDirectory = ftpDAO.downloadSync(
					ftpClient, repositoryName, remotePath);
			repository.setSync(syncTreeDirectory);
			ftpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			throw new FtpException("Failed to connect to repository.");
		}
	}

	// public void getChangelogs(String repositoryName)
	// throws RepositoryException, FtpException {
	//
	// Repository repository = repositoryDAO.getMap().get(repositoryName);
	// if (repository == null) {
	// throw new RepositoryException("Repository " + repositoryName
	// + " not found!");
	// }
	// try {
	// FTPClient ftpClient = new FTPClient();
	// String remotePath = connectToRepository(ftpClient, repository);
	// Changelogs changelogs = ftpDAO.downloadChangelog(ftpClient,
	// repositoryName, remotePath);
	// repository.setChangelogs(changelogs);
	// ftpClient.disconnect();
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new FtpException("Failed to connect to repository.");
	// }
	// }

	public boolean upLoadEvents(String repositoryName)
			throws RepositoryException, FtpException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		boolean response = false;
		try {
			FTPClient ftpClient = new FTPClient();
			String remotePath = connectToRepository(ftpClient, repository);
			response = ftpDAO.uploadEvents(ftpClient, repository.getEvents(),
					remotePath);
			ftpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			throw new FtpException(e.getMessage());
		}
		return response;
	}

	private String connectToRepository(FTPClient ftpClient,
			Repository repository) throws FtpException, NumberFormatException,
			SocketException, IOException {

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
		ftpClient.setConnectTimeout(15000);// 15 sec
		ftpClient.connect(hostname, Integer.parseInt(port));
		ftpClient.login(login, password);
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		ftpClient.enterLocalPassiveMode();// passive mode
		int reply = ftpClient.getReplyCode();
		if (FTPReply.isPositiveCompletion(reply)) {
			System.out.println("Connection to " + repository.getName()
					+ " success.");
		} else {
			System.out.println("Connection to " + repository.getName()
					+ " failed.");
			ftpClient.disconnect();
			throw new FtpException("Failed to connect to repository "
					+ repository.getName());
		}
		return remotePath;
	}

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

		ftpClient = null;
		String rootRemotePath = null;
		try {
			ftpClient = new FTPClient();
			rootRemotePath = connectToRepository(ftpClient, repository);
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

				if (canceled) {
					break;
				}

				boolean found = ftpDAO.downloadAddon(ftpClient, remotePath,
						destinationPath, node.getName(), node.isLeaf(), resume);

				resume = false;

				if (!found) {
					throw new FileNotFoundException(
							"File not found on repository : " + node.getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WritingException(e.getMessage());
		}

		try {
			ftpClient.disconnect();
		} catch (IOException e) {
		}
	}

	private String determinePath(SyncTreeNodeDTO syncTreeNodeDTO) {

		String path = "";
		while (syncTreeNodeDTO.getParent() != null
				&& syncTreeNodeDTO.getParent().getName() != "racine") {
			path = syncTreeNodeDTO.getParent().getName() + "/" + path;
			syncTreeNodeDTO = syncTreeNodeDTO.getParent();
		}
		// if (syncTreeNodeDTO.getParent() != null
		// && syncTreeNodeDTO.getParent().getName() != "racine"){
		// path = syncTreeNodeDTO.getParent().getName();
		// }
		return path;
	}

	public void stopDownload() {
		if (ftpClient != null) {
			try {
				ftpClient.abort();
				canceled = true;
			} catch (IOException e) {
			}
		}
	}

	public long pauseDownlaod() {
		long offset = 0;
		if (ftpClient != null) {
			try {
				offset = ftpClient.getRestartOffset();
				ftpClient.abort();
				canceled = true;
			} catch (IOException e) {
			}
		}
		return offset;
	}

	public void disconnect() {
		if (ftpClient != null) {
			try {
				ftpClient.abort();
				canceled = true;
			} catch (IOException e) {
			}
		}
	}

	public FtpDAO getFtpDAO() {
		return ftpDAO;
	}

	private AutoConfigDTO transformAutoConfig2DTO(AutoConfig autoConfig) {

		final AutoConfigDTO autoConfigDTO = new AutoConfigDTO();
		autoConfigDTO.setRepositoryName(autoConfig.getRepositoryName());
		FtpDTO ftpDTO = new FtpDTO();
		autoConfigDTO.setFtpDTO(ftpDTO);
		ftpDTO.setUrl(autoConfig.getProtocole().getUrl());
		ftpDTO.setPort(autoConfig.getProtocole().getPort());
		ftpDTO.setPassword(autoConfig.getProtocole().getPassword());
		ftpDTO.setLogin(autoConfig.getProtocole().getLogin());
		ftpDTO.setEncryptionMode(autoConfig.getProtocole().getEncryptionMode());
		return autoConfigDTO;
	}

	private ServerInfoDTO transformServerInfo2DTO(ServerInfo serverInfo) {

		final ServerInfoDTO serverInfoDTO = new ServerInfoDTO();
		serverInfoDTO.setBuildDate(serverInfo.getBuildDate());
		serverInfoDTO.setNumberOfFiles(serverInfo.getNumberOfFiles());
		serverInfoDTO.setRevision(serverInfo.getRevision());
		serverInfoDTO.setTotalFilesSize(serverInfo.getTotalFilesSize());
		return serverInfoDTO;
	}

	private ServerInfo transformDTO2ServerInfo(ServerInfoDTO serverInfoDTO) {

		final ServerInfo serverInfo = new ServerInfo();
		serverInfo.setBuildDate(serverInfoDTO.getBuildDate());
		serverInfo.setNumberOfFiles(serverInfoDTO.getNumberOfFiles());
		serverInfo.setRevision(serverInfoDTO.getRevision());
		serverInfo.setTotalFilesSize(serverInfoDTO.getTotalFilesSize());
		return serverInfo;
	}
}
