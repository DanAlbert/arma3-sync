package fr.soe.a3s.ui.repositoryEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

public abstract class ProgressPanel extends JDialog implements UIConstants {
	
	protected Facade facade;
	protected JLabel labelTitle;
	protected JButton buttonCancel;
	protected JProgressBar progressBar;
	protected boolean canceled = false;

	public ProgressPanel(Facade facade){
		super(facade.getMainPanel(), "Repository", false);
		
		this.facade = facade;
		this.setResizable(false);
		this.setSize(200, 120);
		setIconImage(ICON);
		this.setLocation(
				(int) facade.getMainPanel().getLocation().getX()
						+ facade.getMainPanel().getWidth() / 2
						- this.getWidth() / 2,
				(int) facade.getMainPanel().getLocation().getY()
						+ facade.getMainPanel().getHeight() / 2
						- this.getHeight() / 2);
		this.setLayout(new BorderLayout());
		{
			JPanel controlPanel = new JPanel();
			buttonCancel = new JButton("Cancel");
			FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
			controlPanel.setLayout(flowLayout);
			controlPanel.add(buttonCancel);
			this.add(controlPanel, BorderLayout.SOUTH);
		}
		{
			JPanel sidePanel1 = new JPanel();
			this.add(sidePanel1, BorderLayout.EAST);
			JPanel sidePanel2 = new JPanel();
			this.add(sidePanel2, BorderLayout.WEST);
		}

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(null);
		this.add(centerPanel, BorderLayout.CENTER);
		{
			labelTitle = new JLabel();
			centerPanel.add(labelTitle, BorderLayout.CENTER);
			labelTitle.setBounds(0, 9, 180, 28);
		}
		{
			progressBar = new JProgressBar();
			centerPanel.add(progressBar);
			progressBar.setBounds(0, 36, 174, 16);
		}
		getRootPane().setDefaultButton(buttonCancel);
	}
}
