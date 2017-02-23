package fr.soe.a3s.ui;

import java.awt.Image;
import java.awt.Toolkit;

public interface UIConstants {

	String APPLICATION_NAME = "ArmA3Sync";

	int DEFAULT_HEIGHT = 665;
	
	int OP_PROFILE_CHANGED = 1;
	int OP_ADDON_FILES_CHANGED = 2;
	int OP_ADDON_PRIORITY_CHANGED = 3;
	int OP_ADDON_SELECTION_CHANGED = 4;
	int OP_ONLINE_CHANGED = 5;
	int OP_REPOSITORY_CHANGED = 6;
	int OP_GROUP_CHANGED = 7;

	Image ICON = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/ArmA3SyncBlue32x32.png"));

	Image TRAYICON = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/ArmA3SyncBlue16x16.png"));

	Image PICTURE = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/ArmA3SyncBlue64x64.png"));

	Image SOE = Toolkit.getDefaultToolkit().getImage(
			java.lang.ClassLoader
					.getSystemResource("resources/pictures/system/soe2.png"));

	/* Buttons */

	Image ADD = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/add16x16.png"));

	Image EDIT = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/edit16x16.png"));

	Image DELETE = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/delete16x16.png"));

	Image ADMIN = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/admin16x16.png"));

	Image REPORT = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/report16x16.png"));

	Image CONNECT = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/import16x16.png"));

	Image ONOFF = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/on_off_icon16x16.png"));

	Image REFRESH = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/sync16x16.png"));

	Image TOP = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/top16x16.png"));

	Image UP = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/up16x16.png"));

	Image DOWN = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/down16x16.png"));

	Image REPOSITORY = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/repository24x24.png"));

	Image DOWNLOAD = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/download24x24.png"));

	Image EVENTS = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/events24x24.png"));

	Image START = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/start16x16.png"));

	Image CHECK = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/check16x16.png"));

	Image PAUSE = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/pause16x16.png"));
	Image UPLOAD = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/upload16x16.png"));

	Image SAVE = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/save16x16.png"));

	/* Menu */

	Image SHORTCUT = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/shortcut_16x16.png"));

	Image ACRE_SMALL = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/acre16x16.png"));

	Image ACRE_BIG = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/acre48x48.png"));

	Image ACRE2_SMALL = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/acre216x16.png"));

	Image ACRE2_BIG = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/acre248x48.png"));

	Image AIA_SMALL = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/transmission16x16.png"));

	Image AIA_BIG = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/allinarma.png"));

	Image TFAR_SMALL = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/tfar18x18.png"));

	Image TFAR_BIG = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/tfar48x48.png"));

	Image RPT_SMALL = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/rpt16x16.png"));

	Image BIKEY_BIG = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/bikey48x48.png"));

	Image BIKEY_SMALL = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/bikey16x16.png"));

	Image HELP = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/help16x16.png"));

	Image BIS = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/bis16x16.png"));

	Image PREFERENCES = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/preferences16x16.png"));

	Image UPDATE = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/update16x16.png"));

	Image ABOUT = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/about16x16.png"));

	/* Repository Tree */

	Image EXCLAMATION = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/exclamation16x16.png"));

	Image BRICK = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/brick_16x16.png"));

	/* Repository Tab */

	Image CLOSE_GRAY = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/closegray12x12.png"));

	Image CLOSE_RED = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/closered12x12.png"));

	/* Connection lost */

	Image WARNING = Toolkit
			.getDefaultToolkit()
			.getImage(
					java.lang.ClassLoader
							.getSystemResource("resources/pictures/system/warning32x32.png"));
}
