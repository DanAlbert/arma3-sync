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
import java.util.zip.ZipException;

import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.Changelogs;
import fr.soe.a3s.domain.repository.Events;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;

public class A3SFilesAccessor implements DataAccessConstants {

	protected static final String FILE_CORRUPTED = "The file appears to be corrupted.";

	public static SyncTreeDirectory readSyncFile(File file) throws IOException {

		SyncTreeDirectory sync = null;
		ObjectInputStream fRo = null;
		try {
			if (file.exists()) {
				fRo = new ObjectInputStream(new GZIPInputStream(
						new FileInputStream(file)));
				sync = (SyncTreeDirectory) fRo.readObject();
			}
		} catch (IOException e) {
			String message = "Failed to read file on disk " + SYNC_FILE_PATH;
			if (e instanceof ZipException) {
				message = message + "\n" + FILE_CORRUPTED;
			} else if (e.getMessage() != null) {
				message = message + "\n" + e.getMessage();
			}
			throw new IOException(message);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			if (fRo != null) {
				fRo.close();
			}
		}
		return sync;
	}

	public static ServerInfo readServerInfoFile(File file) throws IOException {

		ServerInfo serverInfo = null;
		ObjectInputStream fRo = null;
		try {
			if (file.exists()) {
				fRo = new ObjectInputStream(new GZIPInputStream(
						new FileInputStream(file)));
				serverInfo = (ServerInfo) fRo.readObject();
			}
		} catch (IOException e) {
			String message = "Failed to read file on disk "
					+ SERVERINFO_FILE_PATH;
			if (e instanceof ZipException) {
				message = message + "\n" + FILE_CORRUPTED;
			} else if (e.getMessage() != null) {
				message = message + "\n" + e.getMessage();
			}
			throw new IOException(message);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			if (fRo != null) {
				fRo.close();
			}
		}
		return serverInfo;
	}

	public static Changelogs readChangelogsFile(File file) throws IOException {

		Changelogs changelogs = null;
		ObjectInputStream fRo = null;
		try {
			if (file.exists()) {
				fRo = new ObjectInputStream(new GZIPInputStream(
						new FileInputStream(file)));
				changelogs = (Changelogs) fRo.readObject();
				fRo.close();
			}
		} catch (IOException e) {
			String message = "Failed to read file on disk "
					+ CHANGELOGS_FILE_PATH;
			if (e instanceof ZipException) {
				message = message + "\n" + FILE_CORRUPTED;
			} else if (e.getMessage() != null) {
				message = message + "\n" + e.getMessage();
			}
			throw new IOException(message);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			if (fRo != null) {
				fRo.close();
			}
		}
		return changelogs;
	}

	public static Events readEventsFile(File file) throws IOException {

		Events events = null;
		ObjectInputStream fRo = null;
		try {
			if (file.exists()) {
				fRo = new ObjectInputStream(new GZIPInputStream(
						new FileInputStream(file)));
				events = (Events) fRo.readObject();
			}
		} catch (IOException e) {
			String message = "Failed to read file on disk " + EVENTS_FILE_PATH;
			if (e instanceof ZipException) {
				message = message + "\n" + FILE_CORRUPTED;
			} else if (e.getMessage() != null) {
				message = message + "\n" + e.getMessage();
			}
			throw new IOException(message);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			if (fRo != null) {
				fRo.close();
			}
		}
		return events;
	}

	public static AutoConfig readAutoConfigFile(File file) throws IOException {

		AutoConfig autoConfig = null;
		ObjectInputStream fRo = null;
		try {
			if (file.exists()) {
				fRo = new ObjectInputStream(new GZIPInputStream(
						new FileInputStream(file)));
				autoConfig = (AutoConfig) fRo.readObject();
				fRo.close();
			}
		} catch (IOException e) {
			String message = "Failed to read file on disk "
					+ AUTOCONFIG_FILE_PATH;
			if (e instanceof ZipException) {
				message = message + "\n" + FILE_CORRUPTED;
			} else if (e.getMessage() != null) {
				message = message + "\n" + e.getMessage();
			}
			throw new IOException(message);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			if (fRo != null) {
				fRo.close();
			}
		}
		return autoConfig;
	}

