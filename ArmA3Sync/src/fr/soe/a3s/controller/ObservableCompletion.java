package fr.soe.a3s.controller;

public interface ObservableCompletion {

	public void addObserverCompletion(ObserverCompletion obs);

	public void updateCompletionCount(int value);

	public void updateEnd();

	public void updateError(Exception e);
}
