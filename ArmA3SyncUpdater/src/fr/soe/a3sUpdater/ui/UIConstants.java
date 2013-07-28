package fr.soe.a3sUpdater.ui;

import java.awt.Image;
import java.awt.Toolkit;

public interface UIConstants {

	String APPLICATION_NAME = "ArmA3Sync Updater";
	String TARGET_APPLICATION_NAME = "ArmA3Sync";
	
	Image ICON = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/ArmA3SyncBlue24x24.png"));
	Image PICTURE = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/ArmA3SyncBlue64x64.png"));
}
