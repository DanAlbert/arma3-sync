package fr.soe.a3s.dao.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.soe.a3s.controller.ObservableCount;
import fr.soe.a3s.controller.ObserverCount;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.FileAccessMethods;
import fr.soe.a3s.domain.repository.FileAttributes;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.domain.repository.SyncTreeLeaf;
import fr.soe.a3s.domain.repository.SyncTreeNode;

public class RepositoryAddonsCheckerDAO implements DataAccessConstants,
		ObservableCount {

	/** Variables for SHA1 computation */
	private List<Callable<Integer>> callables;
	private Map<String, FileAttributes> mapFiles;
	/** Variables for observerCount Interface */
	private ObserverCount observerCount;
	private long totalNbFiles, nbFiles;

	public void determineLocalSHA1(SyncTreeNode parent, Repository repository)
			throws IOException {

		this.totalNbFiles = 0;
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
		try {
			executor.invokeAll(callables);
		} catch (InterruptedException e) {
			new RuntimeException(
					"SHA1 computation has been anormaly interrupted.");
		}

		executor.shutdownNow();
		System.gc();
	}

	private void generateLocalSHA1(SyncTreeNode syncTreeNode)
			throws IOException {

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
						this.totalNbFiles++;
						Callable<Integer> c = new Callable<Integer>() {
							@Override
							public Integer call() throws IOException {
								String sha1 = FileAccessMethods
										.computeSHA1(file);
								leaf.setLocalSHA1(sha1);
								increment();
								updateObserverCount();
								mapFiles.put(path, new FileAttributes(sha1,
										lastModified));
								return 0;
							}
						};
						callables.add(c);
					} else {
						String sha1 = currentFileAttributes.getSha1();
						leaf.setLocalSHA1(sha1);
					}
				}
			}
		}
	}

	/* observerCount Interface */

	public synchronized void increment() {
		nbFiles++;
	}

	@Override
	public void addObserverCount(ObserverCount obs) {
		this.observerCount = obs;
	}

	@Override
	public void updateObserverCount() {
		observerCount.update((int) (this.nbFiles * 100 / totalNbFiles));
	}
}
