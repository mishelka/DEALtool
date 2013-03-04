package gui.model.application.componentPath;

import gui.analyzer.util.ComponentFinder;
import gui.model.application.Scene;

import java.awt.Component;
import java.util.List;

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
