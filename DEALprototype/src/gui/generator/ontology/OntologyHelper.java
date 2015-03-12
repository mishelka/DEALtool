package gui.generator.ontology;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

public class OntologyHelper {
	
	private OWLOntologyManager manager;
	private OWLOntology ontology = null;
	private IRI ontologyIRI;
	private OWLDataFactory dataFactory;
	private PrefixManager pm;
	private List<String> usedClasses; //prevent duplicated name of classes
	
	public OntologyHelper(String filePath, boolean newOntology) {
		manager = OWLManager.createOWLOntologyManager();
		dataFactory = manager.getOWLDataFactory();
		usedClasses = new ArrayList<String>();
		if (filePath != null && new File(filePath).exists() && !newOntology) {
			File file = new File(filePath);
			try {
				ontology = manager.loadOntologyFromOntologyDocument(file);
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				ontology = manager.createOntology(IRI.create(new File(filePath)));
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ontologyIRI = ontology.getOntologyID().getOntologyIRI();
		pm = new DefaultPrefixManager(getOntologyIRI()+"#");
	}
	
        
	public String getOntologyIRI() {
		return ontologyIRI.toString();
	} 
	
	public OWLClass getClassByName(String name) {
		return dataFactory.getOWLClass(":"+name, pm);
	}
	
	public OWLOntology getOntology() {
		return ontology;
	}
	
	public OWLClass createClass(String name) {
		return dataFactory.getOWLClass(":"+name, pm);
	}
	
	public OWLDataFactory getDataFactory() {
		return dataFactory;
	}
	
	public OWLClass createClassInHierarchy(String name, OWLClass superClass) {
		OWLClass owlClass = createClass(name);
		createAndAddSubClassAssertionAxiom(superClass, owlClass);
		return owlClass;
	}
	
	public OWLNamedIndividual createIndividual(String name) {
		return dataFactory.getOWLNamedIndividual(":"+name, pm); 
	}
	
	public OWLObjectProperty createObjectProperty(String name) {
		return dataFactory.getOWLObjectProperty(":"+name, pm);
	}
	
	public OWLDataProperty createDataProperty(String name) {
		return dataFactory.getOWLDataProperty(":"+name, pm);
	}
	
	public boolean createAndAddObjectPropertyAxiom(OWLObjectProperty objectProperty, OWLNamedIndividual subject, OWLNamedIndividual object) {
		OWLObjectPropertyAssertionAxiom axiom = dataFactory.getOWLObjectPropertyAssertionAxiom(objectProperty, subject, object);
		manager.addAxiom(ontology, axiom);
		return true;
	}
	
	public boolean createAndAddDisjointClassAxiom(List<OWLClass> classes) {
		OWLDisjointClassesAxiom axiom = dataFactory.getOWLDisjointClassesAxiom(new HashSet<OWLClass>(classes));
		manager.addAxiom(ontology, axiom);
		return true;
	}
	
	public boolean createAndAddMutuallyNotExclusiveAxiom(List<OWLClass> classes) {
		OWLObjectProperty objectProperty = createObjectProperty("mutuallyNotExclusiveWith");
		if (classes.isEmpty()) return false;
		for (int i=0; i<classes.size(); i++) {
			OWLClassExpression firstClassExpression = dataFactory.getOWLObjectSomeValuesFrom(objectProperty, classes.get(i));
			for (int j=0; j<classes.size(); j++) {
				if (i==j) continue;
				OWLEquivalentClassesAxiom axiom = dataFactory.getOWLEquivalentClassesAxiom(classes.get(j), firstClassExpression);
				manager.addAxiom(ontology, axiom);
			}
		}
		return true;
	}
	
	public boolean createAndAddDataPropertyAxiom(final OWLDataProperty objectProperty, OWLClass subject, String value) {
		//OWLDataPropertyAssertionAxiom axiom = dataFactory.getOWLDataPropertyAssertionAxiom(objectProperty, subject, value);
		OWLClassExpression expression = dataFactory.getOWLDataHasValue(objectProperty, dataFactory.getOWLLiteral(value));
		//manager.add
		OWLEquivalentClassesAxiom axiom = dataFactory.getOWLEquivalentClassesAxiom(subject, expression);

		manager.addAxiom(ontology, axiom);
		return true;
	}
	
	public boolean createAndAddClassAserrtionAxiom(OWLClass owlClass, OWLNamedIndividual individual) {
		OWLClassAssertionAxiom classAxiom = dataFactory.getOWLClassAssertionAxiom(owlClass, individual);
		manager.addAxiom(ontology, classAxiom);
		return true;
	}
	
	public boolean createAndAddSubClassAssertionAxiom(OWLClass superClass, OWLClass subClass) {
		OWLSubClassOfAxiom subClassOfAxiom = dataFactory.getOWLSubClassOfAxiom(subClass, superClass);
		manager.addAxiom(ontology, subClassOfAxiom);
		return true;
	}
	
	
	public boolean saveOntology(String filePath) {
		File file = new File(filePath);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			manager.saveOntology(ontology, new StreamDocumentTarget(fos));
			fos.close();
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
			return false;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public String convertToClassName(String string) {
		String adjustedString = string.replaceAll("[-+.^:,\\s\"\\'\\]\\[\\/\\\\]",""); 
		if (usedClasses.contains(adjustedString)) {
			adjustedString += "_";
		}
		usedClasses.add(adjustedString);
		return adjustedString;
		//if(string.trim().isEmpty()) return "";
		//return new String(string.substring(0, string.indexOf(" ")));
	}
	
	
}
