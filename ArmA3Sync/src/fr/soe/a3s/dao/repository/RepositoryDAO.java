package fr.soe.a3s.dao.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
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
import fr.soe.a3s.exception.CreateDirectoryException;
import fr.soe.a3s.exception.WritingException;

public class RepositoryDAO implements DataAccessConstants {

	private static final Map<String, Repository> mapRepositories = new HashMap<String, Repository>();

	public Map<String, Repository> getMap() {
		return mapRepositories;
	}

	public void add(Repository repository) {
		mapRepositories.put(repository.getName(), repository);
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

	public Map<String, Exception> readAll() throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException {

		Cipher cipher = EncryptionProvider.getDecryptionCipher();

		File directory = new File(REPOSITORY_FOLDER_PATH);
		File[] subfiles = directory.listFiles();
		Map<String, Exception> repositoriesFailedToLoad = new TreeMap<String, Exception>();
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
						e.printStackTrace();
						repositoriesFailedToLoad.put(file.getName(), e);
					}
				}
			}
		}
		return repositoriesFailedToLoad;
	}

	public void write(Repository repository) throws WritingException {

		assert (repository != null);

		File repositoryFile = null;
		File backupFile = null;
		try {
			Cipher cipher = EncryptionProvider.getEncryptionCipher();
			File folder = new File(REPOSITORY_FOLDER_PATH);
			folder.mkdirs();
			if (!folder.exists()) {
				throw new CreateDirectoryException(folder.getCanonicalPath());
			}
			String repositoryFilename = repository.getName()
					+ REPOSITORY_EXTENSION;
			repositoryFile = new File(folder, repositoryFilename);
			backupFile = new File(folder, repositoryFilename + ".backup");
			if (repositoryFile.exists()) {
				FileAccessMethods.deleteFile(backupFile);
				repositoryFile.renameTo(backupFile);
			}
			ObjectOutputStream fWo = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(
							repositoryFile.getCanonicalPath())));
			fWo.writeObject(repository);
			fWo.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (backupFile.exists()) {
				backupFile.renameTo(repositoryFile);
			}
			throw new WritingException("Failed to save repository: "
					+ repository.getName() + "\n" + e.getMessage());
		} finally {
			if (backupFile.exists()) {
				FileAccessMethods.deleteFile(backupFile);
			}
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

	public void writeEvents(String repositoryName) throws IOException {

		Repository repository = mapRepositories.get(repositoryName);
		if (repository != null) {
			Events events = repository.getEvents();
			if (events != null) {
				String path = repository.getPath();
				File a3sFolder = new File(path + A3S_FOlDER_PATH);
				a3sFolder.mkdir();
				String eventsPath = a3sFolder.getAbsolutePath() + "/" + EVENTS;
				File file = new File(eventsPath);
				A3SFilesAccessor.writeEvents(events, file);
			}
		}
	}
}
