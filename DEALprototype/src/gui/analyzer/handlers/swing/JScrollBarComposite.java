package gui.analyzer.handlers.swing;

import gui.analyzer.handlers.Composite;

import javax.swing.JScrollBar;

public class JScrollBarComposite implements Composite<JScrollBar> {

	@Override
	public Object[] getComponents(JScrollBar container) {
		return new Object[] {};
	}
}
