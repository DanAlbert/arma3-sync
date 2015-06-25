package fr.soe.a3s.service;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;
import java.util.Map;

import fr.soe.a3s.dao.AbstractConnexionDAO;
import fr.soe.a3s.dto.AutoConfigDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.FtpException;
import fr.soe.a3s.exception.HttpException;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.exception.WritingException;

public abstract class AbstractConnexionService extends ObjectDTOtransformer {

	public abstract AutoConfigDTO importAutoConfig(String url)
			throws WritingException, HttpException, FtpException,
			ConnectException, IOException;

	public abstract void checkRepository(String repositoryName)
			throws RepositoryException, ConnectException, IOException,
			FtpException;

	public abstract void getSync(String repositoryName)
			throws RepositoryException, FtpException, HttpException,
			ConnectException, IOException;

	public abstract AbstractConnexionDAO getConnexionDAO();

	public abstract List<AbstractConnexionDAO> getConnexionDAOs();

	public abstract void downloadAddons(String repositoryName,
			List<SyncTreeNodeDTO> newListFiles) throws Exception;

	public abstract void cancel(boolean resumable);

	public abstract void disconnect();

	public abstract boolean upLoadEvents(String repositoryName)
			throws RepositoryException, FtpException, HttpException,
			ConnectException;

	public abstract void determineCompletion(String repositoryName,
			SyncTreeDirectoryDTO parent) throws Exception;

	public abstract void getServerInfo(String repositoryName)
			throws RepositoryException, ConnectException, FtpException,
			WritingException, HttpException, IOException;

	public abstract void getChangelogs(String repositoryName)
			throws ConnectException, FtpException, RepositoryException,
			WritingException, HttpException, IOException;

	public abstract void uploadRepository(String repositoryName,
			List<SyncTreeNodeDTO> filesToUpload,
			List<SyncTreeNodeDTO> filesToDelete, boolean resume)
			throws RepositoryException, ConnectException, FtpException;

	public abstract void getSyncWithRepositoryUploadProtocole(
			String repositoryName) throws RepositoryException,
			WritingException, ConnectException, FtpException, HttpException,
			IOException;

	public abstract boolean remoteFileExists(String repositoryName,
			SyncTreeNodeDTO remoteNode) throws RepositoryException,
			ConnectException, FtpException;

	public abstract int getNumberConnections();

	public abstract void remoteFileExists(String repositoryName,
			Map<SyncTreeNodeDTO, Boolean> mapRemoteNodeExists)
			throws RepositoryException, ConnectException, FtpException;
}
