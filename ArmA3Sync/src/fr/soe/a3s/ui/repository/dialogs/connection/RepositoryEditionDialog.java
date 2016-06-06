package fr.soe.a3s.ui.repository.dialogs.connection;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dto.ProtocolDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.exception.repository.RepositoryNotFoundException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.AbstractDialog;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repository.dialogs.progress.ProgressSynchronizationDialog;

public class RepositoryEditionDialog extends AbstractDialog implements
		DataAccessConstants {

	private DescriptionPanel descriptionPanel;
	private ProtocolPanel protocolPanel;
	private ConnectionPanel connectionPanel;
	private JButton buttonProxy;
	private ProxyConfigurationDialog proxyConfigurationDialog;
	/* Data */
	private String initialRepositoryName = null;
	private DefaultComboBoxModel comboBoxProtocolModel = null;
	/* Service */
	private final RepositoryService repositoryService = new RepositoryService();

	public RepositoryEditionDialog(Facade facade) {
		super(facade, "Repository", true);
		this.setResizable(false);

		{
			buttonProxy = new JButton("Proxy");
			panelControl.removeAll();
			panelControl.add(buttonProxy);
			panelControl.add(buttonOK);
			panelControl.add(buttonCancel);
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			getRootPane().setDefaultButton(buttonOK);
			buttonProxy.setPreferredSize(buttonCancel.getPreferredSize());
			proxyConfigurationDialog = new ProxyConfigurationDialog(facade);
		}
		{
			Box vBox = Box.createVerticalBox();
			this.add(vBox, BorderLayout.CENTER);
			{
				descriptionPanel = new DescriptionPanel(this);
				connectionPanel = new ConnectionPanel();
				protocolPanel = new ProtocolPanel(connectionPanel);
				vBox.add(descriptionPanel);
				vBox.add(protocolPanel);
				vBox.add(connectionPanel);
			}
		}

		buttonProxy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonProxyPerformed();
			}
		});

		this.pack();
		int height = this.getBounds().height;
		int width = this.getBounds().width;
		if (width < 450) {
			this.setPreferredSize(new Dimension(450, height));
		}
		this.pack();
		this.setLocationRelativeTo(facade.getMainPanel());
	}

	public void init() {

		this.setTitle("New repository");

		/* Init Protocol Section */
		comboBoxProtocolModel = new DefaultComboBoxModel(new String[] {
				ProtocolType.FTP.getDescription(),
				ProtocolType.HTTP.getDescription(),
				ProtocolType.HTTPS.getDescription() });
		protocolPanel.init(comboBoxProtocolModel);

		/* Init Connection Section */
		connectionPanel.init();
	}

	public void init(String repositoryName) {

		this.setTitle("Edit repository");
		this.initialRepositoryName = repositoryName;

		/* Init Repository Section */
		descriptionPanel.init(repositoryName);

		/* Init Protocol Section */
		comboBoxProtocolModel = new DefaultComboBoxModel(new String[] {
				ProtocolType.FTP.getDescription(),
				ProtocolType.HTTP.getDescription(),
				ProtocolType.HTTPS.getDescription() });
		protocolPanel.init(comboBoxProtocolModel);

		/* Init Repository and Connection Section */
		try {
			RepositoryDTO repositoryDTO = repositoryService
					.getRepository(repositoryName);
			ProtocolDTO protocoleDTO = repositoryDTO.getProtocoleDTO();
			ProtocolType protocole = protocoleDTO.getProtocolType();
			comboBoxProtocolModel.setSelectedItem(protocole.getDescription());
			connectionPanel.init(protocoleDTO);
		} catch (RepositoryException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected void buttonOKPerformed() {

		try {
			String newRepositoryName = descriptionPanel.getRepositoryName();
			ProtocolType protocolType = ProtocolType
					.getEnum((String) comboBoxProtocolModel.getSelectedItem());
			String url = connectionPanel.getUrl();
			String port = connectionPanel.getPort();
			String login = connectionPanel.getLogin();
			String password = connectionPanel.getPassword();

			if (initialRepositoryName != null) {// Edit Repository
				if (initialRepositoryName.equals(newRepositoryName)) {
					repositoryService.setRepository(initialRepositoryName, url,
							port, login, password, protocolType);
				} else {
					repositoryService.renameRepository(initialRepositoryName,
							newRepositoryName);
					repositoryService.setRepository(newRepositoryName, url,
							port, login, password, protocolType);
				}
				repositoryService
						.resetRepositoryUploadProtocol(newRepositoryName);
			} else {// New Repository
				repositoryService.createRepository(newRepositoryName, url,
						port, login, password, protocolType);
			}

			ProtocolDTO proxyProtocolDTO = proxyConfigurationDialog
					.getProxyProtocolDTO();
			repositoryService.setProxyProtocol(newRepositoryName,
					proxyProtocolDTO);
			repositoryService.write(newRepositoryName);

			this.dispose();
			ProgressSynchronizationDialog synchronizingPanel = new ProgressSynchronizationDialog(
					facade);
			synchronizingPanel.setVisible(true);
			synchronizingPanel.init(newRepositoryName);
		} catch (CheckException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Warning",
					JOptionPane.WARNING_MESSAGE);
		} catch (RepositoryNotFoundException | WritingException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void buttonProxyPerformed() {

		proxyConfigurationDialog.init(this.initialRepositoryName);
		proxyConfigurationDialog.setVisible(true);
	}

	@Override
	protected void menuExitPerformed() {
		connectionPanel.clearPassword();
		this.dispose();
	}

	@Override
	protected void buttonCancelPerformed() {
		connectionPanel.clearPassword();
		this.dispose();
	}

	public ConnectionPanel getConnectionPanel() {
		return this.connectionPanel;
	}

	public DefaultComboBoxModel getComboBoxProtocolModel() {
		return this.comboBoxProtocolModel;
	}

	public ProxyConfigurationDialog getProxyConfigurationDialog() {
		return proxyConfigurationDialog;
	}
}
