package fr.soe.a3s.service.synchronization;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.soe.a3s.constant.DownloadStatus;
import fr.soe.a3s.constant.RepositoryStatus;
import fr.soe.a3s.controller.ObserverConnectionLost;
import fr.soe.a3s.controller.ObserverCountInt;
import fr.soe.a3s.controller.ObserverCountLong;
import fr.soe.a3s.controller.ObserverDownload;
import fr.soe.a3s.controller.ObserverEnd;
import fr.soe.a3s.controller.ObserverError;
import fr.soe.a3s.controller.ObserverProceed;
import fr.soe.a3s.controller.ObserverUncompress;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.FileAccessMethods;
import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.service.connection.ConnexionService;
import fr.soe.a3s.service.connection.ConnexionServiceFactory;
import fr.soe.a3s.utils.UnitConverter;

public class FilesSynchronizationProcessor {

	/* Data */
	private final String repositoryName;
	private long startTime, deltaTimeSpeed;
	/* Services */
	private final RepositoryService repositoryService = new RepositoryService();
	private ConnexionService connexionService;
	private final FilesSynchronizationManager filesManager;
	/* observers */
	private ObserverCountInt observerCountTotalProgress,
			observerCountSingleProgress, observerActiveConnections;// null if no
																	// recording
	private ObserverCountLong observerTotalSize, observerDownloadedSize,
			observerSpeed, observerRemainigTime;// null if no recording
	private ObserverEnd observerEnd;// not null
	private ObserverError observerError;// not null
	private ObserverConnectionLost observerConnectionLost; // not null
	private ObserverProceed observerProceedUncompress, observerProceedDelete;// not
																				// null

	public FilesSynchronizationProcessor(String repositoryName,
			FilesSynchronizationManager filesManager) {
		this.repositoryName = repositoryName;
		this.filesManager = filesManager;
	}

	public void run() {

		try {
			repositoryService.setDownloading(repositoryName, true);

			int numberOfServerInfoConnections = repositoryService
					.getServerInfoNumberOfConnections(repositoryName);
			int numberOfClientConnections = repositoryService
					.getNumberOfClientConnections(repositoryName);

			if (numberOfServerInfoConnections == 0) {
				numberOfServerInfoConnections = 1;
			}
			if (numberOfClientConnections == 0) {
				numberOfClientConnections = 1;
			}

			int numberOfConnections = 1;
			if (numberOfClientConnections >= numberOfServerInfoConnections) {
				numberOfConnections = numberOfServerInfoConnections;
			} else {
				numberOfConnections = numberOfClientConnections;
			}

			connexionService = ConnexionServiceFactory
					.getServiceForFilesSynchronization(repositoryName,
							numberOfConnections);

			for (AbstractConnexionDAO connect : connexionService
					.getConnexionDAOs()) {
				connect.addObserverDownload(new ObserverDownload() {

					@Override
					public void updateSingleSizeProgress(long value,
							int pourcentage) {
						executeUpdateSingleSizeProgress(value, pourcentage);
					}

					@Override
					public void updateTotalSizeProgress(long value) {
						executeUpdateTotalSizeProgress(value);
					}

					@Override
					public void updateTotalSize() {
						filesManager.update();
						executeUpdateTotalSize(filesManager.getTotalFilesSize());
					}

					@Override
					public void updateResponseTime(long value) {
						executeUpdateResponseTime(value);
					}

					@Override
					public void updateSpeed() {
						executeUpdateSpeed();
					}

					@Override
					public void updateActiveConnections() {
						executeUpdateActiveConnections();
					}

					@Override
					public void end() {
						excuteEnd();
					}

					@Override
					public void error(List<Exception> errors) {
						String message = "Download finished with errors";
						executeError(message, errors);
					}

					@Override
					public void updateCancelTooManyErrors(int value,
							List<Exception> errors) {
						String message = "Download has been canceled due to too many errors (>"
								+ value + ")";
						executeError(message, errors);
					}

					@Override
					public void updateConnectionLost() {
						executeConnectionLost();
					}
				});
			}

			connexionService.getUnZipFlowProcessor().addObserverUncompress(
					new ObserverUncompress() {

						@Override
						public void start() {
							executeUpdateUncompressStart();
						}

						@Override
						public void update(int value) {
							executeUpdateUncompress(value);
						}

						@Override
						public void end() {
							excuteEnd();
						}

						@Override
						public void error(List<Exception> errors) {
							String message = "Download finished with error";
							executeError(message, errors);
						}
					});

			// Start uncompressing in background already downloaded .pbo.zip
			// files
			for (SyncTreeNodeDTO node : filesManager.getListFilesToUpdate()) {
				if (node.isLeaf()) {
					SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
					if (leaf.isCompressed()
							&& node.getDownloadStatus().equals(
									DownloadStatus.DONE)) {
						File parentDirectory = new File(
								leaf.getDestinationPath());
						File zipFile = new File(parentDirectory + "/"
								+ leaf.getName()
								+ DataAccessConstants.ZIP_EXTENSION);
						if (zipFile.exists()) {
							connexionService.getUnZipFlowProcessor()
									.unZipAsynchronously(zipFile);
						}
					}
				}
			}

			executeUpdateTotalSize(filesManager.getTotalFilesSize());

			// Start synchronization
			startTime = System.nanoTime();
			deltaTimeSpeed = startTime;
			connexionService.synchronize(repositoryName,
					filesManager.getResumedFiles());

		} catch (Exception e) {
			e.printStackTrace();
			List<Exception> errors = new ArrayList<Exception>();
			errors.add(e);
			String message = "Download finished with error";
			executeError(message, errors);
		}
	}

