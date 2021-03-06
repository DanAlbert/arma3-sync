package fr.soe.a3s.exception.repository;


public class SyncFileNotFoundException extends RepositoryException {

	private static String message = "File /.a3s/sync not found on repository: ";

	public SyncFileNotFoundException(String repositoryName) {
		super(message + repositoryName);
	}
}
