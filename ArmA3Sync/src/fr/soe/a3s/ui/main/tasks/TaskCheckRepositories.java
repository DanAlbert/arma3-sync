package fr.soe.a3s.ui.main.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

import fr.soe.a3s.constant.RepositoryStatus;
import fr.soe.a3s.controller.ObserverEnd;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.service.connection.ConnexionService;
import fr.soe.a3s.service.connection.ConnexionServiceFactory;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.main.dialogs.InfoUpdatedRepositoryDialog;
import fr.soe.a3s.ui.repository.RepositoryPanel;

public class TaskCheckRepositories extends TimerTask implements UIConstants {

	private final Facade facade;
	/* Services */
	private final RepositoryService repositoryService = new RepositoryService();

	public TaskCheckRepositories(Facade facade) {
		this.facade = facade;
	}

	@Override
	public void run() {

		/* Check repositories */

		System.out.println("Checking repositories...");

		List<RepositoryDTO> list = repositoryService.getRepositories();

		List<Callable<Integer>> callables = new ArrayList<Callable<Integer>>();
		for (final RepositoryDTO repositoryDTO : list) {
			Callable<Integer> c = new Callable<Integer>() {
				@Override
				public Integer call() {
					try {
						ConnexionService connexion = ConnexionServiceFactory
								.getServiceForRepositoryManagement(repositoryDTO
										.getName());
						connexion.checkRepository(repositoryDTO.getName());
					} catch (Exception e) {
						System.out.println("Error when checking repository "
								+ repositoryDTO.getName() + ":" + "\n"
								+ e.getMessage());
					}
					return 0;
				}
			};
			callables.add(c);
		}

		ExecutorService executor = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());
		try {
			executor.invokeAll(callables);
		} catch (InterruptedException e) {
			System.out
					.println("Checking repositories has been anormaly interrupted.");
		}
		
		executor.shutdownNow();
	
		System.out.println("Checking repositories done.");

		/* Get updated repositories */

		final List<RepositoryDTO> updatedRepositoryDTOs = new ArrayList<RepositoryDTO>();

		for (RepositoryDTO repositoryDTO : list) {
			RepositoryStatus repositoryStatus = repositoryService
					.determineRepositorySyncStatus(repositoryDTO.getName());
			if (repositoryStatus.equals(RepositoryStatus.UPDATED)) {
				updatedRepositoryDTOs.add(repositoryDTO);
			}
		}

		if (!updatedRepositoryDTOs.isEmpty()) {
			// Show info on concole
			String message = "The following repositories have been updated: ";
			for (RepositoryDTO updatedRepositoryDTO : updatedRepositoryDTOs) {
				message = message + "\n" + updatedRepositoryDTO.getName();
			}
			System.out.println(message);
			// Show info on SystemTray
			message = "Repositories updates!";
			facade.getMainPanel().displayMessageToSystemTray(message);
		}
		
		facade.getMainPanel().updateTabs(OP_REPOSITORY_CHANGED);

		/* Run auto update on repositories */

		final List<RepositoryDTO> autoUpdateRepositoryDTOs = new ArrayList<RepositoryDTO>();

		for (RepositoryDTO updatedRepositoryDTO : list) {
			if (updatedRepositoryDTO.isAuto()) {
				autoUpdateRepositoryDTOs.add(updatedRepositoryDTO);
			}
		}

		for (final RepositoryDTO autoUpdateRepositoryDTO : autoUpdateRepositoryDTOs) {

			System.out.println("Auto updating repository: "
					+ autoUpdateRepositoryDTO.getName());

			RepositoryPanel repositoryPanel = facade.getMainPanel()
					.openRepository(autoUpdateRepositoryDTO.getName(), false);
			if (repositoryPanel != null) {
				ObserverEnd obs = new ObserverEnd() {
					@Override
					public void end() {
						facade.getMainPanel().closeRepository(
								autoUpdateRepositoryDTO.getName());
					}
				};
				repositoryPanel.autoUpdate(autoUpdateRepositoryDTO.getName(),
						null, obs);
			}
		}

		/* Get notified repositories */

		final List<RepositoryDTO> notifyRepositoryDTOs = new ArrayList<RepositoryDTO>();

		for (RepositoryDTO updatedRepositoryDTO : updatedRepositoryDTOs) {
			if (updatedRepositoryDTO.isNotify()) {
				notifyRepositoryDTOs.add(updatedRepositoryDTO);
			}
		}

		if (!notifyRepositoryDTOs.isEmpty()) {
			InfoUpdatedRepositoryDialog infoUpdatedRepositoryPanel = facade
					.getInfoUpdatedRepositoryPanel();
			if (infoUpdatedRepositoryPanel != null) {
				infoUpdatedRepositoryPanel.dispose();
			}

			facade.getMainPanel().recoverFromTray();
			facade.getMainPanel().showSyncPanel();
			infoUpdatedRepositoryPanel = new InfoUpdatedRepositoryDialog(facade);
			infoUpdatedRepositoryPanel.init(notifyRepositoryDTOs);
			infoUpdatedRepositoryPanel.setVisible(true);
			facade.setInfoUpdatedRepositoryPanel(infoUpdatedRepositoryPanel);
		}
	}
}
