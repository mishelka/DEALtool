package gui.model;

import gui.analyzer.Recorder;
import gui.analyzer.util.ComponentFinder;
import gui.model.Extractor.ExtractionException;
import gui.model.application.Scene;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates a domain model based on a corresponding Scene. A Scene can be a window, a dialog, etc.
 * @author Michaela Baèíková
 */
public class DomainModelGenerator {
	/**
	 * The domain model which is created from the given scene is stored in this field - only for the purposes of this class.
	 */
	private DomainModel domainModel;
	private Recorder recorder;
	
	/**
	 * Creates a domain model from the given scene.
	 * @param scene The scene which the model should be created from.
	 * @param name The name of the domain model / equals the name of the scene (title, application name).
	 * @return The domain model created from the scene.
	 */
	public DomainModel createDomainModel(Scene<?> scene, String name) throws ExtractionException {
		//create a new domain model with a default root
		domainModel = new DomainModel(name);
		Term rootTerm = new Term(domainModel, name);
		domainModel.setRoot(rootTerm);
		
		//1. PHASE, extraction algorithm
		Extractor extractor = new Extractor(scene, domainModel);
		domainModel = extractor.eXTRACT();
		
		//2. PHASE, simplification algorithm
		Simplifier simplifier = new Simplifier(domainModel);
		domainModel = simplifier.sIMPLIFY();
		
		//3. PHASE, recorder registration
		RecorderRegistator registrator = new RecorderRegistator(recorder);
		registrator.rEGISTER_RECORDER(scene);
		
		return domainModel;
	}
	
	/***************************************** Recorder stuff *************************************/
	public Recorder getRecorder() {
		return recorder;
	}

	public void setRecorder(Recorder recorder) {
		this.recorder = recorder;
	}
	
	public DomainModel getDomainModel() {
		return domainModel;
	}
}