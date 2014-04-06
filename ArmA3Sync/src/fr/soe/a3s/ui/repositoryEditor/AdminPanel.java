package fr.soe.a3s.ui.repositoryEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import fr.soe.a3s.constant.RepositoryStatus;
import fr.soe.a3s.dto.ChangelogDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.dto.ServerInfoDTO;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.repositoryEditor.workers.RepositoryBuilder;
import fr.soe.a3s.ui.repositoryEditor.workers.RepositoryChecker;

public class AdminPanel extends JPanel implements UIConstants {

	private JLabel labelRevision, labelRevisionValue;
	private JLabel labelDate, labelDateValue;
	private JLabel labelStatus, labelStatusValue;
	private JLabel labelNbFiles, labelNbFilesValue;
	private JLabel labelTotalSize, labelTotalSizeValue;
	private JLabel labelChangelog;
	private JButton buttonView;
	private final Facade facade;
	private final RepositoryPanel repositoryPanel;
	private JButton buttonSelectFTPfolderPath, buttonBuild,
			buttonCopyAutoConfigURL, buttonCheck;
	private final RepositoryService repositoryService = new RepositoryService();
	private String repositoryName;
	private JTextField textFieldftpSharedFolderLocation,
			textFieldAutoConfigURL;
	private JProgressBar buildProgressBar, checkProgressBar;