	private synchronized void executeUpdateSingleSizeProgress(long value,
			int pourcentage) {

		if (observerCountSingleProgress != null) {
			observerCountSingleProgress.update(pourcentage);
		}

		double endTime = System.nanoTime();
		double elapsedTime = endTime - startTime;

		long remainingFilesSize = filesManager.getTotalFilesSize()
				- filesManager.getResumedFilesSize() - value;
		long downloadedFilesSize = filesManager.getResumedFilesSize() + value;

		executeUpdateTotalDownloadedSize(downloadedFilesSize);

		if (observerRemainigTime != null && downloadedFilesSize > 0) {
			long remainingTime = (long) ((remainingFilesSize * elapsedTime) / downloadedFilesSize);
			observerRemainigTime.update(remainingTime);
		}
	}

	private synchronized void executeUpdateTotalSizeProgress(long value) {

		if (observerCountTotalProgress != null
				&& filesManager.getTotalFilesSize() > 0) {
			int pourcentage = (int) (((filesManager.getResumedFilesSize()) * 100) / filesManager
					.getTotalFilesSize());
			observerCountTotalProgress.update(pourcentage);
		}
	}

	private synchronized void executeUpdateTotalSize(long value) {

		if (observerTotalSize != null) {
			observerTotalSize.update(value);
		}
	}

	private synchronized void executeUpdateTotalDownloadedSize(long value) {

		if (observerDownloadedSize != null) {
			observerDownloadedSize.update(value);
		}
	}

	private synchronized void executeUpdateSpeed() {

		long endTime = System.nanoTime();
		long delta = endTime - deltaTimeSpeed;

		if (delta > (Math.pow(10, 9) / 2)) {// 0.5s
			long speed = 0;
			for (AbstractConnexionDAO connect : connexionService
					.getConnexionDAOs()) {
				if (connect.isActiveConnection()) {
					speed = speed + connect.getSpeed();
				}
			}
			deltaTimeSpeed = endTime;
			if (observerSpeed != null) {
				observerSpeed.update(speed);
			}
			// Report
			if (speed > 0) {
				if (filesManager.getAverageDownloadSpeed() == 0) {
					filesManager.setAverageDownloadSpeed(speed);
				} else {
					filesManager.setAverageDownloadSpeed((filesManager
							.getAverageDownloadSpeed() + speed) / 2);
				}
			}
		}
	}

	private synchronized void executeUpdateActiveConnections() {

		int activeConnections = 0;
		for (AbstractConnexionDAO connect : connexionService.getConnexionDAOs()) {
			if (connect.isActiveConnection()) {
				activeConnections++;
			}
		}

		double maximumClientDownloadSpeed = repositoryService
				.getMaximumClientDownloadSpeed(repositoryName);
		if (activeConnections == 0) {
			connexionService
					.setMaximumClientDownloadSpeed(maximumClientDownloadSpeed);
		} else {
			connexionService
					.setMaximumClientDownloadSpeed(maximumClientDownloadSpeed
							/ activeConnections);
		}

		if (observerActiveConnections != null) {
			observerActiveConnections.update(activeConnections);
		}

		// Report
		if (activeConnections > filesManager.getMaxActiveconnections()) {
			filesManager.setMaxActiveconnections(activeConnections);
		}
	}

	private synchronized void executeUpdateResponseTime(long value) {

		// Report
		filesManager.setAverageResponseTime((filesManager
				.getAverageResponseTime() + value) / 2);
	}

	private void executeUpdateUncompressStart() {

		if (observerProceedUncompress != null) {
			observerProceedUncompress.proceed();
		}
	}

