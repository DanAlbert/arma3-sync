package fr.soe.a3s.ui.tools.rptEditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.mainEditor.LauncherOptionsPanel;

public class RptViewerPanel extends JFrame implements UIConstants {

	private Facade facade;
	private JMenuBar menuBar;
	private JMenu menuFile;
	private JMenuItem menuItemSelectRPT, menuItemExit;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private Tail tail;

	public RptViewerPanel(Facade facade) {
		this.facade = facade;
		this.setTitle("RPT Viewer");
		this.setResizable(true);
		this.setMinimumSize(new Dimension(800, 300));
		setIconImage(ICON);
		this.setLocation(
				(int) facade.getMainPanel().getLocation().getX()
						+ facade.getMainPanel().getWidth() / 2
						- this.getWidth() / 2,
				(int) facade.getMainPanel().getLocation().getY()
						+ facade.getMainPanel().getHeight() / 2
						- this.getHeight() / 2);
		this.setLayout(new BorderLayout());
		{
			menuBar = new JMenuBar();
			menuFile = new JMenu("File");
			menuItemSelectRPT = new JMenuItem("Select RPT");
			menuItemExit = new JMenuItem("Exit");
			menuBar.add(menuFile);
			menuFile.add(menuItemSelectRPT);
			menuFile.add(menuItemExit);
			setJMenuBar(menuBar);

			textArea = new JTextArea();
			textArea.setFont(new Font("Courier New", Font.ROMAN_BASELINE, 13));
			textArea.setLineWrap(true);
			textArea.setEditable(false);
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(textArea);
			scrollPane.setBorder(BorderFactory
					.createEtchedBorder(BevelBorder.LOWERED));
			this.add(scrollPane, BorderLayout.CENTER);
		}
		menuItemSelectRPT.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menuItemSelectRPTPerformed();
			}
		});
		menuItemExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuItemExitPerformed();
			}
		});
		// Add Listeners
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				menuItemExitPerformed();
			}
		});
	}

	private void menuItemSelectRPTPerformed() {

		ConfigurationService configurationService = new ConfigurationService();
		
		String arma3RPTfolderPath = configurationService.getRptPath();

		JFileChooser fc = new JFileChooser();
		if (arma3RPTfolderPath != null) {
			File file = new File(arma3RPTfolderPath);
			if (file.exists()) {
				fc = new JFileChooser(arma3RPTfolderPath);
			}
		}
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showOpenDialog(RptViewerPanel.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File rpt = fc.getSelectedFile();
			configurationService.setRptPath(rpt.getAbsolutePath());
			if (rpt != null) {
				this.setTitle("RPT Viewer - Reading " + rpt.getName());
				tail = new Tail(rpt, textArea);
				tail.start();
			}
		}
	}

	private void menuItemExitPerformed() {
		if (tail != null) {
			tail.stop();
		}
		this.dispose();
	}
}
