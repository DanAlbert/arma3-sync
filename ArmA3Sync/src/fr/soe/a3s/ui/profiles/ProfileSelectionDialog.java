package fr.soe.a3s.ui.profiles;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.constant.DefaultProfileName;
import fr.soe.a3s.exception.ProfileException;
import fr.soe.a3s.exception.WritingException;
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
public class ProfileSelectionDialog extends AbstractDialog {

	private JList profilesList;
	private JButton buttonDuplicate;
	private JButton buttonNew;
	private JButton buttonRemove;
	private JButton buttonEdit;
	private JScrollPane scrollPane;
	private String initProfileName;
	// Services
	private final ProfileService profileService = new ProfileService();
	private final ConfigurationService configurationService = new ConfigurationService();

	public ProfileSelectionDialog(Facade facade) {
		super(facade, "Profiles", true);
		this.setResizable(true);

		{
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			getRootPane().setDefaultButton(buttonOK);
		}
		{
			JPanel centerPanel = new JPanel();
			centerPanel.setLayout(new BorderLayout());
			this.add(centerPanel, BorderLayout.CENTER);
			{
				profilesList = new JList();
				profilesList
						.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				scrollPane = new JScrollPane(profilesList);
				scrollPane.setColumnHeader(null);
				scrollPane.setBorder(BorderFactory
						.createEtchedBorder(BevelBorder.LOWERED));
				centerPanel.add(scrollPane, BorderLayout.CENTER);
			}
			{
				JPanel componentsPanel = new JPanel();
				FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
				componentsPanel.setLayout(flowLayout);

				buttonNew = new JButton("New");
				buttonDuplicate = new JButton("Duplicate");
				buttonEdit = new JButton("Edit");
				buttonRemove = new JButton("Remove");

				JPanel panel = new JPanel();
				panel.setLayout(new GridBagLayout());
				{
					GridBagConstraints c1 = new GridBagConstraints();
					c1.fill = GridBagConstraints.BOTH;
					c1.weightx = 0.5;
					c1.weighty = 0;
					c1.gridx = 0;
					c1.gridy = 0;
					c1.insets = new Insets(0, 0, 5, 0);
					panel.add(buttonNew, c1);
				}
				{
					GridBagConstraints c2 = new GridBagConstraints();
					c2.fill = GridBagConstraints.BOTH;
					c2.weightx = 0.5;
					c2.weighty = 0;
					c2.gridx = 0;
					c2.gridy = 1;
					c2.insets = new Insets(0, 0, 5, 0);
					panel.add(buttonDuplicate, c2);
				}
				{
					GridBagConstraints c3 = new GridBagConstraints();
					c3.fill = GridBagConstraints.BOTH;
					c3.weightx = 0.5;
					c3.weighty = 0;
					c3.gridx = 0;
					c3.gridy = 2;
					c3.insets = new Insets(0, 0, 5, 0);
					panel.add(buttonEdit, c3);
				}
				{
					GridBagConstraints c4 = new GridBagConstraints();
					c4.fill = GridBagConstraints.BOTH;
					c4.weightx = 0.5;
					c4.weighty = 0.1;
					c4.gridx = 0;
					c4.gridy = 3;
					c4.insets = new Insets(0, 0, 5, 0);
					panel.add(buttonRemove, c4);
				}

				componentsPanel.add(panel);
				centerPanel.add(componentsPanel, BorderLayout.EAST);
			}
		}

		this.setMinimumSize(new Dimension(350, 350));
		this.setPreferredSize(new Dimension(350, 350));
		this.pack();
		this.setLocationRelativeTo(facade.getMainPanel());

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
		ProfileEditionDialog profileEditionPanel = new ProfileEditionDialog(
				facade, this, "New Profile", "");
		profileEditionPanel.setVisible(true);
	}

	private void buttonEditPerformed() {
		String profileName = (String) profilesList.getSelectedValue();
		if (profileName.equals(DefaultProfileName.DEFAULT.getDeclaringClass())) {
			return;
		}
		ProfileEditionDialog profileEditionPanel = new ProfileEditionDialog(
				facade, this, "Edit Profile", profileName);
		profileEditionPanel.setVisible(true);

	}

	private void buttonDuplicatePerformed() {

		String profileName = (String) profilesList.getSelectedValue();
		if (profileName.contains("Duplicate")) {
			return;
		} else {
			try {
				String duplicateProfileName = profileName + " - Duplicate";
				profileService.duplicateProfile(profileName,
						duplicateProfileName);
				init();
				profilesList.setSelectedValue(duplicateProfileName, true);
			} catch (WritingException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
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

	@Override
	protected void buttonOKPerformed() {

		String profileName = (String) profilesList.getSelectedValue();
		if (profileName == null) {
			JOptionPane.showMessageDialog(this, "You must select a profile.",
					"Information", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		configurationService.setProfileName(profileName);
		facade.getMainPanel().updateTabs(OP_PROFILE_CHANGED);
		this.dispose();
	}

	@Override
	protected void buttonCancelPerformed() {

		List<String> profileNames = profileService.getProfileNames();
		if (!profileNames.contains(initProfileName)) {
			initProfileName = DefaultProfileName.DEFAULT.getDescription();
		}
		configurationService.setProfileName(initProfileName);
		facade.getMainPanel().updateTabs(OP_PROFILE_CHANGED);
		this.dispose();
	}

	@Override
	protected void menuExitPerformed() {

		List<String> profileNames = profileService.getProfileNames();
		if (!profileNames.contains(initProfileName)) {
			initProfileName = DefaultProfileName.DEFAULT.getDescription();
		}
		configurationService.setProfileName(initProfileName);
		facade.getMainPanel().updateTabs(OP_PROFILE_CHANGED);
		this.dispose();
	}

	public void selectProfile(String profileName) {
		profilesList.setSelectedValue(profileName, true);
	}
}
