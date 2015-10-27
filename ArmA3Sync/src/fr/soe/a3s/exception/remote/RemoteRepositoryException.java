package fr.soe.a3s.exception.remote;

public abstract class RemoteRepositoryException extends Exception {

	protected RemoteRepositoryException() {
	}

	protected RemoteRepositoryException(final String message) {
		super(message);
	}
}
