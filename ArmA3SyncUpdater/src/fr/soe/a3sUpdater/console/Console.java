package fr.soe.a3sUpdater.console;

import fr.soe.a3sUpdater.controller.Observateur;
import fr.soe.a3sUpdater.service.Service;
import fr.soe.a3sUpdater.ui.UIConstants;

public class Console {

	private boolean devMode = false;
	private int value = 0;

	public Console(boolean devMode) {
		this.devMode = devMode;
	}

	public void execute() {

		System.out.println("Console");
		Service service = new Service();

		try {
			String version = service.getVersion();
			if (version == null) {
				System.out
						.println("Can't determine current version of the application");
				System.exit(0);
			} else {
				System.out.println("Updating ArmA3Sync...");
				final int size = (int) service.getSize(devMode);
				service.setDownload();
				service.getFtpDAO().getDos().addObservateur(new Observateur() {
					public void update(int v) {
						v = v * 100 / size;
						if (value != v) {
							value = v;
							System.out
									.println("Download complete: " + value + " %");
						}
					}
				});
				service.download(devMode);
				System.out.println("");
				System.out.println("Processing update...");
				service.install();
				System.out.println("");
				System.out.println(UIConstants.TARGET_APPLICATION_NAME
						+ " has been successfully updated to version "
						+ version);
			}
		} catch (Exception e) {
			System.out.println("An error occured." + "\n" + e.getMessage()
					+ "\n" + "Update process aborded.");
		} finally {
			service.clean();
			System.exit(0);
		}
	}
}
