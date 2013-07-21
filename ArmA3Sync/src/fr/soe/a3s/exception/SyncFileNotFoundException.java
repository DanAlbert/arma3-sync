package fr.soe.a3s.exception;

public class SyncFileNotFoundException extends ApplicationException {

	private static String message = "File sync not found. \n Try to rebuild the repository.";

	public SyncFileNotFoundException() {
		super(message);
	}

}
