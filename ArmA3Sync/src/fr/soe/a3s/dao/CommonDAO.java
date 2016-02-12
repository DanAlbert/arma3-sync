package fr.soe.a3s.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import fr.soe.a3s.domain.AutoConfig;
import fr.soe.a3s.exception.CheckException;

public class CommonDAO implements DataAccessConstants {

	/* Test */
	private boolean canceled = false;
	/* Data */
	private List<File> extractedBikeyFiles = null;

	public AutoConfig importAutoConfig(String path)
			throws FileNotFoundException, IOException, ClassNotFoundException {

		File file = new File(path);
		AutoConfig autoConfig = null;
		if (file.exists()) {
			ObjectInputStream fRo = new ObjectInputStream(new GZIPInputStream(
					new FileInputStream(file.getAbsolutePath())));
			autoConfig = (AutoConfig) fRo.readObject();
			fRo.close();
		}
		return autoConfig;
	}

	public void exportAutoConfig(AutoConfig autoConfig, String path)
			throws FileNotFoundException, IOException {

		ObjectOutputStream fWo = new ObjectOutputStream(new GZIPOutputStream(
				new FileOutputStream(path + "/auto-config"
						+ AUTOCONFIG_EXTENSION)));
		fWo.writeObject(autoConfig);
		fWo.close();
	}

	/**
	 * 
	 * @param sourceDirectoryPath
	 *            not null
	 * @param targetDirectoryPath
	 *            not null
	 * @throws CheckException
	 * @throws IOException
	 */
	public int extractBikeys(String sourceDirectoryPath,
			String targetDirectoryPath) throws IOException {

		File sourceDirectory = new File(sourceDirectoryPath);
		File targetDirectory = new File(targetDirectoryPath);
		extractedBikeyFiles = new ArrayList<File>();
		extractBikeyFiles(sourceDirectory);
		if (extractedBikeyFiles.isEmpty()) {
			return 0;
		} else {
			for (File sourceFile : extractedBikeyFiles) {
				File targetFile = new File(targetDirectory.getAbsolutePath()
						+ "/" + sourceFile.getName());
				FileAccessMethods.copyFile(sourceFile, targetFile);
			}
			int count = extractedBikeyFiles.size();
			extractedBikeyFiles = null;
			return count;
		}
	}

	private void extractBikeyFiles(File file) {

		if (!canceled) {
			if (file.isDirectory()) {
				File[] subFiles = file.listFiles();
				if (subFiles != null) {
					for (File f : subFiles) {
						extractBikeyFiles(f);
					}
				}
			} else {
				int index = file.getName().lastIndexOf(".");
				if (index != -1) {
					String extension = file.getName().substring(index);
					if (extension.equalsIgnoreCase(BIKEY)) {
						extractedBikeyFiles.add(file);
					}
				}
			}
		}
	}

	public void writeLog(String print, String path) throws IOException {

		PrintWriter fWo = new PrintWriter(new FileWriter(
				new File(path).getAbsolutePath()));
		fWo.println(print);
		fWo.close();
	}

	public void cancel() {
		this.canceled = true;
	}
}
