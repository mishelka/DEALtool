package gui.analyzer.observable;

import gui.model.application.scenes.Scene;

import java.util.Observable;

public abstract class AppObservable extends Observable {
	public abstract void notifySceneAdded(Scene<?> scene);
	public abstract void notifySceneRemoved(Scene<?> scene);
	public abstract void notifySceneEdited(Scene<?> scene);
	public abstract void notifyAppNameChanged();
}
