package fr.soe.a3s.ui.repository.workers;

import java.util.List;

import fr.soe.a3s.controller.ObserverConnectionLost;
import fr.soe.a3s.controller.ObserverEnd;
import fr.soe.a3s.controller.ObserverError;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repository.DownloadPanel;

public class AddonsAutoUpdater extends Thread {

	private Facade facade;
	private final String repositoryName;
	private DownloadPanel downloadPanel;
	/* Workers */
	private AddonsChecker addonsChecker;
	private AddonsDownloader addonsDownloader;
	/* Tests */
	private boolean check1IsDone, check2IsDone;
	/* observers */
	private ObserverEnd observerEnd;
	private ObserverError observerError;
	private ObserverConnectionLost observerConnectionLost;

	public AddonsAutoUpdater(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public AddonsAutoUpdater(String repositoryName,
			AddonsChecker addonsChecker, AddonsDownloader addonsDownloader,
			DownloadPanel downloadPanel) {
		this.repositoryName = repositoryName;
		this.addonsChecker = addonsChecker;
		this.addonsDownloader = addonsDownloader;
		this.downloadPanel = downloadPanel;
	}

	@Override
	public void run() {

		System.out.println("Auto updating with repository: " + repositoryName);

		check1IsDone = false;
		check2IsDone = false;

		addonsChecker.addObserverEnd(new ObserverEnd() {
			@Override
			public void end() {
				addonsCheckerEnd();
			}
		});
		addonsDownloader.addObserverEnd(new ObserverEnd() {
			@Override
			public void end() {
				addonsDownloaderEnd();
			}
		});
		addonsChecker.addObserverError(new ObserverError() {
			@Override
			public void error(List<Exception> errors) {
				observerError.error(errors);
			}
		});
		addonsDownloader.addObserverError(new ObserverError() {
			@Override
			public void error(List<Exception> errors) {
				observerError.error(errors);
			}
		});
		addonsDownloader.addObserverConnectionLost(new ObserverConnectionLost() {
			@Override
			public void lost() {
				observerConnectionLost.lost();
			}
		});

		addonsChecker.run();
	}

	private void addonsCheckerEnd() {

		if (!check1IsDone) {
			check1IsDone = true;
		} else if (!check2IsDone) {
			check2IsDone = true;
		}

		if (check1IsDone && check2IsDone) {
			System.out.println("Synchronization with repository: "
					+ repositoryName + " finished.");
			observerEnd.end();
		} else if (check1IsDone && !check2IsDone) {
			downloadPanel.updateArbre(addonsChecker.getParent());
			downloadPanel.getCheckBoxSelectAll().setSelected(true);
			downloadPanel.checkBoxSelectAllPerformed();
			addonsDownloader.run();
		}
	}

	private void addonsDownloaderEnd() {
		addonsChecker.run();
	}

	public void addObserverEnd(ObserverEnd obs) {
		this.observerEnd = obs;
	}

	public void addObserverError(ObserverError obs) {
		this.observerError = obs;
	}
	
	public void addObserverConnectionLost(ObserverConnectionLost obs){
		this.observerConnectionLost = obs;
	}
}
