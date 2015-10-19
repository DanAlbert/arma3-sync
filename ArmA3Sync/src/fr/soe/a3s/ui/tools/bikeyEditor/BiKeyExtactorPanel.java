package fr.soe.a3s.ui.tools.bikeyEditor;

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
import java.io.IOException;

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

import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.service.CommonService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ImagePanel;
import fr.soe.a3s.ui.UIConstants;

public class BiKeyExtactorPanel extends JDialog implements UIConstants {

	protected Facade facade;
	protected JButton buttonProceed, buttonCancel;
	private JTextField textFieldSourceDirectory;
	private JButton buttonSelectSourceDirectory;
	private JTextField textFieldTargetDirectory;
	private JButton buttonSelectTargetDirectory;
	/* Services */
	private final CommonService commonService = new CommonService();

	public BiKeyExtactorPanel(Facade facade) {

		super(facade.getMainPanel(), "Bikey extractor wizard", false);
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
				ImagePanel imagePanel = new ImagePanel();
				imagePanel.setBackground(new java.awt.Color(255, 255, 255));
				ImageIcon imageIcon = new ImageIcon(BIKEY_BIG);
				Image myNewImage = imageIcon.getImage();
				imagePanel.setImage(myNewImage);
				topPanel.add(imagePanel);
				imagePanel.setBounds(6, 4, 48, 48);
				imagePanel.repaint();
			}
			{
				JLabel labelDescription = new JLabel();
				labelDescription.setText("Extract *.bikey files");
				topPanel.add(labelDescription);
				labelDescription.setBounds(60, 6, 300, 42);
			}
		}
		{
			JPanel controlPanel = new JPanel();
			buttonProceed = new JButton("Proceed");
			getRootPane().setDefaultButton(buttonProceed);
			buttonProceed.setPreferredSize(new Dimension(80, 25));
			buttonCancel = new JButton("Cancel");
			buttonCancel.setPreferredSize(new Dimension(80, 25));
			FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
			controlPanel.setLayout(flowLayout);
			controlPanel.add(buttonProceed);
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
					buttonSelectSourceDirectory.setPreferredSize(new Dimension(
							75, 25));
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
					buttonSelectTargetDirectory.setPreferredSize(new Dimension(
							75, 25));
					textFieldTargetDirectory.setEditable(false);
					textFieldTargetDirectory.setBackground(Color.WHITE);
					panel.add(textFieldTargetDirectory, BorderLayout.CENTER);
					panel.add(buttonSelectTargetDirectory, BorderLayout.EAST);
					vBox.add(panel);
				}
			}
		}
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

	}

	private void buttonSelectSourceDirectoryPerformed() {

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(BiKeyExtactorPanel.this);
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
		int returnVal = fc.showOpenDialog(BiKeyExtactorPanel.this);
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
		try {
			int nbBikeys = commonService.extractBikeys(sourceDirectoryPath,
					targetDirectoryPath);
			if (nbBikeys != 0) {
				JOptionPane.showMessageDialog(facade.getMainPanel(), nbBikeys
						+ " " + "have been copied to target directory.",
						"Bikey extractor wizard",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(facade.getMainPanel(), nbBikeys
						+ " " + "have been found.", "Bikey extractor wizard",
						JOptionPane.WARNING_MESSAGE);
			}
		} catch (CheckException e1) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e1.getMessage(), "Bikey extractor wizard",
					JOptionPane.WARNING_MESSAGE);
		} catch (IOException e2) {
			e2.printStackTrace();
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e2.getMessage(), "Bikey extractor wizard",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void buttonCancelPerformed() {
		this.dispose();
	}

	private void menuExitPerformed() {
		this.dispose();
	}
}
