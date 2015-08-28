package fr.soe.a3s.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.main.Version;
import fr.soe.a3s.service.CommonService;

public class ErrorLogDialog implements DataAccessConstants {

	private final Facade facade;
	private final Exception e;

	public ErrorLogDialog(Facade facade, Exception e) {
		this.facade = facade;
		this.e = e;
	}

	public void show() {

		// Dialog message
		String dialogMessage = "An unexpected error has occured." + "\n\n"
				+ "Do you want export the errors log file to desktop ("
				+ LOG_FILE_NAME + ")?" + "\n\n";

		int value = JOptionPane.showConfirmDialog(facade.getMainPanel(),
				dialogMessage, "Error", 0, JOptionPane.ERROR_MESSAGE);

		if (value == 0) {
			// Title
			String title = "An unexpected error has occured.";

			// ArmA3Sync installed version
			String arma3SyncInstalledVersion = "ArmA3Sync Installed version = "
					+ Version.getVersion();

			// JRE installed version
			String jreInstalledVersion = "JRE installed version = "
					+ System.getProperty("java.version");

			// OS name
			String osName = "OS Name = " + System.getProperty("os.name");

			// Stacktrace as a string
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String stacktrace = sw.toString();

			// Export message
			String message = title + "\n\n" + arma3SyncInstalledVersion + "\n"
					+ jreInstalledVersion + "\n" + osName + "\n\n"
					+ "StackTrace:" + "\n" + stacktrace;

			try {
				CommonService commonService = new CommonService();
				commonService.exportToDesktop(message, LOG_FILE_NAME);
				JOptionPane.showMessageDialog(facade.getMainPanel(),
						"Log file has been exported to desktop", "ArmA3Sync",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(
						facade.getMainPanel(),
						"Failed to export log file to desktop" + "\n"
								+ e1.getMessage(), "ArmA3Sync",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
