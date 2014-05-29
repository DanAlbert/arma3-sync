package fr.soe.a3s.dao;

import fr.soe.a3s.controller.ObservableFileSize;
import fr.soe.a3s.controller.ObservableFilesNumber;
import fr.soe.a3s.controller.ObservableSpeed;
import fr.soe.a3s.controller.ObserverFileSize;
import fr.soe.a3s.controller.ObserverFilesNumber;
import fr.soe.a3s.controller.ObserverSpeed;

public class AbstractConnexionDAO implements DataAccessConstants,
		ObservableFilesNumber, ObservableFileSize, ObservableSpeed {

	protected ObserverFilesNumber observerFilesNumber;

	protected ObserverFileSize observerFileSize;

	protected ObserverSpeed observerSpeed;

	protected int countFilesNumber = 0;

	protected int countFileSize = 0;

	protected long size = 0;

	protected long startTime = 0;

	protected long endTime = 0;

	protected long offset = 0;

	protected boolean canceled = false;

	/* File size controller */
	@Override
	public void addObserverFileSize(ObserverFileSize obs) {
		this.observerFileSize = obs;
	}

	@Override
	public void updateFileSizeObserver() {
		this.observerFileSize.update((int) offset + countFileSize);
	}

	@Override
	public void delObserverFileSize() {
		this.observerFileSize = null;
	}

	/* Files number controller */
	@Override
	public void addObserverFilesNumber(ObserverFilesNumber obs) {
		this.observerFilesNumber = obs;
	}

	@Override
	public void updateFilesNumberObserver() {
		this.observerFilesNumber.update(countFilesNumber);
	}

	@Override
	public void delObserverFilesNumber() {
		this.observerFilesNumber = null;
	}

	/* Speed controller */
	@Override
	public void addObserverSpeed(ObserverSpeed obs) {
		this.observerSpeed = obs;
	}

	@Override
	public void updateObserverSpeed(long nbBytes) {
		long totalTime = endTime - startTime;
		if (totalTime > Math.pow(10, 9) / 2) {// 0.5s
			double value = nbBytes / (totalTime * Math.pow(10, -9));
			this.observerSpeed.update((long) value);
		}
	}

	@Override
	public void delObserverSpeed() {
		this.observerSpeed = null;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setCountFileSize(int countFileSize) {
		this.countFileSize = countFileSize;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void cancel(boolean canceled) {
		this.canceled = canceled;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}
}
