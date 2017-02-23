package fr.soe.a3s.ui.repository.workers;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import fr.soe.a3s.dao.FileAccessMethods;
import fr.soe.a3s.service.ProfileService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.service.synchronization.FilesSynchronizationManager;
import fr.soe.a3s.ui.Facade;

public class UserconfigUpdater {

	private final Facade facade;
	private final String repositoryName;
	private final boolean silent;
	/* Services */
	private final RepositoryService repositoryService = new RepositoryService();
	private final ProfileService profileService = new ProfileService();
	private final FilesSynchronizationManager filesManager;

	public UserconfigUpdater(Facade facade, String repositoryName,
			boolean silent, FilesSynchronizationManager filesManager) {
		this.facade = facade;
		this.repositoryName = repositoryName;
		this.silent = silent;
		this.filesManager = filesManager;
	}

	public void run() {

		boolean proceed = false;

		String defaultDownloadLocation = repositoryService
				.getDefaultDownloadLocation(repositoryName);

		File arma3Directory = null;
		String arma3ExePath = profileService.getArma3ExePath();
		if (arma3ExePath != null) {
			if (!arma3ExePath.isEmpty()) {
				arma3Directory = new File(arma3ExePath).getParentFile();
			}
		}

		if (arma3Directory != null) {
			if (!arma3Directory.getAbsolutePath().equals(
					defaultDownloadLocation)) {
				if (!silent) {
					String message = "Repository name: "
							+ repositoryName
							+ "\n"
							+ "Userconfig folder have changed."
							+ "\n"
							+ "Copy userconfig folder into ArmA 3 installation directory?";
					int response = JOptionPane.showConfirmDialog(
							facade.getMainPanel(), message, "Userconfig",
							JOptionPane.OK_CANCEL_OPTION);
					if (response == 0) {
						proceed = true;
					}
				} else {
					proceed = true;
				}
			}
		}

		if (proceed) {
			String userconfigSourcePath = filesManager.getUserconfigNode()
					.getDestinationPath()
					+ "/"
					+ filesManager.getUserconfigNode().getName();
			File userconfigSourceDirectory = new File(userconfigSourcePath);

			String userconfigTargetPath = arma3Directory + "/"
					+ filesManager.getUserconfigNode().getName();
			File userconfigTargetDirectory = new File(userconfigTargetPath);

			if (!userconfigSourceDirectory.exists() && !silent) {
				String message = "File not found:" + userconfigSourcePath;
				JOptionPane.showMessageDialog(facade.getMainPanel(), message,
						"Error", JOptionPane.ERROR_MESSAGE);
			} else {
				userconfigTargetDirectory.mkdir();
				if (!userconfigTargetDirectory.exists() && !silent) {
					String message = "Failed to create directory: "
							+ userconfigTargetPath + "\n"
							+ "Please checkout file access permissions.";
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							message, "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					try {
						FileAccessMethods.copyDirectory(
								userconfigSourceDirectory,
								userconfigTargetDirectory);

						if (!silent) {
							String message = "Userconfig folder have been updated into ArmA 3 installation directory.";
							JOptionPane.showMessageDialog(
									facade.getMainPanel(), message,
									"Userconfig",
									JOptionPane.INFORMATION_MESSAGE);
						}
					} catch (IOException e) {
						e.printStackTrace();
						if (!silent) {
							JOptionPane.showMessageDialog(
									facade.getMainPanel(), e.getMessage(),
									"Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		}
	}
}
