package gui.model.application.events;

import gui.analyzer.util.Util;
import gui.model.application.componentPath.ComponentPath;
import gui.model.application.scenes.Scene;
import gui.model.domain.Term;

import java.util.Arrays;

/**
 * UiEvent class encapsualtes the event fired by the action, which was
 * performed by a user on the target application UI.
 * 
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class UiEvent {
	/** The scene, on which the action, which fired the event, was performed. */
	private Scene<?> sourceScene;
	// TODO: mozno spravit dalsie triedy pre reprezentaciu roznych typov eventov
	// zmena obrazovky, zmena obsahu
	// graficka zmena, textova zmena, atd.
	// zatial staci len zaznamenavat postupnost udalosti, to ze nastali, potom
	// dalsie udaje
//	/** Not used yet. */
//	private Scene<?> targetScene; // ako toto zistit?
	/** The term, which is the cause of an action, which fired this event */
	private Term cause;
	/** The component, which is the cause of an action, which fired this event */
	private Object component;
	// TODO: in the future this will be replaced by a list of default actions,
	// for now it is a list of strings
	/** Sometimes, not only the term and component are needed to 
	 * identify the action. A list of commands is needed as additional information.
	 * For example, for a button, only its name is important because a button
	 * can only be clicked. But for text components, there are multiple
	 * possibilities: writing a text, moving a cursor, deleting text, etc. */
	private String[] commands;

	/**
	 * @param component the component, which is the cause of an action, which fired this event
	 */
	public UiEvent(Object component) {
		this.component = component;
	}
	
	public UiEvent() {
		// TODO docasny konstruktor
	}

	/**
	 * @return the scene, on which the action, which fired the event, was performed.
	 */
	public Scene<?> getSourceScene() {
		return sourceScene;
	}

	/**
	 * @param sourceScene the scene, on which the action, which fired the event, was performed.
	 */
	public void setSourceScene(Scene<?> sourceScene) {
		this.sourceScene = sourceScene;
	}

//	/**Not used yet*/
//	public Scene<?> getTargetScene() {
//		return targetScene;
//	}

//	/** Not used yet */
//	public void setTargetScene(Scene<?> targetScene) {
//		this.targetScene = targetScene;
//	}

	/**
	 * @return the term, which is the cause of an action, which fired this event
	 */
	public Term getCause() {
		return cause;
	}

	/**
	 * @param sourceTerm the term, which is the cause of an action, which fired this event
	 */
	public void setCause(Term sourceTerm) {
		this.cause = sourceTerm;
	}

	/**
	 * @return the component, which is the cause of an action, which fired this event
	 */
	public Object getComponent() {
		return component;
	}

	/**
	 * Sometimes, not only the term and component are needed to 
	 * identify the action. A list of commands is needed as additional information.
	 * For example, for a button, only its name is important because a button
	 * can only be clicked. But for text components, there are multiple
	 * possibilities: writing a text, moving a cursor, deleting text, etc.
	 * @return the set of additional commands for this event
	 */
	public String[] getCommands() {
		return commands;
	}

	/**
	 * Sometimes, not only the term and component are needed to 
	 * identify the action. A list of commands is needed as additional information.
	 * For example, for a button, only its name is important because a button
	 * can only be clicked. But for text components, there are multiple
	 * possibilities: writing a text, moving a cursor, deleting text, etc.
	 * @param commands the set of additional commands for this event
	 */
	public void setCommands(String[] commands) {
		this.commands = commands;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		String name = "";
		if(cause != null)
			name = cause.toString();
		
		if(Util.isEmpty(name)) {
			//TODO: docasne takto vyriesene, treba opravit bug pri zobrazeni okna vizualizacie
			if(sourceScene == null) return null;
			//end
			
			ComponentPath cPath = new ComponentPath(sourceScene, component);
			name = cPath.toString();
		}
		
		sb.append("{" + name + "}");

		sb.append(" ");

		if (commands != null && commands.length != 0) {
			sb.append(Arrays.toString(commands));
		}

		return sb.toString();
	}
}