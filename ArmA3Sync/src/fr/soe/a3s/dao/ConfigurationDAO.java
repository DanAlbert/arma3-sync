package fr.soe.a3s.dao;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import fr.soe.a3s.domain.configration.Configuration;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.WritingException;

public class ConfigurationDAO implements DataAccessConstants {

	private static Configuration configuration = new Configuration();
	private static final String REGQUERY_UTIL = "reg query ";
	private static final String REGSTR_TOKEN = "REG_SZ";

	public void read() throws LoadingException {

		try {
			File file = new File(CONFIGURATION_FILE_PATH);
			if (file.exists()) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(
								file.getAbsolutePath())));
				Configuration config = (Configuration) fRo.readObject();
				fRo.close();
				if (config != null) {
					configuration = config;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new LoadingException();
		}
	}

	public void write() throws WritingException {

		try {
			ObjectOutputStream fWo = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(
							CONFIGURATION_FILE_PATH)));
			fWo.writeObject(configuration);
			fWo.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new WritingException("Failded to write configuration.");
		}
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public String determineSteamPath() {

		String osName = System.getProperty("os.name");
		if (!osName.contains("Windows")) {
			return null;
		}
		
		String steamPath = null;

		try {

			Process process = Runtime.getRuntime().exec(
					REGQUERY_UTIL
							+ "\"HKLM\\SOFTWARE\\Wow6432Node\\Valve\\Steam");
			StreamReader reader = new StreamReader(process);

			reader.start();
			reader.join();

			String result = reader.getResult();

			String keyREG_SZ = REGSTR_TOKEN;

			int pkeyREG_SZ = result.indexOf("SteamPID");

			if (pkeyREG_SZ == -1) {
				return null;
			}

			result = result.substring(0, pkeyREG_SZ);
			int pkeyReg_BINARY = result.indexOf(REGSTR_TOKEN);
			result = result.substring(pkeyReg_BINARY + REGSTR_TOKEN.length(),
					result.length());

			steamPath = result.trim();

		} catch (Exception e) {
			e.printStackTrace();
			steamPath = null;
		}
		return steamPath;
	}

	public String determineArmA3Path() {

		String arma3Path = null;
		
		String osName = System.getProperty("os.name");
		if (!osName.contains("Windows")) {
			return null;
		}

		try {
			String QUERY = "\"HKLM\\SOFTWARE\\Wow6432Node\\Bohemia Interactive\\Arma 3\" /v MAIN";

			Process process = Runtime.getRuntime().exec(REGQUERY_UTIL + QUERY);
			StreamReader reader = new StreamReader(process);

			reader.start();
			reader.join();

			String result = reader.getResult();

			int pkeyREG_SZ = result.indexOf(REGSTR_TOKEN);

			if (pkeyREG_SZ == -1) {
				return null;
			}

			arma3Path = result.substring(pkeyREG_SZ + REGSTR_TOKEN.length())
					.trim();

		} catch (Exception e) {
			e.printStackTrace();
			arma3Path = null;
		}
		return arma3Path;
	}

	public String determineTS3path() {

		String ts3Path = null;
		
		String osName = System.getProperty("os.name");
		if (!osName.contains("Windows")) {
			return null;
		}

		try {
			String QUERY = "\"HKCU\\Software\\TeamSpeak 3 Client\" /ve";// default
																		// value
			Process process = Runtime.getRuntime().exec(REGQUERY_UTIL + QUERY);
			StreamReader reader = new StreamReader(process);

			reader.start();
			reader.join();

			String result = reader.getResult();

			int pkeyREG_SZ = result.indexOf(REGSTR_TOKEN);

			if (pkeyREG_SZ == -1) {
				return null;
			}

			ts3Path = result.substring(pkeyREG_SZ + REGSTR_TOKEN.length())
					.trim();

		} catch (Exception e) {
			e.printStackTrace();
			ts3Path = null;
		}
		return ts3Path;
	}

	public String determineTS3version(String ts3InstallationDirectoryPath) {

		assert (ts3InstallationDirectoryPath != null);
		String ts3Version = null;
		String changelogPath = ts3InstallationDirectoryPath + "\\"
				+ "changelog.txt";
		File file = new File(changelogPath);
		if (!file.exists()) {
			return null;
		} else {
			try {
				FileInputStream fin = new FileInputStream(file);
				byte[] buffer = new byte[(int) file.length()];
				new DataInputStream(fin).readFully(buffer);
				fin.close();
				String s = new String(buffer);
				String[] lines = s.split("\r\n|\r|\n");
				for (String line : lines) {
					if (line.toLowerCase().contains("client release")) {
						StringTokenizer stz = new StringTokenizer(line, " ");
						if (stz.countTokens() >= 4) {
							stz.nextToken();
							stz.nextToken();
							stz.nextToken();
							ts3Version = stz.nextToken();
							break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				ts3Version = null;
			}
		}
		return ts3Version;
	}

	public String determineArmA2Path() {

		String arma2Path = null;
		
		String osName = System.getProperty("os.name");
		if (!osName.contains("Windows")) {
			return null;
		}

		try {
			String QUERY = "\"HKLM\\SOFTWARE\\Wow6432Node\\Bohemia Interactive Studio\\ArmA 2\" /v MAIN";

			Process process = Runtime.getRuntime().exec(REGQUERY_UTIL + QUERY);
			StreamReader reader = new StreamReader(process);

			reader.start();
			reader.join();

			String result = reader.getResult();

			int pkeyREG_SZ = result.indexOf(REGSTR_TOKEN);

			if (pkeyREG_SZ == -1) {
				return null;
			}

			arma2Path = result.substring(pkeyREG_SZ + REGSTR_TOKEN.length())
					.trim();

		} catch (Exception e) {
			e.printStackTrace();
			arma2Path = null;
		}
		return arma2Path;
	}

	public String determineArmA2OAPath() {

		String arma2OAPath = null;
		
		String osName = System.getProperty("os.name");
		if (!osName.contains("Windows")) {
			return null;
		}

		try {
			String QUERY = "\"HKLM\\SOFTWARE\\Wow6432Node\\Bohemia Interactive Studio\\ArmA 2 OA\" /v MAIN";

			Process process = Runtime.getRuntime().exec(REGQUERY_UTIL + QUERY);
			StreamReader reader = new StreamReader(process);

			reader.start();
			reader.join();

			String result = reader.getResult();

			int pkeyREG_SZ = result.indexOf(REGSTR_TOKEN);

			if (pkeyREG_SZ == -1) {
				return null;
			}

			arma2OAPath = result.substring(pkeyREG_SZ + REGSTR_TOKEN.length())
					.trim();

		} catch (Exception e) {
			e.printStackTrace();
			arma2OAPath = null;
		}
		return arma2OAPath;
	}

	public String determineArmAPath() {

		String armaPath = null;
		
		String osName = System.getProperty("os.name");
		if (!osName.contains("Windows")) {
			return null;
		}

		try {
			String QUERY = "\"HKLM\\SOFTWARE\\Wow6432Node\\Bohemia Interactive Studio\\ArmA\" /v MAIN";

			Process process = Runtime.getRuntime().exec(REGQUERY_UTIL + QUERY);
			StreamReader reader = new StreamReader(process);

			reader.start();
			reader.join();

			String result = reader.getResult();

			int pkeyREG_SZ = result.indexOf(REGSTR_TOKEN);

			if (pkeyREG_SZ == -1) {
				return null;
			}

			armaPath = result.substring(pkeyREG_SZ + REGSTR_TOKEN.length())
					.trim();

		} catch (Exception e) {
			e.printStackTrace();
			armaPath = null;
		}
		return armaPath;
	}

	public String determineTOHPath() {

		String tohPath = null;
		
		String osName = System.getProperty("os.name");
		if (!osName.contains("Windows")) {
			return null;
		}

		try {
			String QUERY = "\"HKLM\\SOFTWARE\\Wow6432Node\\Bohemia Interactive Studio\\Take On Helicopters\" /v MAIN";

			Process process = Runtime.getRuntime().exec(REGQUERY_UTIL + QUERY);
			StreamReader reader = new StreamReader(process);

			reader.start();
			reader.join();

			String result = reader.getResult();

			int pkeyREG_SZ = result.indexOf(REGSTR_TOKEN);

			if (pkeyREG_SZ == -1) {
				return null;
			}

			tohPath = result.substring(pkeyREG_SZ + REGSTR_TOKEN.length())
					.trim();

		} catch (Exception e) {
			e.printStackTrace();
			tohPath = null;
		}
		return tohPath;
	}

	public String determineRptPath() {
		
		String arma3RPTfolderPath = null;
		
		String osName = System.getProperty("os.name");
		if (osName.contains("Windows")) {
			String appDataFolderPath = System.getenv("APPDATA");// AppDATA\Roaming
			if (appDataFolderPath != null) {
				arma3RPTfolderPath = new File(appDataFolderPath)
						.getParentFile().getAbsolutePath()
						+ "\\Local\\Arma 3 Alpha";
			}
		}
		return arma3RPTfolderPath;
	}
}
