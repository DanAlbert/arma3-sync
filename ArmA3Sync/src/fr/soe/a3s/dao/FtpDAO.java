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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.Changelogs;
import fr.soe.a3s.domain.repository.Events;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class FtpDAO extends AbstractConnexionDAO {

	public String downloadXMLupdateFile(FTPClient ftpClient, boolean devMode)
			throws IOException, DocumentException {

		if (devMode) {
			ftpClient.changeWorkingDirectory(UPDATE_REPOSITORY_DEV);
		} else {
			ftpClient.changeWorkingDirectory(UPDATE_REPOSITORY);
		}
		File file = new File(INSTALLATION_PATH + "/" + "a3s.xml");
		FileOutputStream fos = new FileOutputStream(file);
		boolean found = ftpClient.retrieveFile("a3s.xml", fos);
		String nom = null;
		fos.close();
		if (found) {
			SAXReader reader = new SAXReader();
			Document documentLeaVersion = reader.read(file);
			Element root = documentLeaVersion.getRootElement();
			nom = root.selectSingleNode("nom").getText();
		}
		return nom;
	}

	public ServerInfo downloadSeverInfo(FTPClient ftpClient,
			String repositoryName, String remotePath) {

		ServerInfo serverInfo = null;

		try {
			remotePath = remotePath + A3S_FOlDER_PATH;
			boolean test = ftpClient.changeWorkingDirectory(remotePath);
			File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
			directory.mkdir();
			File file = new File(directory + "/"
					+ DataAccessConstants.SERVERINFO);
			FileOutputStream fos = new FileOutputStream(file);
			boolean found = ftpClient.retrieveFile(
					DataAccessConstants.SERVERINFO, fos);
			fos.close();

			if (found) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				serverInfo = (ServerInfo) fRo.readObject();
				fRo.close();
			}
			FileAccessMethods.deleteDirectory(directory);
		} catch (Exception e) {
			e.printStackTrace();
			serverInfo = null;
		}
		return serverInfo;
	}

	public AutoConfig downloadAutoConfig(FTPClient ftpClient, String remotePath) {

		AutoConfig autoConfig = null;

		try {
			boolean test = ftpClient.changeWorkingDirectory(remotePath);
			File file = new File(TEMP_FOLDER_PATH + "/"
					+ DataAccessConstants.AUTOCONFIG);
			FileOutputStream fos = new FileOutputStream(file);
			boolean found = ftpClient.retrieveFile(
					DataAccessConstants.AUTOCONFIG, fos);
			fos.close();
			if (found) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				autoConfig = (AutoConfig) fRo.readObject();
				fRo.close();
			}
			FileAccessMethods.deleteFile(file);
		} catch (Exception e) {
			e.printStackTrace();
			autoConfig = null;
		}
		return autoConfig;
	}

	public FTPFile[] getFiles(FTPClient ftpClient, String remotePath)
			throws IOException {

		boolean test = ftpClient.changeWorkingDirectory(remotePath);
		FTPFile[] ftpFiles = ftpClient.listFiles();
		return ftpFiles;
	}

	public SyncTreeDirectory downloadSync(FTPClient ftpClient,
			String repositoryName, String remotePath) {

		SyncTreeDirectory syncTreeDirectory = null;

		try {
			remotePath = remotePath + A3S_FOlDER_PATH;
			boolean test = ftpClient.changeWorkingDirectory(remotePath);
			File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
			directory.mkdir();
			File file = new File(directory + "/" + DataAccessConstants.SYNC);
			FileOutputStream fos = new FileOutputStream(file);
			boolean found = ftpClient.retrieveFile(DataAccessConstants.SYNC,
					fos);
			fos.close();
			if (found) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				syncTreeDirectory = (SyncTreeDirectory) fRo.readObject();
				fRo.close();
			}
			FileAccessMethods.deleteDirectory(directory);
		} catch (Exception e) {
			e.printStackTrace();
			syncTreeDirectory = null;
		}
		return syncTreeDirectory;
	}

	public Changelogs downloadChangelog(FTPClient ftpClient,
			String repositoryName, String remotePath) {

		Changelogs changelogs = null;

		try {
			remotePath = remotePath + A3S_FOlDER_PATH;
			boolean test = ftpClient.changeWorkingDirectory(remotePath);
			File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
			directory.mkdir();
			File file = new File(directory + "/"
					+ DataAccessConstants.CHANGELOGS);
			FileOutputStream fos = new FileOutputStream(file);
			boolean found = ftpClient.retrieveFile(
					DataAccessConstants.CHANGELOGS, fos);
			fos.close();
			if (found) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				changelogs = (Changelogs) fRo.readObject();
				fRo.close();
			}
			FileAccessMethods.deleteDirectory(directory);
		} catch (Exception e) {
			e.printStackTrace();
			changelogs = null;
		}
		return changelogs;
	}

	public Events downloadEvent(FTPClient ftpClient, String repositoryName,
			String remotePath) {

		Events events = null;

		try {
			remotePath = remotePath + A3S_FOlDER_PATH;
			boolean test = ftpClient.changeWorkingDirectory(remotePath);
			File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
			directory.mkdir();
			File file = new File(directory + "/" + DataAccessConstants.EVENTS);
			FileOutputStream fos = new FileOutputStream(file);
			boolean found = ftpClient.retrieveFile(DataAccessConstants.EVENTS,
					fos);
			fos.close();
			if (found) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(file)));
				events = (Events) fRo.readObject();
				fRo.close();
			}
			FileAccessMethods.deleteDirectory(directory);
		} catch (Exception e) {
			e.printStackTrace();
			events = null;
		}
		return events;
	}

	public boolean downloadAddon(FTPClient ftpClient, String remotePath,
			String destinationPath, SyncTreeNodeDTO node, boolean resume)
			throws IOException {

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

	public boolean uploadEvents(FTPClient ftpClient, Events events,
			String remotePath) throws IOException {

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
}
