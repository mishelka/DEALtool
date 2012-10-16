package gui.analyzer.handlers.jscicalc;

import gui.analyzer.handlers.CommandHandler;
import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.PathFinder;
import gui.model.domain.relation.RelationType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.JLabel;

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

			toolTip = removeHtmlTags(toolTip);

			return toolTip;
		}
		
		String label = component.getText();
		label = removeHtmlTags(label);
		return label;
	}

	@Override
	public String getDomainDescriptor(CalculatorButton component) {
		if(component instanceof LeftButton || component instanceof RightButton
				|| component instanceof UpButton || component instanceof DownButton || component instanceof InfoButton) {
			String label = component.getText();
			label = removeHtmlTags(label);
			return label;
		}
		
		String toolTip = component.getToolTipText();

		toolTip = removeHtmlTags(toolTip);

		return toolTip;
	}

	@Override
	public String getDomainLabelDescriptor(CalculatorButton component) {
		String label = null;
		JLabel l = PathFinder.getInstance().findLabelFor(component);
		if (l != null) {
			label = l.getText();
			label = removeHtmlTags(label);
		}
		return label;
	}

	@Override
	public Icon getIcon(CalculatorButton component) {
		return component.getIcon();
	}

	@Override
	public RelationType getRelation(CalculatorButton component) {
		return null;
	}

	private final static Pattern htmlTagPattern = Pattern
			.compile("[<][/]?[.]*[>]");

	private String removeHtmlTags(String text) {
		Matcher dataMatcher = htmlTagPattern.matcher(text);
		while (dataMatcher.find()) {
			String toRemove = dataMatcher.group(0);
			text = text.replace(toRemove, "");
			dataMatcher = htmlTagPattern.matcher(text);
		}

		return text;
	}
}
