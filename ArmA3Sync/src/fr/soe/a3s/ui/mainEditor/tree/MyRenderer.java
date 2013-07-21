package fr.soe.a3s.ui.mainEditor.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

import fr.soe.a3s.dto.TreeNodeDTO;

@Deprecated
public class MyRenderer extends DefaultTreeCellRenderer {
	
	private JCheckBox leafRenderer = new JCheckBox();

	
	Color selectionBorderColor, selectionForeground, selectionBackground,
	textForeground, textBackground;

	public MyRenderer(){
		Font fontValue;
		fontValue = UIManager.getFont("Tree.font");
		if (fontValue != null) {
			leafRenderer.setFont(fontValue);
		}
		Boolean booleanValue = (Boolean) UIManager
				.get("Tree.drawsFocusBorderAroundIcon");
		leafRenderer.setFocusPainted((booleanValue != null)
				&& (booleanValue.booleanValue()));

		selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
		selectionForeground = UIManager.getColor("Tree.selectionForeground");
		selectionBackground = UIManager.getColor("Tree.selectionBackground");
		textForeground = UIManager.getColor("Tree.textForeground");
		textBackground = UIManager.getColor("Tree.textBackground");
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);

		
		String stringValue = tree.convertValueToText(value, selected, expanded,
				leaf, row, false);
		leafRenderer.setText(stringValue);
		leafRenderer.setSelected(false);
		leafRenderer.setEnabled(tree.isEnabled());
		
		if (selected) {
			leafRenderer.setForeground(selectionForeground);
			leafRenderer.setBackground(selectionBackground);
			leafRenderer.setSelected(true);
		}else {
			leafRenderer.setForeground(textForeground);
			leafRenderer.setBackground(textBackground);
			leafRenderer.setSelected(false);
		}
		if ((value != null)) {
			TreeNodeDTO treeNodeDTO = (TreeNodeDTO) value;
				leafRenderer.setText(treeNodeDTO.getName());
				leafRenderer.setSelected(treeNodeDTO.isSelected());
				if (treeNodeDTO.isLeaf()&&treeNodeDTO.isMissing()){
					leafRenderer.setForeground(Color.RED);
				}else {
					leafRenderer.setForeground(textForeground);
				}
		}
		return leafRenderer;
	}
}