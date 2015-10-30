package fr.soe.a3s.ui.repositoryEditor.progressDialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JOptionPane;

import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.AbstractConnexionService;
import fr.soe.a3s.service.AbstractConnexionServiceFactory;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ProgressPanel;
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
public class ConnectionPanel extends ProgressPanel {

	private AbstractConnexionService connexion;
	private final String repositoryName;
	private final String eventName;
	/* Services */
	private final RepositoryService repositoryService = new RepositoryService();

	public ConnectionPanel(Facade facade, String repositoryName,
			String eventName) {
		super(facade);
		this.repositoryName = repositoryName;
		this.eventName = eventName;
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
					connexion = AbstractConnexionServiceFactory
							.getServiceFromRepository(repositoryName);
					connexion.checkRepository(repositoryName);

					if (repositoryService.getServerInfo(repositoryName) == null) {
						System.out.println("ServerInfo not found.");
					}
					if (repositoryService.getChangelogs(repositoryName) == null) {
						System.out.println("Changelogs not found.");
					}
					if (repositoryService.getAutoconfig(repositoryName) == null) {
						System.out.println("Autoconfig not found.");
					}
					if (repositoryService.getEvents(repositoryName) == null) {
						System.out.println("Events not found.");
					}
				} catch (Exception e) {
					if (e instanceof RepositoryException) {
						JOptionPane.showMessageDialog(facade.getMainPanel(),
								e.getMessage(), "Repository",
								JOptionPane.ERROR_MESSAGE);
					} else if (!canceled && e instanceof IOException) {
						System.out.println(e.getMessage());
					} else {
						e.printStackTrace();
						if (!canceled) {
							UnexpectedErrorDialog dialog = new UnexpectedErrorDialog(
									facade, "Repository", e, repositoryName);
							dialog.show();
						}
					}
				} finally {
					facade.getSyncPanel().enableAllButtons();
					if (!canceled) {
						facade.getSyncPanel().init();
						facade.getOnlinePanel().init();
						facade.getLaunchPanel().init();
						facade.getMainPanel().openRepository(repositoryName,
								eventName, false);
						progressBar.setIndeterminate(false);
					}
					setVisible(false);
					dispose();
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
		this.dispose();
	}
}
