package fr.soe.a3s.ui.repository.workers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import fr.soe.a3s.controller.ObserverCountInt;
import fr.soe.a3s.controller.ObserverEnd;
import fr.soe.a3s.controller.ObserverError;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.service.synchronization.FilesCheckProcessor;
import fr.soe.a3s.service.synchronization.FilesCompletionProcessor;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repository.DownloadPanel;

public class AddonsChecker extends Thread {

	private final Facade facade;
	private final DownloadPanel downloadPanel;
	/* Data */
	private final String repositoryName;
	private final boolean withEvents;
	private SyncTreeDirectoryDTO parent;
	private String serverRangeRequestResponseHeader = null;
	private boolean saveStateCheckBoxExactMath, saveStateCheckBoxAutoDiscover;
	/* Test */
	private boolean canceled;
	/* Services */
	private FilesCheckProcessor filesCheckProcessor;
	private FilesCompletionProcessor filesCompletionProcessor;
	/* observers */
	private ObserverEnd observerEnd;
	private ObserverError observerError;

	public AddonsChecker(Facade facade, String repositoryName,
			boolean withEvents, DownloadPanel downloadPanel) {
		this.facade = facade;
		this.repositoryName = repositoryName;
		this.withEvents = withEvents;
		this.downloadPanel = downloadPanel;
	}

	@Override
	public void run() {

		System.out.println("Checking for Addons on repository: "
				+ repositoryName);

		// Initialize
		initDownloadPanelForStartCheck();
		canceled = false;

		filesCheckProcessor = new FilesCheckProcessor(repositoryName,
				withEvents);
		filesCheckProcessor.addObserverCount(new ObserverCountInt() {
			@Override
			public void update(int value) {
				executeUpdate(value);
			}
		});
		filesCheckProcessor.addObserverError(new ObserverError() {
			@Override
			public void error(List<Exception> errors) {
				executeError(errors.get(0));
			}
		});

		filesCompletionProcessor = new FilesCompletionProcessor(repositoryName);
		filesCompletionProcessor.addObserverCount(new ObserverCountInt() {
			@Override
			public void update(int value) {
				executeUpdate(value);
			}
		});
		filesCompletionProcessor.addObserverEnd(new ObserverEnd() {
			@Override
			public void end() {
				executeEnd();
			}
		});
		filesCompletionProcessor.addObserverError(new ObserverError() {
			@Override
			public void error(List<Exception> errors) {
				executeError(errors.get(0));
			}
		});

		downloadPanel.getProgressBarCheckForAddons().setIndeterminate(true);
		downloadPanel.getProgressBarCheckForAddons().setMinimum(0);
		downloadPanel.getProgressBarCheckForAddons().setMaximum(100);

		this.parent = filesCheckProcessor.run();// blocking
												// execution
		if (!canceled) {
			if (parent == null) {// true if default download location is null
				executeEnd();
			} else {
				System.out
						.println("Determining file completion on repository: "
								+ repositoryName);

				downloadPanel.getProgressBarCheckForAddons().setIndeterminate(
						true);
				downloadPanel.getProgressBarCheckForAddons().setMinimum(0);
				downloadPanel.getProgressBarCheckForAddons().setMaximum(100);

				this.serverRangeRequestResponseHeader = filesCompletionProcessor
						.run(parent); // non blocking execution
			}
		}
	}

	private void initDownloadPanelForStartCheck() {

		downloadPanel.getArbre().setEnabled(false);
		downloadPanel.getLabelCheckForAddonsStatus().setText(
				"Checking files...");
		downloadPanel.getLabelCheckForAddonsStatus().setForeground(
				DownloadPanel.GREEN);
		downloadPanel.getCheckBoxSelectAll().setEnabled(false);
		downloadPanel.getCheckBoxExpandAll().setEnabled(false);
		saveStateCheckBoxExactMath = downloadPanel.getCheckBoxExactMatch()
				.isEnabled();
		saveStateCheckBoxAutoDiscover = downloadPanel.getCheckBoxAutoDiscover()
				.isEnabled();
		downloadPanel.getCheckBoxExactMatch().setEnabled(false);
		downloadPanel.getCheckBoxAutoDiscover().setEnabled(false);
		downloadPanel.getComBoxDestinationFolder().setEnabled(false);
		downloadPanel.getButtonCheckForAddonsStart().setEnabled(false);
		downloadPanel.getButtonCheckForAddonsCancel().setEnabled(true);
		downloadPanel.getButtonSettings().setEnabled(false);
		downloadPanel.getButtonDownloadStart().setEnabled(false);
		downloadPanel.getButtonDownloadPause().setEnabled(false);
		downloadPanel.getButtonDownloadCancel().setEnabled(false);
		downloadPanel.getButtonDownloadReport().setEnabled(false);
		downloadPanel.getProgressBarCheckForAddons().setMinimum(0);
		downloadPanel.getProgressBarCheckForAddons().setMaximum(100);
	}

