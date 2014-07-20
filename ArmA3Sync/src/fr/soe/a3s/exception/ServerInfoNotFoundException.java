package fr.soe.a3s.exception;

public class ServerInfoNotFoundException extends ApplicationException {

	private static String message = "File /.a3s/serverInfo not found on repository url:";

	public ServerInfoNotFoundException(String url) {
		super(message + "\n" + url);
	}
}
