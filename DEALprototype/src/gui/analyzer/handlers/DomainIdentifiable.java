package gui.analyzer.handlers;

import gui.analyzer.util.PathFinder;
import gui.analyzer.util.Util;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.model.domain.constraint.Constraint;
import gui.model.domain.relation.RelationType;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;

public abstract class DomainIdentifiable<T> {
	public abstract String getDomainIdentifier(T component);

	public abstract String getDomainDescriptor(T component);

	public abstract Icon getIcon(T component);

	public Term getTerm(T component, DomainModel featureModel) {
		Term t = new Term(featureModel);

		String str = this.getDomainIdentifier(component);
		if (!Util.isEmpty(str))
			t.setName(str);

		str = this.getDomainDescriptor(component);
		if (!Util.isEmpty(str))
			t.setDescription(str);

		RelationType ft = getRelation(component);
		t.setRelation(ft == null ? RelationType.AND : ft);

		t.setComponentClass(component.getClass());
		t.setComponent(component);

		t.setIcon(this.getIcon(component));
		
		t.setConstraints(this.getConstraints(component));

		RelationType parentRelation = getParentRelation(component);
		if (parentRelation != null)
			t.setParentRelation(parentRelation);

		return t;
	}

	/**
	 * Relation between this component's children.
	 * 
	 * @param component
	 *            this component
	 * @return relation between this component's children.
	 */
	public RelationType getRelation(T component) {
		return RelationType.AND;
	}

	/**
	 * Relation between this component and other components of the same type.
	 * 
	 * @param component
	 *            this component
	 * @return relation between this component and other components of the same
	 *         type.
	 */
	public RelationType getParentRelation(T component) {
		return null;
	}

	public String getDomainLabelDescriptor(T component) {
		if (component instanceof Component) {
			JLabel l = PathFinder.getInstance().findLabelFor(
					(Component) component);
			if (l != null)
				return l.getText();
		}
		return null;
	}
	
	public List<Constraint> getConstraints(T component) {
		return new ArrayList<Constraint>();
	}
}