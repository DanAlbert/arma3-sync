package fr.soe.a3s.controller;

import java.util.List;

public interface ObserverDownload {

	public void updateTotalSizeProgress(long value);

	public void updateTotalSize();

	public void updateSingleSizeProgress(long value, int pourcentage);

	public void updateSpeed();

	public void updateActiveConnections();

	public void end();

	public void updateResponseTime(long responseTime);

	public void error(List<Exception> errors);

	public void updateCancelTooManyErrors(int value, List<Exception> errors);

	public void updateConnectionLost();
}
