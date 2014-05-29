package fr.soe.a3s.exception;

public class AutoConfigNotFoundException extends ApplicationException{
	
	private static String message = "File /.a3s/autoconfig not found on repository.\nTry to rebuild the repository.";

	public AutoConfigNotFoundException() {
		super(message);
	}
}
