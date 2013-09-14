package fr.soe.a3s.service;

import java.util.List;

import fr.soe.a3s.dao.CommonDAO;
import fr.soe.a3s.dao.ConfigurationDAO;
import fr.soe.a3s.dao.PreferencesDAO;
import fr.soe.a3s.dao.ProfileDAO;
import fr.soe.a3s.dao.RepositoryDAO;
import fr.soe.a3s.domain.AutoConfig;
import fr.soe.a3s.domain.Profile;
import fr.soe.a3s.domain.configration.FavoriteServer;
import fr.soe.a3s.domain.repository.Repository;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.WritingException;

public class CommonService {

	private static final ConfigurationDAO configurationDAO = new ConfigurationDAO();
	private static final ProfileDAO profileDAO = new ProfileDAO();
	private static final PreferencesDAO preferencesDAO = new PreferencesDAO();
	private static final RepositoryDAO repositoryDAO = new RepositoryDAO();
	private static final CommonDAO commonDAO = new CommonDAO();
	private static String WIKI = "http://www.sonsofexiled.fr/wiki/index.php/1._ArmA3Sync";
	private static String BIS = "http://forums.bistudio.com/showthread.php?162236-ArmA3Sync-launcher-and-addons-synchronization-software-for-ArmA-3&p=2477805#post2477805";

	public void saveAllParameters() throws WritingException {

		configurationDAO.write();
		profileDAO.writeProfiles();
		preferencesDAO.write();
	}

	public void exportAutoConfig(List<String> listSelectedProfileNames,
			List<String> listSelectedFavoriteServerNames,
			List<String> listSelectedRepositoryNames, String path)
			throws WritingException {

		AutoConfig autoConfig = new AutoConfig();

		for (String profileName : listSelectedProfileNames) {
			Profile profile = (Profile) profileDAO.getMap().get(profileName);
			if (profile != null) {
				autoConfig.getProfiles().add(profile);
			}
		}

		List<FavoriteServer> favoriteServers = configurationDAO
				.getConfiguration().getFavoriteServers();

		for (FavoriteServer favoriteServer : favoriteServers) {
			if (listSelectedFavoriteServerNames.contains(favoriteServer
					.getName())) {
				autoConfig.getFavoriteServers().add(favoriteServer);
			}
		}

		for (String repositoryName : listSelectedRepositoryNames) {
			Repository repository = repositoryDAO.getMap().get(repositoryName);
			if (repository != null) {
			    Repository clonedRepository = new Repository(repository.getName(), repository.getProtocole());
				autoConfig.getRepositories().add(clonedRepository);
			}
		}

		try {
			commonDAO.exportAutoConfig(autoConfig, path);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WritingException();
		}
	}

	public void importAutoConfig(String path) throws LoadingException {

		AutoConfig autoConfig = null;
		try {
			autoConfig = commonDAO.importAutoConfig(path);
			if (autoConfig == null) {
				throw new LoadingException();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new LoadingException();
		}

		List<Profile> listProfiles = autoConfig.getProfiles();

		for (Profile profile : listProfiles) {
			profileDAO.getMap().put(profile.getName(), profile);
		}

		List<FavoriteServer> favoriteServers = autoConfig.getFavoriteServers();

		for (FavoriteServer favoriteServer : favoriteServers) {
				configurationDAO.getConfiguration().getFavoriteServers()
						.add(favoriteServer);
		}

		List<Repository> repositories = autoConfig.getRepositories();

		for (Repository repository : repositories) {
			if (!repositoryDAO.getMap().containsKey(repository.getName())) {
				repositoryDAO.getMap().put(repository.getName(), repository);
			}
		}
		
	}

	public String getWiki() {
		return WIKI;
	}
	
	public String getBIS(){
		return BIS;
	}
}
