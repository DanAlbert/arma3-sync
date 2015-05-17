package fr.soe.a3s.ui.repositoryEditor.workers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import fr.soe.a3s.exception.RepositoryCheckException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repositoryEditor.AdminPanel;

public class RepositoryChecker extends Thread {

	private final Facade facade;
	private final AdminPanel adminPanel;
	private final String path;
	private final String repositoryName;

	public RepositoryChecker(Facade facade, String repositoryName, String path,
			AdminPanel adminPanel) {
		this.facade = facade;
		this.adminPanel = adminPanel;
		this.repositoryName = repositoryName;
		this.path = path;
	}

	@Override
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

			List<String> messages = new ArrayList<String>();
			StringTokenizer stk = new StringTokenizer(e1.getMessage(), "*");
			while (stk.hasMoreTokens()) {
				messages.add(stk.nextToken());
			}

			String message = "Repository is out of synchronization.";
			if (messages.size() > 5) {
				for (int i = 0; i < 5; i++) {
					String m = messages.get(i);
					message = message + "\n" + " - " + m;
				}
				message = message + "\n" + "["
						+ Integer.toString(messages.size() - 5) + "] more...";
			} else {
				for (String m : messages) {
					message = message + "\n" + " - " + m;
				}
			}

			String fileName = "ArmA3Sync-log.txt";
			message = message + "\n\n"
					+ "Do you want export the errors log file to desktop ("
					+ fileName + ")?";

			int value = JOptionPane.showConfirmDialog(facade.getMainPanel(),
					message, "Check repository", 0, JOptionPane.ERROR_MESSAGE);

			if (value == 0) {
				try {
					String title = "Repository " + repositoryName
							+ " is out of synchronization:";
					repositoryService.exportErrorsToDesktop(title, messages,
							fileName);
					JOptionPane
							.showMessageDialog(facade.getMainPanel(),
									"Log file has been exported to desktop",
									"Check repository",
									JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(
							facade.getMainPanel(),
							"Failed to export log file to desktop" + "\n"
									+ e1.getMessage(), "Check repository",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (Exception e2) {
			e2.printStackTrace();
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
