package gui.analyzer.handlers.swing.record;

import gui.analyzer.handlers.RecordSupport;
import gui.model.application.events.UiEvent;
import gui.tools.Recorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

public class JFileChooserRecordSupport extends RecordSupport<JFileChooser> {

	public JFileChooserRecordSupport() {
	}

	@Override
	public boolean register(JFileChooser component, Recorder _recorder) {
		if (listener == null) {
			listener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					UiEvent event = createUiEvent((JFileChooser) e.getSource());
					recorder.record(event);
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
	protected String[] createCommands(JFileChooser component) {
		File file = component.getSelectedFile();
		return new String[]{file.getAbsolutePath()};
	}

	@Override
	protected boolean isRegistered(JFileChooser component) {
		for (ActionListener l : component.getActionListeners()) {
			if (l == listener) {
				return true;
			}
		}
		return false;
	}

}
