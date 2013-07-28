package fr.soe.a3sUpdater.dao;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FtpDAO implements DataAccessConstants {

	private DownloadCountingOutputStream dos = null;
	private FileOutputStream fos = null;
	private File folderUpdate = null;
	private File zipFile = null;

	public long getFtpFileSize(String zipFileName, FTPClient ftpClient, boolean devMode)throws IOException {

		long size = 0;
		if (devMode){
			ftpClient.changeWorkingDirectory(UPDATE_REPOSITORY_DEV);
		}else {
			ftpClient.changeWorkingDirectory(UPDATE_REPOSITORY);
		}
		System.out.println(zipFileName);
		FTPFile[] ftpFiles = ftpClient.listFiles(zipFileName);
		if (ftpFiles.length!=0) {
			size = ftpFiles[0].getSize();
		}
		return size;
	}

	public void setDownload(String zipFileName) throws IOException {
		folderUpdate = new File("update");
		folderUpdate.mkdir();
		zipFile = new File(folderUpdate.getAbsolutePath() + "/" + zipFileName);
		fos = new FileOutputStream(zipFile);
		dos = new DownloadCountingOutputStream(fos);
	}

	public void download(String zipFileName, FTPClient ftpClient,boolean devMode)
			throws IOException {
		
		if (devMode){
			ftpClient.changeWorkingDirectory(UPDATE_REPOSITORY_DEV);
		}else {
			ftpClient.changeWorkingDirectory(UPDATE_REPOSITORY);
		}
		ftpClient.retrieveFile(zipFileName, dos);
		dos.close();
		fos.close();
	}

	public DownloadCountingOutputStream getDos() {
		return dos;
	}
	
	public void removeDirectory(String path) {
		File file = new File(path);
		deleteDirectory(file);
	}
	
	public void install() throws IOException {
		
		extractToFolder(zipFile, folderUpdate);
		File f = new File(folderUpdate + "/"+ zipFile.getName().replaceAll(".zip", ""));
		f.mkdir();
		File sourceLocation = new File(f.getAbsolutePath()); 
		File targetLocation = new File(System.getProperty("user.dir"));
		copyDirectory(sourceLocation,targetLocation);
	}
	
	public void clean(){
		if (folderUpdate!=null){
			deleteDirectory(folderUpdate);
		}
	}
	
	//Business methods
	
	private void extractToFolder(File zipFile, File folder) throws IOException {

		// création de la ZipInputStream qui va servir à lire les données du
		// fichier zip
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(
				new FileInputStream(zipFile.getCanonicalFile())));

		// extractions des entrées du fichiers zip (i.e. le contenu du zip)
		ZipEntry ze = null;
		try {
			while ((ze = zis.getNextEntry()) != null) {

				// Pour chaque entrée, on crée un fichier
				// dans le répertoire de sortie "folder"
				File f = new File(folder.getCanonicalPath(), ze.getName());

				// Si l'entrée est un répertoire,
				// on le crée dans le répertoire de sortie
				// et on passe à l'entrée suivante (continue)
				if (ze.isDirectory()) {
					f.mkdirs();
					continue;
				}

				// L'entrée est un fichier, on crée une OutputStream
				// pour écrire le contenu du nouveau fichier
				f.getParentFile().mkdirs();
				OutputStream fos = new BufferedOutputStream(
						new FileOutputStream(f));

				// On écrit le contenu du nouveau fichier
				// qu'on lit à partir de la ZipInputStream
				// au moyen d'un buffer (byte[])
				try {
					try {
						final byte[] buf = new byte[8192];
						int bytesRead;
						while (-1 != (bytesRead = zis.read(buf)))
							fos.write(buf, 0, bytesRead);
					} finally {
						fos.close();
					}
				} catch (final IOException ioe) {
					// en cas d'erreur on efface le fichier
					f.delete();
					throw ioe;
				}
			}
		} finally {
			// fermeture de la ZipInputStream
			zis.close();
		}
	}

	// If targetLocation does not exist, it will be created.
	public void copyDirectory(File sourceLocation, File targetLocation)
			throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(
						targetLocation, children[i]));
			}
		} else {
			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);
			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}
}
