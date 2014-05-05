package fr.soe.a3s.domain.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.domain.configration.FavoriteServer;

public class AutoConfig implements Serializable {

	private static final long serialVersionUID = 2706280725619197755L;
	private String repositoryName;
	private AbstractProtocole protocole;
	private List<FavoriteServer> favoriteServers = new ArrayList<FavoriteServer>();

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public AbstractProtocole getProtocole() {
		return protocole;
	}

	public void setProtocole(AbstractProtocole protocole) {
		this.protocole = protocole;
	}

	public List<FavoriteServer> getFavoriteServers() {
		if (favoriteServers == null) {
			favoriteServers = new ArrayList<FavoriteServer>();
		}
		return favoriteServers;
	}
}
