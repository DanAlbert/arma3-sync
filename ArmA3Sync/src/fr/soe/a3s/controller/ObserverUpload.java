package fr.soe.a3s.controller;

public interface ObserverUpload {

	public void updateTotalSize(long value);

	public void updateTotalSizeProgress(long value);

	public void updateSingleSizeProgress(int pourcentage, long value);

	public void updateSpeed();

	public void updateLastIndexFileUploaded();
}
