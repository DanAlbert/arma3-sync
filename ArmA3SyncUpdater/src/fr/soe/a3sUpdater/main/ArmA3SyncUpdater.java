package fr.soe.a3sUpdater.main;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import fr.soe.a3sUpdater.console.Console;
import fr.soe.a3sUpdater.ui.Facade;
import fr.soe.a3sUpdater.ui.Updater;

public class ArmA3SyncUpdater {

	public static void main(String[] args) {

		Facade facade = new Facade();
		
		if (args.length == 0) {
			facade.setDevMode(false);
			System.out.println("DevMode=false");
			start(facade);
		} else if (args.length == 1 && args[0].equalsIgnoreCase("-dev")) {
			facade.setDevMode(true);
			System.out.println("DevMode=true");
			start(facade);
		} else if (args.length == 1 && args[0].equalsIgnoreCase("-console")) {
			System.out.println("DevMode=false");
			Console console = new Console(false);
			console.execute();
		} else if (args.length == 2 && args[0].equalsIgnoreCase("-dev")
				&& args[1].equalsIgnoreCase("-console")) {
			System.out.println("DevMode=true");
			Console console = new Console(true);
			console.execute();
		} else {
			System.out.println("ArmA3Sync-Updater - command not found.");
		}
	}

	private static void start(Facade facade) {

		if (GraphicsEnvironment.isHeadless()) {
			System.out
					.println("Can't start ArmA3Sync Updater. GUI is missing.");
			System.exit(1);
		}

		/* set ui properties */
		try {
			String osName = System.getProperty("os.name");
			if (osName.contains("Windows")) {
				javax.swing.UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			} else {
				Font fontButton = UIManager.getFont("Button.font");
				Font fontButtonA3S = new Font(fontButton.getName(), Font.PLAIN,
						11);
				javax.swing.UIManager.put("Button.font", new FontUIResource(
						fontButtonA3S));

				Font fontLabel = UIManager.getFont("Label.font");
				Font fontLabelA3S = new Font(fontLabel.getName(), Font.PLAIN,
						11);
				UIManager.put("Label.font", new FontUIResource(fontLabelA3S));

				Font fontTextField = UIManager.getFont("TextField.font");
				Font fontTextFieldA3S = new Font(fontTextField.getName(),
						fontTextField.getStyle(), 11);
				UIManager
						.put("TextField", new FontUIResource(fontTextFieldA3S));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Updater updater = new Updater(facade);
		updater.setVisible(true);
		updater.init();
	}
}
