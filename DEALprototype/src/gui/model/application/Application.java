package gui.model.application;

import gui.model.application.observable.AppObservable;
import gui.model.application.observable.ApplicationEvent;
import gui.model.application.observable.ApplicationEvent.ApplicationChangeState;
import gui.model.application.scenes.Scene;
import gui.model.domain.DomainModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Application class encapsualtes the target application.
 * Each application has one or more scenes (windows, dialogs, pages) and a name.
 * <br/>
 * On an application, some actions can be performed by a user. The result is
 * a sequence of events, which can be stored in this class.
 * 
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class Application extends AppObservable {
	/** The name of this application. */
	private String name;
	/** The list of scenes in this application. */
	private List<Scene<?>> scenes;
	/** Sequences of recorded events performed by the user. 
//	 * Not used yet. */
//	private List<UiEventSequence> sequences;

	/**
	 * Creates a new application object.
	 */
	public Application() {
		this.scenes = new ArrayList<Scene<?>>();
//		this.sequences = new ArrayList<UiEventSequence>();
	}

	/**
	 * @return the name of this application
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name of this application
	 */
	public void setName(String name) {
		this.name = name;
		notifyAppNameChanged();
	}
	
	/**
	 * Each scene has its domain model. This method returns all domain
	 * models of all scenes.
	 * @return list of domain models of all scenes
	 */
	public List<DomainModel> getDomainModels() {
		List<DomainModel> domainModels = new ArrayList<DomainModel>();
		for(Scene<?> s : scenes) {
			domainModels.add(s.getDomainModel());
		}
		
		return domainModels;
	}
	
	/**
	 * Each scene has its domain model. This method returns the count
	 * of all domain models of all scenes.
	 * @return count of domain models.
	 */
	public int getDomainModelCount() {
		return getDomainModels().size();
	}

	/**
	 * @return the list of all scenes in this application
	 */
	public List<Scene<?>> getScenes() {
		return scenes;
	}
	
	/**
	 * @return the count of all scenes in this application
	 */
	public int getSceneCount() {
		return scenes.size();
	}
	
	/**
	 * Adds a scene into the list of this applications' scenes.
	 * When any scene is added into the list, the listeners are notified.
	 * @param scene the scene to be added into this applications' scenes list
	 */
	public void addScene(Scene<?> scene) {
		if(!scenes.contains(scene)){
			if(getSceneCount() == 0 || name == null) {
				this.name = scene.getName();
			}
			
			scenes.add(scene);
			notifySceneAdded(scene);
		}
	}

	/**
	 * Removes the specified scene from the list of this application's scenes.
	 * When any scene is removed from the list, the listeners are notified.
	 * @param scene the scene to be removed from this applications' scenes list
	 */
	public void removeScene(Scene<?> scene) {
		scenes.remove(scene);
		notifySceneRemoved(scene);
	}

	/**
	 * Removes the scene at the specified index from the list of this application's scenes.
	 * When any scene is removed from the list, the listeners are notified.
	 * @param index the index of the scene to be removed from this applications' scenes list
	 */
	public void removeScene(int index) {
		scenes.remove(index);
		notifySceneRemoved(scenes.get(index));
	}

	/**
	 * Two scenes are equal only if their scene components are equal
	 * The list of scenes contains the specified scene only if there is
	 * the same scene component in the list.
	 * The method returns true if the list of this applications' scenes
	 * contains the specified scene (scene component).
	 * @param scene the scene whose presence in this application should be tested
	 * @return true if the specified scene is present in this application, false otherwise
	 */
	public boolean contains(Scene<?> scene) {
		for (Scene<?> s : scenes) {
			if (s.equals(scene)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Each scene encapsulates a scene component (window, dialog, frame, webpage).
	 * The method returns true if the list of this applications' scenes
	 * contains the specified scene component.
	 * @param sceneComponent the scene component component whose presence in this application should be tested
	 * @return true if the specified scene component is present in this application, false otherwise
	 */
	public boolean containsSceneComponent(Object sceneComponent) {
		return (getSceneForSceneComponent(sceneComponent) != null);
	}
	
	/**
	 * Each scene encapsulates a scene component (window, dialog, grame, webpage).
	 * The method returns the scene, which contains the specified scene compoennt 
	 * @param sceneComponent the scene component, from which the scene is to be searched for
	 * @return the scene for the specified scene component
	 */
	public Scene<?> getSceneForSceneComponent(Object sceneComponent) {
		for(Scene<?> s : scenes) {
			if(s.getSceneContainer().equals(sceneComponent)) return s;
		}
		return null;
	}

	/**
	 * Returns a scene at the specified index.
	 * @param index the index of the scene to be returned
	 * @return the scene at the specified index
	 */
	public Scene<?> getScene(int index) {
		return scenes.get(index);
	}
	
	@Override
	public String toString() {
		return this.getName() + " domain model";
	}

//	/************ RECORDER STUFF *********************/
//	/**
//	 * Not used yet.
//	 * @return the list of recorded ui event sequences
//	 */
//	public List<UiEventSequence> getSequences() {
//		return sequences;
//	}
//
//	/**
//	 * Not used yet.
//	 * @param sequences the list of recorded ui event sequences
//	 */
//	public void setSequences(List<UiEventSequence> sequences) {
//		this.sequences = sequences;
//	}
//
//	/**
//	 * Not used yet.
//	 * Records a sequence on the target UI
//	 */
//	public void recordSequence() {
//		// TODO: implement recording of a sequence, normal priority
//		throw new UnsupportedOperationException();
//	}
//
//	/**
//	 * Not used yet.
//	 * Replays the specified sequence on the target UI
//	 * @param sequence
//	 */
//	public void playSequence(UiEventSequence sequence) {
//		// TODO: implement playing a recorded sequence on the ui, normal
//		// priority
//		throw new UnsupportedOperationException();
//	}

	/************ OBSERVER STUFF ***********************/
	@Override
	public void notifySceneAdded(Scene<?> scene) {
		setChanged();
		notifyObservers(new ApplicationEvent(scene, ApplicationChangeState.ADDED));
	}

	@Override
	public void notifySceneRemoved(Scene<?> scene) {
		setChanged();
		notifyObservers(new ApplicationEvent(scene, ApplicationChangeState.REMOVED));
	}

	@Override
	public void notifySceneEdited(Scene<?> scene) {
		setChanged();
		notifyObservers(new ApplicationEvent(scene, ApplicationChangeState.EDITED));
	}

	@Override
	public void notifyAppNameChanged() {
		setChanged();
		notifyObservers(new ApplicationEvent(this.name));
	}
}
