package fr.soe.a3s.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import fr.soe.a3s.constant.DefaultProfileName;
import fr.soe.a3s.domain.Profile;
import fr.soe.a3s.exception.WritingException;

public class ProfileDAO implements DataAccessConstants {

	private static final Map<String, Profile> mapProfiles = new HashMap<String, Profile>();

	public Map<String, Profile> getMap() {
		return mapProfiles;
	}

	public List<String> readProfiles() {

		File directory = new File(PROFILES_FOLDER_PATH);
		File[] subfiles = directory.listFiles();
		List<String> profilesFailedToLoad = new ArrayList<String>();
		if (subfiles != null) {
			for (File file : subfiles) {
				if (file.isFile() && file.getName().contains(PROFILE_EXTENSION)) {
					try {
						ObjectInputStream fRo = new ObjectInputStream(
								new GZIPInputStream(new FileInputStream(file)));
						Profile profile = (Profile) fRo.readObject();
						fRo.close();
						if (profile != null) {
							mapProfiles.put(profile.getName(), profile);
						}
					} catch (Exception e) {
						e.printStackTrace();
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

		return profilesFailedToLoad;
	}

	public void writeProfiles() throws WritingException {

		/* Clear profiles folder */
		File profilesFolder = new File(PROFILES_FOLDER_PATH);

		File backup = null;

		if (profilesFolder.exists()) {
			try {
				backup = new File(profilesFolder.getCanonicalPath() + ".backup");
				if (backup.exists())
					FileAccessMethods.deleteDirectory(backup);
				profilesFolder.renameTo(backup);
			} catch (IOException e) {
				e.printStackTrace();
				// cancel here before it gets even worse
				throw new WritingException(
						"Failed to create a backup while saving profiles. Reason: "
								+ e.getMessage());
			}
		}

		profilesFolder.mkdirs();

		String error = null;
		try {
			// write all, or nothing at all (better be save)

			for (Profile profile : mapProfiles.values()) {
				// XXX: are there really null values in the map?
				if (profile == null)
					continue;

				String profileFilename = profile.getName() + PROFILE_EXTENSION;
				File profileFile = new File(profilesFolder, profileFilename);

				/*
				 * Write
				 */
				ObjectOutputStream fWo = new ObjectOutputStream(
						new GZIPOutputStream(new FileOutputStream(profileFile)));
				fWo.writeObject(profile);
				fWo.close();
			}

		} catch (Throwable e) {
			e.printStackTrace();
			error = "Failed to save profile(s).\n\tReason: " + e.getMessage()
					+ ".";
			// delete all the garbage
			if (profilesFolder.exists())
				FileAccessMethods.deleteDirectory(profilesFolder);

			if (backup != null) {
				// recover from backup
				backup.renameTo(profilesFolder);
			}

			// forward error message
			throw new WritingException(error);
		} finally {
			// don't need the backup anymore
			if (backup != null && backup.exists())
				FileAccessMethods.deleteDirectory(backup);
		}
	}
}
