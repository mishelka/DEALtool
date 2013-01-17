package gui.analyzer.handlers.jscicalc;

import gui.analyzer.handlers.CommandHandler;
import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.PathFinder;
import gui.analyzer.util.Util;
import gui.model.domain.relation.RelationType;

import javax.swing.Icon;
import javax.swing.JLabel;

import jscicalc.DisplayLabel;

public class CalculatorDisplayLabelHandler extends
		DomainIdentifiable<DisplayLabel> implements CommandHandler<DisplayLabel> {

	@Override
	public String getDomainIdentifier(DisplayLabel component) {
		return "Display Label";
	}

	@Override
	public String getDomainDescriptor(DisplayLabel component) {
		String desc = "<TEXT>";
		desc += " [SIZE=" + component.getDigits() + "]";
		desc = Util.htmlToText(desc);
		return desc + " ";
	}

	@Override
	public String getDomainLabelDescriptor(DisplayLabel component) {
		String label = null;
		JLabel l = PathFinder.getInstance().findLabelFor(component);
		if (l != null) {
			label = l.getText();
		}
		label = Util.htmlToText(label);
		return label;
	}

	@Override
	public Icon getIcon(DisplayLabel component) {
		return null;
	}

	@Override
	public RelationType getRelation(DisplayLabel component) {
		return null;
	}
}
