package fr.soe.a3s.domain.configration;

import java.io.Serializable;

public class BikeyExtractOptions implements Serializable {

	private static final long serialVersionUID = 12345L;
	private String sourceDirectoryPath;
	private String targetDirectoryPath;

	public String getSourceDirectoryPath() {
		return sourceDirectoryPath;
	}

	public void setSourceDirectoryPath(String sourceDirectoryPath) {
		this.sourceDirectoryPath = sourceDirectoryPath;
	}

	public String getTargetDirectoryPath() {
		return targetDirectoryPath;
	}

	public void setTargetDirectoryPath(String targetDirectoryPath) {
		this.targetDirectoryPath = targetDirectoryPath;
	}
}
