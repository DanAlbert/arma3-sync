package fr.soe.a3s.ui.main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
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
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import fr.soe.a3s.constant.ModsetType;
import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.TreeLeafDTO;
import fr.soe.a3s.dto.TreeNodeDTO;
import fr.soe.a3s.service.AddonService;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.service.LaunchService;
import fr.soe.a3s.service.ProfileService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.groups.AddGroupDialog;
import fr.soe.a3s.ui.groups.DuplicateGroupDialog;
import fr.soe.a3s.ui.groups.RenameGroupDialog;
import fr.soe.a3s.ui.main.tree.AddonTreeModel;
import fr.soe.a3s.ui.main.tree.CheckTreeCellRenderer;
import fr.soe.a3s.ui.main.tree.MyRenderer;
import fr.soe.a3s.ui.main.tree.TreeDnD;
import fr.soe.a3s.ui.repository.dialogs.progress.ProgressModsetsSelectionDialog;

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
	private JTree arbre1, arbre2;
	private JCheckBox checkBoxTree, checkBoxList;
	private JPopupMenu popup;
	private TreeDnD treeDnD;
	private JMenuItem menuItemAddGroup, menuItemDuplicate, menuItemRename,
			menuItemRemove;
	private JButton buttonRefresh, buttonModsets;
	private JCheckBox checkBoxSelectAll, checkBoxExpandAll;

	private enum Selection {
		EMPTY, SINGLE, MULTIPLE
	}

	// Data
	private TreeDirectoryDTO racine1 = null;
	private TreeDirectoryDTO racine2 = null;

	// Manager
	private final GroupManager groupManager = new GroupManager();

	// Services
	private final ConfigurationService configurationService = new ConfigurationService();
	private final ProfileService profileService = new ProfileService();
	private final AddonService addonService = new AddonService();
	private final RepositoryService repositoryService = new RepositoryService();

	public AddonsPanel(final Facade facade) {
		this.facade = facade;
		this.facade.setAddonsPanel(this);
		this.setLayout(new BorderLayout());

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(1, 2));
		{
			JPanel controlPanel1 = new JPanel();
			controlPanel1.setLayout(new FlowLayout(FlowLayout.LEFT));
			JPanel controlPanel2 = new JPanel();
			controlPanel2.setLayout(new FlowLayout(FlowLayout.LEFT));
			controlPanel.add(controlPanel1);
			controlPanel.add(controlPanel2);
			{
				checkBoxTree = new JCheckBox("Tree");
				checkBoxTree.setFocusable(false);
				checkBoxList = new JCheckBox("List");
				checkBoxList.setFocusable(false);
				ButtonGroup groupCheckBox = new ButtonGroup();
				groupCheckBox.add(checkBoxTree);
				groupCheckBox.add(checkBoxList);
				buttonRefresh = new JButton("Refresh");
				buttonRefresh.setFocusable(false);
				buttonRefresh.setContentAreaFilled(false);
				buttonRefresh.setBorderPainted(false);
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
				buttonModsets.setContentAreaFilled(false);
				buttonModsets.setBorderPainted(false);

				ImageIcon checkIcon = new ImageIcon(ADD);
				buttonModsets.setIcon(checkIcon);
				controlPanel2.add(checkBoxSelectAll);
				controlPanel2.add(checkBoxExpandAll);
				controlPanel2.add(buttonModsets);
			}
		}
		this.add(controlPanel, BorderLayout.NORTH);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(1, 2));
		mainPanel.setFocusable(false);

		{
			AddonTreeModel addonTreeModel = new AddonTreeModel(racine1);
			arbre1 = new JTree(addonTreeModel);
			arbre1.setRootVisible(false);
			arbre1.setEditable(false);
			arbre1.setLargeModel(true);
			arbre1.getSelectionModel().setSelectionMode(
					TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
			arbre1.setBorder(BorderFactory
					.createBevelBorder(BevelBorder.LOWERED));
			availableAddonsScrollPane = new JScrollPane(arbre1);
			availableAddonsScrollPane.setBorder(BorderFactory
					.createTitledBorder(BorderFactory.createEtchedBorder(),
							"Available Addons"));
			mainPanel.add(availableAddonsScrollPane);

			Font fontArbre = UIManager.getFont("Tree.font");
			FontMetrics metrics = arbre1.getFontMetrics(fontArbre);
			int fontHeight = metrics.getAscent() + metrics.getDescent()
					+ metrics.getLeading();
			arbre1.setRowHeight(fontHeight);

			MyRenderer myRenderer = new MyRenderer();
			arbre1.setCellRenderer(myRenderer);
		}
		{
			AddonTreeModel addonTreeModel = new AddonTreeModel(racine2);
			arbre2 = new JTree(addonTreeModel);
			arbre2.setRootVisible(false);
			arbre2.setEditable(false);
			arbre2.setShowsRootHandles(true);
			arbre2.setLargeModel(true);
			arbre2.setToggleClickCount(0);
			arbre2.getSelectionModel().setSelectionMode(
					TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
			arbre2.setBorder(BorderFactory
					.createBevelBorder(BevelBorder.LOWERED));
			addonGroupsPanelScrollPane = new JScrollPane(arbre2);
			addonGroupsPanelScrollPane.setBorder(BorderFactory
					.createTitledBorder(BorderFactory.createEtchedBorder(),
							"Addon Groups"));
			mainPanel.add(addonGroupsPanelScrollPane);
			this.add(mainPanel, BorderLayout.CENTER);

			Font fontArbre = UIManager.getFont("Tree.font");
			FontMetrics metrics = arbre2.getFontMetrics(fontArbre);
			int fontHeight = metrics.getAscent() + metrics.getDescent()
					+ metrics.getLeading();
			arbre2.setRowHeight(fontHeight);

			MyRenderer myRenderer = new MyRenderer();
			CheckTreeCellRenderer renderer = new CheckTreeCellRenderer(
					myRenderer);
			arbre2.setCellRenderer(renderer);
		}

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

		menuItemDuplicate = new JMenuItem("Duplicate");
		menuItemDuplicate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				popupActionPerformed(evt);
			}
		});
		menuItemDuplicate.setActionCommand("Duplicate");
		popup.add(menuItemDuplicate);

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

		popup.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
			}

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				TreeNodeDTO[] nodes = getSelectedNodes();

				Selection selection = Selection.EMPTY;
				if (nodes != null) {
					if (nodes.length == 1) {
						selection = Selection.SINGLE;
					} else if (nodes.length > 1) {
						selection = Selection.MULTIPLE;
					}
				}

				if (selection.equals(Selection.EMPTY)) {
					// no selection => only enable Add group
					menuItemAddGroup.setEnabled(true);
					menuItemDuplicate.setEnabled(false);
					menuItemRename.setEnabled(false);
					menuItemRemove.setEnabled(false);
				} else if (selection.equals(Selection.SINGLE)) {
					boolean isLeaf = nodes[0].isLeaf();
					if (isLeaf) {
						TreeLeafDTO leaf = (TreeLeafDTO) nodes[0];

						ModsetType modsetType = leaf.getParent()
								.getModsetType();
						TreeDirectoryDTO parent = leaf.getParent().getParent();

						while (parent != null && modsetType == null) {
							modsetType = parent.getModsetType();
							parent = parent.getParent();
						}

						if (modsetType == null) {
							menuItemAddGroup.setEnabled(true);
							menuItemDuplicate.setEnabled(false);
							menuItemRename.setEnabled(false);
							menuItemRemove.setEnabled(true);
						} else {
							menuItemAddGroup.setEnabled(false);
							menuItemDuplicate.setEnabled(false);
							menuItemRename.setEnabled(false);
							menuItemRemove.setEnabled(false);
						}
					} else {
						TreeDirectoryDTO directory = (TreeDirectoryDTO) nodes[0];

						ModsetType modsetType = directory.getModsetType();

						if (modsetType != null) {
							menuItemAddGroup.setEnabled(false);
							menuItemDuplicate.setEnabled(false);
							menuItemRename.setEnabled(false);
							menuItemRemove.setEnabled(true);
						} else {
							TreeDirectoryDTO parent = directory.getParent();

							while (parent != null && modsetType == null) {
								modsetType = parent.getModsetType();
								parent = parent.getParent();
							}

							if (modsetType == null) {
								menuItemAddGroup.setEnabled(true);
								menuItemDuplicate.setEnabled(true);
								menuItemRename.setEnabled(true);
								menuItemRemove.setEnabled(true);
							} else {
								menuItemAddGroup.setEnabled(false);
								menuItemDuplicate.setEnabled(false);
								menuItemRename.setEnabled(false);
								menuItemRemove.setEnabled(false);
							}
						}
					}
				} else if (selection.equals(Selection.MULTIPLE)) {
					// multiple selection => only enable Remove
					menuItemAddGroup.setEnabled(false);
					menuItemDuplicate.setEnabled(false);
					menuItemRename.setEnabled(false);
					menuItemRemove.setEnabled(true);

					List<TreeNodeDTO> list = new ArrayList<TreeNodeDTO>();

					for (int i = 0; i < nodes.length; i++) {
						TreeNodeDTO node = (TreeNodeDTO) nodes[i];
						boolean isLeaf = node.isLeaf();
						if (isLeaf) {
							TreeLeafDTO leaf = (TreeLeafDTO) node;

							ModsetType modsetType = leaf.getParent()
									.getModsetType();
							TreeDirectoryDTO parent = leaf.getParent()
									.getParent();

							while (parent != null && modsetType == null) {
								modsetType = parent.getModsetType();
								parent = parent.getParent();
							}

							if (modsetType == null) {
								list.add(node);
							}
						} else {
							TreeDirectoryDTO directory = (TreeDirectoryDTO) node;
							ModsetType modsetType = directory.getModsetType();

							if (modsetType != null) {
								list.add(node);
							} else {
								TreeDirectoryDTO parent = directory.getParent();

								while (parent != null && modsetType == null) {
									modsetType = parent.getModsetType();
									parent = parent.getParent();
								}

								if (modsetType == null) {
									list.add(node);
								}
							}
						}
					}

					if (list.isEmpty()) {
						menuItemRemove.setEnabled(false);
					}
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

				TreePath treePath = arbre2.getPathForLocation(e.getX(),
						e.getY());

				if (treePath == null) {
					arbre2.setSelectionPath(null);

					arbre2.setEnabled(false);
					int numberRowShown = arbre2.getRowCount();
					arbre2.setVisibleRowCount(numberRowShown);
					arbre2.setPreferredSize(arbre2
							.getPreferredScrollableViewportSize());
					arbre2.updateUI();

					if (numberRowShown == 0) {
						arbre2.setToolTipText("Right click to add a group");
					} else {
						arbre2.setToolTipText(null);
					}
					arbre2.setEnabled(true);

					return;
				}

				int hotspot = new JCheckBox().getPreferredSize().width;

				if (e.getX() > arbre2.getPathBounds(treePath).x + hotspot) {
					return;
				} else {
					groupManager.select();
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
					groupManager.removeGroup();
				} else if (evt.getKeyCode() == evt.VK_SPACE) {
					groupManager.select();
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
		buttonRefresh.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent evt) {
				buttonRefresh.setContentAreaFilled(true);
			}

			@Override
			public void mouseExited(MouseEvent evt) {
				buttonRefresh.setContentAreaFilled(false);
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
				buttonModsetsPerformed();
			}
		});
		buttonModsets.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent evt) {
				buttonModsets.setContentAreaFilled(true);
			}

			@Override
			public void mouseExited(MouseEvent evt) {
				buttonModsets.setContentAreaFilled(false);
			}
		});
		setContextualHelp();
	}

	/* Set contextual help on jComponents */
	private void setContextualHelp() {

		buttonRefresh.setToolTipText("Reload Availabe Addons list");
		buttonModsets.setToolTipText("Generate addons group from modset");
	}

	public void update(int flag) {

		if (flag == OP_PROFILE_CHANGED || flag == OP_ADDON_FILES_CHANGED) {

			/* View Mode */
			boolean isViewTreeMode = configurationService.isViewModeTree();
			if (isViewTreeMode) {
				checkBoxTree.setSelected(true);
				arbre1.setShowsRootHandles(true);
			} else {
				checkBoxList.setSelected(true);
				arbre1.setShowsRootHandles(false);
			}

			/* Load available addons */
			loadAvailableAddons();

			/* Load addon groups */
			loadAddonGroups();
		}
	}

	private void loadAvailableAddons() {

		// System.out.println("Updating AddonsPanel arbre1...");

		arbre1.setEnabled(false);

		boolean isViewTreeMode = configurationService.isViewModeTree();
		if (isViewTreeMode) {
			racine1 = addonService.getAvailableAddonsTree();
		} else {
			racine1 = addonService.getAvailableAddonsList();
		}

		arbre1.removeAll();
		AddonTreeModel addonTreeModel = new AddonTreeModel(racine1);
		arbre1.setModel(addonTreeModel);
		addonTreeModel.fireTreeStructureChanged();

		int numberRowShown = arbre1.getRowCount();
		arbre1.setVisibleRowCount(numberRowShown);
		arbre1.setPreferredSize(arbre1.getPreferredScrollableViewportSize());

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				arbre1.updateUI();
			}
		});

		arbre1.setEnabled(true);

		// System.out.println("Updating AddonsPanel arbre1 done...");
	}

	private void loadAddonGroups() {

		// System.out.println("Updating AddonsPanel arbre2...");

		arbre2.setEnabled(false);

		racine2 = profileService.getAddonGroups();
		addonService.resolveAddonGroups(racine2);
		profileService.setAddonGroups(racine2);

		addonService.checkMissingAddons(racine2, new ArrayList<String>());
		addonService.checkDuplicateAddons(racine2, racine1);
		addonService.checkDuplicateAddonsSelection(racine2,
				new ArrayList<String>());

		arbre2.removeAll();
		AddonTreeModel addonTreeModel = new AddonTreeModel(racine2);
		arbre2.setModel(addonTreeModel);
		addonTreeModel.fireTreeStructureChanged();

		Set<TreePath> expandedTreePaths = new HashSet<TreePath>();
		getPathsForInitialExpansion(new TreePath(arbre2.getModel().getRoot()),
				expandedTreePaths);
		for (TreePath treePath : expandedTreePaths) {
			arbre2.expandPath(treePath);
		}

		int numberRowShown = arbre2.getRowCount();
		arbre2.setVisibleRowCount(numberRowShown);
		arbre2.setPreferredSize(arbre2.getPreferredScrollableViewportSize());
		if (numberRowShown == 0) {
			arbre2.setToolTipText("Right click to add a group");
		} else {
			arbre2.setToolTipText(null);
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				arbre2.updateUI();
			}
		});

		arbre2.setEnabled(true);

		// System.out.println("Updating AddonsPanel arbre2 done...");
	}

	private void refreshAddonGroups() {

		arbre2.setEnabled(false);

		addonService.checkMissingAddons(racine2, new ArrayList<String>());
		addonService.checkDuplicateAddons(racine2, racine1);
		addonService.checkDuplicateAddonsSelection(racine2,
				new ArrayList<String>());

		Set<TreePath> expandedTreePaths = new HashSet<TreePath>();
		getExpandedTreePaths(new TreePath(arbre2.getModel().getRoot()),
				expandedTreePaths);

		((AddonTreeModel) arbre2.getModel()).fireTreeStructureChanged();

		for (TreePath treePath : expandedTreePaths) {
			arbre2.expandPath(treePath);
		}

		int numberRowShown = arbre2.getRowCount();
		arbre2.setVisibleRowCount(numberRowShown);
		arbre2.setPreferredSize(arbre2.getPreferredScrollableViewportSize());
		if (numberRowShown == 0) {
			arbre2.setToolTipText("Right click to add a group");
		} else {
			arbre2.setToolTipText(null);
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				arbre2.updateUI();
			}
		});

		arbre2.setEnabled(true);
	}

	private void getPathsForInitialExpansion(TreePath path,
			Set<TreePath> expandedTreePaths) {

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
					if (child.isLeaf()) {
						TreeLeafDTO leaf = (TreeLeafDTO) child;
						if (leaf.isMissing()) {
							nbMissingNodes++;
						}
					}
				}
				if (nbSelectedNodes != 0 && nbSelectedNodes != nbNodes) {
					expandedTreePaths.add(path);
				} else if (nbSelectedNodes != 0 && nbMissingNodes != 0) {
					expandedTreePaths.add(path);
				}

				for (TreeNodeDTO child : treeDirectoryDTO.getList()) {
					getPathsForInitialExpansion(path.pathByAddingChild(child),
							expandedTreePaths);
				}
			}
		}
	}

	private void getExpandedTreePaths(TreePath path,
			Set<TreePath> expandedTreePaths) {

		if (path != null) {
			TreeNodeDTO treeNodeDTO = (TreeNodeDTO) path.getLastPathComponent();
			if (!treeNodeDTO.isLeaf()) {
				TreeDirectoryDTO directory = (TreeDirectoryDTO) treeNodeDTO;
				if (arbre2.isExpanded(path)) {
					expandedTreePaths.add(path);
				}
				for (TreeNodeDTO child : directory.getList()) {
					getExpandedTreePaths(path.pathByAddingChild(child),
							expandedTreePaths);
				}
			}
		}
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
	}

	private void onArbre2Collapsed(TreePath path) {
		int numberRowShown = arbre2.getRowCount();
		arbre2.setVisibleRowCount(numberRowShown);
		arbre2.setPreferredSize(arbre2.getPreferredScrollableViewportSize());
		addonGroupsPanelScrollPane.updateUI();
		arbre2.setSelectionPath(null);
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

	private TreeNodeDTO[] getSelectedNodes() {

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

	/* */

	private void popupActionPerformed(ActionEvent evt) {

		if (evt.getActionCommand().equals("Add Group")) {
			groupManager.addGroup();
		} else if (evt.getActionCommand().equals("Duplicate")) {
			groupManager.duplicateGroup();
		} else if (evt.getActionCommand().equals("Rename")) {
			groupManager.renameGroup();
		} else if (evt.getActionCommand().equals("Remove")) {
			groupManager.removeGroup();
		}
	}

	private void checkBoxTreeSelectionPerformed() {
		configurationService.setViewModeTree(true);
		loadAvailableAddons();
	}

	private void checkBoxListSelectionPerformed() {
		configurationService.setViewModeTree(false);
		loadAvailableAddons();
	}

	private void buttonRefreshPerformed() {
		facade.getMainPanel().updateTabs(OP_ADDON_FILES_CHANGED);
	}

	private void checkBoxSelectAllPerformed() {
		groupManager.selectAll();
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

	private void buttonModsetsPerformed() {

		List<RepositoryDTO> repositoryDTOs = repositoryService
				.getRepositories();
		final List<String> repositoryNames = new ArrayList<String>();
		for (RepositoryDTO repositoryDTO : repositoryDTOs) {
			repositoryNames.add(repositoryDTO.getName());
		}

		ProgressModsetsSelectionDialog progressModsetSelectionPanel = new ProgressModsetsSelectionDialog(
				facade);
		progressModsetSelectionPanel.setVisible(true);
		progressModsetSelectionPanel.init(repositoryNames);
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

	public GroupManager getGroupManager() {
		return this.groupManager;
	}

	public class GroupManager {

		public void dragAndDrop(boolean isLeaf, TreePath newPath) {

			profileService.setAddonGroups(racine2);
			refreshAddonGroups();
			arbre2.expandPath(newPath);
			facade.getMainPanel().updateTabs(OP_GROUP_CHANGED);
		}

		public void addGroup() {

			TreeNodeDTO[] treeNodeDTOs = getSelectedNodes();
			boolean complete = false;
			TreePath treePath = null;

			if (treeNodeDTOs == null) {

				AddGroupDialog addGroupPanel = new AddGroupDialog(facade,
						racine2);
				addGroupPanel.init();
				addGroupPanel.setVisible(true);
				complete = addGroupPanel.isComplete();

			} else if (treeNodeDTOs.length != 0) {
				TreeNodeDTO node = treeNodeDTOs[0];
				treePath = arbre2.getSelectionPath();

				if (node.isLeaf()) {
					TreeLeafDTO leaf = (TreeLeafDTO) node;

					ModsetType modsetType = leaf.getParent().getModsetType();
					TreeDirectoryDTO parent = leaf.getParent().getParent();

					while (parent != null && modsetType == null) {
						modsetType = parent.getModsetType();
						parent = parent.getParent();
					}

					if (modsetType == null) {
						TreeDirectoryDTO directory = node.getParent();
						AddGroupDialog addGroupPanel = new AddGroupDialog(
								facade, directory);
						addGroupPanel.init();
						addGroupPanel.setVisible(true);
						complete = addGroupPanel.isComplete();
					}
				} else {
					TreeDirectoryDTO directory = (TreeDirectoryDTO) node;

					ModsetType modsetType = directory.getModsetType();
					TreeDirectoryDTO parent = directory.getParent();

					while (parent != null && modsetType == null) {
						modsetType = parent.getModsetType();
						parent = parent.getParent();
					}

					if (modsetType == null) {
						AddGroupDialog addGroupPanel = new AddGroupDialog(
								facade, directory);
						addGroupPanel.init();
						addGroupPanel.setVisible(true);
						complete = addGroupPanel.isComplete();
					}
				}
			}

			if (complete) {
				profileService.setAddonGroups(racine2);
				refreshAddonGroups();
				arbre2.expandPath(treePath);
				facade.getMainPanel().updateTabs(OP_GROUP_CHANGED);
			}
		}

		public void duplicateGroup() {

			TreeNodeDTO[] nodes = getSelectedNodes();
			boolean complete = false;

			if (nodes == null) {
				return;
			} else if (nodes.length == 0) {
				return;
			} else if (!nodes[0].isLeaf()) {
				TreeDirectoryDTO directory = (TreeDirectoryDTO) nodes[0];

				ModsetType modsetType = directory.getModsetType();
				TreeDirectoryDTO parent = directory.getParent();

				while (parent != null && modsetType == null) {
					modsetType = parent.getModsetType();
					parent = parent.getParent();
				}

				if (modsetType == null) {
					DuplicateGroupDialog duplicateGroupPanel = new DuplicateGroupDialog(
							facade, directory.getParent(), directory);
					duplicateGroupPanel.init();
					duplicateGroupPanel.setVisible(true);
					complete = duplicateGroupPanel.isComplete();
				}
			}

			if (complete) {
				profileService.setAddonGroups(racine2);
				refreshAddonGroups();
				facade.getMainPanel().updateTabs(OP_GROUP_CHANGED);
			}
		}

		public void renameGroup() {

			TreeNodeDTO[] nodes = getSelectedNodes();
			boolean complete = false;

			if (nodes == null) {
				return;
			} else if (nodes.length == 0) {
				return;
			} else if (!nodes[0].isLeaf()) {
				TreeDirectoryDTO directory = (TreeDirectoryDTO) nodes[0];

				ModsetType modsetType = directory.getModsetType();
				TreeDirectoryDTO parent = directory.getParent();

				while (parent != null && modsetType == null) {
					modsetType = parent.getModsetType();
					parent = parent.getParent();
				}

				if (modsetType == null) {
					RenameGroupDialog renameGroupPanel = new RenameGroupDialog(
							facade, directory.getParent(), directory);
					renameGroupPanel.init();
					renameGroupPanel.setVisible(true);
				}
			}

			if (complete) {
				profileService.setAddonGroups(racine2);
				refreshAddonGroups();
				facade.getMainPanel().updateTabs(OP_GROUP_CHANGED);
			}
		}

		public void removeGroup() {

			TreeNodeDTO[] nodes = getSelectedNodes();
			if (nodes == null) {
				return;
			} else if (nodes.length == 0) {
				return;
			} else {
				List<TreeNodeDTO> list = new ArrayList<TreeNodeDTO>();

				for (int i = 0; i < nodes.length; i++) {
					TreeNodeDTO node = (TreeNodeDTO) nodes[i];
					boolean isLeaf = node.isLeaf();
					if (isLeaf) {
						TreeLeafDTO leaf = (TreeLeafDTO) node;

						ModsetType modsetType = leaf.getParent()
								.getModsetType();
						TreeDirectoryDTO parent = leaf.getParent().getParent();

						while (parent != null && modsetType == null) {
							modsetType = parent.getModsetType();
							parent = parent.getParent();
						}

						if (modsetType == null) {
							list.add(node);
						}
					} else {
						TreeDirectoryDTO directory = (TreeDirectoryDTO) node;
						ModsetType modsetType = directory.getModsetType();

						if (modsetType != null) {
							list.add(node);
						} else {
							TreeDirectoryDTO parent = directory.getParent();

							while (parent != null && modsetType == null) {
								modsetType = parent.getModsetType();
								parent = parent.getParent();
							}

							if (modsetType == null) {
								list.add(node);
							}
						}
					}
				}

				for (int i = 0; i < list.size(); i++) {
					TreeNodeDTO node = list.get(i);
					TreeNodeDTO parent = node.getParent();
					if (parent != null) {
						if (configurationService.getDefaultModset() != null) {
							if (configurationService.getDefaultModset().equals(
									nodes[i].getName())) {
								configurationService.setDefautlModset(null);
							}
						}
						((TreeDirectoryDTO) parent).removeTreeNode(node);
					}
				}

				profileService.setAddonGroups(racine2);
				refreshAddonGroups();
				facade.getMainPanel().updateTabs(OP_GROUP_CHANGED);
			}
		}

		public void select() {

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

			profileService.setAddonGroups(racine2);
			refreshAddonGroups();
			facade.getMainPanel().updateTabs(OP_ADDON_SELECTION_CHANGED);
		}

		public void selectAll() {

			if (checkBoxSelectAll.isSelected()) {
				selectAllDescending(racine2);
			} else {
				deselectAllDescending(racine2);
			}

			profileService.setAddonGroups(racine2);
			refreshAddonGroups();
			facade.getMainPanel().updateTabs(OP_ADDON_SELECTION_CHANGED);
		}

		public void select(String groupName) {

			for (TreeNodeDTO node : racine2.getList()) {
				if (node.getName().equals(groupName)) {
					deselectAllDescending(racine2);
					TreeDirectoryDTO treeDirectoryDTO = treeDirectoryDTO = (TreeDirectoryDTO) node;
					selectAllDescending(treeDirectoryDTO);
					profileService.setAddonGroups(racine2);
					refreshAddonGroups();
					break;
				}
			}
		}

		public void addGroupFromRepository(List<String> repositoryNames,
				boolean updated) {

			onAddGroupFromRepository(repositoryNames, updated);
			addonService.resolveAddonGroups(racine2);
			profileService.setAddonGroups(racine2);
			refreshAddonGroups();
			facade.getMainPanel().updateTabs(OP_GROUP_CHANGED);
		}

		private void onAddGroupFromRepository(List<String> repositoryNames,
				boolean updated) {

			for (String repositoryName : repositoryNames) {
				TreeDirectoryDTO directory = repositoryService
						.getGroupFromRepository(repositoryName, false);
				if (directory != null) {
					TreeNodeDTO nodeToRemove = null;
					List<String> selectdAddonPaths = new ArrayList<String>();
					for (TreeNodeDTO node : racine2.getList()) {
						if (node.getName().equals(repositoryName)) {
							nodeToRemove = node;
						}
					}
					if (nodeToRemove != null) {
						getSelectedAddonPaths(nodeToRemove, selectdAddonPaths);
						racine2.getList().remove(nodeToRemove);
					}
					directory.setName(repositoryName);
					directory.setModsetType(ModsetType.REPOSITORY);
					directory.setModsetRepositoryName(repositoryName);
					directory.setUpdated(updated);
					directory.setParent(racine2);
					racine2.addTreeNode(directory);
					setSelectedPaths(directory, selectdAddonPaths);
				}
			}
		}

		public void addGroupFromEvents(List<EventDTO> eventDTOs, boolean updated) {

			onAddGroupFromEvents(eventDTOs, updated);
			addonService.resolveAddonGroups(racine2);
			profileService.setAddonGroups(racine2);
			refreshAddonGroups();
			facade.getMainPanel().updateTabs(OP_GROUP_CHANGED);
		}

		public void onAddGroupFromEvents(List<EventDTO> eventDTOs,
				boolean updated) {

			for (EventDTO eventDTO : eventDTOs) {

				TreeNodeDTO nodeToRemove = null;
				List<String> selectdAddonPaths = new ArrayList<String>();
				for (TreeNodeDTO node : racine2.getList()) {
					if (node.getName().equals(eventDTO.getName())) {
						nodeToRemove = node;
					}
				}
				if (nodeToRemove != null) {
					getSelectedAddonPaths(nodeToRemove, selectdAddonPaths);
					racine2.getList().remove(nodeToRemove);
				}

				TreeDirectoryDTO directory = new TreeDirectoryDTO();
				directory.setName(eventDTO.getName());
				directory.setModsetType(ModsetType.EVENT);
				directory.setModsetRepositoryName(eventDTO.getRepositoryName());
				directory.setUpdated(updated);
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
				setSelectedPaths(directory, selectdAddonPaths);
			}
		}

		public void updateGroupModsets(String updateRepositoryName) {

			System.out
					.println("Updating modset selection within addon groups...");

			List<String> repositoryGroupModsets = new ArrayList<String>();
			for (TreeNodeDTO node : racine2.getList()) {
				if (node instanceof TreeDirectoryDTO) {
					TreeDirectoryDTO directory = (TreeDirectoryDTO) node;
					String repositoryName = directory.getModsetRepositoryName();
					if (directory.getModsetType() != null
							&& repositoryName != null) {
						if (directory.getModsetType().equals(
								ModsetType.REPOSITORY)) {
							if (repositoryName.equals(updateRepositoryName)) {
								if (!repositoryGroupModsets
										.contains(repositoryName)) {
									repositoryGroupModsets.add(repositoryName);
								}
							}
						}
					}
				}
			}

			onAddGroupFromRepository(repositoryGroupModsets, true);

			List<EventDTO> eventGroupModsets = new ArrayList<EventDTO>();
			for (TreeNodeDTO node : racine2.getList()) {
				if (node instanceof TreeDirectoryDTO) {
					TreeDirectoryDTO directory = (TreeDirectoryDTO) node;
					String repositoryName = directory.getModsetRepositoryName();
					if (directory.getModsetType() != null
							&& repositoryName != null) {
						if (directory.getModsetType().equals(ModsetType.EVENT)) {
							if (repositoryName.equals(updateRepositoryName)) {
								String eventDTOName = directory.getName();
								List<EventDTO> eventDTOs = repositoryService
										.getEvents(repositoryName);
								for (EventDTO eventDTO : eventDTOs) {
									if (eventDTO.getName().equals(eventDTOName)) {
										eventGroupModsets.add(eventDTO);
									}
								}
							}
						}
					}
				}
			}

			onAddGroupFromEvents(eventGroupModsets, true);

			addonService.resolveAddonGroups(racine2);
			profileService.setAddonGroups(racine2);
			refreshAddonGroups();

			System.out.println("Addon groups update done.");
		}
	}
}
