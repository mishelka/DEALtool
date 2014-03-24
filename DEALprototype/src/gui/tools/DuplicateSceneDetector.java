package gui.tools;

import gui.analyzer.util.Util;
import gui.editor.DomainModelEditor;
import gui.model.application.scenes.Scene;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;

import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

/**
 * Detects duplicate scenes in the list of stored scenes or domain models.
 * Usually, each window/dialog is created as a new instance, 
 * therefore it is difficult to be sure if the windows are the same or not.
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk 
 */
public class DuplicateSceneDetector {
	
	/**
	 * Detects similar window in the list of stored scenes/domain models. 
	 * If the window is the same or at least 90% similar to the one in editor, 
	 * returns the corresponding scene, otherwise returns null.
	 * @param window the window to be detected
	 * @param windowDomainModel the domainModel of the window to be detected
	 * @return the Scene matching the window if the percentage of match is 100%,
	 * null otherwise
	 */
	public Scene<?> detect(Window window, DomainModel windowDomainModel) {
		if(windowDomainModel == null) return null;
		for (DomainModel dm : DomainModelEditor.getInstance().getDomainModels()) {
			if(dm != null && dm.getScene() != null && dm.getScene().getSceneContainer() != null) {
				if (dm.getScene().getSceneContainer() instanceof Window) {
					//if the window and the scene container are same instances, return the scene
					if(dm.getScene().getSceneContainer().equals(window)) {
						return dm.getScene();
					}
					int matchPerc = compareModels(dm, windowDomainModel);
					//if the dialog is the same to the one in editor, return true, otherwise return false
					if (matchPerc >= 90)
						return dm.getScene();
				}
			}
		}
		return null;
	}

	/**
	 * Compares two domain models for all the strings they contain.
	 * @param dm1 First domain model to be compared
	 * @param dm2 Second domain model to be compared
	 * @return Percentage of matching string elements in both models.
	 */
	private int compareModels(DomainModel dm1, DomainModel dm2) {
		List<String> list1 = getAllTermsAsStrings(dm1);
		List<String> list2 = getAllTermsAsStrings(dm2);
		list1.add(dm1.getName());
		list2.add(dm2.getName());

		int size = Math.max(list1.size(), list2.size());
		if (size == 0)
			return 0;

		//in the list1 only the items matching items in the list2 will remain.
		//The percentage of matching elements will be returned.
		list1.retainAll(list2);

		int matches = list1.size();
		
		return 100 * matches / size;
	}
	

	/**
	 * Gets the list of the names of all the terms, located in the given domain model.
	 * @param domainModel the domain model the names to be selected from
	 * @return the names of all terms located in the given model
	 */
	private List<String> getAllTermsAsStrings(DomainModel domainModel) {
		List<String> allTermsAsStrings = new ArrayList<String>();
		
		Term root = domainModel.getRoot();
		if(domainModel == null || root == null) return allTermsAsStrings;
		
		List<Term> allTerms = root.getAllTerms(new ArrayList<Term>());
		
		for(Term t : allTerms) {
			String name = t.getName();
			if(Util.isEmpty(name)) {
				allTermsAsStrings.add(t.getName());
			}
			String desc = t.getDescription();
			if(Util.isEmpty(desc)) {
				allTermsAsStrings.add(t.getDescription());
			}
		}
		
		return allTermsAsStrings;
	}
}
