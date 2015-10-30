package fr.soe.a3s.ui.repositoryEditor.progressDialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.service.AbstractConnexionService;
import fr.soe.a3s.service.AbstractConnexionServiceFactory;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ProgressPanel;

public class SynchronizingPanel extends ProgressPanel {

	private final RepositoryService repositoryService = new RepositoryService();
	private AbstractConnexionService connexion;

	public SynchronizingPanel(Facade facade) {
		super(facade);
		labelTitle.setText("Checking repositories...");
	}

	public void init(final String repositoryName) {

		facade.getSyncPanel().disableAllButtons();
		progressBar.setIndeterminate(true);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (repositoryName == null) {
					List<RepositoryDTO> list = repositoryService
							.getRepositories();
					for (final RepositoryDTO repositoryDTO : list) {
						if (!canceled) {
							try {
								connexion = AbstractConnexionServiceFactory
										.getServiceFromRepository(repositoryDTO
												.getName());
								connexion.checkRepository(repositoryDTO
										.getName());
								facade.getAddonsPanel().updateModsetSelection(
										repositoryDTO.getName());
							} catch (Exception e) {
							}
						}
					}
				} else {
					try {
						connexion = AbstractConnexionServiceFactory
								.getServiceFromRepository(repositoryName);
						connexion.checkRepository(repositoryName);
						facade.getAddonsPanel().updateModsetSelection(
								repositoryName);
					} catch (Exception e) {
					}
				}

				facade.getSyncPanel().init();
				facade.getOnlinePanel().init();
				facade.getLaunchPanel().init();
				progressBar.setIndeterminate(false);
				dispose();
				facade.getSyncPanel().enableAllButtons();
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
		this.dispose();
	}
}
