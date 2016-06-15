package fr.soe.a3s.constant;

public enum ProtocolType {

	FTP("FTP", "ftp://", "21"), HTTP("HTTP", "http://", "80"), HTTPS("HTTPS",
			"https://", "443"), A3S("A3S", "a3s://", ""), FTP_BITTORRENT(
			"FTP + BitTorrent", "ftp://", "21"), HTTP_BITTORRENT(
			"HTTP + BitTorrent", "http://", "80"), HTTPS_BITTORRENT(
			"HTTPS + BitTorrent", "https://", "443"), SOCKS4("SOCKS4",
			"socks4://", "1080"), SOCKS5("SOCKS5", "socks5://", "1080");

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
		} else if (description.equals(FTP_BITTORRENT.getDescription())) {
			return FTP_BITTORRENT;
		} else if (description.equals(HTTP_BITTORRENT.getDescription())) {
			return HTTP_BITTORRENT;
		} else if (description.equals(HTTPS_BITTORRENT.getDescription())) {
			return HTTPS_BITTORRENT;
		} else if (description.equals(SOCKS4.getDescription())) {
			return SOCKS4;
		} else if (description.equals(SOCKS5.getDescription())) {
			return SOCKS5;
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
