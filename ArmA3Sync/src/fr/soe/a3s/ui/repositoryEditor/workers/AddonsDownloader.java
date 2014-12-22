package fr.soe.a3s.ui.repositoryEditor.workers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import fr.soe.a3s.constant.DownloadStatus;
import fr.soe.a3s.controller.ObserverActiveConnnection;
import fr.soe.a3s.controller.ObserverEnd;
import fr.soe.a3s.controller.ObserverError;
import fr.soe.a3s.controller.ObserverFileSize;
import fr.soe.a3s.controller.ObserverFilesNumber;
import fr.soe.a3s.controller.ObserverSpeed;
import fr.soe.a3s.dao.AbstractConnexionDAO;
import fr.soe.a3s.dao.FileAccessMethods;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.service.AbstractConnexionService;
import fr.soe.a3s.service.ConnexionServiceFactory;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repositoryEditor.DownloadPanel;
import fr.soe.a3s.ui.repositoryEditor.UnitConverter;
import fr.soe.a3s.ui.tools.acre2Editor.FirstPageACRE2InstallerPanel;
import fr.soe.a3s.ui.tools.acreEditor.FirstPageACREInstallerPanel;
import fr.soe.a3s.ui.tools.tfarEditor.FirstPageTFARInstallerPanel;

public class AddonsDownloader extends Thread {

	private final Facade facade;
	private final SyncTreeDirectoryDTO racine;
	private long incrementedFilesSize;
	private final long totalFilesSize;
	private final String repositoryName;
	private final List<SyncTreeNodeDTO> listFilesToUpdate = new ArrayList<SyncTreeNodeDTO>();
	private final List<SyncTreeNodeDTO> listFilesToDelete = new ArrayList<SyncTreeNodeDTO>();
	private boolean canceled = false;
	private boolean tfarIsUpdated = false;
	private boolean acreIsUpdated = false;
	private boolean acre2IsUpdated = false;
	private final DownloadPanel downloadPanel;

	/* Services */
	private AbstractConnexionService connexionService;
	private final RepositoryService repositoryService = new RepositoryService();

	public AddonsDownloader(Facade facade, String repositoryName,
			SyncTreeDirectoryDTO racine, long totalFilesSize,
			DownloadPanel downloadPanel) {
		this.facade = facade;
		this.racine = racine;
		this.repositoryName = repositoryName;
		this.totalFilesSize = totalFilesSize;
		this.downloadPanel = downloadPanel;
	}

