package fr.soe.a3s.constant;

public enum Protocole {

	FTP("FTP", "ftp://"), HTTP("HTTP", "http://");

	private String description;

	private String prompt;

	private Protocole(String description, String prompt) {
		this.description = description;
		this.prompt = prompt;
	}

	public String getDescription() {
		return this.description;
	}

	public static Protocole getEnum(String description) {
		if (description.equals(FTP.getDescription())) {
			return FTP;
		} else if (description.equals(HTTP.getDescription())) {
			return HTTP;
		}
		return null;
	}

	public String getPrompt() {
		return this.prompt;
	}
}
