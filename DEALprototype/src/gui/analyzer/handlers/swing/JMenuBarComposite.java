package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.Composite;
import gui.analyzer.handlers.DomainIdentifiable;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.relation.RelationType;

import javax.swing.Icon;
import javax.swing.JMenuBar;

public class JMenuBarComposite extends DomainIdentifiable<JMenuBar> implements
		Composite<JMenuBar> {

	private static final String MENU_BAR = "Menu";

	@Override
	public Object[] getComponents(JMenuBar container) {
		return container.getComponents();
	}

	@Override
	public String getDomainIdentifier(JMenuBar component) {
		return MENU_BAR;
	}

	@Override
	public String getDomainDescriptor(JMenuBar component) {
		return MENU_BAR;
	}

	@Override
	public Icon getIcon(JMenuBar component) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public RelationType getRelation(JMenuBar component) {
		return RelationType.MUTUALLY_EXCLUSIVE;
	}
	
	@Override
	public ComponentInfoType getComponentInfoType(JMenuBar component) {
		return ComponentInfoType.GRAPHICALLY_GROUPING;
	}
}
