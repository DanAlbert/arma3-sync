package fr.soe.a3s.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.List;

import fr.soe.a3s.controller.ObserverConnectionLost;
import fr.soe.a3s.controller.ObserverCountInt;
import fr.soe.a3s.controller.ObserverEnd;
import fr.soe.a3s.controller.ObserverError;
import fr.soe.a3s.controller.ObserverProceed;
import fr.soe.a3s.controller.ObserverText;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.FtpException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.exception.remote.RemoteRepositoryException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.CommonService;
import fr.soe.a3s.service.administration.RepositoryBuildProcessor;
import fr.soe.a3s.service.administration.RepositoryCheckProcessor;
import fr.soe.a3s.service.connection.FtpService;
import fr.soe.a3s.service.synchronization.FilesCheckProcessor;
import fr.soe.a3s.service.synchronization.FilesCompletionProcessor;
import fr.soe.a3s.service.synchronization.FilesSynchronizationManager;
import fr.soe.a3s.service.synchronization.FilesSynchronizationProcessor;
import fr.soe.a3s.utils.ErrorPrinter;
import fr.soe.a3s.utils.UnitConverter;

public class CommandGeneral {

	/* Build */

	protected void build(String repositoryName, ObserverEnd observerEndBuild) {

		RepositoryBuilder repositoryBuilder = new RepositoryBuilder(
				repositoryName, observerEndBuild);
		repositoryBuilder.run();
	}

	private class RepositoryBuilder {

		private final String repositoryName;
		private RepositoryBuildProcessor repositoryBuildProcessor;
		private int value;
		private String text;
		private final ObserverEnd observerEndBuild;

		public RepositoryBuilder(String repositoryName,
				ObserverEnd observerEndBuild) {
			this.repositoryName = repositoryName;
			this.observerEndBuild = observerEndBuild;
		}

		public void run() {

			System.out.println("Building repository: " + repositoryName);

			repositoryBuildProcessor = new RepositoryBuildProcessor(
					repositoryName);
			repositoryBuildProcessor.addObserverText(new ObserverText() {
				@Override
				public void update(String t) {
					text = t;
					executeUpdateText(text);
				}
			});
			repositoryBuildProcessor
					.addObserverCountProgress(new ObserverCountInt() {
						@Override
						public void update(int value) {
							executeUpdateCountProgress(value);
						}
					});
			repositoryBuildProcessor.addObserverEnd(new ObserverEnd() {
				@Override
				public void end() {
					executeEnd();
				}
			});
			repositoryBuildProcessor.addObserverError(new ObserverError() {
				@Override
				public void error(List<Exception> errors) {
					executeError(errors);
				}
			});

			value = 0;
			repositoryBuildProcessor.run();
		}

		private void executeUpdateText(String text) {
			System.out.println(text);
		}

		private void executeUpdateCountProgress(int v) {
			if (v > value) {
				value = v;
				System.out.println(text + " complete: " + value + " %");
			}
		}

		private void executeEnd() {

			System.out.println("Repository " + repositoryName
					+ " - build finished.");

			observerEndBuild.end();
		}

		private void executeError(List<Exception> errors) {

			System.out.println("Repository " + repositoryName
					+ " - build finished with error.");

			Exception ex = errors.get(0);
			if (ex instanceof RepositoryException | ex instanceof IOException
					| ex instanceof WritingException) {
				ErrorPrinter.printRepositoryManagedError(repositoryName, ex);
			} else {
				ErrorPrinter.printRepositoryUnexpectedError(repositoryName, ex);
			}

			observerEndBuild.end();
		}
	}

	/* Check */

	protected void check(String repositoryName, ObserverEnd observerEndCheck) {

		RepositoryChecker repositoryChecker = new RepositoryChecker(
				repositoryName, observerEndCheck);
		repositoryChecker.run();
	}

	private class RepositoryChecker {

		private final String repositoryName;
		private RepositoryCheckProcessor repositoryCheckProcessor;
		private int value;
		private final ObserverEnd observerEndCheck;

		public RepositoryChecker(String repositoryName,
				ObserverEnd observerEndCheck) {
			this.repositoryName = repositoryName;
			this.observerEndCheck = observerEndCheck;
		}

		public void run() {

			repositoryCheckProcessor = new RepositoryCheckProcessor(
					repositoryName);
			repositoryCheckProcessor
					.addObserverCountProgress(new ObserverCountInt() {
						@Override
						public void update(int value) {
							executeUpdateCountProgress(value);
						}
					});
			repositoryCheckProcessor.addObserverEnd(new ObserverError() {
				@Override
				public void error(List<Exception> errors) {
					executeEnd(errors);
				}
			});
			repositoryCheckProcessor.addObserverError(new ObserverError() {
				@Override
				public void error(List<Exception> errors) {
					executeError(errors);
				}
			});

			value = 0;
			repositoryCheckProcessor.run();
		}

		private void executeUpdateCountProgress(int v) {
			if (v > value) {
				value = v;
				System.out.println("Check Complete: " + value + " %");
			}
		}

