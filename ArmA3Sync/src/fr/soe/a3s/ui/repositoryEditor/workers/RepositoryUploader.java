package fr.soe.a3s.ui.repositoryEditor.workers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.controller.ObserverCount;
import fr.soe.a3s.controller.ObserverText;
import fr.soe.a3s.controller.ObserverUpload;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dto.ProtocolDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.exception.repository.SyncFileNotFoundException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.service.connection.ConnexionService;
import fr.soe.a3s.service.connection.ConnexionServiceFactory;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UnitConverter;
import fr.soe.a3s.ui.repositoryEditor.AdminPanel;
import fr.soe.a3s.ui.repositoryEditor.errorDialogs.UnexpectedErrorDialog;

public class RepositoryUploader extends Thread implements DataAccessConstants {

	private final Facade facade;
	private final AdminPanel adminPanel;
	/* Data */
	private final String repositoryName;
	private final String repositoryPath;
	private final List<SyncTreeNodeDTO> filesToUpload = new ArrayList<SyncTreeNodeDTO>();
	private final List<SyncTreeNodeDTO> filesToCheck = new ArrayList<SyncTreeNodeDTO>();
	private final List<SyncTreeNodeDTO> filesToDelete = new ArrayList<SyncTreeNodeDTO>();
	private long incrementedFilesSize = 0;
	private int lastIndexFileUploaded = 0;
	private long totalFilesSize = 0;
	private long currentSize = 0;
	/* Tests */
	private boolean canceled = false;
	/* Service */
	private final RepositoryService repositoryService = new RepositoryService();
	private ConnexionService connexionService;

	public RepositoryUploader(Facade facade, String repositoryName,
			String repositoryPath, AdminPanel adminPanel) {
		this.facade = facade;
		this.repositoryName = repositoryName;
		this.repositoryPath = repositoryPath;
		this.adminPanel = adminPanel;
	}

