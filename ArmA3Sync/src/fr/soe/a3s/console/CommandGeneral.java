package fr.soe.a3s.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import fr.soe.a3s.controller.ObserverCount;
import fr.soe.a3s.controller.ObserverDownload;
import fr.soe.a3s.controller.ObserverText;
import fr.soe.a3s.controller.ObserverUncompress;
import fr.soe.a3s.dao.FileAccessMethods;
import fr.soe.a3s.dao.connection.AbstractConnexionDAO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.exception.FtpException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.exception.remote.RemoteAutoconfigFileNotFoundException;
import fr.soe.a3s.exception.remote.RemoteChangelogsFileNotFoundException;
import fr.soe.a3s.exception.remote.RemoteRepositoryException;
import fr.soe.a3s.exception.remote.RemoteServerInfoFileNotFoundException;
import fr.soe.a3s.exception.remote.RemoteSyncFileNotFoundException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.CommonService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.service.connection.ConnexionService;
import fr.soe.a3s.service.connection.ConnexionServiceFactory;
import fr.soe.a3s.service.connection.FtpService;
import fr.soe.a3s.ui.FileSizeComputer;

public class CommandGeneral {

	private int value = 0;
	private String text = "";
	/** Sync variables */
	private long incrementedFilesSize;
	private long totalExpectedFilesSize;
	private final List<SyncTreeNodeDTO> listFilesToUpdate = new ArrayList<SyncTreeNodeDTO>();
	private final List<SyncTreeNodeDTO> listFilesToDelete = new ArrayList<SyncTreeNodeDTO>();

	protected void buildRepository(String repositoryName, boolean exit) {

		RepositoryService repositoryService = new RepositoryService();

		repositoryService.getRepositoryBuilderDAO().addObserverText(
				new ObserverText() {
					@Override
					public void update(String t) {
						text = t;
						value = 0;
					}
				});

		repositoryService.getRepositoryBuilderDAO().addObserverCount(
				new ObserverCount() {
					@Override
					public synchronized void update(int v) {
						if (v > value) {
							value = v;
							System.out.println(text + " complete: " + value
									+ " %");
						}
					}
				});

		try {
			System.out.println("Building repository...");
			repositoryService.buildRepository(repositoryName);
			repositoryService.write(repositoryName);
			System.out.println("");
			System.out.println("Repository build finished.");
		} catch (RepositoryException | IOException e) {
			System.out.println("Build repository failed.");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("Build repository failed.");
			System.out.println("An unexpected error has occured.");
			e.printStackTrace();
		}

		if (exit) {
			System.exit(0);
		} else {
			CommandConsole console = new CommandConsole(false);
			System.out.println("");
			console.execute();
		}
	}

	protected void checkRepository(String repositoryName, boolean exit) {

		this.value = 0;
		RepositoryService repositoryService = new RepositoryService();
		ConnexionService connexionService = null;

		try {
			RepositoryDTO repositoryDTO = repositoryService
					.getRepository(repositoryName);
			System.out.println("Checking repository...");
			connexionService = ConnexionServiceFactory
					.getServiceForRepositoryManagement(repositoryName);
			connexionService.getSync(repositoryName);
			connexionService.getServerInfo(repositoryName);
			connexionService.getChangelogs(repositoryName);
			connexionService.getAutoconfig(repositoryName);

			if (repositoryService.getSync(repositoryName) == null) {
				throw new RemoteSyncFileNotFoundException();
			}

			if (repositoryService.getServerInfo(repositoryName) == null) {
				throw new RemoteServerInfoFileNotFoundException();
			}

			if (repositoryService.getChangelogs(repositoryName) == null) {
				throw new RemoteChangelogsFileNotFoundException();
			}

			if (repositoryService.getAutoconfig(repositoryName) == null) {
				throw new RemoteAutoconfigFileNotFoundException();
			}

			this.value = 0;

			connexionService.getConnexionDAO().addObserverCount(
					new ObserverCount() {
						@Override
						public void update(int v) {
							if (v > value) {
								value = v;
								System.out.println("Check Complete: " + value
										+ " %");
							}
						}
					});

			List<Exception> errors = connexionService
					.checkRepositoryContent(repositoryName);

			if (errors.isEmpty()) {
				System.out.println("Repository is synchronized.");
			} else {
				System.out.println("Repository is out of synchronization.");
				for (Exception e : errors) {
					System.out.println(e.getMessage());
				}
			}
		} catch (RepositoryException | RemoteRepositoryException | IOException e) {
			System.out.println("Check repository failed.");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("Check repository failed.");
			System.out.println("An unexpected error has occured.");
			e.printStackTrace();
		}

		if (exit) {
			System.exit(0);
		} else {
			CommandConsole console = new CommandConsole(false);
			System.out.println("");
			console.execute();
		}
	}

	protected void syncRepository(String repositoryName, boolean exit) {

		/* Proceed with command */

		boolean ok = checkForAddons(repositoryName);
		if (ok) {
			downloadAddons(repositoryName, exit);
		} else {
			if (exit) {
				System.exit(0);
			} else {
				CommandConsole console = new CommandConsole(false);
				System.out.println("");
				console.execute();
			}
		}
	}

