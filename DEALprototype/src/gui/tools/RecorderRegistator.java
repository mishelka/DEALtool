package gui.tools;

import gui.analyzer.handlers.Composite;
import gui.analyzer.handlers.Composites;
import gui.analyzer.handlers.RecordSupport;
import gui.analyzer.handlers.RecordSupports;
import gui.model.application.scenes.Scene;

/**
 * In order for the Recorder to work, each component in the UI has to be
 * registered with it. RecorderRegistrator registers the recorder to the given
 * scene and into all supported components.
 * <br></br>
 * For the registration to process, a RecordSupport handler must exist for the
 * given component. The supported handlers are in the
 * <code>gui.handlers.swing.record</code> package. The registration process has
 * two phases.
 * <ol>
 * <li>Each superclass of the given component is traversed. If at least one
 * superclass has a handler, the recorder is registered to the given component.
 * If not, then the second phase continues.</li>
 * <li>If the given component is a container, then the child components are
 * traversed. If at least one child component has a handler, the recorder is
 * registered to the child component.</li>
 * </ol>
 * These two phases are performed for each component in the given scene.
 * 
 * @author Michaela Bacikova, Slovakia, michaela.bacikova@tuke.sk
 */
public class RecorderRegistator {
	/** The recorder instance to be registered. */
	private Recorder recorder;
	
	/**
	 * Creates a new RecorderRegistrator instance and stores the reference to the recorder.
	 * @param recorder the reference to the recorder to be registered
	 */
	public RecorderRegistator(Recorder recorder) {
		this.recorder = recorder;
	}
	
	/**
	 * Registers recorder to all components in the given scenes.
	 * @param scene the scene the recorder should be registered to.
	 */
	public void rEGISTER_RECORDER(Scene<?> scene) {
		registerComponentTree(scene.getSceneContainer());
	}
	
	/**
	 * Registers one component in the tree and then tries to register its children.
	 * For the registration to process, a RecordSupport handler must exist for the given component.
	 * The supported handlers are in the <code>gui.handlers.swing.record</code> package.
	 * The registration process has two phases.
	 * <ol>
	 * <li>Each superclasse of the given component is traversed. 
	 * If at least one superclass has a handler, the recorder is registered to the given component.
	 * If not, then the second phase continues.</li>
	 * <li>If the given component is a container,
	 * then the child components are traversed. 
	 * If at least one child component has a handler, 
	 * the recorder is registered to the child component.</li>
	 * </ol>
	 * @param component the component the recorder should be registered to (or to its child components).
	 */
	private <T> void registerComponentTree(T component) {
		boolean continueToChildren = tryRegisterSuperclass(component);
		
		if(continueToChildren) tryRegisterChildren(component);
	}
	
	/**
	 * Tries to register the recorder on superclasses of the given component.
	 * Sometimes it is not necessary to extract information from the children of 
	 * the given component. For instance, a JSpinner is a component, which serves for
	 * number input. It however contains two buttons, too, which are unnecessary for extraction,
	 * but necessary for recording. Therefore if this method returns false, the registraction
	 * process will not continue to the children of the given component.
	 * @param component the component the recorder should be registered to.
	 * @return true if the registration should continue to the children of this component, false otherwise.
	 */
	private <T> boolean tryRegisterSuperclass(T component) {
		boolean continueToChildren = true;
		@SuppressWarnings("unchecked")
		Class<? super T> componentClass = (Class<T>) component.getClass();
		
		while (componentClass != null) {
			RecordSupport<? super T> recordSupport = RecordSupports
					.getInstance().getRecordSupport(componentClass);
			if (recordSupport != null) {
				continueToChildren = recordSupport.register(component, recorder);
				
				componentClass = null;
				break;
			} else {
				componentClass = componentClass.getSuperclass();
			}
		}
		return continueToChildren;
	}
	
	/**
	 * Registration of all children of the given component.
	 * This method will not execute if the tryRegisterSuperclass(..) method returned false.
	 * @param component the component whose children should be traversed for registration.
	 */
	private <T> void tryRegisterChildren(T component) {
		@SuppressWarnings("unchecked")
		Class<? super T> componentClass = (Class<T>) component.getClass();
		Composite<? super T> composite = Composites.getInstance().getComposite(
				componentClass);
		if (composite != null) {
			for (Object subcomponent : composite.getComponents(component)) {
				registerComponentTree(subcomponent);
			}
		}
	}
}
