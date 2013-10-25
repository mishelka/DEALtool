package gui.analyzer.observable;

import gui.model.application.scenes.Scene;

/**
 * The event type, which is used in AppObservable.
 * 
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
@SuppressWarnings("rawtypes")
public class ApplicationEvent {
	/** The source scene - the scene that fired the event. */
	private Scene source;
	/** The type of change that occured: Scene was added, edited, removed or application name was changed. */
	private ApplicationChangeState changeState;
	/** The name of the application, in which the event was fired. */
	private String applicationName;
	
	/** Constructor for the state change events */
	public ApplicationEvent(Scene source, ApplicationChangeState changeState) {
		this.source = source;
		this.changeState = changeState;
	}
	
	/** Constructor for the name change events */
	public ApplicationEvent(String newName) {
		this.applicationName = newName;
		changeState = ApplicationChangeState.NAME_CHANGED;
	}
	
	/**
	 * @return the scene which fired the event
	 */
	public Scene getSourceScene() {
		return source;
	}
	
	/**
	 * @return the change, which was performed
	 */
	public ApplicationChangeState getChangeState() {
		return changeState;
	}
	
	/**
	 * @return the name of the application, in which the event was fired
	 */
	public String getApplicationName() {
		return applicationName;
	}
	
	/**
	 * Stores the type of change, that was performed as a source of this event.
	 * @author Michaela Bacikova, Slovakia,
	 * michaela.bacikova@tuke.sk
	 */
	public enum ApplicationChangeState {
		/**
		 * A scene was added into the Applications' scene list.
		 */
		ADDED, 
		/**
		 * A scene was edited in the Applications' scene list.
		 */
		EDITED, 
		/**
		 * A scene was removed from the Applications' scene list.
		 */
		REMOVED, 
		/**
		 * A name of the application was changed.
		 */
		NAME_CHANGED;
	}
}