	public static void writeSync(SyncTreeDirectory sync, File syncFile)
			throws IOException {

		if (sync != null) {
			ObjectOutputStream fWo = null;
			try {
				fWo = new ObjectOutputStream(new GZIPOutputStream(
						new FileOutputStream(syncFile.getAbsolutePath())));
				fWo.writeObject(sync);
			} catch (IOException e) {
				String message = "Failed to write file on disk "
						+ SYNC_FILE_PATH;
				if (e instanceof ZipException) {
					message = message + "\n" + FILE_CORRUPTED;
				} else if (e.getMessage() != null) {
					message = message + "\n" + e.getMessage();
				}
				throw new IOException(message);
			} finally {
				if (fWo != null) {
					fWo.close();
				}
			}
		}
	}

	public static void writeServerInfo(ServerInfo serverInfo,
			File serverInfoFile) throws IOException {

		if (serverInfo != null) {
			ObjectOutputStream fWo = null;
			try {
				fWo = new ObjectOutputStream(new GZIPOutputStream(
						new FileOutputStream(serverInfoFile.getAbsolutePath())));
				fWo.writeObject(serverInfo);
			} catch (IOException e) {
				String message = "Failed to write file on disk "
						+ SERVERINFO_FILE_PATH;
				if (e instanceof ZipException) {
					message = message + "\n" + FILE_CORRUPTED;
				} else if (e.getMessage() != null) {
					message = message + "\n" + e.getMessage();
				}
				throw new IOException(message);
			} finally {
				if (fWo != null) {
					fWo.close();
				}
			}
		}
	}

	public static void writeChangelogs(Changelogs changelogs,
			File changelogsFile) throws IOException {

		if (changelogs != null) {
			ObjectOutputStream fWo = null;
			try {
				fWo = new ObjectOutputStream(new GZIPOutputStream(
						new FileOutputStream(changelogsFile.getAbsolutePath())));
				fWo.writeObject(changelogs);
			} catch (IOException e) {
				String message = "Failed to write file on disk "
						+ CHANGELOGS_FILE_PATH;
				if (e instanceof ZipException) {
					message = message + "\n" + FILE_CORRUPTED;
				} else if (e.getMessage() != null) {
					message = message + "\n" + e.getMessage();
				}
				throw new IOException(message);
			} finally {
				if (fWo != null) {
					fWo.close();
				}
			}
		}
	}

	public static void writeAutoConfig(AutoConfig autoConfig,
			File autoConfigFile) throws IOException {

		if (autoConfig != null) {
			ObjectOutputStream fWo = null;
			try {
				fWo = new ObjectOutputStream(new GZIPOutputStream(
						new FileOutputStream(autoConfigFile.getAbsolutePath())));
				fWo.writeObject(autoConfig);
			} catch (IOException e) {
				String message = "Failed to write file on disk "
						+ AUTOCONFIG_FILE_PATH;
				if (e instanceof ZipException) {
					message = message + "\n" + FILE_CORRUPTED;
				} else if (e.getMessage() != null) {
					message = message + "\n" + e.getMessage();
				}
				throw new IOException(message);
			} finally {
				if (fWo != null) {
					fWo.close();
				}
			}
		}
	}

	public static void writeEvents(Events events, File eventsFile)
			throws IOException {

		if (events != null) {
			ObjectOutputStream fWo = null;
			try {
				fWo = new ObjectOutputStream(new GZIPOutputStream(
						new FileOutputStream(eventsFile.getAbsolutePath())));
				fWo.writeObject(events);
			} catch (IOException e) {
				String message = "Failed to write file on disk "
						+ EVENTS_FILE_PATH;
				if (e instanceof ZipException) {
					message = message + "\n" + FILE_CORRUPTED;
				} else if (e.getMessage() != null) {
					message = message + "\n" + e.getMessage();
				}
				throw new IOException(message);
			} finally {
				if (fWo != null) {
					fWo.close();
				}
			}
		}
	}
}