	@Override
	public void run() {

		System.out.println("Starting uploading repository: " + repositoryName);

		// Init AdminPanel for start uploading
		intiAdminPanelForStartUpload();

		// Set Uploading state
		repositoryService.setUploading(repositoryName, true);

		adminPanel.getUploadrogressBar().setIndeterminate(true);

		try {
			// 1. Check repository upload protocol
			RepositoryDTO repositoryDTO = repositoryService
					.getRepository(repositoryName);
			ProtocolDTO protocoleDTO = repositoryDTO.getProtocoleDTO();
			ProtocolType protocolType = protocoleDTO.getProtocolType();
			ProtocolDTO uploadProtocoleDTO = repositoryDTO
					.getRepositoryUploadProtocoleDTO();
			if (uploadProtocoleDTO == null
					&& protocoleDTO.getProtocolType().equals(ProtocolType.FTP)) {
				repositoryService.setRepositoryUploadProtocole(repositoryName,
						protocoleDTO.getUrl(), protocoleDTO.getPort(),
						protocoleDTO.getLogin(), protocoleDTO.getPassword(),
						protocolType, protocoleDTO.getConnectionTimeOut(),
						protocoleDTO.getReadTimeOut());
			} else if (uploadProtocoleDTO == null) {
				String message = "Please use the upload options to configure a connection.";
				throw new CheckException(message);
			}

			// 2. Read local sync, autoconfig, serverInfo, changelogs
			repositoryService.readLocalyBuildedRepository(repositoryName);

			// 3. Determine files to check, upload and delete, throw
			connexionService = ConnexionServiceFactory
					.getServiceForRepositoryUpload(repositoryName);
			connexionService.getSyncWithUploadProtocole(repositoryName);

			SyncTreeDirectoryDTO remoteSync = repositoryService
					.getSync(repositoryName);// may be
												// null
			SyncTreeDirectoryDTO localSync = repositoryService
					.getLocalSync(repositoryName);

			if (localSync == null) {
				throw new SyncFileNotFoundException(repositoryName);
			}

			Map<String, SyncTreeNodeDTO> mapLocalSync = new HashMap<String, SyncTreeNodeDTO>();
			syncToMap(localSync, mapLocalSync);

			if (remoteSync == null) {
				for (Iterator<String> iter = mapLocalSync.keySet().iterator(); iter
						.hasNext();) {
					String path = iter.next();
					SyncTreeNodeDTO localNode = mapLocalSync.get(path);
					filesToUpload.add(localNode);
				}
			} else {
				Map<String, SyncTreeNodeDTO> mapRemoteSync = new HashMap<String, SyncTreeNodeDTO>();
				syncToMap(remoteSync, mapRemoteSync);

				for (Iterator<String> iter = mapLocalSync.keySet().iterator(); iter
						.hasNext();) {
					String path = iter.next();
					SyncTreeNodeDTO localNode = mapLocalSync.get(path);
					SyncTreeNodeDTO remoteNode = mapRemoteSync.get(path);

					boolean upload = true;
					if (remoteNode != null) {
						if (localNode.getName().equals(remoteNode.getName())) {
							if (localNode.isLeaf() && remoteNode.isLeaf()) {
								SyncTreeLeafDTO localLeaf = (SyncTreeLeafDTO) localNode;
								SyncTreeLeafDTO remoteLeaf = (SyncTreeLeafDTO) remoteNode;
								if (localLeaf.getSha1().equals(
										remoteLeaf.getSha1())) {
									upload = false;
								}
							} else if (!localNode.isLeaf()
									&& !remoteNode.isLeaf()) {// 2 same
																// directories
								upload = false;
							}
						}
					}

					if (upload) {
						filesToUpload.add(mapLocalSync.get(path));
					} else {
						filesToCheck.add(mapLocalSync.get(path));
					}
				}

				for (Iterator<String> iter = mapRemoteSync.keySet().iterator(); iter
						.hasNext();) {
					String path = iter.next();
					if (!mapLocalSync.containsKey(path)) {
						filesToDelete.add(mapRemoteSync.get(path));
					}
				}
			}

			// 5. Resume Upload
			lastIndexFileUploaded = repositoryService
					.getLastIndexFileTransfered(repositoryName);

			connexionService.getConnexionDAO().addObserverCount(
					new ObserverCount() {
						@Override
						public void update(final int value) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									adminPanel.getUploadrogressBar()
											.setIndeterminate(false);
									adminPanel.getUploadrogressBar().setValue(
											value);
								}
							});
						}
					});

			connexionService.getConnexionDAO().addObserverText(
					new ObserverText() {
						@Override
						public void update(final String text) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									adminPanel.getUploadrogressBar().setString(
											text);
								}
							});
						}
					});

			connexionService.getConnexionDAO().addObserverUpload(
					new ObserverUpload() {

						@Override
						public void updateTotalSize(long value) {
							totalFilesSize = value;
							adminPanel.getUploadSizeLabelValue().setText(
									UnitConverter.convertSize(totalFilesSize));
							adminPanel.getUploadedLabelValue().setText(
									UnitConverter
											.convertSize(incrementedFilesSize));
							adminPanel.getUploadedLabelValue().setText("0.0");
							adminPanel.getUploadSpeedLabelValue().setText("");
							adminPanel.getUploadRemainingTimeValue()
									.setText("");
							adminPanel.getUploadInformationBox().setVisible(
									true);
							adminPanel.getUploadrogressBar().setValue(0);
						}

						@Override
						public void updateTotalSizeProgress(long value) {
							incrementedFilesSize = incrementedFilesSize + value;
							adminPanel.getUploadedLabelValue().setText(
									UnitConverter
											.convertSize(incrementedFilesSize));
						}

						@Override
						public void updateSingleSizeProgress(
								final int pourcentage, final long value) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									adminPanel.getUploadrogressBar()
											.setIndeterminate(false);
									adminPanel.getUploadrogressBar().setValue(
											pourcentage);
									currentSize = value;
								}
							});
						}

						@Override
						public void updateSpeed() {
							long value = connexionService.getConnexionDAO()
									.getSpeed();
							if (value != 0) {// division by 0
								adminPanel.getUploadSpeedLabelValue().setText(
										UnitConverter.convertSpeed(value));
								long remainingFileSize = totalFilesSize
										- incrementedFilesSize - currentSize;
								long time = remainingFileSize / value;
								adminPanel
										.getUploadRemainingTimeValue()
										.setText(
												UnitConverter.convertTime(time));
							}
						}

						@Override
						public void updateLastIndexFileUploaded() {
							lastIndexFileUploaded++;
						}
					});

			connexionService.uploadRepository(repositoryName, filesToCheck,
					filesToUpload, filesToDelete, lastIndexFileUploaded);

			adminPanel.getUploadrogressBar().setIndeterminate(false);

			if (!canceled) {
				adminPanel.getUploadrogressBar().setString("100%");
				adminPanel.getUploadrogressBar().setValue(100);

				JOptionPane.showMessageDialog(facade.getMainPanel(),
						"Repository upload finished.", "Repository upload",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (Exception e) {
			adminPanel.getUploadrogressBar().setIndeterminate(false);
			if (!canceled) {
				e.printStackTrace();
				if (e instanceof RepositoryException
						|| e instanceof LoadingException) {
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							e.getMessage(), "Upload repository",
							JOptionPane.ERROR_MESSAGE);
				} else if (e instanceof CheckException) {
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							e.getMessage(), "Upload repository",
							JOptionPane.WARNING_MESSAGE);
				} else if (e instanceof IOException) {
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							e.getMessage(), "Upload repository",
							JOptionPane.ERROR_MESSAGE);
				} else {
					UnexpectedErrorDialog dialog = new UnexpectedErrorDialog(
							facade, "Upload repository", e, repositoryName);
					dialog.show();
				}
			}
		} finally {
			if (canceled) {
				repositoryService.saveTransfertParameters(repositoryName,
						incrementedFilesSize, lastIndexFileUploaded, true);
			} else {
				repositoryService.saveTransfertParameters(repositoryName, 0, 0,
						false);
			}
			if (connexionService != null) {
				connexionService.cancel();
			}
			initAdminPanelForEndUpload();
			terminate();
		}
	}

	private void syncToMap(SyncTreeNodeDTO node,
			Map<String, SyncTreeNodeDTO> map) {

		if (!node.getName().equals(SyncTreeNodeDTO.RACINE)) {
			map.put(node.getRelativePath(), node);
		}
		if (!node.isLeaf()) {
			SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) node;
			for (SyncTreeNodeDTO n : directory.getList()) {
				syncToMap(n, map);
			}
		}
	}

	private void intiAdminPanelForStartUpload() {

		adminPanel.getButtonSelectRepositoryfolderPath().setEnabled(false);
		adminPanel.getButtonBuild().setEnabled(false);
		adminPanel.getButtonCopyAutoConfigURL().setEnabled(false);
		adminPanel.getButtonCheck().setEnabled(false);
		adminPanel.getButtonBuildOptions().setEnabled(false);
		adminPanel.getButtonUploadOptions().setEnabled(false);
		adminPanel.getButtonView().setEnabled(false);
		adminPanel.getRepositoryPanel().getDownloadPanel()
				.getButtonCheckForAddonsStart().setEnabled(false);
		adminPanel.getRepositoryPanel().getDownloadPanel()
				.getButtonDownloadStart().setEnabled(false);
		adminPanel.getButtonUpload().setText("Stop");
		adminPanel.getUploadrogressBar().setString("");
		adminPanel.getUploadrogressBar().setStringPainted(true);
		adminPanel.getUploadrogressBar().setMaximum(100);
		adminPanel.getUploadrogressBar().setMinimum(0);
	}

	private void initAdminPanelForEndUpload() {

		adminPanel.getButtonSelectRepositoryfolderPath().setEnabled(true);
		adminPanel.getButtonBuild().setEnabled(true);
		adminPanel.getButtonCopyAutoConfigURL().setEnabled(true);
		adminPanel.getButtonCheck().setEnabled(true);
		adminPanel.getButtonBuildOptions().setEnabled(true);
		adminPanel.getButtonUploadOptions().setEnabled(true);
		adminPanel.getButtonView().setEnabled(true);
		adminPanel.getRepositoryPanel().getDownloadPanel()
				.getButtonCheckForAddonsStart().setEnabled(true);
		adminPanel.getRepositoryPanel().getDownloadPanel()
				.getButtonDownloadStart().setEnabled(true);
		adminPanel.getUploadInformationBox().setVisible(false);
		adminPanel.getButtonUpload().setText("Upload");
		adminPanel.getUploadrogressBar().setString("");
		adminPanel.getUploadrogressBar().setStringPainted(false);
		adminPanel.getUploadrogressBar().setMaximum(0);
		adminPanel.getUploadrogressBar().setMinimum(0);
	}

	private void terminate() {

		repositoryService.setUploading(repositoryName, false);
		this.interrupt();
		System.gc();
	}

	public void cancel() {

		this.canceled = true;
		adminPanel.getUploadrogressBar().setString("Canceling...");
		if (connexionService != null) {
			connexionService.cancel();
		}
		initAdminPanelForEndUpload();
		terminate();
	}
}
