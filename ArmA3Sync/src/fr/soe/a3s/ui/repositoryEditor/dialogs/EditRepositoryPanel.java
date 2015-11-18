package fr.soe.a3s.ui.repositoryEditor.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.connection.AutoConfigURLAccessMethods;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.dto.AutoConfigDTO;
import fr.soe.a3s.dto.ProtocolDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.exception.remote.RemoteAutoconfigFileNotFoundException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.exception.repository.RepositoryNotFoundException;
import fr.soe.a3s.service.AbstractConnexionService;
import fr.soe.a3s.service.AbstractConnexionServiceFactory;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.repositoryEditor.progressDialogs.SynchronizingPanel;

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
public class EditRepositoryPanel extends JDialog implements UIConstants,
		DataAccessConstants {

	private final Facade facade;
	private JButton buttonOK, buttonCancel, buttonAdvanced;
	private JLabel labelRepositoryName;
	private JLabel labelPort;
	private JLabel labelConnection;
	private JComboBox comboBoxEncryption;
	private JLabel labelProtocol;
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
	private JPanel repositoryPanel, connectionPanel, protocolPanel;
	private JPasswordField passwordField;
	private char[] password;
	private String initialRepositoryName = null;
	private JComboBox comboBoxProtocol;
	private final JPopupMenu popup;
	private final JMenuItem menuItemPaste;
	private final AdvancedConnectionPanel repositoryAdvancedPanel;

	/* Service */
	private final RepositoryService repositoryService = new RepositoryService();

	public EditRepositoryPanel(Facade facade) {
		super(facade.getMainPanel(), "Repository", true);
		this.facade = facade;
		this.setResizable(false);
		this.setSize(405, 400);
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
				repositoryPanel = new JPanel();
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
					textFieldAutoConfigUrl.requestFocus();
				}
				{
					buttonImport = new JButton();
					repositoryPanel.add(buttonImport);
					buttonImport.setText("Import");
					buttonImport.setBounds(293, 12, 72, 24);
					buttonImport.setFocusable(false);
				}
				{
					labelConnection = new JLabel();
					repositoryPanel.add(labelConnection);
					labelConnection.setBounds(144, 21, 147, 21);
				}
			}
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
					ComboBoxModel comboBoxProtocolModel = new DefaultComboBoxModel(
							new String[] { ProtocolType.FTP.getDescription(),
									ProtocolType.HTTP.getDescription(),
									ProtocolType.HTTPS.getDescription() });
					comboBoxProtocol.setModel(comboBoxProtocolModel);
					comboBoxProtocol.setBounds(141, 24, 77, 23);
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

			Box vertBox = Box.createVerticalBox();
			vertBox.add(Box.createVerticalStrut(5));
			vertBox.add(repositoryPanel);
			vertBox.add(Box.createVerticalStrut(5));
			repositoryPanel.setPreferredSize(new java.awt.Dimension(344, 65));
			vertBox.add(protocolPanel);
			protocolPanel.setPreferredSize(new java.awt.Dimension(344, 5));
			vertBox.add(Box.createVerticalStrut(5));
			vertBox.add(connectionPanel);
			vertBox.add(Box.createVerticalStrut(5));
			connectionPanel.setPreferredSize(new java.awt.Dimension(384, 71));
			// vertBox.add(protocolePanel);
			// {
			// ComboBoxModel comboBoxEncryptionModel = new DefaultComboBoxModel(
			// new String[] {
			// EncryptionMode.NO_ENCRYPTION.getDescription(),
			// EncryptionMode.EXPLICIT_SSL.getDescription(),
			// EncryptionMode.IMPLICIT_SSL.getDescription() });
			// comboBoxEncryption = new JComboBox();
			// protocolePanel.add(comboBoxEncryption);
			// comboBoxEncryption.setModel(comboBoxEncryptionModel);
			// comboBoxEncryption.setBounds(123, 24, 146, 25);
			// }
			// protocolePanel.setPreferredSize(new java.awt.Dimension(384, 20));
			centerPanel.add(vertBox);
		}

		/* Right clic menu */
		popup = new JPopupMenu();

		menuItemPaste = new JMenuItem("Paste");
		menuItemPaste.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				popupActionPerformed(evt);
			}
		});
		menuItemPaste.setActionCommand("Paste");
		popup.add(menuItemPaste);

		repositoryAdvancedPanel = new AdvancedConnectionPanel(facade);
		repositoryAdvancedPanel.init();
		repositoryAdvancedPanel.setVisible(false);

		textFieldAutoConfigUrl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.show((JComponent) e.getSource(), e.getX(), e.getY());
				} else if (SwingUtilities.isRightMouseButton(e)) {
					popup.show((JComponent) e.getSource(), e.getX(), e.getY());
				}
			}
		});
		buttonImport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonImportPerformed();
			}
		});
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
		// comboBoxEncryption.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent arg0) {
		// comboBoxEncryptionPerformed();
		// }
		// });
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

	public void init() {

		this.setTitle("New repository");
		this.textFieldHost.setText(ProtocolType.FTP.getPrompt());
		this.textFieldPort.setText(ProtocolType.FTP.getDefaultPort());
	}

	public void init(String repositoryName) {

		this.setTitle("Edit repository");
		this.textFieldHost.setText(ProtocolType.FTP.getPrompt());
		this.textFieldPort.setText(ProtocolType.FTP.getDefaultPort());

		try {
			RepositoryDTO repositoryDTO = repositoryService
					.getRepository(repositoryName);
			initialRepositoryName = repositoryName;
			textFieldRepositoryName.setText(repositoryDTO.getName());
			ProtocolDTO protocoleDTO = repositoryDTO.getProtocoleDTO();
			ProtocolType protocole = protocoleDTO.getProtocolType();
			comboBoxProtocol.setSelectedItem(protocole.getDescription());
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
			textFieldRepositoryName.setCaretPosition(0);
			textFieldHost.setCaretPosition(0);
			textFieldPort.setCaretPosition(0);
			textFieldPort.setCaretPosition(0);
			repositoryAdvancedPanel.getTextFiledReadTimeout().setText(
					protocoleDTO.getReadTimeOut());
			repositoryAdvancedPanel.getTextFiledConnectionTimeout().setText(
					protocoleDTO.getConnectionTimeOut());
		} catch (RepositoryException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void buttonImportPerformed() {

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				// Check autoconfig url spelling
				String url = textFieldAutoConfigUrl.getText().trim();
				boolean invalid = false;
				int index = url.lastIndexOf("/");
				if (index != -1) {
					String autoconfigWord = url.substring(index + 1);
					if (!autoconfigWord.equals(DataAccessConstants.AUTOCONFIG)) {
						invalid = true;
					}
				} else {
					invalid = true;
				}

				if (invalid) {
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							"Url must ends with " + "\""
									+ DataAccessConstants.AUTOCONFIG + "\""
									+ ".", "Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				// Determine protocol
				AbstractProtocole protocol = null;
				try {
					protocol = AutoConfigURLAccessMethods.parse(url);
				} catch (CheckException e) {
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							e.getMessage(), "Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				assert (protocol != null);

				AbstractConnexionService connexion = null;
				try {
					connexion = AbstractConnexionServiceFactory
							.getServiceFromProtocol(protocol);
				} catch (CheckException e) {
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							e.getMessage(), "Error",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				assert (connexion != null);

				labelConnection.setText("Connecting to repository...");
				labelConnection.setFont(new Font("Tohama", Font.ITALIC, 11));
				labelConnection.setForeground(Color.BLACK);

				try {
					AutoConfigDTO autoConfigDTO = connexion
							.importAutoConfig(protocol);
					if (autoConfigDTO != null) {
						// Init UI fields
						labelConnection.setText("Connection success!");
						labelConnection.setFont(new Font("Tohama", Font.ITALIC,
								11));
						labelConnection.setForeground(new Color(45, 125, 45));
						textFieldRepositoryName.setText(autoConfigDTO
								.getRepositoryName());
						ProtocolType protocolType = autoConfigDTO
								.getProtocoleDTO().getProtocolType();
						comboBoxProtocol.setSelectedItem(protocolType
								.getDescription());
						textFieldHost.setText(protocolType.getPrompt()
								+ autoConfigDTO.getProtocoleDTO().getUrl());
						textFieldLogin.setText(autoConfigDTO.getProtocoleDTO()
								.getLogin());
						textFieldPort.setText(autoConfigDTO.getProtocoleDTO()
								.getPort());
						passwordField.setText(autoConfigDTO.getProtocoleDTO()
								.getPassword());
						if (autoConfigDTO.getProtocoleDTO().getLogin()
								.equalsIgnoreCase("anonymous")) {
							textFieldLogin.setEnabled(false);
							passwordField.setEnabled(false);
							checkBoxAnonymous.setSelected(true);
						}
						// Update online panel
						facade.getOnlinePanel().init();
						// Update join server list
						facade.getLaunchPanel().init();
					} else {
						labelConnection.setText("Url is not reachable!");
						labelConnection.setFont(new Font("Tohama", Font.ITALIC,
								11));
						labelConnection.setForeground(Color.RED);
						throw new RemoteAutoconfigFileNotFoundException();
					}
				} catch (Exception e) {
					labelConnection.setText("Url is not reachable!");
					labelConnection
							.setFont(new Font("Tohama", Font.ITALIC, 11));
					labelConnection.setForeground(Color.RED);
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		t.start();
	}

	private void buttonAdvancedPerformed() {
		repositoryAdvancedPanel.setVisible(true);
	}

	private void buttonOKPerformed() {

		String name = textFieldRepositoryName.getText().trim();

		List<String> forbiddenCharactersList = new ArrayList<String>();
		forbiddenCharactersList.add("/");
		forbiddenCharactersList.add("\\");
		forbiddenCharactersList.add("*");
		forbiddenCharactersList.add("?");
		forbiddenCharactersList.add("\"");
		forbiddenCharactersList.add("<");
		forbiddenCharactersList.add(">");
		forbiddenCharactersList.add("|");
		String forbiddenCharactersLine = "";
		for (String stg : forbiddenCharactersList) {
			forbiddenCharactersLine = forbiddenCharactersLine + " " + stg;
		}

		for (String stg : forbiddenCharactersList) {
			if (name.contains(stg)) {
				String message = "Repository name must not contains special characters like:"
						+ forbiddenCharactersLine;
				JOptionPane.showMessageDialog(this, message, "Warning",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
		}

		/* Remove prompt from url */
		String url = textFieldHost.getText().trim();

		String test = url.toLowerCase()
				.replaceAll(ProtocolType.FTP.getPrompt(), "")
				.replaceAll(ProtocolType.HTTP.getPrompt(), "")
				.replaceAll(ProtocolType.HTTPS.getPrompt(), "");
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

		String port = textFieldPort.getText().trim();
		String login = textFieldLogin.getText().trim();
		password = passwordField.getPassword();
		String pass = "";
		for (int i = 0; i < password.length; i++) {
			pass = pass + password[i];
		}
		ProtocolType protocole = ProtocolType.getEnum((String) comboBoxProtocol
				.getSelectedItem());

		assert (protocole != null);

		String connectionTimeOut = repositoryAdvancedPanel
				.getTextFiledConnectionTimeout().getText().trim();
		String readTimeOut = repositoryAdvancedPanel.getTextFiledReadTimeout()
				.getText().trim();

		try {
			repositoryService.createRepository(name, url, port, login, pass,
					protocole, connectionTimeOut, readTimeOut);

			if (initialRepositoryName != null) {
				if (!initialRepositoryName.equals(name)) {
					repositoryService.removeRepository(initialRepositoryName);
				}
			}
			repositoryService.write(name);
		} catch (RepositoryNotFoundException | CheckException
				| WritingException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		SynchronizingPanel synchronizingPanel = new SynchronizingPanel(facade);
		synchronizingPanel.setVisible(true);
		synchronizingPanel.init(name);

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

	private void comboBoxEncryptionPerformed() {
		JOptionPane.showMessageDialog(this, "Not implemented yet.",
				"Repository", JOptionPane.INFORMATION_MESSAGE);
		comboBoxEncryption.setSelectedIndex(0);
	}

	private void popupActionPerformed(ActionEvent evt) {

		if (evt.getActionCommand().equals("Paste")) {
			String autoconfigUrl = getClipboardContents();
			textFieldAutoConfigUrl.setText(autoconfigUrl);
		}
	}

	/**
	 * Get the String residing on the clipboard. See
	 * http://www.javapractices.com/topic/TopicAction.do?Id=82
	 * 
	 * @return any text found on the Clipboard; if none found, return an empty
	 *         String.
	 */
	private String getClipboardContents() {
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		// odd: the Object param of getContents is not currently used
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText = (contents != null)
				&& contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasTransferableText) {
			try {
				result = (String) contents
						.getTransferData(DataFlavor.stringFlavor);
			} catch (Exception ex) {
				System.out.println(ex);
				ex.printStackTrace();
			}
		}
		return result;
	}
}