		private void executeEnd(List<Exception> errors) {

			if (errors.isEmpty()) {
				System.out.println("Repository " + repositoryName
						+ " - repository is synchronized.");
			} else {
				System.out.println("Repository " + repositoryName
						+ " - repository is not synchronized.");
				for (Exception e : errors) {
					System.out.println(e.getMessage());
				}
			}

			observerEndCheck.end();
		}

		private void executeError(List<Exception> errors) {

			System.out.println("Repository " + repositoryName
					+ " - synchronization finished with error.");

			Exception ex = errors.get(0);
			if (ex instanceof RepositoryException
					|| ex instanceof RemoteRepositoryException
					|| ex instanceof IOException) {
				ErrorPrinter.printRepositoryManagedError(repositoryName, ex);
			} else {
				ErrorPrinter.printRepositoryUnexpectedError(repositoryName, ex);
			}

			observerEndCheck.end();
		}
	}

	/* Sync */

	protected void sync(String repositoryName, ObserverEnd observerEnd) {

		AddonsUpdater addonsUpdater = new AddonsUpdater(repositoryName);
		addonsUpdater.addObserverEndUpdate(observerEnd);
		addonsUpdater.run();
	}

	private class AddonsUpdater {

		private final String repositoryName;
		private boolean lost;
		//
		private AddonsChecker addonsChecker;
		private AddonsDownloader addonsDownloader;
		private FilesSynchronizationManager filesManager;
		//
		private boolean check1IsDone, check2IsDone;
		//
		private ObserverEnd observerEndUpdate;

		public AddonsUpdater(String repositoryName) {
			this.repositoryName = repositoryName;
		}

		public void run() {

			System.out.println("Synchronising with repository: "
					+ repositoryName);

			lost = false;

			filesManager = new FilesSynchronizationManager();
			addonsChecker = new AddonsChecker();
			addonsDownloader = new AddonsDownloader();

			check1IsDone = false;
			check2IsDone = false;

			addonsChecker.addObserverEnd(new ObserverEnd() {
				@Override
				public void end() {
					addonsCheckerEnd();
				}
			});
			addonsDownloader.addObserverEnd(new ObserverEnd() {
				@Override
				public void end() {
					addonsDownloaderEnd();
				}
			});
			addonsChecker.addObserverError(new ObserverError() {
				@Override
				public void error(List<Exception> errors) {
					executeError(errors);
				}
			});
			addonsDownloader.addObserverError(new ObserverError() {
				@Override
				public void error(List<Exception> errors) {
					executeError(errors);
				}
			});
			addonsDownloader
					.addObserverConnectionLost(new ObserverConnectionLost() {
						@Override
						public void lost() {
							executeConnectionLost();
						}
					});

			addonsChecker.run();
		}

		private void addonsCheckerEnd() {

			if (!check1IsDone) {
				check1IsDone = true;
			} else if (!check2IsDone) {
				check2IsDone = true;
			}

			if (check1IsDone && check2IsDone) {
				System.out.println("Synchronization with repository: "
						+ repositoryName + " finished.");
				addonsChecker = null;
				addonsDownloader = null;
				System.gc();
				observerEndUpdate.end();
			} else if (check1IsDone && !check2IsDone) {
				addonsDownloader.run();
			}
		}

		private void addonsDownloaderEnd() {
			addonsChecker.run();
		}

		private void executeError(List<Exception> errors) {

			System.out.println("Synchronization with repository: "
					+ repositoryName + " finished with errors:");
			for (Exception ex : errors) {
				if (ex instanceof IOException) {
					ErrorPrinter
							.printRepositoryManagedError(repositoryName, ex);
				} else {
					ErrorPrinter.printRepositoryUnexpectedError(repositoryName,
							ex);
				}
			}
			addonsChecker = null;
			addonsDownloader = null;
			System.gc();
			observerEndUpdate.end();
		}

		private void executeConnectionLost() {

			if (!lost) {
				lost = true;
				System.out.println("Synchronization with repository: "
						+ repositoryName + " - Connection failed.");
				try {
					Thread.sleep(5000);
					addonsDownloader.run();
				} catch (InterruptedException e) {
				}
			}
		}

		public void addObserverEndUpdate(ObserverEnd obs) {
			this.observerEndUpdate = obs;
		}

		private class AddonsChecker {

			private FilesCheckProcessor filesCheckProcessor;
			private FilesCompletionProcessor filesCompletionProcessor;
			private ObserverEnd observerEnd;
			private ObserverError observerError;
			private SyncTreeDirectoryDTO parent;

