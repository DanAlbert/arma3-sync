package fr.soe.a3s.controller;


public interface ObservableFileDownload {
	public void addObserverFileDownload(ObserverFileDownload obs);

	public void updateFileDownloadObserver() throws Exception;
}
