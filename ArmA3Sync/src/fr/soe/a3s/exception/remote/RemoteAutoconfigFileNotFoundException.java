package fr.soe.a3s.exception.remote;

import fr.soe.a3s.dao.DataAccessConstants;

public class RemoteAutoconfigFileNotFoundException extends RemoteRepositoryException
		implements DataAccessConstants {

	private static String message = "Remote file" + " " + AUTOCONFIG_FILE_PATH
			+ " " + "not found on repository.";

	public RemoteAutoconfigFileNotFoundException() {
		super(message);
	}
}
