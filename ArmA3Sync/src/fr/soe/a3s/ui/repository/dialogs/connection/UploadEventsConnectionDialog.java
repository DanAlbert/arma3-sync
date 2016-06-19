package fr.soe.a3s.ui.repository.dialogs.connection;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.dto.ProtocolDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.AbstractDialog;
import fr.soe.a3s.ui.Facade;

public class UploadEventsConnectionDialog extends AbstractDialog {

	private ProtocolPanel protocolPanel;
	private ConnectionPanel connectionPanel;
	private OptionsPanel optionsPanel;
	// Data
	private String repositoryName;
	private DefaultComboBoxModel comboBoxProtocolModel;
	// Services
	private final RepositoryService repositoryService = new RepositoryService();

	public UploadEventsConnectionDialog(Facade facade) {
		super(facade, "Upload options", true);
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

	public void init(String repositoryName) {

		this.repositoryName = repositoryName;

		/* Init Protocol Section */
		comboBoxProtocolModel = new DefaultComboBoxModel(
				new String[] { ProtocolType.FTP.getDescription() });
		protocolPanel.init(comboBoxProtocolModel);

		/* Init Connection Section */
		try {
			RepositoryDTO repositoryDTO = repositoryService
					.getRepository(repositoryName);
			ProtocolDTO protocolDTO = repositoryDTO.getProtocoleDTO();
			ProtocolDTO uploadProtocolDTO = repositoryDTO
					.getUploadProtocoleDTO();
			if (uploadProtocolDTO != null) {
				connectionPanel.init(uploadProtocolDTO);
			} else if (protocolDTO.getProtocolType().equals(ProtocolType.FTP)) {
				connectionPanel.init(protocolDTO);
			} else {
				connectionPanel.init(ProtocolType.FTP);
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected void buttonOKPerformed() {

		ProtocolType protocolType = ProtocolType
				.getEnum((String) comboBoxProtocolModel.getSelectedItem());
		String url = connectionPanel.getUrl();
		String port = connectionPanel.getPort();
		String login = connectionPanel.getLogin();
		String password = connectionPanel.getPassword();

		try {
			repositoryService.setRepositoryUploadProtocole(repositoryName, url,
					port, login, password, protocolType, "0", "0");
			repositoryService.write(repositoryName);
			connectionPanel.clearPassword();
			this.dispose();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected void buttonCancelPerformed() {
		connectionPanel.clearPassword();
		this.dispose();
	}

	@Override
	protected void menuExitPerformed() {
		connectionPanel.clearPassword();
		this.dispose();
	}
}
