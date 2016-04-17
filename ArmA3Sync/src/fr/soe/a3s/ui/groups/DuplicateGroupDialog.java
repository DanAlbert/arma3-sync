package fr.soe.a3s.ui.groups;

import java.awt.Color;
import java.awt.Font;

import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.TreeLeafDTO;
import fr.soe.a3s.dto.TreeNodeDTO;
import fr.soe.a3s.ui.Facade;

public class DuplicateGroupDialog extends AbstractGroupDialog {

	private final TreeDirectoryDTO treeDirectoryDTO;
	private final TreeNodeDTO selectedTreeNodeDTO;

	public DuplicateGroupDialog(Facade facade,
			TreeDirectoryDTO treeDirectoryDTO, TreeNodeDTO selectedTreeNodeDTO) {
		super(facade, "Duplicate", true);
		this.treeDirectoryDTO = treeDirectoryDTO;
		this.selectedTreeNodeDTO = selectedTreeNodeDTO;
	}

	public void init() {
		textFieldGroupName.setText(selectedTreeNodeDTO.getName());
		textFieldGroupName.requestFocus();
		textFieldGroupName.selectAll();
	}

	@Override
	protected void buttonOKPerformed() {

		String groupName = textFieldGroupName.getText().trim();
		if (!groupName.isEmpty()) {
			labelWarning.setText("");
			for (TreeNodeDTO node : treeDirectoryDTO.getList()) {
				if (!node.isLeaf()
						&& node.getName().equals(textFieldGroupName.getText())) {
					labelWarning.setText("duplicate name!");
					Font italicFont = labelWarning.getFont().deriveFont(
							Font.ITALIC);
					labelWarning.setFont(italicFont);
					labelWarning.setForeground(Color.RED);
					return;
				}
			}
			TreeDirectoryDTO directory = new TreeDirectoryDTO();
			directory.setName(textFieldGroupName.getText());
			directory.setParent(treeDirectoryDTO);
			treeDirectoryDTO.addTreeNode(directory);
			for (TreeNodeDTO n : ((TreeDirectoryDTO) selectedTreeNodeDTO)
					.getList()) {
				duplicate(directory, n);
			}
			facade.getAddonsPanel().refreshViewArbre2();
			facade.getAddonsPanel().saveAddonGroups();
			this.dispose();
		}
	}

	private void duplicate(TreeDirectoryDTO directory, TreeNodeDTO node) {

		if (node.isLeaf()) {
			TreeLeafDTO leaf = new TreeLeafDTO();
			leaf.setName(node.getName());
			leaf.setParent(directory);
			directory.addTreeNode(leaf);
		} else {
			TreeDirectoryDTO d = (TreeDirectoryDTO) node;
			TreeDirectoryDTO newDirectory = new TreeDirectoryDTO();
			newDirectory.setName(d.getName());
			newDirectory.setParent(directory);
			directory.addTreeNode(newDirectory);
			for (TreeNodeDTO n : d.getList()) {
				duplicate(newDirectory, n);
			}
		}
	}

	@Override
	protected void buttonCancelPerformed() {
		this.dispose();
	}

	@Override
	protected void menuExitPerformed() {
		this.dispose();
	}
}
