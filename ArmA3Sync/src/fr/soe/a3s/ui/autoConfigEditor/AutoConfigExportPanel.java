package fr.soe.a3s.ui.autoConfigEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.constant.DefaultProfileName;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.dto.configuration.FavoriteServerDTO;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.service.CommonService;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.service.ProfileService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.CheckBoxList;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

public class AutoConfigExportPanel extends JDialog implements UIConstants {

	private Facade facade;
	private JButton buttonOK;
	private JButton buttonCancel;
	private CheckBoxList checkBoxListProfiles;
	private JScrollPane scrollPane1;
	private CheckBoxList checkBoxListFavoriteServers;
	private JScrollPane scrollPane2;
	private CheckBoxList checkBoxListRepositories;
	private JScrollPane scrollPane3;
	private ProfileService profileService = new ProfileService();
	private ConfigurationService configurationService = new ConfigurationService();
	private RepositoryService repositoryService = new RepositoryService();
	private JTextField textFieldDestinationDirectory;
	private JButton buttonSelect;

	public AutoConfigExportPanel(Facade facade) {
		super(facade.getMainPanel(), "Export auto-config", true);
		this.facade = facade;
		setLocationRelativeTo(facade.getMainPanel());
		this.setResizable(false);
		this.setSize(400, 500);
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

			JPanel profilesPanel = new JPanel();
			profilesPanel.setLayout(new BorderLayout());
			{
				profilesPanel.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(), "Profiles"));
				checkBoxListProfiles = new CheckBoxList();
				scrollPane1 = new JScrollPane(checkBoxListProfiles);
				scrollPane1.setBorder(BorderFactory
						.createEtchedBorder(BevelBorder.LOWERED));
				profilesPanel.add(scrollPane1, BorderLayout.CENTER);
				// centerPanel.add(profilesPanel);
			}
			JPanel favoriteServersPanel = new JPanel();
			favoriteServersPanel.setLayout(new BorderLayout());
			{
				favoriteServersPanel.setBorder(BorderFactory
						.createTitledBorder(BorderFactory.createEtchedBorder(),
								"Favorite servers"));
				checkBoxListFavoriteServers = new CheckBoxList();
				scrollPane2 = new JScrollPane(checkBoxListFavoriteServers);
				scrollPane2.setBorder(BorderFactory
						.createEtchedBorder(BevelBorder.LOWERED));
				favoriteServersPanel.add(scrollPane2, BorderLayout.CENTER);
			}
			JPanel repositoriesPanel = new JPanel();
			repositoriesPanel.setLayout(new BorderLayout());
			{
				repositoriesPanel.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(), "Repositories"));
				checkBoxListRepositories = new CheckBoxList();
				scrollPane3 = new JScrollPane(checkBoxListRepositories);
				scrollPane3.setBorder(BorderFactory
						.createEtchedBorder(BevelBorder.LOWERED));
				repositoriesPanel.add(scrollPane3, BorderLayout.CENTER);
			}
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
			vertBox.add(profilesPanel);
			vertBox.add(Box.createVerticalStrut(5));
			vertBox.add(favoriteServersPanel);
			vertBox.add(Box.createVerticalStrut(5));
			vertBox.add(repositoriesPanel);
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
		buttonSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonSelectPerformed();
			}
		});
		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
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
			public void windowClosing(WindowEvent e) {
				menuExitPerformed();
			}
		});
	}

	public void init() {

		List<String> profileNames = profileService.getProfileNames();

		profileNames.remove(DefaultProfileName.DEFAULT.getDescription());

		JCheckBox[] tab1 = new JCheckBox[profileNames.size()];
		for (int i = 0; i < profileNames.size(); i++) {
			String profileName = profileNames.get(i);
			JCheckBox checkBox = new JCheckBox();
			checkBox.setText(profileNames.get(i));
			tab1[i] = checkBox;
		}
		checkBoxListProfiles.setListData(tab1);

		List<FavoriteServerDTO> favoriteServerDTOs = configurationService
				.getFavoriteServers();

		JCheckBox[] tab2 = new JCheckBox[favoriteServerDTOs.size()];
		for (int i = 0; i < favoriteServerDTOs.size(); i++) {
			JCheckBox checkBox = new JCheckBox();
			checkBox.setText(favoriteServerDTOs.get(i).getName());
			tab2[i] = checkBox;
		}
		checkBoxListFavoriteServers.setListData(tab2);

		List<RepositoryDTO> repositoryDTOs = repositoryService
				.getRepositories();

		JCheckBox[] tab3 = new JCheckBox[repositoryDTOs.size()];
		for (int i = 0; i < repositoryDTOs.size(); i++) {
			JCheckBox checkBox = new JCheckBox();
			checkBox.setText(repositoryDTOs.get(i).getName());
			tab3[i] = checkBox;
		}
		checkBoxListRepositories.setListData(tab3);
	}

	private void buttonSelectPerformed() {

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(AutoConfigExportPanel.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			textFieldDestinationDirectory.setText(path);
		} else {
			textFieldDestinationDirectory.setText("");
		}
	}

	private void buttonOKPerformed() {

		String path = textFieldDestinationDirectory.getText();

		if (path.isEmpty()) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Destination directory is empty.", "Export auto-config",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		List<String> listSelectedProfileNames = new ArrayList<String>();
		List<String> listSelectedFavoriteServerNames = new ArrayList<String>();
		List<String> listSelectedRepositoryNames = new ArrayList<String>();

		List<String> list1 = checkBoxListProfiles.getSelectedItems();
		for (int i = 0; i < list1.size(); i++) {
			listSelectedProfileNames.add(list1.get(i));
		}

		List<String> list2 = checkBoxListFavoriteServers.getSelectedItems();
		for (int i = 0; i < list2.size(); i++) {
			listSelectedFavoriteServerNames.add(list2.get(i));
		}

		List<String> list3 = checkBoxListRepositories.getSelectedItems();
		for (int i = 0; i < list3.size(); i++) {
			listSelectedRepositoryNames.add(list3.get(i));
		}

		if (listSelectedProfileNames.isEmpty()
				&& listSelectedFavoriteServerNames.isEmpty()
				&& listSelectedRepositoryNames.isEmpty()) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"There is nothing to export.", "Export auto-config",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		try {
			CommonService commonService = new CommonService();
			commonService.exportAutoConfig(listSelectedProfileNames,
					listSelectedFavoriteServerNames,
					listSelectedRepositoryNames, path);
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Auto-config file has been exported.",
					"Export auto-config", JOptionPane.INFORMATION_MESSAGE);
			this.dispose();
		} catch (WritingException e) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"An error occured. \n Failed to export auto-config.",
					"Export auto-config", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void buttonCancelPerformed() {
		this.dispose();
	}

	private void menuExitPerformed() {
		this.dispose();
	}
}
