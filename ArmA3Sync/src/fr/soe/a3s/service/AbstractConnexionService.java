package fr.soe.a3s.service;

import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.util.List;

import fr.soe.a3s.constant.Protocole;
import fr.soe.a3s.dao.AbstractConnexionDAO;
import fr.soe.a3s.domain.Http;
import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.dto.AutoConfigDTO;
import fr.soe.a3s.dto.ProtocoleDTO;
import fr.soe.a3s.dto.ServerInfoDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.FtpException;
import fr.soe.a3s.exception.HttpException;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.exception.WritingException;

public abstract class AbstractConnexionService extends ObjectDTOtransformer {

	public abstract AutoConfigDTO importAutoConfig(String url)
			throws WritingException, HttpException, FtpException,
			ConnectException;

	public abstract void checkRepository(String repositoryName)
			throws FtpException, RepositoryException, WritingException,
			ConnectException;

	public abstract void getSync(String repositoryName)
			throws RepositoryException, FtpException, HttpException,
			WritingException, ConnectException;

	public abstract AbstractConnexionDAO getConnexionDAO();

	public abstract void downloadAddons(String repositoryName,
			List<SyncTreeNodeDTO> newListFiles, boolean resume)
			throws RepositoryException, FtpException, WritingException,
			FileNotFoundException, HttpException, ConnectException;

	public abstract void stopDownload(boolean resumable);

	public abstract void disconnect();

	public abstract boolean upLoadEvents(String repositoryName)
			throws RepositoryException, FtpException, HttpException,
			ConnectException;

	public abstract void determineCompletion(String repositoryName,
			SyncTreeDirectoryDTO parent) throws RepositoryException,
			HttpException, WritingException;
}
