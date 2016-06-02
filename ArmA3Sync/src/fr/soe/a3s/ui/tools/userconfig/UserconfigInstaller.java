package fr.soe.a3s.ui.tools.userconfig;

import java.awt.Image;

import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.tools.WizardCopyDirectoryDialog;

public class UserconfigInstaller extends WizardCopyDirectoryDialog {

	public UserconfigInstaller(Facade facade, String title, String description,
			Image image) {
		super(facade, title, description, image);
		sourceDirectoryLabel.setText("Userconfig directory to copy");
		targetDirectoryLabel.setText("ArmA 3 installation directory");
	}

	public void init(String userconfig) {

	}

	@Override
	protected void buttonProceedPerformed() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void menuExitPerformed() {
		// TODO Auto-generated method stub

	}

}
