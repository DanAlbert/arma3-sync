package fr.soe.a3s.ui.repositoryEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
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

import fr.soe.a3s.constant.Protocole;
import fr.soe.a3s.dto.ProtocoleDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

public class UploadRepositoryOptionsPanel extends JDialog implements
		UIConstants {

	private final Facade facade;
	private JButton buttonOK, buttonCancel;
	private JLabel labelPort;
	private JLabel labelConnection;
	private JLabel labelProtocole;
	private JCheckBox checkBoxAnonymous;
	private JLabel labelPassword;
	private JTextField textFieldLogin;
	private JLabel labelLogin;
	private JTextField textFieldPort;
	private JTextField textFieldHost;
	private JLabel labelHost;
	private JPanel connectionPanel, protocolePanel;
	private JPasswordField passwordField;
	private char[] password;
	private JComboBox<Object> comboBoxProtocole;
	/* Service */
	private final RepositoryService repositoryService = new RepositoryService();
	// Data
	private String repositoryName;

	public UploadRepositoryOptionsPanel(Facade facade) {
		super(facade.getMainPanel(), "Upload options", true);
		this.facade = facade;
		this.setResizable(false);
		this.setSize(405, 280);
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
				protocolePanel = new JPanel();
				protocolePanel.setLayout(null);
				protocolePanel.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(), "Protocole"));
				{
					labelProtocole = new JLabel();
					protocolePanel.add(labelProtocole);
					labelProtocole.setText("File transfer protocole");
					labelProtocole.setBounds(18, 23, 122, 24);
				}
				{
					comboBoxProtocole = new JComboBox();
					comboBoxProtocole.setFocusable(false);
					protocolePanel.add(comboBoxProtocole);
					protocolePanel.add(comboBoxProtocole);
					ComboBoxModel comboBoxProtocoleModel = new DefaultComboBoxModel(
							new String[] { Protocole.FTP.getDescription(), });
					comboBoxProtocole.setModel(comboBoxProtocoleModel);
					comboBoxProtocole.setBounds(141, 24, 77, 23);
				}
			}
			{
				connectionPanel = new JPanel();
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

			Box vertBox = Box.createVerticalBox();
			vertBox.add(Box.createVerticalStrut(5));
			vertBox.add(protocolePanel);
			vertBox.add(Box.createVerticalStrut(5));
			vertBox.add(connectionPanel);
			connectionPanel.setPreferredSize(new java.awt.Dimension(405, 70));
			vertBox.add(Box.createVerticalStrut(5));
			centerPanel.add(vertBox);
		}
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
		comboBoxProtocole.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				comboBoxProtocolePerformed();
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

	public void init(String repositoryName) {

		try {
			this.repositoryName = repositoryName;
			RepositoryDTO repositoryDTO = repositoryService
					.getRepository(repositoryName);
			ProtocoleDTO protocoleDTO = repositoryDTO.getProtocoleDTO();
			Protocole protocole = protocoleDTO.getProtocole();
			ProtocoleDTO uploadProtocoleDTO = repositoryDTO
					.getRepositoryUploadProtocoleDTO();
			if (uploadProtocoleDTO == null && protocole.equals(Protocole.FTP)) {
				comboBoxProtocole.setSelectedItem(protocole.getDescription());
				textFieldHost.setText(protocole.getDescription().toLowerCase()
						+ "://" + protocoleDTO.getUrl());
				textFieldPort.setText(protocoleDTO.getPort());
				textFieldLogin.setText(protocoleDTO.getLogin());
				passwordField.setText(protocoleDTO.getPassword());
				if (protocoleDTO.getLogin().equals("anonymous")) {
					checkBoxAnonymous.setSelected(true);
					textFieldLogin.setEnabled(false);
					passwordField.setText("");
					passwordField.setEnabled(false);
				}
			} else if (uploadProtocoleDTO != null) {
				Protocole uploadProtocole = uploadProtocoleDTO.getProtocole();
				comboBoxProtocole.setSelectedItem(uploadProtocole
						.getDescription());
				textFieldHost.setText(uploadProtocole.getPrompt()
						+ uploadProtocoleDTO.getUrl());
				textFieldPort.setText(uploadProtocoleDTO.getPort());
				textFieldLogin.setText(uploadProtocoleDTO.getLogin());
				passwordField.setText(uploadProtocoleDTO.getPassword());
				if (uploadProtocoleDTO.getLogin().equals("anonymous")) {
					checkBoxAnonymous.setSelected(true);
					textFieldLogin.setEnabled(false);
					passwordField.setText("");
					passwordField.setEnabled(false);
				}
			}

			textFieldHost.setCaretPosition(0);
			textFieldPort.setCaretPosition(0);
			textFieldPort.setCaretPosition(0);
		} catch (RepositoryException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void buttonOKPerformed() {

		String url = textFieldHost.getText()
				.replace(Protocole.FTP.getPrompt(), "")
				.replace(Protocole.HTTP.getPrompt(), "").trim();
		String port = textFieldPort.getText().trim();
		String login = textFieldLogin.getText().trim();
		password = passwordField.getPassword();
		String pass = "";
		for (int i = 0; i < password.length; i++) {
			pass = pass + password[i];
		}
		Protocole protocole = Protocole.getEnum((String) comboBoxProtocole
				.getSelectedItem());

		assert (protocole != null);

		try {
			repositoryService.setRepositoryUploadProtocole(repositoryName, url,
					port, login, pass, protocole);
			repositoryService.write(repositoryName);
		} catch (CheckException | RepositoryException | WritingException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		this.dispose();
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

	private void comboBoxProtocolePerformed() {
		if (comboBoxProtocole.getSelectedItem().equals(
				Protocole.HTTP.getDescription())) {
			textFieldHost.setText("http://");
			textFieldPort.setText("80");
		} else {
			textFieldHost.setText("ftp://");
			textFieldPort.setText("21");
		}
	}

	private void menuExitPerformed() {
		passwordField.setText("");
		if (password != null) {
			Arrays.fill(password, '0');
		}
		this.dispose();
	}

}
