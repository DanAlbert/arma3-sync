package fr.soe.a3s.ui.repositoryEditor.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.repositoryEditor.DownloadPanel;

public class AdvancedConfigurationPanel2 extends JDialog implements UIConstants {

	private final Facade facade;
	private final DownloadPanel downloadPanel;
	private final String repositoryName;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JComboBox<Integer> comboBoxConnections;
	private JTextField textFieldMaximumDownloadSpeed;
	/* Services */
	private final RepositoryService repositoryService = new RepositoryService();

	public AdvancedConfigurationPanel2(Facade facade, String repositoryName,
			DownloadPanel downloadPanel) {
		super(facade.getMainPanel(), "Advanced configuration", true);
		this.facade = facade;
		this.downloadPanel = downloadPanel;
		this.repositoryName = repositoryName;
		this.setResizable(false);
		this.setSize(350, 165);
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
			buttonOK = new JButton("OK");
			getRootPane().setDefaultButton(buttonOK);
			buttonOK.setPreferredSize(new Dimension(75, 25));
			buttonCancel = new JButton("Cancel");
			buttonCancel.setPreferredSize(new Dimension(75, 25));
			FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
			controlPanel.setLayout(flowLayout);
			controlPanel.add(buttonOK);
			controlPanel.add(buttonCancel);
			this.add(controlPanel, BorderLayout.SOUTH);
			JPanel panelNorth = new JPanel();
			this.add(panelNorth, BorderLayout.NORTH);
			JPanel panelEast = new JPanel();
			this.add(panelEast, BorderLayout.EAST);
			JPanel panelWest = new JPanel();
			this.add(panelWest, BorderLayout.WEST);
		}
		{
			JPanel centerPanel = new JPanel();
			GridLayout grid1 = new GridLayout(1, 1);
			centerPanel.setLayout(grid1);
			centerPanel.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(), "Settings"));
			this.add(centerPanel, BorderLayout.CENTER);
			{
				Box vBox = Box.createVerticalBox();
				centerPanel.add(vBox);
				vBox.add(Box.createVerticalStrut(5));
				{
					JLabel labelConnections = new JLabel();
					labelConnections.setText("Maximum number of connections: ");
					labelConnections.setPreferredSize(new Dimension(250, 25));
					comboBoxConnections = new JComboBox<Integer>();
					comboBoxConnections.setFocusable(false);
					comboBoxConnections.setPreferredSize(new Dimension(50, 25));
					Box hBox = Box.createHorizontalBox();
					hBox.add(Box.createHorizontalStrut(10));
					hBox.add(labelConnections);
					hBox.add(comboBoxConnections);
					hBox.add(Box.createHorizontalGlue());
					vBox.add(hBox);
				}
				vBox.add(Box.createVerticalStrut(10));
				{
					JLabel labelMaximumDownloadSpeed = new JLabel();
					labelMaximumDownloadSpeed
							.setText("Maximum download speed (MB/s): ");
					labelMaximumDownloadSpeed.setPreferredSize(new Dimension(
							250, 23));
					textFieldMaximumDownloadSpeed = new JTextField();
					textFieldMaximumDownloadSpeed
							.setPreferredSize(new Dimension(50, 25));
					Box hBox = Box.createHorizontalBox();
					hBox.add(Box.createHorizontalStrut(10));
					hBox.add(labelMaximumDownloadSpeed);
					hBox.add(textFieldMaximumDownloadSpeed);
					hBox.add(Box.createHorizontalGlue());
					vBox.add(hBox);
				}
				vBox.add(Box.createVerticalStrut(5));
			}
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
		setContextualHelp();
	}

	private void setContextualHelp() {
		textFieldMaximumDownloadSpeed.setToolTipText("0.0 = no speed limit");
	}

	public void init() {

		int numberOfServerInfoConnections = repositoryService
				.getServerInfoNumberOfConnections(repositoryName);
		int numberOfClientConnections = repositoryService
				.getNumberOfClientConnections(repositoryName);

		/* Fill in comboBoxConnections */
		if (numberOfServerInfoConnections == 0) {
			ComboBoxModel comboBoxModel = new DefaultComboBoxModel(
					new Integer[] { 1 });
			comboBoxConnections.setModel(comboBoxModel);
		} else {
			Integer[] tab = new Integer[numberOfServerInfoConnections];
			int entry = 1;
			for (int i = 0; i < numberOfServerInfoConnections; i++) {
				tab[i] = entry;
				entry++;
			}
			ComboBoxModel comboBoxModel = new DefaultComboBoxModel(tab);
			comboBoxConnections.setModel(comboBoxModel);
			comboBoxConnections
					.setMaximumRowCount(numberOfServerInfoConnections);
		}

		/* Set current number of connections */
		if (numberOfClientConnections == 0) {
			comboBoxConnections.setSelectedItem(1);
		} else if (numberOfClientConnections < numberOfServerInfoConnections) {
			comboBoxConnections.setSelectedItem(numberOfClientConnections);
		} else if (numberOfClientConnections >= numberOfServerInfoConnections) {
			comboBoxConnections.setSelectedItem(numberOfServerInfoConnections);
		}

		/* Set current maximum download speed in B/s */
		double maximumClientDownloadSpeed = repositoryService
				.getMaximumClientDownloadSpeed(repositoryName);
		/* MB/s */
		maximumClientDownloadSpeed = maximumClientDownloadSpeed
				/ Math.pow(10, 6);
		textFieldMaximumDownloadSpeed.setText(Double
				.toString(maximumClientDownloadSpeed));
	}

	private void buttonOKPerformed() {

		/* Set Client number of connections */
		int numberOfClientConnections = (Integer) comboBoxConnections
				.getSelectedItem();
		repositoryService.setNumberOfClientConnections(repositoryName,
				numberOfClientConnections);

		/* Set Client maximum download speed */
		try {
			double maximumClientDownloadSpeed = Double
					.parseDouble(textFieldMaximumDownloadSpeed.getText());
			if (maximumClientDownloadSpeed < 0) {
				throw new NumberFormatException();
			} else {
				maximumClientDownloadSpeed = maximumClientDownloadSpeed
						* Math.pow(10, 6);
			}
			repositoryService.setMaximumClientDownloadSpeed(repositoryName,
					maximumClientDownloadSpeed);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this,
					"Download speed must be a positive value.", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		this.dispose();
	}

	private void buttonCancelPerformed() {
		this.dispose();
	}

	private void menuExitPerformed() {
		this.dispose();
	}
}
