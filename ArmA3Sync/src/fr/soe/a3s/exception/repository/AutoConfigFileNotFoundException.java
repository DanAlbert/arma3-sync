package fr.soe.a3s.exception.repository;

public class AutoConfigFileNotFoundException extends RepositoryException {

	private static String message = "File 'autoconfig' not found on repository: ";

	public AutoConfigFileNotFoundException(String repositoryName) {
		super(message + repositoryName);
	}
}
