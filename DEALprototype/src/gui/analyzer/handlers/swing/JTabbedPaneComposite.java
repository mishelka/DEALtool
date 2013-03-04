package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.Util;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.relation.RelationType;

import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class JTabbedPaneComposite extends DomainIdentifiable<JTabbedPane> {

	@Override
	public String getDomainIdentifier(JTabbedPane component) {
		String id = component.getName();
		if (Util.isEmpty(id))
			id = "Tabs";
		return id;
	}

	@Override
	public String getDomainDescriptor(JTabbedPane component) {
		return component.getToolTipText();
	}

	@Override
	public Icon getIcon(JTabbedPane component) {
		return null;
	}

	@Override
	public RelationType getRelation(JTabbedPane component) {
		return RelationType.MUTUALLY_EXCLUSIVE;
	}
	
	@Override
	public ComponentInfoType getComponentInfoType(JTabbedPane component) {
		return ComponentInfoType.LOGICALLY_GROUPING;
	}
}
