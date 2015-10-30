package fr.soe.a3s.ui.repositoryEditor.dialogs;

import javax.swing.JOptionPane;

import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.ui.Facade;

public class UploadEventsOptionsPanel extends UploadOptionsPanel {

	public UploadEventsOptionsPanel(Facade facade) {
		super(facade);
		this.setSize(405, 280);
		this.setLocation(
				(int) facade.getMainPanel().getLocation().getX()
						+ facade.getMainPanel().getWidth() / 2
						- this.getWidth() / 2,
				(int) facade.getMainPanel().getLocation().getY()
						+ facade.getMainPanel().getHeight() / 2
						- this.getHeight() / 2);
	}

	@Override
	public void init(String repositoryName) {
		super.init(repositoryName);
	}

	@Override
	protected void buttonOKPerformed() {

		boolean ok = super.proceed();
		if (ok) {
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
