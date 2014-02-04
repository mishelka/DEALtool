package gui.analyzer.handlers.swing.record;

import gui.analyzer.handlers.RecordSupport;
import gui.model.application.events.UiEvent;
import gui.tools.Recorder;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class JTreeRecordSupport extends RecordSupport<JTree> {

	/** Multiple nodes selection command name. */
	private static final String NODES = "nodes";

	public JTreeRecordSupport() {
	}

	@Override
	public boolean register(JTree component, Recorder _recorder) {
		if (listener == null) {
			listener = new TreeSelectionListener() {

				@Override
				public void valueChanged(TreeSelectionEvent e) {
					if (recorder.isRecording()) {
						UiEvent event = createUiEvent((JTree) e.getSource());
						recorder.record(event);
					}
				}
			};
		}

		if (!isRegistered(component)) {
			recorder = _recorder;
			component
					.addTreeSelectionListener((TreeSelectionListener) listener);
		}
		return false;
	}

	@Override
	protected String[] createCommands(JTree component) {
		List<String> commands = null;

		TreePath[] paths = component.getSelectionPaths();

		if (paths != null) {
			commands = new ArrayList<String>();
			commands.add(NODES);
			for (int p = 0; p < paths.length; p++) {
				if (paths[p] != null) {
					Object o = paths[p].getLastPathComponent();
					if (o != null) {
						if (o instanceof TreeNode) {
							TreeNode tn = (TreeNode) o;
							String value = tn.toString();
							if (value != null && !value.equals("")) {
								commands.add(value);
							}
						}
					}
				}
			}
			
			return commands.toArray(new String[]{});
		}

		return null;
	}

	@Override
	protected boolean isRegistered(JTree component) {
		for (TreeSelectionListener l : component.getTreeSelectionListeners()) {
			if (l.equals(listener)) {
				return true;
			}
		}

		return false;
	}
}
