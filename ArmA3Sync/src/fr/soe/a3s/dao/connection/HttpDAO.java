package fr.soe.a3s.dao.connection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;

import fr.soe.a3s.constant.DownloadStatus;
import fr.soe.a3s.dao.A3SFilesAccessor;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.FileAccessMethods;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.Changelogs;
import fr.soe.a3s.domain.repository.Events;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.HttpException;
import fr.soe.a3s.jazsync.Jazsync;

public class HttpDAO extends AbstractConnexionDAO {

	/*
	 * http://www.codejava.net/java-se/networking/use-httpurlconnection-to-download
	 * -file-from-an-http-url
	 */
	private MyHttpConnection myHttpConnection;

	private void connect(AbstractProtocole protocole,
			String relativePathFromRepository) throws IOException {

		// Determine the full relativeUrl
		String remotePath = protocole.getRemotePath();
		if (relativePathFromRepository != null) {
			remotePath = remotePath + relativePathFromRepository;
		}

		// open connection
		myHttpConnection = new MyHttpConnection(protocole, this);
		myHttpConnection.openConnection(remotePath);
	}

	public void connectToRepository(AbstractProtocole protocole,
			String relativePathFromRepository) throws IOException {

		boolean found = true;
		try {
			connect(protocole, relativePathFromRepository);
		} catch (IOException e) {
			if (!canceled) {
				String coreMessage = "Failed to connect to repository on url: "
						+ "\n" + protocole.getProtocolType().getPrompt()
						+ protocole.getUrl() + relativePathFromRepository;
				IOException ioe = transferIOExceptionFactory(coreMessage, e);
				throw ioe;
			}
		}
	}

	private void downloadFile(File file, String relativePath)
			throws IOException {

		try {
			myHttpConnection.downloadFile(file);
		} catch (IOException e) {
			if (!canceled) {
				String coreMessage = "Failed to retreive file " + relativePath;
				IOException ioe = transferIOExceptionFactory(coreMessage, e);
				throw ioe;
			}
		} catch (HttpException e) {
			if (!canceled) {
				String message = "Server returned message " + e.getMessage()
						+ " on url:" + "\n" + relativePath;
				throw new ConnectException(message);
			}
		}
	}

	private void downloadFileWithRecordProgress(File file, String relativePath)
			throws IOException {

		try {
			myHttpConnection.downloadFileWithRecordProgress(file);
		} catch (IOException e) {
			String coreMessage = "Failed to retreive file " + relativePath;
			IOException ioe = transferIOExceptionFactory(coreMessage, e);
			throw ioe;
		} catch (HttpException e) {
			String message = "Server returned message " + e.getMessage()
					+ " on url:" + "\n" + relativePath;
			throw new ConnectException(message);
		}
	}

	private void downloadPartialFileWithRecordProgress(File file, String sha1,
			String relativeFileUrl, String relativeZsyncFileUrl,
			AbstractProtocole protocole) throws IOException {

		try {
			Jazsync.sync(file, sha1, relativeFileUrl, relativeZsyncFileUrl,
					protocole, this);
		} catch (IOException e) {
			String coreMessage = "Failed to retreive file " + relativeFileUrl;
			IOException ioe = transferIOExceptionFactory(coreMessage, e);
			throw ioe;
		} catch (HttpException e) {
			String message = "Server returned message " + e.getMessage()
					+ " on url:" + "\n" + relativeFileUrl;
			throw new ConnectException(message);
		}
	}

