package fr.soe.a3s.ui.repository.dialogs.connection;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class OptionsPanel extends JPanel {

	private JCheckBox checkBoxOptions;

	public OptionsPanel() {

		this.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Options"));

		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		this.add(panel);
		{
			checkBoxOptions = new JCheckBox();
			checkBoxOptions.setText("Upload only compressed pbo files");
		}
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.5;
			c.weighty = 0;
			c.gridx = 0;
			c.gridy = 0;
			c.insets = new Insets(5, 10, 5, 10);
			panel.add(checkBoxOptions, c);
		}
	}

	public void init(boolean enabled, boolean selected) {
		checkBoxOptions.setEnabled(enabled);
		checkBoxOptions.setSelected(selected);
	}

	public boolean isSelected() {
		return checkBoxOptions.isSelected();
	}
}
