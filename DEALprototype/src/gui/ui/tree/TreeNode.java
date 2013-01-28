package gui.ui.tree;

import gui.model.application.Application;
import gui.model.domain.Term;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;
	private boolean hidden = false;

	public TreeNode(Term feature) {
		super(feature);
	}
	
	public TreeNode(Application application) {
		super(application);
	}

	public TreeNode(String string) {
		super(string);
	}

	@Override
	public TreeNode getRoot() {
		return (TreeNode) super.getRoot();
	}
	
	public TreeNode getParent() {
		return (TreeNode) super.getParent();
	}

	public Term getTerm() {
		Object o = this.getUserObject();
		if (o instanceof Term)
			return (Term) o;
		return null;
	}

	public TreeNode getChildAt(int i) {
		return (TreeNode) super.getChildAt(i);
	}
	
	public int getChildCount() {
		return super.getChildCount();
	}
	
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	public boolean isHidden() {
		return hidden;
	}
}
