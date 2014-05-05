package fr.soe.a3s.domain;

import java.io.Serializable;

import fr.soe.a3s.constant.EncryptionMode;
import fr.soe.a3s.exception.CheckException;

public abstract class AbstractProtocole implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -886357032287815203L;
	protected String login;
	protected String password;
	protected String url;
	protected String port;
	protected EncryptionMode encryptionMode;

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

	public String getHostname() {
		String hostname = this.url;
		int index = url.indexOf("/");
		if (index != -1) {
			hostname = url.substring(0, index);
		}
		return hostname;
	}

	public String getRemotePath() {
		String remotePath = "";
		int index = url.indexOf("/");
		if (index != -1) {
			remotePath = url.substring(index);
		}
		return remotePath;
	}

	public void checkData() throws CheckException {
		if ("".equals(getUrl()) || getUrl() == null) {
			throw new CheckException("URL field is empty!");
		}
		if ("".equals(getLogin()) || getLogin() == null) {
			throw new CheckException("Login field is empty!");
		}
		if ("".equals(getPort()) || getPort() == null) {
			throw new CheckException("Port field is empty!");
		}
		try {
			Integer.parseInt(port);
		} catch (NumberFormatException e) {
			throw new CheckException("Port is invalid!");
		}
	}
}
