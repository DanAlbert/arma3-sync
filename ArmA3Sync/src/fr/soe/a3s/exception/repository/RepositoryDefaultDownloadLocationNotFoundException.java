package fr.soe.a3s.exception.repository;

public class RepositoryDefaultDownloadLocationNotFoundException extends
		RepositoryException {

	public RepositoryDefaultDownloadLocationNotFoundException(
			String repositoryName) {
		super("Repository " + repositoryName
				+ " default download location is empty!");
	}
}
