package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.model.domain.constraint.Constraint;
import gui.model.domain.constraint.Enumeration;
import gui.model.domain.relation.RelationType;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComboBox;

public class JComboBoxHandler extends DomainIdentifiable<JComboBox<?>> {

	@Override
	public String getDomainIdentifier(JComboBox<?> component) {
		return null;
	}

	@Override
	public String getDomainDescriptor(JComboBox<?> component) {
		String ttt = component.getToolTipText();
		if (ttt == null || ttt.isEmpty())
			return null;
		return ttt;
	}

	@Override
	public Icon getIcon(JComboBox<?> component) {
		return null;
	}

	@Override
	public RelationType getRelation(JComboBox<?> component) {
		return RelationType.MUTUALLY_EXCLUSIVE;
	}

	@Override
	public Term createTerm(JComboBox<?> component, DomainModel domainModel) {
		Term thisTerm = super.createTerm(component, domainModel);

		for (int i = 0; i < component.getItemCount(); i++) {
			Object o = component.getItemAt(i);
			Term t = new Term(domainModel);
			t.setName(o.toString());
			t.setComponent(o);
			t.setComponentClass(o.getClass());
			t.setRelation(RelationType.MUTUALLY_EXCLUSIVE);
			thisTerm.addChild(t);
		}

		return thisTerm;
	}
	
	@Override
	public List<Constraint> getConstraints(JComboBox<?> component) {
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

	@Override
	public ComponentInfoType getComponentInfoType(JComboBox<?> component) {
		return ComponentInfoType.LOGICALLY_GROUPING;
	}
}
