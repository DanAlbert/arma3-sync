package fr.soe.a3s.exception.repository;


public class RepositoryNotFoundException extends RepositoryException {

	public RepositoryNotFoundException(String repositoryName) {
		super("Repository " + repositoryName + " not found!");
	}
}
