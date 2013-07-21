package fr.soe.a3s.exception;

public class ServerInfoNotFoundException extends ApplicationException{

	private static String message = "File serverInfo not found. \n Try to rebuild the repository.";
	
	public ServerInfoNotFoundException(){
		super(message);
	}
	
}
