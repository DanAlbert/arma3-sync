package fr.soe.a3s.ui.repositoryEditor.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.service.CommonService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.repositoryEditor.DownloadPanel;

public class ReportPanel extends JFrame implements UIConstants,
		DataAccessConstants {

	private final Facade facade;
	private final DownloadPanel downloadPanel;
	private JButton buttonExport;
	private JButton buttonOK;
	private JTextArea textArea;
	private JScrollPane scrollpane;
	// Data
	private final String repositoryName;
	private String downloadReport;

	public ReportPanel(Facade facade, String repositoryName,
			DownloadPanel downloadPanel) {

		this.facade = facade;
		this.downloadPanel = downloadPanel;
		this.repositoryName = repositoryName;
		this.setTitle("Download");
		setIconImage(ICON);

		this.setLayout(new BorderLayout());
		{
			JPanel controlPanel = new JPanel();
			FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
			controlPanel.setLayout(flowLayout);
			buttonOK = new JButton("OK");
			buttonOK.setPreferredSize(new Dimension(75, 25));
			buttonExport = new JButton("Export");
			buttonExport.setPreferredSize(new Dimension(75, 25));
			controlPanel.add(buttonExport);
			controlPanel.add(buttonOK);
			this.add(controlPanel, BorderLayout.SOUTH);
		}
		{
			JPanel northPanel = new JPanel();
			this.add(northPanel, BorderLayout.NORTH);
			JPanel eastPanel = new JPanel();
			this.add(eastPanel, BorderLayout.EAST);
			JPanel westPanel = new JPanel();
			this.add(westPanel, BorderLayout.WEST);
		}
		{
			textArea = new JTextArea();
			textArea.setFont(new Font("Tohama", Font.PLAIN, 12));
			textArea.setLineWrap(true);
			textArea.setEditable(false);
			scrollpane = new JScrollPane();
			scrollpane.setViewportView(textArea);
			scrollpane.setBorder(BorderFactory
					.createEtchedBorder(BevelBorder.LOWERED));
			this.add(scrollpane, BorderLayout.CENTER);
		}
		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonOKPerformed();
			}
		});
		buttonExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonExportPerformed();
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

	public void init(String downloadReport) {

		this.downloadReport = downloadReport;
		this.downloadPanel.getButtonDownloadReport().setEnabled(false);
		if (downloadReport == null) {
			this.setMinimumSize(new Dimension(300, 300));
			String message = "Report is not available.";
			textArea.setText(message);
			buttonExport.setEnabled(false);
		} else {
			this.setMinimumSize(new Dimension(500, 550));
			String message = downloadReport;
			textArea.setText(message);
			textArea.setCaretPosition(0);
			buttonExport.setEnabled(true);
		}
		this.setLocation(
				(int) facade.getMainPanel().getLocation().getX()
						+ facade.getMainPanel().getWidth() / 2
						- this.getWidth() / 2,
				(int) facade.getMainPanel().getLocation().getY()
						+ facade.getMainPanel().getHeight() / 2
						- this.getHeight() / 2);
	}

	private void buttonExportPerformed() {

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

	private void buttonOKPerformed() {
		menuExitPerformed();
	}

	private void menuExitPerformed() {
		this.dispose();
		this.downloadPanel.getButtonDownloadReport().setEnabled(true);
	}
}
