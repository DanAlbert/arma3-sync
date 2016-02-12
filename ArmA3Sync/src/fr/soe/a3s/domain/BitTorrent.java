package fr.soe.a3s.domain;

import fr.soe.a3s.constant.ProtocolType;

public class BitTorrent extends AbstractProtocole {

	private static final long serialVersionUID = -30L;

	public BitTorrent(String url, String port, String login, String password,
			String connectionTimeOut, String readTimeOut,
			ProtocolType protocolType) {
		this.login = login;
		this.password = password;
		this.url = url;
		this.port = port;
		this.connectionTimeOut = connectionTimeOut;
		this.readTimeOut = readTimeOut;
		this.protocolType = protocolType;
	}

	public BitTorrent(String url, String port, String login, String password,
			ProtocolType protocolType) {
		this.login = login;
		this.password = password;
		this.url = url;
		this.port = port;
		this.protocolType = protocolType;
	}

	@Override
	public ProtocolType getProtocolType() {
		if (this.protocolType == null) {
			return this.protocolType = ProtocolType.HTTP_BITTORRENT;
		}
		return this.protocolType;
	}
}
