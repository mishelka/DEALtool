package gui.tools;

import gui.model.application.scenes.Scene;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.tools.exception.ExtractionException;

/**
 * Creates a domain model based on a corresponding Scene. 
 * A Scene can be a window, a dialog, etc.
 * @author Michaela Bacikova, Slovakia,
 * michaela.bacikova@tuke.sk
 */
public class DomainModelGenerator {
	/**
	 * The domain model which is created from the given 
	 * scene is stored in this field - only for the purposes of this class.
	 */
	private DomainModel domainModel;
	private AbstractDealExtractor extractor;
	private Simplifier simplifier;
	private RecorderRegistator registrator;
	
	/**
	 * The constructor creating the new instance of DomainModelGenerator.
	 * Creates a new Extractor and Simplifier and a new RecorderRegistrator.
	 * @param recorder a reference to the recorder
	 */
	public DomainModelGenerator(Recorder recorder, AbstractDealExtractor extractor) {
		this.extractor = extractor;
		this.simplifier = new Simplifier();
		this.registrator = new RecorderRegistator(recorder);
	}
	
	/**
	 * Creates a domain model from the given scene.
	 * The method has three phases:
	 * <ol>
	 * <li>extraction (using the Extractor instance)</li>
	 * <li>simplification (using the Simplifier instance)</li>
	 * <li>recorder registration (using the RecorderRegistrator instance)</li>
	 * </ol>
	 * @param scene The scene which the model should be created from.
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
}