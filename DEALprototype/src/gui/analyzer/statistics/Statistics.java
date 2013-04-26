package gui.analyzer.statistics;

import gui.analyzer.util.ComponentFinder;
import gui.analyzer.util.Logger;
import gui.model.application.Scene;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

public class Statistics {
	private DomainModel domainModel;
	private Scene scene;
	
	List<List<Component>> infoTypeComponentGroups = new ArrayList<List<Component>>();
	List<ComponentInfoType> componentInfoTypes;
	
	public Statistics(DomainModel domainModel, Scene scene) {
		this.domainModel = domainModel;
		this.scene = scene;
		componentInfoTypes = ComponentInfoType.getInfoTypes();
		
		for(ComponentInfoType infoType : componentInfoTypes) {
			infoTypeComponentGroups.add(new ArrayList<Component>());
		}
		
		loadComponentsIntoTypeList();
	}
	
	public void loadComponentsIntoTypeList() {
		List<Component> allSceneComponents = ComponentFinder.getInstance().toComponentList(scene);
		
		for(Component c : allSceneComponents) {
			Term t = domainModel.getTermForComponent(c);
			ComponentInfoType cit = ComponentInfoType.UNKNOWN;
			
			if(t != null) {
				if(t.getComponentInfoType() != null) {
					cit = t.getComponentInfoType();
				}
			}
			
			addComponentIntoTypeGroup(c, cit);
		}
	}
	
	private void addComponentIntoTypeGroup(Component c, ComponentInfoType infoType) {
		infoTypeComponentGroups.get(componentInfoTypes.indexOf(infoType)).add(c);
	}
	
	public void performCalculations() {
		Logger.logError(domainModel.getName() + ":");
		
		for(ComponentInfoType cit : componentInfoTypes) {
			Logger.logError(cit + " = " + infoTypeComponentGroups.get(componentInfoTypes.indexOf(cit)).size());
		}
		
		
		performCalculationsOnTextualComponents();
		performCalculationsOnFunctionalComponents();
		
		performCalculationsWithContainers();
	}
	
	private void performCalculationsWithContainers() {
		int containerCount = getComponentsForInfoType(ComponentInfoType.CONTAINERS).size();
		int componentCount = 0;
		for(ComponentInfoType infoType : componentInfoTypes) {
			if(!infoType.equals(ComponentInfoType.CONTAINERS)) {
				componentCount += getComponentsForInfoType(infoType).size();
			}
		}
		
		int allCompCount = containerCount + componentCount;
		
		double compCountPerc = 100 * componentCount / allCompCount;
		double contCountPerc = 100 * containerCount / allCompCount;
		
		Logger.logError("========COMPLEXITY========");
		Logger.logError("Component count: " + componentCount + " = "  + compCountPerc + "%");
		Logger.logError("Container count: " + containerCount + " = " + contCountPerc + "%");
		
		double complexity = (double) containerCount / (double) (componentCount == 0 ? 1 : componentCount);
		Logger.logError("Component count / container count = complexity = " + complexity);
	}
	
	private void performCalculationsOnTextualComponents() {
		List<Component> textualComponents = getComponentsForInfoType(ComponentInfoType.TEXTUAL);
		
		int withoutName = 0;
		int withoutNameAndDesc = 0;
		int withoutNameAndDescAndIcon = 0;
		for(Component c : textualComponents) {			
			Term t = domainModel.getTermForComponent(c);

			if(!t.hasName()) {
				withoutName++;
				if(!t.hasDescription()) {
					withoutNameAndDesc++;
					Logger.logError(t.getComponent().getClass().getName());
					if(!t.hasIcon()) {
						withoutNameAndDescAndIcon++;
					}
				}
			}
		}
		
		int size = textualComponents.size(); 
		
		if(size != 0) {
			double withoutNamePerc = 100 * withoutName / size;
			double withoutDescPerc = 100 * withoutNameAndDesc / size;
			double withoutIconPerc = 100 * withoutNameAndDescAndIcon / size;
			
			Logger.logError("========TEXTUAL: " + size + "========");
			Logger.logError("Without name: " + withoutName + " = " + withoutNamePerc + "%");
			Logger.logError("Without desc: " + withoutNameAndDesc + " = " + withoutDescPerc + "%");
			Logger.logError("Without icon: " + withoutNameAndDescAndIcon + " = " + withoutIconPerc + "%");
		} else {
			Logger.logError("There are no textual components.");
		}
	}
	
	private void performCalculationsOnFunctionalComponents() {
		List<Component> functionalComponents = getComponentsForInfoType(ComponentInfoType.FUNCTIONAL);
		
		int withoutName = 0;
		int withoutNameAndDesc = 0;
		int withoutNameAndDescAndIcon = 0;
		for(Component c : functionalComponents) {			
			Term t = domainModel.getTermForComponent(c);

			if(!t.hasName()) {
				withoutName++;
				if(!t.hasDescription()) {
					withoutNameAndDesc++;
					if(!t.hasIcon()) {
						withoutNameAndDescAndIcon++;
					}
				}
			}
		}
		
		int size = functionalComponents.size();
		
		if(size != 0) {
			double withoutNamePerc = 100 * withoutName / size;
			double withoutDescPerc = 100 * withoutNameAndDesc / size;
			double withoutIconPerc = 100 * withoutNameAndDescAndIcon / size;
			
			Logger.logError("========FUNCTIONAL: " + size + "========");
			Logger.logError("Without name: " + withoutName + " = " + withoutNamePerc + "%");
			Logger.logError("Without desc: " + withoutNameAndDesc + " = " + withoutDescPerc + "%");
			Logger.logError("Without icon: " + withoutNameAndDescAndIcon + " = " + withoutIconPerc + "%");
		} else {
			Logger.logError("There are no functional componnets");
		}
	}
	
	
	private List<Component> getComponentsForInfoType(ComponentInfoType infoType) {
		return infoTypeComponentGroups.get(componentInfoTypes.indexOf(infoType));
	}

	public DomainModel getDomainModel() {
		return domainModel;
	}
	
	public void setDomainModel(DomainModel domainModel) {
		this.domainModel = domainModel;
	}
	
	public Scene getScene() {
		return scene;
	}
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}
}
