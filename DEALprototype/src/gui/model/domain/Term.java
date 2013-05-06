package gui.model.domain;

import gui.analyzer.util.Util;
import gui.model.domain.constraint.Constraint;
import gui.model.domain.relation.RelationType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;

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
	
	/** a label describing this component will be temporarily saved here */
	private JLabel labelForComponent;
	
	/** a flag telling the extractor if it should extract any children of this term's component further */
	private boolean extractChildren = true;
	
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

	public void setRelation(RelationType relation) {
		this.relation = relation;
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
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}
	
	public JLabel getLabelForComponent() {
		return labelForComponent;
	}
	
	public void setLabelForComponent(JLabel labelFor) {
		this.labelForComponent = labelFor;
	}
	
	public void setExtractChildren(boolean extractChildren) {
		this.extractChildren = extractChildren;
	}
	
	public boolean extractChildren() {
		return extractChildren;
	}

	public void setParent(Term newParent) {
		if (newParent == parent)
			return;
		parent = newParent;
	}

	public Term getParent() {
		return parent;
	}

	public List<Term> getChildren() {
		return children;
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
	}

	public void addChildAtPosition(int index, Term newChild) {
		children.add(index, newChild);
		newChild.setParent(this);
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
	}

	public void removeChild(Term child) {
		children.remove(child);
		child.setParent(null);
	}

	public Term removeLastChild() {
		Term child = children.get(children.size() - 1);
		children.remove(child);
		child.setParent(null);
		return child;
	}
	
	public void removeAll(List<Term> childrenToRemove) {
		for(Term t : childrenToRemove) {
			if(children.contains(t)) {
				removeChild(t);
			}
		}
	}
	
	public void removeAllWithNesting(List<Term> childrenToRemove) {
		for(Term t : childrenToRemove) {
			if(children.contains(t)) {
				removeChild(t);
			}
		}
		
		for(Term t : children) {
			t.removeAllWithNesting(childrenToRemove);
		}
	}
	
	public boolean removeEmptyLeafs() {
		boolean removed = false;
		Iterator<Term> i = iterator();
		
		while(i.hasNext()) {
			removed |= i.next().removeEmptyLeafs();
		}
		
		//actual removing of empty leafs
		i = iterator();
		while(i.hasNext()) {
			Term t = i.next();
			if(t.isLeaf() && t.canBeRemoved()) {
				i.remove();
				removed = true;
			}
		}
		
		return removed;
	}
	
	public boolean removeMultipleNestings() {
		boolean removed = false;
		Iterator<Term> i = iterator();
		
		while(i.hasNext()) {
			removed |= i.next().removeMultipleNestings();
		}
		
		//actual removing of a multiple nesting
		if(getChildrenCount() == 1) {
			Term son = this.getFirstChild();
			if(son.canBeRemoved()) {
				// this: o1---o2---o3
				// is transformed to: o1---o3
				this.addAll(son.getChildren());
				this.removeChild(son);
				removed = true;
			}
		}
		
		return removed;
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
				&& labelForComponent == null
				&& icon == null);
		
		return b;
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
	
	public Iterator<Term> iterator() {
		return this.children.iterator();
	}
	
	public Term getTermForComponent(Object component) {
		if(this.component != null && this.component.equals(component))
			return this;
		Term result = null;
		for(Term t : children) {
			if((result = t.getTermForComponent(component)) != null) {
				return result;
			}
		}
		return null;
	}
	
	public List<Term> getAllTerms(List<Term> list) {
		list.add(this);
		for(Term t : children) {
			t.getAllTerms(list);
		}
		return list;
	}
}