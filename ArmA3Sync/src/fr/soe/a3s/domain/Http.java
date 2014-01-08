package fr.soe.a3s.domain;

import java.io.Serializable;

import fr.soe.a3s.constant.EncryptionMode;

public class Http extends AbstractProtocole {

	private static final long serialVersionUID = -20L;
	
	public Http(String url, String port, String login, String password) {
		this.login = login;
		this.password = password;
		this.url = url;
		this.port = port;
	}

	public Http(String url, String port, String login, String password,
			EncryptionMode encryptionMode) {
		this.login = login;
		this.password = password;
		this.url = url;
		this.port = port;
		this.encryptionMode = encryptionMode;
	}
}
