package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.DomainIdentifiable;
import gui.analyzer.util.Util;
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
		if (component instanceof JRadioButton
				|| component instanceof JRadioButtonMenuItem
				|| component instanceof JToggleButton)
			return RelationType.MUTUALLY_EXCLUSIVE;
		if (component instanceof JCheckBox
				|| component instanceof JCheckBoxMenuItem)
			return RelationType.MUTUALLY_NOT_EXCLUSIVE;
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

		if(component instanceof MetalComboBoxButton) {
			return ComponentInfoType.UNKNOWN;
		}
		
		return ComponentInfoType.FUNCTIONAL;
	}
}
