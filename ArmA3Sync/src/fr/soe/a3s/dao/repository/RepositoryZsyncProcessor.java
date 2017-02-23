package fr.soe.a3s.dao.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.soe.a3s.controller.ObservableCountInt;
import fr.soe.a3s.controller.ObserverCountInt;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.repository.SyncTreeLeaf;
import fr.soe.a3s.jazsync.Jazsync;

public class RepositoryZsyncProcessor implements ObservableCountInt,
		DataAccessConstants {

	/** Parameters */
	private List<SyncTreeLeaf> filesToCompute = null;
	private AbstractProtocole protocol = null;
	/** observable count Interface */
	private ObserverCountInt observerCount;
	protected int count = 0, totalCount = 0;
	/***/
	private List<Callable<Integer>> callables = null;
	private boolean canceled = false;
	private IOException ex = null;

	public void init(List<SyncTreeLeaf> filesToCompute,
			AbstractProtocole protocol) {
		this.filesToCompute = filesToCompute;
		this.protocol = protocol;
		this.callables = new ArrayList<Callable<Integer>>();
		this.count = 0;
		this.totalCount = 0;
	}

	public void run() throws IOException {

		// Generates zsync files on disk
		compute();

		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			executor.invokeAll(callables);
		} catch (InterruptedException e) {
			throw new RuntimeException(
					"ZSync files generation has been anormaly interrupted");
		}

		System.out.println("Number of zsync files generated = "
				+ this.callables.size());

		executor.shutdownNow();
		System.gc();

		if (ex != null) {
			throw ex;
		}
	}

	private void compute() {

		for (final SyncTreeLeaf leaf : filesToCompute) {
			final File file = new File(leaf.getDestinationPath() + "/"
					+ leaf.getName());
			final File zsyncFile = new File(file.getParentFile() + "/"
					+ file.getName() + ZSYNC_EXTENSION);
			final String url = protocol.getProtocolType().getPrompt()
					+ protocol.getUrl() + "/" + leaf.getRelativePath();
			totalCount++;
			Callable<Integer> c = new Callable<Integer>() {
				@Override
				public Integer call() {
					if (!canceled) {
						try {
							Jazsync.make(file, zsyncFile, url, leaf.getSha1());
							increment();
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
