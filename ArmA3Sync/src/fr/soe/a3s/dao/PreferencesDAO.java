package fr.soe.a3s.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import fr.soe.a3s.domain.Preferences;
import fr.soe.a3s.exception.CreateDirectoryException;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.WritingException;

public class PreferencesDAO implements DataAccessConstants {

	private static Preferences preferences = new Preferences();

	public void read() throws LoadingException {

		try {
			File file = new File(PREFERENCES_FILE_PATH);
			if (file.exists()) {
				ObjectInputStream fRo = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(
								file.getAbsolutePath())));
				Preferences prefs = (Preferences) fRo.readObject();
				fRo.close();
				if (prefs != null) {
					preferences = prefs;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new LoadingException();
		}
	}

	public void write() throws WritingException {

		try {
			File folder = new File(CONFIGURATION_FOLDER_PATH);
			folder.mkdirs();
			if (!folder.exists()) {
				throw new CreateDirectoryException(folder);
			}
			File file = new File(PREFERENCES_FILE_PATH);
			ObjectOutputStream fWo = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(
							file.getCanonicalPath())));
			fWo.writeObject(preferences);
			fWo.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new WritingException("Failed to save preferences." + "\n"
					+ e.getMessage());
		}
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

}
