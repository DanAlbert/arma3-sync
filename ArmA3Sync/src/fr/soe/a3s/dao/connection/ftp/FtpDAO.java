package fr.soe.a3s.dao.connection.ftp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.connection.RemoteFile;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.exception.ConnectionExceptionFactory;
import fr.soe.a3s.exception.IncompleteFileTransferException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class FtpDAO extends AbstractConnexionDAO {

	private FTPClient ftpClient;

	@Override
	protected void connect(AbstractProtocole protocol, RemoteFile remoteFile,
			long startOffset, long endOffset) throws IOException {

		try {
			ftpClient = new FTPClient();

			String port = protocol.getPort();
			String login = protocol.getLogin();
			String password = protocol.getPassword();
			String hostname = protocol.getHostname();

			// Set connection and read time out
			int connectionTimeOutValue = Integer.parseInt(protocol
					.getConnectionTimeOut());
			if (connectionTimeOutValue != 0) {
				ftpClient.setConnectTimeout(connectionTimeOutValue);
			}
			int readTimeOutValue = Integer.parseInt(protocol.getReadTimeOut());
			if (readTimeOutValue != 0) {
				ftpClient.setDataTimeout(readTimeOutValue);
			}

			// Set buffer size
			ftpClient.setBufferSize(1048576);// 1024*1024

			ftpClient.connect(hostname, Integer.parseInt(port));
			boolean isLoged = ftpClient.login(login, password);

			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);// binary transfer
			ftpClient.enterLocalPassiveMode();// passive mode

			ftpClient.setRestartOffset(startOffset);// start offset

			int reply = ftpClient.getReplyCode();

			if (!isLoged) {
				throw new IOException(
						ConnectionExceptionFactory.WRONG_LOGIN_PASSWORD);
			}

			if (!FTPReply.isPositiveCompletion(reply)) {
				throw new IOException("Server returned FTP error: "
						+ Integer.toString(reply));
			}

			String remoteDirectory = null;
			if (!protocol.getRemotePath().isEmpty()) {
				remoteDirectory = protocol.getRemotePath();
				if (remoteFile != null) {
					String parentDirectoryRelativePath = remoteFile
							.getParentDirectoryRelativePath();
					if (!parentDirectoryRelativePath.isEmpty()) {
						remoteDirectory += "/" + parentDirectoryRelativePath;
					}
				}
			} else {
				if (remoteFile != null) {
					String parentDirectoryRelativePath = remoteFile
							.getParentDirectoryRelativePath();
					if (!parentDirectoryRelativePath.isEmpty()) {
						remoteDirectory = parentDirectoryRelativePath;
					}
				}
			}

			if (remoteDirectory != null) {
				boolean ok = ftpClient.changeWorkingDirectory(remoteDirectory);
				if (!ok) {
					throw new FileNotFoundException("Remote file not found: "
							+ remoteFile.getRelativeFilePath());
				} else if (remoteFile != null) {
					ftpClient.mlistFile(remoteFile.getFilename());
					reply = ftpClient.getReplyCode();
					if (reply != 500) {// mlst is supported
						if (!FTPReply.isPositiveCompletion(reply)) {
							throw new FileNotFoundException(
									"Remote file not found: "
											+ remoteFile.getRelativeFilePath());
						}
					}
				}
			}

		} catch (IOException e) {
			if (!isCanceled()) {
				String coreMessage = "Failed to connect to the FTP server on url: "
						+ protocol.getHostUrl()
						+ "/"
						+ remoteFile.getRelativeFilePath();
				IOException ioe = ConnectionExceptionFactory.Exception(
						coreMessage, e);
				throw ioe;
			}
		}
	}

	@Override
	protected void disconnect() {

		if (ftpClient != null) {
			try {
				ftpClient.disconnect();
			} catch (Exception e) {
			}
		}
	}

	@Override
	protected void downloadFile(File file, RemoteFile remoteFile,
			boolean doRecordProgress, boolean doControlSpeed)
			throws IOException {

		boolean resume = false;
		if (ftpClient.getRestartOffset() > 0) {
			resume = true;
		} else {
			resume = false;
		}

		FileOutputStream fos = null;
		InputStream inputStream = null;
		DownloadProgressListener downloadProgressListener = null;
		SpeedControlListener speedControlListener = null;

		try {
			fos = new FileOutputStream(file, resume);
			downloadProgressListener = new DownloadProgressListener(
					doRecordProgress);
			downloadProgressListener.init(fos);

			speedControlListener = new SpeedControlListener(doControlSpeed);

			inputStream = ftpClient
					.retrieveFileStream(remoteFile.getFilename());

			if (inputStream == null) {
				int code = ftpClient.getReplyCode();
				if (code == 550) {
					throw new FileNotFoundException("Remote file not found");
				} else {
					throw new IOException("Server returned FTP error: "
							+ Integer.toString(code));
				}
			} else {
				int bytesRead = -1;
				ReadableByteChannel inChannel = Channels
						.newChannel(inputStream);
				ByteBuffer buffer = ByteBuffer.allocate(4096);
				while (((bytesRead = inChannel.read(buffer)) != -1)
						&& !isCanceled()) {
					byte[] array = buffer.array();
					downloadProgressListener.write(array, bytesRead);
					buffer.clear();
					long wait = speedControlListener.getWaitTime();
					if (wait > 0) {
						try {
							Thread.sleep(wait);
						} catch (InterruptedException e) {
						}
					}
				}

				// Must close before ftpClient.completePendingCommand()
				fos.close();
				inputStream.close();
				downloadProgressListener.close();

				// Ensure transfer is complete
				if (!isCanceled()) {
					boolean ok = ftpClient.completePendingCommand();
					if (!ok) {
						int code = ftpClient.getReplyCode();
						throw new IOException("Server returned FTP error: "
								+ Integer.toString(code));
					} else {
						long actualSize = file.length();
						FTPFile ftpFile = ftpClient.mlistFile(remoteFile
								.getFilename());
						int reply = ftpClient.getReplyCode();
						if (FTPReply.isPositiveCompletion(reply)) {
							long remoteSize = ftpFile.getSize();
							if (actualSize < remoteSize) {
								throw new IncompleteFileTransferException(
										file.getAbsolutePath(), actualSize,
										remoteSize);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			if (!isCanceled()) {
				String coreMessage = "Failed to retrieve file: "
						+ remoteFile.getRelativeFilePath();
				IOException ioe = ConnectionExceptionFactory.Exception(
						coreMessage, e);
				throw ioe;
			}
		} finally {
			if (fos != null) {
				fos.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
			if (downloadProgressListener != null) {
				downloadProgressListener.close();
			}
		}
	}

	@Override
	public void downloadPartialFile(File file, Repository repository,
			SyncTreeLeafDTO leaf) {
		throw new NotImplementedException();
	}

	@Override
	protected boolean fileExists(RemoteFile remoteFile) throws IOException {

		boolean exists = false;
		try {
			if (remoteFile.isDirectory()) {
				int returnCode = ftpClient.getReplyCode();
				if (returnCode != 550) {
					exists = true;
				}
			} else {// isFile
				ftpClient.mlistFile(remoteFile.getFilename());
				int returnCode = ftpClient.getReplyCode();
				if (returnCode == 500) {// mlist is not supported
					System.out
							.println("WARNING: MLST FTP command is not supported, using LST FTP command instead.");
					FTPFile[] subfiles = ftpClient.listFiles();
					if (subfiles != null) {
						for (FTPFile ftpFile : subfiles) {
							if (ftpFile.getName().equals(
									remoteFile.getFilename())) {
								exists = true;
								break;
							}
						}
					}
				} else {// mlist is supported
					if (returnCode != 550) {
						exists = true;
					}
				}
			}
		} catch (IOException e) {
			if (!isCanceled()) {
				String coreMessage = "Failed to check file: "
						+ remoteFile.getRelativeFilePath();
				IOException ioe = ConnectionExceptionFactory.Exception(
						coreMessage, e);
				throw ioe;
			}
		}
		return exists;
	}

	@Override
	public void uploadFile(File file, RemoteFile remoteFile,
			boolean doRecordProgress) throws IOException {

		if (remoteFile.isDirectory()) {
			makeDir(remoteFile.getRelativeFilePath());
		} else {
			makeDir(remoteFile.getParentDirectoryRelativePath());

			updateObserverText("Uploading file: "
					+ remoteFile.getRelativeFilePath());

			FileInputStream fis = null;
			OutputStream outputStream = null;
			UploadProgressListener uplooadProgressListener = null;

			try {
				fis = new FileInputStream(file);
				uplooadProgressListener = new UploadProgressListener();
				uplooadProgressListener.init(fis, doRecordProgress);

				outputStream = ftpClient.storeFileStream(remoteFile
						.getFilename());

				if (outputStream == null) {
					int code = ftpClient.getReplyCode();
					if (code == 550) {
						throw new FileNotFoundException(
								"Remote file not found: "
										+ remoteFile.getRelativeFilePath());
					} else {
						throw new IOException("Server returned FTP error: "
								+ Integer.toString(code));
					}
				} else {
					int bytesRead = -1;
					// http://stackoverflow.com/questions/14000341/why-is-ftp-upload-slow-in-java-7
					byte[] buffer = new byte[1048576];// 1024*1024
					while ((bytesRead = uplooadProgressListener.read(buffer)) != -1
							&& !isCanceled()) {
						outputStream.write(buffer, 0, bytesRead);
					}

					// Must close before ftpClient.completePendingCommand()
					fis.close();
					outputStream.close();
					uplooadProgressListener.close();

					if (!isCanceled()) {
						boolean ok = ftpClient.completePendingCommand();
						if (!ok) {
							int code = ftpClient.getReplyCode();
							throw new IOException("Server returned FTP error: "
									+ Integer.toString(code));
						}
					}
				}
			} catch (IOException e) {
				if (!isCanceled()) {
					String coreMessage = "Failed to upload file: "
							+ file.getAbsolutePath() + "\n"
							+ "To repository directory: "
							+ remoteFile.getParentDirectoryRelativePath();
					IOException ioe = ConnectionExceptionFactory.Exception(
							coreMessage, e);
					throw ioe;
				}
			} finally {
				if (fis != null) {
					fis.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
				if (uplooadProgressListener != null) {
					uplooadProgressListener.close();
				}
			}
		}
	}

	@Override
	protected void uploadObjectFile(Object object, RemoteFile remoteFile)
			throws IOException {

		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		InputStream uis = null;

		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(new GZIPOutputStream(baos));
			oos.writeObject(object);
			oos.flush();
			oos.close();
			uis = new ByteArrayInputStream(baos.toByteArray());
			makeDir(remoteFile.getParentDirectoryRelativePath());
			boolean ok = ftpClient.storeFile(remoteFile.getFilename(), uis);
			if (!ok) {
				int code = ftpClient.getReplyCode();
				throw new IOException("Failed to upload file: "
						+ remoteFile.getRelativeFilePath() + "\n"
						+ "Server returned error code: " + code);
			}
			ftpClient.noop();
		} finally {
			if (baos != null) {
				baos.close();
			}
			if (oos != null) {
				oos.close();
			}
			if (uis != null) {
				uis.close();
			}
		}
	}

	private void makeDir(String dirTree) throws IOException {

		updateObserverText("Creating remote directory: " + dirTree);

		String[] directories = dirTree.split("/");
		for (String dir : directories) {
			if (!dir.isEmpty()) {
				boolean dirExists = ftpClient.changeWorkingDirectory(dir);
				if (!dirExists) {
					if (!ftpClient.makeDirectory(dir)) {
						throw new IOException(
								"Unable to create remote directory " + dirTree
										+ "\n" + "Server returned FTP error: "
										+ ftpClient.getReplyString());
					}
					if (!ftpClient.changeWorkingDirectory(dir)) {
						throw new IOException(
								"Unable to change into newly created remote directory "
										+ dirTree + "\n"
										+ "Server returned FTP error: "
										+ ftpClient.getReplyString());
					}
				}
			}
		}
	}

	@Override
	protected void deleteFile(RemoteFile remoteFile) throws IOException {

		if (remoteFile.isDirectory()) {
			FTPFile[] subFiles = ftpClient.listFiles(remoteFile.getFilename());
			if (subFiles != null) {
				for (FTPFile aFile : subFiles) {
					RemoteFile newRemoteFile = new RemoteFile(aFile.getName(),
							remoteFile.getRelativeFilePath(), !aFile.isFile());
					deleteFile(newRemoteFile);
				}
			}
			boolean exists = ftpClient.changeWorkingDirectory(remoteFile
					.getFilename());
			if (exists) {
				ftpClient.removeDirectory(remoteFile.getFilename());
			}
		} else {// isFile
			ftpClient.deleteFile(remoteFile.getFilename());
		}
	}

	@Override
	public String checkPartialFileTransfer(Repository repository) {
		return null;
	}

	@Override
	public double getFileCompletion(Repository repository, SyncTreeLeafDTO leaf) {
		return 0;
	}
}
