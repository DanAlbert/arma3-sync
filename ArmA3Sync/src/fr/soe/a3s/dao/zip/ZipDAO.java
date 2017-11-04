package fr.soe.a3s.dao.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.FileAccessMethods;

public class ZipDAO implements DataAccessConstants {

	private static int BUFFER_SIZE = 4096;
	private boolean active = false;
	private boolean canceled = false;

	/**
	 * http://stackoverflow.com/questions/21897286/how-to-extract-files-from-a-7
	 * -zip-stream-in-java-without-store-it-on-hard-disk
	 * 
	 * @param zipFile
	 * @throws Exception
	 */
	public void unZip(File zipFile) throws Exception {

		System.out.println("Uncompressing file: " + zipFile.getAbsolutePath());

		this.active = true;

		final File targetFile = new File(zipFile.getParentFile().getAbsolutePath() + "/" + zipFile.getName()
				.substring(0, zipFile.getName().length() - DataAccessConstants.ZIP_EXTENSION.length()));

		int index = zipFile.getName().lastIndexOf(".");
		String extension = "";
		if (index != -1) {
			extension = zipFile.getName().substring(index);
		}

		if (!zipFile.exists()) {
			throw new FileNotFoundException("File not found on disk: " + zipFile.getAbsolutePath());

		} else if (!extension.equals(DataAccessConstants.ZIP_EXTENSION)) {
			throw new IOException("Can not unzip file - bad file extension");
		} else {
			if (targetFile.exists()) {
				FileAccessMethods.deleteFile(targetFile);
			}

			ZipInputStream zis = null;
			FileOutputStream fos = null;

			try {
				zis = new ZipInputStream(new FileInputStream(zipFile));
				zis.getNextEntry();
				fos = new FileOutputStream(targetFile);

				byte[] buffer = new byte[BUFFER_SIZE];
				int len;
				while ((len = zis.read(buffer)) > 0 && !canceled) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				zis.closeEntry();
				zis.close();

				this.active = false;

			} catch (Exception e) {
				this.active = false;
				if (!canceled) {
					FileAccessMethods.deleteFile(zipFile);
					String message = "Failed to unzip file: " + zipFile.getAbsolutePath();
					System.out.println(message);
					e.printStackTrace();
					if (e.getMessage() == null) {
						throw new IOException(message);
					} else {
						throw new IOException(message + "\n" + e.getMessage());
					}
				}
			} finally {
				if (zis != null) {
					zis.close();
				}
				if (fos != null) {
					fos.close();
				}
				if (canceled) {
					FileAccessMethods.deleteFile(targetFile);
				} else {
					FileAccessMethods.deleteFile(zipFile);
				}
			}
		}

	}

	/**
	 * http://stackoverflow.com/questions/28229095/apache-commons-compress-using
	 * -7zip
	 * 
	 * @param sourceFile
	 * @return
	 * @throws Exception
	 */
	public long zip(File sourceFile) throws Exception {

		this.active = true;
		long compressedFileSize = 0;

		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		FileInputStream in = null;

		if (!sourceFile.exists()) {
			throw new FileNotFoundException("File not found on disk: " + sourceFile.getAbsolutePath());
		} else {

			final File zipFile = new File(sourceFile.getParentFile().getAbsolutePath() + "/" + sourceFile.getName()
					+ DataAccessConstants.ZIP_EXTENSION);

			if (zipFile.exists()) {
				FileAccessMethods.deleteFile(zipFile);
			}

			try {
				fos = new FileOutputStream(zipFile);
				zos = new ZipOutputStream(fos);
				ZipEntry ze = new ZipEntry(sourceFile.getName());
				zos.putNextEntry(ze);
				zos.setLevel(Deflater.BEST_COMPRESSION);
				in = new FileInputStream(sourceFile);

				byte[] buffer = new byte[BUFFER_SIZE];
				int len;
				while ((len = in.read(buffer)) > 0 && !canceled) {
					zos.write(buffer, 0, len);
				}

				in.close();
				fos.close();
				zos.closeEntry();
				zos.close();

				if (canceled) {
					FileAccessMethods.deleteFile(zipFile);
					compressedFileSize = 0;
				} else {
					compressedFileSize = zipFile.length();
				}

				this.active = false;

			} catch (Exception e) {
				this.active = false;
				if (!canceled) {
					String message = "Failed to zip file: " + sourceFile.getAbsolutePath();
					System.out.println(message);
					e.printStackTrace();
					if (e.getMessage() == null) {
						throw new IOException(message);
					} else {
						throw new IOException(message + "\n" + e.getMessage());
					}
				}
			} finally {
				if (in != null) {
					in.close();
				}
				if (zos != null) {
					zos.close();
				}
				if (fos != null) {
					fos.close();
				}
			}
		}
		return compressedFileSize;
	}

	public boolean isActive() {
		return this.active;
	}

	public void cancel() {
		this.canceled = true;
	}

	public void setActive(boolean value) {
		this.active = value;
	}
}
