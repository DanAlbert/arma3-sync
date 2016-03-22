package fr.soe.a3sUpdater.service;

import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.dom4j.DocumentException;

import fr.soe.a3sUpdater.dao.DataAccessConstants;
import fr.soe.a3sUpdater.dao.FtpDAO;
import fr.soe.a3sUpdater.dao.XmlDAO;
import fr.soe.a3sUpdater.exception.FinderException;
import fr.soe.a3sUpdater.exception.FtpException;
import fr.soe.a3sUpdater.exception.WritingException;
import fr.soe.a3sUpdater.exception.XmlException;

public class Service implements DataAccessConstants {

	private static FtpDAO ftpDAO = new FtpDAO();
	private static XmlDAO xmlDAO = new XmlDAO();
	private String version = "";
	private String zipFileName = "";
	private FTPClient ftpClient;

	public String getVersion() throws XmlException {
		try {
			String version = xmlDAO.getVersion();
			if (version == null) {
				return null;
			} else {
				this.version = version;
			}
			getZipFileName();
		} catch (Exception e) {
			e.printStackTrace();
			throw new XmlException("Can't get update version.");
		}
		return version;
	}

	private String getZipFileName() throws DocumentException {
		this.zipFileName = xmlDAO.getZipFileName();
		return this.zipFileName;
	}

	public long getSize(boolean devMode) throws FtpException, FinderException {

		long size = 0;
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(UPDTATE_REPOSITORY_ADRESS,
					UPDTATE_REPOSITORY_PORT);
			ftpClient.login(UPDTATE_REPOSITORY_LOGIN, UPDTATE_REPOSITORY_PASS);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);// binary transfert mode
			ftpClient.enterLocalPassiveMode();// passive mode

			int reply = ftpClient.getReplyCode();

			if (FTPReply.isPositiveCompletion(reply)) {
				System.out.println("Connected Success");
			} else {
				System.out.println("Connection Failed");
				ftpClient.disconnect();
				throw new FtpException("Fail to connect to remote repository.");
			}

		} catch (Exception e1) {
			e1.printStackTrace();
			throw new FtpException("Fail to connect to remote repository.");
		}

		try {
			size = ftpDAO.getFtpFileSize(zipFileName, ftpClient, devMode);
			if (size == 0) {
				throw new FinderException(
						"Can't find update file on repository.");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new FinderException("Can't find update file on repository.");
		}
		return size;
	}

	public void setDownload() throws WritingException {
		try {
			ftpDAO.setDownload(zipFileName);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WritingException("Can't write files on disk.");
		}
	}

	public void download(boolean devMode) throws Exception {

		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);// binary transfert mode
		ftpClient.enterLocalPassiveMode();// passive mode
		int reply = ftpClient.getReplyCode();
		if (FTPReply.isPositiveCompletion(reply)) {
			System.out.println("Connected Success");
		} else {
			System.out.println("Connection Failed");
			ftpClient.disconnect();
			throw new Exception("Fail to connect to remote repository.");
		}
		boolean found = ftpDAO.download(zipFileName, ftpClient, devMode);
		if (!found) {
			throw new Exception("Update file not found.");
		}
	}

	public FtpDAO getFtpDAO() {
		return ftpDAO;
	}

	public void install() throws WritingException {
		try {
			ftpDAO.install();
		} catch (IOException e) {
			throw new WritingException("Can't write files on disk!");
		}
	}

	public void clean() {
		ftpDAO.clean();
	}

}
