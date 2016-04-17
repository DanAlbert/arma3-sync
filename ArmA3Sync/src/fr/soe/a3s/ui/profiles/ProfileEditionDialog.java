package fr.soe.a3s.ui.profiles;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.soe.a3s.exception.ProfileException;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.service.ProfileService;
import fr.soe.a3s.ui.AbstractDialog;
import fr.soe.a3s.ui.Facade;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class ProfileEditionDialog extends AbstractDialog {

	private JTextField textFieldProfileName;
	private JLabel labelProfileName;
	private final String initProfileName;
	private final ProfileSelectionDialog profileSelectionDialog;
	// Services
	private final ProfileService profileService = new ProfileService();
	private final ConfigurationService configurationService = new ConfigurationService();

	public ProfileEditionDialog(Facade facade,
			ProfileSelectionDialog profileSelectionDialog, String title,
			String initProfileName) {

		super(facade, title, true);
		this.profileSelectionDialog = profileSelectionDialog;
		this.initProfileName = initProfileName;
		this.setResizable(false);

		{
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			getRootPane().setDefaultButton(buttonOK);
		}
		{
			Box hBox = Box.createHorizontalBox();

			labelProfileName = new JLabel();
			labelProfileName.setText("Profile Name");
			textFieldProfileName = new JTextField();
			if (initProfileName != null) {
				textFieldProfileName.setText(initProfileName);
				textFieldProfileName.selectAll();
			}

			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			{
				GridBagConstraints c1 = new GridBagConstraints();
				c1.fill = GridBagConstraints.HORIZONTAL;
				c1.weightx = 0.5;
				c1.weighty = 0;
				c1.gridx = 0;
				c1.gridy = 0;
				panel.add(labelProfileName, c1);
			}
			{
				GridBagConstraints c2 = new GridBagConstraints();
				c2.fill = GridBagConstraints.BOTH;
				c2.weightx = 0.5;
				c2.weighty = 0;
				c2.gridx = 0;
				c2.gridy = 1;
				panel.add(textFieldProfileName, c2);
			}

			hBox.add(panel);
			this.add(hBox, BorderLayout.CENTER);
		}

		this.pack();
		if (textFieldProfileName.getBounds().height < 25) {
			textFieldProfileName.setPreferredSize(new Dimension(this
					.getBounds().width, 25));
		}
		this.setMinimumSize(new Dimension(250, this.getBounds().height));
		this.setPreferredSize(new Dimension(250, this.getBounds().height));
		this.pack();
		setLocationRelativeTo(facade.getMainPanel());
	}

	@Override
	protected void buttonOKPerformed() {

		String newProfileName = textFieldProfileName.getText().trim();

		if (newProfileName.isEmpty()) {
			return;
		}
		try {
			// create a new profile
			if (initProfileName == null || initProfileName.isEmpty()) {
				profileService.createProfile(newProfileName);
			} else {// rename profile
				profileService.renameProfile(initProfileName, newProfileName);
			}
			this.profileSelectionDialog.init();
			this.profileSelectionDialog.selectProfile(newProfileName);
			this.dispose();
		} catch (ProfileException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
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
}
