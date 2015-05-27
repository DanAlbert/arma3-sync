package fr.soe.a3s.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import fr.soe.a3s.constant.ConsoleCommands;
import fr.soe.a3s.constant.Protocol;
import fr.soe.a3s.constant.TimeOutValues;
import fr.soe.a3s.controller.ObserverActiveConnnection;
import fr.soe.a3s.controller.ObserverEnd;
import fr.soe.a3s.controller.ObserverError;
import fr.soe.a3s.controller.ObserverFileSize;
import fr.soe.a3s.controller.ObserverFilesNumber;
import fr.soe.a3s.controller.ObserverFilesNumber3;
import fr.soe.a3s.controller.ObserverSpeed;
import fr.soe.a3s.dao.AbstractConnexionDAO;
import fr.soe.a3s.dao.FileAccessMethods;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.FtpException;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.RepositoryCheckException;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.exception.ServerInfoNotFoundException;
import fr.soe.a3s.exception.SyncFileNotFoundException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.main.Version;
import fr.soe.a3s.service.AbstractConnexionService;
import fr.soe.a3s.service.ConnexionServiceFactory;
import fr.soe.a3s.service.FtpService;
import fr.soe.a3s.service.RepositoryService;

public class Console {

	private boolean devMode = false;

	private double value;
	/** Sync variables */
	private long incrementedFilesSize;
	private long totalFilesSize;
	private final List<SyncTreeNodeDTO> listFilesToUpdate = new ArrayList<SyncTreeNodeDTO>();
	private final List<SyncTreeNodeDTO> listFilesToDelete = new ArrayList<SyncTreeNodeDTO>();

	public Console(boolean devMode) {
		this.devMode = devMode;
	}

	public void displayCommands() {

		System.out.println("ArmA3Sync console commands:");
		System.out.println(ConsoleCommands.NEW.toString()
				+ ": create a new repository");
		System.out.println(ConsoleCommands.BUILD.toString()
				+ ": build repository");
		System.out.println(ConsoleCommands.CHECK.toString()
				+ ": check repository synchronization");
		System.out.println(ConsoleCommands.DELETE.toString()
				+ ": delete repository");
		System.out.println(ConsoleCommands.LIST.toString()
				+ ": list repositories");
		System.out.println(ConsoleCommands.UPDATE.toString()
				+ ": check for updates");
		System.out.println(ConsoleCommands.SYNC.toString()
				+ ": synchronize content with a repository");
		System.out.println(ConsoleCommands.COMMANDS.toString()
				+ ": display commands");
		System.out.println(ConsoleCommands.VERSION.toString()
				+ ": display version");
		System.out.println(ConsoleCommands.QUIT.toString() + ": quit");
		System.out.println("");
	}

	public void execute() {

		Scanner c = new Scanner(System.in);
		System.out.print("Please enter a command = ");
		String command = c.nextLine().trim();

		if (command.equalsIgnoreCase(ConsoleCommands.VERSION.toString())) {
			displayVersion();
		} else if (command.equalsIgnoreCase(ConsoleCommands.LIST.toString())) {
			listRepositories();
		} else if (command.equalsIgnoreCase(ConsoleCommands.NEW.toString())) {
			newRepository();
		} else if (command.equalsIgnoreCase(ConsoleCommands.CHECK.toString())) {
			checkRepository();
		} else if (command.equalsIgnoreCase(ConsoleCommands.BUILD.toString())) {
			buildRepository();
		} else if (command.equalsIgnoreCase(ConsoleCommands.DELETE.toString())) {
			deleteRepository();
		} else if (command.equalsIgnoreCase(ConsoleCommands.UPDATE.toString())) {
			checkForUpdates();
		} else if (command.equalsIgnoreCase(ConsoleCommands.SYNC.toString())) {
			syncRepository();
		} else if (command
				.equalsIgnoreCase(ConsoleCommands.COMMANDS.toString())) {
			displayCommands();
			execute();
		} else if (command.equalsIgnoreCase(ConsoleCommands.QUIT.toString())) {
			quit();
		} else {
			System.out.println("ArmA3Sync - bad command.");
			System.out.print("");
			execute();
		}
	}

