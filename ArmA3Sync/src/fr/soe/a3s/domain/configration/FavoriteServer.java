package fr.soe.a3s.domain.configration;

import java.io.Serializable;

public class FavoriteServer implements Comparable, Serializable {

	private static final long serialVersionUID = 4613236292596702132L;
	private String name;
	private String ipAddress;
	private int port;
	private String password;
	private boolean selected = false;
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

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
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

	@Override
	public int compareTo(Object other) {
		String name = ((FavoriteServer) other).getName();
		int result = 1;
		if (name.compareToIgnoreCase(getName()) > 0)
			result = -1;
		else if (name.compareToIgnoreCase(getName()) == 0)
			result = 0;
		return result;
	}
}
