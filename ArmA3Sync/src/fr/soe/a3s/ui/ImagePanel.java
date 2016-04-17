package fr.soe.a3s.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {

	private Image image = null;

	public void setImage(Image image) {
		this.image = image;
		if (image != null) {
			ImageIcon imageIcon = new ImageIcon(image);
			int height = imageIcon.getIconHeight();
			int width = imageIcon.getIconWidth();
			this.setPreferredSize(new Dimension(width, height));
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // paint background
		if (image != null) { // there is a picture: draw it
			g.drawImage(image, 0, 0, this); // use image size
			// int height = this.getSize().height;
			// int width = this.getSize().width;
			// g.drawImage(image, 0, 0, width, height, this);
		}
	}
}
