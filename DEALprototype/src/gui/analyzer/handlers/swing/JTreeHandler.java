package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.Logger;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;

public class JTreeHandler extends DomainIdentifiable<JTree> {

	@Override
	public String getDomainIdentifier(JTree component) {
		return null;
	}

	@Override
	public String getDomainDescriptor(JTree component) {
		return component.getToolTipText();
	}

	@Override
	public Icon getIcon(JTree component) {
		return null;
	}

	@Override
	public ComponentInfoType getComponentInfoType(JTree component) {
		return ComponentInfoType.CONTAINERS;
	}

	@Override
	public Term createTerm(JTree component, DomainModel domainModel) {
		Term thisTerm = super.createTerm(component, domainModel);

		TreeModel tm = component.getModel();
		Object o = tm.getRoot();
		
		if(o != null)
			Logger.log(o.getClass().getSimpleName());
		
		return thisTerm;
	}
	
	@Override
	public boolean extractChildren() {
		return false;
	}
}
