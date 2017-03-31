package fr.soe.a3s.ui.repository.workers;

import java.awt.Color;
import java.util.List;

import javax.swing.SwingUtilities;

import fr.soe.a3s.controller.ObserverConnectionLost;
import fr.soe.a3s.controller.ObserverCountInt;
import fr.soe.a3s.controller.ObserverCountLong;
import fr.soe.a3s.controller.ObserverEnd;
import fr.soe.a3s.controller.ObserverError;
import fr.soe.a3s.controller.ObserverProceed;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.service.synchronization.FilesSynchronizationManager;
import fr.soe.a3s.service.synchronization.FilesSynchronizationProcessor;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repository.DownloadPanel;
import fr.soe.a3s.utils.UnitConverter;

public class AddonsDownloader extends Thread implements DataAccessConstants {

	private final Facade facade;
	private final DownloadPanel downloadPanel;
	/* Data */
	private final String repositoryName;
	private boolean saveStateCheckBoxExactMath, saveStateCheckBoxAutoDiscover;
	/* Tests */
	private boolean canceled;
	/* Services */
	private final FilesSynchronizationManager filesManager;
	private FilesSynchronizationProcessor filesSynchronizationProcessor;
	/* observers */
	private ObserverEnd observerEnd;
	private ObserverError observerError;
	private ObserverConnectionLost observerConnectionLost;

	public AddonsDownloader(Facade facade, String repositoryName,
			FilesSynchronizationManager filesManager,
			DownloadPanel downloadPanel) {
		this.facade = facade;
		this.filesManager = filesManager;
		this.repositoryName = repositoryName;
		this.downloadPanel = downloadPanel;
	}

	@Override
	public void run() {

		System.out.println("Synchronizing from repository: " + repositoryName);

		// Initialize
		initDownloadPanelForStartDownload();
		canceled = false;

		filesSynchronizationProcessor = new FilesSynchronizationProcessor(
				repositoryName, filesManager);
		filesSynchronizationProcessor
				.addObserverCountSingleProgress(new ObserverCountInt() {
					@Override
					public void update(int value) {
						executeUpdateSingleProgress(value);
					}
				});
		filesSynchronizationProcessor
				.addObserverCountTotalProgress(new ObserverCountInt() {
					@Override
					public void update(int value) {
						executeUpdateTotalProgress(value);
					}
				});
		filesSynchronizationProcessor
				.addObserverTotalSize(new ObserverCountLong() {
					@Override
					public void update(long value) {
						executeUpdateTotalSize(value);
					}
				});
		filesSynchronizationProcessor
				.addObserverDownloadedSize(new ObserverCountLong() {
					@Override
					public void update(long value) {
						executeUpdateDownloadedSize(value);
					}
				});
		filesSynchronizationProcessor.addObserverSpeed(new ObserverCountLong() {
			@Override
			public void update(long value) {
				executeUpdateSpeed(value);
			}
		});
		filesSynchronizationProcessor
				.addObserverActiveConnections(new ObserverCountInt() {
					@Override
					public void update(int value) {
						executeUpdateActiveConnections(value);
					}
				});
		filesSynchronizationProcessor
				.addObserverRemainingTime(new ObserverCountLong() {
					@Override
					public void update(long value) {
						executeUpdateRemainingTime(value);
					}
				});
		filesSynchronizationProcessor.addObserverEnd(new ObserverEnd() {
			@Override
			public void end() {
				executeEnd();
			}
		});
		filesSynchronizationProcessor.addObserverError(new ObserverError() {
			@Override
			public void error(List<Exception> errors) {
				executeError(errors);
			}
		});
		filesSynchronizationProcessor
				.addObserverConnectionLost(new ObserverConnectionLost() {
					@Override
					public void lost() {
						executeConnectionLost();
					}
				});
		filesSynchronizationProcessor
				.addObserverProceedUncompress(new ObserverProceed() {
					@Override
					public void proceed() {
						executeProceedUncompress();
					}
				});
		filesSynchronizationProcessor
				.addObserverProceedDelete(new ObserverProceed() {
					@Override
					public void proceed() {
						executeProceedDelete();
					}
				});

		filesSynchronizationProcessor.run();
	}

