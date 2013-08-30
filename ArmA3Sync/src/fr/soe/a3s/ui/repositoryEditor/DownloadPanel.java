package fr.soe.a3s.ui.repositoryEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.net.nntp.NewsgroupInfo;

import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.dto.TreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.service.LaunchService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.repositoryEditor.tree.AddonSyncTreeModel;
import fr.soe.a3s.ui.repositoryEditor.tree.CheckTreeCellRendererRepository;
import fr.soe.a3s.ui.repositoryEditor.tree.MyRendererRepository;
import fr.soe.a3s.ui.repositoryEditor.workers.AddonsChecker;
import fr.soe.a3s.ui.repositoryEditor.workers.AddonsDownloader;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class DownloadPanel extends JPanel implements UIConstants {

	private Facade facade;
	private JProgressBar progressBarCheckForAddons, progressBarDownloadAddons,
			progressBarDownloadSingleAddon;
	private AddonSyncTreeModel addonSyncTreeModel;
	private SyncTreeDirectoryDTO racine;
	private JTree arbre;
	private JScrollPane tableScrollPane;
	private JLabel labelTotalFilesSize, labelTotalFilesSizeValue;
	private JLabel labelTotalFilesUpdatedValue, labeltTotalFilesDeletedValue;
	private JLabel labelSpeed;
	private JLabel labelSpeedValue;
	private JLabel labelDownloaded;
	private JLabel labelDownloadedValue;
	private JLabel labelRemainingTime;
	private JLabel labelRemainingTimeValue;
	private JLabel labelDownloadStatus;
	private JButton buttonCheckForAddonsStart, buttonCheckForAddonsCancel;
	private JButton buttonDownloadStart, buttonDownloadPause,
			buttonDownloadCancel;
	private JComboBox comBoxDestinationFolder;
	private String repositoryName;
	private JLabel labelCheckForAddonsStatus;
	private RepositoryService repositoryService = new RepositoryService();
	private ConfigurationService configurationService = new ConfigurationService();
	private TreePath arbreTreePath;
	private long totalFilesSize;
	private int totalFilesSelected;
	private int totalFilesUpdated;
	private int totalFilesDeleted;
	private JButton buttonAdvancedConfigurationPerformed;
	private AddonsChecker addonsChecker;
	private AddonsDownloader addonsDownloader;
	private JCheckBox checkBoxSelectAll;
	private JCheckBox checkBoxExpandAll;
	private JCheckBox checkBoxUpdated;
	private String eventName;

	public DownloadPanel(Facade facade) {

		this.facade = facade;
		this.facade.setDownloadPanel(this);
		this.setLayout(new BorderLayout());

		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				panel1, panel2);
		splitPane.setOneTouchExpandable(false);
		splitPane.setDividerLocation(190);
		flattenSplitPane(splitPane);
		this.add(splitPane, BorderLayout.CENTER);
		panel1.setLayout(new BorderLayout());
		Box vBox = Box.createVerticalBox();
		panel1.add(vBox, BorderLayout.NORTH);
		{
			JPanel checkForAddonsLabelPanel = new JPanel();
			checkForAddonsLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel labelCheckForAddons = new JLabel("Check for Addons");
			labelCheckForAddons.setFont(new Font("Tohama", Font.PLAIN, 11));
			checkForAddonsLabelPanel.add(labelCheckForAddons);
			labelCheckForAddonsStatus = new JLabel();
			labelCheckForAddonsStatus.setFont(new Font("Tohama", Font.ITALIC,
					11));
			labelCheckForAddonsStatus.setForeground(new Color(45, 125, 45));
			checkForAddonsLabelPanel.add(labelCheckForAddonsStatus);
			vBox.add(checkForAddonsLabelPanel);
		}
		{
			JPanel checkForAddonsProgressBarPanel = new JPanel();
			checkForAddonsProgressBarPanel.setPreferredSize(new Dimension(20,
					20));
			checkForAddonsProgressBarPanel.setLayout(new BorderLayout());
			progressBarCheckForAddons = new JProgressBar();
			checkForAddonsProgressBarPanel.add(progressBarCheckForAddons,
					BorderLayout.CENTER);
			vBox.add(checkForAddonsProgressBarPanel);
		}
		{
			JPanel checkForAddonsControls = new JPanel();
			checkForAddonsControls.setLayout(new BorderLayout());
			buttonCheckForAddonsStart = new JButton("");
			buttonCheckForAddonsCancel = new JButton("");
			Box hBox = Box.createHorizontalBox();
			hBox.add(buttonCheckForAddonsStart);
			hBox.add(buttonCheckForAddonsCancel);
			ImageIcon addIcon = new ImageIcon(CHECK);
			buttonCheckForAddonsStart.setIcon(addIcon);
			ImageIcon cancelIcon = new ImageIcon(DELETE);
			buttonCheckForAddonsCancel.setIcon(cancelIcon);
			checkForAddonsControls.add(hBox, BorderLayout.EAST);
			vBox.add(checkForAddonsControls);
		}
		vBox.add(Box.createVerticalStrut(20));
		{
			JPanel filesPanel = new JPanel();
			filesPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			filesPanel.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(), "Repository changes"));
			vBox.add(filesPanel);
			Box vBox2 = Box.createVerticalBox();
			filesPanel.add(vBox2);
			{
				JLabel totalFilesUpdated = new JLabel("Total files updated: ");
				labelTotalFilesUpdatedValue = new JLabel();
				Box hBox = Box.createHorizontalBox();
				hBox.add(totalFilesUpdated);
				hBox.add(labelTotalFilesUpdatedValue);
				hBox.add(Box.createHorizontalGlue());
				vBox2.add(hBox);
			}
			vBox2.add(Box.createVerticalStrut(10));
			{
				JLabel totalFilesDeleted = new JLabel("Total files deleted : ");
				labeltTotalFilesDeletedValue = new JLabel();
				Box hBox = Box.createHorizontalBox();
				hBox.add(totalFilesDeleted);
				hBox.add(labeltTotalFilesDeletedValue);
				hBox.add(Box.createHorizontalGlue());
				vBox2.add(hBox);
			}
		}
		vBox.add(Box.createVerticalStrut(20));
		{
			JPanel optionsPanel = new JPanel();
			optionsPanel.setLayout(new BorderLayout());
			buttonAdvancedConfigurationPerformed = new JButton(
					"Advanced configuration");
			ImageIcon addIcon = new ImageIcon(OPTIONS);
			buttonAdvancedConfigurationPerformed.setIcon(addIcon);
			optionsPanel.add(buttonAdvancedConfigurationPerformed);
			vBox.add(optionsPanel);

		}
		vBox.add(Box.createVerticalStrut(10));
		{
			JPanel downloadLabelPanel = new JPanel();
			downloadLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel labelDownload = new JLabel("Download Addons");
			labelDownload.setFont(new Font("Tohama", Font.PLAIN, 11));
			downloadLabelPanel.add(labelDownload);
			labelDownloadStatus = new JLabel();
			labelDownloadStatus.setFont(new Font("Tohama", Font.ITALIC, 11));
			labelDownloadStatus.setForeground(new Color(45, 125, 45));
			downloadLabelPanel.add(labelDownloadStatus);
			vBox.add(downloadLabelPanel);
		}
		{
			JPanel downloadProgressBarPanel = new JPanel();
			downloadProgressBarPanel.setPreferredSize(new Dimension(20, 20));
			downloadProgressBarPanel.setLayout(new BorderLayout());
			progressBarDownloadAddons = new JProgressBar();
			downloadProgressBarPanel.add(progressBarDownloadAddons,
					BorderLayout.CENTER);
			vBox.add(downloadProgressBarPanel);
		}
		{
			JPanel downloadProgressBarPanel = new JPanel();
			downloadProgressBarPanel.setPreferredSize(new Dimension(20, 20));
			downloadProgressBarPanel.setLayout(new BorderLayout());
			progressBarDownloadSingleAddon = new JProgressBar();
			downloadProgressBarPanel.add(progressBarDownloadSingleAddon,
					BorderLayout.CENTER);
			vBox.add(downloadProgressBarPanel);
		}
		{
			JPanel downloadControls = new JPanel();
			downloadControls.setLayout(new BorderLayout());
			buttonDownloadStart = new JButton();
			buttonDownloadPause = new JButton();
			buttonDownloadCancel = new JButton();
			Box hBox = Box.createHorizontalBox();
			hBox.add(buttonDownloadStart);
			hBox.add(buttonDownloadPause);
			hBox.add(buttonDownloadCancel);
			ImageIcon addIcon = new ImageIcon(START);
			buttonDownloadStart.setIcon(addIcon);
			ImageIcon pauseIcon = new ImageIcon(PAUSE);
			buttonDownloadPause.setIcon(pauseIcon);
			ImageIcon cancelIcon = new ImageIcon(DELETE);
			buttonDownloadCancel.setIcon(cancelIcon);
			downloadControls.add(hBox, BorderLayout.EAST);
			vBox.add(downloadControls);
		}
		vBox.add(Box.createVerticalStrut(20));

		JPanel progressPanel = new JPanel();
		progressPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		progressPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Progress monitor"));
		panel1.add(progressPanel, BorderLayout.CENTER);
		vBox = Box.createVerticalBox();
		progressPanel.add(vBox, BorderLayout.NORTH);
		{
			labelTotalFilesSize = new JLabel("Total files size: ");
			labelTotalFilesSizeValue = new JLabel();
			Box hBox = Box.createHorizontalBox();
			hBox.add(labelTotalFilesSize);
			hBox.add(labelTotalFilesSizeValue);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
			vBox.add(Box.createVerticalStrut(10));
		}
		{
			labelDownloaded = new JLabel("Downloaded: ");
			labelDownloadedValue = new JLabel();
			Box hBox = Box.createHorizontalBox();
			hBox.add(labelDownloaded);
			hBox.add(labelDownloadedValue);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
			vBox.add(Box.createVerticalStrut(10));
		}
		{
			labelSpeed = new JLabel("Speed: ");
			labelSpeedValue = new JLabel();
			Box hBox = Box.createHorizontalBox();
			hBox.add(labelSpeed);
			hBox.add(labelSpeedValue);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
			vBox.add(Box.createVerticalStrut(10));
		}
		{
			labelRemainingTime = new JLabel("Remaining time: ");
			labelRemainingTimeValue = new JLabel();
			Box hBox = Box.createHorizontalBox();
			hBox.add(labelRemainingTime);
			hBox.add(labelRemainingTimeValue);
			hBox.add(Box.createHorizontalGlue());
			vBox.add(hBox);
			vBox.add(Box.createVerticalStrut(10));
		}

		panel2.setLayout(new BorderLayout());
		vBox = Box.createVerticalBox();
		panel2.add(vBox, BorderLayout.NORTH);
		{
			JPanel defaultDownloadLocationPanel = new JPanel();
			defaultDownloadLocationPanel.setLayout(new FlowLayout(
					FlowLayout.LEFT));
			JLabel labelDefaultDestinationDirectoryPanel = new JLabel(
					"Default destination folder");
			defaultDownloadLocationPanel
					.add(labelDefaultDestinationDirectoryPanel);
			vBox.add(defaultDownloadLocationPanel);
		}
		{
			JPanel locationPanel = new JPanel();
			locationPanel.setLayout(new BorderLayout());
			comBoxDestinationFolder = new JComboBox();
			comBoxDestinationFolder.setPreferredSize(new Dimension(this
					.getWidth(), 22));
			locationPanel.add(comBoxDestinationFolder, BorderLayout.CENTER);
			vBox.add(locationPanel);
			vBox.add(Box.createVerticalStrut(5));
		}
		{
			JPanel selectionPanel = new JPanel();
			selectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			checkBoxSelectAll = new JCheckBox("Select All");
			checkBoxExpandAll = new JCheckBox("Expand All");
			selectionPanel.add(checkBoxSelectAll);
			selectionPanel.add(checkBoxExpandAll);
			vBox.add(selectionPanel);
		}
		{
			JPanel addonsPanel = new JPanel();
			addonsPanel.setLayout(new BorderLayout());
			addonSyncTreeModel = new AddonSyncTreeModel(racine);
			arbre = new JTree(addonSyncTreeModel);
			arbre.setRootVisible(false);
			arbre.setEditable(false);
			arbre.setShowsRootHandles(true);
			arbre.setToggleClickCount(0);
			arbre.getSelectionModel().setSelectionMode(
					TreeSelectionModel.SINGLE_TREE_SELECTION);
			tableScrollPane = new JScrollPane(arbre);
			addonsPanel.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(), "Repository content"));
			addonsPanel.add(tableScrollPane, BorderLayout.CENTER);
			MyRendererRepository myRendererRepository = new MyRendererRepository();
			arbre.setCellRenderer(myRendererRepository);
			CheckTreeCellRendererRepository renderer = new CheckTreeCellRendererRepository(
					myRendererRepository);
			arbre.setCellRenderer(renderer);
			panel2.add(addonsPanel, BorderLayout.CENTER);
		}
		checkBoxSelectAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxSelectAllPerformed();
			}
		});
		checkBoxExpandAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBoxExpandAllPerformed();
			}
		});
		arbre.addTreeExpansionListener(new TreeExpansionListener() {
			public void treeExpanded(TreeExpansionEvent event) {
				onArbre2Expanded();
			}

			public void treeCollapsed(TreeExpansionEvent event) {
				onArbre2Collapsed();
			}
		});
		arbre.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent arg0) {
				arbreTreePath = arbre.getSelectionPath();
				//System.out.println(arbreTreePath);
			}
		});
		arbre.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (arbreTreePath == null) {
					return;
				}
				if (SwingUtilities.isRightMouseButton(e)){
					return;
				}
				int hotspot = new JCheckBox().getPreferredSize().width;

				TreePath path = arbre.getPathForLocation(e.getX(), e.getY());
				if (path == null) {
					return;
				} else if (e.getX() > arbre.getPathBounds(path).x + hotspot) {
					return;
				}

				SyncTreeNodeDTO syncTreeNodeDTO = (SyncTreeNodeDTO) arbre
						.getLastSelectedPathComponent();
				syncTreeNodeDTO.setSelected(!syncTreeNodeDTO.isSelected());
				SyncTreeDirectoryDTO parent = syncTreeNodeDTO.getParent();
				if (syncTreeNodeDTO.isSelected()) {
					selectAllAscending(parent);
					if (!syncTreeNodeDTO.isLeaf()) {
						SyncTreeDirectoryDTO syncTreeDirectoryDTO = (SyncTreeDirectoryDTO) syncTreeNodeDTO;
						for (SyncTreeNodeDTO t : syncTreeDirectoryDTO.getList()) {
							selectAllDescending(t);
						}
					}
				} else {
					if (!syncTreeNodeDTO.isLeaf()) {
						SyncTreeDirectoryDTO syncTreeDirectoryDTO = (SyncTreeDirectoryDTO) syncTreeNodeDTO;
						for (SyncTreeNodeDTO t : syncTreeDirectoryDTO.getList()) {
							deselectAllDescending(t);
						}
					}
					int nbNodesSelected = 0;
					for (SyncTreeNodeDTO t : parent.getList()) {
						if (t.isSelected()) {
							nbNodesSelected++;
						}
					}
					if (nbNodesSelected == 0) {
						unselectAllAscending(parent);
					}
				}

				if (!repositoryService.isDownloading()) {
					totalFilesSize = 0;
					compute(racine);
					labelTotalFilesSizeValue.setText(UnitConverter
							.convertSize(totalFilesSize));
				}

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						arbre.updateUI();
					}
				});
			}
		});
		comBoxDestinationFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				comBoxDestinationFolderPerformed();
			}
		});
		buttonCheckForAddonsStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonCheckForAddonsStartPerformed();
			}
		});
		buttonCheckForAddonsCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonCheckForAddonsCancelPerformed();
			}
		});
		buttonAdvancedConfigurationPerformed
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						buttonAdvancedConfigurationPerformed();
					}
				});
		buttonDownloadStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonDownloadStartPerformed();
			}
		});
		buttonDownloadPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonDownloadPausePerformed();
			}
		});
		buttonDownloadCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonDownloadCancelPerformed();
			}
		});
		setContextualHelp();
	}

	private void setContextualHelp() {
		buttonCheckForAddonsStart.setToolTipText("Check for Addons");
		buttonCheckForAddonsCancel.setToolTipText("Cancel");
		buttonDownloadStart.setToolTipText("Start/Resume");
		buttonDownloadPause.setToolTipText("Pause");
		buttonDownloadCancel.setToolTipText("Cancel");
	}

	public void init(String repositoryName) {
		this.repositoryName = repositoryName;
		updateDefaultFolderDestination();
	}

	public void init(String repositoryName, String eventName) {
		this.repositoryName = repositoryName;
		this.eventName = eventName;
		updateDefaultFolderDestination();
		// Lock user action on addons tree
		arbre.setEnabled(false);
		// Check addons repository
		addonsChecker = new AddonsChecker(facade, repositoryName, eventName);
		addonsChecker.start();
	}

	public void updateDefaultFolderDestination() {

		Set<String> directories = configurationService
				.getAddonSearchDirectoryPaths();
		Iterator iter = directories.iterator();
		String[] tab = new String[directories.size()];
		int i = 0;
		while (iter.hasNext()) {
			tab[i] = (String) iter.next();
			i++;
		}
		ComboBoxModel model = new DefaultComboBoxModel(tab);
		comBoxDestinationFolder.setModel(model);
		String path = repositoryService
				.getDefaultDownloadLocation(repositoryName);

		if (path == null && model.getSize() > 0) {
			comBoxDestinationFolder.setSelectedIndex(0);
		} else {
			comBoxDestinationFolder.setSelectedItem(path);
		}

	}

	private void comBoxDestinationFolderPerformed() {

		String defaultDownloadLocation = (String) comBoxDestinationFolder
				.getSelectedItem();
		repositoryService.setDefaultDownloadLocation(repositoryName,
				defaultDownloadLocation);
	}

	private void buttonCheckForAddonsStartPerformed() {
	    
	    if (comBoxDestinationFolder.getSelectedItem() == null) {
	            JOptionPane
	                    .showMessageDialog(
	                            facade.getMainPanel(),
	                            "A default destination folder must be set. \n Please checkout Addon Options panel.",
	                            "Download", JOptionPane.WARNING_MESSAGE);
	            return;
	     }

		// Lock user action on addons tree
		arbre.setEnabled(false);

		// Repository status changed to ok
		repositoryService.updateRepositoryRevision(repositoryName);
		repositoryService.setOutOfSync(repositoryName, false);
		try {
			repositoryService.write(repositoryName);
		} catch (WritingException e) {
		}
		facade.getAdminPanel().init(repositoryName);
		facade.getSyncPanel().init();

		// Check addons repository
		addonsChecker = new AddonsChecker(facade, repositoryName, eventName);
		addonsChecker.start();
	}

	private void buttonCheckForAddonsCancelPerformed() {

		if (addonsChecker != null) {
			try {
				addonsChecker.join();
				addonsChecker.interrupt();
			} catch (Exception e) {
			}

			labelCheckForAddonsStatus.setText("Canceled");
			buttonCheckForAddonsStart.setEnabled(true);
			progressBarCheckForAddons.setMaximum(0);
			arbre.setEnabled(true);
			arbre.removeAll();
			labelTotalFilesSizeValue.setText("");
			labelTotalFilesUpdatedValue.setText("");
			labeltTotalFilesDeletedValue.setText("");
			addonSyncTreeModel = new AddonSyncTreeModel(
					new SyncTreeDirectoryDTO());
			arbre.setModel(addonSyncTreeModel);
			int numberRowShown = arbre.getRowCount();
			arbre.setVisibleRowCount(numberRowShown);
			arbre.setPreferredSize(arbre.getPreferredScrollableViewportSize());
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					arbre.updateUI();
				}
			});
		}
	}

	private void buttonDownloadStartPerformed() {

		if (repositoryService.isDownloading(repositoryName)) {
			return;
		}

		if (totalFilesSelected == 0) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"There is no addon selected.", "Download",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		LaunchService launchService = new LaunchService();
		if (launchService.isArmA3Running()) {
			int ok = JOptionPane
					.showConfirmDialog(
							facade.getMainPanel(),
							"ArmA 3 is currently running. \n "
									+ "Updating will failed if selected files are in use by the game.",
							"Download", JOptionPane.WARNING_MESSAGE);
			if (ok != 0) {
				return;
			}
		}

		if (comBoxDestinationFolder.getSelectedItem() == null) {
			JOptionPane
					.showMessageDialog(
							facade.getMainPanel(),
							"A default destination folder must be set. \n Please checkout Addon Options panel.",
							"Download", JOptionPane.WARNING_MESSAGE);
			return;
		} else {
			String defaultDownloadLocation = (String) comBoxDestinationFolder
					.getSelectedItem();
			repositoryService.setDefaultDownloadLocation(repositoryName,
					defaultDownloadLocation);
		}

		addonsDownloader = new AddonsDownloader(facade, repositoryName, racine,
				totalFilesSize,eventName);
		addonsDownloader.setDaemon(true);
		addonsDownloader.start();
	}

	private void buttonDownloadPausePerformed() {

		if (repositoryService.isDownloading(repositoryName)) {
			addonsDownloader.pause();
			addonsDownloader.interrupt();
			addonsDownloader = null;
			repositoryService.setDownloading(repositoryName, false);
		}
		buttonDownloadStart.setEnabled(true);
	}

	private void buttonDownloadCancelPerformed() {

		if (repositoryService.isDownloading(repositoryName)) {
			addonsDownloader.cancel();
			addonsDownloader.interrupt();
			addonsDownloader = null;
			repositoryService.setDownloading(repositoryName, false);
		}else {
			facade.getDownloadPanel().getLabelDownloadStatus()
					.setText("Canceled!");
			repositoryService.saveDownloadParameters(repositoryName,0,0,false);
		}
		buttonDownloadStart.setEnabled(true);
	}

	private void buttonAdvancedConfigurationPerformed() {

		if (racine == null) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Please check for addons first.", "Advanced configuration",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		String defaultDestinationPath = repositoryService.getDefaultDownloadLocation(repositoryName);
		
		AdvancedConfigurationPanel advancedConfigurationPanel = new AdvancedConfigurationPanel(
				facade, racine,defaultDestinationPath);
		advancedConfigurationPanel.setVisible(true);
		buttonAdvancedConfigurationPerformed.setEnabled(false);
	}

	private void checkBoxSelectAllPerformed() {

		if (racine == null) {
			return;
		}

		if (checkBoxSelectAll.isSelected()) {
			selectAllDescending(racine);
		} else {
			deselectAllDescending(racine);
		}
		totalFilesSize = 0;
		compute(racine);
		labelTotalFilesSizeValue.setText(UnitConverter
				.convertSize(totalFilesSize));
		refreshViewArbre();
	}

	private void checkBoxExpandAllPerformed() {

		if (racine == null) {
			return;
		}

		Set<TreePath> paths = new HashSet<TreePath>();
		if (checkBoxExpandAll.isSelected()) {
			getPathDirectories(new TreePath(arbre.getModel().getRoot()), paths);
			for (TreePath treePath : paths) {
				arbre.expandPath(treePath);
			}
		} else {
			TreePath rootPath = new TreePath(arbre.getModel().getRoot());
			for (SyncTreeNodeDTO child : racine.getList()) {
				paths.add(rootPath.pathByAddingChild(child));
			}
			for (TreePath treePath : paths) {
				arbre.collapsePath(treePath);
			}
		}
	}

	private void getPathDirectories(TreePath path, Set<TreePath> paths) {

		if (path != null) {
			SyncTreeNodeDTO syncTreeNodeDTO = (SyncTreeNodeDTO) path
					.getLastPathComponent();
			if (!syncTreeNodeDTO.isLeaf()) {
				SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) syncTreeNodeDTO;
				paths.add(path);
				for (SyncTreeNodeDTO child : directory.getList()) {
					getPathDirectories(path.pathByAddingChild(child), paths);
				}
			}
		}
	}

	public void updateAddons(SyncTreeDirectoryDTO syncTreeDirectoryDTO) {

		arbre.removeAll();
		racine = syncTreeDirectoryDTO;
		totalFilesSize = 0;
		totalFilesSelected = 0;
		totalFilesUpdated = 0;
		totalFilesDeleted = 0;
		compute(racine);
		labelTotalFilesSizeValue.setText(UnitConverter
				.convertSize(totalFilesSize));
		labelTotalFilesUpdatedValue
				.setText(Integer.toString(totalFilesUpdated));
		labeltTotalFilesDeletedValue.setText(Integer
				.toString(totalFilesDeleted));
		addonSyncTreeModel = new AddonSyncTreeModel(racine);
		arbre.setModel(addonSyncTreeModel);
		int numberRowShown = arbre.getRowCount();
		arbre.setVisibleRowCount(numberRowShown);
		arbre.setPreferredSize(arbre.getPreferredScrollableViewportSize());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				arbre.updateUI();
			}
		});
		checkBoxExpandAll.setSelected(false);
		checkBoxSelectAll.setSelected(false);
	}

	private void compute(SyncTreeNodeDTO syncTreeNodeDTO) {

		if (syncTreeNodeDTO.isLeaf()) {
			if (syncTreeNodeDTO.isUpdated()) {
				totalFilesUpdated++;
			} else if (syncTreeNodeDTO.isDeleted()) {
				totalFilesDeleted++;
			}
			if (syncTreeNodeDTO.isSelected()) {
				totalFilesSelected++;
			}
			// Compute total files size
			if (syncTreeNodeDTO.isSelected() && syncTreeNodeDTO.isUpdated()) {
				SyncTreeLeafDTO leafDTO = (SyncTreeLeafDTO) syncTreeNodeDTO;
				totalFilesSize = totalFilesSize + leafDTO.getSize();
			}
		} else {
			SyncTreeDirectoryDTO directoryDTO = (SyncTreeDirectoryDTO) syncTreeNodeDTO;
			if (directoryDTO.getList().isEmpty()) {
				if (syncTreeNodeDTO.isUpdated()) {
					totalFilesUpdated++;
				} else if (syncTreeNodeDTO.isDeleted()) {
					totalFilesDeleted++;
				}
				if (syncTreeNodeDTO.isSelected()) {
					totalFilesSelected++;
				}
			} else {
				for (SyncTreeNodeDTO node : directoryDTO.getList()) {
					compute(node);
				}
			}
		}
	}

	private void refreshViewArbre() {
		int numberRowShown = arbre.getRowCount();
		arbre.setVisibleRowCount(numberRowShown);
		arbre.setPreferredSize(arbre.getPreferredScrollableViewportSize());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				arbre.updateUI();
			}
		});
	}

	private void onArbre2Expanded() {
		int numberRowShown = arbre.getRowCount();
		arbre.setVisibleRowCount(numberRowShown);
		arbre.setPreferredSize(arbre.getPreferredScrollableViewportSize());
		tableScrollPane.updateUI();
		arbre.setSelectionPath(null);
	}

	private void onArbre2Collapsed() {
		int numberRowShown = arbre.getRowCount();
		arbre.setVisibleRowCount(numberRowShown);
		arbre.setPreferredSize(arbre.getPreferredScrollableViewportSize());
		tableScrollPane.updateUI();
		arbre.setSelectionPath(null);
	}

	private void selectAllAscending(SyncTreeNodeDTO syncTreeNodeDTO) {
		if (syncTreeNodeDTO != null) {
			syncTreeNodeDTO.setSelected(true);
			SyncTreeNodeDTO parent = syncTreeNodeDTO.getParent();
			selectAllAscending(parent);
		}
	}

	private void unselectAllAscending(SyncTreeNodeDTO syncTreeNodeDTO) {
		if (syncTreeNodeDTO != null) {
			syncTreeNodeDTO.setSelected(false);
			SyncTreeNodeDTO parent = syncTreeNodeDTO.getParent();
			unselectAllAscending(parent);
		}
	}

	private void selectAllDescending(SyncTreeNodeDTO syncTreeNodeDTO) {
		syncTreeNodeDTO.setSelected(true);
		if (!syncTreeNodeDTO.isLeaf()) {
			SyncTreeDirectoryDTO syncTreeDirectoryDTO = (SyncTreeDirectoryDTO) syncTreeNodeDTO;
			for (SyncTreeNodeDTO t : syncTreeDirectoryDTO.getList()) {
				selectAllDescending(t);
			}
		}
	}

	private void deselectAllDescending(SyncTreeNodeDTO syncTreeNodeDTO) {
		syncTreeNodeDTO.setSelected(false);
		if (!syncTreeNodeDTO.isLeaf()) {
			SyncTreeDirectoryDTO syncTreeDirectoryDTO = (SyncTreeDirectoryDTO) syncTreeNodeDTO;
			for (SyncTreeNodeDTO t : syncTreeDirectoryDTO.getList()) {
				deselectAllDescending(t);
			}
		}
	}

	private void flattenSplitPane(JSplitPane jSplitPane) {
		jSplitPane.setUI(new BasicSplitPaneUI() {
			public BasicSplitPaneDivider createDefaultDivider() {
				return new BasicSplitPaneDivider(this) {
					public void setBorder(Border b) {
					}
				};
			}
		});
		jSplitPane.setBorder(null);
	}

	/* Getters */

	public JButton getButtonCheckForAddonsStart() {
		return buttonCheckForAddonsStart;
	}

	public JButton getButtonCheckForAddonsCancel() {
		return buttonCheckForAddonsCancel;
	}

	public JProgressBar getProgressBarCheckForAddons() {
		return progressBarCheckForAddons;
	}

	public JLabel getLabelCheckForAddonsStatus() {
		return labelCheckForAddonsStatus;
	}

	public JProgressBar getProgressBarDownloadAddons() {
		return progressBarDownloadAddons;
	}

	public JProgressBar getProgressBarDownloadSingleAddon() {
		return progressBarDownloadSingleAddon;
	}

	public JButton getButtonDownloadStart() {
		return buttonDownloadStart;
	}

	public JLabel getLabelDownloadStatus() {
		return labelDownloadStatus;
	}

	public JLabel getLabelDownloadedValue() {
		return labelDownloadedValue;
	}

	public JLabel getLabelSpeedValue() {
		return labelSpeedValue;
	}

	public JLabel getLabelRemainingTimeValue() {
		return labelRemainingTimeValue;
	}

	public JLabel getLabelTotalFilesSizeValue() {
		return labelTotalFilesSizeValue;
	}

	public JTree getArbre() {
		return this.arbre;
	}

	public JButton getButtonAdvancedConfiguration() {
		return buttonAdvancedConfigurationPerformed;
	}

}
