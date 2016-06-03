package fr.soe.a3s.domain;

import java.io.Serializable;

import fr.soe.a3s.constant.EncryptionMode;
import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.constant.TimeOutValues;
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
	protected ProtocolType protocolType;
	@Deprecated
	protected EncryptionMode encryptionMode;
	protected String connectionTimeOut;
	protected String readTimeOut;

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public String getUrl() {
		return url;
	}

	public String getPort() {
		return port;
	}

	public abstract ProtocolType getProtocolType();

	public String getConnectionTimeOut() {
		if (connectionTimeOut == null) {
			connectionTimeOut = Integer
					.toString(TimeOutValues.CONNECTION_TIME_OUT.getValue());
		}
		return connectionTimeOut;
	}

	public void setConnectionTimeOut(String connectionTimeOut) {
		this.connectionTimeOut = connectionTimeOut;
	}

	public String getReadTimeOut() {
		if (readTimeOut == null) {
			readTimeOut = Integer.toString(TimeOutValues.READ_TIME_OUT
					.getValue());
		}
		return readTimeOut;
	}

	public void setReadTimeOut(String readTimeOut) {
		this.readTimeOut = readTimeOut;
	}

	public String getHostname() {

		String hostname = url;
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
			Integer.parseInt(getPort());
		} catch (NumberFormatException e) {
			throw new CheckException("Port is invalid!");
		}
		// http://stackoverflow.com/questions/15657266/java-lang-illegalargumentexception-port-out-of-range67001
		if (Integer.parseInt(getPort()) > 65535) {
			throw new CheckException("Port number is out of range!" + "\n"
					+ "Maximum port number is 65535.");
		}
		if ("".equals(getConnectionTimeOut()) || getConnectionTimeOut() == null) {
			throw new CheckException("Connection timeout field is empty!");
		}
		try {
			Integer.parseInt(getConnectionTimeOut());
		} catch (NumberFormatException e) {
			throw new CheckException("Connection timeout is invalid!");
		}
		if ("".equals(getReadTimeOut()) || getReadTimeOut() == null) {
			throw new CheckException("Read timeout field is empty!");
		}
		try {
			Integer.parseInt(getReadTimeOut());
		} catch (NumberFormatException e) {
			throw new CheckException("Read timeout is invalid!");
		}
	}
}
