package fr.soe.a3s.ui.mainEditor.groups;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

public abstract class AddonsEditGroupPanel extends JDialog implements UIConstants{

	protected Facade facade;
	protected JTextField textFieldGroupName;
	protected JButton buttonOK;
	protected JLabel labelGroupName;
	protected JButton buttonCancel;
	protected JLabel labelWarning;
	
	public AddonsEditGroupPanel(Facade facade) {
		super(facade.getMainPanel());
		this.facade = facade;
		setLocationRelativeTo(facade.getMainPanel());
		this.setResizable(false);
		this.setSize(232, 130);
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
			textFieldGroupName = new JTextField();
			this.add(textFieldGroupName);
			textFieldGroupName.requestFocusInWindow();
			textFieldGroupName.setBounds(11, 35, 203, 23);
		}
		{
			buttonOK = new JButton();
			getContentPane().add(buttonOK);
			buttonOK.setText("OK");
			buttonOK.setBounds(78, 64, 67, 25);
			getRootPane().setDefaultButton(buttonOK);
		}
		{
			buttonCancel = new JButton();
			getContentPane().add(buttonCancel);
			buttonCancel.setText("Cancel");
			buttonCancel.setBounds(150, 64, 65, 25);
		}
		{
			labelGroupName = new JLabel();
			getContentPane().add(labelGroupName);
			labelGroupName.setText("Group name");
			labelGroupName.setBounds(12, 12, 77, 17);
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
				String groupName = textFieldGroupName.getText().trim();
				if (!groupName.isEmpty()){
					buttonOKPerformed(groupName);
				}
			}
		});
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				menuExitPerformed();
			}
		});
	}
	
	protected abstract void buttonOKPerformed(String groupName);
	
	private void menuExitPerformed() {
		this.dispose();
	}

}
