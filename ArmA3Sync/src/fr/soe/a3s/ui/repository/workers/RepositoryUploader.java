package fr.soe.a3s.ui.repository.workers;

import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import fr.soe.a3s.controller.ObserverConnectionLost;
import fr.soe.a3s.controller.ObserverCountInt;
import fr.soe.a3s.controller.ObserverCountLong;
import fr.soe.a3s.controller.ObserverEnd;
import fr.soe.a3s.controller.ObserverError;
import fr.soe.a3s.controller.ObserverText;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.administration.RepositoryUploadProcessor;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repository.AdminPanel;
import fr.soe.a3s.ui.repository.dialogs.ConnectionLostDialog;
import fr.soe.a3s.ui.repository.dialogs.error.UnexpectedErrorDialog;
import fr.soe.a3s.utils.ErrorPrinter;
import fr.soe.a3s.utils.UnitConverter;

public class RepositoryUploader extends Thread implements DataAccessConstants {

	private final Facade facade;
	private final AdminPanel adminPanel;
	/* Data */
	private final String repositoryName;
	private final String repositoryPath;
	/* Tests */
	private boolean canceled;
	/* Service */
	private RepositoryUploadProcessor repositoryUploadProcessor;
	/* observers */
	private ObserverEnd observerEnd;
	private ObserverError observerError;
	private ObserverConnectionLost observerConnectionLost;

	public RepositoryUploader(Facade facade, String repositoryName,
			String repositoryPath, AdminPanel adminPanel) {
		this.facade = facade;
		this.repositoryName = repositoryName;
		this.repositoryPath = repositoryPath;
		this.adminPanel = adminPanel;
	}

	@Override
	public void run() {

		System.out.println("Starting uploading repository: " + repositoryName);

		// Init AdminPanel for start uploading
		intiAdminPanelForStartUpload();
		canceled = false;

		adminPanel.getUploadrogressBar().setIndeterminate(true);

		repositoryUploadProcessor = new RepositoryUploadProcessor(
				repositoryName);
		repositoryUploadProcessor.addObserverText(new ObserverText() {
			@Override
			public void update(String text) {
				executeUpdateText(text);
			}
		});
		repositoryUploadProcessor
				.addObserverCountProgress(new ObserverCountInt() {
					@Override
					public void update(int value) {
						executeUpdateProgress(value);
					}
				});
		repositoryUploadProcessor
				.addObserverUploadedSize(new ObserverCountLong() {
					@Override
					public void update(long value) {
						executeUpdateUploadedSize(value);
					}
				});
		repositoryUploadProcessor.addObserverTotalSize(new ObserverCountLong() {
			@Override
			public void update(long value) {
				executeUpdateTotalSize(value);
			}
		});
		repositoryUploadProcessor
				.addObserverRemainingTime(new ObserverCountLong() {
					@Override
					public void update(long value) {
						executeUpdateRemainingTime(value);
					}
				});
		repositoryUploadProcessor.addObserverSpeed(new ObserverCountLong() {
			@Override
			public void update(long value) {
				executeUpdateSpeed(value);
			}
		});
		repositoryUploadProcessor.addObserverEnd(new ObserverEnd() {
			@Override
			public void end() {
				executeEnd();
			}
		});
		repositoryUploadProcessor.addObserverError(new ObserverError() {
			@Override
			public void error(List<Exception> errors) {
				executeError(errors);
			}
		});
		repositoryUploadProcessor
				.addObserverConnectionLost(new ObserverConnectionLost() {
					@Override
					public void lost() {
						executeConnectionLost();
					}
				});

		repositoryUploadProcessor.run();
	}

	private void intiAdminPanelForStartUpload() {

		adminPanel.getButtonSelectRepositoryfolderPath().setEnabled(false);
		adminPanel.getButtonBuild().setEnabled(false);
		adminPanel.getButtonCopyAutoConfigURL().setEnabled(false);
		adminPanel.getButtonCheck().setEnabled(false);
		adminPanel.getButtonBuildOptions().setEnabled(false);
		adminPanel.getButtonUploadOptions().setEnabled(false);
		adminPanel.getButtonView().setEnabled(false);
		adminPanel.getRepositoryPanel().getDownloadPanel()
				.getButtonCheckForAddonsStart().setEnabled(false);
		adminPanel.getRepositoryPanel().getDownloadPanel()
				.getButtonDownloadStart().setEnabled(false);
		adminPanel.getUploadInformationBox().setVisible(true);
		adminPanel.getUploadTotalSizeLabelValue().setText("");
		adminPanel.getUploadedLabelValue().setText("");
		adminPanel.getUploadRemainingTimeValue().setText("");
		adminPanel.getUploadSpeedLabelValue().setText("");
		adminPanel.getButtonUpload().setText("Stop");
		adminPanel.getUploadrogressBar().setString("");
		adminPanel.getUploadrogressBar().setStringPainted(true);
		adminPanel.getUploadrogressBar().setMaximum(100);
		adminPanel.getUploadrogressBar().setMinimum(0);
	}

