package fr.soe.a3s.ui.profileEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.constant.DefaultProfileName;
import fr.soe.a3s.dto.configuration.LauncherOptionsDTO;
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
public class ProfilePanel extends JDialog implements UIConstants {

	private final Facade facade;
	private JButton buttonOK, buttonCancel;
	private JList profilesList;
	private JButton buttonDuplicate;
	private JButton buttonNew;
	private JButton buttonRemove;
	private JButton buttonEdit;
	private JScrollPane scrollPane;
	private String initProfileName;
	private final ProfileService profileService = new ProfileService();
	private final ConfigurationService configurationService = new ConfigurationService();

	public ProfilePanel(Facade facade) {
		super(facade.getMainPanel(), "Profiles", true);
		this.facade = facade;
		this.facade.setProfilePanel(this);
		this.setResizable(false);
		this.setSize(285, 256);
		setIconImage(ICON);
		this.setLocation(
				(int) facade.getMainPanel().getLocation().getX()
						+ facade.getMainPanel().getWidth() / 2
						- this.getWidth() / 2,
				(int) facade.getMainPanel().getLocation().getY()
						+ facade.getMainPanel().getHeight() / 2
						- this.getHeight() / 2);
		this.setLayout(new BorderLayout());
		{
			JPanel controlPanel = new JPanel();
			buttonOK = new JButton("OK");
			buttonCancel = new JButton("Cancel");
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
			controlPanel.setLayout(flowLayout);
			controlPanel.add(buttonOK);
			controlPanel.add(buttonCancel);
			this.add(controlPanel, BorderLayout.SOUTH);
		}
		{
			JPanel centerPanel = new JPanel();
			profilesList = new JList();
			profilesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			scrollPane = new JScrollPane(profilesList);
			scrollPane.setColumnHeader(null);
			scrollPane.setBorder(BorderFactory
					.createEtchedBorder(BevelBorder.LOWERED));
			centerPanel.add(scrollPane);
			scrollPane.setBounds(6, 5, 171, 173);
			{
				buttonNew = new JButton("New");
				centerPanel.add(buttonNew);
				buttonNew.setBounds(183, 5, 87, 25);
				centerPanel.add(buttonNew);
			}
			{
				buttonDuplicate = new JButton();
				centerPanel.add(buttonDuplicate);
				buttonDuplicate.setText("Duplicate");
				buttonDuplicate.setBounds(183, 32, 87, 25);
			}
			{
				buttonEdit = new JButton();
				centerPanel.add(buttonEdit);
				buttonEdit.setText("Edit");
				buttonEdit.setBounds(183, 61, 87, 25);
			}
			{
				buttonRemove = new JButton();
				centerPanel.add(buttonRemove);
				buttonRemove.setText("Remove");
				buttonRemove.setBounds(183, 88, 87, 25);
			}
			this.add(centerPanel, BorderLayout.CENTER);
			centerPanel.setPreferredSize(new java.awt.Dimension(269, 182));
			centerPanel.setLayout(null);
		}
		{
			JPanel sidePanel1 = new JPanel();
			this.add(sidePanel1, BorderLayout.NORTH);
		}
		getRootPane().setDefaultButton(buttonOK);

		buttonNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonNewPerformed();
			}
		});
		buttonDuplicate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonDuplicatePerformed();
			}
		});
		buttonEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonEditPerformed();
			}
		});
		buttonRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonRemovePerformed();
			}
		});
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
			@Override
			public void windowClosing(WindowEvent e) {
				menuExitPerformed();
			}
		});
		init();
	}

	public void init() {

		List<String> profileNames = profileService.getProfileNames();
		initProfileName = configurationService.getProfileName();
		assert (initProfileName != null);
		String[] names = new String[profileNames.size()];
		boolean contains = false;
		for (int i = 0; i < profileNames.size(); i++) {
			names[i] = profileNames.get(i);
			if (profileNames.contains(initProfileName)) {
				contains = true;
			}
		}

		profilesList.clearSelection();
		profilesList.setListData(names);
		if (contains) {
			profilesList.setSelectedValue(initProfileName, true);
		} else {
			profilesList.setSelectedValue(
					DefaultProfileName.DEFAULT.getDescription(), true);
		}
		int numberLigneShown = profileNames.size();
		profilesList.setVisibleRowCount(numberLigneShown);
		profilesList.setPreferredSize(profilesList
				.getPreferredScrollableViewportSize());
		scrollPane.updateUI();
	}

	private void buttonNewPerformed() {
		String profileName = (String) profilesList.getSelectedValue();
		configurationService.setProfileName(profileName);
		ProfileEditionPanel profileEditionPanel = new ProfileEditionPanel(
				facade, "New Profile", "");
		profileEditionPanel.setVisible(true);
	}

	private void buttonEditPerformed() {
		String profileName = (String) profilesList.getSelectedValue();
		if (profileName.equals(DefaultProfileName.DEFAULT.getDeclaringClass())) {
			return;
		}
		ProfileEditionPanel profileEditionPanel = new ProfileEditionPanel(
				facade, "Edit Profile", profileName);
		profileEditionPanel.setVisible(true);

	}

	private void buttonDuplicatePerformed() {
		String profileName = (String) profilesList.getSelectedValue();
		if (profileName.contains("Duplicate")) {
			return;
		} else {
			String duplicateProfileName = profileName + " - Duplicate";
			profileService.duplicateProfile(profileName, duplicateProfileName);
			init();
			profilesList.setSelectedValue(duplicateProfileName, true);
		}
	}

	private void buttonRemovePerformed() {

		String profileName = (String) profilesList.getSelectedValue();

		if (profileName.equals(DefaultProfileName.DEFAULT.getDescription())) {
			JOptionPane.showMessageDialog(this,
					"Default profile can't be removed.", "Information",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		int option = JOptionPane.showConfirmDialog(this,
				"Remove selected profile?", "Confirmation",
				JOptionPane.OK_CANCEL_OPTION);
		if (option == 0) {
			try {
				profileService.removeProfile(profileName);
				init();
			} catch (ProfileException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void buttonOKPerformed() {
		
		String profileName = (String) profilesList.getSelectedValue();
		if (profileName == null) {
			JOptionPane.showMessageDialog(this, "You must select a profile.",
					"Information", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		configurationService.setProfileName(profileName);
		facade.getInfoPanel().init();
		facade.getAddonsPanel().init();
		facade.getAddonOptionsPanel().init();
		facade.getLaunchOptionsPanel().init();
		facade.getMainPanel().updateProfilesMenu();
		this.dispose();
	}

	private void buttonCancelPerformed() {

		List<String> profileNames = profileService.getProfileNames();
		if (!profileNames.contains(initProfileName)) {
			initProfileName = DefaultProfileName.DEFAULT.getDescription();
		}
		configurationService.setProfileName(initProfileName);
		facade.getInfoPanel().init();
		this.dispose();
	}

	private void menuExitPerformed() {

		List<String> profileNames = profileService.getProfileNames();
		if (!profileNames.contains(initProfileName)) {
			initProfileName = DefaultProfileName.DEFAULT.getDescription();
		}
		configurationService.setProfileName(initProfileName);
		facade.getInfoPanel().init();
		this.dispose();
	}

	public void selectProfile(String profileName) {
		profilesList.setSelectedValue(profileName, true);
	}
}
