package fr.soe.a3s.ui.help;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import fr.soe.a3s.service.CommonService;
import fr.soe.a3s.ui.AbstractDialog;
import fr.soe.a3s.ui.ExtensionFilter;
import fr.soe.a3s.ui.Facade;

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
public class AutoConfigImportDialog extends AbstractDialog {

	private JButton buttonSelect;
	private JTextField textField;

	public AutoConfigImportDialog(Facade facade) {
		super(facade, "Import auto-config", true);
		this.setResizable(false);

		{
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			getRootPane().setDefaultButton(buttonOK);
		}
		{
			Box vBox = Box.createVerticalBox();
			this.add(vBox, BorderLayout.CENTER);
			{
				JPanel panel = new JPanel();
				panel.setLayout(new FlowLayout(FlowLayout.LEFT));
				JLabel label = new JLabel(
						"Please select *.a3s.autoconfig file to import");
				panel.add(label);
				vBox.add(panel);
			}
			{
				JPanel panel = new JPanel();
				panel.setLayout(new BorderLayout());
				textField = new JTextField();
				buttonSelect = new JButton("Select");
				textField.setEditable(false);
				textField.setBackground(Color.WHITE);
				panel.add(textField, BorderLayout.CENTER);
				panel.add(buttonSelect, BorderLayout.EAST);
				vBox.add(panel);
			}
		}

		buttonSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonSelectPerformed();
			}
		});
		this.pack();
		if (textField.getBounds().height < 25) {
			textField
					.setPreferredSize(new Dimension(this.getBounds().width, 25));
		}
		this.setMinimumSize(new Dimension(350, this.getBounds().height));
		this.setPreferredSize(new Dimension(350, this.getBounds().height));
		this.pack();
		setLocationRelativeTo(facade.getMainPanel());
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
		int returnVal = fc.showOpenDialog(facade.getMainPanel());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			textField.setText(path);
		} else {
			textField.setText("");
		}
	}

	@Override
	protected void buttonOKPerformed() {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				String path = textField.getText();

				if (path.isEmpty()) {
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							"Import file path empty.", "Import auto-config",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				File file = new File(path);

				if (!file.exists()) {
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							"Import file does not exits.",
							"Import auto-config", JOptionPane.ERROR_MESSAGE);
					return;
				}

				try {
					CommonService commonService = new CommonService();
					commonService.importAutoConfig(file);
					dispose();
					facade.getMainPanel().updateTabs(OP_PROFILE_CHANGED);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(facade.getMainPanel(),
							e.getMessage(), "Import auto-config",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	@Override
	protected void buttonCancelPerformed() {
		this.dispose();
	}

	@Override
	protected void menuExitPerformed() {
		this.dispose();
	}
}