	private void initAdminPanelForEndUpload() {

		adminPanel.getButtonSelectRepositoryfolderPath().setEnabled(true);
		adminPanel.getButtonBuild().setEnabled(true);
		adminPanel.getButtonCopyAutoConfigURL().setEnabled(true);
		adminPanel.getButtonCheck().setEnabled(true);
		adminPanel.getButtonBuildOptions().setEnabled(true);
		adminPanel.getButtonUploadOptions().setEnabled(true);
		adminPanel.getButtonView().setEnabled(true);
		adminPanel.getRepositoryPanel().getDownloadPanel()
				.getButtonCheckForAddonsStart().setEnabled(true);
		adminPanel.getRepositoryPanel().getDownloadPanel()
				.getButtonDownloadStart().setEnabled(true);
		adminPanel.getUploadInformationBox().setVisible(false);
		adminPanel.getUploadTotalSizeLabelValue().setText("");
		adminPanel.getUploadedLabelValue().setText("");
		adminPanel.getUploadRemainingTimeValue().setText("");
		adminPanel.getUploadSpeedLabelValue().setText("");
		adminPanel.getButtonUpload().setText("Upload");
		adminPanel.getUploadrogressBar().setString("");
		adminPanel.getUploadrogressBar().setStringPainted(false);
		adminPanel.getUploadrogressBar().setMaximum(0);
		adminPanel.getUploadrogressBar().setMinimum(0);
	}

	private void executeUpdateText(String text) {
		adminPanel.getUploadrogressBar().setString(text);
	}

	private void executeUpdateProgress(final int value) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				adminPanel.getUploadrogressBar().setIndeterminate(false);
				adminPanel.getUploadrogressBar().setValue(value);
			}
		});
	}

	private void executeUpdateUploadedSize(long value) {
		if (!canceled) {
			adminPanel.getUploadedLabelValue().setText(
					UnitConverter.convertSize(value));
		}
	}

	private void executeUpdateTotalSize(long value) {
		if (!canceled) {
			adminPanel.getUploadTotalSizeLabelValue().setText(
					UnitConverter.convertSize(value));
		}
	}

	private void executeUpdateRemainingTime(long value) {
		if (!canceled) {
			adminPanel.getUploadRemainingTimeValue().setText(
					UnitConverter.convertTime(value));
		}
	}

	private void executeUpdateSpeed(long value) {
		if (!canceled) {
			adminPanel.getUploadSpeedLabelValue().setText(
					UnitConverter.convertSpeed(value));
		}
	}

	private void executeEnd() {

		adminPanel.getUploadrogressBar().setIndeterminate(false);

		if (!canceled) {

			canceled = true;

			System.out.println("Repository: " + repositoryName
					+ " - repository upload finished.");

			adminPanel.getUploadrogressBar().setString("100%");
			adminPanel.getUploadrogressBar().setValue(100);

			initAdminPanelForEndUpload();
			terminate();

			// Admin panel
			observerEnd.end();
		}
	}

	private void executeError(List<Exception> errors) {

		adminPanel.getUploadrogressBar().setIndeterminate(false);

		if (!canceled) {

			canceled = true;

			System.out.println("Repository: " + repositoryName
					+ " - repository upload finished with error.");

			adminPanel.getUploadrogressBar().setString("Error!");

			initAdminPanelForEndUpload();
			terminate();

			// Admin panel
			observerError.error(errors);
		}
	}

	private void executeConnectionLost() {

		adminPanel.getUploadrogressBar().setIndeterminate(false);

		if (!canceled) {

			canceled = true;
			
			System.out.println("Repository: " + repositoryName
					+ " - connection lost.");
			
			adminPanel.getUploadrogressBar().setString("Error!");

			initAdminPanelForEndUpload();
			terminate();

			// Admin panel
			observerConnectionLost.lost();
		}
	}

	private void terminate() {

		repositoryUploadProcessor.cancel();
		System.gc();
	}

	public void cancel() {

		this.canceled = true;
		initAdminPanelForEndUpload();
		terminate();
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
