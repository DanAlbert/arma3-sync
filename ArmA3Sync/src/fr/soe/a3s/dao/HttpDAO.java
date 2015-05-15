package fr.soe.a3s.dao;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import fr.soe.a3s.constant.DownloadStatus;
import fr.soe.a3s.constant.Protocol;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.Changelogs;
import fr.soe.a3s.domain.repository.Events;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.HttpException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.jazsync.Base64Coder;
import fr.soe.a3s.jazsync.Jazsync;

public class HttpDAO extends AbstractConnexionDAO {

	/*
	 * http://www.codejava.net/java-se/networking/use-httpurlconnection-to-download
	 * -file-from-an-http-url
	 */

	private HttpURLConnection httpURLConnection;
	private static final int BUFFER_SIZE = 4096;
	private File downloadingFile;
	private boolean acquiredSmaphore;

	private void connect(AbstractProtocole protocole,
			String relativePathFromRepository) throws IOException {

		String url = protocole.getUrl();
		String hostname = protocole.getHostname();
		String remotePath = protocole.getRemotePath();// the repository relative
														// url

		if (relativePathFromRepository != null) {
			remotePath = remotePath + relativePathFromRepository;
		}

		String port = protocole.getPort();
		String login = protocole.getLogin();
		String password = protocole.getPassword();

		URL urlObject = new URL("http", hostname, Integer.parseInt(port),
				remotePath);
		httpURLConnection = (HttpURLConnection) urlObject.openConnection();
		httpURLConnection.setConnectTimeout(Integer.parseInt(protocole
				.getConnectionTimeOut()));
		httpURLConnection.setReadTimeout(Integer.parseInt(protocole
				.getReadTimeOut()));

		if (!(login.equalsIgnoreCase("anonymous"))) {
			String encoding = Base64Coder.encodeLines((login + ":" + password)
					.getBytes());
			httpURLConnection.setRequestProperty("Authorization", "Basic "
					+ encoding.substring(0, encoding.length() - 1));
		}
	}

	private void downloadFile(File file) throws IOException {

		// opens input stream from the HTTP connection
		InputStream inputStream = httpURLConnection.getInputStream();

		// opens an output stream to save into file
		FileOutputStream outputStream = new FileOutputStream(file);

		int bytesRead = -1;
		byte[] buffer = new byte[BUFFER_SIZE];
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		outputStream.close();
		inputStream.close();
	}

	public ServerInfo downloadSeverInfo(String repositoryName,
			AbstractProtocole protocole) throws HttpException,
			WritingException, ConnectException {

		ServerInfo serverInfo = null;
		try {
			connect(protocole, SERVERINFO_FILE_PATH);
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				String message = "Server return error " + responseCode
						+ " on url:" + "\n" + "http://" + protocole.getUrl()
						+ SERVERINFO_FILE_PATH;
				System.out.println(message);
				throw new HttpException(message);
			}
		} catch (IOException e) {// happens if repository url is wrong
			String message = "Failed to connect to repository "
					+ repositoryName + " on url " + "http://"
					+ protocole.getUrl();
			System.out.println(message);
			throw new ConnectException(message);
		}

