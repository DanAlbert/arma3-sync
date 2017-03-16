package fr.soe.a3s.ui.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import fr.soe.a3s.dto.configuration.ExternalApplicationDTO;
import fr.soe.a3s.service.ConfigurationService;
import fr.soe.a3s.ui.Facade;
import fr.soe.a3s.ui.ImageResizer;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.main.dialogs.ExternalApplicationsEditionDialog;

public class ExternalApplicationsPanel extends JPanel implements UIConstants {

	private final Facade facade;
	private final JButton buttonAdd, buttonDelete, buttonEdit, buttonEnable;
	private final JTable tableApplications;
	private final MyTableModel model;
	private final JScrollPane jScrollPane1;
	private final ConfigurationService configurationService = new ConfigurationService();
	private static final String ENABLE_TEXT = "YES";
	private static final String DISABLE_TEXT = "NO";

	public ExternalApplicationsPanel(Facade facade) {
		this.facade = facade;
		this.facade.setExternalApplicationsPanel(this);
		this.setLayout(new BorderLayout());

		Box vertBox1 = Box.createVerticalBox();
		vertBox1.add(Box.createVerticalStrut(10));
		this.add(vertBox1);

		JPanel containerPanel = new JPanel();
		containerPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "External Applications"));
		vertBox1.add(containerPanel);
		containerPanel.setLayout(new BorderLayout());

		model = new MyTableModel();
		tableApplications = new JTable(model);
		tableApplications.setShowGrid(false);
		tableApplications.setFillsViewportHeight(true);
		tableApplications.setRowSelectionAllowed(true);
		tableApplications.setAutoCreateRowSorter(false);
		tableApplications.getTableHeader().setReorderingAllowed(false);
		tableApplications.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jScrollPane1 = new JScrollPane(tableApplications);
		jScrollPane1.setBorder(BorderFactory
				.createEtchedBorder(BevelBorder.LOWERED));
		containerPanel.add(jScrollPane1, BorderLayout.CENTER);

		TableColumn col0 = tableApplications.getColumnModel().getColumn(0);
		col0.setMaxWidth(100);

		// Adapt cells Height to font height
		Font fontTable = UIManager.getFont("Table.font");
		FontMetrics metrics = tableApplications.getFontMetrics(fontTable);
		int fontHeight = metrics.getAscent() + metrics.getDescent()
				+ metrics.getLeading();
		tableApplications.setRowHeight(fontHeight);

		MyTableCellRenderer renderer = new MyTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		col0.setCellRenderer(renderer);

		/*
		 * http://bugs.java.com/view_bug.do?bug_id=6429812
		 */
		String osName = System.getProperty("os.name");
		boolean changeFont = true;
		if (!osName.toLowerCase().contains("windows server")) {
			JTableHeader header = tableApplications.getTableHeader();
			if (header != null) {
				header.setDefaultRenderer(new HeaderRenderer(tableApplications));
			}
		}

		Box vertBox2 = Box.createVerticalBox();
		vertBox2.add(Box.createVerticalStrut(25));
		buttonAdd = new JButton();
		ImageIcon addIcon = new ImageIcon(
				ImageResizer.resizeToScreenResolution(ADD));
		buttonAdd.setIcon(addIcon);
		vertBox2.add(buttonAdd);
		buttonEdit = new JButton();
		ImageIcon editIcon = new ImageIcon(
				ImageResizer.resizeToScreenResolution(EDIT));
		buttonEdit.setIcon(editIcon);
		vertBox2.add(buttonEdit);
		buttonDelete = new JButton();
		ImageIcon deleteIcon = new ImageIcon(
				ImageResizer.resizeToScreenResolution(DELETE));
		buttonDelete.setIcon(deleteIcon);
		vertBox2.add(buttonDelete);
		buttonEnable = new JButton();
		ImageIcon enableIcon = new ImageIcon(
				ImageResizer.resizeToScreenResolution(ONOFF));
		buttonEnable.setIcon(enableIcon);
		vertBox2.add(buttonEnable);
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
		buttonEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonEditPerformed();
			}
		});
		buttonEnable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonEnablePerformed();
			}
		});
		setContextualHelp();
	}

	private void setContextualHelp() {

		buttonAdd.setToolTipText("Add a new application");
		buttonEdit.setToolTipText("Edit");
		buttonDelete.setToolTipText("Delete");
		buttonEnable
				.setToolTipText("Set the selected application to run at game launch");
	}

	public void update(int flag) {

		if (flag == OP_PROFILE_CHANGED) {
			updateTableApplications();
		}
	}

	private void updateTableApplications() {

		tableApplications.setEnabled(false);

		List<ExternalApplicationDTO> externalApplicationDTOs = configurationService
				.getExternalApplications();
		model.setDataSize(externalApplicationDTOs.size());
		Iterator<ExternalApplicationDTO> iter = externalApplicationDTOs
				.iterator();
		int i = 0;
		while (iter.hasNext()) {
			ExternalApplicationDTO externalApplicationDTO = iter.next();
			boolean active = externalApplicationDTO.isEnable();
			String description = externalApplicationDTO.getName();
			String executablePath = externalApplicationDTO.getExecutablePath();
			String parameters = externalApplicationDTO.getParameters();
			model.addRow(i, i);
			model.setValueAt(active, i, 0);
			model.setValueAt(description, i, 1);
			model.setValueAt(executablePath, i, 2);
			model.setValueAt(parameters, i, 3);
			i++;
		}

		model.fireTableDataChanged();
		jScrollPane1.updateUI();

		tableApplications.setEnabled(true);
	}

	private void buttonAddPerformed() {

		List<ExternalApplicationDTO> list = configurationService
				.getExternalApplications();
		ExternalApplicationDTO externalApplicationDTO = new ExternalApplicationDTO();
		externalApplicationDTO.setEnable(true);
		externalApplicationDTO.setName("New Application");
		externalApplicationDTO.setExecutablePath("");
		externalApplicationDTO.setParameters("");
		list.add(externalApplicationDTO);
		configurationService.saveExternalApps(list);
		updateTableApplications();
	}

	private void buttonDeletePerformed() {

		int index = tableApplications.getSelectedRow();

		if (index == -1 || index >= tableApplications.getRowCount()) {
			return;
		}

		List<ExternalApplicationDTO> list = configurationService
				.getExternalApplications();
		list.remove(index);
		configurationService.saveExternalApps(list);
		updateTableApplications();
		if (index != 0) {
			tableApplications.setRowSelectionInterval(index - 1, index - 1);
		}
	}

	private void buttonEditPerformed() {

		int index = tableApplications.getSelectedRow();

		if (index == -1 || index >= tableApplications.getRowCount()) {
			return;
		}

		String value = (String) model.getValueAt(index, 0);
		boolean active = false;
		if (value.equals(ENABLE_TEXT)) {
			active = true;
		}
		String description = (String) model.getValueAt(index, 1);
		String executablePath = (String) model.getValueAt(index, 2);
		String parameters = (String) model.getValueAt(index, 3);

		ExternalApplicationDTO externalApplicationDTO = new ExternalApplicationDTO();
		externalApplicationDTO.setEnable(active);
		externalApplicationDTO.setName(description);
		externalApplicationDTO.setExecutablePath(executablePath);
		externalApplicationDTO.setParameters(parameters);

		ExternalApplicationsEditionDialog externalApplicationsEditionPanel = new ExternalApplicationsEditionDialog(
				facade, externalApplicationDTO);
		externalApplicationsEditionPanel.init();
		externalApplicationsEditionPanel.setVisible(true);

		// After UI has closed
		List<ExternalApplicationDTO> list = configurationService
				.getExternalApplications();
		list.set(index, externalApplicationDTO);
		configurationService.saveExternalApps(list);
		updateTableApplications();
	}

	private void buttonEnablePerformed() {

		int index = tableApplications.getSelectedRow();

		if (index == -1 || index >= tableApplications.getRowCount()) {
			return;
		}

		List<ExternalApplicationDTO> list = configurationService
				.getExternalApplications();
		ExternalApplicationDTO externalApplicationDTO = list.get(index);
		boolean active = externalApplicationDTO.isEnable();
		externalApplicationDTO.setEnable(!active);
		configurationService.saveExternalApps(list);
		updateTableApplications();
		tableApplications.setRowSelectionInterval(index, index);
	}

	class MyTableModel extends AbstractTableModel {
		private final String[] columnNames = { "Active", "Description",
				".exe/.bat/.sh Path", "Parameters" };
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
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		/*
		 * Don't need to implement this method unless your table's editable.
		 */
		@Override
		public boolean isCellEditable(int row, int col) {
			// Note that the data/cell address is constant,
			// no matter where the cell appears onscreen.
			return false;
		}

		/*
		 * Don't need to implement this method unless your table's data can
		 * change.
		 */
		@Override
		public void setValueAt(Object value, int row, int col) {
			if (value instanceof Integer) {
				value = Integer.toString((Integer) value);
			} else if (value instanceof Boolean) {
				boolean enabled = (Boolean) value;
				if (enabled) {
					value = new String(ENABLE_TEXT);
				} else {
					value = new String(DISABLE_TEXT);
				}
			}

			data[row][col] = value;
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

	class MyTableCellRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int col) {

			Component c = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, col);
			if (value.toString().equals(ENABLE_TEXT)) {
				c.setFont(c.getFont().deriveFont(Font.BOLD));
				c.setForeground(new Color(0, 128, 0));
			} else if (value.toString().equals(DISABLE_TEXT)) {
				c.setFont(c.getFont().deriveFont(Font.BOLD));
				c.setForeground(Color.RED);
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

			if (col == 0) {
				renderer.setHorizontalAlignment(JLabel.CENTER);
			} else {
				renderer.setHorizontalAlignment(JLabel.LEFT);
			}
			return renderer.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, col);
		}
	}
}
