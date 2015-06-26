package fr.soe.a3s.ui.repositoryEditor.workers;

import javax.swing.JOptionPane;

import fr.soe.a3s.constant.RepositoryStatus;
import fr.soe.a3s.controller.ObserverFilesNumber3;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repositoryEditor.AdminPanel;

public class RepositoryBuilder extends Thread {

	private final Facade facade;
	private final String path;
	private final String repositoryName;
	private final AdminPanel adminPanel;
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

		// Initialize Admin panel for start building
		initAdminPanelForStartBuild();

		repositoryService.getRepositoryBuilderDAO().addObserverFilesNumber3(
				new ObserverFilesNumber3() {
					@Override
					public synchronized void update(int value) {
						adminPanel.getBuildProgressBar().setValue(value);
					}
				});
		try {
			// Build repository
			repositoryService.buildRepository(repositoryName, path);
			repositoryService.setOutOfSync(repositoryName, false);
			repositoryService.write(repositoryName);

			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Repository build finished.", "Build repository",
					JOptionPane.INFORMATION_MESSAGE);

			// Init views
			this.adminPanel.init(repositoryName);
			this.facade.getSyncPanel().init();
			this.adminPanel.updateRepositoryStatus(RepositoryStatus.UPDATED);
			this.adminPanel.getRepositoryPanel().getEventsPanel()
					.init(repositoryName);// update addons list

		} catch (Exception e) {
			e.printStackTrace();
			String message = "";
			if (e instanceof RuntimeException) {
				message = "An unexpected error has occured.";
			} else {
				message = e.getMessage();
			}
			JOptionPane.showMessageDialog(facade.getMainPanel(), message,
					"Error", JOptionPane.ERROR_MESSAGE);
		} finally {
			initAdminPanelForEndBuild();
			terminate();
		}
	}

	private void initAdminPanelForStartBuild() {

		adminPanel.getButtonSelectRepositoryfolderPath().setEnabled(false);
		adminPanel.getButtonBuild().setEnabled(false);
		adminPanel.getButtonCopyAutoConfigURL().setEnabled(false);
		adminPanel.getButtonCheck().setEnabled(false);
		adminPanel.getButtonBuildOptions().setEnabled(false);
		adminPanel.getButtonUpload().setEnabled(false);
		adminPanel.getButtonUploadOptions().setEnabled(false);
		adminPanel.getButtonView().setEnabled(false);
		adminPanel.getBuildProgressBar().setMinimum(0);
		adminPanel.getBuildProgressBar().setMaximum(100);
	}

	private void initAdminPanelForEndBuild() {

		adminPanel.getButtonSelectRepositoryfolderPath().setEnabled(true);
		adminPanel.getButtonBuild().setEnabled(true);
		adminPanel.getButtonCopyAutoConfigURL().setEnabled(true);
		adminPanel.getButtonCheck().setEnabled(true);
		adminPanel.getButtonBuildOptions().setEnabled(true);
		adminPanel.getButtonUpload().setEnabled(true);
		adminPanel.getButtonUploadOptions().setEnabled(true);
		adminPanel.getButtonView().setEnabled(true);
		adminPanel.getBuildProgressBar().setMaximum(0);
	}

	private void terminate() {

		this.interrupt();
		System.gc();
	}

}
