package fr.soe.a3sUpdater.ui;

import java.awt.Image;

import javax.swing.ImageIcon;

public class ImageResizer {

	public static Image resizeToNewWidth(Image image, int newWidth) {

		ImageIcon imageIcon = new ImageIcon(image);
		int height = imageIcon.getIconHeight();
		int width = imageIcon.getIconWidth();
		int newHeight = newWidth * height / width;
		Image newImage = image.getScaledInstance(newWidth, newHeight,
				Image.SCALE_SMOOTH);
		return newImage;
	}
}
