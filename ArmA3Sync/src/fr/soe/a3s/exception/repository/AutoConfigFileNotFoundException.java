package fr.soe.a3s.exception.repository;

public class AutoConfigFileNotFoundException extends RepositoryException {

	private static String message = "File /.a3s/autoconfig not found on repository: ";

	public AutoConfigFileNotFoundException(String repositoryName) {
		super(message + repositoryName);
	}
}
