package fr.soe.a3s.controller;

public interface ObservableUpload {

	public void addObserverUpload(ObserverUpload obs);

	public void updateObserverUploadTotalSize(long totalFilesSize);

	public void updateObserverUploadProgress();

	public void updateObserverUploadTotalSizeProgress();

	public void updateObserverUploadSpeed();

	public void updateObserverUploadLastIndexFileUploaded();
	
	public void updateObserverUploadConnectionLost();
}
