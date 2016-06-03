package fr.soe.a3s.dto;

public class RepositoryDTO implements java.lang.Comparable {

	private String name;
	private boolean notify;
	private ProtocolDTO protocoleDTO;
	private int revision;
	private String path;
	private String autoConfigURL;
	private ProtocolDTO repositoryUploadProtocoleDTO;

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

	public ProtocolDTO getRepositoryUploadProtocoleDTO() {
		return repositoryUploadProtocoleDTO;
	}

	public void setRepositoryUploadProtocoleDTO(
			ProtocolDTO repositoryUploadProtocoleDTO) {
		this.repositoryUploadProtocoleDTO = repositoryUploadProtocoleDTO;
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
}
