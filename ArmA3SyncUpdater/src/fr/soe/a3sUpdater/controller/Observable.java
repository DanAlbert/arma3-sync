package fr.soe.a3sUpdater.controller;

public interface Observable {
	
	public void addObservateur(Observateur obs);
	public void updateObservateur();
	public void delObservateur();

}
