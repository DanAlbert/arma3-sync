package fr.soe.a3s.ui.repository;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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

import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.TreeLeafDTO;
import fr.soe.a3s.dto.TreeNodeDTO;
import fr.soe.a3s.exception.WritingException;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ImageResizer;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.main.tree.AddonTreeModel;
import fr.soe.a3s.ui.main.tree.CheckTreeCellRenderer;
import fr.soe.a3s.ui.main.tree.MyRenderer;
import fr.soe.a3s.ui.repository.dialogs.connection.UploadEventsConnectionDialog;
import fr.soe.a3s.ui.repository.dialogs.progress.ProgressUploadEventsDialog;
import fr.soe.a3s.ui.repository.events.EventAddDialog;
import fr.soe.a3s.ui.repository.events.EventRenameDialog;
import fr.soe.a3s.ui.repository.tree.AddonSyncTreeModel;

public class EventsPanel extends JPanel implements UIConstants {

	private Facade facade;
	private RepositoryPanel repositoryPanel;
	private JButton buttonNew;
	private JButton buttonRemove;
	private JScrollPane scrollPane1;
	private AddonSyncTreeModel addonSyncTreeModel;
	private JTree arbre;
	private JScrollPane tableScrollPane;
	private JList listEvents;
	private JButton buttonEdit;
	private TreeDirectoryDTO racine1;
	private AddonTreeModel addonTreeModel1;
	private JButton buttonUpload;
	private TreePath arbreTreePath;
	private JPopupMenu popup;
	private JMenuItem menuItemSetRequired;
	private JMenuItem menuItemSetOptional;
	private JButton buttonSaveToDisk;
	// Services
	private final RepositoryService repositoryService = new RepositoryService();
	// Data
	private String repositoryName;
	private List<EventDTO> eventDTOs;
	private JButton buttonDeleteFromDisk;
	private JButton buttonUploadOptions;

