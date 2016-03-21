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
import fr.soe.a3s.exception.remote.RemoteEventsFileNotFoundException;
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
			/*
			 * 1. Try to retrieve the remote repository: - sync file: required
			 * for SHA1 comparisons - serverinfo: file completion with zsync -
			 * events: synchronization against select eventName, may be null
			 */
			connexionService.checkRepository(repositoryName);

			if (repositoryService.getSync(repositoryName) == null) {
				throw new RemoteSyncFileNotFoundException();
			}

			if (repositoryService.getServerInfo(repositoryName) == null) {
				throw new RemoteServerInfoFileNotFoundException();
			}

			if (repositoryService.getEvents(repositoryName) == null
					&& eventName != null) {
				throw new RemoteEventsFileNotFoundException();
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

			// 4. Update repository status
			repositoryService.updateRepositoryRevision(repositoryName);
			
			// 5. Update online panel and launch panel
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					facade.getSyncPanel().init();
					facade.getOnlinePanel().init();
					facade.getLaunchPanel().init();
				}
			});
			
			if (!canceled) {
				
				// 6. Update modset selection
				facade.getAddonsPanel().updateModsetSelection(repositoryName);
				
				// 7. Update download panel tree
				if (eventName != null) {
					extractAddonSelectionForEventName();
				}
				downloadPanel.updateAddons(parent);

				// 8. Display messages
				downloadPanel.getLabelCheckForAddonsStatus().setText(
						"Finished!");

				if (header != null && showPartialFileTransferWarningMessage) {
					HeaderErrorDialog dialog = new HeaderErrorDialog(facade,
							"Check for Addons", header, repositoryName);
					dialog.show();
					downloadPanel
							.setShowPartialFileTransferWarningMessage(false);
				}

				// 9. Save SHA1 computation on disk
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

	private void refineAddons(SyncTreeDirectoryDTO oldRacine,
			SyncTreeDirectoryDTO newRacine, Map<String, Boolean> addonNames) {

		for (SyncTreeNodeDTO nodeDTO : oldRacine.getList()) {
			if (!nodeDTO.isLeaf()) {
				SyncTreeDirectoryDTO directoryDTO = (SyncTreeDirectoryDTO) nodeDTO;
				if (directoryDTO.isMarkAsAddon()
						&& addonNames.containsKey(nodeDTO.getName())) {
					newRacine.addTreeNode(directoryDTO);
					directoryDTO.setParent(newRacine);
					directoryDTO.setOptional(addonNames.get(nodeDTO.getName()));
				} else if (!directoryDTO.isMarkAsAddon()) {
					found = false;
					seek(directoryDTO, addonNames);
					if (found) {
						SyncTreeDirectoryDTO newDirectory = new SyncTreeDirectoryDTO();
						newDirectory.setName(directoryDTO.getName());
						newRacine.addTreeNode(newDirectory);
						newDirectory.setParent(newRacine);
						refineAddons(directoryDTO, newDirectory, addonNames);
					}
				}
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
					directoryDTO.setOptional(addonNames.get(nodeDTO
							.getName()));
				} else {
					seek(directoryDTO, addonNames);
				}
			}
		}
	}

	private void refineUserconfig(SyncTreeDirectoryDTO oldRacine,
			SyncTreeDirectoryDTO newRacine,
			Map<String, Boolean> userconfigFolderNames) {

		SyncTreeDirectoryDTO userconfigNode = null;

		for (SyncTreeNodeDTO nodeDTO : oldRacine.getList()) {
			if (!nodeDTO.isLeaf()
					&& nodeDTO.getName().toLowerCase().equals("userconfig")) {
				userconfigNode = (SyncTreeDirectoryDTO) nodeDTO;
				break;
			}
		}

		if (userconfigNode != null) {
			SyncTreeDirectoryDTO newUserconfigNode = new SyncTreeDirectoryDTO();
			newUserconfigNode.setName(userconfigNode.getName());
			newRacine.addTreeNode(newUserconfigNode);
			for (SyncTreeNodeDTO nodeDTO : userconfigNode.getList()) {
				if (!nodeDTO.isLeaf()
						&& userconfigFolderNames.containsKey(nodeDTO.getName())) {
					newUserconfigNode.addTreeNode(nodeDTO);
					nodeDTO.setOptional(userconfigFolderNames
							.get(nodeDTO.getName()));
				}
			}
		}
	}
}
