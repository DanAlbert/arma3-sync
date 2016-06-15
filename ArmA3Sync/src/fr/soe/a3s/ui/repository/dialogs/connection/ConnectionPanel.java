package fr.soe.a3s.ui.repository.dialogs.connection;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.dto.ProtocolDTO;

public class ConnectionPanel extends JPanel {

	private JLabel labelHost;
	private JTextField textFieldHost;
	private JLabel labelPort;
	private JTextField textFieldPort;
	private JLabel labelLogin;
	private JTextField textFieldLogin;
	private JLabel labelPassword;
	private JPasswordField passwordField;
	private JCheckBox checkBoxAnonymous;

	public ConnectionPanel() {

		this.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Connection"));

		this.setLayout(new GridBagLayout());
		{
			labelHost = new JLabel();
			labelHost.setText("Host or url");
			textFieldHost = new JTextField();
		}
		{
			labelPort = new JLabel();
			labelPort.setText("Port");
			textFieldPort = new JTextField();
		}
		{
			labelLogin = new JLabel();
			labelLogin.setText("Login");
			textFieldLogin = new JTextField();
		}
		{
			labelPassword = new JLabel();
			labelPassword.setText("Password");
			passwordField = new JPasswordField();
		}
		{
			checkBoxAnonymous = new JCheckBox();
			checkBoxAnonymous.setText("Anonymous");
			checkBoxAnonymous.setFocusable(false);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.5;
			c.weighty = 0;
			c.gridx = 0;
			c.gridy = 0;
			c.insets = new Insets(10, 10, 0, 10);
			this.add(labelHost, c);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 20;
			c.weighty = 0;
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 2;
			c.gridheight = 1;
			c.insets = new Insets(0, 10, 0, 0);
			this.add(textFieldHost, c);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.5;
			c.weighty = 0;
			c.gridx = 2;
			c.gridy = 0;
			c.insets = new Insets(10, 10, 0, 10);
			this.add(labelPort, c);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 0.5;
			c.weighty = 0;
			c.gridx = 2;
			c.gridy = 1;
			c.insets = new Insets(0, 10, 0, 40);
			this.add(textFieldPort, c);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.5;
			c.weighty = 0;
			c.gridx = 0;
			c.gridy = 2;
			c.insets = new Insets(10, 10, 0, 10);
			this.add(labelLogin, c);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 0.5;
			c.weighty = 0;
			c.gridx = 0;
			c.gridy = 3;
			c.insets = new Insets(0, 10, 10, 0);
			this.add(textFieldLogin, c);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.5;
			c.weighty = 0;
			c.gridx = 1;
			c.gridy = 2;
			c.insets = new Insets(10, 10, 0, 10);
			this.add(labelPassword, c);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 0.5;
			c.weighty = 0;
			c.gridx = 1;
			c.gridy = 3;
			c.insets = new Insets(0, 10, 10, 0);
			this.add(passwordField, c);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.5;
			c.weighty = 0;
			c.gridx = 2;
			c.gridy = 3;
			c.insets = new Insets(0, 10, 10, 10);
			this.add(checkBoxAnonymous, c);
		}

		textFieldHost.setPreferredSize(new Dimension(textFieldHost
				.getPreferredSize().width,
				checkBoxAnonymous.getPreferredSize().height));

		checkBoxAnonymous.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				checkBoxAnonymousPerformed();
			}
		});
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

	public void init(ProtocolType protocolType) {

		textFieldHost.setText(protocolType.getPrompt());
		textFieldPort.setText(protocolType.getDefaultPort());
	}

	public void init(ProtocolDTO protocolDTO) {

		textFieldHost.setText(protocolDTO.getProtocolType().getPrompt()
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
		textFieldHost.setCaretPosition(0);
		textFieldPort.setCaretPosition(0);
	}

	public String getUrl() {

		String url = textFieldHost.getText().trim();

		/*
		 * Remove all white spaces in the url
		 * http://stackoverflow.com/questions/
		 * 18295759/java-lang-illegalargumentexception
		 * -illegal-character-in-authority-at-index-7-wh
		 */
		url = url.replaceAll("\\s+", "");

		/* Remove prompt from url */
		String test = url.toLowerCase()
				.replaceAll(ProtocolType.FTP.getPrompt(), "")
				.replaceAll(ProtocolType.HTTP.getPrompt(), "")
				.replaceAll(ProtocolType.HTTPS.getPrompt(), "")
				.replaceAll(ProtocolType.SOCKS4.getPrompt(), "")
				.replaceAll(ProtocolType.SOCKS5.getPrompt(), "");
		if (url.length() > test.length()) {
			int index = url.length() - test.length();
			url = url.substring(index);
		}

		/* Remove port from url */
		int index1 = url.indexOf(":");
		int index2 = url.indexOf("/");
		if (index1 != -1) {
			if (index2 != -1) {
				url = url.substring(0, index1) + url.substring(index2);
			} else {
				url = url.substring(0, index1);
			}
		}

		/* Remove / at the end of url */
		if (!url.isEmpty()) {
			boolean urlCleanded = false;
			do {
				int index = url.lastIndexOf("/");
				if (index != -1) {
					if (index == url.length() - 1) {
						url = url.substring(0, url.length() - 1);
					} else {
						urlCleanded = true;
					}
				} else {
					urlCleanded = true;
				}
			} while (!urlCleanded);
		}

		return url;
	}

	public String getPort() {
		return textFieldPort.getText().trim();
	}

	public String getLogin() {
		return textFieldLogin.getText().trim();
	}

	public String getPassword() {

		String password = "";
		for (int i = 0; i < passwordField.getPassword().length; i++) {
			password = password + passwordField.getPassword()[i];
		}
		return password;
	}

	public void clearPassword() {

		passwordField.setText("");
		if (passwordField.getPassword() != null) {
			Arrays.fill(passwordField.getPassword(), '0');
		}
	}

	public void activate(boolean value) {

		textFieldHost.setEnabled(value);
		textFieldPort.setEnabled(value);
		checkBoxAnonymous.setEnabled(value);
		if (!checkBoxAnonymous.isSelected()) {
			textFieldLogin.setEnabled(value);
			passwordField.setEnabled(value);
		}
	}

	public JLabel getLabelHost() {
		return labelHost;
	}

	public JLabel getLabelPort() {
		return labelPort;
	}

	public JLabel getLabelLogin() {
		return labelLogin;
	}

	public JLabel getLabelPassword() {
		return labelPassword;
	}
}
