package gui.analyzer.handlers.swing;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;

import gui.analyzer.handlers.DomainIdentifiable;

public class JTextComponentHandler extends DomainIdentifiable<JTextComponent> {

	@Override
	public String getDomainIdentifier(JTextComponent component) {
		return "<TEXT>";
	}

	@Override
	public String getDomainDescriptor(JTextComponent component) {
		return component.getToolTipText();
	}

	@Override
	public Icon getIcon(JTextComponent component) {
		// TODO Auto-generated method stub
		return null;
	}
}
