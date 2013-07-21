package fr.soe.a3s.ui.repositoryEditor.outline;

import org.netbeans.swing.outline.RowModel;

import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class AddonSyncRowModel implements RowModel {

	private String[] columnNames = { "Destination folder", "      Change" };
	private Object[][] data = {};

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	// public Object getValueAt(int row, int col) {
	// return data[row][col];
	// }

	@Override
	public Class getColumnClass(int col) {
		// return getValueAt(0, c).getClass();
		switch (col) {
		case 0:
			return String.class;
		case 1:
			return String.class;
		default:
			assert false;
		}
		return null;
	}

	@Override
	public boolean isCellEditable(Object object, int col) {
//		if (col == 0) {
//			return false;
//		}
//		else if (col == 1) {
//			return true;
//		}
		return true;
	}

	@Override
	public Object getValueFor(Object object, int col) {
		SyncTreeNodeDTO syncTreeNodeDTO = (SyncTreeNodeDTO) object;
		if (col==-2){
			return syncTreeNodeDTO;
		}
		else if (!syncTreeNodeDTO.isLeaf()) {
			SyncTreeDirectoryDTO syncTreeDirectoryDTO = (SyncTreeDirectoryDTO) syncTreeNodeDTO;
			if (syncTreeDirectoryDTO.isMarkAsAddon()) {
				switch (col) {
				case 0:
					return syncTreeNodeDTO.getDestinationPath();
				case 1:
					return "select folder";
				}
			}
		}
		return null;
	}

	@Override
	public void setValueFor(Object arg0, int arg1, Object arg2) {

	}

}
