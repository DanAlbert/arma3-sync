package fr.soe.a3s.dao.connection.processors;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import fr.soe.a3s.controller.ObserverProceed;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.connection.FtpDAO;
import fr.soe.a3s.dao.connection.HttpDAO;
import fr.soe.a3s.dao.zip.UnZipFlowProcessor;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class ConnectionDownloadProcessor implements DataAccessConstants {

	private List<AbstractConnexionDAO> connexionDAOs = null;
	private Stack<SyncTreeNodeDTO> downloadFilesStack = null;
	private List<Exception> downloadErrors = null;
	private List<Exception> downloadTimeouterrors = null;
	private int semaphore;
	private Repository repository = null;
	private UnZipFlowProcessor unZipFlowProcessor = null;

	public void init(List<SyncTreeNodeDTO> listFiles,
			List<AbstractConnexionDAO> connexionDAOs, Repository repository,
			UnZipFlowProcessor unZipFlowProcessor) {

		this.connexionDAOs = connexionDAOs;
		this.downloadFilesStack = new Stack<SyncTreeNodeDTO>();
		this.downloadFilesStack.addAll(listFiles);
		this.downloadErrors = new ArrayList<Exception>();
		this.downloadTimeouterrors = new ArrayList<Exception>();
		this.semaphore = 1;
		this.repository = repository;
		this.unZipFlowProcessor = unZipFlowProcessor;
	}

	public void run() throws IOException {

		for (final AbstractConnexionDAO connexionDAO : connexionDAOs) {
			connexionDAO.addObserverProceed(new ObserverProceed() {
				@Override
				public void proceed() {
					if (!connexionDAO.isCanceled()) {
						final SyncTreeNodeDTO node = popDownloadFilesStack();
						if (node != null) {
							Thread t = new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										if (aquireSemaphore()) {
											connexionDAO
													.setAcquiredSemaphore(true);
										}

										connexionDAO.setActiveConnection(true);
										connexionDAO
												.updateObserverDownloadActiveConnections();

										File downloadedFile = downloadFile(
												connexionDAO, node);

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
									} catch (Exception e) {
										e.printStackTrace();
										if (!connexionDAO.isCanceled()) {
											if (e instanceof SocketTimeoutException) {
												addTimeoutError(e);
												addError(e);
											} else if (e instanceof IOException) {
												// reset count
												downloadTimeouterrors.clear();
												addError(e);
											}
										}
									} finally {
										if (connexionDAO.isAcquiredSemaphore()) {
											releaseSemaphore();
											connexionDAO
													.setAcquiredSemaphore(false);
										}
										connexionDAO.setActiveConnection(false);
										connexionDAO
												.updateObserverDownloadActiveConnections();

										if (downloadTimeouterrors.size() > connexionDAOs
												.size()) {
											connexionDAO
													.updateObserverDownloadTooManyTimeoutErrors(
															connexionDAOs
																	.size(),
															downloadTimeouterrors);
										} else if (downloadErrors.size() > 10) {
											connexionDAO
													.updateObserverDownloadTooManyErrors(
															10, downloadErrors);
										} else {
											connexionDAO
													.updateObserverProceed();
										}
									}
								}
							});
							t.start();
						} else {// no more file to download for this DAO

							// Check if there is no more active connections
							boolean downloadFinished = true;
							for (final AbstractConnexionDAO connexionDAO : connexionDAOs) {
								if (connexionDAO.isActiveConnection()) {
									downloadFinished = false;
									break;
								}
							}

							// download is finished
							if (downloadFinished) {
								// display uncompressing progress
								if (unZipFlowProcessor
										.uncompressionIsFinished()) {
									downloadErrors.addAll(unZipFlowProcessor
											.getErrors());
									if (downloadErrors.isEmpty()) {
										connexionDAO
												.updateObserverDownloadEnd();
									} else {
										connexionDAO
												.updateObserverDownloadEndWithErrors(downloadErrors);
									}
								} else {
									if (!unZipFlowProcessor.isStarted()) {
										unZipFlowProcessor
												.start(downloadErrors);
									}
								}
							} else {
								// Give semaphore to the other DAOs
								for (final AbstractConnexionDAO connexionDAO : connexionDAOs) {
									if (connexionDAO.isActiveConnection()
											&& aquireSemaphore()) {
										connexionDAO.setAcquiredSemaphore(true);
										break;
									}
								}
							}
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
						if (connexionDAO instanceof FtpDAO) {
							((FtpDAO) connexionDAO)
									.connectToRepository(repository.getProtocol());
						} else if (connexionDAO instanceof HttpDAO) {
							((HttpDAO) connexionDAO).connectToRepository(
									repository.getProtocol(), SYNC_FILE_PATH);
							((HttpDAO) connexionDAO).disconnect();
						}
						connexionDAO.updateObserverProceed();
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
							throw e;
						}
					}
				}
			}
		}
	}

	private File downloadFile(final AbstractConnexionDAO connexionDAO,
			final SyncTreeNodeDTO node) throws IOException {

		final String rootDestinationPath = repository
				.getDefaultDownloadLocation();

		String destinationPath = null;
		String remotePath = repository.getProtocol().getRemotePath();
		String path = node.getParentRelativePath();
		if (node.getDestinationPath() != null) {
			destinationPath = node.getDestinationPath();
			if (!path.isEmpty()) {
				remotePath = remotePath + "/" + path;
			}
		} else {
			destinationPath = rootDestinationPath;
			if (!path.isEmpty()) {
				destinationPath = rootDestinationPath + "/" + path;
				remotePath = remotePath + "/" + path;
			}
		}

		return connexionDAO.downloadFile(repository, remotePath,
				destinationPath, node);
	}

	private synchronized void addError(Exception e) {
		downloadErrors.add(e);
	}

	private synchronized void addTimeoutError(Exception e) {
		downloadTimeouterrors.add(e);
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
}
