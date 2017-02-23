package fr.soe.a3s.controller;

import java.util.List;

public interface ObserverUncompress {

	public void start();

	public void update(int value);

	public void end();

	public void error(List<Exception> errors);
}
