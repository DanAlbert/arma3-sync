package fr.soe.a3s.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import fr.soe.a3s.domain.AutoConfig;
import fr.soe.a3s.exception.CheckException;

public class CommonDAO implements DataAccessConstants {

	/* Data */
	private List<File> extractedFiles;

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

	public int extractBikeys(String sourceDirectoryPath,
			String targetDirectoryPath) throws CheckException, IOException {

		if (targetDirectoryPath == null) {
			throw new CheckException("Source directory is empty!");
		} else if (sourceDirectoryPath.isEmpty()) {
			throw new CheckException("Source directory is empty!");
		} else if (!new File(sourceDirectoryPath).exists()) {
			throw new CheckException("Source directory does not exists!");
		} else if (targetDirectoryPath == null) {
			throw new CheckException("Target directory is empty!");
		} else if (targetDirectoryPath.isEmpty()) {
			throw new CheckException("Target directory is empty!");
		} else if (!new File(targetDirectoryPath).exists()) {
			throw new CheckException("Target directory does not exists!");
		} else if (!Files.isWritable(FileSystems.getDefault().getPath(
				targetDirectoryPath))) {// Check write permissions on target
										// directory
			throw new CheckException("Can't write on target directory!");
		} else {
			File sourceDirectory = new File(sourceDirectoryPath);
			File targetDirectory = new File(targetDirectoryPath);
			extractedFiles = new ArrayList<File>();
			extractBikeyFiles(sourceDirectory);
			if (extractedFiles.isEmpty()) {
				return 0;
			} else {
				for (File file : extractedFiles) {
					FileAccessMethods.copyFile(file, targetDirectory);
				}
				int count = extractedFiles.size();
				extractedFiles = null;
				return count;
			}
		}
	}

	private void extractBikeyFiles(File file) {

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
					extractedFiles.add(file);
				}
			}
		}
	}
}
