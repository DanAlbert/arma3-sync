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

import fr.soe.a3s.controller.ObservableFileSize;
import fr.soe.a3s.controller.ObservableFilesNumber;
import fr.soe.a3s.controller.ObservableSpeed;
import fr.soe.a3s.controller.ObserverFileSize;
import fr.soe.a3s.controller.ObserverFilesNumber;
import fr.soe.a3s.controller.ObserverSpeed;
import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.Changelogs;
import fr.soe.a3s.domain.repository.Events;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;

public class FtpDAO implements DataAccessConstants, ObservableFilesNumber,
		ObservableFileSize, ObservableSpeed {

	private ObserverFilesNumber observerFilesNumber;
	private ObserverFileSize observerFileSize;
	private ObserverSpeed observerSpeed;
	private int countFilesNumber = 0;
	private int countFileSize = 0;
	private long size = 0;
	private long startTime = 0;
	private long endTime = 0;
	private long offset = 0;

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
			String repositoryName, String remotePath) throws IOException,
			ClassNotFoundException {

		remotePath = remotePath + A3S_FOlDER_PATH;
		boolean test = ftpClient.changeWorkingDirectory(remotePath);
		File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
		directory.mkdir();
		File file = new File(directory + "/" + DataAccessConstants.SERVERINFO);
		FileOutputStream fos = new FileOutputStream(file);
		boolean found = ftpClient.retrieveFile(DataAccessConstants.SERVERINFO,
				fos);
		fos.close();
		ServerInfo serverInfo = null;
		if (found) {
			ObjectInputStream fRo = new ObjectInputStream(new GZIPInputStream(
					new FileInputStream(file)));
			serverInfo = (ServerInfo) fRo.readObject();
			fRo.close();
		}
		FileAccessMethods.deleteDirectory(directory);
		return serverInfo;
	}

	public AutoConfig downloadAutoConfig(FTPClient ftpClient, String remotePath)
			throws IOException, ClassNotFoundException {

		boolean test = ftpClient.changeWorkingDirectory(remotePath);
		File file = new File(TEMP_FOLDER_PATH + "/"
				+ DataAccessConstants.AUTOCONFIG);
		FileOutputStream fos = new FileOutputStream(file);
		boolean found = ftpClient.retrieveFile(DataAccessConstants.AUTOCONFIG,
				fos);
		fos.close();
		AutoConfig autoConfig = null;
		if (found) {
			ObjectInputStream fRo = new ObjectInputStream(new GZIPInputStream(
					new FileInputStream(file)));
			autoConfig = (AutoConfig) fRo.readObject();
			fRo.close();
		}
		FileAccessMethods.deleteFile(file);
		return autoConfig;
	}

	public FTPFile[] getFiles(FTPClient ftpClient, String remotePath)
			throws IOException {

		boolean test = ftpClient.changeWorkingDirectory(remotePath);
		FTPFile[] ftpFiles = ftpClient.listFiles();
		return ftpFiles;
	}

	public SyncTreeDirectory downloadSync(FTPClient ftpClient,
			String repositoryName, String remotePath) throws IOException,
			ClassNotFoundException {

		remotePath = remotePath + A3S_FOlDER_PATH;
		boolean test = ftpClient.changeWorkingDirectory(remotePath);
		File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
		directory.mkdir();
		File file = new File(directory + "/" + DataAccessConstants.SYNC);
		FileOutputStream fos = new FileOutputStream(file);
		boolean found = ftpClient.retrieveFile(DataAccessConstants.SYNC, fos);
		fos.close();
		SyncTreeDirectory syncTreeDirectory = null;
		if (found) {
			ObjectInputStream fRo = new ObjectInputStream(new GZIPInputStream(
					new FileInputStream(file)));
			syncTreeDirectory = (SyncTreeDirectory) fRo.readObject();
			fRo.close();
		}
		FileAccessMethods.deleteDirectory(directory);
		return syncTreeDirectory;
	}

	public Changelogs downloadChangelog(FTPClient ftpClient,
			String repositoryName, String remotePath) throws IOException,
			ClassNotFoundException {

		remotePath = remotePath + A3S_FOlDER_PATH;
		boolean test = ftpClient.changeWorkingDirectory(remotePath);
		File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
		directory.mkdir();
		File file = new File(directory + "/" + DataAccessConstants.CHANGELOGS);
		FileOutputStream fos = new FileOutputStream(file);
		boolean found = ftpClient.retrieveFile(DataAccessConstants.CHANGELOGS,
				fos);
		fos.close();
		Changelogs changelogs = null;
		if (found) {
			ObjectInputStream fRo = new ObjectInputStream(new GZIPInputStream(
					new FileInputStream(file)));
			changelogs = (Changelogs) fRo.readObject();
			fRo.close();
		}
		FileAccessMethods.deleteDirectory(directory);
		return changelogs;
	}

	public Events downloadEvent(FTPClient ftpClient, String repositoryName,
			String remotePath) throws IOException, ClassNotFoundException {

		remotePath = remotePath + A3S_FOlDER_PATH;
		boolean test = ftpClient.changeWorkingDirectory(remotePath);
		File directory = new File(TEMP_FOLDER_PATH + "/" + repositoryName);
		directory.mkdir();
		File file = new File(directory + "/" + DataAccessConstants.EVENTS);
		FileOutputStream fos = new FileOutputStream(file);
		boolean found = ftpClient.retrieveFile(DataAccessConstants.EVENTS, fos);
		fos.close();
		Events events = null;
		if (found) {
			ObjectInputStream fRo = new ObjectInputStream(new GZIPInputStream(
					new FileInputStream(file)));
			events = (Events) fRo.readObject();
			fRo.close();
		}
		FileAccessMethods.deleteDirectory(directory);
		return events;
	}

	public boolean downloadAddon(FTPClient ftpClient, String remotePath,
			String destinationPath, String name, boolean isLeaf, boolean resume)
			throws IOException {

		boolean test = ftpClient.changeWorkingDirectory(remotePath);
		File parentDirectory = new File(destinationPath);
		parentDirectory.mkdirs();
		File file = new File(parentDirectory + "/" + name);

		boolean found = false;
		if (isLeaf) {
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
					countFileSize = getCount();
					endTime = System.nanoTime();
					updateFileSizeObserver();
					updateObserverSpeed();
				}
			};
			// System.out.println("offset = " + offset);
			ftpClient.setRestartOffset(this.offset);
			found = ftpClient.retrieveFile(file.getName(), dos);

			if (fos != null) {
				fos.close();
			}
	        if (dos != null) {
	            dos.close();
	        }
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
		ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(baos));
		oos.writeObject(events);
		oos.flush();
		oos.close();
		InputStream uis = new ByteArrayInputStream(baos.toByteArray());
		boolean response = ftpClient.storeFile(EVENTS, uis);
		ftpClient.noop();
		return response;
	}

	/* File size controller */
	@Override
	public void addObserverFileSize(ObserverFileSize obs) {
		this.observerFileSize = obs;
	}

	@Override
	public void updateFileSizeObserver() {
		this.observerFileSize.update((int) offset + countFileSize);
	}

	@Override
	public void delObserverFileSize() {
		this.observerFileSize = null;
	}

	/* Files number controller */
	@Override
	public void addObserverFilesNumber(ObserverFilesNumber obs) {
		this.observerFilesNumber = obs;
	}

	@Override
	public void updateFilesNumberObserver() {
		this.observerFilesNumber.update(countFilesNumber);
	}

	@Override
	public void delObserverFilesNumber() {
		this.observerFilesNumber = null;
	}

	/* Speed controller */
	@Override
	public void addObserverSpeed(ObserverSpeed obs) {
		this.observerSpeed = obs;

	}

	@Override
	public void updateObserverSpeed() {
		long totalTime = endTime - startTime;
		if (totalTime > 2 * Math.pow(10, 9)) {// 2s
			double value = countFileSize / (totalTime * Math.pow(10, -9));
			this.observerSpeed.update((long) value);
		}
	}

	@Override
	public void delObserverSpeed() {
		this.observerSpeed = null;
	}

}
