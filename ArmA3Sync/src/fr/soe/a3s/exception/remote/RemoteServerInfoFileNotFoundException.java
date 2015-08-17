package fr.soe.a3s.exception.remote;

import fr.soe.a3s.dao.DataAccessConstants;

public class RemoteServerInfoFileNotFoundException extends RemoteRepositoryException
		implements DataAccessConstants {

	private static String message = "Remote file" + " " + SERVERINFO_FILE_PATH
			+ " " + "not found on repository.";

	public RemoteServerInfoFileNotFoundException() {
		super(message);
	}
}
