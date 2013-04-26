package gui.model;

import gui.model.domain.DomainModel;
import gui.model.domain.Term;

public class Simplifier {
	private DomainModel domainModel;
	
	public Simplifier(DomainModel domainModel) {
		this.domainModel = domainModel;
	}
	
/************************* Methods for Domain Model simplification ******************************/
	
	/* 
	 * Second phase of DEAL - simplification of the term graph.
	 * The simplification is performed recursively and has x steps:
	 * - The default root removal.
	 * - Deletion of void containers.
	 * - Deletion of multiple nesting.
	 * - Deletion non-relevant terms.
	 * @return true if the model simplified, false otherwise
	 */
	public DomainModel sIMPLIFY() {
		shiftRootInModel();
		
		Term root = domainModel.getRoot();

		// if there is nothing in the model, then there's nothing to remove
		if (root == null || root.getChildrenCount() == 0)
			return domainModel;

		simplifyTerm(root);
		
		return domainModel;
	}
	
	/**
	 * Removes root from the model and sets the first child of root as a new root (shifts the first child of root up).
	 * This is to remove the default root.
	 * @param model the model, in which the root should be removed.
	 */
	private void shiftRootInModel() {
		Term child = domainModel.getRoot().getFirstChild();
		domainModel.setRoot(child);
		child.setParent(null);
	}

	/**
	 * Recursive step of the simplification phase. Steps:
	 * - Deletion of void containers. 
	 * - Deletion of multiple nesting.
	 * - Deletion non-relevant terms.
	 * @param thisTerm The actual term, which should be checked for simplification possibilities.
	 * @return true if any term was removed, false otherwise.
	 */
	private boolean simplifyTerm(Term thisTerm) {
		//each cycle is performed recursively: in reversed order (last to first - because of the deletion).
		
		//First step: removing empty leaves
		boolean wasRemoved = removeVoidLeafs(thisTerm);
		
		//Second step: removing multiple nesting of containers which have only one child (container).
		wasRemoved |= removeMultipleNestings(thisTerm);
		
		//Third step: removing terms which contain no domain information and have only one parent.
		wasRemoved |= removeNonrelevantTerms(thisTerm);
		
		
		return wasRemoved;
	}	
	
	/**
	 * The first step of the simplification phase.
	 * The child term, which has no children, is a leaf and is a term with no relevant information, we call a "void leaf".
	 * @param term  The term whose children should be checked for void leafs.
	 * @return  true if any term node was removed, false otherwise.
	 */
	private boolean removeVoidLeafs(Term term) {
		boolean wasRemoved = false;
		
		for (int i = term.getChildrenCount() - 1; i >= 0; i--) {
			Term childTerm = term.getChildAt(i);
			if(!removeIfVoidLeaf(childTerm)) {
				simplifyTerm(childTerm);
				wasRemoved = true;
			}
		}
		
		return wasRemoved;
	}

	/**
	 * If the child has no children, it is a leaf and it is a void container or a term with no relevant information, then remove it.
	 * @param term  The term which should be removed.
	 * @return true if the term was removed from its parent, false otherwise.
	 */
	private boolean removeIfVoidLeaf(Term term) {
		Term parent = term.getParent();
		if (term.isLeaf() && term.canBeRemoved() && parent != null) {
			parent.removeChild(term);
			return true;
		} else
			return false;
	}

	/**
	 * The second step of the simplification phase.
	 * Removes multiple nesting of containers which have only one child of container-type.
	 * @param term  The term whose children should be checked for multiple nesting.
	 * @return  true if any term node was removed, false otherwise.
	 */
	private boolean removeMultipleNestings(Term term) {
		boolean wasRemoved = false;
		
		for (int i = term.getChildrenCount() - 1; i >= 0; i--) {
			Term childTerm = term.getChildAt(i);

			if (removeMultipleNesting(childTerm)) {
				simplifyTerm(childTerm);
				wasRemoved = true;
			}
		}
		
		return wasRemoved;
	}
	
	/**
	 * If the child belongs to a parent, which can be removed and this parent has only this one child, then this child is shifted to its grandparent and the parent term is removed.
	 * @param term  The term which should be shifted and its parent removed.
	 * @return true if term was shifted, false otherwise.
	 */
	private boolean removeMultipleNesting(Term child) {
		Term parent = child.getParent();
		if (parent != null && parent.hasChildren()
				&& parent.getChildrenCount() == 1 && parent.canBeRemoved()) {
			Term grandParent = parent.getParent();
			if (grandParent != null) {
				grandParent.replaceChild(parent, child);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * The third step of the simplification phase.
	 * Removes terms which contain no domain information and have only one child.
	 * @param term  a term which should be checked for non-relevant child terms.
	 * @return true if any term was removed, false otherwise.
	 */
	private boolean removeNonrelevantTerms(Term term) {
		boolean wasRemoved = false;
		
		for(int i = term.getChildrenCount() - 1; i >= 0; i--) {
			Term t = term.getChildAt(i);
			
			if(term.getChildrenCount() == 1 && t.canBeRemoved()) {
				for(Term child : t.getChildren()) {
					term.addChild(child);
				}
				term.removeChild(t);
				wasRemoved = true;
			}
		}
		
		return wasRemoved;
	}
}
