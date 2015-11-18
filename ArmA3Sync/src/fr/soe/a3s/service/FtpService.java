package fr.soe.a3s.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import org.apache.commons.net.ftp.FTPFile;

import fr.soe.a3s.controller.ObserverProceed;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dao.connection.FtpDAO;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.Http;
import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.domain.repository.Changelogs;
import fr.soe.a3s.domain.repository.Events;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.domain.repository.ServerInfo;
import fr.soe.a3s.domain.repository.SyncTreeDirectory;
import fr.soe.a3s.domain.repository.SyncTreeLeaf;
import fr.soe.a3s.dto.AutoConfigDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.FtpException;
import fr.soe.a3s.exception.repository.AutoConfigFileNotFoundException;
import fr.soe.a3s.exception.repository.ChangelogsFileNotFoundExeption;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.exception.repository.RepositoryNotFoundException;
import fr.soe.a3s.exception.repository.ServerInfoNotFoundException;
import fr.soe.a3s.exception.repository.SyncFileNotFoundException;
import fr.soe.a3s.main.Version;

public class FtpService extends AbstractConnexionService implements DataAccessConstants {

    private final List<FtpDAO> ftpDAOPool = new ArrayList<FtpDAO>();

    /* Initialize Service */

    public FtpService(int nbConnections) {
        assert (nbConnections != 0);
        for (int i = 0; i < nbConnections; i++) {
            FtpDAO ftpDAO = new FtpDAO();
            ftpDAOPool.add(ftpDAO);
        }
    }

    public FtpService() {
        FtpDAO ftpDAO = new FtpDAO();
        ftpDAOPool.add(ftpDAO);
    }

    /* Get A3S Files */

    @Override
    public void getSync(String repositoryName) throws RepositoryException, IOException {

        Repository repository = repositoryDAO.getMap().get(repositoryName);
        if (repository == null) {
            throw new RepositoryNotFoundException(repositoryName);
        }
        try {
            ftpDAOPool.get(0).connectToRepository(repository.getName(), repository.getProtocol());
            SyncTreeDirectory syncTreeDirectory = ftpDAOPool.get(0).downloadSync(repositoryName,
                    repository.getProtocol());
            repository.setSync(syncTreeDirectory);
        }
        finally {
            ftpDAOPool.get(0).disconnect();
        }
    }

    @Override
    public void getServerInfo(String repositoryName) throws RepositoryException, IOException {

        Repository repository = repositoryDAO.getMap().get(repositoryName);
        if (repository == null) {
            throw new RepositoryNotFoundException(repositoryName);
        }
        try {
            ftpDAOPool.get(0).connectToRepository(repository.getName(), repository.getProtocol());
            ServerInfo serverInfo = ftpDAOPool.get(0).downloadSeverInfo(repositoryName,
                    repository.getProtocol());
            repository.setServerInfo(serverInfo);
        }
        finally {
            ftpDAOPool.get(0).disconnect();
        }
    }

    @Override
    public void getChangelogs(String repositoryName) throws RepositoryException, IOException {

        Repository repository = repositoryDAO.getMap().get(repositoryName);
        if (repository == null) {
            throw new RepositoryNotFoundException(repositoryName);
        }
        try {
            ftpDAOPool.get(0).connectToRepository(repository.getName(), repository.getProtocol());
            Changelogs changelogs = ftpDAOPool.get(0).downloadChangelogs(repositoryName,
                    repository.getProtocol());
            repository.setChangelogs(changelogs);
        }
        finally {
            ftpDAOPool.get(0).disconnect();
        }
    }

    @Override
    public void getAutoconfig(String repositoryName) throws RepositoryException, IOException {

        Repository repository = repositoryDAO.getMap().get(repositoryName);
        if (repository == null) {
            throw new RepositoryNotFoundException(repositoryName);
        }
        try {
            ftpDAOPool.get(0).connectToRepository(repository.getName(), repository.getProtocol());
            AutoConfig autoConfig = ftpDAOPool.get(0).downloadAutoconfig(repositoryName,
                    repository.getProtocol());
            repository.setAutoConfig(autoConfig);
        }
        finally {
            ftpDAOPool.get(0).disconnect();
        }
    }

