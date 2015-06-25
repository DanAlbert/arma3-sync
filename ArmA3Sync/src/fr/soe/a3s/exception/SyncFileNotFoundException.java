package fr.soe.a3s.exception;

public class SyncFileNotFoundException extends ApplicationException {

	private static String message = "File 'sync' not found on repository: ";

	public SyncFileNotFoundException(String repositoryName) {
		super(message + repositoryName);
	}
}
