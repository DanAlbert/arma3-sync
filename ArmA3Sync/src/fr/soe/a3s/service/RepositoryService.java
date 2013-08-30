package fr.soe.a3s.service;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import fr.soe.a3s.constant.EncryptionMode;
import fr.soe.a3s.dao.AddonDAO;
import fr.soe.a3s.dao.RepositoryBuilderDAO;
import fr.soe.a3s.dao.RepositoryDAO;
import fr.soe.a3s.domain.Addon;
import fr.soe.a3s.domain.Ftp;
import fr.soe.a3s.domain.TreeDirectory;
import fr.soe.a3s.domain.TreeLeaf;
import fr.soe.a3s.domain.TreeNode;
import fr.soe.a3s.domain.repository.Changelog;
import fr.soe.a3s.domain.repository.Changelogs;
import fr.soe.a3s.domain.repository.Event;
import fr.soe.a3s.domain.repository.Events;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.domain.repository.SyncTreeLeaf;
import fr.soe.a3s.domain.repository.SyncTreeNode;
import fr.soe.a3s.dto.ChangelogDTO;
import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.dto.FtpDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.dto.ServerInfoDTO;
import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.TreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.RepositoryCheckException;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.exception.ServerInfoNotFoundException;
import fr.soe.a3s.exception.SyncFileNotFoundException;
import fr.soe.a3s.exception.WritingException;

public class RepositoryService {

	private static final RepositoryDAO repositoryDAO = new RepositoryDAO();
	private RepositoryBuilderDAO repositoryBuilderDAO = new RepositoryBuilderDAO();
	private static final AddonDAO addonDAO = new AddonDAO();

	private static final byte[] secreteKey = new byte[] { 0x01, 0x72, 0x43,
			0x3E, 0x1C, 0x7A, 0x55, 0, 0x01, 0x72, 0x43, 0x3E, 0x1C, 0x7A,
			0x55, 0x4F };

	public void readAll() throws LoadingException {

		try {
			Cipher cipher = getDecryptionCipher();
			repositoryDAO.readAll(cipher);
		} catch (Exception e) {
			e.printStackTrace();
			throw new LoadingException();
		}
	}