    @Override
    public void getEvents(String repositoryName) throws RepositoryException, IOException {

        Repository repository = repositoryDAO.getMap().get(repositoryName);
        if (repository == null) {
            throw new RepositoryNotFoundException(repositoryName);
        }
        try {
            ftpDAOPool.get(0).connectToRepository(repository.getName(), repository.getProtocol());
            Events events = ftpDAOPool.get(0).downloadEvents(repositoryName,
                    repository.getProtocol());
            repository.setEvents(events);
        }
        finally {
            ftpDAOPool.get(0).disconnect();
        }
    }

    /* Check Repository */

    @Override
    public void checkRepository(String repositoryName) throws RepositoryException, IOException {

        System.out.println("Checking repository: " + repositoryName);

        Repository repository = repositoryDAO.getMap().get(repositoryName);
        if (repository == null) {
            throw new RepositoryNotFoundException(repositoryName);
        }

        /* Sync */
        if (!ftpDAOPool.get(0).isCanceled()) {
            getSync(repositoryName);
        }

        /* Serverinfo */
        if (!ftpDAOPool.get(0).isCanceled()) {
            getServerInfo(repositoryName);
            if (repository.getServerInfo() != null) {
                repository.getHiddenFolderPath().addAll(
                        repository.getServerInfo().getHiddenFolderPaths());
            }
        }
        /* Changelogs */
        if (!ftpDAOPool.get(0).isCanceled()) {
            getChangelogs(repositoryName);
        }
        /* Events */
        if (!ftpDAOPool.get(0).isCanceled()) {
            getEvents(repositoryName);
        }
        /* Autoconfig */
        if (!ftpDAOPool.get(0).isCanceled()) {
            getAutoconfig(repositoryName);
            if (repository.getAutoConfig() != null) {
                updateFavoriteServersFromAutoconfig(repository.getAutoConfig());
            }
        }
    }

    /* Import autoconfig */

    @Override
    public AutoConfigDTO importAutoConfig(AbstractProtocole protocol) throws IOException {

        AutoConfigDTO autoConfigDTO = null;
        try {
            AutoConfig autoConfig = ftpDAOPool.get(0).importAutoConfig(protocol);
            if (autoConfig != null) {
                updateFavoriteServersFromAutoconfig(autoConfig);
                autoConfigDTO = transformAutoConfig2DTO(autoConfig);
            }
        }
        finally {
            ftpDAOPool.get(0).disconnect();
        }
        return autoConfigDTO;
    }

    /* Determine file completion */