	public AdminPanel(Facade facade, RepositoryPanel repositoryPanel) {

		this.facade = facade;
		this.repositoryPanel = repositoryPanel;
		setLayout(new BorderLayout());

		Box vertBox1 = Box.createVerticalBox();
		vertBox1.add(Box.createVerticalStrut(5));

		JPanel centerPanel = new JPanel();
		GridLayout grid1 = new GridLayout(2, 1);
		centerPanel.setLayout(grid1);
		vertBox1.add(centerPanel);
		this.add(vertBox1, BorderLayout.CENTER);

		JPanel repositoryInfoPanel = new JPanel();
		repositoryInfoPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Infos"));
		centerPanel.add(repositoryInfoPanel, BorderLayout.NORTH);

		repositoryInfoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		Box vBox = Box.createVerticalBox();
		repositoryInfoPanel.add(vBox);
		{
			labelRevision = new JLabel("Revision: ");
			labelRevisionValue = new JLabel();
			Box hBox = Box.createHorizontalBox();
			hBox.add(labelRevision);
			hBox.add(labelRevisionValue);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
			vBox.add(Box.createVerticalStrut(10));
		}
		{
			labelDate = new JLabel("Build date: ");
			labelDateValue = new JLabel();
			Box hBox = Box.createHorizontalBox();
			hBox.add(labelDate);
			hBox.add(labelDateValue);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
			vBox.add(Box.createVerticalStrut(10));
		}
		{
			labelStatus = new JLabel("Status: ");
			labelStatusValue = new JLabel();
			Box hBox = Box.createHorizontalBox();
			hBox.add(labelStatus);
			hBox.add(labelStatusValue);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
			vBox.add(Box.createVerticalStrut(10));
		}
		{
			labelNbFiles = new JLabel("Number of files: ");
			labelNbFilesValue = new JLabel();
			Box hBox = Box.createHorizontalBox();
			hBox.add(labelNbFiles);
			hBox.add(labelNbFilesValue);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
			vBox.add(Box.createVerticalStrut(10));
		}
		{
			labelTotalSize = new JLabel("Total files size: ");
			labelTotalSizeValue = new JLabel();
			Box hBox = Box.createHorizontalBox();
			hBox.add(labelTotalSize);
			hBox.add(labelTotalSizeValue);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
			vBox.add(Box.createVerticalStrut(5));
		}
		{
			labelChangelog = new JLabel("Changelog: ");
			buttonView = new JButton("View");
			Box hBox = Box.createHorizontalBox();
			hBox.add(labelChangelog);
			hBox.add(buttonView);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
			vBox.add(Box.createVerticalStrut(5));
		}

		JPanel repositoryAdministrationPanel = new JPanel();
		repositoryAdministrationPanel.setBorder(BorderFactory
				.createTitledBorder(BorderFactory.createEtchedBorder(),
						"Administration"));
		centerPanel.add(repositoryAdministrationPanel, BorderLayout.CENTER);
		repositoryAdministrationPanel.setLayout(new BorderLayout());
		vBox = Box.createVerticalBox();
		repositoryAdministrationPanel.add(vBox, BorderLayout.NORTH);
		{
			JPanel locationLabelPanel = new JPanel();
			locationLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel labelFtpSharedFolderLocation = new JLabel(
					"Repository main folder location");
			locationLabelPanel.add(labelFtpSharedFolderLocation);
			vBox.add(locationLabelPanel);
		}
		{
			JPanel locationPanel = new JPanel();
			locationPanel.setLayout(new BorderLayout());
			textFieldftpSharedFolderLocation = new JTextField();
			buttonSelectFTPfolderPath = new JButton("Select");
			buttonSelectFTPfolderPath.setPreferredSize(new Dimension(75, 25));
			textFieldftpSharedFolderLocation.setEditable(false);
			textFieldftpSharedFolderLocation.setBackground(Color.WHITE);
			locationPanel.add(textFieldftpSharedFolderLocation,
					BorderLayout.CENTER);
			locationPanel.add(buttonSelectFTPfolderPath, BorderLayout.EAST);
			vBox.add(locationPanel);
		}
		vBox.add(Box.createVerticalStrut(5));
		{
			JPanel buildLabelPanel = new JPanel();
			buildLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel buildLabelLocation = new JLabel("Build or update repository");
			buildLabelPanel.add(buildLabelLocation);
			vBox.add(buildLabelPanel);
		}
		{
			JPanel buildPanel = new JPanel();
			buildPanel.setLayout(new BorderLayout());
			buildProgressBar = new JProgressBar();
			buttonBuild = new JButton("Build");
			buttonBuild.setPreferredSize(new Dimension(75, 25));
			buildPanel.add(buildProgressBar, BorderLayout.CENTER);
			buildPanel.add(buttonBuild, BorderLayout.EAST);
			vBox.add(buildPanel);
		}
		vBox.add(Box.createVerticalStrut(5));
		{
			JPanel autoConfigLabelPanel = new JPanel();
			autoConfigLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel autoConfigLabelLocation = new JLabel(
					"Repository auto-config url");
			autoConfigLabelPanel.add(autoConfigLabelLocation);
			vBox.add(autoConfigLabelPanel);
		}
		{
			JPanel autoConfigURLPanel = new JPanel();
			autoConfigURLPanel.setLayout(new BorderLayout());
			textFieldAutoConfigURL = new JTextField();
			buttonCopyAutoConfigURL = new JButton("Copy");
			buttonCopyAutoConfigURL.setPreferredSize(new Dimension(75, 25));
			textFieldAutoConfigURL.setEditable(false);
			textFieldAutoConfigURL.setBackground(Color.WHITE);
			autoConfigURLPanel.add(textFieldAutoConfigURL, BorderLayout.CENTER);
			autoConfigURLPanel.add(buttonCopyAutoConfigURL, BorderLayout.EAST);
			vBox.add(autoConfigURLPanel);
		}
		vBox.add(Box.createVerticalStrut(5));
		{
			JPanel checkLabelPanel = new JPanel();
			checkLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel buildLabelLocation = new JLabel(
					"Check repository synchronization");
			checkLabelPanel.add(buildLabelLocation);
			vBox.add(checkLabelPanel);
		}
		{
			JPanel checkPanel = new JPanel();
			checkPanel.setLayout(new BorderLayout());
			checkProgressBar = new JProgressBar();
			buttonCheck = new JButton("Check");
			buttonCheck.setPreferredSize(new Dimension(75, 25));
			checkPanel.add(checkProgressBar, BorderLayout.CENTER);
			checkPanel.add(buttonCheck, BorderLayout.EAST);
			vBox.add(checkPanel);
		}
		vBox.add(Box.createVerticalStrut(3));

		buttonSelectFTPfolderPath.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonSelectFTPfolderPathPerformed();
			}
		});
		buttonBuild.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						buttonBuildPerformed();
					}
				});
			}
		});
		buttonCopyAutoConfigURL.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonCopyAutoConfigPerformed();
			}
		});
		buttonCheck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonCheckPerformed();
			}
		});
		buttonView.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonViewPerformed();
			}
		});
		setContextualHelp();

	}

	private void setContextualHelp() {
		buttonBuild.setToolTipText("Build repository");
	}

	public void init(String repositoryName) {

		this.repositoryName = repositoryName;
		try {
			RepositoryDTO repositoryDTO = repositoryService
					.getRepository(repositoryName);
			ServerInfoDTO serverInfoDTO = repositoryService
					.getServerInfo(repositoryName);

			textFieldftpSharedFolderLocation.setText(repositoryDTO.getPath());

			if (repositoryDTO.getAutoConfigURL() != null) {
				textFieldAutoConfigURL.setText(repositoryDTO.getProtocoleDTO()
						.getProtocole().getPrompt()
						+ repositoryDTO.getAutoConfigURL());
			}

			if (repositoryDTO.isOutOfSynk()) {
				labelStatusValue.setText(RepositoryStatus.OUTOFSYNC
						.getDescription());
				labelStatusValue.setFont(new Font("Tohama", Font.BOLD, 11));
				labelStatusValue.setForeground(Color.RED);
			} else if (serverInfoDTO != null) {
				labelRevisionValue.setText(Integer.toString(serverInfoDTO
						.getRevision()));
				labelDateValue.setText(serverInfoDTO.getBuildDate()
						.toLocaleString());
				labelNbFilesValue.setText(Long.toString(serverInfoDTO
						.getNumberOfFiles()));
				long size = serverInfoDTO.getTotalFilesSize();
				labelTotalSizeValue.setText(UnitConverter.convertSize(size));
				if (repositoryDTO.getRevision() == serverInfoDTO.getRevision()) {
					labelStatusValue.setText(RepositoryStatus.OK
							.getDescription());
					labelStatusValue.setFont(new Font("Tohama", Font.BOLD, 11));
					labelStatusValue.setForeground(new Color(45, 125, 45));
				} else {
					labelStatusValue.setText(RepositoryStatus.UPDATED
							.getDescription());
					labelStatusValue.setFont(new Font("Tohama", Font.BOLD, 11));
					labelStatusValue.setForeground(Color.RED);
				}
			} else {
				labelStatusValue.setText(RepositoryStatus.INDETERMINATED
						.getDescription());
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void buttonSelectFTPfolderPathPerformed() {

		RepositoryDTO repositoryDTO = null;
		try {
			repositoryDTO = repositoryService.getRepository(repositoryName);
		} catch (RepositoryException e) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		assert (repositoryDTO != null);

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(this.repositoryPanel);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			/* Check consistency between repository url and ftp folder path */
			String ftpFolderName = file.getName();
			String url = repositoryDTO.getProtocoleDTO().getUrl();
			if (url.endsWith("/")) {// there is / at the end of url
				int lastIndex = url.lastIndexOf("/");
				if (lastIndex != -1) {
					url = url.substring(0, lastIndex);
				}
			}
			int index = url.lastIndexOf("/");
			if (index != -1) {// There is a folder after the root
				String folderName = url.substring(index + 1);
				if (!ftpFolderName.equalsIgnoreCase(folderName)) {
					JOptionPane
							.showMessageDialog(
									facade.getMainPanel(),
									"The selected FTP shared folder "
											+ file.getName()
											+ "\n does not correspond with the repository url: \n"
											+ url, "Build repository",
									JOptionPane.WARNING_MESSAGE);
					textFieldftpSharedFolderLocation.setText("");
					textFieldAutoConfigURL.setText("");
				} else {
					textFieldftpSharedFolderLocation.setText(file
							.getAbsolutePath());
				}
			} else {
				textFieldftpSharedFolderLocation
						.setText(file.getAbsolutePath());
			}
		} else {
			textFieldftpSharedFolderLocation.setText("");
			textFieldAutoConfigURL.setText("");
		}

		try {
			repositoryService.setRepositoryPath(repositoryName,
					textFieldftpSharedFolderLocation.getText().trim());
			if (textFieldAutoConfigURL.getText().isEmpty()) {
				repositoryService.setAutoConfigURL(repositoryName, null);
			}
			repositoryService.write(repositoryName);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void buttonBuildPerformed() {

		final String path = textFieldftpSharedFolderLocation.getText().trim();

		if (path.isEmpty()) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Repository main folder location is empty!",
					"Build repository", JOptionPane.WARNING_MESSAGE);
			return;
		} else if (!(new File(path).exists())) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Repository main folder does not exists!",
					"Build repository", JOptionPane.WARNING_MESSAGE);
			return;
		}

		RepositoryBuilder repositoryBuilder = new RepositoryBuilder(facade,
				repositoryName, path, this);
		repositoryBuilder.start();
	}

	private void buttonCopyAutoConfigPerformed() {

		try {
			StringSelection ss = new StringSelection(
					textFieldAutoConfigURL.getText());
			Toolkit.getDefaultToolkit().getSystemClipboard()
					.setContents(ss, null);
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Auto-config url copied to clipboard.", "Auto-config",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			// Clipboard may not be available (Windows).
		}
	}

	private void buttonCheckPerformed() {

		final String path = textFieldftpSharedFolderLocation.getText().trim();

		if (path.isEmpty()) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Repository main folder location is empty!",
					"Check repository", JOptionPane.WARNING_MESSAGE);
			return;
		} else if (!(new File(path).exists())) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Repository main folder does not exists!",
					"Check repository", JOptionPane.WARNING_MESSAGE);
			return;
		}

		RepositoryChecker checker = new RepositoryChecker(facade,
				repositoryName, path, this);
		checker.start();
	}

	private void buttonViewPerformed() {

		ChangelogPanel changelogPanel = new ChangelogPanel(facade,
				repositoryName, this);
		changelogPanel.init();
		changelogPanel.setVisible(true);
	}

	public JProgressBar getBuildProgressBar() {
		return buildProgressBar;
	}

	public JButton getButtonBuild() {
		return buttonBuild;
	}

	public JButton getButtonCheck() {
		return buttonCheck;
	}

	public JProgressBar getCheckProgressBar() {
		return checkProgressBar;
	}

	public JButton getButtonSelectRepositoryfolderPath() {
		return buttonSelectFTPfolderPath;
	}

	public JButton getButtonCopyAutoConfigURL() {
		return buttonCopyAutoConfigURL;
	}

	public JButton getButtonView() {
		return buttonView;
	}

	public RepositoryPanel getRepositoryPanel() {
		return repositoryPanel;
	}
}
