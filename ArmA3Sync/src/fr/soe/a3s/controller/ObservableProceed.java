package fr.soe.a3s.controller;

import java.io.IOException;

public interface ObservableProceed {

	public void addObserverProceed(ObserverProceed obs);

	public void updateObserverProceed() throws IOException;
}
