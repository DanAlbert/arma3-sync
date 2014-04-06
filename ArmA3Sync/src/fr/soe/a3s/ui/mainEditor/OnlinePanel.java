package fr.soe.a3s.ui.mainEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import fr.soe.a3s.dto.EventDTO;
import fr.soe.a3s.dto.RepositoryDTO;
import fr.soe.a3s.dto.configuration.FavoriteServerDTO;
import fr.soe.a3s.exception.RepositoryException;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.service.RepositoryService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.UIConstants;

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
public class OnlinePanel extends JPanel implements UIConstants {

	private Facade facade;
	private JButton buttonAdd, buttonDelete;
	private JTable tableServers;
	private MyTableModel model;
	private JScrollPane jScrollPane1;
	private ConfigurationService configurationService = new ConfigurationService();
	private boolean isModifying = false;
	private JComboBox comboBoxModsets;

	public OnlinePanel(final Facade facade) {
		this.facade = facade;
		this.facade.setOnlinePanel(this);
		this.setLayout(new BorderLayout());

		Box vertBox1 = Box.createVerticalBox();
		vertBox1.add(Box.createVerticalStrut(10));
		this.add(vertBox1, BorderLayout.CENTER);

		JPanel containerPanel = new JPanel();
		containerPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Favorite servers"));
		vertBox1.add(containerPanel);
		containerPanel.setLayout(new BorderLayout());

		model = new MyTableModel();
		tableServers = new JTable(model);
		tableServers.setShowGrid(false);
		tableServers.setFillsViewportHeight(true);
		tableServers.setRowSelectionAllowed(false);
		tableServers.setCellSelectionEnabled(true);
		tableServers.setAutoCreateRowSorter(false);
		tableServers.getTableHeader().setReorderingAllowed(false);
		tableServers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jScrollPane1 = new JScrollPane(tableServers);
		jScrollPane1.setBorder(BorderFactory
				.createEtchedBorder(BevelBorder.LOWERED));
		containerPanel.add(jScrollPane1, BorderLayout.CENTER);

		TableColumn col1 = tableServers.getColumnModel().getColumn(1);
		//col1.setMaxWidth(300);

		TableColumn col2 = tableServers.getColumnModel().getColumn(2);
		col2.setMaxWidth(50);

		TableColumn col3 = tableServers.getColumnModel().getColumn(3);
		col3.setMaxWidth(150);

		comboBoxModsets = new JComboBox();
		comboBoxModsets.setFocusable(false);
		TableColumn col4 = tableServers.getColumnModel().getColumn(4);
		col4.setCellEditor(new DefaultCellEditor(comboBoxModsets));

		Box vertBox2 = Box.createVerticalBox();
		vertBox2.add(Box.createVerticalStrut(25));
		buttonAdd = new JButton();
		ImageIcon addIcon = new ImageIcon(ADD);
		buttonAdd.setIcon(addIcon);
		vertBox2.add(buttonAdd);
		buttonDelete = new JButton();
		ImageIcon deleteIcon = new ImageIcon(DELETE);
		buttonDelete.setIcon(deleteIcon);
		vertBox2.add(buttonDelete);
		this.add(vertBox2, BorderLayout.EAST);

		buttonAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonAddPerformed();
			}
		});
		buttonDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonDeletePerformed();
			}
		});
		model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent arg0) {
				int index = tableServers.getSelectedRow();
				if (index != -1 && !isModifying) {
					List<FavoriteServerDTO> list = new ArrayList<FavoriteServerDTO>();
					int nbRows = model.getRowCount();
					for (int i = 0; i < nbRows; i++) {
						String description = (String) model.getValueAt(i, 0);
						String ipAddress = (String) model.getValueAt(i, 1);
						int port = Integer.parseInt((String) model.getValueAt(
								i, 2));
						String password = (String) model.getValueAt(i, 3);
						String modsetName = (String) model.getValueAt(i, 4);
						FavoriteServerDTO favoriteServerDTO = new FavoriteServerDTO();
						favoriteServerDTO.setName(description);
						favoriteServerDTO.setIpAddress(ipAddress);
						favoriteServerDTO.setPort(port);
						favoriteServerDTO.setPassword(password);
						favoriteServerDTO.setModsetName(modsetName);
						list.add(favoriteServerDTO);
					}
					configurationService.setFavoriteServers(list);
					facade.getLaunchPanel().init();
					init();
				}
			}
		});
		comboBoxModsets.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				updatecomboBoxModsets();
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {
			}
		});
		setContextualHelp();
	}

	private void setContextualHelp() {

		buttonAdd.setToolTipText("Add a new favorite server");
		buttonDelete.setToolTipText("Delete the selected server");
	}

	public void init() {

		isModifying = true;
		List<FavoriteServerDTO> favoriteServersDTO = configurationService
				.getFavoriteServers();
		model.setDataSize(favoriteServersDTO.size());
		Iterator<FavoriteServerDTO> iter = favoriteServersDTO.iterator();
		int i = 0;
		while (iter.hasNext()) {
			FavoriteServerDTO favoriteServerDTO = iter.next();
			String description = favoriteServerDTO.getName();
			String ipAddress = favoriteServerDTO.getIpAddress();
			int port = favoriteServerDTO.getPort();
			String password = favoriteServerDTO.getPassword();
			String modsetName = favoriteServerDTO.getModsetName();
			if (description == null) {
				description = "";
			}
			if (ipAddress == null) {
				ipAddress = "";
			}
			if (password == null) {
				password = "";
			}
			if (modsetName == null) {
				modsetName = "";
			}
			model.addRow(i, i);
			model.setValueAt(description, i, 0);
			model.setValueAt(ipAddress, i, 1);
			model.setValueAt(port, i, 2);
			model.setValueAt(password, i, 3);
			model.setValueAt(modsetName, i, 4);
			i++;
		}
		isModifying = false;
	}

	private void buttonAddPerformed() {

		List<FavoriteServerDTO> list = configurationService
				.getFavoriteServers();
		FavoriteServerDTO favoriteServerDTO = new FavoriteServerDTO();
		favoriteServerDTO.setName("New Server");
		favoriteServerDTO.setIpAddress("0.0.0.0");
		favoriteServerDTO.setPassword("");
		favoriteServerDTO.setModsetName("");
		favoriteServerDTO.setPort(0);
		list.add(favoriteServerDTO);
		configurationService.setFavoriteServers(list);
		facade.getLaunchPanel().init();
		init();
		model.fireTableDataChanged();
		jScrollPane1.updateUI();
	}

	private void buttonDeletePerformed() {

		int index = tableServers.getSelectedRow();

		if (index == -1) {
			return;
		}

		List<FavoriteServerDTO> list = new ArrayList<FavoriteServerDTO>();
		int nbRows = model.getRowCount();
		for (int i = 0; i < nbRows; i++) {
			String description = (String) model.getValueAt(i, 0);
			String ipAddress = (String) model.getValueAt(i, 1);
			int port = Integer.parseInt((String) model.getValueAt(i, 2));
			String password = (String) model.getValueAt(i, 3);
			FavoriteServerDTO favoriteServerDTO = new FavoriteServerDTO();
			favoriteServerDTO.setName(description);
			favoriteServerDTO.setIpAddress(ipAddress);
			favoriteServerDTO.setPort(port);
			favoriteServerDTO.setPassword(password);
			list.add(favoriteServerDTO);
		}

		list.remove(index);
		configurationService.setFavoriteServers(list);
		facade.getLaunchPanel().init();
		init();
		model.fireTableDataChanged();
		jScrollPane1.updateUI();
		if (index != 0) {
			tableServers.setRowSelectionInterval(index - 1, index - 1);
		}
	}

	private void updatecomboBoxModsets() {

		RepositoryService repositoryService = new RepositoryService();
		List<RepositoryDTO> repositoryDTOs = repositoryService
				.getRepositories();

		List<String> list = new ArrayList<String>();
		for (RepositoryDTO repositoryDTO : repositoryDTOs) {
			list.add(repositoryDTO.getName());
		}

		Collections.sort(list);

		ComboBoxModel modsetsModel = new DefaultComboBoxModel(
				new String[] { "" });
		comboBoxModsets.setModel(modsetsModel);
		for (String stg : list) {
			comboBoxModsets.addItem(stg);
		}
	}

	class MyTableModel extends AbstractTableModel {
		private String[] columnNames = { "Description", "IP Addresse", "Port",
				"Password", "Repository" };
		private Object[][] data = {};

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		/*
		 * JTable uses this method to determine the default renderer/ editor for
		 * each cell. If we didn't implement this method, then the last column
		 * would contain text ("true"/"false"), rather than a check box.
		 */
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		/*
		 * Don't need to implement this method unless your table's editable.
		 */
		public boolean isCellEditable(int row, int col) {
			// Note that the data/cell address is constant,
			// no matter where the cell appears onscreen.
			return true;
		}

		/*
		 * Don't need to implement this method unless your table's data can
		 * change.
		 */
		public void setValueAt(Object value, int row, int col) {

			if (value instanceof Integer) {
				data[row][col] = Integer.toString((Integer) value);
			} else {
				data[row][col] = value;
			}
			fireTableCellUpdated(row, col);
		}

		public void addRow(int firstRow, int lastRow) {
			fireTableRowsInserted(firstRow, lastRow);
		}

		public void setDataSize(int numberRows) {
			data = new Object[numberRows][5];
		}

		public Object[][] getData() {
			return data;
		}
	}
}
