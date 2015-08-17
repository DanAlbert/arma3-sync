package fr.soe.a3s.constant;

public enum ProtocolType {

	FTP("FTP", "ftp://", "21"), HTTP("HTTP", "http://", "80"), HTTPS("HTTPS",
			"https://", "443"), A3S("A3S", "a3s://", ""), HTTP_WEBDAV(
			"HTTP/WEBDAV", "http://", "80"), HTTPS_WEBDAV("HTTPS/WEBDAV",
			"https://", "443");

	private String description;
	private String prompt;
	private String defaultPort;

	private ProtocolType(String description, String prompt, String defaultPort) {
		this.description = description;
		this.prompt = prompt;
		this.defaultPort = defaultPort;
	}

	public String getDescription() {
		return this.description;
	}

	public static ProtocolType getEnum(String description) {
		if (description.equals(FTP.getDescription())) {
			return FTP;
		} else if (description.equals(HTTP.getDescription())) {
			return HTTP;
		} else if (description.equals(HTTPS.getDescription())) {
			return HTTPS;
		} else if (description.equals(HTTP_WEBDAV.getDescription())) {
			return HTTP_WEBDAV;
		} else if (description.equals(HTTPS_WEBDAV.getDescription())) {
			return HTTPS_WEBDAV;
		}
		return null;
	}

	public String getPrompt() {
		return this.prompt;
	}

	public String getDefaultPort() {
		return this.defaultPort;
	}
}
