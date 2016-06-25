package fr.soe.a3s.ui.repository.workers;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import fr.soe.a3s.controller.ObserverCount;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.exception.remote.RemoteAutoconfigFileNotFoundException;
import fr.soe.a3s.exception.remote.RemoteChangelogsFileNotFoundException;
import fr.soe.a3s.exception.remote.RemoteRepositoryException;
import fr.soe.a3s.exception.remote.RemoteServerInfoFileNotFoundException;
import fr.soe.a3s.exception.remote.RemoteSyncFileNotFoundException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.service.connection.ConnexionService;
import fr.soe.a3s.service.connection.ConnexionServiceFactory;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repository.AdminPanel;
import fr.soe.a3s.ui.repository.dialogs.error.ErrorsListDialog;
import fr.soe.a3s.ui.repository.dialogs.error.UnexpectedErrorDialog;

public class RepositoryChecker extends Thread implements DataAccessConstants {

	private final Facade facade;
	private final AdminPanel adminPanel;
	/* Data */
	private final String repositoryName;
	private final List<SyncTreeLeafDTO> listFilesToCheck = new ArrayList<SyncTreeLeafDTO>();
	private List<Exception> errors = new ArrayList<Exception>();
	/* Tests */
	private boolean canceled = false;
	/* Services */
	private final RepositoryService repositoryService = new RepositoryService();
	private ConnexionService connexionService;

	public RepositoryChecker(Facade facade, String repositoryName,
			AdminPanel adminPanel) {
		this.facade = facade;
		this.adminPanel = adminPanel;
		this.repositoryName = repositoryName;
	}

