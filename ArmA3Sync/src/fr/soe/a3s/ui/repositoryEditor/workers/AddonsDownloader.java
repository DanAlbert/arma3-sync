package fr.soe.a3s.ui.repositoryEditor.workers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import fr.soe.a3s.controller.ObserverFileSize;
import fr.soe.a3s.controller.ObserverFilesNumber;
import fr.soe.a3s.controller.ObserverSpeed;
import fr.soe.a3s.dao.FileAccessMethods;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.service.AbstractConnexionService;
import fr.soe.a3s.service.AddonService;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.service.ConnexionServiceFactory;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repositoryEditor.DownloadPanel;
import fr.soe.a3s.ui.repositoryEditor.UnitConverter;
import fr.soe.a3s.ui.tfarEditor.FirstPageTFARInstallerPanel;

public class AddonsDownloader extends Thread {

	private final Facade facade;
	private final SyncTreeDirectoryDTO racine;
	private int lastIndexFileDownloaded;
	private long incrementedFilesSize;
	private boolean resume;
	private final long totalFilesSize;
	private final String repositoryName;
	private final List<SyncTreeNodeDTO> listFilesToUpdate = new ArrayList<SyncTreeNodeDTO>();
	private final List<SyncTreeNodeDTO> listFilesToDelete = new ArrayList<SyncTreeNodeDTO>();
	private boolean canceled = false;
	private boolean paused = false;
	private long offset;
	private final String eventName;
	private SyncTreeDirectoryDTO parent;
	private boolean found;
	private boolean tfarIsUpdated = false;
	private boolean acreIsUpdated = false;
	private final DownloadPanel downloadPanel;
	/* Services */
	private AbstractConnexionService connexionService;
	private final RepositoryService repositoryService = new RepositoryService();
	private final ConfigurationService configurationService = new ConfigurationService();
	private final AddonService addonService = new AddonService();;

	public AddonsDownloader(Facade facade, String repositoryName,
			SyncTreeDirectoryDTO racine, long totalFilesSize, String eventName,
			DownloadPanel downloadPanel) {
		this.facade = facade;
		this.racine = racine;
		this.repositoryName = repositoryName;
		this.totalFilesSize = totalFilesSize;
		this.eventName = eventName;
		this.downloadPanel = downloadPanel;
	}

