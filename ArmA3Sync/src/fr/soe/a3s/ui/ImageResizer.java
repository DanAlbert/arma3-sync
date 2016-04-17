package fr.soe.a3s.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

public class ImageResizer {

	public enum Resizing {
		SMALL, MEDIUM
	};

	public static Image resizeToNewWidth(Image image, int newWidth) {

		ImageIcon imageIcon = new ImageIcon(image);
		int height = imageIcon.getIconHeight();
		int width = imageIcon.getIconWidth();
		int newHeight = newWidth * height / width;
		Image newImage = image.getScaledInstance(newWidth, newHeight,
				Image.SCALE_SMOOTH);
		return newImage;
	}

	public static Image resizeToNewHeight(Image image, int newHeight) {

		ImageIcon imageIcon = new ImageIcon(image);
		int height = imageIcon.getIconHeight();
		int width = imageIcon.getIconWidth();
		int newWidth = newHeight * width / height;
		Image newImage = image.getScaledInstance(newWidth, newHeight,
				Image.SCALE_SMOOTH);
		return newImage;
	}

	public static Image resize(Image image, int newWidth, int newHeight) {

		ImageIcon imageIcon = new ImageIcon(image);
		Image newImage = image.getScaledInstance(newWidth, newHeight,
				Image.SCALE_SMOOTH);
		return newImage;
	}

	public static Image resizeToScreenResolution(Image image, Resizing resizing) {

		ImageIcon imageIcon = new ImageIcon(image);
		int imageHeight = imageIcon.getIconHeight();
		int imageWidth = imageIcon.getIconWidth();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenHeight = (int) screenSize.getHeight();
		int screenWidth = (int) screenSize.getWidth();

		// int screenHeight = 2160;
		// int screenWidth = 3840;

		int newImageHeight = 0;
		int newImageWidth = 0;

		if (resizing.equals(Resizing.SMALL)) {
			newImageHeight = screenHeight * 16 / 1050;
			newImageWidth = screenWidth * 16 / 1680;
		} else if (resizing.equals(Resizing.MEDIUM)) {
			newImageHeight = screenHeight * 24 / 1050;
			newImageWidth = screenWidth * 24 / 1680;
		}

		if (newImageHeight > newImageWidth) {
			newImageWidth = newImageHeight;
		} else {
			newImageHeight = newImageWidth;
		}

		if (newImageHeight < 16 || newImageWidth < 16) {
			newImageHeight = 16;
			newImageWidth = 16;
		}

		Image newImage = image.getScaledInstance(newImageWidth, newImageHeight,
				Image.SCALE_SMOOTH);
		return newImage;
	}
}
