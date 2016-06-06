package fr.soe.a3s.ui.repository.dialogs.progress;

import javax.swing.JOptionPane;

import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.dto.ProtocolDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.service.connection.ConnexionService;
import fr.soe.a3s.service.connection.ConnexionServiceFactory;
import fr.soe.a3s.ui.AbstractProgressDialog;
import fr.soe.a3s.ui.Facade;

public class ProgressUploadEventsDialog extends AbstractProgressDialog {

	private ConnexionService connexion;
	private final String repositoryName;
	private Thread t;
	/* Service */
	private final RepositoryService repositoryService = new RepositoryService();

	public ProgressUploadEventsDialog(Facade facade, String repositoryName) {
		super(facade, "Uploading events informations...");
		this.repositoryName = repositoryName;
	}

	public void init() {
		try {
			// 1. Check repository upload protocole
			RepositoryDTO repositoryDTO = repositoryService
					.getRepository(repositoryName);
			ProtocolDTO protocoleDTO = repositoryDTO.getProtocoleDTO();
			ProtocolType protocolType = protocoleDTO.getProtocolType();
			ProtocolDTO uploadProtocoleDTO = repositoryDTO
					.getUploadProtocoleDTO();
			if (uploadProtocoleDTO == null
					&& protocolType.equals(ProtocolType.FTP)) {
				repositoryService.setRepositoryUploadProtocole(repositoryName,
						protocoleDTO.getUrl(), protocoleDTO.getPort(),
						protocoleDTO.getLogin(), protocoleDTO.getPassword(),
						protocolType, "0", "0");
			} else if (uploadProtocoleDTO == null) {
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
					connexion = ConnexionServiceFactory
							.getServiceForRepositoryUpload(repositoryName);
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
