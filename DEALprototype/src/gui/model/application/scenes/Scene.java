package gui.model.application.scenes;

import gui.model.domain.DomainModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a GUI scene.
 * A scene can be a window, dialog, frame or a web page.
 *
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 * 
 * @param <T> the type of the scene (e.g. java.awt.Window) 
 */
public abstract class Scene<T> {
	/** The name of the scene, usually extracted from the GUI scene title. */
	protected String name;
	/** The container, from which the scene was extracted. */
	protected T sceneContainer;
	/** The domain model, which was extracted from this scene. */
	protected DomainModel domainModel;
//	/** The list of components in this scene. */
//	protected List<Component> componentList;
	/** The history of old domain models, when this scene changes in the future. */
	protected List<DomainModel> oldDomainModels;

	/**
	 * The constructor of a scene.
	 * @param sceneContainer the GUI container, from which this scene was extracted
	 */
	public Scene(T sceneContainer) {
		this.sceneContainer = sceneContainer;
		this.oldDomainModels = new ArrayList<DomainModel>();
	}
	
	/**
	 * Updates this scene's name.
	 * @param sceneContainer the sceneContainer, from which the name is automatically extracted and saved in this scene.
	 */
	public void updateName(T sceneContainer) {
		this.name = getSceneName(sceneContainer);
	}
	
	/**
	 * Returns the string name of the scene, depending on the scene type T.
	 * @param sceneContainer the container, from which the name should be extracted.
	 * @return the string name of the scene
	 */
	protected abstract String getSceneName(T sceneContainer);
	
	/**
	 * @return the name of this scene
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the container, which is encapsulated by this scene
	 */
	public T getSceneContainer() {
		return sceneContainer;
	}
	
	/**
	 * @param sceneContainer the container, which is encapsulated by this scene
	 */
	public void setSceneContainer(T sceneContainer) {
		this.sceneContainer = sceneContainer;
	}
	
	/**
	 * @param domainModel the domain model, which was extracted from this scene
	 */
	public void setDomainModel(DomainModel domainModel) {
		if(this.domainModel != null) {
			oldDomainModels.add(this.domainModel);
		}
		this.domainModel = domainModel;
	}
	
	/**
	 * @return the domain model, which was extracted from this scene
	 */
	public DomainModel getDomainModel() {
		return domainModel;
	}
	
	/**
	 * Two scenes are equal only if their sceneContainer is the same.
	 * @param scene the scene to be compared with this scene
	 * @return true if the sceneContainers in both scenes are the same objects, false otherwise
	 */
	public boolean equals(Scene<?> scene) {
		return sceneContainer.equals(scene.getSceneContainer());
	}
	
	@Override
	public String toString() {
		return name + " [" + sceneContainer.getClass().getSimpleName() + "]";
	}
}
