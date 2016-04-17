package fr.soe.a3s.ui.repository.tree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

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
			if (!syncTreeNodeDTO.isUpdated() && syncTreeNodeDTO.isDeleted()) {
				renderer.setForeground(Color.BLUE);
			}
			// Updated addons
			else if (syncTreeNodeDTO.isUpdated()) {
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
		}
		removeAll();
		add(checkBox, BorderLayout.WEST);
		add(renderer, BorderLayout.CENTER);
		return this;
	}
}
