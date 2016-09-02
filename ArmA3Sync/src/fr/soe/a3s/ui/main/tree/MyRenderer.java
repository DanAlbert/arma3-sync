package fr.soe.a3s.ui.main.tree;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import fr.soe.a3s.dto.TreeLeafDTO;
import fr.soe.a3s.dto.TreeNodeDTO;
import fr.soe.a3s.ui.UIConstants;

public class MyRenderer extends DefaultTreeCellRenderer implements UIConstants {

	@Override
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
			TreeLeafDTO leaf = (TreeLeafDTO) treeNodeDTO;
			setIcon(new ImageIcon(BRICK));
			// if (leaf.isDuplicate()) {
			// setIcon(new ImageIcon(EXCLAMATION));
			// } else {
			// setIcon(new ImageIcon(BRICK));
			// }
		}
	}
}
