package gui.analyzer.handlers.swing.record;

import gui.analyzer.handlers.RecordSupport;
import gui.analyzer.handlers.RecordSupports;
import gui.tools.Recorder;

import java.awt.Component;

import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

public class JSpinnerRecordSupport extends RecordSupport<JSpinner> {

	@Override
	public boolean register(JSpinner component, Recorder _recorder) {
		JTextField jSpinnerTextField = null;
		for (Component c : component.getComponents()) {
			if (c != null) {
				if (c instanceof NumberEditor) {
					jSpinnerTextField = ((NumberEditor) c).getTextField();
					break;
				}
			}
		}

		// do not register jspinner or its children, rather register the text field
		// of jspinner
		if (jSpinnerTextField != null) {
			RecordSupport<JTextComponent> txtRS = RecordSupports.getInstance()
					.getRecordSupport(JTextComponent.class);
			
			txtRS.register(jSpinnerTextField, _recorder);
		}

		//do not continue to register the children of jspinner
		return false;
	}

	@Override
	protected String[] createCommands(JSpinner component) {
		return null;
	}

	protected boolean isRegistered(JSpinner component) {
		return false;
	}
}
