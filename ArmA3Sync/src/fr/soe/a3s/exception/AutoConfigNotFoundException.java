package fr.soe.a3s.exception;

public class AutoConfigNotFoundException extends ApplicationException {

	private static String message = "File /.a3s/autoconfig not found on repository url:";

	public AutoConfigNotFoundException(String url) {
		super(message + "\n" + url);
	}
}
