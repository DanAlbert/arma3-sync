package fr.soe.a3s.ui.repositoryEditor.workers;

import javax.swing.JOptionPane;

import fr.soe.a3s.controller.ObserverFileSize;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repositoryEditor.AdminPanel;

public class RepositoryBuilder extends Thread {

	private Facade facade;
	private String path;
	private String repositoryName;
	private AdminPanel adminPanel;

	public RepositoryBuilder(Facade facade, String repositoryName, String path,
			AdminPanel adminPanel) {
		this.facade = facade;
		this.repositoryName = repositoryName;
		this.path = path;
		this.adminPanel = adminPanel;
	}

	public void run() {

		adminPanel.getButtonSelectRepositoryfolderPath().setEnabled(false);
		adminPanel.getButtonBuild().setEnabled(false);
		adminPanel.getButtonCopyAutoConfigURL().setEnabled(false);
		adminPanel.getButtonCheck().setEnabled(false);

		adminPanel.getBuildProgressBar().setMinimum(0);
		adminPanel.getBuildProgressBar().setMaximum(100);
		RepositoryService repositoryService = new RepositoryService();
		repositoryService.getRepositoryBuilderDAO().addObserverFileSize(
				new ObserverFileSize() {
					@Override
					public synchronized void update(long value) {
						adminPanel.getBuildProgressBar().setValue((int) value);
					}
				});
		try {
			repositoryService.buildRepository(repositoryName, path);
			repositoryService.setOutOfSync(repositoryName, false);
			repositoryService.write(repositoryName);
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Repository build finished.", "Build repository",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		} finally {
			adminPanel.getButtonSelectRepositoryfolderPath().setEnabled(true);
			adminPanel.getButtonBuild().setEnabled(true);
			adminPanel.getButtonCopyAutoConfigURL().setEnabled(true);
			adminPanel.getButtonCheck().setEnabled(true);
			adminPanel.getBuildProgressBar().setMaximum(0);
			adminPanel.init(repositoryName);
			facade.getSyncPanel().init();
			System.gc();// Required for unlocking files!
		}
	}
}
