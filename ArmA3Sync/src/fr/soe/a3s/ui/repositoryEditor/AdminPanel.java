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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import fr.soe.a3s.constant.RepositoryStatus;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.dto.ServerInfoDTO;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.repositoryEditor.workers.RepositoryBuilder;
import fr.soe.a3s.ui.repositoryEditor.workers.RepositoryChecker;
import fr.soe.a3s.ui.repositoryEditor.workers.RepositoryUploader;

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
	private JTextField textFieldMainSharedFolderLocation,
			textFieldAutoConfigURL;
	private JProgressBar buildProgressBar, checkProgressBar;
	private JButton buttonBuildOptions;
	private JProgressBar uploadrogressBar;
	private JButton buttonUploadOptions;
	private JButton buttonUpload;
	private JLabel uploadSizeLabelValue;

	/* Workers */
	private RepositoryUploader repositoryUploader;
	private JLabel uploadedLabelValue;
	private JLabel uploadSpeedLabelValue;
	private JLabel uploadRemainingTimeValue;
	private Box uploadInformationBox;

	public AdminPanel(Facade facade, RepositoryPanel repositoryPanel) {

		this.facade = facade;
		this.repositoryPanel = repositoryPanel;
		setLayout(new BorderLayout());

		Box vertBox1 = Box.createVerticalBox();
		vertBox1.add(Box.createVerticalStrut(5));
		this.add(vertBox1, BorderLayout.CENTER);

		JPanel centerPanel = new JPanel();
		vertBox1.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

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
			textFieldMainSharedFolderLocation = new JTextField();
			buttonSelectFTPfolderPath = new JButton("Select");
			buttonSelectFTPfolderPath.setPreferredSize(new Dimension(85, 25));
			textFieldMainSharedFolderLocation.setEditable(false);
			textFieldMainSharedFolderLocation.setBackground(Color.WHITE);
			locationPanel.add(textFieldMainSharedFolderLocation,
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
			buttonBuildOptions = new JButton("Options");
			buttonBuildOptions.setPreferredSize(new Dimension(85, 25));
			buttonBuild = new JButton("Build");
			buttonBuild.setPreferredSize(new Dimension(85, 25));
			buildPanel.add(buildProgressBar, BorderLayout.CENTER);
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(1, 2));
			panel.add(buttonBuildOptions);
			panel.add(buttonBuild);
			buildPanel.add(panel, BorderLayout.EAST);
			vBox.add(buildPanel);
		}
		vBox.add(Box.createVerticalStrut(5));
		{
			JPanel uploadLabelPanel = new JPanel();
			uploadLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel uploadLabel = new JLabel("Upload repository");
			uploadLabelPanel.add(uploadLabel);

			uploadInformationBox = Box.createHorizontalBox();
			JLabel uploadSizeLabel = new JLabel("size: ");
			uploadInformationBox.add(uploadSizeLabel);
			uploadSizeLabelValue = new JLabel();
			uploadInformationBox.add(uploadSizeLabelValue);
			JLabel uploadedLabel = new JLabel(", uploaded: ");
			uploadInformationBox.add(uploadedLabel);
			uploadedLabelValue = new JLabel();
			uploadInformationBox.add(uploadedLabelValue);
			JLabel uploadSpeedLabel = new JLabel(", speed: ");
			uploadInformationBox.add(uploadSpeedLabel);
			uploadSpeedLabelValue = new JLabel();
			uploadInformationBox.add(uploadSpeedLabelValue);
			JLabel uploadRemainingTime = new JLabel(", time: ");
			uploadInformationBox.add(uploadRemainingTime);
			uploadRemainingTimeValue = new JLabel();
			uploadInformationBox.add(uploadRemainingTimeValue);
			uploadLabelPanel.add(uploadInformationBox);
			uploadInformationBox.setVisible(false);
			vBox.add(uploadLabelPanel);
		}
		{
			JPanel uploadPanel = new JPanel();
			uploadPanel.setLayout(new BorderLayout());
			uploadrogressBar = new JProgressBar();
			buttonUploadOptions = new JButton("Options");
			buttonUploadOptions.setPreferredSize(new Dimension(85, 25));
			buttonUpload = new JButton("Upload");
			buttonUpload.setPreferredSize(new Dimension(85, 25));
			uploadPanel.add(uploadrogressBar, BorderLayout.CENTER);
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(1, 2));
			panel.add(buttonUploadOptions);
			panel.add(buttonUpload);
			uploadPanel.add(panel, BorderLayout.EAST);
			vBox.add(uploadPanel);
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
			buttonCopyAutoConfigURL.setPreferredSize(new Dimension(85, 25));
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
			buttonCheck.setPreferredSize(new Dimension(85, 25));
			checkPanel.add(checkProgressBar, BorderLayout.CENTER);
			checkPanel.add(buttonCheck, BorderLayout.EAST);
			vBox.add(checkPanel);
		}
		vBox.add(Box.createVerticalStrut(3));

		buttonSelectFTPfolderPath.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonSelectMainfolderPathPerformed();
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
		buttonBuildOptions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonBuildOptionsPerformed();
			}
		});
		buttonUpload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						buttonUploadPerformed();
					}
				});
			}
		});
		buttonUploadOptions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonUploadOptionsPerformed();
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

			textFieldMainSharedFolderLocation.setText(repositoryDTO.getPath());

			if (repositoryDTO.getAutoConfigURL() != null) {
				textFieldAutoConfigURL.setText(repositoryDTO.getProtocoleDTO()
						.getProtocole().getPrompt()
						+ repositoryDTO.getAutoConfigURL());
			}

			RepositoryStatus repositoryStatus = repositoryService
					.getRepositoryStatus(repositoryName);
			updateRepositoryStatus(repositoryStatus);

			if (serverInfoDTO != null) {
				labelRevisionValue.setText(Integer.toString(serverInfoDTO
						.getRevision()));
				labelDateValue.setText(serverInfoDTO.getBuildDate()
						.toLocaleString());
				labelNbFilesValue.setText(Long.toString(serverInfoDTO
						.getNumberOfFiles()));
				long size = serverInfoDTO.getTotalFilesSize();
				labelTotalSizeValue.setText(UnitConverter.convertSize(size));
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void updateRepositoryStatus(RepositoryStatus repositoryStatus) {

		if (repositoryStatus.equals(RepositoryStatus.OUTOFSYNC)) {
			labelStatusValue.setText(RepositoryStatus.OUTOFSYNC
					.getDescription());
			labelStatusValue.setFont(new Font("Tohama", Font.BOLD, 11));
			labelStatusValue.setForeground(Color.RED);
		} else if (repositoryStatus.equals(RepositoryStatus.OK)) {
			labelStatusValue.setText(RepositoryStatus.OK.getDescription());
			labelStatusValue.setFont(new Font("Tohama", Font.BOLD, 11));
			labelStatusValue.setForeground(new Color(45, 125, 45));
		} else if (repositoryStatus.equals(RepositoryStatus.UPDATED)) {
			labelStatusValue.setText(RepositoryStatus.UPDATED.getDescription());
			labelStatusValue.setFont(new Font("Tohama", Font.BOLD, 11));
			labelStatusValue.setForeground(Color.RED);
		} else {
			labelStatusValue.setText(RepositoryStatus.INDETERMINATED
					.getDescription());
			labelStatusValue.setText(RepositoryStatus.INDETERMINATED
					.getDescription());
		}
	}

	private void buttonSelectMainfolderPathPerformed() {

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
			textFieldMainSharedFolderLocation.setText(file.getAbsolutePath());
		} else {
			textFieldMainSharedFolderLocation.setText("");
			textFieldAutoConfigURL.setText("");
		}

		// Save path to repository
		try {
			repositoryService.setRepositoryPath(repositoryName,
					textFieldMainSharedFolderLocation.getText());
		} catch (RepositoryException e) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			textFieldAutoConfigURL.setText("");
		}

		// Reset upload parameters
		// repositoryService.saveTransfertParameters(repositoryName, 0, 0,
		// false);
	}

	@Deprecated
	private void checkRepositoryPath(File file, RepositoryDTO repositoryDTO) {

		/* Check consistency between repository url and main folder path */
		String mainFolderName = file.getName();
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
			if (!mainFolderName.equalsIgnoreCase(folderName)) {
				JOptionPane
						.showMessageDialog(
								facade.getMainPanel(),
								"The selected shared folder "
										+ file.getName()
										+ "\n does not correspond with the repository url: \n"
										+ url, "Build repository",
								JOptionPane.WARNING_MESSAGE);
				textFieldMainSharedFolderLocation.setText("");
				textFieldAutoConfigURL.setText("");
			} else {
				textFieldMainSharedFolderLocation.setText(file
						.getAbsolutePath());
			}
		} else {
			textFieldMainSharedFolderLocation.setText(file.getAbsolutePath());
		}
	}

	private void buttonBuildPerformed() {

		// Cancel repository upload

		final String path = textFieldMainSharedFolderLocation.getText().trim();

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

		// Reset upload parameters
		// repositoryService.saveTransfertParameters(repositoryName, 0, 0,
		// false);

		RepositoryBuilder repositoryBuilder = new RepositoryBuilder(facade,
				repositoryName, path, this);
		repositoryBuilder.start();
	}

	private void buttonBuildOptionsPerformed() {

		// Repository main folder location must be set
		if (textFieldMainSharedFolderLocation.getText().isEmpty()) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Please set the repository main folder location first.",
					"Build repository", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		BuildOptionsPanel buildOptionsPanel = new BuildOptionsPanel(facade,
				repositoryName);
		buildOptionsPanel.init();
		buildOptionsPanel.setVisible(true);
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

	private void buttonUploadPerformed() {

		if (repositoryUploader == null
				|| !repositoryService.isUploading(repositoryName)) {
			String path = textFieldMainSharedFolderLocation.getText();
			// Repository main folder location must be set
			if (path.isEmpty()) {
				JOptionPane
						.showMessageDialog(
								facade.getMainPanel(),
								"Please set the repository main folder location first.",
								"Build repository",
								JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			repositoryUploader = new RepositoryUploader(facade, repositoryName,
					path, this);
			repositoryUploader.setDaemon(true);
			repositoryUploader.start();
		} else if (repositoryUploader != null
				&& repositoryService.isUploading(repositoryName)) {
			repositoryUploader.cancel();
			repositoryUploader.interrupt();
			repositoryUploader = null;
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Repository upload stopped.", "Repository upload",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void buttonUploadOptionsPerformed() {

		UploadRepositoryOptionsPanel uploadRepositoryOptionsPanel = new UploadRepositoryOptionsPanel(
				facade);
		uploadRepositoryOptionsPanel.init(repositoryName);
		uploadRepositoryOptionsPanel.setVisible(true);
	}

	private void buttonCheckPerformed() {

		final String path = textFieldMainSharedFolderLocation.getText().trim();

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

	public JButton getButtonBuildOptions() {
		return buttonBuildOptions;
	}

	public JButton getButtonUpload() {
		return buttonUpload;
	}

	public JButton getButtonUploadOptions() {
		return buttonUploadOptions;
	}

	public JProgressBar getUploadrogressBar() {
		return uploadrogressBar;
	}

	public JLabel getUploadSizeLabelValue() {
		return uploadSizeLabelValue;
	}

	public JLabel getUploadedLabelValue() {
		return uploadedLabelValue;
	}

	public JLabel getUploadSpeedLabelValue() {
		return uploadSpeedLabelValue;
	}

	public JLabel getUploadRemainingTimeValue() {
		return uploadRemainingTimeValue;
	}

	public Box getUploadInformationBox() {
		return uploadInformationBox;
	}
}