	private void executeUpdateUncompress(int value) {

		if (observerCountTotalProgress != null) {
			observerCountTotalProgress.update(value);
		}
	}

	private void excuteEnd() {

		repositoryService.setDownloading(repositoryName, false);

		/* Delete extra files */
		deleteExtraFiles();

		/* Generate Report */
		String report = generateReport("Download finished successfully.");
		repositoryService.setReport(repositoryName, report);

		/* End */
		observerEnd.end();
	}

	private void executeError(String message, List<Exception> errors) {

		repositoryService.setDownloading(repositoryName, false);

		/* Delete extra files */
		deleteExtraFiles();

		/* Generate Report */
		String report = generateReport(message, errors);
		repositoryService.setReport(repositoryName, report);

		/* End */
		observerError.error(errors);
	}

	private void deleteExtraFiles() {

		/* Delete extra files */
		observerProceedDelete.proceed();
		for (SyncTreeNodeDTO node : filesManager.getListFilesToDelete()) {
			String path = node.getDestinationPath() + "/" + node.getName();
			if (path != null) {
				File file = new File(path);
				if (file.isFile()) {
					FileAccessMethods.deleteFile(file);
				} else if (file.isDirectory()) {
					FileAccessMethods.deleteDirectory(file);
				}
			}
		}
	}

	private void executeConnectionLost() {

		repositoryService.setDownloading(repositoryName, false);

		/* End */
		observerConnectionLost.lost();
	}

	public void cancel() {

		repositoryService.setDownloading(repositoryName, false);

		if (connexionService != null) {
			connexionService.cancel();
		}
	}

	/* Generate Report */

	private String generateReport(String message) {

		String header = "--- Download report ---";
		String repositoryInfo = "Repository name: " + repositoryName;
		String repositoryUrl = "Repository url: "
				+ repositoryService.getRepositoryUrl(repositoryName);
		String endDate = "Download finished on: " + new Date().toLocaleString();

		// Server Connection
		long averageResponseTime = Math.round(filesManager
				.getAverageResponseTime() / Math.pow(10, 6));
		String avgRespTime = "unavailable";
		if (averageResponseTime == 0) {
			avgRespTime = "1 ms";
		} else if (averageResponseTime > 0) {
			avgRespTime = Long.toString(averageResponseTime) + " ms";
		}
		String avgDlSpeed = "unavailable";
		if (filesManager.getAverageDownloadSpeed() > 0) {
			avgDlSpeed = UnitConverter.convertSpeed(filesManager
					.getAverageDownloadSpeed());
		}
		String serverConnectionInfo = "Server connection:" + "\n"
				+ "- Average response time: " + avgRespTime + "\n"
				+ "- Average download speed: " + avgDlSpeed + "\n"
				+ "- Number of active connections used: "
				+ filesManager.getMaxActiveconnections();

		filesManager.report();

		// Global File transfer:
		String savedSizeFileTransfer = UnitConverter.convertSize(filesManager
				.getTotalDiskFilesSize() - filesManager.getResumedFilesSize());
		int savedSizeFileTransferFraction = 0;
		if (filesManager.getTotalDiskFilesSize() != 0) {
			savedSizeFileTransferFraction = (int) (((filesManager
					.getTotalDiskFilesSize() - filesManager
					.getResumedFilesSize()) * 100) / filesManager
					.getTotalDiskFilesSize());
		}

		String fileTransfer = "Global file transfer:"
				+ "\n"
				+ "- Number of files updated: "
				+ filesManager.getListFilesToUpdate().size()
				+ "\n"
				+ "- Total files size on disk: "
				+ UnitConverter.convertSize(filesManager
						.getTotalDiskFilesSize()) + "\n"
				+ "- Downloaded data: "
				+ UnitConverter.convertSize(filesManager.getResumedFilesSize())
				+ "\n" + "- Saved: " + savedSizeFileTransfer + " ("
				+ savedSizeFileTransferFraction + "%)";

		// Partial file transfer
		String savedPartialSizeFileTransfer = UnitConverter
				.convertSize(filesManager.getTotalUncompleteDiskFileSize()
						- filesManager.getTotalUncompleteExpectedFileSize());
		int savedPartialSizeFileTransferFraction = 0;
		if (filesManager.getTotalUncompleteDiskFileSize() != 0) {
			savedPartialSizeFileTransferFraction = (int) (((filesManager
					.getTotalUncompleteDiskFileSize() - filesManager
					.getTotalUncompleteExpectedFileSize()) * 100) / filesManager
					.getTotalUncompleteDiskFileSize());
		}

		String partialFileTransferInfo = "Partial file transfer:"
				+ "\n"
				+ "- Number of files updated: "
				+ filesManager.getTotalNumberUnCompleteFiles()
				+ "\n"
				+ "- Total files size on disk: "
				+ UnitConverter.convertSize(filesManager
						.getTotalUncompleteDiskFileSize())
				+ "\n"
				+ "- Downloaded data: "
				+ UnitConverter.convertSize(filesManager
						.getTotalUncompleteExpectedFileSize()) + "\n"
				+ "- Saved: " + savedPartialSizeFileTransfer + " ("
				+ savedPartialSizeFileTransferFraction + "%)";

		// Compressed file transfer
		String savedCompressedSizeFileTransfer = UnitConverter
				.convertSize(filesManager.getTotalUncompressedFilesSize()
						- filesManager.getTotalCompressedFilesSize());
		int savedCompressedSizeFileTransferFraction = 0;
		if (filesManager.getTotalUncompressedFilesSize() != 0) {
			savedCompressedSizeFileTransferFraction = (int) (((filesManager
					.getTotalUncompressedFilesSize() - filesManager
					.getTotalCompressedFilesSize()) * 100) / filesManager
					.getTotalUncompressedFilesSize());
		}

		String compressionFileTransferInfo = "Compressed file transfer:"
				+ "\n"
				+ "- Number of files updated: "
				+ filesManager.getTotalNumberCompressedFiles()
				+ "\n"
				+ "- Total files size on disk: "
				+ UnitConverter.convertSize(filesManager
						.getTotalUncompressedFilesSize())
				+ "\n"
				+ "- Downloaded data: "
				+ UnitConverter.convertSize(filesManager
						.getTotalCompressedFilesSize()) + "\n" + "- Saved: "
				+ savedCompressedSizeFileTransfer + " ("
				+ savedCompressedSizeFileTransferFraction + "%)";

		String report = header + "\n" + repositoryInfo + "\n" + repositoryUrl
				+ "\n" + endDate + "\n\n" + message + "\n\n"
				+ serverConnectionInfo + "\n\n" + fileTransfer + "\n\n"
				+ partialFileTransferInfo + "\n\n"
				+ compressionFileTransferInfo;

		return report;
	}

