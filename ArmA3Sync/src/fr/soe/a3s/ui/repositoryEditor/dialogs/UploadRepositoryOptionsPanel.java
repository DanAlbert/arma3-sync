package fr.soe.a3s.ui.repositoryEditor.dialogs;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.ui.Facade;

public class UploadRepositoryOptionsPanel extends UploadOptionsPanel {

	private JCheckBox checkBoxOptions;
	private JPanel optionsPanel;

	public UploadRepositoryOptionsPanel(Facade facade) {
		super(facade);
		this.setSize(405, 350);
		this.setLocation(
				(int) facade.getMainPanel().getLocation().getX()
						+ facade.getMainPanel().getWidth() / 2
						- this.getWidth() / 2,
				(int) facade.getMainPanel().getLocation().getY()
						+ facade.getMainPanel().getHeight() / 2
						- this.getHeight() / 2);
		{
			optionsPanel = new JPanel();
			optionsPanel.setLayout(null);
			optionsPanel.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(), "Options"));
			{
				checkBoxOptions = new JCheckBox();
				optionsPanel.add(checkBoxOptions);
				checkBoxOptions.setText("Upload only compressed pbo files");
				checkBoxOptions.setBounds(18, 28, 261, 23);
			}
		}

		vertBox.add(Box.createVerticalStrut(5));
		vertBox.add(optionsPanel);
	}

	@Override
	public void init(String repositoryName) {

		super.init(repositoryName);

		// Set Upload options
		boolean isUploadCompressedPboFilesOnly = repositoryService
				.isUploadCompressedPboFilesOnly(repositoryName);
		if (isUploadCompressedPboFilesOnly) {
			checkBoxOptions.setSelected(true);
		} else {
			checkBoxOptions.setSelected(false);
		}
		boolean isCompressed = repositoryService.isCompressed(repositoryName);
		if (isCompressed) {
			checkBoxOptions.setEnabled(true);
		} else {
			checkBoxOptions.setEnabled(false);
		}
	}

	@Override
	public void buttonOKPerformed() {

		boolean ok = super.proceed();
		if (ok) {
			// Set Upload options
			repositoryService.setUploadCompressedPboFilesOnly(repositoryName,
					checkBoxOptions.isSelected());
			// Write to disk
			try {
				repositoryService.write(repositoryName);
				this.dispose();
			} catch (WritingException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
