package fr.soe.a3s.controller;

public interface ObservableUpload {

	public void addObserverUpload(ObserverUpload obs);

	public void updateObserverUploadTotalSize(long totalFilesSize);

	public void updateObserverUploadTotalSizeProgress();

	public void updateObserverUploadSingleSizeProgress();

	public void updateObserverUploadSpeed();

	void updateObserverUploadLastIndexFileUploaded();
}
