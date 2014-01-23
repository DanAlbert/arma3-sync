package fr.soe.a3s.ui.repositoryEditor.workers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import fr.soe.a3s.controller.ObserverFilesNumber;
import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.service.AbstractConnexionService;
import fr.soe.a3s.service.AddonService;
import fr.soe.a3s.service.ConnexionServiceFactory;
import fr.soe.a3s.service.FtpService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repositoryEditor.DownloadPanel;

public class AddonsChecker extends Thread {

	private Facade facade;
	private String repositoryName;
	private String eventName;
	private SyncTreeDirectoryDTO parent;
	private boolean found;
	private boolean update;
	private DownloadPanel downloadPanel;
	/* Services */
	private RepositoryService repositoryService = new RepositoryService();

	public AddonsChecker(Facade facade, String repositoryName,
			String eventName, boolean update, DownloadPanel downloadPanel) {
		this.facade = facade;
		this.repositoryName = repositoryName;
		this.eventName = eventName;
		this.update = update;
		this.downloadPanel = downloadPanel;
	}

	public void run() {

		downloadPanel.getButtonCheckForAddonsCancel().setEnabled(true);
		downloadPanel.getLabelCheckForAddonsStatus().setText("");
		downloadPanel.getButtonCheckForAddonsStart().setEnabled(false);
		downloadPanel.getButtonDownloadStart().setEnabled(false);
		downloadPanel.getProgressBarCheckForAddons().setMinimum(0);
		downloadPanel.getProgressBarCheckForAddons().setMaximum(100);
		RepositoryService repositoryService = new RepositoryService();
		repositoryService.getRepositoryBuilderDAO().addObserverFilesNumber(
				new ObserverFilesNumber() {
					public synchronized void update(int value) {
						downloadPanel.getProgressBarCheckForAddons().setValue(
								value);
					}
				});

		try {
			AbstractConnexionService connexionService = ConnexionServiceFactory
					.getServiceFromRepository(repositoryName);
			connexionService.getSync(repositoryName);
			AddonService addonService = new AddonService();
			addonService.resetAvailableAddonTree();
			addonService.getAvailableAddonsTree();
			parent = repositoryService.getSync(repositoryName);
			//connexionService .determineCompletion(repositoryName,parent);// slow with zsync!
			
			if (eventName != null) {
				setEventAddonSelection();
			} else if (update) {
				selectAllDescending(parent);
			}
			downloadPanel.updateAddons(parent);
			downloadPanel.getLabelCheckForAddonsStatus().setText("Finished!");
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
			downloadPanel.getButtonCheckForAddonsStart().setEnabled(true);
			downloadPanel.getButtonCheckForAddonsCancel().setEnabled(true);
			downloadPanel.getButtonDownloadStart().setEnabled(true);
			downloadPanel.getProgressBarCheckForAddons().setMaximum(0);
			downloadPanel.getArbre().setEnabled(true);
			facade.getAdminPanel().init(repositoryName);
			facade.getSyncPanel().init();
			this.interrupt();
			System.gc();
		}
	}

