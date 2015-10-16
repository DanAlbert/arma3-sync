package fr.soe.a3s.ui;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {

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
