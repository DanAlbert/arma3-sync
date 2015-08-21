package fr.soe.a3s.dao.zip;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.soe.a3s.controller.ObservableCountWithText;
import fr.soe.a3s.controller.ObserverCountWithText;
import fr.soe.a3s.domain.repository.SyncTreeLeaf;

public class ZipBatchProcessor implements ObservableCountWithText {

	private final List<SyncTreeLeaf> filesList = new ArrayList<SyncTreeLeaf>();
	private final List<ZipDAO> zipDAOPool = new ArrayList<ZipDAO>();
	private ObserverCountWithText observerCountWithText;
	private int count, numberOfFiles;
	private boolean canceled = false;
	private final List<Callable<Integer>> callables = new ArrayList<Callable<Integer>>();
	private IOException ex = null;

	public void init(List<SyncTreeLeaf> list) {
		filesList.clear();
		filesList.addAll(list);
		numberOfFiles = filesList.size();
		count = 0;
	}

	public void zipBatch() throws IOException {

		for (SyncTreeLeaf syncTreeLeaf : filesList) {
			zip(syncTreeLeaf);
		}

		ExecutorService executor = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());
		try {
			executor.invokeAll(callables);
		} catch (InterruptedException e) {
			new RuntimeException(
					"ZipBatch processor has been anormaly interrupted.");
		}

		executor.shutdownNow();

		if (ex != null) {
			throw ex;
		}
	}

	private void zip(final SyncTreeLeaf leaf) {

		Callable<Integer> c = new Callable<Integer>() {
			@Override
			public Integer call() {
				if (!canceled) {
					try {
						leaf.setCompressedSize(0);
						leaf.setCompressed(false);
						ZipDAO zipDAO = new ZipDAO();
						addZipDAO(zipDAO);
						final File file = new File(leaf.getDestinationPath()
								+ "/" + leaf.getName());
						long compressedSize = zipDAO.zip(file);
						removeZipDAO(zipDAO);
						if (!canceled) {
							leaf.setCompressedSize(compressedSize);
							leaf.setCompressed(true);
							count++;
							updateObserverCountWithText();
						}
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

	private synchronized void addZipDAO(ZipDAO zipDAO) {
		zipDAOPool.add(zipDAO);
	}

	private synchronized void removeZipDAO(ZipDAO zipDAO) {
		zipDAOPool.remove(zipDAO);
	}

	public void cancel() {

		this.canceled = true;
		for (ZipDAO zipDAO : zipDAOPool) {
			zipDAO.cancel();
		}
	}

	/* Interface ObservableCountWithText */

	@Override
	public void addObserverCountWithText(ObserverCountWithText obs) {
		this.observerCountWithText = obs;
	}

	@Override
	public void updateObserverCountWithText() {
		this.observerCountWithText.update(this.count * 100 / numberOfFiles);
	}

	@Override
	public void updateObserverCountWithText(String text) {
		this.observerCountWithText.update(text);
	}
}
