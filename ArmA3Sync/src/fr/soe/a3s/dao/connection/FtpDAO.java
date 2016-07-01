package fr.soe.a3s.dao.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

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
import fr.soe.a3s.exception.FtpException;

public class FtpDAO extends AbstractConnexionDAO {

	private FTPClient ftpClient;
	private static final int BUFFER_SIZE = 4096;// 4KB
	private int bufferSize = BUFFER_SIZE;
	private long elapsedTime = 0;

	private void connect(AbstractProtocole protocole) throws IOException,
			FtpException {

		ftpClient = new FTPClient();

		String port = protocole.getPort();
		String login = protocole.getLogin();
		String password = protocole.getPassword();
		String hostname = protocole.getHostname();

		// Set connection and read time out
		int connectionTimeOutValue = Integer.parseInt(protocole
				.getConnectionTimeOut());
		if (connectionTimeOutValue != 0) {
			ftpClient.setConnectTimeout(connectionTimeOutValue);
		}
		int readTimeOutValue = Integer.parseInt(protocole.getReadTimeOut());
		if (readTimeOutValue != 0) {
			ftpClient.setDataTimeout(readTimeOutValue);
		}

		ftpClient.setBufferSize(1048576);// 1024*1024
		boolean isLoged = false;

		ftpClient.connect(hostname, Integer.parseInt(port));

		isLoged = ftpClient.login(login, password);
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);// binary transfer
		ftpClient.enterLocalPassiveMode();// passive mode
		int reply = ftpClient.getReplyCode();

		if (!isLoged) {
			throw new FtpException(WRONG_LOGIN_PASSWORD);
		}

