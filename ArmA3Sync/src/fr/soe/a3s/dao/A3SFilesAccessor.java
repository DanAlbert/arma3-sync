package fr.soe.a3s.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;

import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.Changelogs;
import fr.soe.a3s.domain.repository.Events;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;

public class A3SFilesAccessor implements DataAccessConstants {

	protected static final String FILE_CORRUPTED = "The file may be corrupted.";

	public static Object read(File file) throws IOException {

		assert (file != null);

		Object object = null;
		ObjectInputStream fRo = null;
		try {
			fRo = new ObjectInputStream(new GZIPInputStream(
					new FileInputStream(file)));
			object = fRo.readObject();
		} catch (IOException e) {
			e.printStackTrace();
			String message = e.getMessage();
			if (e instanceof ZipException) {
				message = message + "\n" + FILE_CORRUPTED;
			}
			throw new IOException(message);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} finally {
			if (fRo != null) {
				fRo.close();
			}
		}
		return object;
	}

	public static Object read(Cipher cipher, File file) throws IOException {

		assert (file != null);

		Object object = null;
		ObjectInputStream fRo = null;
		try {
			fRo = new ObjectInputStream(new GZIPInputStream(
					new FileInputStream(file)));
			SealedObject sealedObject = (SealedObject) fRo.readObject();
			object = sealedObject.getObject(cipher);
		} catch (IOException e) {
			e.printStackTrace();
			String message = e.getMessage();
			if (e instanceof ZipException) {
				message = message + "\n" + FILE_CORRUPTED;
			}
			throw new IOException(message);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} finally {
			if (fRo != null) {
				fRo.close();
			}
		}
		return object;
	}

	public static void write(Serializable object, File file) throws IOException {

		assert (object != null);

		ObjectOutputStream fWo = null;
		try {
			fWo = new ObjectOutputStream(new GZIPOutputStream(
					new FileOutputStream(file)));
			fWo.writeObject(object);
		} finally {
			if (fWo != null) {
				fWo.close();
			}
		}
	}

	public static void write(Serializable object, Cipher cipher, File file)
			throws IllegalBlockSizeException, IOException {

		assert (object != null);

		ObjectOutputStream fWo = null;
		try {
			SealedObject sealedObject = new SealedObject(object, cipher);
			fWo = new ObjectOutputStream(new GZIPOutputStream(
					new FileOutputStream(file)));
			if (sealedObject != null) {
				fWo.writeObject(sealedObject);
			}
		} finally {
			if (fWo != null) {
				fWo.close();
			}
		}
	}
}
