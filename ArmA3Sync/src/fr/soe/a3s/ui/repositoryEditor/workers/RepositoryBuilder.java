package fr.soe.a3s.ui.repositoryEditor.workers;

import javax.swing.JOptionPane;

import fr.soe.a3s.controller.ObserverFileSize;
import fr.soe.a3s.controller.ObserverFilesNumber;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;

public class RepositoryBuilder extends Thread {

	private Facade facade;
	private String path;
	private String repositoryName;

	public RepositoryBuilder(Facade facade, String repositoryName, String path) {
		this.facade = facade;
		this.repositoryName = repositoryName;
		this.path = path;
	}

	public void run() {

		facade.getAdminPanel().getButtonSelectFTPfolderPath().setEnabled(false);
		facade.getAdminPanel().getButtonBuild().setEnabled(false);
		facade.getAdminPanel().getButtonCopyAutoConfigURL().setEnabled(false);
		facade.getAdminPanel().getButtonCheck().setEnabled(false);
		
		facade.getAdminPanel().getBuildProgressBar().setMinimum(0);
		facade.getAdminPanel().getBuildProgressBar().setMaximum(100);
		RepositoryService repositoryService = new RepositoryService();
		repositoryService.getRepositoryBuilderDAO().addObserverFileSize(
				new ObserverFileSize() {
					@Override
					public void update(long value) {
						facade.getAdminPanel().getBuildProgressBar()
								.setValue((int)value);
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
			facade.getAdminPanel().getButtonSelectFTPfolderPath().setEnabled(true);
			facade.getAdminPanel().getButtonBuild().setEnabled(true);
			facade.getAdminPanel().getButtonCopyAutoConfigURL().setEnabled(true);
			facade.getAdminPanel().getButtonCheck().setEnabled(true);
			facade.getAdminPanel().getBuildProgressBar().setMaximum(0);
			facade.getAdminPanel().init(repositoryName);
			facade.getSyncPanel().init();
			System.gc();// Required for unlocking files!
		}
	}
}