		if (!FTPReply.isPositiveCompletion(reply)) {
			throw new FtpException("FTP error code: " + Integer.toString(reply));
		}
	}

	public void connectToRepository(AbstractProtocole protocole)
			throws IOException {

		try {
			connect(protocole);
		} catch (IOException e) {
			if (!canceled) {
				String coreMessage = "Failed to connect to repository on url: "
						+ "\n" + protocole.getProtocolType().getPrompt()
						+ protocole.getUrl();
				IOException ioe = transferIOExceptionFactory(coreMessage, e);
				throw ioe;
			}
		} catch (FtpException e) {
			if (!canceled) {
				String message = "Server returned message " + e.getMessage()
						+ " on url:" + "\n"
						+ protocole.getProtocolType().getPrompt()
						+ protocole.getUrl();
				throw new ConnectException(message);
			}
		}
	}

	private boolean downloadFile(File file, String remotePath)
			throws IOException {

		boolean found = false;
		FileOutputStream fos = null;
		try {
			boolean ok = ftpClient.changeWorkingDirectory(remotePath);
			fos = new FileOutputStream(file);
			if (ok) {
				found = ftpClient.retrieveFile(file.getName(), fos);
			}
		} catch (IOException e) {
			if (!canceled) {
				String coreMessage = "Failed to retrieve file " + remotePath
						+ "/" + file.getName();
				IOException ioe = transferIOExceptionFactory(coreMessage, e);
				throw ioe;
			}
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
		return found;
	}

	private boolean downloadFileWithRecordProgress(File file, String remotePath)
			throws IOException {

		boolean resume = false;
		if (this.offset > 0) {
			resume = true;
			System.out.println("Resuming file: " + file.getAbsolutePath()
					+ " at offset " + offset);
		} else {
			resume = false;
			System.out.println("Downloading whole file: "
					+ file.getAbsolutePath());
		}

		FileOutputStream fos = null;
		CountingOutputStream dos = null;
		InputStream inputStream = null;
		boolean found = false;

		countFileSize = 0;
		speed = 0;
		updateObserverDownloadSingleSizeProgress();
		updateObserverDownloadSpeed();

		try {
			final long startTime = System.nanoTime();
			this.elapsedTime = 0;
			fos = new FileOutputStream(file, resume);
			dos = new CountingOutputStream(fos) {
				@Override
				protected void afterWrite(int n) throws IOException {
					super.afterWrite(n);
					long nbBytes = getByteCount();
					countFileSize = nbBytes;
					long endTime = System.nanoTime();
					long totalTime = endTime - startTime;
					long deltaTime = totalTime - elapsedTime;
					long speed = (long) ((nbBytes * Math.pow(10, 9)) / totalTime);// B/s
					if (maximumClientDownloadSpeed != 0) {
						if (speed > maximumClientDownloadSpeed) {
							bufferSize = bufferSize - 1;
							if (bufferSize < 0) {
								bufferSize = 0;
							}
						} else {
							bufferSize = bufferSize + 1;
							if (bufferSize > BUFFER_SIZE) {
								bufferSize = BUFFER_SIZE;
							}
						}
					}

					if (acquiredSemaphore) {
						updateObserverDownloadSingleSizeProgress();
					}

					if (deltaTime > Math.pow(10, 9) / 2) {
						setSpeed(speed);
						elapsedTime = totalTime;
						if (acquiredSemaphore) {
							updateObserverDownloadSpeed();
						}
					}
				}
			};

			ftpClient.setRestartOffset(this.offset);
			found = ftpClient.changeWorkingDirectory(remotePath);

			if (found) {

				long startResponseTime = System.nanoTime();
				int code = ftpClient.getReplyCode();
				long endResponseTime = System.nanoTime();
				responseTime = endResponseTime - startResponseTime;
				updateObserverDownloadResponseTime();

				if (FTPReply.isPositiveCompletion(code)) {

					inputStream = ftpClient.retrieveFileStream(remotePath + "/"
							+ file.getName());

					if (inputStream == null) {
						found = false;
					} else {
						byte[] bytesArray = bytesArray = new byte[bufferSize];
						int bytesRead = -1;
						while ((bytesRead = inputStream.read(bytesArray)) != -1
								&& !canceled) {
							dos.write(bytesArray, 0, bytesRead);
							bytesArray = bytesArray = new byte[bufferSize];
						}

						if (fos != null) {
							fos.close();
						}
						if (dos != null) {
							dos.close();
						}
						if (inputStream != null) {
							inputStream.close();
						}

						if (!canceled) {
							found = ftpClient.completePendingCommand();
						}
					}
				}
			}
		} catch (IOException e) {
			String coreMessage = "Failed to retrieve file " + remotePath + "/"
					+ file.getName();
			IOException ioe = transferIOExceptionFactory(coreMessage, e);
			throw ioe;
		} finally {
			if (fos != null) {
				fos.close();
			}
			if (dos != null) {
				dos.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
			speed = 0;
			updateObserverDownloadSpeed();
		}
		return found;
	}

	public SyncTreeDirectory downloadSync(String repositoryName,
			AbstractProtocole protocole) throws IOException {

		SyncTreeDirectory sync = null;
		File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
		File file = new File(directory + "/" + DataAccessConstants.SYNC);
		String remotePath = protocole.getRemotePath() + A3S_FOlDER_PATH;

		try {
			directory.mkdir();
			if (!directory.exists()) {
				throw new IOException("Failed to write file: "
						+ directory.getAbsolutePath() + "\n"
						+ "Permission dinied.");
			}
			boolean found = downloadFile(file, remotePath);
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

	public ServerInfo downloadSeverInfo(String repositoryName,
			AbstractProtocole protocole) throws IOException {

		ServerInfo serverInfo = null;
		File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
		File file = new File(directory + "/" + DataAccessConstants.SERVERINFO);
		String remotePath = protocole.getRemotePath() + A3S_FOlDER_PATH;

		try {
			directory.mkdir();
			if (!directory.exists()) {
				throw new IOException("Failed to write file: "
						+ directory.getAbsolutePath() + "\n"
						+ "Permission dinied.");
			}
			boolean found = downloadFile(file, remotePath);
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

	public Changelogs downloadChangelogs(String repositoryName,
			AbstractProtocole protocole) throws IOException {

		Changelogs changelogs = null;
		File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
		File file = new File(directory + "/" + DataAccessConstants.CHANGELOGS);
		String remotePath = protocole.getRemotePath() + A3S_FOlDER_PATH;

		try {
			directory.mkdir();
			if (!directory.exists()) {
				throw new IOException("Failed to write file: "
						+ directory.getAbsolutePath() + "\n"
						+ "Permission dinied.");
			}
			boolean found = downloadFile(file, remotePath);
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

	public Events downloadEvents(String repositoryName,
			AbstractProtocole protocole) throws IOException {

		Events events = null;
		File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
		File file = new File(directory + "/" + DataAccessConstants.EVENTS);
		String remotePath = protocole.getRemotePath() + A3S_FOlDER_PATH;

		try {
			directory.mkdir();
			if (!directory.exists()) {
				throw new IOException("Failed to write file: "
						+ directory.getAbsolutePath() + "\n"
						+ "Permission dinied.");
			}
			boolean found = downloadFile(file, remotePath);
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

	public AutoConfig downloadAutoconfig(String repositoryName,
			AbstractProtocole protocole) throws IOException {

		AutoConfig autoConfig = null;
		File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
		File file = new File(directory + "/" + DataAccessConstants.AUTOCONFIG);
		String remotePath = protocole.getRemotePath() + A3S_FOlDER_PATH;

		try {
			directory.mkdir();
			if (!directory.exists()) {
				throw new IOException("Failed to write file: "
						+ directory.getAbsolutePath() + "\n"
						+ "Permission dinied.");
			}
			boolean found = downloadFile(file, remotePath);
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

	public AutoConfig importAutoConfig(AbstractProtocole protocol)
			throws ConnectException, IOException {

		AutoConfig autoConfig = null;
		File directory = new File(TEMP_FOLDER_PATH);
		File file = new File(directory + "/" + DataAccessConstants.AUTOCONFIG);
		// Parent path from full autoconfig url
		String remotePath = protocol.getRemotePath();

		try {
			try {
				connect(protocol);
			} catch (IOException e) {
				String coreMessage = "Failed to connect to url: " + "\n"
						+ protocol.getProtocolType().getPrompt()
						+ protocol.getUrl() + "/"
						+ DataAccessConstants.AUTOCONFIG;
				IOException ioe = transferIOExceptionFactory(coreMessage, e);
				throw ioe;
			} catch (FtpException e) {
				String message = "Server returned error message "
						+ e.getMessage() + " on url:" + "\n"
						+ protocol.getProtocolType().getPrompt()
						+ protocol.getUrl() + "/"
						+ DataAccessConstants.AUTOCONFIG;
				throw new ConnectException(message);
			}

			directory.mkdir();
			if (!directory.exists()) {
				throw new IOException("Failed to write file: "
						+ directory.getAbsolutePath() + "\n"
						+ "Permission dinied.");
			}
			boolean found = downloadFile(file, protocol.getRemotePath());
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
			String destinationPath, SyncTreeNodeDTO node) throws IOException {

		File downloadedFile = null;

		File parentDirectory = new File(destinationPath);
		parentDirectory.mkdirs();

		if (node.isLeaf()) {

			SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;

			if (leaf.isCompressed()) {
				downloadedFile = new File(parentDirectory + "/"
						+ leaf.getName() + ZIP_EXTENSION);
				this.expectedFullSize = leaf.getCompressedSize();
			} else {
				downloadedFile = new File(parentDirectory + "/"
						+ leaf.getName());
				this.expectedFullSize = leaf.getSize();
			}

			// Resuming
			boolean resume = false;
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
				boolean found = downloadFileWithRecordProgress(downloadedFile,
						remotePath);
				if (!found) {
					String message = "File not found on repository: "
							+ remotePath + "/" + downloadedFile.getName();
					throw new FileNotFoundException(message);
				} else if (!downloadedFile.exists()) {
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
			}
		} else {// directory
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

	public String downloadXMLupdateFile(boolean devMode) throws IOException,
			DocumentException, FtpException {

		ftpClient = new FTPClient();
		ftpClient.connect(UPDTATE_REPOSITORY_ADRESS, UPDTATE_REPOSITORY_PORT);
		ftpClient.login(UPDTATE_REPOSITORY_LOGIN, UPDTATE_REPOSITORY_PASS);
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		ftpClient.enterLocalPassiveMode();// passive mode
		int reply = ftpClient.getReplyCode();
		if (FTPReply.isPositiveCompletion(reply)) {
			System.out.println("Connection updates repository Success");
		} else {
			System.out.println("Connection Failed");
			throw new FtpException("Failed to connect to updates repository.");
		}

		File file = new File(INSTALLATION_PATH + "/" + "a3s.xml");
		boolean found = false;
		if (devMode) {
			found = downloadFile(file, UPDATE_REPOSITORY_DEV);
		} else {
			found = downloadFile(file, UPDATE_REPOSITORY);
		}

		String nom = null;
		if (found && file.exists()) {
			SAXReader reader = new SAXReader();
			Document documentLeaVersion = reader.read(file);
			Element root = documentLeaVersion.getRootElement();
			nom = root.selectSingleNode("nom").getText();
		}
		return nom;
	}

	@Override
	public boolean fileExists(AbstractProtocole protocol, RemoteFile remoteFile)
			throws IOException {

		String fileName = remoteFile.getFilename();
		String relativeParentDirectoryPath = remoteFile
				.getParentDirectoryRelativePath();

		String relativeFilePath = "/" + relativeParentDirectoryPath + "/"
				+ fileName;
		String remoteParentDirectoryPath = protocol.getRemotePath() + "/"
				+ relativeParentDirectoryPath;
		if (relativeParentDirectoryPath.isEmpty()) {
			relativeFilePath = "/" + fileName;
			remoteParentDirectoryPath = protocol.getRemotePath();
		}

		System.out.println("Checking remote file: " + relativeFilePath);

		boolean exists = false;
		if (remoteFile.isDirectory()) {
			exists = directoryExists(remoteParentDirectoryPath, fileName);
		} else {
			exists = fileExists(remoteParentDirectoryPath, fileName,
					relativeFilePath);
		}

		if (exists) {
			System.out.println("Remote file found: " + relativeFilePath);
		} else {
			System.out.println("Remote file not found: " + relativeFilePath);
		}

		return exists;
	}

	/**
	 * http://www.codejava.net/java-se/networking/ftp/get-size-of-a-file-
	 * on-ftp-server
	 */
	private boolean fileExists(String remoteParentDirectoryPath,
			String fileName, String relativeFilePath) throws IOException {

		boolean exists = false;
		InputStream inputStream = null;

		try {
			boolean ok = ftpClient
					.changeWorkingDirectory(remoteParentDirectoryPath);

			if (ok) {
				ftpClient.mlistFile(fileName);
				int returnCode = ftpClient.getReplyCode();
				if (returnCode == 500) {// mlist is not supported
					System.out
							.println("WARNING: FTP command MLST is not supported, using LST instead.");
					FTPFile[] subfiles = ftpClient.listFiles();
					if (subfiles != null) {
						for (FTPFile ftpFile : subfiles) {
							if (ftpFile.getName().equals(fileName)) {
								exists = true;
								break;
							}
						}
					}
				} else {// mlist is supported
					if (returnCode == 550) {
						exists = false;
					} else {
						exists = true;
					}
				}
			}
		} catch (IOException e) {
			String coreMessage = "Failed to check file " + relativeFilePath;
			IOException ioe = transferIOExceptionFactory(coreMessage, e);
			throw ioe;
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		return exists;
	}

	private boolean directoryExists(String remoteParentDirectoryPath,
			String fileName) throws IOException {

		boolean exists = false;
		boolean ok = ftpClient.changeWorkingDirectory(remoteParentDirectoryPath
				+ "/" + fileName);
		if (ok) {
			int returnCode = ftpClient.getReplyCode();
			if (returnCode == 550) {
				return false;
			}
			exists = true;
		}
		return exists;
	}

	@Override
	public boolean uploadFile(RemoteFile remoteFile, String repositoryPath,
			String repositoryRemotePath) throws IOException {

		String parentDirectoryRelativePath = remoteFile
				.getParentDirectoryRelativePath();
		String fileName = remoteFile.getFilename();
		boolean isFile = !remoteFile.isDirectory();

		boolean found = false;

		if (isFile) {
			File file = new File(repositoryPath + "/"
					+ parentDirectoryRelativePath + "/" + fileName);
			this.expectedFullSize = file.length();

			makeDir(repositoryRemotePath, parentDirectoryRelativePath);
			boolean ok = ftpClient.changeWorkingDirectory(repositoryRemotePath
					+ "/" + parentDirectoryRelativePath);
			if (!ok) {
				return false;
			}

			System.out.println("");
			System.out.println("Uploading file: " + file.getAbsolutePath());
			System.out.println("to target directory: " + repositoryRemotePath
					+ "/" + parentDirectoryRelativePath);

			this.offset = 0;
			FileInputStream fis = null;
			CountingInputStream uis = null;
			try {
				final long startTime = System.nanoTime();
				fis = new FileInputStream(file);
				uis = new CountingInputStream(fis) {
					@Override
					protected void afterRead(int n) {
						super.afterRead(n);
						long nbBytes = getByteCount();
						countFileSize = nbBytes;
						updateObserverUploadSingleSizeProgress();
						long endTime = System.nanoTime();
						long totalTime = endTime - startTime;
						speed = (long) (nbBytes / (totalTime * Math.pow(10, -9)));
						if (totalTime > Math.pow(10, 9) / 2) {// 0.5s
							updateObserverUploadSpeed();
						}
					}
				};
				found = ftpClient.storeFile(file.getName(), uis);
				ftpClient.noop();
			} catch (IOException e) {
				String coreMessage = "Failed to upload file "
						+ parentDirectoryRelativePath + "/" + file.getName();
				IOException ioe = transferIOExceptionFactory(coreMessage, e);
				throw ioe;
			} finally {
				if (fis != null) {
					fis.close();
				}
				if (uis != null) {
					uis.close();
				}
			}
		} else {
			makeDir(repositoryRemotePath, parentDirectoryRelativePath + "/"
					+ fileName);
			found = true;
		}
		return found;
	}

	public void makeDir(String remotePath, String dirTree) throws IOException {

		boolean dirExists = ftpClient.changeWorkingDirectory(remotePath);
		if (!dirExists) {
			if (!ftpClient.makeDirectory(remotePath)) {
				throw new IOException("Unable to create remote directory "
						+ remotePath + "\n" + "Server returned FTP error: "
						+ ftpClient.getReplyString());
			}
			if (!ftpClient.changeWorkingDirectory(remotePath)) {
				throw new IOException(
						"Unable to change into newly created remote directory"
								+ remotePath + "\n"
								+ "Server returned FTP error: "
								+ ftpClient.getReplyString());
			}
		}

		// tokenize the string and attempt to change into each directory level.
		// If you cannot, then start creating.
		String[] directories = dirTree.split("/");
		for (String dir : directories) {
			if (!dir.isEmpty()) {
				dirExists = ftpClient.changeWorkingDirectory(dir);
				if (!dirExists) {
					if (!ftpClient.makeDirectory(dir)) {
						throw new IOException(
								"Unable to create remote directory "
										+ remotePath + "/" + dirTree + "\n"
										+ "Server returned FTP error: "
										+ ftpClient.getReplyString());
					}
					if (!ftpClient.changeWorkingDirectory(dir)) {
						throw new IOException(
								"Unable to change into newly created remote directory "
										+ remotePath + "/" + dirTree + "\n"
										+ "Server returned FTP error: "
										+ ftpClient.getReplyString());
					}
				}
			}
		}
	}

	@Override
	public void deleteFile(RemoteFile remoteFile, String repositoryRemotePath)
			throws IOException {

		String relativePath = remoteFile.getParentDirectoryRelativePath();
		String fileName = remoteFile.getFilename();
		boolean isFile = !remoteFile.isDirectory();

		String remotePath = repositoryRemotePath;
		if (!relativePath.isEmpty()) {
			remotePath = repositoryRemotePath + "/" + relativePath;
		}

		boolean exists = ftpClient.changeWorkingDirectory(remotePath);
		if (exists) {
			if (isFile) {
				System.out.println("Deleting remote file: "
						+ remoteFile.getParentDirectoryRelativePath() + "/"
						+ remoteFile.getFilename());
				ftpClient.deleteFile(fileName);
			} else {
				System.out.println("Deleting remote directory: "
						+ remoteFile.getParentDirectoryRelativePath() + "/"
						+ remoteFile.getFilename());
				removeDirectory(fileName, relativePath, repositoryRemotePath);
			}
		}
	}

	private void removeDirectory(String folderName, String relativePath,
			String repositoryRemotePath) throws IOException {

		FTPFile[] subFiles = ftpClient.listFiles(folderName);
		if (subFiles != null && subFiles.length > 0) {
			for (FTPFile aFile : subFiles) {
				String newRemotePath = relativePath + "/" + folderName;
				deleteFile(
						new RemoteFile(aFile.getName(), newRemotePath,
								aFile.isDirectory()), repositoryRemotePath);
			}
		}
		String remotePath = repositoryRemotePath + "/" + relativePath;
		boolean exists = ftpClient.changeWorkingDirectory(remotePath);
		if (exists) {
			ftpClient.removeDirectory(folderName);
		}
	}

	public void uploadSync(SyncTreeDirectory sync, String remotePath)
			throws IOException {

		makeDir(remotePath, A3S_FOlDER_PATH);

		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		InputStream uis = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(new GZIPOutputStream(baos));
			oos.writeObject(sync);
			oos.flush();
			oos.close();
			uis = new ByteArrayInputStream(baos.toByteArray());
			boolean ok = ftpClient.storeFile(SYNC, uis);
			if (!ok) {
				int code = ftpClient.getReplyCode();
				throw new IOException("Failed to upload /.a3s/sync." + "\n"
						+ "Server returned error code " + code);
			}
			ftpClient.noop();
		} finally {
			if (baos != null) {
				baos.close();
			}
			if (oos != null) {
				oos.close();
			}
			if (uis != null) {
				uis.close();
			}
		}
	}

	public void uploadServerInfo(ServerInfo serverInfo, String remotePath)
			throws IOException {

		makeDir(remotePath, A3S_FOlDER_PATH);

		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		InputStream uis = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(new GZIPOutputStream(baos));
			oos.writeObject(serverInfo);
			oos.flush();
			oos.close();
			uis = new ByteArrayInputStream(baos.toByteArray());
			boolean ok = ftpClient.storeFile(SERVERINFO, uis);
			if (!ok) {
				int code = ftpClient.getReplyCode();
				throw new IOException("Failed to upload /.a3s/serverInfo."
						+ "\n" + "Server returned error code " + code);
			}
			ftpClient.noop();
		} finally {
			if (baos != null) {
				baos.close();
			}
			if (oos != null) {
				oos.close();
			}
			if (uis != null) {
				uis.close();
			}
		}
	}

	public void uploadChangelogs(Changelogs changelogs, String remotePath)
			throws IOException {

		makeDir(remotePath, A3S_FOlDER_PATH);

		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		InputStream uis = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(new GZIPOutputStream(baos));
			oos.writeObject(changelogs);
			oos.flush();
			oos.close();
			uis = new ByteArrayInputStream(baos.toByteArray());
			boolean ok = ftpClient.storeFile(CHANGELOGS, uis);
			if (!ok) {
				int code = ftpClient.getReplyCode();
				throw new IOException("Failed to upload /.a3s/changelogs."
						+ "\n" + "Server returned error code " + code);
			}
			ftpClient.noop();
		} finally {
			if (baos != null) {
				baos.close();
			}
			if (oos != null) {
				oos.close();
			}
			if (uis != null) {
				uis.close();
			}
		}
	}

	public void uploadAutoconfig(AutoConfig autoConfig, String remotePath)
			throws IOException {

		makeDir(remotePath, A3S_FOlDER_PATH);

		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		InputStream uis = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(new GZIPOutputStream(baos));
			oos.writeObject(autoConfig);
			oos.flush();
			oos.close();
			uis = new ByteArrayInputStream(baos.toByteArray());
			boolean ok = ftpClient.storeFile(AUTOCONFIG, uis);
			if (!ok) {
				int code = ftpClient.getReplyCode();
				throw new IOException("Failed to upload /.a3s/autoconfig."
						+ "\n" + "Server returned error code " + code);
			}
			ftpClient.noop();
		} finally {
			if (baos != null) {
				baos.close();
			}
			if (oos != null) {
				oos.close();
			}
			if (uis != null) {
				uis.close();
			}
		}
	}

	public void uploadEvents(Events events, String remotePath)
			throws IOException {

		makeDir(remotePath, A3S_FOlDER_PATH);

		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		InputStream uis = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(new GZIPOutputStream(baos));
			oos.writeObject(events);
			oos.flush();
			oos.close();
			uis = new ByteArrayInputStream(baos.toByteArray());
			boolean ok = ftpClient.storeFile(EVENTS, uis);
			if (!ok) {
				int code = ftpClient.getReplyCode();
				throw new IOException("Failed to upload /.a3s/events." + "\n"
						+ "Server returned error code " + code);
			}
			ftpClient.noop();
		} finally {
			if (baos != null) {
				baos.close();
			}
			if (oos != null) {
				oos.close();
			}
			if (uis != null) {
				uis.close();
			}
		}
	}

	@Override
	public void disconnect() {

		if (ftpClient != null) {
			try {
				ftpClient.disconnect();
			} catch (Exception e) {
			}
		}
	}
}
