package fr.soe.a3s.dao.repository;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Cipher;

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
import fr.soe.a3s.exception.LoadingException;
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

	public Map<String, Exception> readAll() throws LoadingException {

		Map<String, Exception> repositoriesFailedToLoad;
		try {
			Cipher cipher = EncryptionProvider.getDecryptionCipher();
			File directory = new File(REPOSITORY_FOLDER_PATH);
			File[] subfiles = directory.listFiles();
			repositoriesFailedToLoad = new TreeMap<String, Exception>();
			mapRepositories.clear();
			if (subfiles != null) {
				for (File file : subfiles) {
					if (file.isFile()
							&& file.getName().contains(REPOSITORY_EXTENSION)) {
						try {
							Repository repository = (Repository) A3SFilesAccessor
									.read(cipher, file);
							if (repository != null) {
								mapRepositories.put(repository.getName(),
										repository);
							}
						} catch (IOException e) {
							repositoriesFailedToLoad.put(file.getName(), e);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			String message = "Failded to read repository files:";
			throw new LoadingException(message + "\n" + e.getMessage());
		}

		if (!repositoriesFailedToLoad.isEmpty()) {
			String message = "Failded to read repository files:";
			for (Iterator<String> iter = repositoriesFailedToLoad.keySet()
					.iterator(); iter.hasNext();) {
				String repositoryName = iter.next();
				Exception e = repositoriesFailedToLoad.get(repositoryName);
				message = message + "\n" + " - " + repositoryName + ": "
						+ e.getMessage();
			}
			throw new LoadingException(message);
		}

		return repositoriesFailedToLoad;
	}

	public void write(Repository repository) throws WritingException {

		assert (repository != null);

		File folder = new File(REPOSITORY_FOLDER_PATH);
		String repositoryFilename = repository.getName().replaceAll(" ", "")
				+ REPOSITORY_EXTENSION;
		File repositoryFile = new File(folder, repositoryFilename);
		File backupFile = new File(folder, repositoryFilename + ".backup");

		try {
			folder.mkdirs();
			if (!folder.exists()) {
				throw new CreateDirectoryException(folder);
			}
			if (repositoryFile.exists()) {
				FileAccessMethods.deleteFile(backupFile);
				repositoryFile.renameTo(backupFile);
			}
			Cipher cipher = EncryptionProvider.getEncryptionCipher();
			A3SFilesAccessor.write(repository, cipher, repositoryFile);
		} catch (Exception e) {
			e.printStackTrace();
			if (backupFile.exists()) {
				backupFile.renameTo(repositoryFile);
			}
			String message = "Failed to write file: "
					+ FileAccessMethods.getCanonicalPath(repositoryFile);
			throw new WritingException(e.getMessage());
		} finally {
			if (backupFile.exists()) {
				FileAccessMethods.deleteFile(backupFile);
			}
		}
	}

	public SyncTreeDirectory readSync(Repository repository) throws IOException {

		assert (repository != null);

		String path = repository.getPath();
		String syncPath = path + "/" + SYNC_FILE_PATH;
		File file = new File(syncPath);
		SyncTreeDirectory sync = (SyncTreeDirectory) A3SFilesAccessor
				.read(file);
		return sync;
	}

	public ServerInfo readServerInfo(Repository repository) throws IOException {

		assert (repository != null);

		String path = repository.getPath();
		String serverInfoPath = path + "/" + SERVERINFO_FILE_PATH;
		File file = new File(serverInfoPath);
		ServerInfo serverInfo = (ServerInfo) A3SFilesAccessor.read(file);
		return serverInfo;
	}

	public Changelogs readChangelogs(Repository repository) throws IOException {

		assert (repository != null);

		String path = repository.getPath();
		String changelogsPath = path + "/" + CHANGELOGS_FILE_PATH;
		File file = new File(changelogsPath);
		Changelogs changelogs = (Changelogs) A3SFilesAccessor.read(file);
		return changelogs;
	}

	public AutoConfig readAutoConfig(Repository repository) throws IOException {

		assert (repository != null);

		String path = repository.getPath();
		String autocOnfigPath = path + "/" + AUTOCONFIG_FILE_PATH;
		File file = new File(autocOnfigPath);
		AutoConfig autoconfig = (AutoConfig) A3SFilesAccessor.read(file);
		return autoconfig;
	}

	public Events readEvents(Repository repository) throws IOException {

		String path = repository.getPath();
		String eventsPath = path + "/" + EVENTS_FILE_PATH;
		File file = new File(eventsPath);
		Events events = (Events) A3SFilesAccessor.read(file);
		return events;
	}

	public void writeEvents(Repository repository) throws WritingException {

		assert (repository != null);

		Events events = repository.getEvents();
		if (events != null) {
			String path = repository.getPath();
			File a3sFolder = new File(path + A3S_FOlDER_PATH);
			File file = new File(a3sFolder, EVENTS);
			try {
				a3sFolder.mkdir();
				if (!a3sFolder.exists()) {
					throw new CreateDirectoryException(a3sFolder);
				}
				A3SFilesAccessor.write(events, file);
			} catch (IOException e) {
				e.printStackTrace();
				String message = "Failed to write file: "
						+ FileAccessMethods.getCanonicalPath(file);
				throw new WritingException(e.getMessage());
			}
		}
	}
}
