package fr.soe.a3s.ui.mainEditor.groups;

import java.awt.Color;
import java.awt.Font;

import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.TreeNodeDTO;
import fr.soe.a3s.ui.Facade;

public class AddonsRenameGroupPanel extends AddonsEditGroupPanel{

	private TreeDirectoryDTO treeDirectoryDTO;
	private TreeNodeDTO selectedTreeNodeDTO;
	
	public AddonsRenameGroupPanel(Facade facade,TreeDirectoryDTO treeDirectoryDTO,TreeNodeDTO selectedTreeNodeDTO) {
		super(facade);
		this.setTitle("Rename Group");
		this.treeDirectoryDTO = treeDirectoryDTO;
		this.selectedTreeNodeDTO = selectedTreeNodeDTO;
	}
	
	public void init() {
		textFieldGroupName.setText(selectedTreeNodeDTO.getName());
		textFieldGroupName.requestFocus();
		textFieldGroupName.selectAll();
	}

	@Override
	protected void buttonOKPerformed(String groupName) {
		
		labelWarning.setText("");
		for (TreeNodeDTO node : treeDirectoryDTO.getList()) {
			if (!node.isLeaf()
					&& node.getName().equals(textFieldGroupName.getText())) {
				labelWarning.setText("duplicate name!");
				labelWarning.setFont(new Font("Tohama", Font.ITALIC, 11));
				labelWarning.setForeground(Color.RED);
				return;
			}
		}
		selectedTreeNodeDTO.setName(groupName);
		facade.getAddonsPanel().refreshViewArbre2();
		facade.getAddonsPanel().saveAddonGroups();
		this.dispose();
	}
}
