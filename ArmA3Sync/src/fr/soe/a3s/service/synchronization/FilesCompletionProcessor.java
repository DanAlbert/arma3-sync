package fr.soe.a3s.service.synchronization;

import java.util.ArrayList;
import java.util.List;

import fr.soe.a3s.constant.RepositoryStatus;
import fr.soe.a3s.controller.ObserverCountInt;
import fr.soe.a3s.controller.ObserverEnd;
import fr.soe.a3s.controller.ObserverError;
import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.service.connection.ConnexionService;
import fr.soe.a3s.service.connection.ConnexionServiceFactory;

public class FilesCompletionProcessor {

	/* Data */
	private final String repositoryName;
	/* Services */
	private final RepositoryService repositoryService = new RepositoryService();
	private ConnexionService connexionService;
	/* observers */
	private ObserverCountInt observerCount;// null for no recording
	private ObserverEnd observerEnd;// not null
	private ObserverError observerError;// not null

	public FilesCompletionProcessor(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public String run(SyncTreeDirectoryDTO parent) {

		assert (parent != null);

		String serverRangeRequestResponseHeader = null;
		try {
			repositoryService.setCheckingForAddons(repositoryName, true);

			// Determine number of connections to use
			int numberConnections = repositoryService
					.getServerInfoNumberOfConnections(repositoryName);

			if (numberConnections > Runtime.getRuntime().availableProcessors()) {
				numberConnections = Runtime.getRuntime().availableProcessors();
			}

			connexionService = ConnexionServiceFactory
					.getServiceForFilesSynchronization(repositoryName,
							numberConnections);

			for (AbstractConnexionDAO connect : connexionService
					.getConnexionDAOs()) {
				connect.addObserverCount(new ObserverCountInt() {
					@Override
					public void update(int value) {
						executeUpdate(value);
					}
				});
				connect.addObserverEnd(new ObserverEnd() {
					@Override
					public void end() {
						executeEnd();
					}
				});
				connect.addObserverError(new ObserverError() {
					@Override
					public void error(List<Exception> errors) {
						executeError(errors);
					}
				});
			}

			serverRangeRequestResponseHeader = connexionService
					.determineFilesCompletion(repositoryName, parent);

		} catch (Exception e) {
			List<Exception> errors = new ArrayList<Exception>();
			errors.add(e);
			executeError(errors);
		}
		return serverRangeRequestResponseHeader;
	}

	private void executeUpdate(int value) {
		if (observerCount != null) {
			observerCount.update(value);
		}
	}

	private void executeEnd() {
		repositoryService.setCheckingForAddons(repositoryName, false);
		observerEnd.end();
	}

	private void executeError(List<Exception> errors) {
		repositoryService.setCheckingForAddons(repositoryName, false);
		observerError.error(errors);
	}

	public void cancel() {
		repositoryService.setCheckingForAddons(repositoryName, false);
		if (connexionService != null) {
			connexionService.cancel();
		}
	}

	public void addObserverCount(ObserverCountInt obs) {
		this.observerCount = obs;
	}

	public void addObserverEnd(ObserverEnd obs) {
		this.observerEnd = obs;
	}

	public void addObserverError(ObserverError obs) {
		this.observerError = obs;
	}
}
