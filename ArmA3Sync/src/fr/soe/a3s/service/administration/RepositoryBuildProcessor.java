package fr.soe.a3s.service.administration;

import java.util.ArrayList;
import java.util.List;

import fr.soe.a3s.controller.ObserverCountInt;
import fr.soe.a3s.controller.ObserverEnd;
import fr.soe.a3s.controller.ObserverError;
import fr.soe.a3s.controller.ObserverText;
import fr.soe.a3s.service.RepositoryService;

public class RepositoryBuildProcessor {

	private final String repositoryName;
	/* Services */
	private final RepositoryService repositoryService = new RepositoryService();
	/* observers */
	private ObserverCountInt observerCountProgress;// not null
	private ObserverText observerText;// not null
	private ObserverEnd observerEnd;// not null
	private ObserverError observerError;// not null
	private String path;

	public RepositoryBuildProcessor(String repositoryName, String path) {
		this.repositoryName = repositoryName;
		this.path = path;
	}

	public RepositoryBuildProcessor(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public void run() {

		try {
			if (path != null) {
				repositoryService.setRepositoryPath(repositoryName, path);
			}

			repositoryService.getRepositoryBuilderDAO().addObserverText(
					observerText);
			repositoryService.getRepositoryBuilderDAO().addObserverCount(
					observerCountProgress);

			repositoryService.setBuilding(repositoryName, true);
			repositoryService.buildRepository(repositoryName);
			repositoryService.write(repositoryName);
			observerEnd.end();
		} catch (Exception e) {
			List<Exception> errors = new ArrayList<Exception>();
			observerError.error(errors);
		} finally {
			repositoryService.setBuilding(repositoryName, false);
		}
	}

	public void cancel() {
		repositoryService.cancel();
	}

	public void addObserverText(ObserverText obs) {
		this.observerText = obs;
	}

	public void addObserverCountProgress(ObserverCountInt obs) {
		this.observerCountProgress = obs;
	}

	public void addObserverEnd(ObserverEnd obs) {
		this.observerEnd = obs;
	}

	public void addObserverError(ObserverError obs) {
		this.observerError = obs;
	}
}
