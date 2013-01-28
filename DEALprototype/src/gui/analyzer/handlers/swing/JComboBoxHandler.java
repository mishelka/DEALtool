package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.Util;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.model.domain.constraint.Constraint;
import gui.model.domain.constraint.DataTypeConstraint;
import gui.model.domain.constraint.Enumeration;
import gui.model.domain.relation.RelationType;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComboBox;

public class JComboBoxHandler extends DomainIdentifiable<JComboBox> {

	private static final String COMBO_BOX = "Combo Box";

	@Override
	public String getDomainIdentifier(JComboBox component) {
		String l = getDomainLabelDescriptor(component);
		if (Util.isEmpty(l))
			l = COMBO_BOX;
		return l;
	}

	@Override
	public String getDomainDescriptor(JComboBox component) {
		String ttt = component.getToolTipText();
		if (ttt == null || ttt.isEmpty())
			return null;
		return ttt;
	}

	@Override
	public Icon getIcon(JComboBox component) {
		return null;
	}

	@Override
	public RelationType getRelation(JComboBox component) {
		return RelationType.MUTUALLY_EXCLUSIVE;
	}

	@Override
	public Term getTerm(JComboBox component, DomainModel featureModel) {
		Term thisFeature = super.getTerm(component, featureModel);

		for (int i = 0; i < component.getItemCount(); i++) {
			Object o = component.getItemAt(i);
			Term f = new Term(featureModel);
			f.setName(o.toString());
			f.setComponent(o);
			f.setComponentClass(o.getClass());
			f.setRelation(RelationType.MUTUALLY_EXCLUSIVE);
			thisFeature.addChild(f);
		}

		return thisFeature;
	}
	
	@Override
	public List<Constraint> getConstraints(JComboBox component) {
		List<Constraint> constraints = super.getConstraints(component);
		
		List<String> values = new ArrayList<String>();
		
		for (int i = 0; i < component.getItemCount(); i++) {
			String item = component.getItemAt(i).toString();
			values.add(item);
		}
		
		Enumeration enumeration = new Enumeration();
		enumeration.setValues(values.toArray(new String[]{}));
		
		constraints.add(enumeration);
		
		return constraints;
	}

}
