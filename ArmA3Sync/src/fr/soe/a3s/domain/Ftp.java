package fr.soe.a3s.domain;

public class Ftp extends AbstractProtocole {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1917448655850030044L;

	public Ftp(String url, String port, String login, String password,
			String connectionTimeOut, String readTimeOut) {
		this.login = login;
		this.password = password;
		this.url = url;
		this.port = port;
		this.connectionTimeOut = connectionTimeOut;
		this.readTimeOut = readTimeOut;
	}

	public Ftp(String url, String port, String login, String password) {
		this.login = login;
		this.password = password;
		this.url = url;
		this.port = port;
	}
}