	public SyncTreeDirectory downloadSync(Repository repository)
			throws IOException {

		SyncTreeDirectory sync = null;
		File directory = new File(TEMP_FOLDER_PATH + "/" + repository.getName());
		File file = new File(directory + "/" + DataAccessConstants.SYNC);

		try {
			directory.mkdir();
			if (!directory.exists()) {
				throw new IOException("Failed to write file: "
						+ directory.getAbsolutePath() + "\n"
						+ "Permission dinied.");
			}
			connectToRepository(repository.getProtocol(), SYNC_FILE_PATH);
			boolean found = true;
			try {
				downloadFile(file, SYNC_FILE_PATH);
			} catch (FileNotFoundException e) {
				found = false;
			}
			if (found) {
				if (file.exists()) {
					sync = A3SFilesAccessor.readSyncFile(file);
				} else {
					throw new IOException("Failed to write file: "
							+ file.getAbsolutePath() + "\n"
							+ "Permission dinied.");
				}
			}
		} finally {
			FileAccessMethods.deleteDirectory(directory);
		}
		return sync;
	}

	public ServerInfo downloadSeverInfo(Repository repository)
			throws IOException {

		ServerInfo serverInfo = null;
		File directory = new File(TEMP_FOLDER_PATH + "/" + repository.getName());
		File file = new File(directory + "/" + DataAccessConstants.SERVERINFO);

		try {
			directory.mkdir();
			if (!directory.exists()) {
				throw new IOException("Failed to write file: "
						+ directory.getAbsolutePath() + "\n"
						+ "Permission dinied.");
			}
			connectToRepository(repository.getProtocol(), SERVERINFO_FILE_PATH);
			boolean found = true;
			try {
				downloadFile(file, SERVERINFO_FILE_PATH);
			} catch (FileNotFoundException e) {
				found = false;
			}
			if (found) {
				if (file.exists()) {
					serverInfo = A3SFilesAccessor.readServerInfoFile(file);
				} else {
					throw new IOException("Failed to write file: "
							+ file.getAbsolutePath() + "\n"
							+ "Permission dinied.");
				}
			}
		} finally {
			FileAccessMethods.deleteDirectory(directory);
		}
		return serverInfo;
	}

	public Changelogs downloadChangelogs(Repository repository)
			throws IOException {

		Changelogs changelogs = null;
		File directory = new File(TEMP_FOLDER_PATH + "/" + repository.getName());
		File file = new File(directory + "/" + DataAccessConstants.CHANGELOGS);

		try {
			directory.mkdir();
			if (!directory.exists()) {
				throw new IOException("Failed to write file: "
						+ directory.getAbsolutePath() + "\n"
						+ "Permission dinied.");
			}
			connectToRepository(repository.getProtocol(), CHANGELOGS_FILE_PATH);
			boolean found = true;
			try {
				downloadFile(file, CHANGELOGS_FILE_PATH);
			} catch (FileNotFoundException e) {
				found = false;
			}
			if (found) {
				if (file.exists()) {
					changelogs = A3SFilesAccessor.readChangelogsFile(file);
				} else {
					throw new IOException("Failed to write file: "
							+ file.getAbsolutePath() + "\n"
							+ "Permission dinied.");
				}
			}
		} finally {
			FileAccessMethods.deleteDirectory(directory);
		}
		return changelogs;
	}

	public Events downloadEvents(Repository repository) throws IOException {

		Events events = null;
		File directory = new File(TEMP_FOLDER_PATH + "/" + repository.getName());
		File file = new File(directory + "/" + DataAccessConstants.EVENTS);

		try {
			directory.mkdir();
			if (!directory.exists()) {
				throw new IOException("Failed to write file: "
						+ directory.getAbsolutePath() + "\n"
						+ "Permission dinied.");
			}
			connectToRepository(repository.getProtocol(), EVENTS_FILE_PATH);
			boolean found = true;
			try {
				downloadFile(file, EVENTS_FILE_PATH);
			} catch (FileNotFoundException e) {
				found = false;
			}
			if (found) {
				if (file.exists()) {
					events = A3SFilesAccessor.readEventsFile(file);
				} else {
					throw new IOException("Failed to write file: "
							+ file.getAbsolutePath() + "\n"
							+ "Permission dinied.");
				}
			}
		} finally {
			FileAccessMethods.deleteDirectory(directory);
		}
		return events;
	}

