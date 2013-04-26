package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;
import javax.swing.JPopupMenu.Separator;

public class JPopupMenuSeparatorHandler extends DomainIdentifiable<Separator> {

	@Override
	public String getDomainIdentifier(Separator component) {
		return null;
	}

	@Override
	public String getDomainDescriptor(Separator component) {
		return null;
	}

	@Override
	public Icon getIcon(Separator component) {
		return null;
	}

	@Override
	public ComponentInfoType getComponentInfoType(Separator component) {
		return ComponentInfoType.SEPARATORS;
	}
}
