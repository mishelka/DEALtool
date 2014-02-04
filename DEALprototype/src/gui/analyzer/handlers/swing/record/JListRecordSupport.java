package gui.analyzer.handlers.swing.record;

import gui.analyzer.handlers.RecordSupport;
import gui.model.application.events.UiEvent;
import gui.tools.Recorder;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("rawtypes")
public class JListRecordSupport extends RecordSupport<JList> {

   /** The item name selection command name. Unused for now. */
   private static final String SELECT = "select";
   /** The item index selection command name. */
   private static final String INDICES = "indices";
	
	public JListRecordSupport() {
	}

	@Override
	public boolean register(JList component, Recorder _recorder) {
		 if (listener == null) {
	         listener = new ListSelectionListener() {

	            @Override
	            public void valueChanged(ListSelectionEvent e) {
	               if (recorder.isRecording()) {
	                  if (e.getValueIsAdjusting()) {
	                     JList source = (JList) e.getSource();
	                     UiEvent event = createUiEvent((JList) e
									.getSource());
	                     recorder.record(event);
	                  }
	               }
	            }
	         };
	      }
		 
	      if (!isRegistered(component)) {
	         this.recorder = _recorder;
	         component.addListSelectionListener((ListSelectionListener) listener);
	      }
	      return false;
	}

	@Override
	protected String[] createCommands(JList component) {
		int[] indices = component.getSelectedIndices();
	    String[] commands = null;
	    if(indices.length == 0) {
	    	return null;
	    } else if(indices.length > 0) {
	    	commands = new String[indices.length + 1];
	    	commands[0] = INDICES;
	    	for(int i = 0; i < indices.length; i++) {
	    		commands[i+1] = indices[i] + "";
	    	}
	    }
	    return commands;
	}

	@Override
	protected boolean isRegistered(JList component) {
		for(ListSelectionListener l : component.getListSelectionListeners()) {
			if(listener.equals(l)) {
				return true;
			}
		}
		
		return false;
	}

}
