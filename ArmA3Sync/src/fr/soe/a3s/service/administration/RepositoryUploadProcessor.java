package fr.soe.a3s.service.administration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import fr.soe.a3s.controller.ObserverConnectionLost;
import fr.soe.a3s.controller.ObserverCountInt;
import fr.soe.a3s.controller.ObserverCountLong;
import fr.soe.a3s.controller.ObserverEnd;
import fr.soe.a3s.controller.ObserverError;
import fr.soe.a3s.controller.ObserverText;
import fr.soe.a3s.controller.ObserverUpload;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.repository.SyncFileNotFoundException;
import fr.soe.a3s.service.ConnectionService;
import fr.soe.a3s.service.RepositoryService;

public class RepositoryUploadProcessor {

	/* Data */
	private final String repositoryName;
	private int lastIndexFileUploaded;
	private long uploadedFilesSize, totalFilesSize, currentSize;
	private long startTime, deltaTimeSpeed;
	/* Services */
	private final RepositoryService repositoryService = new RepositoryService();
	private ConnectionService connexionService;
	private FilesUploadManager filesManager;
	/* Tests */
	private boolean canceled = false;
	/* observers */
	private ObserverText observerText;
	private ObserverCountInt observerCountProgress;
	private ObserverCountLong observerTotalSize, observerUploadedSize,
			observerSpeed, observerRemainingTime;
	private ObserverEnd observerEndUpload;
	private ObserverError observerError;
	private ObserverConnectionLost observerConnectionLost;

