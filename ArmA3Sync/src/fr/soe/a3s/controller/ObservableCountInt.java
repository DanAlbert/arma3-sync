package fr.soe.a3s.controller;

public interface ObservableCountInt {

	public void addObserverCount(ObserverCountInt obs);

	public void updateObserverCount(int value);
}