	@Override
	public void run() {

		// Get files list
		for (SyncTreeNodeDTO node : racine.getList()) {
			getFiles(node);
		}

		int nbFiles = listFilesToUpdate.size();

		// Check if @TFAR and @ACRE have been updated
		for (SyncTreeNodeDTO node : listFilesToUpdate) {
			checkTFARandACREupdate(node);
		}

		repositoryService.setDownloading(repositoryName, true);

		downloadPanel.getLabelDownloadStatus().setText("Downloading...");
		downloadPanel.getLabelTotalFilesSizeValue().setText(
				UnitConverter.convertSize(totalFilesSize));
		downloadPanel.getLabelDownloadedValue().setText("");
		downloadPanel.getButtonCheckForAddonsStart().setEnabled(false);
		downloadPanel.getButtonDownloadStart().setEnabled(false);
		downloadPanel.getProgressBarDownloadAddons().setMinimum(0);
		downloadPanel.getProgressBarDownloadAddons().setMaximum(nbFiles);
		downloadPanel.getProgressBarDownloadSingleAddon().setMinimum(0);
		downloadPanel.getProgressBarDownloadSingleAddon().setMaximum(100);
		downloadPanel.getProgressBarDownloadSingleAddon()
				.setIndeterminate(true);

		// Resuming download
		lastIndexFileDownloaded = repositoryService
				.getLastIndexFileTransfered(repositoryName);
		incrementedFilesSize = repositoryService
				.getIncrementedFilesSize(repositoryName);
		resume = repositoryService.isResume(repositoryName);
		downloadPanel.getProgressBarDownloadAddons().setValue(
				lastIndexFileDownloaded);

		try {
			connexionService = ConnexionServiceFactory
					.getServiceFromRepository(repositoryName);

			connexionService.getConnexionDAO().addObserverFilesNumber(
					new ObserverFilesNumber() {
						@Override
						public void update(int value) {
							lastIndexFileDownloaded++;
							downloadPanel.getProgressBarDownloadAddons()
									.setValue(lastIndexFileDownloaded);
							SyncTreeNodeDTO node = listFilesToUpdate
									.get(lastIndexFileDownloaded - 1);
							if (node.isLeaf()) {
								SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
								long size = leaf.getSize();
								incrementedFilesSize = incrementedFilesSize
										+ size;
							}
							if (!paused && !canceled) {
								downloadPanel
										.getLabelDownloadedValue()
										.setText(
												UnitConverter
														.convertSize(incrementedFilesSize));
							}
						}
					});
			connexionService.getConnexionDAO().addObserverFileSize(
					new ObserverFileSize() {
						@Override
						public void update(long value) {
							downloadPanel.getProgressBarDownloadSingleAddon()
									.setIndeterminate(false);
							offset = value;
							SyncTreeNodeDTO node = listFilesToUpdate
									.get(lastIndexFileDownloaded);
							if (node.isLeaf()) {
								SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
								long size = leaf.getSize();
								downloadPanel
										.getProgressBarDownloadSingleAddon()
										.setValue((int) (value * 100 / size));
								downloadPanel
										.getLabelDownloadedValue()
										.setText(
												UnitConverter
														.convertSize(incrementedFilesSize
																+ value));
							}
						}
					});
			connexionService.getConnexionDAO().addObserverSpeed(
					new ObserverSpeed() {
						@Override
						public void update(long value) {
							if (value != 0) {
								downloadPanel.getLabelSpeedValue().setText(
										UnitConverter.convertSpeed(value));
								long remainingFileSize = totalFilesSize
										- incrementedFilesSize - offset;
								long time = remainingFileSize / value;
								downloadPanel
										.getLabelRemainingTimeValue()
										.setText(
												UnitConverter.convertTime(time));
							}
						}
					});

			/* Download files */
			List<SyncTreeNodeDTO> newListFiles = new ArrayList<SyncTreeNodeDTO>();
			for (int i = lastIndexFileDownloaded; i < nbFiles; i++) {
				newListFiles.add(listFilesToUpdate.get(i));
			}

			connexionService.downloadAddons(repositoryName, newListFiles,
					resume);

			resume = false;

			/* Stop indeterminate single download */
			downloadPanel.getProgressBarDownloadSingleAddon().setIndeterminate(
					false);

			/* Delete extra files */
			downloadPanel.getLabelDownloadStatus().setText(
					"Deleting extra files...");
			deleteExtraFiles();

			/* End messages */
			if (!canceled && !paused) {
				JOptionPane.showMessageDialog(facade.getMainPanel(),
						"Download is finished.", "Download",
						JOptionPane.INFORMATION_MESSAGE);
				downloadPanel.getLabelDownloadStatus().setText("Finished!");
				repositoryService.saveTransfertParameters(repositoryName, 0, 0,
						false);
			} else if (canceled) {
				JOptionPane.showMessageDialog(facade.getMainPanel(),
						"Download canceled.", "Download",
						JOptionPane.INFORMATION_MESSAGE);
				downloadPanel.getLabelDownloadStatus().setText("Canceled!");
			} else if (paused) {
				downloadPanel.getLabelDownloadStatus().setText("Paused");
			}
		} catch (Exception e) {
			/* Stop indeterminate single download */
			downloadPanel.getProgressBarDownloadSingleAddon().setIndeterminate(
					false);
			downloadPanel.getLabelDownloadStatus().setText("");
			// if (!canceled && !paused) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			downloadPanel.getLabelDownloadStatus().setText("Error!");
			// }
		} finally {
			downloadPanel.getButtonCheckForAddonsStart().setEnabled(true);
			downloadPanel.getButtonDownloadStart().setEnabled(true);
			downloadPanel.getProgressBarCheckForAddons().setMaximum(0);
			downloadPanel.getProgressBarDownloadAddons().setMaximum(0);
			downloadPanel.getProgressBarDownloadSingleAddon().setMaximum(0);
			downloadPanel.getLabelTotalFilesSizeValue().setText("");
			downloadPanel.getLabelDownloadedValue().setText("");
			downloadPanel.getLabelSpeedValue().setText("");
			downloadPanel.getLabelRemainingTimeValue().setText("");
			repositoryService.setDownloading(repositoryName, false);
			if (!paused) {
				downloadPanel.checkForAddons();
				if (tfarIsUpdated) {
					int response = JOptionPane
							.showConfirmDialog(
									facade.getMainPanel(),
									"TFAR files have changed. Proceed with TFAR installer?",
									"TFAR installer",
									JOptionPane.OK_CANCEL_OPTION);
					if (response == 0) {
						FirstPageTFARInstallerPanel firstPage = new FirstPageTFARInstallerPanel(
								facade);
						firstPage.init();
						firstPage.setVisible(true);
					}
				}
			}
			this.interrupt();
			System.gc();
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
			if (node.getName().toLowerCase().contains("@task_force_radio")) {
				if (directory.isUpdated() || directory.isChanged()) {
					tfarIsUpdated = true;
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
		repositoryService.saveTransfertParameters(repositoryName, 0, 0, false);
	}

	public void pause() {
		this.paused = true;
		connexionService.cancel(true);
		repositoryService.saveTransfertParameters(repositoryName,
				incrementedFilesSize, lastIndexFileDownloaded, true);
	}

	public int getLastIndexFileDownloaded() {
		return lastIndexFileDownloaded;
	}

	public long getIncrementedFilesSize() {
		return incrementedFilesSize;
	}

	private void selectAllAscending(SyncTreeNodeDTO syncTreeNodeDTO) {
		if (syncTreeNodeDTO != null) {
			syncTreeNodeDTO.setSelected(true);
			SyncTreeNodeDTO parent = syncTreeNodeDTO.getParent();
			selectAllAscending(parent);
		}
	}
}
