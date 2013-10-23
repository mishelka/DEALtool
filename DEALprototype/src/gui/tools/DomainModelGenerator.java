package gui.tools;

import gui.model.application.scenes.Scene;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.tools.Extractor.ExtractionException;

/**
 * Creates a domain model based on a corresponding Scene. A Scene can be a window, a dialog, etc.
 * @author Michaela Baèíková
 */
public class DomainModelGenerator {
	/**
	 * The domain model which is created from the given scene is stored in this field - only for the purposes of this class.
	 */
	private DomainModel domainModel;
	private Extractor extractor;
	private Simplifier simplifier;
	private RecorderRegistator registrator;
	private Recorder recorder;
	
	public DomainModelGenerator(Recorder recorder) {
		this.extractor = new Extractor();
		this.simplifier = new Simplifier();
		this.recorder = recorder;
		this.registrator = new RecorderRegistator(recorder);
	}
	
	/**
	 * Creates a domain model from the given scene.
	 * @param scene The scene which the model should be created from.
	 * @param name The name of the domain model / equals the name of the scene (title, application name).
	 * @return The domain model created from the scene.
	 */
	public DomainModel createDomainModel(Scene<?> scene) throws ExtractionException {
		//create a new domain model with a default root and set it to the scene
		createDefaultDomainModel(scene.getName());
		scene.setDomainModel(domainModel);
		
		//1. PHASE, extraction algorithm
		extractor.eXTRACT(scene);
		
		//2. PHASE, simplification algorithm
		simplifier.sIMPLIFY(domainModel);
		
		//3. PHASE, recorder registration
		registrator.rEGISTER_RECORDER(scene);
		
		return domainModel;
	}
	
	private void createDefaultDomainModel(String name) {
		domainModel = new DomainModel(name);
		Term rootTerm = new Term(domainModel, name);
		domainModel.replaceRoot(rootTerm);
	}
	
	/***************************************** Recorder stuff *************************************/
	public Recorder getRecorder() {
		return recorder;
	}

	public void setRecorder(Recorder recorder) {
		this.recorder = recorder;
	}
}