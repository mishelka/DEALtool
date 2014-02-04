package gui.analyzer.handlers.swing.record;

import gui.analyzer.handlers.RecordSupport;
import gui.analyzer.util.Logger;
import gui.model.application.events.UiEvent;
import gui.tools.Recorder;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class JTableRecordSupport extends RecordSupport<JTable>{
	
	/** The item index selection command name. */
	private static final String INDICES = "indices";
	   
	public JTableRecordSupport() { }
	
	@Override
	public boolean register(final JTable component, Recorder _recorder) {
		listener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				UiEvent event = createUiEvent(component);
				recorder.record(event);
			}
		};
		
		if (!isRegistered(component)) {
			recorder = _recorder;
				component.getSelectionModel().
				addListSelectionListener((ListSelectionListener)listener);
		}
		
		return false;
	}

	@Override
	protected String[] createCommands(JTable component) {
		int[] selectedRows = component.getSelectedRows();
		String[] commands = null;
		if(selectedRows.length == 0) {
			return null;
		} else {
			commands = new String[selectedRows.length + 1];
			commands[0] = INDICES;
			for(int i = 0; i < selectedRows.length; i++) {
	    		commands[i+1] = selectedRows[i] + "";
	    	}
		}
		return commands;
	}

	@Override
	protected boolean isRegistered(JTable component) {
		for(ListSelectionListener l 
				: component.getListeners(ListSelectionListener.class)) {
			if(listener.equals(l)) {
				Logger.log("equals");
				return true;
			}
		}
		
		return false;
	}
}
