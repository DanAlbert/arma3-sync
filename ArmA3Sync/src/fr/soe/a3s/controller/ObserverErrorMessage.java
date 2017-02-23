package fr.soe.a3s.controller;

import java.util.List;

public interface ObserverErrorMessage {
	public void error(String message, List<Exception> errors);
}
