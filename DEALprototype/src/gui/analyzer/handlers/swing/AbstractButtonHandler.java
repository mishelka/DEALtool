package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.Util;
import gui.model.domain.relation.RelationType;

import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;

public class AbstractButtonHandler extends DomainIdentifiable<AbstractButton> {

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
		if (component instanceof JRadioButton)
			return RelationType.MUTUALLY_EXCLUSIVE;
		if (component instanceof JCheckBox)
			return RelationType.MUTUALLY_NOT_EXCLUSIVE;
		return super.getParentRelation(component);
	}
}
