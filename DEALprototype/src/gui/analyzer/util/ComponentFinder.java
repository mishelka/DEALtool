package gui.analyzer.util;

import gui.model.application.componentPath.ComponentPath;
import gui.model.application.scenes.Scene;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

/**
 * Offers different methods for searching in the component tree. - obtaining a
 * scene containing the given component, from the given list of scenes -
 * obtaining a component path for the given component - searching a component in
 * a tree according to various parameters - transformation of component tree to
 * a list of components
 */
public class ComponentFinder {

	/** ComponentFinder singleton instance. */
	private static ComponentFinder instance;

	private ComponentFinder() {
	}

	/**
	 * @return ComponentFinder singleton instance.
	 */
	public static ComponentFinder getInstance() {
		if (instance == null) {
			instance = new ComponentFinder();
		}
		return instance;
	}

	/**
	 * Each Scene contains a reference to the scene container, which is of type
	 * Object by default. This method selects the sceneContainer, which is of
	 * type Object by default, and if it is of type Component, then returns the
	 * sceneContainer as Component. If it is null or the sceneContainer is not
	 * of type Component, returns null.
	 * 
	 * @param scene
	 *            a scene to select the sceneContainer from.
	 * @return The sceneContainer of type Component, if there is such, null
	 *         otherwise.
	 */
	public Component getSceneContainerAsComponent(Scene<?> scene) {
		Object sceneContainer = scene.getSceneContainer();
		if (sceneContainer instanceof Component)
			return (Component) sceneContainer;
		return null;
	}

	/**
	 * Finds a scene, which contains the given component.
	 * 
	 * @param scenes
	 *            a list of all scenes where the component should be searched
	 *            in.
	 * @param component
	 *            the component which should be found in a scene.
	 * @return a scene from the list of scenes which contains the given
	 *         component.
	 */
	public Scene<?> getSceneContainingComponent(List<Scene<?>> scenes,
			Object component) {
		for (Scene<?> s : scenes) {
			Component sceneContainer = getSceneContainerAsComponent(s);
			if (sceneContainer != null) {
				List<Component> components = toComponentList(sceneContainer);
				if (components.contains(component))
					return s;
			}
		}
		return null;
	}

	public Component getComponentFromPath(Scene<?> scene,
			ComponentPath componentPath) {
		Component component = getSceneContainerAsComponent(scene);

		if (component == null)
			return null;

		for (Integer i : componentPath.getComponentPath()) {
			if (component instanceof Container) {
				Component temp = ((Container) component).getComponents()[i];
				if (temp != null) {
					component = ((Container) component).getComponents()[i];
				} else
					return null;
			} else
				return null;
		}
		return component;
	}

	public List<Integer> getPathForComponent(Scene<?> scene,
			Component component) {
		Component sceneComponent = getSceneContainerAsComponent(scene);

		if (sceneComponent == null)
			return null;

		List<Component> allComponents = (List<Component>) toComponentList(sceneComponent);
		if (!allComponents.contains(component))
			return null;

		List<Integer> path = new ArrayList<Integer>();

		// if the scene is a root component, then return path with only one path
		// item - root (0).
		path.add(0);
		if (component.equals(sceneComponent)) {
			return path;
		}

		if (sceneComponent instanceof Container) {
			Component[] components = ((Container) sceneComponent)
					.getComponents();
			getPathForComponent(components, path, component);
			return path;
		}

		return null;
	}

	private void getPathForComponent(Component[] components,
			List<Integer> path, Component component) {
		for (int i = 0; i < components.length; i++) {
			if (component.equals(components[i])) {
				path.add(i);
				return;
			} else {
				List<Component> componentsOfComponent = (List<Component>) toComponentList(components[i]);
				if (componentsOfComponent.contains(component)) {
					path.add(i);
					if (components[i] instanceof Container) {
						getPathForComponent(
								((Container) components[i]).getComponents(),
								path, component);
						return;
					}
				}
			}
		}

		path.removeAll(path);
		// if the path contains -1, then the component was not found in the
		// scene - it has no path and then it is not possible to play anything.
		path.add(-1);
	}
	
	@SuppressWarnings("rawtypes")
	public List<Component> toComponentList(Scene scene) {
		Component sceneContainer = getSceneContainerAsComponent(scene);
		if (sceneContainer != null) {
			List<Component> components = toComponentList(sceneContainer);
			return components;
		}
		return null;
	}

	/**
	 * Retrieves the list of all subcomponents of the given component.
	 * 
	 * @param component
	 *            the component, from which the list of all subcomponent should
	 *            be retrieved.
	 * @return a list of all subcomponents of the given component.
	 */
	public List<Component> toComponentList(Component component) {
		List<Component> componentList = new ArrayList<Component>();
		if (component instanceof Container) {
			Component[] components = ((Container) component).getComponents();
			componentsToList(components, componentList);
		}
		return componentList;
	}

	private void componentsToList(Component[] components,
			List<Component> componentList) {
		for (Component component : components) {
			if (component instanceof Container) {
				Container container = (Container) component;
				if (component instanceof JComponent) {
					componentList.add(component);
				}
				componentsToList(container.getComponents(), componentList);
			}
		}
	}
}
