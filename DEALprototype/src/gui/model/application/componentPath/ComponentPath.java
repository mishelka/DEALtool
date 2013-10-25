package gui.model.application.componentPath;

import gui.analyzer.util.ComponentFinder;
import gui.model.application.scenes.Scene;

import java.awt.Component;
import java.util.List;

/**
 * Represents the path to the component in the component tree.
 * 
 * In the recording process, when the component has no domain identifier,
 * this component path is used to identify the component in the component
 * tree.
 * 
 * The path is in the form of integer number sequence, where each number represents
 * the placement of the component in the tree.
 * 
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class ComponentPath {
	private List<Integer> componentPath;
	
	public ComponentPath(Scene<?> scene, Component component) {
		componentPath = ComponentFinder.getInstance().getPathForComponent(scene, component);
	}

	public List<Integer> getComponentPath() {
		return componentPath;
	}

	public void setComponentPath(List<Integer> componentPath) {
		this.componentPath = componentPath;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for(Integer i : componentPath) {
			sb.append(i);
			if(componentPath.indexOf(i) < componentPath.size()) {
				sb.append("/");
			}
		}
		
		return sb.toString();
	}
}
