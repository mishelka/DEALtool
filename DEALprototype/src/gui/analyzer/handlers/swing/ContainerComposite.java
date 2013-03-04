package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.Composite;
import gui.analyzer.handlers.DomainIdentifiable;
import gui.model.domain.ComponentInfoType;

import java.awt.Container;

import javax.swing.Icon;

/**
 * Ovladac pre kontajnery typu Container.
 */
public class ContainerComposite extends DomainIdentifiable<Container> implements
		Composite<Container> {

	public static final String CONTAINER = "Container";

	@Override
	public Object[] getComponents(Container container) {
		return container.getComponents();
	}

	@Override
	public String getDomainIdentifier(Container component) {
		return CONTAINER + " [" + component.getClass().getSimpleName() + "]";
	}

	@Override
	public String getDomainDescriptor(Container component) {
		return component.getClass().getName();
	}

	@Override
	public Icon getIcon(Container component) {
		return null;
	}
	
	@Override
	public ComponentInfoType getComponentInfoType(Container component) {
		return ComponentInfoType.GRAPHICALLY_GROUPING;
	}
}
