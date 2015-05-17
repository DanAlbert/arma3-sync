package fr.soe.a3s.ui.repositoryEditor.workers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import fr.soe.a3s.constant.Protocol;
import fr.soe.a3s.controller.ObserverFileSize2;
import fr.soe.a3s.controller.ObserverFilesNumber2;
import fr.soe.a3s.controller.ObserverSpeed;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dto.ProtocolDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.service.AbstractConnexionService;
import fr.soe.a3s.service.ConnexionServiceFactory;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repositoryEditor.AdminPanel;
import fr.soe.a3s.ui.repositoryEditor.UnitConverter;

public class RepositoryUploader extends Thread implements DataAccessConstants {

	private final Facade facade;
	private final AdminPanel adminPanel;

	/* Service */
	private final RepositoryService repositoryService = new RepositoryService();
	private AbstractConnexionService connexionService;

	/* Data */
	private final String repositoryName;
	private final String path;
	private final List<SyncTreeNodeDTO> filesToUpload = new ArrayList<SyncTreeNodeDTO>();
	private final List<SyncTreeNodeDTO> filesToDelete = new ArrayList<SyncTreeNodeDTO>();
	private long incrementedFilesSize = 0;
	private final long offset = 0;
	private int lastIndexFileUploaded = 0;
	private long totalFilesSize = 0;
	private long cumulative = 0;
	private boolean canceled = false;

	public RepositoryUploader(Facade facade, String repositoryName,
			String path, AdminPanel adminPanel) {
		this.facade = facade;
		this.repositoryName = repositoryName;
		this.path = path;
		this.adminPanel = adminPanel;
	}