	public EventsPanel(Facade facade, RepositoryPanel repositoryPanel) {

		this.facade = facade;
		this.repositoryPanel = repositoryPanel;
		setLayout(new BorderLayout());

		Box vertBox1 = Box.createVerticalBox();
		vertBox1.add(Box.createVerticalStrut(5));
		this.add(vertBox1, BorderLayout.CENTER);
		{
			JPanel listEventsPanel = new JPanel();
			listEventsPanel.setLayout(new BorderLayout());
			{
				Box hBox = Box.createHorizontalBox();
				listEventsPanel.add(hBox, BorderLayout.NORTH);
				buttonNew = new JButton("");
				ImageIcon addIcon = new ImageIcon(
						ImageResizer.resizeToScreenResolution(ADD));
				buttonNew.setIcon(addIcon);
				hBox.add(buttonNew);
				buttonEdit = new JButton("");
				ImageIcon editIcon = new ImageIcon(
						ImageResizer.resizeToScreenResolution(EDIT));
				buttonEdit.setIcon(editIcon);
				hBox.add(buttonEdit);
				buttonRemove = new JButton("");
				ImageIcon deleteIcon = new ImageIcon(
						ImageResizer.resizeToScreenResolution(DELETE));
				buttonRemove.setIcon(deleteIcon);
				hBox.add(buttonRemove);
				buttonUpload = new JButton("");
				ImageIcon saveUploadIcon = new ImageIcon(
						ImageResizer.resizeToScreenResolution(UPLOAD));
				buttonUpload.setIcon(saveUploadIcon);
				hBox.add(buttonUpload);
				buttonUploadOptions = new JButton();
				ImageIcon uploadOptionIcon = new ImageIcon(
						ImageResizer.resizeToScreenResolution(PREFERENCES));
				buttonUploadOptions.setIcon(uploadOptionIcon);
				hBox.add(buttonUploadOptions);
				buttonSaveToDisk = new JButton("");
				ImageIcon saveToDiskIcon = new ImageIcon(
						ImageResizer.resizeToScreenResolution(SAVE));
				buttonSaveToDisk.setIcon(saveToDiskIcon);
				hBox.add(buttonSaveToDisk);
			}
			{
				listEvents = new JList();
				listEvents.getSelectionModel().setSelectionMode(
						ListSelectionModel.SINGLE_SELECTION);
				scrollPane1 = new JScrollPane(listEvents);
				scrollPane1.setBorder(BorderFactory
						.createEtchedBorder(BevelBorder.LOWERED));
				listEventsPanel.add(scrollPane1, BorderLayout.CENTER);
			}

			JPanel addonsSelectionPanel = new JPanel();
			addonsSelectionPanel.setLayout(new BorderLayout());
			{
				addonTreeModel1 = new AddonTreeModel(racine1);
				arbre = new JTree(addonTreeModel1);
				arbre.setRootVisible(false);
				arbre.setEditable(false);
				arbre.setShowsRootHandles(true);
				arbre.setToggleClickCount(0);
				arbre.getSelectionModel().setSelectionMode(
						TreeSelectionModel.SINGLE_TREE_SELECTION);
				tableScrollPane = new JScrollPane(arbre);
				addonsSelectionPanel.setBorder(BorderFactory
						.createTitledBorder(BorderFactory.createEtchedBorder(),
								"Addons selection"));
				addonsSelectionPanel.add(tableScrollPane, BorderLayout.CENTER);

				Font fontArbre = UIManager.getFont("Tree.font");
				FontMetrics metrics = arbre.getFontMetrics(fontArbre);
				int fontHeight = metrics.getAscent() + metrics.getDescent()
						+ metrics.getLeading();
				arbre.setRowHeight(fontHeight);

				MyRenderer myRenderer = new MyRenderer();
				CheckTreeCellRenderer renderer = new CheckTreeCellRenderer(
						myRenderer);
				arbre.setCellRenderer(renderer);
			}

			JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					listEventsPanel, addonsSelectionPanel);
			splitPane.setOneTouchExpandable(false);
			splitPane.setDividerLocation(200);
			flattenSplitPane(splitPane);
			vertBox1.add(splitPane);
		}

		/* Right clic menu */
		popup = new JPopupMenu();

