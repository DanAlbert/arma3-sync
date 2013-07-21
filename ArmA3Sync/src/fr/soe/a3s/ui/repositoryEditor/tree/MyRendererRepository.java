package fr.soe.a3s.ui.repositoryEditor.tree;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.ui.UIConstants;

public class MyRendererRepository extends DefaultTreeCellRenderer implements UIConstants {

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean isLeaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, isLeaf,
				row, hasFocus);

		TreePath path = tree.getPathForRow(row);
		if (path != null) {
			SyncTreeNodeDTO syncTreeNodeDTO = (SyncTreeNodeDTO) value;
			setIcon(syncTreeNodeDTO);
		}
		return this;
	}
	
	private void setIcon(SyncTreeNodeDTO syncTreeNodeDTO){
		if (!syncTreeNodeDTO.isLeaf()) {
			SyncTreeDirectoryDTO syncTreeDirectoryDTO = (SyncTreeDirectoryDTO) syncTreeNodeDTO;
			for (SyncTreeNodeDTO n : syncTreeDirectoryDTO.getList()) {
				if (n.isUpdated() || n.isDeleted()) {
					setIcon(new ImageIcon(EXCLAMATION));
				}
				setIcon(n);
			}
		}
	}
}