	public AutoConfig downloadAutoconfig(Repository repository)
			throws IOException {

		AutoConfig autoConfig = null;
		File directory = new File(TEMP_FOLDER_PATH + "/" + repository.getName());
		File file = new File(directory + "/" + DataAccessConstants.AUTOCONFIG);

		try {
			directory.mkdir();
			if (!directory.exists()) {
				throw new IOException("Failed to write file: "
						+ directory.getAbsolutePath() + "\n"
						+ "Permission dinied.");
			}
			connectToRepository(repository.getProtocol(), AUTOCONFIG_FILE_PATH);
			boolean found = true;
			try {
				downloadFile(file, AUTOCONFIG_FILE_PATH);
			} catch (FileNotFoundException e) {
				found = false;
			}
			if (found) {
				if (file.exists()) {
					autoConfig = A3SFilesAccessor.readAutoConfigFile(file);
				} else {
					throw new IOException("Failed to write file: "
							+ file.getAbsolutePath() + "\n"
							+ "Permission dinied.");
				}
			}
		} finally {
			FileAccessMethods.deleteDirectory(directory);
		}
		return autoConfig;
	}

	public AutoConfig importAutoConfig(AbstractProtocole protocole)
			throws IOException {

		AutoConfig autoConfig = null;
		File directory = new File(TEMP_FOLDER_PATH);
		File file = new File(directory + "/" + DataAccessConstants.AUTOCONFIG);
		String relativePath = AUTOCONFIG_FILE_PATH;

		try {
			connect(protocole, "/" + DataAccessConstants.AUTOCONFIG);
		} catch (IOException e) {
			String coreMessage = "Failed to retreive file " + relativePath;
			IOException ioe = transferIOExceptionFactory(coreMessage, e);
			throw ioe;
		}
		try {
			directory.mkdir();
			if (!directory.exists()) {
				throw new IOException("Failed to write file: "
						+ directory.getAbsolutePath() + "\n"
						+ "Permission dinied.");
			}
			boolean found = true;
			try {
				downloadFile(file, relativePath);
			} catch (FileNotFoundException e) {
				found = false;
			}
			if (found) {
				if (file.exists()) {
					autoConfig = A3SFilesAccessor.readAutoConfigFile(file);
				} else {
					throw new IOException("Failed to write file: "
							+ file.getAbsolutePath() + "\n"
							+ "Permission dinied.");
				}
			}
		} finally {
			FileAccessMethods.deleteFile(file);
		}
		return autoConfig;
	}

