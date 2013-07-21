package fr.soe.a3s.ui.about;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
/**
 * Credit dialog.
 *
 */

public class CreditsPanel extends JDialog implements ActionListener,UIConstants {

	private JButton jButtonClose;
	private JPanel jPanel1;
	private JTextArea jTextArea1;
	private ImagePanel jPanel2;

	public CreditsPanel(Facade facade) {
		super(facade.getMainPanel(), "Credits", true);

		this.setSize(385, 290);
		setResizable(false);
		setIconImage(ICON);
		this.setLocation((int) facade.getMainPanel().getLocation().getX()
				+ facade.getMainPanel().getWidth() / 2 - this.getWidth() / 2,
				(int) facade.getMainPanel().getLocation().getY()
						+ facade.getMainPanel().getHeight() / 2
						- this.getHeight() / 2);

		getContentPane().setLayout(null);
		
		jButtonClose = new JButton();
		getContentPane().add(jButtonClose, "North");
		jButtonClose.setText("Close");
		jButtonClose.setBounds(287, 224, 76, 27);
		jButtonClose.addActionListener(this);

		jPanel1 = new JPanel();
		getContentPane().add(jPanel1);
		jPanel1.setBounds(15, 11, 348, 207);
		jPanel1.setLayout(null);
		jPanel1
				.setBorder(BorderFactory
						.createEtchedBorder(BevelBorder.LOWERED));
		jPanel1.setBackground(new java.awt.Color(255, 255, 255));

		jPanel2 = new ImagePanel();
		jPanel1.add(jPanel2);
		jPanel2.setBounds(154, 13, 180, 179);
		jPanel2.setBackground(new java.awt.Color(255, 255, 255));
		ImageIcon imageIcon = new ImageIcon(java.lang.ClassLoader
				.getSystemResource("resources/pictures/system/soe2.png"));
			Image myNewImage = imageIcon.getImage();
			jPanel2.setImage(myNewImage);
		{
			jTextArea1 = new JTextArea();
			jPanel1.add(jTextArea1);
			jTextArea1.setBounds(15, 19, 188, 173);
			
			jTextArea1.setText("Software development\n" +"[S.o.E] Major_Shepard\n" +"\nGraphical design\n" +"[S.o.E] Matt2507\n" + "\nTesting\n" + "[S.o.E] Team Members\n" + "\nInspired from \n" + "ArmA II Game Launcher\n" + "by SpiritedMachine.");
			jTextArea1.setFont(new java.awt.Font("Tahoma",0,11));
			jTextArea1.setEditable(false);

		}
		jPanel2.repaint();

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jButtonClose) {
			setVisible(false);
			dispose();
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
}
