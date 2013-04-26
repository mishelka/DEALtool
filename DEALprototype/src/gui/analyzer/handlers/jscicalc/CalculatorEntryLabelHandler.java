package gui.analyzer.handlers.jscicalc;

import gui.analyzer.handlers.CommandHandler;
import gui.analyzer.handlers.Composite;
import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.JLabelFinder;
import gui.analyzer.util.Util;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.constraint.Constraint;
import gui.model.domain.constraint.DataType;
import gui.model.domain.constraint.DataTypeConstraint;
import gui.model.domain.relation.RelationType;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;

import jscicalc.EntryLabel;

public class CalculatorEntryLabelHandler extends DomainIdentifiable<EntryLabel>
		implements CommandHandler<EntryLabel>, Composite<EntryLabel> {

	@Override
	public String getDomainIdentifier(EntryLabel component) {
		return "Entry";
	}

	@Override
	public String getDomainDescriptor(EntryLabel component) {
		return null;
	}

	@Override
	public String getDomainLabelDescriptor(EntryLabel component) {
		String label = null;
		JLabel l = JLabelFinder.findLabelFor(component);
		if (l != null) {
			label = l.getText();
		}
		label = Util.htmlToText(label);
		return label;
	}

	@Override
	public Icon getIcon(EntryLabel component) {
		return null;
	}

	@Override
	public RelationType getRelation(EntryLabel component) {
		return null;
	}
	
	@Override
	public List<Constraint> getConstraints(EntryLabel component) {
		List<Constraint> constraints = super.getConstraints(component);
		
		DataTypeConstraint c = new DataTypeConstraint(DataType.STRING);
		constraints.add(c);
		
		return constraints;
	}
	
	@Override
	public Object[] getComponents(EntryLabel container) {
		return new Object[]{};
	}
	
	@Override
	public ComponentInfoType getComponentInfoType(EntryLabel component) {
		return ComponentInfoType.TEXTUAL;
	}
}
