package fr.soe.a3s.ui.repository.dialogs.error;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.service.CommonService;
import fr.soe.a3s.ui.Facade;

public class ErrorsListDialog implements DataAccessConstants {

	private final Facade facade;
	private final String dialogTitle;
	private final List<Exception> errors;
	private final String mainMessage;
	private final String repositoryName;
	private String reportMessage;

	public ErrorsListDialog(Facade facade, String dialogTitle,
			String mainMessage, List<Exception> errors, String repositoryName) {
		this.facade = facade;
		this.dialogTitle = dialogTitle;
		this.errors = errors;
		this.mainMessage = mainMessage;
		this.repositoryName = repositoryName;

	}

	public void show() {

		List<String> messages = new ArrayList<String>();
		List<String> causes = new ArrayList<String>();

		for (Exception e : errors) {
			if (e instanceof IOException) {
				messages.add(e.getMessage());
			} else {
				String coreMessage = "An unexpected error has occured.";
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				String stacktrace = sw.toString(); // stack trace as a string
				coreMessage = coreMessage + "\n" + "StackTrace:" + "\n"
						+ stacktrace;
				messages.add(coreMessage);
			}
		}

		String message = mainMessage;
		if (messages.size() > 5) {
			for (int i = 0; i < 5; i++) {
				String m = messages.get(i);
				message = message + "\n" + " - " + m;
			}
			message = message + "\n" + "["
					+ Integer.toString(messages.size() - 5) + "] more...";
		} else {
			for (String m : messages) {
				message = message + "\n" + " - " + m;
			}
		}

		message = message + "\n\n"
				+ "Do you want export the errors log file to desktop ("
				+ LOG_FILE_NAME + ")?" + "\n\n";

		int value = JOptionPane.showConfirmDialog(facade.getMainPanel(),
				message, dialogTitle, 0, JOptionPane.ERROR_MESSAGE);

		// Report
		String title = dialogTitle + " "
				+ "finished with errors for repository name: " + repositoryName;

		String coreMessage = "";
		for (String m : messages) {
			coreMessage = coreMessage + "\n" + " - " + m;
		}

		this.reportMessage = title + coreMessage;

		if (value == 0) {
			try {
				CommonService commonService = new CommonService();
				commonService
						.exportToDesktop(this.reportMessage, LOG_FILE_NAME);
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

	public String getReportMessage() {
		return this.reportMessage;
	}
}
