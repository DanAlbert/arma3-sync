package fr.soe.a3s.domain.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.configration.FavoriteServer;

public class Repository implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8142021113361619970L;
	private String name;
	private AbstractProtocole protocole;
	private boolean notify = false;
	private int revision;
	private String path;
	private String autoConfigURL;
	private String defaultDownloadLocation;
	private boolean outOfSynk = false;
	private boolean noAutoDiscover = true;
	/* Build options */
	private List<FavoriteServer> favoriteServersSetToAutoconfig = new ArrayList<FavoriteServer>();
	// Path of file excluded from build tolowercase!
	private Set<String> excludedFilesFromBuild = new HashSet<String>();
	private Set<String> excludedFoldersFromSync = new HashSet<String>();
	/* Local data: Hide extra local folder content */
	private Set<String> hidedFolderPaths = new HashSet<String>();
	/* Local data: SHA1 computation <Path,FileAttrbutes> */
	private Map<String, FileAttributes> mapFiles = new HashMap<String, FileAttributes>();
	/* Remote files */
	private transient ServerInfo serverInfo;
	private transient SyncTreeDirectory sync;
	private transient Changelogs changelogs;
	private transient Events events;
	private transient AutoConfig autoConfig;
	/* Local files */
	private transient ServerInfo localServerInfo;
	private transient SyncTreeDirectory localSync;
	private transient Changelogs localChangelogs;
	private transient AutoConfig localAutoConfig;
	/* Resuming download */
	private transient boolean downloading;
	private transient int lastIndexFileTransfered;
	private transient long incrementedFilesSize;
	private transient boolean resume;
	/* Repository upload */
	private transient boolean uploading;
	private AbstractProtocole repositoryUploadProtocole;

	public Repository(String name, AbstractProtocole protocole) {
		this.name = name;
		this.protocole = protocole;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AbstractProtocole getProtocole() {
		return protocole;
	}

	public void setProtocole(AbstractProtocole protocole) {
		this.protocole = protocole;
	}

	public boolean isNotify() {
		return notify;
	}

	public void setNotify(boolean verifySync) {
		this.notify = verifySync;
	}

	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getAutoConfigURL() {
		return autoConfigURL;
	}

	public void setAutoConfigURL(String autoConfigURL) {
		this.autoConfigURL = autoConfigURL;
	}

	public AutoConfig getAutoConfig() {
		return autoConfig;
	}

	public void setAutoConfig(AutoConfig autoConfig) {
		this.autoConfig = autoConfig;
	}

	public ServerInfo getServerInfo() {
		return serverInfo;
	}

	public void setServerInfo(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}

	public SyncTreeDirectory getSync() {
		return sync;
	}

	public void setSync(SyncTreeDirectory sync) {
		this.sync = sync;
	}

	public String getDefaultDownloadLocation() {
		return defaultDownloadLocation;
	}

	public void setDefaultDownloadLocation(String defaultDownloadLocation) {
		this.defaultDownloadLocation = defaultDownloadLocation;
	}

	public boolean isOutOfSynk() {
		return outOfSynk;
	}

	public void setOutOfSynk(boolean outOfSynk) {
		this.outOfSynk = outOfSynk;
	}

	public boolean isDownloading() {
		return downloading;
	}

	public void setDownloading(boolean downloading) {
		this.downloading = downloading;
	}

	public int getLastIndexFileTransfered() {
		return lastIndexFileTransfered;
	}

	public void setLastIndexFileTransfered(int lastIndexFileTransfered) {
		this.lastIndexFileTransfered = lastIndexFileTransfered;
	}

	public long getIncrementedFilesSize() {
		return incrementedFilesSize;
	}

	public void setIncrementedFilesSize(long incrementedFilesSize) {
		this.incrementedFilesSize = incrementedFilesSize;
	}

	public void setResume(boolean value) {
		this.resume = value;
	}

	public boolean isResume() {
		return resume;
	}

	public Changelogs getChangelogs() {
		return changelogs;
	}

	public void setChangelogs(Changelogs changelogs) {
		this.changelogs = changelogs;
	}

	public Events getEvents() {
		return events;
	}

	public void setEvents(Events events) {
		this.events = events;
	}

	public Map<String, FileAttributes> getMapFiles() {
		if (mapFiles == null) {
			mapFiles = new HashMap<String, FileAttributes>();
		}
		return mapFiles;
	}

	public void setMapFiles(Map<String, FileAttributes> mapFiles) {
		this.mapFiles = mapFiles;
	}

	public boolean isNoAutoDiscover() {
		return noAutoDiscover;
	}

	public void setNoAutoDiscover(boolean noAutoDiscover) {
		this.noAutoDiscover = noAutoDiscover;
	}

	public Set<String> getHiddenFolderPath() {
		if (hidedFolderPaths == null) {
			hidedFolderPaths = new HashSet<String>();
		}
		return hidedFolderPaths;
	}

	public List<FavoriteServer> getFavoriteServersSetToAutoconfig() {
		if (favoriteServersSetToAutoconfig == null) {
			favoriteServersSetToAutoconfig = new ArrayList<FavoriteServer>();
		}
		return favoriteServersSetToAutoconfig;
	}

	public Set<String> getExcludedFilesFromBuild() {
		if (excludedFilesFromBuild == null) {
			excludedFilesFromBuild = new HashSet<String>();
		}
		return excludedFilesFromBuild;
	}

	public Set<String> getExcludedFoldersFromSync() {
		if (excludedFoldersFromSync == null) {
			excludedFoldersFromSync = new HashSet<String>();
		}
		return excludedFoldersFromSync;
	}

	public boolean isUploading() {
		return this.uploading;
	}

	public void setUploading(boolean value) {
		this.uploading = value;
	}

	public AbstractProtocole getRepositoryUploadProtocole() {
		return repositoryUploadProtocole;
	}

	public void setRepositoryUploadProtocole(
			AbstractProtocole repositoryUploadProtocole) {
		this.repositoryUploadProtocole = repositoryUploadProtocole;
	}

	/* Local files */

	public SyncTreeDirectory getLocalSync() {
		return localSync;
	}

	public void setLocalSync(SyncTreeDirectory localSync) {
		this.localSync = localSync;
	}

	public ServerInfo getLocalServerInfo() {
		return localServerInfo;
	}

	public void setLocalServerInfo(ServerInfo localServerInfo) {
		this.localServerInfo = localServerInfo;
	}

	public Changelogs getLocalChangelogs() {
		return localChangelogs;
	}

	public void setLocalChangelogs(Changelogs localChangelogs) {
		this.localChangelogs = localChangelogs;
	}

	public AutoConfig getLocalAutoConfig() {
		return localAutoConfig;
	}

	public void setLocalAutoConfig(AutoConfig localaAutoConfig) {
		this.localAutoConfig = localaAutoConfig;
	}
}
