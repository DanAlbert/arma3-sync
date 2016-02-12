package fr.soe.a3s.ui.repositoryEditor.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.AbstractButton;
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

import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.dto.ProtocolDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.exception.repository.RepositoryException;
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
public abstract class UploadOptionsPanel extends JDialog implements UIConstants {

	private final Facade facade;
	private JButton buttonOK, buttonCancel;
	private JLabel labelPort;
	private JLabel labelConnection;
	private JLabel labelProtocol;
	private JCheckBox checkBoxAnonymous;
	private JLabel labelPassword;
	private JTextField textFieldLogin;
	private JLabel labelLogin;
	private JTextField textFieldPort;
	private JTextField textFieldHost;
	private JLabel labelHost;
	private JPanel connectionPanel, protocolPanel;
	private JPasswordField passwordField;
	private char[] password;
	private JComboBox comboBoxProtocol;
	protected Box vertBox;
	private final AdvancedConnectionPanel repositoryAdvancedPanel;
	/* Service */
	protected final RepositoryService repositoryService = new RepositoryService();
	// Data
	protected String repositoryName;
	private AbstractButton buttonAdvanced;

	public UploadOptionsPanel(Facade facade) {
		super(facade.getMainPanel(), "Upload options", true);
		this.facade = facade;
		this.setResizable(false);
		setIconImage(ICON);
		this.setLayout(new BorderLayout());
		{
			JPanel controlPanel = new JPanel();
			buttonAdvanced = new JButton("Advanced");
			buttonOK = new JButton("OK");
			buttonCancel = new JButton("Cancel");
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
			controlPanel.setLayout(flowLayout);
			controlPanel.add(buttonAdvanced);
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
				protocolPanel = new JPanel();
				protocolPanel.setLayout(null);
				protocolPanel.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(), "Protocol"));
				{
					labelProtocol = new JLabel();
					protocolPanel.add(labelProtocol);
					labelProtocol.setText("File transfer protocol");
					labelProtocol.setBounds(18, 23, 122, 24);
				}
				{
					comboBoxProtocol = new JComboBox();
					comboBoxProtocol.setFocusable(false);
					protocolPanel.add(comboBoxProtocol);
					protocolPanel.add(comboBoxProtocol);
					ComboBoxModel comboBoxProtocolModel = new DefaultComboBoxModel(
							new String[] { ProtocolType.FTP.getDescription() });
					comboBoxProtocol.setModel(comboBoxProtocolModel);
					comboBoxProtocol.setBounds(141, 24, 64, 23);
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

			vertBox = Box.createVerticalBox();
			vertBox.add(Box.createVerticalStrut(5));
			vertBox.add(protocolPanel);
			vertBox.add(Box.createVerticalStrut(5));
			vertBox.add(connectionPanel);
			connectionPanel.setPreferredSize(new java.awt.Dimension(405, 70));
			vertBox.add(Box.createVerticalStrut(5));
			centerPanel.add(vertBox);
		}

		repositoryAdvancedPanel = new AdvancedConnectionPanel(facade);
		repositoryAdvancedPanel.init();
		repositoryAdvancedPanel.setVisible(false);

		buttonAdvanced.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonAdvancedPerformed();
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
		comboBoxProtocol.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				comboBoxProtocolPerformed();
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

	protected void init(String repositoryName) {

		this.textFieldHost.setText(ProtocolType.FTP.getPrompt());
		this.textFieldPort.setText(ProtocolType.FTP.getDefaultPort());

		try {
			this.repositoryName = repositoryName;
			// Set Upload protocol
			RepositoryDTO repositoryDTO = repositoryService
					.getRepository(repositoryName);
			ProtocolDTO protocolDTO = repositoryDTO.getProtocoleDTO();
			ProtocolType protocolType = protocolDTO.getProtocolType();
			ProtocolDTO uploadProtocolDTO = repositoryDTO
					.getRepositoryUploadProtocoleDTO();
			if (uploadProtocolDTO == null
					&& protocolType.equals(ProtocolType.FTP)) {
				comboBoxProtocol.setSelectedItem(protocolType.getDescription());
				textFieldHost.setText(protocolType.getPrompt()
						+ protocolDTO.getUrl());
				textFieldPort.setText(protocolDTO.getPort());
				textFieldLogin.setText(protocolDTO.getLogin());
				passwordField.setText(protocolDTO.getPassword());
				if (protocolDTO.getLogin().equals("anonymous")) {
					checkBoxAnonymous.setSelected(true);
					textFieldLogin.setEnabled(false);
					passwordField.setText("");
					passwordField.setEnabled(false);
				}
			} else if (uploadProtocolDTO != null) {
				ProtocolType uploadProtocolType = uploadProtocolDTO
						.getProtocolType();
				comboBoxProtocol.setSelectedItem(uploadProtocolType
						.getDescription());
				textFieldHost.setText(uploadProtocolType.getPrompt()
						+ uploadProtocolDTO.getUrl());
				textFieldPort.setText(uploadProtocolDTO.getPort());
				textFieldLogin.setText(uploadProtocolDTO.getLogin());
				passwordField.setText(uploadProtocolDTO.getPassword());
				if (uploadProtocolDTO.getLogin().equals("anonymous")) {
					checkBoxAnonymous.setSelected(true);
					textFieldLogin.setEnabled(false);
					passwordField.setText("");
					passwordField.setEnabled(false);
				}
				repositoryAdvancedPanel.getTextFiledReadTimeout().setText(
						uploadProtocolDTO.getReadTimeOut());
				repositoryAdvancedPanel.getTextFiledConnectionTimeout()
						.setText(uploadProtocolDTO.getConnectionTimeOut());
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

	private void buttonAdvancedPerformed() {
		repositoryAdvancedPanel.setVisible(true);
	}

	protected abstract void buttonOKPerformed();

	protected boolean proceed() {

		String url = textFieldHost.getText().trim();

		String test = url.toLowerCase()
				.replaceAll(ProtocolType.FTP.getPrompt(), "")
				.replaceAll(ProtocolType.HTTP.getPrompt(), "");
		if (url.length() > test.length()) {
			int index = url.length() - test.length();
			url = url.substring(index);
		}

		String port = textFieldPort.getText().trim();
		String login = textFieldLogin.getText().trim();
		password = passwordField.getPassword();
		String pass = "";
		for (int i = 0; i < password.length; i++) {
			pass = pass + password[i];
		}
		ProtocolType protocol = ProtocolType.getEnum((String) comboBoxProtocol
				.getSelectedItem());

		assert (protocol != null);

		String connectionTimeOut = repositoryAdvancedPanel
				.getTextFiledConnectionTimeout().getText().trim();
		String readTimeOut = repositoryAdvancedPanel.getTextFiledReadTimeout()
				.getText().trim();

		try {
			// Set Upload protocol
			repositoryService
					.setRepositoryUploadProtocole(repositoryName, url, port,
							login, pass, protocol, connectionTimeOut,
							readTimeOut);
			return true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
					JOptionPane.WARNING_MESSAGE);
			return false;
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

	private void comboBoxProtocolPerformed() {

		String description = (String) comboBoxProtocol.getSelectedItem();
		ProtocolType protocolType = ProtocolType.getEnum(description);
		if (protocolType != null) {
			textFieldHost.setText(protocolType.getPrompt());
			textFieldPort.setText(protocolType.getDefaultPort());
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
