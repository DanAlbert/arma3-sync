package fr.soe.a3s.dto;

public class RepositoryDTO implements java.lang.Comparable {

	private String name;
	private boolean notify;
	private ProtocolDTO protocoleDTO;
	private int revision;
	private String path;
	private String autoConfigURL;
	private ProtocolDTO repositoryUploadProtocoleDTO;
	private ProtocolDTO repositoryProxyProtocoleDTO;
	private boolean enableProxy;
	private boolean auto;

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

	public ProtocolDTO getProtocoleDTO() {
		return protocoleDTO;
	}

	public void setProtocoleDTO(ProtocolDTO protocoleDTO) {
		this.protocoleDTO = protocoleDTO;
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

	public ProtocolDTO getUploadProtocoleDTO() {
		return repositoryUploadProtocoleDTO;
	}

	public void setUploadProtocoleDTO(ProtocolDTO repositoryUploadProtocoleDTO) {
		this.repositoryUploadProtocoleDTO = repositoryUploadProtocoleDTO;
	}

	public ProtocolDTO getProxyProtocoleDTO() {
		return repositoryProxyProtocoleDTO;
	}

	public void setProxyProtocoleDTO(ProtocolDTO repositoryProxyProtocoleDTO) {
		this.repositoryProxyProtocoleDTO = repositoryProxyProtocoleDTO;
	}

	public boolean isEnableProxy() {
		return enableProxy;
	}

	public void setEnableProxy(boolean enableProxy) {
		this.enableProxy = enableProxy;
	}

	@Override
	public int compareTo(Object other) {
		String name = ((RepositoryDTO) other).getName();
		int result = 1;
		if (name.compareToIgnoreCase(getName()) > 0)
			result = -1;
		else if (name.compareToIgnoreCase(getName()) == 0)
			result = 0;
		return result;
	}

	public boolean isAuto() {
		return auto;
	}

	public void setAuto(boolean value) {
		this.auto = value;
	}
}
