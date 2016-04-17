package fr.soe.a3s.ui.repository.events;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.soe.a3s.ui.AbstractDialog;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repository.EventsPanel;

public abstract class AbstractEventDialog extends AbstractDialog {

	protected EventsPanel eventsPanel;
	protected String repositoryName;
	protected JTextField textFieldEventName;
	private JLabel labelEventName;
	private JLabel labelEventDescription;
	protected JTextField textFieldDescription;
	protected JLabel labelWarning;

	public AbstractEventDialog(Facade facade, String repositoryName,
			EventsPanel eventsPanel) {
		super(facade, null, true);
		this.eventsPanel = eventsPanel;
		this.repositoryName = repositoryName;
		this.setResizable(false);

		{
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			getRootPane().setDefaultButton(buttonOK);
		}
		{
			Box vBox = Box.createVerticalBox();
			this.add(vBox, BorderLayout.CENTER);
			{
				JPanel panel1 = new JPanel();
				panel1.setLayout(new GridBagLayout());
				vBox.add(panel1);
				{
					labelEventName = new JLabel();
					labelEventName.setText("Event name");
					labelWarning = new JLabel();
					textFieldEventName = new JTextField();
					textFieldEventName.requestFocusInWindow();
					{
						GridBagConstraints c1 = new GridBagConstraints();
						c1.fill = GridBagConstraints.HORIZONTAL;
						c1.weightx = 0.5;
						c1.weighty = 0;
						c1.gridx = 0;
						c1.gridy = 0;
						c1.gridwidth = 1;
						c1.gridheight = 1;
						panel1.add(labelEventName, c1);
					}
					{
						GridBagConstraints c2 = new GridBagConstraints();
						c2.fill = GridBagConstraints.HORIZONTAL;
						c2.weightx = 0.5;
						c2.weighty = 0;
						c2.gridx = 1;
						c2.gridy = 0;
						c2.gridwidth = 1;
						c2.gridheight = 1;
						panel1.add(labelWarning, c2);
					}
					{
						GridBagConstraints c3 = new GridBagConstraints();
						c3.fill = GridBagConstraints.BOTH;
						c3.weightx = 0;
						c3.weighty = 0;
						c3.gridx = 0;
						c3.gridy = 1;
						c3.gridwidth = 2;
						c3.gridheight = 1;
						panel1.add(textFieldEventName, c3);
					}
				}

				vBox.add(Box.createVerticalStrut(10));

				JPanel panel2 = new JPanel();
				panel2.setLayout(new GridBagLayout());
				vBox.add(panel2);
				{
					labelEventDescription = new JLabel();
					labelEventDescription.setText("Event description");
					textFieldDescription = new JTextField();
					{
						GridBagConstraints c1 = new GridBagConstraints();
						c1.fill = GridBagConstraints.HORIZONTAL;
						c1.weightx = 0.5;
						c1.weighty = 0;
						c1.gridx = 0;
						c1.gridy = 0;
						c1.gridwidth = 1;
						c1.gridheight = 1;
						panel2.add(labelEventDescription, c1);
					}
					{
						GridBagConstraints c3 = new GridBagConstraints();
						c3.fill = GridBagConstraints.BOTH;
						c3.weightx = 0;
						c3.weighty = 0;
						c3.gridx = 0;
						c3.gridy = 1;
						c3.gridwidth = 2;
						c3.gridheight = 1;
						panel2.add(textFieldDescription, c3);
					}
				}
			}
		}

		this.pack();
		this.setMinimumSize(new Dimension(250, this.getSize().height));
		this.setPreferredSize(new Dimension(250, this.getSize().height));
		this.pack();
		setLocationRelativeTo(facade.getMainPanel());
	}
}
