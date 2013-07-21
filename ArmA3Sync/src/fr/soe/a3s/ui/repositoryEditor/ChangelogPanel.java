package fr.soe.a3s.ui.repositoryEditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.ScrollPane;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.domain.repository.Changelogs;
import fr.soe.a3s.dto.ChangelogDTO;
import fr.soe.a3s.exception.FtpException;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.service.FtpService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

public class ChangelogPanel extends JFrame implements UIConstants {

	private Facade facade;
	private JButton buttonSwitch;
	private JButton buttonCopy;
	private JButton buttonClose;
	private static final String LAST_BUILD = "Last build";
	private static final String TEN_LAST_BUILD = "10 last builds";
	private JTextArea textArea;
	private JScrollPane scrollpane;
	private String repositoryName;
	private RepositoryService repositoryService = new RepositoryService();
	private List<ChangelogDTO> changelogDTOs;

	public ChangelogPanel(Facade facade, String repositoryName) {
		this.facade = facade;
		this.repositoryName = repositoryName;
		this.setTitle("Changelog");
		this.setMinimumSize(new Dimension(400, 400));
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
			JPanel controlPanel = new JPanel();
			FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
			controlPanel.setLayout(flowLayout);
			buttonSwitch = new JButton(TEN_LAST_BUILD);
			buttonCopy = new JButton("Copy");
			buttonClose = new JButton("Close");
			controlPanel.add(buttonSwitch);
			controlPanel.add(buttonCopy);
			controlPanel.add(buttonClose);
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
		buttonSwitch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonSwitchPerformed();
			}
		});
		buttonCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonCopyPerformed();
			}
		});
		buttonClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonClosePerformed();
			}
		});
		// Add Listeners
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				menuExitPerformed();
			}
		});

		getRootPane().setDefaultButton(buttonClose);
	}

	public void init() {

		try {
			facade.getAdminPanel().getButtonView().setEnabled(false);
			changelogDTOs = repositoryService.getChangelogs(repositoryName);
			if (changelogDTOs == null || changelogDTOs.size() == 0) {
				String message = "Changelog is not available.";
				textArea.setText(message);
			} else {
				display(changelogDTOs.get(changelogDTOs.size() - 1));
			}
			textArea.setRows(1);
			textArea.setCaretPosition(0);
		} catch (RepositoryException e) {
			String message = "Changelog is not available.";
			textArea.setText(message);
			textArea.setRows(1);
			textArea.setCaretPosition(0);
			JOptionPane.showMessageDialog(facade.getMainPanel(), e.getMessage(),
					"Repository", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void buttonSwitchPerformed() {

		if (changelogDTOs == null || changelogDTOs.isEmpty()) {
			return;
		}

		textArea.setText("");

		if (buttonSwitch.getText().equals(LAST_BUILD)) {
			display(changelogDTOs.get(changelogDTOs.size() - 1));
			buttonSwitch.setText(TEN_LAST_BUILD);
		} else if (buttonSwitch.getText().equals(TEN_LAST_BUILD)) {
			buttonSwitch.setText(LAST_BUILD);
			for (ChangelogDTO changelogDTO : changelogDTOs) {
				display(changelogDTO);
				textArea.append("\n\n");
			}
		}
		textArea.setRows(1);
		textArea.setCaretPosition(0);
	}

	private void buttonCopyPerformed() {

		try {
			StringSelection ss = new StringSelection(textArea.getText());
			Toolkit.getDefaultToolkit().getSystemClipboard()
					.setContents(ss, null);
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Changelog copied to clipboard.", "Changelog",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			// Clipboard may not be available (Windows).
		}
	}

	private void display(ChangelogDTO changelogDTO) {

		textArea.append("--- Revision: " + changelogDTO.getRevision()+ " ---");
		textArea.append("\nBuild date: "
				+ changelogDTO.getBuildDate().toLocaleString());
		textArea.append("\n");
		textArea.append("\nNew: " + changelogDTO.getNewAddons().size() + "\n");
		if (changelogDTO.getNewAddons().isEmpty()) {
			textArea.append("-");
		} else {
			for (String stg : changelogDTO.getNewAddons()) {
				textArea.append(stg + ";");
			}
		}
		textArea.append("\n");
		textArea.append("\nUpdated: " + changelogDTO.getUpdatedAddons().size() + "\n");
		if (changelogDTO.getUpdatedAddons().isEmpty()) {
			textArea.append("-");
		} else {
			for (String stg : changelogDTO.getUpdatedAddons()) {
				textArea.append(stg + ";");
			}
		}
		textArea.append("\n");
		textArea.append("\nDeleted: " + changelogDTO.getDeletedAddons().size() + "\n");
		if (changelogDTO.getDeletedAddons().isEmpty()) {
			textArea.append("-");
		} else {
			for (String stg : changelogDTO.getDeletedAddons()) {
				textArea.append(stg + ";");
			}
		}
	}

	private void buttonClosePerformed() {
		menuExitPerformed();
	}

	private void menuExitPerformed() {
		this.dispose();
		facade.getAdminPanel().getButtonView().setEnabled(true);
	}
}
