package fr.soe.a3s.ui.mainEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fr.soe.a3s.constant.LookAndFeel;
import fr.soe.a3s.constant.MinimizationType;
import fr.soe.a3s.dto.configuration.PreferencesDTO;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.service.PreferencesService;
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
public class PreferencesPanel extends JDialog implements UIConstants {

	private Facade facade;
	private JButton buttonOK, buttonCancel;
	private JCheckBox checkBoxLauncherMinimized;
	private JComboBox comboBoxLauncherMinimized;
	private JComboBox comboBoxLookAndFeel;
	private JCheckBox checkBoxLookAndFeel;
	private JComboBox comboBoxGameLaunch;
	private JCheckBox checkBoxGameLaunch;
	private JPanel launcherPanel;
	private PreferencesService preferencesServices = new PreferencesService();

	public PreferencesPanel(Facade facade) {
		super(facade.getMainPanel(), "Preferences", true);
		this.facade = facade;
		this.facade.setPreferencesPanel(this);
		this.setResizable(false);
		this.setSize(330, 215);
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
			JPanel centerPanel = new JPanel();
			GridLayout grid1 = new GridLayout(1, 1);
			centerPanel.setLayout(grid1);
			this.add(centerPanel, BorderLayout.CENTER);
			{
				launcherPanel = new JPanel();
				launcherPanel.setLayout(null);
				launcherPanel.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(), ""));

			}
			Box vertBox = Box.createVerticalBox();
			vertBox.add(Box.createVerticalStrut(5));
			vertBox.add(launcherPanel);
			launcherPanel.setPreferredSize(new java.awt.Dimension(304, 134));
			{
				checkBoxGameLaunch = new JCheckBox();
				launcherPanel.add(checkBoxGameLaunch);
				checkBoxGameLaunch.setText("At game launch");
				checkBoxGameLaunch.setBounds(18, 23, 118, 25);
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
				launcherPanel.add(comboBoxGameLaunch);
				comboBoxGameLaunch.setModel(comboBoxGameLaunchModel);
				comboBoxGameLaunch.setBounds(141, 24, 139, 25);
				comboBoxGameLaunch.setFocusable(false);
			}
			{
				checkBoxLauncherMinimized = new JCheckBox();
				launcherPanel.add(checkBoxLauncherMinimized);
				checkBoxLauncherMinimized.setText("When minimized");
				checkBoxLauncherMinimized.setBounds(18, 61, 119, 25);
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
				launcherPanel.add(comboBoxLauncherMinimized);
				comboBoxLauncherMinimized
						.setModel(comboBoxLauncherMinimizedModel);
				comboBoxLauncherMinimized.setBounds(141, 60, 139, 25);
				comboBoxLauncherMinimized.setFocusable(false);
			}
			{
				checkBoxLookAndFeel = new JCheckBox();
				checkBoxLookAndFeel.setSelected(true);
				checkBoxLookAndFeel.setFocusable(false);
				launcherPanel.add(checkBoxLookAndFeel);
				checkBoxLookAndFeel.setText("Look & Feel");
				checkBoxLookAndFeel.setBounds(18, 99, 118, 25);
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
				launcherPanel.add(comboBoxLookAndFeel);
				comboBoxLookAndFeel.setModel(comboBoxLookAndFeelModel);
				comboBoxLookAndFeel.setBounds(141, 97, 139, 25);
				comboBoxLookAndFeel.setFocusable(false);
			}
			vertBox.add(Box.createVerticalStrut(5));
			centerPanel.add(vertBox);
		}
		{
			JPanel controlPanel = new JPanel();
			getContentPane().add(controlPanel, BorderLayout.SOUTH);
			buttonOK = new JButton("OK");
			buttonCancel = new JButton("Cancel");
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
			controlPanel.setLayout(flowLayout);
			controlPanel.add(buttonOK);
			controlPanel.add(buttonCancel);
			JPanel sidePanel1 = new JPanel();
			this.add(sidePanel1, BorderLayout.EAST);
			JPanel sidePanel2 = new JPanel();
			this.add(sidePanel2, BorderLayout.WEST);
		}
		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonOKPerformed();
			}
		});
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menuExitPerformed();
			}
		});

		// Add Listeners
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				menuExitPerformed();
			}
		});
		getRootPane().setDefaultButton(buttonOK);
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

	private void buttonOKPerformed() {

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

	private void menuExitPerformed() {
		this.dispose();
	}
}
