package gui.analyzer.handlers.swing.record;

import gui.analyzer.handlers.RecordSupport;
import gui.tools.Recorder;

import javax.swing.JList;

public class JListRecordSupport extends RecordSupport<JList> {

	public JListRecordSupport() {
	}

	@Override
	public boolean register(JList component, Recorder _recorder) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String[] createCommands(JList component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isRegistered(JList component) {
		// TODO Auto-generated method stub
		return false;
	}

}
