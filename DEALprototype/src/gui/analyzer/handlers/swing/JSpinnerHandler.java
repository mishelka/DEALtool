package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.constraint.Constraint;
import gui.model.domain.constraint.DataType;
import gui.model.domain.constraint.DataTypeConstraint;
import gui.model.domain.constraint.Enumeration;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class JSpinnerHandler extends DomainIdentifiable<JSpinner> {

	@Override
	public String getDomainIdentifier(JSpinner component) {
		return null;
	}

	@Override
	public String getDomainDescriptor(JSpinner component) {
		String ttt = component.getToolTipText();
		if (ttt == null || ttt.isEmpty())
			return null;
		return ttt;
	}

	@Override
	public Icon getIcon(JSpinner component) {
		return null;
	}

	@Override
	public List<Constraint> getConstraints(JSpinner component) {
		List<Constraint> constraints = super.getConstraints(component);

		SpinnerModel sModel = component.getModel();

		if (sModel instanceof SpinnerNumberModel) {
			constraints.add(new DataTypeConstraint(DataType.NUMERIC));
		} else if (sModel instanceof SpinnerDateModel) {
			constraints.add(new DataTypeConstraint(DataType.DATE));
		} else if (sModel instanceof SpinnerListModel) {
			Enumeration enumerationConstraint = new Enumeration();
			SpinnerListModel sListModel = (SpinnerListModel) sModel;
			List<?> spinnerValues = sListModel.getList();
			String[] constraintValues = new String[spinnerValues.size()];

			for (int i = 0; i < constraintValues.length; i++) {
				constraintValues[i] = spinnerValues.get(i).toString();
			}

			enumerationConstraint.setValues(constraintValues);

			constraints.add(enumerationConstraint);
		}

		return constraints;
	}
	
	@Override
	public ComponentInfoType getComponentInfoType(JSpinner component) {
		return ComponentInfoType.TEXTUAL;
	}
	
	@Override
	public boolean extractChildren() {
		return false;
	}
}
