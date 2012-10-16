package gui.analyzer.handlers;

import gui.analyzer.util.PathFinder;
import gui.analyzer.util.Util;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.model.domain.relation.RelationType;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;

public abstract class DomainIdentifiable<T> {
	public abstract String getDomainIdentifier(T component);

	public abstract String getDomainDescriptor(T component);

	public abstract Icon getIcon(T component);

	public Term getTerm(T component, DomainModel featureModel) {
		Term f = new Term(featureModel);

		String str = this.getDomainIdentifier(component);
		if (!Util.isEmpty(str))
			f.setName(str);

		str = this.getDomainDescriptor(component);
		if (!Util.isEmpty(str))
			f.setDescription(str);

		RelationType ft = getRelation(component);
		f.setRelation(ft == null ? RelationType.AND : ft);

		f.setComponentClass(component.getClass());
		f.setComponent(component);

		f.setIcon(this.getIcon(component));

		RelationType parentRelation = getParentRelation(component);
		if (parentRelation != null)
			f.setParentRelation(parentRelation);

		return f;
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
}