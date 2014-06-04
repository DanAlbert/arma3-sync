package fr.soe.a3s.ui.mainEditor.tree;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import fr.soe.a3s.dto.TreeNodeDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.ui.UIConstants;

public class MyRenderer2 extends DefaultTreeCellRenderer implements UIConstants {

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean isLeaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, isLeaf,
				row, hasFocus);

		TreePath path = tree.getPathForRow(row);
		if (path != null) {
			TreeNodeDTO syncTreeNodeDTO = (TreeNodeDTO) value;
			setIcon(syncTreeNodeDTO);
		}
		return this;
	}

	private void setIcon(TreeNodeDTO treeNodeDTO) {

		if (treeNodeDTO.isLeaf()) {
			setIcon(new ImageIcon(BRICK));
		}
	}
}
