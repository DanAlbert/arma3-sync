package fr.soe.a3s.ui.repositoryEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.FtpException;
import fr.soe.a3s.exception.HttpException;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.service.AbstractConnexionService;
import fr.soe.a3s.service.ConnexionServiceFactory;
import fr.soe.a3s.service.FtpService;
import fr.soe.a3s.ui.Facade;

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
	private String repositoryName;
	private String eventName;
	private Thread t;

	public ConnectionPanel(Facade facade, String repositoryName,
			String eventName) {
		super(facade);
		this.repositoryName = repositoryName;
		this.eventName = eventName;
		labelTitle.setText("Connecting to repository...");

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
					connexion = ConnexionServiceFactory
							.getServiceFromRepository(repositoryName);
					connexion.checkRepository(repositoryName);
				} catch (Exception e) {
					e.printStackTrace();
					if (!canceled && e.getMessage()!=null) {
						setVisible(false);
						JOptionPane.showMessageDialog(facade.getMainPanel(),
								e.getMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				} finally {
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
