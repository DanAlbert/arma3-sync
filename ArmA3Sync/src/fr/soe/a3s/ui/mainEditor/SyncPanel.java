package fr.soe.a3s.ui.mainEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
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
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import fr.soe.a3s.constant.RepositoryStatus;
import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.exception.repository.RepositoryException;
import fr.soe.a3s.exception.repository.RepositoryNotFoundException;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.repositoryEditor.dialogs.EditRepositoryPanel;
import fr.soe.a3s.ui.repositoryEditor.progressDialogs.ConnectionPanel;
import fr.soe.a3s.ui.repositoryEditor.progressDialogs.SynchronizingPanel;

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
	private boolean isModifying = false;
	private JButton buttonCheckEvent;
	private JPanel containerPanel1, containerPanel2;
	private JButton buttonSync2;

	/** EventDTO, Repository Name */
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

		JPanel centerPanel = new JPanel();
		vertBox1.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		{
			JPanel addonRepositoriesPanel = new JPanel();
			addonRepositoriesPanel.setLayout(new BorderLayout());
			centerPanel.add(addonRepositoriesPanel);
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

				TableColumn col1 = tableRepositories.getColumnModel()
						.getColumn(1);
				col1.setWidth(50);
				col1.setMaxWidth(50);
				TableColumn col2 = tableRepositories.getColumnModel()
						.getColumn(2);
				col2.setWidth(100);
				col2.setMaxWidth(100);

				MyTableCellRenderer renderer = new MyTableCellRenderer();
				renderer.setHorizontalAlignment(SwingConstants.CENTER);
				col2.setCellRenderer(renderer);

				JTableHeader header = tableRepositories.getTableHeader();
				header.setDefaultRenderer(new HeaderRenderer(tableRepositories));
			}
			{
				Box vertBox = Box.createVerticalBox();
				vertBox.add(Box.createVerticalStrut(15));
				buttonNew = new JButton("");
				ImageIcon addIcon = new ImageIcon(ADD);
				buttonNew.setIcon(addIcon);
				vertBox.add(buttonNew);
				buttonEdit = new JButton("");
				ImageIcon editIcon = new ImageIcon(EDIT);
				buttonEdit.setIcon(editIcon);
				vertBox.add(buttonEdit);
				buttonRemove = new JButton("");
				ImageIcon deleteIcon = new ImageIcon(DELETE);
				buttonRemove.setIcon(deleteIcon);
				vertBox.add(buttonRemove);
				buttonSync1 = new JButton("");
				ImageIcon syncIcon = new ImageIcon(REFRESH);
				buttonSync1.setIcon(syncIcon);
				vertBox.add(buttonSync1);
				buttonConnectToRepository = new JButton("");
				ImageIcon joinIcon = new ImageIcon(CONNECT);
				buttonConnectToRepository.setIcon(joinIcon);
				vertBox.add(buttonConnectToRepository);
				addonRepositoriesPanel.add(vertBox, BorderLayout.EAST);
			}
			JPanel eventsPanel = new JPanel();
			eventsPanel.setLayout(new BorderLayout());
			centerPanel.add(eventsPanel);
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
				ImageIcon syncIcon = new ImageIcon(REFRESH);
				buttonSync2.setIcon(syncIcon);
				vertBox.add(buttonSync2);
				buttonCheckEvent = new JButton("");
				ImageIcon checkIcon = new ImageIcon(CONNECT);
				buttonCheckEvent.setIcon(checkIcon);
				vertBox.add(buttonCheckEvent);
				vertBox.add(Box.createVerticalStrut(86));
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
		buttonConnectToRepository.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonConnectPerformed();
			}
		});
		buttonCheckEvent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonCheckEventPerformed();
			}
		});
		// tableRepositories.addMouseListener(new MouseAdapter() {
		// public void mouseClicked(MouseEvent e) {
		// if (isModifying) {
		// return;
		// } else if (e.getClickCount() >= 2) {
		// connectToRepository();
		// }
		// }
		// });
		model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent arg0) {
				if (isModifying) {
					return;
				} else {
					int index = tableRepositories.getSelectedRow();
					try {
						if (index != -1
								&& !(index > tableRepositories.getRowCount())) {
							String name = (String) model.getValueAt(index, 0);
							boolean notify = (Boolean) model.getValueAt(index,
									1);
							repositoryService.setRepositoryNotification(name,
									notify);
							init();
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

	private void setContextualHelp() {

		buttonNew.setToolTipText("Add a new repsository");
		buttonEdit.setToolTipText("Edit selected repository");
		buttonRemove.setToolTipText("Remove selected repository");
		buttonSync1.setToolTipText("Refresh");
		buttonConnectToRepository.setToolTipText("Connect to repository");
		buttonCheckEvent.setToolTipText("Check event");
		buttonSync2.setToolTipText("Refresh");
	}

	public void init() {

		isModifying = true;
		tableRepositories.setRowSelectionAllowed(false);
		tableRepositories.setEnabled(false);
		listEvents.setEnabled(false);

		List<RepositoryDTO> repositoryDTOs = repositoryService
				.getRepositories();
		Collections.sort(repositoryDTOs);
		model.setDataSize(repositoryDTOs.size());
		Iterator<RepositoryDTO> iter = repositoryDTOs.iterator();
		int i = 0;
		mapEvents.clear();
		while (iter.hasNext()) {
			RepositoryDTO repositoryDTO = iter.next();
			String name = repositoryDTO.getName();
			boolean notify = repositoryDTO.isNotify();
			String status = RepositoryStatus.INDETERMINATED.getDescription();

			try {
				RepositoryStatus repositoryStatus = repositoryService
						.getRepositoryStatus(repositoryDTO.getName());
				status = repositoryStatus.getDescription();

				List<EventDTO> eventDTOs = repositoryService.getEvents(name);
				if (eventDTOs != null) {
					for (EventDTO eventDTO : eventDTOs) {
						mapEvents.put(eventDTO, name);
					}
				}
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
			model.addRow(i, i);
			model.setValueAt(name, i, 0);
			model.setValueAt(notify, i, 1);
			model.setValueAt(status, i, 2);
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

		tableRepositories.setRowSelectionAllowed(true);
		tableRepositories.setEnabled(true);
		listEvents.setEnabled(true);
		isModifying = false;
	}

	private void refresh() {

		init();
		isModifying = true;
		model.fireTableDataChanged();
		isModifying = false;
		scrollPane1.updateUI();
		scrollPane2.updateUI();
	}

	private void buttonNewPerformed() {
		EditRepositoryPanel repositoryEditPanel = new EditRepositoryPanel(
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
		boolean closed = facade.getMainPanel().closeRepository(name);
		if (closed) {
			EditRepositoryPanel repositoryEditPanel = new EditRepositoryPanel(
					facade);
			repositoryEditPanel.init(name);
			repositoryEditPanel.setVisible(true);
		}
	}

	private void buttonRemovePerformed() {

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
		String name = (String) model.getValueAt(index, 0);
		try {
			boolean remove = repositoryService.removeRepository(name);
			if (remove) {
				System.out.println("Repository " + name + " removed.");
			} else {
				System.out.println("Failded to remove repository.");
			}
			refresh();
			facade.getOnlinePanel().init();
		} catch (RepositoryNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void buttonSyncPerformed() {
		SynchronizingPanel synchronizingPanel = new SynchronizingPanel(facade);
		synchronizingPanel.setVisible(true);
		List<String> repositoryNames = new ArrayList<String>();
		for (final RepositoryDTO repositoryDTO : repositoryService.getRepositories()) {
			repositoryNames.add(repositoryDTO.getName());
		}
		synchronizingPanel.init(repositoryNames);
	}

	private void buttonConnectPerformed() {
		connectToRepository();
	}

	private void connectToRepository() {

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
		String repositoryName = null;
		try {
			repositoryName = (String) model.getValueAt(index, 0);
		} catch (Exception e) {
			// e.printStackTrace();
		}

		if (repositoryName != null) {
			ConnectionPanel connectionPanel = new ConnectionPanel(facade,
					repositoryName, null);
			connectionPanel.setVisible(true);
			connectionPanel.init();
		}
	}

	private void buttonCheckEventPerformed() {

		int index = listEvents.getSelectedIndex();
		if (index != -1 && !(index > listEvents.getVisibleRowCount())) {
			int j = 0;
			for (Iterator<EventDTO> iter2 = mapEvents.keySet().iterator(); iter2
					.hasNext();) {
				EventDTO eventDTO = iter2.next();
				if (index == j) {
					String repositoryName = mapEvents.get(eventDTO);
					ConnectionPanel connectionPanel = new ConnectionPanel(
							facade, repositoryName, eventDTO.getName());
					connectionPanel.setVisible(true);
					connectionPanel.init();
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
		buttonConnectToRepository.setEnabled(true);
		buttonSync2.setEnabled(true);
		buttonCheckEvent.setEnabled(true);
	}

	public void disableAllButtons() {

		buttonNew.setEnabled(false);
		buttonEdit.setEnabled(false);
		buttonRemove.setEnabled(false);
		buttonSync1.setEnabled(false);
		buttonConnectToRepository.setEnabled(false);
		buttonSync2.setEnabled(false);
		buttonCheckEvent.setEnabled(false);
	}

	class MyTableModel extends AbstractTableModel {
		private final String[] columnNames = { "Name", "Notify", "Status" };

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
			if (col == 1) {
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
			if (col == 1) {
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
			data = new Object[numberRows][3];
		}

		public Object[][] getData() {
			return data;
		}
	}

	class MyTableCellRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int col) {

			Component c = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, col);
			c.setFont(new Font("Tohama", Font.BOLD, 12));

			if (value == null) {
				return null;
			}

			if (value.toString().equals(RepositoryStatus.OK.getDescription())) {
				c.setForeground(new Color(45, 125, 45));
			} else if (value.toString().equals(
					RepositoryStatus.UPDATED.getDescription())) {
				c.setForeground(Color.RED);
			} else if (value.toString().equals(
					RepositoryStatus.OUTOFSYNC.getDescription())) {
				c.setForeground(Color.RED);
			} else {
				c.setForeground(Color.BLACK);
			}
			return c;
		}
	}

	class HeaderRenderer implements TableCellRenderer {

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
			return renderer.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, col);
		}
	}
}
