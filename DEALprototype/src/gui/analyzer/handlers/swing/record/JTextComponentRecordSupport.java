package gui.analyzer.handlers.swing.record;

import gui.analyzer.Recorder;
import gui.analyzer.handlers.RecordSupport;
import gui.model.application.events.UiEvent;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

public class JTextComponentRecordSupport extends RecordSupport<JTextComponent> {
	
	public JTextComponentRecordSupport() {
	}

	/** Command name for changing the whole text (alternatively: SET). */
	private static final String TEXT = "text";
	/** Command name for changing the whole text (alternatively: TEXT). */
//	private static final String SET = "set";
//	/** Command name for adding string to the existing text. */
//	private static final String ADD = "add";
//	/** Command name for changing the cursor position. */
//	private static final String POSITION = "position";
	/** Shortened command name for changing the cursor position. */
	private static final String POS = "pos";
//	/** Command name for text replacement. */
//	private static final String REPLACE = "replace";
	/** Command name for text selection. */
	private static final String SELECT = "select";
//	/** Command name for text deletion. */
//	private static final String DELETE = "delete";
//	/** Command name for selected text replacement (alternatively: REPLACE_SELECTION). */
//	private static final String REPLACE_SELECTED = "replaceSelected";
//	/** Command name for selected text replacement  (alternatively: REPLACE_SELECTED). */
//	private static final String REPLACE_SELECTION = "replaceSelection";
//	/** Command name for selected text delection (alternatively: DELETE_SELECTION). */
//	private static final String DELETE_SELECTED = "deleteSelected";
//	/** Command name for selected text delection (alternatively: DELETE_SELECTED). */
//	private static final String DELETE_SELECTION = "deleteSelection";
//	/** Command name for selecting the whole text. */
//	private static final String SELECT_ALL = "selectAll";
//	/** Command name for replacing the whole text. */
//	private static final String REPLACE_ALL = "replaceAll";
//	/** Command name for deleting the whole text. */
//	private static final String DELETE_ALL = "deleteAll";

	@Override
	public boolean register(JTextComponent component, Recorder _recorder) {
		if (listener == null) {
			listener = new CaretListener() {

				@Override
				public void caretUpdate(CaretEvent e) {
					JTextComponent source = (JTextComponent) e.getSource();
					UiEvent event = createUiEvent(source);
					recorder.record(event);
				}
			};
		}
		
		CaretListener[] listeners = component.getCaretListeners();
		boolean registered = false;
		for (CaretListener l : listeners) {
			if (l == listener) {
				registered = true;
				break;
			}
		}
		if (!registered) {
			recorder = _recorder;
			component.addCaretListener((CaretListener) listener);
		}
		return false;
	}

	@Override
	protected List<String> createCommands(JTextComponent component) {
		List<String> commands = new ArrayList<String>();

		commands.add(TEXT + "=" + component.getText());
		commands.add(POS + "=" + component.getCaretPosition());

		String selectedText = component.getSelectedText();
		if (selectedText != null && !selectedText.isEmpty()) {
			commands.add(SELECT + "=" + component.getSelectedText());
		}

		return commands;
	}
}
