package fr.soe.a3s.console;

import java.io.File;

import fr.soe.a3s.controller.ObserverEnd;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.service.RepositoryService;

public class CommandLine extends CommandGeneral {

	public void build(String repositoryName) {

		RepositoryService repositoryService = new RepositoryService();

		/* Load Repositories */

		try {
			repositoryService.readAll();
		} catch (LoadingException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}

		/* Proceed with command */

		ObserverEnd observerEndBuild = new ObserverEnd() {
			@Override
			public void end() {
				System.exit(0);
			}
		};

		super.build(repositoryName, observerEndBuild);
	}

	public void check(String repositoryName) {

		RepositoryService repositoryService = new RepositoryService();

		/* Load Repositories */

		try {
			repositoryService.readAll();
		} catch (LoadingException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}

		/* Proceed with command */

		ObserverEnd observerEndCheck = new ObserverEnd() {
			@Override
			public void end() {
				System.exit(0);
			}
		};

		super.check(repositoryName, observerEndCheck);
	}

	public void sync(final String repositoryName, String destinationFolderPath,
			String withExactMath) {

		assert (repositoryName != null);
		assert (destinationFolderPath != null);

		RepositoryService repositoryService = new RepositoryService();

		/* Load Repositories */

		try {
			repositoryService.readAll();
		} catch (LoadingException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}

		// Set parameters
		if (!new File(destinationFolderPath).exists()
				|| !new File(destinationFolderPath).isDirectory()) {
			String message = "Error: destination folder path "
					+ destinationFolderPath + " does not exist!";
			System.out.println(message);
			System.exit(0);
		}
		if (!(withExactMath.equalsIgnoreCase("true") || withExactMath
				.equalsIgnoreCase("false"))) {
			String message = "Unrecognized exact math parameter (true/false).";
			System.out.println(message);
			System.exit(0);
		}

		/* Proceed with command */

		repositoryService.setDefaultDownloadLocation(repositoryName,
				destinationFolderPath);
		repositoryService.setExactMatch(Boolean.parseBoolean(withExactMath),
				repositoryName);
		repositoryService.setConnectionTimeout(repositoryName, "0");
		repositoryService.setReadTimeout(repositoryName, "0");

		ObserverEnd observerEnd = new ObserverEnd() {
			@Override
			public void end() {
				System.exit(0);
			}
		};

		super.sync(repositoryName, observerEnd);
	}

	@Override
	public void extractBikeys(String sourceDirectoryPath,
			String targetDirectoryPath) {

		super.extractBikeys(sourceDirectoryPath, targetDirectoryPath);

		System.exit(0);
	}

	public void checkForUpdates() {

		super.checkForUpdates(false);

		System.exit(0);
	}
}
