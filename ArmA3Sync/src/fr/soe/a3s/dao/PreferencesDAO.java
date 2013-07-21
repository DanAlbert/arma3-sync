package fr.soe.a3s.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import fr.soe.a3s.domain.Preferences;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.WritingException;

public class PreferencesDAO implements DataAccessConstants{
	
	private static Preferences preferences = new Preferences();
	
	public void read() throws LoadingException {

		try {
			File file = new File(PREFERENCES_FILE_PATH);
			if (file.exists()){
				ObjectInputStream fRo = new ObjectInputStream(new GZIPInputStream(
						new FileInputStream(file.getAbsolutePath())));
				Preferences prefs = (Preferences) fRo.readObject();
				fRo.close();
				if (prefs!=null){
					preferences = prefs;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new LoadingException();
		}
	}

	public void write() throws WritingException  {

		try {
			ObjectOutputStream fWo = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(PREFERENCES_FILE_PATH)));
			fWo.writeObject(preferences);
			fWo.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new WritingException("Failded to write preferences.");
		}
	}
	
	public Preferences getPreferences(){
		return preferences;
	}
	
	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

}
