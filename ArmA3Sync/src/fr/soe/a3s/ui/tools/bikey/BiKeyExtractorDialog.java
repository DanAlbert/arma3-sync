package fr.soe.a3s.ui.tools.bikey;

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
import java.nio.file.FileSystems;
import java.nio.file.Files;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ImagePanel;
import fr.soe.a3s.ui.UIConstants;

public class BiKeyExtractorDialog extends JDialog implements UIConstants {

	protected Facade facade;
	protected JButton buttonProceed, buttonClose;
	private JTextField textFieldSourceDirectory;
	private JButton buttonSelectSourceDirectory;
	private JTextField textFieldTargetDirectory;
	private JButton buttonSelectTargetDirectory;
	private ImagePanel imagePanel;
	/* Services */
	private final ConfigurationService configurationService = new ConfigurationService();

	public BiKeyExtractorDialog(Facade facade) {

		super(facade.getMainPanel(), "Bikey extractor wizard", true);
		this.facade = facade;
		setIconImage(ICON);

		this.setLayout(new BorderLayout());
		{
			JPanel topPanel = new JPanel();
			topPanel.setLayout(new BorderLayout());
			this.add(topPanel, BorderLayout.NORTH);
			topPanel.setBackground(new java.awt.Color(255, 255, 255));
			{
				imagePanel = new ImagePanel();
				imagePanel.setBackground(new java.awt.Color(255, 255, 255));
				ImageIcon imageIcon = new ImageIcon(BIKEY_BIG);
				Image myNewImage = imageIcon.getImage();
				imagePanel.setImage(myNewImage);
				imagePanel.repaint();
				topPanel.add(imagePanel, BorderLayout.WEST);
			}
			{
				Box hBox = Box.createHorizontalBox();
				hBox.add(Box.createHorizontalStrut(10));
				JLabel labelDescription = new JLabel();
				labelDescription.setText("Extract *.bikey files");
				hBox.add(labelDescription);
				topPanel.add(hBox, BorderLayout.CENTER);
			}
		}
		{
			JPanel controlPanel = new JPanel();
			buttonProceed = new JButton("Proceed");
			getRootPane().setDefaultButton(buttonProceed);
			buttonClose = new JButton("Close");
			buttonClose.setPreferredSize(buttonProceed.getPreferredSize());
			FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
			controlPanel.setLayout(flowLayout);
			controlPanel.add(buttonProceed);
			controlPanel.add(buttonClose);
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
						BorderFactory.createEtchedBorder(), "Directories"));
				locationsPanel.setLayout(new BorderLayout());
				centerPanel.add(locationsPanel);
				Box vBox = Box.createVerticalBox();
				locationsPanel.add(vBox, BorderLayout.NORTH);
				{
					JPanel panel = new JPanel();
					panel.setLayout(new FlowLayout(FlowLayout.LEFT));
					JLabel label = new JLabel(
							"Source directory to search for *.bikey files");
					panel.add(label);
					vBox.add(panel);
				}
				{
					JPanel panel = new JPanel();
					panel.setLayout(new BorderLayout());
					textFieldSourceDirectory = new JTextField();
					buttonSelectSourceDirectory = new JButton("Select");
					textFieldSourceDirectory.setEditable(false);
					textFieldSourceDirectory.setBackground(Color.WHITE);
					panel.add(textFieldSourceDirectory, BorderLayout.CENTER);
					panel.add(buttonSelectSourceDirectory, BorderLayout.EAST);
					vBox.add(panel);
				}
				vBox.add(Box.createVerticalStrut(5));
				{
					JPanel panel = new JPanel();
					panel.setLayout(new FlowLayout(FlowLayout.LEFT));
					JLabel label = new JLabel(
							"Target directory to copy *.bikey files");
					panel.add(label);
					vBox.add(panel);
				}
				{
					JPanel panel = new JPanel();
					panel.setLayout(new BorderLayout());
					textFieldTargetDirectory = new JTextField();
					buttonSelectTargetDirectory = new JButton("Select");
					textFieldTargetDirectory.setEditable(false);
					textFieldTargetDirectory.setBackground(Color.WHITE);
					panel.add(textFieldTargetDirectory, BorderLayout.CENTER);
					panel.add(buttonSelectTargetDirectory, BorderLayout.EAST);
					vBox.add(panel);
				}
				vBox.add(Box.createVerticalStrut(150));
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

		buttonSelectSourceDirectory.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonSelectSourceDirectoryPerformed();
			}
		});
		buttonSelectTargetDirectory.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonSelectTargetDirectoryPerformed();
			}
		});
		buttonProceed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonProceedPerformed();
			}
		});
		buttonClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menuExitPerformed();
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

		String sourceDirectoryPath = configurationService
				.getBiketyExtractSourceDirectoryPath();
		textFieldSourceDirectory.setText(sourceDirectoryPath);
		String targetDirectoryPath = configurationService
				.getBiketyExtractTargetDirectoryPath();
		textFieldTargetDirectory.setText(targetDirectoryPath);
	}

	private void buttonSelectSourceDirectoryPerformed() {

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(BiKeyExtractorDialog.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			textFieldSourceDirectory.setText(path);
		} else {
			textFieldSourceDirectory.setText("");
		}
	}

	private void buttonSelectTargetDirectoryPerformed() {

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(BiKeyExtractorDialog.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			textFieldTargetDirectory.setText(path);
		} else {
			textFieldTargetDirectory.setText("");
		}
	}

	private void buttonProceedPerformed() {

		String sourceDirectoryPath = textFieldSourceDirectory.getText();
		String targetDirectoryPath = textFieldTargetDirectory.getText();

		String message = "";
		if (sourceDirectoryPath.isEmpty()) {
			message = "Source directory is empty!";
		} else if (!new File(sourceDirectoryPath).exists()) {
			message = "Source directory does not exists!";
		} else if (targetDirectoryPath.isEmpty()) {
			message = "Target directory is empty!";
		} else if (!new File(targetDirectoryPath).exists()) {
			message = "Target directory does not exists!";
		} else if (!Files.isWritable(FileSystems.getDefault().getPath(
				targetDirectoryPath))) {// Check write permissions on target
										// directory
			message = "Can't write on target directory!";
		}

		if (!message.isEmpty()) {
			JOptionPane.showMessageDialog(facade.getMainPanel(), message,
					"Bikey extractor wizard", JOptionPane.WARNING_MESSAGE);
		} else {
			ExtractProgressDialog extractProgressPanel = new ExtractProgressDialog(
					facade);
			extractProgressPanel.setVisible(true);
			extractProgressPanel.init(sourceDirectoryPath, targetDirectoryPath);
		}
	}

	private void menuExitPerformed() {

		configurationService
				.setBiketyExtractSourceDirectoryPath(textFieldSourceDirectory
						.getText());
		configurationService
				.setBiketyExtractTargetDirectoryPath(textFieldTargetDirectory
						.getText());
		this.dispose();
	}
}
