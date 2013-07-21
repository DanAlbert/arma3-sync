package fr.soe.a3s.ui.repositoryEditor;

import java.awt.BorderLayout;

import javax.swing.JDialog;

import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

public class DestinationFolderPanel extends JDialog implements UIConstants{

	private Facade facade;
	private SyncTreeDirectoryDTO syncTreeDirectoryDTO;
	
	public DestinationFolderPanel(Facade facade, SyncTreeDirectoryDTO syncTreeDirectoryDTO){
		this.facade = facade;
		this.syncTreeDirectoryDTO = syncTreeDirectoryDTO;
		this.facade.setDestinationFolderPanel(this);
		this.setResizable(false);
		this.setSize(220, 120);
		setIconImage(ICON);
		this.setLocation((int) facade.getMainPanel().getLocation().getX()
				+ facade.getMainPanel().getWidth() / 2 - this.getWidth() / 2,
				(int) facade.getMainPanel().getLocation().getY()
						+ facade.getMainPanel().getHeight() / 2
						- this.getHeight() / 2);
		this.setLayout(new BorderLayout());
	}
	
	private void buttonSelectDownloadLocationSelectionPerformed() {

//		JFileChooser fc = new JFileChooser();
//		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//		int returnVal = fc.showOpenDialog(facade.getRepositoryPanel());
//		if (returnVal == JFileChooser.APPROVE_OPTION) {
//			File file = fc.getSelectedFile();
//			textFieldDownloadLocation.setText(file.getAbsolutePath());
//			repositoryService.setDefaultDownloadLocation(repositoryName,
//					textFieldDownloadLocation.getText());
//		} else {
//			textFieldDownloadLocation.setText(null);
//			repositoryService.setDefaultDownloadLocation(repositoryName, null);
//		}
	}
	
	
}
