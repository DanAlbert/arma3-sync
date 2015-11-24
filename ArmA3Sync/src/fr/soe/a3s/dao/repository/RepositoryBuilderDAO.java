package fr.soe.a3s.dao.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;

import fr.soe.a3s.controller.ObservableCountWithText;
import fr.soe.a3s.controller.ObserverCountWithText;
import fr.soe.a3s.dao.A3SFilesAccessor;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.FileAccessMethods;
import fr.soe.a3s.dao.connection.AutoConfigURLAccessMethods;
import fr.soe.a3s.dao.zip.ZipBatchProcessor;
import fr.soe.a3s.domain.AbstractProtocole;
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
import fr.soe.a3s.service.jazsync.Jazsync;

public class RepositoryBuilderDAO implements DataAccessConstants,
		ObservableCountWithText {

	/** ServerInfo variables */
	private long numberOfFiles;
	private boolean repositoryContentUpdated;

	/** Variables for SHA1 computation */
	private List<Callable<Integer>> callables;
	private Map<String, FileAttributes> mapFiles;
	private List<SyncTreeLeaf> updatedFiles = new ArrayList<SyncTreeLeaf>();

	/** Variables for ObservableCountText Interface */
	private ObserverCountWithText observerCountWithText;
	private int count;
	private int totalCount;

	/** pbo files compression */
	private ZipBatchProcessor zipBatchProcessor = null;
	private double compressionRatio;

	/** Cancel build */
	private boolean canceled = false;
	private IOException ex = null;

	@SuppressWarnings("unchecked")
	public void buildRepository(Repository repository) throws IOException {

		assert (repository != null);
		String repositoryPath = repository.getPath();
		assert (repositoryPath != null);
		final File repositoryMainDirectory = new File(repository.getPath());
		assert (repositoryMainDirectory.exists());

		/*
		 * Read sync, serverInfo, changelogs and events file before .a3s folder
		 * deletion
		 */
		Changelogs oldChangelogs = A3SFilesAccessor
				.readChangelogsFile(new File(repositoryMainDirectory
						.getAbsolutePath() + CHANGELOGS_FILE_PATH));
		Events oldEvents = A3SFilesAccessor.readEventsFile(new File(
				repositoryMainDirectory.getAbsolutePath() + EVENTS_FILE_PATH));
		SyncTreeDirectory oldSync = A3SFilesAccessor.readSyncFile(new File(
				repositoryMainDirectory.getAbsolutePath() + SYNC_FILE_PATH));
		ServerInfo oldServerInfo = A3SFilesAccessor
				.readServerInfoFile(new File(repositoryMainDirectory
						.getAbsolutePath() + SERVERINFO_FILE_PATH));

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
		long totalFilesSize = FileUtils
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
		serverInfo.setNumberOfFiles(numberOfFiles);
		serverInfo.setTotalFilesSize(totalFilesSize);
		serverInfo.setNumberOfConnections(repository.getNumberOfConnections());
		serverInfo.setNoPartialFileTransfer((!repository
				.isUsePartialFileTransfer()));
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
		autoConfig.setProtocole(repository.getProtocol());
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
		repository.setSync(sync);
		repository.setServerInfo(serverInfo);
		String autoConfigURL = AutoConfigURLAccessMethods
				.determineAutoConfigUrl(repository.getProtocol());
		repository.setAutoConfigURL(autoConfigURL);
		repository.setChangelogs(changelogs);

		/* Determine .zsync files for HTTP based Repository */
		if (repository.getProtocol() instanceof Http) {
			determineZSyncFiles(sync, repository.getProtocol());
		}

		/* Determine .zip files */
		if (repository.isCompressed()) {
			determineZipPboFilesToAdd(sync);

		} else {
			determineZipFilesToDelete(sync);

		}

		/* Write files */
		File folder = new File(repositoryMainDirectory.getAbsolutePath()
				+ "/.a3s");
		folder.mkdir();

		// Write Sync file
		File syncFile = new File(repositoryMainDirectory.getAbsolutePath()
				+ SYNC_FILE_PATH);
		A3SFilesAccessor.writeSync(sync, syncFile);

		// Write ServerInfo file
		File serverInfoFile = new File(
				repositoryMainDirectory.getAbsolutePath()
						+ SERVERINFO_FILE_PATH);
		A3SFilesAccessor.writeServerInfo(serverInfo, serverInfoFile);

		// Write Changelogs file
		File changelogsFile = new File(
				repositoryMainDirectory.getAbsolutePath()
						+ CHANGELOGS_FILE_PATH);
		A3SFilesAccessor.writeChangelogs(changelogs, changelogsFile);

		// Write AutoConfig file
		File autoConfigFile = new File(
				repositoryMainDirectory.getAbsolutePath()
						+ AUTOCONFIG_FILE_PATH);
		A3SFilesAccessor.writeAutoConfig(autoConfig, autoConfigFile);

		// Write Events file
		File eventsFile = new File(repositoryMainDirectory.getAbsolutePath()
				+ EVENTS_FILE_PATH);
		A3SFilesAccessor.writeEvents(events, eventsFile);
	}

	private void determineTotalNbFiles(SyncTreeNode node) {

		if (!node.isLeaf()) {
			SyncTreeDirectory directory = (SyncTreeDirectory) node;
			for (SyncTreeNode n : directory.getList()) {
				determineTotalNbFiles(n);
			}
		} else {
			this.numberOfFiles++;
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
			final SyncTreeDirectory parent, final File file) {

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
		// exclude .zsync and .pbo.7z files
		else if (!file.getName().contains(ZSYNC_EXTENSION)
				&& !file.getName().contains(PBO_ZIP_EXTENSION)
				&& !file.getName().contains(A3S_FOlDER_NAME)
				&& !excludedFilesFromBuild.contains(file.getAbsolutePath())) {

			final SyncTreeLeaf treeSyncTreeLeaf = new SyncTreeLeaf(
					file.getName(), parent);
			parent.addTreeNode(treeSyncTreeLeaf);
			treeSyncTreeLeaf.setDestinationPath(file.getParentFile()
					.getAbsolutePath());
			long size = FileUtils.sizeOf(file);
			treeSyncTreeLeaf.setSize(size);
		}
	}

	private void determineRemoteSHA1(SyncTreeNode parent, Repository repository) throws IOException {

		this.callables = new ArrayList<Callable<Integer>>();
		this.mapFiles = repository.getMapFilesForBuild();
		this.count = 0;
		this.totalCount = 0;
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
		updateObserverCountWithText("Processing SHA1 signatures...");
		generateRemoteSHA1(parent);

		ExecutorService executor = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());
		try {
			executor.invokeAll(callables);
		} catch (InterruptedException e) {
			throw new RuntimeException(
					"SHA1 computation has been anormaly interrupted");
		}

		executor.shutdownNow();
		System.gc();

		if (ex != null) {
			throw ex;
		}
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
						totalCount++;
						Callable<Integer> c = new Callable<Integer>() {
							@Override
							public Integer call() {
								if (!canceled) {
									try {
										String sha1 = FileAccessMethods
												.computeSHA1(file);
										leaf.setSha1(sha1);
										updatedFiles.add(leaf);
										increment();
										updateObserverCountWithText();
										mapFiles.put(path, new FileAttributes(
												sha1, lastModified));
										repositoryContentUpdated = true;
									} catch (IOException e) {
										canceled = true;
										ex = e;
									}
								}
								return 0;
							}
						};
						callables.add(c);
					} else {
						String sha1 = currentFileAttributes.getSha1();
						leaf.setSha1(sha1);
					}
				}
			}
		}
	}

	private void determineZSyncFiles(SyncTreeNode sync,
			AbstractProtocole protocol) throws IOException {

		this.callables = new ArrayList<Callable<Integer>>();
		this.count = 0;
		this.totalCount = 0;

		// Generates zsync files on disk
		updateObserverCountWithText("Processing *"
				+ DataAccessConstants.ZSYNC_EXTENSION + " files...");
		generateZsyncFiles(sync, protocol);

		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			executor.invokeAll(callables);
		} catch (InterruptedException e) {
			throw new RuntimeException(
					"ZSync files generation has been anormaly interrupted");
		}

		executor.shutdownNow();
		System.gc();

		if (ex != null) {
			throw ex;
		}
	}

	private void generateZsyncFiles(SyncTreeNode syncTreeNode,
			AbstractProtocole protocol) {

		if (!syncTreeNode.isLeaf()) {
			SyncTreeDirectory directory = (SyncTreeDirectory) syncTreeNode;
			for (SyncTreeNode n : directory.getList()) {
				generateZsyncFiles(n, protocol);
			}
		} else {
			final SyncTreeLeaf leaf = (SyncTreeLeaf) syncTreeNode;

			if (leaf.getDestinationPath() != null) {
				final File file = new File(leaf.getDestinationPath() + "/"
						+ leaf.getName());
				if (file.exists()) {
					final File zsyncFile = new File(file.getParentFile() + "/"
							+ file.getName() + ZSYNC_EXTENSION);
					final String url = protocol.getProtocolType().getPrompt()
							+ protocol.getUrl() + "/" + determinePath(leaf);

					boolean compute = false;
					if (updatedFiles.contains(leaf)) {
						compute = true;
					} else if (!zsyncFile.exists()) {
						compute = true;
					}

					if (compute) {
						totalCount++;
						Callable<Integer> c = new Callable<Integer>() {
							@Override
							public Integer call() {
								if (!canceled) {
									try {
										Jazsync.make(file, zsyncFile, url,
												leaf.getSha1());
										increment();
										updateObserverCountWithText();
									} catch (IOException e) {
										canceled = true;
										ex = e;
									}
								}
								return 0;
							}
						};
						callables.add(c);
					}
				}
			}
		}
	}

	private void determineZipPboFilesToAdd(SyncTreeDirectory sync)
			throws IOException {

		updateObserverCountWithText("Processing *"
				+ DataAccessConstants.PBO_ZIP_EXTENSION + " files...");
		List<SyncTreeLeaf> list = new ArrayList<SyncTreeLeaf>();
		getPboFiles(sync, list);

		zipBatchProcessor = new ZipBatchProcessor();
		zipBatchProcessor.init(list);
		zipBatchProcessor.addObserverCountWithText(observerCountWithText);
		zipBatchProcessor.zipBatch();
		zipBatchProcessor = null;
		System.gc();
		// determineCompressionRatio(sync);
		// System.out.println(compressionRatio);

	}

	private void determineCompressionRatio(SyncTreeNode node) {

		if (node.isLeaf()) {
			SyncTreeLeaf leaf = (SyncTreeLeaf) node;
			double ratio = (leaf.getSize() - leaf.getCompressedSize())
					/ leaf.getSize();
			compressionRatio = (compressionRatio + ratio) / 2;
		} else {
			SyncTreeDirectory directory = (SyncTreeDirectory) node;
			for (SyncTreeNode n : directory.getList()) {
				determineCompressionRatio(n);
			}
		}
	}

	private void getPboFiles(SyncTreeNode syncTreeNode, List<SyncTreeLeaf> list) {

		if (!syncTreeNode.isLeaf()) {
			SyncTreeDirectory directory = (SyncTreeDirectory) syncTreeNode;
			for (SyncTreeNode n : directory.getList()) {
				getPboFiles(n, list);
			}
		} else {
			final SyncTreeLeaf leaf = (SyncTreeLeaf) syncTreeNode;

			int index = leaf.getName().lastIndexOf(".");
			String extension = "";
			if (index != -1) {
				extension = leaf.getName().substring(index);
			}
			if (extension.toLowerCase().equals(PBO_EXTENSION)) {
				File zipFile = new File(leaf.getDestinationPath() + "/"
						+ leaf.getName() + ZIP_EXTENSION);
				boolean compute = false;
				if (updatedFiles.contains(leaf)) {
					// Force delete in case of the user has stopped the process
					// previously
					FileAccessMethods.deleteFile(zipFile);
					compute = true;
				} else if (!zipFile.exists()) {
					compute = true;
				} else {
					leaf.setCompressed(true);
					leaf.setCompressedSize(zipFile.length());
				}
				if (compute) {
					list.add(leaf);
				}
			}
		}
	}

	private void determineZipFilesToDelete(SyncTreeDirectory sync)
			throws IOException {

		this.callables = new ArrayList<Callable<Integer>>();
		this.count = 0;
		this.totalCount = 0;

		// Deletes 7z files on disk
		observerCountWithText.update("");
		deleteZipFiles(sync);

		if (callables.size() > 0) {
			observerCountWithText.update("Deleting *"
					+ DataAccessConstants.PBO_ZIP_EXTENSION + " files...");
			ExecutorService executor = Executors.newSingleThreadExecutor();
			try {
				executor.invokeAll(callables);
			} catch (InterruptedException e) {
				new RuntimeException(
						"Zip files deletion has been anormaly interrupted.");
			}
			executor.shutdownNow();
			System.gc();
		}
	}

	private void deleteZipFiles(SyncTreeNode syncTreeNode) {

		if (!syncTreeNode.isLeaf()) {
			SyncTreeDirectory directory = (SyncTreeDirectory) syncTreeNode;
			for (SyncTreeNode n : directory.getList()) {
				deleteZipFiles(n);
			}
		} else {
			final SyncTreeLeaf leaf = (SyncTreeLeaf) syncTreeNode;

			leaf.setCompressed(false);

			if (leaf.getDestinationPath() != null) {

				int index = leaf.getName().lastIndexOf(".");
				String extension = "";
				if (index != -1) {
					extension = leaf.getName().substring(index);
				}

				if (extension.toLowerCase().equals(PBO_EXTENSION)) {// *.pbo
					final File file = new File(leaf.getDestinationPath() + "/"
							+ leaf.getName());

					final File sevenZipFile = new File(file.getParentFile()
							+ "/" + file.getName() + ZIP_EXTENSION);

					totalCount++;

					Callable<Integer> c = new Callable<Integer>() {
						@Override
						public Integer call() throws Exception {
							FileAccessMethods.deleteFile(sevenZipFile);
							leaf.setCompressed(false);
							increment();
							updateObserverCountWithText();
							return 0;
						}
					};
					callables.add(c);
				}
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

	public void cancel() {
		this.canceled = true;
		if (zipBatchProcessor != null) {
			zipBatchProcessor.cancel();
		}
	}

	/* Interface observableFilesNumber3 */

	public synchronized void increment() {
		count++;
	}

	@Override
	public void addObserverCountWithText(ObserverCountWithText obs) {
		this.observerCountWithText = obs;
	}

	@Override
	public void updateObserverCountWithText() {
		this.observerCountWithText.update(this.count * 100 / this.totalCount);
	}

	@Override
	public void updateObserverCountWithText(String text) {
		this.observerCountWithText.update(text);
	}
}
