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
import gui.model.domain.constraint.Length;
import gui.model.domain.relation.RelationType;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;

import jscicalc.DisplayLabel;
import jscicalc.EntryLabel;

public class CalculatorDisplayLabelHandler extends
		DomainIdentifiable<DisplayLabel> implements CommandHandler<DisplayLabel>, Composite<DisplayLabel> {

	@Override
	public String getDomainIdentifier(DisplayLabel component) {
		return "Display";
	}

	@Override
	public String getDomainDescriptor(DisplayLabel component) {
		return null;
	}

	@Override
	public String getDomainLabelDescriptor(DisplayLabel component) {
		String label = null;
		JLabel l = JLabelFinder.findLabelFor(component);
		if (l != null) {
			label = l.getText();
		}
		label = Util.htmlToText(label);
		return label;
	}

	@Override
	public Icon getIcon(DisplayLabel component) {
		return null;
	}

	@Override
	public RelationType getRelation(DisplayLabel component) {
		return null;
	}
	
	@Override
	public List<Constraint> getConstraints(DisplayLabel component) {
		List<Constraint> constraints = super.getConstraints(component);
		
		DataTypeConstraint c = new DataTypeConstraint(DataType.STRING);
		constraints.add(c);
		
		Length l = new Length(0, component.getDigits());
		constraints.add(l);
		
		return constraints;
	}
	
	@Override
	public Object[] getComponents(DisplayLabel container) {
		return new Object[]{};
	}
	
	@Override
	public ComponentInfoType getComponentInfoType(DisplayLabel component) {
		return ComponentInfoType.TEXTUAL;
	}
}
