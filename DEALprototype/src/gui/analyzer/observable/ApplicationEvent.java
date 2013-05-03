package gui.analyzer.observable;

import gui.model.application.scenes.Scene;

@SuppressWarnings("rawtypes")
public class ApplicationEvent {
	private Scene target;
	private ApplicationChangeState changeState;
	private String applicationName;
	
	public ApplicationEvent(Scene target, ApplicationChangeState changeState) {
		this.target = target;
		this.changeState = changeState;
	}
	
	public ApplicationEvent(String newName) {
		this.applicationName = newName;
		changeState = ApplicationChangeState.NAME_CHANGED;
	}
	
	public Scene getTargetScene() {
		return target;
	}
	
	public ApplicationChangeState getChangeState() {
		return changeState;
	}
	
	public String getApplicationName() {
		return applicationName;
	}
	
	public enum ApplicationChangeState {
		ADDED, EDITED, REMOVED, NAME_CHANGED;
	}
}
