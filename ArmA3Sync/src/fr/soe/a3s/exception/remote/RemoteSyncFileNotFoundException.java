package fr.soe.a3s.exception.remote;

import fr.soe.a3s.dao.DataAccessConstants;

public class RemoteSyncFileNotFoundException extends RemoteRepositoryException
		implements DataAccessConstants {

	private static String message = "Remote file no found: " + "/"
			+ A3S_FOlDER_NAME + "/" + SYNC_FILE_NAME;

	public RemoteSyncFileNotFoundException() {
		super(message);
	}
}
