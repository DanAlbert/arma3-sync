package fr.soe.a3s.ui.repository.dialogs.connection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import fr.soe.a3s.ui.AbstractDialog;
import fr.soe.a3s.ui.Facade;

public abstract class AbstractConnectionDialog extends AbstractDialog {

	private JButton buttonProxy;
	protected ProxyConfigurationDialog proxyConfigurationDialog;

	public AbstractConnectionDialog(Facade facade, String title, boolean modal) {
		super(facade, title, modal);

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
		buttonProxy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonProxyPerformed();
			}
		});
	}

	private void buttonProxyPerformed() {
		proxyConfigurationDialog.setVisible(true);
	}
}
