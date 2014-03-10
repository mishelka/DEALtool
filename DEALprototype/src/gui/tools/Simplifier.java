package gui.tools;

import gui.model.domain.ComponentInfoType;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;

/**
 * Simplifies the given domain model. The simplification process is the second
 * DEAL phase and it includes 4 steps:
 * <ul>
 * <li>The default root removal.</li>
 * <li>Deletion of void containers.</li>
 * <li>Deletion of multiple nesting.</li>
 * <li>Deletion non-relevant terms.</li>
 * </ul>
 * 
 * @author Michaela Bacikova, Slovakia, michaela.bacikova@tuke.sk
 */
public class Simplifier {
	/** The reference to the domain model to be simplified */
	private DomainModel domainModel;
	private boolean extractFunctionalComponents = true;

	/**
	 * Simplifies the given domain model and returns the simplified version.
	 * 
	 * @param domainModel
	 *            the domain model to be simplified
	 * @return the simplified version of the given domain model
	 */
	public DomainModel sIMPLIFY(DomainModel domainModel) {
		this.domainModel = domainModel;

		sIMPLIFY();

		if (!extractFunctionalComponents) {
			removeFunctionalComponents();
		}

		return this.domainModel;
	}

	private boolean removeFunctionalComponents() {
		return domainModel.removeTermsOfInfoType(ComponentInfoType.FUNCTIONAL);
	}

	// <editor-fold defaultstate="collapsed"
	// desc="Methods for domain model simplification">
	/**
	 * Second phase of DEAL - simplification of the term graph. The
	 * simplification is performed recursively and has x steps:
	 * <ul>
	 * <li>The default root removal.</li>
	 * <li>Deletion of void containers.</li>
	 * <li>Deletion of multiple nesting.</li>
	 * <li>Deletion non-relevant terms.</li>
	 * </ul>
	 * 
	 * @return true if the model was simplified, false otherwise
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
	 * Removes root from the model and sets the first child of root as a new
	 * root (shifts the first child of root up). This is to remove the default
	 * root.
	 * 
	 * @param model
	 *            the model, in which the root should be removed.
	 */
	private void shiftRootInModel() {
		Term child = domainModel.getRoot().getFirstChild();
		domainModel.replaceRoot(child);
		child.setParent(null);
	}

	/**
	 * Recursive step of the simplification phase. Steps:
	 * <ul>
	 * <li>Removal of bad characters.</li>
	 * <li>Deletion of void containers.</li>
	 * <li>Deletion of multiple nestings.</li>
	 * <li>Deletion non-relevant terms.</li>
	 * </ul>
	 * 
	 * @param thisTerm
	 *            The actual term, which should be checked for simplification
	 *            possibilities.
	 * @return true if any term was removed, false otherwise.
	 */
	private void simplifyModel() {
		domainModel.removeBadCharacters();

		boolean wasRemoved;
		do {
			wasRemoved = domainModel.removeEmptyLeafs();
			wasRemoved |= domainModel.removeMultipleNestings();
			wasRemoved |= domainModel.shiftSingleChildLeafs();
			wasRemoved |= domainModel.shiftParentRelations();
		} while (!wasRemoved);
	}

	// </editor-fold>

	public void extractFunctionalComponents(boolean extract) {
		this.extractFunctionalComponents = extract;
	}
}
