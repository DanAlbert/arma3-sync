package fr.soe.a3s.dao;

import fr.soe.a3s.constant.Protocol;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.Ftp;
import fr.soe.a3s.domain.Http;

public class AutoConfigURLAccessMethods implements DataAccessConstants {

	/**
	 * Determine autoconfigUrl from repository protocole
	 * 
	 * @param protocole
	 *            not null
	 * @return string autoconfig url
	 */
	public static String determineAutoConfigUrl(AbstractProtocole protocole) {

		String repositoryUrl = protocole.getUrl();
		String port = protocole.getPort();
		String login = protocole.getLogin();
		String password = protocole.getPassword();

		if (login.equalsIgnoreCase("anonymous")) {
			login = "";
			password = "";
		}

		if (protocole instanceof Http) {
			if (port.equals("80")) {
				port = "";
			}
		} else if (protocole instanceof Ftp) {
			if (protocole.getPort().equals("21")) {
				port = "";
			}
		}

		String autoConfigURL = repositoryUrl;
		if (!port.isEmpty()) {
			autoConfigURL = autoConfigURL + ":" + port;
		}

		autoConfigURL = autoConfigURL + AUTOCONFIG_FILE_PATH;

		return autoConfigURL;
	}

	public static AbstractProtocole parse(String autoConfigURL,
			Protocol protocol) {

		String url = "";
		String port = "";
		String login = "";
		String password = "";

		String hostname = "";
		String relativePath = "";

		if ( autoConfigURL.toLowerCase().trim().contains(protocol.getPrompt())){
			autoConfigURL = autoConfigURL.substring(protocol.getPrompt().length());
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

		if (protocol.equals(Protocol.FTP)) {
			if (port.isEmpty()) {
				port = "21";
			}
			return new Ftp(url, port, login, password);
		} else if (protocol.equals(Protocol.HTTP)) {
			if (port.isEmpty()) {
				port = "80";
			}
			return new Http(url, port, login, password);
		} else {
			return null;
		}
	}

	private String getUrlWithPort(String url, String port) {

		int index = url.indexOf("/");
		if (index != -1) {
			String hostname = url.substring(0, index);
			String urlWithPort = hostname + ":" + port + url.substring(index);
			return urlWithPort;
		} else {
			String urlWithPort = url + ":" + port;
			return urlWithPort;
		}
	}
}
