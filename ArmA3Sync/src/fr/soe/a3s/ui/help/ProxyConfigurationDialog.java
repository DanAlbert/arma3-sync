package fr.soe.a3s.ui.help;

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
import fr.soe.a3s.dto.configuration.ProxyDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.ui.AbstractDialog;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repository.dialogs.connection.ConnectionPanel;
import fr.soe.a3s.ui.repository.dialogs.connection.ProtocolPanel;
import fr.soe.a3s.ui.repository.dialogs.connection.ProxyPanel;

public class ProxyConfigurationDialog extends AbstractDialog implements
		DataAccessConstants {

	private ProxyPanel proxyPanel;
	private ProtocolPanel protocolPanel;
	private ConnectionPanel connectionPanel;
	private DefaultComboBoxModel comboBoxProtocolModel = null;
	// Services
	private final ConfigurationService configurationService = new ConfigurationService();

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
		comboBoxProtocolModel = new DefaultComboBoxModel(new String[] {
				ProtocolType.FTP.getDescription(),
				ProtocolType.HTTP.getDescription(),
				ProtocolType.HTTPS.getDescription() });
		protocolPanel.init(comboBoxProtocolModel);

		/* Init Connection Section */
		ProxyDTO proxyDTO = configurationService.getProxy();
		if (proxyDTO.getProtocolDTO() == null) {
			comboBoxProtocolModel.setSelectedItem(ProtocolType.FTP
					.getDescription());
			connectionPanel.init(ProtocolType.FTP);
			connectionPanel.activate(false);
			protocolPanel.activate(false);
			proxyPanel.getCheckBoxProxy().setSelected(false);
		} else {
			comboBoxProtocolModel.setSelectedItem(proxyDTO.getProtocolDTO()
					.getProtocolType().getDescription());
			connectionPanel.init(proxyDTO.getProtocolDTO());
			connectionPanel.activate(proxyDTO.isEnableProxy());
			protocolPanel.activate(proxyDTO.isEnableProxy());
			proxyPanel.getCheckBoxProxy().setSelected(proxyDTO.isEnableProxy());
		}
	}

	private void enableProxyPerformed() {

		if (proxyPanel.getCheckBoxProxy().isSelected()) {
			connectionPanel.activate(true);
			protocolPanel.activate(true);
		} else {
			connectionPanel.activate(false);
			protocolPanel.activate(false);
		}
	}

	@Override
	protected void buttonOKPerformed() {

		try {
			ProtocolDTO proxyProtocolDTO = new ProtocolDTO();
			if (connectionPanel.getUrl().isEmpty()){
				configurationService.setProxy(null, false);
			}else {
				ProtocolType protocolType = ProtocolType
						.getEnum((String) comboBoxProtocolModel.getSelectedItem());
				proxyProtocolDTO.setUrl(connectionPanel.getUrl());
				proxyProtocolDTO.setPort(connectionPanel.getPort());
				proxyProtocolDTO.setLogin(connectionPanel.getLogin());
				proxyProtocolDTO.setPassword(connectionPanel.getPassword());
				proxyProtocolDTO.setProtocolType(protocolType);
				configurationService.setProxy(proxyProtocolDTO, isEnableProxy());
			}
			configurationService.loadProxy();
			this.dispose();
		} catch (CheckException e) {
			if (isEnableProxy()) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Warning",
						JOptionPane.WARNING_MESSAGE);
			} else {
				this.dispose();
			}
		}
	}

	@Override
	protected void buttonCancelPerformed() {
		this.dispose();
	}

	@Override
	protected void menuExitPerformed() {
		this.dispose();
	}

	public boolean isEnableProxy() {
		return this.proxyPanel.getCheckBoxProxy().isSelected();
	}
}
