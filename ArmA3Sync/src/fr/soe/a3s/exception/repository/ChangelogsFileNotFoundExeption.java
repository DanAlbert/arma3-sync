package fr.soe.a3s.exception.repository;

public class ChangelogsFileNotFoundExeption extends RepositoryException {

	private static String message = "File 'changelogs' not found on repository: ";

	public ChangelogsFileNotFoundExeption(String repositoryName) {
		super(message + repositoryName);
	}
}
