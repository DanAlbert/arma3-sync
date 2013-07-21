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
import fr.soe.a3s.exception.FtpException;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.exception.ServerInfoNotFoundException;
import fr.soe.a3s.exception.SyncFileNotFoundException;
import fr.soe.a3s.service.AddonService;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.service.FtpService;
import fr.soe.a3s.service.ProfileService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repositoryEditor.UnitConverter;

public class AddonsDownloader extends Thread {

	private Facade facade;
	private SyncTreeDirectoryDTO racine;
	private int lastIndexFileDownloaded;
	private long incrementedFilesSize;
	private boolean resume;
	private long totalFilesSize;
	private String repositoryName;
	private List<SyncTreeNodeDTO> listFilesToUpdate = new ArrayList<SyncTreeNodeDTO>();
	private List<SyncTreeNodeDTO> listFilesToDelete = new ArrayList<SyncTreeNodeDTO>();
	private FtpService ftpService = new FtpService();
	private RepositoryService repositoryService = new RepositoryService();
	private ConfigurationService configurationService = new ConfigurationService();
	private boolean canceled = false;
	private boolean paused = false;
	private long offset;

	public AddonsDownloader(Facade facade, String repositoryName,
			SyncTreeDirectoryDTO racine, long totalFilesSize) {
		this.facade = facade;
		this.racine = racine;
		this.repositoryName = repositoryName;
		this.totalFilesSize = totalFilesSize;
	}

	public void run() {

		// Get files list
		for (SyncTreeNodeDTO node : racine.getList()) {
			getFiles(node);
		}
	
		int nbFiles = listFilesToUpdate.size();

		repositoryService.setDownloading(repositoryName, true);
		facade.getDownloadPanel().getLabelDownloadStatus()
				.setText("Downloading...");
		facade.getDownloadPanel().getLabelTotalFilesSizeValue()
				.setText(UnitConverter.convertSize(totalFilesSize));
		facade.getDownloadPanel().getLabelDownloadedValue().setText("");
		facade.getDownloadPanel().getButtonCheckForAddonsStart()
				.setEnabled(false);
		facade.getDownloadPanel().getButtonDownloadStart().setEnabled(false);
		facade.getDownloadPanel().getProgressBarDownloadAddons().setMinimum(0);
		facade.getDownloadPanel().getProgressBarDownloadAddons()
				.setMaximum(nbFiles);
		facade.getDownloadPanel().getProgressBarDownloadSingleAddon()
				.setMinimum(0);
		facade.getDownloadPanel().getProgressBarDownloadSingleAddon()
				.setMaximum(100);
		
		//Resuming download
		lastIndexFileDownloaded = repositoryService.getLastIndexFileDownloaded(repositoryName);
		incrementedFilesSize = repositoryService.getIncrementedFilesSize(repositoryName);
		resume = repositoryService.isResume(repositoryName);
		facade.getDownloadPanel().getProgressBarDownloadAddons().setValue(lastIndexFileDownloaded);
		
		ftpService.getFtpDAO().addObserverFilesNumber(
				new ObserverFilesNumber() {
					@Override
					public void update(int value) {
						lastIndexFileDownloaded++;
						facade.getDownloadPanel()
								.getProgressBarDownloadAddons().setValue(lastIndexFileDownloaded);
						SyncTreeNodeDTO node = listFilesToUpdate.get(lastIndexFileDownloaded - 1);
						if (node.isLeaf()) {
							SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
							long size = leaf.getSize();
							incrementedFilesSize = incrementedFilesSize + size;
						}
						if (!paused && !canceled) {
							facade.getDownloadPanel()
									.getLabelDownloadedValue()
									.setText(
											UnitConverter
													.convertSize(incrementedFilesSize));
						}
					}
				});
		ftpService.getFtpDAO().addObserverFileSize(new ObserverFileSize() {
			@Override
			public void update(long value) {
				
				offset = value;
				
				SyncTreeNodeDTO node = listFilesToUpdate
						.get(lastIndexFileDownloaded);
				if (node.isLeaf()) {
					SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
					long size = leaf.getSize();
					facade.getDownloadPanel()
							.getProgressBarDownloadSingleAddon()
							.setValue((int) (value * 100 / size));
					facade.getDownloadPanel()
							.getLabelDownloadedValue()
							.setText(
									UnitConverter
											.convertSize(incrementedFilesSize
													+ value));
				}
			}
		});
		ftpService.getFtpDAO().addObserverSpeed(new ObserverSpeed() {
			@Override
			public void update(long value) {
				facade.getDownloadPanel().getLabelSpeedValue()
						.setText(UnitConverter.convertSpeed(value));
				long remainingFileSize = totalFilesSize - incrementedFilesSize-offset;
				long time = remainingFileSize / value;
				facade.getDownloadPanel().getLabelRemainingTimeValue()
						.setText(UnitConverter.convertTime(time));
			}
		});

		try {
			/* Download files */
			List<SyncTreeNodeDTO> newListFiles = new ArrayList<SyncTreeNodeDTO>();
			for (int i = lastIndexFileDownloaded; i < nbFiles; i++) {
				newListFiles.add(listFilesToUpdate.get(i));
			}

			ftpService.downloadAddons(repositoryName,newListFiles,resume);
			
			resume = false;

			/* Delete extra files */
			facade.getDownloadPanel().getLabelDownloadStatus()
					.setText("Deleting extra files...");
			deleteExtraFiles();

			/* End messages */
			if (!canceled && !paused) {
				JOptionPane.showMessageDialog(facade.getMainPanel(),
						"Download is finished.", "Download",
						JOptionPane.INFORMATION_MESSAGE);
				facade.getDownloadPanel().getLabelDownloadStatus()
						.setText("Finished!");
				repositoryService.saveDownloadParameters(repositoryName,0,0,false);
				/* Check for Addons */
				checkForAddons();
			} else if (canceled) {
				JOptionPane.showMessageDialog(facade.getMainPanel(),
						"Download canceled.", "Download",
						JOptionPane.INFORMATION_MESSAGE);
				facade.getDownloadPanel().getLabelDownloadStatus()
						.setText("Canceled!");
				/* Check for Addons */
				checkForAddons();
			} else if (paused) {
				facade.getDownloadPanel().getLabelDownloadStatus()
						.setText("Paused");
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			facade.getDownloadPanel().getLabelDownloadStatus()
					.setText("Error!");
		} finally {
			facade.getDownloadPanel().getButtonCheckForAddonsStart()
					.setEnabled(true);
			facade.getDownloadPanel().getButtonDownloadStart().setEnabled(true);
			facade.getDownloadPanel().getProgressBarCheckForAddons()
					.setMaximum(0);
			facade.getDownloadPanel().getProgressBarDownloadAddons()
					.setMaximum(0);
			facade.getDownloadPanel().getProgressBarDownloadSingleAddon()
					.setMaximum(0);
			facade.getDownloadPanel().getLabelTotalFilesSizeValue().setText("");
			facade.getDownloadPanel().getLabelDownloadedValue().setText("");
			facade.getDownloadPanel().getLabelSpeedValue().setText("");
			facade.getDownloadPanel().getLabelRemainingTimeValue().setText("");
			repositoryService.setDownloading(repositoryName, false);
			System.gc();// Required for unlocking files!
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
				listFilesToDelete.add(syncTreeDirectoryDTO);
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
				listFilesToDelete.add(syncTreeLeafDTO);
			}
		}
	}

