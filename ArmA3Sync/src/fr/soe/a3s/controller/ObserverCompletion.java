package fr.soe.a3s.controller;

public interface ObserverCompletion {

	public void updateCount(int value);

	public void updateEnd();

	public void updateError(Exception e);
}
