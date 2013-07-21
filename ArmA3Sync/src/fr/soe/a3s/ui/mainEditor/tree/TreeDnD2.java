package fr.soe.a3s.ui.mainEditor.tree;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.TreeLeafDTO;
import fr.soe.a3s.dto.TreeNodeDTO;
import fr.soe.a3s.ui.Facade;

@Deprecated
public class TreeDnD2 {

	private JTree arbre;
	@SuppressWarnings("unused")
	private TreeDragSource ds;
	@SuppressWarnings("unused")
	private TreeDropTarget dt;
	public static DataFlavor TREE_PATH_FLAVOR = new DataFlavor(TreePath.class,
			"Tree Path");
	private TreePath newPath;
	private TreePath[] oldPaths;

	private Facade facade;

	public TreeDnD2(JTree arbre, Facade facade) {
		this.arbre = arbre;
		ds = new TreeDragSource(arbre, DnDConstants.ACTION_MOVE);
		dt = new TreeDropTarget(arbre);
		this.facade = facade;
		this.arbre.setDragEnabled(true);
	}

	/**
	 * Source
	 */
	private class TreeDragSource implements DragSourceListener,
			DragGestureListener {

		private DragSource source;
		private int actions;
		private TransferableTreeNode transferable;
		private JTree tree;
		@SuppressWarnings("unused")
		private DragGestureRecognizer recognizer;

		public TreeDragSource(JTree _tree, int actions) {
			this.tree = _tree;
			this.actions = actions;
			source = new DragSource();
			recognizer = source.createDefaultDragGestureRecognizer(tree,
					this.actions, this);
		}

		/*
		 * Drag Gesture Handler
		 */
		@Override
		public void dragGestureRecognized(DragGestureEvent dge) {

			oldPaths = tree.getSelectionPaths();
			if (oldPaths == null) {
				return;
			}

			try {
				transferable = new TransferableTreeNode(oldPaths);
				source.startDrag(dge, DragSource.DefaultMoveDrop, transferable,
						this);
			} catch (java.awt.dnd.InvalidDnDOperationException e) {
				e.printStackTrace();
			}
		}

		/*
		 * Drag Event Handlers
		 */
		@Override
		public void dragEnter(DragSourceDragEvent arg0) {
		}

		@Override
		public void dragExit(DragSourceEvent arg0) {
		}

		@Override
		public void dragOver(DragSourceDragEvent dsde) {
			Point p = new Point(dsde.getX(), dsde.getY());
			SwingUtilities.convertPointFromScreen(p, tree);
			TreePath selPath = tree.getPathForLocation((int) p.getX(),
					(int) p.getY());
			tree.setSelectionPath(selPath);
		}

		@Override
		public void dropActionChanged(DragSourceDragEvent arg0) {
		}

		@Override
		public void dragDropEnd(DragSourceDropEvent arg0) {
		}

	}

	/**
	 * Target
	 */
	private class TreeDropTarget implements DropTargetListener {

		@SuppressWarnings("unused")
		private DropTarget target;
		private JTree tree;

		public TreeDropTarget(JTree _tree) {
			tree = _tree;
			target = new DropTarget(tree, this);
		}

		@Override
		public void dragEnter(DropTargetDragEvent arg0) {
		}

		@Override
		public void dragExit(DropTargetEvent arg0) {
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent arg0) {

		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			Point p = dtde.getLocation();
			newPath = tree.getClosestPathForLocation(p.x, p.y);
			dtde.acceptDrag(DnDConstants.ACTION_MOVE);

			if (oldPaths == null) {
				dtde.rejectDrag();
				return;
			}
		}

		@Override
		public void drop(DropTargetDropEvent dtde) {

			if (newPath == null) {
				dtde.rejectDrop();
				return;
			}
			if (oldPaths == null) {
				dtde.rejectDrop();
				return;
			}

			TreeNodeDTO newTreeNode = (TreeNodeDTO) newPath.getLastPathComponent();
			TreeDirectoryDTO targetDirectory = null;
			if (newTreeNode.isLeaf()) {
				TreeDirectoryDTO treeDirectoryDTO = newTreeNode.getParent();
				targetDirectory = treeDirectoryDTO;
			} else {
				targetDirectory = (TreeDirectoryDTO) newTreeNode;
			}
			
			if (targetDirectory==null){
				dtde.rejectDrop();
				return;
			}
			
			for (TreePath treePath : oldPaths) {
				TreeNodeDTO oldTreeNode = (TreeNodeDTO) treePath
						.getLastPathComponent();
				//drop only addon
				if (oldTreeNode.isLeaf()) {
					// check if addon name no already exists
					String addonName = oldTreeNode.getName();
					boolean contains = false;
					for (TreeNodeDTO n:targetDirectory.getList()){
						if (n.getName().equals(addonName)){
							contains = true;
							break;
						}
					}
					if (!contains) {
						TreeLeafDTO newTreeLeaf = new TreeLeafDTO();
						newTreeLeaf.setName(oldTreeNode.getName());
						newTreeLeaf.setSelected(false);
						newTreeLeaf.setParent(targetDirectory);
						targetDirectory.addTreeNode(newTreeLeaf);
						TreeDirectoryDTO parent = oldTreeNode.getParent();
						parent.removeTreeNode(oldTreeNode);
					}
				}
			}
			dtde.dropComplete(true);
			facade.getAddonsPanel().refreshViewArbre2();
			facade.getAddonsPanel().expand(newPath);
			facade.getAddonsPanel().saveAddonGroups();
			newPath=null;
			oldPaths=null;
		}
	}

	/**
	 * Transfer
	 */
	private class TransferableTreeNode implements Transferable {

		private DataFlavor flavors[] = { TREE_PATH_FLAVOR };
		private TreePath[] path;

		public TransferableTreeNode(TreePath[] tp) {
			path = tp;
		}

		public synchronized DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return (flavor.getRepresentationClass() == TreePath.class);
		}

		public synchronized Object[] getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if (isDataFlavorSupported(flavor)) {
				return (Object[]) path;
			} else {
				throw new UnsupportedFlavorException(flavor);
			}
		}
	}
}
