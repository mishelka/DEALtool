package gui.model.domain;

import gui.analyzer.util.Util;
import gui.model.domain.constraint.Constraint;
import gui.model.domain.relation.RelationType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;

import org.apache.commons.lang3.StringUtils;

/**
 * A term represents a single unit the domain model.
 * Each term can contain a list of children terms.
 * A term can represent domain, sub-domain or a single term.
 * The hierarchy of terms in the domain models represents the hierarchy 
 * or domains and sub-domains in a domain.
 * <br/>
 * Each term is extracted from a UI component, which we call the "target component".
 * <br/>
 * Each term contains the basic domain information, which can be extracted from the target component:
 * <ul>
 * <li>name - primary domain information, which we are able to extract from a component</li>
 * <li>description - secondary domain information, which we are able to extract from a component (such as tooltip) - description can provide additional information about the term</li>
 * <li>relation - the relation between this term's children</li>
 * <li>parentRelation - the relation, which transfers to this term's parent</li>
 * <li>icon - sometimes, if there is no textual information available in the target component, an icon can contain domain information</li>
 * <li>labelForComponent - if there is no textual information available in the target component, a JLabel can provide additional domain information</li>
 * <li>constraints - the constraints for the term, such as: length, range, enumeration, etc.</li>
 * </ul>
 * 
 * <br/>
 * 
 * Each term contains a reference to the domain model in which it is located, 
 * to the target component and to its class.
 * 
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class Term {
	/**
	 * Each term has its name, which is a primary information extracted from a
	 * component. If a term has no name and no description, then the term is not
	 * important for the domain model (it was created from a component, which
	 * does not provide any usable domain information).
	 */
	private String name;
	/**
	 * Optionally each term can have its description, which is secondary
	 * information extracted from a component. For example from tooltip text, or
	 * from actionCommand field.
	 */
	private String description;
	// TODO: remove relation and set outgoingRelations instead of this!!!
	// priority highest!
	/**
	 * If it is possible to extract any relations from components, which they
	 * have between ich other in one group, then this relation is saved in this
	 * variable.
	 */
	private RelationType relation = RelationType.AND;
	/**
	 * This is a relation which a parent generates for its children. For example
	 * a TabbedPane component generates a mutual exclusive relation between its
	 * children.
	 */
	private RelationType parentRelation = null;
	/**
	 * Sometimes, an icon contains domain information either graphically or
	 * textually. This information is difficult to parse, therefore we extract
	 * the whole icon and it is on user (domain analyst) to review the important
	 * icons.
	 */
	private Icon icon;

	/** a label describing this component will be temporarily saved here */
	private JLabel labelForComponent;

	/**
	 * a flag telling the extractor if it should extract any children of this
	 * term's component further
	 */
	private boolean extractChildren = true;

	/**
	 * For textual components (like text area, text field, etc.) we can also
	 * extract some constraints, like text length, possible values, type, etc.
	 * A constraint can be one of the types contained in the 
	 * gui.model.domain.constraint package.
	 */
	private List<Constraint> constraints = new ArrayList<Constraint>();

	/**
	 * Each term is located in a domain model, this is a reference to the domain
	 * model.
	 */
	private DomainModel domainModel;

	/**
	 * The parent of this term in the term graph. If this term is a root term,
	 * then this term has no parent and the value of this field is null.
	 */
	private Term parent;
	/**
	 * The child terms of this term in the term graph. If this term is a leaf,
	 * then the children array contains no item.
	 */
	private List<Term> children = new ArrayList<Term>();

	/**
	 * Each term is created from a component. The componentClass and component
	 * fields are references to this component.
	 */
	@SuppressWarnings("rawtypes")
	private Class componentClass;
	/**
	 * Each term is created from a component. The componentClass and component
	 * fields are references to this component.
	 */
	private Object component;

	/**
	 * Alternative for componentClass in HTML user interfaces.
	 */
	private String componentClassHtml;
	
	/**
	 * HTML href if the term was an html link
	 */
	private String componentHrefHtml;
	
	/**
	 * Each term is extracted from a component and this component is included in
	 * a group according to what information can be extracted from this
	 * component.
	 */
	private ComponentInfoType componentInfoType;
	
	/**
	 * Each term can be set to be hidden so it will not appear in generated ontology. 
	 */
	private boolean hidden = false;
	
	/**
	 * @param domainModel the domain model, in which this term is located
	 */
	public Term(DomainModel domainModel) {
		this.domainModel = domainModel;
	}

	/**
	 * @param domainModel the domain model, in which this term is located
	 * @param name the name of this term
	 */
	public Term(DomainModel domainModel, String name) {
		this.domainModel = domainModel;
		this.name = name;
	}

	/**
	 * @return the relation, which should 
	 * in the future be shifted to this terms' parent
	 */
	public RelationType getParentRelation() {
		return parentRelation;
	}

	/**
	 * @param parentRelation the relation, which should 
	 * in the future be shifted to this term's parent
	 */
	public void setParentRelation(RelationType parentRelation) {
		this.parentRelation = parentRelation;
	}

	/**
	 * @param relation the relation between this node's children
	 */
	public void setRelation(RelationType relation) {
		this.relation = relation;
	}

	/**
	 * @return the relation between this node's children
	 */
	public RelationType getRelation() {
		return relation;
	}

	/**
	 * @return the list of constraints for this term
	 */
	public List<Constraint> getConstraints() {
		return constraints;
	}

	/**
	 * @param constraints the list of constraints for this term
	 */
	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}

	/**
	 * Each term is generated from a component. This method sets the reference to the class of the component, from which this term was generated.
	 * @param objectClass the reference to the class of the component, from which this term was generated.
	 */
	public void setComponentClass(
			@SuppressWarnings("rawtypes") Class objectClass) {
		this.componentClass = objectClass;
	}
	
	/**
	 * Alternative for componentClass in HTML user interfaces.
	 * @param componentClassHtml the HTML component class
	 */
	public void setComponentClassHtml(String componentClassHtml) {
		this.componentClassHtml = componentClassHtml;
	}
	
	/**
	 * HTML href if the term was an html link
	 * @return html href, if the term was an html link, null otherwise
	 */
	public String getComponentHrefHtml() {
		return componentHrefHtml;
	}
	
	/**
	 * HTML href if the term was an html link
	 * @param html href, if the term was an html link, null otherwise
	 */
	public void setComponentHrefHtml(String componentHrefHtml) {
		this.componentHrefHtml = componentHrefHtml;
	}

	/**
	 * Each term is generated from a component. This method gets the reference to the class of the component, from which this term was generated, and returns it's simple name.
	 * @return the simple name of the class of the component, from which this term was generated.
	 */
	public String getComponentClassSimpleName() {
		return componentClass.getSimpleName();
	}

	/**
	 * Each term is generated from a component. This method gets the reference to the class of the component, from which this term was generated.
	 * @return the reference to the class of the component, from which this term was generated.
	 */
	@SuppressWarnings("rawtypes")
	public Class getComponentClass() {
		return componentClass;
	}
	
	/**
	 * Alternative for the componentClass in HTML user interfaces.
	 * @return the component class in HTML user interfaces
	 */
	public String getComponentClassHtml() {
		return componentClassHtml;
	}

	/**
	 * @return the info type of the component, from which this term was generated
	 */
	public ComponentInfoType getComponentInfoType() {
		return componentInfoType;
	}

	/**
	 * @param componentInfoType  the info type of the component, from which this term was generated
	 */
	public void setComponentInfoType(ComponentInfoType componentInfoType) {
		this.componentInfoType = componentInfoType;
	}

	/**
	 * @return the domain model, in which this term is located.
	 */
	public DomainModel getDomainModel() {
		return domainModel;
	}

	/**
	 * @return the name of this term
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name of this term
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description of this term (secondary domain information)
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description of this term (secondary domain information)
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sometimes, even if a component does not have any textual description,
	 * it can have an icon, which explains its meaning. This icon can be
	 * graphical or textual, but either way it can contain valuable domain 
	 * information.
	 * @return the icon associated with this term
	 */
	public Icon getIcon() {
		return icon;
	}

	/**
 	 * Sometimes, even if a component does not have any textual description,
	 * it can have an icon, which explains its meaning. This icon can be
	 * graphical or textual, but either way it can contain valuable domain 
	 * information.
	 * @param icon the icon associated with this term
	 */
	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	/**
	 * Sometimes, if the component has no domain information, it is described
	 * by a label. The association between the component and the label component
	 * is set by the "labelFor" attribute of the JLabel component.
	 * @return reference to a JLabel component, which describes this component.
	 */
	public JLabel getLabelForComponent() {
		return labelForComponent;
	}

	/**
	 * Sometimes, if the component has no domain information, it is described
	 * by a label. The association between the component and the label component
	 * is set by the "labelFor" attribute of the JLabel component.
	 * @param labelFor reference to a JLabel component, which describes this component.
	 */
	public void setLabelForComponent(JLabel labelFor) {
		this.labelForComponent = labelFor;
	}

	/**
	 * The extractChildren flag tells the extractor if it should extract any children of this
	 * term's component further.
	 * @param extractChildren the value of the extractChildren flag to be set
	 */
	public void setExtractChildren(boolean extractChildren) {
		this.extractChildren = extractChildren;
	}

	/**
	 * The extractChildren flag tells the extractor if it should extract any children of this
	 * term's component further.
	 * @return the value of the extractChildren flag
	 */
	public boolean extractChildren() {
		return extractChildren;
	}

	/**
	 * @param newParent the new parent term of this term
	 */
	public void setParent(Term newParent) {
		if (newParent == parent)
			return;
		parent = newParent;
	}

	/**
	 * @return the parent term of this term
	 */
	public Term getParent() {
		return parent;
	}

	/**
	 * @return the list of this terms' children
	 */
	public List<Term> getChildren() {
		return children;
	}

	/**
	 * @return true if this term has children, false otherwise
	 */
	public boolean hasChildren() {
		return !isLeaf();
	}

	/**
	 * @return true if this term is a leaf, false otherwise
	 */
	public boolean isLeaf() {
		return children.isEmpty();
	}

	/**
	 * This method returns true, if this term is a direct sibling to <tt>to</tt>, false if not.
	 * Direct sibling means, that in the list of all siblings of this node's parent,
	 * the <tt>to</tt> term is right next to this term. 
	 * @param to the second sibling
	 * @return true if this term is a direct sibling to <tt>to</tt>, false otherwise
	 */
	public boolean isDirectSiblingTo(Term to) {
		Term parent = this.getParent();

		if (parent == null || !parent.getChildren().contains(to))
			return false;

		int index = parent.getChildIndex(this);
		int indexTo = parent.getChildIndex(to);

		if ((index + 1) == indexTo || (index - 1) == indexTo)
			return true;

		return false;
	}

	/**
	 * Adds the newChild to this term and this term becomes a parent of the newChild term.
	 * @param newChild the term to be added to this term as a child
	 */
	public void addChild(Term newChild) {
		children.add(newChild);
		newChild.setParent(this);
	}

	/**
	 * Adds a child to this term on a specified position
	 * @param index the index of the new child's position
	 * @param newChild the term to be added to this term as a child
	 */
	public void addChildAtPosition(int index, Term newChild) {
		children.add(index, newChild);
		newChild.setParent(this);
	}

	/**
	 * Adds all the terms in the specified list as children into this term.
	 * @param childrenToAdd the list of terms to be added to this term as children
	 */
	public void addAll(List<Term> childrenToAdd) {
		for (Term t : childrenToAdd) {
			addChild(t);
		}
	}

	/**
	 * Replaces the oldChild term with the newChild term. 
	 * Also sets the parent of oldChild to null and newChild to this.
	 * @param oldChild the child to be removed and replaced with newChild
	 * @param newChild the child to be added instead of the oldChild
	 */
	public void replaceChild(Term oldChild, Term newChild) {
		int index = children.indexOf(oldChild);
		children.set(index, newChild);
		oldChild.setParent(null);
		newChild.setParent(this);
	}

	/**
	 * Removes the specified child from this term.
	 * @param child the term to be removed from this term's children
	 */
	public void removeChild(Term child) {
		children.remove(child);
		child.setParent(null);
	}

	/**
	 * Removes the last child of this term.
	 * @return the removed child
	 */
	public Term removeLastChild() {
		Term child = children.get(children.size() - 1);
		children.remove(child);
		child.setParent(null);
		return child;
	}

	/**
	 * Removes all terms in the specified list from this term.
	 * @param childrenToRemove the list of child terms to be removed from this term
	 */
	public void removeAll(List<Term> childrenToRemove) {
		for (Term t : childrenToRemove) {
			if (children.contains(t)) {
				removeChild(t);
			}
		}
	}

	/**
	 * Removes all children specified in the list from this node and all children of their children recursively.
	 * @param childrenToRemove the list of terms to be removed recursively.
	 */
	public void removeAllWithNesting(List<Term> childrenToRemove) {
		for (Term t : childrenToRemove) {
			if (children.contains(t)) {
				removeChild(t);
			}
		}

		for (Term t : children) {
			t.removeAllWithNesting(childrenToRemove);
		}
	}
	
	/**
	 * Removes all bad characters from term name and description. Bad charactes are: [^\\w\\s-]*.
	 */
	public void removeBadCharacters() {
		Iterator<Term> i = iterator();

		while (i.hasNext()) {
			i.next().removeBadCharacters();
		}

		// actual removing of bad characters
		setName(Util.removeBadCharacters(getName()));
		setDescription(Util.removeBadCharacters(getDescription()));
	}
	
	/**
	 * Finds stereotypes in html and transforms the terms accordingly.
	 * Example of a stereotype: 
	 * <div>
	 * 	<h5>some title<h5>
	 * 	<ul>
	 * 		some items
	 * 	</ul>
	 * </div>
	 * The "some title" title is bound with the underlying list by applying the div element. 
	 */
	public void transformHtmlTags() {
		if(this.getComponentInfoType() == ComponentInfoType.CONTAINERS && this.hasChildren()) {
			System.out.println(">>>> " + this);
	
			Term t = this.getFirstChild();
			if(t.getComponentClassHtml() != null)
				switch(t.getComponentClassHtml()) {
					case "<h6>": case "<h5>": case "<h4>": case "<h3>": case "<h2>": case "<h1>": case "<span>": 
						this.setName(t.getName()); 
						this.setDescription(t.getDescription());
						t.setName(null);
						t.setDescription(null);
						break;
				}
		}
		
		for(Term child : this.children) {
			child.transformHtmlTags();
		}
	}

	/**
	 * Removes all leafs with no relevant domain information 
	 * from this term children recursively.
	 * 							<br/>
	 * |_x			|_x 		<br/>
	 *   |_o		  |_x 		<br/>
	 *   |_x   ==>	 			<br/>
	 *   |_o					<br/>
	 *  						<br/>
	 * x - term with relevant domain information <br/>
	 * o - term with no relevant domain information <br/>
	 * 							<br/>
	 * @return true if anything has been removed, false otherwise
	 */
	public boolean removeEmptyLeafs() {
		boolean removed = false;
		Iterator<Term> i = iterator();

		while (i.hasNext()) {
			removed |= i.next().removeEmptyLeafs();
		}

		// actual removing of empty leafs
		i = iterator();
		while (i.hasNext()) {
			Term t = i.next();
			if ((t.isLeaf() && t.canBeRemoved()) || (t.isLeaf() && t.hasNoInfoExceptRelation())) {
				i.remove();
				removed = true;
			}
		}

		return removed;
	}

	/**
	 * This method does the following for all children of this term recursively:
	 * If node without any relevant information has any number of children and 
	 * no siblings, shift the children to its parent and remove the node. 
	 * 											<br/>
	 * o				o				o 		<br/>
	 *	|_o				|_o				|_x 	<br/>
	 *	  |_o	  ==> 	  |_x	  ==> 	|_x 	<br/>
	 *		|_x		  	  |_x			 		<br/>
	 *		|_x					 				<br/>
	 *											<br/>
	 * x - term with relevant domain information <br/>
	 * o - term with no relevant domain information <br/>
	 * 							<br/>
	 *  
	 * @return true if anything has been removed, false otherwise
	 */
	public boolean removeMultipleNestings() {
		boolean removed = false;
		Iterator<Term> i = iterator();

		//recursion
		while (i.hasNext()) {
			removed |= i.next().removeMultipleNestings();
		}
		
		for(int indx = getChildrenCount() - 1; indx >= 0; indx--) {
			Term ch = getChildAt(indx);
			if(ch.canBeRemoved()) {
				this.addAll(ch.getChildren());
				this.removeChild(ch);
				removed = true;
			}
		}

		// actual removing of a multiple nesting
//		if (getChildrenCount() == 1) {
//			Term son = this.getFirstChild();
//			if (son.canBeRemoved()) {
//				// this: o1---o2---o3
//				// is transformed to: o1---o3
//				this.addAll(son.getChildren());
//				this.removeChild(son);
//				removed = true;
//			}
//		}

		return removed;
	}
	
	/**
	 * If there are terms, which have only one child and the child is a leaf, 
	 * then this leaf is moved to its grandparent and its parent is removed. Recursive function.
	 * <br/>
	 * Parent-Grandparent-Leaf ==> Parent-Leaf
	 * @return true if anything was shifted, false otherwise
	 */
	public boolean shiftSingleChildLeafs() {
		boolean removed = false;
		Iterator <Term> i = iterator();
		
		if(this.hasChildren()) {
			if(this.getChildrenCount() == 1) {
				Term singleChild = this.getChildAt(0);
				if(singleChild.isLeaf() && this.canBeRemoved()) {
					//shift and don't go into children --> return now
					this.copyInfoFrom(singleChild);
					this.removeChild(singleChild);
					
					return true;
				}
			}
		}
		
		while(i.hasNext()) {
			removed |= i.next().shiftSingleChildLeafs();
		}
		
		return removed;
	}
	
	/**
	 * If there are terms, which have only one child (with relation) and that relation child has some children, then 
	 * the children will be moved to the first term and the relation will be moved to the first term. The relation term
	 * will be removed.
	 * 
	 * Grandparent-RelationParent-Children ==> RelationGrandparent-Children
	 * @return true if anything was shifted, false otherwise
	 */
	public boolean shiftParentRelations() {
		boolean removed  = false;
		
		if(this.hasChildren()) {
			if(this.getChildrenCount() == 1) {
				Term relationChild = this.getChildAt(0);
				if(relationChild.isRelationOnly() && relationChild.hasChildren()) {
					this.setRelation(relationChild.getRelation());
					this.addAll(relationChild.getChildren());
					this.removeChild(relationChild);
				}
			}
		}
		
		Iterator <Term> i = iterator();
		
		while(i.hasNext()) {
			removed |= i.next().shiftParentRelations();
		}
		
		return removed;
	}
	
	/**
	 * Copies all domain relevant information from the given term to this term.
	 * @param term the term the information should be copied from.
	 */
	private void copyInfoFrom(Term term) {
		this.setComponent(term.getComponent());
		this.setComponentClass(term.getComponentClass());
		this.setComponentInfoType(term.getComponentInfoType());
		this.setConstraints(term.getConstraints());
		this.setDescription(term.getDescription());
		this.setExtractChildren(term.extractChildren());
		this.setIcon(term.getIcon());
		this.setLabelForComponent(term.getLabelForComponent());
		this.setName(term.getName());
		this.setParent(term.getParent());
		this.setParentRelation(term.getParentRelation());
		this.setRelation(term.getRelation());
	}
	
	/**
	 * Removes all terms of the given component info type.
	 * @param infoType the component info type, according to which the terms should be removed
	 * @return true if anything was removed, false otherwise
	 */
	public boolean removeTermsOfInfoType(ComponentInfoType infoType) {
		boolean removed = false;
		Iterator<Term> i = iterator();
		ArrayList<Term> toBeAdded = new ArrayList<Term>();

		while (i.hasNext()) {
			Term son = i.next();
			removed |= son.removeTermsOfInfoType(infoType);
		}
		
		i = iterator();
		while (i.hasNext()) {
			Term son = i.next();
			
			if (infoType.equals(son.getComponentInfoType())) {
				//remove son and transfer all its children into this
				if(son.hasChildren()) {
					toBeAdded.addAll(son.getChildren());
				}
				i.remove();
				removed = true;
			}
		}
		
		this.addAll(toBeAdded);
		
		return removed;
	}

	/**
	 * @return the number of this term's children.
	 */
	public int getChildrenCount() {
		return children.size();
	}

	/**
	 * @return the first child of this term.
	 */
	public Term getFirstChild() {
		if (children.isEmpty())
			return null;
		return children.get(0);
	}

	/**
	 * @return the last child of this term.
	 */
	public Term getLastChild() {
		if (!children.isEmpty()) {
			return children.get(children.size() - 1);
		}
		return null;
	}

	/**
	 * @param index the position of the child term
	 * @return the term at the specified position
	 */
	public Term getChildAt(int index) {
		return children.get(index);
	}

	/**
	 * @param term the term
	 * @return the index of the given term in the list of the children
	 */
	public int getChildIndex(Term term) {
		return children.indexOf(term);
	}

	/**
	 * @return the component, from which this term is extracted
	 */
	public Object getComponent() {
		return component;
	}

	/**
	 * @param component the component, from which this term is extracted
	 */
	public void setComponent(Object component) {
		this.component = component;
	}

	/**
	 * If this term contains no relevant domain information, it can be removed.
	 * @return true if this term can be removed, false otherwise
	 */
	public boolean canBeRemoved() {
		return hasNoInfoExceptRelation() && relation == RelationType.AND;
	}
	
	/**
	 * @return true if this term contains relation only, false otherwise.
	 */
	public boolean isRelationOnly() {
		return hasNoInfoExceptRelation() && relation != RelationType.AND;
	}
	
	private boolean hasNoInfoExceptRelation() {
		return !hasName()
				&& (!hasDescription() || (description.equals(componentClass.getName())))
				&& (!hasLabelForComponent() && !hasIcon());
	}

	/**
	 * @return true if this term has a name, false otherwise
	 */
	public boolean hasName() {
		return !Util.isEmpty(name);
	}

	/**
	 * @return true if this term has a description, false otherwise
	 */
	public boolean hasDescription() {
		return !Util.isEmpty(description);
	}

	/**
	 * @return true if this term has an icon, false otherwise
	 */
	public boolean hasIcon() {
		return icon != null;
	}
	
	/**
	 * @return true if this term has a labelFor component, false otherwise
	 */
	public boolean hasLabelForComponent() {
		return labelForComponent != null;
	}

	/**
	 * Each term is extracted from a component.
	 * This method recursively finds a term which was extracted from the specified component.
	 * @param component the component from which the term was extracted
	 * @return the term, which was extracted from the given component
	 */
	public Term getTermForComponent(Object component) {
		if (this.component != null && this.component.equals(component))
			return this;
		Term result = null;
		for (Term t : children) {
			if ((result = t.getTermForComponent(component)) != null) {
				return result;
			}
		}
		return null;
	}
	
	/**
	 * Finds all terms with the given name. Serves for the purpose of search.
	 * @param name the name to be searched for
	 * @return the list of all terms with the given name
	 */
	public List<Term> findTermsByName(String name) {
		List<Term> terms = new ArrayList<Term>();
		if (this.name != null && this.name.equalsIgnoreCase(name))
			terms.add(this);
		List<Term> result = null;
		for (Term t : children) {
			result = t.findTermsByName(name);
			terms.addAll(result);
		}
		return terms;
	}
	
	/**
	 * Finds all terms with the given description. Serves for the purpose of search.
	 * @param description the description to be searched for
	 * @return the list of all terms with the given description
	 */
	public List<Term> findTermsByDescription(String description) {
		List<Term> terms = new ArrayList<Term>();
		if (this.description != null && this.description.equalsIgnoreCase(description))
			terms.add(this);
		List<Term> result = null;
		for (Term t : children) {
			result = t.findTermsByDescription(description);
			terms.addAll(result);
		}
		return terms;
	}

	/**
	 * Returns all children (including this term) of this term in a list. Recurisve function.
	 * @param list the list, to which the children should be added.
	 * @return all children (including this term) of this term in a list.
	 */
	public List<Term> getAllTerms(List<Term> list) {
		list.add(this);
		for (Term t : children) {
			t.getAllTerms(list);
		}
		return list;
	}

	/**
	 * Returns the iterator for the children terms
	 * @return the iterator for the children terms
	 */
	public Iterator<Term> iterator() {
		return this.children.iterator();
	}

	@Override
	public String toString() {
		if (!Util.isEmpty(name))
			return name;
		if (!Util.isEmpty(description))
			return description;
		if (componentClass != null)
			return "\"\"";
		return "";
	}
	
	public String toPlainText(int indent) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(StringUtils.repeat("\t", indent));
		sb.append(this.toString());
		sb.append("\n");
		
		for(Term t : children) {
			sb.append(t.toPlainText(indent+1));
		}
		
		return sb.toString();
	}
	
	/**
	 * Removes all domain information from this term, which includes:
	 * <ul>
	 * <li>setting its name to null</li>
	 * <li>setting its relation to default (RelationType.AND)</li>
	 * <li>setting its description to null</li>
	 * <li>setting its label component to null</li>
	 * <li>setting its icon to null</li>
	 * </ul>
	 */
	public void removeDomainInformation() {		
		this.setName(null);
		this.setRelation(RelationType.AND);
		this.setDescription(null);
		this.setLabelForComponent(null);
		this.setIcon(null);
	}
	
	/**
	 * Set if term should be included in generated ontology.
	 * @param hidden whether to hide 
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	/**
	 * Will term appear in generated ontology?
	 */
	public boolean isHidden() {
		return hidden;
	}
}