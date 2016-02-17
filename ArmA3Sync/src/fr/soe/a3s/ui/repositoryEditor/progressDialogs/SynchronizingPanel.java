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

public class SynchronizingPanel extends ProgressPanel {

	private final RepositoryService repositoryService = new RepositoryService();
	private ConnexionService connexion;
	private Thread t = null;
	private boolean withNotification = false;

	public SynchronizingPanel(Facade facade, boolean withNotification) {
		super(facade);
		labelTitle.setText("Synchronizing with repositories...");
		this.withNotification = withNotification;
	}

	public void init(final List<String> repositoryNames) {

		facade.getSyncPanel().disableAllButtons();
		progressBar.setIndeterminate(true);
		t = new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					t.sleep(1000);
				} catch (InterruptedException e) {
				}

				System.out.println("Synchronizing with repositories...");

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
							facade.getAddonsPanel().updateModsetSelection(
									repositoryNames);
							facade.getSyncPanel().init();
							facade.getOnlinePanel().init();
							facade.getLaunchPanel().init();
							if (withNotification) {
								List<String> updatedRepositoryNames = new ArrayList<String>();
								for (String repositoryName : repositoryNames) {
									try {
										RepositoryStatus repositoryStatus = repositoryService
												.getRepositoryStatus(repositoryName);
										RepositoryDTO repositoryDTO = repositoryService
												.getRepository(repositoryName);
										if (repositoryStatus
												.equals(RepositoryStatus.UPDATED)
												&& repositoryDTO.isNotify()) {
											updatedRepositoryNames
													.add(repositoryName);
										}
									} catch (RepositoryException e) {
										e.printStackTrace();
									}
								}

								if (!updatedRepositoryNames.isEmpty()) {
									String message = "The following repositories have been updated:";
									for (String rep : repositoryNames) {
										message = message + "\n" + "> " + rep;
									}
									InfoUpdatedRepositoryPanel infoUpdatedRepositoryPanel = new InfoUpdatedRepositoryPanel(
											facade);
									infoUpdatedRepositoryPanel
											.init(updatedRepositoryNames);
									infoUpdatedRepositoryPanel.setVisible(true);
								}
							}
							System.out
									.println("Synchronization with repositories done.");
							terminate();
						}
					});
				} else {
					System.out
							.println("Synchronization with repositories canceled.");
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
