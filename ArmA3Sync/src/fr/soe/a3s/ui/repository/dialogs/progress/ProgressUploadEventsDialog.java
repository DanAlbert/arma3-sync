package fr.soe.a3s.ui.repository.dialogs.progress;

import javax.swing.JOptionPane;

import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.ConnectionService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.AbstractProgressDialog;
import fr.soe.a3s.ui.Facade;

public class ProgressUploadEventsDialog extends AbstractProgressDialog {

	private final String repositoryName;
	private Thread t;
	private AbstractProtocole uploadProtocol;
	/* Service */
	private ConnectionService connexion;
	private final RepositoryService repositoryService = new RepositoryService();

	public ProgressUploadEventsDialog(Facade facade, String repositoryName) {
		super(facade, "Uploading events informations...");
		this.repositoryName = repositoryName;
	}

	public void init() {

		try {
			uploadProtocol = repositoryService
					.getUploadProtocol(repositoryName);

			if (uploadProtocol == null) {
				String message = "Please use the upload options to configure a connection.";
				throw new CheckException(message);
			}
		} catch (CheckException e) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Inforamation",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		progressBar.setIndeterminate(true);
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					connexion = new ConnectionService(uploadProtocol);
					connexion.upLoadEvents(repositoryName);
					setVisible(false);
					JOptionPane.showMessageDialog(
							facade.getMainPanel(),
							"Events informatons have been uploaded to repository.",
							"Information", JOptionPane.INFORMATION_MESSAGE);
					ProgressSynchronizationDialog synchronizingPanel = new ProgressSynchronizationDialog(
							facade);
					synchronizingPanel.setVisible(true);
					synchronizingPanel.init(repositoryName);
				} catch (RepositoryException e) {
					setVisible(false);
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				} catch (Exception e) {
					if (!canceled) {
						setVisible(false);
						JOptionPane.showMessageDialog(
								facade.getMainPanel(),
								e.getMessage()
										+ "\n"
										+ "Please check upload options and server permissions.",
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				} finally {
					setVisible(false);
					dispose();
					t.interrupt();
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
