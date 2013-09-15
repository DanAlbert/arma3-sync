package fr.soe.a3s.console;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

import fr.soe.a3s.constant.ConsoleCommands;
import fr.soe.a3s.constant.EncryptionMode;
import fr.soe.a3s.controller.ObserverFileSize;
import fr.soe.a3s.dao.AfficheurFlux;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.RepositoryCheckException;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.exception.ServerInfoNotFoundException;
import fr.soe.a3s.exception.SyncFileNotFoundException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.main.Version;
import fr.soe.a3s.service.FtpService;
import fr.soe.a3s.service.RepositoryService;

public class Console {

	private boolean devMode = false;

	private double value;

	public Console(boolean devMode) {
		this.devMode = devMode;
		displayCommands();
	}

	private void displayCommands() {

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
			System.out.println("Failded to load on or more repositories");
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
			String url = repositoryDTO.getFtpDTO().getUrl();
			String login = repositoryDTO.getFtpDTO().getLogin();
			String password = repositoryDTO.getFtpDTO().getPassword();
			String port = repositoryDTO.getFtpDTO().getPort();

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
			System.out.println("Url: ftp://" + url);
			System.out.println("Auto-config url: ftp://" + autoconfig);
			System.out.println("FTP folder path: " + path);
			System.out.println("");
		}

		if (repositoryDTOs.size() == 0) {
			System.out.println("");
		}
		execute();
	}

	private void newRepository() {

		System.out.println("");
		System.out.println("Build a new repository");

		Scanner c = new Scanner(System.in);

		System.out.print("Enter repository name: ");
		String name = c.nextLine();
		while (name.isEmpty()) {
			System.out.print("Enter repository name: ");
			name = c.nextLine();
		}
		System.out.print("Enter repository url: ftp://");
		String url = c.nextLine();
		while (url.isEmpty()) {
			System.out.print("Enter repository url: ");
			url = c.nextLine();
		}
		System.out.print("Enter FTP shared folder path: ");
		String path = c.nextLine();
		while (path.isEmpty()) {
			System.out.print("Enter FTP shared folder path: ");
			path = c.nextLine();
		}

		RepositoryService repositoryService = new RepositoryService();
		try {
			repositoryService.createRepository(name, url, "21", "anonymous",
					"", EncryptionMode.NO_ENCRYPTION);
			repositoryService.write(name);
		} catch (CheckException e) {
			System.out.println(e.getMessage());
			System.out.println("");
			execute();
			return;
		} catch (WritingException e) {
			System.out.println("Failded to write repository.");
			System.out.println(e.getMessage());
			System.out.println("");
			execute();
			return;
		}

		this.value = 0;

		repositoryService.getRepositoryBuilderDAO().addObserverFileSize(
				new ObserverFileSize() {
					@Override
					public void update(long v) {
						if (v > value) {
							value = v;
							System.out.println("Progress: " + value + " %");
						}
					}
				});

		try {
			repositoryService.buildRepository(name, path);
			repositoryService.write(name);
			System.out.println("Repository creation finished.");
			System.out.println("Auto-config url: ftp://" + url + "/.a3s/autoconfig");
		} catch (Exception e) {
			System.out
					.println("An error occured. Failed to create repository.");
			System.out.println(e.getMessage());
		} finally {
			System.out.println("");
			execute();
		}
	}

	private void checkRepository() {

		System.out.println("");
		System.out.println("Check repository");

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
			System.out.println("Failded to load on or more repositories");
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
			System.out.println("");
			execute();
		}
	}

	private void buildRepository() {

		System.out.println("");
		System.out.println("Update repository");

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
			System.out.println("Failded to read on or more repositories");
			System.exit(0);
		}

		this.value = 0;

		repositoryService.getRepositoryBuilderDAO().addObserverFileSize(
				new ObserverFileSize() {
					@Override
					public void update(long v) {
						if (v > value) {
							value = v;
							System.out.println("Build complete: " + value
									+ " %");
						}
					}
				});

		try {
			repositoryService.buildRepository(name);
			System.out.println("Repository build finished.");
		} catch (Exception e) {
			System.out.println("Failed to build repository.");
			System.out.println(e.getMessage());
		} finally {
			System.out.println("");
			execute();
		}
	}

	private void deleteRepository() {

		System.out.println("");
		System.out.println("Delete repository");

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
			repositoryService.removeRepository(name.trim());
			System.out.println("Repository " + name + " removed.");
		} catch (LoadingException e) {
			System.out.println("Failded to read on or more repositories");
		} finally {
			System.out.println("");
			execute();
		}
	}

	private void checkForUpdates() {

		System.out.println("");
		FtpService ftpService = new FtpService();
		String availableVersion = ftpService.checkForUpdate(devMode);

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
}
