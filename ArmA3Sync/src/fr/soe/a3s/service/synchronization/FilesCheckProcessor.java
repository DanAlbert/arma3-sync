package fr.soe.a3s.service.synchronization;

import java.util.ArrayList;
import java.util.List;

import fr.soe.a3s.controller.ObserverCountInt;
import fr.soe.a3s.controller.ObserverError;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.exception.remote.RemoteEventsFileNotFoundException;
import fr.soe.a3s.exception.remote.RemoteServerInfoFileNotFoundException;
import fr.soe.a3s.exception.remote.RemoteSyncFileNotFoundException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.service.connection.ConnexionService;
import fr.soe.a3s.service.connection.ConnexionServiceFactory;

public class FilesCheckProcessor {

	/* Data */
	private final String repositoryName;
	private final boolean withEvents;
	/* Services */
	private ConnexionService connexionService;
	private final RepositoryService repositoryService = new RepositoryService();;
	/* observers */
	private ObserverCountInt observerCount;// null for no recording
	private ObserverError observerError;// not null

	public FilesCheckProcessor(String repositoryName, boolean withEvents) {
		this.repositoryName = repositoryName;
		this.withEvents = withEvents;
	}

	public SyncTreeDirectoryDTO run() {

		SyncTreeDirectoryDTO parent = null;

		try {
			repositoryService.setCheckingForAddons(repositoryName, true);

			connexionService = ConnexionServiceFactory
					.getServiceForRepositoryManagement(repositoryName);
			connexionService.checkRepository(repositoryName);

			if (repositoryService.getSync(repositoryName) == null) {
				throw new RemoteSyncFileNotFoundException();
			}

			if (repositoryService.getServerInfo(repositoryName) == null) {
				throw new RemoteServerInfoFileNotFoundException();
			}

			if (withEvents
					&& repositoryService.getEvents(repositoryName) == null) {
				throw new RemoteEventsFileNotFoundException();
			}

			repositoryService.getRepositorySHA1Processor().addObserverCount(
					new ObserverCountInt() {
						@Override
						public void update(int value) {
							executeUpdate(value);
						}
					});

			parent = repositoryService.checkForAddons(repositoryName);
			repositoryService.write(repositoryName);// save SHA1 computations

		} catch (Exception e) {
			List<Exception> errors = new ArrayList<Exception>();
			errors.add(e);
			observerError.error(errors);
		} finally {
			repositoryService.setCheckingForAddons(repositoryName, false);
		}
		return parent;
	}

	private void executeUpdate(int value) {

		if (observerCount != null) {
			observerCount.update(value);
		}
	}

	public void cancel() {

		if (connexionService != null) {
			connexionService.cancel();
		}
		repositoryService.cancel();
	}

	public void addObserverCount(ObserverCountInt obs) {
		this.observerCount = obs;
	}

	public void addObserverError(ObserverError obs) {
		this.observerError = obs;
	}
}
