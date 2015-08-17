package fr.soe.a3s.exception.remote;

import fr.soe.a3s.dao.DataAccessConstants;

public class RemoteSyncFileNotFoundException extends RemoteRepositoryException implements
		DataAccessConstants {

	private static String message = "Remote file" + " " + SYNC_FILE_PATH + " "
			+ "not found on repository.";

	public RemoteSyncFileNotFoundException() {
		super(message);
	}
}
