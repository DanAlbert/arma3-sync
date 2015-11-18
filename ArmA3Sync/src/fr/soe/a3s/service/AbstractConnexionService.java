package fr.soe.a3s.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import fr.soe.a3s.dao.ConfigurationDAO;
import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.repository.RepositoryDAO;
import fr.soe.a3s.dao.zip.UnZipFlowProcessor;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.configration.FavoriteServer;
import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.domain.repository.SyncTreeLeaf;
import fr.soe.a3s.domain.repository.SyncTreeNode;
import fr.soe.a3s.dto.AutoConfigDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.repository.RepositoryException;

public abstract class AbstractConnexionService extends ObjectDTOtransformer {

    protected Stack<SyncTreeNodeDTO> downloadFilesStack = null;
    protected List<Exception> downloadErrors = null;
    protected List<Exception> downloadTimeouterrors = null;
    protected int semaphore;
    protected List<SyncTreeLeaf> checkRepositoryFilesList = null;
    protected final UnZipFlowProcessor unZipFlowProcessor = new UnZipFlowProcessor();
    private static final ConfigurationDAO configurationDAO = new ConfigurationDAO();
    protected static final RepositoryDAO repositoryDAO = new RepositoryDAO();
    
    /* Get A3S Files */
    public abstract void getSync(String repositoryName) throws RepositoryException, IOException;

    public abstract void getServerInfo(String repositoryName) throws RepositoryException,
            IOException;

    public abstract void getChangelogs(String repositoryName) throws RepositoryException,
            IOException;

    public abstract void getAutoconfig(String repositoryName) throws RepositoryException,
            IOException;

    public abstract void getEvents(String repositoryName) throws RepositoryException, IOException;

    /* Check Repository */
    public abstract void checkRepository(String repositoryName) throws RepositoryException,
            IOException;

    /* Import autoconfig */
    public abstract AutoConfigDTO importAutoConfig(AbstractProtocole protocol) throws IOException;

    /* Determine file completion */
    public abstract String determineCompletion(String repositoryName, SyncTreeDirectoryDTO parent)
            throws RepositoryException, IOException;

    /* Download Addons */
    public abstract void downloadAddons(String repositoryName, List<SyncTreeNodeDTO> newListFiles)
            throws RepositoryException, IOException;

    /* Upload Events */
    public abstract boolean upLoadEvents(String repositoryName) throws RepositoryException,
            IOException;

    /* Upload Repository */
    public abstract void getSyncWithRepositoryUploadProtocole(String repositoryName)
            throws RepositoryException, IOException;

    public abstract void uploadRepository(String repositoryName,
            List<SyncTreeNodeDTO> allLocalFiles, List<SyncTreeNodeDTO> filesToUpload,
            List<SyncTreeNodeDTO> filesToDelete) throws RepositoryException, IOException;

    /* Check Repository synchronization */
    public abstract List<Exception> checkRepositoryContent(String repositoryName)
            throws RepositoryException, IOException;

    /* Getters */
    public abstract AbstractConnexionDAO getConnexionDAO();

    public abstract List<AbstractConnexionDAO> getConnexionDAOs();

    public abstract int getNumberConnections();

    public UnZipFlowProcessor getUnZipFlowProcessor() {
        return this.unZipFlowProcessor;
    }

    /* Setters */
    public abstract void setMaximumClientDownloadSpeed(double maximumClientDownloadSpeed);

    /* Cancel */
    public abstract void cancel();

    /* */
    protected void updateFavoriteServersFromAutoconfig(AutoConfig autoConfig) {

        List<FavoriteServer> list1 = autoConfig.getFavoriteServers();
        List<FavoriteServer> list2 = configurationDAO.getConfiguration().getFavoriteServers();

        List<FavoriteServer> newList = new ArrayList<FavoriteServer>();

        for (FavoriteServer favoriteServerList2 : list2) {
            if (!autoConfig.getRepositoryName().equals(favoriteServerList2.getRepositoryName())) {
                boolean nameIsDifferent = true;
                for (FavoriteServer favoriteServerList1 : list1) {
                    if (favoriteServerList1.getName().equals(favoriteServerList2.getName())) {
                        nameIsDifferent = false;
                    }
                }
                if (nameIsDifferent) {
                    newList.add(favoriteServerList2);
                }
            }
        }
        newList.addAll(list1);

        configurationDAO.getConfiguration().getFavoriteServers().clear();
        configurationDAO.getConfiguration().getFavoriteServers().addAll(newList);
    }

    protected void getFiles(SyncTreeNode node) {

        if (!node.isLeaf()) {
            SyncTreeDirectory syncTreeDirectory = (SyncTreeDirectory) node;
            for (SyncTreeNode n : syncTreeDirectory.getList()) {
                getFiles(n);
            }
        }
        else {
            SyncTreeLeaf syncTreeLeaf = (SyncTreeLeaf) node;
            checkRepositoryFilesList.add(syncTreeLeaf);
        }
    }

    protected String determinePath(SyncTreeNode syncTreeNode) {
        assert (syncTreeNode.getParent() != null);
        String path = "";
        while (!syncTreeNode.getParent().getName().equals("racine")) {
            if (path.isEmpty()) {
                path = syncTreeNode.getParent().getName();
            }
            else {
                path = syncTreeNode.getParent().getName() + "/" + path;
            }
            syncTreeNode = syncTreeNode.getParent();
        }
        return path;
    }

    protected String determinePath(SyncTreeNodeDTO syncTreeNodeDTO) {

        assert (syncTreeNodeDTO.getParent() != null);
        String path = "";
        while (syncTreeNodeDTO.getParent().getName() != "racine") {
            if (path.isEmpty()) {
                path = syncTreeNodeDTO.getParent().getName();
            }
            else {
                path = syncTreeNodeDTO.getParent().getName() + "/" + path;
            }
            syncTreeNodeDTO = syncTreeNodeDTO.getParent();
        }
        return path;
    }

    protected synchronized void addError(Exception e) {
        downloadErrors.add(e);
    }

    protected synchronized void addTimeoutError(Exception e) {
        downloadTimeouterrors.add(e);
    }

    protected synchronized SyncTreeNodeDTO popDownloadFilesStack() {

        if (downloadFilesStack.isEmpty()) {
            return null;
        }
        else {
            return downloadFilesStack.pop();
        }
    }

    protected synchronized boolean aquireSemaphore() {

        if (this.semaphore == 1) {
            this.semaphore = 0;
            return true;
        }
        else {
            return false;
        }
    }

    protected synchronized void releaseSemaphore() {
        semaphore = 1;
    }
}
