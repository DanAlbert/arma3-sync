package fr.soe.a3s.ui.profileEditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.soe.a3s.exception.ProfileException;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.service.ProfileService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

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
public class ProfileEditionPanel extends JDialog implements UIConstants {

	private Facade facade;
	private JButton buttonOK, buttonCancel;
	private JTextField textFieldProfileName;
	private JLabel labelProfileName;
	private String initProfileName;
	private ProfileService profileService = new ProfileService();
	private ConfigurationService configurationService = new ConfigurationService();

	public ProfileEditionPanel(Facade facade, String title,
			String initProfileName) {
		
		super(facade.getProfilePanel(), title, true);
		this.facade = facade;
		this.initProfileName = initProfileName;
		this.setResizable(false);
		this.setSize(220, 120);
		setIconImage(ICON);
		this.setLocation((int) facade.getMainPanel().getLocation().getX()
				+ facade.getMainPanel().getWidth() / 2 - this.getWidth() / 2,
				(int) facade.getMainPanel().getLocation().getY()
						+ facade.getMainPanel().getHeight() / 2
						- this.getHeight() / 2);
		this.setLayout(new BorderLayout());

		Container contenu = getContentPane();
		JPanel controlPanel = new JPanel();
		buttonOK = new JButton("OK");
		getRootPane().setDefaultButton(buttonOK);
		buttonCancel = new JButton("Cancel");
		buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
		FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
		controlPanel.setLayout(flowLayout);
		controlPanel.add(buttonOK);
		controlPanel.add(buttonCancel);
		contenu.add(controlPanel, BorderLayout.SOUTH);
		{
			JPanel centerPanel = new JPanel();
			contenu.add(centerPanel, BorderLayout.CENTER);
			centerPanel.setLayout(null);
			centerPanel.setPreferredSize(new java.awt.Dimension(270, 122));
			{
				labelProfileName = new JLabel();
				centerPanel.add(labelProfileName);
				labelProfileName.setText("Profile Name");
				labelProfileName.setBounds(18, 12, 149, 15);
			}
			{
				textFieldProfileName = new JTextField();
				centerPanel.add(textFieldProfileName);
				textFieldProfileName.setBounds(18, 27, 179, 22);
				if (initProfileName != null) {
					textFieldProfileName.setText(initProfileName);
					textFieldProfileName.selectAll();
				}
			}
		}
		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonOKPerformed();
			}
		});
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonCancelPerformed();
			}
		});
		// Add Listeners
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				menuExitPerformed();
			}
		});
	}

	private void buttonOKPerformed() {

		String newProfileName = textFieldProfileName.getText().trim();

		if (newProfileName.isEmpty()) {
			return;
		}
		try {
			if (initProfileName == null || initProfileName.isEmpty()) {// create a new profile
				profileService.createProfile(newProfileName);
			} else {// rename profile
				profileService.renameProfile(initProfileName, newProfileName);
			}
			this.facade.getProfilePanel().init();
			this.facade.getProfilePanel().selectProfile(newProfileName);
			this.dispose();
		} catch (ProfileException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void buttonCancelPerformed() {
		this.dispose();
	}

	private void menuExitPerformed() {
		this.dispose();
	}
}
