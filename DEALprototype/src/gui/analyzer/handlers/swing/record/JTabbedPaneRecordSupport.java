package gui.analyzer.handlers.swing.record;

import gui.analyzer.handlers.RecordSupport;
import gui.analyzer.util.Logger;
import gui.analyzer.util.Util;
import gui.model.application.events.UiEvent;
import gui.tools.Recorder;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JTabbedPaneRecordSupport extends RecordSupport<JTabbedPane> {

	public JTabbedPaneRecordSupport() {
	}

	@Override
	public boolean register(JTabbedPane component, Recorder _recorder) {
		if (listener == null) {
			listener = new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					UiEvent event = createUiEvent((JTabbedPane) e.getSource());
					recorder.record(event);
				}
			};
		}
		
		if (!isRegistered(component)) {
	         recorder = _recorder;
	         component.addChangeListener((ChangeListener) listener);
	      }
	      return true;
	}

	@Override
	protected String[] createCommands(JTabbedPane component) {
		String clickedTab = component.getTitleAt(component.getSelectedIndex());
	      if (clickedTab == null || clickedTab.equals("")) {
	         clickedTab = component.getToolTipTextAt(component.getSelectedIndex());
	      }
	      
	      Logger.log(clickedTab);
	      
	      if(!Util.isEmpty(clickedTab)) return new String[]{clickedTab};
	      
	      return null;
	}

	@Override
	protected boolean isRegistered(JTabbedPane component) {
		for (ChangeListener l : component.getChangeListeners()) {
			if (l == listener) {
				return true;
			}
		}
		return false;
	}

}
