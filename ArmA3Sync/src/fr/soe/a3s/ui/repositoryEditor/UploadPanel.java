package fr.soe.a3s.ui.repositoryEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.service.AbstractConnexionService;
import fr.soe.a3s.service.ConnexionServiceFactory;
import fr.soe.a3s.ui.Facade;

public class UploadPanel extends ProgressPanel {

	private AbstractConnexionService connexion;
	private final String repositoryName;
	private Thread t;

	public UploadPanel(Facade facade, String repositoryName) {
		super(facade);
		this.repositoryName = repositoryName;
		labelTitle.setText("Uploading events informations...");

		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuExitPerformed();
			}
		});
		// Add Listeners
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				menuExitPerformed();
			}
		});
	}

	public void init() {

		progressBar.setIndeterminate(true);
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					connexion = ConnexionServiceFactory
							.getServiceFromRepository(repositoryName);
					boolean response = connexion.upLoadEvents(repositoryName);
					setVisible(false);
					if (response == false) {
						JOptionPane.showMessageDialog(
								facade.getMainPanel(),
								"Failed to upload events informations. \n Please check out the server permissions.",
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

	private void menuExitPerformed() {
		this.setVisible(false);
		canceled = true;
		if (connexion != null) {
			connexion.disconnect();
		}
		this.dispose();
		t.interrupt();
	}

}
