package fr.soe.a3s.ui.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import fr.soe.a3s.constant.RepositoryStatus;
import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.exception.repository.RepositoryNotFoundException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.ColumnsAutoSizer;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ImageResizer;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.repository.RepositoryPanel;
import fr.soe.a3s.ui.repository.dialogs.connection.RepositoryEditionDialog;
import fr.soe.a3s.ui.repository.dialogs.progress.ProgressConnectionAsAdminDialog;
import fr.soe.a3s.ui.repository.dialogs.progress.ProgressSynchronizationDialog;

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
public class SyncPanel extends JPanel implements UIConstants {

	private Facade facade;
	private JTable tableRepositories;
	private JList listEvents;
	private MyTableModel model;
	private JScrollPane scrollPane1, scrollPane2;
	private JButton buttonNew, buttonEdit, buttonRemove, buttonSync1;
	private JButton buttonConnectToRepository;
	private JButton buttonCheckEvent;
	private JPanel containerPanel1, containerPanel2;
	private JButton buttonSync2;
	private JButton buttonAdmin;
	private JSplitPane splitPane;
	/* Tests */
	private boolean painted = false;
	private boolean isModifying = false;
	/* Data */
	// Map<EventDTO, RepositoryName>
	private final Map<EventDTO, String> mapEvents = new LinkedHashMap<EventDTO, String>();
	/* Services */
	private final RepositoryService repositoryService = new RepositoryService();

	public SyncPanel(final Facade facade) {
		this.facade = facade;
		this.facade.setSyncPanel(this);
		this.setLayout(new BorderLayout());

		Box vertBox1 = Box.createVerticalBox();
		vertBox1.add(Box.createVerticalStrut(10));
		this.add(vertBox1, BorderLayout.CENTER);

		JPanel addonRepositoriesPanel = new JPanel();
		JPanel eventsPanel = new JPanel();
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				addonRepositoriesPanel, eventsPanel);
		splitPane.setOneTouchExpandable(false);
		flattenSplitPane(splitPane);
		vertBox1.add(splitPane);
		{
			addonRepositoriesPanel.setLayout(new BorderLayout());
			{
				containerPanel1 = new JPanel();
				containerPanel1.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(),
						"Addons Repositories"));
				addonRepositoriesPanel.add(containerPanel1);
				containerPanel1.setLayout(new BorderLayout());

				model = new MyTableModel();
				tableRepositories = new JTable(model);
				tableRepositories.setShowGrid(false);
				tableRepositories.setFillsViewportHeight(true);
				tableRepositories.setRowSelectionAllowed(true);
				tableRepositories.setAutoCreateRowSorter(false);
				tableRepositories.getTableHeader().setReorderingAllowed(false);
				tableRepositories
						.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				scrollPane1 = new JScrollPane(tableRepositories);
				scrollPane1.setBorder(BorderFactory
						.createEtchedBorder(BevelBorder.LOWERED));
				containerPanel1.add(scrollPane1, BorderLayout.CENTER);

				// Adapt cells Height to font height
				Font fontTable = UIManager.getFont("Table.font");
				FontMetrics metrics = tableRepositories
						.getFontMetrics(fontTable);
				int fontHeight = metrics.getAscent() + metrics.getDescent()
						+ metrics.getLeading();
				tableRepositories.setRowHeight(fontHeight);

				TableColumn col3 = tableRepositories.getColumnModel()
						.getColumn(3);
				MyStatusColumnRenderer rendererColumn3 = new MyStatusColumnRenderer();
				rendererColumn3.setHorizontalAlignment(SwingConstants.CENTER);
				col3.setCellRenderer(rendererColumn3);

