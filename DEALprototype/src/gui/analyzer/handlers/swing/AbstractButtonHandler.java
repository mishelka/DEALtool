package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.CommandHandler;
import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.JLabelFinder;
import gui.analyzer.util.Util;
import gui.model.application.program.Command;
import gui.model.application.program.DelegateCommand;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.relation.RelationType;

import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;
import javax.swing.plaf.metal.MetalComboBoxButton;

public class AbstractButtonHandler extends DomainIdentifiable<AbstractButton>
		implements CommandHandler<AbstractButton> {

	@Override
	public String getDomainIdentifier(AbstractButton component) {
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
	public String getDomainDescriptor(AbstractButton component) {
		String desc = component.getToolTipText();
		if (desc == null)
			desc = "";
		return desc;
	}

	@Override
	public Icon getIcon(AbstractButton component) {
		return component.getIcon();
	}

	@Override
	public RelationType getParentRelation(AbstractButton component) {
		if (component instanceof JCheckBox
				|| component instanceof JCheckBoxMenuItem) {
			return RelationType.MUTUALLY_NOT_EXCLUSIVE;
		} else if (component instanceof JRadioButtonMenuItem // JRadioButton
																// dedi od
																// JToggleButton
																// takze to
																// mozeme dat do
																// tej istej
																// skupiny
				|| component instanceof JToggleButton) {
			return RelationType.MUTUALLY_EXCLUSIVE;
		}
		return super.getParentRelation(component);
	}

	@Override
	public ComponentInfoType getComponentInfoType(AbstractButton component) {
		if (component instanceof JRadioButton
				|| component instanceof JRadioButtonMenuItem
				|| component instanceof JCheckBox
				|| component instanceof JCheckBoxMenuItem
				|| component instanceof JToggleButton)
			return ComponentInfoType.LOGICALLY_GROUPING;

		if (component instanceof MetalComboBoxButton) {
			return ComponentInfoType.UNKNOWN;
		}

		return ComponentInfoType.FUNCTIONAL;
	}

	@Override
	public boolean supportsCommand(Command command, AbstractButton component) {
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
	public void execute(Command command, AbstractButton component) {
		component.doClick();
	    System.out.println(">>" + ((DelegateCommand) command).getName() + " executed.");
	}
}
