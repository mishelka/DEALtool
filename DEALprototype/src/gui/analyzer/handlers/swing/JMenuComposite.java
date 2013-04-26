package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.CommandHandler;
import gui.analyzer.handlers.Composite;
import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.Util;
import gui.model.domain.ComponentInfoType;

import java.util.ArrayList;

import javax.swing.*;

public class JMenuComposite extends DomainIdentifiable<JMenu> implements
		CommandHandler<JMenu>, Composite<JMenu> {

	@Override
	public Object[] getComponents(JMenu container) {
		return container.getMenuComponents();
	}

	@Override
	public String getDomainIdentifier(JMenu component) {
		ArrayList<String> ids = new ArrayList<String>();
		ids.add(component.getText());
		ids.add(component.getActionCommand());

		for (String str : ids) {
			if (!Util.isEmpty(str)) {
				return str;
			}
		}

		return null;
	}

	@Override
	public String getDomainDescriptor(JMenu component) {
		String desc = component.getToolTipText();
		if (Util.isEmpty(desc))
			return desc;
		return null;
	}

	@Override
	public Icon getIcon(JMenu component) {
		return component.getIcon();
	}
	
	@Override
	public ComponentInfoType getComponentInfoType(JMenu component) {
		if(component instanceof JMenuItem) {
			return ComponentInfoType.FUNCTIONAL;
		} else return ComponentInfoType.CONTAINERS;
	}
}
