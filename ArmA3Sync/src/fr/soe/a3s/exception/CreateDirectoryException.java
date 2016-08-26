package fr.soe.a3s.exception;

import java.io.File;
import java.io.IOException;

public class CreateDirectoryException extends IOException {

	private String filePath;

	public CreateDirectoryException(File file) {

		this.filePath = null;
		try {
			this.filePath = file.getCanonicalPath();
		} catch (IOException e) {
			this.filePath = file.getAbsolutePath();
		}
	}

	@Override
	public String getMessage() {
		String message = "Cannot create directory " + filePath + "\n"
				+ "Please checkout file access permissions.";
		return message;
	}
}
