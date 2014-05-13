package fr.soe.a3s.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.output.CountingOutputStream;

import fr.soe.a3s.constant.Protocole;
import fr.soe.a3s.dao.ConfigurationDAO;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.HttpDAO;
import fr.soe.a3s.dao.RepositoryDAO;
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
import fr.soe.a3s.exception.JazsyncException;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.exception.WritingException;

public class HttpService extends AbstractConnexionService implements
		DataAccessConstants {

	private final HttpDAO httpDAO = new HttpDAO();
	private static final RepositoryDAO repositoryDAO = new RepositoryDAO();
	private static final ConfigurationDAO configurationDAO = new ConfigurationDAO();

	@Override
	public AutoConfigDTO importAutoConfig(String url) throws WritingException,
			HttpException, ConnectException {

		AutoConfig autoConfig = httpDAO.downloadAutoConfig(url);
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
	public void checkRepository(String repositoryName)
			throws RepositoryException, WritingException, ConnectException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		try {
			SyncTreeDirectory syncTreeDirectory = httpDAO
					.downloadSync(repository);
			repository.setSync(syncTreeDirectory);// null if not found
			ServerInfo serverInfo = httpDAO.downloadSeverInfo(repository);
			repository.setServerInfo(serverInfo);// null if not found
			if (serverInfo != null) {
				repository.getHiddenFolderPath().addAll(
						serverInfo.getHiddenFolderPaths());
			}
			Changelogs changelogs = httpDAO.downloadChangelogs(repository);
			repository.setChangelogs(changelogs);// null if not found
			Events events = httpDAO.downloadEvent(repository);
			repository.setEvents(events);// null if not found
		} catch (HttpException e) {
			// error http 404 may happen if repository has not been built so far
		}
	}

	@Override
	public void getSync(String repositoryName) throws RepositoryException,
			HttpException, WritingException, ConnectException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		SyncTreeDirectory syncTreeDirectory = httpDAO.downloadSync(repository);
		repository.setSync(syncTreeDirectory);// null if not found
	}

	@Override
	public void getServerInfo(String repositoryName)
			throws RepositoryException, ConnectException, WritingException,
			HttpException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		ServerInfo serverInfo = httpDAO.downloadSeverInfo(repository);
		repository.setServerInfo(serverInfo);// null if not found
	}

	@Override
	public void getChangelogs(String repositoryName) throws ConnectException,
			RepositoryException, WritingException, HttpException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		Changelogs changelogs = httpDAO.downloadChangelogs(repository);
		repository.setChangelogs(changelogs);// null if not found
	}

	@Override
	public void downloadAddons(String repositoryName,
			List<SyncTreeNodeDTO> listFiles, boolean resume)
			throws RepositoryException, HttpException, WritingException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		assert (repository.getSync() != null);
		assert (repository.getServerInfo() != null);
		String url = repository.getProtocole().getUrl();
		String hostname = url;
		String rootRemotePath = "";
		int index = url.indexOf("/");
		if (index != -1) {
			hostname = url.substring(0, index);
			rootRemotePath = url.substring(index);
		}
		String port = repository.getProtocole().getPort();
		String login = repository.getProtocole().getLogin();
		String password = repository.getProtocole().getPassword();

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

				if (httpDAO.isCanceled()) {
					break;
				}

				httpDAO.downloadFile(hostname, login, password, port,
						remotePath, destinationPath, node, resume);

				resume = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WritingException(e.getMessage()
					+ "\n Download have been interrupted.");
		}
	}

	public void determineCompletion(String repositoryName,
			SyncTreeDirectoryDTO parent) throws RepositoryException,
			HttpException, WritingException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		assert (repository.getSync() != null);
		assert (repository.getServerInfo() != null);
		String url = repository.getProtocole().getUrl();
		String hostname = url;
		String rootRemotePath = "";
		int index = url.indexOf("/");
		if (index != -1) {
			hostname = url.substring(0, index);
			rootRemotePath = url.substring(index);
		}
		String port = repository.getProtocole().getPort();
		String login = repository.getProtocole().getLogin();
		String password = repository.getProtocole().getPassword();

		String rootDestinationPath = repository.getDefaultDownloadLocation();

		for (SyncTreeNodeDTO node : parent.getList()) {
			if (node.isLeaf()) {
				SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
				try {
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

					if (httpDAO.isCanceled()) {
						break;
					}

					httpDAO.getFileCompletion(hostname, login, password, port,
							remotePath, destinationPath, node);
				} catch (Exception e) {
					e.printStackTrace();
					throw new WritingException(
							"An unexpected error has occured.\n Internal error.");
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

	@Override
	public void stopDownload(boolean resumable) {
		httpDAO.stopDownload(resumable);
	}

	public void disconnect() {
		httpDAO.disconnect();
	}

	@Override
	public HttpDAO getConnexionDAO() {
		return httpDAO;
	}

	@Override
	public boolean upLoadEvents(String repositoryName)
			throws RepositoryException, HttpException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		boolean response = httpDAO.uploadEvents(repository);
		return response;
	}
}
