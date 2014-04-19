package fr.soe.a3s.service;

import java.io.FileNotFoundException;
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

public abstract class AbstractConnexionService {

	public abstract AutoConfigDTO importAutoConfig(String url)
			throws WritingException, HttpException, FtpException;

	public abstract void checkRepository(String repositoryName)
			throws FtpException, RepositoryException, HttpException, WritingException;

	public abstract void getSync(String repositoryName)
			throws RepositoryException, FtpException, HttpException, WritingException;

	public abstract AbstractConnexionDAO getConnexionDAO();

	public abstract void downloadAddons(String repositoryName,
			List<SyncTreeNodeDTO> newListFiles, boolean resume)
			throws RepositoryException, FtpException, WritingException,
			FileNotFoundException, HttpException;

	public abstract void stopDownload(boolean resumable);

	public abstract void disconnect();

	public abstract boolean upLoadEvents(String repositoryName)
			throws RepositoryException, FtpException, HttpException;

	public abstract void determineCompletion(String repositoryName,
			SyncTreeDirectoryDTO parent) throws RepositoryException,
			HttpException, WritingException;

	protected AutoConfigDTO transformAutoConfig2DTO(AutoConfig autoConfig) {

		final AutoConfigDTO autoConfigDTO = new AutoConfigDTO();
		autoConfigDTO.setRepositoryName(autoConfig.getRepositoryName());
		ProtocoleDTO protocoleDTO = new ProtocoleDTO();
		autoConfigDTO.setProtocoleDTO(protocoleDTO);
		protocoleDTO.setUrl(autoConfig.getProtocole().getUrl());
		protocoleDTO.setPort(autoConfig.getProtocole().getPort());
		protocoleDTO.setPassword(autoConfig.getProtocole().getPassword());
		protocoleDTO.setLogin(autoConfig.getProtocole().getLogin());
		protocoleDTO.setEncryptionMode(autoConfig.getProtocole()
				.getEncryptionMode());
		if (autoConfig.getProtocole() instanceof Http) {
			protocoleDTO.setProtocole(Protocole.HTTP);
		} else {
			protocoleDTO.setProtocole(Protocole.FTP);
		}
		return autoConfigDTO;
	}

	protected ServerInfoDTO transformServerInfo2DTO(ServerInfo serverInfo) {

		final ServerInfoDTO serverInfoDTO = new ServerInfoDTO();
		serverInfoDTO.setBuildDate(serverInfo.getBuildDate());
		serverInfoDTO.setNumberOfFiles(serverInfo.getNumberOfFiles());
		serverInfoDTO.setRevision(serverInfo.getRevision());
		serverInfoDTO.setTotalFilesSize(serverInfo.getTotalFilesSize());
		return serverInfoDTO;
	}

	protected ServerInfo transformDTO2ServerInfo(ServerInfoDTO serverInfoDTO) {

		final ServerInfo serverInfo = new ServerInfo();
		serverInfo.setBuildDate(serverInfoDTO.getBuildDate());
		serverInfo.setNumberOfFiles(serverInfoDTO.getNumberOfFiles());
		serverInfo.setRevision(serverInfoDTO.getRevision());
		serverInfo.setTotalFilesSize(serverInfoDTO.getTotalFilesSize());
		return serverInfo;
	}
}
