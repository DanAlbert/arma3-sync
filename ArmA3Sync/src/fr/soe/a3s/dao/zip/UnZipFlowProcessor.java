package fr.soe.a3s.dao.zip;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import fr.soe.a3s.controller.ObservableUncompress;
import fr.soe.a3s.controller.ObserverUncompress;

public class UnZipFlowProcessor implements ObservableUncompress {

	private final List<File> compressedFilesList = new LinkedList<File>();
	// private final ZipDAO zipDAO = new ZipDAO();
	private final List<ZipDAO> zipDAOPool = new ArrayList<ZipDAO>();
	private final List<Exception> errors = new ArrayList<Exception>();

	/** observableFileUncompress Interface */
	private ObserverUncompress observerUncompress;
	private int size = 0;
	private int count = 0;
	private boolean started = false;

	private boolean canceled;

	public void init() {
		compressedFilesList.clear();
		for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
			ZipDAO zipDAO = new ZipDAO();
			zipDAOPool.add(zipDAO);
		}
	}

	public void unZipAsynchronously(File zipFile) {

		addToCompressedFilesList(zipFile);
		unZip();
	}

	public void start(List<Exception> errors) {

		size = compressedFilesList.size();
		for (ZipDAO zipDAO : zipDAOPool) {
			if (zipDAO.isActive()) {
				size = size + 1;
			}
		}
		started = true;
		this.errors.addAll(errors);
		observerUncompress.start();
	}

	public void cancel() {

		canceled = true;
		for (ZipDAO zipDAO : zipDAOPool) {
			zipDAO.cancel();
		}
	}

	private void unZip() {

		for (final ZipDAO zipDAO : zipDAOPool) {
			if (!zipDAO.isActive()) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						while (!compressedFilescheckEmpty() && !canceled) {
							File zipFile = takeFromCompressedFilesList();
							try {
								if (zipFile != null) {
									zipDAO.unZip(zipFile);
								}
							} catch (Exception e) {
								addError(e);
							}
							if (started && !canceled) {
								count++;
								updateObserverUncompress();
							}
						}

						if (uncompressionIsFinished() && started && !canceled) {
							if (errors.isEmpty()) {
								observerUncompress.end();
							} else {
								observerUncompress.endWithError(errors);
							}
						}
					}
				});
				t.start();
			}
		}
	}

	private synchronized void addToCompressedFilesList(File zipFile) {
		compressedFilesList.add(zipFile);
	}

	private synchronized File takeFromCompressedFilesList() {
		if (compressedFilesList.isEmpty()) {
			return null;
		} else {
			File file = compressedFilesList.remove(0);
			return file;
		}
	}

	private synchronized boolean compressedFilescheckEmpty() {
		return compressedFilesList.isEmpty();
	}

	private synchronized void addError(Exception e) {
		errors.add(e);
	}

	public synchronized boolean uncompressionIsFinished() {

		boolean active = false;
		for (ZipDAO zipDAO : zipDAOPool) {
			if (zipDAO.isActive()) {
				active = true;
				break;
			}
		}
		return compressedFilesList.isEmpty() && !active;
	}

	public List<Exception> getErrors() {
		return this.errors;
	}

	/* observableUncompress Interface */

	@Override
	public void addObserverUncompress(ObserverUncompress obs) {
		this.observerUncompress = obs;
	}

	@Override
	public void updateObserverUncompress() {// update progress in %
		this.observerUncompress.update((count * 100) / size);
	}
}
