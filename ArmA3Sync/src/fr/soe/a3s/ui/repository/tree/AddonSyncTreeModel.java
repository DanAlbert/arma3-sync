package fr.soe.a3s.ui.repository.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import fr.soe.a3s.dto.sync.SyncTreeDirectoryDTO;
import fr.soe.a3s.dto.sync.SyncTreeNodeDTO;

public class AddonSyncTreeModel implements TreeModel {

	private SyncTreeDirectoryDTO root;

	private Vector listeners = new Vector();

	private List<SyncTreeNodeDTO> selectedNodes = new ArrayList<SyncTreeNodeDTO>();

	public AddonSyncTreeModel(SyncTreeDirectoryDTO syncTreeDirectoryDTO) {
		root = syncTreeDirectoryDTO;
	}

	@Override
	public Object getRoot() {
		return root;
	}

	@Override
	public Object getChild(Object parent, int index) {
		SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) parent;
		List<SyncTreeNodeDTO> list = directory.getList();
		SyncTreeNodeDTO treeNodeDTO = ((SyncTreeNodeDTO) list.get(index));
		return treeNodeDTO;
	}

	@Override
	public int getChildCount(Object parent) {
		SyncTreeNodeDTO syncTreeNodeDTO = (SyncTreeNodeDTO) parent;
		if (!syncTreeNodeDTO.isLeaf()) {
			SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) syncTreeNodeDTO;
			List<SyncTreeNodeDTO> list = directory.getList();
			return list.size();
		} else {
			return 0;
		}
	}

	@Override
	public boolean isLeaf(Object node) {
		SyncTreeNodeDTO syncTreeNodeDTO = (SyncTreeNodeDTO) node;
		if (syncTreeNodeDTO.isLeaf()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent != null && parent instanceof SyncTreeDirectoryDTO) {
			SyncTreeDirectoryDTO directory = (SyncTreeDirectoryDTO) parent;
			SyncTreeNodeDTO syncTreeNodeeDTO = (SyncTreeNodeDTO) child;
			List<SyncTreeNodeDTO> list = directory.getList();
			for (int i = 0; i < list.size(); i++) {
				if (syncTreeNodeeDTO.getName() != null) {
					if (syncTreeNodeeDTO.getName()
							.equals(list.get(i).getName())) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object value) {

		SyncTreeNodeDTO syncTreeNodeDTO = (SyncTreeNodeDTO) path
				.getLastPathComponent();
		String newName = (String) value;
		syncTreeNodeDTO.setName(newName);
		int[] changedChildrenIndices = { getIndexOfChild(
				syncTreeNodeDTO.getParent(), syncTreeNodeDTO) };
		Object[] changedChildren = { syncTreeNodeDTO };
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
	public void removeTreeModelListener(TreeModelListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void addTreeModelListener(TreeModelListener listener) {
		listeners.add(listener);
	}

}
