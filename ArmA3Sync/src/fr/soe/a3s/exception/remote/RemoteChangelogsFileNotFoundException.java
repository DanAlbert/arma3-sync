package fr.soe.a3s.exception.remote;

import fr.soe.a3s.dao.DataAccessConstants;

public class RemoteChangelogsFileNotFoundException extends RemoteRepositoryException
		implements DataAccessConstants {

	private static String message = "Remote file" + " " + CHANGELOGS_FILE_PATH
			+ " " + "not found on repository.";

	public RemoteChangelogsFileNotFoundException() {
		super(message);
	}
}