	@Override
	public void run() {

		// Get files list
		for (SyncTreeNodeDTO node : racine.getList()) {
			getFiles(node);
		}

		// Check if @TFAR and @ACRE have been updated
		for (SyncTreeNodeDTO node : listFilesToUpdate) {
			checkTFARandACREupdate(node);
		}

		// Set downloading state
		repositoryService.setDownloading(repositoryName, true);

		// Init DownloadPanel for start download
		initDownloadPanelForStartDownload();

		// Resuming download
		incrementedFilesSize = 0;
		List<SyncTreeNodeDTO> list = new ArrayList<SyncTreeNodeDTO>();
		for (SyncTreeNodeDTO node : listFilesToUpdate) {
			if (node.getDownloadStatus().equals(DownloadStatus.DONE)) {
				if (node.isLeaf()) {
					SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
					long size = leaf.getSize();
					incrementedFilesSize = incrementedFilesSize + size;
				}
			} else {
				list.add(node);// not yet downloaded files
			}
		}
		listFilesToUpdate.clear();
		listFilesToUpdate.addAll(list);

		if (listFilesToUpdate.isEmpty()) {
			finish();
			initDownloadPanelForFinishedDownload();
			terminate();
			return;
		}

		downloadPanel.getProgressBarDownloadAddons().setValue(
				(int) ((incrementedFilesSize * 100) / totalFilesSize));

		// Download Files

		try {
			connexionService = ConnexionServiceFactory
					.getServiceFromRepositoryMultiConnections(repositoryName);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Download", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			for (AbstractConnexionDAO connect : connexionService
					.getConnexionDAOs()) {

				connect.addObserverFilesNumber(new ObserverFilesNumber() {
					@Override
					public synchronized void update(SyncTreeNodeDTO node) {
						if (node.isLeaf()) {
							SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
							long size = leaf.getSize();
							incrementedFilesSize = incrementedFilesSize + size;
							downloadPanel
									.getProgressBarDownloadAddons()
									.setValue(
											(int) (((incrementedFilesSize) * 100) / totalFilesSize));
						}
					}
				});

				connect.addObserverFileSize(new ObserverFileSize() {
					@Override
					public void update(long value, SyncTreeNodeDTO node) {
						downloadPanel.getProgressBarDownloadSingleAddon()
								.setIndeterminate(false);
						if (node.isLeaf()) {
							SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
							long size = leaf.getSize();
							downloadPanel.getProgressBarDownloadSingleAddon()
									.setValue((int) (value * 100 / size));
							downloadPanel.getLabelDownloadedValue().setText(
									UnitConverter
											.convertSize(incrementedFilesSize
													+ value));
						}
					}
				});

				connect.addObserverSpeed(new ObserverSpeed() {
					@Override
					public synchronized void update() {
						long speed = 0;
						long offset = 0;
						for (AbstractConnexionDAO connect : connexionService
								.getConnexionDAOs()) {
							speed = speed + connect.getSpeed();
							offset = offset + connect.getOffset();
						}
						if (speed != 0) {// division by 0
							downloadPanel.getLabelSpeedValue().setText(
									UnitConverter.convertSpeed(speed));
							long remainingFileSize = totalFilesSize
									- incrementedFilesSize - offset;
							long time = remainingFileSize / speed;
							downloadPanel.getLabelRemainingTimeValue().setText(
									UnitConverter.convertTime(time));
						}
					}
				});

				connect.addObserverActiveConnection(new ObserverActiveConnnection() {
					@Override
					public synchronized void update() {
						int activeConnections = 0;
						for (AbstractConnexionDAO connect : connexionService
								.getConnexionDAOs()) {
							if (connect.isActiveConnection()) {
								activeConnections++;
							}
						}
						downloadPanel.getLabelActiveConnectionsValue().setText(
								Integer.toString(activeConnections));
					}
				});

				connect.addObserverEnd(new ObserverEnd() {
					@Override
					public void end() {
						connexionService.cancel(false);
						finish();
						initDownloadPanelForFinishedDownload();
						terminate();
					}
				});

				connect.addObserverError(new ObserverError() {
					@Override
					public void error(List<Exception> errors) {
						if (!canceled) {
							connexionService.cancel(false);
							finishWithErrors(errors);
							initDownloadPanelForFinishedDownload();
							terminate();
						}
					}
				});
			}

			connexionService.downloadAddons(repositoryName, listFilesToUpdate);

		} catch (Exception e) {
			List<Exception> errors = new ArrayList<Exception>();
			errors.add(e);
			connexionService.cancel(false);// not null
			finishWithErrors(errors);
			initDownloadPanelForFinishedDownload();
			terminate();
		}
	}

	private void initDownloadPanelForStartDownload() {

		downloadPanel.getLabelDownloadStatus().setText("Downloading...");
		downloadPanel.getComBoxDestinationFolder().setEnabled(false);
		downloadPanel.getButtonAdvancedConfiguration().setEnabled(false);
		downloadPanel.getLabelTotalFilesSizeValue().setText(
				UnitConverter.convertSize(totalFilesSize));
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

	private void initDownloadPanelForFinishedDownload() {

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

	private void finish() {

		downloadPanel.getProgressBarDownloadSingleAddon().setIndeterminate(
				false);
		downloadPanel.getLabelSpeedValue().setText(
				UnitConverter.convertSpeed(0));
		downloadPanel.getLabelRemainingTimeValue().setText(
				UnitConverter.convertTime(0));

		/* Delete extra files */
		downloadPanel.getLabelDownloadStatus().setText(
				"Deleting extra files...");
		deleteExtraFiles();

		/* End Message */
		downloadPanel.getLabelDownloadStatus().setText("Finished!");
		JOptionPane.showMessageDialog(facade.getMainPanel(),
				"Download is finished.", "Download",
				JOptionPane.INFORMATION_MESSAGE);

		/* Check for Addons */
		downloadPanel.checkForAddons();

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

		/* Check for ACRE Update */
		if (acreIsUpdated) {
			int response = JOptionPane.showConfirmDialog(facade.getMainPanel(),
					"ACRE files have changed. Proceed with ACRE installer?",
					"ACRE installer", JOptionPane.OK_CANCEL_OPTION);
			if (response == 0) {
				FirstPageACREInstallerPanel firstPage = new FirstPageACREInstallerPanel(
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
	}

	private void finishWithErrors(List<Exception> errors) {

		connexionService.cancel(false);

		downloadPanel.getProgressBarDownloadSingleAddon().setIndeterminate(
				false);
		downloadPanel.getLabelSpeedValue().setText(
				UnitConverter.convertSpeed(0));
		downloadPanel.getLabelRemainingTimeValue().setText(
				UnitConverter.convertTime(0));

		/* Delete extra files */
		downloadPanel.getLabelDownloadStatus().setText(
				"Deleting extra files...");
		deleteExtraFiles();

		/* End Message */
		downloadPanel.getLabelDownloadStatus().setText("Error!");
		String message = "Download finished with errors:";

		List<String> messages = new ArrayList<String>();
		List<String> causes = new ArrayList<String>();

		for (Exception e : errors) {
			if (!messages.contains(e.getMessage())) {
				if (e instanceof FileNotFoundException) {
					messages.add(e.getMessage());
				} else if (e.getCause() != null) {
					if (!causes.contains(e.getCause().toString())) {
						causes.add(e.getCause().toString());
						messages.add(e.getMessage());
					}
				} else if (e.getMessage() != null) {
					messages.add(e.getMessage());
				}
			}
		}
		for (String m : messages) {
			message = message + "\n" + " - " + m;
		}
		JOptionPane.showMessageDialog(facade.getMainPanel(), message,
				"Download", JOptionPane.ERROR_MESSAGE);

		/* Check for Addons */
		downloadPanel.checkForAddons();
	}

	private void terminate() {

		repositoryService.setDownloading(repositoryName, false);
		this.interrupt();
		System.gc();
	}

	private void checkTFARandACREupdate(SyncTreeNodeDTO node) {

		if (node.isLeaf()) {
			SyncTreeDirectoryDTO parent = node.getParent();
			if (parent != null) {
				checkTFARandACREupdate(parent);
			}
		} else {
			SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) node;
			if (node.getName().toLowerCase().contains("@task_force_radio")) {
				if (directory.isUpdated() || directory.isChanged()) {
					tfarIsUpdated = true;
				}
			} else if (node.getName().toLowerCase().contains("@acre2")) {
				if (directory.isUpdated() || directory.isChanged()) {
					acre2IsUpdated = true;
				}
			} else if (node.getName().toLowerCase().contains("@acre")) {
				if (directory.isUpdated() || directory.isChanged()) {
					acreIsUpdated = true;
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
		connexionService.cancel(false);
		initDownloadPanelForFinishedDownload();
		terminate();
		JOptionPane.showMessageDialog(facade.getMainPanel(),
				"Download canceled!", "Download",
				JOptionPane.INFORMATION_MESSAGE);
		downloadPanel.getLabelDownloadStatus().setText("Canceled!");
		downloadPanel.checkForAddons();
	}

	public void pause() {

		connexionService.cancel(true);
		downloadPanel.getLabelDownloadStatus().setText("Paused");
		terminate();
	}
}
