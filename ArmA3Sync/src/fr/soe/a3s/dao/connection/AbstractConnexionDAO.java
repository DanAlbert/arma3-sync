package fr.soe.a3s.dao.connection;

import java.util.List;

import fr.soe.a3s.controller.ObservableCheck;
import fr.soe.a3s.controller.ObservableCount;
import fr.soe.a3s.controller.ObservableCountWithText;
import fr.soe.a3s.controller.ObservableDownload;
import fr.soe.a3s.controller.ObservableProceed;
import fr.soe.a3s.controller.ObservableUpload;
import fr.soe.a3s.controller.ObserverCheck;
import fr.soe.a3s.controller.ObserverCount;
import fr.soe.a3s.controller.ObserverCountWithText;
import fr.soe.a3s.controller.ObserverDownload;
import fr.soe.a3s.controller.ObserverProceed;
import fr.soe.a3s.controller.ObserverUpload;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;

public abstract class AbstractConnexionDAO implements DataAccessConstants,
		ObservableCount, ObservableCountWithText, ObservableUpload,
		ObservableProceed, ObservableDownload, ObservableCheck {

	/***/
	private ObserverCount observerCount;
	private ObserverCountWithText observerCountWithText;
	private ObserverUpload observerUpload;
	private ObserverProceed observerProceed;
	private ObserverDownload observerDownload;
	private ObserverCheck observerCheck;

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

	protected static final String UNKNOWN_HOST = "Host cannot be reached."
			+ "\n" + "Checkout repository url, firewall and connection.";
	protected static final String CONNECTION_TIME_OUT_REACHED = "Connection time out reached."
			+ "\n" + "Checkout Advanced options from repository edit panel.";
	protected static final String READ_TIME_OUT_REACHED = "Read time out reached."
			+ "\n" + "Checkout Advanced options from repository edit panel.";
	protected static final String CONNECTION_FAILED = "Connection failed.";
	protected static final String WRONG_LOGIN_PASSWORD = "login or password wrong or missing";

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

	/* Count with text observable Interface */

	@Override
	public void addObserverCountWithText(ObserverCountWithText obs) {
		this.observerCountWithText = obs;
	}

	@Override
	public void updateObserverCountWithText() {
		this.observerCountWithText.update(this.count * 100 / totalCount);
	}

	@Override
	public void updateObserverCountWithText(String text) {
		this.observerCountWithText.update(text);
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

	/* Check observable Interface */

	@Override
	public void addObserverCheck(ObserverCheck obs) {
		this.observerCheck = obs;
	}

	@Override
	public void updateObserverCheckProgress() {
		this.observerCheck.updateProgress(this.count * 100 / totalCount);
	}

	@Override
	public void updateObserverCheckCountError(int value) {
		this.observerCheck.updateErrorCount(value);
	}
}
