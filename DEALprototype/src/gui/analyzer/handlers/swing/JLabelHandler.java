package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.Util;

import javax.swing.Icon;
import javax.swing.JLabel;

public class JLabelHandler extends DomainIdentifiable<JLabel> {

	@Override
	public String getDomainIdentifier(JLabel component) {
		String id = component.getText();
		if (!Util.isEmpty(id))
			return id;
		return null;
	}

	@Override
	public String getDomainDescriptor(JLabel component) {
		String desc = component.getToolTipText();
		if (desc == null)
			desc = "";
		return desc;
	}

	@Override
	public Icon getIcon(JLabel component) {
		return component.getIcon();
	}
}
