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

public class Term {
	/** Each term has its name, which is a primary information extracted from a component. 
	 * If a term has no name and no description, then the term is not important for the domain model 
	 * (it was created from a component, which does not provide any usable domain information). */
	private String name;
	/** Optionally each term can have its description, which is secondary information extracted from a component. For example from tooltip text, or from actionCommand field.*/
	private String description;
	//TODO: remove relation and set outgoingRelations instead of this!!! priority highest!
	/** If it is possible to extract any relations from components, which they have between ich other in one group, then this relation is saved in this variable. */
	private RelationType relation = RelationType.AND;
	/** This is a relation which a parent generates for its children. For example a TabbedPane component generates a mutual exclusive relation between its children. */
	private RelationType parentRelation = null;
	/** Sometimes, an icon contains domain information either graphically or textually. This information is difficult to parse, therefore we extract the whole icon and it is on user (domain analyst) to review the important icons. */
	private Icon icon;
	
	//TODO: add everything for this, priority highest
	private List<AbstractRelation> outgoingRelations;
	
	/** For textual components (like text area, text field, etc.) we can also extract some constraints, like text length, possible values, type, etc. 
	 * This is not implemented yet. 
	 */
	private List<Constraint> constraints;

	/** Each term is located in a domain model, this is a reference to the domain model. */
	private DomainModel domainModel;

	/** The parent of this term in the term graph.
	 * If this term is a root term, then this term has no parent and the value of this field is null. */
	private Term parent;
	/** The child terms of this term in the term graph.
	 * If this term is a leaf, then the children array contains no item. */
	private List<Term> children = new ArrayList<Term>();
	
	/** Each term is created from a component. The componentClass and component fields are references to this component. */
	@SuppressWarnings("rawtypes")
	private Class componentClass;
	/** Each term is created from a component. The componentClass and component fields are references to this component. */
	private Object component;
	
	/** Each term is extracted from a component and this component is included in a group according to what information can be extracted from this component. */
	private ComponentInfoType componentInfoType;

	
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
	
	public List<Constraint> getConstraints() {
		return constraints;
	}
	
	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
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
	
	public ComponentInfoType getComponentInfoType() {
		return componentInfoType;
	}
	
	public void setComponentInfoType(ComponentInfoType componentInfoType) {
		this.componentInfoType = componentInfoType;
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
	
	public void addAll(List<Term> childrenToAdd) {
		for(Term t : childrenToAdd) {
			addChild(t);
		}
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
	
	public void removeAll(List<Term> childrenToRemove) {
		for(Term t : childrenToRemove) {
			if(children.contains(childrenToRemove)) {
				removeChild(t);
			}
		}
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

	public boolean canBeRemoved() {
		boolean b = false;
		
		b = (name==null || name.isEmpty()) 
				&& (relation == RelationType.AND
				&& (componentClass.getName().equals(description) 
						|| description == null || description.isEmpty()) 
				&& icon == null);
		
		return b;
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
	
	public boolean hasName() {
		return name != null && !name.isEmpty();
	}
	
	public boolean hasDescription() {
		return description != null && !description.isEmpty();
	}
	
	public boolean hasIcon() {
		return icon != null;
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

	@Override
	public String toString() {
		if (!Util.isEmpty(name))
			return name;
		if (!Util.isEmpty(description))
			return description;
		if (componentClass != null)
			return "\"\"";
		return null;
	}
	
	public boolean isEmpty() {
		return Util.isEmpty(name) && Util.isEmpty(description) && getChildrenCount() <= 1 && this.relation == RelationType.AND;
	}
}
