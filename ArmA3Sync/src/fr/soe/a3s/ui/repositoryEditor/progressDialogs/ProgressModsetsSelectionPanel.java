package fr.soe.a3s.ui.repositoryEditor.progressDialogs;

import java.util.List;

import fr.soe.a3s.service.connection.ConnexionService;
import fr.soe.a3s.service.connection.ConnexionServiceFactory;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ProgressPanel;
import fr.soe.a3s.ui.mainEditor.ModdsetsSelectionPanel;

public class ProgressModsetsSelectionPanel extends ProgressPanel {

	private ConnexionService connexion = null;
	private Thread t = null;

	public ProgressModsetsSelectionPanel(Facade facade) {
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
				}
				exit();
				terminate();
			}
		});
		t.start();
	}

	@Override
	protected void menuExitPerformed() {
		exit();
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

	private void terminate() {

		ModdsetsSelectionPanel modsetsSelectionPanel = new ModdsetsSelectionPanel(
				facade);
		modsetsSelectionPanel.init();
		modsetsSelectionPanel.setVisible(true);
	}
}
