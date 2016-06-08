package fr.soe.a3s.ui.repository.dialogs.connection;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class ProxyPanel extends JPanel {

	private JCheckBox checkBoxProxy;

	public ProxyPanel() {

		this.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Proxy"));

		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		this.add(panel);
		{
			checkBoxProxy = new JCheckBox();
			checkBoxProxy.setText("Enable proxy");
			checkBoxProxy.setFocusable(false);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 20;
			c.weighty = 0;
			c.gridx = 0;
			c.gridy = 0;
			c.insets = new Insets(0, 10, 10, 10);
			panel.add(checkBoxProxy, c);
		}
	}

	public JCheckBox getCheckBoxProxy() {
		return checkBoxProxy;
	}
}
