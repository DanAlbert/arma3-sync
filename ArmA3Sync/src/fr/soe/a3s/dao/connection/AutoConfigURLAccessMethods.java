package fr.soe.a3s.dao.connection;

import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.domain.AbstractProtocoleFactory;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.exception.CheckException;

public class AutoConfigURLAccessMethods implements DataAccessConstants {

	/**
	 * Determine autoconfigUrl from repository protocol
	 * 
	 * @param protocol
	 *            not null
	 * @return string autoconfig url
	 */
	public static String determineAutoConfigUrl(AbstractProtocole protocol) {

		String repositoryUrl = protocol.getUrl();
		String port = protocol.getPort();
		String login = protocol.getLogin();
		String password = protocol.getPassword();

		if (login.equalsIgnoreCase("anonymous")) {
			login = "";
			password = "";
		}

		if (protocol.getPort().equals(
				protocol.getProtocolType().getDefaultPort())) {
			port = "";
		}

		String autoConfigURL = repositoryUrl;
		if (!port.isEmpty()) {
			autoConfigURL = autoConfigURL + ":" + port;
		}

		autoConfigURL = autoConfigURL + AUTOCONFIG_FILE_PATH;

		return autoConfigURL;
	}

	/**
	 * Parse autoconfig url
	 * 
	 * @param autoConfigURL
	 * @param protocolType
	 * @return
	 * @throws CheckException
	 */
	public static AbstractProtocole parse(String autoConfigURL)
			throws CheckException {

		String url = "";
		String port = "";
		String login = "";
		String password = "";

		String hostname = "";
		String relativePath = "";

		ProtocolType protocolType = null;
		if (autoConfigURL.toLowerCase().trim()
				.contains(ProtocolType.FTP.getPrompt())) {
			protocolType = ProtocolType.FTP;
		} else if (autoConfigURL.toLowerCase().trim()
				.contains(ProtocolType.HTTP.getPrompt())) {
			protocolType = ProtocolType.HTTP;
		} else if (autoConfigURL.toLowerCase().trim()
				.contains(ProtocolType.HTTPS.getPrompt())) {
			protocolType = ProtocolType.HTTPS;
		} else {
			String message = "Invalid url or unsupported protocol." + "\n"
					+ "Url must start with " + ProtocolType.FTP.getPrompt()
					+ " or " + ProtocolType.HTTP.getPrompt() + " or  "
					+ ProtocolType.HTTPS.getPrompt();
			throw new CheckException(message);
		}

		assert (protocolType != null);

		if (autoConfigURL.toLowerCase().trim()
				.contains(protocolType.getPrompt())) {// remove prompt from
														// autoConfigURL
			autoConfigURL = autoConfigURL.substring(protocolType.getPrompt()
					.length());
		}

		int index = autoConfigURL.indexOf("/");
		if (index != -1) {
			hostname = autoConfigURL.substring(0, index);
			relativePath = autoConfigURL.substring(index);
		} else {
			hostname = autoConfigURL;
		}

		int index2 = hostname.indexOf(":");
		if (index2 != -1) {
			port = hostname.substring(index2 + 1);
			hostname = hostname.substring(0, index2);
		}

		int index3 = relativePath.toLowerCase().lastIndexOf("/autoconfig");
		if (index3 != -1) {
			relativePath = relativePath.substring(0, index3);
		}

		url = hostname + relativePath;

		try {
			Integer.parseInt(port);
		} catch (NumberFormatException e) {
			port = "";
		}

		if (login.isEmpty()) {
			login = "anonymous";
			password = "";
		}

		if (port.isEmpty()) {
			port = protocolType.getDefaultPort();
		}

		return AbstractProtocoleFactory.getProtocol(url, port, login, password,
				protocolType);
	}
}
