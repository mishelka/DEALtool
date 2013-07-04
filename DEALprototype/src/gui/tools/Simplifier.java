package gui.tools;

import gui.model.domain.ComponentInfoType;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;

public class Simplifier {
	private DomainModel domainModel;
	
	public DomainModel sIMPLIFY(DomainModel domainModel) {
		this.domainModel = domainModel;
		
		sIMPLIFY();
		
//		Use this only when using YAJCo
//		removeFunctionalComponents();
		
		return domainModel;
	}
	
	//TODO: toto sa musi spravit na zaskrtnutie v DEAL.
	private boolean removeFunctionalComponents() {
		return domainModel.removeTermsOfInfoType(ComponentInfoType.FUNCTIONAL);
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
		
		simplifyModel();
		
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
	private void simplifyModel() {
		boolean wasRemoved;
		do {
			wasRemoved = domainModel.removeEmptyLeafs();
			wasRemoved |= domainModel.removeMultipleNestings();
		} while (!wasRemoved);
	}
}
