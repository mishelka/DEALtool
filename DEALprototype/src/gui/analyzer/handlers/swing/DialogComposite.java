package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.model.domain.ComponentInfoType;

import java.awt.Dialog;
import java.awt.Image;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class DialogComposite extends DomainIdentifiable<Dialog> {

	@Override
	public String getDomainIdentifier(Dialog composite) {
		return composite.getTitle();
	}

	@Override
	public String getDomainDescriptor(Dialog component) {
		return null;
	}

	@Override
	public Icon getIcon(Dialog component) {
		List<Image> smth = component.getIconImages();
		if (smth == null || smth.size() <= 0)
			return null;

		for (int i = 0; i < smth.size(); i++) {
			Image icon = smth.get(i);

			if (icon != null)
				return new ImageIcon(icon);
		}

		return null;
	}
	
	@Override
	public ComponentInfoType getComponentInfoType(Dialog component) {
		return ComponentInfoType.CONTAINERS;
	}
}
