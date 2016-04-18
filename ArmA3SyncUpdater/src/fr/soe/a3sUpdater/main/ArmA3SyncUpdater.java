package fr.soe.a3sUpdater.main;

import java.awt.GraphicsEnvironment;

import javax.swing.UIManager;

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
		} else {
			// Apply default system look and feel
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}

			Updater updater = new Updater(facade);
			updater.setVisible(true);
			updater.init();
		}
	}
}
