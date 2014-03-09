package fr.soe.a3s.exception;

public class SyncFileNotFoundException extends ApplicationException {

	private static String message = "File /.a3s/sync not found on repository.\nTry to rebuild the repository.";

	public SyncFileNotFoundException() {
		super(message);
	}

}
