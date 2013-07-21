package fr.soe.a3s.domain.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.soe.a3s.domain.AbstractProtocole;

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
	private transient ServerInfo serverInfo;
	private transient SyncTreeDirectory sync;
	private transient Changelogs changelogs;
	private transient boolean downloading;
	/* Resuming download */
	private transient int lastIndexFileDownloaded;
	private transient long incrementedFilesSize;
	private transient boolean resume;
	/* Events */; 
	private transient Events events;
	
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

	public int getLastIndexFileDownloaded() {
		return lastIndexFileDownloaded;
	}

	public void setLastIndexFileDownloaded(int lastIndexFileDownloaded) {
		this.lastIndexFileDownloaded = lastIndexFileDownloaded;
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
	
}
