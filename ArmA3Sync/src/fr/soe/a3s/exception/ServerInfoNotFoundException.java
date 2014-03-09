package fr.soe.a3s.exception;

public class ServerInfoNotFoundException extends ApplicationException{

	private static String message = "File /.a3s/serverInfo not found on repository.\nTry to rebuild the repository.";
	
	public ServerInfoNotFoundException(){
		super(message);
	}
	
}
