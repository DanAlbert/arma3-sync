package fr.soe.a3s.ui.tools.bikey;

import java.awt.Image;

import javax.swing.JOptionPane;

import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.tools.WizardCopyDirectoryDialog;

public class BiKeyExtractorDialog2 extends WizardCopyDirectoryDialog {

	/* Services */
	private final ConfigurationService configurationService = new ConfigurationService();

	public BiKeyExtractorDialog2(Facade facade, String title,
			String description, Image image) {
		super(facade, title, description, image);
		sourceDirectoryLabel
				.setText("Source directory to search for *.bikey files");
		targetDirectoryLabel.setText("Target directory to copy *.bikey files");
	}

	public void init() {

		String sourceDirectoryPath = configurationService
				.getBiketyExtractSourceDirectoryPath();
		textFieldSourceDirectory.setText(sourceDirectoryPath);
		String targetDirectoryPath = configurationService
				.getBiketyExtractTargetDirectoryPath();
		textFieldTargetDirectory.setText(targetDirectoryPath);
	}

	@Override
	protected void buttonProceedPerformed() {

		String message = super.check();
		if (!message.isEmpty()) {
			JOptionPane.showMessageDialog(facade.getMainPanel(), message,
					"Bikey extractor wizard", JOptionPane.WARNING_MESSAGE);
		} else {
			ExtractProgressDialog extractProgressPanel = new ExtractProgressDialog(
					facade);
			extractProgressPanel.setVisible(true);
			String sourceDirectoryPath = textFieldSourceDirectory.getText();
			String targetDirectoryPath = textFieldTargetDirectory.getText();
			extractProgressPanel.init(sourceDirectoryPath, targetDirectoryPath);
		}
	}

	@Override
	protected void menuExitPerformed() {

		configurationService
				.setBiketyExtractSourceDirectoryPath(textFieldSourceDirectory
						.getText());
		configurationService
				.setBiketyExtractTargetDirectoryPath(textFieldTargetDirectory
						.getText());
		this.dispose();
	}
}
