package fr.soe.a3s.dao;

import java.io.File;
import java.io.FileInputStream;
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
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.WritingException;

public class CommonDAO implements DataAccessConstants {

	/* Test */
	private boolean canceled = false;
	/* Data */
	private List<File> extractedBikeyFiles = null;

	public AutoConfig importAutoConfig(File file) throws LoadingException {

		assert (file != null);

		AutoConfig autoConfig = null;
		try {
			autoConfig = (AutoConfig) A3SFilesAccessor.read(file);
		} catch (IOException e) {
			throw new LoadingException("Failed to read autoconfig file." + "\n"
					+ e.getMessage());
		}
		return autoConfig;
	}

	public void exportAutoConfig(AutoConfig autoConfig, File file)
			throws WritingException {

		assert (autoConfig != null);

		try {
			A3SFilesAccessor.write(autoConfig, file);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WritingException("Failed to write autoconfig file."
					+ "\n" + e.getMessage());
		}
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
