package fr.soe.a3s.domain;

import fr.soe.a3s.constant.ProtocolType;

public class Socks5 extends AbstractProtocole {

	private static final long serialVersionUID = -50L;

	public Socks5(String url, String port, String login, String password,
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
			return this.protocolType = ProtocolType.SOCKS5;
		}
		return this.protocolType;
	}
}
