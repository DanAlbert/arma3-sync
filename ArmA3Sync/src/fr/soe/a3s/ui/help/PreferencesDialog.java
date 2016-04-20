package fr.soe.a3s.ui.help;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemTray;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fr.soe.a3s.constant.LookAndFeel;
import fr.soe.a3s.constant.MinimizationType;
import fr.soe.a3s.dto.configuration.PreferencesDTO;
import fr.soe.a3s.service.PreferencesService;
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
public class PreferencesDialog extends AbstractDialog {

	private JCheckBox checkBoxLauncherMinimized;
	private JComboBox comboBoxLauncherMinimized;
	private JComboBox comboBoxLookAndFeel;
	private JCheckBox checkBoxLookAndFeel;
	private JComboBox comboBoxGameLaunch;
	private JCheckBox checkBoxGameLaunch;
	// Service
	private final PreferencesService preferencesServices = new PreferencesService();

	public PreferencesDialog(Facade facade) {
		super(facade, "Preferences", true);
		this.setResizable(false);

		{
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			getRootPane().setDefaultButton(buttonOK);
		}
		{
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(), ""));
			panel.setLayout(new GridBagLayout());
			this.add(panel, BorderLayout.CENTER);
			{
				checkBoxGameLaunch = new JCheckBox();
				checkBoxGameLaunch.setText("At game launch:");
				checkBoxGameLaunch.setSelected(true);
				checkBoxGameLaunch.setFocusable(false);
			}
			{
				String[] tab = null;
				if (SystemTray.isSupported()) {
					tab = new String[] {
							MinimizationType.NOTHING.getDescription(),
							MinimizationType.TASK_BAR.getDescription(),
							MinimizationType.TRAY.getDescription(),
							MinimizationType.CLOSE.getDescription() };
				} else {
					tab = new String[] {
							MinimizationType.NOTHING.getDescription(),
							MinimizationType.TASK_BAR.getDescription() };
				}

				ComboBoxModel comboBoxGameLaunchModel = new DefaultComboBoxModel(
						tab);
				comboBoxGameLaunch = new JComboBox();
				comboBoxGameLaunch.setModel(comboBoxGameLaunchModel);
				comboBoxGameLaunch.setFocusable(false);
			}
			{
				checkBoxLauncherMinimized = new JCheckBox();
				checkBoxLauncherMinimized.setText("When minimized:");
				checkBoxLauncherMinimized.setSelected(true);
				checkBoxLauncherMinimized.setFocusable(false);
			}
			{
				String[] tab = null;
				if (SystemTray.isSupported()) {
					tab = new String[] {
							MinimizationType.TASK_BAR.getDescription(),
							MinimizationType.TRAY.getDescription() };
				} else {
					tab = new String[] { MinimizationType.TASK_BAR
							.getDescription() };
				}
				ComboBoxModel comboBoxLauncherMinimizedModel = new DefaultComboBoxModel(
						tab);
				comboBoxLauncherMinimized = new JComboBox();
				comboBoxLauncherMinimized
						.setModel(comboBoxLauncherMinimizedModel);
				comboBoxLauncherMinimized.setFocusable(false);
			}
			{
				checkBoxLookAndFeel = new JCheckBox();
				checkBoxLookAndFeel.setSelected(true);
				checkBoxLookAndFeel.setFocusable(false);
				checkBoxLookAndFeel.setText("Look & Feel:");
			}
			{
				String[] tab = new String[] { "Default",
						LookAndFeel.LAF_ALUMINIUM.getName(),
						LookAndFeel.LAF_GRAPHITE.getName(),
						LookAndFeel.LAF_HIFI.getName(),
						LookAndFeel.LAF_NOIRE.getName() };

				ComboBoxModel comboBoxLookAndFeelModel = new DefaultComboBoxModel(
						tab);
				comboBoxLookAndFeel = new JComboBox();
				comboBoxLookAndFeel.setModel(comboBoxLookAndFeelModel);
				comboBoxLookAndFeel.setFocusable(false);
			}
			{
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 0.5;
				c.weighty = 0;
				c.gridx = 0;
				c.gridy = 0;
				c.insets = new Insets(5, 10, 5, 10);
				panel.add(checkBoxGameLaunch, c);
			}
			{
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.BOTH;
				c.weightx = 0.5;
				c.weighty = 0;
				c.gridx = 1;
				c.gridy = 0;
				c.insets = new Insets(5, 10, 5, 10);
				panel.add(comboBoxGameLaunch, c);
			}
			{
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 0.5;
				c.weighty = 0;
				c.gridx = 0;
				c.gridy = 1;
				c.insets = new Insets(5, 10, 5, 10);
				panel.add(checkBoxLauncherMinimized, c);
			}
			{
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.BOTH;
				c.weightx = 0.5;
				c.weighty = 0;
				c.gridx = 1;
				c.gridy = 1;
				c.insets = new Insets(5, 10, 5, 10);
				panel.add(comboBoxLauncherMinimized, c);
			}
			{
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 0.5;
				c.weighty = 0;
				c.gridx = 0;
				c.gridy = 2;
				c.insets = new Insets(5, 10, 5, 10);
				panel.add(checkBoxLookAndFeel, c);
			}
			{
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.BOTH;
				c.weightx = 0.5;
				c.weighty = 0;
				c.gridx = 1;
				c.gridy = 2;
				c.insets = new Insets(5, 10, 5, 10);
				panel.add(comboBoxLookAndFeel, c);
			}
		}

		this.pack();
		int width = this.getBounds().width;
		if (comboBoxGameLaunch.getBounds().height < 25
				&& comboBoxLauncherMinimized.getBounds().height < 25
				&& comboBoxLookAndFeel.getBounds().height < 25) {
			comboBoxGameLaunch.setPreferredSize(new Dimension(width, 25));
			comboBoxLauncherMinimized
					.setPreferredSize(new Dimension(width, 25));
			comboBoxLookAndFeel.setPreferredSize(new Dimension(width, 25));
		}
		this.setMinimumSize(new Dimension(width, this.getBounds().height));
		this.setPreferredSize(new Dimension(width, this.getBounds().height));
		this.pack();
		this.setLocationRelativeTo(facade.getMainPanel());
	}

	public void init() {

		PreferencesDTO preferencesDTO = preferencesServices.getPreferences();
		String gameLaunch = preferencesDTO.getLaunchPanelGameLaunch()
				.getDescription();
		if (gameLaunch != null) {
			comboBoxGameLaunch.setSelectedItem(gameLaunch);
		}
		String launcherMinimized = preferencesDTO.getLaunchPanelMinimized()
				.getDescription();
		if (launcherMinimized != null) {
			comboBoxLauncherMinimized.setSelectedItem(launcherMinimized);
		}
		String lookAndFeel = preferencesDTO.getLookAndFeel().getName();
		if (lookAndFeel != null) {
			comboBoxLookAndFeel.setSelectedItem(lookAndFeel);
		}
	}

	@Override
	protected void buttonOKPerformed() {

		PreferencesDTO preferencesDTO = new PreferencesDTO();
		String gameLaunch = (String) comboBoxGameLaunch.getSelectedItem();
		preferencesDTO.setLaunchPanelGameLaunch(MinimizationType
				.getEnum(gameLaunch));
		String launcherMinimized = (String) comboBoxLauncherMinimized
				.getSelectedItem();
		preferencesDTO.setLaunchPanelMinimized(MinimizationType
				.getEnum(launcherMinimized));
		String lookAndFeel = (String) comboBoxLookAndFeel.getSelectedItem();
		LookAndFeel newLookAndFeel = LookAndFeel.getEnum(lookAndFeel);
		preferencesDTO.setLookAndFeel(newLookAndFeel);

		this.dispose();

		/* Warning user to restart app if L&F has changed */
		LookAndFeel currentLookAndFeel = preferencesServices.getPreferences()
				.getLookAndFeel();

		if (!newLookAndFeel.equals(currentLookAndFeel)) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"ArmA3Sync must be restart to apply the new look & feel.",
					"Information", JOptionPane.INFORMATION_MESSAGE);
		}

		preferencesServices.setPreferences(preferencesDTO);
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