	public void write(String repositoryName) throws WritingException {

		try {
			Cipher cipher = getEncryptionCipher();
			repositoryDAO.write(cipher, repositoryName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WritingException("Failed to write repository.");
		}
	}

	public void createRepository(String name, String url, String port,
			String login, String password, EncryptionMode encryptionMode)
			throws CheckException {

		if (name == null || "".equals(name)) {
			throw new CheckException("Repository name can't be empty");
		}

		Ftp ftp = new Ftp(url, port, login, password, encryptionMode);
		ftp.checkData();

		if (repositoryDAO.getMap().containsKey(name)) {
			throw new CheckException("Repository with name " + name
					+ " already exists.");
		}

		Repository repository = new Repository(name, ftp);
		repositoryDAO.getMap().put(repository.getName(), repository);
	}

	public void removeRepository(String repositoryName) {
		repositoryDAO.remove(repositoryName);
		repositoryDAO.getMap().remove(repositoryName);
	}

	public List<RepositoryDTO> getRepositories() {

		List<RepositoryDTO> repositoryDTOs = new ArrayList<RepositoryDTO>();
		for (Iterator<String> i = repositoryDAO.getMap().keySet().iterator(); i
				.hasNext();) {
			Repository repository = repositoryDAO.getMap().get(i.next());
			RepositoryDTO repositoryDTO = transformRepository2DTO(repository);
			repositoryDTOs.add(repositoryDTO);
		}
		return repositoryDTOs;
	}

	public RepositoryDTO getRepository(String repositoryName)
			throws RepositoryException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			RepositoryDTO repositoryDTO = transformRepository2DTO(repository);
			return repositoryDTO;
		} else {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}
	}

	public void setRepositoryPath(String repositoryName, String repositoryPath)
			throws RepositoryException {
		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setPath(repositoryPath);
		} else {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}
	}

	public void setAutoConfigURL(String repositoryName, String autoConfigURL)
			throws RepositoryException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setAutoConfigURL(autoConfigURL);
		} else {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

	}

	public ServerInfoDTO getServerInfo(String repositoryName)
			throws RepositoryException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			ServerInfo serverInfo = repository.getServerInfo();
			if (serverInfo != null) {
				ServerInfoDTO serverInfoDTO = transformServerInfo2DTO(serverInfo);
				return serverInfoDTO;
			} else {
				return null;
			}
		} else {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}
	}

	public List<ChangelogDTO> getChangelogs(String repositoryName)
			throws RepositoryException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			Changelogs changelogs = repository.getChangelogs();
			if (changelogs != null) {
				List<Changelog> list = changelogs.getList();
				List<ChangelogDTO> changelogDTOs = new ArrayList<ChangelogDTO>();
				for (Changelog changelog : list) {
					ChangelogDTO changelogDTO = transformChangelog2DTO(changelog);
					changelogDTOs.add(changelogDTO);
				}
				return changelogDTOs;
			} else {
				return null;
			}
		} else {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}
	}

	public void buildRepository(String repositoryName, String path)
			throws RepositoryException, WritingException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}
		repository.setPath(path);
		repositoryBuilderDAO.buildRepository(repository);
	}

	public SyncTreeDirectoryDTO getSync(String repositoryName) throws Exception {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		if (repository.getServerInfo() == null) {
			throw new ServerInfoNotFoundException();
		}

		if (repository.getSync() == null) {
			throw new SyncFileNotFoundException();
		}

		SyncTreeDirectory parent = repository.getSync();

		determineDestinationPaths(parent,
				repository.getDefaultDownloadLocation());
		determineAddonFilesToDelete(parent);
		determineAddonFoldersToDelete(parent);

		if (repository.getServerInfo().getNumberOfFiles() > 0) {
			repositoryBuilderDAO.determineLocalSHA1(parent, repository
					.getServerInfo().getNumberOfFiles());
		}

		SyncTreeDirectoryDTO parentDTO = new SyncTreeDirectoryDTO();
		parentDTO.setName("racine");
		parentDTO.setParent(null);
		transformSyncTreeDirectory2DTO(parent, parentDTO);
		return parentDTO;
	}

	private void determineDestinationPaths(SyncTreeNode syncTreeNode,
			String defaultDestinationPath) {

		if (!syncTreeNode.isLeaf()) {
			SyncTreeDirectory directory = (SyncTreeDirectory) syncTreeNode;
			SyncTreeDirectory parent = directory.getParent();
			if (parent == null) {
				directory.setDestinationPath(null);
			} else {
				String path = directory.getParent().getDestinationPath();
				if (path != null) {
					directory.setDestinationPath(new File(path + "/"
							+ directory.getParent().getName())
							.getAbsolutePath());
				} else {
					directory.setDestinationPath(defaultDestinationPath);
				}
				if (directory.isMarkAsAddon()
						&& addonDAO.getMap().containsKey(
								directory.getName().toLowerCase())) {
					Addon addon = addonDAO.getMap().get(
							directory.getName().toLowerCase());
					String newPath = addon.getPath();
					directory.setDestinationPath(newPath);
				}
			}
			for (SyncTreeNode n : directory.getList()) {
				determineDestinationPaths(n, defaultDestinationPath);
			}
		} else {
			SyncTreeLeaf leaf = (SyncTreeLeaf) syncTreeNode;
			String path = leaf.getParent().getDestinationPath();
			if (path == null) {
				leaf.setDestinationPath(defaultDestinationPath);
			} else {
				leaf.setDestinationPath(new File(path + "/"
						+ leaf.getParent().getName()).getAbsolutePath());
			}
		}

		// if (!syncTreeNode.isLeaf()) {
		// SyncTreeDirectory directory = (SyncTreeDirectory) syncTreeNode;
		// if (directory.isMarkAsAddon()) {
		// if (addonDAO.getMap().containsKey(directory.getName().toLowerCase()))
		// {
		// Addon addon = addonDAO.getMap().get(
		// directory.getName().toLowerCase());
		// String path = addon.getPath();
		// directory.setDestinationPath(path);
		// for (SyncTreeNode n : directory.getList()) {
		// determineDestinationPathsForAddonFiles(n);
		// }
		// }
		// else {
		// directory.setDestinationPath(defaultDownloadLocation);
		// }
		// } else {
		// for (SyncTreeNode n : directory.getList()) {
		// determineDestinationPaths(n,defaultDownloadLocation);
		// }
		// }
		// }
	}

	private void determineAddonFilesToDelete(SyncTreeNode syncTreeNode) {

		if (!syncTreeNode.isLeaf()) {
			SyncTreeDirectory directory = (SyncTreeDirectory) syncTreeNode;
			if (directory.isMarkAsAddon()
					&& addonDAO.getMap().containsKey(
							directory.getName().toLowerCase())) {
				Addon addon = addonDAO.getMap().get(
						directory.getName().toLowerCase());
				File file = new File(addon.getPath() + "/" + addon.getName());
				addFilesToDelete(directory, file);
			} else {
				for (SyncTreeNode n : directory.getList()) {
					determineAddonFilesToDelete(n);
				}
			}
		}
	}

	private void addFilesToDelete(SyncTreeDirectory directory, File file) {

		File[] subFiles = file.listFiles();

		if (subFiles == null) {
			return;
		}

		List<SyncTreeNode> nodes = directory.getList();
		List<String> listNames = new ArrayList<String>();
		for (SyncTreeNode node : nodes) {
			listNames.add(node.getName().toLowerCase());
		}
		for (File f : subFiles) {
			if (!listNames.contains(f.getName().toLowerCase())) {
				if (f.isDirectory()) {
					SyncTreeDirectory d = new SyncTreeDirectory(f.getName(),
							directory);
					directory.addTreeNode(d);
					d.setDeleted(true);
					d.setDestinationPath(directory.getDestinationPath() + "/"
							+ directory.getName());
				} else {
					SyncTreeLeaf l = new SyncTreeLeaf(f.getName(), directory);
					directory.addTreeNode(l);
					l.setDeleted(true);
					l.setDestinationPath(directory.getDestinationPath() + "/"
							+ directory.getName());

				}
			}
		}

		for (File f : subFiles) {
			if (f.isDirectory()) {
				for (SyncTreeNode node : nodes) {
					if (node.getName().equals(f.getName()) && !node.isLeaf()) {
						SyncTreeDirectory d = (SyncTreeDirectory) node;
						addFilesToDelete(d, f);
						break;
					}
				}
			}
		}
	}

	private void determineAddonFoldersToDelete(SyncTreeNode syncTreeNode) {

		if (!syncTreeNode.isLeaf()) {
			SyncTreeDirectory directory = (SyncTreeDirectory) syncTreeNode;
			if (!directory.isMarkAsAddon() && directory.getParent() != null) {
				File file = new File(directory.getDestinationPath() + "/"
						+ directory.getName());
				// folder must exists locally and remotely
				File[] subFiles = file.listFiles();
				if (subFiles != null) {
					List<String> listNames = new ArrayList<String>();
					for (SyncTreeNode node : directory.getList()) {
						listNames.add(node.getName().toLowerCase());
					}
					for (File f : subFiles) {
						if (!listNames.contains(f.getName().toLowerCase())) {
							addFilesToDelete(directory, file);
						}
					}
				}
			}
			for (SyncTreeNode n : directory.getList()) {
				determineAddonFoldersToDelete(n);
			}
		}
	}

	private void determineDestinationPathsForAddonFiles(
			SyncTreeNode syncTreeNode) {

		if (!syncTreeNode.isLeaf()) {
			SyncTreeDirectory directory = (SyncTreeDirectory) syncTreeNode;
			String path = directory.getParent().getDestinationPath();
			directory.setDestinationPath(new File(path + "/"
					+ directory.getParent().getName()).getAbsolutePath());
		} else {
			SyncTreeLeaf leaf = (SyncTreeLeaf) syncTreeNode;
			String path = leaf.getParent().getDestinationPath();
			leaf.setDestinationPath(new File(path + "/"
					+ leaf.getParent().getName()).getAbsolutePath());
		}
	}

	public void checkRepository(String repositoryName, String path)
			throws RepositoryException, ServerInfoNotFoundException,
			SyncFileNotFoundException, RepositoryCheckException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}

		try {
			ServerInfo serverInfo = repositoryDAO
					.readServerInfo(repositoryName);
			repository.setServerInfo(serverInfo);
		} catch (Exception e) {
			throw new ServerInfoNotFoundException();
		}

		try {
			SyncTreeDirectory sync = repositoryDAO.readSync(repositoryName);
			repository.setSync(sync);
		} catch (Exception e) {
			throw new SyncFileNotFoundException();
		}

		if (!repository.getPath().equals(path) || path.isEmpty()) {
			throw new RepositoryException("Repository path does not match "
					+ path + "!");
		}
		repositoryBuilderDAO.checkRepository(repository);
	}

	public String getDefaultDownloadLocation(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			return null;
		} else {
			return repository.getDefaultDownloadLocation();
		}
	}

	public void setDefaultDownloadLocation(String repositoryName,
			String defaultDownloadLocation) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setDefaultDownloadLocation(defaultDownloadLocation);
			try {
				write(repositoryName);
			} catch (WritingException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateRepositoryRevision(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			ServerInfo serverInfo = repository.getServerInfo();
			if (serverInfo != null) {
				repository.setRevision(serverInfo.getRevision());
			}
			try {
				write(repositoryName);
			} catch (WritingException e) {
			}
		}
	}

	public void setRepositoryNotification(String repositoryName, boolean notify) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setNotify(notify);
			try {
				write(repositoryName);
			} catch (WritingException e) {
			}
		}
	}

	public void setOutOfSync(String repositoryName, boolean value) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setOutOfSynk(value);
		}
	}

	public void setDownloading(String repositoryName, boolean value) {
		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setDownloading(value);
		}
	}

	public boolean isDownloading(String repositoryName) {
		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.isDownloading();
		} else {
			return false;
		}
	}

	public boolean isDownloading() {
		boolean response = false;
		for (Iterator<String> i = repositoryDAO.getMap().keySet().iterator(); i
				.hasNext();) {
			Repository repository = repositoryDAO.getMap().get(i.next());
			if (repository.isDownloading()) {
				response = true;
				break;
			}
		}
		return response;
	}

	public void saveDownloadParameters(String repositoryName,
			long incrementedFilesSize, int lastIndexFileDownloaded,
			boolean resume) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setIncrementedFilesSize(incrementedFilesSize);
			repository.setLastIndexFileDownloaded(lastIndexFileDownloaded);
			repository.setResume(resume);
		}
	}

	public int getLastIndexFileDownloaded(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.getLastIndexFileDownloaded();
		} else {
			return 0;
		}
	}

	public long getIncrementedFilesSize(String repositoryName) {
		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.getIncrementedFilesSize();
		} else {
			return 0;
		}
	}

	public boolean isResume(String repositoryName) {
		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.isResume();
		} else {
			return false;
		}
	}

	public static RepositoryDAO getRepositoryDAO() {
		return repositoryDAO;
	}

	public RepositoryBuilderDAO getRepositoryBuilderDAO() {
		return repositoryBuilderDAO;
	}

	public List<EventDTO> getEvents(String repositoryName)
			throws RepositoryException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			Events events = repository.getEvents();
			if (events != null) {
				List<Event> list = events.getList();
				List<EventDTO> eventDTOs = new ArrayList<EventDTO>();
				for (Event event : list) {
					EventDTO eventDTO = transformEvent2DTO(event);
					eventDTOs.add(eventDTO);
				}
				return eventDTOs;
			} else {
				return null;
			}
		} else {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}
	}

	public void addEvent(String repositoryName, EventDTO eventDTO)
			throws RepositoryException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			Events events = repository.getEvents();
			if (events == null) {
				events = new Events();
				repository.setEvents(events);
			}
			Event event = transformDTO2Event(eventDTO);
			events.getList().add(event);
		} else {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}
	}

	public void renameEvent(String repositoryName, String eventName,
			String newEventName, String description) throws RepositoryException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			Events events = repository.getEvents();
			if (events != null) {
				for (Event event : events.getList()) {
					if (event.getName().equals(eventName)) {
						event.setName(newEventName);
						event.setDescription(description);
					}
				}
			}
		} else {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}
	}

	public void removeEvent(String repositoryName, String eventName)
			throws RepositoryException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			Events events = repository.getEvents();
			if (events != null) {
				Event eventFound = null;
				for (Event event : events.getList()) {
					if (event.getName().equals(eventName)) {
						eventFound = event;
						break;
					}
				}
				if (eventFound != null) {
					events.getList().remove(eventFound);
				}
			}
		} else {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}
	}

	public TreeDirectoryDTO getEventAddonSelection(String repositoryName)
			throws RepositoryException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			SyncTreeDirectory parentSyncTreeDirectory = repository.getSync();
			if (parentSyncTreeDirectory == null) {
				return null;
			} else {
				TreeDirectory parentTreeDirectory = new TreeDirectory(
						parentSyncTreeDirectory.getName(), null);
				extractAddons(parentSyncTreeDirectory, parentTreeDirectory);

				// keep marked directory, change terminal directory to leaf
				TreeDirectory racineCleaned = new TreeDirectory("racine1", null);

				for (TreeNode directory : parentTreeDirectory.getList()) {
					TreeDirectory d = (TreeDirectory) directory;
					cleanTree(d, racineCleaned);
				}
				TreeDirectoryDTO treeDirectoryDTO = new TreeDirectoryDTO();
				treeDirectoryDTO.setName("racine1");
				treeDirectoryDTO.setParent(null);
				transformTreeDirectory2DTO(racineCleaned, treeDirectoryDTO);
				return treeDirectoryDTO;
			}
		} else {
			throw new RepositoryException("Repository " + repositoryName
					+ " not found!");
		}
	}

	private void cleanTree(TreeDirectory directory,
			TreeDirectory directoryCleaned) {

		if (directory.isMarked() && directory.getList().size() != 0) {
			TreeDirectory newDirectory = new TreeDirectory(directory.getName(),
					directoryCleaned);
			directoryCleaned.addTreeNode(newDirectory);
			for (TreeNode n : directory.getList()) {
				TreeDirectory d = (TreeDirectory) n;
				cleanTree(d, newDirectory);
			}
		} else if (directory.isMarked() && directory.getList().size() == 0) {
			TreeLeaf newTreelLeaf = new TreeLeaf(directory.getName(),
					directoryCleaned);
			directoryCleaned.addTreeNode(newTreelLeaf);
		}
	}

	private void extractAddons(SyncTreeDirectory syncTreeDirectory,
			TreeDirectory treeDirectory) {

		List<SyncTreeNode> list = syncTreeDirectory.getList();

		for (SyncTreeNode node : list) {
			if (!node.isLeaf()) {
				SyncTreeDirectory syncTreeDirectory2 = (SyncTreeDirectory) node;
				TreeDirectory treeDirectory2 = new TreeDirectory(
						node.getName(), treeDirectory);
				if (syncTreeDirectory2.isMarkAsAddon()) {
					treeDirectory.addTreeNode(treeDirectory2);
					markRecursively(treeDirectory2);
				} else {
					treeDirectory.addTreeNode(treeDirectory2);
					extractAddons(syncTreeDirectory2, treeDirectory2);
				}
			}
		}
	}

	private void markRecursively(TreeDirectory treeDirectory) {

		treeDirectory.setMarked(true);
		TreeDirectory parent = treeDirectory.getParent();
		if (parent != null) {
			markRecursively(parent);
		}
	}

	public void saveEvent(String repositoryName, EventDTO eventDTO) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			Events events = repository.getEvents();
			if (events != null) {
				for (Event event : events.getList()) {
					if (event.getName().equals(eventDTO.getName())) {
						event.getAddonNames().clear();
						for (Iterator<String> iter = eventDTO.getAddonNames()
								.keySet().iterator(); iter.hasNext();) {
							String key = iter.next();
							boolean value = eventDTO.getAddonNames().get(key);
							event.getAddonNames().put(key, value);
						}
					}
				}
			}
		}
	}

	private Cipher getEncryptionCipher() throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance("AES");
		SecretKey key = new SecretKeySpec(secreteKey, "AES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher;
	}

	private Cipher getDecryptionCipher() throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance("AES");
		SecretKey key = new SecretKeySpec(secreteKey, "AES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher;
	}

	private RepositoryDTO transformRepository2DTO(Repository repository) {
		final RepositoryDTO repositoryDTO = new RepositoryDTO();
		repositoryDTO.setName(repository.getName());
		repositoryDTO.setNotify(repository.isNotify());
		FtpDTO ftpDTO = new FtpDTO();
		ftpDTO.setUrl(repository.getProtocole().getUrl());
		ftpDTO.setLogin(repository.getProtocole().getLogin());
		ftpDTO.setPassword(repository.getProtocole().getPassword());
		ftpDTO.setPort(repository.getProtocole().getPort());
		ftpDTO.setEncryptionMode(repository.getProtocole().getEncryptionMode());
		repositoryDTO.setFtpDTO(ftpDTO);
		repositoryDTO.setPath(repository.getPath());
		repositoryDTO.setRevision(repository.getRevision());
		repositoryDTO.setAutoConfigURL(repository.getAutoConfigURL());
		repositoryDTO.setOutOfSynk(repository.isOutOfSynk());
		return repositoryDTO;
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

	private void transformSyncTreeDirectory2DTO(
			SyncTreeDirectory syncTreeDirectory,
			SyncTreeDirectoryDTO syncTreeDirectoryDTO) {

		List<SyncTreeNode> list = syncTreeDirectory.getList();

		for (SyncTreeNode node : list) {
			if (node.isLeaf()) {
				SyncTreeLeaf syncTreeLeaf = (SyncTreeLeaf) node;
				SyncTreeLeafDTO syncTreeLeafDTO = transformSyncTreeLeaf2DTO(syncTreeLeaf);
				syncTreeLeafDTO.setParent(syncTreeDirectoryDTO);
				syncTreeDirectoryDTO.addTreeNode(syncTreeLeafDTO);
				if (syncTreeLeafDTO.isUpdated()) {
					SyncTreeDirectoryDTO parent = syncTreeLeafDTO.getParent();
					parent.setUpdated(true);
					while (!parent.isMarkAsAddon()) {
						parent = parent.getParent();
						if (parent == null) {
							break;
						} else {
							parent.setUpdated(true);
						}
					}
				}
			} else {
				SyncTreeDirectory syncTreeDirectory2 = (SyncTreeDirectory) node;
				SyncTreeDirectoryDTO syncTreedDirectoryDTO2 = new SyncTreeDirectoryDTO();
				syncTreedDirectoryDTO2.setName(syncTreeDirectory2.getName());
				syncTreedDirectoryDTO2.setParent(syncTreeDirectoryDTO);
				syncTreedDirectoryDTO2.setMarkAsAddon(syncTreeDirectory2
						.isMarkAsAddon());
				syncTreedDirectoryDTO2.setDestinationPath(syncTreeDirectory2
						.getDestinationPath());
				syncTreedDirectoryDTO2.setSelected(false);
				syncTreedDirectoryDTO2.setUpdated(false);
				syncTreedDirectoryDTO2.setDeleted(syncTreeDirectory2
						.isDeleted());
				syncTreeDirectoryDTO.addTreeNode(syncTreedDirectoryDTO2);
				transformSyncTreeDirectory2DTO(syncTreeDirectory2,
						syncTreedDirectoryDTO2);
			}
		}
	}

	private SyncTreeLeafDTO transformSyncTreeLeaf2DTO(SyncTreeLeaf syncTreeLeaf) {

		SyncTreeLeafDTO syncTreeLeafDTO = new SyncTreeLeafDTO();
		syncTreeLeafDTO.setName(syncTreeLeaf.getName());
		syncTreeLeafDTO.setSize(syncTreeLeaf.getSize());
		syncTreeLeafDTO.setSelected(false);
		syncTreeLeafDTO.setDeleted(syncTreeLeaf.isDeleted());
		String remoteSHA1 = syncTreeLeaf.getSha1();
		String localSHA1 = syncTreeLeaf.getLocalSHA1();
		if (remoteSHA1 == null) {// remote does not exists => file to delete
			syncTreeLeafDTO.setUpdated(false);
		} else if (!remoteSHA1.equals(localSHA1)) {
			syncTreeLeafDTO.setUpdated(true);
		} else {
			syncTreeLeafDTO.setUpdated(false);
		}
		syncTreeLeafDTO.setDestinationPath(syncTreeLeaf.getDestinationPath());
		return syncTreeLeafDTO;
	}

	private void propagateUpdatedStatus(
			SyncTreeDirectoryDTO syncTreeDirectoryDTO) {

		if (syncTreeDirectoryDTO != null) {
			syncTreeDirectoryDTO.setUpdated(true);
			SyncTreeDirectoryDTO parentDTO = syncTreeDirectoryDTO.getParent();
			propagateUpdatedStatus(parentDTO);
		}
	}

	private void propagateDeletedStatus(
			SyncTreeDirectoryDTO syncTreeDirectoryDTO) {

		if (syncTreeDirectoryDTO != null) {
			syncTreeDirectoryDTO.setDeleted(true);
			SyncTreeDirectoryDTO parentDTO = syncTreeDirectoryDTO.getParent();
			propagateDeletedStatus(parentDTO);
		}
	}

	private ChangelogDTO transformChangelog2DTO(Changelog changelog) {

		final ChangelogDTO changelogDTO = new ChangelogDTO();
		changelogDTO.setRevision(changelog.getRevision());
		changelogDTO.setBuildDate(changelog.getBuildDate());
		for (String stg : changelog.getUpdatedAddons()) {
			changelogDTO.getUpdatedAddons().add(stg);
		}
		for (String stg : changelog.getNewAddons()) {
			changelogDTO.getNewAddons().add(stg);
		}
		for (String stg : changelog.getDeletedAddons()) {
			changelogDTO.getDeletedAddons().add(stg);
		}
		return changelogDTO;
	}

	private Event transformDTO2Event(EventDTO eventDTO) {

		final Event event = new Event(eventDTO.getName());
		event.setDescription(eventDTO.getDescription());
		for (Iterator<String> iter = eventDTO.getAddonNames().keySet()
				.iterator(); iter.hasNext();) {
			String key = iter.next();
			boolean value = eventDTO.getAddonNames().get(key);
			event.getAddonNames().put(key, value);
		}
		return event;
	}

	private EventDTO transformEvent2DTO(Event event) {

		final EventDTO eventDTO = new EventDTO();
		eventDTO.setName(event.getName());
		eventDTO.setDescription(event.getDescription());
		for (Iterator<String> iter = event.getAddonNames().keySet().iterator(); iter
				.hasNext();) {
			String key = iter.next();
			boolean value = event.getAddonNames().get(key);
			eventDTO.getAddonNames().put(key, value);
		}
		return eventDTO;
	}

	public void transformTreeDirectory2DTO(TreeDirectory treeDirectory,
			TreeDirectoryDTO treeDirectoryDTO) {

		List<TreeNode> list = treeDirectory.getList();

		for (TreeNode treeNode : list) {
			if (treeNode.isLeaf()) {
				TreeLeaf treeLeaf = (TreeLeaf) treeNode;
				TreeLeafDTO treeLeafDTO = transformTreeLeaf2DTO(treeLeaf);
				treeLeafDTO.setParent(treeDirectoryDTO);
				treeDirectoryDTO.addTreeNode(treeLeafDTO);
			} else {
				TreeDirectory treeDirectory2 = (TreeDirectory) treeNode;
				TreeDirectoryDTO treedDirectoryDTO2 = new TreeDirectoryDTO();
				treedDirectoryDTO2.setName(treeDirectory2.getName());
				treedDirectoryDTO2.setSelected(treeDirectory2.isSelected());
				treedDirectoryDTO2.setParent(treeDirectoryDTO);
				treeDirectoryDTO.addTreeNode(treedDirectoryDTO2);
				transformTreeDirectory2DTO(treeDirectory2, treedDirectoryDTO2);
			}
		}
	}

	private TreeLeafDTO transformTreeLeaf2DTO(TreeLeaf treeLeaf) {

		TreeLeafDTO treeLeafDTO = new TreeLeafDTO();
		treeLeafDTO.setName(treeLeaf.getName());
		treeLeafDTO.setSelected(treeLeaf.isSelected());
		return treeLeafDTO;
	}
}
