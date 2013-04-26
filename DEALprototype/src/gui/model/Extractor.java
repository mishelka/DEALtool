package gui.model;

import gui.analyzer.handlers.Composite;
import gui.analyzer.handlers.Composites;
import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.handlers.DomainIdentifiables;
import gui.analyzer.util.Util;
import gui.model.application.Scene;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.model.domain.relation.RelationType;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

public class Extractor {
	private DomainModel domainModel;
	private Scene scene;
	private Term rootTerm;
	private String sceneName;

	public Extractor(Scene scene, DomainModel domainModel)
			throws ExtractionException {
		if (domainModel != null) {
			this.domainModel = domainModel;
			this.rootTerm = domainModel.getRoot();
		} else
			throw new ExtractionException("domainModel must not be null");
		if (scene != null) {
			this.scene = scene;
			this.sceneName = scene.getName();
		} else
			throw new ExtractionException("scene must not be null");
	}

	/**
	 * Extracts information from the given scene into the domainModel and returns the extracted domain model.
	 * @return the extracted domain model.
	 */
	public DomainModel eXTRACT() {
		eXTRACT(scene, sceneName, rootTerm);
		return domainModel;
	}

	/************************* Methods for Domain Model extraction ******************************/

	/**
	 * First phase of DEAL - extraction of the term graph. The extraction is
	 * performed recursively by traversing the component tree. Additional
	 * sub-step of the extraction phase is is extraction of additional relations
	 * from parent components.
	 * 
	 * @param scene
	 *            A scene to create the term graph from.
	 * @param name
	 *            The name of the domain model.
	 * @param rootTerm
	 *            The root term of the domain model.
	 */
	private void eXTRACT(Scene<?> scene, String name, Term rootTerm) {
		// extract subtree from the root scene
		extractSubtree(scene.getSceneContainer(), rootTerm);

		// additional derivation of relations from parents
		findParentRelationsInModel();

		// find separators
		findSeparatorsInModel();

		// set the model root name and type and references to the scene
		domainModel.getRoot().setName(name);
		domainModel.getRoot().setRelation(RelationType.MODEL);
		domainModel.getRoot().setComponent(scene);
		domainModel.setScene(scene);
	}

	private void findSeparatorsInModel() {
		if (domainModel.getRoot() == null
				|| domainModel.getRoot().getChildrenCount() == 0) {
			return;
		}
		findSeparators(domainModel.getRoot());
	}

	private void findSeparators(Term thisTerm) {
		List<List<Term>> childrenToSeparate = new ArrayList<List<Term>>();
		childrenToSeparate.add(new ArrayList<Term>());

		Iterator<Term> i = thisTerm.getChildren().iterator();

		// find separators - if there are any, then the childrenToSeparate list
		// will contain more than one list of terms.
		// the separators will be removed because they are not representing
		// terminology
		while (i.hasNext()) {
			Term child = i.next();
			if (child.getComponentInfoType() == ComponentInfoType.SEPARATORS
					&& childrenToSeparate.size() > 0) {
				childrenToSeparate.add(new ArrayList<Term>());
				i.remove(); // remove the separator
			} else {
				// add term into the last term list in the childrenToSeparate
				// list
				childrenToSeparate.get(childrenToSeparate.size() - 1)
						.add(child);
			}
		}

		// transfer the children into separated groups (if there are any)
		if (childrenToSeparate.size() > 1) {
			for (List<Term> subList : childrenToSeparate) {
				thisTerm.removeAll(subList);
				Term newSubItem = new Term(domainModel);

				newSubItem.addAll(subList);

				thisTerm.addChild(newSubItem);
			}
		}

		// do the same for all children
		i = thisTerm.getChildren().iterator();
		while (i.hasNext()) {
			Term child = i.next();
			findSeparators(child);
		}
	}