	@Override
	public void run() {

		System.out.println("Starting checking repository content: "
				+ repositoryName);

		// Init AdminPanel for start checking
		initAdminPanelForStartCheck();

		// Set checking state
		repositoryService.setChecking(repositoryName, true);

		this.adminPanel.getCheckProgressBar().setIndeterminate(true);

		try {
			// 1. Try to retrieve the remote repository sync, serverInfo,
			// changelogs, autoconfig files.
			connexionService = ConnexionServiceFactory
					.getServiceForRepositoryManagement(repositoryName);
			connexionService.getSync(repositoryName);
			connexionService.getServerInfo(repositoryName);
			connexionService.getChangelogs(repositoryName);
			connexionService.getAutoconfig(repositoryName);

			if (repositoryService.getSync(repositoryName) == null) {
				throw new RemoteSyncFileNotFoundException();
			}

			if (repositoryService.getServerInfo(repositoryName) == null) {
				throw new RemoteServerInfoFileNotFoundException();
			}

			if (repositoryService.getChangelogs(repositoryName) == null) {
				throw new RemoteChangelogsFileNotFoundException();
			}

			if (repositoryService.getAutoconfig(repositoryName) == null) {
				throw new RemoteAutoconfigFileNotFoundException();
			}

			connexionService.getConnexionDAO().addObserverCount(
					new ObserverCount() {
						@Override
						public void update(final int value) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									adminPanel.getCheckProgressBar()
											.setIndeterminate(false);
									adminPanel.getCheckProgressBar().setValue(
											value);
								}
							});
						}
					});

			connexionService.getConnexionDAO().addObserverCountErrors(
					new ObserverCount() {
						@Override
						public void update(int value) {
							adminPanel.getCheckErrorLabelValue().setText(
									Integer.toString(value));
							adminPanel.getCheckErrorLabel().setForeground(
									Color.RED);
							adminPanel.getCheckErrorLabelValue().setForeground(
									Color.RED);
						}
					});

			this.errors = connexionService
					.checkRepositoryContent(repositoryName);

			adminPanel.getCheckProgressBar().setIndeterminate(false);

			if (!canceled) {
				this.adminPanel.getCheckProgressBar().setValue(100);
				this.adminPanel.getCheckProgressBar().setString("100%");
				if (errors.isEmpty()) {
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							"Repository is synchronized.", "Check repository",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					ErrorsListDialog dialog = new ErrorsListDialog(facade,
							"Check repository",
							"Check repository finished with errors:", errors,
							repositoryName);
					dialog.show();
				}
				this.adminPanel.init(repositoryName);
				this.facade.getSyncPanel().init();
			}
		} catch (Exception e) {
			this.adminPanel.getCheckProgressBar().setIndeterminate(false);
			if (!canceled) {
				e.printStackTrace();
				if (e instanceof RepositoryException
						|| e instanceof RemoteRepositoryException) {
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							e.getMessage(), "Check repository synchronization",
							JOptionPane.ERROR_MESSAGE);
				} else if (e instanceof IOException) {
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							e.getMessage(), "Check repository synchronization",
							JOptionPane.ERROR_MESSAGE);
				} else {
					UnexpectedErrorDialog dialog = new UnexpectedErrorDialog(
							facade, "Check repository synchronization", e,
							repositoryName);
					dialog.show();
				}
			}
		} finally {
			if (connexionService != null) {
				connexionService.cancel();
			}
			initAdminPanelForEndCheck();
			terminate();
		}
	}

	private void initAdminPanelForStartCheck() {

		this.adminPanel.getButtonSelectRepositoryfolderPath().setEnabled(false);
		this.adminPanel.getButtonBuild().setEnabled(false);
		this.adminPanel.getButtonBuildOptions().setEnabled(false);
		this.adminPanel.getButtonUpload().setEnabled(false);
		this.adminPanel.getButtonUploadOptions().setEnabled(false);
		this.adminPanel.getButtonCopyAutoConfigURL().setEnabled(false);
		this.adminPanel.getButtonCheck().setText("Stop");
		this.adminPanel.getCheckProgressBar().setString(
				"Checking remote files...");
		this.adminPanel.getCheckProgressBar().setStringPainted(true);
		this.adminPanel.getCheckProgressBar().setMaximum(100);
		this.adminPanel.getCheckProgressBar().setMinimum(0);
		this.adminPanel.getCheckErrorLabel().setForeground(
				new Color(45, 125, 45));
		this.adminPanel.getCheckErrorLabelValue().setText("0");
		this.adminPanel.getCheckErrorLabelValue().setForeground(
				new Color(45, 125, 45));
		this.adminPanel.getCheckInformationBox().setVisible(true);
	}

	private void initAdminPanelForEndCheck() {

		this.adminPanel.getButtonSelectRepositoryfolderPath().setEnabled(true);
		this.adminPanel.getButtonBuild().setEnabled(true);
		this.adminPanel.getButtonBuildOptions().setEnabled(true);
		this.adminPanel.getButtonUpload().setEnabled(true);
		this.adminPanel.getButtonUploadOptions().setEnabled(true);
		this.adminPanel.getButtonCopyAutoConfigURL().setEnabled(true);
		this.adminPanel.getButtonCheck().setText("Check");
		this.adminPanel.getCheckProgressBar().setString("");
		this.adminPanel.getCheckProgressBar().setStringPainted(false);
		this.adminPanel.getCheckProgressBar().setMaximum(0);
		this.adminPanel.getCheckProgressBar().setMinimum(0);
		this.adminPanel.getCheckErrorLabel().setForeground(
				new Color(45, 125, 45));
		this.adminPanel.getCheckErrorLabelValue().setText("0");
		this.adminPanel.getCheckErrorLabelValue().setForeground(
				new Color(45, 125, 45));
		this.adminPanel.getCheckErrorLabelValue().setFont(
				this.adminPanel.getCheckErrorLabelValue().getFont()
						.deriveFont(Font.ITALIC));
		this.adminPanel.getCheckInformationBox().setVisible(false);
	}

	private void terminate() {

		repositoryService.setChecking(repositoryName, false);
		this.interrupt();
		System.gc();// Required for unlocking files!
	}

	public void cancel() {

		this.canceled = true;
		adminPanel.getCheckProgressBar().setString("Canceling...");
		if (connexionService != null) {
			connexionService.cancel();
		}
		initAdminPanelForEndCheck();
		terminate();
	}
}
