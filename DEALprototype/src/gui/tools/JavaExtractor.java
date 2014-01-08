package gui.tools;

import gui.analyzer.handlers.Composite;
import gui.analyzer.handlers.Composites;
import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.handlers.DomainIdentifiables;
import gui.analyzer.util.Util;
import gui.model.domain.Term;
import gui.model.domain.relation.RelationType;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

/**
 * Extracts a domain model from the given scene.
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class JavaExtractor extends AbstractDealExtractor {
	protected <E> void extractSubtree(E component, Term parentTerm) {
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
		if (thisTerm.extractChildren()) {
			extractSubtreesFromChildren(component, thisTerm);
		}
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
}
