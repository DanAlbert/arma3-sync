package fr.soe.a3s.exception.remote;

import fr.soe.a3s.dao.DataAccessConstants;

public class RemoteEventsFileNotFoundException extends
		RemoteRepositoryException implements DataAccessConstants {

	private static String message = "Remote file" + " " + EVENTS_FILE_PATH
			+ " " + "not found on repository.";

	public RemoteEventsFileNotFoundException() {
		super(message);
	}
}
