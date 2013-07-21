package fr.soe.a3s.ui.mainEditor.groups;

import java.awt.Color;
import java.awt.Font;

import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.TreeNodeDTO;
import fr.soe.a3s.service.ProfileService;
import fr.soe.a3s.ui.Facade;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class AddonsAddGroupPanel extends AddonsEditGroupPanel {

	private TreeDirectoryDTO treeDirectoryDTO;

	public AddonsAddGroupPanel(Facade facade,TreeDirectoryDTO treeDirectoryDTO) {
		super(facade);
		this.setTitle("Add Group");
		this.treeDirectoryDTO = treeDirectoryDTO;
	}

	public void init() {

		int i = 0;
		for (TreeNodeDTO node : treeDirectoryDTO.getList()) {
			if (node.getName().contains("Group_")) {
				i++;
			}
		}
		String name = "Group_" + i;
		textFieldGroupName.setText(name);
		textFieldGroupName.requestFocus();
		textFieldGroupName.selectAll();
	}

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
		TreeDirectoryDTO directory = new TreeDirectoryDTO();
		directory.setName(textFieldGroupName.getText());
		directory.setParent(treeDirectoryDTO);
		treeDirectoryDTO.addTreeNode(directory);
		facade.getAddonsPanel().refreshViewArbre2();
		facade.getAddonsPanel().saveAddonGroups();
		this.dispose();
	}
}
