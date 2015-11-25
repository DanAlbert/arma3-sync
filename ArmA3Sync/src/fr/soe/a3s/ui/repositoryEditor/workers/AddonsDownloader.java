package fr.soe.a3s.ui.repositoryEditor.workers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import fr.soe.a3s.constant.DownloadStatus;
import fr.soe.a3s.controller.ObserverDownload;
import fr.soe.a3s.controller.ObserverUncompress;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.FileAccessMethods;
import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.AbstractConnexionService;
import fr.soe.a3s.service.AbstractConnexionServiceFactory;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.FileSizeComputer;
import fr.soe.a3s.ui.UnitConverter;
import fr.soe.a3s.ui.repositoryEditor.DownloadPanel;
import fr.soe.a3s.ui.repositoryEditor.errorDialogs.UnexpectedErrorDialog;
import fr.soe.a3s.ui.tools.acre2Editor.FirstPageACRE2InstallerPanel;
import fr.soe.a3s.ui.tools.tfarEditor.FirstPageTFARInstallerPanel;

public class AddonsDownloader extends Thread implements DataAccessConstants {

	private final Facade facade;
	private final DownloadPanel downloadPanel;
	/* Data */
	private final String repositoryName;
	private final SyncTreeDirectoryDTO racine;
	private long incrementedFilesSize;
	private long totalExpectedFilesSize;
	private long totalDiskFilesSize;
	private long totalCompressedFilesSize;
	private long totalUncompressedFilesSize;
	private long totalUncompleteExpectedFileSize;
	private long totalUncompleteDiskFileSize;
	private final List<SyncTreeNodeDTO> listFilesToUpdate = new ArrayList<SyncTreeNodeDTO>();
	private final List<SyncTreeNodeDTO> listFilesToDelete = new ArrayList<SyncTreeNodeDTO>();
	private long averageDownloadSpeed;
	private long averageResponseTime;
	private int maxActiveconnections;
	private int unCompleteFiles;
	private int compressedFiles;
	/* Tests */
	private boolean canceled = false;
	private boolean tfarIsUpdated = false;
	private boolean acre2IsUpdated = false;
	/* Services */
	private final RepositoryService repositoryService = new RepositoryService();
	private AbstractConnexionService connexionService;

	public AddonsDownloader(Facade facade, String repositoryName,
			SyncTreeDirectoryDTO racine, DownloadPanel downloadPanel) {
		this.facade = facade;
		this.racine = racine;
		this.repositoryName = repositoryName;
		this.downloadPanel = downloadPanel;
	}

	@Override
	public void run() {

		System.out.println("Starting downloading from repository: "
				+ repositoryName);

		// Init DownloadPanel for start download
		initDownloadPanelForStartDownload();

		// Set downloading state
		repositoryService.setDownloading(repositoryName, true);

		// Reset download report
		repositoryService.setReport(repositoryName, null);

		// Get update files list
		for (SyncTreeNodeDTO node : racine.getList()) {
			getFiles(node);
		}

		// Return if update files list is empoty
		if (this.listFilesToUpdate.size() == 0) {
			finish();
			initDownloadPanelForEndDownload();
			terminate();
			return;
		}

		// Determine @TFAR/@ACRE/@ACRE2 updates
		determineTFARandACREupdates();

		// Determine files variables
		determineFilesVariables();

		downloadPanel.getLabelDownloadedValue().setText(
				UnitConverter.convertSize(incrementedFilesSize));
		downloadPanel.getLabelTotalFilesSizeValue().setText(
				UnitConverter.convertSize(totalExpectedFilesSize));

		// Initialize connection
		try {
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

			connexionService = AbstractConnexionServiceFactory
					.getServiceFromRepositoryMultiConnections(repositoryName,
							numberOfConnections);

		} catch (RepositoryException | CheckException e) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Download", JOptionPane.ERROR_MESSAGE);
			initDownloadPanelForEndDownload();
			terminate();
			return;
		}

