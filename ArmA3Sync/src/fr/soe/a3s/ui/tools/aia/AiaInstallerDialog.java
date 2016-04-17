package fr.soe.a3s.ui.tools.aia;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import fr.soe.a3s.ui.ImagePanel;
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
public class AiaInstallerDialog extends JDialog implements UIConstants {

	protected Facade facade;
	protected JButton buttonOK, buttonCancel;
	private JTextField textFieldArmA3InstallationDirectory;
	private JTextField textFieldArmA2InstallationDirectory;
	private JButton buttonSelectArmA2InstallationDirectory;
	private JTextField textFieldArmAInstallationDirectory;
	private JButton buttonSelectArmAInstallationDirectory;
	private JTextField textFieldTOHInstallationDirectory;
	private JButton buttonSelectTOHInstallationDirectory;
	private final AddonService addonService = new AddonService();
	private JTextField textFieldArmA2OAInstallationDirectory;
	private JButton buttonSelectArmA2OAInstallationDirectory;
	private JTextField textFieldAllInArmAInstallationDirectory;
	private JButton buttonSelectAllInArmAInstallationDirectory;
	private ImagePanel imagePanel;
	// Service
	private final ConfigurationService configurationService = new ConfigurationService();

	public AiaInstallerDialog(Facade facade) {
		super(facade.getMainPanel(), "AiA tweaker wizard", false);
		this.facade = facade;
		setIconImage(ICON);

		this.setLayout(new BorderLayout());
		{
			JPanel topPanel = new JPanel();
			topPanel.setLayout(new BorderLayout());
			{
				imagePanel = new ImagePanel();
				imagePanel.setBackground(new java.awt.Color(255, 255, 255));
				ImageIcon imageIcon = new ImageIcon(AIA_BIG);
				Image myNewImage = imageIcon.getImage();
				imagePanel.setImage(myNewImage);
				imagePanel.repaint();
				topPanel.add(imagePanel, BorderLayout.WEST);
			}
			{
				Box hBox = Box.createHorizontalBox();
				hBox.add(Box.createHorizontalStrut(10));
				JLabel labelDescription = new JLabel();
				labelDescription.setText("TOH, ArmA 1, ArmA 2/OA in ArmA 3");
				hBox.add(labelDescription);
				topPanel.add(hBox, BorderLayout.CENTER);
			}
			topPanel.setBackground(new java.awt.Color(255, 255, 255));
			this.add(topPanel, BorderLayout.NORTH);
		}
		{
			JPanel controlPanel = new JPanel();
			buttonOK = new JButton("OK");
			getRootPane().setDefaultButton(buttonOK);
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
					textFieldAllInArmAInstallationDirectory.setEditable(false);
					textFieldAllInArmAInstallationDirectory
							.setPreferredSize(new Dimension(75, 25));
					textFieldAllInArmAInstallationDirectory
							.setBackground(Color.WHITE);
					allInArmAInstallationDirectoryPanel.add(
							textFieldAllInArmAInstallationDirectory,
							BorderLayout.CENTER);
					vBox.add(allInArmAInstallationDirectoryPanel);
				}
				vBox.add(Box.createVerticalStrut(10));
			}
		}

		this.pack();
		this.setMinimumSize(new Dimension(
				facade.getMainPanel().getBounds().width,
				this.getBounds().height));
		this.setPreferredSize(new Dimension(
				facade.getMainPanel().getBounds().width,
				this.getBounds().height));

		this.setLocationRelativeTo(facade.getMainPanel());

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
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
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
		int returnVal = fc.showOpenDialog(AiaInstallerDialog.this);
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
		int returnVal = fc.showOpenDialog(AiaInstallerDialog.this);
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
		int returnVal = fc.showOpenDialog(AiaInstallerDialog.this);
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
		int returnVal = fc.showOpenDialog(AiaInstallerDialog.this);
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

	private void buttonCancelPerformed() {
		this.dispose();
	}

	private void menuExitPerformed() {
		this.dispose();
	}
}
