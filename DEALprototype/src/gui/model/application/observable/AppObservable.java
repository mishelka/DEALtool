package gui.model.application.observable;

import gui.model.application.scenes.Scene;

import java.util.Observable;

/**
 * Observer for the Application class.
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public abstract class AppObservable extends Observable {
	/**
	 * Notifies observers when a scene was added to the Applications' scene list.
	 * @param scene the scene, which was added to the Applications' scene list
	 */
	public abstract void notifySceneAdded(Scene<?> scene);
	/**
	 * Notifies observers when a scene was removed from the Applications' scene list.
	 * @param scene the scene which was removed
	 */
	public abstract void notifySceneRemoved(Scene<?> scene);
	/**
	 * Notifies observers when a scene in the Applications' scene list was edited.
	 * @param scene the scene which was edited
	 */
	public abstract void notifySceneEdited(Scene<?> scene);
	/**
	 * Notifies observers when the application name is changed.
	 */
	public abstract void notifyAppNameChanged();
}
