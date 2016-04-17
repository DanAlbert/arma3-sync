package fr.soe.a3s.ui.groups;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.soe.a3s.ui.AbstractDialog;
import fr.soe.a3s.ui.Facade;

public abstract class AbstractGroupDialog extends AbstractDialog {

	protected JTextField textFieldGroupName;
	protected JLabel labelGroupName;
	protected JLabel labelWarning;

	public AbstractGroupDialog(Facade facade, String title, boolean modal) {
		super(facade, title, modal);
		this.setResizable(false);

		{
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			getRootPane().setDefaultButton(buttonOK);
		}
		{
			labelGroupName = new JLabel();
			labelGroupName.setText("Group name");
			labelWarning = new JLabel();
			textFieldGroupName = new JTextField();
			textFieldGroupName.requestFocusInWindow();

			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			{
				GridBagConstraints c1 = new GridBagConstraints();
				c1.fill = GridBagConstraints.HORIZONTAL;
				c1.weightx = 0.5;
				c1.weighty = 0;
				c1.gridx = 0;
				c1.gridy = 0;
				c1.gridwidth = 1;
				c1.gridheight = 1;
				panel.add(labelGroupName, c1);
			}
			{
				GridBagConstraints c2 = new GridBagConstraints();
				c2.fill = GridBagConstraints.HORIZONTAL;
				c2.weightx = 0.5;
				c2.weighty = 0;
				c2.gridx = 1;
				c2.gridy = 0;
				c2.gridwidth = 1;
				c2.gridheight = 1;
				panel.add(labelWarning, c2);
			}
			{
				GridBagConstraints c3 = new GridBagConstraints();
				c3.fill = GridBagConstraints.BOTH;
				c3.weightx = 0;
				c3.weighty = 0;
				c3.gridx = 0;
				c3.gridy = 1;
				c3.gridwidth = 2;
				c3.gridheight = 1;
				panel.add(textFieldGroupName, c3);
			}
			this.add(panel, BorderLayout.CENTER);
		}

		this.pack();
		this.setMinimumSize(new Dimension(250, this.getSize().height));
		this.setPreferredSize(new Dimension(250, this.getSize().height));
		this.pack();
		setLocationRelativeTo(facade.getMainPanel());
	}
}
