package fr.soe.a3s.ui.repository.dialogs.progress;

import fr.soe.a3s.service.connection.ConnexionService;
import fr.soe.a3s.service.connection.ConnexionServiceFactory;
import fr.soe.a3s.ui.AbstractProgressDialog;
import fr.soe.a3s.ui.Facade;

public class ProgressModsetsSynchronizationDialog extends
		AbstractProgressDialog {

	private ConnexionService connexion = null;
	private Thread t = null;
	private String repositoryName = null;

	public ProgressModsetsSynchronizationDialog(Facade facade) {
		super(facade, "Synchronizing Addon groups...");
	}

	public void init(final String repositoryName) {

		this.repositoryName = repositoryName;
		progressBar.setIndeterminate(true);
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					connexion = ConnexionServiceFactory
							.getServiceForRepositoryManagement(repositoryName);
					connexion.checkRepository(repositoryName);
				} catch (Exception e) {
				}

				if (!canceled) {
					try {
						t.sleep(1000);
					} catch (InterruptedException e) {
					}
					terminate();
				}
				exit();
			}
		});
		t.start();
	}

	private void exit() {

		this.setVisible(false);
		canceled = true;
		if (connexion != null) {
			connexion.cancel();
		}
		progressBar.setIndeterminate(false);
		dispose();
	}

	@Override
	protected void menuExitPerformed() {
		exit();
	}

	private void terminate() {
		facade.getAddonsPanel().updateModsetSelection(repositoryName);
	}
}
