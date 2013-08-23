package fr.soe.a3s.ui.repositoryEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import fr.soe.a3s.constant.EncryptionMode;
import fr.soe.a3s.dto.AutoConfigDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.FtpException;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.service.FtpService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class RepositoryEditPanel extends JDialog implements UIConstants {

	private Facade facade;
	private JButton buttonOK, buttonCancel;
	private JLabel labelRepositoryName;
	private JLabel labelPort;
	private JLabel labelConnection;
	private JComboBox comboBoxEncryption;
	private JLabel labelProtocole;
	private JButton buttonImport;
	private JTextField textFieldAutoConfigUrl;
	private JLabel labelAutoConfigUrl;
	private JCheckBox checkBoxAnonymous;
	private JLabel labelPassword;
	private JTextField textFieldLogin;
	private JLabel labelLogin;
	private JTextField textFieldPort;
	private JTextField textFieldHost;
	private JLabel labelHost;
	private JTextField textFieldRepositoryName;
	private JPanel repositoryPanel, connectionPanel, protocolePanel;
	private JPasswordField passwordField;
	private char[] password;
	private RepositoryService repositoryService = new RepositoryService();
	private String initialRepositoryName = "";

	public RepositoryEditPanel(Facade facade) {
		super(facade.getMainPanel(), "New repository", true);
		this.facade = facade;
		this.facade.setRepositoryEditPanel(this);
		this.setResizable(false);
		this.setSize(405, 350);
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
			buttonOK = new JButton("OK");
			buttonCancel = new JButton("Cancel");
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
			controlPanel.setLayout(flowLayout);
			controlPanel.add(buttonOK);
			controlPanel.add(buttonCancel);
			this.add(controlPanel, BorderLayout.SOUTH);
			JPanel sidePanel1 = new JPanel();
			this.add(sidePanel1, BorderLayout.EAST);
			JPanel sidePanel2 = new JPanel();
			this.add(sidePanel2, BorderLayout.WEST);
		}
		{
			JPanel centerPanel = new JPanel();
			GridLayout grid1 = new GridLayout(1, 1);
			centerPanel.setLayout(grid1);
			this.add(centerPanel, BorderLayout.CENTER);
			{
				repositoryPanel = new JPanel();
				// centerPanel.add(repositoryPanel);
				repositoryPanel.setLayout(null);
				repositoryPanel.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(), "Repository"));
				{
					labelRepositoryName = new JLabel();
					repositoryPanel.add(labelRepositoryName);
					labelRepositoryName.setBounds(18, 67, 126, 22);
					labelRepositoryName.setText("Repository name");
				}
				{
					textFieldRepositoryName = new JTextField();
					repositoryPanel.add(textFieldRepositoryName);
					textFieldRepositoryName.setBounds(18, 88, 346, 24);
				}
				{
					labelAutoConfigUrl = new JLabel();
					repositoryPanel.add(labelAutoConfigUrl);
					labelAutoConfigUrl.setBounds(18, 20, 130, 22);
					labelAutoConfigUrl.setText("Public auto-config url");
				}
				{
					textFieldAutoConfigUrl = new JTextField();
					repositoryPanel.add(textFieldAutoConfigUrl);
					textFieldAutoConfigUrl.setBounds(18, 41, 346, 24);
				}
				{
					buttonImport = new JButton();
					repositoryPanel.add(buttonImport);
					buttonImport.setText("Import");
					buttonImport.setBounds(293, 12, 72, 24);
				}
				{
					labelConnection = new JLabel();
					repositoryPanel.add(labelConnection);
					labelConnection.setBounds(144, 21, 147, 21);
				}
			}
			{
				connectionPanel = new JPanel();
				// centerPanel.add(connectionPanel);
				connectionPanel.setLayout(null);
				connectionPanel.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(), "Connection"));
				{
					labelHost = new JLabel();
					connectionPanel.add(labelHost);
					labelHost.setText("Host or url");
					labelHost.setBounds(18, 18, 73, 20);
				}
				{
					textFieldHost = new JTextField();
					connectionPanel.add(textFieldHost);
					textFieldHost.setBounds(18, 38, 250, 24);
					textFieldHost.setText("ftp://");
				}
				{
					labelPort = new JLabel();
					connectionPanel.add(labelPort);
					labelPort.setText("Port");
					labelPort.setBounds(280, 19, 33, 24);
				}
				{
					textFieldPort = new JTextField();
					connectionPanel.add(textFieldPort);
					textFieldPort.setBounds(280, 38, 80, 24);
					textFieldPort.setText("21");
				}
				{
					labelLogin = new JLabel();
					connectionPanel.add(labelLogin);
					labelLogin.setText("Login");
					labelLogin.setBounds(18, 68, 43, 24);
				}
				{
					textFieldLogin = new JTextField();
					connectionPanel.add(textFieldLogin);
					textFieldLogin.setBounds(18, 90, 134, 24);
				}
				{
					labelPassword = new JLabel();
					connectionPanel.add(labelPassword);
					labelPassword.setBounds(166, 69, 63, 24);
					labelPassword.setText("Password");
				}
				{
					passwordField = new JPasswordField();
					connectionPanel.add(passwordField);
					passwordField.setBounds(166, 90, 102, 24);

				}
				{
					checkBoxAnonymous = new JCheckBox();
					connectionPanel.add(checkBoxAnonymous);
					checkBoxAnonymous.setText("Anonymous");
					checkBoxAnonymous.setBounds(280, 91, 93, 24);
				}
			}
			{
				protocolePanel = new JPanel();
				// centerPanel.add(protocolePanel);
				protocolePanel.setLayout(null);
				protocolePanel.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(), "Protocole"));
			}
			{
				labelProtocole = new JLabel();
				protocolePanel.add(labelProtocole);
				labelProtocole.setText("FTP file protocole");
				labelProtocole.setBounds(18, 23, 100, 24);
			}
			Box vertBox = Box.createVerticalBox();
			vertBox.add(Box.createVerticalStrut(5));
			vertBox.add(repositoryPanel);
			vertBox.add(Box.createVerticalStrut(5));
			repositoryPanel.setPreferredSize(new java.awt.Dimension(344, 65));
			vertBox.add(connectionPanel);
			vertBox.add(Box.createVerticalStrut(5));
			connectionPanel.setPreferredSize(new java.awt.Dimension(384, 71));
			//vertBox.add(protocolePanel);
			{
				ComboBoxModel comboBoxEncryptionModel = new DefaultComboBoxModel(
						new String[] {
								EncryptionMode.NO_ENCRYPTION.getDescription(),
								EncryptionMode.EXPLICIT_SSL.getDescription(),
								EncryptionMode.IMPLICIT_SSL.getDescription() });
				comboBoxEncryption = new JComboBox();
				protocolePanel.add(comboBoxEncryption);
				comboBoxEncryption.setModel(comboBoxEncryptionModel);
				comboBoxEncryption.setBounds(123, 24, 146, 25);
			}
			// protocolePanel.setPreferredSize(new java.awt.Dimension(384, 20));
			centerPanel.add(vertBox);
		}
		buttonImport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonImportPerformed();
			}
		});
		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonOKPerformed();
			}
		});
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuExitPerformed();
			}
		});
		checkBoxAnonymous.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				checkBoxAnonymousPerformed();
			}
		});
		comboBoxEncryption.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				comboBoxEncryptionPerformed();
			}
		});
		// Add Listeners
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				menuExitPerformed();
			}
		});
	}

	public void init(String repositoryName) {

		try {
			this.setTitle("Edit repository");
			RepositoryDTO repositoryDTO = repositoryService
					.getRepository(repositoryName);
			initialRepositoryName = repositoryName;
			textFieldRepositoryName.setText(repositoryDTO.getName());
			textFieldHost
					.setText("ftp://" + repositoryDTO.getFtpDTO().getUrl());
			textFieldPort.setText(repositoryDTO.getFtpDTO().getPort());
			textFieldLogin.setText(repositoryDTO.getFtpDTO().getLogin());
			passwordField.setText(repositoryDTO.getFtpDTO().getPassword());
			// comboBoxEncryption.setSelectedItem(repositoryDTO.getFtpDTO()
			// .getEncryptionMode().getDescription());
			if (repositoryDTO.getFtpDTO().getLogin().equals("anonymous")) {
				checkBoxAnonymous.setSelected(true);
				textFieldLogin.setEnabled(false);
				passwordField.setText("");
				passwordField.setEnabled(false);
			}
			textFieldRepositoryName.setCaretPosition(0);
			textFieldHost.setCaretPosition(0);
			textFieldPort.setCaretPosition(0);
			textFieldPort.setCaretPosition(0);
		} catch (RepositoryException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void buttonImportPerformed() {

		final String url = textFieldAutoConfigUrl.getText()
				.replace("ftp://", "").trim();
		if (url.isEmpty()) {
			JOptionPane.showMessageDialog(this, "The url is empty!", "Error",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		labelConnection.setText("Connecting to repository...");
		labelConnection.setFont(new Font("Tohama", Font.ITALIC, 11));

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					FtpService ftpService = new FtpService();
					AutoConfigDTO autoConfigDTO = ftpService
							.importAutoConfig(url);
					if (autoConfigDTO != null) {
						labelConnection.setText("Connexion success!");
						labelConnection.setFont(new Font("Tohama", Font.ITALIC,
								11));
						labelConnection.setForeground(new Color(45, 125, 45));
						textFieldRepositoryName.setText(autoConfigDTO
								.getRepositoryName());
						textFieldHost.setText(autoConfigDTO.getFtpDTO()
								.getUrl());
						textFieldLogin.setText(autoConfigDTO.getFtpDTO()
								.getLogin());
						textFieldPort.setText(autoConfigDTO.getFtpDTO()
								.getPort());
						passwordField.setText(autoConfigDTO.getFtpDTO()
								.getPassword());
						if (autoConfigDTO.getFtpDTO().getLogin()
								.equalsIgnoreCase("anonymous")) {
							textFieldLogin.setEnabled(false);
							passwordField.setEnabled(false);
						checkBoxAnonymous.setSelected(true);
						}
					} else {
						labelConnection.setText("Url is not reachable!");
						labelConnection.setFont(new Font("Tohama", Font.ITALIC,
								11));
						labelConnection.setForeground(Color.RED);
					}
				} catch (Exception e) {
					labelConnection.setText("Url is not reachable!");
					labelConnection
							.setFont(new Font("Tohama", Font.ITALIC, 11));
					labelConnection.setForeground(Color.RED);
				}
			}
		});
		t.start();
	}

	private void buttonOKPerformed() {

		String name = textFieldRepositoryName.getText().trim();
		String url = textFieldHost.getText().replace("ftp://", "").trim();
		String port = textFieldPort.getText().trim();
		String login = textFieldLogin.getText().trim();
		password = passwordField.getPassword();
		String pass = "";
		for (int i = 0; i < password.length; i++) {
			pass = pass + password[i];
		}
		String encryption = (String) comboBoxEncryption.getSelectedItem();

		try {
			if (!initialRepositoryName.isEmpty()) {
				repositoryService.removeRepository(initialRepositoryName);
			}
			repositoryService.createRepository(name, url, port, login, pass,
					EncryptionMode.getEnum(encryption));
			repositoryService.write(name);
			this.dispose();
			facade.getSyncPanel().refresh();
		} catch (CheckException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
					JOptionPane.WARNING_MESSAGE);
		} catch (WritingException e) {
			JOptionPane.showMessageDialog(this,
					"An error occured. \n Failded to write repository.",
					"Error", JOptionPane.ERROR_MESSAGE);
		} 
	}

	private void checkBoxAnonymousPerformed() {
		if (checkBoxAnonymous.isSelected()) {
			textFieldLogin.setText("anonymous");
			textFieldLogin.setEnabled(false);
			passwordField.setText("");
			passwordField.setEnabled(false);
		} else {
			textFieldLogin.setText("");
			textFieldLogin.setEnabled(true);
			passwordField.setEnabled(true);
		}
	}

	private void menuExitPerformed() {
		passwordField.setText("");
		if (password != null) {
			Arrays.fill(password, '0');
		}
		this.dispose();
	}

	private void comboBoxEncryptionPerformed() {
		JOptionPane.showMessageDialog(this, "Not implemented yet.",
				"Repository", JOptionPane.INFORMATION_MESSAGE);
		comboBoxEncryption.setSelectedIndex(0);
	}
}
