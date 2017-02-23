package fr.soe.a3s.exception.repository;


public class ServerInfoNotFoundException extends RepositoryException {

	private static String message = "File /.a3s/serverinfo not found on repository: ";

	public ServerInfoNotFoundException(String repositoryName) {
		super(message + "\n" + repositoryName);
	}
}