	private void setEventAddonSelection() {

		try {
			List<EventDTO> eventDTOs = repositoryService
					.getEvents(this.repositoryName);
			Map<String, Boolean> addonNames = new HashMap<String, Boolean>();
			Map<String, Boolean> userconfigFolderNames = new HashMap<String, Boolean>();
			if (eventDTOs != null) {
				for (EventDTO eventDTO : eventDTOs) {
					if (eventDTO.getName().equals(eventName)) {
						addonNames = eventDTO.getAddonNames();
						userconfigFolderNames = eventDTO
								.getUserconfigFolderNames();
						break;
					}
				}
			}

			SyncTreeDirectoryDTO newRacine = new SyncTreeDirectoryDTO();
			newRacine.setName(parent.getName());
			newRacine.setParent(null);
			refineUserconfig(parent, newRacine, userconfigFolderNames);
			refineAddons(parent, newRacine, addonNames);
			parent = newRacine;
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	// private void retrieve(SyncTreeDirectoryDTO syncTreeDirectoryDTO,
	// List<SyncTreeDirectoryDTO> retrievedList,
	// Map<String, Boolean> addonNames) {
	//
	// for (SyncTreeNodeDTO nodeDTO : syncTreeDirectoryDTO.getList()) {
	// if (!nodeDTO.isLeaf()) {
	// SyncTreeDirectoryDTO directoryDTO = (SyncTreeDirectoryDTO) nodeDTO;
	// if (directoryDTO.isMarkAsAddon()
	// && addonNames.containsKey(nodeDTO.getName())) {
	// boolean optional = addonNames.get(nodeDTO.getName());
	// if (optional) {
	// directoryDTO.setSelected(false);
	// directoryDTO.setOptional(true);
	// } else {
	// directoryDTO.setSelected(true);
	// directoryDTO.setOptional(false);
	// }
	// retrievedList.add(directoryDTO);
	// } else {
	// retrieve(directoryDTO, retrievedList, addonNames);
	// }
	// }
	// }
	// }

	// private void build(SyncTreeDirectoryDTO syncTreeDirectoryDTO,
	// SyncTreeDirectoryDTO newSyncTreeDirectoryDTO,
	// List<SyncTreeDirectoryDTO> retrievedList) {
	//
	// for (SyncTreeNodeDTO nodeDTO : retrievedList) {
	// if (nodeDTO.getParent().getName().contains("racine")) {
	// newSyncTreeDirectoryDTO.addTreeNode(nodeDTO);
	// nodeDTO.setParent(newSyncTreeDirectoryDTO);
	// } else {
	// SyncTreeDirectoryDTO newParent = new SyncTreeDirectoryDTO();
	// newParent.setName(nodeDTO.getParent().getName());
	// SyncTreeDirectoryDTO p = nodeDTO.getParent().getParent();
	// SyncTreeDirectoryDTO previousParent = newParent;
	// while (!p.getName().contains("racine")) {
	// SyncTreeDirectoryDTO newP = new SyncTreeDirectoryDTO();
	// newP.setName(p.getName());
	// previousParent.setParent(newP);
	// newP.addTreeNode(previousParent);
	// previousParent = newP;
	// p = p.getParent();
	// }
	// newSyncTreeDirectoryDTO.addTreeNode(previousParent);
	// newParent.addTreeNode(nodeDTO);
	// nodeDTO.setParent(newParent);
	// }
	// }
	// }

	private void refineAddons(SyncTreeDirectoryDTO oldSyncTreeDirectoryDTO,
			SyncTreeDirectoryDTO newSyncTreeDirectoryDTO,
			Map<String, Boolean> addonNames) {

		for (SyncTreeNodeDTO nodeDTO : oldSyncTreeDirectoryDTO.getList()) {
			if (!nodeDTO.isLeaf()) {
				SyncTreeDirectoryDTO directoryDTO = (SyncTreeDirectoryDTO) nodeDTO;
				if (directoryDTO.isMarkAsAddon()
						&& addonNames.containsKey(nodeDTO.getName())) {
					SyncTreeDirectoryDTO newDirectory = new SyncTreeDirectoryDTO();
					newDirectory.setName(directoryDTO.getName());
					newDirectory.setDestinationPath(directoryDTO
							.getDestinationPath());
					newDirectory.setParent(newSyncTreeDirectoryDTO);
					newDirectory.setMarkAsAddon(true);
					boolean optional = addonNames.get(nodeDTO.getName());
					if (optional) {
						newDirectory.setOptional(true);
						newDirectory.setSelected(false);
					} else {
						newDirectory.setOptional(false);
						newDirectory.setSelected(true);
						selectAllAscending(newDirectory);
					}
					newSyncTreeDirectoryDTO.addTreeNode(newDirectory);
					fill(directoryDTO, newDirectory);
				} else if (!directoryDTO.isMarkAsAddon()) {
					found = false;
					seek(directoryDTO, addonNames);
					if (found) {
						SyncTreeDirectoryDTO newDirectory = new SyncTreeDirectoryDTO();
						newDirectory.setName(directoryDTO.getName());
						newSyncTreeDirectoryDTO.addTreeNode(newDirectory);
						newDirectory.setParent(newSyncTreeDirectoryDTO);
						refineAddons(directoryDTO, newDirectory, addonNames);
					}
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
				newLeafDTO.setSelected(newDirectoryDTO.isSelected());
				newLeafDTO.setSize(leafDTO.getSize());
				newLeafDTO.setDestinationPath(leafDTO.getDestinationPath());
				newDirectoryDTO.addTreeNode(newLeafDTO);
			} else {
				SyncTreeDirectoryDTO dDTO = (SyncTreeDirectoryDTO) nodeDTO;
				SyncTreeDirectoryDTO newdDTO = new SyncTreeDirectoryDTO();
				newdDTO.setName(dDTO.getName());
				newdDTO.setParent(newDirectoryDTO);
				newdDTO.setUpdated(dDTO.isUpdated());
				newdDTO.setDeleted(dDTO.isDeleted());
				newdDTO.setSelected(newDirectoryDTO.isSelected());
				newdDTO.setDestinationPath(dDTO.getDestinationPath());
				newdDTO.setMarkAsAddon(dDTO.isMarkAsAddon());
				newDirectoryDTO.addTreeNode(newdDTO);
				fill(dDTO, newdDTO);
			}
		}
	}

	private void seek(SyncTreeDirectoryDTO seakDirectory,
			Map<String, Boolean> addonNames) {

		for (SyncTreeNodeDTO nodeDTO : seakDirectory.getList()) {
			if (!nodeDTO.isLeaf()) {
				SyncTreeDirectoryDTO directoryDTO = (SyncTreeDirectoryDTO) nodeDTO;
				if (directoryDTO.isMarkAsAddon()
						&& addonNames.containsKey(nodeDTO.getName())) {
					found = true;
				} else {
					seek(directoryDTO, addonNames);
				}
			}
		}
	}

	private void refineUserconfig(SyncTreeDirectoryDTO oldSyncTreeDirectoryDTO,
			SyncTreeDirectoryDTO newSyncTreeDirectoryDTO,
			Map<String, Boolean> userconfigFolderNames) {

		for (SyncTreeNodeDTO nodeDTO : oldSyncTreeDirectoryDTO.getList()) {
			if (!nodeDTO.isLeaf()
					&& nodeDTO.getName().toLowerCase().equals("userconfig")) {
				SyncTreeDirectoryDTO userconfig = (SyncTreeDirectoryDTO) nodeDTO;
				SyncTreeDirectoryDTO newDirectory = new SyncTreeDirectoryDTO();
				newDirectory.setName(userconfig.getName());
				newDirectory
						.setDestinationPath(userconfig.getDestinationPath());
				newDirectory.setParent(newSyncTreeDirectoryDTO);
				newSyncTreeDirectoryDTO.addTreeNode(newDirectory);

				for (SyncTreeNodeDTO d : userconfig.getList()) {
					if (userconfigFolderNames.containsKey(d.getName())) {
						SyncTreeDirectoryDTO folder = new SyncTreeDirectoryDTO();
						folder.setName(d.getName());
						folder.setDestinationPath(d.getDestinationPath());
						folder.setParent(newSyncTreeDirectoryDTO);
						boolean optional = userconfigFolderNames.get(d
								.getName());
						if (optional) {
							folder.setOptional(true);
							folder.setSelected(false);
						} else {
							folder.setOptional(false);
							folder.setSelected(true);
							selectAllAscending(newDirectory);
						}
						newDirectory.addTreeNode(folder);
						fill((SyncTreeDirectoryDTO) d, folder);
					}
				}
			}
		}
	}

	private void selectAllAscending(SyncTreeNodeDTO syncTreeNodeDTO) {
		if (syncTreeNodeDTO != null) {
			syncTreeNodeDTO.setSelected(true);
			SyncTreeNodeDTO parent = syncTreeNodeDTO.getParent();
			selectAllAscending(parent);
		}
	}

	private void selectAllDescending(SyncTreeNodeDTO syncTreeNodeDTO) {
		syncTreeNodeDTO.setSelected(true);
		if (!syncTreeNodeDTO.isLeaf()) {
			SyncTreeDirectoryDTO syncTreeDirectoryDTO = (SyncTreeDirectoryDTO) syncTreeNodeDTO;
			for (SyncTreeNodeDTO t : syncTreeDirectoryDTO.getList()) {
				selectAllDescending(t);
			}
		}
	}
}
