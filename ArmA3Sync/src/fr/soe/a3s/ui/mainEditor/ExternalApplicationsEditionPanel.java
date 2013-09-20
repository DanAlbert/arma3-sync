package fr.soe.a3s.ui.mainEditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import fr.soe.a3s.dto.configuration.ExternalApplicationDTO;
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
public class ExternalApplicationsEditionPanel extends JDialog implements
		UIConstants {

	private Facade facade;
	private ExternalApplicationDTO externalApplicationDTO;
	private JLabel labelParameters;
	private JButton buttonSelect;
	private JTextArea textAreaParameters;
	private JTextField textFieldExecutablePath;
	private JLabel labelExecutablePath;
	private JTextField textFieldDescription;
	private JLabel labelDescription;
	private JButton buttonOK, buttonCancel;

	public ExternalApplicationsEditionPanel(Facade facade,
			ExternalApplicationDTO externalApplicationDTO) {
		super(facade.getMainPanel(), "External Applications", true);
		this.facade = facade;
		this.externalApplicationDTO = externalApplicationDTO;

		this.facade.setExternalApplicationsEditionPanel(this);
		this.setSize(450, 300);
		setIconImage(ICON);

		this.setLocation(
				(int) facade.getMainPanel().getLocation().getX()
						+ facade.getMainPanel().getWidth() / 2
						- this.getWidth() / 2,
				(int) facade.getMainPanel().getLocation().getY()
						+ facade.getMainPanel().getHeight() / 2
						- this.getHeight() / 2);

		this.setLayout(new BorderLayout());
		Container contenu = getContentPane();

		{
			JPanel controlPanel = new JPanel();
			buttonOK = new JButton("OK");
			getRootPane().setDefaultButton(buttonOK);
			buttonCancel = new JButton("Cancel");
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
			controlPanel.setLayout(flowLayout);
			controlPanel.add(buttonOK);
			controlPanel.add(buttonCancel);
			contenu.add(controlPanel, BorderLayout.SOUTH);
		}
		{
			JPanel centerPanel = new JPanel();
			contenu.add(centerPanel, BorderLayout.CENTER);
			centerPanel.setLayout(null);
			centerPanel.setPreferredSize(new java.awt.Dimension(379, 230));
			{
				labelDescription = new JLabel();
				centerPanel.add(labelDescription);
				labelDescription.setText("Description:");
				labelDescription.setBounds(40, 22, 70, 25);
			}
			{
				textFieldDescription = new JTextField();
				centerPanel.add(textFieldDescription);
				textFieldDescription.setBounds(116, 21, 237, 25);
			}
			{
				labelExecutablePath = new JLabel();
				centerPanel.add(labelExecutablePath);
				labelExecutablePath.setText("Executable Path:");
				labelExecutablePath.setBounds(17, 59, 93, 25);
			}
			{
				textFieldExecutablePath = new JTextField();
				centerPanel.add(textFieldExecutablePath);
				textFieldExecutablePath.setBounds(116, 57, 237, 25);
				textFieldExecutablePath.setEnabled(false);
			}
			{
				textAreaParameters = new JTextArea();
				centerPanel.add(textAreaParameters);
				textAreaParameters.setBounds(116, 97, 311, 132);
				textAreaParameters.setBorder(BorderFactory
						.createEtchedBorder(BevelBorder.LOWERED));
				textAreaParameters.setFont(new Font("Tohama", Font.ITALIC, 11));
				textAreaParameters.setLineWrap(true);
			}
			{
				buttonSelect = new JButton();
				centerPanel.add(buttonSelect);
				buttonSelect.setText("Select");
				buttonSelect.setBounds(359, 57, 68, 26);
			}
			{
				labelParameters = new JLabel();
				centerPanel.add(labelParameters);
				labelParameters.setText("Parameters:");
				labelParameters.setBounds(41, 96, 69, 25);
			}
		}
		buttonSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonSelectPerformed();
			}
		});
		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonOKPerformed();
			}
		});
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonCancelPerformed();
			}
		});
		// Add Listeners
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				menuExitPerformed();
			}
		});
		init();
	}

	private void init() {
		textFieldDescription.setText(externalApplicationDTO.getName());
		textFieldExecutablePath.setText(externalApplicationDTO
				.getExecutablePath());
		textAreaParameters.setText(externalApplicationDTO.getParameters());
	}

	private void buttonSelectPerformed() {
		JFileChooser fc = new JFileChooser();
		fc.setLocale(Locale.ENGLISH);
		int returnVal = fc
				.showOpenDialog(ExternalApplicationsEditionPanel.this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			textFieldExecutablePath.setText(file.getAbsolutePath());
		}
	}

	private void buttonOKPerformed() {
		externalApplicationDTO.setName(textFieldDescription.getText());
		externalApplicationDTO.setExecutablePath(textFieldExecutablePath
				.getText());
		externalApplicationDTO.setParameters(textAreaParameters.getText());
		this.dispose();
	}

	private void buttonCancelPerformed() {
		this.dispose();
	}

	private void menuExitPerformed() {
		this.dispose();
	}
}
