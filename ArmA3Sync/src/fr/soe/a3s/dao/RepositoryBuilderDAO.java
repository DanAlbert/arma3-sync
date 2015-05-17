package fr.soe.a3s.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;

import fr.soe.a3s.constant.Protocol;
import fr.soe.a3s.controller.ObservableFilesNumber3;
import fr.soe.a3s.controller.ObserverFilesNumber3;
import fr.soe.a3s.domain.Http;
import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.Changelog;
import fr.soe.a3s.domain.repository.Changelogs;
import fr.soe.a3s.domain.repository.Events;
import fr.soe.a3s.domain.repository.FileAttributes;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.domain.repository.SyncTreeLeaf;
import fr.soe.a3s.domain.repository.SyncTreeNode;
import fr.soe.a3s.exception.RepositoryCheckException;
import fr.soe.a3s.jazsync.Jazsync;

public class RepositoryBuilderDAO implements DataAccessConstants,
		ObservableFilesNumber3 {

	private long totalNbFiles, totalFilesSize;
	private boolean repositoryContentUpdated;
	/* Variables for SHA1 computation */
	private List<Callable<Integer>> callables;
	private Map<String, FileAttributes> mapFiles;
	private List<SyncTreeLeaf> updatedFiles = new ArrayList<SyncTreeLeaf>();
	/* Variables for ObservableFilesNumber3 Interface */
	private ObserverFilesNumber3 observerFilesNumber3;
	private long nbFiles;

	@SuppressWarnings("unchecked")
	public void buildRepository(Repository repository) throws Exception {

		assert (repository != null);
		String repositoryPath = repository.getPath();
		assert (repositoryPath != null);
		final File repositoryMainDirectory = new File(repository.getPath());
		assert (repositoryMainDirectory.exists());

		/*
		 * Read sync, serverInfo, changelogs and events file before .a3s folder
		 * deletion
		 */
		Changelogs oldChangelogs = readChangelogs(repositoryMainDirectory);
		Events oldEvents = readEvents(repositoryMainDirectory);
		SyncTreeDirectory oldSync = readSync(repositoryMainDirectory);
		ServerInfo oldServerInfo = readServerInfo(repositoryMainDirectory);

		/* Remove .a3s folder */
		File folderA3S = new File(repositoryMainDirectory.getAbsolutePath()
				+ "/.a3s");
		if (folderA3S.exists()) {
			FileAccessMethods.deleteDirectory(folderA3S);
		}

		/* Generate new Sync */
		final SyncTreeDirectory sync = new SyncTreeDirectory("racine", null);
		for (File f : repositoryMainDirectory.listFiles()) {
			generateSync(repository.getExcludedFilesFromBuild(), sync, f);
		}

		/* Determine totalFilesSize and totalNbFiles */
		this.totalFilesSize = FileUtils
				.sizeOfDirectory(repositoryMainDirectory);
		determineTotalNbFiles(sync);// totalNbFiles

		/* Determine SHA1 values for Sync */
		determineRemoteSHA1(sync, repository);

		/* Generate new ServerInfo */
		final ServerInfo serverInfo = new ServerInfo();
		int revision = 1;
		if (oldServerInfo != null) {
			revision = oldServerInfo.getRevision() + 1;
		}
		serverInfo.setRevision(revision);
		serverInfo.setBuildDate(new Date());
		serverInfo.setNumberOfFiles(nbFiles);
		serverInfo.setTotalFilesSize(totalFilesSize);
		serverInfo.setNumberOfConnections(repository.getNumberOfConnections());
		serverInfo.setRepositoryContentUpdated(repositoryContentUpdated);

		int index = repository.getPath().lastIndexOf(File.separator);
		String repositoryName = repository.getPath().substring(index + 1);

		Iterator iterator = repository.getExcludedFoldersFromSync().iterator();
		while (iterator.hasNext()) {
			String path = (String) iterator.next();
			index = path.toLowerCase().indexOf(
					File.separator + repositoryName.toLowerCase());
			String folderPath = path.substring(index + repositoryName.length()
					+ 2);
			folderPath = backslashReplace(folderPath);
			serverInfo.getHiddenFolderPaths().add(folderPath);
		}

		/* Generate new AutoConfig */
		final AutoConfig autoConfig = new AutoConfig();
		autoConfig.setRepositoryName(repository.getName());
		autoConfig.setProtocole(repository.getProtocole());
		autoConfig.getFavoriteServers().addAll(
				repository.getFavoriteServersSetToAutoconfig());

		/* Generate new Changelogs */
		final Changelogs changelogs = new Changelogs();
		if (oldChangelogs == null || oldChangelogs.getList().isEmpty()) {
			Changelog changelog = new Changelog();
			changelogs.getList().add(changelog);
			changelog.setRevision(revision);
			changelog.setBuildDate(new Date());
			getAddonsByName(sync, changelog.getAddons());
			if (revision == 1) {
				for (String stg : changelog.getAddons()) {
					changelog.getNewAddons().add(stg);
				}
			}
		} else {
			Changelog changelog = new Changelog();
			changelogs.getList().addAll(oldChangelogs.getList());
			changelogs.getList().add(changelog);
			if (changelogs.getList().size() > 10) {
				changelogs.getList().remove(0);
			}
			changelog.setRevision(revision);
			changelog.setBuildDate(new Date());
			getAddonsByName(sync, changelog.getAddons());
			Changelog previousChangelog = changelogs.getList().get(
					changelogs.getList().size() - 2);
			for (String stg : changelog.getAddons()) {
				if (!previousChangelog.getAddons().contains(stg)) {
					changelog.getNewAddons().add(stg);
				}
			}
			for (String stg : previousChangelog.getAddons()) {
				if (!changelog.getAddons().contains(stg)) {
					changelog.getDeletedAddons().add(stg);
				}
			}
			if (oldSync != null) {
				Map<String, SyncTreeDirectory> mapOldSync = new HashMap<String, SyncTreeDirectory>();// <AddonName,SyncTreeDirectory>
				getAddons(oldSync, mapOldSync);
				Map<String, SyncTreeDirectory> mapSync = new HashMap<String, SyncTreeDirectory>();// <AddonName,SyncTreeDirectory>
				getAddons(sync, mapSync);
				for (Iterator iter = mapSync.keySet().iterator(); iter
						.hasNext();) {
					String addonName = (String) iter.next();
					if (mapOldSync.containsKey(addonName)) {
						SyncTreeDirectory syncDirectory = mapSync
								.get(addonName);
						SyncTreeDirectory oldSyncDirectory = mapOldSync
								.get(addonName);
						List<SyncTreeLeaf> listOldleaf = new ArrayList<SyncTreeLeaf>();
						getLeafs(oldSyncDirectory, listOldleaf);
						List<SyncTreeLeaf> listleaf = new ArrayList<SyncTreeLeaf>();
						getLeafs(syncDirectory, listleaf);
						Collections.sort(listleaf);
						Collections.sort(listOldleaf);
						if (listleaf.size() != listOldleaf.size()) {
							changelog.getUpdatedAddons().add(addonName);
						} else {
							for (int i = 0; i < listleaf.size(); i++) {
								if (!listleaf.get(i).getSha1()
										.equals(listOldleaf.get(i).getSha1())) {
									changelog.getUpdatedAddons().add(addonName);
									break;
								}
							}
						}
					}
				}
			}
		}

		/* Generate new Events */
		final Events events = new Events();
		if (oldEvents != null) {
			events.getList().addAll(oldEvents.getList());
		}

		/* Repository update */
		repository.setServerInfo(serverInfo);
		String autoConfigURL = AutoConfigURLAccessMethods
				.determineAutoConfigUrl(repository.getProtocole());
		repository.setAutoConfigURL(autoConfigURL);
		repository.setSync(sync);
		repository.setChangelogs(oldChangelogs);

		/* Write files */
		File folder = new File(repositoryMainDirectory.getAbsolutePath()
				+ "/.a3s");
		folder.mkdir();

		// Write Sync file
		File syncFile = new File(repositoryMainDirectory.getAbsolutePath()
				+ SYNC_FILE_PATH);
		if (sync != null) {
			ObjectOutputStream fWo = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(
							syncFile.getAbsolutePath())));
			fWo.writeObject(sync);
			fWo.close();
		}

		// Write ServerInfo file
		File serverInfoFile = new File(
				repositoryMainDirectory.getAbsolutePath()
						+ SERVERINFO_FILE_PATH);
		if (serverInfo != null) {
			ObjectOutputStream fWo = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(
							serverInfoFile.getAbsolutePath())));
			fWo.writeObject(serverInfo);
			fWo.close();
		}

		// Write AutoConfig file
		File autoConfigFile = new File(
				repositoryMainDirectory.getAbsolutePath()
						+ AUTOCONFIG_FILE_PATH);
		if (autoConfig != null) {
			ObjectOutputStream fWo = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(
							autoConfigFile.getAbsolutePath())));
			fWo.writeObject(autoConfig);
			fWo.close();
		}

		// Write Changelogs file
		File changelogsFile = new File(
				repositoryMainDirectory.getAbsolutePath()
						+ CHANGELOGS_FILE_PATH);
		if (changelogs != null) {
			ObjectOutputStream fWo = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(
							changelogsFile.getAbsolutePath())));
			fWo.writeObject(changelogs);
			fWo.close();
		}

		// Write Events file
		File eventsFile = new File(repositoryMainDirectory.getAbsolutePath()
				+ EVENTS_FILE_PATH);
		if (events != null) {
			ObjectOutputStream fWo = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(
							eventsFile.getAbsolutePath())));
			fWo.writeObject(events);
			fWo.close();
		}

		/* Write .zsync files for HTTP based Repository */
		if (repository.getProtocole() instanceof Http) {
			determineZSyncFiles(sync, repository.getProtocole().getUrl());
		}
	}

	private void determineTotalNbFiles(SyncTreeNode node) {

		if (!node.isLeaf()) {
			SyncTreeDirectory directory = (SyncTreeDirectory) node;
			for (SyncTreeNode n : directory.getList()) {
				determineTotalNbFiles(n);
			}
		} else {
			this.totalNbFiles++;
		}
	}

	private String backslashReplace(String myStr) {

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

	private void getAddonsByName(final SyncTreeDirectory syncTreeDirectory,
			List<String> newAddons) {

		for (SyncTreeNode node : syncTreeDirectory.getList()) {
			if (!node.isLeaf()) {
				SyncTreeDirectory directory = (SyncTreeDirectory) node;
				if (directory.isMarkAsAddon()) {
					newAddons.add(directory.getName());
				} else {
					getAddonsByName(directory, newAddons);
				}
			}
		}
	}

	private void getAddons(final SyncTreeDirectory syncTreeDirectory,
			Map<String, SyncTreeDirectory> map) {

		for (SyncTreeNode node : syncTreeDirectory.getList()) {
			if (!node.isLeaf()) {
				SyncTreeDirectory directory = (SyncTreeDirectory) node;
				if (directory.isMarkAsAddon()) {
					map.put(directory.getName(), directory);
				} else {
					getAddons(directory, map);
				}
			}
		}
	}

	private void getLeafs(SyncTreeDirectory syncTreeDirectory,
			List<SyncTreeLeaf> listleaf) {

		for (SyncTreeNode node : syncTreeDirectory.getList()) {
			if (node.isLeaf()) {
				SyncTreeLeaf leaf = (SyncTreeLeaf) node;
				listleaf.add(leaf);
			} else {
				SyncTreeDirectory directory = (SyncTreeDirectory) node;
				getLeafs(directory, listleaf);
			}
		}
	}

	private void generateSync(Set<String> excludedFilesFromBuild,
			final SyncTreeDirectory parent, final File file) throws Exception {

		if (file.isDirectory()) {
			if (!file.getName().contains(A3S_FOlDER_NAME)) {// always true
															// unless
															// deleteDitrectory(.a3s)
															// failed
				SyncTreeDirectory syncTreeDirectory = new SyncTreeDirectory(
						file.getName(), parent);
				parent.addTreeNode(syncTreeDirectory);
				syncTreeDirectory.setDestinationPath(file.getParentFile()
						.getAbsolutePath());

				for (File f : file.listFiles()) {
					if (f.getName().toLowerCase().equals("addons")) {
						syncTreeDirectory.setMarkAsAddon(true);
					}
					generateSync(excludedFilesFromBuild, syncTreeDirectory, f);
				}
			}
		}
		// exclude .zsync files
		else if (!file.getName().contains(ZSYNC_EXTENSION)
				&& !file.getName().contains(A3S_FOlDER_NAME)
				&& !excludedFilesFromBuild.contains(file.getAbsolutePath())) {

			final SyncTreeLeaf treeSyncTreeLeaf = new SyncTreeLeaf(
					file.getName(), parent);
			parent.addTreeNode(treeSyncTreeLeaf);
			treeSyncTreeLeaf.setDestinationPath(file.getParentFile()
					.getAbsolutePath());
			long size = FileUtils.sizeOf(file);
			treeSyncTreeLeaf.setSize(size);

			/*
			 * Callable<Integer> c = new Callable<Integer>() {
			 * 
			 * @Override public Integer call() throws Exception { long size =
			 * FileUtils.sizeOf(file); treeSyncTreeLeaf.setSize(size); String
			 * sha1 = FileAccessMethods.computeSHA1(file); if
			 * (protocole.equals(Protocol.HTTP)) { String url =
			 * Protocol.HTTP.getPrompt() + repositortUrl + "/" +
			 * determinePath(treeSyncTreeLeaf); Jazsync.make(file, url, sha1); }
			 * treeSyncTreeLeaf.setSha1(sha1); increment(size);
			 * updateFileSizeObserver2(); return 0; } }; callables.add(c);
			 */
		}
	}

	private void determineRemoteSHA1(SyncTreeNode parent, Repository repository)
			throws Exception {

		this.callables = new ArrayList<Callable<Integer>>();
		this.mapFiles = repository.getMapFilesForBuild();
		this.nbFiles = 0;
		this.repositoryContentUpdated = false;
		this.updatedFiles = new ArrayList<SyncTreeLeaf>();

		// Remove no more existing files on disk from mapFiles
		List<String> paths = new ArrayList<String>();
		for (Iterator<String> iter = mapFiles.keySet().iterator(); iter
				.hasNext();) {
			String path = iter.next();
			File file = new File(path);
			if (!file.exists()) {
				paths.add(path);
			}
		}

		for (String path : paths) {
			mapFiles.remove(path);
			this.repositoryContentUpdated = true;
		}

		// Compute SHA1 for files on disk
		generateRemoteSHA1(parent);

		ExecutorService executor = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());
		executor.invokeAll(callables);

		executor.shutdownNow();
		System.gc();
	}

	private void generateRemoteSHA1(SyncTreeNode syncTreeNode) {

		if (!syncTreeNode.isLeaf()) {
			SyncTreeDirectory directory = (SyncTreeDirectory) syncTreeNode;
			for (SyncTreeNode n : directory.getList()) {
				generateRemoteSHA1(n);
			}
		} else {
			final SyncTreeLeaf leaf = (SyncTreeLeaf) syncTreeNode;
			if (leaf.getDestinationPath() != null) {
				final File file = new File(leaf.getDestinationPath() + "/"
						+ leaf.getName());
				if (file.exists()) {
					boolean compute = false;
					final String path = file.getAbsolutePath();
					final long lastModified = file.lastModified();
					FileAttributes currentFileAttributes = mapFiles.get(path);
					if (currentFileAttributes != null) {
						String currentSHA1 = currentFileAttributes.getSha1();
						long currentLastModified = currentFileAttributes
								.getLastModified();
						if (lastModified != currentLastModified
								|| currentSHA1 == null) {
							compute = true;
						} else {
							compute = false;
						}
					} else {
						compute = true;
					}

					if (compute) {
						Callable<Integer> c = new Callable<Integer>() {
							@Override
							public Integer call() throws Exception {
								String sha1 = FileAccessMethods
										.computeSHA1(file);
								leaf.setSha1(sha1);
								updatedFiles.add(leaf);
								increment();
								updateFilesNumberObserver3();
								mapFiles.put(path, new FileAttributes(sha1,
										lastModified));
								repositoryContentUpdated = true;
								return 0;
							}
						};
						callables.add(c);
					} else {
						String sha1 = currentFileAttributes.getSha1();
						leaf.setSha1(sha1);
						increment();
						updateFilesNumberObserver3();
					}
				}
			}
		}
	}

	private void determineZSyncFiles(SyncTreeNode sync, String repositortUrl)
			throws Exception {

		this.callables = new ArrayList<Callable<Integer>>();
		this.nbFiles = 0;

		generateZsyncFiles(sync, repositortUrl);

		ExecutorService executor = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());
		executor.invokeAll(callables);

		executor.shutdownNow();
		System.gc();
	}

	private void generateZsyncFiles(SyncTreeNode syncTreeNode,
			String repositortUrl) throws Exception {

		if (!syncTreeNode.isLeaf()) {
			SyncTreeDirectory directory = (SyncTreeDirectory) syncTreeNode;
			for (SyncTreeNode n : directory.getList()) {
				generateZsyncFiles(n, repositortUrl);
			}
		} else {
			final SyncTreeLeaf leaf = (SyncTreeLeaf) syncTreeNode;
			final File file = new File(leaf.getDestinationPath() + "/"
					+ leaf.getName());
			final File zsyncFile = new File(file.getParentFile() + "/"
					+ file.getName() + ".zsync");
			final String url = Protocol.HTTP.getPrompt() + repositortUrl + "/"
					+ determinePath(leaf);
			boolean compute = false;
			if (updatedFiles.contains(leaf)) {
				compute = true;
			} else if (!zsyncFile.exists()) {
				compute = true;
			}

			if (compute) {
				Callable<Integer> c = new Callable<Integer>() {
					@Override
					public Integer call() throws Exception {
						Jazsync.make(file, zsyncFile, url, leaf.getSha1());
						increment();
						updateFilesNumberObserver3();
						return 0;
					}
				};
				callables.add(c);
			} else {
				increment();
				updateFilesNumberObserver3();
			}
		}
	}

	private String determinePath(SyncTreeNode syncTreeNode) {

		assert (syncTreeNode.getParent() != null);
		String path = syncTreeNode.getName();
		while (syncTreeNode.getParent().getName() != "racine") {
			path = syncTreeNode.getParent().getName() + "/" + path;
			syncTreeNode = syncTreeNode.getParent();
		}
		return path;
	}

	private Changelogs readChangelogs(File file) {

		Changelogs changelogs = null;
		try {
			File changelogsFile = new File(file.getAbsolutePath()
					+ CHANGELOGS_FILE_PATH);
			if (changelogsFile.exists()) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(changelogsFile)));
				if (fRo != null) {
					changelogs = (Changelogs) fRo.readObject();
					fRo.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return changelogs;
	}

	private Events readEvents(File file) {

		Events events = null;
		try {
			File eventsFile = new File(file.getAbsolutePath()
					+ EVENTS_FILE_PATH);
			if (eventsFile.exists()) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(eventsFile)));
				if (fRo != null) {
					events = (Events) fRo.readObject();
					fRo.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return events;
	}

	private SyncTreeDirectory readSync(File file) {

		SyncTreeDirectory sync = null;
		try {
			File syncFile = new File(file.getAbsolutePath() + SYNC_FILE_PATH);
			if (syncFile.exists()) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(syncFile)));

				if (fRo != null) {
					sync = (SyncTreeDirectory) fRo.readObject();
					fRo.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sync;
	}

	private ServerInfo readServerInfo(File file) {

		ServerInfo serverInfo = null;
		try {
			File serverInfoFile = new File(file.getAbsolutePath()
					+ SERVERINFO_FILE_PATH);
			if (serverInfoFile.exists()) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(serverInfoFile)));

				if (fRo != null) {
					serverInfo = (ServerInfo) fRo.readObject();
					fRo.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverInfo;
	}

	/* */

	public synchronized void increment() {
		nbFiles++;
	}

	@Override
	public void addObserverFilesNumber3(ObserverFilesNumber3 obs) {
		this.observerFilesNumber3 = obs;
	}

	@Override
	public void updateFilesNumberObserver3() {
		observerFilesNumber3.update((int) (this.nbFiles * 100 / totalNbFiles));
	}
}
