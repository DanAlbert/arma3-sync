package fr.soe.a3s.domain;

import java.io.Serializable;

import fr.soe.a3s.constant.EncryptionMode;

public class Ftp extends AbstractProtocole implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1917448655850030044L;

	public Ftp(String url, String port, String login, String password) {
		this.login = login;
		this.password = password;
		this.url = url;
		this.port = port;
	}

	public Ftp(String url, String port, String login, String password,
			EncryptionMode encryptionMode) {
		this.login = login;
		this.password = password;
		this.url = url;
		this.port = port;
		this.encryptionMode = encryptionMode;
	}
}
