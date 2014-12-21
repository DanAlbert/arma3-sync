package fr.soe.a3s.constant;

public enum Protocol {

	FTP("FTP", "ftp://"), HTTP("HTTP", "http://");

	private String description;

	private String prompt;

	private Protocol(String description, String prompt) {
		this.description = description;
		this.prompt = prompt;
	}

	public String getDescription() {
		return this.description;
	}

	public static Protocol getEnum(String description) {
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