		try {
			File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
			directory.mkdir();
			File file = new File(directory + "/"
					+ DataAccessConstants.SERVERINFO);
			downloadFile(file);
			if (file.exists()) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				serverInfo = (ServerInfo) fRo.readObject();
				fRo.close();
			}
			FileAccessMethods.deleteDirectory(directory);
		} catch (Exception e) {
			e.printStackTrace();
			serverInfo = null;
			throw new WritingException(
					"Failded to read the downloaded file Serverinfo." + "\n"
							+ e.getMessage());
		} finally {
			disconnect();
		}
		return serverInfo;
	}

	public Changelogs downloadChangelogs(String repositoryName,
			AbstractProtocole protocole) throws HttpException,
			WritingException, ConnectException {

		Changelogs changelogs = null;
		try {
			connect(protocole, CHANGELOGS_FILE_PATH);
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				String message = "Server return HTTP error " + responseCode
						+ " on url:" + "\n" + "http://" + protocole.getUrl()
						+ CHANGELOGS_FILE_PATH;
				System.out.println(message);
				throw new HttpException(message);
			}
		} catch (IOException e) {// happens if repository url is wrong
			String message = "Failed to connect to repository "
					+ repositoryName + " on url " + "http://"
					+ protocole.getUrl();
			System.out.println(message);
			throw new ConnectException(message);
		}

		try {
			File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
			directory.mkdir();
			File file = new File(directory + "/"
					+ DataAccessConstants.CHANGELOGS);
			downloadFile(file);
			if (file.exists()) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				changelogs = (Changelogs) fRo.readObject();
				fRo.close();
			}
			FileAccessMethods.deleteDirectory(directory);
		} catch (Exception e) {
			e.printStackTrace();
			changelogs = null;
			throw new WritingException(
					"Failded to read the downloaded file Changelogs." + "\n"
							+ e.getMessage());
		} finally {
			disconnect();
		}
		return changelogs;
	}

	public Events downloadEvent(String repositoryName,
			AbstractProtocole protocole) throws HttpException,
			WritingException, ConnectException {

		Events events = null;
		try {
			connect(protocole, EVENTS_FILE_PATH);
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				String message = "Server return HTTP error " + responseCode
						+ " on url:" + "\n" + "http://" + protocole.getUrl()
						+ EVENTS_FILE_PATH;
				System.out.println(message);
				throw new HttpException(message);
			}
		} catch (IOException e) {// happens if repository url is wrong
			String message = "Failed to connect to repository "
					+ repositoryName + " on url " + "http://"
					+ protocole.getUrl();
			System.out.println(message);
			throw new ConnectException(message);
		}

		try {
			File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
			directory.mkdir();
			File file = new File(directory + "/" + DataAccessConstants.EVENTS);
			downloadFile(file);
			if (file.exists()) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				events = (Events) fRo.readObject();
				fRo.close();
			}
			FileAccessMethods.deleteDirectory(directory);
		} catch (Exception e) {
			e.printStackTrace();
			events = null;
			throw new WritingException(
					"Failded to read the downloaded file Events." + "\n"
							+ e.getMessage());
		} finally {
			disconnect();
		}
		return events;
	}

	public SyncTreeDirectory downloadSync(String repositoryName,
			AbstractProtocole protocole) throws HttpException,
			WritingException, ConnectException {

		SyncTreeDirectory syncTreeDirectory = null;
		try {
			connect(protocole, SYNC_FILE_PATH);
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				String message = "Server return HTTP error " + responseCode
						+ " on url:" + "\n" + "http://" + protocole.getUrl()
						+ SYNC_FILE_PATH;
				System.out.println(message);
				throw new HttpException(message);
			}
		} catch (IOException e) {// happens if repository url is wrong
			String message = "Failed to connect to repository "
					+ repositoryName + " on url " + "http://"
					+ protocole.getUrl();
			System.out.println(message);
			throw new ConnectException(message);
		}

		try {
			File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
			directory.mkdir();
			File file = new File(directory + "/" + DataAccessConstants.SYNC);
			downloadFile(file);
			if (file.exists()) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				syncTreeDirectory = (SyncTreeDirectory) fRo.readObject();
				fRo.close();
			}
			FileAccessMethods.deleteDirectory(directory);
		} catch (Exception e) {
			e.printStackTrace();
			syncTreeDirectory = null;
			throw new WritingException(
					"Failded to read the downloaded file Sync." + "\n"
							+ e.getMessage());
		} finally {
			disconnect();
		}
		return syncTreeDirectory;
	}

	public AutoConfig downloadAutoconfig(String repositoryName,
			AbstractProtocole protocole) throws HttpException,
			ConnectException, WritingException {

		AutoConfig autoConfig = null;
		try {
			connect(protocole, AUTOCONFIG_FILE_PATH);
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				String message = "Server return HTTP error " + responseCode
						+ " on url:" + "\n" + "http://" + protocole.getUrl()
						+ AUTOCONFIG_FILE_PATH;
				System.out.println(message);
				throw new HttpException(message);
			}
		} catch (IOException e) {// happens if repository url is wrong
			String message = "Failed to connect to repository "
					+ repositoryName + " on url " + "http://"
					+ protocole.getUrl();
			System.out.println(message);
			throw new ConnectException(message);
		}

		try {
			File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
			directory.mkdir();
			File file = new File(directory + "/"
					+ DataAccessConstants.AUTOCONFIG);
			downloadFile(file);
			if (file.exists()) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				autoConfig = (AutoConfig) fRo.readObject();
				fRo.close();
			}
			FileAccessMethods.deleteDirectory(directory);
		} catch (Exception e) {
			e.printStackTrace();
			autoConfig = null;
			throw new WritingException(
					"Failded to read the downloaded file Autoconfig." + "\n"
							+ e.getMessage());
		} finally {
			disconnect();
		}
		return autoConfig;
	}

	public AutoConfig importAutoConfig(String autoConfigURL)
			throws WritingException, HttpException, ConnectException {

		if (autoConfigURL == null) {
			return null;
		}

		AbstractProtocole protocole = AutoConfigURLAccessMethods.parse(
				autoConfigURL, Protocol.HTTP);

		if (protocole == null) {
			return null;
		}

		try {
			connect(protocole, "/" + DataAccessConstants.AUTOCONFIG);
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				throw new HttpException("Connection failed.");
			}
		} catch (IOException e) {// happens if repository url is wrong
			throw new ConnectException("Connection failed.");
		}

		AutoConfig autoConfig = null;
		try {
			File file = new File(TEMP_FOLDER_PATH + "/"
					+ DataAccessConstants.AUTOCONFIG);
			downloadFile(file);
			if (file.exists()) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				autoConfig = (AutoConfig) fRo.readObject();
				fRo.close();
				FileAccessMethods.deleteFile(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			autoConfig = null;
			throw new WritingException(
					"Failded to read the downloaded file Autoconfig." + "\n"
							+ e.getMessage());
		} finally {
			disconnect();
		}
		return autoConfig;
	}

	public void downloadFile(String hostname, String login, String password,
			String port, String remotePath, String destinationPath,
			SyncTreeNodeDTO node, String connectionTimeOut, String readTimeOut)
			throws Exception, FileNotFoundException {

		this.downloadingNode = node;
		File parentDirectory = new File(destinationPath);
		parentDirectory.mkdirs();
		this.downloadingFile = new File(parentDirectory + "/" + node.getName());

		if (node.isLeaf()) {
			// ZSync file
			String relativeZsyncFileUrl = remotePath + node.getName()
					+ ZSYNC_EXTENSION;
			String relativeFileUrl = remotePath + node.getName();
			String zsyncFileUrl = Protocol.HTTP.getPrompt() + hostname
					+ relativeZsyncFileUrl;
			String fileUrl = Protocol.HTTP.getPrompt() + hostname
					+ relativeFileUrl;

			SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
			String sha1 = leaf.getLocalSHA1();

			Jazsync.sync(this.downloadingFile, sha1, relativeFileUrl,
					relativeZsyncFileUrl, hostname, login, password, port,
					connectionTimeOut, readTimeOut, this);
		} else {
			this.downloadingFile.mkdir();
		}

		updateFilesNumberObserver();
		this.downloadingFile = null;
		this.downloadingNode = null;
		node.setDownloadStatus(DownloadStatus.DONE);
	}

	public double getFileCompletion(String hostname, String login,
			String password, String port, String remotePath,
			String destinationPath, SyncTreeNodeDTO node,
			String connectionTimeOut, String readTimeOut) throws Exception {

		File targetFile = new File(destinationPath + "/" + node.getName());

		String relativeZsyncFileUrl = remotePath + node.getName()
				+ ZSYNC_EXTENSION;
		String zsyncFileUrl = Protocol.HTTP.getPrompt() + hostname
				+ relativeZsyncFileUrl;

		SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
		String sha1 = leaf.getLocalSHA1();

		double complete = Jazsync.getCompletion(targetFile, sha1,
				relativeZsyncFileUrl, hostname, login, password, port,
				connectionTimeOut, readTimeOut, this);

		this.nbFiles++;
		updateFilesNumberObserver3();
		return complete;
	}

	public boolean uploadEvents(Events events, String repositoryName,
			AbstractProtocole protocole) throws HttpException {

		boolean response = true;

		try {
			// // set some connection properties
			// String param = "value";
			// String charset = "UTF-8";
			// String CRLF = "\r\n";
			// String boundary =
			// Long.toHexString(System.currentTimeMillis()); // Just
			// // generate some unique random value.
			// httpURLConnection.setRequestProperty("Content-Type",
			// "multipart/form-data; boundary=" + boundary);
			// httpURLConnection.setDoOutput(true);
			// OutputStream output = httpURLConnection.getOutputStream();
			// PrintWriter writer = new PrintWriter(new OutputStreamWriter(
			// output, charset), true);
			//
			// // Send normal param.
			// writer.append("--" + boundary).append(CRLF);
			// writer.append("Content-Disposition: form-data; name=\"param\"")
			// .append(CRLF);
			// writer.append("Content-Type: text/plain; charset=" + charset)
			// .append(CRLF);
			// writer.append(CRLF);
			// writer.append(param).append(CRLF).flush();
			//
			// ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// ObjectOutputStream oos = new ObjectOutputStream(
			// new GZIPOutputStream(baos));
			// oos.writeObject(repository.getEvents());
			// oos.flush();
			// oos.close();
			// InputStream uis = new
			// ByteArrayInputStream(baos.toByteArray());
			//
			// byte[] buffer = new byte[4096];
			// int length;
			// while ((length = uis.read(buffer)) > 0) {
			// output.write(buffer, 0, length);
			// }
			// output.flush();
			// writer.append(CRLF).flush();
			// writer.append("--" + boundary + "--").append(CRLF).flush();

			connect(protocole, A3S_FOlDER_PATH);

			String attachmentName = "events";
			String attachmentFileName = "events";
			String crlf = "\r\n";
			String twoHyphens = "--";
			String boundary = "*****";

			httpURLConnection.setUseCaches(false);
			httpURLConnection.setDoOutput(true);

			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Cache-Control", "no-cache");
			httpURLConnection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			DataOutputStream request = new DataOutputStream(
					httpURLConnection.getOutputStream());

			request.writeBytes(twoHyphens + boundary + crlf);
			request.writeBytes("Content-Disposition: form-data; name=\""
					+ attachmentName + "\";filename=\"" + attachmentFileName
					+ "\"" + crlf);
			request.writeBytes(crlf);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(
					new GZIPOutputStream(baos));
			oos.writeObject(events);
			oos.flush();
			oos.close();
			InputStream uis = new ByteArrayInputStream(baos.toByteArray());

			byte[] buffer = new byte[4096];
			int length;
			while ((length = uis.read(buffer)) > 0) {
				request.write(buffer, 0, length);
			}

			request.writeBytes(crlf);
			request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
			request.flush();
			request.close();

			InputStream responseStream = new BufferedInputStream(
					httpURLConnection.getInputStream());

			BufferedReader responseStreamReader = new BufferedReader(
					new InputStreamReader(responseStream));
			String line = "";
			StringBuilder stringBuilder = new StringBuilder();
			while ((line = responseStreamReader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
			responseStreamReader.close();

			String res = stringBuilder.toString();
			System.out.println(res);

		} catch (IOException e) {
			e.printStackTrace();
			response = false;
		}
		return false;
	}

	@Override
	public void cancel(boolean resumable) {
		canceled = true;
		// disconnect();
		if (!resumable && downloadingFile != null) {
			FileAccessMethods.deleteFile(downloadingFile);
		}
		downloadingNode = null;
	}

	public void disconnect() {
		if (httpURLConnection != null) {
			try {
				httpURLConnection.disconnect();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public void seConnexion(HttpURLConnection httpURLConnection) {
		this.httpURLConnection = httpURLConnection;
	}

	public boolean isAcquiredSmaphore() {
		return acquiredSmaphore;
	}

	public void setAcquiredSmaphore(boolean acquiredSmaphore) {
		this.acquiredSmaphore = acquiredSmaphore;
	}
}
