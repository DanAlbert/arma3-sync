package fr.soe.a3s.ui.tools.bikeyEditor;

import java.io.IOException;

import javax.swing.JOptionPane;

import fr.soe.a3s.exception.CheckException;
import fr.soe.a3s.service.CommonService;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ProgressPanel;

public class ExtractProgressPanel extends ProgressPanel {
	
	/* Services */
	private final CommonService commonService = new CommonService();
	private final ConfigurationService configurationService = new ConfigurationService();

	public ExtractProgressPanel(Facade facade) {
		super(facade);
		labelTitle.setText("Extracting *.bikey files...");
	}

	public void init(final String sourceDirectoryPath,final String targetDirectoryPath) {

		progressBar.setIndeterminate(true);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					int nbBikeys = commonService.extractBikeys(sourceDirectoryPath,
							targetDirectoryPath);
					setVisible(false);
					if (canceled){
						JOptionPane.showMessageDialog(facade.getMainPanel(), "Extraction canceled!",
								"Bikey extractor wizard",
								JOptionPane.INFORMATION_MESSAGE);
					}else {
						if (nbBikeys != 0) {
							JOptionPane.showMessageDialog(facade.getMainPanel(), nbBikeys
									+ " " + "have been copied to target directory.",
									"Bikey extractor wizard",
									JOptionPane.INFORMATION_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(facade.getMainPanel(), nbBikeys
									+ " " + "have been found.", "Bikey extractor wizard",
									JOptionPane.WARNING_MESSAGE);
						}
					}
				} catch (CheckException e1) {
					setVisible(false);
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							e1.getMessage(), "Bikey extractor wizard",
							JOptionPane.WARNING_MESSAGE);
				} catch (IOException e2) {
					e2.printStackTrace();
					setVisible(false);
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							e2.getMessage(), "Bikey extractor wizard",
							JOptionPane.ERROR_MESSAGE);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally{
					dispose();
				}
			}
		});
		t.start();
	}

	@Override
	protected void menuExitPerformed() {

		this.setVisible(false);
		this.canceled = true;
		this.commonService.cancel();
		this.dispose();
	}
}
