package fr.soe.a3s.dto;

import fr.soe.a3s.constant.ProtocolType;

public class ProtocolDTO {

	private String login;
	private String password;
	private String url;
	private String port;
	private ProtocolType protocolType;
	private String connectionTimeOut;
	private String readTimeOut;

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

	public ProtocolType getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(ProtocolType protocolType) {
		this.protocolType = protocolType;
	}

	public String getConnectionTimeOut() {
		return connectionTimeOut;
	}

	public void setConnectionTimeOut(String connectionTimeOut) {
		this.connectionTimeOut = connectionTimeOut;
	}

	public String getReadTimeOut() {
		return readTimeOut;
	}

	public void setReadTimeOut(String readTimeOut) {
		this.readTimeOut = readTimeOut;
	}
}
