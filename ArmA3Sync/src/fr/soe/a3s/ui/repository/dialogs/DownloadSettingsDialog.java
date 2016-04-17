package fr.soe.a3s.ui.repository.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.soe.a3s.dto.ProtocolDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.AbstractDialog;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.repository.DownloadPanel;

public class DownloadSettingsDialog extends AbstractDialog {

	private final DownloadPanel downloadPanel;
	private final String repositoryName;
	private JComboBox<Integer> comboBoxConnections;
	private JTextField textFieldMaximumDownloadSpeed;
	private JTextField textFieldConnectionTimeout;
	private JTextField textFieldReadTimeout;
	private JLabel labelConnections;
	private JLabel labelMaximumDownloadSpeed;
	private JLabel labelConnectionTimeout;
	private JLabel labelReadTimeout;
	/* Services */
	private final RepositoryService repositoryService = new RepositoryService();

	public DownloadSettingsDialog(Facade facade, String repositoryName,
			DownloadPanel downloadPanel) {
		super(facade, "Download", true);
		this.downloadPanel = downloadPanel;
		this.repositoryName = repositoryName;
		this.setResizable(false);

		{
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			getRootPane().setDefaultButton(buttonOK);
		}
		{
			{
				JPanel panel = new JPanel();
				panel.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(), "Settings"));
				panel.setLayout(new GridBagLayout());
				this.add(panel, BorderLayout.CENTER);
				{
					labelConnections = new JLabel();
					labelConnections.setText("Maximum number of connections:");
					comboBoxConnections = new JComboBox<Integer>();
					comboBoxConnections.setFocusable(false);
				}
				{
					labelMaximumDownloadSpeed = new JLabel();
					labelMaximumDownloadSpeed
							.setText("Maximum download speed (MB/s):");
					textFieldMaximumDownloadSpeed = new JTextField();
				}
				{
					labelConnectionTimeout = new JLabel();
					labelConnectionTimeout
							.setText("Connection timeout (seconds):");
					textFieldConnectionTimeout = new JTextField();
				}
				{
					labelReadTimeout = new JLabel();
					labelReadTimeout.setText("Read timeout (seconds):");
					textFieldReadTimeout = new JTextField();
				}
				{
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.HORIZONTAL;
					c.weightx = 20;
					c.weighty = 0;
					c.gridx = 0;
					c.gridy = 0;
					c.insets = new Insets(5, 10, 5, 10);
					panel.add(labelConnections, c);
				}
				{
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.BOTH;
					c.weightx = 0.5;
					c.weighty = 0;
					c.gridx = 1;
					c.gridy = 0;
					c.insets = new Insets(5, 10, 5, 10);
					panel.add(comboBoxConnections, c);
				}
				{
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.HORIZONTAL;
					c.weightx = 20;
					c.weighty = 0;
					c.gridx = 0;
					c.gridy = 1;
					c.insets = new Insets(5, 10, 5, 10);
					panel.add(labelMaximumDownloadSpeed, c);
				}
				{
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.BOTH;
					c.weightx = 0.5;
					c.weighty = 0;
					c.gridx = 1;
					c.gridy = 1;
					c.insets = new Insets(5, 10, 5, 10);
					panel.add(textFieldMaximumDownloadSpeed, c);
				}
				{
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.HORIZONTAL;
					c.weightx = 20;
					c.weighty = 0;
					c.gridx = 0;
					c.gridy = 2;
					c.insets = new Insets(5, 10, 5, 10);
					panel.add(labelConnectionTimeout, c);
				}
				{
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.BOTH;
					c.weightx = 0.5;
					c.weighty = 0;
					c.gridx = 1;
					c.gridy = 2;
					c.insets = new Insets(5, 10, 5, 10);
					panel.add(textFieldConnectionTimeout, c);
				}
				{
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.HORIZONTAL;
					c.weightx = 20;
					c.weighty = 0;
					c.gridx = 0;
					c.gridy = 3;
					c.insets = new Insets(5, 10, 5, 10);
					panel.add(labelReadTimeout, c);
				}
				{
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.BOTH;
					c.weightx = 0.5;
					c.weighty = 0;
					c.gridx = 1;
					c.gridy = 3;
					c.insets = new Insets(5, 10, 5, 10);
					panel.add(textFieldReadTimeout, c);
				}
			}
		}

		this.pack();
		this.setLocationRelativeTo(facade.getMainPanel());
		setContextualHelp();
	}

	private void setContextualHelp() {
		textFieldMaximumDownloadSpeed.setToolTipText("0 = no speed limit");
		textFieldConnectionTimeout.setToolTipText("0 = no time limit");
		textFieldReadTimeout.setToolTipText("0 = no time limit");
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
		maximumClientDownloadSpeed = maximumClientDownloadSpeed
				/ Math.pow(10, 6);// MB/s
		textFieldMaximumDownloadSpeed.setText(Double
				.toString(maximumClientDownloadSpeed));

		try {
			/* Set Connection and Read timeout */
			RepositoryDTO repositoryDTO = repositoryService
					.getRepository(repositoryName);
			ProtocolDTO protocolDTO = repositoryDTO.getProtocoleDTO();
			int connectionTimeout = Integer.parseInt(protocolDTO
					.getConnectionTimeOut());// ms
			textFieldConnectionTimeout.setText(Integer
					.toString((int) (connectionTimeout / Math.pow(10, 3))));
			int readTimeout = Integer.parseInt(protocolDTO.getReadTimeOut());// ms
			textFieldReadTimeout.setText(Integer
					.toString((int) (readTimeout / Math.pow(10, 3))));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* Adapt view size */
		this.pack();
		int width = this.getBounds().width;
		if (comboBoxConnections.getBounds().height < 25) {
			comboBoxConnections.setPreferredSize(new Dimension(width, 25));
		}
		this.setMinimumSize(new Dimension(width, this.getBounds().height));
		this.setPreferredSize(new Dimension(width, this.getBounds().height));
		this.pack();
	}

	@Override
	protected void buttonOKPerformed() {

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

		/* Set Client Connection timeout */
		try {
			int connectionTimeout = Integer.parseInt(textFieldConnectionTimeout
					.getText());
			if (connectionTimeout < 0) {
				throw new NumberFormatException();
			} else {
				connectionTimeout = (int) (connectionTimeout * Math.pow(10, 3));
			}
			repositoryService.setConnectionTimeout(repositoryName,
					Integer.toString(connectionTimeout));
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this,
					"Connection timeout must be a positive integer value.",
					"Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}

		/* Set Client Read timeout */
		try {
			int readTimeout = Integer.parseInt(textFieldReadTimeout.getText());
			if (readTimeout < 0) {
				throw new NumberFormatException();
			} else {
				readTimeout = (int) (readTimeout * Math.pow(10, 3));
			}
			repositoryService.setReadTimeout(repositoryName,
					Integer.toString(readTimeout));
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this,
					"Read timeout must be a positive integer value.",
					"Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}

		/* Save repository to disk */
		try {
			repositoryService.write(repositoryName);
		} catch (WritingException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

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
