package fr.soe.a3s.ui.repository.dialogs.connection;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.connection.AutoConfigURLAccessMethods;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.dto.AutoConfigDTO;
import fr.soe.a3s.dto.ProtocolDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.remote.RemoteAutoconfigFileNotFoundException;
import fr.soe.a3s.service.connection.ConnexionService;
import fr.soe.a3s.service.connection.ConnexionServiceFactory;

public class DescriptionPanel extends JPanel {

	private final RepositoryEditionDialog repositoryEditionDialog;
	private JLabel labelRepositoryName;
	private JTextField textFieldRepositoryName;
	private JTextField textFieldAutoConfigUrl;
	private JLabel labelAutoConfigUrl;
	private JButton buttonImport;
	private JLabel labelConnection;
	private final JPopupMenu popup;
	private final JMenuItem menuItemPaste;
	/* Services */
	private ConnexionService connexion = null;
	/* Test */
	private boolean connexionCanceled = false;

	public DescriptionPanel(RepositoryEditionDialog repositoryEditionDialog) {

		this.repositoryEditionDialog = repositoryEditionDialog;

		this.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Repository"));

		this.setLayout(new GridBagLayout());
		{
			labelAutoConfigUrl = new JLabel();
			labelAutoConfigUrl.setText("Public auto-config url");
			textFieldAutoConfigUrl = new JTextField();
			textFieldAutoConfigUrl.requestFocus();
			labelConnection = new JLabel();
			buttonImport = new JButton();
			buttonImport.setText("Import");
			buttonImport.setFocusable(false);
		}
		{
			labelRepositoryName = new JLabel();
			labelRepositoryName.setText("Repository name");
			textFieldRepositoryName = new JTextField();
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 20;
			c.weighty = 0;
			c.gridx = 0;
			c.gridy = 0;
			c.insets = new Insets(10, 10, 0, 10);
			this.add(labelAutoConfigUrl, c);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.5;
			c.weighty = 0;
			c.gridx = 0;
			c.gridy = 1;
			c.gridheight = 1;
			c.gridwidth = 3;
			c.insets = new Insets(0, 10, 0, 10);
			this.add(textFieldAutoConfigUrl, c);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 20;
			c.weighty = 0;
			c.gridx = 1;
			c.gridy = 0;
			c.insets = new Insets(10, 5, 0, 5);
			this.add(labelConnection, c);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 0.5;
			c.weighty = 0;
			c.gridx = 2;
			c.gridy = 0;
			c.insets = new Insets(10, 0, 0, 10);
			this.add(buttonImport, c);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.5;
			c.weighty = 0;
			c.gridx = 0;
			c.gridy = 2;
			c.insets = new Insets(10, 10, 0, 10);
			this.add(labelRepositoryName, c);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.5;
			c.weighty = 0;
			c.gridx = 0;
			c.gridy = 3;
			c.gridheight = 1;
			c.gridwidth = 3;
			c.insets = new Insets(0, 10, 10, 10);
			this.add(textFieldRepositoryName, c);
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
	}

	public void init(String repositoryName) {

		this.textFieldRepositoryName.setText(repositoryName);
		this.textFieldRepositoryName.setCaretPosition(0);
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

	private void buttonImportPerformed() {

		if (buttonImport.getText().equals("Cancel")) {
			if (connexion != null) {
				connexion.cancel();
			}
			connexionCanceled = true;
		} else {
			connexionCanceled = false;
			importAutoConfig();
		}
	}

	public void importAutoConfig() {

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
					JOptionPane.showMessageDialog(repositoryEditionDialog,
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
					protocol.setConnectionTimeOut("0");
					protocol.setReadTimeOut("0");
				} catch (CheckException e) {
					JOptionPane.showMessageDialog(repositoryEditionDialog,
							e.getMessage(), "Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				assert (protocol != null);

				try {
					connexion = ConnexionServiceFactory
							.getServiceForAutoconfigURLimportation(protocol);
				} catch (CheckException e) {
					JOptionPane.showMessageDialog(repositoryEditionDialog,
							e.getMessage(), "Error",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				assert (connexion != null);

				buttonImport.setText("Cancel");
				labelConnection.setText("Connecting to repository...");
				labelConnection.setFont(labelConnection.getFont().deriveFont(
						Font.ITALIC));
				labelConnection.setForeground(Color.BLACK);

				try {
					AutoConfigDTO autoConfigDTO = connexion
							.importAutoConfig(protocol);
					
					if (autoConfigDTO != null) {

						labelConnection.setText("Connection success!");
						labelConnection.setFont(labelConnection.getFont()
								.deriveFont(Font.ITALIC));
						labelConnection.setForeground(new Color(45, 125, 45));

						// Init Repository Section
						init(autoConfigDTO.getRepositoryName());

						// Init Protocol Section
						ProtocolDTO protocolDTO = autoConfigDTO
								.getProtocoleDTO();
						ProtocolType protocolType = protocolDTO
								.getProtocolType();
						repositoryEditionDialog.getComboBoxProtocolModel()
								.setSelectedItem(protocolType.getDescription());

						// Init Connection Setion
						repositoryEditionDialog.getConnectionPanel().init(
								protocolDTO);
					} else {
						throw new RemoteAutoconfigFileNotFoundException();
					}
				} catch (Exception e) {
					if (!connexionCanceled) {
						labelConnection.setText("Connection failed!");
						labelConnection.setFont(labelConnection.getFont()
								.deriveFont(Font.ITALIC));
						labelConnection.setForeground(Color.RED);
						JOptionPane.showMessageDialog(repositoryEditionDialog,
								e.getMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				} finally {
					buttonImport.setText("Import");
					if (connexion != null) {
						connexion.cancel();
					}
				}
			}
		});
		t.start();
	}

	public String getRepositoryName() throws CheckException {

		String repositoryName = this.textFieldRepositoryName.getText().trim();

		/* Check validity of repository name */
		List<String> forbiddenCharactersList = new ArrayList<String>();
		forbiddenCharactersList.add("/");
		forbiddenCharactersList.add("\\");
		forbiddenCharactersList.add("*");
		forbiddenCharactersList.add("?");
		forbiddenCharactersList.add("\"");
		forbiddenCharactersList.add("<");
		forbiddenCharactersList.add(">");
		forbiddenCharactersList.add("|");

		for (String stg : forbiddenCharactersList) {
			if (repositoryName.contains(stg)) {
				String forbiddenCharactersLine = "";
				for (String s : forbiddenCharactersList) {
					forbiddenCharactersLine = forbiddenCharactersLine + " " + s;
				}
				String message = "Repository name must not contains special characters like:"
						+ forbiddenCharactersLine;
				throw new CheckException(message);
			}
		}

		return repositoryName;
	}
}
