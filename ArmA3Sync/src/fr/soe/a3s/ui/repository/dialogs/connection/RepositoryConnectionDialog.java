package fr.soe.a3s.ui.repository.dialogs.connection;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.domain.constants.ProtocolType;
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

public class RepositoryConnectionDialog extends AbstractDialog implements
		DataAccessConstants {

	private DescriptionPanel repositoryPanel;
	private ProtocolPanel protocolPanel;
	private ConnectionPanel connectionPanel;
	/* Data */
	private String initialRepositoryName = null;
	private DefaultComboBoxModel comboBoxProtocolModel;
	/* Service */
	private final RepositoryService repositoryService = new RepositoryService();

	public RepositoryConnectionDialog(Facade facade) {
		super(facade, "Repository", true);
		this.setResizable(false);

		{
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			getRootPane().setDefaultButton(buttonOK);
		}
		{
			Box vBox = Box.createVerticalBox();
			this.add(vBox, BorderLayout.CENTER);
			{
				repositoryPanel = new DescriptionPanel(this);
				vBox.add(repositoryPanel);
				protocolPanel = new ProtocolPanel();
				vBox.add(protocolPanel);
				connectionPanel = new ConnectionPanel();
				vBox.add(connectionPanel);
			}
		}

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
		repositoryPanel.init(repositoryName);

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
			connectionPanel.init(protocoleDTO);
			ProtocolType protocole = protocoleDTO.getProtocolType();
			comboBoxProtocolModel.setSelectedItem(protocole.getDescription());
		} catch (RepositoryException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected void buttonOKPerformed() {

		try {
			String newRepositoryName = repositoryPanel.getRepositoryName();
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
}
