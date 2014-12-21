package fr.soe.a3s.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
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
import fr.soe.a3s.constant.Protocol;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.Changelogs;
import fr.soe.a3s.domain.repository.Events;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.FtpException;
import fr.soe.a3s.exception.WritingException;

public class FtpDAO extends AbstractConnexionDAO {

	private FTPClient ftpClient;
	private File downloadingFile;
	private String rootRemotePath;
	private boolean acquiredSmaphore;
	private static final int CONNECTION_TIMEOUT = 5000;
	private static final int READ_TIMEOUT = 30000;

	private String connect(String url) throws NumberFormatException,
			SocketException, IOException {

		ftpClient = new FTPClient();
		String address = url.replace(Protocol.FTP.getPrompt(), "");
		int index1 = address.indexOf("/");
		String hostname = address.substring(0, index1);
		int index2 = address.lastIndexOf("/");
		String remotePath = address.substring(index1, index2);

		String port = "21";
		String login = "anonymous";
		String password = "";

		ftpClient.setConnectTimeout(CONNECTION_TIMEOUT);
		ftpClient.setDataTimeout(READ_TIMEOUT);
		ftpClient.connect(hostname, Integer.parseInt(port));
		ftpClient.login(login, password);
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		ftpClient.enterLocalPassiveMode();// passive mode

		return remotePath;
	}

	public void connectToRepository(String repositoryName,
			AbstractProtocole protocole) throws FtpException, ConnectException {

		String url = protocole.getUrl();
		String hostname = url;
		String remotePath = "";
		int index = url.indexOf("/");
		if (index != -1) {
			hostname = url.substring(0, index);
			remotePath = url.substring(index);
		}
		String port = protocole.getPort();
		String login = protocole.getLogin();
		String password = protocole.getPassword();
		ftpClient = new FTPClient();
		ftpClient.setConnectTimeout(CONNECTION_TIMEOUT);
		ftpClient.setDataTimeout(READ_TIMEOUT);
		ftpClient.setBufferSize(1048576);// 1024*1024
		boolean isLoged = false;
		int reply = 0;
		try {
			ftpClient.connect(hostname, Integer.parseInt(port));
			isLoged = ftpClient.login(login, password);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();// passive mode
			reply = ftpClient.getReplyCode();
		} catch (Exception e) {
			String message = "Failed to connect to repository "
					+ repositoryName + " on url " + "ftp://"
					+ protocole.getUrl();
			System.out.println(message);
			throw new ConnectException(message);
		}

		if (!isLoged) {
			String message = "Failed to connect to repository "
					+ repositoryName + " on url " + "ftp://"
					+ protocole.getUrl() + "\n" + "Wrong login or password.";
			System.out.println(message);
			throw new FtpException(message);
		}

		if (!FTPReply.isPositiveCompletion(reply)) {
			String message = "Failed to connect to repository "
					+ repositoryName + " on url " + "ftp://"
					+ protocole.getUrl() + "\n" + "Server return FTP error "
					+ reply;
			System.out.println(message);
			throw new FtpException(message);
		}

		// System.out.println("Connection to " + repositoryName + " success.");

		rootRemotePath = remotePath;
	}

	private boolean download(File file, String remotePath) throws IOException {

		boolean test = ftpClient.changeWorkingDirectory(remotePath);
		FileOutputStream fos = new FileOutputStream(file);
		boolean found = ftpClient.retrieveFile(file.getName(), fos);
		fos.close();
		return found;
	}

