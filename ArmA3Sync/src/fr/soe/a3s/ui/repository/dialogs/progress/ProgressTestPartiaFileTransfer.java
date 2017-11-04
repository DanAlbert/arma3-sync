package fr.soe.a3s.ui.repository.dialogs.progress;

import java.io.IOException;

import javax.swing.JOptionPane;

import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.ConnectionService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.AbstractProgressDialog;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repository.dialogs.error.HeaderErrorDialog;
import fr.soe.a3s.ui.repository.dialogs.error.UnexpectedErrorDialog;

public class ProgressTestPartiaFileTransfer extends AbstractProgressDialog {

	private final String repositoryName;
	/* Services */
	private ConnectionService connexionService;
	private final RepositoryService repositoryService = new RepositoryService();

	public ProgressTestPartiaFileTransfer(Facade facade, String repositoryName) {
		super(facade, "Testing partial file transfer...");
		this.repositoryName = repositoryName;
	}

	public void init() {

		progressBar.setIndeterminate(true);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					AbstractProtocole protocole = repositoryService
							.getProtocol(repositoryName);
					connexionService = new ConnectionService(protocole);
					String header = connexionService
							.CheckPartialFileTransfer(repositoryName);
					setVisible(false);
					if (header != null) {
						HeaderErrorDialog dialog = new HeaderErrorDialog(
								facade, "Partial file transfer", header,
								repositoryName);
						dialog.show();
					} else {
						JOptionPane.showMessageDialog(
								facade.getMainPanel(),
								"Partial file transfer is supported by the HTTP server",
								"Repository", JOptionPane.INFORMATION_MESSAGE);
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
		if (connexionService != null) {
			connexionService.cancel();
		}
		terminate();
	}

	private void terminate() {

		buttonCancel.setEnabled(false);
		progressBar.setIndeterminate(false);
		this.dispose();
	}
}