	private void initDownlaodPanelForEndCheck() {

		downloadPanel.getProgressBarCheckForAddons().setIndeterminate(false);
		downloadPanel.getArbre().setEnabled(true);
		downloadPanel.getCheckBoxSelectAll().setEnabled(true);
		downloadPanel.getCheckBoxExpandAll().setEnabled(true);
		downloadPanel.getCheckBoxExactMatch().setEnabled(
				saveStateCheckBoxExactMath);
		downloadPanel.getCheckBoxAutoDiscover().setEnabled(
				saveStateCheckBoxAutoDiscover);
		downloadPanel.getComBoxDestinationFolder().setEnabled(true);
		downloadPanel.getButtonCheckForAddonsStart().setEnabled(true);
		downloadPanel.getButtonCheckForAddonsCancel().setEnabled(true);
		downloadPanel.getButtonSettings().setEnabled(true);
		downloadPanel.getButtonDownloadStart().setEnabled(true);
		downloadPanel.getButtonDownloadPause().setEnabled(true);
		downloadPanel.getButtonDownloadCancel().setEnabled(true);
		downloadPanel.getButtonDownloadReport().setEnabled(true);
		downloadPanel.getProgressBarCheckForAddons().setMaximum(0);
		downloadPanel.getArbre().setEnabled(true);
	}

	private void executeUpdate(final int value) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				downloadPanel.getProgressBarCheckForAddons().setIndeterminate(
						false);
				downloadPanel.getProgressBarCheckForAddons().setValue(value);
			}
		});
	}

	private void executeEnd() {

		downloadPanel.getProgressBarCheckForAddons().setIndeterminate(false);

		if (!canceled) {

			this.canceled = true;

			System.out.println("Checking for Addons on repository: "
					+ repositoryName + " - finished.");

			// Set notification
			downloadPanel.getLabelCheckForAddonsStatus().setText("Finished!");
			downloadPanel.getLabelCheckForAddonsStatus().setForeground(
					DownloadPanel.GREEN);

			initDownlaodPanelForEndCheck();

			terminate();

			// Download panel
			observerEnd.end();
		}
	}

	private void executeError(Exception e) {

		downloadPanel.getProgressBarCheckForAddons().setIndeterminate(false);

		if (!canceled) {

			this.canceled = true;

			System.out.println("Checking for Addons on repository: "
					+ repositoryName + " - finished with errors.");

			// Set notification
			downloadPanel.getLabelCheckForAddonsStatus().setText("Error!");
			downloadPanel.getLabelCheckForAddonsStatus().setForeground(
					Color.RED);

			initDownlaodPanelForEndCheck();

			terminate();

			// Download panel
			List<Exception> errors = new ArrayList<Exception>();
			errors.add(e);
			observerError.error(errors);
		}
	}

	private void terminate() {

		filesCheckProcessor.cancel();
		filesCompletionProcessor.cancel();
	}

	public void cancel() {

		System.out.println("Canceling Checking for Addons on repository: "
				+ repositoryName);

		this.canceled = true;

		downloadPanel.getLabelCheckForAddonsStatus().setText("Canceled!");
		downloadPanel.getLabelCheckForAddonsStatus().setForeground(
				DownloadPanel.GREEN);

		initDownlaodPanelForEndCheck();

		terminate();
	}

	public String getServerRangeRequestResponseHeader() {
		return serverRangeRequestResponseHeader;
	}

	public void addObserverEnd(ObserverEnd obs) {
		this.observerEnd = obs;
	}

	public void addObserverError(ObserverError obs) {
		this.observerError = obs;
	}

	public SyncTreeDirectoryDTO getParent() {
		return this.parent;
	}
}
