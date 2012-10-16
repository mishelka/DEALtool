package gui.model.domain;

import gui.analyzer.handlers.swing.ContainerComposite;
import gui.analyzer.util.Util;
import gui.model.domain.constraint.Constraint;
import gui.model.domain.relation.AbstractRelation;
import gui.model.domain.relation.RelationType;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;

public class Term { //implements PropertyConstants {
	private String name;
	private String description;
	//TODO: remove this and set outgoingRelations instead of this!!! priority highest!
	private RelationType relation = RelationType.AND;
	private RelationType parentRelation = null;
	private Icon icon;
	
	//TODO: add everything for this, priority highest
	private List<AbstractRelation> outgoingRelations;
	private List<Constraint> constraints;

	private DomainModel domainModel;

	private Term parent;

	private List<Term> children = new ArrayList<Term>();

	@SuppressWarnings("rawtypes")
	private Class componentClass;
	private Object component;

	public Term(DomainModel domainModel) {
		this.domainModel = domainModel;
	}

	public Term(DomainModel domainModel, String name) {
		this.domainModel = domainModel;
		this.name = name;
	}

	public RelationType getParentRelation() {
		return parentRelation;
	}

	public void setParentRelation(RelationType parentRelation) {
		this.parentRelation = parentRelation;
	}

	public boolean generatesParentRelation() {
		return parentRelation != null;
	}

	public void setRelation(RelationType relation) {
		this.relation = relation;
//		fireChildrenChanged();
	}

	public RelationType getRelation() {
		return relation;
	}

	public void setComponentClass(
			@SuppressWarnings("rawtypes") Class objectClass) {
		this.componentClass = objectClass;
	}

	public String getComponentClassSimpleName() {
		return componentClass.getSimpleName();
	}

	@SuppressWarnings("rawtypes")
	public Class getComponentClass() {
		return componentClass;
	}

	public DomainModel getDomainModel() {
		return domainModel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
//		fireNameChanged();
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
//		fireDescriptionChanged();
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public void setParent(Term newParent) {
		if (newParent == parent)
			return;
		parent = newParent;
	}

	public Term getParent() {
		return parent;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public List<Term> getChildren() {
		return children;
	}

	public void setChildren(List<Term> children) {
		if (this.children == children)
			return;
		for (Term child : children) {
			child.setParent(this);
		}
		this.children = children;
//		fireChildrenChanged();
	}

	public boolean hasChildren() {
		return !isLeaf();
	}

	public boolean isLeaf() {
		return children.isEmpty();
	}

	public void addChild(Term newChild) {
		children.add(newChild);
		newChild.setParent(this);
//		fireChildrenChanged();
	}

	public void addChildAtPosition(int index, Term newChild) {
		children.add(index, newChild);
		newChild.setParent(this);
//		fireChildrenChanged();
	}

	public void replaceChild(Term oldChild, Term newChild) {
		int index = children.indexOf(oldChild);
		children.set(index, newChild);
		oldChild.setParent(null);
		newChild.setParent(this);
//		fireChildrenChanged();
	}

	public void removeChild(Term child) {
		children.remove(child);
		child.setParent(null);
//		fireChildrenChanged();
	}

	public Term removeLastChild() {
		Term child = children.get(children.size() - 1);
		children.remove(child);
		child.setParent(null);
//		fireChildrenChanged();
		return child;
	}

	private LinkedList<PropertyChangeListener> listenerList = new LinkedList<PropertyChangeListener>();

	public void addListener(PropertyChangeListener listener) {
		if (!listenerList.contains(listener))
			listenerList.add(listener);
	}

	public void removeListener(PropertyChangeListener listener) {
		listenerList.remove(listener);
	}

//	private void fireNameChanged() {
//		PropertyChangeEvent event = new PropertyChangeEvent(this, NAME_CHANGED,
//				false, true);
//		for (PropertyChangeListener listener : listenerList)
//			listener.propertyChange(event);
//	}
//
//	private void fireDescriptionChanged() {
//		PropertyChangeEvent event = new PropertyChangeEvent(this,
//				DESCRIPTION_CHANGED, false, true);
//		for (PropertyChangeListener listener : listenerList)
//			listener.propertyChange(event);
//	}
//
//	private void fireChildrenChanged() {
//		PropertyChangeEvent event = new PropertyChangeEvent(this,
//				CHILDREN_CHANGED, false, true);
//		for (PropertyChangeListener listener : listenerList)
//			listener.propertyChange(event);
//	}

	public boolean isAncestorOf(Term next) {
		while (next.getParent() != null) {
			if (next.getParent() == this)
				return true;
			next = next.getParent();
		}
		return false;
	}

	public boolean isFirstChild(Term child) {
		return children.indexOf(child) == 0;
	}

	public int getChildrenCount() {
		return children.size();
	}

	public Term getFirstChild() {
		if (children.isEmpty())
			return null;
		return children.get(0);
	}

	public Term getLastChild() {
		if (!children.isEmpty()) {
			return children.get(children.size() - 1);
		}
		return null;
	}

	public Term getChildAt(int index) {
		return children.get(index);
	}

	public int getChildIndex(Term term) {
		return children.indexOf(term);
	}

	public Object getComponent() {
		return component;
	}

	public void setComponent(Object component) {
		this.component = component;
	}

	public boolean canHide() {
		boolean b = false;
		Pattern p = Pattern.compile(ContainerComposite.CONTAINER + " \\["
				+ "[A-Za-z]*" + "\\]");
		if (name == null)
			b = true;
		else {
			Matcher m = p.matcher(name);
			b = m.matches();
		}

		return relation == RelationType.AND && b
				&& componentClass.getName().equals(description) && icon == null;
	}

	public Term copy() {
		Term f = new Term(domainModel);
		f.setName(name);
		f.setDescription(description);
		f.setRelation(relation);
		f.setIcon(icon);
		f.setParent(parent);
		f.setChildren(children);
		f.setComponentClass(componentClass);
		f.setComponent(component);
		return f;
	}

	/**
	 * used externally to fire events, eg for graphical changes not anticipated
	 * in the core implementation
	 * 
	 * @param event
	 */
	public void fire(PropertyChangeEvent event) {
		for (PropertyChangeListener listener : listenerList)
			listener.propertyChange(event);
	}

	//
	// @Override
	// public Feature clone() {
	// Feature feature = new Feature(featureModel, name);
	// for (Feature child : children) {
	// feature.addChild(child.clone());
	// }
	// feature.relationType = relationType;
	// return feature;
	// }

	@Override
	public String toString() {
		if (!Util.isEmpty(name))
			return name;
		if (!Util.isEmpty(description))
			return description;
		if (componentClass != null)
			return "Term with componentClass ["
					+ componentClass.getSimpleName() + "]";
		return null;
	}
	
	public boolean isEmpty() {
		return Util.isEmpty(name) && Util.isEmpty(description) && getChildrenCount() <= 1 && this.relation == RelationType.AND;
	}
}