	private void initDownloadPanelForStartDownload() {

		downloadPanel.getArbre().setEnabled(false);
		downloadPanel.getLabelDownloadStatus().setText("Downloading...");
		downloadPanel.getLabelDownloadStatus().setForeground(
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
		downloadPanel.getButtonSettings().setEnabled(false);
		downloadPanel.getLabelTotalFilesSizeValue().setText("");
		downloadPanel.getLabelDownloadedValue().setText("");
		downloadPanel.getButtonCheckForAddonsStart().setEnabled(false);
		downloadPanel.getButtonCheckForAddonsCancel().setEnabled(false);
		downloadPanel.getButtonDownloadStart().setEnabled(false);
		downloadPanel.getButtonDownloadPause().setEnabled(true);
		downloadPanel.getButtonDownloadCancel().setEnabled(true);
		downloadPanel.getButtonDownloadReport().setEnabled(true);
		downloadPanel.getProgressBarDownloadAddons().setMinimum(0);
		downloadPanel.getProgressBarDownloadAddons().setMaximum(100);
		downloadPanel.getProgressBarDownloadSingleAddon().setMinimum(0);
		downloadPanel.getProgressBarDownloadSingleAddon().setMaximum(100);
		downloadPanel.getProgressBarDownloadSingleAddon()
				.setIndeterminate(true);
	}

	private void initDownloadPanelForStartUncompressing() {

		downloadPanel.getLabelDownloadStatus().setText("Uncompressing...");
		downloadPanel.getLabelDownloadStatus().setForeground(
				DownloadPanel.GREEN);
		downloadPanel.getLabelSpeedValue().setText("");
		downloadPanel.getLabelRemainingTimeValue().setText("");
		downloadPanel.getLabelActiveConnectionsValue().setText("");
		downloadPanel.getProgressBarDownloadAddons().setMinimum(0);
		downloadPanel.getProgressBarDownloadAddons().setMaximum(100);
		downloadPanel.getProgressBarDownloadSingleAddon()
				.setIndeterminate(true);
	}

	private void initDownloadPanelForStartDeleting() {

		downloadPanel.getLabelDownloadStatus().setText("Deleting...");
		downloadPanel.getLabelDownloadStatus().setForeground(
				DownloadPanel.GREEN);
		downloadPanel.getLabelSpeedValue().setText("");
		downloadPanel.getLabelRemainingTimeValue().setText("");
		downloadPanel.getLabelActiveConnectionsValue().setText("");
		downloadPanel.getProgressBarDownloadAddons().setMinimum(0);
		downloadPanel.getProgressBarDownloadAddons().setMaximum(100);
		downloadPanel.getProgressBarDownloadSingleAddon()
				.setIndeterminate(true);
	}

	private void initDownloadPanelForEndDownload() {

		downloadPanel.getProgressBarDownloadSingleAddon().setIndeterminate(
				false);
		downloadPanel.getArbre().setEnabled(true);
		downloadPanel.getCheckBoxSelectAll().setEnabled(true);
		downloadPanel.getCheckBoxExpandAll().setEnabled(true);
		downloadPanel.getCheckBoxExactMatch().setEnabled(
				saveStateCheckBoxExactMath);
		downloadPanel.getCheckBoxAutoDiscover().setEnabled(
				saveStateCheckBoxAutoDiscover);
		downloadPanel.getComBoxDestinationFolder().setEnabled(true);
		downloadPanel.getButtonSettings().setEnabled(true);
		downloadPanel.getLabelTotalFilesSizeValue().setText("");
		downloadPanel.getLabelDownloadedValue().setText("");
		downloadPanel.getButtonCheckForAddonsStart().setEnabled(true);
		downloadPanel.getButtonCheckForAddonsCancel().setEnabled(true);
		downloadPanel.getButtonDownloadStart().setEnabled(true);
		downloadPanel.getButtonDownloadPause().setEnabled(true);
		downloadPanel.getButtonDownloadCancel().setEnabled(true);
		downloadPanel.getButtonDownloadReport().setEnabled(true);
		downloadPanel.getProgressBarCheckForAddons().setMaximum(0);
		downloadPanel.getProgressBarDownloadAddons().setMaximum(0);
		downloadPanel.getProgressBarDownloadSingleAddon().setMaximum(0);
		downloadPanel.getLabelSpeedValue().setText("");
		downloadPanel.getLabelRemainingTimeValue().setText("");
		downloadPanel.getLabelActiveConnectionsValue().setText("");
	}

	private void executeUpdateSingleProgress(final int value) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				downloadPanel.getProgressBarDownloadSingleAddon()
						.setIndeterminate(false);
				downloadPanel.getProgressBarDownloadSingleAddon().setValue(
						value);
			}
		});
	}

	private void executeUpdateTotalProgress(final int value) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				downloadPanel.getProgressBarDownloadAddons().setIndeterminate(
						false);
				downloadPanel.getProgressBarDownloadAddons().setValue(value);
			}
		});
	}

	private void executeUpdateTotalSize(long value) {
		if (!canceled) {
			downloadPanel.getLabelTotalFilesSizeValue().setText(
					UnitConverter.convertSize(value));
		}
	}

	private void executeUpdateDownloadedSize(final long value) {
		if (!canceled) {
			downloadPanel.getLabelDownloadedValue().setText(
					UnitConverter.convertSize(value));
		}
	}

	private void executeUpdateSpeed(final long value) {
		if (!canceled) {
			if (value == 0) {
				downloadPanel.getLabelSpeedValue().setText("-");
			} else {
				downloadPanel.getLabelSpeedValue().setText(
						UnitConverter.convertSpeed(value));
			}
		}
	}

	private void executeUpdateWaitingForServer(final boolean waiting) {
		if (!canceled) {
			if (waiting) {
				downloadPanel.getLabelDownloadStatus().setText(
						"Waiting for server...");
				downloadPanel.getLabelDownloadStatus().setForeground(Color.RED);
			} else {
				downloadPanel.getLabelDownloadStatus()
						.setText("Downloading...");
				downloadPanel.getLabelDownloadStatus().setForeground(
						DownloadPanel.GREEN);
			}
		}
	}

	private void executeUpdateActiveConnections(int value) {
		if (!canceled) {
			downloadPanel.getLabelActiveConnectionsValue().setText(
					Integer.toString(value));
		}
	}

	private void executeUpdateRemainingTime(long value) {
		if (!canceled) {
			downloadPanel.getLabelRemainingTimeValue()
					.setText(
							UnitConverter.convertTime((long) (value * Math.pow(
									10, -9))));
		}
	}

	private void executeProceedUncompress() {
		initDownloadPanelForStartUncompressing();
	}

	private void executeProceedDelete() {
		initDownloadPanelForStartDeleting();
	}

	private void executeEnd() {

		downloadPanel.getProgressBarDownloadSingleAddon().setIndeterminate(
				false);

		if (!canceled) {

			canceled = true;

			System.out.println("Synchronization with repository: "
					+ repositoryName + " - finished.");

			// Set notification
			downloadPanel.getLabelDownloadStatus().setText("Finished!");
			downloadPanel.getLabelDownloadStatus().setForeground(
					DownloadPanel.GREEN);

			initDownloadPanelForEndDownload();
			terminate();

			// Download panel
			observerEnd.end();
		}
	}

	private void executeError(List<Exception> errors) {

		downloadPanel.getProgressBarDownloadSingleAddon().setIndeterminate(
				false);

		if (!canceled) {

			canceled = true;

			System.out.println("Synchronization with repository: "
					+ repositoryName + " - finished with errors.");

			// Set notification
			downloadPanel.getLabelDownloadStatus().setText("Error!");
			downloadPanel.getLabelDownloadStatus().setForeground(Color.RED);

			initDownloadPanelForEndDownload();
			terminate();

			// Download panel
			observerError.error(errors);
		}
	}

	private void executeConnectionLost() {

		downloadPanel.getProgressBarDownloadSingleAddon().setIndeterminate(
				false);

		if (!canceled) {

			canceled = true;

			System.out.println("Synchronization with repository: "
					+ repositoryName + " - connection lost.");

			// Set notification
			downloadPanel.getLabelDownloadStatus().setText("Error!");
			downloadPanel.getLabelDownloadStatus().setForeground(Color.RED);

			initDownloadPanelForEndDownload();
			terminate();

			// Download panel
			observerConnectionLost.lost();
		}
	}

	public void pause() {

		this.canceled = true;

		downloadPanel.getLabelDownloadStatus().setText("Paused...");
		downloadPanel.getLabelDownloadStatus().setForeground(
				DownloadPanel.GREEN);
		downloadPanel.getLabelSpeedValue().setText("");
		downloadPanel.getLabelRemainingTimeValue().setText("");
		downloadPanel.getLabelActiveConnectionsValue().setText("");
		downloadPanel.getButtonSettings().setEnabled(true);
		downloadPanel.getButtonDownloadStart().setEnabled(true);
		terminate();
	}

	public void cancel() {

		this.canceled = true;

		downloadPanel.getLabelDownloadStatus().setText("Canceled!");
		downloadPanel.getLabelDownloadStatus().setForeground(
				DownloadPanel.GREEN);
		initDownloadPanelForEndDownload();
		terminate();
	}

	private void terminate() {

		filesSynchronizationProcessor.cancel();
	}

	public void addObserverEnd(ObserverEnd obs) {
		this.observerEnd = obs;
	}

	public void addObserverError(ObserverError obs) {
		this.observerError = obs;
	}

	public void addObserverConnectionLost(ObserverConnectionLost obs) {
		this.observerConnectionLost = obs;
	}
}