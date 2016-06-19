package fr.soe.a3s.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public abstract class AbstractDialog extends JDialog implements UIConstants {

	protected final Facade facade;
	protected JButton buttonOK;
	protected JButton buttonCancel;
	protected JPanel panelControl;

	public AbstractDialog(Facade facade, String title, boolean modal) {
		super(facade.getMainPanel(), title, modal);
		this.facade = facade;
		setIconImage(ICON);

		this.setLayout(new BorderLayout());
		{
			{
				JPanel sidePanel1 = new JPanel();
				this.add(sidePanel1, BorderLayout.NORTH);
				JPanel sidePanel2 = new JPanel();
				this.add(sidePanel2, BorderLayout.WEST);
				JPanel sidePanel3 = new JPanel();
				this.add(sidePanel3, BorderLayout.EAST);
			}
			{
				Box vBox = Box.createVerticalBox();
				vBox.add(Box.createVerticalStrut(10));
				{
					buttonOK = new JButton("OK");
					buttonCancel = new JButton("Cancel");
					panelControl = new JPanel();
					FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
					panelControl.setLayout(flowLayout);
					panelControl.add(buttonOK);
					panelControl.add(buttonCancel);
					vBox.add(panelControl);
				}
				this.add(vBox, BorderLayout.SOUTH);
			}
		}

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
			@Override
			public void windowClosing(WindowEvent e) {
				menuExitPerformed();
			}
		});
	}

	protected abstract void buttonOKPerformed();

	protected abstract void buttonCancelPerformed();

	protected abstract void menuExitPerformed();
}
