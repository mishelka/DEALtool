package gui.analyzer.handlers.swing.record;

import gui.analyzer.handlers.RecordSupport;
import gui.model.application.events.UiEvent;
import gui.tools.Recorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;

public class JMenuRecordSupport extends RecordSupport<JMenu> {

	public JMenuRecordSupport() {
	}

	@Override
	public boolean register(JMenu component, Recorder _recorder) {
		if (listener == null) {
			listener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					UiEvent event = createUiEvent((JMenu) e.getSource());
					recorder.record(event);
				}
			};
		}

		if (!isRegistered(component)) {
			recorder = _recorder;
			component.addActionListener((ActionListener) listener);
		}

		return true;
	}

	@Override
	protected String[] createCommands(JMenu component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isRegistered(JMenu component) {
		for (ActionListener l : component.getActionListeners()) {
			if (l == listener) {
				return true;
			}
		}
		return false;
	}

}
