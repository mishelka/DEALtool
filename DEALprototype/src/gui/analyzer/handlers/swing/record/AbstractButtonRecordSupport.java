package gui.analyzer.handlers.swing.record;

import gui.analyzer.handlers.RecordSupport;
import gui.model.application.events.UiEvent;
import gui.tools.Recorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JMenu;

public class AbstractButtonRecordSupport extends RecordSupport<AbstractButton> {

	public AbstractButtonRecordSupport() {
	}

	@Override
	public boolean register(AbstractButton component, Recorder _recorder) {
		if (!(component instanceof JMenu)) {
			if (listener == null) {
				listener = new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (recorder.isRecording()) {
							if (e.getID() == ActionEvent.ACTION_PERFORMED) {
								UiEvent event = createUiEvent((AbstractButton) e
										.getSource());
								recorder.record(event);
							}
						}
					}
				};
			}
			
			//Logger.log(">>> is registered: " + isRegistered(component));
			
			// If there's no such listener on this component, then register it.
			if (!isRegistered(component)) {
				recorder = _recorder;
				component.addActionListener((ActionListener) listener);
			}
		}

		//continue to register children of this component only if it is a JMenu
		return (component instanceof JMenu);
	}

	@Override
	protected String[] createCommands(AbstractButton component) {
		//we're not implementing anything, because a button click is a single event
		return null;
	}
	
	/**
	 * This method is overriden just to know it's there.
	 */
	@Override
	protected UiEvent createUiEvent(AbstractButton component) {
		return super.createUiEvent(component);
	}
	
	protected boolean isRegistered(AbstractButton component) {
		for(ActionListener l : component.getActionListeners()) {
			if(listener.equals(l)) {
				return true;
			}
		}
		
		return false;
	}
}
