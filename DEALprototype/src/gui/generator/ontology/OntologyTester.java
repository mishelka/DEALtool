package gui.generator.ontology;

import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.model.domain.relation.RelationType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

public class OntologyTester {
	
	private static int offset = 1;
	
	private static OntologyHelper ontoHelper;
	private static OWLClass currentClass;
	private static boolean generated = false;//generate ontology only once
	//data properties for class
	static OWLDataProperty descriptionProperty;
	static OWLDataProperty nameProperty;
	static OWLDataProperty componentClassProperty;
	static OWLDataProperty labelProperty;
	
	public static void main(String args[]) {
		ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
		
		Runnable task = new Runnable() {
			@Override 
			public void run() {
//				performTest(DomainModelEditor.getInstance().getDomainModels());
			}	
		};
		
		worker.schedule(task, 5, TimeUnit.SECONDS);
		//performTest(DomainModelEditor.getDomainModels());
		/*OntologyHelper ontoHelper = new OntologyHelper("d:\\Projects\\EclipseProjects\\DEALprototype\\bin\\owl\\hiearchyTest.owl", true);
		OWLClass parentClass = ontoHelper.createClass(ontoHelper.convertToClassName("Parent"));
        OWLClass childClass = ontoHelper.createClass(ontoHelper.convertToClassName("Child"));
        OWLClass childChild = ontoHelper.createClass(ontoHelper.convertToClassName("Childchild"));
        ontoHelper.createAndAddSubClassAssertionAxiom(childClass, childChild);
        ontoHelper.createAndAddSubClassAssertionAxiom(parentClass, childClass);
        OWLClass parent2 = ontoHelper.createClass(ontoHelper.convertToClassName("Parent2"));
        OWLClass child2class = ontoHelper.createClass(ontoHelper.convertToClassName("Child"));
        ontoHelper.createAndAddSubClassAssertionAxiom(parent2, child2class);
        ontoHelper.saveOntology("d:\\Projects\\EclipseProjects\\DEALprototype\\bin\\owl\\hiearchyTest.owl");*/
	}

	/**
	 * Generates ontology from domain model and save it to defined file.
	 * @param models - domain model to create ontology from
	 * @param file - where to save ontology
	 */
	public static void generateOntology(List<DomainModel> models, File file) {
		/*if (generated) return;
		generated = true;*/
		System.out.println("Domain models count = " + models.size());
		String saveFilePath = file.getAbsolutePath();
		ontoHelper = new OntologyHelper(saveFilePath, true);
		//OWLObjectProperty objectProperty = ontoHelper.createObjectProperty("hasSon");
		descriptionProperty = ontoHelper.createDataProperty("description");
		nameProperty = ontoHelper.createDataProperty("name");
		componentClassProperty = ontoHelper.createDataProperty("componentClass");
		labelProperty = ontoHelper.createDataProperty("label");
		
		for (DomainModel dm : models) {
			hiearchyAllChilds(dm.getAllTerms().get(0), ontoHelper.convertToClassName(dm.getRoot().toString()));
		}
		ontoHelper.saveOntology(saveFilePath);
	}
	
