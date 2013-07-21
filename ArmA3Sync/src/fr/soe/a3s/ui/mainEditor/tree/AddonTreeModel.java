package fr.soe.a3s.ui.mainEditor.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.TreeNodeDTO;


public class AddonTreeModel implements TreeModel {

	private TreeDirectoryDTO root;

	private Vector listeners = new Vector();
	
	private List<TreeNodeDTO> selectedNodes = new ArrayList<TreeNodeDTO>();

	public AddonTreeModel(TreeDirectoryDTO treeDirectoryDTO) {
		root = treeDirectoryDTO;
	}

	@Override
	public Object getRoot() {
		return root;
	}

	@Override
	public Object getChild(Object parent, int index) {
		TreeDirectoryDTO directory = (TreeDirectoryDTO) parent;
		List<TreeNodeDTO> list = directory.getList();
		TreeNodeDTO treeNodeDTO = ((TreeNodeDTO) list.get(index));
		return treeNodeDTO;
	}

	@Override
	public int getChildCount(Object parent) {
		TreeNodeDTO treeNodeDTO = (TreeNodeDTO) parent;
		if (!treeNodeDTO.isLeaf()) {
			TreeDirectoryDTO directory = (TreeDirectoryDTO) treeNodeDTO;
			List<TreeNodeDTO> list = directory.getList();
			return list.size();
		} else {
			return 0;
		}
	}

	@Override
	public boolean isLeaf(Object node) {
		TreeNodeDTO	treeNodeDTO = (TreeNodeDTO) node;
		if (treeNodeDTO.isLeaf()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent!=null && parent instanceof TreeDirectoryDTO){
			TreeDirectoryDTO directory = (TreeDirectoryDTO) parent;
			TreeNodeDTO treeNodeeDTO = (TreeNodeDTO) child;
			List<TreeNodeDTO> list = directory.getList();
			for (int i = 0; i < list.size(); i++) {
				if (treeNodeeDTO.getName()!=null){
					if (treeNodeeDTO.getName().equals(list.get(i).getName())) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object value) {

		TreeNodeDTO treeNodeDTO = (TreeNodeDTO) path
				.getLastPathComponent();
		String newName = (String) value;
		treeNodeDTO.setName(newName);
		int[] changedChildrenIndices = { getIndexOfChild(treeNodeDTO
				.getParent(), treeNodeDTO) };
		Object[] changedChildren = { treeNodeDTO };
		fireTreeNodesChanged(path.getParentPath(), changedChildrenIndices,
				changedChildren);
	}

	private void fireTreeNodesChanged(TreePath parentPath, int[] indices,
			Object[] children) {
		TreeModelEvent event = new TreeModelEvent(this, parentPath, indices,
				children);
		Iterator iterator = listeners.iterator();
		TreeModelListener listener = null;
		while (iterator.hasNext()) {
			listener = (TreeModelListener) iterator.next();
			listener.treeNodesChanged(event);
		}
	}

	@Override
	public void addTreeModelListener(TreeModelListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener listener) {
		listeners.remove(listener);
	}

	public void removeNodeFromParent(TreeNodeDTO oldNode) {
		oldNode.getParent().removeTreeNode(oldNode);
	}

	public void insertNodeInto(TreeNodeDTO node, TreeNodeDTO parent, int i) {
		TreeDirectoryDTO treeDirectoryDTO = (TreeDirectoryDTO) parent;
		treeDirectoryDTO.getList().add(i, node);
	}
	
	public void updateSelectedState(List<TreeNodeDTO> selectedNodes){
		this.selectedNodes = selectedNodes;
		for (TreeNodeDTO treeNodeDTO:root.getList()){
			updateSelectedState(treeNodeDTO);
		}
	}
	
	private void updateSelectedState(TreeNodeDTO treeNodeDTO){
		if (treeNodeDTO.isLeaf()){
			if (selectedNodes.contains(treeNodeDTO)){
				treeNodeDTO.setSelected(true);
			}else {
				treeNodeDTO.setSelected(false);
			}
		}else {
			TreeDirectoryDTO treeDirectoryDTO = (TreeDirectoryDTO) treeNodeDTO;
			for (TreeNodeDTO t:treeDirectoryDTO.getList()){
				updateSelectedState(t);
			}
		}
	}
}