	private void listRepositories() {

		System.out.println("");
		RepositoryService repositoryService = new RepositoryService();

		try {
			repositoryService.readAll();
		} catch (LoadingException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}

		List<RepositoryDTO> repositoryDTOs = repositoryService
				.getRepositories();
		Collections.sort(repositoryDTOs);
		Iterator<RepositoryDTO> iter = repositoryDTOs.iterator();

		System.out.println("Listing repositories");
		System.out.println("Number of repositories found: "
				+ repositoryDTOs.size());

		while (iter.hasNext()) {
			RepositoryDTO repositoryDTO = iter.next();
			String name = repositoryDTO.getName();
			String autoconfig = repositoryDTO.getAutoConfigURL();
			String path = repositoryDTO.getPath();
			String url = repositoryDTO.getProtocoleDTO().getUrl();
			String login = repositoryDTO.getProtocoleDTO().getLogin();
			String password = repositoryDTO.getProtocoleDTO().getPassword();
			String port = repositoryDTO.getProtocoleDTO().getPort();
			Protocol protocole = repositoryDTO.getProtocoleDTO().getProtocole();

			if (name != null) {
				if (name.isEmpty()) {
					name = null;
				}
			}
			if (autoconfig != null) {
				if (autoconfig.isEmpty()) {
					autoconfig = null;
				}
			}
			if (path != null) {
				if (path.isEmpty()) {
					path = null;
				}
			}
			if (url != null) {
				if (url.isEmpty()) {
					url = null;
				}
			}
			if (login != null) {
				if (login.isEmpty()) {
					login = null;
				}
			}
			if (password != null) {
				if (password.isEmpty()) {
					password = null;
				}
			}

			System.out.println("Repository name: " + name);
			System.out.println("Protocole: " + protocole.getDescription());
			System.out.println("Url: " + url);
			System.out.println("Port: " + port);
			System.out.println("Login: " + login);
			System.out.println("Password: " + password);
			if (autoconfig == null) {
				System.out.println("Auto-config url: " + autoconfig);
			} else {
				System.out.println("Auto-config url: " + protocole.getPrompt()
						+ autoconfig);
			}
			System.out.println("Repository main folder path: " + path);
			System.out.println("");
		}

		if (repositoryDTOs.size() == 0) {
			System.out.println("");
		}
		execute();
	}

