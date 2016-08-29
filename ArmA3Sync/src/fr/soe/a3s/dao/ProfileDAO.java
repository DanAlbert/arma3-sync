package fr.soe.a3s.dao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.soe.a3s.constant.DefaultProfileName;
import fr.soe.a3s.domain.Profile;
import fr.soe.a3s.exception.CreateDirectoryException;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.WritingException;

public class ProfileDAO implements DataAccessConstants {

	private static final Map<String, Profile> mapProfiles = new HashMap<String, Profile>();

	public Map<String, Profile> getMap() {
		return mapProfiles;
	}

	public void readProfiles() throws LoadingException {

		File directory = new File(PROFILES_FOLDER_PATH);
		File[] subfiles = directory.listFiles();
		List<String> profilesFailedToLoad = new ArrayList<String>();
		if (subfiles != null) {
			for (File file : subfiles) {
				if (file.isFile() && file.getName().contains(PROFILE_EXTENSION)) {
					try {
						Profile profile = (Profile) A3SFilesAccessor.read(file);
						if (profile != null) {
							mapProfiles.put(profile.getName(), profile);
						}
					} catch (Exception e) {
						profilesFailedToLoad.add(file.getName());
					}
				}
			}
		}

		if (!mapProfiles.containsKey(DefaultProfileName.DEFAULT
				.getDescription())) {
			Profile profile = new Profile("Default");
			mapProfiles.put(profile.getName(), profile);
		}

		if (!profilesFailedToLoad.isEmpty()) {
			String message = "Failded to load profiles:";
			for (String name : profilesFailedToLoad) {
				message = message + "\n" + " - " + name;
			}
			throw new LoadingException(message);
		}
	}

	public void write(Profile profile) throws WritingException {

		assert (profile != null);

		File folder = new File(PROFILES_FOLDER_PATH);
		String profileFilename = profile.getName() + PROFILE_EXTENSION;
		File profileFile = new File(folder, profileFilename);
		File backupFile = new File(folder, profileFilename + ".backup");
		try {
			folder.mkdir();
			if (!folder.exists()) {
				throw new CreateDirectoryException(folder);
			}
			if (profileFile.exists()) {
				FileAccessMethods.deleteFile(backupFile);
				profileFile.renameTo(backupFile);
			}
			A3SFilesAccessor.write(profile, profileFile);
		} catch (IOException e) {
			e.printStackTrace();
			if (backupFile.exists()) {
				backupFile.renameTo(profileFile);
			}
			String message = "Failed to write file: "
					+ FileAccessMethods.getCanonicalPath(profileFile);
			throw new WritingException(e.getMessage());
		} finally {
			if (backupFile.exists()) {
				FileAccessMethods.deleteFile(backupFile);
			}
		}
	}

	public void delete(Profile profile) {

		assert (profile != null);

		File folder = new File(PROFILES_FOLDER_PATH);
		String profileFilename = profile.getName() + PROFILE_EXTENSION;
		File profileFile = new File(folder, profileFilename);
		FileAccessMethods.deleteFile(profileFile);
	}
}
