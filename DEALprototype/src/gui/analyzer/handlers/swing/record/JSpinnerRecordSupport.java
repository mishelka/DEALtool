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

	private RecordSupport<JTextComponent> txtRS;
	
	@Override
	public boolean register(JSpinner component, Recorder _recorder) {
		JTextField jSpinnerTextField = getJSpinnerTextField(component);

		// do not register jspinner or its children, rather register the text field
		// of jspinner
		if (jSpinnerTextField != null) {
			txtRS = RecordSupports.getInstance()
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
	
	private JTextField getJSpinnerTextField(JSpinner component) {
		JTextField jstf = null;
		for (Component c : component.getComponents()) {
			if (c != null) {
				if (c instanceof NumberEditor) {
					jstf = ((NumberEditor) c).getTextField();
					return jstf;
				}
			}
		}
		return jstf;
	}
}