	/**
	 * Create a subtree in the domain model. The subtree is created from
	 * information extracted from the component tree - recusively. Each
	 * recursive cycle has four steps: - traversing the superclasses to find the
	 * DomainIdentifiable handler. - if no handler is found in the first step,
	 * then extract the basic information available in the current component. -
	 * try to extract any information from the custom types of components. The
	 * tryCustom(..) method is available to add new extraction rules. - the
	 * fourth step is an attempt to use the extractSubtree() method recursively
	 * for each subcomponent of the current component - but only if the current
	 * component is a Composite (i.e. a container).
	 * 
	 * @param component
	 *            The current component which the new term subtree should be
	 *            extracted from.
	 * @param parentTerm
	 *            a future parent of the newly created subtree.
	 */
	private <T> void extractSubtree(T component, Term parentTerm) {
		Term thisTerm = null;

		// first step: traversing the superclasses to find the
		// DomainIdentifiable handler
		thisTerm = trySuperclass(component);
		if (thisTerm != null) {
			parentTerm.addChild(thisTerm);
		}

		// second step: try to extract information from the current component
		if (thisTerm == null) {
			thisTerm = tryComponent(component);
			if (thisTerm != null) {
				parentTerm.addChild(thisTerm);
			}
		}

		// third step: try custom extraction rules
		tryCustom(component, parentTerm, thisTerm);

		// fourth step: if the current component is a composite, extract
		// subtrees from the its child components by recursively calling of the
		// extractSubtree(..) method.
		extractSubtreesFromChildren(component, thisTerm);
	}

	/**
	 * The first step of the extraction algorithm: traversing the superclasses
	 * to find the DomainIdentifiable handler. If there is a DomainIdentifiable
	 * handler for the provided component's class, then the term is extracted
	 * using this handler. If there is no DomainIdentifiable handler for the
	 * provided component's class, then the algorithm tries to retrieve a
	 * handler for each superclass of the provided component's class in
	 * iterations. If there is no DomainIdentifiable handler for any of the
	 * provided component's superclasses, then this method returns null.
	 * 
	 * @param component
	 *            The component for which we try to find a DomainIdentifiable
	 *            handler. This handler is found only if the component's class
	 *            (or any of its superclasses) is supported by DEAL. @see
	 *            gui.analyzer.handlers
	 * @param domainModel
	 *            The existing domain model is needed only as a reference when
	 *            creating a new Term.
	 * @return a new Term with extracted information using the
	 *         DomainIdentifiable handler, or null if there is no such handler
	 *         for the provided component's class or for any of its
	 *         superclasses.
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
				t = domainIdentifiable.createTerm(component, domainModel);
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

	/**
	 * The second step of the extraction algorithm: if no handler is found in
	 * the first step, then extract the basic information available in the
	 * current component. Try to extract any available information from the
	 * current component. The possibilities to extract: - component name (as the
	 * term name) - toolTipText (as the term description) - only if the
	 * component is a JComponent - component class (as the term description) -
	 * if the component is not a JComponent
	 * 
	 * @param component
	 *            The component to extract the available information from.
	 * @return the extracted Term with all information possible to extract from
	 *         the component.
	 */
	private <T> Term tryComponent(T component) {
		// create a new term with references to the component and its class
		Term thisTerm = new Term(domainModel);
		thisTerm.setComponent(component);
		thisTerm.setComponentClass(component.getClass());

		if (component instanceof Component) {
			// if there's any, extract the component name
			Component c = ((Component) component);
			String str = c.getName();
			if (!Util.isEmpty(str))
				thisTerm.setName(str);

			// if the component is a JComponent, then we can extract toolTipText
			// as a description
			if (component instanceof JComponent) {
				str = ((JComponent) component).getToolTipText();
				if (!Util.isEmpty(str))
					thisTerm.setDescription(str);
			}
			// if it's not a JComponent, then extract the component class name
			// as a description
			else {
				thisTerm.setDescription(c.getClass().getSimpleName());
			}

			// set the default relation type
			thisTerm.setRelation(RelationType.AND);
		}

		return thisTerm;
	}

