package fr.soe.a3s.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
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

import fr.soe.a3s.controller.ObservableFileSize;
import fr.soe.a3s.controller.ObservableFilesNumber;
import fr.soe.a3s.controller.ObserverFileSize;
import fr.soe.a3s.controller.ObserverFilesNumber;
import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.Changelog;
import fr.soe.a3s.domain.repository.Changelogs;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.domain.repository.SyncTreeLeaf;
import fr.soe.a3s.domain.repository.SyncTreeNode;
import fr.soe.a3s.exception.RepositoryCheckException;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.exception.WritingException;

public class RepositoryBuilderDAO implements DataAccessConstants,
		ObservableFilesNumber, ObservableFileSize {

	private String repositoryPath;
	private ObserverFilesNumber observerFilesNumber;
	private ObserverFileSize observerFileSize;
	private long nbFiles, totalNbFiles;
	private long totalFilesSize;
	private long cumulativeFileSize;
	private boolean error;
	// private List<Thread> listThreads = new ArrayList<Thread>();
	private List<Callable<Integer>> callables;

	@SuppressWarnings("unchecked")
	public void buildRepository(Repository repository)
			throws RepositoryException, WritingException {

		try {
			repositoryPath = repository.getPath();
			final File file = new File(repositoryPath);

			totalFilesSize = FileUtils.sizeOfDirectory(file);
			nbFiles = 0;
			cumulativeFileSize = 0;
			error = false;
			// listThreads = new ArrayList<Thread>();

			/*
			 * Read sync, serverInfo and changelogs file before .a3s folder
			 * deletion
			 */
			Changelogs changelogs = readChangelogs(file);
			SyncTreeDirectory oldSync = readSync(file);
			ServerInfo oldServerInfo = readServerInfo(file);

			/* Remove .a3s folder */
			File folderA3S = new File(file.getAbsolutePath() + "/.a3s");
			if (folderA3S.exists()) {
				FileAccessMethods.deleteDirectory(folderA3S);
			}

			/* Sync */
			callables = new ArrayList<Callable<Integer>>();
			final SyncTreeDirectory sync = new SyncTreeDirectory("racine", null);
			for (File f : file.listFiles()) {
				generateSync(sync, f);
			}
			// attendre();
			ExecutorService executor = Executors.newCachedThreadPool();
			executor.invokeAll(callables);
			executor.shutdownNow();

			if (error) {
				throw new RepositoryException(
						"Build repository failed.\n An unexpected error has occured.\n Debug: try running ArmA3Sync.bat");
			}

			/* ServerInfo */
			ServerInfo serverInfo = new ServerInfo();
			int revision = 1;
			if (oldServerInfo != null) {
				revision = oldServerInfo.getRevision() + 1;
			}
			serverInfo.setRevision(revision);
			serverInfo.setBuildDate(new Date());
			serverInfo.setNumberOfFiles(nbFiles);
			serverInfo.setTotalFilesSize(totalFilesSize);

			/* AutoConfig */
			AutoConfig autoConfig = new AutoConfig();
			autoConfig.setRepositoryName(repository.getName());
			autoConfig.setProtocole(repository.getProtocole());

			/* Changelogs */
			if (changelogs == null || changelogs.getList().isEmpty()) {
				changelogs = new Changelogs();
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
					Map<String, SyncTreeDirectory> mapOldSync = new HashMap<String, SyncTreeDirectory>();
					getAddons(oldSync, mapOldSync);
					Map<String, SyncTreeDirectory> mapSync = new HashMap<String, SyncTreeDirectory>();
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
									if (!listleaf
											.get(i)
											.getSha1()
											.equals(listOldleaf.get(i)
													.getSha1())) {
										changelog.getUpdatedAddons().add(
												addonName);
										break;
									}
								}
							}
						}
					}
				}
			}

			/* Repository */
			repository.setServerInfo(serverInfo);
			String autoConfigURL = repository.getProtocole().getUrl()
					+ AUTOCONFIG_FILE_PATH;
			repository.setAutoConfigURL(autoConfigURL);
			repository.setSync(sync);
			repository.setChangelogs(changelogs);

			/* Write files */
			File folder = new File(file.getAbsolutePath() + "/.a3s");
			folder.mkdir();

			// Sync file
			File syncFile = new File(file.getAbsolutePath() + SYNC_FILE_PATH);
			ObjectOutputStream fWo = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(
							syncFile.getAbsolutePath())));
			if (sync != null)
				fWo.writeObject(sync);
			fWo.close();

			// ServerInfo file
			File serverInfoFile = new File(file.getAbsolutePath()
					+ SERVERINFO_FILE_PATH);
			fWo = new ObjectOutputStream(new GZIPOutputStream(
					new FileOutputStream(serverInfoFile.getAbsolutePath())));
			if (serverInfo != null)
				fWo.writeObject(serverInfo);
			fWo.close();

			// AutoConfig file
			File autoConfigFile = new File(file.getAbsolutePath()
					+ AUTOCONFIG_FILE_PATH);
			fWo = new ObjectOutputStream(new GZIPOutputStream(
					new FileOutputStream(autoConfigFile.getAbsolutePath())));
			if (autoConfig != null)
				fWo.writeObject(autoConfig);
			fWo.close();

			// Changelogs file
			File changelogsFile = new File(file.getAbsolutePath()
					+ CHANGELOG_FILE_PATH);
			fWo = new ObjectOutputStream(new GZIPOutputStream(
					new FileOutputStream(changelogsFile.getAbsolutePath())));
			if (changelogs != null)
				fWo.writeObject(changelogs);
			fWo.close();

		} catch (Exception e) {
			//e.printStackTrace();
			throw new WritingException(e.getMessage());
		}
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

	private void generateSync(final SyncTreeDirectory parent, final File file)
			throws Exception {

		if (file.isDirectory()) {
			SyncTreeDirectory syncTreeDirectory = new SyncTreeDirectory(
					file.getName(), parent);
			parent.addTreeNode(syncTreeDirectory);
			for (File f : file.listFiles()) {
				if (f.getName().toLowerCase().equals("addons")) {
					syncTreeDirectory.setMarkAsAddon(true);
				}
				generateSync(syncTreeDirectory, f);
			}
		} else {
			final SyncTreeLeaf treeSyncTreeLeaf = new SyncTreeLeaf(
					file.getName(), parent);
			parent.addTreeNode(treeSyncTreeLeaf);
			Callable<Integer> c = new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					long size = FileUtils.sizeOf(file);
					treeSyncTreeLeaf.setSize(size);
					String sha1 = FileAccessMethods.computeSHA1(file);
					treeSyncTreeLeaf.setSha1(sha1);
					increment(size);
					updateFileSizeObserver();
					return 0;
				}
			};
			callables.add(c);
			// Thread t = new Thread(new Runnable() {
			// @Override
			// public void run() {
			// try {
			// long size = FileUtils.sizeOf(file);
			// treeSyncTreeLeaf.setSize(size);
			// String sha1 = FileAccessMethods.computeSHA1(file);
			// treeSyncTreeLeaf.setSha1(sha1);
			// increment(size);
			// updateFileSizeObserver();
			// } catch (Exception e) {
			// error = true;
			// e.printStackTrace();
			// }
			// }
			// });
			// listThreads.add(t);
			// t.start();
		}
	}

	// private void attendre() {
	// try {
	// Thread.sleep(100);
	// boolean alive = false;
	// for (Thread t : listThreads) {
	// if (t.isAlive()) {
	// alive = true;
	// break;
	// }
	// }
	// if (alive) {
	// attendre();
	// }
	// } catch (InterruptedException e) {
	// // e.printStackTrace();
	// }
	// }

	public void determineLocalSHA1(SyncTreeNode parent, long numberOfFiles)
			throws Exception {

		totalNbFiles = numberOfFiles;
		nbFiles = 0;
		callables = new ArrayList<Callable<Integer>>();
		// listThreads = new ArrayList<Thread>();
		generateLocalSHA1(parent);
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.invokeAll(callables);
		executor.shutdownNow();
		// attendre();
	}

	private void generateLocalSHA1(SyncTreeNode syncTreeNode) throws Exception {
		if (!syncTreeNode.isLeaf()) {
			SyncTreeDirectory directory = (SyncTreeDirectory) syncTreeNode;
			for (SyncTreeNode n : directory.getList()) {
				generateLocalSHA1(n);
			}
		} else {
			final SyncTreeLeaf leaf = (SyncTreeLeaf) syncTreeNode;
			if (leaf.getDestinationPath() != null) {
				final File file = new File(leaf.getDestinationPath() + "/"
						+ leaf.getName());
				if (file.exists()) {
					// Thread t = new Thread(new Runnable() {
					// @Override
					// public void run() {
					// try {
					// String sha1 = FileAccessMethods
					// .computeSHA1(file);
					// leaf.setLocalSHA1(sha1);
					// increment();
					// updateFilesNumberObserver();
					// } catch (Exception e) {
					// error = true;
					// e.printStackTrace();
					// }
					// }
					// });
					// listThreads.add(t);
					// t.start();
					Callable<Integer> c = new Callable<Integer>() {
						@Override
						public Integer call() throws Exception {
							String sha1 = FileAccessMethods.computeSHA1(file);
							leaf.setLocalSHA1(sha1);
							increment();
							updateFilesNumberObserver();
							return 0;
						}
					};
					callables.add(c);
				}
			}
		}
	}

	public void checkRepository(Repository repository)
			throws RepositoryCheckException {

		File parentFile = new File(repository.getPath());
		SyncTreeDirectory parentTree = repository.getSync();
		check(parentFile, parentTree);
	}

	private void check(File file, SyncTreeNode syncTreeNode)
			throws RepositoryCheckException {

		if (file.isDirectory()) {
			if (!syncTreeNode.getName().equals("racine")) {
				String name = file.getName();
				if (syncTreeNode.isLeaf()) {
					throw new RepositoryCheckException();
				} else if (!syncTreeNode.getName().equals(name)) {
					throw new RepositoryCheckException();
				}
			}

			SyncTreeDirectory syncTreeDirectory = (SyncTreeDirectory) syncTreeNode;

			// Alphabetically ordered files by their name
			Set<File> set1 = new TreeSet<File>();
			for (int i = 0; i < file.listFiles().length; i++) {
				if (!file.listFiles()[i].getName().equals(
						DataAccessConstants.A3S_FOlDER_NAME)) {
					set1.add(file.listFiles()[i]);
				}
			}
			List<File> list1 = new ArrayList<File>();
			list1.addAll(set1);

			Map<String, SyncTreeNode> map = new TreeMap<String, SyncTreeNode>();
			List<SyncTreeNode> list2 = new ArrayList<SyncTreeNode>();
			for (int i = 0; i < syncTreeDirectory.getList().size(); i++) {
				map.put(syncTreeDirectory.getList().get(i).getName()
						.toLowerCase(), syncTreeDirectory.getList().get(i));// lowercase!
			}

			for (Iterator<String> i = map.keySet().iterator(); i.hasNext();) {
				list2.add(map.get(i.next()));
			}

			if (list1.size() != list2.size()) {
				throw new RepositoryCheckException();
			}

			for (int i = 0; i < list1.size(); i++) {
				check(list1.get(i), list2.get(i));
			}
		} else {
			String name = file.getName();
			if (!syncTreeNode.isLeaf()) {
				throw new RepositoryCheckException();
			} else if (!syncTreeNode.getName().equals(name)) {
				throw new RepositoryCheckException();
			}
		}
	}

	private Changelogs readChangelogs(File file) {

		Changelogs changelogs = null;
		try {
			File changelogsFile = new File(file.getAbsolutePath()
					+ CHANGELOG_FILE_PATH);
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

	public synchronized void increment(long size) {
		nbFiles++;
		cumulativeFileSize = cumulativeFileSize + size;
	}

	public synchronized void increment() {
		nbFiles++;
	}

	/* File size controller */
	@Override
	public void addObserverFileSize(ObserverFileSize obs) {
		this.observerFileSize = obs;
	}

	@Override
	public void updateFileSizeObserver() {
		observerFileSize
				.update((int) (this.cumulativeFileSize * 100 / totalFilesSize));
	}

	@Override
	public void delObserverFileSize() {
		this.observerFileSize = null;
	}

	/* Files number controller */
	@Override
	public void addObserverFilesNumber(ObserverFilesNumber obs) {
		this.observerFilesNumber = obs;
	}

	@Override
	public void updateFilesNumberObserver() {
		observerFilesNumber.update((int) (this.nbFiles * 100 / totalNbFiles));
	}

	@Override
	public void delObserverFilesNumber() {
		this.observerFilesNumber = null;
	}

}
