package fr.soe.a3s.exception.repository;

public abstract class RepositoryException extends Exception {

	protected RepositoryException() {
	}

	protected RepositoryException(final String message) {
		super(message);
	}
}