    @Override
    public String determineCompletion(String repositoryName, SyncTreeDirectoryDTO parent) {

        for (SyncTreeNodeDTO node : parent.getList()) {
            if (node.isLeaf()) {
                SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
                if (leaf.isUpdated()) {
                    leaf.setComplete(0);
                }
                else {
                    leaf.setComplete(100);
                }
            }
            else {
                SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) node;
                determineCompletion(repositoryName, directory);
            }
        }
        return null;
    }

    /* Download Addons */

    @Override
    public void downloadAddons(String repositoryName, List<SyncTreeNodeDTO> listFiles)
            throws RepositoryException, IOException {

        final Repository repository = repositoryDAO.getMap().get(repositoryName);
        if (repository == null) {
            throw new RepositoryNotFoundException(repositoryName);
        }

        final String rootDestinationPath = repository.getDefaultDownloadLocation();

        assert (repository.getSync() != null);
        assert (repository.getServerInfo() != null);
        assert (rootDestinationPath != null);

        this.downloadFilesStack = new Stack<SyncTreeNodeDTO>();
        this.downloadFilesStack.addAll(listFiles);
        this.downloadErrors = new ArrayList<Exception>();
        this.downloadTimeouterrors = new ArrayList<Exception>();
        this.semaphore = 1;
        this.unZipFlowProcessor.init();

        for (final FtpDAO ftpDAO : ftpDAOPool) {
            ftpDAO.addObserverProceed(new ObserverProceed() {
                @Override
                public void proceed() {
                    if (!ftpDAO.isCanceled()) {
                        final SyncTreeNodeDTO node = popDownloadFilesStack();
                        if (node != null) {
                            Thread t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (aquireSemaphore()) {
                                            ftpDAO.setAcquiredSemaphore(true);
                                        }

                                        ftpDAO.setActiveConnection(true);
                                        ftpDAO.updateObserverDownloadActiveConnections();

                                        File downloadedFile = downloadAddon(ftpDAO, node,
                                                rootDestinationPath, repository);

                                        if (downloadedFile != null) {
                                            if (downloadedFile.isFile()) {
                                                if (downloadedFile
                                                        .getName()
                                                        .toLowerCase()
                                                        .contains(
                                                                DataAccessConstants.PBO_ZIP_EXTENSION)) {
                                                    unZipFlowProcessor
                                                            .unZipAsynchronously(downloadedFile);
                                                }
                                            }
                                        }
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                        if (!ftpDAO.isCanceled()) {
                                            if (e instanceof SocketTimeoutException) {
                                                addTimeoutError(e);
                                                addError(e);
                                            }
                                            else if (e instanceof IOException) {
                                                // reset count
                                                downloadTimeouterrors.clear();
                                                addError(e);
                                            }
                                        }
                                    }
                                    finally {
                                        if (ftpDAO.isAcquiredSemaphore()) {
                                            releaseSemaphore();
                                            ftpDAO.setAcquiredSemaphore(false);
                                        }
                                        ftpDAO.setActiveConnection(false);
                                        ftpDAO.updateObserverDownloadActiveConnections();

                                        if (downloadTimeouterrors.size() > ftpDAOPool.size()) {
                                            ftpDAO.updateObserverDownloadTooManyTimeoutErrors(
                                                    ftpDAOPool.size(), downloadTimeouterrors);
                                        }
                                        else if (downloadErrors.size() > 10) {
                                            ftpDAO.updateObserverDownloadTooManyErrors(10,
                                                    downloadErrors);
                                        }
                                        else {
                                            ftpDAO.updateObserverProceed();
                                        }
                                    }
                                }
                            });
                            t.start();
                        }
                        else {// no more file to download for this DAO

                            // Check if there is no more active connections
                            boolean downloadFinished = true;
                            for (FtpDAO ftpDAO : ftpDAOPool) {
                                if (ftpDAO.isActiveConnection()) {
                                    downloadFinished = false;
                                    break;
                                }
                            }

                            // download is finished
                            if (downloadFinished) {
                                // display uncompressing progress
                                if (unZipFlowProcessor.uncompressionIsFinished()) {
                                    downloadErrors.addAll(unZipFlowProcessor.getErrors());
                                    if (downloadErrors.isEmpty()) {
                                        ftpDAO.updateObserverDownloadEnd();
                                    }
                                    else {
                                        ftpDAO.updateObserverDownloadEndWithErrors(downloadErrors);
                                    }
                                }
                                else {
                                    if (!unZipFlowProcessor.isStarted()) {
                                        unZipFlowProcessor.start(downloadErrors);
                                    }
                                }
                            }
                            else {
                                // Give semaphore to the other DAOs
                                for (FtpDAO ftpDAO : ftpDAOPool) {
                                    if (ftpDAO.isActiveConnection() && aquireSemaphore()) {
                                        ftpDAO.setAcquiredSemaphore(true);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }

        for (FtpDAO ftpDAO : ftpDAOPool) {
            if (!downloadFilesStack.isEmpty()) {// nb files < nb connections
                try {
                    ftpDAO.connectToRepository(repository.getName(), repository.getProtocol());
                    ftpDAO.updateObserverProceed();
                }
                catch (IOException e) {
                    boolean isDowloading = false;
                    ftpDAO.setActiveConnection(false);
                    for (FtpDAO fDAO : ftpDAOPool) {
                        if (fDAO.isActiveConnection()) {
                            isDowloading = true;
                            break;
                        }
                    }
                    if (!isDowloading) {
                        throw e;
                    }
                }
            }
        }
    }

    private File downloadAddon(final FtpDAO ftpDAO, final SyncTreeNodeDTO node,
            final String rootDestinationPath, final Repository repository) throws IOException {

        String destinationPath = null;
        String remotePath = repository.getProtocol().getRemotePath();
        String path = determinePath(node);
        if (node.getDestinationPath() != null) {
            destinationPath = node.getDestinationPath();
            if (!path.isEmpty()) {
                remotePath = remotePath + "/" + path;
            }
        }
        else {
            destinationPath = rootDestinationPath;
            if (!path.isEmpty()) {
                destinationPath = rootDestinationPath + "/" + path;
                remotePath = remotePath + "/" + path;
            }
        }
        return ftpDAO.downloadFile(remotePath, destinationPath, node);
    }

    /* Upload Events */

    @Override
    public boolean upLoadEvents(String repositoryName) throws RepositoryException, IOException {

        Repository repository = repositoryDAO.getMap().get(repositoryName);
        if (repository == null) {
            throw new RepositoryNotFoundException(repositoryName);
        }
        boolean response = false;
        try {
            ftpDAOPool.get(0).connectToRepository(repository.getName(),
                    repository.getRepositoryUploadProtocole());
            response = ftpDAOPool.get(0).uploadEvents(repository.getEvents(),
                    repository.getRepositoryUploadProtocole());
        }
        finally {
            ftpDAOPool.get(0).disconnect();
        }
        return response;
    }

    /* Upload Repository */

    @Override
    public void getSyncWithRepositoryUploadProtocole(String repositoryName)
            throws RepositoryException, IOException {

        Repository repository = repositoryDAO.getMap().get(repositoryName);
        if (repository == null) {
            throw new RepositoryNotFoundException(repositoryName);
        }
        try {
            ftpDAOPool.get(0).connectToRepository(repository.getName(),
                    repository.getRepositoryUploadProtocole());

            SyncTreeDirectory syncTreeDirectory = ftpDAOPool.get(0).downloadSync(repositoryName,
                    repository.getRepositoryUploadProtocole());
            repository.setSync(syncTreeDirectory);
        }
        finally {
            ftpDAOPool.get(0).disconnect();
        }
    }

    @Override
    public void uploadRepository(String repositoryName, List<SyncTreeNodeDTO> allLocalFiles,
            List<SyncTreeNodeDTO> filesToUpload, List<SyncTreeNodeDTO> filesToDelete)
            throws RepositoryException, IOException {

        Repository repository = repositoryDAO.getMap().get(repositoryName);
        if (repository == null) {
            throw new RepositoryNotFoundException(repositoryName);
        }

        SyncTreeDirectory sync = repository.getLocalSync();
        if (sync == null) {
            throw new SyncFileNotFoundException(repositoryName);
        }

        ServerInfo serverInfo = repository.getLocalServerInfo();
        if (serverInfo == null) {
            throw new ServerInfoNotFoundException(repositoryName);
        }

        Changelogs changelogs = repository.getLocalChangelogs();
        if (changelogs == null) {
            throw new ChangelogsFileNotFoundExeption(repositoryName);
        }

        AutoConfig autoConfig = repository.getLocalAutoConfig();
        if (autoConfig == null) {
            throw new AutoConfigFileNotFoundException(repositoryName);
        }

        Events events = repository.getLocalEvents();// could be null

        ftpDAOPool.get(0).updateObserverCountWithText("Checking remote files...");

        Map<SyncTreeNodeDTO, List<FTPFile>> mapFtpFilesToUpload = new LinkedHashMap<SyncTreeNodeDTO, List<FTPFile>>();

        ftpDAOPool.get(0).setTotalCount(allLocalFiles.size());
        int count = 0;
        int nbFilesToUpload = 0;

        try {
            // Connect
            ftpDAOPool.get(0).connectToRepository(repository.getName(),
                    repository.getRepositoryUploadProtocole());

            for (SyncTreeNodeDTO node : allLocalFiles) {
                List<FTPFile> ftpFilesToUpload = new ArrayList<FTPFile>();
                if (ftpDAOPool.get(0).isCanceled()) {
                    return;
                }
                else {
                    String relativePath = determinePath(node);
                    if (node.isLeaf()) {
                        SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
                        List<String> listFilesName = new ArrayList<String>();

                        if (leaf.isCompressed()) {
                            if (repository.isUploadCompressedPboFilesOnly()) {
                                String fileName = leaf.getName() + ZIP_EXTENSION;// *.pbo.zip
                                listFilesName.add(fileName);
                            }
                            else {
                                String fileName = leaf.getName();// *.*/*.pbo
                                listFilesName.add(fileName);
                                fileName = leaf.getName() + ZIP_EXTENSION;// *.pbo.zip
                                listFilesName.add(fileName);
                                if (repository.getProtocol() instanceof Http) {
                                    fileName = leaf.getName() + ZSYNC_EXTENSION;// *.pbo.zsync
                                    listFilesName.add(fileName);
                                }
                            }
                        }
                        else {
                            String fileName = leaf.getName();// *.*/*.pbo
                            listFilesName.add(fileName);
                            if (repository.getProtocol() instanceof Http) {
                                fileName = leaf.getName() + ZSYNC_EXTENSION;// *.pbo.zsync
                                listFilesName.add(fileName);
                            }
                        }
                        for (String fileName : listFilesName) {
                            FTPFile ftpFile = createFTPFile(fileName, relativePath,
                                    FTPFile.FILE_TYPE);

                            boolean exists = remoteFileExists(repository.getName(), relativePath,
                                    fileName, repository.getRepositoryUploadProtocole());
                            if (!exists || filesToUpload.contains(leaf)) {
                                ftpFilesToUpload.add(ftpFile);
                            }
                        }
                    }
                    else {
                        String fileName = node.getName();
                        FTPFile ftpFile = createFTPFile(fileName, relativePath,
                                FTPFile.DIRECTORY_TYPE);

                        boolean exists = remoteFileExists(repository.getName(), relativePath,
                                fileName, repository.getRepositoryUploadProtocole());
                        if (!exists || filesToUpload.contains(node)) {
                            ftpFilesToUpload.add(ftpFile);
                        }
                    }
                }

                mapFtpFilesToUpload.put(node, ftpFilesToUpload);
                nbFilesToUpload = nbFilesToUpload + ftpFilesToUpload.size();
                count++;
                ftpDAOPool.get(0).setCount(count);
                ftpDAOPool.get(0).updateObserverCountWithText();
            }
        }
        finally {
            // Disconnect
            ftpDAOPool.get(0).disconnect();
        }

        String repositoryPath = repository.getPath();
        long totalFilesSize = 0;

        // Determine total files size
        for (Iterator<List<FTPFile>> iter = mapFtpFilesToUpload.values().iterator(); iter.hasNext();) {
            List<FTPFile> list = iter.next();
            for (FTPFile ftpFile : list) {
                String relativePath = ftpFile.getLink();
                String fileName = ftpFile.getName();
                boolean isFile = (ftpFile.getType() == FTPFile.FILE_TYPE) ? true : false;
                if (isFile) {
                    File file = new File(repositoryPath + "/" + relativePath + "/" + fileName);
                    if (!file.exists()) {
                        throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
                    }
                    else {
                        totalFilesSize = totalFilesSize + file.length();
                    }
                }
            }
        }

        // Set upload total size
        ftpDAOPool.get(0).updateObserverUploadTotalSize(totalFilesSize);

        // Upload file
        ftpDAOPool.get(0).updateObserverCountWithText("Uploading files...");

        try {
            String repositoryRemotePath = repository.getRepositoryUploadProtocole().getRemotePath();

            // Reconnect
            ftpDAOPool.get(0).connectToRepository(repository.getName(),
                    repository.getRepositoryUploadProtocole());

            for (Iterator<List<FTPFile>> iter = mapFtpFilesToUpload.values().iterator(); iter
                    .hasNext();) {
                List<FTPFile> list = iter.next();
                for (FTPFile ftpFile : list) {
                    if (ftpDAOPool.get(0).isCanceled()) {
                        return;
                    }
                    else {
                        String relativePath = ftpFile.getLink();
                        String fileName = ftpFile.getName();
                        ftpDAOPool.get(0).updateObserverCountWithText(
                                "Uploading file: " + relativePath + "/" + fileName);
                        boolean ok = ftpDAOPool.get(0).uploadFile(ftpFile, repositoryPath,
                                repositoryRemotePath);
                        if (!ok) {
                            throw new IOException("Failed to upload file: " + relativePath + "/"
                                    + fileName);
                        }
                    }
                }
                ftpDAOPool.get(0).updateObserverUploadLastIndexFileUploaded();
            }

            // Delete files extra remote files
            ftpDAOPool.get(0).updateObserverCountWithText("Deleting extra remote files...");

            ftpDAOPool.get(0).setTotalCount(filesToDelete.size());
            count = 0;

            for (SyncTreeNodeDTO node : filesToDelete) {
                if (ftpDAOPool.get(0).isCanceled()) {
                    return;
                }
                else {
                    String relativePath = determinePath(node);
                    if (node.isLeaf()) {
                        SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
                        String fileName = leaf.getName();
                        FTPFile ftpFile = createFTPFile(fileName, relativePath, FTPFile.FILE_TYPE);
                        ftpDAOPool.get(0).deleteFile(ftpFile, repositoryRemotePath);
                    }
                    else {
                        String fileName = node.getName();
                        FTPFile ftpFile = createFTPFile(fileName, relativePath,
                                FTPFile.DIRECTORY_TYPE);
                        ftpDAOPool.get(0).deleteFile(ftpFile, repositoryRemotePath);
                    }
                }
                count++;
                ftpDAOPool.get(0).setCount(count);
                ftpDAOPool.get(0).updateObserverCountWithText();
            }

            // Upload sync files
            ftpDAOPool.get(0).updateObserverCountWithText("Uploading synchronization files...");

            // Set serverInfo with upload options
            repository.getLocalServerInfo().setCompressedPboFilesOnly(
                    repository.isUploadCompressedPboFilesOnly());

            ftpDAOPool.get(0).uploadSync(repository.getLocalSync(), repositoryRemotePath);
            ftpDAOPool.get(0).uploadServerInfo(repository.getLocalServerInfo(),
                    repositoryRemotePath);
            ftpDAOPool.get(0).uploadChangelogs(repository.getLocalChangelogs(),
                    repositoryRemotePath);
            ftpDAOPool.get(0).uploadAutoconfig(repository.getLocalAutoConfig(),
                    repositoryRemotePath);
            if (repository.getLocalEvents() != null) {
                ftpDAOPool.get(0).uploadEvents(repository.getLocalEvents(), repositoryRemotePath);
            }
        }
        finally {
            ftpDAOPool.get(0).disconnect();
        }
    }

    private FTPFile createFTPFile(String fileName, String remotePath, int fileType) {

        FTPFile ftpFile = new FTPFile();
        ftpFile.setName(fileName);
        ftpFile.setLink(remotePath);
        ftpFile.setType(fileType);
        return ftpFile;
    }

    /* Check Repository synchronization */

    @Override
    public List<Exception> checkRepositoryContent(String repositoryName)
            throws RepositoryException, IOException {

        Repository repository = repositoryDAO.getMap().get(repositoryName);
        if (repository == null) {
            throw new RepositoryNotFoundException(repositoryName);
        }

        SyncTreeDirectory sync = repository.getSync();
        if (sync == null) {
            throw new SyncFileNotFoundException(repositoryName);
        }

        ServerInfo serverInfo = repository.getServerInfo();
        if (serverInfo == null) {
            throw new ServerInfoNotFoundException(repositoryName);
        }

        /* Get Files */
        this.checkRepositoryFilesList = new ArrayList<SyncTreeLeaf>();
        getFiles(sync);

        /* Errors */
        List<Exception> errorsCheckRepository = new ArrayList<Exception>();

        ftpDAOPool.get(0).setTotalCount(this.checkRepositoryFilesList.size());
        ftpDAOPool.get(0).setCount(0);
        int count = 0;

        try {
            // Connect
            ftpDAOPool.get(0).connectToRepository(repositoryName, repository.getProtocol());

            for (SyncTreeLeaf leaf : this.checkRepositoryFilesList) {
                if (ftpDAOPool.get(0).isCanceled()) {
                    break;
                }
                else {
                    String relativePath = determinePath(leaf);
                    List<String> listFilesName = new ArrayList<String>();
                    if (leaf.isCompressed()) {
                        if (serverInfo.isCompressedPboFilesOnly()) {
                            String fileName = leaf.getName() + ZIP_EXTENSION;// *.pbo.zip
                            listFilesName.add(fileName);
                        }
                        else {
                            String fileName = leaf.getName();// *.*
                            listFilesName.add(fileName);
                            fileName = leaf.getName() + ZIP_EXTENSION;// *.pbo.zip
                            listFilesName.add(fileName);
                            if (repository.getProtocol() instanceof Http) {
                                fileName = leaf.getName() + ZSYNC_EXTENSION;// *.pbo.zsync
                                listFilesName.add(fileName);
                            }
                        }
                    }
                    else {
                        String fileName = leaf.getName();// *.*
                        listFilesName.add(fileName);
                        if (repository.getProtocol() instanceof Http) {
                            fileName = leaf.getName() + ZSYNC_EXTENSION;// *.pbo.zsync
                            listFilesName.add(fileName);
                        }
                    }
                    for (String fileName : listFilesName) {
                        boolean found = remoteFileExists(repository.getName(), relativePath,
                                fileName, repository.getProtocol());
                        if (!found) {
                            errorsCheckRepository.add(new FileNotFoundException(
                                    "File not found on repository: " + relativePath + "/"
                                            + fileName));
                            ftpDAOPool.get(0).updateObserverCheckCountError(
                                    errorsCheckRepository.size());
                        }
                    }
                    count++;
                    ftpDAOPool.get(0).setCount(count);
                }
                ftpDAOPool.get(0).updateObserverCheckProgress();
            }
        }
        finally {
            // Disconnect
            ftpDAOPool.get(0).disconnect();
        }

        return errorsCheckRepository;
    }

    private boolean remoteFileExists(String repositoryName, String relativePath, String fileName,
            AbstractProtocole protocole) throws IOException {

        return ftpDAOPool.get(0).fileExists(repositoryName, relativePath, fileName, protocole);
    }

    private boolean remoteDirectoryExists(String repositoryRemotePath, String relativePath,
            String fileName) throws IOException {

        String remotePath = repositoryRemotePath + "/" + relativePath;
        return ftpDAOPool.get(0).directoryExists(remotePath, fileName);
    }

    /* Check for Updates */

    public String checkForUpdates(boolean devMode) throws FtpException {

        String response = null;
        try {
            String updateVersionName = ftpDAOPool.get(0).downloadXMLupdateFile(devMode);
            ftpDAOPool.get(0).disconnect();
            if (updateVersionName != null) {
                System.out.println("ArmA3Sync Available update version = " + updateVersionName);

                StringTokenizer stringTokenizer = new StringTokenizer(updateVersionName, ".");

                if (stringTokenizer.countTokens() == 3) {
                    String major = stringTokenizer.nextToken();
                    String minor = stringTokenizer.nextToken();
                    String build = stringTokenizer.nextToken();

                    int updateBuild = Integer.parseInt(build);
                    int actualBuild = Version.getBuild();

                    if (updateBuild > actualBuild) {
                        response = updateVersionName;
                    }
                }
            }
            else {
                response = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new FtpException("Failed to connect to updates repository");
        }
        finally {
            ftpDAOPool.get(0).disconnect();
        }
        return response;
    }

    /* Cancel */

    @Override
    public void cancel() {
        unZipFlowProcessor.cancel();
        for (FtpDAO ftpDAO : ftpDAOPool) {
            ftpDAO.cancel();
        }
        for (FtpDAO ftpDAO : ftpDAOPool) {
            ftpDAO.disconnect();
        }
    }

    /* Getters */

    @Override
    public AbstractConnexionDAO getConnexionDAO() {
        return ftpDAOPool.get(0);
    }

    @Override
    public List<AbstractConnexionDAO> getConnexionDAOs() {
        List<AbstractConnexionDAO> list = new ArrayList<>();
        for (FtpDAO ftpDAO : ftpDAOPool) {
            list.add(ftpDAO);
        }
        return list;
    }

    @Override
    public int getNumberConnections() {
        return ftpDAOPool.size();
    }

    @Override
    public void setMaximumClientDownloadSpeed(double value) {
        for (FtpDAO ftpDAO : ftpDAOPool) {
            ftpDAO.setMaximumClientDownloadSpeed(value);
        }
    }
}
