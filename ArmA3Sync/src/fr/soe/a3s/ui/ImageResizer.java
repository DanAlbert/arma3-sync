package fr.soe.a3s.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

import fr.soe.a3s.constant.IconResize;
import fr.soe.a3s.service.PreferencesService;

public class ImageResizer {

	private static final PreferencesService preferencesService = new PreferencesService();

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

	public static Image resizeToScreenResolution(Image image) {

		ImageIcon imageIcon = new ImageIcon(image);
		int imageHeight = imageIcon.getIconHeight();
		int imageWidth = imageIcon.getIconWidth();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenHeight = (int) screenSize.getHeight();
		int screenWidth = (int) screenSize.getWidth();

		// int screenHeight = 2160;
		// int screenWidth = 3840;

		int newImageHeight = imageHeight;
		int newImageWidth = imageWidth;

		IconResize iconResize = preferencesService.getPreferences()
				.getIconResizeSize();
		if (iconResize.equals(IconResize.AUTO)) {
			newImageHeight = screenHeight * imageHeight / 1050;
			newImageWidth = screenWidth * imageWidth / 1680;
		} else {
			newImageHeight = (int) (imageHeight * iconResize.getValue());
			newImageWidth = (int) (imageWidth * iconResize.getValue());
		}

		Image newImage = image.getScaledInstance(newImageWidth, newImageHeight,
				Image.SCALE_SMOOTH);
		return newImage;
	}
}
