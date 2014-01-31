package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.model.domain.ComponentInfoType;

import javax.swing.Icon;
import javax.swing.JToolBar;

public class JToolBarHandler extends DomainIdentifiable<JToolBar>{

	@Override
	public String getDomainIdentifier(JToolBar component) {
		return "Toolbar";
	}

	@Override
	public String getDomainDescriptor(JToolBar component) {
		return component.getToolTipText();
	}

	@Override
	public Icon getIcon(JToolBar component) {
		return null;
	}

	@Override
	public ComponentInfoType getComponentInfoType(JToolBar component) {
		return ComponentInfoType.CONTAINERS;
	}

}
