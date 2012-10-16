package gui.model.application;

import gui.model.domain.DomainModel;

public class Scene<T> {
	protected String name;
	protected T sceneContainer;
	protected DomainModel domainModel;
	
	public Scene(T sceneContainer) {
		this.sceneContainer = sceneContainer;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public T getSceneContainer() {
		return sceneContainer;
	}
	
	public void setSceneContainer(T sceneContainer) {
		this.sceneContainer = sceneContainer;
	}
	
	public void setDomainModel(DomainModel domainModel) {
		this.domainModel = domainModel;
	}
	
	public DomainModel getDomainModel() {
		return domainModel;
	}
	
	public boolean equals(Scene scene) {
		return sceneContainer.equals(scene.getSceneContainer());
	}
}
