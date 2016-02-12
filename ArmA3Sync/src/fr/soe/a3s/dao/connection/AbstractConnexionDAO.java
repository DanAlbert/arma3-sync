package fr.soe.a3s.dao.connection;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import fr.soe.a3s.controller.ObservableCount;
import fr.soe.a3s.controller.ObservableCountErrors;
import fr.soe.a3s.controller.ObservableDownload;
import fr.soe.a3s.controller.ObservableProceed;
import fr.soe.a3s.controller.ObservableText;
import fr.soe.a3s.controller.ObservableUpload;
import fr.soe.a3s.controller.ObserverCount;
import fr.soe.a3s.controller.ObserverDownload;
import fr.soe.a3s.controller.ObserverProceed;
import fr.soe.a3s.controller.ObserverText;
import fr.soe.a3s.controller.ObserverUpload;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public abstract class AbstractConnexionDAO implements DataAccessConstants,
		ObservableCount, ObservableCountErrors, ObservableText,
		ObservableUpload, ObservableProceed, ObservableDownload {

	/***/
	private ObserverCount observerCount;
	private ObserverCount observerCountErrors;
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
	protected int count = 0, totalCount = 0;
	protected long responseTime = 0;
	protected double maximumClientDownloadSpeed = 0;

	protected static final String UNKNOWN_HOST = "Host name cannot be reached."
			+ "\n" + "Checkout the connection settings, DNS and firewall.";
	protected static final String CONNECTION_TIME_OUT_REACHED = "ArmA3Sync closed connection: Connection timeout reached."
			+ "\n" + "Checkout the connection advanced settings.";
	protected static final String READ_TIME_OUT_REACHED = "ArmA3Sync closed connection: Read timeout reached."
			+ "\n" + "Checkout the connection advanced settings.";
	protected static final String TIME_OUT_REACHED = "ArmA3Sync closed connection: Connection or Read timeout reached."
			+ "\n" + "Checkout the connection advanced settings.";
	protected static final String CONNECTION_FAILED = "Connection failed.";
	public static final String WRONG_LOGIN_PASSWORD = "login or password wrong or missing";

	/* Abstract Methods */

	public abstract boolean fileExists(String name, AbstractProtocole protocol,
			RemoteFile remoteFile) throws IOException;

	public abstract void deleteFile(RemoteFile remoteFile,
			String repositoryRemotePath) throws IOException;

	public abstract boolean uploadFile(RemoteFile remoteFile,
			String repositoryPath, String remotePath) throws IOException;

	public abstract File downloadFile(String name, AbstractProtocole protocol,
			String remotePath, String destinationPath, SyncTreeNodeDTO node)
			throws IOException;

	public abstract void disconnect();

	public IOException transferIOExceptionFactory(String coreMessage,
			IOException e) {

		if (e instanceof UnknownHostException) {
			String message = coreMessage + "\n" + UNKNOWN_HOST;
			return new UnknownHostException(message);
		} else if (e instanceof SocketTimeoutException) {
			String message = coreMessage + "\n" + TIME_OUT_REACHED;
			if (e.getMessage() != null) {
				if (e.getMessage().toLowerCase().contains("read")) {
					message = coreMessage + "\n" + READ_TIME_OUT_REACHED;
				} else if (e.getMessage().toLowerCase().contains("connect")) {
					message = coreMessage + "\n" + CONNECTION_TIME_OUT_REACHED;
				}
			}
			return new SocketTimeoutException(message);
		} else if (e.getCause() instanceof SocketTimeoutException) {
			String message = coreMessage + "\n" + TIME_OUT_REACHED;
			if (e.getCause().getMessage() != null) {
				if (e.getCause().getMessage().toLowerCase().contains("read")) {
					message = coreMessage + "\n" + READ_TIME_OUT_REACHED;
				} else if (e.getCause().getMessage().toLowerCase()
						.contains("connect")) {
					message = coreMessage + "\n" + CONNECTION_TIME_OUT_REACHED;
				}
			}
			return new SocketTimeoutException(message);
		} else if (e instanceof SocketException) {
			String message = coreMessage + "\n" + CONNECTION_FAILED;
			return new SocketException(message);
		} else if (e.getCause() instanceof SocketException) {
			String message = coreMessage + "\n" + CONNECTION_FAILED;
			return new SocketException(message);
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

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean isAcquiredSemaphore() {
		return acquiredSemaphore;
	}

	public void setAcquiredSemaphore(boolean acquiredSmaphore) {
		this.acquiredSemaphore = acquiredSmaphore;
	}

	public void setCompletion(double value) {
		this.downloadingLeaf.setComplete(value);
	}

	public double getMaximumClientDownloadSpeed() {
		return this.maximumClientDownloadSpeed;
	}

	public void setMaximumClientDownloadSpeed(double value) {
		this.maximumClientDownloadSpeed = value;
	}

	/* Count observable Interface */

	@Override
	public void addObserverCount(ObserverCount obs) {
		this.observerCount = obs;
	}

	@Override
	public void updateObserverCount() {// value in %
		this.observerCount.update(this.count * 100 / totalCount);
	}

	/* Count errors observable Interface */

	@Override
	public void addObserverCountErrors(ObserverCount obs) {
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
	public void updateObserverUploadSingleSizeProgress() {
		if (expectedFullSize != 0) {// division by 0
			int pourcentage = (int) (((countFileSize + offset) * 100) / expectedFullSize);
			this.observerUpload.updateSingleSizeProgress(pourcentage,
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
			int pourcentage = (int) (((countFileSize + offset) * 100) / expectedFullSize);
			this.observerDownload.updateSingleSizeProgress(countFileSize
					+ offset, pourcentage);
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
		this.observerDownload.updateEnd();
	}

	@Override
	public void updateObserverDownloadEndWithErrors(List<Exception> errors) {
		this.observerDownload.updateEndWithErrors(errors);
	}

	@Override
	public void updateObserverDownloadTooManyTimeoutErrors(
			int maxNumberOfTimeoutErrors, List<Exception> errors) {
		this.observerDownload.updateCancelTooManyTimeoutErrors(
				maxNumberOfTimeoutErrors, errors);
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
