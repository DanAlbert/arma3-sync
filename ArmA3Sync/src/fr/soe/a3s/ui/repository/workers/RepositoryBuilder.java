package fr.soe.a3s.ui.repository.workers;

import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import fr.soe.a3s.constant.RepositoryStatus;
import fr.soe.a3s.controller.ObserverCountInt;
import fr.soe.a3s.controller.ObserverEnd;
import fr.soe.a3s.controller.ObserverError;
import fr.soe.a3s.controller.ObserverText;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.administration.RepositoryBuildProcessor;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repository.AdminPanel;
import fr.soe.a3s.ui.repository.dialogs.error.UnexpectedErrorDialog;
import fr.soe.a3s.utils.ErrorPrinter;

public class RepositoryBuilder extends Thread {

	private final Facade facade;
	private final AdminPanel adminPanel;
	/* Data */
	private final String path;
	private final String repositoryName;
	/* Tests */
	private boolean canceled;
	/* Services */
	private RepositoryBuildProcessor filesBuildProcessor;

	public RepositoryBuilder(Facade facade, String repositoryName, String path,
			AdminPanel adminPanel) {
		this.facade = facade;
		this.repositoryName = repositoryName;
		this.path = path;
		this.adminPanel = adminPanel;
	}

	@Override
	public void run() {

		System.out.println("Starting building repository: " + repositoryName);

		// Init AdminPanel for start building
		initAdminPanelForStartBuild();
		canceled = false;

		adminPanel.getBuildProgressBar().setIndeterminate(true);

		filesBuildProcessor = new RepositoryBuildProcessor(repositoryName, path);
		filesBuildProcessor.addObserverText(new ObserverText() {
			@Override
			public void update(String text) {
				executeUpdateText(text);
			}
		});
		filesBuildProcessor.addObserverCountProgress(new ObserverCountInt() {
			@Override
			public void update(int value) {
				executeUpdateCountProgress(value);
			}
		});
		filesBuildProcessor.addObserverEnd(new ObserverEnd() {
			@Override
			public void end() {
				executeEnd();
			}
		});
		filesBuildProcessor.addObserverError(new ObserverError() {
			@Override
			public void error(List<Exception> errors) {
				executeError(errors);
			}
		});

		filesBuildProcessor.run();
	}

	private void initAdminPanelForStartBuild() {

		adminPanel.getButtonSelectRepositoryfolderPath().setEnabled(false);
		adminPanel.getButtonBuild().setText("Stop");
		adminPanel.getButtonCopyAutoConfigURL().setEnabled(false);
		adminPanel.getButtonCheck().setEnabled(false);
		adminPanel.getButtonBuildOptions().setEnabled(false);
		adminPanel.getButtonUpload().setEnabled(false);
		adminPanel.getButtonUploadOptions().setEnabled(false);
		adminPanel.getButtonView().setEnabled(false);
		adminPanel.getBuildProgressBar().setString("");
		adminPanel.getBuildProgressBar().setStringPainted(true);
		adminPanel.getBuildProgressBar().setMinimum(0);
		adminPanel.getBuildProgressBar().setMaximum(100);
	}

	private void initAdminPanelForEndBuild() {

		adminPanel.getButtonSelectRepositoryfolderPath().setEnabled(true);
		adminPanel.getButtonBuild().setText("Build");
		adminPanel.getButtonCopyAutoConfigURL().setEnabled(true);
		adminPanel.getButtonCheck().setEnabled(true);
		adminPanel.getButtonBuildOptions().setEnabled(true);
		adminPanel.getButtonUpload().setEnabled(true);
		adminPanel.getButtonUploadOptions().setEnabled(true);
		adminPanel.getButtonView().setEnabled(true);
		adminPanel.getBuildProgressBar().setString("");
		adminPanel.getBuildProgressBar().setStringPainted(false);
		adminPanel.getBuildProgressBar().setMinimum(0);
		adminPanel.getBuildProgressBar().setMaximum(0);
	}

	private void executeUpdateText(String text) {

		adminPanel.getBuildProgressBar().setIndeterminate(false);
		adminPanel.getBuildProgressBar().setString(text);
	}

	private void executeUpdateCountProgress(final int value) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				adminPanel.getBuildProgressBar().setIndeterminate(false);
				adminPanel.getBuildProgressBar().setValue(value);
			}
		});
	}

	private void executeEnd() {

		adminPanel.getBuildProgressBar().setIndeterminate(false);

		if (!canceled) {

			canceled = true;

			System.out.println("Repository " + repositoryName
					+ " - build finished.");

			adminPanel.getBuildProgressBar().setValue(100);
			adminPanel.getBuildProgressBar().setString("100%");

			String message = "Repository " + repositoryName + "\n"
					+ "Build finished.";
			JOptionPane.showMessageDialog(facade.getMainPanel(), message,
					"Build repository", JOptionPane.INFORMATION_MESSAGE);

			// Init views
			this.adminPanel.init(repositoryName);
			this.adminPanel.updateRepositoryStatus(RepositoryStatus.UPDATED);
			this.adminPanel.getRepositoryPanel().getEventsPanel()
					.init(repositoryName);// update addons list
			
			initAdminPanelForEndBuild();
			terminate();
		}
	}

	private void executeError(List<Exception> errors) {

		adminPanel.getBuildProgressBar().setIndeterminate(false);

		if (!canceled) {

			canceled = true;
			this.adminPanel.updateRepositoryStatus(RepositoryStatus.ERROR);

			System.out.println("Repository " + repositoryName
					+ " - build finished with error.");
			
			this.adminPanel.getCheckProgressBar().setString("Error!");

			Exception ex = errors.get(0);
			if (ex instanceof RepositoryException | ex instanceof IOException
					| ex instanceof WritingException) {
				String message = ErrorPrinter.printRepositoryManagedError(
						repositoryName, ex);
				JOptionPane.showMessageDialog(facade.getMainPanel(), message,
						"Build repository", JOptionPane.ERROR_MESSAGE);
			} else {
				ErrorPrinter.printRepositoryUnexpectedError(repositoryName, ex);
				UnexpectedErrorDialog dialog = new UnexpectedErrorDialog(
						facade, "Build repository", ex, repositoryName);
				dialog.show();
			}
			
			initAdminPanelForEndBuild();
			terminate();
		}
	}

	private void terminate() {

		filesBuildProcessor.cancel();
		System.gc();
	}

	public void cancel() {

		this.canceled = true;
		initAdminPanelForEndBuild();
		terminate();
	}
}
