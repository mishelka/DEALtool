package gui.model.domain;

import gui.model.application.scenes.Scene;
import gui.model.domain.relation.RelationType;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DomainModel {
	/**The root term of the Domain model.*/
	private Term root;
	/**The name of the domain model. Usually the name of the main window (the window, which first appears when the application starts).*/
	private String name;
//	private Point legendPosition = new Point(0, 0);
	/**
	 * The termTable is used for saving terms for each new dialog. The keys are names of the existing frames and the values are Term instances.
	 * The problem with adding new dialogs/windows into the term tree is that if the same dialog opens multiple times, it's not clear whether it is the same dialog or not. It could be exactly the same dialog, or it could be a new dialog, changed by the events in the application.
	 * For example, if there is a new document in MS Word, then the name of the main window is the default name of the document, e.g. Document1. As soon as the user saves this document under a different name, for example "My new document", the title of the main window will change to this name. But the main window is still the same, the application just slightly changed its state.
	 * Therefore this is just a temporary solution until we find an appropriate way of defining windows and dialogs uniquely.
	 */
	private List<Term> terms = new ArrayList<Term>();
//	private ArrayList<String> termOrderList = new ArrayList<String>();
	private final LinkedList<PropertyChangeListener> listenerList = new LinkedList<PropertyChangeListener>();
	/** Each domain model is associated to a scene - a window or a frame - which it is created from. */
	private Scene<?> scene;
	
	private boolean showComponentInfoTypes = false;

	public DomainModel(String name) {
		this.name = name;
		reset();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	/**
	 * @return the terms
	 */
	public List<Term> getTerms() {
		return terms;
	}

	/**
	 * @param terms
	 *            the terms to set
	 */
	public void setTerms(List<Term> terms) {
		this.terms = terms;
	}

	public void reset() {
		if (root != null) {
			while (root.hasChildren()) {
				Term child = root.getLastChild();
				deleteChildTerms(child);
				root.removeChild(child);
				terms.remove(child.getName());
			}
			root = null;
		}
		terms.clear();
//		termOrderList.clear();
	}
	
	private void deleteChildTerms(Term term) {
		while (term.hasChildren()) {
			Term child = term.getLastChild();
			deleteChildTerms(child);
			term.removeChild(child);
			terms.remove(child.getName());
		}
	}

	public Term getRoot() {
		return root;
	}

	public void setRoot(Term root) {
		this.root = root;
		if (this.root != null) {
			deleteTerm(this.root);
		}
		addTerm(root);
	}

	public Scene<?> getScene() {
		return scene;
	}
	
	public void setScene(Scene<?> scene) {
		this.scene = scene;
	}

	public void addTerm(Term term) {
		terms.add(term);
	}

	public void deleteTermFromTable(Term term) {
		terms.remove(term.getName());
	}

	public boolean deleteTerm(Term term) {

		// the root can not be deleted
		if (term == root)
			return false;

		// check if it exists
		if (!terms.contains(term))
			return false;

		// use the group type of the term to delete
		Term parent = term.getParent();
		// add children to parent

		int index = parent.getChildIndex(term);
		while (term.hasChildren())
			parent.addChildAtPosition(index, term.removeLastChild());

		// delete term
		parent.removeChild(term);
		terms.remove(term);
		return true;
	}

	public List<Term> getTerm(String name) {
		List<Term> result = new ArrayList<Term>();
		
		if (terms.isEmpty()) {
			// create the root term (it is the only one without a reference)
			root = new Term(this, name);
			addTerm(root);
			result.add(root);
			return result;
		}
		
		
		for(Term t : terms) {
			if(t.getName().equals(name)) {
				result.add(t);
			}
		}
		
		return result;
	}

	public void addListener(PropertyChangeListener listener) {
		if (!listenerList.contains(listener))
			listenerList.add(listener);
	}

	public void removeListener(PropertyChangeListener listener) {
		listenerList.remove(listener);
	}
	
	public Term getTermForComponent(Object component) {
		return root.getTermForComponent(component);
	}
	
	public void removeAll(List<Term> terms) {
		root.removeAllWithNesting(terms);
	}

	public void createDefaultValues(String projectName) {
		Term root = new Term(this);
		if (!projectName.equals("")) {
			root.setName(projectName);
		} else {
			root.setName("Root");
		}
		Term term = new Term(this, "Base");
		root.addChild(term);
		addTerm(term);
	}

	public void replaceRoot(Term term) {
		terms.remove(root.getName());
		root = term;
	}

	public List<String> getTermNames() {
		List<String> names = new ArrayList<String>();
		for(Term t : terms) {
			names.add(t.getName());
		}
		return names;
	}

	public int getNumberOfTerms() {
		return terms.size();
	}

	/**
	 * @return true if term model contains and group otherwise false
	 */
	public boolean hasAndGroup() {
		for (Term f : this.terms) {
			if (f.getChildrenCount() > 1
					&& (f.getRelation() == RelationType.AND))
				return true;
		}
		return false;
	}

	/**
	 * @return true if term model contains alternative group otherwise false
	 */
	public boolean hasAlternativeGroup() {
		for (Term f : this.terms) {
			if (f.getChildrenCount() > 1
					&& f.getRelation() == RelationType.MUTUALLY_NOT_EXCLUSIVE)
				return true;
		}
		return false;
	}

	/**
	 * @return true if term model contains or group otherwise false
	 */
	public boolean hasOrGroup() {
		for (Term f : this.terms) {
			if (f.getChildrenCount() > 1 && f.getRelation() == RelationType.MUTUALLY_EXCLUSIVE)
				return true;
		}
		return false;
	}
	
	public boolean isShowComponentInfoTypes() {
		return showComponentInfoTypes;
	}
	
	public void setShowComponentInfoTypes(boolean showComponentInfoTypes) {
		this.showComponentInfoTypes = showComponentInfoTypes;
	}
}