package fr.soe.a3s.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public abstract class AbstractProgressDialog extends JDialog implements
		UIConstants {

	protected Facade facade;
	protected JLabel labelTitle;
	protected JButton buttonCancel;
	protected JProgressBar progressBar;
	protected boolean canceled = false;

	public AbstractProgressDialog(Facade facade, String text) {
		super(facade.getMainPanel(), "Progress", false);

		this.facade = facade;
		this.setResizable(false);
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
					buttonCancel = new JButton("Cancel");
					JPanel panel = new JPanel();
					FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
					panel.setLayout(flowLayout);
					panel.add(buttonCancel);
					vBox.add(panel);
				}
				this.add(vBox, BorderLayout.SOUTH);
			}
			{
				JPanel panel = new JPanel();
				panel.setLayout(new BorderLayout());
				{
					labelTitle = new JLabel();
					labelTitle.setText(text);
					progressBar = new JProgressBar();
					panel.add(labelTitle, BorderLayout.NORTH);
					panel.add(progressBar, BorderLayout.CENTER);
				}
				this.add(panel, BorderLayout.CENTER);
			}
		}

		this.progressBar.setPreferredSize(new Dimension(this.progressBar
				.getPreferredSize().width, 18));
		pack();
		this.setMinimumSize(new Dimension(250, this.getBounds().height));
		this.setPreferredSize(new Dimension(250, this.getBounds().height));

		setLocationRelativeTo(facade.getMainPanel());
		getRootPane().setDefaultButton(buttonCancel);

		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuExitPerformed();
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

	protected abstract void menuExitPerformed();
}
