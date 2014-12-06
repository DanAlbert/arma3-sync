package fr.soe.a3s.dao;

import java.util.List;

import fr.soe.a3s.controller.ObservableActiveConnnection;
import fr.soe.a3s.controller.ObservableEnd;
import fr.soe.a3s.controller.ObservableError;
import fr.soe.a3s.controller.ObservableFileDownload;
import fr.soe.a3s.controller.ObservableFileSize;
import fr.soe.a3s.controller.ObservableFileSize2;
import fr.soe.a3s.controller.ObservableFilesNumber;
import fr.soe.a3s.controller.ObservableFilesNumber2;
import fr.soe.a3s.controller.ObservableSpeed;
import fr.soe.a3s.controller.ObserverActiveConnnection;
import fr.soe.a3s.controller.ObserverEnd;
import fr.soe.a3s.controller.ObserverError;
import fr.soe.a3s.controller.ObserverFileDownload;
import fr.soe.a3s.controller.ObserverFileSize;
import fr.soe.a3s.controller.ObserverFileSize2;
import fr.soe.a3s.controller.ObserverFilesNumber;
import fr.soe.a3s.controller.ObserverFilesNumber2;
import fr.soe.a3s.controller.ObserverSpeed;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class AbstractConnexionDAO implements DataAccessConstants,
		ObservableFilesNumber, ObservableFilesNumber2, ObservableFileSize,
		ObservableFileSize2, ObservableSpeed, ObservableFileDownload,
		ObservableEnd, ObservableActiveConnnection, ObservableError {

	protected ObserverFilesNumber observerFilesNumber;

	protected ObserverFilesNumber2 observerFilesNumber2;

	protected ObserverFileSize observerFileSize;

	protected ObserverFileSize2 observerFileSize2;

	protected ObserverSpeed observerSpeed;

	protected ObserverFileDownload observerFileDownload;

	protected ObserverEnd observerEnd;

	protected ObserverActiveConnnection observerActiveConnnection;

	protected ObserverError observerError;

	protected int countFileSize = 0;

	protected long offset = 0;

	protected long speed = 0;

	protected boolean canceled = false;

	protected SyncTreeNodeDTO downloadingNode;

	protected boolean activeConnection;

	/* File size controller */
	@Override
	public void addObserverFileSize(ObserverFileSize obs) {
		this.observerFileSize = obs;
	}

	@Override
	public void addObserverFileSize2(ObserverFileSize2 obs) {
		this.observerFileSize2 = obs;
	}

	@Override
	public void updateFileSizeObserver() {
		this.observerFileSize.update((int) offset + countFileSize,
				downloadingNode);
	}

	@Override
	public void updateFileSizeObserver2() {
		this.observerFileSize2.update((int) offset + countFileSize);
	}

	/* Files number controller */
	@Override
	public void addObserverFilesNumber(ObserverFilesNumber obs) {
		this.observerFilesNumber = obs;
	}

	@Override
	public void addObserverFilesNumber2(ObserverFilesNumber2 obs) {
		this.observerFilesNumber2 = obs;
	}

	@Override
	public void updateFilesNumberObserver() {
		this.observerFilesNumber.update(downloadingNode);
	}

	@Override
	public void updateFilesNumberObserver2() {
		this.observerFilesNumber2.update();
	}

	/* Speed controller */
	@Override
	public void addObserverSpeed(ObserverSpeed obs) {
		this.observerSpeed = obs;
	}

	@Override
	public void updateObserverSpeed() {
		this.observerSpeed.update();
	}

	public void setCountFileSize(int countFileSize) {
		this.countFileSize = countFileSize;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void cancel(boolean canceled) {
		this.canceled = canceled;
	}

	public void setOffset(long value) {
		this.offset = value;
	}

	@Override
	public void addObserverFileDownload(ObserverFileDownload obs) {
		this.observerFileDownload = obs;
	}

	@Override
	public void updateFileDownloadObserver() {
		this.observerFileDownload.proceed();
	}

	@Override
	public void addObserverEnd(ObserverEnd obs) {
		this.observerEnd = obs;
	}

	@Override
	public void updateObserverEnd() {
		this.observerEnd.end();
	}

	@Override
	public void addObserverError(ObserverError obs) {
		this.observerError = obs;
	}

	@Override
	public void updateObserverError(List<Exception> errors) {
		this.observerError.error(errors);
	}

	public long getSpeed() {
		return this.speed;
	}

	public void setSpeed(long speed) {
		this.speed = speed;
	}

	@Override
	public void addObserverActiveConnection(ObserverActiveConnnection obs) {
		this.observerActiveConnnection = obs;
	}

	@Override
	public void updateObserverActiveConnection() {
		this.observerActiveConnnection.update();
	}

	public boolean isActiveConnection() {
		return activeConnection;
	}

	public void setActiveConnection(boolean activeConnection) {
		this.activeConnection = activeConnection;
	}

	public long getOffset() {
		return this.offset;
	}
}
