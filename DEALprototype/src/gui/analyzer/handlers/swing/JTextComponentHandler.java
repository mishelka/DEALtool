package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.constraint.Constraint;
import gui.model.domain.constraint.DataType;
import gui.model.domain.constraint.DataTypeConstraint;

import java.util.List;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;

public class JTextComponentHandler extends DomainIdentifiable<JTextComponent> {

	@Override
	public String getDomainIdentifier(JTextComponent component) {
		return null;
	}

	@Override
	public String getDomainDescriptor(JTextComponent component) {
		return component.getToolTipText();
	}

	@Override
	public Icon getIcon(JTextComponent component) {
		return null;
	}
	
	@Override
	public List<Constraint> getConstraints(JTextComponent component) {
		List<Constraint> constraints = super.getConstraints(component);
		
		DataTypeConstraint dtc;
		
		if(constraints != null) {
			for(Constraint c : constraints) {
				if(c instanceof DataTypeConstraint) {
					dtc = (DataTypeConstraint) c;
					if(dtc.getType().equals(DataType.STRING)) {
						return constraints;
					} else {
						dtc.setType(DataType.STRING);
					}
				}
			}
		}
		
		constraints.add(new DataTypeConstraint(DataType.STRING));
		
		return constraints;
	}

	@Override
	public ComponentInfoType getComponentInfoType(JTextComponent component) {
		return ComponentInfoType.TEXTUAL;
	}
}