	public RepositoryUploadProcessor(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public void run() {

		try {
			// Set Uploading state
			repositoryService.setUploading(repositoryName, true);

			// 1. Check repository upload protocol
			AbstractProtocole uploadProtocol = repositoryService
					.getUploadProtocol(repositoryName);

			if (uploadProtocol == null) {
				String message = "Please use the upload options to configure a connection.";
				throw new CheckException(message);
			}

			connexionService = new ConnectionService(uploadProtocol);

			connexionService.getConnexionDAOs().get(0)
					.addObserverText(new ObserverText() {
						@Override
						public void update(String text) {
							executeUpdateText(text);
						}
					});

			connexionService.getConnexionDAOs().get(0)
					.addObserverCount(new ObserverCountInt() {
						@Override
						public void update(int value) {
							executeUpdateSingleSizeProgress(value);
						}
					});

			connexionService.getConnexionDAOs().get(0)
					.addObserverUpload(new ObserverUpload() {

						@Override
						public void updateTotalSize(long value) {
							executeUpdateTotalSize(value);
						}

						@Override
						public void updateSingleSizeProgress(int pourcentage,
								long value) {
							executeUpdateSingleSizeProgress(pourcentage);
							executeUpdateUploadedSize(value);
							executeUpdateRemaingTime();
						}

						@Override
						public void updateTotalSizeProgress(long value) {
							executeUpdateTotalSizeProgress(value);
						}

						@Override
						public void updateSpeed() {
							executeUpdateSpeed();
						}

						@Override
						public void updateLastIndexFileUploaded() {
							executeUpdateLastIndexUpdated();
						}

						@Override
						public void updateConnectionLost() {
							executeConnectionLost();
						}
					});

			// 2. Read local sync, autoconfig, serverInfo, changelogs
			repositoryService.readLocalyBuildedRepository(repositoryName);// IOException

			// 3. Determine files to check, upload and delete, throw
			try {
				connexionService.getSyncWithUploadProtocole(repositoryName);// IOException
			} catch (IOException e) {
				if (!(e instanceof FileNotFoundException)) {
					throw e;
				}
			}
			// SocketException
			SyncTreeDirectoryDTO localSync = repositoryService
					.getLocalSync(repositoryName);// not null
			SyncTreeDirectoryDTO remoteSync = repositoryService
					.getSync(repositoryName);// may be null

			filesManager = new FilesUploadManager();
			filesManager.setLocalSync(localSync);
			filesManager.setRemoteSync(remoteSync);
			filesManager.update();

			// 4. Resume Upload
			lastIndexFileUploaded = repositoryService
					.getLastIndexFileTransfered(repositoryName);
			startTime = System.nanoTime();
			connexionService.uploadRepository(repositoryName,
					filesManager.getFilesToCheck(),
					filesManager.getFilesToUpload(),
					filesManager.getFilesToDelete(), lastIndexFileUploaded);

			if (!canceled) {
				repositoryService.setLastIndexFileTransfered(repositoryName, 0);
			}

			executEnd();

		} catch (SocketTimeoutException | SocketException e1) {
			connexionService.getConnexionDAOs().get(0)
					.updateObserverUploadConnectionLost();
		} catch (Exception e2) {
			// e2.printStackTrace();
			List<Exception> errors = new ArrayList<Exception>();
			errors.add(e2);
			executeError(errors);
		}
	}

	private void executEnd() {

		repositoryService.setUploading(repositoryName, false);
		observerEndUpload.end();
	}

	private void executeError(List<Exception> errors) {

		repositoryService.setUploading(repositoryName, false);
		observerError.error(errors);
	}

	private void executeConnectionLost() {

		repositoryService.setUploading(repositoryName, false);
		observerConnectionLost.lost();
	}

	private void executeUpdateText(String text) {
		this.observerText.update(text);
	}

	private void executeUpdateTotalSize(long value) {
		totalFilesSize = value;
		this.observerTotalSize.update(totalFilesSize);
	}

	private void executeUpdateSingleSizeProgress(int pourcentage) {
		this.observerCountProgress.update(pourcentage);
	}

	private void executeUpdateUploadedSize(long value) {
		currentSize = uploadedFilesSize + value;
		this.observerUploadedSize.update(currentSize);
	}

	private void executeUpdateTotalSizeProgress(long value) {
		uploadedFilesSize = uploadedFilesSize + value;
		currentSize = uploadedFilesSize;
	}

	private void executeUpdateRemaingTime() {

		double endTime = System.nanoTime();
		double elapsedTime = endTime - startTime;
		long remainingFilesSize = totalFilesSize - currentSize;

		if (currentSize != 0) {
			long remainingTime = (long) ((elapsedTime * Math.pow(10, -9) * remainingFilesSize) / currentSize);
			this.observerRemainingTime.update(remainingTime);
		}
	}

	private void executeUpdateLastIndexUpdated() {
		lastIndexFileUploaded++;
		repositoryService.setLastIndexFileTransfered(repositoryName,
				lastIndexFileUploaded);
	}

	private void executeUpdateSpeed() {

		long endTime = System.nanoTime();
		long delta = endTime - deltaTimeSpeed;

		if (delta > (Math.pow(10, 9)) / 2) {// 0.5s
			long speed = connexionService.getConnexionDAOs().get(0).getSpeed();
			deltaTimeSpeed = endTime;
			if (observerSpeed != null) {
				observerSpeed.update(speed);
			}
		}
	}

	public void cancel() {

		canceled = true;
		if (connexionService != null) {
			connexionService.cancel();
		}
	}

	public void addObserverText(ObserverText obs) {
		this.observerText = obs;
	}

	public void addObserverCountProgress(ObserverCountInt obs) {
		this.observerCountProgress = obs;
	}

	public void addObserverUploadedSize(ObserverCountLong obs) {
		this.observerUploadedSize = obs;
	}

	public void addObserverTotalSize(ObserverCountLong obs) {
		this.observerTotalSize = obs;
	}

	public void addObserverSpeed(ObserverCountLong obs) {
		this.observerSpeed = obs;
	}

	public void addObserverRemainingTime(ObserverCountLong obs) {
		this.observerRemainingTime = obs;
	}

	public void addObserverEnd(ObserverEnd obs) {
		this.observerEndUpload = obs;
	}

	public void addObserverError(ObserverError obs) {
		this.observerError = obs;
	}

	public void addObserverConnectionLost(ObserverConnectionLost obs) {
		this.observerConnectionLost = obs;
	}
}
