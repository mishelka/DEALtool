package gui.model.application;

import gui.model.application.events.UiEventSequence;

import java.util.ArrayList;
import java.util.List;

public class Application {
	// TODO: finish this, priority highest
	private String name;
	private List<Scene<?>> scenes;
	private List<WindowScene> windowScenes;
	private List<UiEventSequence> sequences;

	public Application() {
		this.scenes = new ArrayList<Scene<?>>();
		this.windowScenes = new ArrayList<WindowScene>();
		this.sequences = new ArrayList<UiEventSequence>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Scene<?>> getScenes() {
		return scenes;
	}

	public List<WindowScene> getWindowScenes() {
		return windowScenes;
	}

	public void addScene(Scene scene) {
		if (scene instanceof WindowScene)
			windowScenes.add((WindowScene) scene);
		scenes.add(scene);
	}

	public void removeScene(Scene scene) {
		if (scene instanceof WindowScene)
			windowScenes.remove((WindowScene) scene);
		scenes.remove(scene);
	}

	public void removeScene(int index) {
		Scene s = scenes.get(index);
		if (s instanceof WindowScene)
			windowScenes.remove((WindowScene) s);

		scenes.remove(index);
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
		for (Scene s : scenes) {
			if (s.getSceneContainer().equals(sceneComponent)) {
				return true;
			}
		}
		return false;
	}

	public Scene getScene(int index) {
		return scenes.get(index);
	}

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
	
	@Override
	public String toString() {
		return this.getName() + " domain model";
	}
}
