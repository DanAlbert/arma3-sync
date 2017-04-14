package fr.soe.a3s.main;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import it.sauronsoftware.junique.MessageHandler;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.Properties;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.jtattoo.plaf.aluminium.AluminiumLookAndFeel;
import com.jtattoo.plaf.graphite.GraphiteLookAndFeel;
import com.jtattoo.plaf.hifi.HiFiLookAndFeel;
import com.jtattoo.plaf.noire.NoireLookAndFeel;

import fr.soe.a3s.console.CommandConsole;
import fr.soe.a3s.console.CommandLine;
import fr.soe.a3s.constant.LookAndFeel;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.FileAccessMethods;
import fr.soe.a3s.service.PreferencesService;
import fr.soe.a3s.ui.ErrorLogDialog;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.main.MainPanel;

public class ArmA3Sync implements DataAccessConstants {

	/**
	 * This is the entry point for ArmA3Sync. ArmA3Sync is a free software and
	 * agree the GNU Global Public Licence in version 3.
	 * 
	 * @author Major_Shepard for the [S.o.E] Team, visit us at
	 *         www.sonsofexiled.fr and BIS forum.
	 * @param args
	 *            command line parameters
	 */

	private static MainPanel mainPanel;

	public static void main(String[] args) {

		checkArmA3SyncVersion();

		checkJREVersion();

		checkOSName();

		setFoldersAndPermissions();

		runArmA3Sync(args);
	}

	private static void checkArmA3SyncVersion() {

		System.out.println("ArmA3Sync Installed version = "
				+ Version.getVersion());
	}

	private static void checkJREVersion() {

		String version = System.getProperty("java.version");
		System.out.println("JRE installed version = " + version);
	}

	private static void checkOSName() {

		String osName = System.getProperty("os.name");
		System.out.println("OS Name = " + osName);
	}

	private static void setFoldersAndPermissions() {

		File profilesFolder = new File(PROFILES_FOLDER_PATH);
		profilesFolder.mkdir();
		File configurationFolder = new File(CONFIGURATION_FOLDER_PATH);
		configurationFolder.mkdirs();
		File repositoryFolder = new File(REPOSITORY_FOLDER_PATH);
		repositoryFolder.mkdirs();
		File tempFolder = new File(TEMP_FOLDER_PATH);
		tempFolder.mkdirs();

		File folder = new File(INSTALLATION_PATH);
		FileAccessMethods.setWritePermissions(folder);
	}

	private static void runArmA3Sync(String[] args) {

		if (args.length == 0) {
			start(false, false, false);
		} else if (args.length == 1 && args[0].equalsIgnoreCase("-dev")) {
			start(true, false, false);
		} else if (args.length == 1 && args[0].equalsIgnoreCase("-run")) {
			start(false, true, false);
		} else if (args.length == 1 && args[0].equalsIgnoreCase("-safe")) {
			start(false, false, true);
		} else if (args.length == 1 && args[0].equalsIgnoreCase("-console")) {
			CommandConsole console = new CommandConsole(false);
			console.displayCommands();
			console.execute();
		} else if (args.length == 2 && args[0].equalsIgnoreCase("-dev")
				&& args[1].equalsIgnoreCase("-console")) {
			CommandConsole console = new CommandConsole(true);
			console.displayCommands();
			console.execute();
		} else if (args.length == 2 && args[0].equalsIgnoreCase("-dev")
				&& args[1].equalsIgnoreCase("-run")) {
			System.out.println("DevMode = true");
			System.out.println("RunMode = true");
			start(true, true, false);
		} else if (args.length == 2 && args[0].equalsIgnoreCase("-build")) {
			CommandLine commandLine = new CommandLine();
			String repositoryName = args[1];
			commandLine.build(repositoryName);
		} else if (args.length == 2 && args[0].equalsIgnoreCase("-check")) {
			CommandLine commandLine = new CommandLine();
			String repositoryName = args[1];
			commandLine.check(repositoryName);
		} else if (args.length == 3 && args[0].equalsIgnoreCase("-extract")) {
			CommandLine commandLine = new CommandLine();
			commandLine.extractBikeys(args[1], args[2]);
		} else if (args.length == 4 && args[0].equalsIgnoreCase("-sync")) {
			CommandLine commandLine = new CommandLine();
			String repositoryName = args[1];
			String destinationFolderPath = args[2];
			String withExactMath = args[3];
			commandLine.sync(repositoryName, destinationFolderPath,
					withExactMath);
		} else if (args.length == 1 && args[0].equalsIgnoreCase("-update")) {
			CommandLine commandLine = new CommandLine();
			commandLine.checkForUpdates();
		} else {
			System.out.println("ArmA3Sync - bad command.");
			System.out.println("-BUILD " + "\"" + "Name of the Repository"
					+ "\"" + " : build repository.");
			System.out.println("-CHECK " + "\"" + "Name of the Repository"
					+ "\"" + " : check repository.");
			System.out.println("-CONSOLE: run ArmASync console management.");
			System.out.println("-EXTRACT " + "\"" + "Source folder path" + "\""
					+ " " + "\"" + "Destination folder path" + "\""
					+ " : extract *.bikey files.");
			System.out.println("-SYNC " + "\"" + "Name of the Repository"
					+ "\"" + " " + "\"" + "Destination folder path" + "\""
					+ " " + "true/false (with/without exact content matching)"
					+ " : synchronize with repository.");
			System.out.println("-UPDATE : check for ArmA3Sync updates.");
		}
	}

