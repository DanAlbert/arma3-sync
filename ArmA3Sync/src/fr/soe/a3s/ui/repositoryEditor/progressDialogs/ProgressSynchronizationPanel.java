package fr.soe.a3s.ui.repositoryEditor.progressDialogs;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import fr.soe.a3s.constant.RepositoryStatus;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.service.connection.ConnexionService;
import fr.soe.a3s.service.connection.ConnexionServiceFactory;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ProgressPanel;
import fr.soe.a3s.ui.mainEditor.InfoUpdatedRepositoryPanel;

public class ProgressSynchronizationPanel extends ProgressPanel {

	private final RepositoryService repositoryService = new RepositoryService();
	private ConnexionService connexion =  null;
	private Thread t = null;

	public ProgressSynchronizationPanel(Facade facade) {
		super(facade);
		labelTitle.setText("Synchronizing with repositories...");
	}

	public void init(final List<String> repositoryNames) {

		facade.getSyncPanel().disableAllButtons();
		progressBar.setIndeterminate(true);
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (repositoryNames.isEmpty()) {
					System.out.println("No repository to synchronize with.");
				} else {
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
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								facade.getSyncPanel().init();
								facade.getOnlinePanel().init();
								facade.getLaunchPanel().init();
								System.out
										.println("Synchronization with repositories done.");
							}
						});
					} else {
						System.out
								.println("Synchronization with repositories canceled.");
					}
					terminate();
				}
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
		facade.getSyncPanel().enableAllButtons();
	}
}
