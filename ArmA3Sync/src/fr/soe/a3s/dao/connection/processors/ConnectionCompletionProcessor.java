package fr.soe.a3s.dao.connection.processors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import fr.soe.a3s.controller.ObserverProceed;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.connection.HttpDAO;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;

public class ConnectionCompletionProcessor implements DataAccessConstants {

	private final List<HttpDAO> httpDAOs;
	private final Stack<SyncTreeLeafDTO> downloadFilesStack;
	private final Repository repository;
	private IOException ex;
	private int count, totalCount;

	public ConnectionCompletionProcessor(List<SyncTreeLeafDTO> filesToDownload,
			List<HttpDAO> httpDAOs, Repository repository) {
		this.httpDAOs = httpDAOs;
		this.downloadFilesStack = new Stack<SyncTreeLeafDTO>();
		this.downloadFilesStack.addAll(filesToDownload);
		this.repository = repository;
		this.ex = null;
	}

	public void run() throws IOException {

		this.totalCount = downloadFilesStack.size();
		this.count = 0;

		for (final HttpDAO httpDAO : httpDAOs) {
			httpDAO.addObserverProceed(new ObserverProceed() {
				@Override
				public void proceed() {
					if (!httpDAO.isCanceled()) {
						final SyncTreeLeafDTO leaf = popDownloadFilesStack();
						if (leaf != null) {
							Thread t = new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										httpDAO.setActiveConnection(true);
										double complete = httpDAO.getFileCompletion(
												leaf.getRemotePath(),
												leaf.getDestinationPath(),
												leaf, repository);
										leaf.setComplete(complete);
										if (!httpDAO.isCanceled()) {
											increment();
										}
									} catch (IOException e) {
										if (!httpDAO.isCanceled()) {
											ex = e;
											for (final HttpDAO hDAO : httpDAOs) {
												hDAO.cancel();
											}
											List<Exception> errors = new ArrayList<Exception>();
											errors.add(e);
											httpDAO.updateObserverError(errors);
										}
									} finally {
										httpDAO.setActiveConnection(false);
										httpDAO.updateObserverProceed();
									}
								}
							});
							t.start();
						} else {// no more file to download for this DAO

							// Check if there is no more active connections
							boolean downloadFinished = true;
							for (final HttpDAO httpDAO : httpDAOs) {
								if (httpDAO.isActiveConnection()) {
									downloadFinished = false;
									break;
								}
							}

							// download is finished
							if (downloadFinished) {
								httpDAO.updateObserverEnd();
							}
						}
					}
				}
			});
		}

		if (downloadFilesStack.isEmpty()) {
			httpDAOs.get(0).updateObserverProceed();
		} else {
			for (final HttpDAO httpDAO : httpDAOs) {
				if (!downloadFilesStack.isEmpty()) {// nb files < nb connections
					try {
						httpDAO.connectToRepository(repository.getProtocol());
						httpDAO.disconnect();
						httpDAO.updateObserverProceed();
					} catch (IOException e) {
						boolean isDowloading = false;
						for (final HttpDAO hDAO : httpDAOs) {
							if (hDAO.isActiveConnection()) {
								isDowloading = true;
								break;
							}
						}
						if (!isDowloading) {
							throw e;
						}
					}
				}
			}
		}
	}

	private synchronized SyncTreeLeafDTO popDownloadFilesStack() {
		if (downloadFilesStack.isEmpty()) {
			return null;
		} else {
			return downloadFilesStack.pop();
		}
	}

	private synchronized void increment() {
		count++;
		int value = count * 100 / totalCount;
		httpDAOs.get(0).updateObserverCount(value);
	}
}