	private void checkForAddons() throws Exception {

		facade.getDownloadPanel().getArbre().setEnabled(false);
		facade.getDownloadPanel().getLabelCheckForAddonsStatus()
				.setText("Checking...");
		facade.getDownloadPanel().getButtonCheckForAddonsStart()
				.setEnabled(false);
		facade.getDownloadPanel().getProgressBarCheckForAddons().setMinimum(0);
		facade.getDownloadPanel().getProgressBarCheckForAddons()
				.setMaximum(100);
		repositoryService.getRepositoryBuilderDAO().addObserverFilesNumber(
				new ObserverFilesNumber() {
					public void update(int value) {
						facade.getDownloadPanel()
								.getProgressBarCheckForAddons().setValue(value);
					}
				});

		// Addons Panel: Update Available Addons
		AddonService addonService = new AddonService();
		addonService.resetAvailableAddonTree();
		facade.getAddonsPanel().updateAvailableAddons();
		
		// Addons Panel: Update Addons Group
		ProfileService profileService = new ProfileService();
		configurationService.setViewMode(true);//required to merge
		facade.getAddonsPanel().getCheckBoxTree().setSelected(true);
		profileService.merge(addonService.getAvailableAddonsTree(),
				profileService.getAddonGroupsTree());
		facade.getAddonsPanel().updateAddonGroups();
		facade.getAddonsPanel().expandAddonGroups();
		facade.getAddonOptionsPanel().updateAddonPriorities();

		FtpService ftpService = new FtpService();
		ftpService.getSync(repositoryName);
		SyncTreeDirectoryDTO parent = repositoryService.getSync(repositoryName);
		facade.getDownloadPanel().updateAddons(parent);
		facade.getDownloadPanel().getButtonCheckForAddonsStart()
				.setEnabled(true);
		facade.getDownloadPanel().getLabelCheckForAddonsStatus()
				.setText("Finished!");
		facade.getDownloadPanel().getArbre().setEnabled(true);
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
		ftpService.stopDownload();
		repositoryService.saveDownloadParameters(repositoryName,0,0,false);
	}

	public void pause() {
		this.paused = true;
		ftpService.stopDownload();
		repositoryService.saveDownloadParameters(repositoryName,incrementedFilesSize,lastIndexFileDownloaded,true);
	}

	public int getLastIndexFileDownloaded() {
		return lastIndexFileDownloaded;
	}

	public long getIncrementedFilesSize() {
		return incrementedFilesSize;
	}

}