		try {
			for (AbstractConnexionDAO connect : connexionService
					.getConnexionDAOs()) {

				connect.addObserverDownload(new ObserverDownload() {

					@Override
					public void updateTotalSize() {
						executeUpdateTotalSize();
					}

					@Override
					public void updateTotalSizeProgress(long value) {
						executeUpdateTotalSizeProgress(value);
					}

					@Override
					public void updateSingleSizeProgress(long value,
							int pourcentage) {
						executeUpdateSingleSizeProgress(value, pourcentage);
					}

					@Override
					public void updateSpeed() {
						executeUpdateDownloadSpeed();
					}

					@Override
					public void updateActiveConnections() {
						executeUpdateActiveConnections();
					}

					@Override
					public void updateResponseTime(long responseTime) {
						executeUpdateResponseTime(responseTime);
					}

					@Override
					public void updateEnd() {
						finish();
					}

					@Override
					public void updateEndWithErrors(List<Exception> errors) {
						finishWithErrors("Download finished with errors:",
								errors);
					}

					@Override
					public void updateCancelTooManyTimeoutErrors(int value,
							List<Exception> errors) {
						executeCancelTooManyTimeoutErrors(value, errors);
					}

					@Override
					public void updateCancelTooManyErrors(int value,
							List<Exception> errors) {
						executeCancelTooManyErrors(value, errors);
					}
				});
			}

			connexionService.getUnZipFlowProcessor().addObserverUncompress(
					new ObserverUncompress() {

						@Override
						public void start() {
							initDownloadPanelForStartUncompressing();
						}

						@Override
						public void update(int value) {
							executeUncompressingProgress(value);
						}

						@Override
						public void end() {
							finish();
						}

						@Override
						public void endWithError(List<Exception> errors) {
							finishWithErrors("Download finished with errors:",
									errors);
						}
					});

			// Resume
			List<SyncTreeNodeDTO> list = new ArrayList<SyncTreeNodeDTO>();
			for (SyncTreeNodeDTO node : listFilesToUpdate) {
				if (!node.getDownloadStatus().equals(DownloadStatus.DONE)) {
					list.add(node);
				} else if (node.isLeaf()) {
					SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
					if (leaf.getDownloadStatus().equals(DownloadStatus.DONE)
							&& leaf.isCompressed()) {
						File parentDirectory = new File(
								leaf.getDestinationPath());
						File zipFile = new File(parentDirectory + "/"
								+ leaf.getName() + ZIP_EXTENSION);
						if (zipFile.exists()) {
							connexionService.getUnZipFlowProcessor()
									.unZipAsynchronously(zipFile);
						}
					}
				}
			}

			connexionService.downloadAddons(repositoryName, list);

		} catch (Exception e) {
			downloadPanel.getProgressBarDownloadSingleAddon().setIndeterminate(
					false);
			if (!canceled) {
				e.printStackTrace();
				if (e instanceof RepositoryException) {
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							e.getMessage(), "Download",
							JOptionPane.ERROR_MESSAGE);
				} else if (e instanceof IOException) {
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							e.getMessage(), "Download",
							JOptionPane.ERROR_MESSAGE);
				} else {
					UnexpectedErrorDialog dialog = new UnexpectedErrorDialog(
							facade, "Download", e, repositoryName);
					dialog.show();
				}
				if (connexionService != null) {
					connexionService.cancel();
				}
				downloadPanel.checkForAddons();
				initDownloadPanelForEndDownload();
				terminate();
			}
		}
	}

	private void initDownloadPanelForStartDownload() {

		downloadPanel.getLabelDownloadStatus().setText("Downloading...");
		downloadPanel.getComBoxDestinationFolder().setEnabled(false);
		downloadPanel.getButtonAdvancedConfiguration().setEnabled(false);
		downloadPanel.getLabelTotalFilesSizeValue().setText(
				UnitConverter.convertSize(totalExpectedFilesSize));
		downloadPanel.getLabelDownloadedValue().setText("");
		downloadPanel.getButtonCheckForAddonsStart().setEnabled(false);
		downloadPanel.getButtonCheckForAddonsCancel().setEnabled(false);
		downloadPanel.getButtonDownloadStart().setEnabled(false);
		downloadPanel.getProgressBarDownloadAddons().setMinimum(0);
		downloadPanel.getProgressBarDownloadAddons().setMaximum(100);
		downloadPanel.getProgressBarDownloadSingleAddon().setMinimum(0);
		downloadPanel.getProgressBarDownloadSingleAddon().setMaximum(100);
		downloadPanel.getProgressBarDownloadSingleAddon()
				.setIndeterminate(true);
	}

	private void initDownloadPanelForStartUncompressing() {

		downloadPanel.getLabelDownloadStatus().setText("Uncompressing...");
		downloadPanel.getLabelSpeedValue().setText("");
		downloadPanel.getLabelRemainingTimeValue().setText("");
		downloadPanel.getLabelActiveConnectionsValue().setText("");
		downloadPanel.getProgressBarDownloadAddons().setMinimum(0);
		downloadPanel.getProgressBarDownloadAddons().setMaximum(100);
		downloadPanel.getProgressBarDownloadSingleAddon()
				.setIndeterminate(true);
	}

	private void initDownloadPanelForEndDownload() {

		downloadPanel.getComBoxDestinationFolder().setEnabled(true);
		downloadPanel.getButtonAdvancedConfiguration().setEnabled(true);
		downloadPanel.getLabelTotalFilesSizeValue().setText("");
		downloadPanel.getLabelDownloadedValue().setText("");
		downloadPanel.getButtonCheckForAddonsStart().setEnabled(true);
		downloadPanel.getButtonCheckForAddonsCancel().setEnabled(true);
		downloadPanel.getButtonDownloadStart().setEnabled(true);
		downloadPanel.getProgressBarCheckForAddons().setMaximum(0);
		downloadPanel.getProgressBarDownloadAddons().setMaximum(0);
		downloadPanel.getProgressBarDownloadSingleAddon().setMaximum(0);
		downloadPanel.getLabelSpeedValue().setText("");
		downloadPanel.getLabelRemainingTimeValue().setText("");
		downloadPanel.getLabelActiveConnectionsValue().setText("");
		downloadPanel.getProgressBarDownloadSingleAddon().setIndeterminate(
				false);
	}

	private void terminate() {

		repositoryService.setDownloading(repositoryName, false);
		this.interrupt();
		System.gc();
	}

	private synchronized void executeUpdateTotalSize() {

		determineTotalExpectedFileSize();
		downloadPanel.getLabelTotalFilesSizeValue().setText(
				UnitConverter.convertSize(totalExpectedFilesSize));
	}

	private synchronized void executeUpdateTotalSizeProgress(long value) {

		if (totalExpectedFilesSize != 0) {// division by 0!
			incrementedFilesSize = incrementedFilesSize + value;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					downloadPanel
							.getProgressBarDownloadAddons()
							.setValue(
									(int) (((incrementedFilesSize) * 100) / totalExpectedFilesSize));
				}
			});
		}
	}

	private synchronized void executeUpdateSingleSizeProgress(final long value,
			final int pourcentage) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				downloadPanel.getProgressBarDownloadSingleAddon()
						.setIndeterminate(false);
				downloadPanel.getProgressBarDownloadSingleAddon().setValue(
						pourcentage);
				downloadPanel.getLabelDownloadedValue()
						.setText(
								UnitConverter.convertSize(incrementedFilesSize
										+ value));
			}
		});
	}

	private synchronized void executeUpdateDownloadSpeed() {

		long speed = 0;
		long offset = 0;
		long countFileSize = 0;
		for (AbstractConnexionDAO connect : connexionService.getConnexionDAOs()) {
			speed = speed + connect.getSpeed();
			offset = offset + connect.getOffset();
			countFileSize = countFileSize + connect.getCountFileSize();
		}
		if (speed != 0) {// division by 0
			downloadPanel.getLabelSpeedValue().setText(
					UnitConverter.convertSpeed(speed));
			if (averageDownloadSpeed > 0) {
				averageDownloadSpeed = (averageDownloadSpeed + speed) / 2;
			} else {
				averageDownloadSpeed = speed;
			}
			long remainingFileSize = totalExpectedFilesSize
					- incrementedFilesSize - (offset + countFileSize);
			long time = remainingFileSize / speed;
			downloadPanel.getLabelRemainingTimeValue().setText(
					UnitConverter.convertTime(time));
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
		connexionService
				.setMaximumClientDownloadSpeed(maximumClientDownloadSpeed
						/ activeConnections);

		downloadPanel.getLabelActiveConnectionsValue().setText(
				Integer.toString(activeConnections));
		if (activeConnections > maxActiveconnections) {
			maxActiveconnections = activeConnections;
		}
	}

	private synchronized void executeUpdateResponseTime(long responseTime) {
		averageResponseTime = (averageResponseTime + responseTime) / 2;
	}

	private synchronized void executeUncompressingProgress(final int value) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				downloadPanel.getProgressBarDownloadAddons().setValue(value);
			}
		});
	}

	private void finish() {

		String message = "Download finished successfully.";

		System.out.println(message);

		/* Cancel all connections */
		if (connexionService != null) {
			connexionService.cancel();
		}

		/* Update UI */
		downloadPanel.getProgressBarDownloadSingleAddon().setIndeterminate(
				false);
		downloadPanel.getProgressBarDownloadAddons().setValue(100);
		downloadPanel.getProgressBarDownloadSingleAddon().setValue(100);
		downloadPanel.getLabelSpeedValue().setText(
				UnitConverter.convertSpeed(0));
		downloadPanel.getLabelRemainingTimeValue().setText(
				UnitConverter.convertTime(0));

		/* Delete extra files */
		downloadPanel.getLabelDownloadStatus().setText(
				"Deleting extra files...");
		deleteExtraFiles();

		/* Generate download report */
		String report = generateReport(message);
		repositoryService.setReport(repositoryName, report);

		/* End Messages */
		downloadPanel.getLabelDownloadStatus().setText("Finished!");
		JOptionPane.showMessageDialog(facade.getMainPanel(),
				"Download is finished.", "Download",
				JOptionPane.INFORMATION_MESSAGE);

		/* Check for TFAR Update */
		if (tfarIsUpdated) {
			int response = JOptionPane.showConfirmDialog(facade.getMainPanel(),
					"TFAR files have changed. Proceed with TFAR installer?",
					"TFAR installer", JOptionPane.OK_CANCEL_OPTION);
			if (response == 0) {
				FirstPageTFARInstallerPanel firstPage = new FirstPageTFARInstallerPanel(
						facade);
				firstPage.init();
				firstPage.setVisible(true);
			}
		}

		/* Check for ACRE 2 Update */
		if (acre2IsUpdated) {
			int response = JOptionPane
					.showConfirmDialog(
							facade.getMainPanel(),
							"ACRE 2 files have changed. Proceed with ACRE 2 installer?",
							"ACRE 2 installer", JOptionPane.OK_CANCEL_OPTION);
			if (response == 0) {
				FirstPageACRE2InstallerPanel firstPage = new FirstPageACRE2InstallerPanel(
						facade);
				firstPage.init();
				firstPage.setVisible(true);
			}
		}

		initDownloadPanelForEndDownload();

		/* Check for Addons */
		downloadPanel.checkForAddons();

		terminate();
	}

	private void finishWithErrors(String message, List<Exception> errors) {

		System.out.println(message);

		/* Cancel all connections */
		if (connexionService != null) {
			connexionService.cancel();
		}

		/* Update UI */
		downloadPanel.getProgressBarDownloadSingleAddon().setIndeterminate(
				false);
		downloadPanel.getProgressBarDownloadAddons().setValue(100);
		downloadPanel.getProgressBarDownloadSingleAddon().setValue(100);
		downloadPanel.getLabelSpeedValue().setText(
				UnitConverter.convertSpeed(0));
		downloadPanel.getLabelRemainingTimeValue().setText(
				UnitConverter.convertTime(0));

		/* Delete extra files */
		downloadPanel.getLabelDownloadStatus().setText(
				"Deleting extra files...");
		deleteExtraFiles();

		/* Generate download report */
		String report = generateReport(message, errors);
		repositoryService.setReport(repositoryName, report);

		/* End Messages */
		downloadPanel.getLabelDownloadStatus().setText("Error!");
		downloadPanel.showDownloadReport();

		initDownloadPanelForEndDownload();

		/* Check for Addons */
		downloadPanel.checkForAddons();

		terminate();
	}

	private synchronized void executeCancelTooManyErrors(int value,
			List<Exception> errors) {

		if (!canceled) {
			canceled = true;
			String message = "Download has been canceled due to too many errors (>"
					+ value + ")";

			System.out.println(message);

			if (connexionService != null) {
				connexionService.cancel();
			}

			finishWithErrors(message, errors);
		}
	}

	private synchronized void executeCancelTooManyTimeoutErrors(int value,
			List<Exception> errors) {

		if (!canceled) {
			canceled = true;
			String message = "Download has been canceled due to too many consecutive time out errors (>"
					+ value + ")";

			System.out.println(message);

			if (connexionService != null) {
				connexionService.cancel();
			}

			finishWithErrors(message, errors);
		}
	}

	/* Generate Report */

	private String generateReport(String message) {

		String header = "--- Download report ---";
		String repositoryInfo = "Repository name: " + repositoryName;
		String endDate = "Download finished on: " + new Date().toLocaleString();

		// Server Connection
		averageResponseTime = Math.round(averageResponseTime / Math.pow(10, 6));
		String avgRespTime = "unavailable";
		if (averageResponseTime > 0) {
			avgRespTime = Long.toString(averageResponseTime) + " ms";
		}
		String avgDlSpeed = "unavailable";
		if (averageDownloadSpeed > 0) {
			avgDlSpeed = UnitConverter.convertSpeed(averageDownloadSpeed);
		}
		String serverConnectionInfo = "Server connection:" + "\n"
				+ "- Average response time: " + avgRespTime + "\n"
				+ "- Average download speed: " + avgDlSpeed + "\n"
				+ "- Number of active connections used: "
				+ maxActiveconnections;

		// Global File transfer:
		String savedSizeFileTransfer = UnitConverter
				.convertSize(totalDiskFilesSize - incrementedFilesSize);
		int savedSizeFileTransferFraction = 0;
		if (totalDiskFilesSize != 0) {
			savedSizeFileTransferFraction = (int) (((totalDiskFilesSize - incrementedFilesSize) * 100) / totalDiskFilesSize);
		}

		String fileTransfer = "Global file transfer:" + "\n"
				+ "- Number of files updated: " + listFilesToUpdate.size()
				+ "\n" + "- Total files size on disk: "
				+ UnitConverter.convertSize(totalDiskFilesSize) + "\n"
				+ "- Downloaded data: "
				+ UnitConverter.convertSize(incrementedFilesSize) + "\n"
				+ "- Saved: " + savedSizeFileTransfer + " ("
				+ savedSizeFileTransferFraction + "%)";

		// Partial file transfer
		String savedPartialSizeFileTransfer = UnitConverter
				.convertSize(totalUncompleteDiskFileSize
						- totalUncompleteExpectedFileSize);
		int savedPartialSizeFileTransferFraction = 0;
		if (totalUncompleteDiskFileSize != 0) {
			savedPartialSizeFileTransferFraction = (int) (((totalUncompleteDiskFileSize - totalUncompleteExpectedFileSize) * 100) / totalUncompleteDiskFileSize);
		}

		String partialFileTransferInfo = "Partial file transfer:" + "\n"
				+ "- Number of files updated: " + unCompleteFiles + "\n"
				+ "- Total files size on disk: "
				+ UnitConverter.convertSize(totalUncompleteDiskFileSize) + "\n"
				+ "- Downloaded data: "
				+ UnitConverter.convertSize(totalUncompleteExpectedFileSize)
				+ "\n" + "- Saved: " + savedPartialSizeFileTransfer + " ("
				+ savedPartialSizeFileTransferFraction + "%)";

		// Compressed file transfer
		String savedCompressedSizeFileTransfer = UnitConverter
				.convertSize(totalUncompressedFilesSize
						- totalCompressedFilesSize);
		int savedCompressedSizeFileTransferFraction = 0;
		if (totalUncompressedFilesSize != 0) {
			savedCompressedSizeFileTransferFraction = (int) (((totalUncompressedFilesSize - totalCompressedFilesSize) * 100) / totalUncompressedFilesSize);
		}

		String compressionFileTransferInfo = "Compressed file transfer:" + "\n"
				+ "- Number of files updated: " + compressedFiles + "\n"
				+ "- Total files size on disk: "
				+ UnitConverter.convertSize(totalUncompressedFilesSize) + "\n"
				+ "- Downloaded data: "
				+ UnitConverter.convertSize(totalCompressedFilesSize) + "\n"
				+ "- Saved: " + savedCompressedSizeFileTransfer + " ("
				+ savedCompressedSizeFileTransferFraction + "%)";

		String report = header + "\n" + repositoryInfo + "\n" + endDate
				+ "\n\n" + message + "\n\n" + serverConnectionInfo + "\n\n"
				+ fileTransfer + "\n\n" + partialFileTransferInfo + "\n\n"
				+ compressionFileTransferInfo;

		return report;
	}

	private String generateReport(String message, List<Exception> errors) {

		String header = "--- Download report ---";
		String repositoryInfo = "Repository name: " + repositoryName;
		String endDate = "Download finished on: " + new Date().toLocaleString();

		List<String> messages = new ArrayList<String>();
		for (Exception e : errors) {
			if (e instanceof IOException || e instanceof RepositoryException) {
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

		String report = header + "\n" + repositoryInfo + "\n" + endDate
				+ "\n\n" + message;
		for (String m : messages) {
			report = report + "\n" + m;
		}
		return report;
	}

	/* Business methods */

	private void determineTFARandACREupdates() {

		for (SyncTreeNodeDTO node : listFilesToUpdate) {
			checkTFARandACREupdate(node);
		}
	}

	private void checkTFARandACREupdate(SyncTreeNodeDTO node) {

		if (node.isLeaf()) {
			SyncTreeDirectoryDTO parent = node.getParent();
			if (parent != null) {
				checkTFARandACREupdate(parent);
			}
		} else {
			SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) node;
			if (node.getName().toLowerCase().contains("task_force_radio")) {
				if (directory.isUpdated() || directory.isChanged()) {
					tfarIsUpdated = true;
				}
			} else if (node.getName().toLowerCase().contains("acre2")) {
				if (directory.isUpdated() || directory.isChanged()) {
					acre2IsUpdated = true;
				}
			} else {
				SyncTreeDirectoryDTO parent = node.getParent();
				if (parent != null) {
					checkTFARandACREupdate(parent);
				}
			}
		}
	}

	private void getFiles(SyncTreeNodeDTO node) {

		if (!node.isLeaf()) {
			SyncTreeDirectoryDTO syncTreeDirectoryDTO = (SyncTreeDirectoryDTO) node;
			if (syncTreeDirectoryDTO.isSelected()
					&& syncTreeDirectoryDTO.isUpdated()) {
				listFilesToUpdate.add(syncTreeDirectoryDTO);
			} else if (syncTreeDirectoryDTO.isSelected()
					&& syncTreeDirectoryDTO.isDeleted()) {

				int count = 0;
				for (SyncTreeNodeDTO n : syncTreeDirectoryDTO.getList()) {
					if (n.isSelected() && n.isDeleted()) {
						count++;
					}
				}
				if (count == syncTreeDirectoryDTO.getList().size()) {
					listFilesToDelete.add(syncTreeDirectoryDTO);
				}
			}
			for (SyncTreeNodeDTO n : syncTreeDirectoryDTO.getList()) {
				getFiles(n);
			}
		} else {
			SyncTreeLeafDTO syncTreeLeafDTO = (SyncTreeLeafDTO) node;
			if (syncTreeLeafDTO.isSelected() && syncTreeLeafDTO.isUpdated()) {
				listFilesToUpdate.add(syncTreeLeafDTO);
			} else if (syncTreeLeafDTO.isSelected()
					&& syncTreeLeafDTO.isDeleted()) {

				SyncTreeDirectoryDTO parent = syncTreeLeafDTO.getParent();
				if (parent.getName().equals("racine")) {
					listFilesToDelete.add(syncTreeLeafDTO);
				} else {
					int count = 0;
					for (SyncTreeNodeDTO n : parent.getList()) {
						if (n.isSelected() && n.isDeleted()) {
							count++;
						}
					}
					if (count == parent.getList().size()) {
						listFilesToDelete.add(parent);
					} else {
						listFilesToDelete.add(syncTreeLeafDTO);
					}
				}
			}
		}
	}

	private void determineFilesVariables() {

		incrementedFilesSize = 0;

		totalDiskFilesSize = 0;
		totalCompressedFilesSize = 0;
		totalUncompressedFilesSize = 0;
		totalUncompleteExpectedFileSize = 0;
		totalUncompleteDiskFileSize = 0;
		for (SyncTreeNodeDTO node : listFilesToUpdate) {
			if (node.isLeaf()) {
				SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
				// TotalExpectedFilesSize
				determineTotalExpectedFileSize();
				// IncrementedFilesSize
				if (leaf.getDownloadStatus().equals(DownloadStatus.DONE)) {
					incrementedFilesSize = incrementedFilesSize
							+ FileSizeComputer.computeExpectedSize(leaf);
				}
				// TotalDiskFilesSize
				totalDiskFilesSize = totalDiskFilesSize + leaf.getSize();
				// TotalUncompleteExpectedFileSize & TotalUncompleteDiskFileSize
				if (leaf.getComplete() != 0) {
					unCompleteFiles++;
					totalUncompleteExpectedFileSize = totalUncompleteExpectedFileSize
							+ FileSizeComputer.computeExpectedSize(leaf);
					totalUncompleteDiskFileSize = totalUncompleteDiskFileSize
							+ leaf.getSize();
				} else if (leaf.isCompressed()) { // TotalCompressedFilesSize &
													// TotalUncompressedFilesSize
					compressedFiles++;
					totalCompressedFilesSize = totalCompressedFilesSize
							+ leaf.getCompressedSize();
					totalUncompressedFilesSize = totalUncompressedFilesSize
							+ leaf.getSize();
				}
			}
		}
	}

	private void determineTotalExpectedFileSize() {

		totalExpectedFilesSize = 0;
		for (SyncTreeNodeDTO node : listFilesToUpdate) {
			if (node.isLeaf()) {
				SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
				totalExpectedFilesSize = totalExpectedFilesSize
						+ FileSizeComputer.computeExpectedSize(leaf);
			}
		}
	}

	private void deleteExtraFiles() {

		for (SyncTreeNodeDTO node : listFilesToDelete) {
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

	public void cancel() {

		this.canceled = true;
		if (connexionService != null) {
			connexionService.cancel();
		}

		initDownloadPanelForEndDownload();
		downloadPanel.getLabelDownloadStatus().setText("Canceled!");
		downloadPanel.checkForAddons();
		terminate();
	}

	public void pause() {

		this.canceled = true;
		if (connexionService != null) {
			connexionService.cancel();
		}

		downloadPanel.getLabelDownloadStatus().setText("Paused");
		downloadPanel.getButtonAdvancedConfiguration().setEnabled(true);
		terminate();
	}
}
