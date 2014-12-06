package fr.soe.a3s.controller;

import java.util.List;

public interface ObservableError {

	public void addObserverError(ObserverError obs);

	public void updateObserverError(List<Exception> errors);
}
