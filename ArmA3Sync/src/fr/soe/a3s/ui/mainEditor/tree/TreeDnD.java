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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import fr.soe.a3s.dto.TreeDirectoryDTO;
import fr.soe.a3s.dto.TreeLeafDTO;
import fr.soe.a3s.dto.TreeNodeDTO;
import fr.soe.a3s.service.ProfileService;
import fr.soe.a3s.ui.Facade;

public class TreeDnD {

	private JTree arbre1, arbre2;
	@SuppressWarnings("unused")
	private TreeDragSource ds1, ds2;
	@SuppressWarnings("unused")
	private TreeDropTarget dt;
	public static DataFlavor TREE_PATH_FLAVOR = new DataFlavor(TreePath.class,
			"Tree Path");
	private TreePath newPath;
	private TreePath[] oldPaths;
	private Facade facade;
	private boolean isLeftClick = false;

	public TreeDnD(JTree arbre1, JTree arbre2, Facade facade) {
		this.arbre1 = arbre1;
		this.arbre2 = arbre2;
		ds1 = new TreeDragSource(arbre1, DnDConstants.ACTION_MOVE);
		ds2 = new TreeDragSource(arbre2, DnDConstants.ACTION_MOVE);
		dt = new TreeDropTarget(arbre2);
		this.facade = facade;
		this.arbre1.setDragEnabled(true);
		this.arbre2.setDragEnabled(true);
		this.arbre1.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				isLeftClick = SwingUtilities.isLeftMouseButton(evt);
			}
		});
		this.arbre2.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				isLeftClick = SwingUtilities.isLeftMouseButton(evt);
			}
		});
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

			if (!isLeftClick) {
				return;
			}

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
		public void dragEnter(DragSourceDragEvent dse) {

		}

		@Override
		public void dragExit(DragSourceEvent dse) {
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
		public void dropActionChanged(DragSourceDragEvent dsde) {
			//System.out.println("Action: " + dsde.getDropAction());
			//System.out.println("Target Action: " + dsde.getTargetActions());
			//System.out.println("User Action: " + dsde.getUserAction());
		}

		@Override
		public void dragDropEnd(DragSourceDropEvent dsde) {
			//System.out.println("Drop Action End: " + dsde.getDropAction());
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

		/*
		 * Drop Event Handlers
		 */
		@Override
		public void dragEnter(DropTargetDragEvent arg0) {
			//System.out.println("drop handler : dragEnter");
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			Point p = dtde.getLocation();
			newPath = tree.getClosestPathForLocation(p.x, p.y);
			dtde.acceptDrag(DnDConstants.ACTION_MOVE);

			// if (newPath == null || oldPaths == null) {
			// dtde.rejectDrag();
			// return;
			// }

			if (oldPaths == null) {
				dtde.rejectDrag();
				return;
			}

			// if (oldPaths.length > 0) {
			// for (int i = 0; i < oldPaths.length; i++) {
			// TreeNodeDTO treeNodeDTO = (TreeNodeDTO) oldPaths[i]
			// .getLastPathComponent();
			// if (!treeNodeDTO.isLeaf()) {
			// dtde.rejectDrag();
			// return;
			// }
			// }
			// }
		}

		@Override
		public void drop(DropTargetDropEvent dtde) {

			if (oldPaths == null) {
				dtde.rejectDrop();
				return;
			}

			TreeNodeDTO oldTreeNodeDTO = (TreeNodeDTO) oldPaths[0]
					.getLastPathComponent();

			TreeNodeDTO newTreeNode = null;
			if (newPath == null) {
				newTreeNode = (TreeNodeDTO) tree.getModel().getRoot();
				if (oldTreeNodeDTO.isLeaf()) {
					dtde.rejectDrop();
					return;
				}
			} else {
				newTreeNode = (TreeNodeDTO) newPath.getLastPathComponent();
			}

			if (oldTreeNodeDTO.getName().equals(newTreeNode.getName())) {
				dtde.rejectDrop();
				return;
			}

			if (oldTreeNodeDTO.getParent() == null) {
				dtde.rejectDrop();
				return;
			}

			if (newTreeNode.getParent() != null) {
				if (oldTreeNodeDTO.getParent().getName()
						.equals(newTreeNode.getParent().getName())) {
					dtde.rejectDrop();
					return;
				}
			}

			if (newTreeNode.isLeaf()) {
				newTreeNode = newTreeNode.getParent();
				newPath = newPath.getParentPath();
				if (newTreeNode == null) {
					dtde.rejectDrop();
					return;
				}
			}

			if (!oldTreeNodeDTO.isLeaf()) {
				newTreeNode = (TreeNodeDTO) tree.getModel().getRoot();
				TreeDirectoryDTO targetDirectory = (TreeDirectoryDTO) newTreeNode;
				TreeDirectoryDTO sourceDirectory = (TreeDirectoryDTO) oldTreeNodeDTO;
				boolean contains = false;
				for (TreeNodeDTO treeNodeDTO : targetDirectory.getList()) {
					if (!treeNodeDTO.isLeaf()
							&& treeNodeDTO.getName().equals(
									sourceDirectory.getName())) {
						contains = true;
						break;
					}
				}
				if (!contains) {
					TreeDirectoryDTO newTreeDirectory = new TreeDirectoryDTO();
					newTreeDirectory.setName(sourceDirectory.getName());
					newTreeDirectory.setSelected(false);
					newTreeDirectory.setParent(targetDirectory);
					targetDirectory.addTreeNode(newTreeDirectory);
					duplicateDirectory(sourceDirectory, newTreeDirectory);
					dtde.dropComplete(true);
				} else {
					dtde.rejectDrop();
					return;
				}
			} else {
				TreeDirectoryDTO targetDirectory = (TreeDirectoryDTO) newTreeNode;
				for (int i = 0; i < oldPaths.length; i++) {
					oldTreeNodeDTO = (TreeNodeDTO) oldPaths[i]
							.getLastPathComponent();
					if (oldTreeNodeDTO.isLeaf()) {
						TreeLeafDTO sourceTreeLeaf = (TreeLeafDTO) oldTreeNodeDTO;
						boolean contains = false;
						for (TreeNodeDTO treeNodeDTO : targetDirectory
								.getList()) {
							if (treeNodeDTO.isLeaf()
									&& treeNodeDTO.getName().equals(
											sourceTreeLeaf.getName())) {
								contains = true;
								break;
							}
						}
						if (!contains) {
							TreeLeafDTO newTreeLeaf = new TreeLeafDTO();
							newTreeLeaf.setName(sourceTreeLeaf.getName());
							newTreeLeaf.setSelected(false);
							newTreeLeaf.setParent(targetDirectory);
							targetDirectory.addTreeNode(newTreeLeaf);
							if (oldPaths[i].toString().contains("racine2")) {
								TreeDirectoryDTO parent = oldTreeNodeDTO
										.getParent();
								parent.removeTreeNode(oldTreeNodeDTO);
							}
							dtde.dropComplete(true);
						} else {
							dtde.rejectDrop();
							return;
						}
					}
				}
			}

			facade.getAddonsPanel().saveAddonGroups();
			if (oldTreeNodeDTO.isLeaf()) {
				facade.getAddonsPanel().refreshViewArbre2();
				facade.getAddonsPanel().expand(newPath);
			} else {
				facade.getAddonsPanel().updateAddonGroups();
			}
			facade.getAddonOptionsPanel().updateAddonPriorities();
			facade.getLaunchOptionsPanel().updateRunParameters();
			
			oldPaths = null;
			newPath = null;
		}

		private void duplicateDirectory(TreeDirectoryDTO sourceDirectory,
				TreeDirectoryDTO duplicateDirectory) {

			List<TreeNodeDTO> list = sourceDirectory.getList();

			for (TreeNodeDTO treeNode : list) {
				if (treeNode.isLeaf()) {
					TreeLeafDTO treeLeafDTO = (TreeLeafDTO) treeNode;
					TreeLeafDTO duplicateLeaf = duplicateLeaf(treeLeafDTO);
					duplicateLeaf.setParent(duplicateDirectory);
					duplicateDirectory.addTreeNode(duplicateLeaf);
				} else {
					TreeDirectoryDTO treeDirectory2 = (TreeDirectoryDTO) treeNode;
					TreeDirectoryDTO duplicateTreedDirectory2 = new TreeDirectoryDTO();
					duplicateTreedDirectory2.setName(treeDirectory2.getName());
					duplicateTreedDirectory2.setParent(duplicateDirectory);
					duplicateTreedDirectory2.setSelected(treeDirectory2
							.isSelected());
					duplicateDirectory.addTreeNode(duplicateTreedDirectory2);
					duplicateDirectory(treeDirectory2, duplicateTreedDirectory2);
				}
			}
		}

		private TreeLeafDTO duplicateLeaf(TreeLeafDTO treeLeafDTO) {
			TreeLeafDTO duplicateTreeLeaf = new TreeLeafDTO();
			duplicateTreeLeaf.setName(treeLeafDTO.getName());
			duplicateTreeLeaf.setSelected(treeLeafDTO.isSelected());
			return duplicateTreeLeaf;
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent arg0) {
		}

		@Override
		public void dragExit(DropTargetEvent arg0) {
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