	@Override
	public File downloadFile(Repository repository, String remotePath,
			String destinationPath, SyncTreeNodeDTO node)
			throws ConnectException, IOException {

		File downloadedFile = null;

		File parentDirectory = new File(destinationPath);
		parentDirectory.mkdirs();

		if (node.isLeaf()) {

			SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;

			if (leaf.getComplete() == 0) {

				String relativePath = null;

				if (leaf.isCompressed()) {
					downloadedFile = new File(parentDirectory + "/"
							+ leaf.getName() + ZIP_EXTENSION);
					this.expectedFullSize = leaf.getCompressedSize();
					relativePath = "/" + node.getRelativePath() + ZIP_EXTENSION;
				} else {
					downloadedFile = new File(parentDirectory + "/"
							+ leaf.getName());
					this.expectedFullSize = leaf.getSize();
					relativePath = "/" + node.getRelativePath();
				}

				// Resuming
				if (leaf.getDownloadStatus().equals(DownloadStatus.RUNNING)
						&& downloadedFile.exists()
						&& downloadedFile.length() != this.expectedFullSize) {
					this.offset = downloadedFile.length();
				} else {
					FileAccessMethods.deleteFile(downloadedFile);
					this.offset = 0;
				}

				this.downloadingLeaf = leaf;
				leaf.setDownloadStatus(DownloadStatus.RUNNING);

				try {
					connectToRepository(repository.getProtocol(), relativePath);
					downloadFileWithRecordProgress(downloadedFile, relativePath);
					if (!downloadedFile.exists()) {
						throw new IOException("Failed to write file: "
								+ downloadedFile.getAbsolutePath() + "\n"
								+ "Permission dinied.");
					}
					if (!canceled) {
						updateObserverDownloadTotalSizeProgress();
						node.setDownloadStatus(DownloadStatus.DONE);
					} else {
						downloadedFile = null;
					}
				} catch (IOException e) {
					downloadedFile = null;
					if (!canceled) {
						throw e;
					}
				} finally {
					this.expectedFullSize = 0;
					this.downloadingLeaf = null;
					this.offset = 0;
					this.countFileSize = 0;
					this.speed = 0;
					disconnect();
				}
			} else {// the file is uncomplete => use .zsync

				downloadedFile = new File(parentDirectory + "/"
						+ leaf.getName());
				this.expectedFullSize = leaf.getSize();

				// this.offset = 0;
				this.downloadingLeaf = leaf;
				leaf.setDownloadStatus(DownloadStatus.RUNNING);

				String relativeZsyncFileUrl = remotePath + "/" + node.getName()
						+ ZSYNC_EXTENSION;
				String relativeFileUrl = remotePath + "/" + node.getName();

				String sha1 = leaf.getLocalSHA1();

				try {
					downloadPartialFileWithRecordProgress(downloadedFile, sha1,
							relativeFileUrl, relativeZsyncFileUrl,
							repository.getProtocol());
					if (!downloadedFile.exists()) {
						throw new IOException("Failed to write file: "
								+ downloadedFile.getAbsolutePath() + "\n"
								+ "Permission dinied.");
					}
					if (!canceled) {
						updateObserverDownloadTotalSizeProgress();
						node.setDownloadStatus(DownloadStatus.DONE);
					} else {
						downloadedFile = null;
					}
				} catch (IOException e) {
					downloadedFile = null;
					if (!canceled) {
						throw e;
					}
				} finally {
					this.expectedFullSize = 0;
					this.downloadingLeaf = null;
					disconnect();
				}
			}
		} else {
			downloadedFile = new File(parentDirectory + "/" + node.getName());
			downloadedFile.mkdir();
			node.setDownloadStatus(DownloadStatus.DONE);
			if (!downloadedFile.exists()) {
				throw new IOException("Failed to write file: "
						+ downloadedFile.getAbsolutePath() + "\n"
						+ "Permission dinied.");
			}
		}

		return downloadedFile;
	}

	public double getFileCompletion(String remotePath, String destinationPath,
			SyncTreeNodeDTO node, Repository repository) throws IOException {

		File targetFile = new File(destinationPath + "/" + node.getName());

		String relativeZsyncFileUrl = remotePath + "/" + node.getName()
				+ ZSYNC_EXTENSION;

		SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
		String sha1 = leaf.getLocalSHA1();

		double complete = 0;

		try {
			complete = Jazsync.getCompletion(targetFile, sha1,
					relativeZsyncFileUrl, repository.getProtocol(), this);
		} catch (IOException e) {
			String coreMessage = "Failed to retreive file "
					+ relativeZsyncFileUrl;
			IOException ioe = transferIOExceptionFactory(coreMessage, e);
			throw ioe;
		} catch (HttpException e) {
			String message = "Server returned message " + e.getMessage()
					+ " on url:" + "\n" + relativeZsyncFileUrl;
			throw new ConnectException(message);
		}

		this.count++;
		updateObserverCount();
		return complete;
	}

