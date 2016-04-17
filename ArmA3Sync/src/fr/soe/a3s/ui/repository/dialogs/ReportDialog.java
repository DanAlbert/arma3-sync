package fr.soe.a3s.ui.repository.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.service.CommonService;
import fr.soe.a3s.ui.AbstractDialog;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repository.DownloadPanel;

public class ReportDialog extends AbstractDialog implements DataAccessConstants {

	private final DownloadPanel downloadPanel;
	private JTextArea textArea;
	// Data
	private final String repositoryName;
	private String downloadReport;

	public ReportDialog(Facade facade, String repositoryName,
			DownloadPanel downloadPanel) {
		super(facade, "Download", true);
		this.downloadPanel = downloadPanel;
		this.repositoryName = repositoryName;

		{
			buttonOK.setText("Export");
			buttonCancel.setText("Close");
			getRootPane().setDefaultButton(buttonCancel);
			buttonCancel.setPreferredSize(buttonOK.getPreferredSize());
		}
		{
			textArea = new JTextArea();
			textArea.setLineWrap(true);
			textArea.setEditable(false);
			JScrollPane scrollpane = new JScrollPane();
			scrollpane.setViewportView(textArea);
			scrollpane.setBorder(BorderFactory
					.createEtchedBorder(BevelBorder.LOWERED));
			this.add(scrollpane, BorderLayout.CENTER);
		}

		this.pack();
	}

	public void init(String downloadReport) {

		this.downloadReport = downloadReport;
		this.downloadPanel.getButtonDownloadReport().setEnabled(false);
		if (downloadReport == null) {
			this.setMinimumSize(new Dimension(300, 300));
			String message = "Download report is not available.";
			textArea.setText(message);
			Font fontTextField = UIManager.getFont("Label.font");
			textArea.setFont(fontTextField.deriveFont(Font.ITALIC));
			buttonOK.setEnabled(false);
		} else {
			this.setMinimumSize(new Dimension(500, 550));
			String message = downloadReport;
			textArea.setText(message);
			Font fontTextField = UIManager.getFont("TextField.font");
			textArea.setFont(fontTextField);
			if (textArea.getFont().getSize() < 12) {
				textArea.setFont(textArea.getFont().deriveFont(new Float(12)));
			}
			textArea.setCaretPosition(0);
			buttonOK.setEnabled(true);
		}
		this.setLocationRelativeTo(facade.getMainPanel());
	}

	@Override
	protected void buttonOKPerformed() {
		try {
			CommonService commonService = new CommonService();
			commonService.exportToDesktop(downloadReport, LOG_FILE_NAME);
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Log file has been exported to desktop", "Download",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(
					facade.getMainPanel(),
					"Failed to export log file to desktop" + "\n"
							+ e1.getMessage(), "Download",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected void buttonCancelPerformed() {
		this.dispose();
		this.downloadPanel.getButtonDownloadReport().setEnabled(true);
	}

	@Override
	protected void menuExitPerformed() {
		this.dispose();
		this.downloadPanel.getButtonDownloadReport().setEnabled(true);
	}
}