	/**
	 * The third step of the extraction algorithm: try to extract any
	 * information from the custom types of components. The tryCustom(..) method
	 * is available to add new extraction rules. In this method, new custom
	 * rules for extracting domain information can be added. For example, if
	 * there are domain information stored in the parent component, such as
	 * JTabbedPane, it is possible to add new extraction procedures into this
	 * method. Each new extracted information should be stored into thisTerm.
	 * The parentTerm is provided to be able to get domainModel or any relevant
	 * information from the parent.
	 * 
	 * @param component
	 *            the component, from which the term is extracted.
	 * @param parentTerm
	 *            the parent term of thisTerm.
	 * @param thisTerm
	 *            the actual term, to which the extracted domain information can
	 *            be stored.
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
	 * The fourth step of the extraction algorithm: an attempt to use the
	 * extractSubtree() method recursively for each subcomponent of the current
	 * component - but only if the current component is a Composite (i.e. a
	 * container).
	 * 
	 * @param component
	 *            The current component which the new term subtree should be
	 *            extracted from.
	 * @param thisTerm
	 *            The actual term - the terms extracted from the current
	 *            component are added into this term as children.
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
	 * Additional sub-step of the extraction phase - extraction of additional
	 * relations from parent components. Finds parent relations in the whole
	 * domain model. A parent relation is a relation, which is generated by a
	 * parent term for its children. For example, because of a JTabbedPane, the
	 * tabs are mutually exclusive. This method works recursively for the whole
	 * subtree of thisTree. A HashMap is created for each type of parent
	 * relation. This is for a case, where the parent generates a relation only
	 * for a subset of its children - in such a case a new subterm is generated
	 * for the group with the new relation and the other terms will remain in
	 * thisTerm children list.
	 * 
	 * @param model
	 *            The domain to search the parent relations in.
	 */
	private void findParentRelationsInModel() {
		// if there is nothing in the model, do nothing
		if (domainModel.getRoot() == null
				|| domainModel.getRoot().getChildrenCount() == 0)
			return;

		// recursively find parent relations in each subtree, starting with root
		findParentRelations(domainModel.getRoot());
	}

	/**
	 * This method is called recursively and it is started by the
	 * findParentRelationsInModel(..) method. Finds parent relations in a
	 * subtree of the domain model. A parent relations is a relation, which is
	 * generated by a parent term for its children. For example, because of a
	 * JTabbedPane, the tabs are mutually exclusive. This method works
	 * recursively for the whole subtree of thisTree. A HashMap is created for
	 * each type of parent relation. This is for a case, where the parent
	 * generates a relation only for a subset of its children - in such a case a
	 * new subterm is generated for the group with the new relation and the
	 * other terms will remain in thisTerm children list.
	 * 
	 * @param thisTerm
	 *            The term, which generates the relations for its children.
	 */
	private void findParentRelations(Term thisTerm) {
		HashMap<RelationType, List<Term>> parentRelations = new HashMap<RelationType, List<Term>>();

		// put all children of thisTerm into a list under the parentRelation key
		// in the parentRelations hash map.
		for (int i = 0; i < thisTerm.getChildrenCount(); i++) {
			Term t = thisTerm.getChildAt(i);

			// get parentRelation from term - this is stored during the Term
			// creation
			// @see gui.analyzer.handlers.DomainIdentifiable.createTerm(..)
			RelationType parentRelation = t.getParentRelation();
			if (parentRelation != null) {
				// for each relation type there is a list of components, to
				// which this relation should be applied - add the child into
				// this list.
				if (!parentRelations.keySet().contains(parentRelation)) {
					parentRelations.put(parentRelation, new ArrayList<Term>());
				}
				parentRelations.get(parentRelation).add(t);
			}
		}

		// for each relation type in the key set of parentRelations
		// create a new subterm of thisTerm and add all its children with this
		// type of parentRelation into this subterm.
		for (RelationType parentRelation : parentRelations.keySet()) {
			// create a new term
			Term newTerm = new Term(thisTerm.getDomainModel());
			// set its relation to parentRelation
			newTerm.setRelation(parentRelation);

			// for each term in the list under the parentRelation key
			// shift every term from thisTerm to the newTerm
			for (Term f : parentRelations.get(parentRelation)) {
				thisTerm.removeChild(f);
				newTerm.addChild(f);
				newTerm.setComponent(f.getComponent());
				newTerm.setComponentClass(f.getComponentClass());
				f.setParentRelation(null);
			}

			thisTerm.addChild(newTerm);
		}

		// do this recursively
		for (int i = 0; i < thisTerm.getChildrenCount(); i++) {
			findParentRelations(thisTerm.getChildAt(i));
		}
	}

	public class ExtractionException extends Exception {
		public ExtractionException(String msg) {
			super(msg);
		}
	}
}
