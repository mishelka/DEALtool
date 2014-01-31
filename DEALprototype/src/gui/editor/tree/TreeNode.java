package gui.editor.tree;

import gui.model.application.Application;
import gui.model.domain.Term;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;
	private boolean hidden = false;

	public TreeNode(Term term) {
		super(term);
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

	/**
	 * If this node or at least one of its children is hidden, returns true;
	 * @return true if this node or at least one of its children is hidden, false otherwise.
	 */
	public boolean isAtLeastOneHidden() {
		if (hidden)
			return true;

		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i).isAtLeastOneHidden())
				return true;
		}

		return false;
	}
	
	/**
	 * If this node or at least one of its children is unhidden, returns true;
	 * @return true if this node or at least one of its children is unhidden, false otherwise.
	 */
	public boolean isAtLeastOneUnhidden() {
		if(!hidden) return true;
		
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i).isAtLeastOneUnhidden())
				return true;
		}

		return false;
	}

	/**
	 * Unhides this node and all of its children.
	 */
	public void unhideSubtree() {
		this.setHidden(false);
		getTerm().setHidden(false);

		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).unhideSubtree();
		}
	}

	/**
	 * Hides this node and all of its children.
	 */
	public void hideSubtree() {
		this.setHidden(true);
		getTerm().setHidden(true);

		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).hideSubtree();
		}
	}
}
