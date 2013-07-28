package fr.soe.a3sUpdater.main;

import fr.soe.a3sUpdater.ui.Facade;
import fr.soe.a3sUpdater.ui.Updater;

public class ArmA3SyncUpdater {

	public static void main(String[] args) {
		
		/* set ui properties */
		try {
			String osName = System.getProperty("os.name");
			if (osName.contains("Windows")) {
				javax.swing.UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			} else if (osName.contains("Linux")) {
				javax.swing.UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			}
		} catch (Exception e) {
		}
		
		
		String path = "";
		if (args.length != 0) {
			path = args[0].trim();
		}
		
		Facade facade = new Facade();
		
		/* Run in dev mode */
		if (path.contains("-dev")) {
			facade.setDevMode(true);
			System.out.println("DevMode=true");
		} else {
			facade.setDevMode(false);
			System.out.println("DevMode=false");
		}
		Updater updater = new Updater(facade);
		updater.setVisible(true);
		updater.init();
	}

}
