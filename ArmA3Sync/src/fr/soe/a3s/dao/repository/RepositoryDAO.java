package fr.soe.a3s.dao.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

import fr.soe.a3s.dao.A3SFilesAccessor;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.EncryptionProvider;
import fr.soe.a3s.dao.FileAccessMethods;
import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.Changelogs;
import fr.soe.a3s.domain.repository.Events;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.exception.WritingException;

public class RepositoryDAO implements DataAccessConstants {

	private static final Map<String, Repository> mapRepositories = new HashMap<String, Repository>();

	public Map<String, Repository> getMap() {
		return mapRepositories;
	}

	public boolean remove(String repositoryName) {

		File directory = new File(REPOSITORY_FOLDER_PATH);
		Repository repository = mapRepositories.get(repositoryName);
		if (repository == null) {
			return false;
		}
		String concatName = repository.getName().replaceAll(" ", "");
		File[] subfiles = directory.listFiles();
		boolean ok = false;
		if (subfiles != null) {
			for (File file : subfiles) {
				if (file.getName().contains(concatName)) {
					ok = FileAccessMethods.deleteFile(file);
					break;
				}
			}
		}
		if (ok) {
			Repository removedRepository = mapRepositories
					.remove(repositoryName);
			if (removedRepository != null) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public List<String> readAll() throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException {

		Cipher cipher = EncryptionProvider.getDecryptionCipher();

		File directory = new File(REPOSITORY_FOLDER_PATH);
		File[] subfiles = directory.listFiles();
		List<String> repositoriesFailedToLoad = new ArrayList<String>();
		mapRepositories.clear();
		if (subfiles != null) {
			for (File file : subfiles) {
				if (file.isFile()
						&& file.getName().contains(REPOSITORY_EXTENSION)) {
					try {
						ObjectInputStream fRo = new ObjectInputStream(
								new GZIPInputStream(new FileInputStream(file)));
						SealedObject sealedObject = (SealedObject) fRo
								.readObject();
						Repository repository = (Repository) sealedObject
								.getObject(cipher);
						fRo.close();
						if (repository != null) {
							mapRepositories.put(repository.getName(),
									repository);
						}
					} catch (Exception e) {
						// e.printStackTrace();
						repositoriesFailedToLoad.add(file.getName());
					}
				}
			}
		}
		return repositoriesFailedToLoad;
	}

	public void write(String repositoryName) throws IOException,
			IllegalBlockSizeException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException {

		Cipher cipher = EncryptionProvider.getEncryptionCipher();

		Repository repository = mapRepositories.get(repositoryName);
		if (repository != null) {
			String concatName = repository.getName().replaceAll(" ", "");
			SealedObject sealedObject = new SealedObject(repository, cipher);
			String filePath = REPOSITORY_FOLDER_PATH + "/" + concatName
					+ REPOSITORY_EXTENSION;
			ObjectOutputStream fWo = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(filePath)));
			if (sealedObject != null) {
				fWo.writeObject(sealedObject);
			}
			fWo.close();
		}
	}

	public SyncTreeDirectory readSync(String repositoryName) throws IOException {

		SyncTreeDirectory sync = null;
		Repository repository = mapRepositories.get(repositoryName);
		if (repository != null) {
			String path = repository.getPath();
			String syncPath = path + "/" + SYNC_FILE_PATH;
			File file = new File(syncPath);
			sync = A3SFilesAccessor.readSyncFile(file);
		}
		return sync;
	}

	public ServerInfo readServerInfo(String repositoryName) throws IOException {

		ServerInfo serverInfo = null;
		Repository repository = mapRepositories.get(repositoryName);
		if (repository != null) {
			String path = repository.getPath();
			String serverInfoPath = path + "/" + SERVERINFO_FILE_PATH;
			File file = new File(serverInfoPath);
			serverInfo = A3SFilesAccessor.readServerInfoFile(file);
		}
		return serverInfo;
	}

	public Changelogs readChangelogs(String repositoryName) throws IOException {

		Changelogs changelogs = null;
		Repository repository = mapRepositories.get(repositoryName);
		if (repository != null) {
			String path = repository.getPath();
			String changelogsPath = path + "/" + CHANGELOGS_FILE_PATH;
			File file = new File(changelogsPath);
			changelogs = A3SFilesAccessor.readChangelogsFile(file);
		}
		return changelogs;
	}

	public AutoConfig readAutoConfig(String repositoryName) throws IOException {

		AutoConfig autoconfig = null;
		Repository repository = mapRepositories.get(repositoryName);
		if (repository != null) {
			String path = repository.getPath();
			String autocOnfigPath = path + "/" + AUTOCONFIG_FILE_PATH;
			File file = new File(autocOnfigPath);
			autoconfig = A3SFilesAccessor.readAutoConfigFile(file);
		}
		return autoconfig;
	}

	public Events readEvents(String repositoryName) throws IOException {

		Events events = null;
		Repository repository = mapRepositories.get(repositoryName);
		if (repository != null) {
			String path = repository.getPath();
			String eventsPath = path + "/" + EVENTS_FILE_PATH;
			File file = new File(eventsPath);
			events = A3SFilesAccessor.readEventsFile(file);
		}
		return events;
	}

	public void saveToDiskEvents(Events events, String repositoryPath)
			throws WritingException {

		try {
			File file = new File(repositoryPath);
			File a3sFolder = new File(repositoryPath + A3S_FOlDER_PATH);
			a3sFolder.mkdirs();
			File eventsFile = new File(file.getAbsolutePath()
					+ EVENTS_FILE_PATH);
			if (events != null) {
				ObjectOutputStream fWo = null;
				fWo = new ObjectOutputStream(new GZIPOutputStream(
						new FileOutputStream(eventsFile.getAbsolutePath())));
				fWo.writeObject(events);
				fWo.close();
			}
		} catch (Exception e) {
			throw new WritingException(e.getMessage());
		}
	}

	public void writeLog(String print, String path) throws IOException {

		PrintWriter fWo = new PrintWriter(new FileWriter(
				new File(path).getAbsolutePath()));
		fWo.println(print);
		fWo.close();
	}
}
