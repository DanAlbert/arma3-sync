package fr.soe.a3sUpdater.main;

import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

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
			} else {
				Font fontButton = UIManager.getFont("Button.font");
				Font fontButtonA3S = new Font(fontButton.getName(),Font.PLAIN,11);
				javax.swing.UIManager.put("Button.font", new FontUIResource(fontButtonA3S));
				
				Font fontLabel = UIManager.getFont("Label.font");
				Font fontLabelA3S = new Font(fontLabel.getName(),Font.PLAIN,11);
				UIManager.put("Label.font", new FontUIResource(fontLabelA3S));
				
				Font fontTextField = UIManager.getFont("TextField.font");
				Font fontTextFieldA3S = new Font(fontTextField.getName(),fontTextField.getStyle(),11);
				UIManager.put("TextField", new FontUIResource(fontTextFieldA3S));
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
