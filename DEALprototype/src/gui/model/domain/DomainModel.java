package gui.model.domain;

import gui.analyzer.util.Util;
import gui.model.application.scenes.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DomainModel class represents a domain model of a GUI.
 * A domain model can be extracted from a Window, Dialog or any other Scene.
 * Each domain model has a <b>name</b> which is usually extracted from the scene name.
 * This name represents the name of the domain, which is described by the domain model.
 * <br/>
 * 
 * Domain model is a tree structure, which contains <b>terms</b> (Term class).
 * The root of this structure is the <b>root term</b>.
 * Each term contains domain information and a list of child terms.
 * The root term usually contains the domain information extracted from the scene component.
 * <br/>
 * 
 * Domain model also contains a reference to the scene, from which it was extracted.
 * <br/>
 * 
 * Domain models are extracted using the DEAL method, which is implemented in DomainModelGenerator class.
 * 
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class DomainModel {
	/**The root term of the Domain model.*/
	private Term root;
	/**The name of the domain model. Usually the name of the main window (the window, which first appears when the application starts).*/
	private String name;
	/** Each domain model is associated to a scene - a window or a frame - which it is created from. */
	private Scene<?> scene;
	/** A flag - for the purpose of displaying the component types in the editor.*/
	private boolean showComponentInfoTypes = false;

	/**
	 * Each domain model is created with a name, usually extracted from a scene, which the domain model is created from.
	 * @param name the domain model name or title.
	 */
	public DomainModel(String name) {
		this.name = name;
		reset();
	}

	/**
	 * Sets this model' name.
	 * @param name the name of this domain model
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name of this domain model
	 */
	public String getName() {
		return name;
	}

	/**
	 * Resets the domain model and deletes children of all terms
	 */
	private void reset() {
		if (root != null) {
			while (root.hasChildren()) {
				Term child = root.getLastChild();
				deleteChildTerms(child);
				root.removeChild(child);
			}
			root = null;
		}
	}
	
	/**
	 * Deletes all terms recursively
	 * @param term
	 */
	private void deleteChildTerms(Term term) {
		while (term.hasChildren()) {
			Term child = term.getLastChild();
			deleteChildTerms(child);
			term.removeChild(child);
		}
	}

	/**
	 * @return the root term of this domain model
	 */
	public Term getRoot() {
		return root;
	}

	/**
	 * Each domain model is extracted from an existing scene. The getScene() method returns the scene which this domain model is generated from.
	 * @return the scene which this domain model is generated from
	 */
	public Scene<?> getScene() {
		return scene;
	}
	
	/**
	 * Sets the scene, which this domain model is created from.
	 * @param scene the scene, which this domain model is generated from
	 */
	public void setScene(Scene<?> scene) {
		this.scene = scene;
	}
	
	/**
	 * Each term is created from a component. This method returns the term for a component, from which it was generated.
	 * @param component the component
	 * @return the term, which was generated from the given component
	 */
	public Term getTermForComponent(Object component) {
		return root.getTermForComponent(component);
	}
	
	/**
	 * Finds all terms with the given name. Serves for the purpose of search.
	 * @param name the name to be searched for
	 * @return the list of all terms with the given name
	 */
	public List<Term> findTermsByName(String name) {
		return root.findTermsByName(name);
	}
	
	/**
	 * Finds all terms with the given description. Serves for the purpose of search.
	 * @param description the description to be searched for
	 * @return the list of all terms with the given description
	 */
	public List<Term> findTermsByDescription(String description) {
		return root.findTermsByDescription(description);
	}
	
	/**
	 * Gets all terms located in the domain model in a list
	 * @return all terms contained in the model in a list
	 */
	public List<Term> getAllTerms() {
		return root.getAllTerms(new ArrayList<Term>());
	}
	
	/**
	 * Removes all the given terms from the model also with all children
	 * @param terms the terms, which should be removed from the model
	 */
	public void removeAll(List<Term> terms) {
		root.removeAllWithNesting(terms);
	}
	
	/**
	 * Removes all bad characters which could interfere with the DSL or ontology (such as '*', '.', '-' etc.)
	 * The term name or description can only contain letters, numbers, underscore and whitespaces, 
	 * all other characters will be removed from the term. 
	 * Regex: [^\\w\\s-]*
	 * @return true if anything was removed, false otherwise
	 */
	public void removeBadCharacters() {
		name = Util.removeBadCharacters(name);
		root.removeBadCharacters();
	}
	
	/**
	 * Removes all empty leaves in the model
	 * @return true if anything was removed, false otherwise
	 */
	public boolean removeEmptyLeafs() {
		return root.removeEmptyLeafs();
	}
	
	/**
	 * Removes multiple nestings in the model. A multiple nesting means, 
	 * that there is an empty term, which has only one child.
	 * @return true if anything was removed, false otherwise
	 */
	public boolean removeMultipleNestings() {
		return root.removeMultipleNestings();
	}

	/**
	 * If there are terms, which have only one child and the child is a leaf, 
	 * then this leaf is moved to its grandparent and its parent is removed.<br/>
	 * Parent-Grandparent-Leaf ==> Parent-Leaf
	 * @return true if anything was shifted, false otherwise
	 */
	public boolean shiftSingleChildLeafs() {
		return root.shiftSingleChildLeafs();
	}
	
	/**
	 * Removes all terms, which have the given ComponentInfoType.
	 * This method is used, when removing all functional components.
	 * @param type the ComponentInfoType type of terms, which should be removed
	 * @return true if anything was removed, false otherwise
	 */
	public boolean removeTermsOfInfoType(ComponentInfoType type) {
		return root.removeTermsOfInfoType(type);
	}

	/**
	 * Replaces the current root with the given term.
	 * @param term the term to be the new root.
	 */
	public void replaceRoot(Term term) {
		root = term;
	}

	/**
	 * Returns the value of the showComponentInfoTypes flag.
	 * @return the value of the showComponentInfoTypes flag.
	 */
	public boolean isShowComponentInfoTypes() {
		return showComponentInfoTypes;
	}
	
	/**
	 * Returns the value of the showComponentInfoTypes flag.
	 * @param showComponentInfoTypes the value of the showComponentInfoTypes flag.
	 */
	public void setShowComponentInfoTypes(boolean showComponentInfoTypes) {
		this.showComponentInfoTypes = showComponentInfoTypes;
	}
	
	@Override
	public String toString() {
		return root.toPlainText(0);
	}
}