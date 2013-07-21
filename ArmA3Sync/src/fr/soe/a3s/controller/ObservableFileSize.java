package fr.soe.a3s.controller;

public interface ObservableFileSize {
	public void addObserverFileSize(ObserverFileSize obs);
	public void updateFileSizeObserver();
	public void delObserverFileSize();
}
