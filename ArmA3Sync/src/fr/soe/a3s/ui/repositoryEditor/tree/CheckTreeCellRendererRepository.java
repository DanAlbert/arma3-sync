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

import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class CheckTreeCellRendererRepository extends JPanel implements
		TreeCellRenderer {

	private final TreeCellRenderer delegate;
	private final JCheckBox checkBox = new JCheckBox();

	private Color selectionBorderColor, selectionForeground,
			selectionBackground, textForeground, textBackground;

	public CheckTreeCellRendererRepository(TreeCellRenderer delegate) {
		this.delegate = delegate;
		// this.selectionModel = selectionModel;
		setLayout(new BorderLayout());
		setOpaque(false);
		checkBox.setOpaque(false);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		Component renderer = delegate.getTreeCellRendererComponent(tree, value,
				selected, expanded, leaf, row, hasFocus);

		TreePath path = tree.getPathForRow(row);
		if (path != null) {
			SyncTreeNodeDTO syncTreeNodeDTO = (SyncTreeNodeDTO) value;
			// Deleted addons
			if (!syncTreeNodeDTO.isUpdated() && syncTreeNodeDTO.isDeleted()
					&& syncTreeNodeDTO.isLeaf()) {
				renderer.setForeground(Color.BLUE);
			}
			// Updated addons
			else if (syncTreeNodeDTO.isUpdated() && syncTreeNodeDTO.isLeaf()) {
				renderer.setForeground(Color.RED);
			} else {
				renderer.setBackground(UIManager
						.getColor("Tree.textBackground"));
				/* Set bold font for Addons */
				// if (!syncTreeNodeDTO.isLeaf()) {
				// SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO)
				// syncTreeNodeDTO;
				// Font font = UIManager.getFont("Tree.font");
				// if (directory.isMarkAsAddon()) {
				// Font newFont = new Font(font.getName(), Font.BOLD, 11);
				// renderer.setFont(newFont);
				// }
				// else {
				// renderer.setFont(font);
				// }
				// }
			}

			if (syncTreeNodeDTO.isSelected()) {
				checkBox.setSelected(Boolean.TRUE);
			} else {
				checkBox.setSelected(Boolean.FALSE);
			}
		}
		removeAll();
		add(checkBox, BorderLayout.WEST);
		add(renderer, BorderLayout.CENTER);
		return this;
	}
}
