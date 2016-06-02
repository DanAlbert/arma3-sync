package fr.soe.a3s.ui.main.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import fr.soe.a3s.service.AddonService;
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
public class WellcomeDialog extends AbstractDialog {

	private JTextField textField;
	private JButton buttonSelect;
	// Services
	private final ConfigurationService configurationService = new ConfigurationService();
	private final AddonService addonService = new AddonService();
	private final ProfileService profileService = new ProfileService();

	public WellcomeDialog(Facade facade) {
		super(facade, "Configuration", true);
		setResizable(false);

		{
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			getRootPane().setDefaultButton(buttonOK);
		}
		{
			Box vBox = Box.createVerticalBox();
			this.add(vBox, BorderLayout.CENTER);
			{
				JPanel arma3ExeLabelPanel = new JPanel();
				arma3ExeLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
				JLabel labelarma3ExeLocation = new JLabel(
						"Please set ArmA 3 executable location");
				arma3ExeLabelPanel.add(labelarma3ExeLocation);
				vBox.add(arma3ExeLabelPanel);
			}
			{
				JPanel panel = new JPanel();
				panel.setLayout(new BorderLayout());
				textField = new JTextField();
				buttonSelect = new JButton("Select");
				textField.setEditable(false);
				textField.setBackground(Color.WHITE);
				panel.add(textField, BorderLayout.CENTER);
				panel.add(buttonSelect, BorderLayout.EAST);
				vBox.add(panel);
			}
		}

		this.pack();
		if (textField.getBounds().height < 25) {
			textField
					.setPreferredSize(new Dimension(this.getBounds().width, 25));
		}
		if (this.getBounds().width < 350) {
			this.setMinimumSize(new Dimension(350, this.getBounds().height));
			this.setPreferredSize(new Dimension(350, this.getBounds().height));
		} else {
			this.setMinimumSize(new Dimension(this.getBounds().width, this
					.getBounds().height));
			this.setPreferredSize(new Dimension(this.getBounds().width, this
					.getBounds().height));
		}

		this.pack();
		setLocationRelativeTo(facade.getMainPanel());

		buttonSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						buttonSelectPerformed();
					}
				});
			}
		});
	}

	private void buttonSelectPerformed() {

		JFileChooser fc = null;
		String arma3Path = configurationService.determineArmA3Path();
		if (arma3Path == null) {
			fc = new JFileChooser();
		} else {
			File arma3Folder = new File(arma3Path);
			if (arma3Folder.exists()) {
				fc = new JFileChooser(arma3Path);
			} else {
				fc = new JFileChooser();
			}
		}

		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showOpenDialog(WellcomeDialog.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			profileService.setArmA3ExePath(path);
			if (file.getParent() != null) {
				String parentPath = file.getParentFile().getAbsolutePath();
				List<String> set = profileService
						.getAddonSearchDirectoryPaths();
				Iterator iter = set.iterator();
				List<String> list = new ArrayList<String>();
				while (iter.hasNext()) {
					list.add((String) iter.next());
				}
				boolean contains = false;
				for (int i = 0; i < list.size(); i++) {
					String osName = System.getProperty("os.name");
					if (osName.contains("Windows")) {
						if (parentPath.equalsIgnoreCase(list.get(i))) {
							contains = true;
						}
					} else {
						if (parentPath.equals(list.get(i))) {
							contains = true;
						}
					}
				}
				if (!contains) {
					profileService.addAddonSearchDirectoryPath(parentPath);
					facade.getAddonOptionsPanel()
							.updateAddonSearchDirectories();
					facade.getAddonOptionsPanel().updateAddonPriorities();
				}
			}
			textField.setText(path);
		} else {
			profileService.setArmA3ExePath(null);
			textField.setText("");
		}
	}

	@Override
	protected void buttonOKPerformed() {

		if (!textField.getText().isEmpty()) {
			this.dispose();
			facade.getAddonOptionsPanel().updateAddonSearchDirectories();
			facade.getAddonOptionsPanel().updateAddonPriorities();
			facade.getAddonsPanel().updateAvailableAddons();
			facade.getAddonsPanel().updateAddonGroups();
			facade.getLaunchOptionsPanel().init();
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
