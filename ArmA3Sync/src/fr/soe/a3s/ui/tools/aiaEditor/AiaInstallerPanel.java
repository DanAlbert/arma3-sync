package fr.soe.a3s.ui.tools.aiaEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.dto.configuration.AiAOptionsDTO;
import fr.soe.a3s.service.AddonService;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.mainEditor.LauncherOptionsPanel;

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
public class AiaInstallerPanel extends JDialog implements UIConstants {

	protected Facade facade;
	protected JButton buttonOK;
	private JTextField textFieldArmA3InstallationDirectory;
	private JTextField textFieldArmA2InstallationDirectory;
	private JButton buttonSelectArmA2InstallationDirectory;
	private JTextField textFieldArmAInstallationDirectory;
	private JButton buttonSelectArmAInstallationDirectory;
	private JTextField textFieldTOHInstallationDirectory;
	private JButton buttonSelectTOHInstallationDirectory;
	private ConfigurationService configurationService = new ConfigurationService();
	private AddonService addonService = new AddonService();
	private JTextField textFieldArmA2OAInstallationDirectory;
	private JButton buttonSelectArmA2OAInstallationDirectory;
	private JTextField textFieldAllInArmAInstallationDirectory;
	private JButton buttonSelectAllInArmAInstallationDirectory;

