package fr.soe.a3s.ui.repositoryEditor.workers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import fr.soe.a3s.controller.ObserverFilesNumber;
import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.service.AddonService;
import fr.soe.a3s.service.FtpService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;

public class AddonsChecker extends Thread {

	private Facade facade;
	private String repositoryName;
	private String eventName;
	private RepositoryService repositoryService = new RepositoryService();
	private SyncTreeDirectoryDTO parent;

	public AddonsChecker(Facade facade, String repositoryName, String eventName) {
		this.facade = facade;
		this.repositoryName = repositoryName;
		this.eventName = eventName;
	}

	public void run() {

		facade.getDownloadPanel().getButtonCheckForAddonsCancel()
				.setEnabled(true);
		facade.getDownloadPanel().getLabelCheckForAddonsStatus().setText("");
		facade.getDownloadPanel().getButtonCheckForAddonsStart()
				.setEnabled(false);
		facade.getDownloadPanel().getButtonDownloadStart().setEnabled(false);
		facade.getDownloadPanel().getProgressBarCheckForAddons().setMinimum(0);
		facade.getDownloadPanel().getProgressBarCheckForAddons()
				.setMaximum(100);
		RepositoryService repositoryService = new RepositoryService();
		repositoryService.getRepositoryBuilderDAO().addObserverFilesNumber(
				new ObserverFilesNumber() {
					public void update(int value) {
						facade.getDownloadPanel()
								.getProgressBarCheckForAddons().setValue(value);
					}
				});

		try {
			FtpService ftpService = new FtpService();
			ftpService.checkRepository(repositoryName);// Update all repository
														// infos
														// (sync,serverInfos,changelogs,
														// events)
			AddonService addonService = new AddonService();
			addonService.resetAvailableAddonTree();
			addonService.getAvailableAddonsTree();
			parent = repositoryService.getSync(repositoryName);
			if (eventName != null) {
				setEventAddonSelection();
			}
			facade.getDownloadPanel().updateAddons(parent);
			facade.getDownloadPanel().getLabelCheckForAddonsStatus()
					.setText("Finished!");
		} catch (Exception e) {
			e.printStackTrace();
			String message = "";
			if (e.getMessage().isEmpty()) {
				message = "An unexpected error has occured. \n Try to close and relaunch ArmA3Sync.";
			} else {
				message = e.getMessage();
			}
			JOptionPane.showMessageDialog(facade.getMainPanel(), message,
					"Error", JOptionPane.ERROR_MESSAGE);
		} finally {
			facade.getDownloadPanel().getButtonCheckForAddonsStart()
					.setEnabled(true);
			facade.getDownloadPanel().getButtonCheckForAddonsCancel()
					.setEnabled(true);
			facade.getDownloadPanel().getButtonDownloadStart().setEnabled(true);
			facade.getDownloadPanel().getProgressBarCheckForAddons()
					.setMaximum(0);
			facade.getDownloadPanel().getArbre().setEnabled(true);
			facade.getAdminPanel().init(repositoryName);
			facade.getSyncPanel().init();
			System.gc();// Required for unlocking files!
		}
	}

	private void setEventAddonSelection() {

		try {
			List<EventDTO> eventDTOs = repositoryService
					.getEvents(this.repositoryName);
			List<String> addonNames = new ArrayList<String>();
			if (eventDTOs != null) {
				for (EventDTO eventDTO : eventDTOs) {
					if (eventDTO.getName().equals(eventName)) {
						addonNames = eventDTO.getAddonNames();
						break;
					}
				}
			}
			SyncTreeDirectoryDTO newRacine = new SyncTreeDirectoryDTO();
			newRacine.setName(parent.getName());
			newRacine.setParent(null);
			refine(parent, newRacine, addonNames);
			parent = newRacine;
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	private void refine(SyncTreeDirectoryDTO oldSyncTreeDirectoryDTO,
			SyncTreeDirectoryDTO newSyncTreeDirectoryDTO,
			List<String> addonNames) {

		for (SyncTreeNodeDTO nodeDTO : oldSyncTreeDirectoryDTO.getList()) {
			if (!nodeDTO.isLeaf()) {
				SyncTreeDirectoryDTO directoryDTO = (SyncTreeDirectoryDTO) nodeDTO;
				if (directoryDTO.isMarkAsAddon()
						&& addonNames.contains(nodeDTO.getName())) {
					SyncTreeDirectoryDTO newDirectory = new SyncTreeDirectoryDTO();
					newDirectory.setName(directoryDTO.getName());
					newDirectory.setParent(newSyncTreeDirectoryDTO);
					newDirectory.setMarkAsAddon(true);
					newSyncTreeDirectoryDTO.addTreeNode(newDirectory);
					fill(directoryDTO, newDirectory);
				} else {
					refine(directoryDTO, newSyncTreeDirectoryDTO, addonNames);
				}
			}
		}
	}

	private void fill(SyncTreeDirectoryDTO directoryDTO,
			SyncTreeDirectoryDTO newDirectoryDTO) {

		for (SyncTreeNodeDTO nodeDTO : directoryDTO.getList()) {
			if (nodeDTO.isLeaf()) {
				SyncTreeLeafDTO leafDTO = (SyncTreeLeafDTO) nodeDTO;
				SyncTreeLeafDTO newLeafDTO = new SyncTreeLeafDTO();
				newLeafDTO.setName(leafDTO.getName());
				newLeafDTO.setParent(newDirectoryDTO);
				newLeafDTO.setDeleted(leafDTO.isDeleted());
				newLeafDTO.setUpdated(leafDTO.isUpdated());
				newLeafDTO.setSize(leafDTO.getSize());
				newLeafDTO.setDestinationPath(leafDTO.getDestinationPath());
				newDirectoryDTO.addTreeNode(newLeafDTO);
			} else {
				SyncTreeDirectoryDTO dDTO = (SyncTreeDirectoryDTO)nodeDTO;
				SyncTreeDirectoryDTO newdDTO = new SyncTreeDirectoryDTO();
				newdDTO.setName(dDTO.getName());
				newdDTO.setParent(newDirectoryDTO);
				newdDTO.setUpdated(dDTO.isUpdated());
				newdDTO.setDeleted(dDTO.isDeleted());
				newdDTO.setDestinationPath(dDTO.getDestinationPath());
				newdDTO.setMarkAsAddon(dDTO.isMarkAsAddon());
				newDirectoryDTO.addTreeNode(newdDTO);
				fill(dDTO,newdDTO);
			}
		}
	}
}
