package gui.model;

import gui.analyzer.Recorder;
import gui.analyzer.handlers.Composite;
import gui.analyzer.handlers.Composites;
import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.handlers.DomainIdentifiables;
import gui.analyzer.handlers.RecordSupport;
import gui.analyzer.handlers.RecordSupports;
import gui.analyzer.util.Util;
import gui.model.application.Scene;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.model.domain.relation.RelationType;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

/**
 * Creates a domain model based on a corresponding Scene. A Scene can be a window, a dialog, etc.
 * @author Michaela Baèíková
 */
public class DomainModelGenerator {
	/**
	 * The domain model which is created from the given scene is stored in this field - only for the purposes of this class.
	 */
	private DomainModel model;
	private Recorder recorder;
	
	/**
	 * Creates a domain model from the given scene.
	 * @param scene The scene which the model should be created from.
	 * @param name The name of the domain model / equals the name of the scene (title, application name).
	 * @return The domain model created from the scene.
	 */
	public DomainModel createDomainModel(Scene<?> scene, String name) {
		//create a new domain model with a default root
		model = new DomainModel(name);
		Term rootTerm = new Term(model, name);
		model.setRoot(rootTerm);
		
		
		//1. PHASE, extraction algorithm
		eXTRACT(scene, name, rootTerm);
		
		
		//2. PHASE, simplification algorithm
		sIMPLIFY();
		

		//The list of all terms is needed for the model to be able to work with recorder
		List<Term> allTermsInModel = new ArrayList<Term>();
		allTermsInModel = collectAllTerms(model.getRoot(), allTermsInModel);
		model.setTerms(allTermsInModel);
		
		
		//3. PHASE, recorder registration
		rEGISTER_RECORDER(scene);
		
		return model;
	}
	
	private List<Term> collectAllTerms(Term thisTerm, List<Term> collectedTerms) {
		collectedTerms.add(thisTerm);
		if(thisTerm.hasChildren()) {
			for(Term child : thisTerm.getChildren()) {
				collectAllTerms(child, collectedTerms);
			}
		}
		return collectedTerms;
	}
	
	
	/************************* Methods for Domain Model extraction ******************************/
	
	/**
	 * First phase of DEAL - extraction of the term graph.
	 * The extraction is performed recursively by traversing the component tree.
	 * Additional sub-step of the extraction phase is is extraction of additional relations from parent components.
	 *  
	 * @param scene  A scene to create the term graph from.
	 * @param name  The name of the domain model.
	 * @param rootTerm  The root term of the domain model.
	 */
	private void eXTRACT(Scene<?> scene, String name, Term rootTerm) {
		//extract subtree from the root scene
		extractSubtree(scene.getSceneContainer(), rootTerm);
		
		//additional derivation of relations from parents
		findParentRelationsInModel();
		
		//set the model root name and type and references to the scene
		model.getRoot().setName(name);
		model.getRoot().setRelation(RelationType.MODEL);
		model.getRoot().setComponent(scene);
		model.setScene(scene);
	}
	
	/**
	 * Create a subtree in the domain model. The subtree is created from information extracted from the component tree - recusively.
	 * Each recursive cycle has four steps:
	 * - traversing the superclasses to find the DomainIdentifiable handler.
	 * - if no handler is found in the first step, then extract the basic information available in the current component.
	 * - try to extract any information from the custom types of components. The tryCustom(..) method is available to add new extraction rules.
	 * - the fourth step is an attempt to use the extractSubtree() method recursively for each subcomponent of the current component - but only if the current component is a Composite (i.e. a container).
	 * @param component  The current component which the new term subtree should be extracted from.
	 * @param parentTerm a future parent of the newly created subtree.
	 */
	private <T> void extractSubtree(T component, Term parentTerm) {
		Term thisTerm = null;
		
		// first step: traversing the superclasses to find the DomainIdentifiable handler
		thisTerm = trySuperclass(component);
		if(thisTerm != null) {
			parentTerm.addChild(thisTerm);
		}
		
		// second step: try to extract information from the current component
		if(thisTerm == null) {
			thisTerm = tryComponent(component);
			if(thisTerm != null) {
				parentTerm.addChild(thisTerm);
			}
		}
		
		// third step: try custom extraction rules
		tryCustom(component, parentTerm, thisTerm);
		
		// fourth step: if the current component is a composite, extract subtrees from the its child components by recursively calling of the extractSubtree(..) method. 
		extractSubtreesFromChildren(component, thisTerm);
	}
	
