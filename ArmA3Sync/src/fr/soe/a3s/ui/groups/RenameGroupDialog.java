package fr.soe.a3s.ui.groups;

import java.awt.Color;
import java.awt.Font;

import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.TreeNodeDTO;
import fr.soe.a3s.ui.Facade;

public class RenameGroupDialog extends AbstractGroupDialog {

	private final TreeDirectoryDTO treeDirectoryDTO;
	private final TreeNodeDTO selectedTreeNodeDTO;

	public RenameGroupDialog(Facade facade, TreeDirectoryDTO treeDirectoryDTO,
			TreeNodeDTO selectedTreeNodeDTO) {
		super(facade, "Rename Group", true);
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
			selectedTreeNodeDTO.setName(groupName);
			this.dispose();
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