	public AiaInstallerPanel(Facade facade) {
		super(facade.getMainPanel(), "AiA tweaker wizard", false);
		this.facade = facade;
		this.setMinimumSize(new Dimension(DEFAULT_WIDTH, 500));
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
			JPanel topPanel = new JPanel();
			this.add(topPanel, BorderLayout.NORTH);
			topPanel.setLayout(null);
			topPanel.setBackground(new java.awt.Color(255, 255, 255));
			topPanel.setPreferredSize(new java.awt.Dimension(484, 55));
			{
				ImagePanel acreImagePanel = new ImagePanel();
				acreImagePanel.setBackground(new java.awt.Color(255, 255, 255));
				ImageIcon imageIcon = new ImageIcon(
						java.lang.ClassLoader
								.getSystemResource("resources/pictures/system/allinarma.png"));
				Image myNewImage = imageIcon.getImage();
				acreImagePanel.setImage(myNewImage);
				topPanel.add(acreImagePanel);
				acreImagePanel.setBounds(7, 6, 160, 42);
				acreImagePanel.repaint();
			}
			{
				JLabel labelDescription = new JLabel();
				labelDescription.setText("TOH, ArmA 1, ArmA 2/OA in ArmA 3");
				topPanel.add(labelDescription);
				labelDescription.setBounds(179, 6, 305, 42);
			}
		}
		{
			JPanel controlPanel = new JPanel();
			buttonOK = new JButton("OK");
			buttonOK.setPreferredSize(new Dimension(80, 25));
			FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
			controlPanel.setLayout(flowLayout);
			controlPanel.add(buttonOK);
			this.add(controlPanel, BorderLayout.SOUTH);
		}
		{
			JPanel centerPanel = new JPanel();
			this.add(centerPanel, BorderLayout.CENTER);
			centerPanel.setBorder(BorderFactory
					.createEtchedBorder(BevelBorder.LOWERED));
			centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
			{
				JPanel locationsPanel = new JPanel();
				locationsPanel.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(),
						"Installation directories"));
				locationsPanel.setLayout(new BorderLayout());
				centerPanel.add(locationsPanel);
				Box vBox = Box.createVerticalBox();
				locationsPanel.add(vBox, BorderLayout.NORTH);
				{
					JPanel arma2OAInstallationDirectoryLabelPanel = new JPanel();
					arma2OAInstallationDirectoryLabelPanel
							.setLayout(new FlowLayout(FlowLayout.LEFT));
					JLabel labelArmA2OAinstallationDirectory = new JLabel(
							"ArmA 2 OA");
					arma2OAInstallationDirectoryLabelPanel
							.add(labelArmA2OAinstallationDirectory);
					vBox.add(arma2OAInstallationDirectoryLabelPanel);
				}
				{
					JPanel arma2OAInstallationDirectoryPanel = new JPanel();
					arma2OAInstallationDirectoryPanel
							.setLayout(new BorderLayout());
					textFieldArmA2OAInstallationDirectory = new JTextField();
					buttonSelectArmA2OAInstallationDirectory = new JButton(
							"Select");
					buttonSelectArmA2OAInstallationDirectory
							.setPreferredSize(new Dimension(75, 25));
					textFieldArmA2OAInstallationDirectory.setEditable(false);
					textFieldArmA2OAInstallationDirectory
							.setBackground(Color.WHITE);
					arma2OAInstallationDirectoryPanel.add(
							textFieldArmA2OAInstallationDirectory,
							BorderLayout.CENTER);
					arma2OAInstallationDirectoryPanel.add(
							buttonSelectArmA2OAInstallationDirectory,
							BorderLayout.EAST);
					vBox.add(arma2OAInstallationDirectoryPanel);
				}
				vBox.add(Box.createVerticalStrut(5));
				{
					JPanel arma2InstallationDirectoryLabelPanel = new JPanel();
					arma2InstallationDirectoryLabelPanel
							.setLayout(new FlowLayout(FlowLayout.LEFT));
					JLabel labelArmA2installationDirectory = new JLabel(
							"ArmA 2");
					arma2InstallationDirectoryLabelPanel
							.add(labelArmA2installationDirectory);
					vBox.add(arma2InstallationDirectoryLabelPanel);
				}
				{
					JPanel arma2InstallationDirectoryPanel = new JPanel();
					arma2InstallationDirectoryPanel
							.setLayout(new BorderLayout());
					textFieldArmA2InstallationDirectory = new JTextField();
					buttonSelectArmA2InstallationDirectory = new JButton(
							"Select");
					buttonSelectArmA2InstallationDirectory
							.setPreferredSize(new Dimension(75, 25));
					textFieldArmA2InstallationDirectory.setEditable(false);
					textFieldArmA2InstallationDirectory
							.setBackground(Color.WHITE);
					arma2InstallationDirectoryPanel.add(
							textFieldArmA2InstallationDirectory,
							BorderLayout.CENTER);
					arma2InstallationDirectoryPanel.add(
							buttonSelectArmA2InstallationDirectory,
							BorderLayout.EAST);
					vBox.add(arma2InstallationDirectoryPanel);
				}
				vBox.add(Box.createVerticalStrut(5));
				{
					JPanel armaInstallationDirectoryLabelPanel = new JPanel();
					armaInstallationDirectoryLabelPanel
							.setLayout(new FlowLayout(FlowLayout.LEFT));
					JLabel labelArmAinstallationDirectory = new JLabel("ArmA 1");
					armaInstallationDirectoryLabelPanel
							.add(labelArmAinstallationDirectory);
					vBox.add(armaInstallationDirectoryLabelPanel);
				}
				{
					JPanel armaInstallationDirectoryPanel = new JPanel();
					armaInstallationDirectoryPanel
							.setLayout(new BorderLayout());
					textFieldArmAInstallationDirectory = new JTextField();
					buttonSelectArmAInstallationDirectory = new JButton(
							"Select");
					buttonSelectArmAInstallationDirectory
							.setPreferredSize(new Dimension(75, 25));
					textFieldArmAInstallationDirectory.setEditable(false);
					textFieldArmAInstallationDirectory
							.setBackground(Color.WHITE);
					armaInstallationDirectoryPanel.add(
							textFieldArmAInstallationDirectory,
							BorderLayout.CENTER);
					armaInstallationDirectoryPanel.add(
							buttonSelectArmAInstallationDirectory,
							BorderLayout.EAST);
					vBox.add(armaInstallationDirectoryPanel);
				}
				vBox.add(Box.createVerticalStrut(5));
				{
					JPanel tohInstallationDirectoryLabelPanel = new JPanel();
					tohInstallationDirectoryLabelPanel
							.setLayout(new FlowLayout(FlowLayout.LEFT));
					JLabel labelTOHinstallationDirectory = new JLabel("TOH");
					tohInstallationDirectoryLabelPanel
							.add(labelTOHinstallationDirectory);
					vBox.add(tohInstallationDirectoryLabelPanel);
				}
				{
					JPanel tohInstallationDirectoryPanel = new JPanel();
					tohInstallationDirectoryPanel.setLayout(new BorderLayout());
					textFieldTOHInstallationDirectory = new JTextField();
					buttonSelectTOHInstallationDirectory = new JButton("Select");
					buttonSelectTOHInstallationDirectory
							.setPreferredSize(new Dimension(75, 25));
					textFieldTOHInstallationDirectory.setEditable(false);
					textFieldTOHInstallationDirectory
							.setBackground(Color.WHITE);
					tohInstallationDirectoryPanel.add(
							textFieldTOHInstallationDirectory,
							BorderLayout.CENTER);
					tohInstallationDirectoryPanel.add(
							buttonSelectTOHInstallationDirectory,
							BorderLayout.EAST);
					vBox.add(tohInstallationDirectoryPanel);
				}
				vBox.add(Box.createVerticalStrut(5));
				{
					JPanel allInArmAInstallationDirectoryLabelPanel = new JPanel();
					allInArmAInstallationDirectoryLabelPanel
							.setLayout(new FlowLayout(FlowLayout.LEFT));
					JLabel labelAllInArmAinstallationDirectory = new JLabel(
							"@AllinArma");
					allInArmAInstallationDirectoryLabelPanel
							.add(labelAllInArmAinstallationDirectory);
					vBox.add(allInArmAInstallationDirectoryLabelPanel);
				}
				{
					JPanel allInArmAInstallationDirectoryPanel = new JPanel();
					allInArmAInstallationDirectoryPanel
							.setLayout(new BorderLayout());
					textFieldAllInArmAInstallationDirectory = new JTextField();
					textFieldAllInArmAInstallationDirectory.setEnabled(false);
					// buttonSelectAllInArmAInstallationDirectory = new JButton(
					// "Select");
					// buttonSelectAllInArmAInstallationDirectory
					// .setPreferredSize(new Dimension(65, 23));
					textFieldAllInArmAInstallationDirectory.setEditable(false);
					textFieldAllInArmAInstallationDirectory
							.setPreferredSize(new Dimension(75, 25));
					textFieldAllInArmAInstallationDirectory
							.setBackground(Color.WHITE);
					allInArmAInstallationDirectoryPanel.add(
							textFieldAllInArmAInstallationDirectory,
							BorderLayout.CENTER);
					// allInArmAInstallationDirectoryPanel.add(
					// buttonSelectAllInArmAInstallationDirectory,
					// BorderLayout.EAST);
					vBox.add(allInArmAInstallationDirectoryPanel);
				}
			}
		}
		buttonSelectArmA2OAInstallationDirectory
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						buttonSelectArmA2OAInstallationDirectoryPerformed();
					}
				});
		buttonSelectArmA2InstallationDirectory
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						buttonSelectArmA2InstallationDirectoryPerformed();
					}
				});
		buttonSelectArmAInstallationDirectory
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						buttonSelectArmAInstallationDirectoryPerfomed();
					}
				});
		buttonSelectTOHInstallationDirectory
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						buttonSelectTOHInstallationDirectoryPerformed();
					}
				});
		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonOKPerformed();
			}
		});
		// Add Listeners
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				menuExitPerformed();
			}
		});
	}

	public void init() {

		AiAOptionsDTO aiAOptionsDTO = configurationService
				.determineAiAOptions();
		textFieldArmA2InstallationDirectory.setText(aiAOptionsDTO
				.getArma2Path());
		textFieldArmA2OAInstallationDirectory.setText(aiAOptionsDTO
				.getArma2OAPath());
		textFieldArmAInstallationDirectory.setText(aiAOptionsDTO.getArmaPath());
		textFieldTOHInstallationDirectory.setText(aiAOptionsDTO.getTohPath());
		if (aiAOptionsDTO.getAllinArmaPath() == null
				|| aiAOptionsDTO.getAllinArmaPath().isEmpty()) {
			textFieldAllInArmAInstallationDirectory.setText("Not found!");
		} else {
			textFieldAllInArmAInstallationDirectory.setText(aiAOptionsDTO
					.getAllinArmaPath());
		}
	}

	private void buttonSelectArmA2OAInstallationDirectoryPerformed() {

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(AiaInstallerPanel.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			textFieldArmA2OAInstallationDirectory.setText(path);
		} else {
			textFieldArmA2OAInstallationDirectory.setText("");
		}
	}

	private void buttonSelectArmA2InstallationDirectoryPerformed() {

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(AiaInstallerPanel.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			textFieldArmA2InstallationDirectory.setText(path);
		} else {
			textFieldArmA2InstallationDirectory.setText("");
		}
	}

	private void buttonSelectArmAInstallationDirectoryPerfomed() {

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(AiaInstallerPanel.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			textFieldArmAInstallationDirectory.setText(path);
		} else {
			textFieldArmAInstallationDirectory.setText("");
		}
	}

	private void buttonSelectTOHInstallationDirectoryPerformed() {

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(AiaInstallerPanel.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			textFieldTOHInstallationDirectory.setText(path);
		} else {
			textFieldTOHInstallationDirectory.setText("");
		}
	}

	private void buttonOKPerformed() {

		AiAOptionsDTO aiAOptionsDTO = new AiAOptionsDTO();
		aiAOptionsDTO.setArma2Path(textFieldArmA2InstallationDirectory
				.getText());
		aiAOptionsDTO.setArma2OAPath(textFieldArmA2OAInstallationDirectory
				.getText());
		aiAOptionsDTO.setArmaPath(textFieldArmAInstallationDirectory.getText());
		aiAOptionsDTO.setTohPath(textFieldTOHInstallationDirectory.getText());
		configurationService.setAiAOptions(aiAOptionsDTO);
		this.dispose();
	}

	private void menuExitPerformed() {
		this.dispose();
	}

	class ImagePanel extends JPanel {

		private Image image = null;

		public void setImage(Image image) {
			this.image = image;
		}

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
