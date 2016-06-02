package fr.soe.a3s.ui.tools;

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
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ImagePanel;
import fr.soe.a3s.ui.UIConstants;

public abstract class WizardCopyDirectoryDialog extends JDialog implements
		UIConstants {

	protected Facade facade;
	protected String title;
	protected String description;
	protected Image image;
	/**/
	protected JButton buttonProceed, buttonClose;
	protected JTextField textFieldSourceDirectory;
	private JButton buttonSelectSourceDirectory;
	protected JTextField textFieldTargetDirectory;
	private JButton buttonSelectTargetDirectory;
	private ImagePanel imagePanel;
	protected JLabel sourceDirectoryLabel;
	protected JLabel targetDirectoryLabel;

	public WizardCopyDirectoryDialog(Facade facade, String title,
			String description, Image image) {
		super(facade.getMainPanel(), title, true);

		this.facade = facade;
		this.title = title;
		this.description = description;
		this.image = image;
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
				imagePanel.setImage(image);
				imagePanel.repaint();
				topPanel.add(imagePanel, BorderLayout.WEST);
			}
			{
				Box hBox = Box.createHorizontalBox();
				hBox.add(Box.createHorizontalStrut(10));
				JLabel labelDescription = new JLabel();
				labelDescription.setText(description);
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
					sourceDirectoryLabel = new JLabel();
					panel.add(sourceDirectoryLabel);
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
					targetDirectoryLabel = new JLabel();
					panel.add(targetDirectoryLabel);
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

	private void buttonSelectSourceDirectoryPerformed() {

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(WizardCopyDirectoryDialog.this);
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
		int returnVal = fc.showOpenDialog(WizardCopyDirectoryDialog.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			textFieldTargetDirectory.setText(path);
		} else {
			textFieldTargetDirectory.setText("");
		}
	}

	protected String check() {

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
		return message;
	}

	protected abstract void buttonProceedPerformed();

	protected abstract void menuExitPerformed();
}
