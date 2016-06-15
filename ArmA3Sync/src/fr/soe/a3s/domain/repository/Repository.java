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
	private String name;// not null
	private AbstractProtocole protocole;// not null
	private AbstractProtocole bitTorrentProtocole;
	private boolean notify = false;
	private int revision;
	private String path;
	private String autoConfigURL;
	private String defaultDownloadLocation;
	@Deprecated
	private final boolean outOfSynk = false;
	private boolean noAutoDiscover = true;
	private boolean exactMatch = false;

	/** Local set for Hide extra local folder content */
	private Set<String> hidddenFolderPaths = new HashSet<String>();

	/** Check for Addons */
	private transient boolean checkingForAddons = false;
	private transient ServerInfo serverInfo;// Gets from remote location
	private transient SyncTreeDirectory sync;// Gets from remote location
	private transient Changelogs changelogs;// Gets from remote location
	private transient Events events;// Gets from remote location
	private transient AutoConfig autoConfig;// Gets from remote location
	private Map<String, FileAttributes> mapFilesForSync = new HashMap<String, FileAttributes>();// <Path,FileAttrbutes>

	/** Repository download */
	private int numberOfClientConnections;
	private double maximumClientDownloadSpeed;
	private transient String downloadReport;
	private transient boolean downloading = false;

	/** Repository upload */
	private AbstractProtocole uploadProtocole;
	private boolean uploadCompressedPboFilesOnly = false;
	private transient ServerInfo localServerInfo;
	private transient SyncTreeDirectory localSync;
	private transient Changelogs localChangelogs;
	private transient AutoConfig localAutoConfig;
	private transient Events localEvents;
	private transient boolean uploading;
	private transient int lastIndexFileTransfered;
	private transient long incrementedFilesSize;

	/** Repository build */
	private int numberOfConnections = 1;
	private boolean compressed = false;
	private boolean noPartialFileTransfer = false;
	private List<FavoriteServer> favoriteServersSetToAutoconfig = new ArrayList<FavoriteServer>();
	private Set<String> excludedFilesFromBuild = new HashSet<String>();
	private Set<String> excludedFoldersFromSync = new HashSet<String>();
	private transient boolean building = false;
	private Map<String, FileAttributes> mapFilesForBuild = new HashMap<String, FileAttributes>();// <Path,FileAttrbutes>

	/** Repository check */
	private transient boolean checking = false;

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

	public AbstractProtocole getProtocol() {
		return protocole;
	}

	public void setProtocol(AbstractProtocole protocol) {
		this.protocole = protocol;
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

	public boolean isDownloading() {
		return downloading;
	}

	public void setDownloading(boolean downloading) {
		this.downloading = downloading;
	}

	public int getLastIndexFileTransfered() {
		return lastIndexFileTransfered;
	}

	public void setLastIndexFileDonwloaded(int lastIndexFileDownloaded) {
		this.lastIndexFileTransfered = lastIndexFileDownloaded;
	}

	public long getIncrementedFilesSize() {
		return incrementedFilesSize;
	}

	public void setIncrementedFilesSize(long incrementedFilesSize) {
		this.incrementedFilesSize = incrementedFilesSize;
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

	public Map<String, FileAttributes> getMapFilesForSync() {
		if (mapFilesForSync == null) {
			mapFilesForSync = new HashMap<String, FileAttributes>();
		}
		return mapFilesForSync;
	}

	public void setMapFilesForSync(Map<String, FileAttributes> mapFilesForSync) {
		this.mapFilesForSync = mapFilesForSync;
	}

	public Map<String, FileAttributes> getMapFilesForBuild() {
		if (mapFilesForBuild == null) {
			mapFilesForBuild = new HashMap<String, FileAttributes>();
		}
		return mapFilesForBuild;
	}

	public void setMapFilesForBuild(Map<String, FileAttributes> mapFilesForBuild) {
		this.mapFilesForBuild = mapFilesForBuild;
	}

	public boolean isNoAutoDiscover() {
		return noAutoDiscover;
	}

	public void setNoAutoDiscover(boolean noAutoDiscover) {
		this.noAutoDiscover = noAutoDiscover;
	}

	public Set<String> getHiddenFolderPath() {
		if (hidddenFolderPaths == null) {
			hidddenFolderPaths = new HashSet<String>();
		}
		return hidddenFolderPaths;
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

	public AbstractProtocole getUploadProtocole() {
		return uploadProtocole;
	}

	public void setUploadProtocole(AbstractProtocole uploadProtocole) {
		this.uploadProtocole = uploadProtocole;
	}

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

	public Events getLocalEvents() {
		return localEvents;
	}

	public void setLocalEvents(Events localEvents) {
		this.localEvents = localEvents;
	}

	public boolean isExactMatch() {
		return this.exactMatch;
	}

	public void setExactMatch(boolean exactMatch) {
		this.exactMatch = exactMatch;
	}

	public int getNumberOfConnections() {
		return this.numberOfConnections;
	}

	public void setNumberOfConnections(int numberOfConnections) {
		this.numberOfConnections = numberOfConnections;
	}

	public boolean isCompressed() {
		return this.compressed;
	}

	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}

	public boolean isBuilding() {
		return this.building;
	}

	public void setBuilding(boolean value) {
		this.building = value;
	}

	public boolean isUsePartialFileTransfer() {
		if (this.noPartialFileTransfer) {
			return false;
		} else {
			return true;
		}
	}

	public void setUsePartialFileTransfer(boolean usePartialFileTransfer) {
		if (usePartialFileTransfer) {
			this.noPartialFileTransfer = false;
		} else {
			this.noPartialFileTransfer = true;
		}
	}

	public boolean isChecking() {
		return this.checking;
	}

	public void setChecking(boolean value) {
		this.checking = value;
	}

	public boolean isCheckingForAddons() {
		return checkingForAddons;
	}

	public void setCheckingForAddons(boolean value) {
		this.checkingForAddons = value;
	}

	public boolean isUploadCompressedPboFilesOnly() {
		return uploadCompressedPboFilesOnly;
	}

	public void setUploadCompressedPboFilesOnly(boolean value) {
		this.uploadCompressedPboFilesOnly = value;
	}

	public String getDownloadReport() {
		return this.downloadReport;
	}

	public void setDownloadReport(String downloadReport) {
		this.downloadReport = downloadReport;
	}

	public int getNumberOfClientConnections() {
		return numberOfClientConnections;
	}

	public void setNumberOfClientConnections(int numberOfClientConnections) {
		this.numberOfClientConnections = numberOfClientConnections;
	}

	public double getMaximumClientDownloadSpeed() {
		return maximumClientDownloadSpeed;
	}

	public void setMaximumClientDownloadSpeed(double maximumClientDownloadSpeed) {
		this.maximumClientDownloadSpeed = maximumClientDownloadSpeed;
	}
}
