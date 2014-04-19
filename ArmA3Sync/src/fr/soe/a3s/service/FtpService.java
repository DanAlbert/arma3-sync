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

	@Override
	public AutoConfigDTO importAutoConfig(String url) throws FtpException,
			WritingException {

		AutoConfig autoConfig = ftpDAO.downloadAutoConfig(url);
		disconnect();
		if (autoConfig != null) {
			return transformAutoConfig2DTO(autoConfig);
		} else {
			return null;
		}
	}

	@Override
	public void checkRepository(String repositoryName) throws FtpException,
			RepositoryException, WritingException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		String remotePath = ftpDAO.connectToRepository(repository);

		SyncTreeDirectory syncTreeDirectory = ftpDAO.downloadSync(
				repositoryName, remotePath);
		repository.setSync(syncTreeDirectory);// null if not found
		ServerInfo serverInfo = ftpDAO.downloadSeverInfo(repositoryName,
				remotePath);
		repository.setServerInfo(serverInfo);// null if not found
		Changelogs changelogs = ftpDAO.downloadChangelog(repositoryName,
				remotePath);
		repository.setChangelogs(changelogs);// null if not found
		Events events = ftpDAO.downloadEvent(repositoryName, remotePath);
		repository.setEvents(events);

		disconnect();
	}

	@Override
	public void getSync(String repositoryName) throws RepositoryException,
			FtpException, WritingException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		String remotePath = ftpDAO.connectToRepository(repository);

		SyncTreeDirectory syncTreeDirectory = ftpDAO.downloadSync(
				repositoryName, remotePath);
		repository.setSync(syncTreeDirectory);

		disconnect();
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
			String remotePath = ftpDAO.connectToRepository(repository);
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
			throws RepositoryException, FtpException, WritingException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		assert (repository.getSync() != null);
		assert (repository.getServerInfo() != null);

		String rootRemotePath = ftpDAO.connectToRepository(repository);
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
			String updateVersionName = ftpDAO.downloadXMLupdateFile(devMode);
			disconnect();
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
		ftpDAO.disconnect();
	}

	@Override
	public FtpDAO getConnexionDAO() {
		return ftpDAO;
	}
}
