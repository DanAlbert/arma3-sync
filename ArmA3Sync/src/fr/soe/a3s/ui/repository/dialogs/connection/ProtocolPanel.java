package fr.soe.a3s.ui.repository.dialogs.connection;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.soe.a3s.constant.ProtocolType;

public class ProtocolPanel extends JPanel {

	private JLabel labelProtocol;
	private JComboBox comboBoxProtocol;
	private final ConnectionPanel connectionPanel;

	public ProtocolPanel(ConnectionPanel connectionPanel) {

		this.connectionPanel = connectionPanel;

		this.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Protocol"));

		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		this.add(panel);
		{
			labelProtocol = new JLabel();
			labelProtocol.setText("File transfer protocol:");
			comboBoxProtocol = new JComboBox();
			comboBoxProtocol.setFocusable(false);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 20;
			c.weighty = 0;
			c.gridx = 0;
			c.gridy = 0;
			c.insets = new Insets(0, 10, 10, 10);
			panel.add(labelProtocol, c);
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 0;
			c.weighty = 0;
			c.gridx = 1;
			c.gridy = 0;
			c.insets = new Insets(0, 10, 10, 10);
			panel.add(comboBoxProtocol, c);
		}

		comboBoxProtocol.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				comboBoxProtocolPerformed();
			}
		});
	}

	public void init(ComboBoxModel comboBoxProtocolModel) {
		comboBoxProtocol.setModel(comboBoxProtocolModel);
	}

	private void comboBoxProtocolPerformed() {

		String description = (String) this.comboBoxProtocol.getSelectedItem();
		ProtocolType protocolType = ProtocolType.getEnum(description);
		if (protocolType != null) {
			this.connectionPanel.init(protocolType);
		}
	}

	public void activate(boolean value) {
		comboBoxProtocol.setEnabled(value);
	}

	public JLabel getLabelProtocol() {
		return labelProtocol;
	}
}
