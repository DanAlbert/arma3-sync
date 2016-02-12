package fr.soe.a3s.dao.zip;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.soe.a3s.controller.ObservableCount;
import fr.soe.a3s.controller.ObserverCount;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.FileAccessMethods;
import fr.soe.a3s.domain.repository.SyncTreeLeaf;

public class DeleteZipBatchProcessor implements ObservableCount,
		DataAccessConstants {

	/** Parameters */
	private List<SyncTreeLeaf> filesToDelete = null;
	/** observable count Interface */
	private ObserverCount observerCount;
	protected int count = 0, totalCount = 0;
	/***/
	private List<Callable<Integer>> callables = null;
	private boolean canceled = false;

	public void init(List<SyncTreeLeaf> filesToDelete) {
		this.filesToDelete = filesToDelete;
	}

	public void run() {

		this.callables = new ArrayList<Callable<Integer>>();
		this.totalCount = 0;
		this.count = 0;

		delete();

		ExecutorService executor = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());
		try {
			executor.invokeAll(callables);
		} catch (InterruptedException e) {
			new RuntimeException(
					"Delete files process has been anormaly interrupted.");
		}

		System.out.println("Number of zip files deleted = "
				+ this.callables.size());

		executor.shutdownNow();
		System.gc();
	}

	private void delete() {

		for (final SyncTreeLeaf leaf : filesToDelete) {
			final File file = new File(leaf.getDestinationPath() + "/"
					+ leaf.getName());
			final File zipFile = new File(file.getParentFile() + "/"
					+ file.getName() + ZIP_EXTENSION);
			totalCount++;
			Callable<Integer> c = new Callable<Integer>() {
				@Override
				public Integer call() {
					if (!canceled) {
						FileAccessMethods.deleteFile(zipFile);
						leaf.setCompressed(false);
						increment();
						updateObserverCount();
					}
					return 0;
				}
			};
			callables.add(c);
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
