package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.model.domain.ComponentInfoType;

import java.awt.Frame;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class FrameComposite extends DomainIdentifiable<Frame> {
	public static final String FRAME = "Application frame";

	@Override
	public String getDomainIdentifier(Frame component) {
		return component.getTitle();
	}

	@Override
	public String getDomainDescriptor(Frame component) {
		return FRAME;
	}

	@Override
	public Icon getIcon(Frame component) {
		Image i = component.getIconImage();

		if (i != null)
			return new ImageIcon(i);
		
		return null;
	}
	
	@Override
	public ComponentInfoType getComponentInfoType(Frame component) {
		return ComponentInfoType.CONTAINERS;
	}
}