	/**
	 * The first step of the extraction algorithm: traversing the superclasses to find the DomainIdentifiable handler.
	 * If there is a DomainIdentifiable handler for the provided component's class, 
	 * then the term is extracted using this handler.
	 * If there is no DomainIdentifiable handler for the provided component's class,
	 * then the algorithm tries to retrieve a handler for each superclass of the provided component's class in iterations.
	 * If there is no DomainIdentifiable handler for any of the provided component's superclasses, 
	 * then this method returns null.
	 * @param component  The component for which we try to find a DomainIdentifiable handler. This handler is found only if the component's class (or any of its superclasses) is supported by DEAL. @see gui.analyzer.handlers
	 * @param domainModel  The existing domain model is needed only as a reference when creating a new Term.
	 * @return a new  Term with extracted information using the DomainIdentifiable handler, 
	 * or null if there is no such handler for the provided component's class or for any of its superclasses.
	 */
	@SuppressWarnings("unchecked")
	private <T> Term trySuperclass(T component) {
		Class<? super T> componentClass = (Class<T>) component.getClass();
		Term thisTerm = null;
		Term t = null;
		
		while (componentClass != null) {
			DomainIdentifiable<? super T> domainIdentifiable = DomainIdentifiables
					.getInstance().getDomainIdentifiable(componentClass);
			if (domainIdentifiable != null) {
				t = domainIdentifiable.createTerm(component, model);
				if (t != null) {
					thisTerm = t;
				}
				
				componentClass = null;
				break;
			} else {
				componentClass = componentClass.getSuperclass();
			}
		}
		
		return thisTerm;
	}
	
	/** The second step of the extraction algorithm: if no handler is found in the first step, then extract the basic information available in the current component.
	 * Try to extract any available information from the current component.
	 * The possibilities to extract:
	 * - component name (as the term name)
	 * - toolTipText (as the term description) - only if the component is a JComponent
	 * - component class (as the term description) - if the component is not a JComponent
	 * @param component The component to extract the available information from.
	 * @return the extracted Term with all information possible to extract from the component.
	 */
	private <T> Term tryComponent(T component) {
		//create a new term with references to the component and its class
		Term thisTerm = new Term(model);
		thisTerm.setComponent(component);
		thisTerm.setComponentClass(component.getClass());

		if (component instanceof Component) {
			//if there's any, extract the component name
			Component c = ((Component) component);
			String str = c.getName();
			if (!Util.isEmpty(str))
				thisTerm.setName(str);
			
			//if the component is a JComponent, then we can extract toolTipText as a description
			if (component instanceof JComponent) {
				str = ((JComponent) component).getToolTipText();
				if (!Util.isEmpty(str))
					thisTerm.setDescription(str);
			}
			//if it's not a JComponent, then extract the component class name as a description
			else {
				thisTerm.setDescription(c.getClass().getSimpleName());
			}

			//set the default relation type
			thisTerm.setRelation(RelationType.AND);
		}
		
		return thisTerm;
	}
	
