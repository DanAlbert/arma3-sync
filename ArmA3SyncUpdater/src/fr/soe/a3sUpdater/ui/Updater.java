package fr.soe.a3sUpdater.ui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
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

	private JTextArea jTextAreaUpdating;
	private JProgressBar progressBar;
	private JButton buttonCancel;
	private JTextArea jTextArea5;
	private JTextArea jTextArea4;
	private JTextArea jTextAreaAction;
	private JTextArea jTextArea1;
	private ImagePanel jPanel2;
	private JPanel jPanel1;
	private Facade facade;

	public Updater(Facade facade) {

		this.facade = facade;
		getContentPane().setLayout(null);
		this.setTitle("Update");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		this.setSize(360, 285);
		setIconImage(ICON);
		setLocationRelativeTo(null);

		{
			jPanel1 = new JPanel();
			getContentPane().add(jPanel1);
			jPanel1.setBounds(10, 11, 332, 179);
			jPanel1.setLayout(null);
			jPanel1.setBorder(BorderFactory
					.createEtchedBorder(BevelBorder.LOWERED));
			jPanel1.setBackground(new java.awt.Color(255, 255, 255));
		}
		{
			jPanel2 = new ImagePanel();
			jPanel1.add(jPanel2);
			jPanel2.setBounds(214, 53, 104, 112);
			jPanel2.setBackground(new java.awt.Color(255, 255, 255));
			jPanel2.setImage(PICTURE);
			jPanel2.repaint();
		}
		{
			jTextArea1 = new JTextArea();
			jPanel1.add(jTextArea1);
			jTextArea1.setText(APPLICATION_NAME);
			jTextArea1.setBounds(12, 7, 310, 35);
			jTextArea1.setFont(new java.awt.Font("Tahoma", 1, 28));
			jTextArea1.setEditable(false);
		}
		{
			jTextArea4 = new JTextArea();
			jPanel1.add(jTextArea4);
			jTextArea4.setText("by the [S.o.E] Team,");
			jTextArea4.setBounds(12, 53, 157, 24);
			jTextArea4.setFont(new java.awt.Font("Tahoma", 1, 11));
			jTextArea4.setEditable(false);
		}
		{
			jTextArea5 = new JTextArea();
			jPanel1.add(jTextArea5);
			jTextArea5.setText("www.sonsofexiled.fr");
			jTextArea5.setBounds(12, 76, 133, 22);
			jTextArea5.setFont(new java.awt.Font("Tahoma", 1, 11));
			jTextArea5.setEditable(false);
		}
		{
			jTextAreaUpdating = new JTextArea();
			jPanel1.add(jTextAreaUpdating);
			jTextAreaUpdating.setBounds(12, 119, 184, 26);
			jTextAreaUpdating.setFont(new java.awt.Font("Tahoma", 0, 11));
			jTextAreaUpdating.setText("Updating");
		}
		{
			jTextAreaAction = new JTextArea();
			jPanel1.add(jTextAreaAction);
			jTextAreaAction.setFont(new java.awt.Font("Tahoma", 0, 11));
			jTextAreaAction.setText("Processing update...");
			jTextAreaAction.setBounds(12, 150, 148, 23);
		}
		{
			buttonCancel = new JButton();
			getContentPane().add(buttonCancel);
			buttonCancel.setText("Cancel");
			buttonCancel.setBounds(268, 220, 75, 27);
		}
		{
			progressBar = new JProgressBar();
			getContentPane().add(progressBar);
			progressBar.setBounds(10, 190, 332, 18);
		}
		// Add Listeners
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				menuExitPerformed();
			}
		});
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuExitPerformed();
			}
		});
	}

	public void init() {
		Service service = new Service();
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
			jTextAreaUpdating.setText("Updating to " + TARGET_APPLICATION_NAME
					+ " " + version);
			jTextAreaAction.setText("Downloading files...");
			service.getFtpDAO().getDos().addObservateur(new Observateur() {
				public void update(int value) {
					progressBar.setValue(value);
				}
			});
			service.download(facade.isDevMode());
			buttonCancel.setEnabled(false);
			jTextAreaAction.setText("Processing update...");
			service.install();
			JOptionPane.showMessageDialog(this, TARGET_APPLICATION_NAME
					+ " has been successfully updated to version " + version
					+ ".", "Update", JOptionPane.INFORMATION_MESSAGE);

			this.dispose();

			String command = "java -jar -Djava.net.preferIPv4Stack=true -Xms256m -Xmx256m -Dsun.java2d.d3d=false "
					+ TARGET_APPLICATION_NAME + ".jar";

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
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"An error occured." + "\n" + e.getMessage() + "\n"
							+ "Update process aborded.", "Error",
					JOptionPane.ERROR_MESSAGE);
			service.clean();
			System.exit(0);
		}
	}

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
