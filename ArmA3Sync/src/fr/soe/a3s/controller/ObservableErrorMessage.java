package fr.soe.a3s.controller;

import java.util.List;

public interface ObservableErrorMessage {

	public void addObserverErrorMessage(ObserverError obs);

	public void updateObserverErrorMessage(String message,
			List<Exception> errors);
}
