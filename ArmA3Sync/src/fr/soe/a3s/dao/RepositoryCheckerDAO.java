package fr.soe.a3s.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.soe.a3s.controller.ObservableFilesNumber3;
import fr.soe.a3s.controller.ObserverFilesNumber3;
import fr.soe.a3s.domain.repository.FileAttributes;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.domain.repository.SyncTreeLeaf;
import fr.soe.a3s.domain.repository.SyncTreeNode;
import fr.soe.a3s.exception.RepositoryCheckException;

public class RepositoryCheckerDAO implements DataAccessConstants,ObservableFilesNumber3{

	private long totalNbFiles, totalFilesSize;
	private boolean repositoryContentUpdated;
	/* Variables for SHA1 computation */
	private List<Callable<Integer>> callables;
	private Map<String, FileAttributes> mapFiles;
	/* Variables for ObservableFilesNumber3 Interface */
	private ObserverFilesNumber3 observerFilesNumber3;
	private long nbFiles, cumulativeFileSize;
	
	public void checkRepository(Repository repository)
			throws RepositoryCheckException {

		File parentFile = new File(repository.getPath());
		SyncTreeDirectory parentTree = repository.getSync();
		Set<String> excludesFiles = repository.getExcludedFilesFromBuild();
		List<String> errorMessages = new ArrayList<String>();
		check(parentFile, parentTree, excludesFiles, errorMessages);

		if (!errorMessages.isEmpty()) {
			String response = "";
			for (String error : errorMessages) {
				response = response + "*" + error;
			}
			throw new RepositoryCheckException(response);
		}
	}

	private void check(File file, SyncTreeNode syncTreeNode,
			Set<String> excludesFiles, List<String> errorMessages) {

		if (file.isDirectory()) {
			boolean error = false;
			if (!syncTreeNode.getName().equals("racine")) {
				String name = file.getName();
				if (syncTreeNode.isLeaf()) {
					// error = true;
					errorMessages.add("Error with file type leaf: "
							+ file.getAbsolutePath());
					return;
				} else if (!syncTreeNode.getName().equals(name)) {
					errorMessages.add("Error with file name: "
							+ file.getAbsolutePath());
					return;
				}
			}

			SyncTreeDirectory syncTreeDirectory = (SyncTreeDirectory) syncTreeNode;

			// Alphabetically ordered files by their name
			Set<File> set1 = new TreeSet<File>();
			for (int i = 0; i < file.listFiles().length; i++) {
				File f = file.listFiles()[i];
				String path = f.getAbsolutePath();
				if (!f.getName().equals(DataAccessConstants.A3S_FOlDER_NAME)
						&& !f.getName().contains(ZSYNC_EXTENSION)
						&& !excludesFiles.contains(path)) {
					set1.add(f);
				}
			}
			List<File> list1 = new ArrayList<File>();
			list1.addAll(set1);

			Map<String, SyncTreeNode> map = new TreeMap<String, SyncTreeNode>();
			List<SyncTreeNode> list2 = new ArrayList<SyncTreeNode>();
			for (int i = 0; i < syncTreeDirectory.getList().size(); i++) {
				map.put(syncTreeDirectory.getList().get(i).getName(),
						syncTreeDirectory.getList().get(i));
			}

			for (Iterator<String> i = map.keySet().iterator(); i.hasNext();) {
				list2.add(map.get(i.next()));
			}

			/*
			 * ! order of files (Linux/Windows)
			 * http://stackoverflow.com
			 * /questions/10783195/java-file-sorting-order-in-
			 * windows-and-linux-difference
			 */
			String osName = System.getProperty("os.name");
			if (osName.contains("Windows")) {
				Collections.sort(list1);
				Collections.sort(list2);
			}

			if (list1.size() != list2.size()) {
				errorMessages.add("Error with directory size content: "
						+ file.getAbsolutePath());
			} else {
				for (int i = 0; i < list1.size(); i++) {
					check(list1.get(i), list2.get(i), excludesFiles,
							errorMessages);
				}
			}
		} else {
			if (file.getName().contains(ZSYNC_EXTENSION)) {
				return;
			} else if (excludesFiles.contains(file.getAbsolutePath())) {
				return;
			} else if (!syncTreeNode.isLeaf()) {
				errorMessages.add("Error with file: " + file.getAbsolutePath());
			} else if (!syncTreeNode.getName().equals(file.getName())) {
				errorMessages.add("Error with file: " + file.getAbsolutePath());
			}
		}
	}
	
	public void determineLocalSHA1(SyncTreeNode parent, Repository repository)
			throws Exception {

		this.totalNbFiles = repository.getServerInfo().getNumberOfFiles();
		this.nbFiles = 0;
		this.callables = new ArrayList<Callable<Integer>>();
		this.mapFiles = repository.getMapFilesForSync();

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
		}

		// Compute SHA1 for files on disk
		generateLocalSHA1(parent);

		ExecutorService executor = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());
		executor.invokeAll(callables);

		executor.shutdownNow();
		System.gc();
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
								leaf.setLocalSHA1(sha1);
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
						leaf.setLocalSHA1(sha1);
						increment();
						updateFilesNumberObserver3();
					}
				}
			}
		}
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
