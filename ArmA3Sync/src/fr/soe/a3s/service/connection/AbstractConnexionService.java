package fr.soe.a3s.service.connection;

import java.util.ArrayList;
import java.util.List;

import fr.soe.a3s.dao.ConfigurationDAO;
import fr.soe.a3s.domain.configration.FavoriteServer;
import fr.soe.a3s.domain.repository.AutoConfig;
import fr.soe.a3s.service.ObjectDTOtransformer;

public class AbstractConnexionService extends ObjectDTOtransformer {

	protected static final ConfigurationDAO configurationDAO = new ConfigurationDAO();

	protected void updateFavoriteServersFromAutoconfig(AutoConfig autoConfig) {

		List<FavoriteServer> list1 = autoConfig.getFavoriteServers();
		List<FavoriteServer> list2 = configurationDAO.getConfiguration()
				.getFavoriteServers();

		List<FavoriteServer> newList = new ArrayList<FavoriteServer>();

		for (FavoriteServer favoriteServerList2 : list2) {
			if (!autoConfig.getRepositoryName().equals(
					favoriteServerList2.getRepositoryName())) {
				boolean nameIsDifferent = true;
				for (FavoriteServer favoriteServerList1 : list1) {
					if (favoriteServerList1.getName().equals(
							favoriteServerList2.getName())) {
						nameIsDifferent = false;
					}
				}
				if (nameIsDifferent) {
					newList.add(favoriteServerList2);
				}
			}
		}
		newList.addAll(list1);

		configurationDAO.getConfiguration().getFavoriteServers().clear();
		configurationDAO.getConfiguration().getFavoriteServers()
				.addAll(newList);
	}
}
