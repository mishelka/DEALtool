package gui.model.application.scenes;

import gui.model.domain.DomainModel;

import java.awt.Component;
import java.awt.Dialog;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class Scene<T> {
	protected String name;
	protected T sceneContainer;
	protected DomainModel domainModel;
	protected List<Component> componentList;
	//history of old domain models - when the window changes
	protected List<DomainModel> oldDomainModels;

	public Scene(T sceneContainer) {
		this.sceneContainer = sceneContainer;
		this.oldDomainModels = new ArrayList<DomainModel>();
	}
	
	public void updateName(T sceneContainer) {
		if(sceneContainer instanceof Dialog) {
			name = ((Dialog) sceneContainer).getTitle();
		} else if (sceneContainer instanceof JFrame) {
			name = ((JFrame)sceneContainer).getTitle();
		}
	}
	
	public List<Component> getComponentList() {
		return componentList;
	}

	public void setComponentList(List<Component> componentList) {
		this.componentList = componentList;
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
		if(this.domainModel != null) {
			oldDomainModels.add(this.domainModel);
		}
		this.domainModel = domainModel;
	}
	
	public DomainModel getDomainModel() {
		return domainModel;
	}
	
	public boolean isWindowScene() {
		return (this instanceof WindowScene);
	}
	
	public WindowScene getWindowScene() {
		if(isWindowScene()) return ((WindowScene) this);
		else return null;
	}
	
	@SuppressWarnings("rawtypes")
	public boolean equals(Scene scene) {
		return sceneContainer.equals(scene.getSceneContainer());
	}
	
	@Override
	public String toString() {
		String toString = "";
		if(this instanceof WindowScene) {
			toString = "Window ";
		} else if (this instanceof FrameScene) {
			toString = "Frame ";
		} else if (this instanceof DialogScene) {
			toString = "Dialog ";
		}
		
		return toString + name + " [" + sceneContainer.getClass().getSimpleName() + "]";
	}
}
