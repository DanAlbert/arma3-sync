package fr.soe.a3s.ui.repositoryEditor.progressDialogs;

import java.util.List;

import javax.swing.SwingUtilities;

import fr.soe.a3s.service.connection.ConnexionService;
import fr.soe.a3s.service.connection.ConnexionServiceFactory;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ProgressPanel;

public class ProgressModsetSelectionPanel extends ProgressPanel {

	private ConnexionService connexion = null;
	private Thread t = null;

	public ProgressModsetSelectionPanel(Facade facade) {
		super(facade);
		labelTitle.setText("Synchronizing modsets...");
	}

	public void init(final List<String> repositoryNames) {

		progressBar.setIndeterminate(true);
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				for (String repositoryName : repositoryNames) {
					if (!canceled) {
						try {
							connexion = ConnexionServiceFactory
									.getServiceForRepositoryManagement(repositoryName);
							connexion.checkRepository(repositoryName);
						} catch (Exception e) {
						}
					}
				}
				
				if (!canceled) {
					try {
						t.sleep(500);
					} catch (InterruptedException e) {
					}
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							facade.getAddonsPanel().updateModsetSelection(
									repositoryNames);
						}
					});
				}
				terminate();
			}
		});
		t.start();
	}

	@Override
	protected void menuExitPerformed() {

		this.setVisible(false);
		canceled = true;
		if (connexion != null) {
			connexion.cancel();
		}
		terminate();
	}

	private void terminate() {

		progressBar.setIndeterminate(false);
		dispose();
	}
}