	/**
	 * http://stackoverflow.com/questions/4596447/check-if-file-exists-on-remote
	 * -server-using-its-url
	 * 
	 * @throws IOException
	 */
	@Override
	public boolean fileExists(AbstractProtocole protocole, RemoteFile remoteFile)
			throws IOException {

		String fileName = remoteFile.getFilename();
		String relativeParentDirectoryPath = remoteFile
				.getParentDirectoryRelativePath();

		String relativeFilePath = "/" + relativeParentDirectoryPath + "/"
				+ fileName;
		if (relativeParentDirectoryPath.isEmpty()) {
			relativeFilePath = "/" + fileName;
		}

		System.out.println("Checking remote file: " + relativeFilePath);

		connectToRepository(protocole, relativeFilePath);

		boolean exists = false;
		try {
			myHttpConnection.setRequestHead();
			int code = myHttpConnection.getHttpStatusCode();
			if (code == HttpURLConnection.HTTP_OK) {
				exists = true;
			}
		} catch (IOException e) {
			String coreMessage = "Failed to check file " + relativeFilePath;
			IOException ioe = transferIOExceptionFactory(coreMessage, e);
			throw ioe;
		}

		if (exists) {
			System.out.println("Remote file found: " + relativeFilePath);
		} else {
			System.out.println("Remote file not found: " + relativeFilePath);
		}
		return exists;
	}

	@Override
	public void deleteFile(RemoteFile remoteFile, String repositoryRemotePath)
			throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean uploadFile(RemoteFile remoteFile, String repositoryPath,
			String remotePath) throws IOException {
		throw new UnsupportedOperationException();
	}

	public String checkPartialFileTransfer(Repository repository)
			throws IOException {

		String header = null;
		try {
			connectToRepository(repository.getProtocol(), SYNC_FILE_PATH);
			boolean accept = myHttpConnection.checkAcceptRanges();
			if (!accept) {
				header = myHttpConnection.getResponseHeader();
			}
		} finally {
			disconnect();
		}
		return header;
	}

	public boolean uploadEvents(Events events, String repositoryName,
			AbstractProtocole protocole) throws IOException {

		// try {
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

		/***/

		// connect(protocole, A3S_FOlDER_PATH);
		//
		// String attachmentName = "events";
		// String attachmentFileName = "events";
		// String crlf = "\r\n";
		// String twoHyphens = "--";
		// String boundary = "*****";
		//
		// httpURLConnection.setUseCaches(false);
		// httpURLConnection.setDoOutput(true);
		//
		// httpURLConnection.setRequestMethod("POST");
		// httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
		// httpURLConnection.setRequestProperty("Cache-Control",
		// "no-cache");
		// httpURLConnection.setRequestProperty("Content-Type",
		// "multipart/form-data;boundary=" + boundary);
		//
		// DataOutputStream request = new DataOutputStream(
		// httpURLConnection.getOutputStream());
		//
		// request.writeBytes(twoHyphens + boundary + crlf);
		// request.writeBytes("Content-Disposition: form-data; name=\""
		// + attachmentName + "\";filename=\"" + attachmentFileName
		// + "\"" + crlf);
		// request.writeBytes(crlf);
		//
		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// ObjectOutputStream oos = new ObjectOutputStream(
		// new GZIPOutputStream(baos));
		// oos.writeObject(events);
		// oos.flush();
		// oos.close();
		// InputStream uis = new ByteArrayInputStream(baos.toByteArray());
		//
		// byte[] buffer = new byte[4096];
		// int length;
		// while ((length = uis.read(buffer)) > 0) {
		// request.write(buffer, 0, length);
		// }
		//
		// request.writeBytes(crlf);
		// request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
		// request.flush();
		// request.close();
		//
		// InputStream responseStream = new BufferedInputStream(
		// httpURLConnection.getInputStream());
		//
		// BufferedReader responseStreamReader = new BufferedReader(
		// new InputStreamReader(responseStream));
		// String line = "";
		// StringBuilder stringBuilder = new StringBuilder();
		// while ((line = responseStreamReader.readLine()) != null) {
		// stringBuilder.append(line).append("\n");
		// }
		// responseStreamReader.close();
		//
		// String res = stringBuilder.toString();
		// System.out.println(res);

		// } catch (IOException e) {
		// e.printStackTrace();
		// response = false;
		// }
		return false;
	}

	@Override
	public void disconnect() {

		if (myHttpConnection != null) {
			try {
				myHttpConnection.closeConnection();
			} catch (Exception e) {
			}
		}
	}

	public void setConnexion(MyHttpConnection myHttpConnection) {
		this.myHttpConnection = myHttpConnection;
	}
}
