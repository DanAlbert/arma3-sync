package fr.soe.a3s.ui.repository.dialogs.progress;

import java.io.IOException;

import javax.swing.JOptionPane;

import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.service.connection.ConnexionService;
import fr.soe.a3s.service.connection.ConnexionServiceFactory;
import fr.soe.a3s.ui.AbstractProgressDialog;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repository.RepositoryPanel;
import fr.soe.a3s.ui.repository.dialogs.error.UnexpectedErrorDialog;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class ProgressConnectionAsAdminDialog extends AbstractProgressDialog {

	private ConnexionService connexion;
	private final String repositoryName;
	/* Services */
	private final RepositoryService repositoryService = new RepositoryService();

	public ProgressConnectionAsAdminDialog(Facade facade, String repositoryName) {
		super(facade, "Connecting to repository...");
		this.repositoryName = repositoryName;
	}

	public void init() {

		System.out.println("Connecting as admin to repository: "
				+ repositoryName);
		facade.getSyncPanel().disableAllButtons();
		progressBar.setIndeterminate(true);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					connexion = ConnexionServiceFactory
							.getServiceForRepositoryManagement(repositoryName);
					connexion.getSync(repositoryName);
					connexion.getServerInfo(repositoryName);
					connexion.getChangelogs(repositoryName);
					connexion.getEvents(repositoryName);
					// do not retrieve autoconfig file => erase online servers
					// conf

					if (repositoryService.getSync(repositoryName) == null) {
						System.out.println("Remote file Sync not found.");
					}
					if (repositoryService.getServerInfo(repositoryName) == null) {
						System.out.println("Remote file ServerInfo not found.");
					}
					if (repositoryService.getChangelogs(repositoryName) == null) {
						System.out.println("Remote file Changelogs not found.");
					}
					if (repositoryService.getEvents(repositoryName) == null) {
						System.out.println("Remote file Events not found.");
					}
				} catch (Exception e) {
					e.printStackTrace();
					setVisible(false);
					if (e instanceof RepositoryException) {
						JOptionPane.showMessageDialog(facade.getMainPanel(),
								e.getMessage(), "Repository",
								JOptionPane.ERROR_MESSAGE);
					} else if (!canceled) {
						if (e instanceof IOException) {
							JOptionPane.showMessageDialog(
									facade.getMainPanel(), e.getMessage(),
									"Repository", JOptionPane.WARNING_MESSAGE);
						} else {
							UnexpectedErrorDialog dialog = new UnexpectedErrorDialog(
									facade, "Repository", e, repositoryName);
							dialog.show();
						}
					}
					setVisible(true);
				} finally {
					terminate();
				}
			}
		});
		t.start();
	}

	@Override
	protected void menuExitPerformed() {

		canceled = true;
		if (connexion != null) {
			connexion.cancel();
		}
		terminate();
	}

	private void terminate() {

		buttonCancel.setEnabled(false);
		progressBar.setIndeterminate(false);
		facade.getSyncPanel().enableAllButtons();
		this.dispose();
		RepositoryPanel repositoryPanel = facade.getMainPanel().openRepository(
				repositoryName, true);
		if (repositoryPanel != null) {
			repositoryPanel.admin(repositoryName);
		}
	}
}
