package gui.editor.tree;

import gui.model.application.Application;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultTreeModel;

public class TreeModel extends DefaultTreeModel {
	private static final long serialVersionUID = 1L;
	
	public TreeModel(Application application) {
		super(new TreeNode(application.getName()));
	}

	public TreeModel(DomainModel domainModel) {
		super(new TreeNode("Fake root"));

		// super(new DefaultMutableTreeNode(domainModel.getName()));

		/*DONT CREATE A STATIC VARIABLE FROM THIS, IT WILL NOT WORK!!!*/
		Term root = domainModel.getRoot();
		TreeNode rootNode = this.getRoot();

		createNodes(root, rootNode);
	}

	private void createNodes(Term thisTrem, TreeNode parentTreeNode) {
		TreeNode thisTreeNode = new TreeNode(thisTrem);
		parentTreeNode.add(thisTreeNode);

		for (Term childTerm : thisTrem.getChildren()) {
			createNodes(childTerm, thisTreeNode == null ? parentTreeNode
					: thisTreeNode);
		}
	}

	public void add(TreeModel toAdd) {
		TreeNode root = toAdd.getRoot();
		TreeNode nodeToAdd = root.getChildAt(0);

		this.getRoot().add(nodeToAdd);
	}

	public boolean hasNodeForFrame(Object frame) {
		return findRootNodeForFrame(frame) != null;
	}

	public TreeNode findRootNodeForFrame(Object frame) {
		for (TreeNode child : getTermRoots()) {
			Term term = child.getTerm();
			Object component = term.getComponent();

			if (frame.equals(component)) {
				return child;
			}
		}
		return null;
	}
	
	public Term findTermForComponent(Object targetComponent) {
		Term t = null;
		
		for (TreeNode tn : getTermRoots()) {
			t = findTermForComponent(targetComponent, tn);
			if (t != null)
				return t;
		}
		
		return null;
	}

	public Term findTermForComponent(Object targetComponent, TreeNode thisNode) {
		Term thisTerm = (Term) thisNode.getTerm();
		Object thisComponent = thisTerm.getComponent();

		if (targetComponent.equals(thisComponent)) {
			return thisTerm;
		}
		
		for (int i = 0; i < thisNode.getChildCount(); i++) {
			thisTerm = findTermForComponent(targetComponent, thisNode.getChildAt(i));
			if (thisTerm != null) {
				return thisTerm;
			}
		}

		return null;
	}

	public TreeNode getRoot() {
		return (TreeNode) super.getRoot();
	}

	public TreeNode[] getTermRoots() {
		List<TreeNode> roots = new ArrayList<TreeNode>();

		TreeNode root = getRoot();
		for (int i = 0; i < root.getChildCount(); i++) {
			TreeNode child = (TreeNode) root.getChildAt(i);
			roots.add(child);
		}
		return roots.toArray(new TreeNode[] {});
	}
}
