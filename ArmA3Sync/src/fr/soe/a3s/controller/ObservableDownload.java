package fr.soe.a3s.controller;

import java.util.List;

public interface ObservableDownload {

	public void addObserverDownload(ObserverDownload obs);

	public void updateObserverDownloadTotalSize();

	public void updateObserverDownloadTotalSizeProgress();

	public void updateObserverDownloadSingleSizeProgress();

	public void updateObserverDownloadSpeed();

	public void updateObserverDownloadActiveConnections();

	public void updateObserverDownloadEnd();

	public void updateObserverDownloadEndWithErrors(List<Exception> errors);

	public void updateObserverDownloadTooManyErrors(int maxNumberOfErrors,
			List<Exception> errors);

	public void updateObserverDownloadResponseTime();

	public void updateObserverDownloadConnectionLost();
}