			public void run() {

				filesCheckProcessor = new FilesCheckProcessor(repositoryName,
						false);
				filesCheckProcessor.addObserverCount(new ObserverCountInt() {
					@Override
					public void update(int value) {
						executeUpdateCheck(value);
					}
				});
				filesCheckProcessor.addObserverError(new ObserverError() {
					@Override
					public void error(List<Exception> errors) {
						executeError(errors);
					}
				});

				filesCompletionProcessor = new FilesCompletionProcessor(
						repositoryName);
				filesCompletionProcessor
						.addObserverCount(new ObserverCountInt() {
							@Override
							public void update(int value) {
								executeUpdateCompletion(value);
							}
						});
				filesCompletionProcessor.addObserverEnd(new ObserverEnd() {
					@Override
					public void end() {
						executeEnd();
					}
				});
				filesCompletionProcessor.addObserverError(new ObserverError() {
					@Override
					public void error(List<Exception> errors) {
						executeError(errors);
					}
				});

				this.parent = filesCheckProcessor.run();// blocking
														// execution
				if (parent == null) {
					executeEnd();
				} else {
					filesCompletionProcessor.run(parent); // non blocking
															// execution
				}
			}

			private void executeUpdateCheck(int value) {
				System.out.println("Computing SHA1 files signatures complete: "
						+ value + " %");
			}

			private void executeUpdateCompletion(int value) {
				System.out.println("Checking files Completion complete: "
						+ value + " %");
			}

			private void executeEnd() {

				if (parent != null) {
					filesManager.setParent(parent);
					selectAll(parent);
					filesManager.update();

					System.out.println("Number of files to update = "
							+ filesManager.getListFilesToUpdate().size());
					System.out.println("Number of files to delete = "
							+ filesManager.getListFilesToDelete().size());
					System.out.println("Update files size: "
							+ UnitConverter.convertSize(filesManager
									.getTotalDownloadFilesSize()));
				}
				filesCheckProcessor.cancel();
				filesCompletionProcessor.cancel();
				observerEnd.end();
			}

			private void executeError(List<Exception> errors) {

				filesCheckProcessor.cancel();
				filesCompletionProcessor.cancel();
				observerError.error(errors);
			}

			private void selectAll(SyncTreeNodeDTO node) {

				if (node.isLeaf()) {
					SyncTreeLeafDTO leaf = (SyncTreeLeafDTO) node;
					leaf.setSelected(true);
				} else {
					SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) node;
					for (SyncTreeNodeDTO n : directory.getList()) {
						selectAll(n);
					}
				}
			}

			public void addObserverEnd(ObserverEnd obs) {
				this.observerEnd = obs;
			}

			public void addObserverError(ObserverError obs) {
				this.observerError = obs;
			}

			public SyncTreeDirectoryDTO getParent() {
				return parent;
			}
		}

		private class AddonsDownloader {

			private FilesSynchronizationProcessor filesSynchronizationProcessor;
			private ObserverEnd observerEnd;
			private ObserverError observerError;
			private ObserverConnectionLost observerConnectionLost;
			private int value;

			public void run() {

				filesSynchronizationProcessor = new FilesSynchronizationProcessor(
						repositoryName, filesManager);
				filesSynchronizationProcessor
						.addObserverCountTotalProgress(new ObserverCountInt() {
							@Override
							public void update(int value) {
								executeUpdateTotalProgress(value);
							}
						});
				filesSynchronizationProcessor.addObserverEnd(new ObserverEnd() {
					@Override
					public void end() {
						executeEnd();
					}
				});
				filesSynchronizationProcessor
						.addObserverProceedDelete(new ObserverProceed() {
							@Override
							public void proceed() {
								executeProceedDelete();
							}
						});
				filesSynchronizationProcessor
						.addObserverError(new ObserverError() {
							@Override
							public void error(List<Exception> errors) {
								executeError(errors);
							}
						});
				filesSynchronizationProcessor
						.addObserverConnectionLost(new ObserverConnectionLost() {
							@Override
							public void lost() {
								executeConnectionLost();
							}
						});

				value = 0;
				lost = false;
				filesSynchronizationProcessor.run();
			}

			private void executeUpdateTotalProgress(int v) {
				if (v > value) {
					value = v;
					System.out.println("Download complete: " + value + " %");
				}
			}

			private void executeProceedDelete() {
				System.out.println("Deleting extra local files...");
			}

			private void executeEnd() {
				filesSynchronizationProcessor.cancel();
				observerEnd.end();
			}

			private void executeError(List<Exception> errors) {
				filesSynchronizationProcessor.cancel();
				observerError.error(errors);
			}

			private void executeConnectionLost() {
				filesSynchronizationProcessor.cancel();
				observerConnectionLost.lost();
			}

			public void addObserverEnd(ObserverEnd obs) {
				this.observerEnd = obs;
			}

			public void addObserverError(ObserverError obs) {
				this.observerError = obs;
			}

			private void addObserverConnectionLost(ObserverConnectionLost obs) {
				this.observerConnectionLost = obs;
			}
		}
	}

	/* Extract Bikeys */

	protected void extractBikeys(String sourceDirectoryPath,
			String targetDirectoryPath) {

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
	}

	/* Check for Updates */

	protected void checkForUpdates(boolean devMode) {

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
	}
}
