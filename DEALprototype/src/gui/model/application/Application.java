package gui.model.application;

import gui.analyzer.observable.AppObservable;
import gui.analyzer.observable.ApplicationEvent;
import gui.analyzer.observable.ApplicationEvent.ApplicationChangeState;
import gui.model.application.events.UiEventSequence;
import gui.model.application.scenes.Scene;
import gui.model.domain.DomainModel;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class Application extends AppObservable {
	// TODO: finish this, priority highest
	private String name;
	private List<Scene<?>> scenes;
	private List<UiEventSequence> sequences;

	public Application() {
		this.scenes = new ArrayList<Scene<?>>();
		this.sequences = new ArrayList<UiEventSequence>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		notifyAppNameChanged();
	}
	
	public List<DomainModel> getDomainModels() {
		List<DomainModel> domainModels = new ArrayList<DomainModel>();
		for(Scene s : scenes) {
			domainModels.add(s.getDomainModel());
		}
		
		return domainModels;
	}
	
	public int getDomainModelCount() {
		return getDomainModels().size();
	}

	public List<Scene<?>> getScenes() {
		return scenes;
	}
	
	public int getSceneCount() {
		return scenes.size();
	}
	
	public void addScene(Scene scene) {
		if(!scenes.contains(scene)){
			if(getSceneCount() == 0 || name == null) {
				this.name = scene.getName();
			}
			
			scenes.add(scene);
			notifySceneAdded(scene);
		}
	}

	public void removeScene(Scene scene) {
		scenes.remove(scene);
		notifySceneRemoved(scene);
	}

	public void removeScene(int index) {
		scenes.remove(index);
		notifySceneRemoved(scenes.get(index));
	}

	public boolean contains(Scene scene) {
		for (Scene s : scenes) {
			if (s.getSceneContainer().equals(scene.getSceneContainer())) {
				return true;
			}
		}
		return false;
	}

	public boolean containsSceneComponent(Object sceneComponent) {
		return (getSceneForSceneComponent(sceneComponent) != null);
	}
	
	public Scene getSceneForSceneComponent(Object sceneComponent) {
		for(Scene s : scenes) {
			if(s.getSceneContainer().equals(sceneComponent)) return s;
		}
		return null;
	}

	public Scene getScene(int index) {
		return scenes.get(index);
	}
	
	@Override
	public String toString() {
		return this.getName() + " domain model";
	}

	/************ RECORDER STUFF *********************/
	public List<UiEventSequence> getSequences() {
		return sequences;
	}

	public void setSequences(List<UiEventSequence> sequences) {
		this.sequences = sequences;
	}

	public void recordSequence() {
		// TODO: implement recording of a sequence, normal priority
		throw new UnsupportedOperationException();
	}

	public void playSequence(UiEventSequence sequence) {
		// TODO: implement playing a recorded sequence on the ui, normal
		// priority
		throw new UnsupportedOperationException();
	}

	/************ OBSERVER STUFF ***********************/
	@Override
	public void notifySceneAdded(Scene scene) {
		setChanged();
		notifyObservers(new ApplicationEvent(scene, ApplicationChangeState.ADDED));
	}

	@Override
	public void notifySceneRemoved(Scene scene) {
		setChanged();
		notifyObservers(new ApplicationEvent(scene, ApplicationChangeState.REMOVED));
	}

	@Override
	public void notifySceneEdited(Scene scene) {
		setChanged();
		notifyObservers(new ApplicationEvent(scene, ApplicationChangeState.EDITED));
	}

	@Override
	public void notifyAppNameChanged() {
		setChanged();
		notifyObservers(new ApplicationEvent(this.name));
	}
}
