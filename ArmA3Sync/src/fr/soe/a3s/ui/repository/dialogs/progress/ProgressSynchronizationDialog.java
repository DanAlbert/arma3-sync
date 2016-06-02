package fr.soe.a3s.ui.repository.dialogs.progress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.service.connection.ConnexionService;
import fr.soe.a3s.service.connection.ConnexionServiceFactory;
import fr.soe.a3s.ui.AbstractProgressDialog;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repository.dialogs.error.UnexpectedErrorDialog;

public class ProgressSynchronizationDialog extends AbstractProgressDialog {

	private final RepositoryService repositoryService = new RepositoryService();
	private ConnexionService connexion = null;
	private Thread t = null;

	public ProgressSynchronizationDialog(Facade facade) {
		super(facade, "Synchronizing with repositories...");
	}

	public void init(final String repositoryName) {

		System.out
				.println("Synchronization with repository: " + repositoryName);
		facade.getSyncPanel().disableAllButtons();
		progressBar.setIndeterminate(true);
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					connexion = ConnexionServiceFactory
							.getServiceForRepositoryManagement(repositoryName);
					connexion.checkRepository(repositoryName);
				} catch (Exception e) {
					setVisible(false);
					if (e instanceof RepositoryException) {
						JOptionPane.showMessageDialog(facade.getMainPanel(),
								e.getMessage(), "Repository",
								JOptionPane.ERROR_MESSAGE);
					} else if (!canceled) {
						if (e instanceof IOException) {
							System.out.println(e.getMessage());
							JOptionPane.showMessageDialog(
									facade.getMainPanel(), e.getMessage(),
									"Repository", JOptionPane.WARNING_MESSAGE);
						} else {
							e.printStackTrace();
							UnexpectedErrorDialog dialog = new UnexpectedErrorDialog(
									facade, "Repository", e, repositoryName);
							dialog.show();
						}
					}
					setVisible(true);
				}

				if (!canceled) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							facade.getSyncPanel().init();
							facade.getOnlinePanel().init();
							facade.getLaunchPanel().init();
							System.out
									.println("Synchronization with repository done.");
						}
					});
				} else {
					System.out
							.println("Synchronization with repository canceled.");
				}
				terminate();
			}
		});
		t.start();
	}

	public void init(final List<String> repositoryNames) {

		assert (!repositoryNames.isEmpty());

		System.out.println("Synchronization with repositories...");

		facade.getSyncPanel().disableAllButtons();
		progressBar.setIndeterminate(true);
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (repositoryNames.isEmpty()) {
					System.out.println("No repository to synchronize with.");
				} else {
					List<Callable<Integer>> callables = new ArrayList<Callable<Integer>>();
					for (final String repositoryName : repositoryNames) {
						Callable<Integer> c = new Callable<Integer>() {
							@Override
							public Integer call() {
								try {
									if (!canceled) {
										connexion = ConnexionServiceFactory
												.getServiceForRepositoryManagement(repositoryName);
										connexion
												.checkRepository(repositoryName);
									}
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

					ExecutorService executor = Executors
							.newFixedThreadPool(Runtime.getRuntime()
									.availableProcessors());
					try {
						executor.invokeAll(callables);
					} catch (InterruptedException e) {
						System.out
								.println("Synchronizing with repositories has been anormaly interrupted.");
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
