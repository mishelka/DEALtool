package gui.analyzer.handlers.swing.record;

import gui.analyzer.handlers.RecordSupport;
import gui.model.application.events.UiEvent;
import gui.tools.Recorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

public class JComboBoxRecordSupport extends RecordSupport<JComboBox> {

	/** Name item selection command name. */
	private static final String SELECT = "select";
	/** Index item selection command name. */
	private static final String INDEX = "index";

	public JComboBoxRecordSupport() {
	}

	@Override
	public boolean register(JComboBox component, Recorder _recorder) {
		if (listener == null) {
			listener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (recorder.isRecording()) {
						if (e.getID() == ActionEvent.ACTION_PERFORMED) {
							UiEvent event = createUiEvent((JComboBox) e
									.getSource());
							recorder.record(event);
						}
					}
				}
			};
		}

		if (!isRegistered(component)) {
	         recorder = _recorder;
	         component.addActionListener((ActionListener) listener);
	      }
	      return false;
	}

	@Override
	protected String[] createCommands(JComboBox component) {
		String[] commands = new String[]{INDEX, component.getSelectedIndex() + ""}; 
		return commands;
	}

	@Override
	protected boolean isRegistered(JComboBox component) {
		for (ActionListener l : component.getActionListeners()) {
			if (l == listener) {
				return true;
			}
		}
		return false;
	}
}
