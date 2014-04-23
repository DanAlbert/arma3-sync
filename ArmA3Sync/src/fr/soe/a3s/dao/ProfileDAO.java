package fr.soe.a3s.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import fr.soe.a3s.constant.DefaultProfileName;
import fr.soe.a3s.domain.Profile;
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
		boolean error = false;
		if (subfiles != null) {
			for (File file : subfiles) {
				try {
					ObjectInputStream fRo = new ObjectInputStream(
							new GZIPInputStream(new FileInputStream(file)));
					Profile profile = (Profile) fRo.readObject();
					fRo.close();
					if (profile != null) {
						mapProfiles.put(profile.getName(), profile);
					}
				} catch (Exception e) {
					error = true;
					e.printStackTrace();
				}
			}
		}

		if (!mapProfiles.containsKey(DefaultProfileName.DEFAULT
				.getDescription())) {
			Profile profile = new Profile("Default");
			mapProfiles.put(profile.getName(), profile);
		}

		if (error) {
			throw new LoadingException();
		}
	}

	public void writeProfiles() throws WritingException {

		/* Clear profiles folder */
		File profilesFolder = new File(PROFILES_FOLDER_PATH);
		FileAccessMethods.deleteDirectory(profilesFolder);
		profilesFolder.mkdirs();

		boolean error = false;
		for (Iterator<String> i = mapProfiles.keySet().iterator(); i.hasNext();) {
			String profileName = i.next();
			Profile profile = mapProfiles.get(profileName);
			try {
				mapProfiles.put(profile.getName(), profile);
				String path = PROFILES_FOLDER_PATH + "/" + profile.getName()
						+ PROFILE_EXTENSION;
				File file = new File(path);
				ObjectOutputStream fWo = new ObjectOutputStream(
						new GZIPOutputStream(new FileOutputStream(file)));
				if (profile != null)
					fWo.writeObject(profile);
				fWo.close();
			} catch (Exception e) {
				e.printStackTrace();
				error = true;
			}
		}

		if (error) {
			throw new WritingException(
					"Failded to write one or more profile(s).");
		}
	}
}
