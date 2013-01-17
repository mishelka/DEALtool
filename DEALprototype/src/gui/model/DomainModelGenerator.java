package gui.model;

import gui.analyzer.handlers.Composite;
import gui.analyzer.handlers.Composites;
import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.handlers.DomainIdentifiables;
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

public class DomainModelGenerator {
	public DomainModel createDomainModel(Scene scene, String name) {
		DomainModel model = new DomainModel(name);

		Term rootTerm = new Term(model, name);
		rootTerm.setDescription("Application frames");
		rootTerm.setRelation(RelationType.MODEL);

		model.setRoot(rootTerm);

		System.out.println(">>>> rootTerm: " + rootTerm);
		
		createTermGroup(scene.getSceneContainer(), rootTerm);
		model.setScene(scene);

		discoverParentRelations(model);
		simplify(model);

		model.getRoot().setName(name);
		model.getRoot().setComponent(scene);

		return model;
	}

	@SuppressWarnings("unchecked")
	private <T> void createTermGroup(T component, Term parentTerm) {
		Class<? super T> componentClass = (Class<T>) component.getClass();
		Term thisTerm = null;
		DomainModel model = parentTerm.getDomainModel();
		boolean wasAdded = false;

		while (componentClass != null) {
			DomainIdentifiable<? super T> domainIdentifiable = DomainIdentifiables
					.getInstance().getDomainIdentifiable(componentClass);

			if (domainIdentifiable != null) {
				thisTerm = domainIdentifiable.getTerm(component, model);
				if (thisTerm != null) {
					parentTerm.addChild(thisTerm);
					wasAdded = true;
				}

				componentClass = null;
				break;
			} else {
				componentClass = componentClass.getSuperclass();
			}
		}

		// Composites
		// Children for this term
		componentClass = (Class<T>) component.getClass();
		Composite<? super T> composite = Composites.getInstance().getComposite(
				componentClass);

		// for components
		if (!wasAdded) {
			thisTerm = new Term(model);
			thisTerm.setComponent(component);
			thisTerm.setComponentClass(component.getClass());

			if (component instanceof Component) {
				Component c = ((Component) component);
				String str = c.getName();
				if (!Util.isEmpty(str)) {
					thisTerm.setName(str);
				}
				if (component instanceof JComponent) {
					str = ((JComponent) component).getToolTipText();
					if (!Util.isEmpty(str))
						thisTerm.setDescription(str);
				} else {
					thisTerm.setDescription(c.getClass().getSimpleName());
				}

				thisTerm.setRelation(RelationType.AND);
			}
			parentTerm.addChild(thisTerm);
		}

		// if parent is tabbedPane
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

		// for composites
		if (composite != null) {
			for (Object subcomponent : composite.getComponents(component)) {
				createTermGroup(subcomponent, thisTerm);
			}
		}
	}

	public void discoverParentRelations(DomainModel model) {
		Term child = model.getRoot().getFirstChild();
		model.setRoot(child);
		child.setParent(null);

		// ak v modeli nic nie je, nie je co odstranovat
		if (model.getRoot() == null || model.getRoot().getChildrenCount() == 0)
			return;

		discoverFutureRelations(model.getRoot());
	}

	private void discoverFutureRelations(Term thisTerm) {
		HashMap<RelationType, List<Term>> parentRelations = new HashMap<RelationType, List<Term>>();

		for (int i = 0; i < thisTerm.getChildrenCount(); i++) {
			Term fg = thisTerm.getChildAt(i);

			RelationType parentRelation = fg.getParentRelation();
			if (parentRelation != null) {
				if (!parentRelations.keySet().contains(parentRelation)) {
					parentRelations.put(parentRelation,
							new ArrayList<Term>());
				}
				parentRelations.get(parentRelation).add(fg);
			}
		}

		for (RelationType parentRelation : parentRelations.keySet()) {
			Term newFeature = new Term(thisTerm.getDomainModel());
			newFeature.setRelation(parentRelation);

			for (Term f : parentRelations.get(parentRelation)) {
				thisTerm.removeChild(f);
				newFeature.addChild(f);
				newFeature.setComponent(f.getComponent());
				newFeature.setComponentClass(f.getComponentClass());
				f.setParentRelation(null);
			}

			thisTerm.addChild(newFeature);
		}

		for (int i = 0; i < thisTerm.getChildrenCount(); i++) {
			discoverFutureRelations(thisTerm.getChildAt(i));
		}
	}

	public void simplify(DomainModel model) {
		shiftRoot(model);
		
		Term root = model.getRoot();

		// ak v modeli nic nie je, nie je co odstranovat
		if (root == null || root.getChildrenCount() == 0)
			return;

		simplify(root);
		
		if(root.canHide()) {
			shiftRoot(model);
		}
	}
	
	private void shiftRoot(DomainModel model) {
		Term child = model.getRoot().getFirstChild();
		model.setRoot(child);
		child.setParent(null);
	}

	private void simplify(Term thisFeature) {
		boolean go = false;
		for (int i = thisFeature.getChildrenCount() - 1; i >= 0; i--) {
			Term fg = thisFeature.getChildAt(i);
			go = !removeVoidContainers(thisFeature, fg);
			if (go)
				simplify(fg);
		}

		for (int i = thisFeature.getChildrenCount() - 1; i >= 0; i--) {
			Term fg = thisFeature.getChildAt(i);

			go = go && !removeMultipleNesting(thisFeature, fg);
			if (go)
				simplify(fg);
		}
	}

	// ak child nema ziadne deti a je nanic, tak sa vyhodi
	private boolean removeVoidContainers(Term thisFeature, Term child) {
		if (child.isLeaf() && child.canHide()) {
			thisFeature.removeChild(child);
			return true;
		} else
			return false;
	}

	// ak je thisFeature nanic, tak sa vyhodi thisFeature !
	private boolean removeMultipleNesting(Term thisFeature, Term child) {
		if (thisFeature.hasChildren() && thisFeature.getChildrenCount() == 1) {
			if (thisFeature.canHide()) {
				Term parent = thisFeature.getParent();
				if (parent != null) {
					parent.replaceChild(thisFeature, child);
					return true;
				}
			}
		}
		return false;
	}

	// private void removeVoidContainers(Feature thisFeature) {
	// for (int i = thisFeature.getChildrenCount() - 1; i >= 0; i--) {
	// Feature fg = thisFeature.getChildAt(i);
	//
	// if (fg.isLeaf() && fg.canHide()) {
	// //&& ContainerComposite.CONTAINER.equals(fg.getName())) {
	// thisFeature.removeChild(fg);
	// } else
	// removeVoidContainers(fg);
	// }
	// }
	//
	// private void removeMultipleNesting(Feature thisFeature) {
	// for (int i = thisFeature.getChildrenCount() - 1; i >= 0; i--) {
	// Feature fg = thisFeature.getChildAt(i);
	// if (thisFeature.hasChildren()
	// && thisFeature.getChildrenCount() == 1) {
	// if (thisFeature.canHide()) {//
	// ContainerComposite.CONTAINER.equals(thisFeature.getName()))
	// // {
	// Feature parent = thisFeature.getParent();
	// if (parent != null)
	// parent.replaceChild(thisFeature, fg);
	// }
	// }
	// removeMultipleNesting(fg);
	// }
	// }
}