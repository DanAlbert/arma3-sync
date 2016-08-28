package fr.soe.a3s.exception;

import java.io.File;
import java.io.IOException;

import fr.soe.a3s.dao.FileAccessMethods;

public class DeleteDirectoryException  extends IOException{

	private String filePath;

	public DeleteDirectoryException(File file) {
		this.filePath = FileAccessMethods.getCanonicalPath(file);
	}

	@Override
	public String getMessage() {
		String message = "Cannot delete directory " + filePath + "\n"
				+ "Please checkout file access permissions.";
		return message;
	}
}
