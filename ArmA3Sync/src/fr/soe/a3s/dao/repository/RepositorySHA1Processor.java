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
import fr.soe.a3s.domain.repository.SyncTreeLeaf;

public class RepositorySHA1Processor implements ObservableCount,
		DataAccessConstants {

	/** Parameters */
	private List<SyncTreeLeaf> filesToCompute = null;
	private Map<String, FileAttributes> mapFiles = null;
	private boolean isLocalSHA1Computation = false;
	/** observable count Interface */
	private ObserverCount observerCount;
	protected int count = 0, totalCount = 0;
	/** */
	private List<Callable<Integer>> callables = null;
	private boolean contentUpdated = false;
	private List<SyncTreeLeaf> updatedFiles = null;
	private boolean canceled = false;
	private IOException ex = null;

	public void init(List<SyncTreeLeaf> filesToCompute,
			Map<String, FileAttributes> mapFiles, boolean isLocalSHA1Computation) {
		this.filesToCompute = filesToCompute;
		this.mapFiles = mapFiles;
		this.updatedFiles = new ArrayList<SyncTreeLeaf>();
		this.isLocalSHA1Computation = isLocalSHA1Computation;
	}

	public void run() throws IOException {
		// Remove no more existing files on disk from mapFiles
		List<String> removedPaths = new ArrayList<String>();
		for (Iterator<String> iter = mapFiles.keySet().iterator(); iter
				.hasNext();) {
			String path = iter.next();
			File file = new File(path);
			if (!file.exists()) {
				removedPaths.add(path);
			}
		}

		for (String path : removedPaths) {
			mapFiles.remove(path);
		}

		this.callables = new ArrayList<Callable<Integer>>();
		this.totalCount = 0;
		this.count = 0;

		// Compute SHA1 for files on disk
		compute();

		// Update contentUpdated
		if (removedPaths.size() != 0 || callables.size() != 0) {
			contentUpdated = true;
		}

		ExecutorService executor = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());
		try {
			executor.invokeAll(callables);
		} catch (InterruptedException e) {
			new RuntimeException(
					"SHA1 computation has been anormaly interrupted.");
		}

		System.out
				.println("Number of SHA1 computed = " + this.callables.size());

		executor.shutdownNow();
		System.gc();

		if (ex != null) {
			throw ex;
		}
	}

	private void compute() throws IOException {

		for (final SyncTreeLeaf leaf : filesToCompute) {
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
						this.totalCount++;
						Callable<Integer> c = new Callable<Integer>() {
							@Override
							public Integer call() {
								if (!canceled) {
									try {
										String sha1 = FileAccessMethods
												.computeSHA1(file);
										if (isLocalSHA1Computation) {
											leaf.setLocalSHA1(sha1);
										} else {
											leaf.setSha1(sha1);
										}
										updatedFiles.add(leaf);
										increment();
										updateObserverCount();
										mapFiles.put(path, new FileAttributes(
												sha1, lastModified));
									} catch (IOException e) {
										ex = e;
										canceled = true;
									}
								}
								return 0;
							}
						};
						callables.add(c);
					} else {
						String sha1 = currentFileAttributes.getSha1();
						if (isLocalSHA1Computation) {
							leaf.setLocalSHA1(sha1);
						} else {
							leaf.setSha1(sha1);
						}
					}
				}
			}
		}
	}

	private synchronized void increment() {
		this.count++;
	}

	/* Getters and Setters */

	public boolean isCanceled() {
		return canceled;
	}

	public void cancel() {
		this.canceled = true;
	}

	public boolean isContentUpdated() {
		return contentUpdated;
	}

	public List<SyncTreeLeaf> getUpdatedFiles() {
		return this.updatedFiles;
	}

	/* observable Count Inteface */

	@Override
	public void addObserverCount(ObserverCount obs) {
		this.observerCount = obs;
	}

	@Override
	public void updateObserverCount() {
		observerCount.update(this.count * 100 / this.totalCount);
	}
}
