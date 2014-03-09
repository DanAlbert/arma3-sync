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
import fr.soe.a3s.ui.repositoryEditor.AdminPanel;

public class RepositoryChecker extends Thread {

	private Facade facade;
	private AdminPanel adminPanel;
	private String path;
	private String repositoryName;

	public RepositoryChecker(Facade facade, String repositoryName, String path,
			AdminPanel adminPanel) {
		this.facade = facade;
		this.adminPanel = adminPanel;
		this.repositoryName = repositoryName;
		this.path = path;
	}

	public void run() {

		this.adminPanel.getButtonSelectRepositoryfolderPath().setEnabled(false);
		this.adminPanel.getButtonBuild().setEnabled(false);
		this.adminPanel.getButtonCopyAutoConfigURL().setEnabled(false);
		this.adminPanel.getButtonCheck().setEnabled(false);

		UIManager.put("ProgressBar.repaintInterval", new Integer(25));
		UIManager.put("ProgressBar.cycleTime", new Integer(10000));
		this.adminPanel.getCheckProgressBar().setIndeterminate(true);
		RepositoryService repositoryService = new RepositoryService();

		try {
			repositoryService.checkRepository(repositoryName, path);
			repositoryService.setOutOfSync(repositoryName, false);
			this.adminPanel.init(repositoryName);
			repositoryService.write(repositoryName);

			this.adminPanel.getCheckProgressBar().setIndeterminate(false);
			this.adminPanel.getCheckProgressBar().setMaximum(0);
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Repository is synchronized.", "Check repository",
					JOptionPane.INFORMATION_MESSAGE);

		} catch (RepositoryCheckException e1) {
			repositoryService.setOutOfSync(repositoryName, true);
			this.adminPanel.getCheckProgressBar().setIndeterminate(false);
			this.adminPanel.getCheckProgressBar().setMaximum(0);
			JOptionPane
					.showMessageDialog(
							facade.getMainPanel(),
							"Repository is out of synchronization and must be rebuilt.",
							"Check repository", JOptionPane.WARNING_MESSAGE);
		} catch (Exception e2) {
			repositoryService.setOutOfSync(repositoryName, false);
			this.adminPanel.getCheckProgressBar().setIndeterminate(false);
			this.adminPanel.getCheckProgressBar().setMaximum(0);
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e2.getMessage(), "Check repository",
					JOptionPane.WARNING_MESSAGE);
		} finally {
			this.adminPanel.getButtonSelectRepositoryfolderPath().setEnabled(
					true);
			this.adminPanel.getButtonBuild().setEnabled(true);
			this.adminPanel.getButtonCopyAutoConfigURL().setEnabled(true);
			this.adminPanel.getButtonCheck().setEnabled(true);
			this.adminPanel.init(repositoryName);
			facade.getSyncPanel().init();
			System.gc();// Required for unlocking files!
		}
	}
}
