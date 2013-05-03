package gui.analyzer.handlers.jscicalc;

import gui.analyzer.handlers.CommandHandler;
import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.Util;
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
}