		menuItemSetRequired = new JMenuItem("Set reqired");
		menuItemSetRequired.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				popupActionPerformed(evt);
			}
		});
		menuItemSetRequired.setActionCommand("Set reqired");
		popup.add(menuItemSetRequired);

		menuItemSetOptional = new JMenuItem("Set optional");
		menuItemSetOptional.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				popupActionPerformed(evt);
			}
		});
		menuItemSetOptional.setActionCommand("Set optional");
		popup.add(menuItemSetOptional);

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
				menuItemSetRequired.setEnabled(false);
				menuItemSetOptional.setEnabled(false);
				if (nodes != null) {
					if (nodes.length > 0) {
						if (nodes[0].isLeaf()) {
							menuItemSetRequired.setEnabled(true);
							menuItemSetOptional.setEnabled(true);
						}
					}
				}
			}
		});

		buttonNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonNewPerformed();
			}
		});
		buttonEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonEditPerformed();
			}
		});
		buttonRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonRemovePerformed();
			}
		});
		buttonUpload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonUploadPerformed();
			}
		});
		buttonUploadOptions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonUploadOptionsPerformed();
			}
		});
		buttonSaveToDisk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonSaveToDiskPerformed();
			}
		});
		listEvents.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				boolean adjust = event.getValueIsAdjusting();
				if (!adjust) {
					updateSelection();
				}
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
		arbre.addTreeExpansionListener(new TreeExpansionListener() {
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				onArbreExpanded(event.getPath());
			}

			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				onArbreCollapsed(event.getPath());
			}
		});
		setContextualHelp();
	}

	private void setContextualHelp() {

		buttonNew.setToolTipText("Add a new event");
		buttonEdit.setToolTipText("Edit selected event");
		buttonRemove.setToolTipText("Remove selected event");
		buttonUpload.setToolTipText("Upload events informations");
		buttonUploadOptions.setToolTipText("Set upload options");
		buttonSaveToDisk.setToolTipText("Save to disk (local repository)");
	}

	public void init(String repositoryName) {

		this.repositoryName = repositoryName;

		// Update list of Events
		updateListEvents();

		// Update list of selectable addons
		updateListAddons();

		// Desable selection
		arbre.setEnabled(false);
	}

	public void refreshViewArbre() {

		int numberRowShown = arbre.getRowCount();
		arbre.setVisibleRowCount(numberRowShown);
		arbre.setPreferredSize(arbre.getPreferredScrollableViewportSize());
		arbre.updateUI();
	}

	private void buttonNewPerformed() {

		listEvents.clearSelection();
		if (racine1 != null) {
			deselectAllDescending(racine1);
		}

		// Desable selection
		arbre.setEnabled(false);

		EventAddDialog eventEditPanel = new EventAddDialog(facade,
				repositoryName, this);
		eventEditPanel.setVisible(true);
	}

	private void buttonEditPerformed() {

		int index = listEvents.getSelectedIndex();

		if (index != -1) {
			listEvents.clearSelection();

			// Desable selection
			arbre.setEnabled(false);

			EventRenameDialog eventRenamePanel = new EventRenameDialog(facade,
					repositoryName, this);
			eventRenamePanel.init(eventDTOs.get(index).getName(), eventDTOs
					.get(index).getDescription());
			eventRenamePanel.setVisible(true);
		}
	}

	private void buttonRemovePerformed() {

		int index = listEvents.getSelectedIndex();

		if (index != -1) {
			try {
				deselectAllDescending(racine1);

				// Desable selection
				arbre.setEnabled(false);

				repositoryService.removeEvent(repositoryName,
						eventDTOs.get(index).getName());
				updateListEvents();
			} catch (RepositoryException e) {
				JOptionPane.showMessageDialog(facade.getMainPanel(),
						e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void buttonUploadPerformed() {

		if (eventDTOs == null) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Nothing to upload.", "Information",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			ProgressUploadEventsDialog uploadPanel = new ProgressUploadEventsDialog(
					facade, repositoryName);
			uploadPanel.setVisible(true);
			uploadPanel.init();
			facade.getMainPanel().updateTabs(OP_REPOSITORY_CHANGED);
		}
	}

	private void buttonUploadOptionsPerformed() {

		UploadEventsConnectionDialog uploadEventsOptionsPanel = new UploadEventsConnectionDialog(
				facade);
		uploadEventsOptionsPanel.init(repositoryName);
		uploadEventsOptionsPanel.setVisible(true);
	}

	private void buttonSaveToDiskPerformed() {

		// Repository path must be set
		String path = repositoryService.getRepositoryPath(repositoryName);
		if ("".equals(path) || path == null) {
			String message = "Repository main folder location is missing."
					+ "\n"
					+ "Please checkout the Repository Administation panel.";
			JOptionPane.showMessageDialog(facade.getMainPanel(), message,
					"Warning", JOptionPane.WARNING_MESSAGE);
		} else if (!(new File(path)).exists()) {
			String message = "Repository main folder location: " + path
					+ " does not exist.";
			JOptionPane.showMessageDialog(facade.getMainPanel(), message,
					"Warning", JOptionPane.ERROR_MESSAGE);
		} else {
			try {
				repositoryService.writeEvents(repositoryName);
				JOptionPane.showMessageDialog(facade.getMainPanel(),
						"Events informatons have been saved to the repository.",
						"Information", JOptionPane.INFORMATION_MESSAGE);
				facade.getMainPanel().updateTabs(OP_REPOSITORY_CHANGED);
			} catch (WritingException e) {
				JOptionPane.showMessageDialog(facade.getMainPanel(),
						e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void updateListEvents() {

		eventDTOs = this.repositoryService.getEvents(repositoryName);
		String[] eventTexts = new String[eventDTOs.size()];
		int i = 0;
		for (EventDTO eventDTO : eventDTOs) {
			if (eventDTO.getDescription() == null) {
				eventTexts[i] = eventDTO.getName();
			} else if (eventDTO.getDescription().isEmpty()) {
				eventTexts[i] = eventDTO.getName();
			} else {
				eventTexts[i] = eventDTO.getName() + " - "
						+ eventDTO.getDescription();
			}
			i++;
		}
		listEvents.clearSelection();
		listEvents.setListData(eventTexts);
		int numberLigneShown = eventDTOs.size();
		listEvents.setVisibleRowCount(numberLigneShown);
		listEvents.setPreferredSize(listEvents
				.getPreferredScrollableViewportSize());
		scrollPane1.updateUI();
	}

	private void updateListAddons() {

		TreeDirectoryDTO treeDirectoryDTO = repositoryService
				.getGroupFromRepository(repositoryName, true);

		if (treeDirectoryDTO != null) {
			arbre.removeAll();
			racine1 = treeDirectoryDTO;
			addonTreeModel1 = new AddonTreeModel(racine1);
			arbre.setModel(addonTreeModel1);
			int numberRowShown = arbre.getRowCount();
			arbre.setVisibleRowCount(numberRowShown);
			arbre.setPreferredSize(arbre.getPreferredScrollableViewportSize());
			arbre.updateUI();
		}
	}

	private void updateSelection() {

		int index = listEvents.getSelectedIndex();

		if (index != -1 && racine1 != null) {
			arbre.setEnabled(true);
			EventDTO eventDTO = eventDTOs.get(index);
			Map<String, Boolean> mapAddonNames = eventDTO.getAddonNames();
			Map<String, Boolean> mapUserconfigFolderNames = eventDTO
					.getUserconfigFolderNames();
			Map<String, Boolean> map = new HashMap<String, Boolean>();
			map.putAll(mapAddonNames);
			map.putAll(mapUserconfigFolderNames);
			deselectAllDescending(racine1);
			setSelection(racine1, map);
			refreshViewArbre();
		}
	}

	private void setSelection(TreeNodeDTO treeNodeDTO, Map<String, Boolean> map) {

		if (treeNodeDTO.isLeaf()) {
			TreeLeafDTO leaf = (TreeLeafDTO) treeNodeDTO;
			if (map.containsKey(treeNodeDTO.getName())) {
				treeNodeDTO.setSelected(true);
				selectAllAscending(treeNodeDTO);
				boolean optional = map.get(treeNodeDTO.getName());
				leaf.setOptional(optional);
			} else {
				leaf.setSelected(false);
			}
		} else {
			TreeDirectoryDTO treeDirectoryDTO = (TreeDirectoryDTO) treeNodeDTO;
			for (TreeNodeDTO n : treeDirectoryDTO.getList()) {
				setSelection(n, map);
			}
		}
	}

	private void saveSelection() {

		int index = listEvents.getSelectedIndex();

		if (index != -1) {
			EventDTO eventDTO = eventDTOs.get(index);
			Map<String, Boolean> mapAddonNames = eventDTO.getAddonNames();
			Map<String, Boolean> mapUserconfigFolderNames = eventDTO
					.getUserconfigFolderNames();
			mapAddonNames.clear();
			mapUserconfigFolderNames.clear();
			getAddonsSelection(racine1, mapAddonNames);
			getUserconfigSelection(racine1, mapUserconfigFolderNames);
			repositoryService.saveEvent(repositoryName, eventDTO);
		}
	}

	private void getAddonsSelection(TreeNodeDTO treeNodeDTO,
			Map<String, Boolean> mapAddonNames) {

		if (treeNodeDTO.isLeaf()) {
			TreeLeafDTO leaf = (TreeLeafDTO) treeNodeDTO;
			if (leaf.isSelected()
					&& !mapAddonNames.containsKey(treeNodeDTO.getName())) {
				TreeNodeDTO parent = treeNodeDTO.getParent();
				boolean found = false;
				while (parent != null) {
					if (parent.getName().toLowerCase().equals("userconfig")) {
						found = true;
						break;
					}
					parent = parent.getParent();
				}
				if (!found) {
					mapAddonNames.put(treeNodeDTO.getName(), leaf.isOptional());
				}
			}
		} else {
			TreeDirectoryDTO treeDirectoryDTO = (TreeDirectoryDTO) treeNodeDTO;
			for (TreeNodeDTO n : treeDirectoryDTO.getList()) {
				getAddonsSelection(n, mapAddonNames);
			}
		}
	}

	private void getUserconfigSelection(TreeNodeDTO treeNodeDTO,
			Map<String, Boolean> mapUserconfigFolderNames) {

		TreeDirectoryDTO treeDirectoryDTO = (TreeDirectoryDTO) treeNodeDTO;
		for (TreeNodeDTO n : treeDirectoryDTO.getList()) {
			if (n.getName().equals("userconfig")) {
				TreeDirectoryDTO userconfig = (TreeDirectoryDTO) n;
				for (TreeNodeDTO u : userconfig.getList()) {
					if (u.isSelected()
							&& !mapUserconfigFolderNames
									.containsKey(treeNodeDTO.getName())) {
						if (u.isLeaf()) {
							TreeLeafDTO leaf = (TreeLeafDTO) u;
							mapUserconfigFolderNames.put(u.getName(),
									leaf.isOptional());
						} else {
							mapUserconfigFolderNames.put(u.getName(), false);
						}
					}
				}
			}
		}
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

	private void onArbreExpanded(TreePath path) {
		int numberRowShown = arbre.getRowCount();
		arbre.setVisibleRowCount(numberRowShown);
		arbre.setPreferredSize(arbre.getPreferredScrollableViewportSize());
		scrollPane1.updateUI();
		arbre.setSelectionPath(null);
	}

	private void onArbreCollapsed(TreePath path) {
		int numberRowShown = arbre.getRowCount();
		arbre.setVisibleRowCount(numberRowShown);
		arbre.setPreferredSize(arbre.getPreferredScrollableViewportSize());
		scrollPane1.updateUI();
		arbre.setSelectionPath(null);
	}

	private void popupActionPerformed(ActionEvent evt) {

		if (evt.getActionCommand().equals("Set reqired")) {
			setRequired();
		} else if (evt.getActionCommand().equals("Set optional")) {
			setOptional();
		}
	}

	private void setRequired() {

		TreeNodeDTO treeNodeDTO = (TreeNodeDTO) arbreTreePath
				.getLastPathComponent();

		if (treeNodeDTO != null) {
			if (treeNodeDTO.isLeaf()) {
				TreeLeafDTO leaf = (TreeLeafDTO) treeNodeDTO;
				leaf.setOptional(false);
			}
		}
		saveSelection();
		refreshViewArbre();
	}

	private void setOptional() {

		TreeNodeDTO treeNodeDTO = (TreeNodeDTO) arbreTreePath
				.getLastPathComponent();

		if (treeNodeDTO != null) {
			if (treeNodeDTO.isLeaf()) {
				TreeLeafDTO leaf = (TreeLeafDTO) treeNodeDTO;
				leaf.setOptional(true);
			}
		}
		saveSelection();
		refreshViewArbre();
	}

	private void addonSelectionPerformed() {

		TreeNodeDTO treeNodeDTO = (TreeNodeDTO) arbre
				.getLastSelectedPathComponent();

		if (treeNodeDTO == null) {
			return;
		}

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
		saveSelection();
		refreshViewArbre();
	}

	private TreeNodeDTO[] getSelectedNode() {

		TreePath[] paths = arbre.getSelectionPaths();
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
}