	public AutoConfig downloadAutoConfig(String url) throws FtpException,
			WritingException, ConnectException {

		if (url == null) {
			return null;
		}

		int replyCode = 0;
		String remotePath = null;
		try {
			remotePath = connect(url);
			replyCode = ftpClient.getReplyCode();
		} catch (Exception e) {
			throw new ConnectException("Connection failed.");
		}

		if (FTPReply.isPositiveCompletion(replyCode)) {
			System.out.println("Connection ok on url: " + url);
		} else {
			System.out.println("Connection ko on url: " + url);
			System.out.println("Server return error " + replyCode + " on url "
					+ url);
			throw new FtpException("Connection failed.");
		}

		AutoConfig autoConfig = null;
		try {
			File file = new File(TEMP_FOLDER_PATH + "/"
					+ DataAccessConstants.AUTOCONFIG);
			boolean found = download(file, remotePath);
			if (found && file.exists()) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				autoConfig = (AutoConfig) fRo.readObject();
				fRo.close();
				FileAccessMethods.deleteFile(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			autoConfig = null;
			throw new WritingException("Failded to read file /.a3s/autoconfig"
					+ "\n" + e.getMessage());
		}
		return autoConfig;
	}

	public AutoConfig downloadAutoconfig(String repositoryName,
			String remotePath) throws WritingException {

		AutoConfig autoConfig = null;
		try {
			remotePath = remotePath + A3S_FOlDER_PATH;
			File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
			directory.mkdir();
			File file = new File(directory + "/"
					+ DataAccessConstants.AUTOCONFIG);
			boolean found = download(file, remotePath);
			if (found && file.exists()) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				autoConfig = (AutoConfig) fRo.readObject();
				fRo.close();
			}
			FileAccessMethods.deleteDirectory(directory);
		} catch (Exception e) {
			e.printStackTrace();
			autoConfig = null;
			throw new WritingException("Failded to read file /.a3s/autoconfig"
					+ "\n" + e.getMessage());
		}
		return autoConfig;
	}