	private void newRepository() {

		System.out.println("");
		System.out.println("Create a new repository");

		Scanner c = new Scanner(System.in);

		System.out.print("Enter repository name: ");
		String name = c.nextLine();
		while (name.isEmpty()) {
			System.out.print("Enter repository name: ");
			name = c.nextLine();
		}

		System.out.print("Enter repository protocole FTP or HTTP: ");
		String prot = c.nextLine().toUpperCase();
		boolean check = true;
		while (check) {
			if (prot.equals(Protocol.FTP.getDescription())) {
				check = false;
			} else if (prot.equals(Protocol.HTTP.getDescription())) {
				check = false;
			} else {
				System.out.print("Enter repository protocole FTP or HTTP: ");
				prot = c.nextLine().toUpperCase();
			}
		}

		boolean portIsWrong = false;
		String port = "";
		do {
			try {
				System.out
						.print("Enter repository port (21 default FTP, 80 default HTTP): ");
				port = c.nextLine();
				int p = Integer.parseInt(port);
				portIsWrong = false;
			} catch (NumberFormatException e) {
				portIsWrong = true;
			}
		} while (portIsWrong);

		System.out
				.print("Enter user login (enter anonymous for public access): ");
		String login = c.nextLine();
		while (login.isEmpty()) {
			System.out
					.print("Enter user login (enter anonymous for public access): ");
			login = c.nextLine();
		}

		System.out.print("Enter user password (leave blank if no password): ");
		String password = c.nextLine();

		System.out.print("Enter repository url: ");
		String url = c.nextLine();
		url = url.toLowerCase();
		url = url.replaceAll(Protocol.FTP.getPrompt(), "").replaceAll(
				Protocol.HTTP.getPrompt(), "");
		while (url.isEmpty()) {
			System.out.print("Enter repository url: ");
			url = c.nextLine();
		}

		// Folder location
		System.out
				.print("Enter root shared folder path (leave blank to pass): ");
		String path = c.nextLine();
		if (!path.isEmpty()) {
			while (!new File(path).exists()) {
				System.out.println("Target folder does not exists!");
				System.out.print("Enter root shared folder path: ");
				path = c.nextLine();
			}
			while (!new File(path).isDirectory()) {
				System.out.println("Target folder does not exists!");
				System.out.print("Enter root shared folder path: ");
				path = c.nextLine();
			}
		}

		// Connection Timeout
		boolean connectionTimeOutIsWrong = false;
		String connectionTimeOut = "";
		do {
			System.out
					.print("Enter connection timeout in milliseconds (default 60000, >0): ");
			connectionTimeOut = c.nextLine();
			if (connectionTimeOut.isEmpty()) {
				connectionTimeOut = Integer
						.toString(TimeOutValues.CONNECTION_TIME_OUT.getValue());
			} else {
				try {
					int time = Integer.parseInt(connectionTimeOut);
					connectionTimeOutIsWrong = false;
					if (time == 0) {
						connectionTimeOutIsWrong = true;
					}
				} catch (NumberFormatException e) {
					connectionTimeOutIsWrong = true;
				}
			}
		} while (connectionTimeOutIsWrong);

		// Read time out
		boolean readTimeOutIsWrong = false;
		String readTimeOut = "";
		do {
			System.out
					.print("Enter read timeout in milliseconds (default 60000, >0): ");
			readTimeOut = c.nextLine();
			if (readTimeOut.isEmpty()) {
				readTimeOut = Integer.toString(TimeOutValues.READ_TIME_OUT
						.getValue());
			} else {
				try {
					int time = Integer.parseInt(readTimeOut);
					readTimeOutIsWrong = false;
					if (time == 0) {
						readTimeOutIsWrong = true;
					}
				} catch (NumberFormatException e) {
					readTimeOutIsWrong = true;
				}
			}
		} while (readTimeOutIsWrong);

		Protocol protocole = Protocol.getEnum(prot);
		RepositoryService repositoryService = new RepositoryService();
		try {
			repositoryService.createRepository(name, url, port, login,
					password, protocole, connectionTimeOut, readTimeOut);
			if (!path.isEmpty()) {
				path = (new File(path)).getAbsolutePath().toLowerCase();// normalize
				// path
				repositoryService.setRepositoryPath(name, path);
			}
			repositoryService.write(name);
			System.out
					.println("Repository creation finished.\nYou can now run the BUILD command to construct the repository");
		} catch (CheckException e) {
			System.out.println(e.getMessage());
			return;
		} catch (WritingException e) {
			System.out.println("Failed to write repository.");
			System.out.println(e.getMessage());
			return;
		} catch (RepositoryException e) {
			System.out.println(e.getMessage());
			return;
		} finally {
			System.out.println("");
			execute();
		}
	}

