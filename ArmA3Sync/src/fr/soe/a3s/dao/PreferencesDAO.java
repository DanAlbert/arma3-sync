package fr.soe.a3s.dao;

import java.io.File;
import java.io.IOException;

import fr.soe.a3s.domain.Preferences;
import fr.soe.a3s.exception.CreateDirectoryException;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.WritingException;

public class PreferencesDAO implements DataAccessConstants {

	private static Preferences preferences = new Preferences();

	public void read() throws LoadingException {

		File file = new File(PREFERENCES_FILE_PATH);
		try {
			if (file.exists()) {
				Preferences prefs = (Preferences) A3SFilesAccessor.read(file);
				if (prefs != null) {
					preferences = prefs;
				}
			}
		} catch (Exception e) {
			throw new LoadingException("Failed to read file: "
					+ FileAccessMethods.getCanonicalPath(file));
		}
	}

	public void write() throws WritingException {

		File file = new File(PREFERENCES_FILE_PATH);
		File folder = new File(CONFIGURATION_FOLDER_PATH);
		try {
			folder.mkdirs();
			if (!folder.exists()) {
				throw new CreateDirectoryException(folder);
			}
			A3SFilesAccessor.write(preferences, file);
		} catch (IOException e) {
			e.printStackTrace();
			String message = "Failed to write file: "
					+ FileAccessMethods.getCanonicalPath(file);
			throw new WritingException(e.getMessage());
		}
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

	// http://stackoverflow.com/questions/5953525/run-java-application-at-windows-startup
	public boolean addToWindowsRegistry(boolean devMode) {

		boolean ok = true;
		
		try {
			
			String key = "Software\\Microsoft\\Windows\\CurrentVersion\\Run";
			String value = "ArmA3Sync";
			String path = "\""
					+ FileAccessMethods.getCanonicalPath(new File(""))
					+ "\\ArmA3Sync-registry-lnk.lnk" + "\"";

			WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, key,
					value, path);
		} catch (Exception e) {
			ok = false;
			String message = "Failed to write into Windows registry.";
			System.out.println(message);
			e.printStackTrace();
		}
		return ok;
	}

	public void deleteFromWindowsRegistry() {

		boolean found = true;
		String key = "Software\\Microsoft\\Windows\\CurrentVersion\\Run";
		String value = "ArmA3Sync";

		if (found) {
			try {
				WinRegistry.deleteValue(WinRegistry.HKEY_LOCAL_MACHINE, key,
						value);
			} catch (Exception e) {
			}
		}
	}
}
