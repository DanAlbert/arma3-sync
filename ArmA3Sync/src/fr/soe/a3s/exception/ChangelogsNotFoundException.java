package fr.soe.a3s.exception;

public class ChangelogsNotFoundException extends ApplicationException {

	private static String message = "File /.a3s/changelogs not found on repository.\nTry to rebuild the repository.";

	public ChangelogsNotFoundException() {
		super(message);
	}
}
