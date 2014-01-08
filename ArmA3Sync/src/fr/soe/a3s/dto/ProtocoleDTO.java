package fr.soe.a3s.dto;

import fr.soe.a3s.constant.EncryptionMode;
import fr.soe.a3s.constant.Protocole;

public class ProtocoleDTO {

	private String login;
	private String password;
	private String url;
	private String port;
	private EncryptionMode encryptionMode;
	private Protocole protocole;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public EncryptionMode getEncryptionMode() {
		return encryptionMode;
	}

	public void setEncryptionMode(EncryptionMode encryptionMode) {
		this.encryptionMode = encryptionMode;
	}

	public Protocole getProtocole() {
		return protocole;
	}

	public void setProtocole(Protocole protocole) {
		this.protocole = protocole;
	}

}
