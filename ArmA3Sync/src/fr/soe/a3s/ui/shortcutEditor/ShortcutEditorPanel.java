package fr.soe.a3s.ui.shortcutEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

public class ShortcutEditorPanel extends JDialog implements UIConstants {

	private final Facade facade;
	private JTextField textFieldDestinationDirectory;
	private JButton buttonSelect;
	private JButton buttonOK;
	private JButton buttonCancel;

	public ShortcutEditorPanel(Facade facade) {
		super(facade.getMainPanel(), "Export as shortcut", true);
		this.facade = facade;
		setLocationRelativeTo(facade.getMainPanel());
		this.setResizable(true);
		this.setMinimumSize(new Dimension(400, 500));
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
			JPanel centerPanel = new JPanel();
			GridLayout grid1 = new GridLayout(1, 1);
			centerPanel.setLayout(grid1);
			this.add(centerPanel, BorderLayout.CENTER);

			JPanel destinationLabelPanel = new JPanel();
			destinationLabelPanel.setLayout((new FlowLayout(FlowLayout.LEFT)));
			{
				JLabel labelDestinationDirectory = new JLabel(
						"Destination directory");
				destinationLabelPanel.add(labelDestinationDirectory);
			}
			JPanel destinationPanel = new JPanel();
			destinationPanel.setLayout(new BorderLayout());
			{
				textFieldDestinationDirectory = new JTextField();
				buttonSelect = new JButton("Select");
				buttonSelect.setPreferredSize(new Dimension(75, 25));
				textFieldDestinationDirectory.setEditable(false);
				textFieldDestinationDirectory.setBackground(Color.WHITE);
				destinationPanel.add(textFieldDestinationDirectory,
						BorderLayout.CENTER);
				destinationPanel.add(buttonSelect, BorderLayout.EAST);
			}
			Box vertBox = Box.createVerticalBox();
			vertBox.add(Box.createVerticalStrut(5));
			vertBox.add(destinationLabelPanel);
			vertBox.add(destinationPanel);
			vertBox.add(Box.createVerticalStrut(5));
			centerPanel.add(vertBox);
		}
		{
			JPanel panelEast = new JPanel();
			this.add(panelEast, BorderLayout.EAST);
			JPanel panelWest = new JPanel();
			this.add(panelWest, BorderLayout.WEST);
		}
		{
			JPanel controlPanel = new JPanel();
			buttonOK = new JButton("OK");
			buttonOK.setPreferredSize(new Dimension(75, 25));
			buttonCancel = new JButton("Cancel");
			buttonCancel.setPreferredSize(new Dimension(75, 25));
			FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
			controlPanel.setLayout(flowLayout);
			controlPanel.add(buttonOK);
			controlPanel.add(buttonCancel);
			this.add(controlPanel, BorderLayout.SOUTH);
		}
		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						buttonOKPerformed();
					}
				});
			}
		});
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
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

	private void buttonOKPerformed() {

	}

	private void buttonCancelPerformed() {
		this.dispose();
	}

	private void menuExitPerformed() {
		this.dispose();
	}
}
