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

public abstract class EventEditPanel extends JDialog implements UIConstants {

	protected Facade facade;
	protected String repositoryName;
	protected JTextField textFieldEventName;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JLabel labelEventName;
	protected JLabel labelWarning;
	protected RepositoryService repositoryService = new RepositoryService();

	public EventEditPanel(Facade facade, String repositoryName) {
		super(facade.getMainPanel());
		this.facade = facade;
		this.repositoryName = repositoryName;
		setLocationRelativeTo(facade.getMainPanel());
		this.setResizable(false);
		this.setSize(393, 130);
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
			textFieldEventName.setBounds(11, 35, 364, 23);
		}
		{
			buttonOK = new JButton();
			getContentPane().add(buttonOK);
			buttonOK.setText("OK");
			buttonOK.setBounds(239, 64, 67, 25);
			getRootPane().setDefaultButton(buttonOK);
		}
		{
			buttonCancel = new JButton();
			getContentPane().add(buttonCancel);
			buttonCancel.setText("Cancel");
			buttonCancel.setBounds(311, 64, 65, 25);
		}
		{
			labelEventName = new JLabel();
			getContentPane().add(labelEventName);
			labelEventName.setText("Event name");
			labelEventName.setBounds(12, 12, 77, 17);
		}
		{
			labelWarning = new JLabel();
			getContentPane().add(labelWarning);
			labelWarning.setBounds(95, 12, 119, 17);
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
				if (!newEventName.isEmpty()) {
					buttonOKPerformed(newEventName);
				}
			}
		});
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				menuExitPerformed();
			}
		});
	}

	public abstract void buttonOKPerformed(String newEventName);

	private void menuExitPerformed(){
		this.dispose();
	}
}