	private String generateReport(String message, List<Exception> errors) {

		String header = "--- Download report ---";
		String repositoryInfo = "Repository name: " + repositoryName;
		String repositoryUrl = "Repository url: "
				+ repositoryService.getRepositoryUrl(repositoryName);
		String endDate = "Download finished on: " + new Date().toLocaleString();

		List<String> messages = new ArrayList<String>();
		for (Exception e : errors) {
			if (e instanceof IOException) {
				messages.add("- " + e.getMessage());
			} else {
				String coreMessage = "- An unexpected error has occured.";
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				String stacktrace = sw.toString(); // stack trace as a string
				coreMessage = coreMessage + "\n" + "StackTrace:" + "\n"
						+ stacktrace;
				messages.add(coreMessage);
			}
		}

		String report = header + "\n" + repositoryInfo + "\n" + repositoryUrl
				+ "\n" + endDate + "\n\n" + message;
		for (String m : messages) {
			report = report + "\n" + m;
		}
		return report;
	}

	/* */

	public void addObserverCountSingleProgress(ObserverCountInt obs) {
		this.observerCountSingleProgress = obs;
	}

	public void addObserverCountTotalProgress(ObserverCountInt obs) {
		this.observerCountTotalProgress = obs;
	}

	public void addObserverTotalSize(ObserverCountLong obs) {
		this.observerTotalSize = obs;
	}

	public void addObserverDownloadedSize(ObserverCountLong obs) {
		this.observerDownloadedSize = obs;
	}

	public void addObserverEnd(ObserverEnd obs) {
		this.observerEnd = obs;
	}

	public void addObserverError(ObserverError obs) {
		this.observerError = obs;
	}

	public void addObserverConnectionLost(ObserverConnectionLost obs) {
		this.observerConnectionLost = obs;
	}

	public void addObserverSpeed(ObserverCountLong obs) {
		this.observerSpeed = obs;
	}

	public void addObserverActiveConnections(ObserverCountInt obs) {
		this.observerActiveConnections = obs;
	}

	public void addObserverRemainingTime(ObserverCountLong obs) {
		this.observerRemainigTime = obs;
	}

	public void addObserverProceedUncompress(ObserverProceed obs) {
		this.observerProceedUncompress = obs;
	}

	public void addObserverProceedDelete(ObserverProceed obs) {
		this.observerProceedDelete = obs;
	}
}
