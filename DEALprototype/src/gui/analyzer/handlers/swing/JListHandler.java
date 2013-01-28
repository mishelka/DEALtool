package gui.analyzer.handlers.swing;

import java.util.ArrayList;
import java.util.List;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.model.domain.constraint.Constraint;
import gui.model.domain.constraint.Enumeration;

import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.ListModel;


public class JListHandler extends DomainIdentifiable<JList> {

	@Override
	public String getDomainIdentifier(JList component) {
		return null;
	}

	@Override
	public String getDomainDescriptor(JList component) {
		String ttt = component.getToolTipText();
		if (ttt == null || ttt.isEmpty())
			return null;
		return ttt;
	}

	@Override
	public Icon getIcon(JList component) {
		return null;
	}
	
	@Override
	public List<Constraint> getConstraints(JList component) {
		List<Constraint> constraints = super.getConstraints(component);
		if(constraints == null) constraints = new ArrayList<Constraint>();
		
		List<String> values = new ArrayList<String>();
		
		ListModel model = component.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			String item = model.getElementAt(i).toString();
			values.add(item);
		}
		
		Enumeration enumeration = new Enumeration();
		enumeration.setValues(values.toArray(new String[]{}));
		
		constraints.add(enumeration);
		
		return constraints;
	}
}
