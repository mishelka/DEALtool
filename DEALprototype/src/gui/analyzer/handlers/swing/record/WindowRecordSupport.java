package gui.analyzer.handlers.swing.record;

import gui.analyzer.handlers.RecordSupport;
import gui.analyzer.util.Logger;
import gui.model.application.events.UiEvent;
import gui.tools.Recorder;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

public class WindowRecordSupport extends RecordSupport<Window> {
	
	public static final String CLOSE = "CLOSE";
	private List<Toolkit> toolkits = new ArrayList<Toolkit>();

	@Override
	//not working
	public boolean register(Window component, Recorder _recorder) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		if (!toolkits.contains(toolkit)) {
			toolkits.add(toolkit);
			toolkit.addAWTEventListener(new AWTEventListener() {
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
			}, AWTEvent.WINDOW_EVENT_MASK);
		}

		WindowListener[] listeners = component.getWindowListeners();
		boolean registered = false;

		// Don't register a new listener, if it's already registered
		for (WindowListener l : listeners) {
			if (l.equals(listener)) {
				registered = true;
				break;
			}
		}

		// If there's no such listener on this component, then register it.
		if (!registered) {
			recorder = _recorder;
			component.addWindowListener((WindowAdapter) listener);
		}
		
		return true;
	}

	@Override
	protected String[] createCommands(Window component) {
		Logger.log(">>>>> CLOSING WINDOW");
		return new String[] {CLOSE};
	}
}
