package fr.soe.a3s.service;

import java.io.File;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.constant.RepositoryStatus;
import fr.soe.a3s.dao.AddonDAO;
import fr.soe.a3s.dao.ConfigurationDAO;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.repository.RepositoryBuildProcessor;
import fr.soe.a3s.dao.repository.RepositoryDAO;
import fr.soe.a3s.dao.repository.RepositorySHA1Processor;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.AbstractProtocoleFactory;
import fr.soe.a3s.domain.Addon;
import fr.soe.a3s.domain.TreeDirectory;
import fr.soe.a3s.domain.TreeLeaf;
import fr.soe.a3s.domain.TreeNode;
import fr.soe.a3s.domain.configration.FavoriteServer;
import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.Changelog;
import fr.soe.a3s.domain.repository.Changelogs;
import fr.soe.a3s.domain.repository.Event;
import fr.soe.a3s.domain.repository.Events;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.domain.repository.SyncTreeLeaf;
import fr.soe.a3s.domain.repository.SyncTreeNode;
import fr.soe.a3s.dto.AutoConfigDTO;
import fr.soe.a3s.dto.ChangelogDTO;
import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.dto.ServerInfoDTO;
import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.configuration.FavoriteServerDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.exception.repository.RepositoryDefaultDownloadLocationNotFoundException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.exception.repository.RepositoryMainFolderLocationNotFoundException;
import fr.soe.a3s.exception.repository.RepositoryNotFoundException;
import fr.soe.a3s.exception.repository.SyncFileNotFoundException;

