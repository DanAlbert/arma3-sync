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
import fr.soe.a3s.controller.ObservableFilesNumber3;
import fr.soe.a3s.controller.ObservableSpeed;
import fr.soe.a3s.controller.ObservableTotalFileSize;
import fr.soe.a3s.controller.ObserverActiveConnnection;
import fr.soe.a3s.controller.ObserverEnd;
import fr.soe.a3s.controller.ObserverError;
import fr.soe.a3s.controller.ObserverFileDownload;
import fr.soe.a3s.controller.ObserverFileSize;
import fr.soe.a3s.controller.ObserverFileSize2;
import fr.soe.a3s.controller.ObserverFilesNumber;
import fr.soe.a3s.controller.ObserverFilesNumber2;
import fr.soe.a3s.controller.ObserverFilesNumber3;
import fr.soe.a3s.controller.ObserverSpeed;
import fr.soe.a3s.controller.ObserverTotalFileSize;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class AbstractConnexionDAO implements DataAccessConstants,
		ObservableFilesNumber, ObservableFilesNumber2, ObservableFilesNumber3,
		ObservableFileSize, ObservableFileSize2, ObservableSpeed,
		ObservableFileDownload, ObservableEnd, ObservableActiveConnnection,
		ObservableError,ObservableTotalFileSize {

	protected ObserverFilesNumber observerFilesNumber;
	protected ObserverFilesNumber2 observerFilesNumber2;
	protected ObserverFilesNumber3 observerFilesNumber3;
	protected ObserverFileSize observerFileSize;
	protected ObserverFileSize2 observerFileSize2;
	protected ObserverTotalFileSize observerTotalFileSize;
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
	protected boolean activeConnection = false;
	protected long nbFiles, totalNbFiles;

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
	public void addObserverFilesNumber3(ObserverFilesNumber3 obs) {
		this.observerFilesNumber3 = obs;
	}

	@Override
	public void updateFilesNumberObserver() {
		this.observerFilesNumber.update(downloadingNode);
	}

	@Override
	public void updateFilesNumberObserver2() {
		this.observerFilesNumber2.update();
	}

	@Override
	public void updateFilesNumberObserver3() {
		observerFilesNumber3.update((int) (this.nbFiles * 100 / totalNbFiles));
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

	/* */
	@Override
	public void addObserverFileDownload(ObserverFileDownload obs) {
		this.observerFileDownload = obs;
	}

	@Override
	public void updateFileDownloadObserver() {
		this.observerFileDownload.proceed();
	}
	
	/* */
	@Override
	public void addObserverEnd(ObserverEnd obs) {
		this.observerEnd = obs;
	}

	@Override
	public void updateObserverEnd() {
		this.observerEnd.end();
	}

	/* */
	@Override
	public void addObserverError(ObserverError obs) {
		this.observerError = obs;
	}

	@Override
	public void updateObserverError(List<Exception> errors) {
		this.observerError.error(errors);
	}

	/* */
	@Override
	public void addObserverActiveConnection(ObserverActiveConnnection obs) {
		this.observerActiveConnnection = obs;
	}

	@Override
	public void updateObserverActiveConnection() {
		this.observerActiveConnnection.update();
	}
	
	/* */
	@Override
	public void addObserverTotalFileSize(ObserverTotalFileSize obs) {
		this.observerTotalFileSize = obs;
	}

	@Override
	public void updateTotalFileSizeObserver() {
		this.observerTotalFileSize.update();
	}
	
	/* Getters and Setters */

	public long getSpeed() {
		return this.speed;
	}

	public void setSpeed(long speed) {
		this.speed = speed;
	}

	public int getCountFileSize() {
		return countFileSize;
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

	public long getOffset() {
		return this.offset;
	}

	public void setOffset(long value) {
		this.offset = value;
	}

	public boolean isActiveConnection() {
		return activeConnection;
	}

	public void setActiveConnection(boolean activeConnection) {
		this.activeConnection = activeConnection;
	}

	public void setTotalNbFiles(long totalNbFiles) {
		this.totalNbFiles = totalNbFiles;
	}
	
	public void setCompletion(double value) {
		if (downloadingNode instanceof SyncTreeLeafDTO){
			SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) this.downloadingNode;
			leaf.setComplete(value);
		}
	}
}
