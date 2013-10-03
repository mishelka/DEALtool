package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.CommandHandler;
import gui.analyzer.handlers.Composite;
import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.JLabelFinder;
import gui.analyzer.util.Util;
import gui.model.application.program.Command;
import gui.model.application.program.DelegateCommand;
import gui.model.domain.ComponentInfoType;

import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

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
		if (component instanceof JMenuItem) {
			return ComponentInfoType.FUNCTIONAL;
		} else
			return ComponentInfoType.CONTAINERS;
	}

	// CommandHandler implemented methods

	@Override
	public boolean supportsCommand(Command command, JMenu component) {
		String commandName = ((DelegateCommand) command).getName().toString();

		return component.isEnabled()
				&& (commandName.equalsIgnoreCase(component.getName())
						|| commandName.equalsIgnoreCase(component
								.getActionCommand())
						|| commandName.equalsIgnoreCase(component.getText())
						|| commandName.equalsIgnoreCase(component
								.getToolTipText()) ||
						JLabelFinder.existsComponentByLabelFor(commandName, component));
//						|| commandName.equalsIgnoreCase(path));
	}

	@Override
	public void execute(Command command, JMenu component) {
		component.setSelected(!component.isSelected());
	      component.setPopupMenuVisible(component.isSelected());
	      System.out.println(">>" + ((DelegateCommand) command).getName() + " executed.");
	}
}
