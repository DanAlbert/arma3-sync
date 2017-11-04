package fr.soe.a3s.ui.repository.dialogs.progress;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.service.ConnectionService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.AbstractProgressDialog;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.main.dialogs.ModdsetsSelectionDialog;

public class ProgressModsetsSelectionDialog extends AbstractProgressDialog {

	private final RepositoryService repositoryService = new RepositoryService();
	private ConnectionService connexionService = null;
	private Thread t = null;

	public ProgressModsetsSelectionDialog(Facade facade) {
		super(facade, "Synchronizing modsets...");
	}

	public void init(final List<String> repositoryNames) {

		System.out.println("Checking repositories...");
		progressBar.setIndeterminate(true);
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				List<Callable<Integer>> callables = new ArrayList<Callable<Integer>>();
				for (final String repositoryName : repositoryNames) {
					Callable<Integer> c = new Callable<Integer>() {
						@Override
						public Integer call() {
							try {
								AbstractProtocole protocole = repositoryService
										.getProtocol(repositoryName);
								connexionService = new ConnectionService(
										protocole);
								connexionService
										.checkRepository(repositoryName);
							} catch (Exception e) {
								System.out
										.println("Error when checking repository "
												+ repositoryName
												+ ": "
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

				if (!canceled) {
					exit();
					terminate();
				}
			}
		});
		t.start();
	}

	@Override
	protected void menuExitPerformed() {
		exit();
		terminate();
	}

	private void exit() {

		this.setVisible(false);
		canceled = true;
		if (connexionService != null) {
			connexionService.cancel();
		}
		progressBar.setIndeterminate(false);
		dispose();
	}

	private void terminate() {

		ModdsetsSelectionDialog modsetsSelectionPanel = new ModdsetsSelectionDialog(
				facade);
		modsetsSelectionPanel.init();
		modsetsSelectionPanel.setVisible(true);
	}
}
