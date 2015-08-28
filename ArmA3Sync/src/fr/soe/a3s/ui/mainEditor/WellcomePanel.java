package fr.soe.a3s.ui.mainEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import fr.soe.a3s.service.AddonService;
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
public class WellcomePanel extends JDialog implements UIConstants {

	Facade facade;
	private JTextField textField;
	private JButton buttonSelect;
	private JButton buttonOK;
	// Services
	private final ConfigurationService configurationService = new ConfigurationService();
	private final AddonService addonService = new AddonService();
	private final ProfileService profileService = new ProfileService();

	public WellcomePanel(Facade facade) {
		super(facade.getMainPanel(), "Configuration", true);
		this.facade = facade;
		setResizable(false);
		this.setMinimumSize(new Dimension(345, 125));
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
			JPanel eastPanel = new JPanel();
			this.add(eastPanel, BorderLayout.EAST);
			JPanel westPanel = new JPanel();
			this.add(westPanel, BorderLayout.WEST);
		}

		{
			JPanel centerPanel = new JPanel();
			centerPanel.setLayout(new BorderLayout());
			this.add(centerPanel, BorderLayout.CENTER);
			Box vBox = Box.createVerticalBox();
			centerPanel.add(vBox, BorderLayout.NORTH);
			vBox.add(Box.createVerticalStrut(5));
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
				buttonSelect.setPreferredSize(new Dimension(75, 25));
				textField.setEditable(false);
				textField.setBackground(Color.WHITE);
				panel.add(textField, BorderLayout.CENTER);
				panel.add(buttonSelect, BorderLayout.EAST);
				vBox.add(panel);
			}
			{
				JPanel panel = new JPanel();
				panel.setLayout(new BorderLayout());
				buttonOK = new JButton("OK");
				buttonOK.setPreferredSize(new Dimension(75, 25));
				panel.add(buttonOK, BorderLayout.EAST);
				vBox.add(Box.createVerticalStrut(5));
				vBox.add(panel);
			}

		}
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
		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						buttonOKPerformed();
					}
				});
			}
		});
	}

	private void buttonOKPerformed() {

		this.dispose();
		facade.getAddonOptionsPanel().updateAddonSearchDirectories();
		addonService.resetAvailableAddonTree();
		facade.getAddonsPanel().updateAvailableAddons();
		facade.getAddonsPanel().updateAddonGroups();
		facade.getAddonsPanel().expandAddonGroups();
		facade.getLaunchOptionsPanel().init();
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

		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnVal = fc.showOpenDialog(WellcomePanel.this);
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
				}
			}
			textField.setText(path);
		} else {
			profileService.setArmA3ExePath(null);
			textField.setText("");
		}
	}

	class ImagePanel extends JPanel {

		private Image image = null;

		public void setImage(Image image) {
			this.image = image;
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g); // paint background
			if (image != null) { // there is a picture: draw it
				int height = this.getSize().height;
				int width = this.getSize().width;
				// g.drawImage(image, 0, 0, this); //use image size

				g.drawImage(image, 0, 0, width, height, this);
			}
		}
	}
}
