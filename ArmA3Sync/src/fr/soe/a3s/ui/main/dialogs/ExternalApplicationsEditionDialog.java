package fr.soe.a3s.ui.main.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.dto.configuration.ExternalApplicationDTO;
import fr.soe.a3s.ui.AbstractDialog;
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
public class ExternalApplicationsEditionDialog extends AbstractDialog {

	private final ExternalApplicationDTO externalApplicationDTO;
	private JLabel labelParameters;
	private JButton buttonSelect;
	private JTextArea textAreaParameters;
	private JTextField textFieldExecutablePath;
	private JLabel labelExecutablePath;
	private JTextField textFieldDescription;
	private JLabel labelDescription;

	public ExternalApplicationsEditionDialog(Facade facade,
			ExternalApplicationDTO externalApplicationDTO) {
		super(facade, "External Applications", true);
		this.externalApplicationDTO = externalApplicationDTO;
		this.setResizable(false);

		{
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			getRootPane().setDefaultButton(buttonOK);
		}
		{
			JPanel centerPanel = new JPanel();
			centerPanel.setLayout(new GridBagLayout());
			this.add(centerPanel, BorderLayout.CENTER);
			{
				labelDescription = new JLabel();
				labelDescription.setText("Description:");
				textFieldDescription = new JTextField();
				labelExecutablePath = new JLabel();
				labelExecutablePath.setText("Executable Path:");
				textFieldExecutablePath = new JTextField();
				textFieldExecutablePath.setEnabled(false);
				labelParameters = new JLabel();
				labelParameters.setText("Parameters:");
				textAreaParameters = new JTextArea();
				Font fontTextField = UIManager.getFont("TextField.font");
				textAreaParameters.setFont(new Font(fontTextField.getName(),
						fontTextField.getStyle(), fontTextField.getSize()));
				textAreaParameters.setBorder(BorderFactory
						.createEtchedBorder(BevelBorder.LOWERED));
				textAreaParameters.setLineWrap(true);
				buttonSelect = new JButton();
				buttonSelect.setText("Select");
				{
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.HORIZONTAL;
					c.weightx = 0.5;
					c.weighty = 0;
					c.gridx = 0;
					c.gridy = 0;
					c.insets = new Insets(0, 0, 10, 0);
					centerPanel.add(labelDescription, c);
				}
				{
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.BOTH;
					c.weightx = 0.5;
					c.weighty = 0;
					c.gridx = 1;
					c.gridy = 0;
					c.gridwidth = 2;
					c.insets = new Insets(0, 10, 10, 0);
					centerPanel.add(textFieldDescription, c);
				}
				{
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.HORIZONTAL;
					c.weightx = 0.5;
					c.weighty = 0;
					c.gridx = 0;
					c.gridy = 1;
					c.insets = new Insets(0, 0, 10, 0);
					centerPanel.add(labelExecutablePath, c);
				}
				{
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.BOTH;
					c.weightx = 20;
					c.weighty = 0;
					c.gridx = 1;
					c.gridy = 1;
					c.insets = new Insets(0, 10, 10, 0);
					centerPanel.add(textFieldExecutablePath, c);
				}
				{
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.BOTH;
					c.weightx = 0.5;
					c.weighty = 0;
					c.gridx = 2;
					c.gridy = 1;
					c.insets = new Insets(0, 5, 10, 0);
					centerPanel.add(buttonSelect, c);
				}
				{
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.HORIZONTAL;
					c.weightx = 0.5;
					c.weighty = 0;
					c.gridx = 0;
					c.gridy = 3;
					c.insets = new Insets(0, 0, 10, 0);
					centerPanel.add(labelParameters, c);
				}
				{
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.BOTH;
					c.weightx = 0.5;
					c.weighty = 0;
					c.gridx = 1;
					c.gridy = 3;
					c.gridwidth = 2;
					c.insets = new Insets(0, 10, 0, 0);
					centerPanel.add(textAreaParameters, c);
				}
			}
		}

		this.pack();
		if (textFieldDescription.getBounds().height < 25) {
			textFieldDescription.setPreferredSize(new Dimension(this
					.getBounds().width, 25));
		}
		if (textFieldExecutablePath.getBounds().height < 25) {
			textFieldExecutablePath.setPreferredSize(new Dimension(this
					.getBounds().width, 25));
		}
		if (textAreaParameters.getBounds().height < 25) {
			textAreaParameters.setPreferredSize(new Dimension(
					this.getBounds().width, 25));
		}

		this.setMinimumSize(new Dimension(450, this.getBounds().height));
		this.setPreferredSize(new Dimension(450, this.getBounds().height));
		this.pack();
		setLocationRelativeTo(facade.getMainPanel());

		buttonSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonSelectPerformed();
			}
		});
	}

	public void init() {
		textFieldDescription.setText(externalApplicationDTO.getName());
		textFieldExecutablePath.setText(externalApplicationDTO
				.getExecutablePath());
		textAreaParameters.setText(externalApplicationDTO.getParameters());
	}

	private void buttonSelectPerformed() {
		JFileChooser fc = new JFileChooser();
		fc.setLocale(Locale.ENGLISH);
		int returnVal = fc.showOpenDialog(facade.getMainPanel());

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			textFieldExecutablePath.setText(file.getAbsolutePath());
		}
	}

	@Override
	protected void buttonOKPerformed() {
		externalApplicationDTO.setName(textFieldDescription.getText());
		externalApplicationDTO.setExecutablePath(textFieldExecutablePath
				.getText());
		externalApplicationDTO.setParameters(textAreaParameters.getText());
		this.dispose();
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
