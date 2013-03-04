package gui.analyzer.handlers;

import gui.analyzer.Recorder;
import gui.analyzer.util.ComponentFinder;
import gui.analyzer.util.Logger;
import gui.editor.DomainModelEditor;
import gui.model.application.Scene;
import gui.model.application.events.UiEvent;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;

import java.util.Collection;
import java.util.EventListener;
import java.util.List;

/**
 * Extension of the RecordSupportRegister handler for recording.
 * 
 * @author Michaela Baèíková <michaela.bacikova@tuke.sk>
 * @param <T>
 *            The component class type.
 */
public abstract class RecordSupport<T> implements RecordSupportRegister<T> {

	/**
	 * A Recorder instance responsible for recording commands to the local
	 * memory.
	 */
	protected Recorder recorder;
	/** A listener which should be registered on a component. */
	protected EventListener listener;

	public RecordSupport() {
	}

	/**
	 * Creates a list of string commands which should be recorded by recorder.
	 * If it's not possible to create the list of commands, i.e. if there is no
	 * component identifier, returns null. <br/>
	 * If there is only one action, which can be performed on the target
	 * component type (i.e. JButton can only be clicked), then this list should
	 * be empty, because the command is only represented by the Term which
	 * represents the component. <br/>
	 * If there are many types of actions which can be performed on the target
	 * component type (i.e. JTextField can be edited, but also only the cursor
	 * can be moved), then this list contains the textual representations of the
	 * commands performed on this component. The definitions of the textual
	 * representations of the commands available for the concrete component type
	 * are stored in a concrete RecordSupport class.
	 * 
	 * @param component
	 *            the component, for which a string command should be created.
	 * @return A list of string commands for the given component if it is
	 *         possible to create it, null otherwise.
	 */
	protected abstract List<String> createCommands(T component);

	protected UiEvent createUiEvent(T component) {
		UiEvent uiEvent = new UiEvent(component);

		uiEvent.setCause(getTermForComponent(component));
		uiEvent.setSourceScene(getSceneForComponent(component));

		uiEvent.setCommands(createCommands(component));

		return uiEvent;
	}

	private Term getTermForComponent(T component) {
		Collection<DomainModel> domainModels = DomainModelEditor
				.getDomainModels().values();
		
		Term term = null;

		for (DomainModel dm : domainModels) {
			term = dm.getTermForComponent(component);
			if (term != null) {
				return term;
			}
		}

		return null;
	}

	protected Scene<?> getSceneForComponent(T component) {
		List<Scene<?>> scenes = DomainModelEditor.getApplication().getScenes();

		Scene<?> scene = ComponentFinder.getInstance()
				.getSceneContainingComponent(scenes, component);

		return scene;
	}
}
