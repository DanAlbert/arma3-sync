package fr.soe.a3s.ui.repositoryEditor.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.repositoryEditor.EventsPanel;

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
public abstract class EventEditPanel extends JDialog implements UIConstants {

	protected Facade facade;
	protected EventsPanel eventsPanel;
	protected String repositoryName;
	protected JTextField textFieldEventName;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JLabel labelEventName;
	private JLabel labelEventDescription;
	protected JTextField textFieldDescription;
	protected JLabel labelWarning;
	protected RepositoryService repositoryService = new RepositoryService();

	public EventEditPanel(Facade facade, String repositoryName,
			EventsPanel eventsPanel) {
		super(facade.getMainPanel());
		this.facade = facade;
		this.eventsPanel = eventsPanel;
		this.repositoryName = repositoryName;
		setLocationRelativeTo(facade.getMainPanel());
		this.setResizable(false);
		this.setSize(310, 185);
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
			textFieldEventName = new JTextField();
			this.add(textFieldEventName);
			textFieldEventName.requestFocusInWindow();
			textFieldEventName.setBounds(6, 35, 286, 23);
		}
		{
			buttonOK = new JButton();
			getContentPane().add(buttonOK);
			buttonOK.setText("OK");
			buttonOK.setBounds(153, 124, 75, 25);
			getRootPane().setDefaultButton(buttonOK);
		}
		{
			buttonCancel = new JButton();
			getContentPane().add(buttonCancel);
			buttonCancel.setText("Cancel");
			buttonCancel.setBounds(225, 124, 75, 25);
		}
		{
			labelEventName = new JLabel();
			getContentPane().add(labelEventName);
			labelEventName.setText("Event name");
			labelEventName.setBounds(6, 18, 79, 17);
		}
		{
			labelWarning = new JLabel();
			getContentPane().add(labelWarning);
			labelWarning.setBounds(95, 12, 153, 17);
		}
		{
			labelEventDescription = new JLabel();
			getContentPane().add(labelEventDescription);
			labelEventDescription.setBounds(6, 70, 131, 19);
			labelEventDescription.setText("Event description");
		}
		{
			textFieldDescription = new JTextField();
			getContentPane().add(textFieldDescription);
			textFieldDescription.setBounds(6, 89, 286, 23);
		}

		// Add Listeners
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				menuExitPerformed();
			}
		});
		buttonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String newEventName = textFieldEventName.getText().trim();
				String description = textFieldDescription.getText().trim();
				if (!newEventName.isEmpty()) {
					buttonOKPerformed(newEventName, description);
				}
			}
		});
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				menuExitPerformed();
			}
		});

	}

	public abstract void buttonOKPerformed(String newEventName,
			String description);

	private void menuExitPerformed() {
		this.dispose();
	}

}
