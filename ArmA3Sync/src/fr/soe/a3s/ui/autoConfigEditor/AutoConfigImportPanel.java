package fr.soe.a3s.ui.autoConfigEditor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import fr.soe.a3s.exception.LoadingException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.service.CommonService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.ExtensionFilter;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class AutoConfigImportPanel extends JDialog implements UIConstants {

	private final Facade facade;
	private JTextField textFieldImportAutoConfig;
	private JButton buttonOK;
	private JButton buttonSelect;
	private JButton buttonCancel;
	private JLabel labelImportAutoConfig;

	public AutoConfigImportPanel(Facade facade) {
		super(facade.getMainPanel(), "Import auto-config", true);
		this.facade = facade;
		setLocationRelativeTo(facade.getMainPanel());
		this.setResizable(false);
		this.setSize(400, 130);
		setIconImage(ICON);
		this.setLocation(
				(int) facade.getMainPanel().getLocation().getX()
						+ facade.getMainPanel().getWidth() / 2
						- this.getWidth() / 2,
				(int) facade.getMainPanel().getLocation().getY()
						+ facade.getMainPanel().getHeight() / 2
						- this.getHeight() / 2);

		this.setLayout(null);
		{
			textFieldImportAutoConfig = new JTextField();
			this.add(textFieldImportAutoConfig);
			textFieldImportAutoConfig.requestFocusInWindow();
			textFieldImportAutoConfig.setBounds(11, 35, 299, 23);
			textFieldImportAutoConfig.setEditable(false);
			textFieldImportAutoConfig.setBackground(Color.WHITE);
		}
		{
			buttonOK = new JButton();
			getContentPane().add(buttonOK);
			buttonOK.setText("OK");
			buttonOK.setBounds(155, 64, 75, 25);
			getRootPane().setDefaultButton(buttonOK);
		}
		{
			buttonCancel = new JButton();
			getContentPane().add(buttonCancel);
			buttonCancel.setText("Cancel");
			buttonCancel.setBounds(235, 64, 75, 25);
		}
		{
			labelImportAutoConfig = new JLabel();
			getContentPane().add(labelImportAutoConfig);
			labelImportAutoConfig
					.setText("Select *.a3s.autoconfig file to import");
			labelImportAutoConfig.setBounds(12, 12, 239, 17);
		}
		{
			buttonSelect = new JButton();
			getContentPane().add(buttonSelect);
			buttonSelect.setText("Select");
			buttonSelect.setBounds(316, 34, 75, 25);
		}
		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						buttonOKPerformed();
					}
				});
			}
		});
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonCancelPerformed();
			}
		});
		buttonSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonSelectPerformed();
			}
		});
	}

	private void buttonOKPerformed() {

		String path = textFieldImportAutoConfig.getText();

		if (path.isEmpty()) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Import directory is empty.", "Import auto-config",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		try {
			CommonService commonService = new CommonService();
			commonService.importAutoConfig(path);
			RepositoryService repositoryService = new RepositoryService();
			repositoryService.writeAll();
			this.dispose();
			this.facade.getMainPanel().updateProfilesMenu();
			this.facade.getSyncPanel().init();
			this.facade.getOnlinePanel().init();
			this.facade.getLaunchPanel().init();
		} catch (LoadingException e) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"An error occured. \n Failed to import auto-config.",
					"Import auto-config", JOptionPane.ERROR_MESSAGE);
		} catch (WritingException e) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void buttonCancelPerformed() {
		this.dispose();
	}

	private void buttonSelectPerformed() {

		JFileChooser fc = new JFileChooser();
		FileFilter currentFileFilter = fc.getFileFilter();
		if (currentFileFilter != null) {
			fc.removeChoosableFileFilter(currentFileFilter);
		}
		FileFilter typeMission = new ExtensionFilter("ArmA3Sync auto-config ("
				+ "a3s.autoconfig" + ")", ".a3s.autoconfig");
		fc.addChoosableFileFilter(typeMission);
		int returnVal = fc.showOpenDialog(AutoConfigImportPanel.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			textFieldImportAutoConfig.setText(path);
		} else {
			textFieldImportAutoConfig.setText("");
		}
	}
}
