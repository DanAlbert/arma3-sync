package fr.soe.a3s.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

			backup = new File(profilesFolder.getAbsolutePath() + ".backup");
			if (backup.exists()) {
				boolean ok = FileAccessMethods.deleteDirectory(backup);
				if (!ok) {
					throw new WritingException(
							"Failed to create a backup file while saving profiles."
									+ "\n"
									+ " Reason: Write access permission denied on "
									+ backup.getPath());
				}
			}

			boolean ok = profilesFolder.renameTo(backup);
			if (!ok) {
				throw new WritingException(
						"Failed to create a backup file while saving profiles."
								+ "\n" + " Reason: Write access is denied on "
								+ profilesFolder.getPath());
			}
		}

		profilesFolder.mkdirs();

		String error = null;
		ObjectOutputStream fWo = null;
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
				fWo = new ObjectOutputStream(new GZIPOutputStream(
						new FileOutputStream(profileFile)));
				fWo.writeObject(profile);
				fWo.close();
			}

			// don't need the backup anymore
			if (backup != null && backup.exists()) {
				FileAccessMethods.deleteDirectory(backup);
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
		}
	}
}