	private static void start(final boolean devMode, final boolean runMode,
			final boolean safeMode) {

		System.out.println("DevMode = " + devMode);
		System.out.println("RunMode = " + runMode);
		System.out.println("SafeMode = " + safeMode);

		if (GraphicsEnvironment.isHeadless()) {
			System.out.println("Can't start ArmA3Sync. GUI is missing.");
			System.exit(1);
		} else {
			applyLookAndFeel(safeMode);
		}

		/*
		 * Start with single instance using JUnique
		 * http://www.sauronsoftware.it/projects/junique/manual.php
		 */
		String appId = ArmA3Sync.class.getName();
		boolean alreadyRunning;
		try {
			JUnique.acquireLock(appId, new MessageHandler() {
				@Override
				public String handle(String message) {
					// http://stackoverflow.com/questions/309023/how-to-bring-a-window-to-the-front
					mainPanel.setAlwaysOnTop(true);
					mainPanel.setToFront();
					mainPanel.requestFocus();
					mainPanel.setAlwaysOnTop(false);
					return null;
				}
			});
			alreadyRunning = false;
		} catch (AlreadyLockedException e) {
			alreadyRunning = true;
		}
		if (!alreadyRunning) {
			// Start sequence
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					final Facade facade = new Facade();
					facade.setDevMode(devMode);
					facade.setRunMode(runMode);
					facade.setSafeMode(safeMode);
					try {
						mainPanel = new MainPanel(facade);
						mainPanel.drawGUI();
						mainPanel.init();
						mainPanel.initBackGround();
					} catch (Exception e) {
						e.printStackTrace();
						ErrorLogDialog dialog = new ErrorLogDialog(facade, e);
						dialog.show();
					}
				}
			});
		} else {
			JUnique.sendMessage(appId, "");
			Runtime.getRuntime().halt(1);
		}
	}

	@Deprecated
	private static String lockInstance() {

		final String path = "lock";
		final File file = new File(path);
		String message = "";

		boolean response = false;
		try {
			final RandomAccessFile randomAccessFile = new RandomAccessFile(
					file, "rw");
			final FileLock fileLock = randomAccessFile.getChannel().tryLock();
			if (fileLock != null) {
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						try {
							fileLock.release();
							randomAccessFile.close();
							file.delete();
						} catch (Exception e) {
							System.out.println("Unable to remove lock file: "
									+ path);
						}
					}
				});
				response = true;
			}
		} catch (Exception e) {
			System.out.println("Unable to create and/or lock file: " + path);
			if (!file.canWrite()) {
				message = "Cannot write into installation directory."
						+ "\n"
						+ "ArmA3Sync requires full write permissions on its whole installation directory."
						+ "\n"
						+ "Try to run with administator priviledges. Checkout file permissions.";
			}
			response = false;
		}

		if (!response && message.isEmpty()) {
			message = "ArmA3Sync is already running.";
		}
		return message;
	}

	private static void applyLookAndFeel(final boolean safeMode) {

		// Apply default system look and feel
		try {
			if (safeMode) {
				UIManager.setLookAndFeel(UIManager
						.getCrossPlatformLookAndFeelClassName());
			} else {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Apply preferences look and feel
		try {
			PreferencesService preferencesService = new PreferencesService();
			preferencesService.read();
			LookAndFeel lookAndFeel = preferencesService.getPreferences()
					.getLookAndFeel();
			if (!lookAndFeel.equals(LookAndFeel.LAF_DEFAULT)) {
				Properties props = new Properties();
				props.put("logoString", "");
				props.put("menuOpaque", "on");
				props.put("textAntiAliasing", "on");
				props.put("windowDecoration", "on");

				{
					Font font = UIManager.getFont("Label.font");
					String fontName = font.getFontName();
					int sytle = font.getStyle();
					int size = font.getSize();
					props.put("userTextFont",
							fontName + " " + Integer.toString(sytle) + " "
									+ Integer.toString(size));
					props.put("subTextFont",
							fontName + " " + Integer.toString(sytle) + " "
									+ Integer.toString(size));

					font = UIManager.getFont("Button.font");
					fontName = font.getFontName();
					sytle = font.getStyle();
					size = font.getSize();
					props.put("controlTextFont",
							fontName + " " + Integer.toString(sytle) + " "
									+ Integer.toString(size));

					font = UIManager.getFont("Menu.font");
					fontName = font.getFontName();
					sytle = font.getStyle();
					size = font.getSize();
					props.put("menuTextFont",
							fontName + " " + Integer.toString(sytle) + " "
									+ Integer.toString(size));
				}

				if (lookAndFeel.equals(LookAndFeel.LAF_ALUMINIUM)) {
					AluminiumLookAndFeel.setCurrentTheme(props);
					UIManager.setLookAndFeel(new AluminiumLookAndFeel());
				} else if (lookAndFeel.equals(LookAndFeel.LAF_GRAPHITE)) {
					GraphiteLookAndFeel.setCurrentTheme(props);
					UIManager.setLookAndFeel(new GraphiteLookAndFeel());
				} else if (lookAndFeel.equals(LookAndFeel.LAF_HIFI)) {
					HiFiLookAndFeel.setCurrentTheme(props);
					UIManager.setLookAndFeel(new HiFiLookAndFeel());
				} else if (lookAndFeel.equals(LookAndFeel.LAF_NOIRE)) {
					NoireLookAndFeel.setCurrentTheme(props);
					UIManager.setLookAndFeel(new NoireLookAndFeel());
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// Set English Language as default for JOptionPane
		UIManager.put("OptionPane.yesButtonText", "Yes");
		UIManager.put("OptionPane.noButtonText", "No");
		UIManager.put("OptionPane.cancelButtonText", "Cancel");
	}
}
