package fr.soe.a3s.ui.mainEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import fr.soe.a3s.constant.ModsetType;
import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.TreeLeafDTO;
import fr.soe.a3s.dto.TreeNodeDTO;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.service.AddonService;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.service.LaunchService;
import fr.soe.a3s.service.ProfileService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.mainEditor.groups.AddonsAddGroupPanel;
import fr.soe.a3s.ui.mainEditor.groups.AddonsRenameGroupPanel;
import fr.soe.a3s.ui.mainEditor.tree.AddonTreeModel;
import fr.soe.a3s.ui.mainEditor.tree.CheckTreeCellRenderer;
import fr.soe.a3s.ui.mainEditor.tree.MyRenderer2;
import fr.soe.a3s.ui.mainEditor.tree.TreeDnD;

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
public class AddonsPanel extends JPanel implements UIConstants {

	private Facade facade;
	private JScrollPane availableAddonsScrollPane, addonGroupsPanelScrollPane;
	private JTree arbre1;
	private JTree arbre2;
	private JCheckBox checkBoxTree, checkBoxList;
	private TreeDirectoryDTO racine1;
	private TreeDirectoryDTO racine2;
	private AddonTreeModel addonTreeModel1, addonTreeModel2;
	private JPopupMenu popup;
	private TreeDnD treeDnD;
	private JMenuItem menuItemAddGroup, menuItemRename, menuItemRemove;
	private final ConfigurationService configurationService = new ConfigurationService();
	private final ProfileService profileService = new ProfileService();
	private final LaunchService launchService = new LaunchService();
	private TreePath arbre2TreePath;
	private JButton buttonRefresh;
	private TreePath arbre2NewTreePath;
	private JCheckBox checkBoxSelectAll;
	private JCheckBox checkBoxExpandAll;
	private JButton buttonModsets;
	// Service
	private final AddonService addonService = new AddonService();
	private final RepositoryService repositoryService = new RepositoryService();

	public AddonsPanel(final Facade facade) {
		this.facade = facade;
		this.facade.setAddonsPanel(this);
		this.setLayout(new BorderLayout());

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(1, 2));
		JPanel controlPanel1 = new JPanel();
		controlPanel1.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel controlPanel2 = new JPanel();
		controlPanel2.setLayout(new FlowLayout(FlowLayout.LEFT));
		controlPanel.add(controlPanel1);
		controlPanel.add(controlPanel2);

		checkBoxTree = new JCheckBox("Tree");
		checkBoxTree.setFocusable(false);
		checkBoxList = new JCheckBox("List");
		checkBoxList.setFocusable(false);
		ButtonGroup groupCheckBox = new ButtonGroup();
		groupCheckBox.add(checkBoxTree);
		groupCheckBox.add(checkBoxList);
		buttonRefresh = new JButton("Refresh");
		buttonRefresh.setFocusable(false);
		ImageIcon refreshIcon = new ImageIcon(REFRESH);
		buttonRefresh.setIcon(refreshIcon);
		controlPanel1.add(checkBoxTree);
		controlPanel1.add(checkBoxList);
		controlPanel1.add(buttonRefresh);

		checkBoxSelectAll = new JCheckBox("Select All");
		checkBoxSelectAll.setFocusable(false);
		checkBoxExpandAll = new JCheckBox("Expand All");
		checkBoxExpandAll.setFocusable(false);
		buttonModsets = new JButton("Modsets");
		buttonModsets.setFocusable(false);
		ImageIcon checkIcon = new ImageIcon(CHECK);
		buttonModsets.setIcon(checkIcon);
		controlPanel2.add(checkBoxSelectAll);
		controlPanel2.add(checkBoxExpandAll);
		controlPanel2.add(buttonModsets);

