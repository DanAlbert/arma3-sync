package fr.soe.a3s.ui.repositoryEditor.workers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import fr.soe.a3s.controller.ObserverCount;
import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.exception.remote.RemoteRepositoryException;
import fr.soe.a3s.exception.remote.RemoteServerInfoFileNotFoundException;
import fr.soe.a3s.exception.remote.RemoteSyncFileNotFoundException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.AddonService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.service.connection.ConnexionService;
import fr.soe.a3s.service.connection.ConnexionServiceFactory;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repositoryEditor.DownloadPanel;
import fr.soe.a3s.ui.repositoryEditor.errorDialogs.HeaderErrorDialog;
import fr.soe.a3s.ui.repositoryEditor.errorDialogs.UnexpectedErrorDialog;
import fr.soe.a3s.ui.repositoryEditor.progressDialogs.ProgressModsetSelectionPanel;

public class AddonsChecker extends Thread {

	private final Facade facade;
	private final DownloadPanel downloadPanel;
	/* Data */
	private final String repositoryName;
	private final String eventName;
	private SyncTreeDirectoryDTO parent;
	/* Tests */
	private boolean found = false;
	private boolean canceled = false;
	private boolean showPartialFileTransferWarningMessage = false;
	/* Services */
	private final RepositoryService repositoryService = new RepositoryService();
	private final AddonService addonService = new AddonService();
	private ConnexionService connexionService;

	public AddonsChecker(Facade facade, String repositoryName,
			String eventName, boolean showPartialFileTransferWarningMessage,
			DownloadPanel downloadPanel) {
		this.facade = facade;
		this.repositoryName = repositoryName;
		this.eventName = eventName;
		this.showPartialFileTransferWarningMessage = showPartialFileTransferWarningMessage;
		this.downloadPanel = downloadPanel;
	}

	@Override
	public void run() {

		System.out.println("Starting Checking for Addons on repository: "
				+ repositoryName);

		// Initialize download panel for start checking
		initDownloadPanelForStartCheck();

		// Set checking for addons state
		repositoryService.setCheckingForAddons(repositoryName, true);

		downloadPanel.getProgressBarCheckForAddons().setIndeterminate(true);

		try {
			connexionService = ConnexionServiceFactory
					.getServiceForRepositoryManagement(repositoryName);
		} catch (RepositoryException | CheckException e) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Download", JOptionPane.ERROR_MESSAGE);
			initDownlaodPanelForEndCheck();
			terminate();
			return;
		}

