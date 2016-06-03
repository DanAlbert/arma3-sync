package fr.soe.a3s.ui.repository.dialogs.connection;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;

import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.ui.AbstractDialog;
import fr.soe.a3s.ui.Facade;

public class ProxyConfigurationDialog extends AbstractDialog implements
		DataAccessConstants {

	private ProtocolPanel protocolPanel;
	private ConnectionPanel connectionPanel;
	private DefaultComboBoxModel comboBoxProtocolModel = null;

	public ProxyConfigurationDialog(Facade facade) {
		super(facade, "Configure proxy server", true);
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

		/* Init Protocol Section */
		comboBoxProtocolModel = new DefaultComboBoxModel(
				new String[] { ProtocolType.HTTP.getDescription() });
		protocolPanel.init(comboBoxProtocolModel);

		/* Init Connection Section */
		connectionPanel.init();
	}

	@Override
	protected void buttonOKPerformed() {

	}

	@Override
	protected void buttonCancelPerformed() {
		this.dispose();
	}

	@Override
	protected void menuExitPerformed() {
		this.dispose();
	}
}
