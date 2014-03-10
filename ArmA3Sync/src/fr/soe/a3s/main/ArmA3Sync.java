package fr.soe.a3s.main;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import fr.soe.a3s.console.Console;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.mainEditor.MainPanel;

public class ArmA3Sync {

	/**
	 * This is the entry point for ArmA3Sync. ArmA3Sync is free software and
	 * agree the GNU Global Public Licence in version 3.
	 * 
	 * @author Major_Shepard for the [S.o.E] Team, visit us at
	 *         www.sonsofexiled.fr and BIS forum.
	 * @param args
	 *            command line parameters
	 * 
	 */

	public static void main(String[] args) {

		checkJRE();
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
			Console console = new Console(false);
			console.displayCommands();
			console.execute();
		} else if (args.length == 2 && args[0].equalsIgnoreCase("-dev")
				&& args[1].equalsIgnoreCase("-console")) {
			Console console = new Console(true);
			console.displayCommands();
			console.execute();
		} else if (args.length == 2 && args[0].equalsIgnoreCase("-build")) {
			Console console = new Console(false);
			String repositoryName = args[1];
			console.build(repositoryName);
		} else if (args.length == 2 && args[0].equalsIgnoreCase("-check")) {
			Console console = new Console(false);
			String repositoryName = args[1];
			console.check(repositoryName);
		} else {
			System.out.println("ArmA3Sync - bad command.");
			System.out.println("-BUILD " + "\"" + "NameOfRepository" + "\""
					+ ": build repository.");
			System.out.println("-CHECK " + "\"" + "NameOfRepository" + "\""
					+ ": check repository.");
			System.out.println("-CONSOLE: run ArmASync console management.");
		}
	}

	private static void checkJRE() {

		String version = System.getProperty("java.version");
		if (!version.contains("1.7.")) {
			String message = "JRE installed version is: " + version + "\n"
					+ "ArmA3Sync required JRE 1.7 (Java 7) to run.";
			if (!GraphicsEnvironment.isHeadless()) {
				JFrame frame = new JFrame();
				JOptionPane.showMessageDialog(frame, message, "ArmA3Sync",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				System.out.println(message);
			}
			System.exit(0);
		}
	}

	private static void start(Facade facade) {

		if (GraphicsEnvironment.isHeadless()) {
			System.out.println("Can't start ArmA3Sync. GUI is missing.");
			System.exit(1);
		}

		/* Set ui properties */
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
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* Single instance */
		if (!lockInstance()) {
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame,
					"ArmA3Sync is already running.", "ArmA3Sync",
					JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
		}

		/* Start */
		MainPanel mainPanel = new MainPanel(facade);
		mainPanel.drawGUI();
		mainPanel.init();
		mainPanel.setVisible(true);

		// Check ArmA 3 executable location
		mainPanel.checkWellcomeDialog();

		// Check for updates
		mainPanel.checkForUpdate(false);
	}

	private static boolean lockInstance() {

		final String path = "lock";
		try {
			final File file = new File(path);
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
							System.out
									.println(("Unable to remove lock file: " + path));
						}
					}
				});
				return true;
			}
		} catch (Exception e) {
			System.out.println(("Unable to create and/or lock file: " + path));
		}
		return false;
	}
}
