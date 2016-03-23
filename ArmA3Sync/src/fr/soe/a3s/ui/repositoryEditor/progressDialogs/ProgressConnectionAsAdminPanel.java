package fr.soe.a3s.ui.repositoryEditor.progressDialogs;

import java.io.IOException;

import javax.swing.JOptionPane;

import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.service.connection.ConnexionService;
import fr.soe.a3s.service.connection.ConnexionServiceFactory;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ProgressPanel;
import fr.soe.a3s.ui.repositoryEditor.RepositoryPanel;
import fr.soe.a3s.ui.repositoryEditor.errorDialogs.UnexpectedErrorDialog;

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
public class ProgressConnectionAsAdminPanel extends ProgressPanel {

	private ConnexionService connexion;
	private final String repositoryName;
	/* Services */
	private final RepositoryService repositoryService = new RepositoryService();

	public ProgressConnectionAsAdminPanel(Facade facade, String repositoryName) {
		super(facade);
		this.repositoryName = repositoryName;
		labelTitle.setText("Connecting to repository...");
	}

	public void init() {

		facade.getSyncPanel().disableAllButtons();
		progressBar.setIndeterminate(true);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("Connecting to repository: "
							+ repositoryName);
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
				repositoryName);
		if (repositoryPanel != null) {
			repositoryPanel.admin(repositoryName);
		}
	}
}
