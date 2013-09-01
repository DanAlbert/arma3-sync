package fr.soe.a3s.main;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.Console;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import fr.soe.a3s.constant.EncryptionMode;
import fr.soe.a3s.constant.MinimizationType;
import fr.soe.a3s.controller.ObserverFileSize;
import fr.soe.a3s.dto.FtpDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.RepositoryCheckException;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.exception.ServerInfoNotFoundException;
import fr.soe.a3s.exception.SyncFileNotFoundException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.service.RepositoryService;
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

		Facade facade = new Facade();

		if (args.length != 0) {
			String command = args[0];
			if (command.contains("-version")) {
				displayVersion();
			} else if (command.contains("-list")) {
				listRepositories();
			} else if (command.contains("-build")) {
				buildRepository();
			} else if (command.contains("-check")) {
				checkRepository();
			} else if (command.contains("-update")) {
				updateRepository();
			} else if (command.contains("-help")) {
				displayHelp();
			}
			if (command.contains("-dev")) {
				facade.setDevMode(true);
				System.out.println("DevMode=true");
			} else {
				facade.setDevMode(false);
				System.out.println("DevMode=false");
			}
		}

		if (!GraphicsEnvironment.isHeadless()) {
			start(facade);
		} else {
			System.out.println("Can't start ArmA3Sync. GUI is missing");
		}
	}

	private static void displayVersion() {

		System.out.println("ArmA3Sync version " + Version.getName());
		System.out.println("Build " + Version.getVersion() + " ("
				+ Version.getYear() + ")");
		System.exit(0);
	}

	private static void listRepositories() {

		RepositoryService repositoryService = new RepositoryService();

		try {
			repositoryService.readAll();
		} catch (LoadingException e) {
			System.out.println("Failded to load on or more repositories");
			System.exit(0);
		}

		List<RepositoryDTO> repositoryDTOs = repositoryService
				.getRepositories();
		Collections.sort(repositoryDTOs);
		Iterator<RepositoryDTO> iter = repositoryDTOs.iterator();

		System.out.println("Number of repositories found: "
				+ repositoryDTOs.size());

		while (iter.hasNext()) {
			RepositoryDTO repositoryDTO = iter.next();
			String name = repositoryDTO.getName();
			String autoconfig = repositoryDTO.getAutoConfigURL();
			String path = repositoryDTO.getPath();
			String url = repositoryDTO.getFtpDTO().getUrl();
			String login = repositoryDTO.getFtpDTO().getLogin();
			String password = repositoryDTO.getFtpDTO().getPassword();
			String port = repositoryDTO.getFtpDTO().getPort();

			System.out.println("Repository name: " + name);
			System.out.println("Auto-config url: " + autoconfig);
			System.out.println("FTP folder path: " + path);
			System.out.println("Url: " + url);
			System.out.println("Login:" + login);
			System.out.println("Password: + password");
			System.out.println("Port: " + port);
			System.out.println("");
		}
		System.exit(0);
	}

	private static void buildRepository() {

		System.out.println("Build a new repository");

		Console c = System.console();
		if (c == null) {
			System.err.println("No console.");
			System.exit(1);
		}

		String name = c.readLine("Enter repository name: ");
		while (name.isEmpty()) {
			name = c.readLine("Enter repository name: ");
		}
		String url = c.readLine("Enter repository url: ");
		while (url.isEmpty()) {
			url = c.readLine("Enter repository url: ");
		}
		String login = c.readLine("Enter repository login: ");
		while (login.isEmpty()) {
			login = c.readLine("Enter repository login: ");
		}
		char[] password = c.readPassword("Enter repository password: ");
		while (password.length == 0) {
			password = c.readPassword("Enter repository password: ");
		}
		String port = c.readLine("Enter repository port: ");
		while (port.isEmpty()) {
			port = c.readLine("Enter repository port: ");
		}
		String path = c.readLine("Enter FTP shared folder path: ");
		while (path.isEmpty()) {
			port = c.readLine("Enter FTP shared folder path: ");
		}

		RepositoryService repositoryService = new RepositoryService();
		try {
			repositoryService.createRepository(name, url, port, login,
					password.toString(), EncryptionMode.NO_ENCRYPTION);
			repositoryService.write(name);
		} catch (CheckException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		} catch (WritingException e) {
			System.out.println("Failded to write repository.");
			System.out.println(e.getMessage());
			System.exit(0);
		}

		repositoryService.getRepositoryBuilderDAO().addObserverFileSize(
				new ObserverFileSize() {
					@Override
					public void update(long value) {
						System.out.println("Build complete: " + value + " %");
					}
				});

		try {
			repositoryService.buildRepository(name, path);
			System.out.println("Repository build finished.");
		} catch (Exception e) {
			System.out.println("Failed to build repository.");
			System.out.println(e.getMessage());
		} finally {
			System.exit(0);
		}
	}

	private static void checkRepository() {

		System.out.println("Check repository");

		Console c = System.console();
		if (c == null) {
			System.err.println("No console.");
			System.exit(1);
		}

		String name = c.readLine("Enter repository name: ");
		while (name.isEmpty()) {
			name = c.readLine("Enter repository name: ");
		}

		RepositoryService repositoryService = new RepositoryService();

		try {
			repositoryService.readAll();
			RepositoryDTO repositoryDTO = repositoryService.getRepository(name);
			System.out.println("Checking repository...");
			repositoryService.checkRepository(name, repositoryDTO.getPath());
		} catch (LoadingException e) {
			System.out.println("Failded to load on or more repositories");
			System.exit(0);
		} catch (RepositoryException e) {
			System.out.println(e.getMessage());
		} catch (RepositoryCheckException e) {
			System.out
					.println("Repository is out of synchronization and must be rebuilt.");
		} catch (ServerInfoNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (SyncFileNotFoundException e) {
			System.out.println(e.getMessage());
		} finally {
			System.exit(0);
		}
	}

	private static void updateRepository() {

		System.out.println("Update repository");

		Console c = System.console();
		if (c == null) {
			System.err.println("No console.");
			System.exit(1);
		}

		String name = c.readLine("Enter repository name: ");
		while (name.isEmpty()) {
			name = c.readLine("Enter repository name: ");
		}

		RepositoryService repositoryService = new RepositoryService();
		
		try {
			repositoryService.readAll();
		} catch (LoadingException e) {
			System.out.println("Failded to load on or more repositories");
			System.exit(0);
		}

		repositoryService.getRepositoryBuilderDAO().addObserverFileSize(
				new ObserverFileSize() {
					@Override
					public void update(long value) {
						System.out.println("Build complete: " + value + " %");
					}
				});

		try {
			repositoryService.buildRepository(name);
			System.out.println("Repository build finished.");
		} catch (Exception e) {
			System.out.println("Failed to build repository.");
			System.out.println(e.getMessage());
		} finally {
			System.exit(0);
		}
	}

	private static void displayHelp() {

		System.out.println("ArmA3Sync command lines:");
		System.out.println("-build : build a new repository");
		System.out.println("-check : check repository synchronization");
		System.out.println("-help : display command lines");
		System.out.println("-list : list repositories");
		System.out.println("-update : update a repository");
		System.out.println("-version : display version");
		System.exit(0);
	}

	private static void start(Facade facade) {

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
