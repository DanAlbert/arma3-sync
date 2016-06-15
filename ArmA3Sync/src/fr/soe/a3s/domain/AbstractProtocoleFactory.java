package fr.soe.a3s.domain;

import fr.soe.a3s.constant.ProtocolType;

public class AbstractProtocoleFactory {

	public static AbstractProtocole getProtocol(String url, String port,
			String login, String password, ProtocolType protocolType) {

		if (protocolType.equals(ProtocolType.FTP)) {
			return new Ftp(url, port, login, password, protocolType);
		} else if (protocolType.equals(ProtocolType.HTTP)
				|| protocolType.equals(ProtocolType.HTTPS)) {
			return new Http(url, port, login, password, protocolType);
		} else if (protocolType.equals(ProtocolType.FTP_BITTORRENT)
				|| protocolType.equals(ProtocolType.HTTP_BITTORRENT)
				|| protocolType.equals(ProtocolType.HTTPS_BITTORRENT)) {
			return new BitTorrent(url, port, login, password, protocolType);
		} else if (protocolType.equals(ProtocolType.SOCKS4)
				|| protocolType.equals(ProtocolType.SOCKS5)) {
			return new Socks(url, port, login, password, protocolType);
		} else {
			return null;
		}
	}
}
