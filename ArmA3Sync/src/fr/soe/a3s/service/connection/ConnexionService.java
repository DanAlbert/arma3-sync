package fr.soe.a3s.service.connection;

import java.io.IOException;
import java.util.List;

import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.zip.UnZipFlowProcessor;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.dto.AutoConfigDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.repository.RepositoryException;

public interface ConnexionService {

	/* Get A3S Files */
	public void getSync(String repositoryName) throws RepositoryException,
			IOException;

	public void getServerInfo(String repositoryName)
			throws RepositoryException, IOException;

	public void getChangelogs(String repositoryName)
			throws RepositoryException, IOException;

	public void getAutoconfig(String repositoryName)
			throws RepositoryException, IOException;

	public void getEvents(String repositoryName) throws RepositoryException,
			IOException;

	/* Check Repository */
	public void checkRepository(String repositoryName)
			throws RepositoryException, IOException;

	/* Import autoconfig */
	public AutoConfigDTO importAutoConfig(AbstractProtocole protocol)
			throws IOException;

	/* Determine file completion */
	public String determineFilesCompletion(String repositoryName,
			SyncTreeDirectoryDTO parent) throws RepositoryException,
			IOException;

	/* Synchronize Addons files */
	public void synchronize(String repositoryName,
			List<SyncTreeNodeDTO> newListFiles) throws RepositoryException,
			IOException;

	/* Upload Events */
	public boolean upLoadEvents(String repositoryName)
			throws RepositoryException, IOException;

	/* Upload Repository */
	public void getSyncWithUploadProtocole(String repositoryName)
			throws RepositoryException, IOException;

	public void uploadRepository(String repositoryName,
			List<SyncTreeNodeDTO> filesToCheck,
			List<SyncTreeNodeDTO> filesToUpload,
			List<SyncTreeNodeDTO> filesToDelete, int lastIndexFileUploaded)
			throws RepositoryException, IOException;

	/* Check Repository synchronization */
	public List<Exception> checkRepositoryContent(String repositoryName)
			throws RepositoryException, IOException;

	/* Getters */
	public AbstractConnexionDAO getConnexionDAO();

	public List<AbstractConnexionDAO> getConnexionDAOs();

	public int getNumberConnections();

	public UnZipFlowProcessor getUnZipFlowProcessor();

	/* Setters */
	public void setMaximumClientDownloadSpeed(double maximumClientDownloadSpeed);

	/* Cancel */
	public void cancel();
}
