package fr.soe.a3s.ui.repositoryEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
import fr.soe.a3s.dto.TreeNodeDTO;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.service.FtpService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.mainEditor.tree.AddonTreeModel;
import fr.soe.a3s.ui.mainEditor.tree.CheckTreeCellRenderer;
import fr.soe.a3s.ui.mainEditor.tree.MyRenderer2;
import fr.soe.a3s.ui.repositoryEditor.events.EventAddPanel;
import fr.soe.a3s.ui.repositoryEditor.events.EventRenamePanel;
import fr.soe.a3s.ui.repositoryEditor.tree.AddonSyncTreeModel;

public class EventsPanel extends JPanel implements UIConstants {

	private Facade facade;
	private JButton buttonNew;
	private JButton buttonRemove;
	private JScrollPane scrollPane1;
	private AddonSyncTreeModel addonSyncTreeModel;
	private JTree arbre;
	private JScrollPane tableScrollPane;
	private JList listEvents;
	private JButton buttonEdit;
	private String repositoryName;
	private RepositoryService repositoryService = new RepositoryService();
	private FtpService ftpService = new FtpService();
	private List<EventDTO> eventDTOs;
	private TreeDirectoryDTO racine1;
	private AddonTreeModel addonTreeModel1;
	private JButton buttonSaveUpload;
	private TreePath arbreTreePath;
	private JPopupMenu popup;
	private JMenuItem menuItemSetRequired;
	private JMenuItem menuItemSetOptional;

	public EventsPanel(Facade facade) {

		this.facade = facade;
		this.facade.setEventsPanel(this);
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
				ImageIcon addIcon = new ImageIcon(ADD);
				buttonNew.setIcon(addIcon);
				hBox.add(buttonNew);
				buttonEdit = new JButton("");
				ImageIcon editIcon = new ImageIcon(EDIT);
				buttonEdit.setIcon(editIcon);
				hBox.add(buttonEdit);
				buttonRemove = new JButton("");
				ImageIcon deleteIcon = new ImageIcon(DELETE);
				buttonRemove.setIcon(deleteIcon);
				hBox.add(buttonRemove);
				buttonSaveUpload = new JButton("");
				ImageIcon saveUploadIcon = new ImageIcon(UPLOAD);
				buttonSaveUpload.setIcon(saveUploadIcon);
				hBox.add(buttonSaveUpload);
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

				MyRenderer2 myRenderer2 = new MyRenderer2();
				arbre.setCellRenderer(myRenderer2);

				CheckTreeCellRenderer renderer = new CheckTreeCellRenderer(
						myRenderer2);
				arbre.setCellRenderer(renderer);
			}

			JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					listEventsPanel, addonsSelectionPanel);
			splitPane.setOneTouchExpandable(false);
			splitPane.setDividerLocation(130);
			flattenSplitPane(splitPane);
			vertBox1.add(splitPane);
		}

		/* Right clic menu */
		popup = new JPopupMenu();

		menuItemSetRequired = new JMenuItem("Set reqired");
		menuItemSetRequired.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				popupActionPerformed(evt);
			}
		});
		menuItemSetRequired.setActionCommand("Set reqired");
		popup.add(menuItemSetRequired);

		menuItemSetOptional = new JMenuItem("Set optional");
		menuItemSetOptional.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				popupActionPerformed(evt);
			}
		});
		menuItemSetOptional.setActionCommand("Set optional");
		popup.add(menuItemSetOptional);

		// popup.addPopupMenuListener(new PopupMenuListener() {
		//
		// @Override
		// public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		// menuItemSetRequired.setEnabled(true);
		// menuItemSetOptional.setEnabled(true);
		// }
		//
		// @Override
		// public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		//
		// }
		//
		// @Override
		// public void popupMenuCanceled(PopupMenuEvent e) {
		//
		// }
		// });

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
		buttonSaveUpload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonUploadPerformed();
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
				//System.out.println(arbreTreePath);
			}
		});
		arbre.addMouseListener(new MouseAdapter() {

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

				TreeNodeDTO treeNodeDTO = (TreeNodeDTO) arbre
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
				saveSelection();
				refreshViewArbre();
			}

			public void mouseReleased(MouseEvent e) {
//				if (e.isPopupTrigger()) {
//					popup.show((JComponent) e.getSource(), e.getX(), e.getY());
//				}
				if (e.isPopupTrigger()) {
					popup.show((JComponent) e.getSource(), e.getX(), e.getY());
				} else if (SwingUtilities.isRightMouseButton(e)) {
					popup.show((JComponent) e.getSource(), e.getX(), e.getY());
				}
			}
		});
		arbre.addTreeExpansionListener(new TreeExpansionListener() {
			public void treeExpanded(TreeExpansionEvent event) {
				onArbreExpanded(event.getPath());
			}

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
		buttonSaveUpload.setToolTipText("Upload events informations");
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
		deselectAllDescending(racine1);

		// Desable selection
		arbre.setEnabled(false);

		EventAddPanel eventEditPanel = new EventAddPanel(facade, repositoryName);
		eventEditPanel.init();
		eventEditPanel.setVisible(true);
	}

	private void buttonEditPerformed() {

		int index = listEvents.getSelectedIndex();

		if (index != -1) {
			listEvents.clearSelection();

			// Desable selection
			arbre.setEnabled(false);

			EventRenamePanel eventRenamePanel = new EventRenamePanel(facade,
					repositoryName);
			eventRenamePanel.init(eventDTOs.get(index).getName(), eventDTOs
					.get(index).getDescription());
			eventRenamePanel.setVisible(true);
			updateListEvents();
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
				uploadInformations();
			} catch (RepositoryException e) {
				JOptionPane.showMessageDialog(facade.getMainPanel(),
						e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void buttonUploadPerformed() {
		uploadInformations();
	}

	private void uploadInformations() {

		UploadPanel uploadPanel = new UploadPanel(facade, repositoryName);
		uploadPanel.setVisible(true);
		uploadPanel.init();
	}

	public void updateListEvents() {

		try {
			eventDTOs = this.repositoryService.getEvents(repositoryName);
			if (eventDTOs != null) {
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
		} catch (RepositoryException e) {
			String[] eventNames = new String[0];
			listEvents.clearSelection();
			listEvents.setListData(eventNames);
			int numberLigneShown = eventDTOs.size();
			listEvents.setVisibleRowCount(numberLigneShown);
			listEvents.setPreferredSize(listEvents
					.getPreferredScrollableViewportSize());
			scrollPane1.updateUI();
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Repository", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updateListAddons() {

		try {
			TreeDirectoryDTO treeDirectoryDTO = repositoryService
					.getEventAddonSelection(repositoryName);

			if (treeDirectoryDTO != null) {
				arbre.removeAll();
				racine1 = treeDirectoryDTO;
				addonTreeModel1 = new AddonTreeModel(racine1);
				arbre.setModel(addonTreeModel1);
				int numberRowShown = arbre.getRowCount();
				arbre.setVisibleRowCount(numberRowShown);
				arbre.setPreferredSize(arbre
						.getPreferredScrollableViewportSize());
				arbre.updateUI();
			}
		} catch (RepositoryException e) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					e.getMessage(), "Repository", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updateSelection() {

		int index = listEvents.getSelectedIndex();

		if (index != -1) {
			arbre.setEnabled(true);
			EventDTO eventDTO = eventDTOs.get(index);
			Map<String, Boolean> map = eventDTO.getAddonNames();
			deselectAllDescending(racine1);
			setSelection(racine1, map);
			refreshViewArbre();
		}
	}

	private void setSelection(TreeNodeDTO treeNodeDTO, Map<String, Boolean> map) {

		if (treeNodeDTO.isLeaf()) {
			if (map.containsKey(treeNodeDTO.getName())) {
				treeNodeDTO.setSelected(true);
				selectAllAscending(treeNodeDTO);
				boolean optional = map.get(treeNodeDTO.getName());
				treeNodeDTO.setOptional(optional);
			} else {
				treeNodeDTO.setSelected(false);
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
			Map<String, Boolean> map = eventDTO.getAddonNames();
			map.clear();
			getSelection(racine1, map);
			repositoryService.saveEvent(repositoryName, eventDTO);
		}
	}

	private void getSelection(TreeNodeDTO treeNodeDTO, Map<String, Boolean> map) {

		if (treeNodeDTO.isLeaf()) {
			if (treeNodeDTO.isSelected()
					&& !map.containsKey(treeNodeDTO.getName())) {
				map.put(treeNodeDTO.getName(), treeNodeDTO.isOptional());
			}
		} else {
			TreeDirectoryDTO treeDirectoryDTO = (TreeDirectoryDTO) treeNodeDTO;
			for (TreeNodeDTO n : treeDirectoryDTO.getList()) {
				getSelection(n, map);
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
			treeNodeDTO.setSelected(true);
			selectAllAscending(treeNodeDTO);
			treeNodeDTO.setOptional(false);
		}
		saveSelection();
		refreshViewArbre();
	}

	private void setOptional() {

		TreeNodeDTO treeNodeDTO = (TreeNodeDTO) arbreTreePath
				.getLastPathComponent();

		if (treeNodeDTO != null) {
			treeNodeDTO.setSelected(true);
			selectAllAscending(treeNodeDTO);
			treeNodeDTO.setOptional(true);
		}
		saveSelection();
		refreshViewArbre();
	}
}