public class RepositoryService extends ObjectDTOtransformer implements
		DataAccessConstants {

	private static final RepositoryDAO repositoryDAO = new RepositoryDAO();
	private static final ConfigurationDAO configurationDAO = new ConfigurationDAO();
	private static final AddonDAO addonDAO = new AddonDAO();
	private final RepositoryBuildProcessor repositoryBuildProcessor = new RepositoryBuildProcessor();
	private final RepositorySHA1Processor repositorySHA1Processor = new RepositorySHA1Processor();

	public void readAll() throws LoadingException {
		System.out.println("Loading repositories...");
		repositoryDAO.readAll();
	}

	public void write(String repositoryName) throws WritingException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repositoryDAO.write(repository);
		}
	}

	public void createRepository(String name, String url, String port,
			String login, String password, ProtocolType protocolType)
			throws CheckException {

		if (name == null || "".equals(name)) {
			throw new CheckException("Repository name can't be empty.");
		}

		AbstractProtocole protocole = AbstractProtocoleFactory.getProtocol(url,
				port, login, password, protocolType);
		if (protocole == null) {
			throw new CheckException("Protocol not supported yet.");
		}

		protocole.checkData();

		Repository repository = new Repository(name, protocole);
		repositoryDAO.add(repository);
	}

	public void setRepository(String name, String url, String port,
			String login, String password, ProtocolType protocolType)
			throws CheckException {

		if (name == null || "".equals(name)) {
			throw new CheckException("Repository name can't be empty.");
		}

		AbstractProtocole protocole = AbstractProtocoleFactory.getProtocol(url,
				port, login, password, protocolType);
		if (protocole == null) {
			throw new CheckException("Protocol is not supported yet.");
		}

		protocole.checkData();

		Repository repository = repositoryDAO.getMap().get(name);
		repository.setProtocol(protocole);
	}

	public void renameRepository(String initialRepositoryName, String newName)
			throws CheckException, RepositoryNotFoundException {

		if (newName == null || "".equals(newName)) {
			throw new CheckException("Repository name can't be empty.");
		}

		Repository repository = repositoryDAO.getMap().get(
				initialRepositoryName);
		if (repository != null) {
			removeRepository(initialRepositoryName);
			repository.setName(newName);
			repositoryDAO.getMap().put(newName, repository);
		}
	}

	public boolean removeRepository(String repositoryName)
			throws RepositoryNotFoundException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}
		return repositoryDAO.remove(repositoryName);
	}

	public void resetRepositoryUploadProtocol(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setUploadProtocole(null);
		}
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
			throw new RepositoryNotFoundException(repositoryName);
		}
	}

	public void setRepositoryPath(String repositoryName, String repositoryPath)
			throws RepositoryException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setPath(repositoryPath);
		} else {
			throw new RepositoryNotFoundException(repositoryName);
		}
	}

	public String getRepositoryPath(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.getPath();
		} else {
			return null;
		}
	}

	public String getRepositoryUrl(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.getProtocol().getProtocolType().getPrompt()
					+ repository.getProtocol().getHostname() + ":"
					+ repository.getProtocol().getPort()
					+ repository.getProtocol().getRemotePath();
		} else {
			return null;
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
			throw new RepositoryNotFoundException(repositoryName);
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
			throw new RepositoryNotFoundException(repositoryName);
		}
	}

	public AutoConfigDTO getAutoconfig(String repositoryName)
			throws RepositoryException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			AutoConfig autoConfig = repository.getAutoConfig();
			if (autoConfig != null) {
				AutoConfigDTO autoConfigDTO = transformAutoConfig2DTO(autoConfig);
				return autoConfigDTO;
			} else {
				return null;
			}
		} else {
			throw new RepositoryNotFoundException(repositoryName);
		}
	}

	public void updateRepository(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			if (!repository.isUpdating()){
				repository.setUpdating(true);
				
				/**/
				if (repository.getServerInfo() != null) {
					repository.getHiddenFolderPath().addAll(
							repository.getServerInfo().getHiddenFolderPaths());
					if (repository.getNumberOfClientConnections() == 0) {
						repository.setNumberOfClientConnections(repository
								.getServerInfo().getNumberOfConnections());
					}
				}
				
				/**/
				if (repository.getAutoConfig() != null) {

					List<FavoriteServer> list1 = repository.getAutoConfig()
							.getFavoriteServers();
					for (FavoriteServer favoriteServerList1 : list1) {
						favoriteServerList1.setRepositoryName(repositoryName);
					}

					List<FavoriteServer> list2 = configurationDAO
							.getConfiguration().getFavoriteServers();

					List<FavoriteServer> newList2 = new ArrayList<FavoriteServer>();

					for (FavoriteServer favoriteServerList1 : list1) {
						newList2.add(favoriteServerList1);
					}

					for (FavoriteServer favoriteServerList2 : list2) {
						boolean found = false;
						for (FavoriteServer favoriteServerNewList2 : newList2) {
							if (favoriteServerNewList2.getName().equals(
									favoriteServerList2.getName())) {
								found = true;
								favoriteServerNewList2
										.setIpAddress(favoriteServerList2
												.getIpAddress());
								favoriteServerNewList2.setPort(favoriteServerList2
										.getPort());
								favoriteServerNewList2
										.setPassword(favoriteServerList2
												.getPassword());
								if (favoriteServerList2.getModsetName() != null) {
									if (!favoriteServerList2.getModsetName()
											.isEmpty()) {
										favoriteServerNewList2
												.setModsetName(favoriteServerList2
														.getModsetName());
									}
								}
								break;
							}
						}
						if (!found) {
							newList2.add(favoriteServerList2);
						} else {

						}
					}

					configurationDAO.getConfiguration().updateFavoriteServers(newList2);
				}

				/**/
				RepositoryStatus repositoryStatus = RepositoryStatus.INDETERMINATED;
				ServerInfo serverInfo = repository.getServerInfo();
				if (serverInfo != null) {
					if (repository.getRevision() == serverInfo.getRevision()) {
						repositoryStatus = RepositoryStatus.OK;
					} else if (repository.getRevision() < serverInfo.getRevision()) {
						if (serverInfo.isRepositoryContentUpdated()) {
							repositoryStatus = RepositoryStatus.UPDATED;
						} else {
							Changelogs changelogs = repository.getChangelogs();
							if (changelogs != null) {
								List<Changelog> list = changelogs.getList();
								Map<Integer, Boolean> map = new TreeMap<Integer, Boolean>();
								for (Changelog changelog : list) {
									map.put(changelog.getRevision(),
											changelog.isContentUpdated());
								}
								boolean change = false;
								if (map.containsKey(repository.getRevision())) {
									for (Iterator<Integer> iter = map.keySet()
											.iterator(); iter.hasNext();) {
										int revision = iter.next();
										if (revision > repository.getRevision()) {
											change = map.get(revision);
											if (change) {
												break;
											}
										}
									}
									if (change) {
										repositoryStatus = RepositoryStatus.UPDATED;
									} else {
										repositoryStatus = RepositoryStatus.OK;
									}
								}
							}
						}
					}
				}
				// Set Repository Synchronization Status
				repository.setRepositorySyncStatus(repositoryStatus);
				
				repository.setUpdating(false);
			}
		}
	}

	public void buildRepository(String repositoryName)
			throws RepositoryException, IOException, RuntimeException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}
		if (repository.getPath() == null || "".equals(repository.getPath())) {
			throw new RepositoryMainFolderLocationNotFoundException(
					repositoryName);
		}
		repositoryBuildProcessor.init(repository);
		repositoryBuildProcessor.run();
	}

	public SyncTreeDirectoryDTO checkForAddons(String repositoryName)
			throws RepositoryException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		SyncTreeDirectory parent = repository.getSync();

		if (parent == null) {
			throw new SyncFileNotFoundException(repositoryName);
		}

		String defaultDownloadLocation = repository
				.getDefaultDownloadLocation();

		if (defaultDownloadLocation == null
				|| "".equals(defaultDownloadLocation)) {
			throw new RepositoryDefaultDownloadLocationNotFoundException(
					repositoryName);
		}

		boolean noAutoDiscover = repository.isNoAutoDiscover();
		boolean exactMatch = repository.isExactMatch();
		Set<String> hiddenFolderPaths = repository.getHiddenFolderPath();

		List<SyncTreeNode> nodesList = parent.getDeepSearchNodesList();
		List<SyncTreeLeaf> leafsList = parent.getDeepSearchLeafsList();

		// 1. Set destination file path
		determineDestinationPaths(nodesList, defaultDownloadLocation,
				noAutoDiscover);

		// 2. Compute SHA1 for local files on disk
		repositorySHA1Processor.init(leafsList,
				repository.getMapFilesForSync(), true);
		repositorySHA1Processor.run();

		// 4. Determine new or updated files
		determineNewAndUpdatedFiles(nodesList);

		// 5. Determine extra local files to hide
		determineHiddenFiles(nodesList, hiddenFolderPaths);

		// 6. Determine extra local files to delete
		determineExtraLocalFilesToDelete(parent, defaultDownloadLocation,
				exactMatch);

		SyncTreeDirectoryDTO parentDTO = new SyncTreeDirectoryDTO();
		parentDTO.setName(SyncTreeDirectoryDTO.RACINE);
		parentDTO.setParent(null);
		transformSyncTreeDirectory2DTO(parent, parentDTO);
		return parentDTO;
	}

	private void determineDestinationPaths(List<SyncTreeNode> nodesList,
			String defaultDestinationPath, boolean noAutoDiscover) {

		for (SyncTreeNode node : nodesList) {
			if (!node.isLeaf()) {
				SyncTreeDirectory directory = (SyncTreeDirectory) node;
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
					if (!noAutoDiscover
							&& directory.isMarkAsAddon()
							&& addonDAO.getMap().containsKey(
									directory.getName().toLowerCase())) {
						Addon addon = addonDAO.getMap().get(
								directory.getName().toLowerCase());
						String newPath = addon.getPath();
						directory.setDestinationPath(newPath);
					}
				}
			} else {
				SyncTreeLeaf leaf = (SyncTreeLeaf) node;
				String path = leaf.getParent().getDestinationPath();
				if (path == null) {
					leaf.setDestinationPath(defaultDestinationPath);
				} else {
					leaf.setDestinationPath(new File(path + "/"
							+ leaf.getParent().getName()).getAbsolutePath());
				}
			}
		}
	}

	private void determineNewAndUpdatedFiles(List<SyncTreeNode> nodesList) {

		for (SyncTreeNode node : nodesList) {
			if (!node.isLeaf()) {
				SyncTreeDirectory directory = (SyncTreeDirectory) node;
				File file = new File(directory.getDestinationPath() + "/"
						+ directory.getName());
				if (!file.exists()) {
					node.setUpdated(true);
				} else {
					node.setUpdated(false);
				}
			} else {
				SyncTreeLeaf leaf = (SyncTreeLeaf) node;
				File file = new File(leaf.getDestinationPath() + "/"
						+ leaf.getName());
				if (!file.exists() || leaf.getLocalSHA1() == null) {
					node.setUpdated(true);
				} else {
					if (!leaf.getLocalSHA1().equals(leaf.getSha1())) {
						node.setUpdated(true);
					} else {
						node.setUpdated(false);
					}
				}
			}
		}
	}

	private void determineHiddenFiles(List<SyncTreeNode> nodesList,
			Set<String> hiddenFolderPaths) {

		for (SyncTreeNode node : nodesList) {
			if (!node.isLeaf()) {
				SyncTreeDirectory directory = (SyncTreeDirectory) node;
				SyncTreeNode parent = directory.getParent();
				String relativePath = directory.getName();

				while (parent != null) {
					if (!parent.getName().equals(SyncTreeNode.RACINE)) {
						relativePath = parent.getName() + "/" + relativePath;
					}
					parent = parent.getParent();
				}

				boolean contains = false;
				for (String stg : hiddenFolderPaths) {
					stg = backlashReplace(stg);
					if (relativePath.length() >= stg.length()) {
						String p = relativePath.substring(0, stg.length());
						if (stg.equals(p)) {
							contains = true;
							break;
						}
					}
				}
				if (contains) {
					directory.setHidden(true);
				} else {
					directory.setHidden(false);
				}
			}
		}
	}

	private String backlashReplace(String myStr) {

		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(
				myStr);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			if (character == '\\') {
				result.append("/");
			} else {
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	private String slashReplace(String myStr) {

		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(
				myStr);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			if (character == '/') {
				result.append("\\");
			} else {
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	private void determineExtraLocalFilesToDelete(SyncTreeNode node,
			String defaultDestinationPath, boolean exactMatch) {

		if (!node.isLeaf()) {
			SyncTreeDirectory directory = (SyncTreeDirectory) node;

			if (!directory.isHidden()) {
				File[] subFiles = null;
				if (directory.getParent() != null) {
					File file = new File(directory.getDestinationPath() + "/"
							+ directory.getName());
					subFiles = file.listFiles();
				} else if (directory.getParent() == null && exactMatch) {
					File file = new File(defaultDestinationPath);
					subFiles = file.listFiles();
				}

				// folder must exists locally and remotely
				if (subFiles != null) {
					List<String> listNames = new ArrayList<String>();
					for (SyncTreeNode n : directory.getList()) {
						listNames.add(n.getName().toLowerCase());
					}
					for (File f : subFiles) {
						if (!listNames.contains(f.getName().toLowerCase())) {
							if (f.isDirectory()) {
								SyncTreeDirectory d = new SyncTreeDirectory(
										f.getName(), directory);
								directory.addTreeNode(d);
								d.setDeleted(true);
								if (directory.getDestinationPath() == null) {
									d.setDestinationPath(defaultDestinationPath);
								} else {
									d.setDestinationPath(directory
											.getDestinationPath()
											+ "/"
											+ directory.getName());
								}
							} else if (!f.getName().contains(PART_EXTENSION)
									&& !f.getName().contains(PBO_ZIP_EXTENSION)) {
								SyncTreeLeaf l = new SyncTreeLeaf(f.getName(),
										directory);
								directory.addTreeNode(l);
								l.setDeleted(true);
								if (directory.getDestinationPath() == null) {
									l.setDestinationPath(defaultDestinationPath);
								} else {
									l.setDestinationPath(directory
											.getDestinationPath()
											+ "/"
											+ directory.getName());
								}
							}
						}
					}
				}
				for (SyncTreeNode n : directory.getList()) {
					determineExtraLocalFilesToDelete(n, defaultDestinationPath,
							exactMatch);
				}
			}
		}
	}

	public void setDestinationPaths(String repositoryName,
			SyncTreeDirectoryDTO syncTreeDirectoryDTO)
			throws RepositoryException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {

			SyncTreeDirectory syncTreeDirectory = repository.getSync();
			setDestinationPaths(repositoryName, syncTreeDirectoryDTO);
		} else {
			throw new RepositoryNotFoundException(repositoryName);
		}
	}

	private void setDestinationPaths(SyncTreeDirectoryDTO syncTreeDirectoryDTO,
			SyncTreeDirectory syncTreeDirectory) {
	}

	public void addFilesToHide(String folderPath, String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.getHiddenFolderPath().add(folderPath);
		}
	}

	public void removeFilesToHide(String folderPath, String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			boolean removed = repository.getHiddenFolderPath().remove(
					folderPath);
			if (!removed) {
				folderPath = slashReplace(folderPath);
				repository.getHiddenFolderPath().remove(folderPath);
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

	public void resetRepositoryRevision(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setRevision(-1);
		}
	}

	public void updateRepositoryRevision(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			ServerInfo serverInfo = repository.getServerInfo();
			if (serverInfo != null) {
				repository.setRevision(serverInfo.getRevision());
				try {
					write(repositoryName);
				} catch (WritingException e) {
					e.printStackTrace();
				}
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

	public void setRepositoryAutoUpdate(String repositoryName, boolean auto) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setAuto(auto);
			try {
				write(repositoryName);
			} catch (WritingException e) {
			}
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

	public void setLastIndexFileTransfered(String repositoryName,
			int lastIndexFileDownloaded) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setLastIndexFileDonwloaded(lastIndexFileDownloaded);
		}
	}

	public int getLastIndexFileTransfered(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.getLastIndexFileTransfered();
		} else {
			return 0;
		}
	}

	public boolean isAutoDiscover(String repositoryName) {
		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.isNoAutoDiscover();
		} else {
			return false;
		}
	}

	public void setAutoDiscover(boolean value, String repositoryName) {
		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setNoAutoDiscover(value);
			try {
				write(repositoryName);
			} catch (WritingException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isExactMatch(String repositoryName) {
		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.isExactMatch();
		} else {
			return false;
		}
	}

	public void setExactMatch(boolean value, String repositoryName) {
		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setExactMatch(value);
			try {
				write(repositoryName);
			} catch (WritingException e) {
				e.printStackTrace();
			}
		}
	}

	public static RepositoryDAO getRepositoryDAO() {
		return repositoryDAO;
	}

	public RepositoryBuildProcessor getRepositoryBuilderDAO() {
		return repositoryBuildProcessor;
	}

	public RepositorySHA1Processor getRepositorySHA1Processor() {
		return this.repositorySHA1Processor;
	}

	public List<EventDTO> getEvents(String repositoryName) {

		List<EventDTO> eventDTOs = new ArrayList<EventDTO>();

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			Events events = repository.getEvents();
			if (events != null) {
				List<Event> list = events.getList();
				for (Event event : list) {
					EventDTO eventDTO = transformEvent2DTO(event);
					eventDTO.setRepositoryName(repositoryName);
					// Ensure no duplicate
					boolean contains = false;
					for (EventDTO evt : eventDTOs) {
						if (evt.getName().equals(eventDTO.getName())) {
							contains = true;
						}
					}
					if (!contains) {
						eventDTOs.add(eventDTO);
					}
				}
			}
		}
		return eventDTOs;
	}

	public void addEvent(String repositoryName, EventDTO eventDTO) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			Events events = repository.getEvents();
			if (events == null) {
				events = new Events();
				repository.setEvents(events);
			}
			Event event = transformDTO2Event(eventDTO);
			events.getList().add(event);
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
			throw new RepositoryNotFoundException(repositoryName);
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
			throw new RepositoryNotFoundException(repositoryName);
		}
	}

	public void writeEvents(String repositoryName) throws WritingException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repositoryDAO.writeEvents(repository);
		}
	}

	public TreeDirectoryDTO getGroupFromRepository(String repositoryName,
			boolean withUserconfig) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			SyncTreeDirectory parent = repository.getSync();
			if (parent == null) {
				return null;
			} else {
				TreeDirectory racineTree = new TreeDirectory(parent.getName(),
						null);
				extractAddons(parent, racineTree);

				// Keep marked directory, change terminal directory to leaf
				TreeDirectory racineCleaned = new TreeDirectory("racine", null);

				for (TreeNode directory : racineTree.getList()) {
					TreeDirectory d = (TreeDirectory) directory;
					cleanTree(d, racineCleaned);
				}

				// Userconfig
				if (withUserconfig) {
					for (SyncTreeNode node : parent.getList()) {
						if (node.getName().toLowerCase().equals("userconfig")
								&& !node.isLeaf()) {
							TreeDirectory d = new TreeDirectory(node.getName(),
									racineCleaned);
							racineCleaned.addTreeNode(d);
							for (SyncTreeNode n : ((SyncTreeDirectory) node)
									.getList()) {
								TreeLeaf l = new TreeLeaf(n.getName(), d);
								d.addTreeNode(l);
							}
						}
					}
				}

				TreeDirectoryDTO treeDirectoryDTO = new TreeDirectoryDTO();
				treeDirectoryDTO.setName("racine");
				treeDirectoryDTO.setParent(null);
				transformTreeDirectory2DTO(racineCleaned, treeDirectoryDTO);
				return treeDirectoryDTO;
			}
		}
		return null;
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
						event.getUserconfigFolderNames().clear();
						for (Iterator<String> iter = eventDTO.getAddonNames()
								.keySet().iterator(); iter.hasNext();) {
							String key = iter.next();
							boolean value = eventDTO.getAddonNames().get(key);
							event.getAddonNames().put(key, value);
						}
						for (Iterator<String> iter = eventDTO
								.getUserconfigFolderNames().keySet().iterator(); iter
								.hasNext();) {
							String key = iter.next();
							boolean value = eventDTO.getUserconfigFolderNames()
									.get(key);
							event.getUserconfigFolderNames().put(key, value);
						}
					}
				}
			}
		}
	}

	public void addExcludedFilesPathFromBuild(String repositoryName, String path) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.getExcludedFilesFromBuild().add(path);
		}
	}

	public Collection<String> getExcludedFilesPathFromBuild(
			String repositoryName) {

		Collection<String> list = new HashSet<String>();
		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			list.addAll(repository.getExcludedFilesFromBuild());
		}
		return list;
	}

	public void setExcludedFilesPathFromBuild(String repositoryName,
			List<String> paths) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.getExcludedFilesFromBuild().clear();
			repository.getExcludedFilesFromBuild().addAll(paths);
		}
	}

	public void clearExcludedFilesPathFromBuild(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.getExcludedFilesFromBuild().clear();
		}
	}

	public void addExcludedFoldersFromSync(String repositoryName, String path) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.getExcludedFoldersFromSync().add(path);
		}
	}

	public void clearExcludedFoldersFromSync(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.getExcludedFoldersFromSync().clear();
		}
	}

	public Collection<String> getExcludedFoldersFromSync(String repositoryName) {

		Collection<String> list = new HashSet<String>();
		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			list.addAll(repository.getExcludedFoldersFromSync());
		}
		return list;
	}

	public void setExcludedFoldersFromSync(String repositoryName,
			List<String> paths) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.getExcludedFoldersFromSync().clear();
			repository.getExcludedFoldersFromSync().addAll(paths);
		}
	}

	public List<FavoriteServerDTO> getFavoriteServerSetToAutoconfig(
			String repositoryName) {

		List<FavoriteServerDTO> favoriteServerDTOs = new ArrayList<FavoriteServerDTO>();
		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			List<FavoriteServer> list = repository
					.getFavoriteServersSetToAutoconfig();
			for (FavoriteServer favoriteServer : list) {
				FavoriteServerDTO f = transformFavoriteServers2DTO(favoriteServer);
				favoriteServerDTOs.add(f);
			}
		}
		return favoriteServerDTOs;
	}

	public void setFavoriteServerToAutoconfig(String repositoryName,
			List<FavoriteServerDTO> favoriteServerDTOs) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.getFavoriteServersSetToAutoconfig().clear();
			for (FavoriteServerDTO favoriteServerDTO : favoriteServerDTOs) {
				FavoriteServer favoriteServer = transformDTO2FavoriteServer(favoriteServerDTO);
				favoriteServer.setRepositoryName(repositoryName);
				repository.getFavoriteServersSetToAutoconfig().add(
						favoriteServer);
			}
		}
	}

	public int getNumberOfConnections(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.getNumberOfConnections();
		} else {
			return 0;
		}
	}

	public void setNumberOfConnections(String repositoryName,
			int numberOfConnections) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setNumberOfConnections(numberOfConnections);
		}
	}

	public boolean isCompressed(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.isCompressed();
		}
		return false;
	}

	public void setCompressed(String repositoryName, boolean value) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setCompressed(value);
		}
	}

	public boolean isUploadCompressedPboFilesOnly(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.isUploadCompressedPboFilesOnly();
		}
		return false;
	}

	public void setUploadCompressedPboFilesOnly(String repositoryName,
			boolean value) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setUploadCompressedPboFilesOnly(value);
		}
	}

	public boolean isUsePartialFileTransfer(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.isUsePartialFileTransfer();
		}
		return false;
	}

	public void setUsePartialFileTransfer(String repositoryName, boolean value) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setUsePartialFileTransfer(value);
		}
	}

	public void setUploading(String repositoryName, boolean value) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setUploading(value);
		}
	}

	public boolean isUploading(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.isUploading();
		}
		return false;
	}

	public void setBuilding(String repositoryName, boolean value) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setBuilding(value);
		}
	}

	public boolean isBuilding(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.isBuilding();
		}
		return false;
	}

	public boolean isChecking(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.isChecking();
		}
		return false;
	}

	public void setChecking(String repositoryName, boolean value) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setChecking(value);
		}
	}

	public boolean isCheckingForAddons(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.isCheckingForAddons();
		}
		return false;
	}

	public void setCheckingForAddons(String repositoryName, boolean value) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setCheckingForAddons(value);
		}
	}

	public void setRepositoryUploadProtocole(String repositoryName, String url,
			String port, String login, String password,
			ProtocolType protocolType, String connectionTimeOut,
			String readTimeOut) throws CheckException, RepositoryException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		AbstractProtocole abstractProtocole = AbstractProtocoleFactory
				.getProtocol(url, port, login, password, protocolType);
		abstractProtocole.setConnectionTimeOut(connectionTimeOut);
		abstractProtocole.setReadTimeOut(readTimeOut);

		if (abstractProtocole == null) {
			throw new CheckException("Upload protocol not supported.");
		}

		abstractProtocole.checkData();
		repository.setUploadProtocole(abstractProtocole);
	}

	public void readLocalyBuildedRepository(String repositoryName)
			throws RepositoryException, IOException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		SyncTreeDirectory sync = null;
		ServerInfo serverInfo = null;
		Changelogs changelogs = null;
		AutoConfig autoConfig = null;
		Events events = null;
		try {
			sync = repositoryDAO.readSync(repository);
			serverInfo = repositoryDAO.readServerInfo(repository);
			changelogs = repositoryDAO.readChangelogs(repository);
			autoConfig = repositoryDAO.readAutoConfig(repository);
			events = repositoryDAO.readEvents(repository);
		} finally {
			repository.setLocalSync(sync);// null if not found
			repository.setLocalServerInfo(serverInfo);// null if not found
			repository.setLocalChangelogs(changelogs);// null if not found
			repository.setLocalAutoConfig(autoConfig);// null if not found
			repository.setLocalEvents(events);// null if not found
		}
	}

	public SyncTreeDirectoryDTO getSync(String repositoryName)
			throws RepositoryException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		if (repository.getSync() == null) {
			return null;
		} else {
			SyncTreeDirectoryDTO parentDTO = new SyncTreeDirectoryDTO();
			parentDTO.setName("racine");
			parentDTO.setParent(null);
			transformSyncTreeDirectory2DTO(repository.getSync(), parentDTO);
			return parentDTO;
		}
	}

	public SyncTreeDirectoryDTO getLocalSync(String repositoryName)
			throws RepositoryException {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryName);
		}

		if (repository.getLocalSync() == null) {
			return null;
		} else {
			SyncTreeDirectoryDTO parentDTO = new SyncTreeDirectoryDTO();
			parentDTO.setName("racine");
			parentDTO.setParent(null);
			transformSyncTreeDirectory2DTO(repository.getLocalSync(), parentDTO);
			return parentDTO;
		}
	}

	public RepositoryStatus getRepositorySyncStatus(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.getRepositorySyncStatus();
		} else {
			return RepositoryStatus.INDETERMINATED;
		}
	}

	public void setRepositorySyncStatus(String repositoryName,
			RepositoryStatus repositoryStatus) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setRepositorySyncStatus(repositoryStatus);
		}
	}

	public RepositoryStatus determineRepositorySyncStatus(String repositoryName) {

		RepositoryStatus repositoryStatus = RepositoryStatus.INDETERMINATED;

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			ServerInfo serverInfo = repository.getServerInfo();
			if (serverInfo != null) {
				if (repository.getRevision() == serverInfo.getRevision()) {
					repositoryStatus = RepositoryStatus.OK;
				} else if (repository.getRevision() < serverInfo.getRevision()) {
					if (serverInfo.isRepositoryContentUpdated()) {
						repositoryStatus = RepositoryStatus.UPDATED;
					} else {
						Changelogs changelogs = repository.getChangelogs();
						if (changelogs != null) {
							List<Changelog> list = changelogs.getList();
							Map<Integer, Boolean> map = new TreeMap<Integer, Boolean>();
							for (Changelog changelog : list) {
								map.put(changelog.getRevision(),
										changelog.isContentUpdated());
							}
							boolean change = false;
							if (map.containsKey(repository.getRevision())) {
								for (Iterator<Integer> iter = map.keySet()
										.iterator(); iter.hasNext();) {
									int revision = iter.next();
									if (revision > repository.getRevision()) {
										change = map.get(revision);
										if (change) {
											break;
										}
									}
								}
								if (change) {
									repositoryStatus = RepositoryStatus.UPDATED;
								} else {
									repositoryStatus = RepositoryStatus.OK;
								}
							}
						}
					}
				}
			}
			// Set Repository Synchronization Status
			repository.setRepositorySyncStatus(repositoryStatus);
		}
		return repositoryStatus;
	}

	public String getReport(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.getDownloadReport();
		} else {
			return null;
		}
	}

	public void setReport(String repositoryName, String message) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setDownloadReport(message);
		}
	}

	public int getServerInfoNumberOfConnections(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			ServerInfo serverInfo = repository.getServerInfo();
			if (serverInfo != null) {
				return serverInfo.getNumberOfConnections();
			}
		}
		return 0;
	}

	public int getNumberOfClientConnections(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.getNumberOfClientConnections();
		}
		return 0;
	}

	public void setNumberOfClientConnections(String repositoryName, int value) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setNumberOfClientConnections(value);
		}
	}

	public double getMaximumClientDownloadSpeed(String repositoryName) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			return repository.getMaximumClientDownloadSpeed();
		}
		return 0;
	}

	public void setMaximumClientDownloadSpeed(String repositoryName,
			double value) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.setMaximumClientDownloadSpeed(value);
		}
	}

	public void setConnectionTimeout(String repositoryName,
			String connectionTimeout) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.getProtocol().setConnectionTimeOut(connectionTimeout);
		}
	}

	public void setReadTimeout(String repositoryName, String readTimeout) {

		Repository repository = repositoryDAO.getMap().get(repositoryName);
		if (repository != null) {
			repository.getProtocol().setReadTimeOut(readTimeout);
		}
	}

	public void cancel() {
		this.repositorySHA1Processor.cancel();
		this.repositoryBuildProcessor.cancel();
	}
}