	/**
	 * The third step of the extraction algorithm: try to extract any information from the custom types of components. The tryCustom(..) method is available to add new extraction rules.
	 * In this method, new custom rules for extracting domain information can be added.
	 * For example, if there are domain information stored in the parent component, such as JTabbedPane, it is possible to
	 * add new extraction procedures into this method.
	 * Each new extracted information should be stored into thisTerm.
	 * The parentTerm is provided to be able to get domainModel or any relevant information from the parent.
	 * @param component the component, from which the term is extracted.
	 * @param parentTerm the parent term of thisTerm.
	 * @param thisTerm the actual term, to which the extracted domain information can be stored.
	 * @return thisTerm with added domain information.
	 */
	private <T> Term tryCustom(T component, Term parentTerm, Term thisTerm) {
		// try if parent is tabbedPane
		Object parentComponent = parentTerm.getComponent();
		if (parentComponent != null) {
			Object parentObject = parentTerm.getComponent();
			if (parentObject != null && parentObject instanceof JTabbedPane) {
				JTabbedPane jtp = (JTabbedPane) parentObject;

				if (component instanceof Component) {
					int i = jtp.indexOfComponent((Component) component);
					if (i != -1) {
						thisTerm.setName(jtp.getTitleAt(i));
						thisTerm.setDescription(jtp.getToolTipTextAt(i));
						thisTerm.setIcon(jtp.getIconAt(i));
					}
				}
			}
		}
		
		return thisTerm;
	}
	
	/**
	 * The fourth step of the extraction algorithm: an attempt to use the extractSubtree() method recursively for each subcomponent of the current component - but only if the current component is a Composite (i.e. a container).
	 * @param component  The current component which the new term subtree should be extracted from. 
	 * @param thisTerm  The actual term - the terms extracted from the current component are added into this term as children.
	 */
	@SuppressWarnings("unchecked")
	private <T> void extractSubtreesFromChildren(T component, Term thisTerm) {
		Class<? super T> componentClass = (Class<T>) component.getClass();
		Composite<? super T> composite = Composites.getInstance().getComposite(
				componentClass);
		if (composite != null) {
			for (Object subcomponent : composite.getComponents(component)) {
				extractSubtree(subcomponent, thisTerm);
			}
		}
	}
	
	/**
	 * Additional sub-step of the extraction phase - extraction of additional relations from parent components.
	 * Finds parent relations in the whole domain model.
	 * A parent relation is a relation, which is generated by a parent term for its children.
	 * For example, because of a JTabbedPane, the tabs are mutually exclusive. This method works recursively for the whole subtree of thisTree.
	 * A HashMap is created for each type of parent relation. This is for a case, where the parent generates a relation only for a subset of its children - in such a case a new subterm is generated for the group with the new relation and the other terms will remain in thisTerm children list.
	 * 
	 * @param model The domain to search the parent relations in.
	 */
	private void findParentRelationsInModel() {
		// if there is nothing in the model, do nothing
		if (model.getRoot() == null || model.getRoot().getChildrenCount() == 0)
			return;
		
		//recursively find parent relations in each subtree, starting with root
		findParentRelations(model.getRoot());
	}

