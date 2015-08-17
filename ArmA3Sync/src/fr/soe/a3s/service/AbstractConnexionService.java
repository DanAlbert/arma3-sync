package fr.soe.a3s.service;

import java.io.IOException;
import java.util.List;

import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.zip.UnZipFlowProcessor;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.dto.AutoConfigDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.repository.RepositoryException;

public abstract class AbstractConnexionService extends ObjectDTOtransformer {

	/* Get A3S Files */
	public abstract void getSync(String repositoryName)
			throws RepositoryException, IOException;

	public abstract void getServerInfo(String repositoryName)
			throws RepositoryException, IOException;

	public abstract void getChangelogs(String repositoryName)
			throws RepositoryException, IOException;

	public abstract void getAutoconfig(String repositoryName)
			throws RepositoryException, IOException;

	public abstract void getEvents(String repositoryName)
			throws RepositoryException, IOException;

	/* Check Repository */
	public abstract void checkRepository(String repositoryName)
			throws RepositoryException, IOException;

	/* Import autoconfig */
	public abstract AutoConfigDTO importAutoConfig(AbstractProtocole protocol)
			throws IOException;

	/* Determine file completion */
	public abstract String determineCompletion(String repositoryName,
			SyncTreeDirectoryDTO parent) throws RepositoryException,
			IOException;

	/* Download Addons */
	public abstract void downloadAddons(String repositoryName,
			List<SyncTreeNodeDTO> newListFiles) throws RepositoryException,
			IOException;

	/* Upload Events */
	public abstract boolean upLoadEvents(String repositoryName)
			throws RepositoryException, IOException;

	/* Upload Repository */
	public abstract void getSyncWithRepositoryUploadProtocole(
			String repositoryName) throws RepositoryException, IOException;

	public abstract void uploadRepository(String repositoryName,
			List<SyncTreeNodeDTO> allLocalFiles,
			List<SyncTreeNodeDTO> filesToUpload,
			List<SyncTreeNodeDTO> filesToDelete) throws RepositoryException,
			IOException;

	/* Check Repository synchronization */
	public abstract List<Exception> checkRepositoryContent(String repositoryName)
			throws RepositoryException, IOException;

	/* Getters */
	public abstract AbstractConnexionDAO getConnexionDAO();

	public abstract List<AbstractConnexionDAO> getConnexionDAOs();

	public abstract int getNumberConnections();

	public abstract UnZipFlowProcessor getUnZipFlowProcessor();

	/* Setters */
	public abstract void setMaximumClientDownloadSpeed(
			double maximumClientDownloadSpeed);

	/* Cancel */
	public abstract void cancel();
}
