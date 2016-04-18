package fr.soe.a3sUpdater.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;

import fr.soe.a3sUpdater.controller.Observateur;
import fr.soe.a3sUpdater.service.Service;

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
public class Updater extends JFrame implements ActionListener, UIConstants {

	private final Facade facade;
	private JPanel panelLeft;
	private ImagePanel panelRight;
	private JButton buttonCancel;
	private JLabel labelUpatingVersion;
	private JLabel labelAction;;
	private JProgressBar progressBar;

	// private JTextArea jTextAreaUpdating;
	// private JTextArea jTextArea5;
	// private JTextArea jTextArea4;
	// private JTextArea jTextAreaAction;
	// private JTextArea jTextArea1;
	// private ImagePanel jPanel2;
	// private JPanel jPanel1;
	// Services
	private final Service service = new Service();

	public Updater(Facade facade) {

		this.facade = facade;
		this.setTitle("Update");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// setResizable(false);
		setIconImage(ICON);

		this.setLayout(new BorderLayout());
		{
			JPanel sidePanel1 = new JPanel();
			this.add(sidePanel1, BorderLayout.NORTH);
			JPanel sidePanel2 = new JPanel();
			this.add(sidePanel2, BorderLayout.WEST);
			JPanel sidePanel3 = new JPanel();
			this.add(sidePanel3, BorderLayout.EAST);
		}
		{
			Box vBox = Box.createVerticalBox();
			vBox.add(Box.createVerticalStrut(10));
			{
				buttonCancel = new JButton("Cancel");
				JPanel panel = new JPanel();
				FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
				panel.setLayout(flowLayout);
				panel.add(buttonCancel);
				vBox.add(panel);
			}
			this.add(vBox, BorderLayout.SOUTH);
		}
		{
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			this.add(panel, BorderLayout.CENTER);
			{
				JPanel mainPanel = new JPanel();
				mainPanel.setBorder(BorderFactory
						.createEtchedBorder(BevelBorder.LOWERED));
				mainPanel.setLayout(new BorderLayout());
				mainPanel.setBackground(new java.awt.Color(255, 255, 255));
				panel.add(mainPanel, BorderLayout.CENTER);
				{
					JPanel sidePanel1 = new JPanel();
					sidePanel1.setBackground(new java.awt.Color(255, 255, 255));
					mainPanel.add(sidePanel1, BorderLayout.NORTH);
					JPanel sidePanel2 = new JPanel();
					sidePanel2.setBackground(new java.awt.Color(255, 255, 255));
					mainPanel.add(sidePanel2, BorderLayout.WEST);
					JPanel sidePanel3 = new JPanel();
					sidePanel3.setBackground(new java.awt.Color(255, 255, 255));
					mainPanel.add(sidePanel3, BorderLayout.EAST);
					JPanel sidePanel4 = new JPanel();
					sidePanel4.setBackground(new java.awt.Color(255, 255, 255));
					mainPanel.add(sidePanel4, BorderLayout.SOUTH);
				}
				{
					Box hBox = Box.createHorizontalBox();
					mainPanel.add(hBox, BorderLayout.CENTER);
					{
						panelLeft = new JPanel();
						panelLeft.setBackground(new java.awt.Color(255, 255,
								255));
						panelLeft.setLayout(new GridBagLayout());
						hBox.add(panelLeft);
						{
							Box vBox = Box.createVerticalBox();
							hBox.add(vBox);
							{
								JLabel label = new JLabel(APPLICATION_NAME);
								label.setFont(label.getFont().deriveFont(
										Font.BOLD, new Float(24)));
								vBox.add(label);
							}
							vBox.add(Box.createVerticalStrut(10));
							{
								JLabel label = new JLabel(
										"by the [S.o.E] Team, www.sonsofexiled.fr");
								vBox.add(label);
							}
							vBox.add(Box.createVerticalStrut(10));
							{
								labelUpatingVersion = new JLabel();
								vBox.add(labelUpatingVersion);
							}
							vBox.add(Box.createVerticalStrut(10));
							{
								labelAction = new JLabel();
								vBox.add(labelAction);
							}
						}

						hBox.add(Box.createHorizontalStrut(10));

						panelRight = new ImagePanel();
						Image image = ImageResizer.resizeToNewWidth(PICTURE,
								120);
						panelRight.setImage(image);
						panelRight.repaint();
						panelRight.setBackground(new java.awt.Color(255, 255,
								255));
						ImageIcon imageIcon = new ImageIcon(image);
						panelRight.setPreferredSize(new Dimension(imageIcon
								.getIconWidth(), imageIcon.getIconHeight()));
						hBox.add(panelRight);
					}
				}
			}

			progressBar = new JProgressBar();
			panel.add(progressBar, BorderLayout.SOUTH);
		}

		this.pack();
		this.progressBar.setPreferredSize(new Dimension(this.getBounds().width,
				18));
		this.pack();
		this.setLocationRelativeTo(null);
		getRootPane().setDefaultButton(buttonCancel);

		// Add Listeners
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				menuExitPerformed();
			}
		});
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menuExitPerformed();
			}
		});
	}

	public void init() {

		try {
			String version = service.getVersion();
			if (version == null) {
				JOptionPane.showMessageDialog(this,
						"Can't determine current version of the application.\n Please run "
								+ TARGET_APPLICATION_NAME + " first.",
						"Information", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}

			int size = (int) service.getSize(facade.isDevMode());
			progressBar.setMinimum(0);
			progressBar.setMaximum(size);
			service.setDownload();
			labelUpatingVersion.setText("Updating to "
					+ TARGET_APPLICATION_NAME + " " + version);
			labelAction.setText("Downloading files...");
			System.out.println("Downloading files...");
			service.getFtpDAO().getDos().addObservateur(new Observateur() {
				@Override
				public void update(int value) {
					progressBar.setValue(value);
				}
			});
			service.download(facade.isDevMode());
			buttonCancel.setEnabled(false);
			labelAction.setText("Processing update...");
			System.out.println("Processing update...");
			service.install();
			JOptionPane.showMessageDialog(this, TARGET_APPLICATION_NAME
					+ " has been successfully updated to version " + version
					+ ".", "Update", JOptionPane.INFORMATION_MESSAGE);

			this.dispose();

			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				String command = "ArmA3Sync.exe";
				if (facade.isDevMode()) {
					command = command + " -dev";
				}
				try {
					Runtime.getRuntime().exec(command);
				} catch (IOException ex) {
					ex.printStackTrace();
				} finally {
					service.clean();
					System.exit(0);
				}
			}
		} catch (Exception e) {
			String message = "An error occured." + "\n" + e.getMessage() + "\n"
					+ "Update process aborded.";
			System.out.println(message);
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, message, "Error",
					JOptionPane.ERROR_MESSAGE);
			service.clean();
			System.exit(0);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonCancel) {
			menuExitPerformed();
		}
	}

	private class ImagePanel extends JPanel {

		private Image image = null;

		public void setImage(Image image) {
			this.image = image;
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g); // paint background
			if (image != null) { // there is a picture: draw it
				g.drawImage(image, 0, 0, this); // use image size
			}
		}
	}

	private void menuExitPerformed() {
		int rep = JOptionPane.showConfirmDialog(this, "Cancel update process?",
				"Update", JOptionPane.OK_CANCEL_OPTION);
		switch (rep) {
		case 0:
			System.exit(0);
			break;
		case 1:
			break;
		}
	}
}
