package fr.soe.a3s.service.connection;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import fr.soe.a3s.controller.ObserverProceed;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.zip.UnZipFlowProcessor;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class ConnectionDownloadProcessor implements DataAccessConstants {

	private List<AbstractConnexionDAO> connexionDAOs = null;
	private Stack<SyncTreeNodeDTO> downloadFilesStack = null;
	private List<Exception> downloadErrors = null;
	private IOException downloadConnectionError = null;
	private int semaphore;
	private Repository repository = null;
	private UnZipFlowProcessor unZipFlowProcessor = null;
	private boolean terminated;

	public ConnectionDownloadProcessor(List<SyncTreeNodeDTO> filesToDownload,
			List<AbstractConnexionDAO> connexionDAOs, Repository repository,
			UnZipFlowProcessor unZipFlowProcessor) {

		this.connexionDAOs = connexionDAOs;
		this.downloadFilesStack = new Stack<SyncTreeNodeDTO>();
		this.downloadFilesStack.addAll(filesToDownload);
		this.downloadErrors = new ArrayList<Exception>();
		this.semaphore = 1;
		this.repository = repository;
		this.unZipFlowProcessor = unZipFlowProcessor;
	}

	public void run() {

		this.terminated = false;

		for (final AbstractConnexionDAO connexionDAO : connexionDAOs) {
			connexionDAO.addObserverProceed(new ObserverProceed() {
				@Override
				public void proceed() {
					if (!connexionDAO.isCanceled()) {
						final SyncTreeNodeDTO node = popDownloadFilesStack();
						if (node != null) {
							try {
								if (aquireSemaphore()) {
									connexionDAO.setAcquiredSemaphore(true);
								}

								connexionDAO.setActiveConnection(true);
								connexionDAO
										.updateObserverDownloadActiveConnections();

								File downloadedFile = connexionDAO
										.downloadFile(repository, node);

								connexionDAO.setActiveConnection(false);
								connexionDAO
										.updateObserverDownloadActiveConnections();

								if (connexionDAO.isAcquiredSemaphore()) {
									releaseSemaphore();
									connexionDAO.setAcquiredSemaphore(false);
								}

								// Give semaphore to the other DAOs
								for (final AbstractConnexionDAO connexionDAO : connexionDAOs) {
									if (connexionDAO.isActiveConnection()
											&& aquireSemaphore()) {
										connexionDAO.setAcquiredSemaphore(true);
										break;
									}
								}

								if (downloadedFile != null) {
									if (downloadedFile.isFile()) {
										if (downloadedFile
												.getName()
												.toLowerCase()
												.contains(
														DataAccessConstants.PBO_ZIP_EXTENSION)) {
											unZipFlowProcessor
													.unZipAsynchronously(downloadedFile);
										}
									}
								}
							} catch (IOException e) {
								
								connexionDAO.setActiveConnection(false);
								if (connexionDAO.isAcquiredSemaphore()) {
									releaseSemaphore();
									connexionDAO.setAcquiredSemaphore(false);
								}

								// e.printStackTrace();
								if (!connexionDAO.isCanceled()) {
									if (e instanceof SocketException
											|| e instanceof SocketTimeoutException) {
										downloadConnectionError = e;
									} else if (e instanceof IOException) {
										addDownloadError(e);
									}
								}
							} finally {
								if (downloadConnectionError != null) {
									terminate(connexionDAO);
								} else if (downloadErrors.size() > 10) {
									terminate(connexionDAO);
								} else {
									connexionDAO.updateObserverProceed();
								}
							}
						} else {// no more file to download for this DAO
							terminate(connexionDAO);
						}
					}
				}
			});
		}

		if (downloadFilesStack.isEmpty()) {
			connexionDAOs.get(0).updateObserverProceed();
		} else {
			for (final AbstractConnexionDAO connexionDAO : connexionDAOs) {
				if (!downloadFilesStack.isEmpty()) {// nb files < nb connections
					try {
						connexionDAO.checkConnection(repository.getProtocol());
						final Thread t = new Thread(new Runnable() {
							@Override
							public void run() {
								connexionDAO.updateObserverProceed();
							}
						});
						t.start();
					} catch (IOException e) {
						boolean isDowloading = false;
						connexionDAO.setActiveConnection(false);
						for (final AbstractConnexionDAO cDAO : connexionDAOs) {
							if (cDAO.isActiveConnection()) {
								isDowloading = true;
								break;
							}
						}
						if (!isDowloading) {
							connexionDAOs.get(0)
									.updateObserverDownloadConnectionLost();
						}
					}
				}
			}
		}
	}

	private synchronized void addDownloadError(Exception e) {
		downloadErrors.add(e);
	}

	private synchronized SyncTreeNodeDTO popDownloadFilesStack() {
		if (downloadFilesStack.isEmpty()) {
			return null;
		} else {
			return downloadFilesStack.pop();
		}
	}

	private synchronized boolean aquireSemaphore() {
		if (this.semaphore == 1) {
			this.semaphore = 0;
			return true;
		} else {
			return false;
		}
	}

	private synchronized void releaseSemaphore() {
		semaphore = 1;
	}

	private synchronized void terminate(AbstractConnexionDAO connexionDAO) {

		if (!terminated) {

			if (downloadConnectionError != null) {
				terminated = true;
				connexionDAO.updateObserverDownloadConnectionLost();
			} else if (downloadErrors.size() > 10) {
				terminated = true;
				connexionDAO.updateObserverDownloadTooManyErrors(10,
						downloadErrors);
			} else {
				// Check if there is no more active connections
				boolean downloadFinished = true;
				for (final AbstractConnexionDAO cDAO : connexionDAOs) {
					if (cDAO.isActiveConnection()) {
						downloadFinished = false;
						break;
					}
				}

				if (downloadFinished) {
					terminated = true;
					// display uncompressing progress
					if (unZipFlowProcessor.uncompressionIsFinished()) {
						downloadErrors.addAll(unZipFlowProcessor.getErrors());
						if (downloadErrors.isEmpty()) {
							connexionDAO.updateObserverDownloadEnd();
						} else {
							connexionDAO
									.updateObserverDownloadEndWithErrors(downloadErrors);
						}
					} else {
						if (!unZipFlowProcessor.isStarted()) {
							unZipFlowProcessor.start(downloadErrors);
						}
					}
				}
			}
		}
	}
}
