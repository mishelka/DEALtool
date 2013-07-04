package gui.analyzer.handlers.swing.record;

import gui.analyzer.handlers.RecordSupport;
import gui.model.application.events.UiEvent;
import gui.tools.Recorder;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;

public class WindowRecordSupport extends RecordSupport<Window> {

	public static final String CLOSE = "CLOSE";

	@Override
	// not working
	public boolean register(Window component, Recorder _recorder) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		if (listener == null) {
			listener = new AWTEventListener() {
				@Override
				public void eventDispatched(AWTEvent windowEvent) {
					if (windowEvent instanceof WindowEvent) {
						WindowEvent we = (WindowEvent) windowEvent;
						if (we.getID() == WindowEvent.WINDOW_CLOSED) {
							Window w = we.getWindow();
							UiEvent uiEvent = createUiEvent(w);
							recorder.record(uiEvent);
						}
					}
				}
			};
			
			boolean registered = false;
			for(AWTEventListener el : toolkit.getAWTEventListeners()) {
				if(listener.equals(el)) 
					registered = true;
			}

			// If there's no such listener on this component, then register it.
			if (!registered) {
				recorder = _recorder;
				toolkit.addAWTEventListener((AWTEventListener) listener, AWTEvent.WINDOW_EVENT_MASK);
			}
		}

		return true;
	}

	@Override
	protected String[] createCommands(Window component) {
		return new String[] { CLOSE };
	}
}
