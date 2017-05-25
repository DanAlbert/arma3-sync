package fr.soe.a3s.ui.help;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.constant.DefaultProfileName;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.dto.configuration.FavoriteServerDTO;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.service.CommonService;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.service.ProfileService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.AbstractDialog;
import fr.soe.a3s.ui.CheckBoxList;
import fr.soe.a3s.ui.Facade;

public class AutoConfigExportDialog extends AbstractDialog {

	private CheckBoxList checkBoxListProfiles;
	private JScrollPane scrollPane1;
	private CheckBoxList checkBoxListFavoriteServers;
	private JScrollPane scrollPane2;
	private CheckBoxList checkBoxListRepositories;
	private JScrollPane scrollPane3;
	private JTextField textFieldDestinationDirectory;
	private JButton buttonSelect;
	/* Services */
	private final ProfileService profileService = new ProfileService();
	private final ConfigurationService configurationService = new ConfigurationService();
	private final RepositoryService repositoryService = new RepositoryService();

	public AutoConfigExportDialog(Facade facade) {
		super(facade, "Export auto-config", true);
		this.setResizable(true);

		{
			buttonOK.setPreferredSize(buttonCancel.getPreferredSize());
			getRootPane().setDefaultButton(buttonOK);
		}
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
				textFieldDestinationDirectory.setEditable(false);
				textFieldDestinationDirectory.setBackground(Color.WHITE);
				destinationPanel.add(textFieldDestinationDirectory,
						BorderLayout.CENTER);
				destinationPanel.add(buttonSelect, BorderLayout.EAST);
			}
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

		this.pack();
		if (textFieldDestinationDirectory.getBounds().height < 25) {
			textFieldDestinationDirectory.setPreferredSize(new Dimension(this
					.getBounds().width, 25));
		}
		this.setMinimumSize(new Dimension(450, this.getBounds().height));
		this.setPreferredSize(new Dimension(450, this.getBounds().height));
		this.pack();
		setLocationRelativeTo(facade.getMainPanel());

		buttonSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonSelectPerformed();
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
		int returnVal = fc.showSaveDialog(facade.getMainPanel());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			textFieldDestinationDirectory.setText(path);
		} else {
			textFieldDestinationDirectory.setText("");
		}
	}

	@Override
	protected void buttonOKPerformed() {

		String path = textFieldDestinationDirectory.getText();

		if (path.isEmpty()) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Destination directory is empty.", "Export auto-config",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		File destinationFolder = new File(path);
		if (!destinationFolder.exists()) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Destination directory does not exists.",
					"Export auto-config", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		File file = new File(destinationFolder,DataAccessConstants.AUTOCONFIG_EXPORT_FILE_NAME);
		if (file.exists()){
			int val = JOptionPane.showConfirmDialog(facade.getMainPanel(),
					"The file " + DataAccessConstants.AUTOCONFIG_EXPORT_FILE_NAME + " already exists into destination directory.\nDo you want to overwrite it?",
					"Export auto-config", JOptionPane.ERROR_MESSAGE);
			if (val==1){
				return;
			}
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
					listSelectedRepositoryNames, destinationFolder);
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Auto-config file has been exported.",
					"Export auto-config", JOptionPane.INFORMATION_MESSAGE);
			this.dispose();
		} catch (WritingException e) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Export auto-config",
					JOptionPane.ERROR_MESSAGE);
		}
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