				/*
				 * http://bugs.java.com/view_bug.do?bug_id=6429812
				 */
				String osName = System.getProperty("os.name");
				if (!osName.toLowerCase().contains("windows server")) {
					JTableHeader header = tableRepositories.getTableHeader();
					if (header != null) {
						header.setDefaultRenderer(new HeaderRenderer(
								tableRepositories));
					}
				}
			}
			{
				Box vertBox = Box.createVerticalBox();
				vertBox.add(Box.createVerticalStrut(15));
				buttonNew = new JButton("");
				ImageIcon addIcon = new ImageIcon(
						ImageResizer.resizeToScreenResolution(ADD));
				buttonNew.setIcon(addIcon);
				vertBox.add(buttonNew);
				buttonEdit = new JButton("");
				ImageIcon editIcon = new ImageIcon(
						ImageResizer.resizeToScreenResolution(EDIT));
				buttonEdit.setIcon(editIcon);
				vertBox.add(buttonEdit);
				buttonRemove = new JButton("");
				ImageIcon deleteIcon = new ImageIcon(
						ImageResizer.resizeToScreenResolution(DELETE));
				buttonRemove.setIcon(deleteIcon);
				vertBox.add(buttonRemove);
				buttonSync1 = new JButton("");
				ImageIcon syncIcon = new ImageIcon(
						ImageResizer.resizeToScreenResolution(REFRESH));
				buttonSync1.setIcon(syncIcon);
				vertBox.add(buttonSync1);
				buttonAdmin = new JButton("");
				ImageIcon adminIcon = new ImageIcon(
						ImageResizer.resizeToScreenResolution(ADMIN));
				buttonAdmin.setIcon(adminIcon);
				vertBox.add(buttonAdmin);
				buttonConnectToRepository = new JButton("");
				ImageIcon joinIcon = new ImageIcon(
						ImageResizer.resizeToScreenResolution(CONNECT));
				buttonConnectToRepository.setIcon(joinIcon);
				vertBox.add(buttonConnectToRepository);
				addonRepositoriesPanel.add(vertBox, BorderLayout.EAST);
			}

			eventsPanel.setLayout(new BorderLayout());
			{
				containerPanel2 = new JPanel();
				containerPanel2.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(), "Events"));
				eventsPanel.add(containerPanel2);
				containerPanel2.setLayout(new BorderLayout());

				listEvents = new JList();
				scrollPane2 = new JScrollPane(listEvents);
				scrollPane2.setBorder(BorderFactory
						.createEtchedBorder(BevelBorder.LOWERED));
				containerPanel2.add(scrollPane2, BorderLayout.CENTER);
			}
			{
				Box vertBox = Box.createVerticalBox();
				vertBox.add(Box.createVerticalStrut(15));
				buttonSync2 = new JButton("");
				ImageIcon syncIcon = new ImageIcon(
						ImageResizer.resizeToScreenResolution(REFRESH));
				buttonSync2.setIcon(syncIcon);
				vertBox.add(buttonSync2);
				buttonCheckEvent = new JButton("");
				ImageIcon checkIcon = new ImageIcon(
						ImageResizer.resizeToScreenResolution(CONNECT));
				buttonCheckEvent.setIcon(checkIcon);
				vertBox.add(buttonCheckEvent);
				eventsPanel.add(vertBox, BorderLayout.EAST);
			}
		}
		buttonNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
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
		buttonSync1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonSyncPerformed();
			}
		});
		buttonSync2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonSyncPerformed();
			}
		});
		buttonAdmin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonConnectAsAdminPerformed();
			}
		});
		buttonConnectToRepository.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonConnectToRepositoryPerformed();
			}
		});
		buttonCheckEvent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonCheckEventPerformed();
			}
		});
		model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent arg0) {
				if (isModifying) {
					return;
				} else {
					int index = tableRepositories.getSelectedRow();
					try {
						if (index != -1
								&& !(index >= tableRepositories.getRowCount())) {
							String name = (String) model.getValueAt(index, 0);
							boolean notify = (Boolean) model.getValueAt(index,
									1);
							boolean auto = (Boolean) model.getValueAt(index, 2);
							repositoryService.setRepositoryNotification(name,
									notify);
							repositoryService.setRepositoryAutoUpdate(name,
									auto);
							updateRepositoriesAndEvents();
							tableRepositories.setRowSelectionInterval(index,
									index);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		setContextualHelp();
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

	/**
	 * http://stackoverflow.com/questions/2311449/jsplitpane-splitting-50-
	 * precisely
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		if (!painted) {
			painted = true;
			splitPane.setDividerLocation(0.60);
		}
	}

	private void setContextualHelp() {

		buttonNew.setToolTipText("Add a new repository");
		buttonEdit.setToolTipText("Edit repository");
		buttonRemove.setToolTipText("Remove repository");
		buttonSync1.setToolTipText("Update repositories status");
		buttonSync2.setToolTipText("Update events list");
		buttonAdmin.setToolTipText("Administrate repository");
		buttonConnectToRepository.setToolTipText("Connect to repository");
		buttonCheckEvent.setToolTipText("Connect to event");
	}

	public void update(int flag) {

		if (flag == OP_PROFILE_CHANGED || flag == OP_REPOSITORY_CHANGED) {
			updateRepositoriesAndEvents();
		}
	}

	private void updateRepositoriesAndEvents() {

		isModifying = true;
		tableRepositories.setRowSelectionAllowed(false);
		tableRepositories.setEnabled(false);
		listEvents.setEnabled(false);

		mapEvents.clear();

		List<RepositoryDTO> repositoryDTOs = repositoryService
				.getRepositories();
		Collections.sort(repositoryDTOs);
		model.setDataSize(repositoryDTOs.size());
		Iterator<RepositoryDTO> iter = repositoryDTOs.iterator();
		int i = 0;
		while (iter.hasNext()) {
			RepositoryDTO repositoryDTO = iter.next();
			String name = repositoryDTO.getName();
			boolean notify = repositoryDTO.isNotify();
			boolean auto = repositoryDTO.isAuto();
			RepositoryStatus status = repositoryService
					.getRepositorySyncStatus(repositoryDTO.getName());
			List<EventDTO> eventDTOs = repositoryService.getEvents(name);
			for (EventDTO eventDTO : eventDTOs) {
				mapEvents.put(eventDTO, name);
			}
			model.addRow(i, i);
			model.setValueAt(name, i, 0);
			model.setValueAt(notify, i, 1);
			model.setValueAt(auto, i, 2);
			model.setValueAt(status.getDescription(), i, 3);
			i++;
		}

		String[] tab = new String[mapEvents.size()];
		int j = 0;
		for (Iterator<EventDTO> iter2 = mapEvents.keySet().iterator(); iter2
				.hasNext();) {
			EventDTO eventDTO = iter2.next();
			String text = eventDTO.getName();
			if (eventDTO.getDescription() != null) {
				String description = eventDTO.getDescription().trim();
				if (!description.isEmpty()) {
					text = text + " - " + eventDTO.getDescription();
				}
			}
			tab[j] = text;
			j++;
		}

		listEvents.setListData(tab);

		model.fireTableDataChanged();
		scrollPane1.updateUI();
		scrollPane2.updateUI();

		// Re-adjust columns size
		List<Integer> columnIndexes = new ArrayList<Integer>();
		columnIndexes.add(1);
		columnIndexes.add(2);
		columnIndexes.add(3);
		ColumnsAutoSizer.sizeColumnsToFit(tableRepositories, columnIndexes);

		tableRepositories.setRowSelectionAllowed(true);
		tableRepositories.setEnabled(true);
		listEvents.setEnabled(true);
		isModifying = false;
	}

	private void buttonNewPerformed() {

		RepositoryEditionDialog repositoryEditPanel = new RepositoryEditionDialog(
				facade);
		repositoryEditPanel.init();
		repositoryEditPanel.setVisible(true);
	}

	private void buttonEditPerformed() {

		if (isModifying) {
			return;
		}

		int index = tableRepositories.getSelectedRow();
		if (index == -1 || index > tableRepositories.getRowCount()) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Please select a repository.", "Information",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		String name = null;
		try {
			name = (String) model.getValueAt(index, 0);
		} catch (Exception e) {
		}
		if (name != null) {
			boolean closed = facade.getMainPanel().closeRepository(name);
			if (closed) {
				RepositoryEditionDialog repositoryEditPanel = new RepositoryEditionDialog(
						facade);
				repositoryEditPanel.init(name);
				repositoryEditPanel.setVisible(true);
			}
		}
	}

	private void buttonRemovePerformed() {

		if (isModifying) {
			return;
		}

		int index = tableRepositories.getSelectedRow();
		if (index == -1 || index >= tableRepositories.getRowCount()) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Please select a repository.", "Information",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		String name = (String) model.getValueAt(index, 0);
		try {
			boolean remove = repositoryService.removeRepository(name);
			if (remove) {
				System.out.println("Repository " + name + " removed.");
			} else {
				System.out.println("Failded to remove repository.");
			}
			facade.getMainPanel().updateTabs(OP_REPOSITORY_CHANGED);
		} catch (RepositoryNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void buttonSyncPerformed() {

		List<String> repositoryNames = new ArrayList<String>();
		for (final RepositoryDTO repositoryDTO : repositoryService
				.getRepositories()) {
			repositoryNames.add(repositoryDTO.getName());
		}

		if (repositoryNames.isEmpty()) {
			System.out.println("No repository to synchronize with.");
		} else {
			ProgressSynchronizationDialog synchronizingPanel = new ProgressSynchronizationDialog(
					facade);
			synchronizingPanel.setVisible(true);
			synchronizingPanel.init(repositoryNames);
		}
	}

	private void buttonConnectAsAdminPerformed() {

		if (isModifying) {
			return;
		}

		int index = tableRepositories.getSelectedRow();
		if (index == -1 || index >= tableRepositories.getRowCount()) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Please select a repository.", "Information",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		String repositoryName = (String) model.getValueAt(index, 0);
		if (repositoryName != null) {
			ProgressConnectionAsAdminDialog progressConnectionAsAdminPanel = new ProgressConnectionAsAdminDialog(
					facade, repositoryName);
			progressConnectionAsAdminPanel.setVisible(true);
			progressConnectionAsAdminPanel.init();
		}
	}

	private void buttonConnectToRepositoryPerformed() {

		if (isModifying) {
			return;
		}

		int index = tableRepositories.getSelectedRow();
		if (index == -1 || index >= tableRepositories.getRowCount()) {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Please select a repository.", "Information",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		String repositoryName = (String) model.getValueAt(index, 0);
		if (repositoryName != null) {
			RepositoryPanel repositoryPanel = facade.getMainPanel()
					.openRepository(repositoryName, true);
			if (repositoryPanel != null) {
				repositoryPanel.synchronize(repositoryName, null);
			}
		}
	}

	private void buttonCheckEventPerformed() {

		int index = listEvents.getSelectedIndex();
		if (index != -1 && !(index >= mapEvents.size())) {
			int j = 0;
			for (Iterator<EventDTO> iter2 = mapEvents.keySet().iterator(); iter2
					.hasNext();) {
				EventDTO eventDTO = iter2.next();
				if (index == j) {
					String repositoryName = mapEvents.get(eventDTO);
					RepositoryPanel repositoryPanel = facade.getMainPanel()
							.openRepository(repositoryName, true);
					if (repositoryPanel != null) {
						repositoryPanel.synchronize(repositoryName,
								eventDTO.getName());
					}
					break;
				}
				j++;
			}
		} else {
			JOptionPane.showMessageDialog(facade.getMainPanel(),
					"Please select an event.", "Information",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void enableAllButtons() {

		buttonNew.setEnabled(true);
		buttonEdit.setEnabled(true);
		buttonRemove.setEnabled(true);
		buttonSync1.setEnabled(true);
		buttonAdmin.setEnabled(true);
		buttonConnectToRepository.setEnabled(true);
		buttonSync2.setEnabled(true);
		buttonCheckEvent.setEnabled(true);
	}

	public void disableAllButtons() {

		buttonNew.setEnabled(false);
		buttonEdit.setEnabled(false);
		buttonRemove.setEnabled(false);
		buttonSync1.setEnabled(false);
		buttonAdmin.setEnabled(false);
		buttonConnectToRepository.setEnabled(false);
		buttonSync2.setEnabled(false);
		buttonCheckEvent.setEnabled(false);
	}

	private class MyTableModel extends AbstractTableModel {

		private final String[] columnNames = { "Name", "Notify", "Auto",
				"Status" };

		private Object[][] data = {};

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		}

		@Override
		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		/*
		 * JTable uses this method to determine the default renderer/ editor for
		 * each cell. If we didn't implement this method, then the last column
		 * would contain text ("true"/"false"), rather than a check box.
		 */
		@Override
		public Class getColumnClass(int col) {
			if (col == 1 || col == 2) {
				return Boolean.class;
			} else {
				return String.class;
			}
			// return getValueAt(0, c).getClass();
		}

		/*
		 * Don't need to implement this method unless your table's editable.
		 */
		@Override
		public boolean isCellEditable(int row, int col) {
			// Note that the data/cell address is constant,
			// no matter where the cell appears onscreen.
			if (col == 1 || col == 2) {
				return true;

			} else {
				return false;
			}
		}

		/*
		 * Don't need to implement this method unless your table's data can
		 * change.
		 */
		@Override
		public void setValueAt(Object value, int row, int col) {
			data[row][col] = value;
			fireTableCellUpdated(row, col);
		}

		public void addRow(int firstRow, int lastRow) {
			fireTableRowsInserted(firstRow, lastRow);
		}

		public void setDataSize(int numberRows) {
			data = new Object[numberRows][columnNames.length];
		}

		public Object[][] getData() {
			return data;
		}
	}

	private class MyStatusColumnRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int col) {

			Component c = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, col);

			Font fontTable = UIManager.getFont("Table.font");
			c.setFont(fontTable.deriveFont(Font.BOLD));

			if (value == null) {
				return null;
			}

			if (value.toString().equals(RepositoryStatus.OK.getDescription())) {
				c.setForeground(new Color(45, 125, 45));
			} else if (value.toString().equals(
					RepositoryStatus.UPDATED.getDescription())) {
				c.setForeground(Color.RED);
			} else if (value.toString().equals(
					RepositoryStatus.ERROR.getDescription())) {
				c.setForeground(Color.RED);
			} else {
				c.setForeground(Color.BLACK);
			}

			return c;
		}
	}

	private class HeaderRenderer implements TableCellRenderer {

		DefaultTableCellRenderer renderer;

		public HeaderRenderer(JTable table) {
			renderer = (DefaultTableCellRenderer) table.getTableHeader()
					.getDefaultRenderer();
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int col) {

			if (col > 0) {
				renderer.setHorizontalAlignment(JLabel.CENTER);
			} else {
				renderer.setHorizontalAlignment(JLabel.LEFT);
			}

			if (col == 1) {
				renderer.setToolTipText("Notify me on repository update");
			} else if (col == 2) {
				renderer.setToolTipText("Automatically synchronize files on repository update");
			} else if (col == 3) {
				renderer.setToolTipText("Show repository update status");
			} else {
				renderer.setToolTipText(null);
			}

			return renderer.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, col);
		}
	}
}
