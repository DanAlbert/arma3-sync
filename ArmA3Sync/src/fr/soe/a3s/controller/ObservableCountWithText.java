package fr.soe.a3s.controller;

public interface ObservableCountWithText {

	public void addObserverCountWithText(ObserverCountWithText obs);

	public void updateObserverCountWithText();
	
	public void updateObserverCountWithText(String text);
}
