package fr.soe.a3s.dao;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import fr.soe.a3s.constant.Protocole;
import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.Changelogs;
import fr.soe.a3s.domain.repository.Events;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.HttpException;
import fr.soe.a3s.exception.JazsyncException;
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

	private void connect(Repository repository,
			String relativePathFromRepository, int timeout) throws IOException {

		String url = repository.getProtocole().getUrl();
		String hostname = url;
		String remotePath = "";
		int index = url.indexOf("/");
		if (index != -1) {
			hostname = url.substring(0, index);
			remotePath = url.substring(index);
		}

		remotePath = remotePath + relativePathFromRepository;
		String port = repository.getProtocole().getPort();
		String login = repository.getProtocole().getLogin();
		String password = repository.getProtocole().getPassword();

		URL urlObject = null;

		urlObject = new URL("http", hostname, Integer.parseInt(port),
				remotePath);
		httpURLConnection = (HttpURLConnection) urlObject.openConnection();
		httpURLConnection.setConnectTimeout(timeout);

		if (!(login.equalsIgnoreCase("anonymous"))) {
			String encoding = Base64Coder.encodeLines((login + ":" + password)
					.getBytes());
			httpURLConnection.setRequestProperty("Authorization", "Basic "
					+ encoding.substring(0, encoding.length() - 1));
		}
	}

	public void connectToRepository(Repository repository) throws HttpException {

		try {
			connect(repository, "", 5000);
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				System.out.println("Connection to " + repository.getName()
						+ " success.");
			} else {
				String message = "Failed to connect to repository " + "\""
						+ repository.getName() + "\""
						+ ".\nServer return error " + responseCode + " on url "
						+ repository.getProtocole().getUrl()
						+ ".\n You may need to check login and password.";
				System.out.println(message);
				throw new HttpException(message);
			}
		} catch (IOException e) {
			String message = "Failed to connect to repository " + "\""
					+ repository.getName() + "\"" + ".";
			throw new HttpException(message);
		}
	}

	private void download(File file) throws IOException {

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

	public AutoConfig downloadAutoConfig(String url) throws WritingException,
			HttpException {

		if (url == null) {
			return null;
		}

		int responseCode = HttpURLConnection.HTTP_OK;
		try {
			URL urlObject = new URL(url);
			httpURLConnection = (HttpURLConnection) urlObject.openConnection();
			httpURLConnection.setConnectTimeout(5000);
			responseCode = httpURLConnection.getResponseCode();
		} catch (IOException e) {
			throw new HttpException("Connection failed.");
		}

		if (responseCode == HttpURLConnection.HTTP_OK) {
			System.out.println("Connection ok on url: " + url);
		} else {
			System.out.println("Connection ko on url: " + url);
			throw new HttpException("Connection failed.");
		}

		AutoConfig autoConfig = null;
		try {
			File file = new File(TEMP_FOLDER_PATH + "/"
					+ DataAccessConstants.AUTOCONFIG);
			download(file);
			ObjectInputStream fRo = new ObjectInputStream(new GZIPInputStream(
					new FileInputStream(file)));
			autoConfig = (AutoConfig) fRo.readObject();
			fRo.close();
			FileAccessMethods.deleteFile(file);
		} catch (Exception e) {
			e.printStackTrace();
			autoConfig = null;
			throw new WritingException(e.getMessage());
		}
		return autoConfig;
	}

	public ServerInfo downloadSeverInfo(Repository repository) {

		ServerInfo serverInfo = null;
		try {
			connect(repository, SERVERINFO_FILE_PATH, 5000);
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				File directory = new File(TEMP_FOLDER_PATH + "/"
						+ repository.getName());
				directory.mkdir();
				File file = new File(directory + "/"
						+ DataAccessConstants.SERVERINFO);
				download(file);
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				serverInfo = (ServerInfo) fRo.readObject();
				fRo.close();
				FileAccessMethods.deleteFile(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			serverInfo = null;
		}
		return serverInfo;
	}

	public Changelogs downloadChangelog(Repository repository) {

		Changelogs changelogs = null;
		try {
			connect(repository, CHANGELOG_FILE_PATH, 5000);
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				File directory = new File(TEMP_FOLDER_PATH + "/"
						+ repository.getName());
				directory.mkdir();
				File file = new File(directory + "/"
						+ DataAccessConstants.CHANGELOGS);
				download(file);
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				changelogs = (Changelogs) fRo.readObject();
				fRo.close();
				FileAccessMethods.deleteFile(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			changelogs = null;
		}
		return changelogs;
	}

	public Events downloadEvent(Repository repository) {

		Events events = null;
		try {
			connect(repository, EVENTS_FILE_PATH, 5000);
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				File directory = new File(TEMP_FOLDER_PATH + "/"
						+ repository.getName());
				directory.mkdir();
				File file = new File(directory + "/"
						+ DataAccessConstants.EVENTS);
				download(file);
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				events = (Events) fRo.readObject();
				fRo.close();
				FileAccessMethods.deleteFile(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			events = null;
		}
		return events;
	}

	public SyncTreeDirectory downloadSync(Repository repository) {

		SyncTreeDirectory syncTreeDirectory = null;
		try {
			connect(repository, SYNC_FILE_PATH, 5000);
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				File directory = new File(TEMP_FOLDER_PATH + "/"
						+ repository.getName());
				directory.mkdir();
				File file = new File(directory + "/" + DataAccessConstants.SYNC);
				download(file);
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				syncTreeDirectory = (SyncTreeDirectory) fRo.readObject();
				fRo.close();
				FileAccessMethods.deleteFile(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			syncTreeDirectory = null;
		}
		return syncTreeDirectory;
	}

	public void downloadAddon(String hostname, String login, String password,
			String port, String remotePath, String destinationPath,
			SyncTreeNodeDTO node, boolean resume) throws WritingException,
			JazsyncException {

		File parentDirectory = new File(destinationPath);
		parentDirectory.mkdirs();
		this.downloadingFile = new File(parentDirectory + "/" + node.getName());

		if (node.isLeaf()) {
			String relativeZsyncFileUrl = remotePath + node.getName()
					+ ZSYNC_EXTENSION;
			String relativeFileUrl = remotePath + node.getName();
			String zsyncFileUrl = Protocole.HTTP.getPrompt() + hostname
					+ relativeZsyncFileUrl;
			String fileUrl = Protocole.HTTP.getPrompt() + hostname
					+ relativeFileUrl;

			SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
			String sha1 = leaf.getLocalSHA1();

			Jazsync.sync(this.downloadingFile, sha1,relativeFileUrl,
					relativeZsyncFileUrl, hostname, login, password, port,
					resume, this);
		} else {
			this.downloadingFile.mkdir();
		}
		countFilesNumber++;
		updateFilesNumberObserver();
	}

	public void getFileCompletion(String hostname, String login,
			String password, String port, String remotePath,
			String destinationPath, SyncTreeNodeDTO node)
			throws WritingException, JazsyncException {

		if (node.isLeaf()) {
			File targetFile = new File(destinationPath + "/" + node.getName());

			String relativeZsyncFileUrl = remotePath + node.getName()
					+ ZSYNC_EXTENSION;
			String zsyncFileUrl = Protocole.HTTP.getPrompt() + hostname
					+ relativeZsyncFileUrl;

			SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
			String sha1 = leaf.getLocalSHA1();

			double complete = Jazsync
					.getCompletion(targetFile, sha1, relativeZsyncFileUrl,
							hostname, login, password, port, this);

			leaf.setComplete(complete);
		}
	}

	public boolean uploadEvents(Repository repository) throws HttpException {

		boolean response = true;

		if (repository.getEvents() != null) {
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

				connect(repository, EVENTS_FILE_PATH, 5000);

				String attachmentName = "events";
				String attachmentFileName = "events";
				String crlf = "\r\n";
				String twoHyphens = "--";
				String boundary = "*****";

				httpURLConnection.setUseCaches(false);
				httpURLConnection.setDoOutput(true);

				httpURLConnection.setRequestMethod("POST");
				httpURLConnection
						.setRequestProperty("Connection", "Keep-Alive");
				httpURLConnection.setRequestProperty("Cache-Control",
						"no-cache");
				httpURLConnection.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);

				DataOutputStream request = new DataOutputStream(
						httpURLConnection.getOutputStream());

				request.writeBytes(twoHyphens + boundary + crlf);
				request.writeBytes("Content-Disposition: form-data; name=\""
						+ attachmentName + "\";filename=\""
						+ attachmentFileName + "\"" + crlf);
				request.writeBytes(crlf);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(
						new GZIPOutputStream(baos));
				oos.writeObject(repository.getEvents());
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
		} else {
			response = false;
		}
		return false;
	}

	public void stopDownload(boolean resumable) {
		canceled = true;
		disconnect();
		if (!resumable) {
			FileAccessMethods.deleteFile(downloadingFile);
		}
	}

	public void disconnect() {
		if (httpURLConnection != null) {
			httpURLConnection.disconnect();
			httpURLConnection.setRequestProperty("Connection", "close");
		}
	}

	public void seConnexion(HttpURLConnection httpURLConnection) {
		this.httpURLConnection = httpURLConnection;
	}
}
