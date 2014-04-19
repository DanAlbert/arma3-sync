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
import java.net.SocketException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import fr.soe.a3s.constant.Protocole;
import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.Changelogs;
import fr.soe.a3s.domain.repository.Events;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.dto.AutoConfigDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.FtpException;
import fr.soe.a3s.exception.HttpException;
import fr.soe.a3s.exception.WritingException;

public class FtpDAO extends AbstractConnexionDAO {

	private FTPClient ftpClient;

	private String connect(String url) throws NumberFormatException,
			SocketException, IOException {

		ftpClient = new FTPClient();
		String address = url.replace(Protocole.FTP.getPrompt(), "");
		int index1 = address.indexOf("/");
		String hostname = address.substring(0, index1);
		int index2 = address.lastIndexOf("/");
		String remotePath = address.substring(index1, index2);

		String port = "21";
		String login = "anonymous";
		String password = "";

		ftpClient.setConnectTimeout(5000);// 5 sec
		ftpClient.connect(hostname, Integer.parseInt(port));
		ftpClient.login(login, password);
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		ftpClient.enterLocalPassiveMode();// passive mode

		return remotePath;
	}

	public String connectToRepository(Repository repository)
			throws FtpException {

		String url = repository.getProtocole().getUrl();
		String hostname = url;
		String remotePath = "";
		int index = url.indexOf("/");
		if (index != -1) {
			hostname = url.substring(0, index);
			remotePath = url.substring(index);
		}
		String port = repository.getProtocole().getPort();
		String login = repository.getProtocole().getLogin();
		String password = repository.getProtocole().getPassword();
		ftpClient = new FTPClient();
		ftpClient.setConnectTimeout(5000);// 5 sec
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
			throw new FtpException("Failed to connect to repository "
					+ repository.getName());
		}

		if (!isLoged) {
			System.out.println("Connection to " + repository.getName()
					+ " failed.\nWrong login or password.");
			throw new FtpException("Failed to connect to repository " + "\""
					+ repository.getName() + "\"" + "." + "\n"
					+ "Wrong login or password.");
		}

		if (!FTPReply.isPositiveCompletion(reply)) {
			System.out.println("Connection to " + repository.getName()
					+ " failed.");
			throw new FtpException("Failed to connect to repository "
					+ repository.getName());
		}

		System.out.println("Connection to " + repository.getName()
				+ " success.");

		return remotePath;
	}

	private boolean download(File file, String remotePath) throws IOException {

		boolean test = ftpClient.changeWorkingDirectory(remotePath);
		FileOutputStream fos = new FileOutputStream(file);
		boolean found = ftpClient.retrieveFile(file.getName(), fos);
		fos.close();
		return found;
	}

	public AutoConfig downloadAutoConfig(String url) throws FtpException,
			WritingException {

		if (url == null) {
			return null;
		}

		int replyCode = 0;
		String remotePath = null;
		try {
			remotePath = connect(url);
			replyCode = ftpClient.getReplyCode();
		} catch (Exception e) {
			throw new FtpException("Connection failed.");
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
			throw new WritingException(e.getMessage());
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
			throw new WritingException(e.getMessage());
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
			throw new WritingException(e.getMessage());
		}
		return serverInfo;
	}

	public Changelogs downloadChangelog(String repositoryName, String remotePath)
			throws WritingException {

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
			throw new WritingException(e.getMessage());
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
			throw new WritingException(e.getMessage());
		}
		return events;
	}

	public FTPFile[] getFiles(FTPClient ftpClient, String remotePath)
			throws IOException {

		boolean test = ftpClient.changeWorkingDirectory(remotePath);
		FTPFile[] ftpFiles = ftpClient.listFiles();
		return ftpFiles;
	}

	public boolean downloadFile(String remotePath, String destinationPath,
			SyncTreeNodeDTO node, boolean resume) throws IOException {

		boolean test = ftpClient.changeWorkingDirectory(remotePath);
		File parentDirectory = new File(destinationPath);
		parentDirectory.mkdirs();
		File file = new File(parentDirectory + "/" + node.getName());

		boolean found = false;
		if (node.isLeaf()) {
			FTPFile[] ftpFiles = ftpClient.listFiles(file.getName());
			if (ftpFiles.length != 0) {
				size = ftpFiles[0].getSize();
			}
			if (resume && file.exists() && file.length() != size) {
				this.offset = file.length();
			} else {
				this.offset = 0;
				FileAccessMethods.deleteFile(file);
			}

			startTime = System.nanoTime();
			FileOutputStream fos = new FileOutputStream(file, resume);
			CountingOutputStream dos = new CountingOutputStream(fos) {
				@Override
				protected void afterWrite(int n) throws IOException {
					super.afterWrite(n);
					// System.out.println(getCount());
					int nbBytes = getCount();
					countFileSize = getCount();
					endTime = System.nanoTime();
					updateFileSizeObserver();
					updateObserverSpeed(nbBytes);
				}
			};
			// System.out.println("offset = " + offset);
			ftpClient.setRestartOffset(this.offset);
			found = ftpClient.retrieveFile(file.getName(), dos);
			fos.close();
			dos.close();
		} else {// directory
			file.mkdir();
			found = ftpClient.changeWorkingDirectory(file.getName());
		}
		countFilesNumber++;
		updateFilesNumberObserver();
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

	public void disconnect() {
		if (ftpClient != null) {
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
			}
		}
	}
}
