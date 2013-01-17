package gui.model.domain;

import gui.model.application.Scene;
import gui.model.domain.relation.RelationType;

import java.awt.Point;
import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

public class DomainModel {// implements PropertyConstants {
	private Term root;
	private String name;
	private Point legendPosition = new Point(0, 0);
	private Hashtable<String, Term> termTable = new Hashtable<String, Term>();
	private ArrayList<String> termOrderList = new ArrayList<String>();
	private final LinkedList<PropertyChangeListener> listenerList = new LinkedList<PropertyChangeListener>();
	private Scene scene;

	/**
	 * @return the termTable
	 */
	public Hashtable<String, Term> getTermTable() {
		return termTable;
	}

	/**
	 * @param termTable
	 *            the termTable to set
	 */
	public void setTermTable(Hashtable<String, Term> termTable) {
		this.termTable = termTable;
	}

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

	public void reset() {
		if (root != null) {
			while (root.hasChildren()) {
				Term child = root.getLastChild();
				deleteChildTerms(child);
				root.removeChild(child);
				termTable.remove(child.getName());
			}
			root = null;
		}
		termTable.clear();
		termOrderList.clear();
	}
	
	private void deleteChildTerms(Term term) {
		while (term.hasChildren()) {
			Term child = term.getLastChild();
			deleteChildTerms(child);
			term.removeChild(child);
			termTable.remove(child.getName());
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

	public Scene getScene() {
		return scene;
	}
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public void addTerm(Term term) {
		// String name = term.getName();
		// if (termTable.containsKey(name))
		// return false;
		termTable.put(name, term);
		// return true;
	}

	public void deleteTermFromTable(Term term) {
		termTable.remove(term.getName());
	}

	public boolean deleteTerm(Term term) {

		// the root can not be deleted
		if (term == root)
			return false;

		// check if it exists
		String name = term.getName();
		if (!termTable.containsKey(name))
			return false;

		// use the group type of the term to delete
		Term parent = term.getParent();
		// add children to parent

		int index = parent.getChildIndex(term);
		while (term.hasChildren())
			parent.addChildAtPosition(index, term.removeLastChild());

		// delete term
		parent.removeChild(term);
		termTable.remove(name);
		return true;
	}

	public Term getTerm(String name) {

		if (termTable.isEmpty()) {
			// create the root term (it is the only one without a reference)
			root = new Term(this, name);
			addTerm(root);
			return root;
		}
		return termTable.get(name);
	}

	public boolean renameTerm(String oldName, String newName) {
		if (!termTable.containsKey(oldName)
				|| termTable.containsKey(newName))
			return false;
		Term term = termTable.remove(oldName);
		term.setName(newName);
		termTable.put(newName, term);
		return true;
	}

	public void addListener(PropertyChangeListener listener) {
		if (!listenerList.contains(listener))
			listenerList.add(listener);
	}

	public void removeListener(PropertyChangeListener listener) {
		listenerList.remove(listener);
	}
//
//	/**
//	 * Fires event for listeners when the model has new data
//	 */
//	public void handleModelDataLoaded() {
//		PropertyChangeEvent event = new PropertyChangeEvent(this,
//				MODEL_DATA_LOADED, false, true);
//		for (PropertyChangeListener listener : listenerList)
//			listener.propertyChange(event);
//	}
//
//	/**
//	 * Fires event for listeners when the model has changed data
//	 */
//	public void handleModelDataChanged() {
//		PropertyChangeEvent event = new PropertyChangeEvent(this,
//				MODEL_DATA_CHANGED, false, true);
//		for (PropertyChangeListener listener : listenerList)
//			listener.propertyChange(event);
//	}
//
//	/**
//	 * Fires event for listeners when the context menu needs to be refreshed
//	 */
//	public void refreshContextMenu() {
//		PropertyChangeEvent event = new PropertyChangeEvent(this,
//				REFRESH_CONTEXT_MENU, false, true);
//		for (PropertyChangeListener listener : listenerList)
//			listener.propertyChange(event);
//	}
//
//	/**
//	 * Fires event for listeners when the diagram needs to be redrawn
//	 */
//	public void redrawDiagram() {
//		PropertyChangeEvent event = new PropertyChangeEvent(this,
//				REDRAW_DIAGRAM, false, true);
//		for (PropertyChangeListener listener : listenerList)
//			listener.propertyChange(event);
//	}

	public Collection<Term> getTerms() {
		return Collections.unmodifiableCollection(termTable.values());
	}

	public void createDefaultValues(String projectName) {
		Term root;
		if (!projectName.equals("")) {
			root = getTerm(projectName);
		} else {
			root = getTerm("Root");
		}
		Term term = new Term(this, "Base");
		root.addChild(term);
		addTerm(term);
	}

	public void replaceRoot(Term term) {
		termTable.remove(root.getName());
		root = term;
	}

	public Set<String> getTermNames() {
		return Collections.unmodifiableSet(termTable.keySet());
	}

	public int getNumberOfTerms() {
		return termTable.size();
	}

//	@Override
//	public DomainModel clone() {
//		DomainModel fm = new DomainModel(name);
//		fm.root = root != null ? root.clone() : new Term(fm, "Root");
//		List<Term> list = new LinkedList<Term>();
//		list.add(fm.root);
//		while (!list.isEmpty()) {
//			Term term = list.remove(0);
//			fm.termTable.put(term.getName(), term);
//			for (Term child : term.getChildren())
//				list.add(child);
//		}
//		return fm;
//	}

	public Point getLegendPosition() {
		return legendPosition;
	}

	public void setLegendPosition(int x, int y) {
		this.legendPosition = new Point(x, y);
	}

	/**
	 * @return true if term model contains and group otherwise false
	 */
	public boolean hasAndGroup() {
		for (Term f : this.termTable.values()) {
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
		for (Term f : this.termTable.values()) {
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
		for (Term f : this.termTable.values()) {
			if (f.getChildrenCount() > 1 && f.getRelation() == RelationType.MUTUALLY_EXCLUSIVE)
				return true;
		}
		return false;
	}

	/**
	 * @return the termOrderList
	 */
	public ArrayList<String> getTermOrderList() {
		// TODO should return getConcreteTermNames() if null.
		return termOrderList;
	}

	/**
	 * @param termOrderList
	 *            the termOrderList to set
	 */
	public void setTermOrderList(ArrayList<String> termOrderList) {
		this.termOrderList = termOrderList;
	}
}