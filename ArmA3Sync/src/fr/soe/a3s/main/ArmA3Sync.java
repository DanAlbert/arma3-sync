package fr.soe.a3s.main;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import com.jtattoo.plaf.aluminium.AluminiumLookAndFeel;
import com.jtattoo.plaf.graphite.GraphiteLookAndFeel;
import com.jtattoo.plaf.hifi.HiFiLookAndFeel;
import com.jtattoo.plaf.noire.NoireLookAndFeel;

import fr.soe.a3s.console.CommandConsole;
import fr.soe.a3s.console.CommandLine;
import fr.soe.a3s.constant.LookAndFeel;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.service.PreferencesService;
import fr.soe.a3s.ui.ErrorLogDialog;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.mainEditor.MainPanel;

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

	public static void main(String[] args) {

		checkArmA3SyncVersion();

		checkJRE();

		setFoldersAndPermissions();

		runArmA3Sync(args);
	}

	private static void checkArmA3SyncVersion() {

		System.out.println("ArmA3Sync Installed version = "
				+ Version.getVersion());
	}

	private static void checkJRE() {

		String version = System.getProperty("java.version");
		System.out.println("JRE installed version = " + version);

		String specification = System.getProperty("java.specification.version");

		if (!(Double.parseDouble(specification) >= 1.7)) {
			String message = "JRE installed version = " + version + "\n"
					+ "ArmA3Sync required JRE 1.7 (Java 7) or above to run.";
			System.out.println(message);
			if (!GraphicsEnvironment.isHeadless()) {
				JFrame frame = new JFrame();
				JOptionPane.showMessageDialog(frame, message, "ArmA3Sync",
						JOptionPane.INFORMATION_MESSAGE);
			}
			System.exit(0);
		}
	}

	private static void setFoldersAndPermissions() {

		File profilesFolder = new File(PROFILES_FOLDER_PATH);
		profilesFolder.mkdir();
		profilesFolder.setWritable(true);
		boolean profilesOK = Files.isWritable(FileSystems.getDefault().getPath(
				profilesFolder.getAbsolutePath()));
		//
		File configurationFolder = new File(CONFIGURATION_FOLDER_PATH);
		configurationFolder.mkdirs();
		configurationFolder.setWritable(true);
		boolean configurationOK = Files.isWritable(FileSystems.getDefault()
				.getPath(configurationFolder.getAbsolutePath()));
		//
		File ftpFolder = new File(REPOSITORY_FOLDER_PATH);
		ftpFolder.mkdirs();
		ftpFolder.setWritable(true);
		boolean ftpOK = Files.isWritable(FileSystems.getDefault().getPath(
				ftpFolder.getAbsolutePath()));
		//
		File tempFolder = new File(TEMP_FOLDER_PATH);
		tempFolder.mkdirs();
		tempFolder.setWritable(true);
		boolean tempOK = Files.isWritable(FileSystems.getDefault().getPath(
				tempFolder.getAbsolutePath()));

		String message = "";
		if (!profilesOK) {
			message = "Cannot write into directory: profiles";
		} else if (!configurationOK) {
			message = "Cannot write into directory: resources" + File.separator
					+ "configuration";
		} else if (!ftpOK) {
			message = "Cannot write into directory: resources" + File.separator
					+ "ftp";
		} else if (!tempOK) {
			message = "Cannot write into directory: resources" + File.separator
					+ "temp";
		}

		if (!message.isEmpty()) {
			message = message
					+ "\n"
					+ "ArmA3Sync requires full write permissions on its whole installation directory."
					+ "\n"
					+ "Try to run with administator priviledges and checkout file access permissions.";

			System.out.println(message);
			if (!GraphicsEnvironment.isHeadless()) {
				JFrame frame = new JFrame();
				JOptionPane.showMessageDialog(frame, message, "ArmA3Sync",
						JOptionPane.INFORMATION_MESSAGE);
			}
			System.exit(0);
		}
	}

	private static void runArmA3Sync(String[] args) {

		if (args.length == 0) {
			System.out.println("DevMode = false");
			start(false);
		} else if (args.length == 1 && args[0].equalsIgnoreCase("-dev")) {
			System.out.println("DevMode = true");
			start(true);
		} else if (args.length == 1 && args[0].equalsIgnoreCase("-console")) {
			CommandConsole console = new CommandConsole(false);
			console.displayCommands();
			console.execute();
		} else if (args.length == 2 && args[0].equalsIgnoreCase("-dev")
				&& args[1].equalsIgnoreCase("-console")) {
			CommandConsole console = new CommandConsole(true);
			console.displayCommands();
			console.execute();
		} else if (args.length == 2 && args[0].equalsIgnoreCase("-build")) {
			CommandLine commandLine = new CommandLine();
			String repositoryName = args[1];
			commandLine.build(repositoryName);
		} else if (args.length == 2 && args[0].equalsIgnoreCase("-check")) {
			CommandLine commandLine = new CommandLine();
			String repositoryName = args[1];
			commandLine.check(repositoryName);
		} else if (args.length == 3 && args[0].equalsIgnoreCase("-extract")){
			CommandLine commandLine = new CommandLine();
			commandLine.extractBikeys(args[1], args[2]);
		}else if (args.length == 4 && args[0].equalsIgnoreCase("-sync")) {
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
			System.out.println("-EXTRACT " + "\"" + "Source folder path"
					+ "\"" + " " + "\"" + "Destination folder path" + "\""
					+ " : extract *.bikey files.");
			System.out.println("-SYNC " + "\"" + "Name of the Repository"
					+ "\"" + " " + "\"" + "Destination folder path" + "\""
					+ " " + "true/false (with/without exact content matching)"
					+ " : synchronize with repository.");
			System.out.println("-UPDATE : check for ArmA3Sync updates.");

		}
	}

	private static void start(boolean devMode) {

		if (GraphicsEnvironment.isHeadless()) {
			System.out.println("Can't start ArmA3Sync. GUI is missing.");
			System.exit(1);
		}

		/* Apply Look and Feel */
		applyLookAndFeel();

		/* Set Single Instance */
		String message = lockInstance();
		if (!message.isEmpty()) {
			JFrame frame = new JFrame();
			System.out.println(message);
			JOptionPane.showMessageDialog(frame, message, "ArmA3Sync",
					JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
		}

		/* Start */
		final Facade facade = new Facade();
		facade.setDevMode(devMode);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MainPanel mainPanel = new MainPanel(facade);
					mainPanel.drawGUI();
					mainPanel.init();
					mainPanel.initBackGround();
				} catch (Exception e) {
					ErrorLogDialog dialog = new ErrorLogDialog(facade, e);
					dialog.show();
				}
			}
		});
	}

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

	private static void applyLookAndFeel() {

		try {
			String osName = System.getProperty("os.name");
			if (osName.contains("Windows")) {
				javax.swing.UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			} else {
				Font fontMenu = UIManager.getFont("Menu.font");
				Font fontMenuA3S = new Font(fontMenu.getName(), Font.PLAIN, 12);
				javax.swing.UIManager.put("Menu.font", new FontUIResource(
						fontMenuA3S));

				Font fontMenuItem = UIManager.getFont("MenuItem.font");
				Font fontMenuItemA3S = new Font(fontMenuItem.getName(),
						Font.PLAIN, 11);
				javax.swing.UIManager.put("MenuItem.font", new FontUIResource(
						fontMenuItemA3S));

				Font fontCheckBoxMenuItem = UIManager
						.getFont("CheckBoxMenuItem.font");
				Font fontCheckBoxMenuItemA3S = new Font(
						fontCheckBoxMenuItem.getName(), Font.PLAIN, 11);
				javax.swing.UIManager.put("CheckBoxMenuItem.font",
						new FontUIResource(fontCheckBoxMenuItemA3S));

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

				Font fontComboBox = UIManager.getFont("ComboBox.font");
				Font fontComboBoxA3S = new Font(fontComboBox.getName(),
						Font.PLAIN, 11);
				UIManager.put("ComboBox.font", new FontUIResource(
						fontComboBoxA3S));

				Font fontCheckBox = UIManager.getFont("CheckBox.font");
				Font fontCheckBoxA3S = new Font(fontCheckBox.getName(),
						Font.PLAIN, 11);
				UIManager.put("CheckBox.font", new FontUIResource(
						fontCheckBoxA3S));

				Font tabbedPane = UIManager.getFont("TabbedPane.font");
				Font tabbedPaneA3S = new Font(tabbedPane.getName(), Font.PLAIN,
						11);
				UIManager.put("TabbedPane.font", new FontUIResource(
						tabbedPaneA3S));

				Font tittleBorder = UIManager.getFont("TitledBorder.font");
				Font tittleBorderA3S = new Font(tittleBorder.getName(),
						Font.PLAIN, 11);
				UIManager.put("TitledBorder.font", new FontUIResource(
						tittleBorderA3S));

				Font textArea = UIManager.getFont("TextArea.font");
				Font textAreaA3S = new Font(textArea.getName(), Font.PLAIN, 11);
				UIManager.put("textArea.font", new FontUIResource(textAreaA3S));

				Font listArea = UIManager.getFont("List.font");
				Font listAreaA3S = new Font(listArea.getName(), Font.PLAIN, 11);
				UIManager.put("List.font", new FontUIResource(listAreaA3S));
			}

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
			e.printStackTrace();
		}
	}
}
