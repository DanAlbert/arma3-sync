package fr.soe.a3s.ui.repositoryEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeLeafDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
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
	private RepositoryPanel repositoryPanel;
	private JProgressBar progressBarCheckForAddons, progressBarDownloadAddons,
			progressBarDownloadSingleAddon;
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
	private TreePath arbreTreePath;
	private JButton buttonAdvancedConfigurationPerformed;
	private JCheckBox checkBoxSelectAll;
	private JCheckBox checkBoxExpandAll;
	private JCheckBox checkBoxUpdated;
	private JCheckBox checkBoxExactMatch;
	private String eventName;
	private JCheckBox checkBoxAutoDiscover;
	private JPopupMenu popup;
	private JMenuItem menuItemHideExtraLocalContent;
	private JMenuItem menuItemShowExtraLocalContent;
	private JLabel labelActiveConnections;
	private JLabel labelActiveConnectionsValue;

	/* Data */
	private AddonSyncTreeModel addonSyncTreeModel;
	private SyncTreeDirectoryDTO racine;
	private long totalFilesSize;
	private int totalFilesSelected;
	private int totalFilesUpdated;
	private int totalFilesDeleted;
	private boolean update;

	/* Services */
	private final RepositoryService repositoryService = new RepositoryService();
	private final ConfigurationService configurationService = new ConfigurationService();

	/* Workers */
	private AddonsChecker addonsChecker;
	private AddonsDownloader addonsDownloader;

	public DownloadPanel(Facade facade, RepositoryPanel repositoryPanel) {

		this.facade = facade;
		this.repositoryPanel = repositoryPanel;
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
			labelCheckForAddonsStatus.setPreferredSize(new java.awt.Dimension(83, 15));
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
			buttonCheckForAddonsStart.setFocusable(false);
			buttonCheckForAddonsCancel = new JButton("");
			buttonCheckForAddonsCancel.setFocusable(false);
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
		vBox.add(Box.createVerticalStrut(34));
		{
			JPanel filesPanel = new JPanel();
			filesPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			filesPanel.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(), "Repository changes"));
			vBox.add(filesPanel);
			Box vBox2 = Box.createVerticalBox();
			filesPanel.add(vBox2);
			{
				JLabel totalFilesUpdated = new JLabel("Total files to update: ");
				totalFilesUpdated.setForeground(Color.red);
				labelTotalFilesUpdatedValue = new JLabel();
				labelTotalFilesUpdatedValue.setForeground(Color.red);
				Box hBox = Box.createHorizontalBox();
				hBox.add(totalFilesUpdated);
				hBox.add(labelTotalFilesUpdatedValue);
				hBox.add(Box.createHorizontalGlue());
				vBox2.add(hBox);
			}
			vBox2.add(Box.createVerticalStrut(10));
			{
				JLabel totalFilesDeleted = new JLabel(
						"Total files to delete : ");
				totalFilesDeleted.setForeground(Color.BLUE);
				labeltTotalFilesDeletedValue = new JLabel();
				labeltTotalFilesDeletedValue.setForeground(Color.BLUE);
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
			buttonAdvancedConfigurationPerformed.setFocusable(false);
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
			buttonDownloadStart.setFocusable(false);
			buttonDownloadPause = new JButton();
			buttonDownloadPause.setFocusable(false);
			buttonDownloadCancel = new JButton();
			buttonDownloadCancel.setFocusable(false);
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
		vBox.add(Box.createVerticalStrut(18));

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
		{
			labelActiveConnections = new JLabel("Active connections: ");
			labelActiveConnectionsValue = new JLabel();
			Box hBox = Box.createHorizontalBox();
			hBox.add(labelActiveConnections);
			hBox.add(labelActiveConnectionsValue);
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
			comBoxDestinationFolder.setFocusable(false);
			comBoxDestinationFolder.setPreferredSize(new Dimension(this
					.getWidth(), 25));
			locationPanel.add(comBoxDestinationFolder, BorderLayout.CENTER);
			vBox.add(locationPanel);
		}
		{
			JPanel selectionPanel = new JPanel();
			selectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			selectionPanel.setPreferredSize(new Dimension(this.getWidth(), 25));
			checkBoxSelectAll = new JCheckBox("Select All  ");
			checkBoxSelectAll.setFocusable(false);
			checkBoxAutoDiscover = new JCheckBox("Auto-discover");
			checkBoxAutoDiscover.setFocusable(false);
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxSelectAll);
			hBox.add(Box.createHorizontalStrut(20));
			hBox.add(checkBoxAutoDiscover);
			selectionPanel.add(hBox);
			vBox.add(selectionPanel);
		}
		{
			JPanel selectionPanel = new JPanel();
			selectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			selectionPanel.setPreferredSize(new Dimension(this.getWidth(), 25));
			checkBoxExpandAll = new JCheckBox("Expand All");
			checkBoxExpandAll.setFocusable(false);
			checkBoxExactMatch = new JCheckBox("Exact match");
			checkBoxExactMatch.setFocusable(false);
			Box hBox = Box.createHorizontalBox();
			hBox.add(checkBoxExpandAll);
			hBox.add(Box.createHorizontalStrut(20));
			hBox.add(checkBoxExactMatch);
			selectionPanel.add(hBox);
			vBox.add(selectionPanel);
		}
		vBox.add(Box.createVerticalStrut(5));
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

		/* Right clic menu */
		popup = new JPopupMenu();

		menuItemHideExtraLocalContent = new JMenuItem(
				"Hide  extra local content");
		menuItemHideExtraLocalContent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				popupActionPerformed(evt);
			}
		});
		menuItemHideExtraLocalContent.setActionCommand("Hide");
		popup.add(menuItemHideExtraLocalContent);

		menuItemShowExtraLocalContent = new JMenuItem(
				"Show extra local content");
		menuItemShowExtraLocalContent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				popupActionPerformed(evt);
			}
		});
		menuItemShowExtraLocalContent.setActionCommand("Show");
		popup.add(menuItemShowExtraLocalContent);

		popup.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
			}

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				SyncTreeNodeDTO syncTreeNodeDTO = (SyncTreeNodeDTO) arbre
						.getLastSelectedPathComponent();
				menuItemHideExtraLocalContent.setEnabled(false);
				menuItemShowExtraLocalContent.setEnabled(false);
				if (syncTreeNodeDTO == null) {
					return;
				} else if (!syncTreeNodeDTO.isLeaf()) {
					menuItemHideExtraLocalContent.setEnabled(true);
					menuItemShowExtraLocalContent.setEnabled(true);
				}
			}
		});

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
		checkBoxAutoDiscover.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				checkBoxAutoDiscoverPerformed();
			}
		});
		checkBoxExactMatch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				checkBoxExactMatchPerformed();
			}
		});
		arbre.addTreeExpansionListener(new TreeExpansionListener() {
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				onArbre2Expanded();
			}

			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				onArbre2Collapsed();
			}
		});
		arbre.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent arg0) {
				arbreTreePath = arbre.getSelectionPath();
				// System.out.println(arbreTreePath);
			}
		});
		arbre.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (arbreTreePath == null) {
					return;
				}
				if (SwingUtilities.isRightMouseButton(e)) {
					return;
				}
				int hotspot = new JCheckBox().getPreferredSize().width;

				TreePath path = arbre.getPathForLocation(e.getX(), e.getY());
				if (path == null) {
					return;
				} else if (e.getX() > arbre.getPathBounds(path).x + hotspot) {
					return;
				}
				addonSelectionPerformed();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.show((JComponent) e.getSource(), e.getX(), e.getY());
				} else if (SwingUtilities.isRightMouseButton(e)) {
					popup.show((JComponent) e.getSource(), e.getX(), e.getY());
				}
			}
		});
		arbre.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				if (evt.getKeyCode() == evt.VK_SPACE) {
					addonSelectionPerformed();
				}
			}
		});
		comBoxDestinationFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				comBoxDestinationFolderPerformed();
			}
		});
		comBoxDestinationFolder.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				defaultFolderDestinationSelection();
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
				defaultFolderDestinationReleased();
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {
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
		checkBoxSelectAll.setToolTipText("Select All");
		checkBoxExpandAll.setToolTipText("Expand All");
		checkBoxAutoDiscover
				.setToolTipText("Auto-discover addons within all destination folders");
		checkBoxExactMatch
				.setToolTipText("Exact match repository content against default destination folder");
	}

	public void init(String repositoryName, String eventName, boolean update) {

		this.repositoryName = repositoryName;
		this.eventName = eventName;
		this.update = update;

		defaultFolderDestinationSelection();
		updateAutoDiscoverSelection();
		updateExactMatchSelection();
	}

	private void popupActionPerformed(ActionEvent evt) {

		if (evt.getActionCommand().equals("Hide")) {
			hidePerformed();
		} else if (evt.getActionCommand().equals("Show")) {
			showPerformed();
		}
	}

	private void addonSelectionPerformed() {

		SyncTreeNodeDTO syncTreeNodeDTO = (SyncTreeNodeDTO) arbre
				.getLastSelectedPathComponent();

		if (syncTreeNodeDTO == null) {
			return;
		}

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

		if (!repositoryService.isDownloading(repositoryName)) {
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

	private void hidePerformed() {

		SyncTreeNodeDTO syncTreeNodeDTO = (SyncTreeNodeDTO) arbre
				.getLastSelectedPathComponent();

		SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) syncTreeNodeDTO;
		String relativePath = directory.getName();
		SyncTreeDirectoryDTO parent = directory.getParent();
		while (!parent.getName().equals("racine")) {
			relativePath = parent.getName() + "/" + relativePath;
			parent = parent.getParent();
		}

		repositoryService.addFilesToHide(relativePath, repositoryName);

		// Check addons repository
		addonsChecker = new AddonsChecker(facade, repositoryName, eventName,
				update, this);
		addonsChecker.start();
	}

	private void showPerformed() {

		SyncTreeNodeDTO syncTreeNodeDTO = (SyncTreeNodeDTO) arbre
				.getLastSelectedPathComponent();

		SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) syncTreeNodeDTO;
		String relativePath = directory.getName();
		SyncTreeDirectoryDTO parent = directory.getParent();
		while (!parent.getName().equals("racine")) {
			relativePath = parent.getName() + "/" + relativePath;
			parent = parent.getParent();
		}

		repositoryService.removeFilesToHide(relativePath, repositoryName);

		// Check addons repository
		addonsChecker = new AddonsChecker(facade, repositoryName, eventName,
				update, this);
		addonsChecker.start();
	}

	private void defaultFolderDestinationSelection() {

		Set<String> set = configurationService.getAddonSearchDirectoryPaths();
		Iterator iter = set.iterator();
		List<String> paths = new ArrayList<String>();
		while (iter.hasNext()) {
			paths.add((String) iter.next());
		}

		String[] tab = new String[paths.size()];
		int i = 0;
		for (String p : paths) {
			tab[i] = p;
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

	private void defaultFolderDestinationReleased() {

		int index = comBoxDestinationFolder.getSelectedIndex();

		if (index != -1 && addonsChecker != null) {
			String path = (String) comBoxDestinationFolder.getSelectedItem();
			repositoryService.setDefaultDownloadLocation(repositoryName, path);
			checkForAddons();
		}
	}

	public void updateAutoDiscoverSelection() {

		boolean value = repositoryService.isAutoDiscover(repositoryName);
		checkBoxAutoDiscover.setSelected(!value);
	}

	private void updateExactMatchSelection() {

		checkBoxExactMatch.setEnabled(true);
		checkBoxAutoDiscover.setEnabled(true);
		
		if (eventName != null) {
			checkBoxExactMatch.setEnabled(false);
		} else {
			boolean value = repositoryService.isExactMatch(repositoryName);
			checkBoxExactMatch.setSelected(value);
			if (value == true) {
				checkBoxAutoDiscover.setSelected(false);
				checkBoxAutoDiscover.setEnabled(false);
			} 
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
		} else {
			String defaultDownloadLocation = (String) comBoxDestinationFolder
					.getSelectedItem();
			repositoryService.setDefaultDownloadLocation(repositoryName,
					defaultDownloadLocation);
		}

		// Repository status changed to ok
		repositoryService.updateRepositoryRevision(repositoryName);
		repositoryService.setOutOfSync(repositoryName, false);
		try {
			repositoryService.write(repositoryName);
		} catch (WritingException e) {
		}

		checkForAddons();
		facade.getSyncPanel().init();
	}

	public void checkForAddons() {

		// Lock user action on addons tree
		arbre.setEnabled(false);
		// Check addons repository
		addonsChecker = new AddonsChecker(facade, repositoryName, eventName,
				update, this);
		addonsChecker.start();
	}

	private void buttonCheckForAddonsCancelPerformed() {

		if (addonsChecker != null) {
		    addonsChecker.cancel();
			labelCheckForAddonsStatus.setText("Canceled!");
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
			refreshViewArbre();
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

		addonsDownloader = new AddonsDownloader(facade, repositoryName, racine, this);
		addonsDownloader.setDaemon(true);
		addonsDownloader.start();
	}

	private void buttonDownloadPausePerformed() {

		if (repositoryService.isDownloading(repositoryName)) {
			addonsDownloader.pause();
			addonsDownloader.interrupt();
		}
		buttonDownloadStart.setEnabled(true);
	}

	private void buttonDownloadCancelPerformed() {

		if (addonsDownloader != null) {
			addonsDownloader.cancel();
			addonsDownloader.interrupt();
			addonsDownloader = null;
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

		String defaultDestinationPath = repositoryService
				.getDefaultDownloadLocation(repositoryName);

		AdvancedConfigurationPanel advancedConfigurationPanel = new AdvancedConfigurationPanel(
				facade, racine, defaultDestinationPath, repositoryName, this);
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

	private void checkBoxAutoDiscoverPerformed() {

		boolean value = checkBoxAutoDiscover.isSelected();
		repositoryService.setAutoDiscover(!value, repositoryName);
		arbre.removeAll();
		addonSyncTreeModel = new AddonSyncTreeModel(new SyncTreeDirectoryDTO());
		arbre.setModel(addonSyncTreeModel);
		refreshViewArbre();
	}

	private void checkBoxExactMatchPerformed() {

		boolean value = checkBoxExactMatch.isSelected();
		repositoryService.setExactMatch(value, repositoryName);
		checkBoxAutoDiscover.setSelected(false);
		if (value == true) {
			checkBoxAutoDiscover.setEnabled(false);
			checkBoxAutoDiscover.setSelected(false);
		} else {
			checkBoxAutoDiscover.setEnabled(true);
		}
		arbre.removeAll();
		addonSyncTreeModel = new AddonSyncTreeModel(new SyncTreeDirectoryDTO());
		arbre.setModel(addonSyncTreeModel);
		refreshViewArbre();
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
		if (racine!=null){
			compute(racine);
		}
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

	/**
	 * Determine total file size, nb file to update and delete
	 * @param syncTreeNodeDTO not null
	 */
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
				totalFilesSize = totalFilesSize
						+ (long) (leafDTO.getSize()
								* (100 - leafDTO.getComplete()) / 100);
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
			@Override
			public BasicSplitPaneDivider createDefaultDivider() {
				return new BasicSplitPaneDivider(this) {
					@Override
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

	public JLabel getLabelActiveConnectionsValue() {
		return labelActiveConnectionsValue;
	}

	public JTree getArbre() {
		return this.arbre;
	}

	public JButton getButtonAdvancedConfiguration() {
		return buttonAdvancedConfigurationPerformed;
	}

	public RepositoryPanel getRepositoryPanel() {
		return this.repositoryPanel;
	}

	public JComboBox getComBoxDestinationFolder() {
		return comBoxDestinationFolder;
	}
}
