package fr.soe.a3s.controller;

public interface ObservableCheck {

	public void addObserverCheck(ObserverCheck obs);

	public void updateObserverCheckProgress();

	public void updateObserverCheckCountError(int value);
}