		this.add(controlPanel, BorderLayout.NORTH);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(1, 2));
		mainPanel.setFocusable(false);

		addonTreeModel1 = new AddonTreeModel(racine1);
		arbre1 = new JTree(addonTreeModel1);
		arbre1.setRootVisible(false);
		arbre1.setEditable(false);
		arbre1.setLargeModel(true);
		arbre1.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		arbre1.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		availableAddonsScrollPane = new JScrollPane(arbre1);
		availableAddonsScrollPane.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Available Addons"));
		mainPanel.add(availableAddonsScrollPane);

		addonTreeModel2 = new AddonTreeModel(racine2);
		arbre2 = new JTree(addonTreeModel2);
		arbre2.setRootVisible(false);
		arbre2.setEditable(false);
		arbre2.setShowsRootHandles(true);
		arbre2.setLargeModel(true);
		arbre2.setToggleClickCount(0);
		arbre2.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		arbre2.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		addonGroupsPanelScrollPane = new JScrollPane(arbre2);
		addonGroupsPanelScrollPane.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Addon Groups"));
		mainPanel.add(addonGroupsPanelScrollPane);
		this.add(mainPanel, BorderLayout.CENTER);

		MyRenderer2 myRenderer2 = new MyRenderer2();
		arbre1.setCellRenderer(myRenderer2);
		arbre2.setCellRenderer(myRenderer2);

		CheckTreeCellRenderer renderer = new CheckTreeCellRenderer(myRenderer2);
		arbre2.setCellRenderer(renderer);

		/* TreeDnD */
		treeDnD = new TreeDnD(arbre1, arbre2, facade);

		/* Right clic menu */
		popup = new JPopupMenu();

		menuItemAddGroup = new JMenuItem("Add Group");
		menuItemAddGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				popupActionPerformed(evt);
			}
		});
		menuItemAddGroup.setActionCommand("Add Group");
		popup.add(menuItemAddGroup);

		menuItemRename = new JMenuItem("Rename");
		menuItemRename.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				popupActionPerformed(evt);
			}
		});
		menuItemRename.setActionCommand("Rename");
		popup.add(menuItemRename);

		menuItemRemove = new JMenuItem("Remove");
		menuItemRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				popupActionPerformed(evt);
			}
		});
		menuItemRemove.setActionCommand("Remove");
		popup.add(menuItemRemove);

		// popup.setOpaque(true);
		// popup.setLightWeightPopupEnabled(true);

		popup.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
			}

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				TreeNodeDTO[] nodes = getSelectedNode();
				if (nodes != null) {
					if (nodes.length > 0) {
						if (nodes[0].isLeaf()) {
							menuItemAddGroup.setEnabled(true);
							menuItemRename.setEnabled(false);
							menuItemRemove.setEnabled(true);
						} else if (!nodes[0].isLeaf()) {
							TreeDirectoryDTO directortDTO = (TreeDirectoryDTO) nodes[0];
							if (directortDTO.getModsetType() == null) {
								menuItemAddGroup.setEnabled(true);
								menuItemRename.setEnabled(true);
								menuItemRemove.setEnabled(true);
							} else {
								menuItemAddGroup.setEnabled(false);
								menuItemRename.setEnabled(false);
								menuItemRemove.setEnabled(true);
							}
						}
					} else {
						menuItemAddGroup.setEnabled(true);
						menuItemRename.setEnabled(false);
						menuItemRemove.setEnabled(false);
					}
				} else {
					menuItemAddGroup.setEnabled(true);
					menuItemRename.setEnabled(false);
					menuItemRemove.setEnabled(false);
				}
			}
		});
		arbre1.addTreeExpansionListener(new TreeExpansionListener() {
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				onArbre1Expanded(event.getPath());
			}

			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				onArbre1Collapsed(event.getPath());
			}
		});
		arbre2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				arbre2TreePath = arbre2.getPathForLocation(e.getX(), e.getY());

				if (arbre2TreePath == null) {
					arbre2.setSelectionPath(null);
					refreshViewArbre2();
					return;
				}

				int hotspot = new JCheckBox().getPreferredSize().width;

				if (e.getX() > arbre2.getPathBounds(arbre2TreePath).x + hotspot) {
					return;
				} else {
					addonSelectionPerformed();
				}
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
		arbre2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				if (evt.getKeyCode() == evt.VK_DELETE) {
					removePerformed();
				} else if (evt.getKeyCode() == evt.VK_SPACE) {
					addonSelectionPerformed();
				}
			}
		});
		arbre2.addTreeExpansionListener(new TreeExpansionListener() {
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				onArbre2Expanded(event.getPath());
			}

			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				onArbre2Collapsed(event.getPath());
			}
		});
		checkBoxTree.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (checkBoxTree.isSelected()) {
					checkBoxTreeSelectionPerformed();
				}
			}
		});
		checkBoxList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (checkBoxList.isSelected()) {
					checkBoxListSelectionPerformed();
				}
			}
		});
		buttonRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonRefreshPerformed();
			}
		});
		checkBoxSelectAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				checkBoxSelectAllPerformed();
			}
		});
		checkBoxExpandAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				checkBoxExpandAllPerformed();
			}
		});
		buttonModsets.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonEventsPerformed();
			}
		});

		setContextualHelp();
	}

	/* Set contextual help on jComponents */
	private void setContextualHelp() {
		buttonRefresh.setToolTipText("Reload Availabe Addons list");
		buttonModsets.setToolTipText("Generate addons group from modset");
	}

	public void init() {

		addonService.resetAvailableAddonTree();

		/* View Mode */
		boolean isViewTreeMode = configurationService.isViewModeTree();
		if (isViewTreeMode) {
			checkBoxTree.setSelected(true);
			arbre1.setShowsRootHandles(true);
		} else {
			checkBoxList.setSelected(true);
			arbre1.setShowsRootHandles(false);
		}

		/* Display available addons */
		updateAvailableAddons();

		/* Display addon groups */
		updateAddonGroups();

		/* Expand */
		expandAddonGroups();

	}

	public void updateAvailableAddons() {

		arbre1.removeAll();
		racine1 = addonService.getAvailableAddonsTree();
		addonTreeModel1 = new AddonTreeModel(racine1);
		arbre1.setModel(addonTreeModel1);
		int numberRowShown = arbre1.getRowCount();
		arbre1.setVisibleRowCount(numberRowShown);
		arbre1.setPreferredSize(arbre1.getPreferredScrollableViewportSize());
		arbre1.updateUI();
	}

	public void updateAddonGroups() {

		arbre2.setEnabled(false);
		arbre2.removeAll();
		racine2 = profileService.getAddonGroupsTree();
		addonTreeModel2 = new AddonTreeModel(racine2);
		arbre2.setModel(addonTreeModel2);
		highlightMissingAddons();
		refreshViewArbre2();
		arbre2.setEnabled(true);
	}

	/* Highlight missing selected addons into Addon Groups */
	private void highlightMissingAddons() {
		List<String> missingAddonNames = launchService.getMissingAddons();
		for (TreeNodeDTO treeNodeDTO : racine2.getList()) {
			markAsMissing(treeNodeDTO, missingAddonNames);
		}
	}

	private void markAsMissing(TreeNodeDTO treeNodeDTO, List<String> addonNames) {
		if (treeNodeDTO.isLeaf() && addonNames.contains(treeNodeDTO.getName())) {
			treeNodeDTO.setMissing(true);
		} else if (!treeNodeDTO.isLeaf()) {
			TreeDirectoryDTO treeDirectoryDTO = (TreeDirectoryDTO) treeNodeDTO;
			for (TreeNodeDTO t : treeDirectoryDTO.getList()) {
				markAsMissing(t, addonNames);
			}
		}
	}

	public void saveAddonGroups() {
		profileService.setAddonGroupsTree(racine2);
	}

	public void expand(TreePath path) {
		if (path != null) {
			TreeNodeDTO treeNodeDTO = (TreeNodeDTO) path.getLastPathComponent();
			if (!treeNodeDTO.isLeaf()) {
				arbre2.expandPath(path);
			}
		}
	}

	public void expandAddonGroups() {
		Set<TreePath> paths = new HashSet<TreePath>();
		expandAddonGroups(new TreePath(arbre2.getModel().getRoot()), paths);
		for (TreePath treePath : paths) {
			arbre2.expandPath(treePath);
		}
	}

	private void expandAddonGroups(TreePath path, Set<TreePath> paths) {

		if (path != null) {
			TreeNodeDTO treeNodeDTO = (TreeNodeDTO) path.getLastPathComponent();
			if (!treeNodeDTO.isLeaf()) {
				TreeDirectoryDTO treeDirectoryDTO = (TreeDirectoryDTO) treeNodeDTO;
				int nbNodes = treeDirectoryDTO.getList().size();
				int nbSelectedNodes = 0;
				int nbMissingNodes = 0;
				for (TreeNodeDTO child : treeDirectoryDTO.getList()) {
					if (child.isSelected()) {
						nbSelectedNodes++;
					}
					if (child.isMissing()) {
						nbMissingNodes++;
					}
				}
				if (nbSelectedNodes != 0 && nbSelectedNodes != nbNodes) {
					paths.add(path);
				} else if (nbSelectedNodes != 0 && nbMissingNodes != 0) {
					paths.add(path);
				}

				for (TreeNodeDTO child : treeDirectoryDTO.getList()) {
					expandAddonGroups(path.pathByAddingChild(child), paths);
				}
			}
		}
	}

	public void refreshViewArbre1() {

		int numberRowShown = arbre1.getRowCount();
		arbre1.setVisibleRowCount(numberRowShown);
		arbre1.setPreferredSize(arbre1.getPreferredScrollableViewportSize());
		arbre1.updateUI();
	}

	public void refreshViewArbre2() {

		int numberRowShown = arbre2.getRowCount();
		arbre2.setVisibleRowCount(numberRowShown);
		arbre2.setPreferredSize(arbre2.getPreferredScrollableViewportSize());
		arbre2.updateUI();

		if (numberRowShown == 0) {
			arbre2.setToolTipText("Right click to add a group");
		} else {
			arbre2.setToolTipText(null);
		}
	}

	private void addonSelectionPerformed() {

		TreeNodeDTO treeNodeDTO = (TreeNodeDTO) arbre2
				.getLastSelectedPathComponent();

		if (treeNodeDTO == null) {
			return;
		}

		treeNodeDTO.setSelected(!treeNodeDTO.isSelected());

		if (treeNodeDTO.isLeaf() && !treeNodeDTO.isSelected()) {
			TreeDirectoryDTO treeDirectoryDTO = treeNodeDTO.getParent();
			treeDirectoryDTO.setSelected(false);
			deselectAllAscending(treeDirectoryDTO);
		} else if (treeNodeDTO.isLeaf() && treeNodeDTO.isSelected()) {
			TreeDirectoryDTO treeDirectoryDTO = treeNodeDTO.getParent();
			int nbNodes = treeDirectoryDTO.getList().size();
			int nbSelectedNodes = 0;
			for (TreeNodeDTO treDto : treeDirectoryDTO.getList()) {
				if (treDto.isSelected()) {
					nbSelectedNodes++;
				}
			}
			if (nbNodes == nbSelectedNodes) {
				treeDirectoryDTO.setSelected(true);
			}
			selectAllAscending(treeNodeDTO);
		} else if (!treeNodeDTO.isLeaf()) {
			TreeDirectoryDTO treeDirectoryDTO = (TreeDirectoryDTO) treeNodeDTO;
			if (treeNodeDTO.isSelected()) {
				selectAllAscending(treeNodeDTO);
				selectAllDescending(treeDirectoryDTO);
			} else {
				deselectAllDescending(treeDirectoryDTO);
			}
		}
		saveAddonGroups();
		highlightMissingAddons();
		refreshViewArbre2();
		facade.getLaunchOptionsPanel().updateRunParameters();
	}

	private void popupActionPerformed(ActionEvent evt) {
		if (evt.getActionCommand().equals("Add Group")) {
			addPerformed();
		} else if (evt.getActionCommand().equals("Rename")) {
			renamePormed();
		} else if (evt.getActionCommand().equals("Remove")) {
			removePerformed();
		}
	}

	public void addPerformed() {

		TreeNodeDTO[] treeNodeDTOs = getSelectedNode();

		if (treeNodeDTOs == null) {
			AddonsAddGroupPanel addGroupPanel = new AddonsAddGroupPanel(facade,
					racine2);
			addGroupPanel.init();
			addGroupPanel.setVisible(true);
		} else if (treeNodeDTOs.length != 0) {
			TreeNodeDTO node = treeNodeDTOs[0];
			if (node.isLeaf()) {
				TreeDirectoryDTO directory = node.getParent();
				AddonsAddGroupPanel addGroupPanel = new AddonsAddGroupPanel(
						facade, directory);
				addGroupPanel.init();
				addGroupPanel.setVisible(true);
			} else {
				TreeDirectoryDTO directory = (TreeDirectoryDTO) node;
				AddonsAddGroupPanel addGroupPanel = new AddonsAddGroupPanel(
						facade, directory);
				addGroupPanel.init();
				addGroupPanel.setVisible(true);
			}
		}
	}

	public void renamePormed() {

		TreeNodeDTO[] nodes = getSelectedNode();
		if (nodes == null) {
			return;
		}
		if (nodes.length == 0) {
			return;
		}
		if (!nodes[0].isLeaf()) {
			TreeNodeDTO selectedNodeDTO = nodes[0];
			/* Repository modset and Event modset can't be renamed */
			TreeDirectoryDTO directoryDTO = (TreeDirectoryDTO) selectedNodeDTO;
			ModsetType modsetType = directoryDTO.getModsetType();
			if (modsetType == null) {
				AddonsRenameGroupPanel renameGroupPanel = new AddonsRenameGroupPanel(
						facade, racine2, selectedNodeDTO);
				renameGroupPanel.init();
				renameGroupPanel.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(facade.getMainPanel(),
						"Repository modset and Event modset can't be renamed.",
						"Addon group", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void removePerformed() {

		TreeNodeDTO[] nodes = getSelectedNode();
		if (nodes == null) {
			return;
		}
		if (nodes.length == 0) {
			return;
		}

		for (int i = 0; i < nodes.length; i++) {
			TreeNodeDTO parent = nodes[i].getParent();
			if (parent != null) {
				((TreeDirectoryDTO) parent).removeTreeNode(nodes[i]);
			}
		}
		arbre2.clearSelection();
		saveAddonGroups();
		boolean contains = false;
		for (int i = 0; i < nodes.length; i++) {
			if (!nodes[i].isLeaf()) {
				contains = true;
				break;
			}
		}
		if (contains) {
			updateAddonGroups();
		} else {
			refreshViewArbre2();
		}
		facade.getAddonOptionsPanel().updateAddonPriorities();
		facade.getLaunchOptionsPanel().updateRunParameters();
	}

	private void onArbre1Expanded(TreePath path) {
		int numberRowShown = arbre1.getRowCount();
		arbre1.setVisibleRowCount(numberRowShown);
		arbre1.setPreferredSize(arbre1.getPreferredScrollableViewportSize());
		addonGroupsPanelScrollPane.updateUI();
	}

	private void onArbre1Collapsed(TreePath path) {
		int numberRowShown = arbre1.getRowCount();
		arbre1.setVisibleRowCount(numberRowShown);
		arbre1.setPreferredSize(arbre1.getPreferredScrollableViewportSize());
		addonGroupsPanelScrollPane.updateUI();
	}

	private void onArbre2Expanded(TreePath path) {
		int numberRowShown = arbre2.getRowCount();
		arbre2.setVisibleRowCount(numberRowShown);
		arbre2.setPreferredSize(arbre2.getPreferredScrollableViewportSize());
		addonGroupsPanelScrollPane.updateUI();
		arbre2.setSelectionPath(null);

		// if (arbre2TreePath == null) {
		// return;
		// }

		// TreeNodeDTO treeNodeDTO = (TreeNodeDTO) arbre2
		// .getLastSelectedPathComponent();
		// if (!treeNodeDTO.isLeaf()) {
		// treeNodeDTO.setSelected(!treeNodeDTO.isSelected());
		// }
	}

	private void onArbre2Collapsed(TreePath path) {
		int numberRowShown = arbre2.getRowCount();
		arbre2.setVisibleRowCount(numberRowShown);
		arbre2.setPreferredSize(arbre2.getPreferredScrollableViewportSize());
		addonGroupsPanelScrollPane.updateUI();
		arbre2.setSelectionPath(null);

		// if (arbre2TreePath == null) {
		// return;
		// }
		// TreeNodeDTO treeNodeDTO = (TreeNodeDTO) arbre2
		// .getLastSelectedPathComponent();
		// if (!treeNodeDTO.isLeaf()) {
		// treeNodeDTO.setSelected(!treeNodeDTO.isSelected());
		// }
	}

	private void selectAllAscending(TreeNodeDTO treeNodeDTO) {
		if (treeNodeDTO != null) {
			TreeNodeDTO parent = treeNodeDTO.getParent();
			if (parent != null) {
				parent.setSelected(true);
				selectAllAscending(parent);
			}
		}
	}

	private void selectAllDescending(TreeDirectoryDTO treeDirectoryDTO) {
		treeDirectoryDTO.setSelected(true);
		for (TreeNodeDTO t : treeDirectoryDTO.getList()) {
			t.setSelected(true);
			if (!t.isLeaf()) {
				TreeDirectoryDTO d = (TreeDirectoryDTO) t;
				selectAllDescending(d);
			}
		}
	}

	private void deselectAllDescending(TreeDirectoryDTO treeDirectoryDTO) {
		treeDirectoryDTO.setSelected(false);
		for (TreeNodeDTO t : treeDirectoryDTO.getList()) {
			t.setSelected(false);
			if (!t.isLeaf()) {
				TreeDirectoryDTO d = (TreeDirectoryDTO) t;
				deselectAllDescending(d);
			}
		}
	}

	private void deselectAllAscending(TreeNodeDTO treeNodeDTO) {
		if (treeNodeDTO != null) {
			TreeNodeDTO parent = treeNodeDTO.getParent();
			if (parent != null) {
				parent.setSelected(false);
				selectAllAscending(parent);
			}
		}
	}

	private void checkBoxTreeSelectionPerformed() {
		configurationService.setViewModeTree(true);
		updateAvailableAddons();
	}

	private void checkBoxListSelectionPerformed() {
		configurationService.setViewModeTree(false);
		updateAvailableAddons();
	}

	private void buttonRefreshPerformed() {
		addonService.resetAvailableAddonTree();
		updateAvailableAddons();
		updateAddonGroups();
		highlightMissingAddons();
		expandAddonGroups();
		facade.getLaunchOptionsPanel().updateRunParameters();
	}

	private void checkBoxSelectAllPerformed() {
		if (checkBoxSelectAll.isSelected()) {
			selectAllDescending(racine2);
		} else {
			deselectAllDescending(racine2);
		}
		saveAddonGroups();
		highlightMissingAddons();
		refreshViewArbre2();
		facade.getLaunchOptionsPanel().updateRunParameters();
	}

	private void checkBoxExpandAllPerformed() {
		Set<TreePath> paths = new HashSet<TreePath>();
		if (checkBoxExpandAll.isSelected()) {
			getPathDirectories(new TreePath(arbre2.getModel().getRoot()), paths);
			for (TreePath treePath : paths) {
				arbre2.expandPath(treePath);
			}
		} else {
			TreePath rootPath = new TreePath(arbre2.getModel().getRoot());
			for (TreeNodeDTO child : racine2.getList()) {
				paths.add(rootPath.pathByAddingChild(child));
			}
			for (TreePath treePath : paths) {
				arbre2.collapsePath(treePath);
			}
		}
	}

	private void buttonEventsPerformed() {

		EventSelectionPanel eventSelectionPanel = new EventSelectionPanel(
				facade);
		eventSelectionPanel.init();
		eventSelectionPanel.setVisible(true);
	}

	public void createGroupFromRepository(List<String> repositoryNames) {

		for (String repositoryName : repositoryNames) {
			try {
				TreeDirectoryDTO directory = repositoryService
						.getAddonTreeFromRepository(repositoryName, false);
				if (directory != null) {
					TreeNodeDTO nodeToRemove = null;
					for (TreeNodeDTO node : racine2.getList()) {
						if (node.getName().equals(repositoryName)) {
							nodeToRemove = node;
						}
					}
					if (nodeToRemove != null) {
						racine2.getList().remove(nodeToRemove);
					}
					directory.setName(repositoryName);
					directory.setModsetType(ModsetType.REPOSITORY);
					directory.setParent(racine2);
					racine2.addTreeNode(directory);
				}
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}

		saveAddonGroups();
		updateAddonGroups();
		highlightMissingAddons();
		refreshViewArbre2();
		expandAddonGroups();
		facade.getAddonOptionsPanel().updateAddonPriorities();
		facade.getLaunchOptionsPanel().updateRunParameters();
	}

	public void createGroupFromEvents(List<EventDTO> eventDTOs) {

		for (EventDTO eventDTO : eventDTOs) {

			TreeNodeDTO nodeToRemove = null;
			for (TreeNodeDTO node : racine2.getList()) {
				if (node.getName().equals(eventDTO.getName())) {
					nodeToRemove = node;
				}
			}
			if (nodeToRemove != null) {
				racine2.getList().remove(nodeToRemove);
			}

			TreeDirectoryDTO directory = new TreeDirectoryDTO();
			directory.setName(eventDTO.getName());
			directory.setModsetType(ModsetType.EVENT);
			directory.setParent(racine2);
			racine2.addTreeNode(directory);
			for (Iterator<String> iter = eventDTO.getAddonNames().keySet()
					.iterator(); iter.hasNext();) {
				String name = iter.next();
				boolean optional = eventDTO.getAddonNames().get(name);
				TreeLeafDTO leaf = new TreeLeafDTO();
				leaf.setName(name);
				leaf.setOptional(optional);
				leaf.setParent(directory);
				directory.addTreeNode(leaf);
			}
		}

		saveAddonGroups();
		updateAddonGroups();
		highlightMissingAddons();
		refreshViewArbre2();
		expandAddonGroups();
		facade.getAddonOptionsPanel().updateAddonPriorities();
		facade.getLaunchOptionsPanel().updateRunParameters();
	}

	private void getPathDirectories(TreePath path, Set<TreePath> paths) {

		if (path != null) {
			TreeNodeDTO treeNodeDTO = (TreeNodeDTO) path.getLastPathComponent();
			if (!treeNodeDTO.isLeaf()) {
				TreeDirectoryDTO directory = (TreeDirectoryDTO) treeNodeDTO;
				paths.add(path);
				for (TreeNodeDTO child : directory.getList()) {
					getPathDirectories(path.pathByAddingChild(child), paths);
				}
			}
		}
	}

	private TreeNodeDTO[] getSelectedNode() {
		TreePath[] paths = arbre2.getSelectionPaths();
		if (paths == null) {
			return null;
		} else if (paths.length == 0) {
			return null;
		} else {// paths !=null
			TreeNodeDTO[] treeNodeDTOs = new TreeNodeDTO[paths.length];
			for (int i = 0; i < paths.length; i++) {
				treeNodeDTOs[i] = (TreeNodeDTO) paths[i].getLastPathComponent();
			}
			return treeNodeDTOs;
		}
	}

	public JCheckBox getCheckBoxTree() {
		return checkBoxTree;
	}

	public void updateModsetSelection(String repositoryName) {

		try {
			/* Repository modsets */
			TreeDirectoryDTO treeDirectoryDTO = null;
			for (TreeNodeDTO node : racine2.getList()) {
				if (node.getName().equals(repositoryName)) {
					treeDirectoryDTO = (TreeDirectoryDTO) node;
					break;
				}
			}

			if (treeDirectoryDTO != null) {
				List<String> selectdAddonPaths = new ArrayList<String>();
				getSelectedAddonPaths(treeDirectoryDTO, selectdAddonPaths);
				racine2.removeTreeNode(treeDirectoryDTO);
				TreeDirectoryDTO newTreeDirectoryDTO = repositoryService
						.getAddonTreeFromRepository(repositoryName, false);
				newTreeDirectoryDTO.setName(repositoryName);
				newTreeDirectoryDTO.setParent(racine2);
				racine2.addTreeNode(newTreeDirectoryDTO);
				setSelectedPaths(newTreeDirectoryDTO, selectdAddonPaths);
			}

			/* Event modsets */
			List<EventDTO> eventDTOs = repositoryService
					.getEvents(repositoryName);

			if (eventDTOs != null) {
				for (EventDTO eventDTO : eventDTOs) {
					treeDirectoryDTO = null;
					for (TreeNodeDTO node : racine2.getList()) {
						if (node.getName().equals(eventDTO.getName())) {
							treeDirectoryDTO = (TreeDirectoryDTO) node;
							break;
						}
					}

					if (treeDirectoryDTO != null) {
						List<String> selectdAddonPaths = new ArrayList<String>();
						getSelectedAddonPaths(treeDirectoryDTO,
								selectdAddonPaths);
						racine2.removeTreeNode(treeDirectoryDTO);

						TreeDirectoryDTO newTreeDirectoryDTO = new TreeDirectoryDTO();
						newTreeDirectoryDTO.setName(eventDTO.getName());
						newTreeDirectoryDTO.setParent(racine2);
						racine2.addTreeNode(newTreeDirectoryDTO);

						for (Iterator<String> iter = eventDTO.getAddonNames()
								.keySet().iterator(); iter.hasNext();) {
							String name = iter.next();
							boolean optional = eventDTO.getAddonNames().get(
									name);
							TreeLeafDTO leaf = new TreeLeafDTO();
							leaf.setName(name);
							leaf.setOptional(optional);
							leaf.setParent(newTreeDirectoryDTO);
							// leaf.setSelected(true);
							newTreeDirectoryDTO.addTreeNode(leaf);
						}
						setSelectedPaths(newTreeDirectoryDTO, selectdAddonPaths);
					}
				}
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}

		saveAddonGroups();
		updateAddonGroups();
		highlightMissingAddons();
		refreshViewArbre2();
		expandAddonGroups();
		facade.getAddonOptionsPanel().updateAddonPriorities();
		facade.getLaunchOptionsPanel().updateRunParameters();
	}

	public void selectModset(String modsetName) {

		for (TreeNodeDTO node : racine2.getList()) {
			if (node.getName().equals(modsetName)) {
				deselectAllDescending(racine2);
				TreeDirectoryDTO treeDirectoryDTO = treeDirectoryDTO = (TreeDirectoryDTO) node;
				selectAllDescending(treeDirectoryDTO);
				saveAddonGroups();
				updateAddonGroups();
				highlightMissingAddons();
				refreshViewArbre2();
				expandAddonGroups();
				facade.getAddonOptionsPanel().updateAddonPriorities();
				facade.getLaunchOptionsPanel().updateRunParameters();
				break;
			}
		}
	}

	private void getSelectedAddonPaths(TreeNodeDTO node,
			List<String> selectedAddonPaths) {

		String path = node.getName();
		TreeNodeDTO parent = node.getParent();
		while (parent != null) {
			path = parent.getName() + "/" + path;
			parent = parent.getParent();
		}

		if (node.isSelected()) {
			selectedAddonPaths.add(path);
		}

		if (!node.isLeaf()) {
			TreeDirectoryDTO directory = (TreeDirectoryDTO) node;
			for (TreeNodeDTO n : directory.getList()) {
				getSelectedAddonPaths(n, selectedAddonPaths);
			}
		}
	}

	private void setSelectedPaths(TreeNodeDTO node,
			List<String> selectdAddonPaths) {

		String path = node.getName();
		TreeNodeDTO parent = node.getParent();
		while (parent != null) {
			path = parent.getName() + "/" + path;
			parent = parent.getParent();
		}

		if (selectdAddonPaths.contains(path)) {
			node.setSelected(true);
		} else {
			node.setSelected(false);
		}

		if (!node.isLeaf()) {
			TreeDirectoryDTO directory = (TreeDirectoryDTO) node;
			for (TreeNodeDTO n : directory.getList()) {
				setSelectedPaths(n, selectdAddonPaths);
			}
		}
	}
}
