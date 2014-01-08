package fr.soe.a3s.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;

import fr.soe.a3s.domain.repository.Events;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.WritingException;

public class RepositoryDAO implements DataAccessConstants {

	private static final Map<String, Repository> mapRepositories = new HashMap<String, Repository>();
	
	public Map<String, Repository> getMap() {
		return mapRepositories;
	}

	public void remove(String repositoryName) {
		File directory = new File(REPOSITORY_FOLDER_PATH);
		Repository repository = mapRepositories.get(repositoryName);
		String concatName = repository.getName().replaceAll(" ", "");
		File[] subfiles = directory.listFiles();
		if (subfiles!=null){
			for (File file : subfiles) {
				if (file.getName().contains(concatName)) {
					FileAccessMethods.deleteFile(file);
				}
			}
		}
	}

	public void readAll(Cipher cipher) throws LoadingException {

		File directory = new File(REPOSITORY_FOLDER_PATH);
		File[] subfiles = directory.listFiles();
		boolean error = false;
		mapRepositories.clear();
		if (subfiles!=null){
			for (File file : subfiles) {
				try {
					ObjectInputStream fRo = new ObjectInputStream(
							new GZIPInputStream(new FileInputStream(file)));
					SealedObject sealedObject = (SealedObject) fRo.readObject();
					Repository repository = (Repository) sealedObject
							.getObject(cipher);
					fRo.close();
					if (repository != null) {
						mapRepositories.put(repository.getName(), repository);
					}
				} catch (Exception e) {
					error = true;
					e.printStackTrace();
				}
			}
		}

		if (error) {
			throw new LoadingException();
		}
	}
	
	public void write(Cipher cipher, String repositoryName)
			throws WritingException {

		Repository repository = mapRepositories.get(repositoryName);
		try {
			if (repository != null) {
				String concatName = repository.getName().replaceAll(" ", "");
				SealedObject sealedObject = new SealedObject(repository, cipher);
				String filePath = REPOSITORY_FOLDER_PATH + "/" + concatName
						+ REPOSITORY_EXTENSION;
				ObjectOutputStream fWo = new ObjectOutputStream(
						new GZIPOutputStream(new FileOutputStream(filePath)));
				if (sealedObject != null)
					fWo.writeObject(sealedObject);
				fWo.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WritingException();
		}
	}
	
	public ServerInfo readServerInfo(String repositoryName) throws FileNotFoundException, IOException, ClassNotFoundException{
		
		ServerInfo serverInfo = null;
		Repository repository = mapRepositories.get(repositoryName);
		if (repository != null) {
			String path = repository.getPath();
			String serverInfoPath = path + "/" + SERVERINFO_FILE_PATH;
			File file = new File(serverInfoPath);
			if (file.exists()){
				ObjectInputStream fRo = new ObjectInputStream(new GZIPInputStream(
						new FileInputStream(file)));
				serverInfo = (ServerInfo) fRo.readObject();
				fRo.close();
			}
		}
		return serverInfo;
	}

	public SyncTreeDirectory readSync(String repositoryName)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		
		SyncTreeDirectory sync = null;
		Repository repository = mapRepositories.get(repositoryName);
		if (repository != null) {
			String path = repository.getPath();
			String syncPath = path + "/" + SYNC_FILE_PATH;
			File file = new File(syncPath);
			if (file.exists()){
				ObjectInputStream fRo = new ObjectInputStream(new GZIPInputStream(
						new FileInputStream(file)));
				sync = (SyncTreeDirectory) fRo.readObject();
				fRo.close();
			}
		}
		return sync;
	}

	public void saveToDiskEvents(Events events,String repositoryPath) throws WritingException {
		
		try {
			File file = new File(repositoryPath);
			File a3sFolder = new File(repositoryPath + A3S_FOlDER_PATH);
			a3sFolder.mkdirs();
			File eventsFile = new File(file.getAbsolutePath()
					+ EVENTS_FILE_PATH);
			if (events != null) {
				ObjectOutputStream fWo = new ObjectOutputStream(
						new GZIPOutputStream(new FileOutputStream(
								eventsFile.getAbsolutePath())));
				fWo.writeObject(events);
				fWo.close();
			}
		} catch (Exception e) {
			throw new WritingException(e.getMessage());
		}
	}
}
