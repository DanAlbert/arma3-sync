package fr.soe.a3s.dao.connection;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.CountingOutputStream;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import fr.soe.a3s.constant.DownloadStatus;
import fr.soe.a3s.controller.ObservableCountErrors;
import fr.soe.a3s.controller.ObservableCountInt;
import fr.soe.a3s.controller.ObservableDownload;
import fr.soe.a3s.controller.ObservableEnd;
import fr.soe.a3s.controller.ObservableError;
import fr.soe.a3s.controller.ObservableProceed;
import fr.soe.a3s.controller.ObservableText;
import fr.soe.a3s.controller.ObservableUpload;
import fr.soe.a3s.controller.ObserverCountInt;
import fr.soe.a3s.controller.ObserverDownload;
import fr.soe.a3s.controller.ObserverEnd;
import fr.soe.a3s.controller.ObserverError;
import fr.soe.a3s.controller.ObserverProceed;
import fr.soe.a3s.controller.ObserverText;
import fr.soe.a3s.controller.ObserverUpload;
import fr.soe.a3s.dao.A3SFilesAccessor;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.FileAccessMethods;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.CreateDirectoryException;
import fr.soe.a3s.exception.IncompleteFileTransferException;

public abstract class AbstractConnexionDAO
		implements
			ObservableCountInt,
			ObservableCountErrors,
			ObservableEnd,
			ObservableError,
			ObservableText,
			ObservableUpload,
			ObservableProceed,
			ObservableDownload {

	/***/
	private ObserverCountInt observerCount;
	private ObserverCountInt observerCountErrors;
	private ObserverEnd observerEnd;
	private ObserverError observerError;
	private ObserverText observerText;
	private ObserverUpload observerUpload;
	private ObserverProceed observerProceed;
	private ObserverDownload observerDownload;

	/***/
	private boolean acquiredSemaphore = false;
	private boolean canceled = false;
	private boolean activeConnection = false;

	/***/
	private long expectedFileSize = 0;
	private double complete = 0;
	private long countFileSize = 0;
	private long offset = 0;
	private long speed = 0;
	private double maximumClientDownloadSpeed = 0;

	/* Protected Abstract Methods */

	protected abstract void connect(AbstractProtocole protocol,
			RemoteFile remoteFile, long startOffset, long endOffset)
			throws IOException;

	protected abstract void disconnect();

	protected abstract void downloadFile(File file, RemoteFile remoteFile,
			boolean doRecordProgress, boolean doControlSpeed)
			throws IOException, IncompleteFileTransferException;

	protected abstract void downloadPartialFile(File file,
			Repository repository, SyncTreeLeafDTO leaf) throws IOException;

	protected abstract boolean fileExists(RemoteFile remoteFile)
			throws IOException;

	protected abstract void uploadFile(File file, RemoteFile remoteFile,
			boolean doRecordProgress) throws IOException;

	protected abstract void uploadObjectFile(Object object,
			RemoteFile remoteFile) throws IOException;

	protected abstract void deleteFile(RemoteFile remoteFile)
			throws IOException;

	/* Public Abstract Methods */

	public abstract String checkPartialFileTransfer(Repository repository)
			throws IOException;

	public abstract double getFileCompletion(Repository repository,
			SyncTreeLeafDTO leaf) throws IOException;

	/* Public Methods */

	public void checkConnection(AbstractProtocole protocol) throws IOException {

		String remoteDirectoryPath = DataAccessConstants.A3S_FOlDER_NAME;
		RemoteFile remoteFile = new RemoteFile(
				DataAccessConstants.SYNC_FILE_NAME, remoteDirectoryPath, false);
		connect(protocol, remoteFile, 0, -1);
		disconnect();
	}

	public Object downloadA3SObject(String repositoryName,
			AbstractProtocole protocol, String objectName) throws IOException {

		File directory = new File(
				DataAccessConstants.TEMP_FOLDER_PATH + "/" + repositoryName);
		File file = new File(directory + "/" + objectName);
		String remoteDirectoryPath = DataAccessConstants.A3S_FOlDER_NAME;

		RemoteFile remoteFile = new RemoteFile(file.getName(),
				remoteDirectoryPath, false);

		System.out
				.println("Downloading file: " + remoteFile.getRelativeFilePath()
						+ " from repository: " + repositoryName);

		connect(protocol, remoteFile, 0, -1);

		Object object = null;
		try {
			directory.mkdir();
			if (!directory.exists()) {
				throw new CreateDirectoryException(directory);
			}
			FileAccessMethods.deleteFile(file);
			downloadFile(file, remoteFile, false, false);
			if (file.exists()) {
				object = A3SFilesAccessor.read(file);
			}
		} finally {
			disconnect();
			FileAccessMethods.deleteDirectory(directory);
		}
		return object;
	}

	public AutoConfig importAutoConfig(AbstractProtocole protocol)
			throws IOException {

		File directory = new File(DataAccessConstants.TEMP_FOLDER_PATH);
		File file = new File(
				directory + "/" + DataAccessConstants.AUTOCONFIG_FILE_NAME);

		RemoteFile remoteFile = new RemoteFile(file.getName(), "", false);

		connect(protocol, remoteFile, 0, -1);

		AutoConfig autoConfig = null;
		try {
			directory.mkdir();
			if (!directory.exists()) {
				throw new CreateDirectoryException(directory);
			}
			FileAccessMethods.deleteFile(file);
			downloadFile(file, remoteFile, false, false);
			if (file.exists()) {
				autoConfig = (AutoConfig) A3SFilesAccessor.read(file);
			}
		} finally {
			disconnect();
			FileAccessMethods.deleteFile(file);
		}
		return autoConfig;
	}

	public String downloadXMLupdateFile(boolean devMode,
			AbstractProtocole protocol) throws IOException, DocumentException {

		File file = new File(
				DataAccessConstants.INSTALLATION_PATH + "/" + "a3s.xml");

		String remoteDirectoryPath = null;
		if (devMode) {
			remoteDirectoryPath = DataAccessConstants.UPDATE_REPOSITORY_DEV_DIR;
		} else {
			remoteDirectoryPath = DataAccessConstants.UPDATE_REPOSITORY_DIR;
		}

		RemoteFile remoteFile = new RemoteFile(file.getName(),
				remoteDirectoryPath, false);

		System.out.println("Retreiving xml update file: "
				+ remoteFile.getRelativeFilePath());

		connect(protocol, remoteFile, 0, -1);

		String nom = null;
		try {
			FileAccessMethods.deleteFile(file);
			downloadFile(file, remoteFile, false, false);
			if (file.exists()) {
				SAXReader reader = new SAXReader();
				Document documentLeaVersion = reader.read(file);
				Element root = documentLeaVersion.getRootElement();
				nom = root.selectSingleNode("nom").getText();
			}
		} finally {
			disconnect();
		}
		return nom;
	}

	public File downloadFile(Repository repository, SyncTreeNodeDTO node)
			throws IOException {

		// Create destination directory
		File destinationDirectory = new File(
				repository.getDefaultDownloadLocation() + "/"
						+ node.getParentRelativePath());
		destinationDirectory.mkdirs();
		if (!destinationDirectory.exists()) {
			throw new CreateDirectoryException(destinationDirectory);
		}

		File downloadedFile = null;
		this.expectedFileSize = 0;
		this.offset = 0;
		this.complete = 0;
		this.countFileSize = 0;
		this.speed = 0;

		try {
			if (node.isLeaf()) {

				SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
				RemoteFile remoteFile = null;
				this.complete = leaf.getComplete();

				if (this.complete == 0) {

					if (leaf.isCompressed()) {
						downloadedFile = new File(
								destinationDirectory + "/" + leaf.getName()
										+ DataAccessConstants.ZIP_EXTENSION);

						this.expectedFileSize = leaf.getCompressedSize();

						remoteFile = new RemoteFile(
								leaf.getName()
										+ DataAccessConstants.ZIP_EXTENSION,
								node.getParentRelativePath(), false);
					} else {
						downloadedFile = new File(
								destinationDirectory + "/" + leaf.getName());

						this.expectedFileSize = leaf.getSize();

						remoteFile = new RemoteFile(leaf.getName(),
								node.getParentRelativePath(), false);
					}

					// Set offset
					if (leaf.getDownloadStatus().equals(DownloadStatus.RUNNING)
							&& downloadedFile.exists()
							&& downloadedFile.length() > 0 && downloadedFile
									.length() < this.expectedFileSize) {

						this.offset = downloadedFile.length();
						System.out.println("Resuming file: "
								+ downloadedFile.getAbsolutePath()
								+ " at offset: " + this.offset);
					} else {
						FileAccessMethods.deleteFile(downloadedFile);
						this.offset = 0;
						System.out.println("Downloading file: "
								+ downloadedFile.getAbsolutePath());
					}

					leaf.setDownloadStatus(DownloadStatus.RUNNING);

					try {
						connect(repository.getProtocol(), remoteFile,
								this.offset, -1);
						downloadFile(downloadedFile, remoteFile, true, true);
						if (!isCanceled() && downloadedFile.exists()) {
							node.setDownloadStatus(DownloadStatus.DONE);
						} else {
							downloadedFile = null;
						}
						disconnect();
					} catch (IncompleteFileTransferException e1) {
						System.out.println("WARNING: " + e1.getMessage());
						disconnect();
						downloadFile(repository, node);
					} catch (Exception e2) {
						downloadedFile = null;
						disconnect();
						if (!isCanceled()) {
							throw e2;
						}
					}
				} else {// the file is uncomplete => use zsync

					leaf.setDownloadStatus(DownloadStatus.RUNNING);

					try {
						downloadPartialFile(downloadedFile, repository, leaf);
						if (!canceled) {
							node.setDownloadStatus(DownloadStatus.DONE);
						} else {
							downloadedFile = null;
						}
					} catch (IOException e) {
						downloadedFile = null;
						if (!canceled) {
							throw e;
						}
					}
				}
			} else {// directory
				downloadedFile = new File(
						destinationDirectory + "/" + node.getName());
				downloadedFile.mkdir();
				if (!downloadedFile.exists()) {
					throw new CreateDirectoryException(downloadedFile);
				} else {
					node.setDownloadStatus(DownloadStatus.DONE);
				}
			}
		} finally {
			updateObserverDownloadSpeed();
			updateObserverDownloadTotalSizeProgress();
			this.expectedFileSize = 0;
			this.offset = 0;
			this.complete = 0;
			this.countFileSize = 0;
			if (isAcquiredSemaphore()) {
				updateObserverDownloadSingleSizeProgress();
			}
		}
		return downloadedFile;
	}

	public boolean fileExists(AbstractProtocole protocol, RemoteFile remoteFile)
			throws IOException {

		System.out.println(
				"Checking remote file: " + remoteFile.getRelativeFilePath());

		boolean exists = false;

		try {
			connect(protocol, remoteFile, 0, -1);
			exists = fileExists(remoteFile);
		} catch (FileNotFoundException e) {
			exists = false;
		} finally {
			disconnect();
		}

		if (exists) {
			System.out.println(
					"Remote file found: " + remoteFile.getRelativeFilePath());
		} else {
			System.out.println("Remote file not found: "
					+ remoteFile.getRelativeFilePath());
		}

		return exists;
	}

	public void uploadFile(AbstractProtocole protocol, File file,
			RemoteFile remoteFile) throws IOException {

		System.out.println("Uploading file: " + file.getAbsolutePath());
		System.out.println("to remote directory: " + protocol.getRemotePath()
				+ remoteFile.getParentDirectoryRelativePath());

		connect(protocol, null, 0, -1);

		this.expectedFileSize = file.length();
		this.countFileSize = 0;
		this.offset = 0;

		try {
			uploadFile(file, remoteFile, true);
		} finally {
			disconnect();
			updateObserverUploadTotalSizeProgress();
			updateObserverUploadLastIndexFileUploaded();
			this.countFileSize = 0;
			updateObserverUploadProgress();
			updateObserverUploadSpeed();
		}
	}

	public void uploadA3SObject(Object object, AbstractProtocole protocol,
			String objectName, String repositoryName) throws IOException {

		RemoteFile remoteFile = new RemoteFile(objectName,
				DataAccessConstants.A3S_FOlDER_NAME, false);

		System.out.println("Uploading file: " + remoteFile.getRelativeFilePath()
				+ " to repository: " + repositoryName);

		connect(protocol, null, 0, -1);

		try {
			uploadObjectFile(object, remoteFile);
		} finally {
			disconnect();
		}
	}

	public void deleteFile(RemoteFile remoteFile, AbstractProtocole protocol)
			throws IOException {

		System.out.println(
				"Deleting remote file: " + remoteFile.getRelativeFilePath());

		connect(protocol, remoteFile, 0, -1);

		try {
			deleteFile(remoteFile);
		} finally {
			disconnect();
		}
	}

	/* Getters and Setters */

	public long getSpeed() {
		return this.speed;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void cancel() {
		this.canceled = true;
	}

	public boolean isActiveConnection() {
		return activeConnection;
	}

	public void setActiveConnection(boolean activeConnection) {
		this.activeConnection = activeConnection;
	}

	public boolean isAcquiredSemaphore() {
		return acquiredSemaphore;
	}

	public void setAcquiredSemaphore(boolean acquiredSmaphore) {
		this.acquiredSemaphore = acquiredSmaphore;
	}

	public void setMaximumClientDownloadSpeed(double value) {
		this.maximumClientDownloadSpeed = value;
	}

	/* Count observable Interface */

	@Override
	public void addObserverCount(ObserverCountInt obs) {
		this.observerCount = obs;
	}

	@Override
	public void updateObserverCount(int value) {// value in %
		this.observerCount.update(value);
	}

	/* Count errors observable Interface */

	@Override
	public void addObserverCountErrors(ObserverCountInt obs) {
		this.observerCountErrors = obs;
	}

	@Override
	public void updateObserverCountErrors(int value) {
		if (this.observerCountErrors != null) {
			this.observerCountErrors.update(value);
		}
	}

	/* Text observable Interface */

	@Override
	public void addObserverText(ObserverText obs) {
		this.observerText = obs;
	}

	@Override
	public void updateObserverText(String text) {
		this.observerText.update(text);
	}

	/* End observable Interface */

	@Override
	public void addObserverEnd(ObserverEnd obs) {
		this.observerEnd = obs;
	}

	@Override
	public void updateObserverEnd() {
		this.observerEnd.end();
	}

	/* error observable Interface */

	@Override
	public void addObserverError(ObserverError obs) {
		this.observerError = obs;
	}

	@Override
	public void updateObserverError(List<Exception> errors) {
		this.observerError.error(errors);
	}

	/* Upload observable Interface */

	@Override
	public void addObserverUpload(ObserverUpload obs) {
		this.observerUpload = obs;
	}

	@Override
	public void updateObserverUploadTotalSize(long value) {
		this.observerUpload.updateTotalSize(value);
	}

	@Override
	public void updateObserverUploadTotalSizeProgress() {
		this.observerUpload.updateTotalSizeProgress(countFileSize + offset);
	}

	@Override
	public void updateObserverUploadProgress() {
		if (expectedFileSize != 0) {// division by 0
			int pourcentage = (int) (((countFileSize + offset) * 100)
					/ expectedFileSize);
			this.observerUpload.updateSingleSizeProgress(pourcentage,
					countFileSize + offset);
		} else {
			this.observerUpload.updateSingleSizeProgress(0,
					countFileSize + offset);
		}
	}

	@Override
	public void updateObserverUploadSpeed() {
		this.observerUpload.updateSpeed();
	}

	@Override
	public void updateObserverUploadLastIndexFileUploaded() {
		this.observerUpload.updateLastIndexFileUploaded();
	}

	@Override
	public void updateObserverUploadConnectionLost() {
		this.observerUpload.updateConnectionLost();
	}

	/* download observable Interface */

	@Override
	public void addObserverDownload(ObserverDownload obs) {
		this.observerDownload = obs;
	}

	@Override
	public void updateObserverDownloadTotalSizeProgress() {
		this.observerDownload.updateTotalSizeProgress(countFileSize + offset);
	}

	@Override
	public void updateObserverDownloadTotalSize() {
		this.observerDownload.updateTotalSize();
	}

	@Override
	public void updateObserverDownloadSingleSizeProgress() {
		if (expectedFileSize != 0) {// division by 0
			int pourcentage = (int) (((countFileSize + offset) * 100)
					/ (expectedFileSize * ((100 - this.complete) / 100)));
			this.observerDownload.updateSingleSizeProgress(
					countFileSize + offset, pourcentage);
		} else {
			this.observerDownload
					.updateSingleSizeProgress(countFileSize + offset, 0);
		}
	}

	@Override
	public void updateObserverDownloadSpeed() {
		this.observerDownload.updateSpeed();
	}

	@Override
	public void updateObserverDownloadActiveConnections() {
		this.observerDownload.updateActiveConnections();
	}

	@Override
	public void updateObserverDownloadEnd() {
		this.observerDownload.end();
	}

	@Override
	public void updateObserverDownloadEndWithErrors(List<Exception> errors) {
		this.observerDownload.error(errors);
	}

	@Override
	public void updateObserverDownloadConnectionLost() {
		this.observerDownload.updateConnectionLost();
	}

	@Override
	public void updateObserverDownloadTooManyErrors(int maxNumberOfErrors,
			List<Exception> errors) {
		this.observerDownload.updateCancelTooManyErrors(maxNumberOfErrors,
				errors);
	}

	/* Proceed observable Interface */

	@Override
	public void addObserverProceed(ObserverProceed obs) {
		this.observerProceed = obs;
	}

	@Override
	public void updateObserverProceed() {
		this.observerProceed.proceed();
	}

	/* Inner classes */

	protected class DownloadProgressListener {

		private boolean doRecordProgress;
		private CountingOutputStream dos;

		public DownloadProgressListener(boolean doRecordProgress) {
			this.doRecordProgress = doRecordProgress;
			this.dos = null;
		}

		public void init(final FileOutputStream fos) {

			final long startTime = System.nanoTime();

			speed = 0;

			dos = new CountingOutputStream(fos) {
				@Override
				protected void afterWrite(int n) throws IOException {
					super.afterWrite(n);
					long nbBytes = getByteCount();
					long endTime = System.nanoTime();
					long totalTime = endTime - startTime;

					countFileSize = nbBytes;

					if (totalTime > Math.pow(10, 9) * 0.25) {// 0.25s
						speed = (long) ((nbBytes * Math.pow(10, 9))
								/ totalTime);// B/s
					}

					if (doRecordProgress && isAcquiredSemaphore()) {
						updateObserverDownloadSingleSizeProgress();
						updateObserverDownloadSpeed();
					}
				}
			};
		}

		public void init(ByteArrayOutputStream byteArrayBuffer,
				final long cumulatedDataTransfered) {

			final long startTime = System.nanoTime();

			speed = 0;

			dos = new CountingOutputStream(byteArrayBuffer) {
				@Override
				protected void afterWrite(int n) throws IOException {
					super.afterWrite(n);
					long nbBytes = getByteCount();
					long endTime = System.nanoTime();
					long totalTime = endTime - startTime;

					countFileSize = nbBytes + cumulatedDataTransfered;

					if (totalTime > Math.pow(10, 9) * 0.25) {// 0.25s
						speed = (long) ((nbBytes * Math.pow(10, 9))
								/ totalTime);// B/s
					}

					if (doRecordProgress && isAcquiredSemaphore()) {
						updateObserverDownloadSingleSizeProgress();
						updateObserverDownloadSpeed();
					}
				}
			};
		}

		public void write(byte[] array, int bytesRead) throws IOException {
			dos.write(array, 0, bytesRead);
		}

		public void close() throws IOException {
			dos.close();
			speed = 0;
		}
	}

	protected class UploadProgressListener {

		private CountingInputStream uis;

		public UploadProgressListener() {
			this.uis = null;
		}

		public void init(final FileInputStream fis,
				final boolean doRecordProgress) {

			final long startTime = System.nanoTime();

			uis = new CountingInputStream(fis) {
				@Override
				protected void afterRead(int n) {
					super.afterRead(n);
					long nbBytes = getByteCount();
					long endTime = System.nanoTime();
					long totalTime = endTime - startTime;

					countFileSize = nbBytes;

					if (totalTime > Math.pow(10, 9) * 0.25) {// 0.25s
						speed = (long) ((nbBytes * Math.pow(10, 9))
								/ totalTime);// B/s
					}

					if (doRecordProgress) {
						updateObserverUploadProgress();
						updateObserverUploadSpeed();
					}
				}
			};
		}

		public int read(byte[] bytes) throws IOException {
			return uis.read(bytes);
		}

		public void close() throws IOException {
			uis.close();
		}
	}

	protected class SpeedControlListener {

		private boolean doControlSpeed;

		public SpeedControlListener(boolean doControlSpeed) {
			this.doControlSpeed = doControlSpeed;
		}

		public long getWaitTime() {

			long wait = 0;
			if (doControlSpeed) {
				if (maximumClientDownloadSpeed != 0) {
					if (speed > maximumClientDownloadSpeed) {
						wait = (long) ((speed / maximumClientDownloadSpeed)
								* Math.pow(10, 3) * 1 / 8);
					}
				}
			}
			return wait;
		}
	}
}
