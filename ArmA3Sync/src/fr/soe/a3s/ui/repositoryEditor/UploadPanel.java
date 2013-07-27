package fr.soe.a3s.ui.repositoryEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

import fr.soe.a3s.exception.FtpException;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.service.FtpService;
import fr.soe.a3s.ui.Facade;

public class UploadPanel extends ProgressPanel {

	private FtpService ftpService = new FtpService();
	private String repositoryName;
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
					boolean response = ftpService.upLoadEvents(repositoryName);
					setVisible(false);
					if (response == false) {
						JOptionPane
								.showMessageDialog(
										facade.getMainPanel(),
										"Failed to upload events informations. \n Please checkout FTP server permissions.",
										"Error", JOptionPane.ERROR_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(facade.getMainPanel(),
								"Events informatons have been uploaded to repository.",
								"Error", JOptionPane.INFORMATION_MESSAGE);
						SynchronizingPanel synchronizingPanel = new SynchronizingPanel(facade);
						synchronizingPanel.setVisible(true);
						synchronizingPanel.init();
					}
				} catch (RepositoryException e) {
					setVisible(false);
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				} catch (FtpException e) {
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
		ftpService.disconnect();
		this.dispose();
		t.interrupt();
	}

}
