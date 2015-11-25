package fr.soe.a3s.ui.repositoryEditor.workers;

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import fr.soe.a3s.constant.RepositoryStatus;
import fr.soe.a3s.controller.ObserverCountWithText;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repositoryEditor.AdminPanel;
import fr.soe.a3s.ui.repositoryEditor.errorDialogs.UnexpectedErrorDialog;

public class RepositoryBuilder extends Thread {

	private final Facade facade;
	private final AdminPanel adminPanel;
	/* Data */
	private final String path;
	private final String repositoryName;
	/* Tests */
	private boolean canceled = false;
	/* Services */
	private final RepositoryService repositoryService = new RepositoryService();

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

		// Set building state
		repositoryService.setBuilding(repositoryName, true);

		// Reset upload repository state
		repositoryService.saveTransfertParameters(repositoryName, 0, 0, false);

		repositoryService.getRepositoryBuilderDAO().addObserverCountWithText(
				new ObserverCountWithText() {
					@Override
					public synchronized void update(final int value) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								adminPanel.getBuildProgressBar()
										.setIndeterminate(false);
								adminPanel.getBuildProgressBar()
										.setValue(value);
							}
						});
					}

					@Override
					public synchronized void update(final String text) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								adminPanel.getBuildProgressBar()
										.setIndeterminate(false);
								adminPanel.getBuildProgressBar()
										.setString(text);
							}
						});
					}
				});

		try {
			// Build repository
			repositoryService.buildRepository(repositoryName, path);

			adminPanel.getBuildProgressBar().setIndeterminate(false);

			if (!canceled) {
				adminPanel.getBuildProgressBar().setValue(100);
				adminPanel.getBuildProgressBar().setString("100%");

				JOptionPane.showMessageDialog(facade.getMainPanel(),
						"Repository build finished.", "Build repository",
						JOptionPane.INFORMATION_MESSAGE);

				// Init views
				this.adminPanel.init(repositoryName);
				this.facade.getSyncPanel().init();
				this.adminPanel
						.updateRepositoryStatus(RepositoryStatus.UPDATED);
				this.adminPanel.getRepositoryPanel().getEventsPanel()
						.init(repositoryName);// update addons list
			}

			// Write repository
			repositoryService.write(repositoryName);

		} catch (Exception e) {
			adminPanel.getBuildProgressBar().setIndeterminate(false);
			if (!canceled) {
				e.printStackTrace();
				if (e instanceof RepositoryException) {
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							e.getMessage(), "Build repository",
							JOptionPane.ERROR_MESSAGE);
				} else if (e instanceof IOException
						| e instanceof WritingException) {
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							e.getMessage(), "Build repository",
							JOptionPane.ERROR_MESSAGE);
				} else {
					UnexpectedErrorDialog dialog = new UnexpectedErrorDialog(
							facade, "Build repository", e, repositoryName);
					dialog.show();
				}
			}
		} finally {
			repositoryService.cancel();
			initAdminPanelForEndBuild();
			terminate();
		}
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

	private void terminate() {

		repositoryService.setBuilding(repositoryName, false);
		this.interrupt();
		System.gc();
	}

	public void cancel() {

		this.canceled = true;
		adminPanel.getBuildProgressBar().setString("Canceling...");
		repositoryService.cancel();
		initAdminPanelForEndBuild();
		terminate();
	}
}
