package fr.soe.a3s.exception;


public class CreateDirectoryException extends Exception {

	private final String filePath;

	public CreateDirectoryException(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public String getMessage() {
		String message = "Cannot create directory " + filePath + "\n"
				+ "Please checkout file access permissions.";
		return message;
	}
}