	protected void extractBikeys(String sourceDirectoryPath,
			String targetDirectoryPath, boolean exit) {

		System.out.println("Extracting *.bikey files...");

		String message = "";
		if (sourceDirectoryPath.isEmpty()) {
			message = "Source directory is empty!";
		} else if (!new File(sourceDirectoryPath).exists()) {
			message = "Source directory does not exists!";
		} else if (targetDirectoryPath.isEmpty()) {
			message = "Target directory is empty!";
		} else if (!new File(targetDirectoryPath).exists()) {
			message = "Target directory does not exists!";
		} else if (!Files.isWritable(FileSystems.getDefault().getPath(
				targetDirectoryPath))) {// Check write permissions on target
										// directory
			message = "Can't write on target directory!";
		}

		if (!message.isEmpty()) {
			System.out.println(message);
		} else {
			try {
				CommonService commonService = new CommonService();
				commonService.extractBikeys(sourceDirectoryPath,
						targetDirectoryPath);
				System.out.println("Extraction done.");
			} catch (IOException e) {
				System.out.println("Extraction failed.");
				System.out.println(e.getMessage());
			}
		}

		if (exit) {
			System.exit(0);
		} else {
			CommandConsole console = new CommandConsole(false);
			System.out.println("");
			console.execute();
		}
	}