	@Override
	public void run() {

		disable();
		adminPanel.getUploadrogressBar().setIndeterminate(true);
		adminPanel.getUploadrogressBar().setString("Checking remote files...");

		repositoryService.setUploading(repositoryName, true);

		try {
			// 1. Check repository upload protocole
			RepositoryDTO repositoryDTO = repositoryService
					.getRepository(repositoryName);
			ProtocolDTO protocoleDTO = repositoryDTO.getProtocoleDTO();
			Protocol protocole = protocoleDTO.getProtocole();
			ProtocolDTO uploadProtocoleDTO = repositoryDTO
					.getRepositoryUploadProtocoleDTO();
			if (uploadProtocoleDTO == null
					&& protocoleDTO.getProtocole().equals(Protocol.FTP)) {
				repositoryService.setRepositoryUploadProtocole(repositoryName,
						protocoleDTO.getUrl(), protocoleDTO.getPort(),
						protocoleDTO.getLogin(), protocoleDTO.getPassword(),
						protocole);
			} else if (uploadProtocoleDTO == null) {
				String message = "Please use the upload options to configure a connection.";
				throw new CheckException(message);
			}

			// 2. Read local sync, autoconfig, serverInfo, changelogs
			repositoryService.readLocalRepository(repositoryName);

			// 3. Determine files to upload and remote files to delete
			connexionService = ConnexionServiceFactory
					.getRepositoryUploadServiceFromRepository(repositoryName);
			connexionService
					.getSyncWithRepositoryUploadProtocole(repositoryName);

			SyncTreeDirectoryDTO remoteSync = repositoryService
					.getSync(repositoryName);
			SyncTreeDirectoryDTO localSync = repositoryService
					.getLocalSync(repositoryName);

			Map<String, SyncTreeNodeDTO> mapLocalSync = new HashMap<String, SyncTreeNodeDTO>();
			syncToMap(localSync, mapLocalSync);

			if (remoteSync == null) {
				for (Iterator<String> iter = mapLocalSync.keySet().iterator(); iter
						.hasNext();) {
					String path = iter.next();
					filesToUpload.add(mapLocalSync.get(path));
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
						if (localNode.isLeaf() && remoteNode.isLeaf()) {
							boolean exists = false;
							if (!canceled) {
								exists = connexionService.remoteFileExists(
										repositoryName, remoteNode);
							}
							if (exists) {
								SyncTreeLeafDTO localLeaf = (SyncTreeLeafDTO) localNode;
								SyncTreeLeafDTO remoteLeaf = (SyncTreeLeafDTO) remoteNode;
								if (localLeaf.getSha1().equals(
										remoteLeaf.getSha1())) {
									upload = false;
								}
							}
						} else if (!localNode.isLeaf() && !remoteNode.isLeaf()) {
							upload = false;
						}
					}

					if (upload) {
						filesToUpload.add(mapLocalSync.get(path));
						filesToDelete.add(mapLocalSync.get(path));
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
		} catch (Exception e) {
			enable();
			adminPanel.getUploadrogressBar().setIndeterminate(false);
			repositoryService.setUploading(repositoryName, false);
			if (e instanceof CheckException) {
				JOptionPane.showMessageDialog(facade.getMainPanel(),
						e.getMessage(), "Information",
						JOptionPane.INFORMATION_MESSAGE);
			} else if (!canceled) {
				JOptionPane.showMessageDialog(facade.getMainPanel(),
						e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
			return;
		}

		try {
			// 4. Determine total file size
			for (SyncTreeNodeDTO node : filesToUpload) {
				if (node.isLeaf()) {
					SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
					totalFilesSize = totalFilesSize + leaf.getSize();
					System.out.println("leaf size =" + leaf.getSize());
				}
			}

			// 5. Upload
			adminPanel.getUploadrogressBar().setMaximum(100);
			adminPanel.getUploadrogressBar().setString("Uploading files...");
			adminPanel.getUploadrogressBar().setIndeterminate(false);

			// Resume Upload
			lastIndexFileUploaded = repositoryService
					.getLastIndexFileTransfered(repositoryName);
			incrementedFilesSize = repositoryService
					.getIncrementedFilesSize(repositoryName);
			boolean resume = repositoryService.isResume(repositoryName);

			for (int i = 0; i < lastIndexFileUploaded; i++) {
				SyncTreeNodeDTO node = filesToUpload.get(i);
				if (node.isLeaf()) {
					SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
					cumulative = cumulative + leaf.getSize();
				}
			}

			int nbFiles = filesToUpload.size();
			List<SyncTreeNodeDTO> newFilesToUpload = new ArrayList<SyncTreeNodeDTO>();
			for (int i = lastIndexFileUploaded; i < nbFiles; i++) {
				newFilesToUpload.add(filesToUpload.get(i));
			}

			adminPanel.getUploadSizeLabelValue().setText(
					UnitConverter.convertSize(totalFilesSize));
			adminPanel.getUploadedLabelValue().setText(
					UnitConverter.convertSize(incrementedFilesSize));
			adminPanel.getUploadedLabelValue().setText("0.0");
			adminPanel.getUploadSpeedLabelValue().setText("");
			adminPanel.getUploadRemainingTimeValue().setText("");
			adminPanel.getUploadInformationBox().setVisible(true);

			connexionService.getConnexionDAO().addObserverFilesNumber2(
					new ObserverFilesNumber2() {
						@Override
						public void update() {
							lastIndexFileUploaded++;
							SyncTreeNodeDTO node = filesToUpload
									.get(lastIndexFileUploaded - 1);
							if (node.isLeaf()) {
								SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
								long size = leaf.getSize();
								cumulative = cumulative + size;
							}
						}
					});
			connexionService.getConnexionDAO().addObserverFileSize2(
					new ObserverFileSize2() {
						@Override
						public synchronized void update(long value) {
							incrementedFilesSize = value + cumulative;
							adminPanel.getUploadedLabelValue().setText(
									UnitConverter
											.convertSize(incrementedFilesSize));
							if (totalFilesSize != 0) {
								adminPanel
										.getUploadrogressBar()
										.setValue(
												(int) (incrementedFilesSize * 100 / totalFilesSize));
							}
						}
					});
			connexionService.getConnexionDAO().addObserverSpeed(
					new ObserverSpeed() {
						@Override
						public void update() {
							long value = connexionService.getConnexionDAO()
									.getSpeed();
							if (value != 0) {// division by 0
								adminPanel.getUploadSpeedLabelValue().setText(
										UnitConverter.convertSpeed(value));
								long remainingFileSize = totalFilesSize
										- incrementedFilesSize;
								long time = remainingFileSize / value;
								adminPanel
										.getUploadRemainingTimeValue()
										.setText(
												UnitConverter.convertTime(time));
							}
						}
					});

			connexionService.uploadRepository(repositoryName, newFilesToUpload,
					filesToDelete, resume);

			if (!canceled) {
				JOptionPane.showMessageDialog(facade.getMainPanel(),
						"Repository upload finished.", "Repository upload",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		} finally {
			enable();
			adminPanel.getUploadInformationBox().setVisible(false);
			repositoryService.setUploading(repositoryName, false);
			System.gc();// Required for unlocking files!
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

	private void disable() {

		adminPanel.getButtonUpload().setText("Stop");
		adminPanel.getUploadrogressBar().setStringPainted(true);
		adminPanel.getUploadrogressBar().setMaximum(0);
		adminPanel.getUploadrogressBar().setMinimum(0);
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
	}

	private void enable() {

		adminPanel.getButtonUpload().setText("Upload");
		adminPanel.getUploadrogressBar().setStringPainted(false);
		adminPanel.getUploadrogressBar().setMaximum(0);
		adminPanel.getUploadrogressBar().setMinimum(0);
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
	}

	public void cancel() {

		this.canceled = true;
		adminPanel.getUploadrogressBar().setString("Canceling...");
		repositoryService.saveTransfertParameters(repositoryName,
				incrementedFilesSize, lastIndexFileUploaded, true);
		connexionService.cancel(true);
		repositoryService.setUploading(repositoryName, false);
	}
}
