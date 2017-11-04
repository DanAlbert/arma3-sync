package fr.soe.a3s.exception.remote;

import fr.soe.a3s.dao.DataAccessConstants;

public class RemoteAutoconfigFileNotFoundException extends
		RemoteRepositoryException implements DataAccessConstants {

	private static String message = "Remote file not found: " + "/"
			+ A3S_FOlDER_NAME + "/" + AUTOCONFIG_FILE_NAME;

	public RemoteAutoconfigFileNotFoundException() {
		super(message);
	}
}
