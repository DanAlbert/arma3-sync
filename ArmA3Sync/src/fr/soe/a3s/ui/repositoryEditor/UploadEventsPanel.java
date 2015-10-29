package fr.soe.a3s.ui.repositoryEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.dto.ProtocolDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.AbstractConnexionService;
import fr.soe.a3s.service.AbstractConnexionServiceFactory;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ProgressPanel;

public class UploadEventsPanel extends ProgressPanel {

	private AbstractConnexionService connexion;
	private final String repositoryName;
	private Thread t;
	/* Service */
	private final RepositoryService repositoryService = new RepositoryService();

	public UploadEventsPanel(Facade facade, String repositoryName) {
		super(facade);
		this.repositoryName = repositoryName;
		labelTitle.setText("Uploading events informations...");
	}

	public void init() {
		try {
			// 1. Check repository upload protocole
			RepositoryDTO repositoryDTO = repositoryService
					.getRepository(repositoryName);
			ProtocolDTO protocoleDTO = repositoryDTO.getProtocoleDTO();
			ProtocolType protocole = protocoleDTO.getProtocolType();
			ProtocolDTO uploadProtocoleDTO = repositoryDTO
					.getRepositoryUploadProtocoleDTO();
			if (uploadProtocoleDTO == null
					&& protocole.equals(ProtocolType.FTP)) {
				repositoryService.setRepositoryUploadProtocole(repositoryName,
						protocoleDTO.getUrl(), protocoleDTO.getPort(),
						protocoleDTO.getLogin(), protocoleDTO.getPassword(),
						protocole);
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
					connexion = AbstractConnexionServiceFactory
							.getRepositoryUploadServiceFromRepository(repositoryName);
					boolean response = connexion.upLoadEvents(repositoryName);
					setVisible(false);
					if (response == false) {
						JOptionPane.showMessageDialog(
								facade.getMainPanel(),
								"Failed to upload events informations. \n Please check upload options and server permissions.",
								"Information", JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(
								facade.getMainPanel(),
								"Events informatons have been uploaded to repository.",
								"Information", JOptionPane.INFORMATION_MESSAGE);
						SynchronizingPanel synchronizingPanel = new SynchronizingPanel(
								facade);
						synchronizingPanel.setVisible(true);
						synchronizingPanel.init(repositoryName);
					}
				} catch (RepositoryException e) {
					setVisible(false);
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				} catch (Exception e) {
					if (!canceled) {
						setVisible(false);
						JOptionPane.showMessageDialog(facade.getMainPanel(),
								e.getMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
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