	public static void performTest(List<DomainModel> models) {
		if (generated) return;
		generated = true;
		DomainModel model = models.get(0);
		System.out.println("Model is " + model);
		
		OntologyHelper ontoHelper = new OntologyHelper("d:\\Projects\\EclipseProjects\\DEALprototype\\bin\\owl\\ontology.owl", false);
		System.out.println("Ontology IRI : " + ontoHelper.getOntologyIRI());
		OWLClass accountClass = ontoHelper.getClassByName("Account");
		System.out.println("Account superclass owl class : " + accountClass.getSuperClasses(ontoHelper.getOntology()).toString());
		//creating individual
		OWLNamedIndividual subject = ontoHelper.createIndividual("Trip");
		OWLNamedIndividual object = ontoHelper.createIndividual("John");
		OWLObjectProperty objectProperty = ontoHelper.createObjectProperty("hasSon");
		
		ontoHelper.createAndAddObjectPropertyAxiom(objectProperty, subject, object);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (DomainModel dm : models) {
			printAllChilds(dm.getRoot());
			
			for (Term term : dm.getAllTerms()) {
				Term testTerm = term;
				System.out.println("TestTerm = " + testTerm);
				while(testTerm.getChildrenCount()>0) {
					testTerm = testTerm.getChildAt(0);
					System.out.println("TestTermIteration = " + testTerm);
				}
				//create owl class
				Term parentTerm = testTerm.getParent();
				if (parentTerm!=null && parentTerm.toString()!=null) { 
					OWLClass termClass = ontoHelper.createClass(ontoHelper.convertToClassName(parentTerm.toString()));
					System.out.println("parentTerm = " + testTerm.getParent().toString());
					for(Term child : parentTerm.getChildren()) {
						System.out.println("siblingTerm = " + child);
						ontoHelper.createAndAddClassAserrtionAxiom(termClass, ontoHelper.createIndividual(ontoHelper.convertToClassName(child.toString())));
					}
				}
			}
			 
		}
		ontoHelper.saveOntology("d:\\Projects\\EclipseProjects\\DEALprototype\\bin\\owl\\test.owl");
	}
	
	private static void hiearchyAllChilds(Term term, String className) {
		//exclude also deal file chooser
		if (term.getChildrenCount()==0 || (term.getName()!=null && term.getName().equals("DealFileChooser"))) {
			return;
		}
		else {
			System.out.println("CurrentClass = " + term.getName());
			currentClass = ontoHelper.createClass(className);
			//axioms for parent
			assignStandardDataProperties(currentClass, term);

			HashMap<Term, String> termsWithClasses = new HashMap<Term, String>();
			//String[] classNames = new String[term.getChildrenCount()];
			//int index = 0;
			List<OWLClass> classes = new ArrayList<OWLClass>();
			for (Term child : term.getChildren()) {
				System.out.println("Child class = " + child.getName());
				if (child != null && child.getName() != null && !child.isHidden()) {
					String newClassName = ontoHelper.convertToClassName(child.getName()); 
					termsWithClasses.put(child, newClassName);
					OWLClass childClass = ontoHelper.createClass(newClassName); 
					classes.add(childClass);
					//axioms
					ontoHelper.createAndAddSubClassAssertionAxiom(currentClass, childClass);
					assignStandardDataProperties(childClass, child);
				}
			}
			if (term.getRelation() == RelationType.MUTUALLY_EXCLUSIVE) {
				ontoHelper.createAndAddDisjointClassAxiom(classes);
			} else if (term.getRelation() == RelationType.MUTUALLY_NOT_EXCLUSIVE) {
				ontoHelper.createAndAddMutuallyNotExclusiveAxiom(classes);
			}
			for (Term child : termsWithClasses.keySet()) {
				hiearchyAllChilds(child, termsWithClasses.get(child));
			}
		}
	}
	
	private static void assignStandardDataProperties(OWLClass cl, Term term) {
		if (term.getDescription()!=null) ontoHelper.createAndAddDataPropertyAxiom(descriptionProperty, cl, term.getDescription());
		if (term.getName()!=null) ontoHelper.createAndAddDataPropertyAxiom(nameProperty, cl, term.getName());
		if (term.getComponentClass()!=null && term.getComponentClassSimpleName()!=null) ontoHelper.createAndAddDataPropertyAxiom(componentClassProperty, cl, term.getComponentClassSimpleName());
		if (term.getLabelForComponent()!=null) ontoHelper.createAndAddDataPropertyAxiom(labelProperty, cl, term.getLabelForComponent().getText());
		
	} 
	
	private static void printAllChilds(Term term) {
		//System.out.println("printterm = " + term.toString());
		if (term.getChildrenCount()==0) {
			return;
		}
		else {
			offset++;
			for (Term child : term.getChildren()) {
				System.out.println(getOffsetString(offset) + child.toString());
				printAllChilds(child);
			}
			offset--;
		}
	}
	
	private static String getOffsetString(int offset) {
		System.out.println("offset = " + offset);
		String offsetString = "";
		for (int i=0; i<offset; i++) {
			offsetString += "\t";
		}
		return offsetString;
	}
	
}
