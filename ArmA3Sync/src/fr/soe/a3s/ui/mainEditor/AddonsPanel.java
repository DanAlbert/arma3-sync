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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.TreeLeafDTO;
import fr.soe.a3s.dto.TreeNodeDTO;
import fr.soe.a3s.service.AddonService;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.service.LaunchService;
import fr.soe.a3s.service.ProfileService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.mainEditor.groups.AddonsAddGroupPanel;
import fr.soe.a3s.ui.mainEditor.groups.AddonsRenameGroupPanel;
import fr.soe.a3s.ui.mainEditor.tree.AddonTreeModel;
import fr.soe.a3s.ui.mainEditor.tree.CheckTreeCellRenderer;
import fr.soe.a3s.ui.mainEditor.tree.MyRenderer2;
import fr.soe.a3s.ui.mainEditor.tree.TreeDnD;
import fr.soe.a3s.ui.mainEditor.tree.TreeDnD2;

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
	private AddonService addonService = new AddonService();
	private TreeDirectoryDTO racine1;
	private TreeDirectoryDTO racine2;
	private AddonTreeModel addonTreeModel1, addonTreeModel2;
	private JPopupMenu popup;
	private TreeDnD treeDnD;
	private TreeDnD2 treeDnD2;
	private JMenuItem menuItemAddGroup, menuItemRename, menuItemRemove;
	private ConfigurationService configurationService = new ConfigurationService();
	private ProfileService profileService = new ProfileService();
	private LaunchService launchService = new LaunchService();
	private TreePath arbre2TreePath;
	private JButton buttonRefresh;
	private TreePath arbre2NewTreePath;
	private JCheckBox checkBoxSelectAll;
	private JCheckBox checkBoxExpandAll;
	private JButton buttonEvents;

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
		checkBoxList = new JCheckBox("List");
		ButtonGroup groupCheckBox = new ButtonGroup();
		groupCheckBox.add(checkBoxTree);
		groupCheckBox.add(checkBoxList);
		buttonRefresh = new JButton("Refresh");
		ImageIcon addIcon = new ImageIcon(REFRESH);
		buttonRefresh.setIcon(addIcon);
		controlPanel1.add(checkBoxTree);
		controlPanel1.add(checkBoxList);
		controlPanel1.add(buttonRefresh);

		checkBoxSelectAll = new JCheckBox("Select All");
		checkBoxExpandAll = new JCheckBox("Expand All");
		buttonEvents = new JButton("Events");
		ImageIcon checkIcon = new ImageIcon(CHECK);
		buttonEvents.setIcon(checkIcon);
		controlPanel2.add(checkBoxSelectAll);
		controlPanel2.add(checkBoxExpandAll);
		controlPanel2.add(buttonEvents);

		this.add(controlPanel, BorderLayout.NORTH);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(1, 2));

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
		arbre2.setCellRenderer(myRenderer2);

		CheckTreeCellRenderer renderer = new CheckTreeCellRenderer(myRenderer2);
		arbre2.setCellRenderer(renderer);

		/* TreeDnD */
		treeDnD = new TreeDnD(arbre1, arbre2, facade);

		/* Right clic menu */
		popup = new JPopupMenu();

		menuItemAddGroup = new JMenuItem("Add Group");
		menuItemAddGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				popupActionPerformed(evt);
			}
		});
		menuItemAddGroup.setActionCommand("Add Group");
		popup.add(menuItemAddGroup);

		menuItemRename = new JMenuItem("Rename");
		menuItemRename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				popupActionPerformed(evt);
			}
		});
		menuItemRename.setActionCommand("Rename");
		popup.add(menuItemRename);

		menuItemRemove = new JMenuItem("Remove");
		menuItemRemove.addActionListener(new ActionListener() {
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
						} else {
							menuItemAddGroup.setEnabled(true);
							menuItemRename.setEnabled(true);
							menuItemRemove.setEnabled(true);
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
			public void treeExpanded(TreeExpansionEvent event) {
				onArbre1Expanded(event.getPath());
			}

			public void treeCollapsed(TreeExpansionEvent event) {
				onArbre1Collapsed(event.getPath());
			}
		});
		arbre2.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent arg0) {
				arbre2TreePath = arbre2.getSelectionPath();
				//System.out.println(arbre2TreePath);
			}
		});
		arbre2.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (arbre2TreePath == null) {
					return;
				}

				TreeNodeDTO treeNodeDTO = (TreeNodeDTO) arbre2
						.getLastSelectedPathComponent();
				treeNodeDTO.setSelected(!treeNodeDTO.isSelected());

				if (treeNodeDTO.isLeaf() && !treeNodeDTO.isSelected()) {
					TreeDirectoryDTO treeDirectoryDTO = treeNodeDTO.getParent();
					treeDirectoryDTO.setSelected(false);
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

			public void mouseReleased(MouseEvent e) {
				// if (e.isPopupTrigger()) {
				// popup.show((JComponent) e.getSource(), e.getX(), e.getY());
				// }
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
				}
			}
		});
		arbre2.addTreeExpansionListener(new TreeExpansionListener() {
			public void treeExpanded(TreeExpansionEvent event) {
				onArbre2Expanded(event.getPath());
			}

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
		buttonEvents.addActionListener(new ActionListener() {

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
	}

	public void init() {

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

		arbre2.removeAll();
		racine2 = profileService.getAddonGroupsTree();
		addonTreeModel2 = new AddonTreeModel(racine2);
		arbre2.setModel(addonTreeModel2);
		highlightMissingAddons();
		refreshViewArbre2();
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

	private void addPerformed() {

		AddonsAddGroupPanel addGroupPanel = new AddonsAddGroupPanel(facade,
				racine2);
		addGroupPanel.init();
		addGroupPanel.setVisible(true);
	}

	private void renamePormed() {

		TreeNodeDTO[] nodes = getSelectedNode();
		if (nodes == null) {
			return;
		}
		if (nodes.length == 0) {
			return;
		}
		if (!nodes[0].isLeaf()) {
			// arbre2.setEditable(true);
			// arbre2.startEditingAtPath(arbre2.getSelectionPath());
			TreeNodeDTO selectedNodeDTO = nodes[0];
			AddonsRenameGroupPanel renameGroupPanel = new AddonsRenameGroupPanel(
					facade, racine2, selectedNodeDTO);
			renameGroupPanel.init();
			renameGroupPanel.setVisible(true);
		}
	}

	private void removePerformed() {

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
			// t.setSelected(treeDirectoryDTO.isSelected());
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

	public void createGroupFromEvent(String eventName,
			Map<String, Boolean> addonNames) {

		for (TreeNodeDTO node : racine2.getList()) {
			if (node.getName().equals(eventName)) {
				return;
			}
		}

		deselectAllDescending(racine2);

		TreeDirectoryDTO directory = new TreeDirectoryDTO();
		directory.setName(eventName);
		directory.setParent(racine2);
		racine2.addTreeNode(directory);
		for (Iterator<String> iter = addonNames.keySet().iterator(); iter
				.hasNext();) {
			String name = iter.next();
			boolean optional = addonNames.get(name);
			TreeLeafDTO leaf = new TreeLeafDTO();
			leaf.setName(name);
			leaf.setOptional(optional);
			leaf.setParent(directory);
			leaf.setSelected(true);
			directory.addTreeNode(leaf);
		}
		directory.setSelected(true);

		saveAddonGroups();
		updateAddonGroups();
		highlightMissingAddons();
		refreshViewArbre2();
		// arbre2.expandPath(new
		// TreePath(arbre2.getModel().getRoot()).pathByAddingChild(directory));
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
		if (paths != null) {
			TreeNodeDTO[] treeNodeDTOs = new TreeNodeDTO[paths.length];
			for (int i = 0; i < paths.length; i++) {
				treeNodeDTOs[i] = (TreeNodeDTO) paths[i].getLastPathComponent();
			}
			return treeNodeDTOs;
		} else {
			return null;
		}
	}

	public JCheckBox getCheckBoxTree() {
		return checkBoxTree;
	}

}
