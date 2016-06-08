package fr.soe.a3s.ui.repository.dialogs.connection;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dto.ProtocolDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.AbstractDialog;
import fr.soe.a3s.ui.Facade;

public class ProxyConfigurationDialog extends AbstractDialog implements
		DataAccessConstants {

	private ProxyPanel proxyPanel;
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
				proxyPanel = new ProxyPanel();
				connectionPanel = new ConnectionPanel();
				protocolPanel = new ProtocolPanel(connectionPanel);
				vBox.add(proxyPanel);
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

		proxyPanel.getCheckBoxProxy().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enableProxyPerformed();
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

		/* Init Protocol Section */
		comboBoxProtocolModel = new DefaultComboBoxModel(
				new String[] { ProtocolType.HTTP.getDescription() });
		protocolPanel.init(comboBoxProtocolModel);

		/* Init Connection Section */
		connectionPanel.init(ProtocolType.HTTP);
		connectionPanel.activate(false);

		/* Init Proxy panel */
		proxyPanel.getCheckBoxProxy().setSelected(false);
	}

	public void init(ProtocolDTO proxyProtocoleDTO, boolean isEnableProxy) {

		/* Init Protocol Section */
		comboBoxProtocolModel = new DefaultComboBoxModel(
				new String[] { ProtocolType.HTTP.getDescription() });
		protocolPanel.init(comboBoxProtocolModel);

		/* Init Connection and Proxy Section */
		connectionPanel.init(ProtocolType.HTTP);
		connectionPanel.activate(false);
		proxyPanel.getCheckBoxProxy().setSelected(false);

		if (proxyProtocoleDTO != null) {
			connectionPanel.init(proxyProtocoleDTO);
			connectionPanel.activate(isEnableProxy);
			proxyPanel.getCheckBoxProxy().setSelected(isEnableProxy);
		}
	}

	private void enableProxyPerformed() {

		if (proxyPanel.getCheckBoxProxy().isSelected()) {
			connectionPanel.activate(true);
		} else {
			connectionPanel.activate(false);
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
			if (isEnableProxy()) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Warning",
						JOptionPane.WARNING_MESSAGE);
			} else {
				this.setVisible(false);
			}
		}
	}

	@Override
	protected void buttonCancelPerformed() {
		if (proxyProtocolDTO == null) {
			proxyPanel.getCheckBoxProxy().setSelected(false);
			connectionPanel.activate(false);
		}
		this.setVisible(false);
	}

	@Override
	protected void menuExitPerformed() {
		if (proxyProtocolDTO == null) {
			proxyPanel.getCheckBoxProxy().setSelected(false);
			connectionPanel.activate(false);
		}
		this.setVisible(false);
	}

	public ProtocolDTO getProxyProtocolDTO() {
		return proxyProtocolDTO;
	}

	public boolean isEnableProxy() {
		return this.proxyPanel.getCheckBoxProxy().isSelected();
	}
}
