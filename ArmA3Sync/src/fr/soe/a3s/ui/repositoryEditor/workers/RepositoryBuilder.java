package fr.soe.a3s.ui.repositoryEditor.workers;

import javax.swing.JOptionPane;

import fr.soe.a3s.constant.RepositoryStatus;
import fr.soe.a3s.controller.ObserverFileSize2;
import fr.soe.a3s.controller.ObserverFilesNumber3;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repositoryEditor.AdminPanel;

public class RepositoryBuilder extends Thread {

	private final Facade facade;
	private final String path;
	private final String repositoryName;
	private final AdminPanel adminPanel;

	public RepositoryBuilder(Facade facade, String repositoryName, String path,
			AdminPanel adminPanel) {
		this.facade = facade;
		this.repositoryName = repositoryName;
		this.path = path;
		this.adminPanel = adminPanel;
	}

	@Override
	public void run() {

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
		RepositoryService repositoryService = new RepositoryService();
		repositoryService.getRepositoryBuilderDAO().addObserverFilesNumber3(
				new ObserverFilesNumber3() {
					@Override
					public synchronized void update(int value) {
						adminPanel.getBuildProgressBar().setValue(value);
					}
				});
		try {
			repositoryService.buildRepository(repositoryName, path);
			repositoryService.setOutOfSync(repositoryName, false);
			repositoryService.write(repositoryName);
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Repository build finished.", "Build repository",
					JOptionPane.INFORMATION_MESSAGE);

			this.adminPanel.init(repositoryName);
			this.facade.getSyncPanel().init();
			this.adminPanel.updateRepositoryStatus(RepositoryStatus.UPDATED);
			this.adminPanel.getRepositoryPanel().getEventsPanel()
					.init(repositoryName);// update addons list
		} catch (Exception e) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		} finally {
			adminPanel.getButtonSelectRepositoryfolderPath().setEnabled(true);
			adminPanel.getButtonBuild().setEnabled(true);
			adminPanel.getButtonCopyAutoConfigURL().setEnabled(true);
			adminPanel.getButtonCheck().setEnabled(true);
			adminPanel.getButtonBuildOptions().setEnabled(true);
			adminPanel.getButtonUpload().setEnabled(true);
			adminPanel.getButtonUploadOptions().setEnabled(true);
			adminPanel.getButtonView().setEnabled(true);
			adminPanel.getBuildProgressBar().setMaximum(0);
			System.gc();// Required for unlocking files!
		}
	}
}
