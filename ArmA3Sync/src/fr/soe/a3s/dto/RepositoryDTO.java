package fr.soe.a3s.dto;


public class RepositoryDTO {
	
	private String name;
	private boolean notify;
	private FtpDTO ftpDTO;
	private int revision;
	private String path;
	private String autoConfigURL;
	private boolean outOfSynk;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isNotify() {
		return notify;
	}
	public void setNotify(boolean notify) {
		this.notify = notify;
	}
	public FtpDTO getFtpDTO() {
		return ftpDTO;
	}
	public void setFtpDTO(FtpDTO ftpDTO) {
		this.ftpDTO = ftpDTO;
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
	public boolean isOutOfSynk() {
		return outOfSynk;
	}
	public void setOutOfSynk(boolean outOfSynk) {
		this.outOfSynk = outOfSynk;
	}

}