	public SyncTreeDirectory downloadSync(String repositoryName,
			String remotePath) throws WritingException {

		SyncTreeDirectory syncTreeDirectory = null;
		try {
			remotePath = remotePath + A3S_FOlDER_PATH;
			File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
			directory.mkdir();
			File file = new File(directory + "/" + DataAccessConstants.SYNC);
			boolean found = download(file, remotePath);
			if (found && file.exists()) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				syncTreeDirectory = (SyncTreeDirectory) fRo.readObject();
				fRo.close();
			}
			FileAccessMethods.deleteDirectory(directory);
		} catch (Exception e) {
			e.printStackTrace();
			syncTreeDirectory = null;
			throw new WritingException("Failded to read file /.a3s/sync" + "\n"
					+ e.getMessage());
		}
		return syncTreeDirectory;
	}

	public ServerInfo downloadSeverInfo(String repositoryName, String remotePath)
			throws WritingException {

		ServerInfo serverInfo = null;
		try {
			remotePath = remotePath + A3S_FOlDER_PATH;
			File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
			directory.mkdir();
			File file = new File(directory + "/"
					+ DataAccessConstants.SERVERINFO);
			boolean found = download(file, remotePath);
			if (found && file.exists()) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				serverInfo = (ServerInfo) fRo.readObject();
				fRo.close();
			}
			FileAccessMethods.deleteDirectory(directory);
		} catch (Exception e) {
			e.printStackTrace();
			serverInfo = null;
			throw new WritingException("Failded to read file /.a3s/serverInfo"
					+ "\n" + e.getMessage());
		}
		return serverInfo;
	}

	public Changelogs downloadChangelogs(String repositoryName,
			String remotePath) throws WritingException {

		Changelogs changelogs = null;
		try {
			remotePath = remotePath + A3S_FOlDER_PATH;
			File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
			directory.mkdir();
			File file = new File(directory + "/"
					+ DataAccessConstants.CHANGELOGS);
			boolean found = download(file, remotePath);
			if (found && file.exists()) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				changelogs = (Changelogs) fRo.readObject();
				fRo.close();
			}
			FileAccessMethods.deleteDirectory(directory);
		} catch (Exception e) {
			e.printStackTrace();
			changelogs = null;
			throw new WritingException("Failded to read file /.a3s/changelogs"
					+ "\n" + e.getMessage());
		}
		return changelogs;
	}

	public Events downloadEvent(String repositoryName, String remotePath)
			throws WritingException {

		Events events = null;
		try {
			remotePath = remotePath + A3S_FOlDER_PATH;
			File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
			directory.mkdir();
			File file = new File(directory + "/" + DataAccessConstants.EVENTS);
			boolean found = download(file, remotePath);
			if (found && file.exists()) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				events = (Events) fRo.readObject();
				fRo.close();
			}
			FileAccessMethods.deleteDirectory(directory);
		} catch (Exception e) {
			e.printStackTrace();
			events = null;
			throw new WritingException("Failded to read file /.a3s/events"
					+ "\n" + e.getMessage());
		}
		return events;
	}

	public List<FTPFile> getFiles(String remotePath) throws IOException {

		List<FTPFile> ftpFiles = new ArrayList<FTPFile>();
		boolean test = ftpClient.changeWorkingDirectory(remotePath);
		FTPFile[] subFiles = ftpClient.listFiles();
		if (subFiles != null && subFiles.length > 0) {
			for (FTPFile aFile : subFiles) {
				ftpFiles.add(aFile);
			}
		}
		return ftpFiles;
	}

	public boolean downloadFile(String remotePath, String destinationPath,
			SyncTreeNodeDTO node) throws IOException {

		this.downloadingNode = node;
		boolean test = ftpClient.changeWorkingDirectory(remotePath);
		File parentDirectory = new File(destinationPath);
		parentDirectory.mkdirs();
		downloadingFile = new File(parentDirectory + "/" + node.getName());

		boolean found = false;
		long size = 0;
		if (node.isLeaf()) {
			FTPFile[] ftpFiles = ftpClient.listFiles(downloadingFile.getName());
			if (ftpFiles.length != 0) {
				size = ftpFiles[0].getSize();
			}
			// Resuming
			boolean resume = false;
			if (node.getDownloadStatus().equals(DownloadStatus.RUNNING)
					&& downloadingFile.exists()
					&& downloadingFile.length() != size) {
				this.offset = downloadingFile.length();
				resume = true;
			} else {
				this.offset = 0;
				node.setDownloadStatus(DownloadStatus.RUNNING);
				FileAccessMethods.deleteFile(downloadingFile);
				resume = false;
			}

			// System.out.println("offset = " + offset);
			ftpClient.setRestartOffset(this.offset);

			final long startTime = System.nanoTime();
			FileOutputStream fos = new FileOutputStream(downloadingFile, resume);
			CountingOutputStream dos = new CountingOutputStream(fos) {
				@Override
				protected void afterWrite(int n) throws IOException {
					super.afterWrite(n);
					// System.out.println(getCount());
					int nbBytes = getCount();
					countFileSize = getCount();
					long endTime = System.nanoTime();
					long totalTime = endTime - startTime;
					speed = (long) (nbBytes / (totalTime * Math.pow(10, -9)));
					if (acquiredSmaphore) {
						updateFileSizeObserver();
						if (totalTime > Math.pow(10, 9) / 2) {// 0.5s
							updateObserverSpeed();
						}
					}
				}
			};
			found = ftpClient.retrieveFile(downloadingFile.getName(), dos);
			fos.close();
			dos.close();
			countFileSize = 0;
			speed = 0;
		} else {// directory
			downloadingFile.mkdir();
			found = true;
		}

		if (found) {
			updateFilesNumberObserver();
			node.setDownloadStatus(DownloadStatus.DONE);
		}
		this.downloadingFile = null;
		this.downloadingNode = null;
		return found;
	}

	public boolean uploadEvents(Events events, String remotePath)
			throws IOException {

		remotePath = remotePath + A3S_FOlDER_PATH;
		boolean test = ftpClient.changeWorkingDirectory(remotePath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(
				baos));
		oos.writeObject(events);
		oos.flush();
		oos.close();
		InputStream uis = new ByteArrayInputStream(baos.toByteArray());
		boolean response = ftpClient.storeFile(EVENTS, uis);
		ftpClient.noop();
		return response;
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
			found = download(file, UPDATE_REPOSITORY_DEV);
		} else {
			found = download(file, UPDATE_REPOSITORY);
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

	public boolean fileExists(String remotePath, SyncTreeNodeDTO node)
			throws IOException {

		boolean exists = ftpClient.changeWorkingDirectory(remotePath);
		if (exists) {
			if (node.isLeaf()) {
				remotePath = remotePath + "/"
						+ node.getParent().getRelativePath();
				exists = ftpClient.changeWorkingDirectory(remotePath);
				if (exists) {
					FTPFile[] subFiles = ftpClient.listFiles();
					for (FTPFile f : subFiles) {
						if (!f.isDirectory()
								&& node.getName().equals(f.getName())) {
							exists = true;
							break;
						} else {
							exists = false;
						}
					}
				}
			} else {
				remotePath = remotePath + "/" + node.getRelativePath();
				exists = ftpClient.changeWorkingDirectory(remotePath);
			}
		}
		return exists;
	}

	public boolean uploadFile(File file, String remotePath,
			SyncTreeNodeDTO node, boolean resume) throws IOException {

		boolean exists = ftpClient.changeWorkingDirectory(remotePath);

		boolean found = false;
		if (node.isLeaf()) {
			makeDir(remotePath, node.getParent().getRelativePath());
			remotePath = remotePath + "/" + node.getParent().getRelativePath();
			ftpClient.changeWorkingDirectory(remotePath);

			this.offset = 0;

			final long startTime = System.nanoTime();
			FileInputStream fis = new FileInputStream(file);
			CountingInputStream uis = new CountingInputStream(fis) {
				@Override
				protected void afterRead(int n) {
					super.afterRead(n);
					System.out.println(getCount());
					int nbBytes = getCount();
					countFileSize = getCount();
					updateFileSizeObserver2();
					long endTime = System.nanoTime();
					long totalTime = endTime - startTime;
					speed = (long) (nbBytes / (totalTime * Math.pow(10, -9)));
					if (totalTime > Math.pow(10, 9) / 2) {// 0.5s
						updateObserverSpeed();
					}
				}
			};

			found = ftpClient.storeFile(file.getName(), uis);
			ftpClient.noop();
			fis.close();
			uis.close();
		} else {
			makeDir(remotePath, node.getRelativePath());
			found = true;
		}
		updateFilesNumberObserver2();
		return found;
	}

	public void makeDir(String remotePath, String dirTree) throws IOException {

		boolean dirExists = ftpClient.changeWorkingDirectory(remotePath);
		if (!dirExists) {
			if (!ftpClient.makeDirectory(remotePath)) {
				throw new IOException("Unable to create remote directory '"
						+ remotePath + "'.  error='"
						+ ftpClient.getReplyString() + "'");
			}
			if (!ftpClient.changeWorkingDirectory(remotePath)) {
				throw new IOException(
						"Unable to change into newly created remote directory '"
								+ remotePath + "'.  error='"
								+ ftpClient.getReplyString() + "'");
			}
		}

		// tokenize the string and attempt to change into each directory level.
		// If you cannot, then start creating.
		String[] directories = dirTree.split("/");
		for (String dir : directories) {
			if (!dir.isEmpty()) {
				if (dirExists) {
					dirExists = ftpClient.changeWorkingDirectory(dir);
				}
				if (!dirExists) {
					if (!ftpClient.makeDirectory(dir)) {
						throw new IOException(
								"Unable to create remote directory '" + dir
										+ "'.  error='"
										+ ftpClient.getReplyString() + "'");
					}
					if (!ftpClient.changeWorkingDirectory(dir)) {
						throw new IOException(
								"Unable to change into newly created remote directory '"
										+ dir + "'.  error='"
										+ ftpClient.getReplyString() + "'");
					}
				}
			}
		}
	}

	public void uploadSingleFile(File file, String remotePath,
			SyncTreeNodeDTO node, boolean resume) throws IOException {

		ftpClient.changeWorkingDirectory(remotePath);
		makeDir(remotePath, node.getParent().getRelativePath());
		remotePath = remotePath + "/" + node.getParent().getRelativePath();
		ftpClient.changeWorkingDirectory(remotePath);
		FileInputStream fis = new FileInputStream(file);
		ftpClient.storeFile(file.getName(), fis);
		ftpClient.noop();
		fis.close();
	}

	public void deleteFile(String fileName, boolean isFile, String remotePath)
			throws IOException {

		boolean exists = ftpClient.changeWorkingDirectory(remotePath);
		if (exists) {
			if (isFile) {
				ftpClient.deleteFile(fileName);
			} else {
				removeDirectory(fileName, remotePath);
			}
		}
	}

	private void removeDirectory(String folderName, String remotePath)
			throws IOException {

		FTPFile[] subFiles = ftpClient.listFiles(folderName);
		if (subFiles != null && subFiles.length > 0) {
			for (FTPFile aFile : subFiles) {
				String newRemotePath = remotePath + "/" + folderName;
				deleteFile(aFile.getName(), aFile.isFile(), newRemotePath);
			}
		}
		boolean exists = ftpClient.changeWorkingDirectory(remotePath);
		if (exists) {
			ftpClient.removeDirectory(folderName);
		}
	}

	public void uploadSync(SyncTreeDirectory sync, String remotePath)
			throws IOException {

		remotePath = remotePath + A3S_FOlDER_PATH;
		boolean exists = ftpClient.changeWorkingDirectory(remotePath);
		if (!exists) {
			if (!ftpClient.makeDirectory(remotePath)) {
				throw new IOException("Unable to create remote directory '"
						+ remotePath + "'.  error='"
						+ ftpClient.getReplyString() + "'");
			}
			if (!ftpClient.changeWorkingDirectory(remotePath)) {
				throw new IOException(
						"Unable to change into newly created remote directory '"
								+ remotePath + "'.  error='"
								+ ftpClient.getReplyString() + "'");
			}
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(
				baos));
		oos.writeObject(sync);
		oos.flush();
		oos.close();
		InputStream uis = new ByteArrayInputStream(baos.toByteArray());
		boolean ok = ftpClient.storeFile(SYNC, uis);
		if (!ok) {
			throw new IOException("Failed to upload /.a3s/sync file.");
		}
		ftpClient.noop();
	}

	public void uploadServerInfo(ServerInfo serverInfo, String remotePath)
			throws IOException {

		remotePath = remotePath + A3S_FOlDER_PATH;
		boolean exists = ftpClient.changeWorkingDirectory(remotePath);
		if (!exists) {
			if (!ftpClient.makeDirectory(remotePath)) {
				throw new IOException("Unable to create remote directory '"
						+ remotePath + "'.  error='"
						+ ftpClient.getReplyString() + "'");
			}
			if (!ftpClient.changeWorkingDirectory(remotePath)) {
				throw new IOException(
						"Unable to change into newly created remote directory '"
								+ remotePath + "'.  error='"
								+ ftpClient.getReplyString() + "'");
			}
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(
				baos));
		oos.writeObject(serverInfo);
		oos.flush();
		oos.close();
		InputStream uis = new ByteArrayInputStream(baos.toByteArray());
		boolean ok = ftpClient.storeFile(SERVERINFO, uis);
		if (!ok) {
			throw new IOException("Failed to upload /.a3s/serverInfo file.");
		}
		ftpClient.noop();
	}

	public void uploadChangelogs(Changelogs changelogs, String remotePath)
			throws IOException {

		remotePath = remotePath + A3S_FOlDER_PATH;
		boolean exists = ftpClient.changeWorkingDirectory(remotePath);
		if (!exists) {
			if (!ftpClient.makeDirectory(remotePath)) {
				throw new IOException("Unable to create remote directory '"
						+ remotePath + "'.  error='"
						+ ftpClient.getReplyString() + "'");
			}
			if (!ftpClient.changeWorkingDirectory(remotePath)) {
				throw new IOException(
						"Unable to change into newly created remote directory '"
								+ remotePath + "'.  error='"
								+ ftpClient.getReplyString() + "'");
			}
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(
				baos));
		oos.writeObject(changelogs);
		oos.flush();
		oos.close();
		InputStream uis = new ByteArrayInputStream(baos.toByteArray());
		boolean ok = ftpClient.storeFile(CHANGELOGS, uis);
		if (!ok) {
			throw new IOException("Failed to upload /.a3s/changelogs file.");
		}
		ftpClient.noop();
	}

	public void uploadAutoconfig(AutoConfig autoConfig, String remotePath)
			throws IOException {

		remotePath = remotePath + A3S_FOlDER_PATH;
		boolean exists = ftpClient.changeWorkingDirectory(remotePath);
		if (!exists) {
			if (!ftpClient.makeDirectory(remotePath)) {
				throw new IOException("Unable to create remote directory '"
						+ remotePath + "'.  error='"
						+ ftpClient.getReplyString() + "'");
			}
			if (!ftpClient.changeWorkingDirectory(remotePath)) {
				throw new IOException(
						"Unable to change into newly created remote directory '"
								+ remotePath + "'.  error='"
								+ ftpClient.getReplyString() + "'");
			}
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(
				baos));
		oos.writeObject(autoConfig);
		oos.flush();
		oos.close();
		InputStream uis = new ByteArrayInputStream(baos.toByteArray());
		boolean ok = ftpClient.storeFile(AUTOCONFIG, uis);
		if (!ok) {
			throw new IOException("Failed to upload /.a3s/autoconfig file.");
		}
		ftpClient.noop();
	}

	@Override
	public void cancel(boolean resumable) {
		canceled = true;
		disconnect();
		if (!resumable && downloadingFile != null) {
			FileAccessMethods.deleteFile(downloadingFile);
		}
		downloadingNode = null;
	}

	public void disconnect() {
		if (ftpClient != null) {
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
			}
		}
	}

	public String getRootRemotePath() {
		return rootRemotePath;
	}

	public boolean isAcquiredSmaphore() {
		return acquiredSmaphore;
	}

	public void setAcquiredSemaphore(boolean acquiredSmaphore) {
		this.acquiredSmaphore = acquiredSmaphore;
	}
}
