package fr.soe.a3s.ui.repositoryEditor.tree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;
import fr.soe.a3s.ui.UIConstants;
import fr.soe.a3s.ui.mainEditor.tree.IndeterminateIcon;

public class CheckTreeCellRendererRepository extends JPanel implements TreeCellRenderer {
	// private AddonTreeModel selectionModel;
	private TreeCellRenderer delegate;
	private JCheckBox checkBox = new JCheckBox();
	private Color selectionBorderColor, selectionForeground,
			selectionBackground, textForeground, textBackground;

	public CheckTreeCellRendererRepository(TreeCellRenderer delegate) {
		this.delegate = delegate;
		// this.selectionModel = selectionModel;
		setLayout(new BorderLayout());
		setOpaque(false);
		checkBox.setOpaque(false);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		Component renderer = delegate.getTreeCellRendererComponent(tree, value,
				selected, expanded, leaf, row, hasFocus);

		TreePath path = tree.getPathForRow(row);
		if (path != null) {
			SyncTreeNodeDTO syncTreeNodeDTO = (SyncTreeNodeDTO) value;

			// Updated addons
			if (!syncTreeNodeDTO.isUpdated() && syncTreeNodeDTO.isDeleted()) {
				renderer.setForeground(Color.BLUE);
			} else if (syncTreeNodeDTO.isUpdated()) {
				renderer.setForeground(Color.RED);
			} else {
				renderer.setBackground(UIManager
						.getColor("Tree.textBackground"));
			}

			if (syncTreeNodeDTO.isSelected()) {
				checkBox.setSelected(Boolean.TRUE);
			} else {
				checkBox.setSelected(Boolean.FALSE);
			}

			// Selected addons
			// if (syncTreeNodeDTO.isLeaf()) {
			// if (syncTreeNodeDTO.isSelected()) {
			// checkBox.setSelected(Boolean.TRUE);
			// } else {
			// checkBox.setSelected(Boolean.FALSE);
			// }
			// } else if (!syncTreeNodeDTO.isLeaf()) {
			// if (syncTreeNodeDTO.isSelected()) {
			// checkBox.setIcon(null);
			// SyncTreeDirectoryDTO syncTreeDirectoryDTO =
			// (SyncTreeDirectoryDTO) syncTreeNodeDTO;
			// int nbNodes = syncTreeDirectoryDTO.getList().size();
			// int nbSelectedNodes = 0;
			// for (SyncTreeNodeDTO t : syncTreeDirectoryDTO.getList()) {
			// if (t.isSelected()) {
			// nbSelectedNodes++;
			// }
			// }
			// if (nbNodes != 0 && nbSelectedNodes != 0
			// && nbSelectedNodes < nbNodes) {
			// // checkBox.setSelected(Boolean.FALSE);
			// checkBox.setIcon(new IndeterminateIcon());
			// } else {
			// checkBox.setSelected(Boolean.TRUE);
			// }
			// }else {
			// checkBox.setSelected(Boolean.FALSE);
			// }
			// }
		}
		removeAll();
		add(checkBox, BorderLayout.WEST);
		add(renderer, BorderLayout.CENTER);
		return this;
	}
}