		try {
			// 1. Try to retrieve the remote repository sync file and serverInfo
			// (required for determining file completion with zsync)
			connexionService.getSync(repositoryName);
			connexionService.getServerInfo(repositoryName);

			if (repositoryService.getSync(repositoryName) == null) {
				throw new RemoteSyncFileNotFoundException();
			}

			if (repositoryService.getServerInfo(repositoryName) == null) {
				throw new RemoteServerInfoFileNotFoundException();
			}

			// 2. Compare remote and local files SHA1
			repositoryService.getRepositorySHA1Processor().addObserverCount(
					new ObserverCount() {
						@Override
						public synchronized void update(final int value) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									downloadPanel
											.getProgressBarCheckForAddons()
											.setIndeterminate(false);
									downloadPanel
											.getProgressBarCheckForAddons()
											.setValue(value);
								}
							});
						}
					});

			parent = repositoryService.checkForAddons(repositoryName);

			downloadPanel.getProgressBarCheckForAddons()
					.setIndeterminate(false);

			// 3. Determine file completion, slower with http/zsync!
			downloadPanel.getProgressBarCheckForAddons().setMinimum(0);
			downloadPanel.getProgressBarCheckForAddons().setMaximum(100);

			connexionService.getConnexionDAO().addObserverCount(
					new ObserverCount() {
						@Override
						public void update(final int value) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									downloadPanel
											.getProgressBarCheckForAddons()
											.setValue(value);
								}
							});
						}
					});

			String header = connexionService.determineFilesCompletion(
					repositoryName, parent);
			
			// 4. Update online panel and launch panel
			facade.getOnlinePanel().init();
			facade.getLaunchPanel().init();

			if (!canceled) {
				// 5. Update download panel tree
				if (eventName != null) {
					extractAddonSelectionForEventName();
				}
				downloadPanel.updateAddons(parent);

				// 6. Display messages
				downloadPanel.getLabelCheckForAddonsStatus().setText(
						"Finished!");

				if (header != null && showPartialFileTransferWarningMessage) {
					HeaderErrorDialog dialog = new HeaderErrorDialog(facade,
							"Check for Addons", header, repositoryName);
					dialog.show();
					downloadPanel
							.setShowPartialFileTransferWarningMessage(false);
				}

				// 7. Save SHA1 computation on disk
				repositoryService.write(repositoryName);
			}
		} catch (Exception e) {
			downloadPanel.getProgressBarCheckForAddons()
					.setIndeterminate(false);
			if (e instanceof RepositoryException
					|| e instanceof WritingException) {
				JOptionPane.showMessageDialog(facade.getMainPanel(),
						e.getMessage(), "Check for Addons",
						JOptionPane.ERROR_MESSAGE);
			} else if (!canceled) {
				e.printStackTrace();
				if (e instanceof RemoteRepositoryException
						|| e instanceof IOException) {
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							e.getMessage(), "Check for Addons",
							JOptionPane.ERROR_MESSAGE);
				} else {
					UnexpectedErrorDialog dialog = new UnexpectedErrorDialog(
							facade, "Check for Addons", e, repositoryName);
					dialog.show();
				}
				downloadPanel.updateAddons(null);
			}
		} finally {
			if (connexionService != null) {
				connexionService.cancel();
			}
			initDownlaodPanelForEndCheck();
			terminate();
		}
	}

	private void initDownloadPanelForStartCheck() {

		downloadPanel.getLabelCheckForAddonsStatus().setText(
				"Checking files...");
		downloadPanel.getButtonCheckForAddonsStart().setEnabled(false);
		downloadPanel.getComBoxDestinationFolder().setEnabled(false);
		downloadPanel.getButtonAdvancedConfiguration().setEnabled(false);
		downloadPanel.getButtonDownloadStart().setEnabled(false);
		downloadPanel.getButtonDownloadPause().setEnabled(false);
		downloadPanel.getButtonDownloadCancel().setEnabled(false);
		downloadPanel.getButtonDownloadReport().setEnabled(false);
		downloadPanel.getProgressBarCheckForAddons().setMinimum(0);
		downloadPanel.getProgressBarCheckForAddons().setMaximum(100);
	}

	private void initDownlaodPanelForEndCheck() {

		downloadPanel.getProgressBarCheckForAddons().setIndeterminate(false);
		downloadPanel.getComBoxDestinationFolder().setEnabled(true);
		downloadPanel.getButtonCheckForAddonsStart().setEnabled(true);
		downloadPanel.getButtonAdvancedConfiguration().setEnabled(true);
		downloadPanel.getButtonDownloadStart().setEnabled(true);
		downloadPanel.getButtonDownloadPause().setEnabled(true);
		downloadPanel.getButtonDownloadCancel().setEnabled(true);
		downloadPanel.getButtonDownloadReport().setEnabled(true);
		downloadPanel.getProgressBarCheckForAddons().setMaximum(0);
		downloadPanel.getArbre().setEnabled(true);
	}

	private void terminate() {

		repositoryService.setCheckingForAddons(repositoryName, false);
		this.interrupt();
		System.gc();
	}

	public void cancel() {

		System.out.println("Canceling Checking for Addons on repository: "
				+ repositoryName);

		this.canceled = true;
		if (repositoryService != null) {
			repositoryService.cancel();
		}
		if (connexionService != null) {
			connexionService.cancel();
		}

		downloadPanel.updateAddons(null);
		downloadPanel.getLabelCheckForAddonsStatus().setText("Canceled!");
		initDownlaodPanelForEndCheck();
		terminate();
	}

	private void extractAddonSelectionForEventName() throws RepositoryException {

		List<EventDTO> eventDTOs = repositoryService
				.getEvents(this.repositoryName);
		Map<String, Boolean> addonNames = new HashMap<String, Boolean>();
		Map<String, Boolean> userconfigFolderNames = new HashMap<String, Boolean>();
		if (eventDTOs != null) {
			for (EventDTO eventDTO : eventDTOs) {
				if (eventDTO.getName().equals(eventName)) {
					addonNames = eventDTO.getAddonNames();
					userconfigFolderNames = eventDTO.getUserconfigFolderNames();
					break;
				}
			}
		}

		SyncTreeDirectoryDTO newRacine = new SyncTreeDirectoryDTO();
		newRacine.setName(parent.getName());
		newRacine.setParent(null);
		if (!userconfigFolderNames.isEmpty()) {
			refineUserconfig(parent, newRacine, userconfigFolderNames);
		}
		if (!addonNames.isEmpty()) {
			refineAddons(parent, newRacine, addonNames);
		}
		parent = newRacine;
	}

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
					newDirectory.setOptional(optional);
					newSyncTreeDirectoryDTO.setHidden(directoryDTO.isHidden());
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
				if (newLeafDTO.isUpdated() || newLeafDTO.isDeleted()) {
					SyncTreeDirectoryDTO parent = newLeafDTO.getParent();
					while (parent != null) {
						parent.setChanged(true);
						parent = parent.getParent();
					}
				}
			} else {
				SyncTreeDirectoryDTO dDTO = (SyncTreeDirectoryDTO) nodeDTO;
				SyncTreeDirectoryDTO newdDTO = new SyncTreeDirectoryDTO();
				newdDTO.setName(dDTO.getName());
				newdDTO.setParent(newDirectoryDTO);
				newdDTO.setUpdated(dDTO.isUpdated());
				newdDTO.setDeleted(dDTO.isDeleted());
				newdDTO.setChanged(dDTO.isChanged());
				newdDTO.setSelected(newDirectoryDTO.isSelected());
				newdDTO.setDestinationPath(dDTO.getDestinationPath());
				newdDTO.setMarkAsAddon(dDTO.isMarkAsAddon());
				newdDTO.setHidden(dDTO.isHidden());
				newDirectoryDTO.addTreeNode(newdDTO);
				if (newdDTO.isUpdated() || newdDTO.isDeleted()
						|| newdDTO.isChanged()) {
					SyncTreeDirectoryDTO parent = newdDTO.getParent();
					while (parent != null) {
						parent.setChanged(true);
						parent = parent.getParent();
					}
				}
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
				SyncTreeDirectoryDTO newUserconfig = new SyncTreeDirectoryDTO();
				newUserconfig.setName(userconfig.getName());
				newUserconfig.setDestinationPath(userconfig
						.getDestinationPath());
				newUserconfig.setParent(newSyncTreeDirectoryDTO);
				newUserconfig.setHidden(userconfig.isHidden());
				newSyncTreeDirectoryDTO.addTreeNode(newUserconfig);

				for (SyncTreeNodeDTO d : userconfig.getList()) {
					if (userconfigFolderNames.containsKey(d.getName())) {
						if (!d.isLeaf()) {
							SyncTreeDirectoryDTO folder = new SyncTreeDirectoryDTO();
							folder.setName(d.getName());
							folder.setDestinationPath(d.getDestinationPath());
							folder.setParent(newUserconfig);
							boolean optional = userconfigFolderNames.get(d
									.getName());
							folder.setOptional(optional);
							newUserconfig.addTreeNode(folder);
							folder.setHidden(((SyncTreeDirectoryDTO) d)
									.isHidden());
							folder.setUpdated(d.isUpdated());
							folder.setDeleted(d.isDeleted());
							folder.setChanged(((SyncTreeDirectoryDTO) d)
									.isChanged());
							if (folder.isUpdated() || folder.isDeleted()
									|| folder.isChanged()) {
								newUserconfig.setChanged(true);
							}
							fill((SyncTreeDirectoryDTO) d, folder);
						} else {
							SyncTreeLeafDTO leaf = new SyncTreeLeafDTO();
							leaf.setName(d.getName());
							leaf.setDestinationPath(d.getDestinationPath());
							leaf.setParent(newSyncTreeDirectoryDTO);
							leaf.setDeleted(d.isDeleted());
							leaf.setUpdated(d.isUpdated());
							leaf.setSelected(d.isSelected());
							leaf.setSize(((SyncTreeLeafDTO) d).getSize());
							boolean optional = userconfigFolderNames.get(d
									.getName());
							leaf.setOptional(optional);
							newUserconfig.addTreeNode(leaf);
							if (leaf.isUpdated() || leaf.isDeleted()) {
								newUserconfig.setChanged(true);
							}
						}
					}
				}
			}
		}
	}
}
