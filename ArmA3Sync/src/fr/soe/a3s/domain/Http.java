package fr.soe.a3s.domain;

public class Http extends AbstractProtocole {

	private static final long serialVersionUID = -20L;

	public Http(String url, String port, String login, String password,
			String connectionTimeOut, String readTimeOut) {
		this.login = login;
		this.password = password;
		this.url = url;
		this.port = port;
		this.connectionTimeOut = connectionTimeOut;
		this.readTimeOut = readTimeOut;
	}

	public Http(String url, String port, String login, String password) {
		this.login = login;
		this.password = password;
		this.url = url;
		this.port = port;
	}
}
