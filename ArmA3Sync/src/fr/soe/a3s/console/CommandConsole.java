package fr.soe.a3s.console;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import fr.soe.a3s.constant.ConsoleCommands;
import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.constant.TimeOutValues;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.main.Version;
import fr.soe.a3s.service.RepositoryService;

public class CommandConsole extends CommandGeneral {

	private boolean devMode = false;

	public CommandConsole(boolean devMode) {
		this.devMode = devMode;
	}

	public void displayCommands() {

		System.out.println("");
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
		System.out.println(ConsoleCommands.SYNC.toString()
				+ ": synchronize content with a repository");
		System.out
				.println(ConsoleCommands.EXTRACT.toString()
						+ ": extract *.bikey files from source directory to target directory");
		System.out.println(ConsoleCommands.UPDATE.toString()
				+ ": check for updates");
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
			list();
		} else if (command.equalsIgnoreCase(ConsoleCommands.NEW.toString())) {
			create();
		} else if (command.equalsIgnoreCase(ConsoleCommands.CHECK.toString())) {
			check();
		} else if (command.equalsIgnoreCase(ConsoleCommands.BUILD.toString())) {
			build();
		} else if (command.equalsIgnoreCase(ConsoleCommands.DELETE.toString())) {
			delete();
		} else if (command.equalsIgnoreCase(ConsoleCommands.UPDATE.toString())) {
			checkForUpdates();
		} else if (command.equalsIgnoreCase(ConsoleCommands.SYNC.toString())) {
			sync();
		} else if (command.equalsIgnoreCase(ConsoleCommands.EXTRACT
				.toString())) {
			extractBikeys();
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

	private void list() {

		System.out.println("");
		System.out.println("List repositories");

		RepositoryService repositoryService = new RepositoryService();
		try {
			repositoryService.readAll();
		} catch (LoadingException e) {
			System.out.println(e.getMessage());
			System.out.println("");
			execute();
			return;
		}

		List<RepositoryDTO> repositoryDTOs = repositoryService
				.getRepositories();
		Collections.sort(repositoryDTOs);
		Iterator<RepositoryDTO> iter = repositoryDTOs.iterator();

		System.out.println("Number of repositories found: "
				+ repositoryDTOs.size());

		System.out.println("");

		while (iter.hasNext()) {
			RepositoryDTO repositoryDTO = iter.next();
			String name = repositoryDTO.getName();
			String autoconfig = repositoryDTO.getAutoConfigURL();
			String path = repositoryDTO.getPath();
			String url = repositoryDTO.getProtocoleDTO().getUrl();
			String login = repositoryDTO.getProtocoleDTO().getLogin();
			String password = repositoryDTO.getProtocoleDTO().getPassword();
			String port = repositoryDTO.getProtocoleDTO().getPort();
			ProtocolType protocole = repositoryDTO.getProtocoleDTO()
					.getProtocolType();

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
		execute();
	}

	private void create() {

		System.out.println("");
		System.out.println("Create a new repository");

		Scanner c = new Scanner(System.in);

		// Set Name
		String name = "";
		do {
			System.out.print("Enter repository name: ");
			name = c.nextLine();

			List<String> forbiddenCharactersList = new ArrayList<String>();
			forbiddenCharactersList.add("/");
			forbiddenCharactersList.add("\\");
			forbiddenCharactersList.add("*");
			forbiddenCharactersList.add("?");
			forbiddenCharactersList.add("\"");
			forbiddenCharactersList.add("<");
			forbiddenCharactersList.add(">");
			forbiddenCharactersList.add("|");
			String forbiddenCharactersLine = "";
			for (String stg : forbiddenCharactersList) {
				forbiddenCharactersLine = forbiddenCharactersLine + " " + stg;
			}

			for (String stg : forbiddenCharactersList) {
				if (name.contains(stg)) {
					System.out
							.println("Repository name must not contains special characters like:"
									+ forbiddenCharactersLine);
					name = "";
					break;
				}
			}
		} while (name.isEmpty());

		// Set Protocol
		String protocol = "";
		boolean protocolIsWrong = true;
		do {
			System.out.print("Enter repository protocol FTP, HTTP or HTTPS: ");
			String prot = c.nextLine().toUpperCase();
			if (prot.equals(ProtocolType.FTP.getDescription())) {
				protocol = ProtocolType.FTP.getDescription();
				protocolIsWrong = false;
			} else if (prot.equals(ProtocolType.HTTP.getDescription())) {
				protocol = ProtocolType.HTTP.getDescription();
				protocolIsWrong = false;
			} else if (prot.equals(ProtocolType.HTTPS.getDescription())) {
				protocol = ProtocolType.HTTPS.getDescription();
				protocolIsWrong = false;
			} else {
				protocolIsWrong = true;
			}
		} while (protocolIsWrong);

		// Set Port
		String port = "";
		boolean portIsWrong = false;
		do {
			System.out.print("Enter repository port ("
					+ ProtocolType.FTP.getDefaultPort() + " " + "default FTP, "
					+ ProtocolType.HTTP.getDefaultPort() + " "
					+ "default HTTP, " + ProtocolType.HTTPS.getDefaultPort()
					+ " " + "default HTTPS): ");
			port = c.nextLine();
			if (port.isEmpty()) {
				if (protocol.equals(ProtocolType.FTP.getDescription())) {
					port = ProtocolType.FTP.getDefaultPort();
					protocolIsWrong = false;
				} else if (protocol.equals(ProtocolType.HTTP.getDescription())) {
					port = ProtocolType.HTTP.getDefaultPort();
					protocolIsWrong = false;
				} else if (protocol.equals(ProtocolType.HTTPS.getDescription())) {
					port = ProtocolType.HTTPS.getDefaultPort();
					protocolIsWrong = false;
				} else {
					portIsWrong = true;
				}
			} else {
				try {
					int p = Integer.parseInt(port);
					portIsWrong = false;
				} catch (NumberFormatException e) {
					portIsWrong = true;
				}
			}
		} while (portIsWrong);

		// Set Login
		String login = "";
		do {
			System.out.print("Enter user login (enter " + "'anonymous'"
					+ " for public access): ");
			login = c.nextLine();
		} while (login.isEmpty());

		// Set Password
		System.out.print("Enter user password (leave blank if no password): ");
		String password = c.nextLine();

		// Set Repository Url
		String url = "";
		do {
			System.out.print("Enter repository url: ");
			url = c.nextLine();

			if (!url.isEmpty()) {
				// Remove prompt
				String test = url.toLowerCase()
						.replaceAll(ProtocolType.FTP.getPrompt(), "")
						.replaceAll(ProtocolType.HTTP.getPrompt(), "")
						.replaceAll(ProtocolType.HTTPS.getPrompt(), "");
				if (url.length() > test.length()) {
					int index = url.length() - test.length();
					url = url.substring(index);
				}
			}
		} while (url.isEmpty());

		// Set Main Folder Location
		String path = "";
		boolean folderLocationIsWrong = true;
		do {
			System.out.print("Enter main folder location (leave blank to pass): ");
			path = c.nextLine();
			if (path.isEmpty()) {
				folderLocationIsWrong = false;
			} else if (!new File(path).exists()
					|| !new File(path).isDirectory()) {
				System.out.println("Target folder does not exists!");
				folderLocationIsWrong = true;
			} else {
				folderLocationIsWrong = false;
			}
		} while (folderLocationIsWrong);

		/* Proceed with command */

		ProtocolType protocole = ProtocolType.getEnum(protocol);
		assert (protocole != null);
		RepositoryService repositoryService = new RepositoryService();
		try {
			repositoryService.createRepository(name, url, port, login,
					password, protocole);
			repositoryService.setConnectionTimeout(name,"0");
			repositoryService.setReadTimeout(name,"0");
			if (!path.isEmpty()) {
				repositoryService.setRepositoryPath(name, path);
			}
			repositoryService.write(name);
			System.out
					.println("Repository creation finished.\nYou can now run the BUILD command to construct the repository");
		} catch (CheckException e) {
			System.out.println(e.getMessage());
		} catch (WritingException e) {
			System.out.println(e.getMessage());
		} catch (RepositoryException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("An unexpeted error has occured.");
			e.printStackTrace();
		} finally {
			System.out.println("");
			execute();
		}
	}

	private void build() {

		System.out.println("");
		System.out.println("Build repository");

		Scanner c = new Scanner(System.in);

		System.out.print("Enter repository name: ");
		String name = c.nextLine();
		while (name.isEmpty()) {
			System.out.print("Enter repository name: ");
			name = c.nextLine();
		}

		/* Load Repositories */

		RepositoryService repositoryService = new RepositoryService();
		try {
			repositoryService.readAll();
		} catch (LoadingException e) {
			System.out.println(e.getMessage());
			System.out.println("");
			execute();
			return;
		}

		// Set Folder Location (if null)
		try {
			RepositoryDTO repositoryDTO = repositoryService.getRepository(name);
			if (repositoryDTO.getPath() == null) {
				String path = "";
				boolean folderLocationIsWrong = true;
				do {
					System.out
							.print("Enter repository main folder location: ");
					path = c.nextLine();
					if (path.isEmpty()) {
						folderLocationIsWrong = true;
					} else if (!new File(path).exists()
							|| !new File(path).isDirectory()) {
						System.out.println("Target folder does not exists!");
						folderLocationIsWrong = true;
					} else {
						folderLocationIsWrong = false;
					}
				} while (folderLocationIsWrong);
				repositoryService.setRepositoryPath(name, path);
			}
		} catch (RepositoryException e) {
			System.out.println(e.getMessage());
			System.out.println("");
			execute();
			return;
		}

		// Set Number of client connections
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

		// Set Add compressed pbo files
		boolean addCompressedPboIsWrong = false;
		boolean addCompressedPbo = false;
		do {
			System.out.print("Add compressed pbo files (yes/no): ");
			String line = c.nextLine();
			if (line.equalsIgnoreCase("YES")) {
				addCompressedPbo = true;
				addCompressedPboIsWrong = false;
			} else if (line.equalsIgnoreCase("NO")) {
				addCompressedPbo = false;
				addCompressedPboIsWrong = false;
			} else {
				addCompressedPboIsWrong = true;
			}
		} while (addCompressedPboIsWrong);

		repositoryService.setCompressed(name, addCompressedPbo);

		try {
			// Set partial file transfer for HTTP
			RepositoryDTO repositoryDTO = repositoryService.getRepository(name);
			if (repositoryDTO.getProtocoleDTO().getProtocolType()
					.equals(ProtocolType.FTP)) {
				repositoryService.setUsePartialFileTransfer(name, true);
			} else {
				boolean partialFileTransferIsWrong = false;
				boolean partialFileTransfer = false;
				do {
					System.out
							.print("Use HTTP partial file transfer (yes/no): ");
					String line = c.nextLine();
					if (line.equalsIgnoreCase("YES")) {
						partialFileTransfer = true;
						partialFileTransferIsWrong = false;
					} else if (line.equalsIgnoreCase("NO")) {
						partialFileTransfer = false;
						partialFileTransferIsWrong = false;
					} else {
						partialFileTransferIsWrong = true;
					}
				} while (partialFileTransferIsWrong);

				repositoryService.setUsePartialFileTransfer(name,
						partialFileTransfer);
			}
		} catch (RepositoryException e) {
			System.out.println(e.getMessage());
			System.out.println("");
			execute();
			return;
		}

		// Set excluded files from build
		repositoryService.clearExcludedFilesPathFromBuild(name);
		String excludedFilePath = "";
		boolean excludedFilePathIsWrong = true;
		do {
			System.out
					.print("Add file path to exclude from build (leave blank to pass): ");
			excludedFilePath = c.nextLine();
			if (excludedFilePath.isEmpty()) {
				excludedFilePathIsWrong = false;
			} else if (!(new File(excludedFilePath)).exists()) {
				System.out.println("Wrong path, file does not exists.");
				excludedFilePathIsWrong = true;
			} else {
				repositoryService.addExcludedFilesPathFromBuild(name,
						excludedFilePath);
				excludedFilePathIsWrong = true;
			}
		} while (excludedFilePathIsWrong);

		// Set excluded folders from sync
		repositoryService.clearExcludedFoldersFromSync(name);
		String excludedFolderFromSync = "";
		boolean excludedFolderFromSyncIsWrong = true;
		do {
			System.out
					.print("Add folder path to exclude extra local content when sync (leave blank to pass): ");
			excludedFolderFromSync = c.nextLine();
			if (excludedFolderFromSync.isEmpty()) {
				excludedFolderFromSyncIsWrong = false;
			} else if (!(new File(excludedFolderFromSync)).exists()) {
				System.out.println("Wrong path, file does not exists.");
				excludedFolderFromSyncIsWrong = true;
			} else {
				repositoryService.addExcludedFilesPathFromBuild(name,
						excludedFolderFromSync);
				excludedFolderFromSyncIsWrong = true;
			}
		} while (excludedFolderFromSyncIsWrong);

		/* Check available disk space */

		boolean isCompressed = repositoryService.isCompressed(name);
		if (isCompressed) {
			long diskSpace = new File("/").getFreeSpace();
			long repositorySize = FileUtils.sizeOfDirectory(new File(
					repositoryService.getRepositoryPath(name)));
			if (diskSpace < repositorySize) {
				String message = "Not enough free space on disk to add compressed pbo files into the repository."
						+ "\n" + "Required space: " + repositorySize;
				System.out.println(message);
				System.out.println("");
				execute();
				return;
			}
		}

		/* Proceed with command */

		super.buildRepository(name, false);
	}

	private void check() {

		System.out.println("");
		System.out.println("Check repository");

		Scanner c = new Scanner(System.in);

		System.out.print("Enter repository name: ");
		String repositoryName = c.nextLine();
		while (repositoryName.isEmpty()) {
			System.out.print("Enter repository name: ");
			repositoryName = c.nextLine();
		}

		/* Load Repositories */

		RepositoryService repositoryService = new RepositoryService();
		try {
			repositoryService.readAll();
		} catch (LoadingException e) {
			System.out.println(e.getMessage());
			System.out.println("");
			execute();
			return;
		}

		/* Proceed with command */

		super.checkRepository(repositoryName, false);
	}

	private void delete() {

		System.out.println("");
		System.out.println("Delete repository");

		Scanner c = new Scanner(System.in);

		System.out.print("Enter repository name: ");
		String name = c.nextLine();
		while (name.isEmpty()) {
			System.out.print("Enter repository name: ");
			name = c.nextLine();
		}

		/* Load Repositories */

		RepositoryService repositoryService = new RepositoryService();
		try {
			repositoryService.readAll();
		} catch (LoadingException e) {
			System.out.println(e.getMessage());
			System.out.println("");
			execute();
			return;
		}

		/* Proceed with command */

		System.out.println("Deleting repository...");
		try {
			boolean remove = repositoryService.removeRepository(name.trim());
			if (remove) {
				System.out.println("Repository " + name + " removed.");
			} else {
				System.out.println("Failded to remove repository.");
			}
		} catch (RepositoryException e) {
			System.out.println(e.getMessage());
		} finally {
			System.out.println("");
			execute();
		}
	}

	private void sync() {

		System.out.println("");
		System.out.println("Synchronize with repository");

		Scanner c = new Scanner(System.in);

		System.out.print("Enter repository name: ");
		String repositoryName = c.nextLine();
		while (repositoryName.isEmpty()) {
			System.out.print("Enter repository name: ");
			repositoryName = c.nextLine();
		}

		/* Load Repositories */

		RepositoryService repositoryService = new RepositoryService();
		try {
			repositoryService.readAll();
		} catch (LoadingException e) {
			System.out.println(e.getMessage());
			System.out.println("");
			execute();
			return;
		}

		// Set destination folder
		boolean destinationFolderIsWrong = true;
		String destinationFolderPath = "";
		do {
			System.out.print("Enter destination folder path: ");
			destinationFolderPath = c.nextLine();
			if (destinationFolderPath.isEmpty()) {
				destinationFolderIsWrong = true;
			} else if (!new File(destinationFolderPath).exists()) {
				System.out.println("Destination folder does not exists!");
				destinationFolderIsWrong = true;
			} else {
				destinationFolderIsWrong = false;
			}
		} while (destinationFolderIsWrong);

		// Set exact file matching
		boolean withExactMatchIsWrong = true;
		String withExactMatch = "";
		do {
			System.out
					.print("Perform Exact file matching (yes/no, choosing yes will erase all extra files into the target folder): ");
			withExactMatch = c.nextLine();
			if (withExactMatch.isEmpty()) {
				withExactMatchIsWrong = true;
			} else if (!(withExactMatch.equalsIgnoreCase("yes") || withExactMatch
					.equalsIgnoreCase("no"))) {
				withExactMatchIsWrong = true;
			} else {
				withExactMatchIsWrong = false;
			}
		} while (withExactMatchIsWrong);

		boolean exactMath = false;
		if (withExactMatch.equalsIgnoreCase("yes")) {
			exactMath = true;
		}

		/* Proceed with command */

		repositoryService.setExactMatch(exactMath, repositoryName);
		repositoryService.setDefaultDownloadLocation(repositoryName,
				destinationFolderPath);
		repositoryService.setConnectionTimeout(repositoryName,"0");
		repositoryService.setReadTimeout(repositoryName, "0");

		super.syncRepository(repositoryName, false);
	}

	private void extractBikeys() {

		System.out.println("");
		System.out.println("Extract *.bikey files");

		Scanner c = new Scanner(System.in);

		String sourceDirectoryPath = "";
		do {
			System.out
					.print("Enter source directory to search for *.bikey files: ");
			sourceDirectoryPath = c.nextLine();
		} while (sourceDirectoryPath.isEmpty());

		String targetDirectoryPath = "";
		do {
			System.out.print("Enter target directory to copy *.bikey files: ");
			targetDirectoryPath = c.nextLine();
		} while (targetDirectoryPath.isEmpty());

		/* Proceed with command */
		
		super.extractBikeys(sourceDirectoryPath, targetDirectoryPath, false);
	}

	private void checkForUpdates() {

		System.out.println("");
		System.out.println("Check for updates.");

		super.checkForUpdates(devMode, false);
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
}