	private void checkRepository() {

		System.out.println("");
		System.out.println("Check repository.");

		Scanner c = new Scanner(System.in);

		System.out.print("Enter repository name: ");
		String name = c.nextLine();
		while (name.isEmpty()) {
			System.out.print("Enter repository name: ");
			name = c.nextLine();
		}

		RepositoryService repositoryService = new RepositoryService();

		try {
			repositoryService.readAll();
			RepositoryDTO repositoryDTO = repositoryService.getRepository(name);
			System.out.println("Checking repository...");
			repositoryService.checkRepository(name, repositoryDTO.getPath());
			System.out.println("Repository is synchronized.");
		} catch (LoadingException e) {
			System.out.println(e.getMessage());
		} catch (RepositoryException e) {
			System.out.println(e.getMessage());
		} catch (RepositoryCheckException e) {
			List<String> messages = new ArrayList<String>();
			StringTokenizer stk = new StringTokenizer(e.getMessage(), "*");
			while (stk.hasMoreTokens()) {
				messages.add(stk.nextToken());
			}
			String message = "Repository is out of synchronization.";
			if (messages.size() > 5) {
				for (int i = 0; i < 5; i++) {
					String m = messages.get(i);
					message = message + "\n" + " - " + m;
				}
				message = message + "\n" + "["
						+ Integer.toString(messages.size() - 5) + "] more...";
			} else {
				for (String m : messages) {
					message = message + "\n" + " - " + m;
				}
			}
			System.out.println(message);
		} catch (ServerInfoNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (SyncFileNotFoundException e) {
			System.out.println(e.getMessage());
		} finally {
			System.gc();
			System.out.println("");
			execute();
		}
	}

	private void buildRepository() {

		System.out.println("");
		System.out.println("Build repository.");

		Scanner c = new Scanner(System.in);

		System.out.print("Enter repository name: ");
		String name = c.nextLine();
		while (name.isEmpty()) {
			System.out.print("Enter repository name: ");
			name = c.nextLine();
		}

		RepositoryService repositoryService = new RepositoryService();

		try {
			repositoryService.readAll();
		} catch (LoadingException e) {
			System.out.println(e.getMessage());
		}

		// Folder location (if null)
		try {
			RepositoryDTO repositoryDTO = repositoryService.getRepository(name);
			if (repositoryDTO.getPath() == null) {
				System.out.print("Enter root shared folder path: ");
				String path = c.nextLine();
				while (path.isEmpty()) {
					System.out.print("Enter root shared folder path: ");
					path = c.nextLine();
				}
				while (!new File(path).exists()) {
					System.out.println("Target folder does not exists!");
					System.out.print("Enter root shared folder path: ");
					path = c.nextLine();
				}
				while (!new File(path).isDirectory()) {
					System.out.println("Target folder does not exists!");
					System.out.print("Enter root shared folder path: ");
					path = c.nextLine();
				}
				path = (new File(path)).getAbsolutePath().toLowerCase();// normalize
				// path
				repositoryService.setRepositoryPath(name, path);
			}
		} catch (RepositoryException e1) {
			System.out.println(e1.getMessage());
			System.out.println("");
			execute();
		}

		// Number of client connections
		boolean numberOfConnectionsIsWrong = false;
		String numberOfConnections = "";
		int n = 0;
		do {
			try {
				System.out
						.print("Set maximum number of client connections (1-10): ");
				numberOfConnections = c.nextLine();
				n = Integer.parseInt(numberOfConnections);
				if (!(n >= 1 && n <= 10)) {
					numberOfConnectionsIsWrong = true;
				} else {
					numberOfConnectionsIsWrong = false;
				}
			} catch (NumberFormatException e) {
				numberOfConnectionsIsWrong = true;
			}
		} while (numberOfConnectionsIsWrong);

		repositoryService.setNumberOfConnections(name, n);

		// Set excluded files from build, excluded folder content from Sync
		repositoryService.clearExcludedFilesPathFromBuild(name);
		repositoryService.clearExcludedFoldersFromSync(name);

		System.out
				.print("Add file path to exclude from build (leave blank to pass): ");
		String excludedFilePath = c.nextLine();
		while (!excludedFilePath.isEmpty()) {
			if (!(new File(excludedFilePath)).exists()) {
				System.out.println("Wrong path, file does not exists.");
			} else {
				String path = new File(excludedFilePath).getAbsolutePath();
				repositoryService.addExcludedFilesPathFromBuild(name, path);
			}
			System.out
					.print("Add file path to exclude from build (leave blank to pass): ");
			excludedFilePath = c.nextLine();
		}

		System.out
				.print("Add folder path to exclude extra local content when sync (leave blank to pass): ");
		String excludedFoldersFromSync = c.nextLine();
		while (!excludedFoldersFromSync.isEmpty()) {
			if (!(new File(excludedFoldersFromSync)).exists()) {
				System.out.println("Wrong path, file does not exists.");
			} else {
				String path = new File(excludedFoldersFromSync)
						.getAbsolutePath();
				repositoryService.addExcludedFoldersFromSync(name, path);
			}
			System.out
					.print("Add folder path to exclude extra local content when sync (leave blank to pass): ");
			excludedFoldersFromSync = c.nextLine();
		}

		this.value = 0;

		repositoryService.getRepositoryBuilderDAO().addObserverFilesNumber3(
				new ObserverFilesNumber3() {
					@Override
					public void update(int v) {
						if (v > value) {
							value = v;
							System.out.println("Build complete: " + value
									+ " %");
						}
					}
				});

		try {
			repositoryService.buildRepository(name);
			repositoryService.write(name);
			System.out.println("Repository build finished.");
		} catch (Exception e) {
			System.out.println("Failed to build repository.");
			System.out.println(e.getMessage());
		} finally {
			System.gc();
			System.out.println("");
			execute();
		}
	}

	private void deleteRepository() {

		System.out.println("");
		System.out.println("Delete repository.");

		Scanner c = new Scanner(System.in);

		System.out.print("Enter repository name: ");
		String name = c.nextLine();
		while (name.isEmpty()) {
			System.out.print("Enter repository name: ");
			name = c.nextLine();
		}

		RepositoryService repositoryService = new RepositoryService();

		try {
			repositoryService.readAll();
			boolean remove = repositoryService.removeRepository(name.trim());
			if (remove) {
				System.out.println("Repository " + name + " removed.");
			} else {
				System.out.println("Failded to remove repository.");
			}
		} catch (LoadingException e) {
			System.out.println(e.getMessage());
		} finally {
			System.out.println("");
			execute();
		}
	}

	private void syncRepository() {

		System.out.println("");
		System.out.println("Synchronize with repository");

		Scanner c = new Scanner(System.in);

		System.out.print("Enter repository name: ");
		String repositoryName = c.nextLine();
		while (repositoryName.isEmpty()) {
			System.out.print("Enter repository name: ");
			repositoryName = c.nextLine();
		}

		System.out.print("Enter destination folder path: ");
		String destinationFolderPath = c.nextLine();
		while (destinationFolderPath.isEmpty()) {
			System.out.print("Enter destination folder path: ");
			destinationFolderPath = c.nextLine();
		}
		while (!new File(destinationFolderPath).exists()) {
			System.out.println("Destination folder does not exists!");
			System.out.print("Enter destination folder path: ");
			destinationFolderPath = c.nextLine();
		}

		System.out
				.print("Perform Exact file matching (yes/no, choosing yes will erase all extra files into the target folder): ");
		String withExactMatch = c.nextLine();
		while (withExactMatch.isEmpty()) {
			System.out.print("Perform Exact file matching (yes/no): ");
			withExactMatch = c.nextLine();
		}
		while (!(withExactMatch.equalsIgnoreCase("yes") || withExactMatch
				.equalsIgnoreCase("no"))) {
			System.out.print("Perform Exact file matching (yes/no): ");
			withExactMatch = c.nextLine();
		}

		boolean exactMath = false;
		if (withExactMatch.equalsIgnoreCase("yes")) {
			exactMath = true;
			System.out
					.print("Performing synchronization with exact files matching.");
		}

		RepositoryService repositoryService = new RepositoryService();
		try {
			repositoryService.readAll();
			repositoryService.setExactMatch(exactMath, repositoryName);

			repositoryService.setDefaultDownloadLocation(repositoryName,
					destinationFolderPath);

			/* Check for addons */
			checkForAddons(repositoryName);

			/* Sync */
			syncRepository(repositoryName, false);

		} catch (Exception e) {
			List<Exception> errors = new ArrayList<Exception>();
			errors.add(e);
			finishWithErrors(errors, repositoryName, false);
		}
	}

	public void checkForUpdates() {

		System.out.println("");
		System.out.println("Check for updates.");

		FtpService ftpService = new FtpService();
		String availableVersion = null;
		try {
			availableVersion = ftpService.checkForUpdate(devMode);
		} catch (FtpException e) {
			System.out.println(e.getMessage());
			return;
		}

		if (availableVersion != null) {
			// Proceed update
			String command = "java -jar -Djava.net.preferIPv4Stack=true ArmA3Sync-Updater.jar";
			if (devMode) {
				command = command + " -dev -console";
			} else {
				command = command + " -console";
			}
			try {
				String line = "";
				Process p = Runtime.getRuntime().exec(command);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				while ((line = in.readLine()) != null) {
					System.out.println(line);
				}
				in.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				System.exit(0);
			}
		} else {
			System.out.println("No new update available.");
			System.exit(0);
		}
	}

	private void displayVersion() {

		System.out.println("");
		System.out.println("ArmA3Sync version " + Version.getName());
		System.out.println("Build " + Version.getVersion() + " ("
				+ Version.getYear() + ")");
		System.out.println("");
		execute();
	}

	private void quit() {

		System.out.println("");
		System.out.println("ArmA3Sync exited.");
		System.exit(0);
	}

	public void build(String repositoryName) {

		RepositoryService repositoryService = new RepositoryService();

		try {
			repositoryService.readAll();
		} catch (LoadingException e) {
			System.out.println(e.getMessage());
		}

		this.value = 0;

		repositoryService.getRepositoryBuilderDAO().addObserverFilesNumber3(
				new ObserverFilesNumber3() {
					@Override
					public void update(int v) {
						if (v > value) {
							value = v;
							System.out.println("Build complete: " + value
									+ " %");
						}
					}
				});

		try {
			System.out.println("Repository build starting.");
			repositoryService.buildRepository(repositoryName);
			System.out.println("Repository build finished.");
		} catch (Exception e) {
			System.out.println("Failed to build repository.");
			System.out.println(e.getMessage());
		} finally {
			System.exit(0);
		}
	}

	public void checkRepository(String repositoryName) {

		System.out.println("");
		System.out.println("Check repository.");

		RepositoryService repositoryService = new RepositoryService();

		try {
			repositoryService.readAll();
			RepositoryDTO repositoryDTO = repositoryService
					.getRepository(repositoryName);
			System.out.println("Checking repository...");
			repositoryService.checkRepository(repositoryName,
					repositoryDTO.getPath());
			System.out.println("Repository is synchronized.");
		} catch (LoadingException e) {
			System.out.println(e.getMessage());
		} catch (RepositoryException e) {
			System.out.println(e.getMessage());
		} catch (RepositoryCheckException e) {
			List<String> messages = new ArrayList<String>();
			StringTokenizer stk = new StringTokenizer(e.getMessage(), "*");
			while (stk.hasMoreTokens()) {
				messages.add(stk.nextToken());
			}
			String message = "Repository is out of synchronization.";
			if (messages.size() > 5) {
				for (int i = 0; i < 5; i++) {
					String m = messages.get(i);
					message = message + "\n" + " - " + m;
				}
				message = message + "\n" + "["
						+ Integer.toString(messages.size() - 5) + "] more...";
			} else {
				for (String m : messages) {
					message = message + "\n" + " - " + m;
				}
			}
			System.out.println(message);
		} catch (ServerInfoNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (SyncFileNotFoundException e) {
			System.out.println(e.getMessage());
		} finally {
			System.exit(0);
		}
	}

	public void syncRepository(final String repositoryName,
			String destinationFolderPath, String withExactMath) {

		assert (repositoryName != null);
		assert (destinationFolderPath != null);

		System.out.println("");
		System.out.println("Synchronize with repository");

		RepositoryService repositoryService = new RepositoryService();

		try {
			repositoryService.readAll();

			System.out.println("Repository Name = " + repositoryName);
			System.out.println("Destination folder path = "
					+ destinationFolderPath);

			if (!new File(destinationFolderPath).exists()) {
				String message = "Error: destination folder path does not exist!";
				throw new FileNotFoundException(message);
			}

			repositoryService.setDefaultDownloadLocation(repositoryName,
					destinationFolderPath);

			if (!(withExactMath.equalsIgnoreCase("true") || withExactMath
					.equalsIgnoreCase("false"))) {
				String message = "Unrecognized exact math parameter (true/false).";
				throw new Exception(message);
			}

			repositoryService.setExactMatch(
					Boolean.parseBoolean(withExactMath), repositoryName);

			/* Check for addons */
			checkForAddons(repositoryName);

			/* Sync */
			syncRepository(repositoryName, true);

		} catch (Exception e) {
			List<Exception> errors = new ArrayList<Exception>();
			errors.add(e);
			finishWithErrors(errors, repositoryName, true);
		}
	}

	private void syncRepository(final String repositoryName, final boolean exit)
			throws Exception {

		System.out.println("Downloading...");

		if (listFilesToUpdate.isEmpty()) {
			System.out.println("No file to download.");
			finish(repositoryName, exit);
		} else {
			incrementedFilesSize = 0;
			totalFilesSize = 0;
			for (SyncTreeNodeDTO node : listFilesToUpdate) {
				if (node.isLeaf()) {
					SyncTreeLeafDTO leafDTO = (SyncTreeLeafDTO) node;
					totalFilesSize = totalFilesSize + leafDTO.getSize();
				}
			}

			AbstractConnexionService connexionService = ConnexionServiceFactory
					.getServiceFromRepositoryMultiConnections(repositoryName);

			value = 0;

			for (AbstractConnexionDAO connect : connexionService
					.getConnexionDAOs()) {

				connect.addObserverFilesNumber(new ObserverFilesNumber() {
					@Override
					public synchronized void update(SyncTreeNodeDTO node) {
						if (node.isLeaf() && totalFilesSize > 0) {
							SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
							long size = leaf.getSize();
							incrementedFilesSize = incrementedFilesSize + size;
							int v = (int) ((incrementedFilesSize * 100) / totalFilesSize);
							if (v > value) {
								value = v;
								System.out.println("Download complete: " + v
										+ " %");
							}
						}
					}
				});

				connect.addObserverFileSize(new ObserverFileSize() {
					@Override
					public void update(long value, SyncTreeNodeDTO node) {
					}
				});

				connect.addObserverSpeed(new ObserverSpeed() {
					@Override
					public synchronized void update() {
					}
				});

				connect.addObserverActiveConnection(new ObserverActiveConnnection() {
					@Override
					public synchronized void update() {
					}
				});

				connect.addObserverEnd(new ObserverEnd() {
					@Override
					public void end() {
						finish(repositoryName, exit);
					}
				});

				connect.addObserverError(new ObserverError() {
					@Override
					public void error(List<Exception> errors) {
						finishWithErrors(errors, repositoryName, exit);
					}
				});
			}
			connexionService.downloadAddons(repositoryName, listFilesToUpdate);
		}
	}

	private void finish(final String repositoryName, final boolean exit) {

		System.out.println("Deleting extra files...");
		deleteExtraFiles();
		System.out.println("Synchronization is finished.");
		try {
			checkForAddons(repositoryName);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Synchronization done.");
		if (exit) {
			System.exit(0);
		} else {
			System.gc();
			System.out.println("");
			execute();
		}
	}

	private void finishWithErrors(List<Exception> errors,
			final String repositoryName, final boolean exit) {

		System.out.println("Deleting extra files...");
		deleteExtraFiles();

		List<String> messages = new ArrayList<String>();
		List<String> causes = new ArrayList<String>();

		String message = "Download finished with errors.";

		for (Exception e : errors) {
			if (!messages.contains(e.getMessage())) {
				if (e instanceof FileNotFoundException) {
					messages.add(e.getMessage());
				} else if (e.getCause() != null) {
					if (!causes.contains(e.getCause().toString())) {
						causes.add(e.getCause().toString());
						messages.add(e.getMessage());
					}
				} else if (e.getMessage() != null) {
					messages.add(e.getMessage());
				}
			}
		}
		for (String m : messages) {
			message = message + "\n" + " - " + m;
		}

		System.out.println(message);

		if (exit) {
			System.exit(0);
		} else {
			System.gc();
			System.out.println("");
			execute();
		}
	}

	private void checkForAddons(String repositoryName) throws Exception {

		System.out.println("Checking for addons...");

		value = 0;

		RepositoryService repositoryService = new RepositoryService();
		repositoryService.getRepositoryCheckerDAO().addObserverFilesNumber3(
				new ObserverFilesNumber3() {
					@Override
					public synchronized void update(int v) {
						if (v > value) {
							value = v;
							System.out.println("Check for Addons complete: "
									+ value + " %");
						}
					}
				});

		AbstractConnexionService connexionService = ConnexionServiceFactory
				.getServiceFromRepository(repositoryName);
		connexionService.getSync(repositoryName);
		connexionService.getServerInfo(repositoryName);
		connexionService.getChangelogs(repositoryName);

		SyncTreeDirectoryDTO racine = repositoryService
				.getSyncForCheckForAddons(repositoryName);

		listFilesToUpdate.clear();
		listFilesToDelete.clear();

		// Get files list
		for (SyncTreeNodeDTO node : racine.getList()) {
			getFiles(node);
		}

		System.out.println("Number of files to update = "
				+ listFilesToUpdate.size());
		System.out.println("Number of files to delete = "
				+ listFilesToDelete.size());
		System.out.println("Checking for addons is finished.");
	}

	private void deleteExtraFiles() {

		for (SyncTreeNodeDTO node : listFilesToDelete) {
			String path = node.getDestinationPath() + "/" + node.getName();
			if (path != null) {
				File file = new File(path);
				if (file.isFile()) {
					FileAccessMethods.deleteFile(file);
				} else if (file.isDirectory()) {
					FileAccessMethods.deleteDirectory(file);
				}
			}
		}
	}

	private void getFiles(SyncTreeNodeDTO node) {

		if (!node.isLeaf()) {
			SyncTreeDirectoryDTO syncTreeDirectoryDTO = (SyncTreeDirectoryDTO) node;
			if (syncTreeDirectoryDTO.isUpdated()) {
				listFilesToUpdate.add(syncTreeDirectoryDTO);
			} else if (syncTreeDirectoryDTO.isDeleted()) {
				int count = 0;
				for (SyncTreeNodeDTO n : syncTreeDirectoryDTO.getList()) {
					if (n.isDeleted()) {
						count++;
					}
				}
				if (count == syncTreeDirectoryDTO.getList().size()) {
					listFilesToDelete.add(syncTreeDirectoryDTO);
				}
			}
			for (SyncTreeNodeDTO n : syncTreeDirectoryDTO.getList()) {
				getFiles(n);
			}
		} else {
			SyncTreeLeafDTO syncTreeLeafDTO = (SyncTreeLeafDTO) node;
			if (syncTreeLeafDTO.isUpdated()) {
				listFilesToUpdate.add(syncTreeLeafDTO);
			} else if (syncTreeLeafDTO.isDeleted()) {
				SyncTreeDirectoryDTO parent = syncTreeLeafDTO.getParent();
				if (parent.getName().equals("racine")) {
					listFilesToDelete.add(syncTreeLeafDTO);
				} else {
					int count = 0;
					for (SyncTreeNodeDTO n : parent.getList()) {
						if (n.isDeleted()) {
							count++;
						}
					}
					if (count == parent.getList().size()) {
						listFilesToDelete.add(parent);
					} else {
						listFilesToDelete.add(syncTreeLeafDTO);
					}
				}
			}
		}
	}
}
