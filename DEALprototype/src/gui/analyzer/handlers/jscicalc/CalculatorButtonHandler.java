package gui.analyzer.handlers.jscicalc;

import gui.analyzer.handlers.CommandHandler;
import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.JLabelFinder;
import gui.analyzer.util.Util;
import gui.model.application.program.Command;
import gui.model.application.program.DelegateCommand;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.relation.RelationType;

import javax.swing.Icon;

import jscicalc.button.CalculatorButton;
import jscicalc.button.DownButton;
import jscicalc.button.InfoButton;
import jscicalc.button.LeftButton;
import jscicalc.button.RightButton;
import jscicalc.button.UpButton;

/*Left, right info, up, down*/
public class CalculatorButtonHandler extends
		DomainIdentifiable<CalculatorButton> implements
		CommandHandler<CalculatorButton> {
	@Override
	public String getDomainIdentifier(CalculatorButton component) {
		if(component instanceof LeftButton || component instanceof RightButton
				|| component instanceof UpButton || component instanceof DownButton || component instanceof InfoButton) {
			String toolTip = component.getToolTipText();

			toolTip = Util.htmlToText(toolTip);

			return toolTip;
		}
		
		String label = component.getText();
		
		label = Util.htmlToText(label);
		return label;
	}

	@Override
	public String getDomainDescriptor(CalculatorButton component) {
		if(component instanceof LeftButton || component instanceof RightButton
				|| component instanceof UpButton || component instanceof DownButton || component instanceof InfoButton) {
			String label = component.getText();
			label = Util.htmlToText(label);
			return label;
		}
		
		String toolTip = component.getToolTipText();

		toolTip = Util.htmlToText(toolTip);
		return toolTip;
	}

	@Override
	public Icon getIcon(CalculatorButton component) {
		return component.getIcon();
	}

	@Override
	public RelationType getRelation(CalculatorButton component) {
		return null;
	}
	
	@Override
	public ComponentInfoType getComponentInfoType(CalculatorButton component) {
		return ComponentInfoType.FUNCTIONAL;
	}
	
	@Override
	public boolean extractChildren() {
		return false;
	}

	@Override
	public boolean supportsCommand(Command command, CalculatorButton component) {
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
	public void execute(Command command, CalculatorButton component) {
		component.doClick();
		System.out.println(">>" + ((DelegateCommand) command).getName() + " executed.");
	}
}
