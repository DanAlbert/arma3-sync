package fr.soe.a3s.dao.connection;

public class RemoteFile {

	private final String filename;
	private final String parentDirectoryRelativePath;
	private final boolean isDirectory;

	public RemoteFile(String filename, String parentDirectoryRelativePath,
			boolean isDirectory) {
		this.filename = filename;
		this.parentDirectoryRelativePath = parentDirectoryRelativePath;
		this.isDirectory = isDirectory;
		assert (this.filename != null);
		assert (this.parentDirectoryRelativePath != null);
	}

	public String getFilename() {
		return filename;
	}

	public String getParentDirectoryRelativePath() {
		return parentDirectoryRelativePath;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public String getRelativeFilePath() {
		return parentDirectoryRelativePath + "/" + filename;
	}
}
