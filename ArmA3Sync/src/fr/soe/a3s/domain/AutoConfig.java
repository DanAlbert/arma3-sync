package fr.soe.a3s.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.soe.a3s.domain.configration.FavoriteServer;
import fr.soe.a3s.domain.repository.Repository;

public class AutoConfig implements Serializable{

	private List<Profile> profiles = new ArrayList<Profile>();
	
	private List<FavoriteServer> favoriteServers = new ArrayList<FavoriteServer>();

	private List<Repository> repositories = new ArrayList<Repository>();

	public List<Profile> getProfiles() {
		return profiles;
	}

	public List<FavoriteServer> getFavoriteServers() {
		return favoriteServers;
	}

	public List<Repository> getRepositories() {
		return repositories;
	}

}

