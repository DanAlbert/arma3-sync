package fr.soe.a3s.domain;

import fr.soe.a3s.constant.ProtocolType;

public class Http extends AbstractProtocole {

	private static final long serialVersionUID = -20L;

	public Http(String url, String port, String login, String password,
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
			return this.protocolType = ProtocolType.HTTP;
		}
		return this.protocolType;
	}
}
