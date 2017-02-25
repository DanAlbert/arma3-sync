package fr.soe.a3s.dao.connection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

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
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public abstract class AbstractConnexionDAO implements DataAccessConstants,
		ObservableCountInt, ObservableCountErrors, ObservableEnd,
		ObservableError, ObservableText, ObservableUpload, ObservableProceed,
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
	protected long expectedFullSize = 0;
	protected SyncTreeLeafDTO downloadingLeaf = null;

	/***/
	protected boolean acquiredSemaphore = false;
	protected boolean canceled = false;
	protected boolean activeConnection = false;
	protected long countFileSize = 0;
	protected long offset = 0;
	protected long speed = 0;
	protected long responseTime = 0;
	protected double maximumClientDownloadSpeed = 0;

	/***/
	protected static final String CONNECTION_FAILED = "Connection failed.";
	protected static final String CONNECTION_TIME_OUT_REACHED = "Connection closed by ArmA3Sync."
			+ "\n" + "Server didn't respond, connection timeout reached.";
	protected static final String READ_TIME_OUT_REACHED = "Connection closed by ArmA3Sync."
			+ "\n" + "Server didn't respond, read timeout reached.";
	protected static final String TIME_OUT_REACHED = "Connection closed by ArmA3Sync."
			+ "\n"
			+ "Server didn't respond, connection or read timeout reached.";
	public static final String WRONG_LOGIN_PASSWORD = "login or password wrong or missing";

	/* Abstract Methods */

	public abstract boolean fileExists(AbstractProtocole protocol,
			RemoteFile remoteFile) throws IOException;

	public abstract void deleteFile(RemoteFile remoteFile,
			String repositoryRemotePath) throws IOException;

	public abstract void uploadFile(RemoteFile remoteFile,
			String repositoryPath, String remotePath) throws IOException;

	public abstract File downloadFile(Repository repository, String remotePath,
			String destinationPath, SyncTreeNodeDTO node) throws IOException;

	public abstract void connectToRepository(AbstractProtocole protocol)
			throws IOException;

	public abstract void disconnect();

	/* Public Methods */

	public IOException transferIOExceptionFactory(String coreMessage,
			IOException e) {

		if (e instanceof UnknownHostException || e instanceof SocketException
				|| e.getCause() instanceof SocketException) {
			String message = coreMessage + "\n" + CONNECTION_FAILED;
			return new SocketException(message);
		} else if (e instanceof SocketTimeoutException
				|| e.getCause() instanceof SocketTimeoutException) {
			Throwable thr = null;
			if (e instanceof SocketTimeoutException) {
				thr = e;
			} else {
				thr = e.getCause();
			}
			String message = coreMessage + "\n" + TIME_OUT_REACHED;
			if (thr.getMessage() != null) {
				if (thr.getMessage().toLowerCase().contains("read")) {
					message = coreMessage + "\n" + READ_TIME_OUT_REACHED;
				} else if (thr.getMessage().toLowerCase().contains("connect")) {
					message = coreMessage + "\n" + CONNECTION_TIME_OUT_REACHED;
				}
			}
			return new SocketTimeoutException(message);
		} else if (e instanceof FileNotFoundException) {
			String message = coreMessage;
			if (e.getMessage() != null) {
				message = message + "\n" + e.getMessage();
			}
			return new FileNotFoundException(message);
		} else {
			String message = coreMessage;
			if (e.getMessage() != null) {
				message = message + "\n" + e.getMessage();
			}
			return new IOException(message);
		}
	}

	/* Getters and Setters */

	public long getSpeed() {
		return this.speed;
	}

	public SyncTreeLeafDTO getDownloadingLeaf() {
		return downloadingLeaf;
	}

	public void setSpeed(long speed) {
		this.speed = speed;
	}

	public long getCountFileSize() {
		return countFileSize;
	}

	public void setCountFileSize(long countFileSize) {
		this.countFileSize = countFileSize;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void cancel() {
		this.canceled = true;
	}

	public long getOffset() {
		return this.offset;
	}

	public void setOffset(long value) {
		this.offset = value;
	}

	public long getExpectedFullSize() {
		return expectedFullSize;
	}

	public void setExpectedFullSize(long expectedFullSize) {
		this.expectedFullSize = expectedFullSize;
	}

	public boolean isActiveConnection() {
		return activeConnection;
	}

	public void setActiveConnection(boolean activeConnection) {
		this.activeConnection = activeConnection;
	}

	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}

	public boolean isAcquiredSemaphore() {
		return acquiredSemaphore;
	}

	public void setAcquiredSemaphore(boolean acquiredSmaphore) {
		this.acquiredSemaphore = acquiredSmaphore;
	}

	public double getMaximumClientDownloadSpeed() {
		return this.maximumClientDownloadSpeed;
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
		if (expectedFullSize != 0) {// division by 0
			int pourcentage = (int) (((countFileSize + offset) * 100) / expectedFullSize);
			this.observerUpload.updateSingleSizeProgress(pourcentage,
					countFileSize + offset);
		} else {
			this.observerUpload.updateSingleSizeProgress(0, countFileSize
					+ offset);
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
		if (expectedFullSize != 0) {// division by 0
			int pourcentage = (int) (((countFileSize + offset) * 100) / (expectedFullSize * ((100 - downloadingLeaf
					.getComplete()) / 100)));
			this.observerDownload.updateSingleSizeProgress(countFileSize
					+ offset, pourcentage);
		} else {
			this.observerDownload.updateSingleSizeProgress(countFileSize
					+ offset, 0);
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
	public void updateObserverDownloadResponseTime() {
		this.observerDownload.updateResponseTime(responseTime);
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
}
