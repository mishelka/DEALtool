package gui.analyzer.statistics;

import gui.analyzer.util.ComponentFinder;
import gui.analyzer.util.Logger;
import gui.model.application.scenes.Scene;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Experimental statistics for the purpose of domain usability analysis. <br>
 * Performs the following statistic calculations on the domain model:
 * <ul>
 * <li>statistics of the textual components</li>
 * <li>statistics of the functional components</li>
 * <li>complexity of the user interface based on the number of components and
 * containers</li>
 * </ul>
 * <br>
 * <h3>Statistics of the textual components</h3> Works with the textual
 * components in the domain model. Counts and writes the following statistics to
 * the console:
 * <ul>
 * <li>The number of textual components without name</li>
 * <li>The number of textual components without description</li>
 * <li>The number of textual components both without name and description</li>
 * </ul>
 * If there are any textual components both without any name and description, it
 * could be applying to a usability issue.
 * 
 * <h3>Statistics of the functional components</h3> It is very similar to the
 * previous one, however works with functional components. If there are any
 * functional components both without any name and description, it could be
 * applying to a usability issue.
 * 
 * <h3>Statistics of the functional components</h3>
 * Works with containers and components in the domain model.
 * Counts and writes the following statistics to
 * the console:
 * <ul>
 * <li>The number of all components</li>
 * <li>The number of all containers</li>
 * <li>The number of components+containers</li>
 * <li>The complexity of the target user interface</li>
 * </ul>
 * The complexity formula is as follows: <br>
 * COMPLEXITY = Container count / Component count <br>
 * <br>
 * If the complexity is too high (there is too many containers and too few
 * components) compared to the number of all components, it could be referring
 * to a usability issue.
 * Also, if the complexity is very low (there are too many components 
 * and too few containers) compared to the number of all components,
 * it could also be a problem.
 * 
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
@SuppressWarnings("rawtypes")
public class Statistics {
	private DomainModel domainModel;
	private Scene scene;
	
	List<List<Component>> infoTypeComponentGroups = new ArrayList<List<Component>>();
	List<ComponentInfoType> componentInfoTypes;
	
	public Statistics(DomainModel domainModel, Scene scene) {
		this.domainModel = domainModel;
		this.scene = scene;
		componentInfoTypes = ComponentInfoType.getInfoTypes();
		
		for(int i = 0; i < componentInfoTypes.size(); i++) {
			infoTypeComponentGroups.add(new ArrayList<Component>());
		}
		
		loadComponentsIntoTypeList();
	}
	
	/**
	 * Loads all components into the type list.
	 * The list contains lists of components according to their type.
	 * The types can be found in the {@link gui.model.ComponentInfoType}.
	 */
	private void loadComponentsIntoTypeList() {
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
	
	/**
	 * Adds the given component into the corresponding type group based on the given infoType
	 * @param c the component to be added into the type group
	 * @param infoType the info type of the given component
	 * @see gui.model.ComponentInfoType
	 */
	private void addComponentIntoTypeGroup(Component c, ComponentInfoType infoType) {
		infoTypeComponentGroups.get(componentInfoTypes.indexOf(infoType)).add(c);
	}
	
	/**
	 * Performs the following statistic calculations on the domain model:
	 * <ul>
	 * <li>statistics of the textual components</li>
	 * <li>statistics of the functional components</li>
	 * <li>complexity of the user interface based on the number of components and containers</li>
	 * </ul>
	 */
	public void performCalculations() {
		Logger.logError(domainModel.getName() + ":");
		
		for(ComponentInfoType cit : componentInfoTypes) {
			Logger.logError(cit + " = " + infoTypeComponentGroups.get(componentInfoTypes.indexOf(cit)).size());
		}
		
		
		performCalculationsOnTextualComponents();
		performCalculationsOnFunctionalComponents();
		
		performCalculationsWithContainers();
	}
	
	/**
	 * Works with containers and components in the domain model.
	 * Counts and writes the following statistics to the console:
	 * <ul>
	 * <li>The number of all components</li>
	 * <li>The number of all containers</li>
	 * <li>The number of components+containers</li>
	 * <li>The complexity of the target user interface</li>
	 * </ul>
	 * The complexity formula is as follows: <br>
	 * Container count / component count = complexity
	 * <br>
	 * <br>
	 * If the complexity is too high (there is too many containers and to few
	 * components, it could be referring to a usability issue.
	 */
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
		Logger.logError("Container & component count: " + componentCount + containerCount);
		
		double complexity = 0;
		if(componentCount == 0) {
			complexity = Double.POSITIVE_INFINITY;
		} else {
			complexity = (double) containerCount / (double) componentCount;
		}
		
		Logger.logError("Container count / component count = complexity = " + complexity);
	}
	
	/**
	 * Works with the textual components in the domain model.
	 * Counts and writes the following statistics to the console:
	 * <ul>
	 * <li>The number of textual components without name</li>
	 * <li>The number of textual components without description</li>
	 * <li>The number of textual components both without name and description</li>
	 * </ul>
	 * If there are any textual components both without any name and description, 
	 * it could be applying to a usability issue.
	 * If there are no textual components, then the statistics is not performed.
	 */
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
					Logger.logError(t.getComponentClassSimpleName());
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
	
	/**
	 * Works with the functional components in the domain model.
	 * Counts and writes the following statistics to the console:
	 * <ul>
	 * <li>The number of functional components without name</li>
	 * <li>The number of functional components without description</li>
	 * <li>The number of functional components both without name and description</li>
	 * </ul>
	 * If there are any functional components both without any name and description, 
	 * it could be applying to a usability issue.
	 * If there are no functional components, then the statistics is not performed.
	 */
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
	
	/**
	 * Returns a list of components of the given info type.
	 * @param infoType the info type of the components to be collected
	 * @return the list of components of the given info type
	 */
	private List<Component> getComponentsForInfoType(ComponentInfoType infoType) {
		return infoTypeComponentGroups.get(componentInfoTypes.indexOf(infoType));
	}
}
