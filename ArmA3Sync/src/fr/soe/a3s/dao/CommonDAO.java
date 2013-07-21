package fr.soe.a3s.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import fr.soe.a3s.domain.AutoConfig;

public class CommonDAO implements DataAccessConstants{

	
	public AutoConfig importAutoConfig(String path) throws FileNotFoundException, IOException, ClassNotFoundException{
		
		File file = new File(path);
		AutoConfig autoConfig = null;
		if (file.exists()) {
			ObjectInputStream fRo = new ObjectInputStream(
					new GZIPInputStream(new FileInputStream(
							file.getAbsolutePath())));
			autoConfig = (AutoConfig) fRo.readObject();
			fRo.close();
		}
		return autoConfig;
	}
	
	public void exportAutoConfig(AutoConfig autoConfig,String path) throws FileNotFoundException, IOException{
		
		ObjectOutputStream fWo = new ObjectOutputStream(
				new GZIPOutputStream(new FileOutputStream(
						path + "/auto-config" + AUTOCONFIG_EXTENSION)));
		fWo.writeObject(autoConfig);
		fWo.close();
		
	}
	
}
