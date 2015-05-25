package fr.soe.a3s.dto.configuration;

public class FavoriteServerDTO {

	private String name;
	private String ipAddress;
	private int port;
	private String password;
	private String modsetName;
	private String repositoryName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getModsetName() {
		return modsetName;
	}

	public void setModsetName(String modsetName) {
		this.modsetName = modsetName;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
}
