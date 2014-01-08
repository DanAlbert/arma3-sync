package fr.soe.a3s.ui.repositoryEditor.workers;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import fr.soe.a3s.exception.RepositoryCheckException;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.exception.ServerInfoNotFoundException;
import fr.soe.a3s.exception.SyncFileNotFoundException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;

public class RepositoryChecker extends Thread {

	private Facade facade;
	private String path;
	private String repositoryName;

	public RepositoryChecker(Facade facade, String repositoryName, String path) {
		this.facade = facade;
		this.repositoryName = repositoryName;
		this.path = path;
	}

	public void run() {

		facade.getAdminPanel().getButtonSelectRepositoryfolderPath().setEnabled(false);
		facade.getAdminPanel().getButtonBuild().setEnabled(false);
		facade.getAdminPanel().getButtonCopyAutoConfigURL().setEnabled(false);
		facade.getAdminPanel().getButtonCheck().setEnabled(false);

		UIManager.put("ProgressBar.repaintInterval", new Integer(25));
		UIManager.put("ProgressBar.cycleTime", new Integer(10000));
		facade.getAdminPanel().getCheckProgressBar().setIndeterminate(true);
		RepositoryService repositoryService = new RepositoryService();

		try {
			repositoryService.checkRepository(repositoryName, path);
			repositoryService.setOutOfSync(repositoryName, false);
			facade.getAdminPanel().init(repositoryName);
			repositoryService.write(repositoryName);

			facade.getAdminPanel().getCheckProgressBar()
					.setIndeterminate(false);
			facade.getAdminPanel().getCheckProgressBar().setMaximum(0);
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Repository is synchronized.", "Check repository",
					JOptionPane.INFORMATION_MESSAGE);

		} catch (RepositoryCheckException e1) {
			repositoryService.setOutOfSync(repositoryName, true);
			facade.getAdminPanel().getCheckProgressBar()
					.setIndeterminate(false);
			facade.getAdminPanel().getCheckProgressBar().setMaximum(0);
			JOptionPane
					.showMessageDialog(
							facade.getMainPanel(),
							"Repository is out of synchronization and must be rebuilt.",
							"Check repository", JOptionPane.WARNING_MESSAGE);
		} catch (Exception e2) {
			repositoryService.setOutOfSync(repositoryName, false);
			facade.getAdminPanel().getCheckProgressBar()
					.setIndeterminate(false);
			facade.getAdminPanel().getCheckProgressBar().setMaximum(0);
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e2.getMessage(), "Check repository",
					JOptionPane.WARNING_MESSAGE);
		}
		finally {
			facade.getAdminPanel().getButtonSelectRepositoryfolderPath()
					.setEnabled(true);
			facade.getAdminPanel().getButtonBuild().setEnabled(true);
			facade.getAdminPanel().getButtonCopyAutoConfigURL()
					.setEnabled(true);
			facade.getAdminPanel().getButtonCheck().setEnabled(true);
			facade.getAdminPanel().init(repositoryName);
			facade.getSyncPanel().init();
			System.gc();// Required for unlocking files!
		}
	}
}
