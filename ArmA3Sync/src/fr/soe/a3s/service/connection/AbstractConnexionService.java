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

		if (autoConfig != null) {
			List<FavoriteServer> list1 = autoConfig.getFavoriteServers();
			for (FavoriteServer favoriteServerList1 : list1) {
				favoriteServerList1.setRepositoryName(autoConfig
						.getRepositoryName());
			}

			List<FavoriteServer> list2 = configurationDAO.getConfiguration()
					.getFavoriteServers();

			List<FavoriteServer> newList2 = new ArrayList<FavoriteServer>();

			for (FavoriteServer favoriteServerList1 : list1) {
				newList2.add(favoriteServerList1);
			}
			
			for (FavoriteServer favoriteServerList2 : list2) {
				boolean found = false;
				for (FavoriteServer favoriteServerNewList2 : newList2) {
					if (favoriteServerNewList2.getName().equals(
							favoriteServerList2.getName())) {
						found = true;
						favoriteServerNewList2.setIpAddress(favoriteServerList2.getIpAddress());
						favoriteServerNewList2.setPort(favoriteServerList2.getPort());
						favoriteServerNewList2.setPassword(favoriteServerList2.getPassword());
						if (favoriteServerList2.getModsetName()!=null){
							if (!favoriteServerList2.getModsetName().isEmpty()){
								favoriteServerNewList2.setModsetName(favoriteServerList2.getModsetName());
							}
						}
						break;
					}
				}
				if (!found){
					newList2.add(favoriteServerList2);
				}else {
					
				}
			}

			configurationDAO.getConfiguration().getFavoriteServers().clear();
			configurationDAO.getConfiguration().getFavoriteServers()
					.addAll(newList2);
		}
	}
}
