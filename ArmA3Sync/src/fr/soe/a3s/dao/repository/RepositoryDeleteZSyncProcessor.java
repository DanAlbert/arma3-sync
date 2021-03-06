package fr.soe.a3s.dao.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.soe.a3s.controller.ObservableCountInt;
import fr.soe.a3s.controller.ObserverCountInt;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.FileAccessMethods;
import fr.soe.a3s.domain.repository.SyncTreeLeaf;

public class RepositoryDeleteZSyncProcessor implements ObservableCountInt,
		DataAccessConstants {

	/** Parameters */
	private List<SyncTreeLeaf> filesToDelete = null;
	/** observable count Interface */
	private ObserverCountInt observerCount;
	protected int count = 0, totalCount = 0;
	/***/
	private List<Callable<Integer>> callables = null;
	private boolean canceled = false;

	public void init(List<SyncTreeLeaf> filesToDelete) {
		this.filesToDelete = filesToDelete;
		this.callables = new ArrayList<Callable<Integer>>();
		this.totalCount = 0;
		this.count = 0;
	}

	public void run() {

		delete();

		ExecutorService executor = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());
		try {
			executor.invokeAll(callables);
		} catch (InterruptedException e) {
			new RuntimeException(
					"Delete zsync files process has been anormaly interrupted.");
		}

		System.out.println("Number of zsync files deleted = "
				+ this.callables.size());

		executor.shutdownNow();
		System.gc();
	}

	private void delete() {

		for (final SyncTreeLeaf leaf : filesToDelete) {

			if (leaf.getDestinationPath() != null) {

				int index = leaf.getName().lastIndexOf(".");
				String extension = "";
				if (index != -1) {
					extension = leaf.getName().substring(index);
				}

				if (extension.toLowerCase().equals(ZSYNC_EXTENSION)) {// *.zsync

					final File zsyncFile = new File(leaf.getDestinationPath()
							+ "/" + leaf.getName());
					totalCount++;
					Callable<Integer> c = new Callable<Integer>() {
						@Override
						public Integer call() {
							if (!canceled) {
								FileAccessMethods.deleteFile(zsyncFile);
								leaf.setCompressed(false);
								increment();
							}
							return 0;
						}
					};
					callables.add(c);
				}
			}
		}
	}

	private synchronized void increment() {
		count++;
		int value = count * 100 / totalCount;
		updateObserverCount(value);

	}

	/* Getters and Setters */

	public boolean isCanceled() {
		return canceled;
	}

	public void cancel() {
		this.canceled = true;
	}

	/* observable Count Inteface */

	@Override
	public void addObserverCount(ObserverCountInt obs) {
		this.observerCount = obs;
	}

	@Override
	public void updateObserverCount(int value) {
		observerCount.update(value);
	}
}
