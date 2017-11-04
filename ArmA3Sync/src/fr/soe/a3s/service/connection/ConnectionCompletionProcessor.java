package fr.soe.a3s.service.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import fr.soe.a3s.controller.ObserverProceed;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;

public class ConnectionCompletionProcessor implements DataAccessConstants {

	private final List<AbstractConnexionDAO> connectionDAOs;
	private final Stack<SyncTreeLeafDTO> downloadFilesStack;
	private final Repository repository;
	private int count, totalCount;
	private boolean terminated;

	public ConnectionCompletionProcessor(List<SyncTreeLeafDTO> filesToCheck,
			List<AbstractConnexionDAO> httpDAOs, Repository repository) {
		this.connectionDAOs = httpDAOs;
		this.downloadFilesStack = new Stack<SyncTreeLeafDTO>();
		this.downloadFilesStack.addAll(filesToCheck);
		this.repository = repository;
	}

	public void run() throws IOException {

		this.totalCount = downloadFilesStack.size();
		this.count = 0;
		this.terminated = false;

		for (final AbstractConnexionDAO connectionDAO : connectionDAOs) {
			connectionDAO.addObserverProceed(new ObserverProceed() {
				@Override
				public void proceed() {
					if (!connectionDAO.isCanceled()) {
						final SyncTreeLeafDTO leaf = popDownloadFilesStack();
						if (leaf != null) {
							try {
								connectionDAO.setActiveConnection(true);
								double complete = connectionDAO
										.getFileCompletion(repository, leaf);
								leaf.setComplete(complete);
								if (!connectionDAO.isCanceled()) {
									increment();
								}
							} catch (IOException e) {
								if (!connectionDAO.isCanceled()) {
									for (final AbstractConnexionDAO hDAO : connectionDAOs) {
										hDAO.cancel();
									}
									List<Exception> errors = new ArrayList<Exception>();
									errors.add(e);
									terminate(connectionDAO, errors);
								}
							} finally {
								connectionDAO.setActiveConnection(false);
								connectionDAO.updateObserverProceed();
							}
						} else {// no more file to download for this DAO
							terminate(connectionDAO, null);
						}
					}
				}
			});
		}

		if (downloadFilesStack.isEmpty()) {
			connectionDAOs.get(0).updateObserverProceed();
		} else {
			for (final AbstractConnexionDAO connectionDAO : connectionDAOs) {
				if (!downloadFilesStack.isEmpty()) {// nb files < nb connections
					try {
						connectionDAO.checkConnection(repository.getProtocol());
						final Thread t = new Thread(new Runnable() {
							@Override
							public void run() {
								connectionDAO.updateObserverProceed();
							}
						});
						t.start();
					} catch (IOException e) {
						boolean isDowloading = false;
						for (final AbstractConnexionDAO hDAO : connectionDAOs) {
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
		connectionDAOs.get(0).updateObserverCount(value);
	}

	private synchronized void terminate(AbstractConnexionDAO connectionDAO,
			List<Exception> errors) {

		if (!terminated) {
			if (errors != null) {
				terminated = true;
				connectionDAO.updateObserverError(errors);
			} else {
				// Check if there is no more active connections
				boolean downloadFinished = true;
				for (final AbstractConnexionDAO cDAO : connectionDAOs) {
					if (cDAO.isActiveConnection()) {
						downloadFinished = false;
						break;
					}
				}
				if (downloadFinished) {
					terminated = true;
					connectionDAO.updateObserverEnd();
				}
			}
		}
	}
}
