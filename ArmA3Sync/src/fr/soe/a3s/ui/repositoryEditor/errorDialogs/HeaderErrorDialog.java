package fr.soe.a3s.ui.repositoryEditor.errorDialogs;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;

public class HeaderErrorDialog {

	private final Facade facade;
	private final String dialogTitle;
	private final String header;
	private final String repositoryName;
	private final String FILENAME = "ArmA3Sync-log.txt";
	/* Services */
	private final RepositoryService repositoryService = new RepositoryService();

	public HeaderErrorDialog(Facade facade, String dialogTitle, String header,
			String repositoryName) {
		this.facade = facade;
		this.dialogTitle = dialogTitle;
		this.header = header;
		this.repositoryName = repositoryName;
	}

	public void show() {

		// Dialog message
		String dialogMessage = "The server appears to not support partial file transfer."
				+ "\n"
				+ "Partial file transfer feature can be disabled from repository build options."
				+ "\n\n"
				+ "Do you want export the errors log file to desktop ("
				+ FILENAME + ")?" + "\n\n";

		int value = JOptionPane.showConfirmDialog(facade.getMainPanel(),
				dialogMessage, dialogTitle, 0, JOptionPane.WARNING_MESSAGE);

		if (value == 0) {
			// Title
			String title = dialogTitle
					+ " "
					+ "finished with errors for repository name: "
					+ repositoryName
					+ "\n"
					+ "The server appears to not support partial file transfer.";

			// Export message
			String exportMessage = title + "\n\n"
					+ "Server response header for range request is:" + "\n"
					+ header;

			try {
				repositoryService.exportToDesktop(exportMessage, FILENAME);
				JOptionPane.showMessageDialog(facade.getMainPanel(),
						"Log file has been exported to desktop", dialogTitle,
						JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(
						facade.getMainPanel(),
						"Failed to export log file to desktop" + "\n"
								+ e1.getMessage(), dialogTitle,
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
