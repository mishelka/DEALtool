package gui.analyzer.handlers.jscicalc;

import gui.analyzer.handlers.CommandHandler;
import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.PathFinder;
import gui.model.domain.relation.RelationType;

import javax.swing.Icon;
import javax.swing.JLabel;

import jscicalc.EntryLabel;

public class CalculatorEntryLabelHandler extends DomainIdentifiable<EntryLabel>
		implements CommandHandler<EntryLabel> {

	@Override
	public String getDomainIdentifier(EntryLabel component) {
		return "Entry Label";
	}

	@Override
	public String getDomainDescriptor(EntryLabel component) {
		String desc = "<TEXT>";
		return desc + " " + component.getToolTipText();
	}

	@Override
	public String getDomainLabelDescriptor(EntryLabel component) {
		String label = null;
		JLabel l = PathFinder.getInstance().findLabelFor(component);
		if (l != null) {
			label = l.getText();
		}
		return label;
	}

	@Override
	public Icon getIcon(EntryLabel component) {
		return null;
	}

	@Override
	public RelationType getRelation(EntryLabel component) {
		return null;
	}
}
