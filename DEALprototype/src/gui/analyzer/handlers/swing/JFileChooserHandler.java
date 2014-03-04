package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.Composite;
import gui.analyzer.handlers.DomainIdentifiable;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;

import javax.swing.Icon;
import javax.swing.JFileChooser;

public class JFileChooserHandler extends DomainIdentifiable<JFileChooser> 
					implements Composite<JFileChooser> {
	@Override
	public Term createTerm(JFileChooser component, DomainModel domainModel) {
		return super.createTerm(component, domainModel);
	}

	@Override
	public String getDomainIdentifier(JFileChooser component) {
		return component.getDialogTitle();
	}

	@Override
	public String getDomainDescriptor(JFileChooser component) {
		return null;
	}

	@Override
	public Icon getIcon(JFileChooser component) {
		return null;
	}

	@Override
	public ComponentInfoType getComponentInfoType(JFileChooser component) {
		return ComponentInfoType.CONTAINERS;
	}

	@Override
	public Object[] getComponents(JFileChooser container) {
		return null;
	}

}
