package fr.soe.a3s.ui.repositoryEditor.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dto.configuration.FavoriteServerDTO;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.CheckBoxList;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

public class BuildRepositoryOptionsPanel extends JDialog implements UIConstants,
		DataAccessConstants {

	private final Facade facade;
	private JList excludedFilesFromBuildList;
	private JScrollPane scrollPane1;
	private JList excludedFoldersFromSyncList;
	private JScrollPane scrollPane2;
	private CheckBoxList checkBoxListFavoriteServers;
	private JScrollPane scrollPane3;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JButton buttonAdd1;
	private JButton buttonRemove1;
	private JButton buttonAdd2;
	private JButton buttonRemove2;
	private JButton buttonAdd3;
	private JButton buttonRemove3;
	private final String repositoryName;
	private JComboBox<Integer> comboBoxConnections;
	private JComboBox<String> comboBoxCompression;
	private JComboBox<String> comboBoxPartialFileTransfer;
	private JLabel labelWholeFileDownload;
	/* Services */
	private final ConfigurationService configurationService = new ConfigurationService();
	private final RepositoryService repositoryService = new RepositoryService();

	public BuildRepositoryOptionsPanel(Facade facade, String repositoryName) {
		super(facade.getMainPanel(), "Build options", true);
		this.facade = facade;
		this.repositoryName = repositoryName;
		setLocationRelativeTo(facade.getMainPanel());
		this.setResizable(true);
		this.setMinimumSize(new Dimension(500, 600));
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
			this.add(centerPanel, BorderLayout.CENTER);
			{
				JPanel pan = new JPanel();
				pan.setLayout(new BorderLayout());
				{
					JPanel optionsPanel = new JPanel();
					optionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
					optionsPanel.setBorder(BorderFactory.createTitledBorder(
							BorderFactory.createEtchedBorder(), "Options"));
					pan.add(optionsPanel, BorderLayout.CENTER);
					Box vBox = Box.createVerticalBox();
					optionsPanel.add(vBox);
					{
						JLabel labelConnections = new JLabel();
						labelConnections
								.setText("Set maximum number of connections per client:");
						labelConnections
								.setPreferredSize(new Dimension(330, 23));
						comboBoxConnections = new JComboBox<Integer>();
						ComboBoxModel comboBoxModel = new DefaultComboBoxModel(
								new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
						comboBoxConnections.setModel(comboBoxModel);
						comboBoxConnections.setFocusable(false);
						comboBoxConnections.setMaximumRowCount(10);
						comboBoxConnections.setPreferredSize(new Dimension(50,
								25));
						Box hBox = Box.createHorizontalBox();
						hBox.add(Box.createHorizontalStrut(10));
						hBox.add(labelConnections);
						hBox.add(comboBoxConnections);
						hBox.add(Box.createHorizontalGlue());
						vBox.add(hBox);
					}
					vBox.add(Box.createVerticalStrut(5));
					{
						JLabel labelCompression = new JLabel();
						labelCompression.setText("Add compressed pbo files ("
								+ DataAccessConstants.ZIP_EXTENSION + "):");
						labelCompression
								.setPreferredSize(new Dimension(330, 23));
						comboBoxCompression = new JComboBox<String>();
						ComboBoxModel comboBoxModel = new DefaultComboBoxModel(
								new String[] { "Yes", "No" });
						comboBoxCompression.setModel(comboBoxModel);
						comboBoxCompression.setFocusable(false);
						comboBoxCompression.setMaximumRowCount(2);
						comboBoxCompression.setPreferredSize(new Dimension(50,
								25));
						Box hBox = Box.createHorizontalBox();
						hBox.add(Box.createHorizontalStrut(10));
						hBox.add(labelCompression);
						hBox.add(comboBoxCompression);
						hBox.add(Box.createHorizontalGlue());
						vBox.add(hBox);
					}
					vBox.add(Box.createVerticalStrut(5));
					{
						labelWholeFileDownload = new JLabel();
						labelWholeFileDownload
								.setText("Use HTTP partial file transfer (recommended):");
						labelWholeFileDownload.setPreferredSize(new Dimension(
								330, 23));
						comboBoxPartialFileTransfer = new JComboBox<String>();
						ComboBoxModel comboBoxModel = new DefaultComboBoxModel(
								new String[] { "Yes", "No" });
						comboBoxPartialFileTransfer.setModel(comboBoxModel);
						comboBoxPartialFileTransfer.setFocusable(false);
						comboBoxPartialFileTransfer.setMaximumRowCount(2);
						comboBoxPartialFileTransfer
								.setPreferredSize(new Dimension(50, 25));
						Box hBox = Box.createHorizontalBox();
						hBox.add(Box.createHorizontalStrut(10));
						hBox.add(labelWholeFileDownload);
						hBox.add(comboBoxPartialFileTransfer);
						hBox.add(Box.createHorizontalGlue());
						vBox.add(hBox);
					}
					vBox.add(Box.createVerticalStrut(5));

					JPanel panel = new JPanel();
					buttonAdd1 = new JButton("");
					ImageIcon addIcon = new ImageIcon(ADD);
					buttonAdd1.setIcon(addIcon);
					panel.setPreferredSize(new Dimension(buttonAdd1
							.getPreferredSize()));
					pan.add(panel, BorderLayout.EAST);
				}

				JPanel favoriteServersPanel = new JPanel();
				favoriteServersPanel.setLayout(new BorderLayout());
				{
					JPanel panel = new JPanel();
					panel.setLayout(new BorderLayout());
					panel.setBorder(BorderFactory.createTitledBorder(
							BorderFactory.createEtchedBorder(),
							"Favorite servers infos set to autoconfig"));
					checkBoxListFavoriteServers = new CheckBoxList();
					scrollPane1 = new JScrollPane(checkBoxListFavoriteServers);
					scrollPane1.setBorder(BorderFactory
							.createEtchedBorder(BevelBorder.LOWERED));
					panel.add(scrollPane1, BorderLayout.CENTER);
					favoriteServersPanel.add(panel, BorderLayout.CENTER);
				}
				{
					JPanel panel = new JPanel();
					buttonAdd1 = new JButton("");
					ImageIcon addIcon = new ImageIcon(ADD);
					buttonAdd1.setIcon(addIcon);
					panel.setPreferredSize(new Dimension(buttonAdd1
							.getPreferredSize()));
					favoriteServersPanel.add(panel, BorderLayout.EAST);
				}

				JPanel excludedFilesPanel = new JPanel();
				excludedFilesPanel.setLayout(new BorderLayout());
				{
					JPanel panel = new JPanel();
					panel.setLayout(new BorderLayout());
					panel.setBorder(BorderFactory.createTitledBorder(
							BorderFactory.createEtchedBorder(),
							"Repository files excluded from build"));
					excludedFilesFromBuildList = new JList();
					scrollPane2 = new JScrollPane(excludedFilesFromBuildList);
					scrollPane2.setBorder(BorderFactory
							.createEtchedBorder(BevelBorder.LOWERED));
					panel.add(scrollPane2, BorderLayout.CENTER);
					excludedFilesPanel.add(panel, BorderLayout.CENTER);
				}
				{
					Box vertBox = Box.createVerticalBox();
					vertBox.add(Box.createVerticalStrut(15));
					buttonAdd2 = new JButton("");
					ImageIcon addIcon = new ImageIcon(ADD);
					buttonAdd2.setIcon(addIcon);
					vertBox.add(buttonAdd2);
					buttonRemove2 = new JButton("");
					ImageIcon deleteIcon = new ImageIcon(DELETE);
					buttonRemove2.setIcon(deleteIcon);
					vertBox.add(buttonRemove2);
					vertBox.add(Box.createVerticalStrut(60));
					excludedFilesPanel.add(vertBox, BorderLayout.EAST);
				}

				JPanel excludedFoldersWithExtraLocalContentPanel = new JPanel();
				excludedFoldersWithExtraLocalContentPanel
						.setLayout(new BorderLayout());
				{
					JPanel panel = new JPanel();
					panel.setLayout(new BorderLayout());
					panel.setBorder(BorderFactory.createTitledBorder(
							BorderFactory.createEtchedBorder(),
							"Repository folders with excluded extra local content when sync"));
					excludedFoldersFromSyncList = new JList();
					scrollPane3 = new JScrollPane(excludedFoldersFromSyncList);
					scrollPane3.setBorder(BorderFactory
							.createEtchedBorder(BevelBorder.LOWERED));
					panel.add(scrollPane3, BorderLayout.CENTER);
					excludedFoldersWithExtraLocalContentPanel.add(panel,
							BorderLayout.CENTER);
				}
				{
					Box vertBox = Box.createVerticalBox();
					vertBox.add(Box.createVerticalStrut(15));
					buttonAdd3 = new JButton("");
					ImageIcon addIcon = new ImageIcon(ADD);
					buttonAdd3.setIcon(addIcon);
					vertBox.add(buttonAdd3);
					buttonRemove3 = new JButton("");
					ImageIcon deleteIcon = new ImageIcon(DELETE);
					buttonRemove3.setIcon(deleteIcon);
					vertBox.add(buttonRemove3);
					vertBox.add(Box.createVerticalStrut(60));
					excludedFoldersWithExtraLocalContentPanel.add(vertBox,
							BorderLayout.EAST);
				}

				Box vertBox = Box.createVerticalBox();
				vertBox.add(pan);
				vertBox.add(Box.createVerticalStrut(5));
				vertBox.add(favoriteServersPanel);
				vertBox.add(Box.createVerticalStrut(5));
				vertBox.add(excludedFilesPanel);
				vertBox.add(Box.createVerticalStrut(5));
				vertBox.add(excludedFoldersWithExtraLocalContentPanel);
				vertBox.add(Box.createVerticalStrut(5));
				centerPanel.add(vertBox);
			}
		}

		buttonAdd2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonAdd2Performed();
			}
		});
		buttonRemove2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonRemove2Performed();
			}
		});
		buttonAdd3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonAdd3Performed();
			}
		});
		buttonRemove3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonRemove3Performed();
			}
		});
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

	public void init() {

		// Client connections
		int numberOfConnections = repositoryService
				.getNumberOfConnections(repositoryName);
		if (numberOfConnections == 0) {
			comboBoxConnections.setSelectedIndex(0);
		} else {
			comboBoxConnections.setSelectedItem(numberOfConnections);
		}

		// Compression
		boolean compression = repositoryService.isCompressed(repositoryName);
		if (compression) {
			comboBoxCompression.setSelectedIndex(0);// Yes
		} else {
			comboBoxCompression.setSelectedIndex(1);
		}

		// Partial files transfer
		try {
			ProtocolType protocolType = repositoryService
					.getRepository(repositoryName).getProtocoleDTO()
					.getProtocolType();
			if (protocolType != null) {
				if (protocolType.equals(ProtocolType.HTTP)
						|| protocolType.equals(ProtocolType.HTTPS)) {
					boolean usePartialFileTransfer = repositoryService
							.isUsePartialFileTransfer(repositoryName);
					if (usePartialFileTransfer) {
						comboBoxPartialFileTransfer.setSelectedIndex(0);// Yes
					} else {
						comboBoxPartialFileTransfer.setSelectedIndex(1);
					}
				} else {
					labelWholeFileDownload.setEnabled(false);
					comboBoxPartialFileTransfer.setSelectedIndex(0);// Yes
					comboBoxPartialFileTransfer.setEnabled(false);
				}
			}
		} catch (RepositoryException e) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}

		// Favorite servers
		List<FavoriteServerDTO> favoriteServerDTOs = configurationService
				.getFavoriteServers();

		List<FavoriteServerDTO> list = repositoryService
				.getFavoriteServerSetToAutoconfig(repositoryName);

		JCheckBox[] tab2 = new JCheckBox[favoriteServerDTOs.size()];
		for (int i = 0; i < favoriteServerDTOs.size(); i++) {
			String name = favoriteServerDTOs.get(i).getName();
			JCheckBox checkBox = new JCheckBox();
			checkBox.setText(name);
			for (FavoriteServerDTO f : list) {
				if (f.getName().equals(name)) {
					checkBox.setSelected(true);
					break;
				}
			}
			tab2[i] = checkBox;
		}
		checkBoxListFavoriteServers.setListData(tab2);

		// Excluded files from build
		Collection<String> excludedFilesFromBuildList = repositoryService
				.getExcludedFilesPathFromBuild(repositoryName);
		updateExcludedFilesFromBuild(excludedFilesFromBuildList);

		// Excluded extra local folder content from sync
		Collection<String> excludedFoldersFromSyncList = repositoryService
				.getExcludedFoldersFromSync(repositoryName);
		updateExcludedFoldersFromSync(excludedFoldersFromSyncList);
	}

	public void updateExcludedFilesFromBuild(Collection<String> list) {

		String[] paths = new String[list.size()];
		Iterator iter = list.iterator();
		int i = 0;
		while (iter.hasNext()) {
			paths[i] = (String) iter.next();
			i++;
		}

		excludedFilesFromBuildList.clearSelection();
		excludedFilesFromBuildList.setListData(paths);
		int numberLigneShown = list.size();
		excludedFilesFromBuildList.setVisibleRowCount(numberLigneShown);
		excludedFilesFromBuildList.setPreferredSize(excludedFilesFromBuildList
				.getPreferredScrollableViewportSize());
		scrollPane2.updateUI();
	}

	private void updateExcludedFoldersFromSync(Collection<String> list) {

		String[] paths = new String[list.size()];
		Iterator iter = list.iterator();
		int i = 0;
		while (iter.hasNext()) {
			paths[i] = (String) iter.next();
			i++;
		}

		excludedFoldersFromSyncList.clearSelection();
		excludedFoldersFromSyncList.setListData(paths);
		int numberLigneShown = list.size();
		excludedFoldersFromSyncList.setVisibleRowCount(numberLigneShown);
		excludedFoldersFromSyncList
				.setPreferredSize(excludedFoldersFromSyncList
						.getPreferredScrollableViewportSize());
		scrollPane3.updateUI();
	}

	private void buttonAdd2Performed() {

		JFileChooser fc = new JFileChooser(
				repositoryService.getRepositoryPath(repositoryName));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showOpenDialog(BuildRepositoryOptionsPanel.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (file != null) {
				String path = file.getAbsolutePath();
				int size = excludedFilesFromBuildList.getModel().getSize();
				List<String> list = new ArrayList<String>();
				for (int i = 0; i < size; i++) {
					list.add((String) excludedFilesFromBuildList.getModel()
							.getElementAt(i));
				}
				boolean contains = false;
				for (int i = 0; i < list.size(); i++) {
					String osName = System.getProperty("os.name");
					if (osName.contains("Windows")) {
						if (path.equalsIgnoreCase(list.get(i))) {
							contains = true;
						}
					} else {
						if (path.equals(list.get(i))) {
							contains = true;
						}
					}
				}

				if (!contains) {
					list.add(path);
					updateExcludedFilesFromBuild(list);
				}
			}
		}
	}

	private void buttonRemove2Performed() {

		List<String> paths = excludedFilesFromBuildList.getSelectedValuesList();

		if (paths != null) {
			int size = excludedFilesFromBuildList.getModel().getSize();
			Collection<String> list = new ArrayList<String>();
			for (int i = 0; i < size; i++) {
				list.add((String) excludedFilesFromBuildList.getModel()
						.getElementAt(i));
			}
			for (String path : paths) {
				list.remove(path);
			}
			updateExcludedFilesFromBuild(list);
		}
	}

	private void buttonAdd3Performed() {

		JFileChooser fc = new JFileChooser(
				repositoryService.getRepositoryPath(repositoryName));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(BuildRepositoryOptionsPanel.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (file != null) {
				String path = file.getAbsolutePath();
				int size = excludedFoldersFromSyncList.getModel().getSize();
				List<String> list = new ArrayList<String>();
				for (int i = 0; i < size; i++) {
					list.add((String) excludedFoldersFromSyncList.getModel()
							.getElementAt(i));
				}
				boolean contains = false;
				for (int i = 0; i < list.size(); i++) {
					String osName = System.getProperty("os.name");
					if (osName.contains("Windows")) {
						if (path.equalsIgnoreCase(list.get(i))) {
							contains = true;
						}
					} else {
						if (path.equals(list.get(i))) {
							contains = true;
						}
					}
				}

				if (!contains) {
					list.add(path);
					updateExcludedFoldersFromSync(list);
				}
				list.add(path);
			}
		}
	}

	private void buttonRemove3Performed() {

		List<String> paths = excludedFoldersFromSyncList
				.getSelectedValuesList();

		if (paths != null) {
			int size = excludedFoldersFromSyncList.getModel().getSize();
			Collection<String> list = new ArrayList<String>();
			for (int i = 0; i < size; i++) {
				list.add((String) excludedFoldersFromSyncList.getModel()
						.getElementAt(i));
			}
			for (String path : paths) {
				list.remove(path);
			}
			updateExcludedFoldersFromSync(list);
		}
	}

	private void buttonOKPerformed() {

		// Set number of connections
		repositoryService.setNumberOfConnections(repositoryName,
				(int) comboBoxConnections.getSelectedItem());

		// Set files compression
		int compression = comboBoxCompression.getSelectedIndex();
		if (compression == 0) {// Yes
			repositoryService.setCompressed(repositoryName, true);
		} else {
			repositoryService.setCompressed(repositoryName, false);
		}

		// Set partial file transfer
		int usePartialFileTransfer = comboBoxPartialFileTransfer
				.getSelectedIndex();
		if (usePartialFileTransfer == 0) {// Yes
			repositoryService.setUsePartialFileTransfer(repositoryName, true);
		} else {
			repositoryService.setUsePartialFileTransfer(repositoryName, false);
		}

		// Set favorite servers
		List<FavoriteServerDTO> favoriteServerDTOs = configurationService
				.getFavoriteServers();

		List<FavoriteServerDTO> selectedServerDTOs = new ArrayList<FavoriteServerDTO>();

		List<String> list = checkBoxListFavoriteServers.getSelectedItems();
		for (int i = 0; i < list.size(); i++) {
			String name = list.get(i);
			for (FavoriteServerDTO f : favoriteServerDTOs) {
				if (f.getName().equals(name)) {
					selectedServerDTOs.add(f);
					break;
				}
			}
		}

		repositoryService.setFavoriteServerToAutoconfig(repositoryName,
				selectedServerDTOs);

		// Set excluded files from build
		int size = excludedFilesFromBuildList.getModel().getSize();
		list = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			list.add((String) excludedFilesFromBuildList.getModel()
					.getElementAt(i));
		}
		repositoryService.setExcludedFilesPathFromBuild(repositoryName, list);

		// Set excluded folders from sync
		size = excludedFoldersFromSyncList.getModel().getSize();
		list = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			list.add((String) excludedFoldersFromSyncList.getModel()
					.getElementAt(i));
		}
		repositoryService.setExcludedFoldersFromSync(repositoryName, list);

		try {
			repositoryService.write(repositoryName);
			this.dispose();
		} catch (WritingException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void buttonCancelPerformed() {
		this.dispose();
	}

	private void menuExitPerformed() {
		this.dispose();
	}
}
