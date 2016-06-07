package fr.soe.a3s.ui.repository.dialogs.connection;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dto.ProtocolDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.AbstractDialog;
import fr.soe.a3s.ui.Facade;

public class ProxyConfigurationDialog extends AbstractDialog implements
		DataAccessConstants {

	private ProtocolPanel protocolPanel;
	private ConnectionPanel connectionPanel;
	private DefaultComboBoxModel comboBoxProtocolModel = null;
	private ProtocolDTO proxyProtocolDTO;
	/* Service */
	private final RepositoryService repositoryService = new RepositoryService();

	public ProxyConfigurationDialog(Facade facade) {
		super(facade, "Configure proxy", true);
		this.setResizable(false);

		{
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			getRootPane().setDefaultButton(buttonOK);
		}
		{
			Box vBox = Box.createVerticalBox();
			this.add(vBox, BorderLayout.CENTER);
			{
				connectionPanel = new ConnectionPanel();
				protocolPanel = new ProtocolPanel(connectionPanel);
				vBox.add(protocolPanel);
				vBox.add(connectionPanel);
			}
			{
				protocolPanel.getLabelProtocol().setText("Proxy protocol");
				connectionPanel.getLabelHost().setText("Proxy host");
				connectionPanel.getLabelPort().setText("Proxy port");
				connectionPanel.getLabelLogin().setText("Proxy login");
				connectionPanel.getLabelPassword().setText("Proxy passord");
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

		/* Init Protocol Section */
		comboBoxProtocolModel = new DefaultComboBoxModel(new String[] {
				ProtocolType.FTP.getDescription(),
				ProtocolType.HTTP.getDescription(),
				ProtocolType.HTTPS.getDescription(),
				ProtocolType.SOCKS4.getDescription(),
				ProtocolType.SOCKS5.getDescription() });
		protocolPanel.init(comboBoxProtocolModel);

		/* Init Connection Section */
		connectionPanel.init();
	}

	public void init(String repositoryName) {

		/* Init Protocol Section */
		comboBoxProtocolModel = new DefaultComboBoxModel(new String[] {
				ProtocolType.FTP.getDescription(),
				ProtocolType.HTTP.getDescription(),
				ProtocolType.HTTPS.getDescription(),
				ProtocolType.SOCKS4.getDescription(),
				ProtocolType.SOCKS5.getDescription() });
		protocolPanel.init(comboBoxProtocolModel);

		/* Init Repository and Connection Section */
		try {
			RepositoryDTO repositoryDTO = repositoryService
					.getRepository(repositoryName);
			ProtocolDTO proxyProtocoleDTO = repositoryDTO
					.getProxyProtocoleDTO();
			if (proxyProtocoleDTO != null) {
				ProtocolType proxyProtocole = proxyProtocoleDTO
						.getProtocolType();
				comboBoxProtocolModel.setSelectedItem(proxyProtocole
						.getDescription());
				connectionPanel.init(proxyProtocoleDTO);
			} else {
				connectionPanel.init();
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected void buttonOKPerformed() {

		try {
			proxyProtocolDTO = new ProtocolDTO();
			ProtocolType protocolType = ProtocolType
					.getEnum((String) comboBoxProtocolModel.getSelectedItem());
			proxyProtocolDTO.setUrl(connectionPanel.getUrl());
			proxyProtocolDTO.setPort(connectionPanel.getPort());
			proxyProtocolDTO.setLogin(connectionPanel.getLogin());
			proxyProtocolDTO.setPassword(connectionPanel.getPassword());
			proxyProtocolDTO.setProtocolType(protocolType);
			repositoryService.checkProxyProtocol(proxyProtocolDTO);
			this.setVisible(false);
		} catch (CheckException e) {
			proxyProtocolDTO = null;
			JOptionPane.showMessageDialog(this, e.getMessage(), "Warning",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	@Override
	protected void buttonCancelPerformed() {
		this.setVisible(false);
	}

	@Override
	protected void menuExitPerformed() {
		this.setVisible(false);
	}

	public ProtocolDTO getProxyProtocolDTO() {
		return proxyProtocolDTO;
	}
}