	/**
	 * This method is called recursively and it is started by the findParentRelationsInModel(..) method.
	 * Finds parent relations in a subtree of the domain model.
	 * A parent relations is a relation, which is generated by a parent term for its children.
	 * For example, because of a JTabbedPane, the tabs are mutually exclusive. This method works recursively for the whole subtree of thisTree.
	 * A HashMap is created for each type of parent relation. This is for a case, where the parent generates a relation only for a subset of its children - in such a case a new subterm is generated for the group with the new relation and the other terms will remain in thisTerm children list.
	 *
	 * @param thisTerm The term, which generates the relations for its children.
	 */
	private void findParentRelations(Term thisTerm) {
		HashMap<RelationType, List<Term>> parentRelations = new HashMap<RelationType, List<Term>>();
		
		//put all children of thisTerm into a list under the parentRelation key in the parentRelations hash map.
		for (int i = 0; i < thisTerm.getChildrenCount(); i++) {
			Term t = thisTerm.getChildAt(i);

			//get parentRelation from term - this is stored during the Term creation 
			//@see gui.analyzer.handlers.DomainIdentifiable.createTerm(..)
			RelationType parentRelation = t.getParentRelation();
			if (parentRelation != null) {
				//for each relation type there is a list of components, to which this relation should be applied - add the child into this list.
				if (!parentRelations.keySet().contains(parentRelation)) {
					parentRelations.put(parentRelation, new ArrayList<Term>());
				}
				parentRelations.get(parentRelation).add(t);
			}
		}

		//for each relation type in the key set of parentRelations
		//create a new subterm of thisTerm and add all its children with this type of parentRelation into this subterm.
		for (RelationType parentRelation : parentRelations.keySet()) {
			//create a new term
			Term newTerm = new Term(thisTerm.getDomainModel());
			//set its relation to parentRelation
			newTerm.setRelation(parentRelation);

			//for each term in the list under the parentRelation key
			//shift every term from thisTerm to the newTerm
			for (Term f : parentRelations.get(parentRelation)) {
				thisTerm.removeChild(f);
				newTerm.addChild(f);
				newTerm.setComponent(f.getComponent());
				newTerm.setComponentClass(f.getComponentClass());
				f.setParentRelation(null);
			}

			thisTerm.addChild(newTerm);
		}

		//do this recursively
		for (int i = 0; i < thisTerm.getChildrenCount(); i++) {
			findParentRelations(thisTerm.getChildAt(i));
		}
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
	public boolean sIMPLIFY() {
		shiftRootInModel();
		
		Term root = model.getRoot();

		// if there is nothing in the model, then there's nothing to remove
		if (root == null || root.getChildrenCount() == 0)
			return false;

		return simplifyTerm(root);
	}
	
	/**
	 * Removes root from the model and sets the first child of root as a new root (shifts the first child of root up).
	 * This is to remove the default root.
	 * @param model the model, in which the root should be removed.
	 */
	private void shiftRootInModel() {
		Term child = model.getRoot().getFirstChild();
		model.setRoot(child);
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
		if (term.isLeaf() && term.canHide() && parent != null) {
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
				&& parent.getChildrenCount() == 1 && parent.canHide()) {
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
			
			if(term.getChildrenCount() == 1 && t.canHide()) {
				for(Term child : t.getChildren()) {
					term.addChild(child);
				}
				term.removeChild(t);
				wasRemoved = true;
			}
		}
		
		return wasRemoved;
	}
	
	
	/***************************************** Recorder stuff *************************************/
	public Recorder getRecorder() {
		return recorder;
	}

	public void setRecorder(Recorder recorder) {
		this.recorder = recorder;
	}
	
	private void rEGISTER_RECORDER(Scene<?> scene) {
		registerComponentTree(scene.getSceneContainer());
	}
	
	private <T> void registerComponentTree(T component) {
		tryRegisterSuperclass(component);
		
		tryRegisterChildren(component);
	}
	
	private <T> void tryRegisterSuperclass(T component) {
		@SuppressWarnings("unchecked")
		Class<? super T> componentClass = (Class<T>) component.getClass();
		
		while (componentClass != null) {
			RecordSupport<? super T> recordSupport = RecordSupports
					.getInstance().getRecordSupport(componentClass);
			if (recordSupport != null) {

				recordSupport.register(component, recorder);
				
				componentClass = null;
				break;
			} else {
				componentClass = componentClass.getSuperclass();
			}
		}
	}
	
	private <T> void tryRegisterChildren(T component) {
		@SuppressWarnings("unchecked")
		Class<? super T> componentClass = (Class<T>) component.getClass();
		Composite<? super T> composite = Composites.getInstance().getComposite(
				componentClass);
		if (composite != null) {
			for (Object subcomponent : composite.getComponents(component)) {
				registerComponentTree(subcomponent);
			}
		}
	}
	
}