	private boolean checkForAddons(String repositoryName) {

		RepositoryService repositoryService = new RepositoryService();
		boolean ok = false;
		ConnexionService connexionService = null;

		try {
			connexionService = ConnexionServiceFactory
					.getServiceForRepositoryManagement(repositoryName);
		} catch (RepositoryException | CheckException e) {
			System.out.println("Sync repository failed.");
			System.out.println(e.getMessage());
			return false;
		}

		try {
			connexionService.getSync(repositoryName);
			connexionService.getServerInfo(repositoryName);

			if (repositoryService.getSync(repositoryName) == null) {
				throw new RemoteSyncFileNotFoundException();
			}

			if (repositoryService.getServerInfo(repositoryName) == null) {
				throw new RemoteServerInfoFileNotFoundException();
			}

			this.value = 0;

			repositoryService.getRepositorySHA1Processor().addObserverCount(
					new ObserverCount() {
						@Override
						public synchronized void update(int v) {
							if (v > value) {
								value = v;
								System.out
										.println("Check for Addons complete: "
												+ value + " %");
							}
						}
					});

			// Check for Addons
			SyncTreeDirectoryDTO parent = repositoryService
					.checkForAddons(repositoryName);
			repositoryService.write(repositoryName);

			System.out.println("Determining files completion...");

			this.value = 0;

			connexionService.getConnexionDAO().addObserverCount(
					new ObserverCount() {
						@Override
						public void update(int v) {
							if (v > value) {
								value = v;
								System.out
										.println("Determining files completion complete: "
												+ value + " %");
							}
						}
					});

			// Determine completion
			connexionService.determineFilesCompletion(repositoryName, parent);

			listFilesToUpdate.clear();
			listFilesToDelete.clear();

			// Get files list
			for (SyncTreeNodeDTO node : parent.getList()) {
				getFiles(node);
			}

			System.out.println("Number of files to update = "
					+ listFilesToUpdate.size());
			System.out.println("Number of files to delete = "
					+ listFilesToDelete.size());
			System.out.println("Checking for addons is finished.");

			ok = true;

		} catch (RepositoryException | RemoteRepositoryException | IOException e) {
			System.out.println("Sync repository failed.");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("Sync repository failed.");
			System.out.println("An unexpected error has occured.");
			e.printStackTrace();
		} finally {
			if (connexionService != null) {
				connexionService.cancel();
			}
		}
		return ok;
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

	private void downloadAddons(final String repositoryName, final boolean exit) {

		System.out.println("Downloading addons...");

		if (listFilesToUpdate.size() == 0) {
			finish(repositoryName, exit);
		} else {
			determineTotalFilesSize();

			RepositoryService repositoryService = new RepositoryService();

			try {
				final ConnexionService connexionService = ConnexionServiceFactory
						.getServiceForRepositoryManagement(repositoryName);

				for (AbstractConnexionDAO connect : connexionService
						.getConnexionDAOs()) {
					connect.addObserverDownload(new ObserverDownload() {

						@Override
						public void updateTotalSizeProgress(long value) {
							executeUpdateTotalSizeProgress(value);
						}

						@Override
						public void updateTotalSize() {
							executeUpdateTotalSize();
						}

						@Override
						public void updateSingleSizeProgress(long value,
								int pourcentage) {
						}

						@Override
						public void updateSpeed() {
						}

						@Override
						public void updateActiveConnections() {
						}

						@Override
						public void updateEnd() {
							if (connexionService != null) {
								connexionService.cancel();
							}
							finish(repositoryName, exit);
						}

						@Override
						public void updateEndWithErrors(List<Exception> errors) {
							if (connexionService != null) {
								connexionService.cancel();
							}
							finishWithErrors("Download finished with errors",
									errors, repositoryName, exit);
						}

						@Override
						public void updateCancelTooManyTimeoutErrors(int value,
								List<Exception> errors) {
							if (connexionService != null) {
								connexionService.cancel();
							}
							finishWithTooManyTimeoutErrors(
									"Download has been canceled due to too many consecutive time out errors (>"
											+ value + ")", errors,
									repositoryName, exit);
						}

						@Override
						public void updateCancelTooManyErrors(int value,
								List<Exception> errors) {
							if (connexionService != null) {
								connexionService.cancel();
							}
							finishWithTooManyErrors(
									"Download has been canceled due to too many errors (>"
											+ value + ")", errors,
									repositoryName, exit);
						}

						@Override
						public void updateResponseTime(long responseTime) {
						}
					});
				}

				connexionService.getUnZipFlowProcessor().addObserverUncompress(
						new ObserverUncompress() {

							@Override
							public void start() {
								System.out
										.println("Uncompressing *pbo.zip files...");
							}

							@Override
							public void update(int value) {
								executeUncompressingProgress(value);
							}

							@Override
							public void end() {
								finish(repositoryName, exit);
							}

							@Override
							public void endWithError(List<Exception> errors) {
								if (connexionService != null) {
									connexionService.cancel();
								}
								finishWithErrors(
										"Download finished with errors",
										errors, repositoryName, exit);
							}
						});

				connexionService.synchronize(repositoryName, listFilesToUpdate);

			} catch (RepositoryException | CheckException | IOException e) {
				System.out.println("Sync repository failed.");
				System.out.println(e.getMessage());
				if (exit) {
					System.exit(0);
				} else {
					CommandConsole console = new CommandConsole(false);
					System.out.println("");
					console.execute();
				}

			} catch (Exception e) {
				System.out.println("Sync repository failed.");
				System.out.println("An unexpected error has occured.");
				e.printStackTrace();
				if (exit) {
					System.exit(0);
				} else {
					CommandConsole console = new CommandConsole(false);
					System.out.println("");
					console.execute();
				}
			}
		}
	}

	private synchronized void executeUpdateTotalSize() {

		determineTotalFilesSize();
	}

	private synchronized void executeUpdateTotalSizeProgress(long value) {

		if (totalExpectedFilesSize != 0) {// division by 0!
			incrementedFilesSize = incrementedFilesSize + value;
			System.out
					.println("Download complete: "
							+ (int) (((incrementedFilesSize) * 100) / totalExpectedFilesSize)
							+ " %");
		}
	}

	private synchronized void executeUncompressingProgress(int value) {
		System.out.println("Uncompressing complete: " + value + " %");
	}

	private void finish(final String repositoryName, boolean exit) {

		System.out.println("Deleting extra files...");
		deleteExtraFiles();
		System.out.println("Checking for addons...");
		checkForAddons(repositoryName);
		System.out.println("Synchronization is finished.");
		if (exit) {
			System.exit(0);
		} else {
			CommandConsole console = new CommandConsole(false);
			System.out.println("");
			console.execute();
		}
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

	private void determineTotalFilesSize() {

		totalExpectedFilesSize = 0;
		for (SyncTreeNodeDTO node : listFilesToUpdate) {
			if (node.isLeaf()) {
				SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
				totalExpectedFilesSize = totalExpectedFilesSize
						+ FileSizeComputer.computeExpectedSize(leaf);
			}
		}
	}

	private void finishWithErrors(String message, List<Exception> errors,
			final String repositoryName, boolean exit) {

		System.out.println("Deleting extra files...");
		deleteExtraFiles();

		System.out.println(message + ":");

		List<String> messages = new ArrayList<String>();
		for (Exception e : errors) {
			if (e instanceof IOException || e instanceof RepositoryException) {
				messages.add("- " + e.getMessage());
			} else {
				String coreMessage = "- An unexpected error has occured.";
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				String stacktrace = sw.toString(); // stack trace as a String
				coreMessage = coreMessage + "\n" + "StackTrace:" + "\n"
						+ stacktrace;
				messages.add(coreMessage);
			}
		}

		for (String m : messages) {
			System.out.println(m);
		}

		System.out.println("Checking for addons...");
		checkForAddons(repositoryName);
		System.out.println("Synchronization is finished.");
		if (exit) {
			System.exit(0);
		} else {
			CommandConsole console = new CommandConsole(false);
			System.out.println("");
			console.execute();
		}
	}

	private synchronized void finishWithTooManyTimeoutErrors(String message,
			List<Exception> errors, String repositoryName, boolean exit) {

		finishWithErrors(message, errors, repositoryName, exit);
	}

	private synchronized void finishWithTooManyErrors(String message,
			List<Exception> errors, String repositoryName, boolean exit) {

		finishWithErrors(message, errors, repositoryName, exit);
	}

	protected void checkForUpdates(boolean devMode, boolean exit) {

		FtpService ftpService = new FtpService();
		String availableVersion = null;
		try {
			availableVersion = ftpService.checkForUpdates(devMode);
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
		}

		if (exit) {
			System.exit(0);
		} else {
			CommandConsole console = new CommandConsole(devMode);
			System.out.println("");
			console.execute();
		}
	}
}
