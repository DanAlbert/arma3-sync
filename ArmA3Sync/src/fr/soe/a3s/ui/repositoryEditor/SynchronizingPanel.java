package fr.soe.a3s.ui.repositoryEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.service.FtpService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;

public class SynchronizingPanel extends ProgressPanel {

	private FtpService ftpService = new FtpService();
	private RepositoryService repositoryService = new RepositoryService();

	public SynchronizingPanel(Facade facade) {
		super(facade);
		labelTitle.setText("Checking repository status...");

		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuExitPerformed();
			}
		});
		// Add Listeners
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				menuExitPerformed();
			}
		});
	}

	public void init() {

		facade.getSyncPanel().getButtonSync().setEnabled(false);
		progressBar.setIndeterminate(true);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				List<RepositoryDTO> list = repositoryService.getRepositories();
				for (final RepositoryDTO repositoryDTO : list) {
					if (canceled) {
						break;
					}
					try {
						ftpService.checkRepository(repositoryDTO.getName());
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
				facade.getSyncPanel().init();
				progressBar.setIndeterminate(false);
				dispose();
				facade.getSyncPanel().getButtonSync().setEnabled(true);
			}
		});
		t.start();

	}

	private void menuExitPerformed() {
		this.setVisible(false);
		canceled = true;
		ftpService.disconnect();
		this.dispose();
	}

}
