package fr.soe.a3s.exception.repository;

public class RepositoryMainFolderLocationNotFoundException extends RepositoryException {

	public RepositoryMainFolderLocationNotFoundException(String repositoryName) {
		super("Repository " + repositoryName
				+ " main folder location is empty!");
	}
}
